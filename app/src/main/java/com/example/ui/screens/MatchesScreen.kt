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
import com.example.data.model.MatchRecommendation
import com.example.data.model.OpportunityType
import com.example.ui.components.MatchScoreCard
import com.example.ui.theme.*

@Composable
fun MatchesScreen(
    matchRecommendations: List<MatchRecommendation>,
    onExplainClick: (MatchRecommendation) -> Unit,
    onBookmarkToggle: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTypeFilter by remember { mutableStateOf<OpportunityType?>(null) }
    var showOnlyBookmarked by remember { mutableStateOf(false) }

    val filteredMatches = matchRecommendations.filter { match ->
        (selectedTypeFilter == null || match.opportunity.type == selectedTypeFilter) &&
        (!showOnlyBookmarked || match.opportunity.isBookmarked)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("matches_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Top Banner: Ethical Blind Matching Shield
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NavySurface,
                border = BorderStroke(1.2.dp, CyanPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = CyanPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ETHICAL BLIND MATCHING ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Recommendations are calculated 100% from verified evidence nodes. Zero demographic or prestige bias.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Filter Bar
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterPill(
                        label = "All (${matchRecommendations.size})",
                        isSelected = selectedTypeFilter == null && !showOnlyBookmarked,
                        onClick = {
                            selectedTypeFilter = null
                            showOnlyBookmarked = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                    FilterPill(
                        label = "Internships",
                        isSelected = selectedTypeFilter == OpportunityType.INTERNSHIP,
                        onClick = {
                            selectedTypeFilter = OpportunityType.INTERNSHIP
                            showOnlyBookmarked = false
                        },
                        modifier = Modifier.weight(1.2f)
                    )
                    FilterPill(
                        label = "Teams",
                        isSelected = selectedTypeFilter == OpportunityType.MULTIDISCIPLINARY_TEAM,
                        onClick = {
                            selectedTypeFilter = OpportunityType.MULTIDISCIPLINARY_TEAM
                            showOnlyBookmarked = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                    FilterPill(
                        label = "Saved",
                        isSelected = showOnlyBookmarked,
                        onClick = { showOnlyBookmarked = !showOnlyBookmarked },
                        modifier = Modifier.weight(0.9f)
                    )
                }
            }
        }

        // Match Results List
        if (filteredMatches.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No matches",
                            tint = TextSubtle,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matches found under this filter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(filteredMatches, key = { it.opportunity.id }) { match ->
                MatchScoreCard(
                    recommendation = match,
                    onExplainClick = { onExplainClick(match) },
                    onBookmarkClick = {
                        onBookmarkToggle(match.opportunity.id, match.opportunity.isBookmarked)
                    }
                )
            }
        }
    }
}
