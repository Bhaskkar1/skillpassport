package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.model.TeamProject
import com.example.data.model.TeamRoleSlot
import com.example.ui.theme.*
import org.json.JSONArray

@Composable
fun TeamSynergyCard(
    team: TeamProject,
    onJoinRole: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val roles = parseRoleSlots(team.roleSlotsJson)
    val openRoles = roles.filter { !it.isFilled }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .testTag("team_card_${team.id}"),
        shape = RoundedCornerShape(18.dp),
        color = NavySurface,
        border = BorderStroke(1.2.dp, if (team.isUserMember) PurpleAccent.copy(alpha = 0.7f) else NavyBorder),
        tonalElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Domain, Diversity & Synergy Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Team",
                            tint = PurpleGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MULTIDISCIPLINARY SQUAD",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                        Text(
                            text = team.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Synergy Score
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PurpleAccent.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${team.synergyScore}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = PurpleGlow,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "SYNERGY",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleGlow,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = team.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Multidisciplinary Balance Meter
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NavyDark,
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Diversity3,
                            contentDescription = "Diversity",
                            tint = CyanPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${team.disciplineDiversityCount} Disciplines Represented",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = if (team.isUserMember) "✓ You are a Member" else "${openRoles.size} Open Slots",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (team.isUserMember) EmeraldSuccess else AmberWarning,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Role Slots
            Text(
                text = "TEAM ROSTER & SKILL COVERAGE:",
                style = MaterialTheme.typography.labelSmall,
                color = TextSubtle,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                roles.forEach { role ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (role.isFilled) NavySurfaceVariant else NavyDark,
                        border = BorderStroke(1.dp, if (role.isFilled) NavyBorder else CyanPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = role.roleTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextWhite,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${role.discipline}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted,
                                        fontSize = 9.5.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (role.isFilled) "Filled by: ${role.assignedMemberAlias}" else "Status: Open for matching",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (role.isFilled) EmeraldSuccess else CyanPrimary,
                                    fontSize = 10.sp
                                )
                            }

                            if (!role.isFilled && !team.isUserMember) {
                                Button(
                                    onClick = { onJoinRole(team.id, role.roleTitle) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyanPrimary,
                                        contentColor = NavyDark
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp).testTag("join_role_${role.roleTitle.take(5)}")
                                ) {
                                    Text(
                                        text = "Join Role",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseRoleSlots(json: String): List<TeamRoleSlot> {
    val list = mutableListOf<TeamRoleSlot>()
    try {
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val comps = mutableListOf<String>()
            val compsArray = obj.optJSONArray("requiredCompetencies")
            if (compsArray != null) {
                for (j in 0 until compsArray.length()) {
                    comps.add(compsArray.getString(j))
                }
            }
            list.add(
                TeamRoleSlot(
                    roleTitle = obj.getString("roleTitle"),
                    discipline = obj.optString("discipline", "Engineering"),
                    requiredCompetencies = comps,
                    assignedMemberAlias = if (obj.isNull("assignedMemberAlias")) null else obj.getString("assignedMemberAlias"),
                    isFilled = obj.optBoolean("isFilled", false)
                )
            )
        }
    } catch (e: Exception) {
        // Ignore
    }
    return list
}
