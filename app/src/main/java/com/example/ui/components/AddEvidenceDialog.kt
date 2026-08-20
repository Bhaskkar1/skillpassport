package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EvidenceCategory
import com.example.data.model.ExtractedSkill
import com.example.data.model.VerificationStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEvidenceDialog(
    aiParsingLoading: Boolean,
    aiParsedSkills: List<ExtractedSkill>,
    onParseText: (String) -> Unit,
    onSave: (
        category: EvidenceCategory,
        title: String,
        issuerOrInstitution: String,
        completionDate: String,
        verificationStatus: VerificationStatus,
        verificationProofHash: String,
        verificationUrl: String,
        gradeOrRanking: String,
        description: String,
        extractedSkills: List<ExtractedSkill>
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf(EvidenceCategory.PROJECT) }
    var title by remember { mutableStateOf("") }
    var issuer by remember { mutableStateOf("") }
    var completionDate by remember { mutableStateOf("May 2026") }
    var verificationStatus by remember { mutableStateOf(VerificationStatus.VERIFIED_GITHUB) }
    var proofHash by remember { mutableStateOf("sha256:d8a9f3... / Merged PR") }
    var gradeOrRanking by remember { mutableStateOf("Grade: A / Top 5%") }
    var description by remember { mutableStateOf("") }

    var customSkillsList by remember { mutableStateOf(mutableListOf<ExtractedSkill>()) }
    var newSkillName by remember { mutableStateOf("") }

    // When AI parsed skills change, update our list
    LaunchedEffect(aiParsedSkills) {
        if (aiParsedSkills.isNotEmpty()) {
            customSkillsList = aiParsedSkills.toMutableList()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NavySurface,
            border = BorderStroke(1.5.dp, CyanPrimary.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .testTag("add_evidence_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add Evidence",
                            tint = CyanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MINT EVIDENCE RECORD",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Category Selector
                    Text(
                        text = "EVIDENCE CATEGORY",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        EvidenceCategory.values().forEach { cat ->
                            val isSelected = category == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CyanPrimary.copy(alpha = 0.2f) else NavyDark,
                                border = BorderStroke(1.dp, if (isSelected) CyanPrimary else NavyBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                onClick = { category = cat }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = cat.displayName.split(" ").first(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) CyanPrimary else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title (e.g. Distributed KV Store in Rust)") },
                        modifier = Modifier.fillMaxWidth().testTag("evidence_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Institution / Issuer
                    OutlinedTextField(
                        value = issuer,
                        onValueChange = { issuer = it },
                        label = { Text("Issuer / University / Platform") },
                        modifier = Modifier.fillMaxWidth().testTag("evidence_issuer_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description / Deliverables
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Technical Deliverables") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("evidence_description_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // AI Auto-Extract Skills Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NavyDark,
                        border = BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.5f)),
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
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI",
                                        tint = PurpleGlow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI Skill Extractor (Gemini)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PurpleGlow,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = {
                                        val combined = "$title. $description"
                                        if (combined.isNotBlank()) {
                                            onParseText(combined)
                                        }
                                    },
                                    enabled = !aiParsingLoading && (title.isNotBlank() || description.isNotBlank()),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PurpleAccent,
                                        contentColor = TextWhite
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp).testTag("ai_extract_skills_button")
                                ) {
                                    if (aiParsingLoading) {
                                        CircularProgressIndicator(
                                            color = TextWhite,
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Extract Skills", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (customSkillsList.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    customSkillsList.forEach { skill ->
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = CyanPrimary.copy(alpha = 0.15f),
                                            border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f))
                                        ) {
                                            Text(
                                                text = "${skill.name} (${skill.level})",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CyanPrimary,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Outcome / Grade
                    OutlinedTextField(
                        value = gradeOrRanking,
                        onValueChange = { gradeOrRanking = it },
                        label = { Text("Grade / Ranking / Benchmark") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Proof Hash / Identifier
                    OutlinedTextField(
                        value = proofHash,
                        onValueChange = { proofHash = it },
                        label = { Text("Verification Proof / Git Hash / Seal") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action: Mint & Save
                Button(
                    onClick = {
                        val finalSkills = if (customSkillsList.isNotEmpty()) {
                            customSkillsList
                        } else {
                            listOf(
                                ExtractedSkill(
                                    name = if (title.isNotBlank()) title.take(20) else "Systems Engineering",
                                    level = "Advanced",
                                    confidence = 92,
                                    taxonomyCategory = "Systems"
                                )
                            )
                        }

                        onSave(
                            category,
                            if (title.isBlank()) "Verified Technical Milestone" else title,
                            if (issuer.isBlank()) "Autonomous Institution" else issuer,
                            completionDate,
                            verificationStatus,
                            proofHash,
                            "https://credento.network/verify",
                            gradeOrRanking,
                            if (description.isBlank()) "Verified deliverables with passing benchmarks." else description,
                            finalSkills
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_evidence_submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = NavyDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mint into Skill Passport",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
