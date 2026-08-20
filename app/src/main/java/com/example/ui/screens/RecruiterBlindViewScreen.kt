package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillNode
import com.example.ui.theme.*

data class BlindCandidate(
    val id: Long,
    val alias: String,
    val primaryDiscipline: String,
    val overallCompetencyScore: Int,
    val verifiedEvidenceCount: Int,
    val topVerifiedSkills: List<String>,
    val recentProofSnippet: String,
    val isShortlisted: Boolean = false
)

@Composable
fun RecruiterBlindViewScreen(
    currentStudentSkills: List<SkillNode>,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var candidates by remember {
        mutableStateOf(
            listOf(
                BlindCandidate(
                    id = 1,
                    alias = "Candidate #CP-9104 (Your Passport)",
                    primaryDiscipline = "Distributed Systems & Cloud Computing",
                    overallCompetencyScore = 92,
                    verifiedEvidenceCount = 10,
                    topVerifiedSkills = listOf("Rust (Mastery)", "Raft Consensus (Mastery)", "PyTorch (Advanced)", "AWS Cloud (Advanced)"),
                    recentProofSnippet = "AegisKV Rust LSM Engine (54 signed commits) + NASA Space Apps Global Finalist"
                ),
                BlindCandidate(
                    id = 2,
                    alias = "Candidate #BM-4102",
                    primaryDiscipline = "Biomedical Engineering & DSP",
                    overallCompetencyScore = 89,
                    verifiedEvidenceCount = 8,
                    topVerifiedSkills = listOf("EMG Signal Processing (Mastery)", "Clinical Protocols (Advanced)", "MATLAB & C++ (Advanced)"),
                    recentProofSnippet = "BioMech Lab Faculty-Signed Transcript + IEEE BME Paper Co-Author"
                ),
                BlindCandidate(
                    id = 3,
                    alias = "Candidate #AI-6691",
                    primaryDiscipline = "Machine Learning & Computer Vision",
                    overallCompetencyScore = 94,
                    verifiedEvidenceCount = 12,
                    topVerifiedSkills = listOf("PyTorch (Mastery)", "ONNX Quantization (Advanced)", "CUDA Kernels (Applied)"),
                    recentProofSnippet = "CVPR Student Workshop Winner + Kaggle Grandmaster Micro-Credential"
                ),
                BlindCandidate(
                    id = 4,
                    alias = "Candidate #UX-2048",
                    primaryDiscipline = "Human-Computer Interaction & Accessibility",
                    overallCompetencyScore = 90,
                    verifiedEvidenceCount = 7,
                    topVerifiedSkills = listOf("Jetpack Compose (Mastery)", "WCAG 2.2 AAA (Mastery)", "User Usability Testing (Advanced)"),
                    recentProofSnippet = "OmniCanvas Accessible PR Maintainer Merged + Google UX Design Cert"
                )
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("recruiter_blind_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Recruiter Portal Header
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NavySurface,
                border = BorderStroke(1.2.dp, CyanPrimary.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Blind View",
                        tint = CyanPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "RECRUITER & LEAD BLIND TALENT POOL",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Evaluate candidates strictly by cryptographic competency evidence, verified code repos, and contest achievements without demographic bias.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Candidates List
        items(candidates, key = { it.id }) { candidate ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NavySurface,
                border = BorderStroke(1.dp, if (candidate.isShortlisted) EmeraldSuccess else NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = candidate.alias,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = candidate.primaryDiscipline,
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldSuccess.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${candidate.overallCompetencyScore}% Score",
                                style = MaterialTheme.typography.titleSmall,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "VERIFIED EVIDENCE PROOFS (${candidate.verifiedEvidenceCount} Total):",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSubtle,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = candidate.recentProofSnippet,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Top Skills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        candidate.topVerifiedSkills.take(3).forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NavyDark,
                                border = BorderStroke(1.dp, NavyBorder)
                            ) {
                                Text(
                                    text = skill,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextWhite,
                                    fontSize = 9.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shortlist / Contact Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                candidates = candidates.map {
                                    if (it.id == candidate.id) it.copy(isShortlisted = !it.isShortlisted) else it
                                }
                                onShowToast(if (!candidate.isShortlisted) "Shortlisted ${candidate.alias}" else "Removed from shortlist")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (candidate.isShortlisted) EmeraldSuccess else NavyBorder)
                        ) {
                            Text(
                                text = if (candidate.isShortlisted) "✓ Shortlisted" else "Shortlist",
                                color = if (candidate.isShortlisted) EmeraldSuccess else TextWhite,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                onShowToast("Blind Interview Request sent to ${candidate.alias} via cryptographic relay.")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = NavyDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Request Interview", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
