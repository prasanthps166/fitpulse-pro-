plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

// Load local SDK and optional signing configuration.
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import org.gradle.api.tasks.compile.JavaCompile

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val signingProperties = Properties()
val signingPropertiesFile = rootProject.file("signing.properties")
if (signingPropertiesFile.exists()) {
    signingProperties.load(FileInputStream(signingPropertiesFile))
}

fun configValue(name: String): String? {
    return System.getenv(name)?.takeIf { it.isNotBlank() }
        ?: signingProperties.getProperty(name)?.takeIf { it.isNotBlank() }
        ?: localProperties.getProperty(name)?.takeIf { it.isNotBlank() }
}

data class CommandResult(
    val exitCode: Int,
    val output: String,
    val timedOut: Boolean
)

fun resolveAndroidSdkPath(): String? {
    return System.getenv("ANDROID_HOME")?.takeIf { it.isNotBlank() }
        ?: System.getenv("ANDROID_SDK_ROOT")?.takeIf { it.isNotBlank() }
        ?: localProperties.getProperty("sdk.dir")?.takeIf { it.isNotBlank() }
}

fun resolveAdbExecutable(): File {
    val sdkPath = resolveAndroidSdkPath()
        ?: throw GradleException("Android SDK path not found. Set ANDROID_HOME, ANDROID_SDK_ROOT, or sdk.dir.")
    val adbName = if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
        "adb.exe"
    } else {
        "adb"
    }
    return file("$sdkPath/platform-tools/$adbName").takeIf { it.exists() }
        ?: throw GradleException("adb not found under $sdkPath/platform-tools")
}

fun runCommand(
    command: List<String>,
    timeoutMs: Long,
    workingDir: File
): CommandResult {
    val output = StringBuilder()
    val process = ProcessBuilder(command)
        .directory(workingDir)
        .redirectErrorStream(true)
        .start()

    val reader = thread(name = "cmd-reader-${command.firstOrNull()}", isDaemon = true) {
        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                output.appendLine(line)
            }
        }
    }

    val finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
    if (!finished) {
        process.destroy()
        if (!process.waitFor(5, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            process.waitFor(5, TimeUnit.SECONDS)
        }
    }

    reader.join(5_000)

    return CommandResult(
        exitCode = if (finished) process.exitValue() else -1,
        output = output.toString().trim(),
        timedOut = !finished
    )
}

fun adbCommand(adb: File, serial: String, vararg args: String): List<String> {
    return buildList {
        add(adb.absolutePath)
        add("-s")
        add(serial)
        addAll(args)
    }
}

fun detectConnectedDeviceSerial(adb: File, workingDir: File): String {
    val requestedSerial = System.getenv("ANDROID_SERIAL")?.trim()?.takeIf { it.isNotEmpty() }
    if (requestedSerial != null) {
        return requestedSerial
    }

    val result = runCommand(listOf(adb.absolutePath, "devices"), 30_000, workingDir)
    if (result.exitCode != 0) {
        throw GradleException("Failed to list adb devices:\n${result.output}")
    }

    val devices = result.output
        .lineSequence()
        .map(String::trim)
        .filter { it.endsWith("\tdevice") }
        .map { it.substringBefore('\t') }
        .toList()

    return when (devices.size) {
        0 -> throw GradleException("No connected Android device found.")
        1 -> devices.single()
        else -> throw GradleException(
            "Multiple Android devices found. Set ANDROID_SERIAL to one of: ${devices.joinToString(", ")}"
        )
    }
}

fun isPackageInstalled(adb: File, serial: String, packageName: String, workingDir: File): Boolean {
    val result = runCommand(
        adbCommand(adb, serial, "shell", "pm", "path", packageName),
        30_000,
        workingDir
    )
    return result.output.lineSequence().any { it.startsWith("package:") }
}

fun waitForPackageInstall(
    adb: File,
    serial: String,
    packageName: String,
    workingDir: File,
    waitMs: Long = 30_000L
): Boolean {
    val deadline = System.currentTimeMillis() + waitMs
    while (System.currentTimeMillis() < deadline) {
        if (isPackageInstalled(adb, serial, packageName, workingDir)) {
            return true
        }
        Thread.sleep(2_000L)
    }
    return false
}

fun installApkWithFallback(
    adb: File,
    serial: String,
    apkFile: File,
    packageName: String,
    workingDir: File,
    timeoutMs: Long = 6 * 60 * 1000L
) {
    val result = runCommand(
        adbCommand(adb, serial, "install", "-r", "-t", apkFile.absolutePath),
        timeoutMs,
        workingDir
    )

    val installed = isPackageInstalled(adb, serial, packageName, workingDir) ||
        waitForPackageInstall(adb, serial, packageName, workingDir)

    if (result.exitCode == 0 && result.output.contains("Success")) {
        return
    }

    if (installed) {
        println("Package $packageName is installed despite adb not returning cleanly.")
        if (result.output.isNotBlank()) {
            println(result.output)
        }
        return
    }

    val reason = buildString {
        append("Failed to install $packageName from ${apkFile.name}.")
        if (result.timedOut) {
            append("\nCommand timed out after ${timeoutMs / 1000}s.")
        }
        if (result.output.isNotBlank()) {
            append("\n")
            append(result.output)
        }
    }
    throw GradleException(reason)
}

val releaseStoreFilePath = configValue("RELEASE_STORE_FILE")
val releaseStorePassword = configValue("RELEASE_STORE_PASSWORD")
val releaseKeyAlias = configValue("RELEASE_KEY_ALIAS")
val releaseKeyPassword = configValue("RELEASE_KEY_PASSWORD")
val hasReleaseSigning = listOf(
    releaseStoreFilePath,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { !it.isNullOrBlank() }

android {
    namespace = "com.fitpulse.pro"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fitpulse.pro"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigning) {
                storeFile = rootProject.file(releaseStoreFilePath!!)
                storePassword = releaseStorePassword!!
                keyAlias = releaseKeyAlias!!
                keyPassword = releaseKeyPassword!!
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    sourceSets["androidTest"].assets.srcDir("$projectDir/schemas")

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        jniLibs {
            keepDebugSymbols += "**/libandroidx.graphics.path.so"
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true

        managedDevices {
            localDevices {
                create("compactPhoneApi36") {
                    device = "Pixel 2"
                    apiLevel = 36
                    systemImageSource = "google"
                }
            }
        }
    }

}

if (!hasReleaseSigning) {
    logger.lifecycle(
        "Release signing is not configured. Add signing.properties or environment variables to create signed release builds."
    )
}

tasks.register("copyReleaseApk") {
    dependsOn("assembleRelease")
    doLast {
        val apkDir = file("${project.layout.buildDirectory.get()}/outputs/apk/release")
        val apkFile = apkDir
            .listFiles()
            ?.firstOrNull { it.isFile && it.extension == "apk" }
        val destFile = file("${rootProject.layout.projectDirectory}/FitPulsePro.apk")
        if (apkFile?.exists() == true) {
            apkFile.copyTo(destFile, overwrite = true)
            println("APK copied to: ${destFile.absolutePath}")
        } else {
            println("APK not found in: ${apkDir.absolutePath}")
        }
    }
}

tasks.register("connectedDebugAndroidTestCompat") {
    group = "verification"
    description = "Runs debug instrumentation on a connected device without relying on UTP installs."
    dependsOn("assembleDebug", "assembleDebugAndroidTest")

    doLast {
        val adb = resolveAdbExecutable()
        val serial = detectConnectedDeviceSerial(adb, rootDir)
        val debugApk = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk").get().asFile
        val androidTestApk = layout.buildDirectory.file("outputs/apk/androidTest/debug/app-debug-androidTest.apk").get().asFile
        val appPackage = "com.fitpulse.pro.debug"
        val testPackage = "$appPackage.test"
        val instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        if (!debugApk.exists()) {
            throw GradleException("Debug APK not found at ${debugApk.absolutePath}")
        }
        if (!androidTestApk.exists()) {
            throw GradleException("Debug androidTest APK not found at ${androidTestApk.absolutePath}")
        }

        // Ignore uninstall failures; some devices report internal errors even when the package is absent.
        runCommand(adbCommand(adb, serial, "uninstall", testPackage), 60_000, rootDir)
        runCommand(adbCommand(adb, serial, "uninstall", appPackage), 60_000, rootDir)

        installApkWithFallback(adb, serial, debugApk, appPackage, rootDir)
        installApkWithFallback(adb, serial, androidTestApk, testPackage, rootDir)

        val result = runCommand(
            adbCommand(
                adb,
                serial,
                "shell",
                "am",
                "instrument",
                "-w",
                "$testPackage/$instrumentationRunner"
            ),
            timeoutMs = 20 * 60 * 1000L,
            workingDir = rootDir
        )

        if (result.output.isNotBlank()) {
            println(result.output)
        }

        if (result.timedOut || result.exitCode != 0 || !result.output.contains("OK (")) {
            throw GradleException(
                buildString {
                    append("Instrumentation failed on device $serial.")
                    if (result.timedOut) {
                        append("\nCommand timed out.")
                    }
                    if (result.output.isNotBlank()) {
                        append("\n")
                        append(result.output)
                    }
                }
            )
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ViewModel with Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Gson for JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // Coil for Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Health Connect
    implementation("androidx.health.connect:connect-client:1.1.0-alpha06")

    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

tasks.withType<JavaCompile>().configureEach {
    doFirst {
        delete(
            fileTree(layout.buildDirectory.dir("generated/ksp").get().asFile) {
                include("**/byRounds/**")
            }
        )
    }
}
