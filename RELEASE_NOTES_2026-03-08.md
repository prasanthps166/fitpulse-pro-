# FitPulse Pro Release Notes

Date: 2026-03-08
Build: `C:\Users\MrCrAzY\Downloads\AntiGravity2\fitpulse-pro\FitPulsePro.apk`

## Included changes

- Reduced unnecessary recomposition and list churn across Home, Workouts, Learn, and Progress screens.
- Updated active workout and carousel lists to use stable keys.
- Cached derived UI data for faster rendering on repeated screen visits.
- Bumped the Room database version to `4` and exported the matching schema.
- Improved compact-screen and large-font behavior:
  - bottom navigation now uses short one-line labels in cramped layouts
  - home focus metrics stack vertically when space is tight
  - workout stat cards use shorter labels and compact values
  - knowledge search uses a shorter single-line placeholder

## Verification

- Passed `.\gradlew.bat :app:testDebugUnitTest`
- Passed `.\gradlew.bat connectedDebugAndroidTestCompat`
- Passed `.\gradlew.bat copyReleaseApk`
- Built fresh release APK and installed it on device `4bca7575`
- Verified manual flows:
  - Knowledge filtering and article open
  - Progress stats render correctly
  - Backup export and import complete without data loss
  - Small-screen / large-font stress pass after responsive fixes

## Evidence

- Manual review artifacts: `C:\Users\MrCrAzY\Downloads\AntiGravity2\fitpulse-pro\manual-review`
- Final small-screen verification: `C:\Users\MrCrAzY\Downloads\AntiGravity2\fitpulse-pro\manual-review\small-screen-final`
