package com.fitpulse.pro.data.local

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FitPulseDatabaseSchemaTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        FitPulseDatabase::class.java,
        emptyList()
    )

    @Test
    fun exportedSchema_opensLatestDatabase() {
        helper.createDatabase(TEST_DB_NAME, FITPULSE_DATABASE_VERSION).apply {
            query("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'user_profile'").use { cursor ->
                assertTrue(cursor.moveToFirst())
            }
            close()
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Room.databaseBuilder(context, FitPulseDatabase::class.java, TEST_DB_NAME)
            .build()
            .apply {
                openHelper.writableDatabase
                close()
            }

        context.deleteDatabase(TEST_DB_NAME)
    }

    private companion object {
        const val TEST_DB_NAME = "fitpulse_schema_test.db"
    }
}

