package com.example.service

import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.SkillNode
import com.example.data.model.UserProfile
import com.example.data.model.VerifiablePassport
import java.security.MessageDigest
import org.json.JSONArray
import org.json.JSONObject

object PassportVerificationEngine {

    fun generatePassport(
        profile: UserProfile,
        evidenceList: List<EvidenceItem>,
        skillsList: List<SkillNode>
    ): VerifiablePassport {
        val breakdown = EvidenceCategory.values().associateWith { cat ->
            evidenceList.count { it.category == cat }
        }

        val rawSignatureString = "${profile.passportDid}-${evidenceList.size}-${skillsList.size}-${evidenceList.sumOf { it.title.length }}"
        val checksum = sha256(rawSignatureString).take(16).uppercase()

        return VerifiablePassport(
            did = profile.passportDid,
            alias = profile.anonymizedAlias,
            issuanceDate = "2026-08-20 (Block #884910)",
            cryptographicChecksum = "0x$checksum",
            verifiedSkillCount = skillsList.size,
            totalVerifiedEvidenceCount = evidenceList.size,
            breakdownByCategory = breakdown,
            evidenceLedger = evidenceList,
            verifiedSkills = skillsList,
            complianceCert = "W3C Verifiable Credentials Data Model v2.0 / Open Badges 3.0"
        )
    }

    fun exportToW3CJsonLd(passport: VerifiablePassport, profile: UserProfile): String {
        val root = JSONObject()
        root.put("@context", JSONArray().apply {
            put("https://www.w3.org/ns/credentials/v2")
            put("https://credento.network/contexts/skill-passport/v1")
        })
        root.put("id", "urn:uuid:credento-${passport.did.removePrefix("did:credento:")}")
        root.put("type", JSONArray().apply {
            put("VerifiableCredential")
            put("VerifiedSkillPassport")
        })
        root.put("issuer", "did:credento:issuer:registrar-consortium")
        root.put("issuanceDate", "2026-08-20T08:00:00Z")

        val subject = JSONObject()
        subject.put("id", passport.did)
        subject.put("blindCandidateAlias", profile.anonymizedAlias)
        subject.put("primaryDiscipline", profile.primaryDiscipline)
        subject.put("secondaryDiscipline", profile.secondaryDiscipline)
        subject.put("overallCompetencyScore", profile.overallCompetencyScore)

        val skillsArray = JSONArray()
        passport.verifiedSkills.forEach { skill ->
            val sObj = JSONObject()
            sObj.put("skillName", skill.name)
            sObj.put("domain", skill.domain.displayName)
            sObj.put("masteryLevel", skill.masteryLevel)
            sObj.put("confidenceScore", "${skill.confidenceScore}%")
            sObj.put("verificationStamp", skill.verificationStamp)
            skillsArray.put(sObj)
        }
        subject.put("verifiedCompetencies", skillsArray)

        val evidenceArray = JSONArray()
        passport.evidenceLedger.forEach { ev ->
            val eObj = JSONObject()
            eObj.put("category", ev.category.displayName)
            eObj.put("title", ev.title)
            eObj.put("institution", ev.issuerOrInstitution)
            eObj.put("verificationStatus", ev.verificationStatus.displayName)
            eObj.put("proofHash", ev.verificationProofHash)
            eObj.put("gradeOrRanking", ev.gradeOrRanking)
            evidenceArray.put(eObj)
        }
        subject.put("evidenceLedger", evidenceArray)

        root.put("credentialSubject", subject)

        val proof = JSONObject()
        proof.put("type", "Ed25519Signature2020")
        proof.put("created", "2026-08-20T08:00:00Z")
        proof.put("verificationMethod", "did:credento:issuer:registrar-consortium#key-1")
        proof.put("proofPurpose", "assertionMethod")
        proof.put("jws", "eyJhbGciOiJFZERTQ...${passport.cryptographicChecksum}")
        root.put("proof", proof)

        return root.toString(2)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
