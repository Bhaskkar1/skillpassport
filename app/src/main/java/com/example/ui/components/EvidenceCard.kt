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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.ExtractedSkill
import com.example.data.model.VerificationStatus
import com.example.ui.theme.*
import org.json.JSONArray

@Composable
fun EvidenceCard(
    item: EvidenceItem,
    onInspectProof: (EvidenceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (item.category) {
        EvidenceCategory.COURSEWORK -> CourseworkColor
        EvidenceCategory.PROJECT -> ProjectColor
        EvidenceCategory.COMPETITION -> CompetitionColor
        EvidenceCategory.MICRO_CREDENTIAL -> CredentialColor
    }

    val categoryIcon: ImageVector = when (item.category) {
        EvidenceCategory.COURSEWORK -> Icons.Default.School
        EvidenceCategory.PROJECT -> Icons.Default.Code
        EvidenceCategory.COMPETITION -> Icons.Default.EmojiEvents
        EvidenceCategory.MICRO_CREDENTIAL -> Icons.Default.Verified
    }

    val extractedSkills = parseSkills(item.extractedSkillsJson)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onInspectProof(item) }
            .testTag("evidence_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        color = NavySurface,
        border = BorderStroke(1.dp, NavyBorder),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Category tag & Verification Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = categoryColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = item.category.displayName,
                            tint = categoryColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.category.displayName.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }

                // Verification Stamp
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldSuccess.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Status",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = item.verificationStatus.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.Medium,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Issuer
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.issuerOrInstitution,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = item.completionDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSubtle
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grade / Performance badge
            if (item.gradeOrRanking.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NavyDark,
                    border = BorderStroke(1.dp, NavyBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Grade or Ranking",
                            tint = AmberWarning,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.gradeOrRanking,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Description
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 2,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Extracted Verified Skills Pills
            if (extractedSkills.isNotEmpty()) {
                Text(
                    text = "MINTED SKILLS (${extractedSkills.size}):",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSubtle,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    extractedSkills.take(3).forEach { skill ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanPrimary.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${skill.name} • ${skill.level}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanPrimary,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                    if (extractedSkills.size > 3) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NavySurfaceVariant,
                            border = BorderStroke(1.dp, NavyBorder)
                        ) {
                            Text(
                                text = "+${extractedSkills.size - 3} more",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Proof Hash Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(NavyDark)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Hash",
                        tint = CyanGlow,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.verificationProofHash,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                }
                Text(
                    text = "Verify Proof →",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
        }
    }
}

private fun parseSkills(json: String): List<ExtractedSkill> {
    val list = mutableListOf<ExtractedSkill>()
    try {
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                ExtractedSkill(
                    name = obj.getString("name"),
                    level = obj.optString("level", "Applied"),
                    confidence = obj.optInt("confidence", 90),
                    taxonomyCategory = obj.optString("taxonomyCategory", "Systems")
                )
            )
        }
    } catch (e: Exception) {
        // Ignore
    }
    return list
}
