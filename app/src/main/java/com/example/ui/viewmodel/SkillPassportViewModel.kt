package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.ExtractedSkill
import com.example.data.model.MatchRecommendation
import com.example.data.model.Opportunity
import com.example.data.model.SkillNode
import com.example.data.model.TeamProject
import com.example.data.model.UserProfile
import com.example.data.model.VerifiablePassport
import com.example.data.model.VerificationStatus
import com.example.data.repository.PassportRepository
import com.example.service.GeminiService
import com.example.service.MatchingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    PASSPORT("Passport"),
    MATCHES("Matches"),
    TEAM_SYNERGY("Team Synergy"),
    EVIDENCE_HUB("Add Evidence"),
    FAIRNESS_AUDIT("Fairness Audit"),
    RECRUITER_BLIND("Blind Recruiter")
}

class SkillPassportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PassportRepository
    val userProfile: StateFlow<UserProfile?>
    val allEvidence: StateFlow<List<EvidenceItem>>
    val allSkills: StateFlow<List<SkillNode>>
    val allOpportunities: StateFlow<List<Opportunity>>
    val allTeamProjects: StateFlow<List<TeamProject>>

    val selectedCategoryFilter = MutableStateFlow<EvidenceCategory?>(null)
    val currentTab = MutableStateFlow(AppTab.PASSPORT)

    val selectedMatchForExplainer = MutableStateFlow<MatchRecommendation?>(null)
    val selectedEvidenceForProof = MutableStateFlow<EvidenceItem?>(null)
    val isExportPassportDialogOpen = MutableStateFlow(false)
    val isAddEvidenceModalOpen = MutableStateFlow(false)

    val aiRoadmapLoading = MutableStateFlow(false)
    val aiRoadmapContent = MutableStateFlow<String?>(null)

    val aiParsingLoading = MutableStateFlow(false)
    val aiParsedSkills = MutableStateFlow<List<ExtractedSkill>>(emptyList())

    val showToastMessage = MutableStateFlow<String?>(null)

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = PassportRepository(database.skillPassportDao())

        userProfile = repository.userProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        allEvidence = repository.allEvidence.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allSkills = repository.allSkills.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allOpportunities = repository.allOpportunities.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allTeamProjects = repository.allTeamProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Dynamic match recommendations computed strictly using verified skills & evidence
    val matchRecommendations: StateFlow<List<MatchRecommendation>> = combine(
        allOpportunities,
        allSkills,
        allEvidence
    ) { opportunities, skills, evidence ->
        opportunities.map { opp ->
            MatchingEngine.computeMatch(opp, skills, evidence)
        }.sortedByDescending { it.matchScore }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setCategoryFilter(category: EvidenceCategory?) {
        selectedCategoryFilter.value = category
    }

    fun setTab(tab: AppTab) {
        currentTab.value = tab
    }

    fun toggleBlindMatching(active: Boolean) {
        viewModelScope.launch {
            repository.toggleBlindMatching(active)
            showToastMessage.value = if (active) {
                "🛡️ Blind Mode Activated: Demographic attributes isolated from scoring."
            } else {
                "Standard Profile Mode Enabled."
            }
        }
    }

    fun toggleBookmark(opportunityId: Long, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(opportunityId, !isBookmarked)
        }
    }

    fun applyOpportunity(opportunityId: Long) {
        viewModelScope.launch {
            repository.toggleApplied(opportunityId, true)
            showToastMessage.value = "🚀 Blind Application Sent! Cryptographic Passport packet submitted."
        }
    }

    fun joinTeamRole(teamId: Long, roleTitle: String) {
        viewModelScope.launch {
            val alias = userProfile.value?.anonymizedAlias ?: "Candidate #CP-9104"
            repository.joinTeamRole(teamId, roleTitle, alias)
            showToastMessage.value = "🎉 Joined team role: $roleTitle! Cross-functional synergy updated."
        }
    }

    fun selectMatchForExplainer(match: MatchRecommendation?) {
        selectedMatchForExplainer.value = match
        aiRoadmapContent.value = null
    }

    fun selectEvidenceForProof(evidence: EvidenceItem?) {
        selectedEvidenceForProof.value = evidence
    }

    fun openExportPassport(open: Boolean) {
        isExportPassportDialogOpen.value = open
    }

    fun openAddEvidenceModal(open: Boolean) {
        isAddEvidenceModalOpen.value = open
        if (open) {
            aiParsedSkills.value = emptyList()
        }
    }

    fun parseEvidenceWithAI(text: String) {
        viewModelScope.launch {
            aiParsingLoading.value = true
            val skills = GeminiService.extractSkillsFromText(text)
            aiParsedSkills.value = skills
            aiParsingLoading.value = false
        }
    }

    fun generateAiRoadmap(recommendation: MatchRecommendation) {
        viewModelScope.launch {
            aiRoadmapLoading.value = true
            val missingSkillNames = recommendation.missingSkills.map { it.skillName }
            val roadmap = repository.generateAiRoadmap(recommendation.opportunity, missingSkillNames)
            aiRoadmapContent.value = roadmap
            aiRoadmapLoading.value = false
        }
    }

    fun saveNewEvidence(
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
        viewModelScope.launch {
            repository.addEvidence(
                category = category,
                title = title,
                issuerOrInstitution = issuerOrInstitution,
                completionDate = completionDate,
                verificationStatus = verificationStatus,
                verificationProofHash = verificationProofHash,
                verificationUrl = verificationUrl,
                gradeOrRanking = gradeOrRanking,
                description = description,
                extractedSkills = extractedSkills
            )
            isAddEvidenceModalOpen.value = false
            showToastMessage.value = "✨ New Evidence Minted & Verified! Skill matrix updated."
        }
    }

    fun deleteEvidence(id: Long) {
        viewModelScope.launch {
            repository.deleteEvidence(id)
            selectedEvidenceForProof.value = null
            showToastMessage.value = "Evidence removed from passport."
        }
    }

    fun clearToast() {
        showToastMessage.value = null
    }
}
