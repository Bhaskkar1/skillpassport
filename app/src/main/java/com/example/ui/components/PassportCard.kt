package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun PassportCard(
    userProfile: UserProfile,
    evidenceList: List<EvidenceItem>,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val courseworkCount = evidenceList.count { it.category == EvidenceCategory.COURSEWORK }
    val projectCount = evidenceList.count { it.category == EvidenceCategory.PROJECT }
    val contestCount = evidenceList.count { it.category == EvidenceCategory.COMPETITION }
    val credCount = evidenceList.count { it.category == EvidenceCategory.MICRO_CREDENTIAL }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("passport_card"),
        shape = RoundedCornerShape(20.dp),
        color = NavySurface,
        border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(CyanPrimary.copy(alpha = 0.8f), PurpleAccent.copy(alpha = 0.6f)))),
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NavySurfaceVariant.copy(alpha = 0.9f),
                            NavySurface.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header: Identity & Blind Protection Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Cryptographic DID",
                                tint = CyanPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PORTABLE SKILL PASSPORT",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanPrimary,
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = userProfile.anonymizedAlias,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // QR / Export button
                    FilledTonalIconButton(
                        onClick = onExportClick,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = NavySurfaceVariant,
                            contentColor = CyanPrimary
                        ),
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("export_passport_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Export & Share Passport",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Discipline & DID Seal
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyDark.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PRIMARY DOMAIN",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                            Text(
                                text = userProfile.primaryDiscipline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Text(
                                text = userProfile.passportDid,
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanGlow,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }

                        // Verified Status Stamp
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldSuccess.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Seal",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "W3C VC ACTIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Evidence Count Breakdown Chips
                Text(
                    text = "VERIFIED EVIDENCE LEDGER (${evidenceList.size} TOTAL)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EvidenceStatChip(
                        label = "Coursework",
                        count = courseworkCount,
                        color = CourseworkColor,
                        modifier = Modifier.weight(1f)
                    )
                    EvidenceStatChip(
                        label = "Projects",
                        count = projectCount,
                        color = ProjectColor,
                        modifier = Modifier.weight(1f)
                    )
                    EvidenceStatChip(
                        label = "Contests",
                        count = contestCount,
                        color = CompetitionColor,
                        modifier = Modifier.weight(1f)
                    )
                    EvidenceStatChip(
                        label = "Credentials",
                        count = credCount,
                        color = CredentialColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Blind Matching Guarantee Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PurpleAccent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Algorithmic Fairness",
                            tint = PurpleGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Algorithmic Blind Shield: Protected traits strictly excluded from matching.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EvidenceStatChip(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextWhite.copy(alpha = 0.85f),
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}
