package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.SkillNode
import com.example.data.model.UserProfile
import com.example.ui.components.EvidenceCard
import com.example.ui.components.PassportCard
import com.example.ui.components.SkillMatrixView
import com.example.ui.theme.*

@Composable
fun PassportScreen(
    userProfile: UserProfile?,
    evidenceList: List<EvidenceItem>,
    skillsList: List<SkillNode>,
    selectedCategory: EvidenceCategory?,
    onSelectCategory: (EvidenceCategory?) -> Unit,
    onInspectProof: (EvidenceItem) -> Unit,
    onExportPassport: () -> Unit,
    onAddEvidenceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredEvidence = if (selectedCategory == null) {
        evidenceList
    } else {
        evidenceList.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("passport_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Digital Passport Card Header
        item {
            if (userProfile != null) {
                PassportCard(
                    userProfile = userProfile,
                    evidenceList = evidenceList,
                    onExportClick = onExportPassport
                )
            }
        }

        // Category Filter Chips
        item {
            Column {
                Text(
                    text = "FILTER EVIDENCE LEDGER",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterPill(
                        label = "All (${evidenceList.size})",
                        isSelected = selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                        modifier = Modifier.weight(1f)
                    )
                    EvidenceCategory.values().forEach { cat ->
                        val count = evidenceList.count { it.category == cat }
                        FilterPill(
                            label = "${cat.displayName.split(" ").first()} ($count)",
                            isSelected = selectedCategory == cat,
                            onClick = { onSelectCategory(cat) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section Title: Evidence Items
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VERIFIED EVIDENCE ITEMS (${filteredEvidence.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = onAddEvidenceClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = CyanPrimary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Evidence", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Evidence Cards
        if (filteredEvidence.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "No Evidence",
                            tint = TextSubtle,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No evidence found in this category",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(filteredEvidence, key = { it.id }) { item ->
                EvidenceCard(
                    item = item,
                    onInspectProof = onInspectProof
                )
            }
        }

        // Minted Skill Taxonomy Matrix
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SkillMatrixView(skills = skillsList)
        }
    }
}

@Composable
fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) CyanPrimary.copy(alpha = 0.2f) else NavySurface,
        border = BorderStroke(1.dp, if (isSelected) CyanPrimary else NavyBorder),
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp)),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) CyanPrimary else TextMuted,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 9.5.sp,
                maxLines = 1
            )
        }
    }
}
