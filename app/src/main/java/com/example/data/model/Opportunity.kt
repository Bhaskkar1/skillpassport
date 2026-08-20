package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OpportunityType(val displayName: String) {
    INTERNSHIP("Internship Role"),
    MULTIDISCIPLINARY_TEAM("Multidisciplinary Team Project")
}

data class RequiredSkill(
    val name: String,
    val minLevel: String, // Foundational, Applied, Advanced, Mastery
    val isEssential: Boolean // true = Essential, false = Beneficial
)

@Entity(tableName = "opportunities")
data class Opportunity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: OpportunityType,
    val title: String,
    val hostOrganization: String,
    val departmentOrDomain: String,
    val location: String,
    val compensationOrGrant: String, // e.g. "$58/hr + Relocation" or "$12k Innovation Fellowship"
    val duration: String, // e.g. "Summer 2026 (12 weeks)" or "Semester Capstone"
    val summary: String,
    val description: String,
    val requiredSkillsJson: String, // JSON array of RequiredSkill
    val targetDisciplinesJson: String, // JSON array of strings
    val ethicalBlindMatchingGuaranteed: Boolean = true,
    val totalApplicantsCount: Int = 24,
    val isBookmarked: Boolean = false,
    val hasApplied: Boolean = false
)
