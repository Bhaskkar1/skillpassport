package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchRecommendation
import com.example.data.model.OpportunityType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchExplainerBottomSheet(
    recommendation: MatchRecommendation,
    aiRoadmapLoading: Boolean,
    aiRoadmapContent: String?,
    onGenerateRoadmap: (MatchRecommendation) -> Unit,
    onApply: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val opp = recommendation.opportunity
    val isTeam = opp.type == OpportunityType.MULTIDISCIPLINARY_TEAM

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        contentColor = TextWhite,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = NavyBorder)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
                .testTag("match_explainer_sheet")
        ) {
            // Header: Title & Host
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTeam) "MULTIDISCIPLINARY TEAM MATCH EXPLAINER" else "INTERNSHIP MATCH EXPLAINER",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPrimary,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = opp.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${opp.hostOrganization} • ${opp.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldSuccess.copy(alpha = 0.15f),
                    border = BorderStroke(1.5.dp, EmeraldSuccess.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${recommendation.matchScore}%",
                            style = MaterialTheme.typography.titleLarge,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = recommendation.matchTier.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldSuccess,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Algorithmic Fairness Audit Certificate
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NavyDark,
                border = BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = PurpleGlow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ALGORITHMIC BLIND RANKING AUDIT",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                        Text(
                            text = recommendation.algorithmicFairnessAudit,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1: Evidence Supporting the Match
            Text(
                text = "1. VERIFIED EVIDENCE SUPPORTING MATCH (${recommendation.matchedSkills.size})",
                style = MaterialTheme.typography.labelMedium,
                color = CyanPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            recommendation.matchedSkills.forEach { matched ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NavySurfaceVariant,
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✓ ${matched.skillName}",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldSuccess.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "${matched.candidateMastery} (Req: ${matched.requiredLevel})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSuccess,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Proven by evidence nodes:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSubtle,
                            fontSize = 9.sp
                        )

                        matched.supportingEvidenceTitles.forEach { evTitle ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Proof",
                                    tint = CyanGlow,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = evTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 2: Missing Skills & Gap Analysis
            Text(
                text = "2. IDENTIFIED MISSING SKILLS & BRIDGING PATHWAYS (${recommendation.missingSkills.size})",
                style = MaterialTheme.typography.labelMedium,
                color = AmberWarning,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (recommendation.missingSkills.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldSuccess.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎉 Zero Missing Skills! Candidate exceeds all competency requirements.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EmeraldSuccess,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            } else {
                recommendation.missingSkills.forEach { gap ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurfaceVariant,
                        border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚠️ ${gap.skillName}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = AmberWarning,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = gap.importance,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Action to bridge: ${gap.bridgingAction}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextWhite,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "📚 Recommended: ${gap.suggestedResourceOrCourse}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanPrimary,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 3: AI-Powered Bridging Roadmap Generator
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NavyDark,
                border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Assistant",
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI 4-WEEK BRIDGING ROADMAP",
                                style = MaterialTheme.typography.labelMedium,
                                color = CyanPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!aiRoadmapLoading && aiRoadmapContent == null) {
                            Button(
                                onClick = { onGenerateRoadmap(recommendation) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanPrimary,
                                    contentColor = NavyDark
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("generate_ai_roadmap_button")
                            ) {
                                Text(
                                    text = "Generate AI Plan",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (aiRoadmapLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = CyanPrimary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Synthesizing personalized 4-week bridging roadmap with Gemini...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    } else if (aiRoadmapContent != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = aiRoadmapContent,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action: Blind Apply with Passport
            Button(
                onClick = {
                    onApply(opp.id)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("blind_apply_submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (opp.hasApplied) EmeraldSuccess else CyanPrimary,
                    contentColor = NavyDark
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (opp.hasApplied) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Apply",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (opp.hasApplied) "Applied (Blind Passport Sent)" else "Apply with Blind Skill Passport",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
