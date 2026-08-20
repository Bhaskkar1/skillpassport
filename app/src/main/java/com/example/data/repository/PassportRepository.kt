package com.example.data.repository

import com.example.data.local.SkillPassportDao
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.ExtractedSkill
import com.example.data.model.MatchRecommendation
import com.example.data.model.Opportunity
import com.example.data.model.SkillDomain
import com.example.data.model.SkillNode
import com.example.data.model.TeamProject
import com.example.data.model.UserProfile
import com.example.data.model.VerifiablePassport
import com.example.data.model.VerificationStatus
import com.example.service.GeminiService
import com.example.service.MatchingEngine
import com.example.service.PassportVerificationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray

class PassportRepository(
    private val dao: SkillPassportDao
) {
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allEvidence: Flow<List<EvidenceItem>> = dao.getAllEvidence()
    val allSkills: Flow<List<SkillNode>> = dao.getAllSkills()
    val allOpportunities: Flow<List<Opportunity>> = dao.getAllOpportunities()
    val allTeamProjects: Flow<List<TeamProject>> = dao.getAllTeamProjects()

    suspend fun addEvidence(
        category: EvidenceCategory,
        title: String,
        issuerOrInstitution: String,
        completionDate: String,
        verificationStatus: VerificationStatus,
        verificationProofHash: String,
        verificationUrl: String,
        gradeOrRanking: String,
        description: String,
        extractedSkills: List<ExtractedSkill>
    ) {
        val jsonArray = JSONArray()
        extractedSkills.forEach { skill ->
            val obj = org.json.JSONObject()
            obj.put("name", skill.name)
            obj.put("level", skill.level)
            obj.put("confidence", skill.confidence)
            obj.put("taxonomyCategory", skill.taxonomyCategory)
            jsonArray.put(obj)
        }

        val item = EvidenceItem(
            category = category,
            title = title,
            issuerOrInstitution = issuerOrInstitution,
            completionDate = completionDate,
            verificationStatus = verificationStatus,
            verificationProofHash = verificationProofHash,
            verificationUrl = verificationUrl,
            gradeOrRanking = gradeOrRanking,
            description = description,
            extractedSkillsJson = jsonArray.toString(),
            evidenceWeight = verificationStatus.trustWeight
        )

        dao.insertEvidence(item)
        recomputeSkillsFromEvidence()
    }

    suspend fun deleteEvidence(id: Long) {
        dao.deleteEvidence(id)
        recomputeSkillsFromEvidence()
    }

    suspend fun toggleBlindMatching(active: Boolean) {
        val current = dao.getUserProfile().firstOrNull() ?: return
        dao.insertOrUpdateProfile(current.copy(isBlindMatchingActive = active))
    }

    suspend fun toggleBookmark(opportunityId: Long, isBookmarked: Boolean) {
        dao.updateBookmark(opportunityId, isBookmarked)
    }

    suspend fun toggleApplied(opportunityId: Long, hasApplied: Boolean) {
        dao.updateApplied(opportunityId, hasApplied)
    }

    suspend fun joinTeamRole(teamId: Long, roleTitle: String, candidateAlias: String) {
        val team = dao.getTeamProjectById(teamId) ?: return
        try {
            val array = JSONArray(team.roleSlotsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                if (obj.getString("roleTitle") == roleTitle) {
                    obj.put("assignedMemberAlias", "$candidateAlias (You)")
                    obj.put("isFilled", true)
                }
            }
            dao.updateTeamProject(
                team.copy(
                    roleSlotsJson = array.toString(),
                    isUserMember = true,
                    synergyScore = (team.synergyScore + 5).coerceAtMost(98)
                )
            )
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun recomputeSkillsFromEvidence() {
        val evidenceList = dao.getAllEvidence().firstOrNull() ?: return
        val skillMap = mutableMapOf<String, MutableList<Pair<ExtractedSkill, EvidenceItem>>>()

        evidenceList.forEach { ev ->
            try {
                val array = JSONArray(ev.extractedSkillsJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val skill = ExtractedSkill(
                        name = obj.getString("name"),
                        level = obj.optString("level", "Applied"),
                        confidence = obj.optInt("confidence", 88),
                        taxonomyCategory = obj.optString("taxonomyCategory", "Systems")
                    )
                    skillMap.getOrPut(skill.name) { mutableListOf() }.add(Pair(skill, ev))
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        val aggregatedSkills = skillMap.map { (skillName, occurrences) ->
            val domain = when (occurrences.first().first.taxonomyCategory) {
                "Systems" -> SkillDomain.SYSTEMS
                "AI/ML" -> SkillDomain.AI_ML
                "Data" -> SkillDomain.DATA
                "Hardware/IoT" -> SkillDomain.HARDWARE_IOT
                "Design" -> SkillDomain.DESIGN_HCI
                else -> SkillDomain.COLLABORATION
            }

            val highestLevel = if (occurrences.size >= 3) "Mastery"
            else if (occurrences.size == 2) "Advanced"
            else occurrences.first().first.level

            val avgConfidence = occurrences.map { it.first.confidence }.average().toInt().coerceIn(75, 99)
            val titles = occurrences.map { it.second.title }.distinct()
            val titlesJson = JSONArray(titles).toString()

            val primaryCat = occurrences.first().second.category
            val stamp = when (occurrences.size) {
                1 -> "Single Source Verified (${occurrences.first().second.verificationStatus.displayName})"
                2 -> "Dual Source Verified (Coursework + Project Evidence)"
                else -> "Multi-Source Triangulated (3+ Verified Nodes)"
            }

            SkillNode(
                name = skillName,
                domain = domain,
                masteryLevel = highestLevel,
                confidenceScore = avgConfidence,
                verifiedEvidenceCount = occurrences.size,
                supportingEvidenceTitlesJson = titlesJson,
                primaryEvidenceCategory = primaryCat,
                verificationStamp = stamp
            )
        }

        dao.clearSkills()
        dao.insertSkills(aggregatedSkills)
    }

    suspend fun getVerifiablePassport(): VerifiablePassport? {
        val profile = dao.getUserProfile().firstOrNull() ?: return null
        val evidence = dao.getAllEvidence().firstOrNull() ?: emptyList()
        val skills = dao.getAllSkills().firstOrNull() ?: emptyList()
        return PassportVerificationEngine.generatePassport(profile, evidence, skills)
    }

    suspend fun generateAiRoadmap(opportunity: Opportunity, missingSkills: List<String>): String {
        val skills = dao.getAllSkills().firstOrNull() ?: emptyList()
        val skillNames = skills.map { it.name }
        return GeminiService.generateBridgingRoadmap(
            targetTitle = opportunity.title,
            missingSkills = missingSkills,
            candidateSkills = skillNames
        )
    }
}
