# TODO

## Product Direction

- [ ] Keep the app focused on the offline-first 90% use case: learn, train, track, improve.
- [ ] Treat `Home`, `Workouts`, `Nutrition`, `Progress`, and `Knowledge` as the only primary pillars.
- [ ] Avoid low-priority online or community features until the offline core is excellent.

## Phase 1: Core Simplification And UX Cleanup

- [x] Audit all primary screens for layout, inset, overflow, and small-screen issues.
- [x] Remove or hide low-priority features that do not support the core offline loop.
- [x] Tighten the `Home` screen so it clearly answers: what should I do today?
- [x] Make the `Knowledge` tab a first-class destination, not a secondary add-on.
- [x] Improve empty states so first-time users always know the next useful action.
- [x] Standardize section spacing, card sizing, and typography across tabs.

## Phase 2: Knowledge Library Foundation

- [x] Build a clear article structure from beginner to expert.
- [x] Add a `Start Here` sequence for complete beginners.
- [x] Add core article tracks:
- [x] `Fitness Fundamentals`
- [x] `Strength Training Basics`
- [x] `Muscle Gain Basics`
- [x] `Fat Loss Basics`
- [x] `Nutrition Basics`
- [x] `Recovery Basics`
- [x] `Programming Basics`
- [x] `Fitness Do's And Don'ts`
- [x] `Common Fitness Myths`
- [x] Label each article by `Beginner`, `Intermediate`, or `Expert`.
- [x] Add article search, category filters, and level filters.
- [x] Add saved articles and continue-reading support.
- [x] Add related-article recommendations inside article detail screens.

## Phase 3: Workout Experience For Daily Use

- [x] Improve quick-start workout creation for common training days.
- [x] Add better default templates for beginner, intermediate, and general fitness users.
- [x] Add warm-up guidance and optional warm-up set support.
- [x] Add rest timer controls that are easy to use during workouts.
- [x] Add simple progression prompts based on last performance.
- [x] Add exercise substitution without breaking workout flow.
- [x] Improve workout notes, personal records, and post-workout summary clarity.
- [x] Make active workout interactions reliable for one-handed use.

## Phase 4: Nutrition For The 90% Use Case

- [x] Prioritize calories, protein, hydration, and meal consistency over advanced diet features.
- [x] Improve hydration logging safeguards and add a clear reset/edit path.
- [x] Add reusable meal templates for common breakfasts, lunches, dinners, and snacks.
- [x] Add simple daily goal adherence feedback instead of only raw totals.
- [x] Make the nutrition screen easier to scan at a glance.
- [x] Add weekly consistency summaries for calories, protein, and hydration.

## Phase 5: Progress That Teaches

- [x] Make `Progress` explain trends in plain language, not just charts.
- [x] Add summaries for training consistency, workout volume, and habit adherence.
- [x] Add body-weight and measurement trend explanations with context.
- [x] Highlight useful changes such as strength improvements and streak recovery.
- [x] Connect progress insights back to relevant knowledge articles.
- [x] Show whether the user is on track, stalled, or improving with simple guidance.

## Phase 6: Trust, Offline Reliability, And Data Safety

- [x] Add better edit, undo, and reset flows for tracked data.
- [x] Add sanity checks for obviously broken values like hydration spikes.
- [x] Add export and import for local backup.
- [x] Add clear offline and privacy messaging in settings or onboarding.
- [x] Make the app resilient when reopened mid-workout or after process death.
- [x] Review local persistence flows for data loss risks.

## Phase 7: Quality Hardening

- [x] Add tests for navigation between all primary tabs.
- [x] Add tests for workout logging, hydration logging, and progress calculations.
- [x] Add tests for article catalog coverage, article rendering, and filters.
- [x] Add regression checks for text overflow and large-number formatting.
- [x] Add a release checklist for APK verification before every handoff.
- [ ] Review performance on lower-end devices and smaller screens.
- [x] Improve accessibility for contrast, touch targets, and large text sizes.

## Execution Order

- [ ] Finish Phase 1 before expanding feature scope.
- [ ] Finish Phase 2 before adding low-priority app sections.
- [ ] Finish Phases 3 and 4 before considering advanced nutrition or coaching systems.
- [ ] Finish Phases 5 through 7 before bringing back any social or cloud-heavy features.
