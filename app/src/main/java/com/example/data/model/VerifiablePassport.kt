package com.example.data.model

data class VerifiablePassport(
    val did: String,
    val alias: String,
    val issuanceDate: String,
    val cryptographicChecksum: String,
    val verifiedSkillCount: Int,
    val totalVerifiedEvidenceCount: Int,
    val breakdownByCategory: Map<EvidenceCategory, Int>,
    val evidenceLedger: List<EvidenceItem>,
    val verifiedSkills: List<SkillNode>,
    val complianceCert: String = "W3C Verifiable Credentials Standard v2.0 compliant"
)
