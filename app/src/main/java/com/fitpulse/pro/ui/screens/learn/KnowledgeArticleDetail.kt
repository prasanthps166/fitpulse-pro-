package com.fitpulse.pro.ui.screens.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitpulse.pro.data.model.FitnessArticle
import com.fitpulse.pro.ui.components.FitPulseChip
import com.fitpulse.pro.ui.theme.Border
import com.fitpulse.pro.ui.theme.FitPulseTheme
import com.fitpulse.pro.ui.theme.FitPulseTypography
import com.fitpulse.pro.ui.theme.Primary

@Composable
internal fun KnowledgeArticleDetail(
    article: FitnessArticle,
    isSaved: Boolean,
    onToggleSaved: () -> Unit,
    relatedArticles: List<FitnessArticle>,
    onNavigateToArticle: (String) -> Unit,
    onBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val categoryColor = article.category.accentColor()
    val categoryIcon = article.category.icon()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 8.dp, end = 20.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = FitPulseTheme.colors.textPrimary
                )
            }
            Text(
                "Knowledge Entry",
                style = FitPulseTypography.headlineMedium,
                color = FitPulseTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onToggleSaved) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isSaved) "Remove from saved" else "Save article",
                    tint = if (isSaved) Primary else FitPulseTheme.colors.textSecondary
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(categoryColor.copy(alpha = 0.28f), categoryColor.copy(alpha = 0.08f))
                        )
                    )
                    .border(1.dp, categoryColor.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(categoryColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            categoryIcon,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = article.title,
                        style = FitPulseTypography.displaySmall,
                        color = FitPulseTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = article.quickTakeaway,
                        style = FitPulseTypography.bodyLarge,
                        color = FitPulseTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        contentPadding = PaddingValues(end = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FitPulseChip(
                                text = article.category.displayName(),
                                selected = true,
                                color = categoryColor
                            )
                        }
                        item {
                            FitPulseChip(
                                text = "Updated ${article.lastUpdated}",
                                selected = true,
                                color = Primary
                            )
                        }
                        item {
                            FitPulseChip(
                                text = "${article.readTimeMinutes} min read",
                                selected = true,
                                color = categoryColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FitPulseChip(
                                text = "${article.primaryLevel.displayName()} entry",
                                selected = true,
                                color = article.primaryLevel.accentColor()
                            )
                        }
                        item {
                            FitPulseChip(
                                text = "Covers ${article.coverageLabel()}",
                                selected = true,
                                color = categoryColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Beginner Section")
            ParagraphCard("Simple explanation", article.beginner.simpleExplanation)
            BulletCard("Why this matters", article.beginner.whyItMatters)
            BulletCard("How to do it", article.beginner.stepByStep)
            BulletCard("Equipment needed", article.beginner.equipmentNeeded)
            BulletCard("Common mistakes and easy fixes", article.beginner.commonMistakes)
            BulletCard("Safety tips for total newbies", article.beginner.safetyTips)

            SectionTitle("Intermediate Section")
            BulletCard("How to progress", article.intermediate.progressGuidance)
            BulletCard("Key principles behind it", article.intermediate.keyPrinciples)
            BulletCard("Sample progression", article.intermediate.progressionExample)
            BulletCard("Integration into weekly routine", article.intermediate.weeklyIntegration)
            BulletCard("Tracking tips", article.intermediate.trackingTips)

            SectionTitle("Expert Section")
            BulletCard("Biomechanics and muscle activation details", article.expert.biomechanicsAndActivation)
            BulletCard("Latest evidence", article.expert.latestEvidence)
            BulletCard("Advanced variables", article.expert.advancedVariables)
            BulletCard("Research-backed tweaks", article.expert.researchBackedTweaks)
            BulletCard("References", article.sources.map { "${it.citation} ${it.title}" })

            if (article.indiaFriendlyNotes.isNotEmpty()) {
                SectionTitle("India-Friendly Notes")
                BulletCard("Adaptations, food options, and budget notes", article.indiaFriendlyNotes)
            }

            if (relatedArticles.isNotEmpty()) {
                SectionTitle("Read Next")
                relatedArticles.forEach { relatedArticle ->
                    RelatedGuideCard(
                        article = relatedArticle,
                        onClick = { onNavigateToArticle(relatedArticle.id) }
                    )
                }
            }

            SectionTitle("Sources and Last Updated")
            article.sources.forEach { source ->
                SourceCard(
                    title = source.title,
                    citation = source.citation,
                    onClick = { uriHandler.openUri(source.url) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Date written/updated: ${article.lastUpdated}",
                style = FitPulseTypography.bodySmall,
                color = FitPulseTheme.colors.textSecondary
            )

            SectionTitle("Disclaimer")
            ParagraphCard("Educational use only", article.disclaimer)

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = text,
        style = FitPulseTypography.headlineSmall,
        color = FitPulseTheme.colors.textPrimary,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun ParagraphCard(
    title: String,
    text: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.75f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = FitPulseTypography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = FitPulseTypography.bodyMedium,
                color = FitPulseTheme.colors.textPrimary
            )
        }
    }
}

@Composable
private fun BulletCard(
    title: String,
    items: List<String>
) {
    if (items.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.75f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = FitPulseTypography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "-",
                        style = FitPulseTypography.bodyMedium,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = item,
                        style = FitPulseTypography.bodyMedium,
                        color = FitPulseTheme.colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceCard(
    title: String,
    citation: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.72f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = FitPulseTypography.titleMedium,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open source",
                    tint = Primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = citation,
                style = FitPulseTypography.bodySmall,
                color = FitPulseTheme.colors.textSecondary
            )
        }
    }
    HorizontalDivider(color = Border.copy(alpha = 0.15f))
}

@Composable
private fun RelatedGuideCard(
    article: FitnessArticle,
    onClick: () -> Unit
) {
    val categoryColor = article.category.accentColor()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.75f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(categoryColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = article.category.icon(),
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    style = FitPulseTypography.titleMedium,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.quickTakeaway,
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FitPulseChip(
                            text = article.category.displayName(),
                            selected = true,
                            color = categoryColor
                        )
                    }
                    item {
                        FitPulseChip(
                            text = "${article.readTimeMinutes} min",
                            selected = true,
                            color = Primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = FitPulseTheme.colors.textTertiary
            )
        }
    }
}
