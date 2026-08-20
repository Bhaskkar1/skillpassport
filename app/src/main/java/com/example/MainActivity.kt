package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddEvidenceDialog
import com.example.ui.components.ExportPassportDialog
import com.example.ui.components.MatchExplainerBottomSheet
import com.example.ui.components.VerificationProofDialog
import com.example.ui.screens.FairnessAuditScreen
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.PassportScreen
import com.example.ui.screens.RecruiterBlindViewScreen
import com.example.ui.screens.TeamSynergyScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SkillPassportViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CredentoTheme {
                CredentoMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentoMainApp(
    viewModel: SkillPassportViewModel = viewModel()
) {
    val context = LocalContext.current

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val allEvidence by viewModel.allEvidence.collectAsStateWithLifecycle()
    val allSkills by viewModel.allSkills.collectAsStateWithLifecycle()
    val allOpportunities by viewModel.allOpportunities.collectAsStateWithLifecycle()
    val allTeamProjects by viewModel.allTeamProjects.collectAsStateWithLifecycle()
    val matchRecommendations by viewModel.matchRecommendations.collectAsStateWithLifecycle()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

    val selectedMatchForExplainer by viewModel.selectedMatchForExplainer.collectAsStateWithLifecycle()
    val selectedEvidenceForProof by viewModel.selectedEvidenceForProof.collectAsStateWithLifecycle()
    val isExportPassportDialogOpen by viewModel.isExportPassportDialogOpen.collectAsStateWithLifecycle()
    val isAddEvidenceModalOpen by viewModel.isAddEvidenceModalOpen.collectAsStateWithLifecycle()

    val aiRoadmapLoading by viewModel.aiRoadmapLoading.collectAsStateWithLifecycle()
    val aiRoadmapContent by viewModel.aiRoadmapContent.collectAsStateWithLifecycle()

    val aiParsingLoading by viewModel.aiParsingLoading.collectAsStateWithLifecycle()
    val aiParsedSkills by viewModel.aiParsedSkills.collectAsStateWithLifecycle()

    val toastMessage by viewModel.showToastMessage.collectAsStateWithLifecycle()

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = NavyDark,
        contentColor = TextWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Credento Logo",
                                tint = CyanPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CREDENTO",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CyanPrimary.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "PASSPORT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyanPrimary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = userProfile?.anonymizedAlias ?: "Verifiable Skill Ledger",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // Blind Mode Indicator Button
                    FilledTonalIconButton(
                        onClick = { viewModel.setTab(AppTab.FAIRNESS_AUDIT) },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = NavySurfaceVariant,
                            contentColor = if (userProfile?.isBlindMatchingActive == true) EmeraldSuccess else TextMuted
                        ),
                        modifier = Modifier.size(38.dp).testTag("top_bar_shield_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Fairness Audit",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // QR / Export button
                    IconButton(
                        onClick = { viewModel.openExportPassport(true) },
                        modifier = Modifier.testTag("top_bar_export_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Export Passport",
                            tint = CyanPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyDark,
                    titleContentColor = TextWhite
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavySurface,
                tonalElevation = 10.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.PASSPORT,
                    onClick = { viewModel.setTab(AppTab.PASSPORT) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Passport"
                        )
                    },
                    label = { Text("Passport", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanPrimary,
                        selectedTextColor = CyanPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = CyanPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_passport")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.MATCHES,
                    onClick = { viewModel.setTab(AppTab.MATCHES) },
                    icon = {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = CyanPrimary) {
                                    Text(matchRecommendations.size.toString(), color = NavyDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = "Matches"
                            )
                        }
                    },
                    label = { Text("Matches", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanPrimary,
                        selectedTextColor = CyanPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = CyanPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_matches")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.TEAM_SYNERGY,
                    onClick = { viewModel.setTab(AppTab.TEAM_SYNERGY) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Teams"
                        )
                    },
                    label = { Text("Teams", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleGlow,
                        selectedTextColor = PurpleGlow,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = PurpleAccent.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_teams")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.FAIRNESS_AUDIT,
                    onClick = { viewModel.setTab(AppTab.FAIRNESS_AUDIT) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = "Fairness Audit"
                        )
                    },
                    label = { Text("Fairness", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanPrimary,
                        selectedTextColor = CyanPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = CyanPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_fairness")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.RECRUITER_BLIND,
                    onClick = { viewModel.setTab(AppTab.RECRUITER_BLIND) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "Recruiter Blind"
                        )
                    },
                    label = { Text("Recruiter", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanPrimary,
                        selectedTextColor = CyanPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = CyanPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_recruiter")
                )
            }
        },
        floatingActionButton = {
            if (currentTab == AppTab.PASSPORT || currentTab == AppTab.MATCHES) {
                FloatingActionButton(
                    onClick = { viewModel.openAddEvidenceModal(true) },
                    containerColor = CyanPrimary,
                    contentColor = NavyDark,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("add_evidence_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Evidence",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Mint Evidence",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.PASSPORT -> {
                    PassportScreen(
                        userProfile = userProfile,
                        evidenceList = allEvidence,
                        skillsList = allSkills,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { viewModel.setCategoryFilter(it) },
                        onInspectProof = { viewModel.selectEvidenceForProof(it) },
                        onExportPassport = { viewModel.openExportPassport(true) },
                        onAddEvidenceClick = { viewModel.openAddEvidenceModal(true) }
                    )
                }
                AppTab.MATCHES -> {
                    MatchesScreen(
                        matchRecommendations = matchRecommendations,
                        onExplainClick = { viewModel.selectMatchForExplainer(it) },
                        onBookmarkToggle = { id, isBm -> viewModel.toggleBookmark(id, isBm) }
                    )
                }
                AppTab.TEAM_SYNERGY -> {
                    TeamSynergyScreen(
                        teamProjects = allTeamProjects,
                        onJoinRole = { teamId, role -> viewModel.joinTeamRole(teamId, role) }
                    )
                }
                AppTab.FAIRNESS_AUDIT -> {
                    FairnessAuditScreen(
                        userProfile = userProfile,
                        onToggleBlindMode = { viewModel.toggleBlindMatching(it) }
                    )
                }
                AppTab.RECRUITER_BLIND, AppTab.EVIDENCE_HUB -> {
                    RecruiterBlindViewScreen(
                        currentStudentSkills = allSkills,
                        onShowToast = { msg ->
                            viewModel.showToastMessage.value = msg
                        }
                    )
                }
            }
        }

        // Match Explainer Bottom Sheet (with Evidence Links, Missing Skills, and AI Roadmap)
        selectedMatchForExplainer?.let { recommendation ->
            MatchExplainerBottomSheet(
                recommendation = recommendation,
                aiRoadmapLoading = aiRoadmapLoading,
                aiRoadmapContent = aiRoadmapContent,
                onGenerateRoadmap = { rec -> viewModel.generateAiRoadmap(rec) },
                onApply = { oppId -> viewModel.applyOpportunity(oppId) },
                onDismiss = { viewModel.selectMatchForExplainer(null) }
            )
        }

        // Evidence Cryptographic Proof Dialog
        selectedEvidenceForProof?.let { item ->
            VerificationProofDialog(
                item = item,
                onDelete = { id -> viewModel.deleteEvidence(id) },
                onDismiss = { viewModel.selectEvidenceForProof(null) }
            )
        }

        // Export Passport Dialog (QR Code & W3C Verifiable Credentials JSON-LD)
        if (isExportPassportDialogOpen && userProfile != null) {
            ExportPassportDialog(
                userProfile = userProfile!!,
                evidenceList = allEvidence,
                skillsList = allSkills,
                onDismiss = { viewModel.openExportPassport(false) }
            )
        }

        // Add Evidence Dialog (with Gemini AI Auto-Parsing)
        if (isAddEvidenceModalOpen) {
            AddEvidenceDialog(
                aiParsingLoading = aiParsingLoading,
                aiParsedSkills = aiParsedSkills,
                onParseText = { text -> viewModel.parseEvidenceWithAI(text) },
                onSave = { category, title, issuer, completionDate, verificationStatus, proofHash, verificationUrl, gradeOrRanking, description, extractedSkills ->
                    viewModel.saveNewEvidence(
                        category, title, issuer, completionDate, verificationStatus, proofHash, verificationUrl, gradeOrRanking, description, extractedSkills
                    )
                },
                onDismiss = { viewModel.openAddEvidenceModal(false) }
            )
        }
    }
}
