package com.example.data.model

data class MatchedSkillEvidence(
    val skillName: String,
    val candidateMastery: String,
    val requiredLevel: String,
    val supportingEvidenceTitles: List<String>,
    val evidenceTrustScore: Float,
    val matchStrength: String // "Full Mastery", "Applied Match", "Exceeds Requirement"
)

data class MissingSkillGap(
    val skillName: String,
    val importance: String, // "Critical Requirement" or "Recommended Addition"
    val bridgingAction: String, // e.g. "Complete 1 micro-project in Container Orchestration"
    val suggestedResourceOrCourse: String // e.g. "Interactive Lab: Docker & K8s Architecture (12h)"
)

data class MatchRecommendation(
    val opportunity: Opportunity,
    val matchScore: Int, // 0 - 100% computed purely from verified evidence
    val matchTier: String, // "Strong Fit", "Target Match", "High-Growth Complement"
    val matchedSkills: List<MatchedSkillEvidence>,
    val missingSkills: List<MissingSkillGap>,
    val evidenceDensityRatio: String, // e.g. "5/6 Verified Evidence Links"
    val algorithmicFairnessAudit: String, // "Audited: 0% weight on demographic/institution traits. 100% competency-proven."
    val multidisciplinarySynergyNotes: String? = null
)
