package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
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
import com.example.data.model.SkillDomain
import com.example.data.model.SkillNode
import com.example.ui.theme.*

@Composable
fun SkillMatrixView(
    skills: List<SkillNode>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("skill_matrix_view")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = "Skills Matrix",
                    tint = CyanPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MINTED SKILL TAXONOMY (${skills.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = EmeraldSuccess.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "100% EVIDENCE-BACKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Group skills by Domain
        val groupedByDomain = skills.groupBy { it.domain }

        groupedByDomain.forEach { (domain, domainSkills) ->
            DomainSkillSection(domain = domain, skills = domainSkills)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun DomainSkillSection(
    domain: SkillDomain,
    skills: List<SkillNode>
) {
    val domainColor = when (domain) {
        SkillDomain.SYSTEMS -> CyanPrimary
        SkillDomain.AI_ML -> PurpleAccent
        SkillDomain.DATA -> BlueInfo
        SkillDomain.HARDWARE_IOT -> AmberWarning
        SkillDomain.DESIGN_HCI -> EmeraldSuccess
        SkillDomain.COLLABORATION -> Color(0xFFEC4899)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = NavySurface,
        border = BorderStroke(1.dp, NavyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Domain Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(domainColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = domain.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = domainColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = "${skills.size} Competencies",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                skills.forEach { skill ->
                    SkillItemRow(skill = skill, accentColor = domainColor)
                }
            }
        }
    }
}

@Composable
fun SkillItemRow(
    skill: SkillNode,
    accentColor: Color
) {
    val levelColor = when (skill.masteryLevel) {
        "Mastery" -> AmberWarning
        "Advanced" -> CyanPrimary
        "Applied" -> EmeraldSuccess
        else -> TextMuted
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = NavyDark,
        border = BorderStroke(1.dp, NavyBorder.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = levelColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, levelColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${skill.masteryLevel} (${skill.confidenceScore}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = levelColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Verification Stamp & Evidence Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Stamp",
                        tint = EmeraldGlow,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = skill.verificationStamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }

                Text(
                    text = "${skill.verifiedEvidenceCount} proofs",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanGlow,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
