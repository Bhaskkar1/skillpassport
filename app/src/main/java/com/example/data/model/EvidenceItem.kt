package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EvidenceCategory(val displayName: String, val iconName: String) {
    COURSEWORK("Coursework", "School"),
    PROJECT("Technical Project", "Code"),
    COMPETITION("Hackathon & Contest", "EmojiEvents"),
    MICRO_CREDENTIAL("Micro-Credential", "Verified")
}

enum class VerificationStatus(val displayName: String, val trustWeight: Float) {
    VERIFIED_REGISTRAR("Registrar Signed", 1.0f),
    VERIFIED_GITHUB("GitHub Commits / PRs", 0.95f),
    VERIFIED_ISSUER_HASH("Issuer Cert SHA-256", 0.98f),
    VERIFIED_PEER_REVIEW("Faculty & Peer Validated", 0.88f),
    PENDING_VERIFICATION("Verification Pending", 0.50f)
}

data class ExtractedSkill(
    val name: String,
    val level: String, // Foundational, Applied, Advanced, Mastery
    val confidence: Int, // 0 - 100%
    val taxonomyCategory: String // Systems, AI/ML, Data, Design, Hardware, Soft Skills
)

@Entity(tableName = "evidence_items")
data class EvidenceItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long = 1,
    val category: EvidenceCategory,
    val title: String,
    val issuerOrInstitution: String,
    val completionDate: String,
    val verificationStatus: VerificationStatus,
    val verificationProofHash: String, // e.g. "sha256:8b4e72a... / Registrar seal: UCB-CS-992"
    val verificationUrl: String, // e.g. "https://github.com/..." or "https://credentials.issuer.org/..."
    val gradeOrRanking: String, // "Grade: A+ (Top 2%)", "1st Place Winner / 350 Teams", "Score: 920/1000"
    val description: String,
    val extractedSkillsJson: String, // JSON array of ExtractedSkill
    val evidenceWeight: Float = 1.0f,
    val createdAt: Long = System.currentTimeMillis()
)
