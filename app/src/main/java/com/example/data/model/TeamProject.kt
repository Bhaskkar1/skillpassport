package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class TeamRoleSlot(
    val roleTitle: String,
    val discipline: String,
    val requiredCompetencies: List<String>,
    val assignedMemberAlias: String?, // null if open
    val isFilled: Boolean
)

@Entity(tableName = "team_projects")
data class TeamProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val domain: String, // e.g. "AI-Powered Bionic Prosthetics", "Clean Energy Smart Grid"
    val description: String,
    val leadOrganization: String,
    val synergyScore: Int, // 0 - 100%
    val disciplineDiversityCount: Int, // e.g. 4 disciplines represented
    val roleSlotsJson: String, // JSON array of TeamRoleSlot
    val coveredSkillsJson: String, // JSON array of strings
    val missingSynergySkillsJson: String, // JSON array of strings
    val isUserMember: Boolean = false,
    val status: String = "Recruiting Cross-Functional Talent"
)
