package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchRecommendation
import com.example.data.model.OpportunityType
import com.example.ui.theme.*

@Composable
fun MatchScoreCard(
    recommendation: MatchRecommendation,
    onExplainClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val opp = recommendation.opportunity
    val isTeam = opp.type == OpportunityType.MULTIDISCIPLINARY_TEAM

    val scoreColor = when {
        recommendation.matchScore >= 90 -> EmeraldSuccess
        recommendation.matchScore >= 78 -> CyanPrimary
        else -> AmberWarning
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onExplainClick() }
            .testTag("match_card_${opp.id}"),
        shape = RoundedCornerShape(18.dp),
        color = NavySurface,
        border = BorderStroke(1.2.dp, if (opp.isBookmarked) CyanPrimary.copy(alpha = 0.6f) else NavyBorder),
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Top Row: Type tag, Blind badge, and Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Type Tag
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isTeam) PurpleAccent.copy(alpha = 0.15f) else BlueInfo.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, if (isTeam) PurpleAccent.copy(alpha = 0.4f) else BlueInfo.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isTeam) "MULTIDISCIPLINARY TEAM" else "INTERNSHIP",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isTeam) PurpleGlow else BlueInfo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    // Blind Verification Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NavyDark,
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Blind Matched",
                                tint = CyanPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "BLIND MATCHED",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bookmark Icon
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (opp.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save Match",
                        tint = if (opp.isBookmarked) CyanPrimary else TextSubtle,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Content & Score Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = opp.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${opp.hostOrganization} • ${opp.departmentOrDomain}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Circular Match Score Component
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = scoreColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.5.dp, scoreColor.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${recommendation.matchScore}%",
                            style = MaterialTheme.typography.titleLarge,
                            color = scoreColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MATCH",
                            style = MaterialTheme.typography.labelSmall,
                            color = scoreColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Location & Compensation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = TextSubtle,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = opp.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Stipend",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = opp.compensationOrGrant,
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Evidence Supporting Match Highlights
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = NavyDark,
                border = BorderStroke(1.dp, NavyBorder.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Evidence Support",
                                tint = CyanPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "EVIDENCE BACKING (${recommendation.matchedSkills.size} Competencies):",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanPrimary,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = recommendation.matchTier,
                            style = MaterialTheme.typography.labelSmall,
                            color = scoreColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Matched competencies pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        recommendation.matchedSkills.take(3).forEach { matched ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldSuccess.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "✓ ${matched.skillName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSuccess,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Missing Skills Alert
                    if (recommendation.missingSkills.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = "Missing Skill Gap",
                                tint = AmberWarning,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Skill Gap: ${recommendation.missingSkills.first().skillName} (Bridging Available)",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmberWarning,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button: Explain Match & Evidence
            Button(
                onClick = onExplainClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("explain_match_button_${opp.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavySurfaceVariant,
                    contentColor = CyanPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Explain Match",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Inspect Evidence Match & Roadmap",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
