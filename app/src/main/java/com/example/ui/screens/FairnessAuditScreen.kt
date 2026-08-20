package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun FairnessAuditScreen(
    userProfile: UserProfile?,
    onToggleBlindMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBlindActive = userProfile?.isBlindMatchingActive ?: true

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("fairness_audit_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Shield Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NavySurface,
                border = BorderStroke(1.5.dp, PurpleAccent.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Fairness",
                                tint = PurpleGlow,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "ALGORITHMIC FAIRNESS AUDIT",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Switch(
                            checked = isBlindActive,
                            onCheckedChange = onToggleBlindMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NavyDark,
                                checkedTrackColor = CyanPrimary,
                                uncheckedTrackColor = NavyDark
                            ),
                            modifier = Modifier.testTag("blind_mode_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Credento is mathematically isolated from protected demographic traits and school pedigree bias. Rankings are 100% computed from verified cryptographic evidence ledger nodes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Section: Algorithmic Attribute Isolation Matrix
        item {
            Text(
                text = "ATTRIBUTE ISOLATION & WEIGHT AUDIT",
                style = MaterialTheme.typography.labelSmall,
                color = CyanPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NavySurface,
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AttributeAuditRow(
                        attributeName = "Verified Coursework & Transcripts",
                        weight = "35% (High Weight)",
                        status = "VALIDATED EVIDENCE",
                        isIncluded = true
                    )
                    AttributeAuditRow(
                        attributeName = "Technical Projects & GitHub Commits",
                        weight = "35% (High Weight)",
                        status = "VALIDATED EVIDENCE",
                        isIncluded = true
                    )
                    AttributeAuditRow(
                        attributeName = "Competitions & Micro-Credentials",
                        weight = "30% (High Weight)",
                        status = "VALIDATED EVIDENCE",
                        isIncluded = true
                    )
                    HorizontalDivider(color = NavyBorder, thickness = 1.dp)
                    AttributeAuditRow(
                        attributeName = "Gender & Demographic Identity",
                        weight = "0% (Zero Weight)",
                        status = "STRIPPED & BLOCKED",
                        isIncluded = false
                    )
                    AttributeAuditRow(
                        attributeName = "Race, Ethnicity & National Origin",
                        weight = "0% (Zero Weight)",
                        status = "STRIPPED & BLOCKED",
                        isIncluded = false
                    )
                    AttributeAuditRow(
                        attributeName = "Age, Graduation Year & Profile Photos",
                        weight = "0% (Zero Weight)",
                        status = "STRIPPED & BLOCKED",
                        isIncluded = false
                    )
                    AttributeAuditRow(
                        attributeName = "University Prestige / Ivy Pedigree Bias",
                        weight = "0% (Zero Weight)",
                        status = "ISOLATED (Competency Only)",
                        isIncluded = false
                    )
                }
            }
        }

        // Section: Traditional Resume vs Credento Passport Comparison
        item {
            Text(
                text = "TRADITIONAL HIRING VS CREDENTO PASSPORT",
                style = MaterialTheme.typography.labelSmall,
                color = PurpleGlow,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NavySurface,
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ComparisonItem(
                        title = "Unconscious Bias & Resume Screening",
                        traditional = "Heavily influenced by names, photos, zip codes, and university reputation.",
                        credento = "Strict pseudonymized alias (e.g. Candidate #CP-9104) with zero demographic fields."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ComparisonItem(
                        title = "Match Explainability & Evidence",
                        traditional = "Black-box keyword screening with no proof of actual student ability.",
                        credento = "Complete trace showing exact coursework, GitHub commit hashes, and contest proofs."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ComparisonItem(
                        title = "Skill Gap Transparency",
                        traditional = "Silent rejections without feedback on why a candidate fell short.",
                        credento = "Clear missing skill identification and personalized 4-week bridging roadmap."
                    )
                }
            }
        }
    }
}

@Composable
fun AttributeAuditRow(
    attributeName: String,
    weight: String,
    status: String,
    isIncluded: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = attributeName,
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Weight in Algorithm: $weight",
                style = MaterialTheme.typography.bodySmall,
                color = if (isIncluded) CyanPrimary else RoseError,
                fontSize = 10.5.sp
            )
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isIncluded) EmeraldSuccess.copy(alpha = 0.15f) else RoseError.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, if (isIncluded) EmeraldSuccess.copy(alpha = 0.4f) else RoseError.copy(alpha = 0.4f))
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = if (isIncluded) EmeraldSuccess else RoseError,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun ComparisonItem(
    title: String,
    traditional: String,
    credento: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RoseError.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, RoseError.copy(alpha = 0.3f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Legacy Resumes",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoseError,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = traditional,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldSuccess.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.3f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Credento Passport",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = credento,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhite,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
