package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SkillDomain(val displayName: String) {
    SYSTEMS("Systems & Cloud"),
    AI_ML("AI & Machine Learning"),
    DATA("Data Engineering & Analytics"),
    HARDWARE_IOT("Hardware & Embedded"),
    DESIGN_HCI("Product Design & HCI"),
    COLLABORATION("Leadership & Multidisciplinary")
}

@Entity(tableName = "skills")
data class SkillNode(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long = 1,
    val name: String,
    val domain: SkillDomain,
    val masteryLevel: String, // Foundational (1), Applied (2), Advanced (3), Mastery (4)
    val confidenceScore: Int, // 0 - 100
    val verifiedEvidenceCount: Int,
    val supportingEvidenceTitlesJson: String, // JSON array of strings
    val primaryEvidenceCategory: EvidenceCategory,
    val verificationStamp: String
)
