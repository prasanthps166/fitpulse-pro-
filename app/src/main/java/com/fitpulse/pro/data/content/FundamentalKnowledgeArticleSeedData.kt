package com.fitpulse.pro.data.content

import com.fitpulse.pro.data.model.ArticleCategory
import com.fitpulse.pro.data.model.ArticleSource
import com.fitpulse.pro.data.model.BeginnerSection
import com.fitpulse.pro.data.model.ExpertSection
import com.fitpulse.pro.data.model.FitnessArticle
import com.fitpulse.pro.data.model.IntermediateSection
import com.fitpulse.pro.data.model.KnowledgeLevel

internal fun fundamentalKnowledgeSeedArticles(): List<FitnessArticle> {
    val allLevels = KnowledgeLevel.entries.toList()

    val hhsGuidelines = source(
        title = "Physical Activity Guidelines for Americans, 2nd edition",
        citation = "U.S. Department of Health and Human Services. 2018.",
        url = "https://health.gov/sites/default/files/2019-09/Physical_Activity_Guidelines_2nd_edition.pdf"
    )
    val whoGuidelines = source(
        title = "WHO guidelines on physical activity and sedentary behaviour",
        citation = "World Health Organization. 2020.",
        url = "https://www.who.int/publications/i/item/9789240015128"
    )
    val resistancePrescriptionMeta = source(
        title = "Effects of resistance training prescription and exercise volume on strength and hypertrophy in regular gym-goers: a systematic review and network meta-analysis",
        citation = "Nascimento MA, et al. Br J Sports Med. 2023.",
        url = "https://pubmed.ncbi.nlm.nih.gov/37871453/"
    )
    val resistanceFailureMeta = source(
        title = "Effects of resistance training performed to repetition failure or non-failure on muscular strength and hypertrophy: a systematic review and meta-analysis",
        citation = "Vieira AF, et al. J Sport Health Sci. 2022.",
        url = "https://pubmed.ncbi.nlm.nih.gov/33497853/"
    )
    val loadHypertrophyReview = source(
        title = "Muscular adaptations in low- versus high-load resistance training: A systematic review and meta-analysis",
        citation = "Lopez P, et al. Rev Bras Cineantropom Desempenho Hum. 2021.",
        url = "https://pubmed.ncbi.nlm.nih.gov/34384972/"
    )
    val concurrentTrainingReview = source(
        title = "Moderators of training-related fitness and skill adaptations during concurrent strength and endurance training: A systematic review and network meta-analysis",
        citation = "Schumann M, et al. Sports Med. 2024.",
        url = "https://pubmed.ncbi.nlm.nih.gov/39724371/"
    )
    val aerobicWeightLossReview = source(
        title = "Dose-response association of aerobic exercise with body weight and body composition in adults with overweight or obesity: a systematic review and meta-analysis",
        citation = "Li T, et al. J Sport Health Sci. 2024.",
        url = "https://pubmed.ncbi.nlm.nih.gov/39151300/"
    )
    val warmUpReview = source(
        title = "Multicomponent warm-up strategies for sports injury prevention",
        citation = "Kuczynski M, et al. Sports Med. 2026.",
        url = "https://pubmed.ncbi.nlm.nih.gov/40203634/"
    )

    return listOf(
        FitnessArticle(
            id = "fitness_fundamentals_full_guide",
            title = "Fitness Fundamentals - Full Guide",
            category = ArticleCategory.TRENDS_SCIENCE,
            primaryLevel = KnowledgeLevel.BEGINNER,
            levelsCovered = allLevels,
            quickTakeaway = "Fitness gets built on boring basics done well: strength training, cardio, daily movement, protein, sleep, and patience. Most people need fewer hacks and more consistency.",
            beginner = beginner(
                simpleExplanation = "Your body adapts to the repeated signals you give it. Lift and it gets stronger, move more and your fitness improves, eat and sleep well and recovery gets easier. The basics work because the body responds to steady habits, not random motivation spikes.",
                whyItMatters = listOf(
                    "It keeps you focused on the few habits that drive most results.",
                    "It helps you ignore flashy advice that sounds advanced but solves the wrong problem.",
                    "It makes training easier to sustain for months instead of only for one hard week."
                ),
                stepByStep = listOf(
                    "Strength train at least 2 days per week and repeat the main lifts or movement patterns long enough to improve them.",
                    "Accumulate regular cardio through brisk walking, cycling, jogging, or similar work each week.",
                    "Keep daily movement high with walks, stairs, and less sitting instead of relying only on one workout.",
                    "Eat enough protein and mostly simple, repeatable meals before worrying about advanced timing tricks.",
                    "Sleep long enough that energy, mood, and training quality stay stable.",
                    "Run the same basic plan for 8 to 12 weeks before deciding it does not work."
                ),
                equipmentNeeded = listOf(
                    "A gym is useful but not required - bodyweight, bands, a backpack, or dumbbells can cover the basics.",
                    "Good shoes, a notebook, and a timer are often more useful than fancy gadgets."
                ),
                commonMistakes = listOf(
                    "Changing the plan every week and never giving adaptation time to happen.",
                    "Treating supplements as the foundation instead of training, food, sleep, and movement.",
                    "Doing too much too soon and mistaking exhaustion for good programming.",
                    "Thinking one workout can offset a low-movement lifestyle."
                ),
                safetyTips = listOf(
                    "Start below your maximum and build up across weeks, not in one hero day.",
                    "Use controlled ranges of motion and stop if sharp pain or instability appears.",
                    "If you have a medical condition or long layoff, build from an easier starting point."
                )
            ),
            intermediate = intermediate(
                progressGuidance = listOf(
                    "Use a weekly structure that you can actually repeat, such as 2 to 4 strength sessions and 2 to 3 cardio sessions.",
                    "Keep most hard sets with 1 to 3 reps in reserve so quality stays high.",
                    "Increase weekly training only when sleep, appetite, soreness, and performance say you are absorbing the work."
                ),
                keyPrinciples = listOf(
                    "Specificity: you adapt to what you practice regularly.",
                    "Progressive overload: the signal needs to grow over time through reps, load, sets, pace, or range.",
                    "Recoverability: progress depends on what you can adapt to, not what you can survive once.",
                    "Energy balance: body weight change follows long-term intake and expenditure, not one perfect workout."
                ),
                progressionExample = listOf(
                    "Weeks 1 to 2: 2 full-body strength days, 2 cardio days, and a simple daily step target.",
                    "Weeks 3 to 4: add a third strength day or a little cardio time if recovery is still good.",
                    "Weeks 5 to 8: keep the same framework and progress slowly on lifts, pace, or duration.",
                    "Weeks 9 to 12: deload slightly if fatigue is high, then resume from a stronger baseline."
                ),
                weeklyIntegration = listOf(
                    "Place the most important training sessions earlier in the week or on your highest-energy days.",
                    "Use low-intensity cardio and walking to support health and work capacity without frying recovery.",
                    "Keep one or two lighter days so the hard days stay productive."
                ),
                trackingTips = listOf(
                    "Track body weight trend, waist, main lifts, cardio pace, step count, and sleep.",
                    "Judge progress over weeks, not based on one bloated morning or one amazing session.",
                    "If everything regresses at once, reduce volume before you assume the whole plan is broken."
                )
            ),
            expert = expert(
                biomechanicsAndActivation = listOf(
                    "Fitness is multi-component: strength, cardiorespiratory fitness, movement skill, body composition, and recovery capacity adapt at different rates but interact constantly.",
                    "Most useful programming decisions come down to dose management: enough stimulus to adapt, not so much that fatigue blunts progress.",
                    "Behavior matters as much as physiology because a mediocre plan repeated for a year beats an ideal plan followed for 10 days."
                ),
                latestEvidence = listOf(
                    "The official U.S. and WHO physical activity guidelines continue to support a simple foundation: regular aerobic work plus muscle-strengthening activity on at least 2 days per week.",
                    "The 2023 British Journal of Sports Medicine network meta-analysis found that many resistance training prescriptions improve hypertrophy, while multi-set and moderate-to-higher loading strategies tend to produce the best strength outcomes.",
                    "The 2024 Journal of Sport and Health Science dose-response review reported that more weekly aerobic exercise generally improved body-weight and waist outcomes, but the pattern was gradual rather than magical."
                ),
                advancedVariables = listOf(
                    "Use minimum effective dose thinking first, then add volume only where results justify the recovery cost.",
                    "Separate priority strength work from harder endurance work when interference or fatigue becomes noticeable.",
                    "Keep your plan specific to the goal: muscle, strength, health, fat loss, or sport performance each changes what deserves the top slot."
                ),
                researchBackedTweaks = listOf(
                    "Short cardio bouts still count when total weekly volume adds up.",
                    "Beginners usually benefit more from repeating the same lifts than from maximal exercise variety.",
                    "When adherence is shaky, simplify the plan before you add more optimization."
                )
            ),
            indiaFriendlyNotes = listOf(
                "Walking after meals, climbing stairs, and a few well-run lifting sessions each week cover a lot of ground.",
                "Affordable basics still work: eggs, milk, curd, paneer, dal, soy, fruit, rice, and roti can support training very well.",
                "You do not need boutique supplements or imported machines to build a strong base."
            ),
            sources = listOf(hhsGuidelines, whoGuidelines, resistancePrescriptionMeta, aerobicWeightLossReview),
            readTimeMinutes = 9,
            tags = listOf("fundamentals", "basics", "consistency", "beginner")
        ),
        FitnessArticle(
            id = "fitness_dos_and_donts_full_guide",
            title = "Fitness Do's and Don'ts - Full Guide",
            category = ArticleCategory.INJURY_PREVENTION,
            primaryLevel = KnowledgeLevel.BEGINNER,
            levelsCovered = allLevels,
            quickTakeaway = "Do repeat the basics, progress slowly, warm up with intent, and recover properly. Do not chase soreness, copy unsustainable volume, ignore pain, or assume harder always means better.",
            beginner = beginner(
                simpleExplanation = "Good fitness habits are mostly simple behaviors repeated for a long time. The problem is not that the basics are secret - it is that people get bored and abandon them for shortcuts.",
                whyItMatters = listOf(
                    "It reduces avoidable setbacks from ego lifting, random programming, and poor recovery.",
                    "It teaches the habits that actually separate consistent trainees from quit-restart cycles.",
                    "It helps beginners learn what productive training feels like."
                ),
                stepByStep = listOf(
                    "Do start each session with a short warm-up that matches the workout you are about to do.",
                    "Do repeat the same key exercises long enough to improve technique and numbers.",
                    "Do progress one variable at a time: a few reps, a little load, or one extra set.",
                    "Do leave some reps in reserve on most sets instead of turning every workout into a test.",
                    "Do sleep and eat like recovery matters, because it does.",
                    "Do log training so your next step is based on evidence instead of mood."
                ),
                equipmentNeeded = listOf(
                    "A simple training setup, a water bottle, and a training log are enough to follow the core do's.",
                    "You do not need a giant supplement stack or fancy recovery tools."
                ),
                commonMistakes = listOf(
                    "Do not max out too early or too often just because the weight moved once.",
                    "Do not copy influencer splits, volume, or exercise menus that your recovery cannot support.",
                    "Do not use soreness, sweat, or nausea as the scorecard for workout quality.",
                    "Do not ignore sharp pain, repeated technique breakdown, or mounting fatigue.",
                    "Do not crash diet while expecting peak performance and muscle gain at the same time."
                ),
                safetyTips = listOf(
                    "If pain rises as load rises, adjust the exercise, range, or dose before pushing harder.",
                    "Use a full range you can control instead of forcing positions you do not own.",
                    "When in doubt, choose the repeatable option over the dramatic one."
                )
            ),
            intermediate = intermediate(
                progressGuidance = listOf(
                    "Build your week around a few non-negotiable sessions and let accessories stay flexible.",
                    "Progress when performance is steady, recovery is acceptable, and technique remains clean.",
                    "If motivation is high but recovery is poor, hold the line instead of adding more work."
                ),
                keyPrinciples = listOf(
                    "Good programming is boring in a useful way: it is trackable, repeatable, and recoverable.",
                    "Most injuries and plateaus come from a mismatch between load and readiness, not from a lack of motivation.",
                    "Warm-ups should improve the first hard set, not become a second workout."
                ),
                progressionExample = listOf(
                    "Weeks 1 to 2: establish the basic plan and stop each working set before technical failure.",
                    "Weeks 3 to 5: add reps or small load jumps only where the logbook says you earned them.",
                    "Weeks 6 to 8: deload or trim sets if bar speed, mood, and recovery all trend down together."
                ),
                weeklyIntegration = listOf(
                    "Place hard lower-body strength and hard intervals far enough apart that both can stay high quality.",
                    "Use easier days for walking, mobility, and practice rather than forcing more hard training.",
                    "Keep technique-heavy work early in the session and fatigue-heavy work later."
                ),
                trackingTips = listOf(
                    "Track performance, effort, sleep, soreness, and any pain points in one place.",
                    "If the same mistake keeps showing up, fix the process rather than blaming discipline.",
                    "Judge a plan by whether it is still working after 6 to 8 weeks, not by one exciting session."
                )
            ),
            expert = expert(
                biomechanicsAndActivation = listOf(
                    "Most useful do's and don'ts are fatigue-management rules in disguise: preserve quality tension, maintain skill, and avoid unnecessary recovery cost.",
                    "Warm-ups, exercise selection, set proximity to failure, and weekly volume all change the stimulus-to-fatigue ratio.",
                    "Pain is not always damage, but symptom behavior is still useful data for programming decisions."
                ),
                latestEvidence = listOf(
                    "The 2023 resistance-training prescription network meta-analysis showed that many prescriptions work, which supports choosing sustainable structure over chasing one magic split.",
                    "The 2022 repetition-failure meta-analysis found no significant hypertrophy or strength advantage for taking every set to failure, which argues against making training unnecessarily costly.",
                    "The 2026 Sports Medicine warm-up review supports multicomponent warm-ups for injury prevention in sport settings, reinforcing the value of brief, purposeful preparation before hard work."
                ),
                advancedVariables = listOf(
                    "Use failure sparingly for lower-skill accessory work, not as the default for every main lift.",
                    "Adjust frequency, volume, and exercise stability before blaming intensity alone.",
                    "Keep recovery behaviors boring and reliable: enough sleep, enough protein, enough calories for the goal."
                ),
                researchBackedTweaks = listOf(
                    "When life stress climbs, cut junk volume before cutting all training.",
                    "If technique degrades first, the answer is often load management, not more hype.",
                    "The best do is usually the one you can still execute next week."
                )
            ),
            indiaFriendlyNotes = listOf(
                "Do not let crowded gyms push you into skipping warm-ups; a few ramp-up sets and bodyweight drills are enough.",
                "If time is tight, do fewer exercises better instead of rushing through a giant list.",
                "Simple meals and consistent sleep usually move progress more than buying more supplements."
            ),
            sources = listOf(hhsGuidelines, resistancePrescriptionMeta, resistanceFailureMeta, warmUpReview),
            readTimeMinutes = 8,
            tags = listOf("dos and donts", "habits", "programming", "injury prevention")
        ),
        FitnessArticle(
            id = "common_fitness_myths_full_guide",
            title = "Common Fitness Myths - Full Guide",
            category = ArticleCategory.TRENDS_SCIENCE,
            primaryLevel = KnowledgeLevel.EXPERT,
            levelsCovered = allLevels,
            quickTakeaway = "Most fitness myths survive because they feel simple and dramatic. Real progress is less magical: many methods can work, and results usually depend on weekly volume, technique, recovery, and long-term consistency.",
            beginner = beginner(
                simpleExplanation = "Fitness myths usually take one small truth and stretch it into a fake rule. The fix is to ask what actually drives the result: training quality, total work, energy balance, recovery, and time.",
                whyItMatters = listOf(
                    "It saves you from wasting time, money, and effort on low-value tricks.",
                    "It helps you make calmer decisions when social media turns every topic into a battle.",
                    "It makes your training choices more evidence-based and less fear-based."
                ),
                stepByStep = listOf(
                    "Myth: Only heavy weights build muscle. Reality: lighter loads can also build muscle if sets are hard enough and volume is sufficient.",
                    "Myth: You must train every set to failure. Reality: most people grow and get stronger without taking every set to the limit.",
                    "Myth: Cardio always kills gains. Reality: cardio mostly becomes a problem when total fatigue, timing, and recovery are poorly managed.",
                    "Myth: More sweat means more fat loss. Reality: sweat mostly reflects heat and fluid loss, not how much body fat you lost.",
                    "Myth: There is one best fat-burning workout. Reality: body-composition change comes from sustainable activity and nutrition habits repeated over time.",
                    "Myth: A few hacks matter more than basics. Reality: training, food quality, protein, sleep, and consistency still dominate outcomes."
                ),
                equipmentNeeded = listOf(
                    "You do not need special gear to debunk myths - you need a logbook, patience, and a willingness to compare claims against real results.",
                    "The same simple training tools work whether the internet calls them basic or advanced."
                ),
                commonMistakes = listOf(
                    "Confusing the hardest option with the most effective option.",
                    "Changing your plan because one clip promised a secret shortcut.",
                    "Trusting single-study headlines or influencers over the broader evidence picture.",
                    "Assuming a method failed when it was never run consistently in the first place."
                ),
                safetyTips = listOf(
                    "Avoid extreme challenges and detox-style advice that promise impossible speed.",
                    "Use skepticism when a claim says everyone must train one exact way.",
                    "If a method keeps hurting or exhausting you, that matters more than the marketing."
                )
            ),
            intermediate = intermediate(
                progressGuidance = listOf(
                    "Evaluate claims by whether they help you train more consistently and recover better over weeks.",
                    "Use weekly totals, not one sensational workout, as the real scorecard.",
                    "Choose methods that fit your goal and schedule instead of chasing what looks most intense online."
                ),
                keyPrinciples = listOf(
                    "The body responds to dosage and specificity more than to hype.",
                    "There are usually multiple workable paths to the same outcome.",
                    "Evidence gets stronger when many studies point the same way, not when one clip goes viral."
                ),
                progressionExample = listOf(
                    "Weeks 1 to 4: run a basic plan with repeatable lifts, cardio, steps, and meals.",
                    "Weeks 5 to 8: compare real data like performance, body weight trend, and recovery instead of comparing yourself to marketing promises.",
                    "Weeks 9 to 12: keep what works, cut what adds cost without improving outcomes."
                ),
                weeklyIntegration = listOf(
                    "Program cardio around strength rather than treating them like enemies.",
                    "Use load, reps, and exercise stability based on what the target muscle or performance goal needs.",
                    "Save maximal-effort work for when it truly serves the goal."
                ),
                trackingTips = listOf(
                    "Track whether the method improves performance, body composition, or adherence better than your previous baseline.",
                    "When two methods both work, choose the one with lower fatigue and better consistency.",
                    "If a myth sounds too neat, test it against several months of reality."
                )
            ),
            expert = expert(
                biomechanicsAndActivation = listOf(
                    "Most myths collapse when you separate external load from internal stimulus, and sensation from adaptation.",
                    "Hypertrophy, strength, endurance, and fat loss are related but distinct outcomes, so one simplified rule rarely covers them all.",
                    "Programming decisions should reflect the signal you want, the fatigue cost you can afford, and the behavior you can sustain."
                ),
                latestEvidence = listOf(
                    "The 2021 low-versus-high-load resistance training review reported that hypertrophy can occur across a wide range of loads when sets are performed with sufficient effort, which weakens the heavy-only myth.",
                    "The 2022 failure meta-analysis found no significant strength or hypertrophy advantage to taking every set to failure, which weakens the no-failure-no-results myth.",
                    "The 2024 concurrent training network meta-analysis suggests strength and endurance can coexist when planned intelligently, while the 2024 aerobic exercise dose-response review reinforces that fat-loss outcomes come from total repeated work rather than one magical format."
                ),
                advancedVariables = listOf(
                    "Use heavy loads when maximal strength is the goal, but do not confuse that with the only route to muscle growth.",
                    "Combine cardio and lifting with smart ordering, spacing, and recovery instead of treating them as mutually exclusive.",
                    "For body composition, focus on weekly nutrition adherence, daily movement, and training quality before specialty tactics."
                ),
                researchBackedTweaks = listOf(
                    "A method that is slightly less optimal on paper but far easier to sustain often wins in real life.",
                    "When a claim depends on a single variable while ignoring sleep, volume, and energy intake, it is usually oversimplified.",
                    "The myth-resistant approach is simple: ask what the mechanism is, what the evidence base says, and whether the tradeoff is worth it."
                )
            ),
            indiaFriendlyNotes = listOf(
                "Do not judge workouts by sweat alone, especially in hot weather where fluid loss rises fast.",
                "Walking, dumbbells, machines, and bodyweight drills are still valid even if they are less glamorous than internet challenge workouts.",
                "You can get excellent results with basic food, basic tools, and a better understanding of what actually matters."
            ),
            sources = listOf(loadHypertrophyReview, resistanceFailureMeta, concurrentTrainingReview, aerobicWeightLossReview),
            readTimeMinutes = 9,
            tags = listOf("myths", "science", "beginner", "critical thinking")
        )
    )
}

private fun beginner(
    simpleExplanation: String,
    whyItMatters: List<String>,
    stepByStep: List<String>,
    equipmentNeeded: List<String>,
    commonMistakes: List<String>,
    safetyTips: List<String>
) = BeginnerSection(
    simpleExplanation = simpleExplanation,
    whyItMatters = whyItMatters,
    stepByStep = stepByStep,
    equipmentNeeded = equipmentNeeded,
    commonMistakes = commonMistakes,
    safetyTips = safetyTips
)

private fun intermediate(
    progressGuidance: List<String>,
    keyPrinciples: List<String>,
    progressionExample: List<String>,
    weeklyIntegration: List<String>,
    trackingTips: List<String>
) = IntermediateSection(
    progressGuidance = progressGuidance,
    keyPrinciples = keyPrinciples,
    progressionExample = progressionExample,
    weeklyIntegration = weeklyIntegration,
    trackingTips = trackingTips
)

private fun expert(
    biomechanicsAndActivation: List<String>,
    latestEvidence: List<String>,
    advancedVariables: List<String>,
    researchBackedTweaks: List<String>
) = ExpertSection(
    biomechanicsAndActivation = biomechanicsAndActivation,
    latestEvidence = latestEvidence,
    advancedVariables = advancedVariables,
    researchBackedTweaks = researchBackedTweaks
)

private fun source(
    title: String,
    citation: String,
    url: String
) = ArticleSource(
    title = title,
    citation = citation,
    url = url
)
