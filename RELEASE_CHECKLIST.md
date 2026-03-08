# Release Checklist

- Build `assembleDebug` and `copyReleaseApk` from the current workspace.
- Install the fresh APK and verify onboarding or the home screen launches cleanly.
- Open every primary tab: `Home`, `Workouts`, `Nutrition`, `Progress`, and `Knowledge`.
- On `Home`, verify the focus card, summary cards, hydration card, and quick-start cards render without clipping.
- Start a workout, log at least one set, finish it, and confirm the workout appears in history.
- Log water and a meal, then confirm nutrition totals update without broken formatting.
- Open `Knowledge`, search for a guide, apply a category filter, and open an article detail screen.
- Open `Progress`, confirm charts and insight cards render with non-overlapping text.
- Export a backup, import the same backup, and confirm core data returns after restore.
- Check the app on a smaller screen or large-text setting for overflow, clipped buttons, and inaccessible touch targets.
- Confirm the delivered APK path and timestamp match the latest build artifact.
