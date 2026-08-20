package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.Opportunity
import com.example.data.model.OpportunityType
import com.example.data.model.SkillDomain
import com.example.data.model.SkillNode
import com.example.data.model.TeamProject
import com.example.data.model.UserProfile
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        EvidenceItem::class,
        SkillNode::class,
        Opportunity::class,
        TeamProject::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun skillPassportDao(): SkillPassportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "credento_skill_passport.db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.skillPassportDao())
                }
            }
        }

        suspend fun populateDatabase(dao: SkillPassportDao) {
            // Seed User Profile
            val profile = UserProfile(
                id = 1,
                fullName = "Alex Rivera",
                anonymizedAlias = "Candidate #CP-9104",
                email = "alex.rivera@berkeley.edu",
                primaryDiscipline = "Distributed Systems & Cloud Computing",
                secondaryDiscipline = "Edge AI & Real-time Robotics",
                headline = "Systems Engineer specializing in high-throughput consensus, fault tolerance, and embedded edge perception.",
                bio = "Passionate about bridging low-level systems with verifiable cross-disciplinary impact. Track record of peer-verified systems projects and top competition finishes.",
                graduationYear = "Class of 2026",
                university = "UC Berkeley (EECS)",
                passportDid = "did:credento:9f8a3d1b7e40c88a",
                isBlindMatchingActive = true,
                totalVerifiedEvidenceCount = 10,
                overallCompetencyScore = 92
            )
            dao.insertOrUpdateProfile(profile)

            // Seed Evidence Items
            val evidenceList = listOf(
                EvidenceItem(
                    id = 1,
                    category = EvidenceCategory.COURSEWORK,
                    title = "CS 452: Distributed Systems & Consensus Protocols",
                    issuerOrInstitution = "UC Berkeley College of Engineering",
                    completionDate = "May 2025",
                    verificationStatus = VerificationStatus.VERIFIED_REGISTRAR,
                    verificationProofHash = "REG-UCB-994821: SHA256(e9f8a4...7b2c)",
                    verificationUrl = "https://registrar.berkeley.edu/verify/994821",
                    gradeOrRanking = "Grade: A+ (Top 2% of 240 Students)",
                    description = "Implemented Raft distributed consensus from scratch, partitioned key-value storage with snapshotting, and vector-clock causal ordering across 10-node Byzantine clusters.",
                    extractedSkillsJson = """[{"name":"Raft Consensus","level":"Advanced","confidence":96,"taxonomyCategory":"Systems"},{"name":"Distributed Storage","level":"Advanced","confidence":94,"taxonomyCategory":"Systems"},{"name":"Fault Tolerance","level":"Advanced","confidence":92,"taxonomyCategory":"Systems"},{"name":"Rust","level":"Applied","confidence":89,"taxonomyCategory":"Systems"}]""",
                    evidenceWeight = 1.0f
                ),
                EvidenceItem(
                    id = 2,
                    category = EvidenceCategory.PROJECT,
                    title = "AegisKV: Fault-Tolerant Distributed Storage Engine",
                    issuerOrInstitution = "Open Source Systems Project / GitHub",
                    completionDate = "Dec 2025",
                    verificationStatus = VerificationStatus.VERIFIED_GITHUB,
                    verificationProofHash = "git:commit/7a8e9d4 (54 signed commits, 100% CI pass)",
                    verificationUrl = "https://github.com/alex-rivera/aegiskv-raft-rust",
                    gradeOrRanking = "Production Benchmark: 120k ops/sec with sub-5ms p99 latency",
                    description = "Engineered an asynchronous high-throughput LSM-tree and Raft consensus engine in Rust. Validated using Jepsen fault-injection testing under split-brain partitions.",
                    extractedSkillsJson = """[{"name":"Rust","level":"Mastery","confidence":98,"taxonomyCategory":"Systems"},{"name":"Raft Consensus","level":"Mastery","confidence":95,"taxonomyCategory":"Systems"},{"name":"gRPC & Protobuf","level":"Advanced","confidence":90,"taxonomyCategory":"Systems"},{"name":"Benchmarking","level":"Advanced","confidence":91,"taxonomyCategory":"Systems"}]""",
                    evidenceWeight = 0.96f
                ),
                EvidenceItem(
                    id = 3,
                    category = EvidenceCategory.COMPETITION,
                    title = "NASA Space Apps Challenge 2025: Autonomous Lunar Grid",
                    issuerOrInstitution = "NASA Space Apps Global Hackathon",
                    completionDate = "Oct 2025",
                    verificationStatus = VerificationStatus.VERIFIED_ISSUER_HASH,
                    verificationProofHash = "NASA-CERT-2025-GLO-8819: SHA256(3d1a9f...c4e2)",
                    verificationUrl = "https://spaceappschallenge.org/2025/awards/autonomous-grid",
                    gradeOrRanking = "Global Finalist: Top 3 of 480 International Teams",
                    description = "Led a 4-person multidisciplinary team (Systems, Aerospace, UI/UX) developing a decentralized peer-to-peer energy routing protocol for lunar surface microgrids.",
                    extractedSkillsJson = """[{"name":"Distributed Optimization","level":"Advanced","confidence":93,"taxonomyCategory":"Systems"},{"name":"Cross-Disciplinary Leadership","level":"Advanced","confidence":90,"taxonomyCategory":"Soft Skills"},{"name":"Autonomous Systems","level":"Applied","confidence":87,"taxonomyCategory":"AI/ML"}]""",
                    evidenceWeight = 0.98f
                ),
                EvidenceItem(
                    id = 4,
                    category = EvidenceCategory.MICRO_CREDENTIAL,
                    title = "AWS Certified Solutions Architect - Associate",
                    issuerOrInstitution = "Amazon Web Services (Credly Issued)",
                    completionDate = "Nov 2025",
                    verificationStatus = VerificationStatus.VERIFIED_ISSUER_HASH,
                    verificationProofHash = "CREDLY-ID: 7a82910d-2b4e (Cryptographically Verified)",
                    verificationUrl = "https://credly.com/badges/alex-rivera-aws-csa",
                    gradeOrRanking = "Exam Score: 915 / 1000",
                    description = "Comprehensive credential covering highly available microservices architectures, VPC multi-region topologies, DynamoDB partitioning, and IAM zero-trust security.",
                    extractedSkillsJson = """[{"name":"Cloud Infrastructure","level":"Advanced","confidence":94,"taxonomyCategory":"Systems"},{"name":"Microservices","level":"Advanced","confidence":92,"taxonomyCategory":"Systems"},{"name":"AWS Lambda & DynamoDB","level":"Applied","confidence":89,"taxonomyCategory":"Systems"}]""",
                    evidenceWeight = 0.97f
                ),
                EvidenceItem(
                    id = 5,
                    category = EvidenceCategory.PROJECT,
                    title = "NeuroSight: Edge-AI Bionic Signal Processor",
                    issuerOrInstitution = "BioMech Multidisciplinary Capstone Lab",
                    completionDate = "Jan 2026",
                    verificationStatus = VerificationStatus.VERIFIED_PEER_REVIEW,
                    verificationProofHash = "PEER-VAL-BM-2026: 3 Faculty Co-Signatories",
                    verificationUrl = "https://biomech.berkeley.edu/projects/neurosight-2026",
                    gradeOrRanking = "Validated: 98.4% EMG Gesture Classification at 8ms Latency",
                    description = "Developed real-time electromyography (EMG) signal processing and quantized ONNX neural inference on STM32 microcontrollers for prosthetic hand control.",
                    extractedSkillsJson = """[{"name":"PyTorch","level":"Advanced","confidence":92,"taxonomyCategory":"AI/ML"},{"name":"Edge ML & ONNX","level":"Advanced","confidence":91,"taxonomyCategory":"AI/ML"},{"name":"DSP & Signal Processing","level":"Applied","confidence":88,"taxonomyCategory":"Hardware/IoT"},{"name":"Embedded C++","level":"Applied","confidence":86,"taxonomyCategory":"Hardware/IoT"}]""",
                    evidenceWeight = 0.94f
                ),
                EvidenceItem(
                    id = 6,
                    category = EvidenceCategory.COURSEWORK,
                    title = "EECS 281: Advanced Data Structures & Algorithms",
                    issuerOrInstitution = "UC Berkeley College of Engineering",
                    completionDate = "Dec 2024",
                    verificationStatus = VerificationStatus.VERIFIED_REGISTRAR,
                    verificationProofHash = "REG-UCB-847291: Official Transcript Seal",
                    verificationUrl = "https://registrar.berkeley.edu/verify/847291",
                    gradeOrRanking = "Grade: A (Top 5%)",
                    description = "Rigorous proofs and optimized implementations of amortized graph algorithms, B-Trees, Lock-Free SkipLists, and dynamic programming.",
                    extractedSkillsJson = """[{"name":"Graph Algorithms","level":"Advanced","confidence":95,"taxonomyCategory":"Data"},{"name":"Lock-Free Concurrency","level":"Advanced","confidence":91,"taxonomyCategory":"Systems"},{"name":"Memory Profiling","level":"Applied","confidence":87,"taxonomyCategory":"Systems"}]""",
                    evidenceWeight = 1.0f
                ),
                EvidenceItem(
                    id = 7,
                    category = EvidenceCategory.COMPETITION,
                    title = "ETHGlobal Systems & Cryptographic Verification Track",
                    issuerOrInstitution = "ETHGlobal Hackathon Consortium",
                    completionDate = "Mar 2025",
                    verificationStatus = VerificationStatus.VERIFIED_ISSUER_HASH,
                    verificationProofHash = "ETH-GLOBAL-AWARD-9901: SHA256(7c2a1e...9d3f)",
                    verificationUrl = "https://ethglobal.com/showcase/zk-passport-proof",
                    gradeOrRanking = "2nd Place Winner (Systems & Privacy Category)",
                    description = "Engineered zero-knowledge proof verification pipeline allowing students to attest to GPA and degree prerequisites without revealing raw transcript records.",
                    extractedSkillsJson = """[{"name":"Cryptographic Hashing","level":"Advanced","confidence":93,"taxonomyCategory":"Systems"},{"name":"Zero Knowledge Proofs","level":"Applied","confidence":85,"taxonomyCategory":"Systems"},{"name":"Rust","level":"Mastery","confidence":96,"taxonomyCategory":"Systems"}]""",
                    evidenceWeight = 0.95f
                ),
                EvidenceItem(
                    id = 8,
                    category = EvidenceCategory.MICRO_CREDENTIAL,
                    title = "DeepLearning.AI: MLOps & Model Deployment Specialization",
                    issuerOrInstitution = "DeepLearning.AI & Coursera",
                    completionDate = "Aug 2025",
                    verificationStatus = VerificationStatus.VERIFIED_ISSUER_HASH,
                    verificationProofHash = "COURSERA-VERIFIED: DL-MLOPS-8842",
                    verificationUrl = "https://coursera.org/verify/specialization/DL-MLOPS-8842",
                    gradeOrRanking = "Certificate with Honors (Score: 98%)",
                    description = "End-to-end continuous training pipelines, model monitoring for data drift, feature stores, and low-latency model serving with TorchServe and Triton.",
                    extractedSkillsJson = """[{"name":"MLOps","level":"Advanced","confidence":91,"taxonomyCategory":"AI/ML"},{"name":"Model Serving & Triton","level":"Applied","confidence":87,"taxonomyCategory":"AI/ML"},{"name":"PyTorch","level":"Advanced","confidence":90,"taxonomyCategory":"AI/ML"}]""",
                    evidenceWeight = 0.92f
                ),
                EvidenceItem(
                    id = 9,
                    category = EvidenceCategory.PROJECT,
                    title = "OmniCanvas: Accessible Collaborative Design Tool",
                    issuerOrInstitution = "Open Source Contribution / HCI Lab",
                    completionDate = "Nov 2024",
                    verificationStatus = VerificationStatus.VERIFIED_GITHUB,
                    verificationProofHash = "git:pr/142 (Approved by 2 maintainers)",
                    verificationUrl = "https://github.com/omni-canvas/client-core",
                    gradeOrRanking = "Merged into core production release (v2.4)",
                    description = "Built accessible real-time screen reader navigation and tactile keyboard navigation for infinite canvas drawing, fully compliant with WCAG 2.2 AAA standards.",
                    extractedSkillsJson = """[{"name":"Jetpack Compose","level":"Advanced","confidence":94,"taxonomyCategory":"Design"},{"name":"Kotlin","level":"Advanced","confidence":95,"taxonomyCategory":"Systems"},{"name":"WCAG Accessibility","level":"Advanced","confidence":92,"taxonomyCategory":"Design"},{"name":"WebSockets","level":"Applied","confidence":88,"taxonomyCategory":"Systems"}]""",
                    evidenceWeight = 0.91f
                ),
                EvidenceItem(
                    id = 10,
                    category = EvidenceCategory.COURSEWORK,
                    title = "ME 135: Mechatronics & Real-Time Embedded Control",
                    issuerOrInstitution = "UC Berkeley Mechanical Engineering",
                    completionDate = "May 2024",
                    verificationStatus = VerificationStatus.VERIFIED_REGISTRAR,
                    verificationProofHash = "REG-UCB-774910: Official Transcript Seal",
                    verificationUrl = "https://registrar.berkeley.edu/verify/774910",
                    gradeOrRanking = "Grade: A- (Top 8%)",
                    description = "Configured FreeRTOS task scheduling, PID motor control loops, I2C/SPI hardware communication, and ultrasonic sensor filtering on ARM Cortex-M4 processors.",
                    extractedSkillsJson = """[{"name":"Embedded C++","level":"Applied","confidence":87,"taxonomyCategory":"Hardware/IoT"},{"name":"RTOS Task Scheduling","level":"Applied","confidence":85,"taxonomyCategory":"Hardware/IoT"},{"name":"Sensor Fusion","level":"Applied","confidence":84,"taxonomyCategory":"Hardware/IoT"}]""",
                    evidenceWeight = 0.98f
                )
            )

            evidenceList.forEach { dao.insertEvidence(it) }

            // Seed Aggregated Skills
            val skillsList = listOf(
                SkillNode(
                    id = 1,
                    name = "Rust",
                    domain = SkillDomain.SYSTEMS,
                    masteryLevel = "Mastery",
                    confidenceScore = 97,
                    verifiedEvidenceCount = 3,
                    supportingEvidenceTitlesJson = """["AegisKV: Fault-Tolerant Distributed Storage Engine", "ETHGlobal Systems & Cryptographic Track", "CS 452: Distributed Systems"]""",
                    primaryEvidenceCategory = EvidenceCategory.PROJECT,
                    verificationStamp = "Triple Source Verified (GitHub + Contest + Coursework)"
                ),
                SkillNode(
                    id = 2,
                    name = "Raft Consensus & Distributed Systems",
                    domain = SkillDomain.SYSTEMS,
                    masteryLevel = "Mastery",
                    confidenceScore = 96,
                    verifiedEvidenceCount = 2,
                    supportingEvidenceTitlesJson = """["CS 452: Distributed Systems", "AegisKV Storage Engine"]""",
                    primaryEvidenceCategory = EvidenceCategory.COURSEWORK,
                    verificationStamp = "Dual Source Verified (Registrar + GitHub CI)"
                ),
                SkillNode(
                    id = 3,
                    name = "Cloud Infrastructure & Microservices",
                    domain = SkillDomain.SYSTEMS,
                    masteryLevel = "Advanced",
                    confidenceScore = 93,
                    verifiedEvidenceCount = 2,
                    supportingEvidenceTitlesJson = """["AWS Certified Solutions Architect", "NASA Space Apps: Autonomous Lunar Grid"]""",
                    primaryEvidenceCategory = EvidenceCategory.MICRO_CREDENTIAL,
                    verificationStamp = "Issuer Cryptographic Hash Verified"
                ),
                SkillNode(
                    id = 4,
                    name = "PyTorch & Edge AI",
                    domain = SkillDomain.AI_ML,
                    masteryLevel = "Advanced",
                    confidenceScore = 91,
                    verifiedEvidenceCount = 2,
                    supportingEvidenceTitlesJson = """["NeuroSight: Edge-AI Bionic Signal Processor", "DeepLearning.AI: MLOps Specialization"]""",
                    primaryEvidenceCategory = EvidenceCategory.PROJECT,
                    verificationStamp = "Peer Validated + Issuer Hash Verified"
                ),
                SkillNode(
                    id = 5,
                    name = "Kotlin & Jetpack Compose",
                    domain = SkillDomain.SYSTEMS,
                    masteryLevel = "Advanced",
                    confidenceScore = 94,
                    verifiedEvidenceCount = 1,
                    supportingEvidenceTitlesJson = """["OmniCanvas: Accessible Collaborative Design Tool"]""",
                    primaryEvidenceCategory = EvidenceCategory.PROJECT,
                    verificationStamp = "GitHub Maintainer Approved PR"
                ),
                SkillNode(
                    id = 6,
                    name = "Embedded C++ & Real-Time Control",
                    domain = SkillDomain.HARDWARE_IOT,
                    masteryLevel = "Applied",
                    confidenceScore = 86,
                    verifiedEvidenceCount = 2,
                    supportingEvidenceTitlesJson = """["ME 135: Mechatronics & Control", "NeuroSight: Edge-AI Bionic Signal Processor"]""",
                    primaryEvidenceCategory = EvidenceCategory.COURSEWORK,
                    verificationStamp = "Registrar Signed Transcript + Lab Signoff"
                ),
                SkillNode(
                    id = 7,
                    name = "Cross-Disciplinary Team Leadership",
                    domain = SkillDomain.COLLABORATION,
                    masteryLevel = "Advanced",
                    confidenceScore = 90,
                    verifiedEvidenceCount = 2,
                    supportingEvidenceTitlesJson = """["NASA Space Apps Challenge 2025", "BioMech Multidisciplinary Capstone Lab"]""",
                    primaryEvidenceCategory = EvidenceCategory.COMPETITION,
                    verificationStamp = "Contest Winner + Multidisciplinary Faculty Review"
                ),
                SkillNode(
                    id = 8,
                    name = "WCAG Accessibility & Inclusive HCI",
                    domain = SkillDomain.DESIGN_HCI,
                    masteryLevel = "Advanced",
                    confidenceScore = 92,
                    verifiedEvidenceCount = 1,
                    supportingEvidenceTitlesJson = """["OmniCanvas: Accessible Collaborative Tool"]""",
                    primaryEvidenceCategory = EvidenceCategory.PROJECT,
                    verificationStamp = "Open Source Accessibility Signoff"
                )
            )
            dao.insertSkills(skillsList)

            // Seed Opportunities (Internships & Multidisciplinary Teams)
            val opportunitiesList = listOf(
                Opportunity(
                    id = 1,
                    type = OpportunityType.INTERNSHIP,
                    title = "Autonomous Fleet Distributed Cloud Systems Intern",
                    hostOrganization = "Apex Autonomous Labs",
                    departmentOrDomain = "Cloud Infrastructure & Fleet Telemetry",
                    location = "San Francisco, CA (Hybrid / Relocation Paid)",
                    compensationOrGrant = "$62/hr + Housing Stipend + Mentorship",
                    duration = "Summer 2026 (12 Weeks)",
                    summary = "Scale real-time telemetry consensus and low-latency data replication across thousands of autonomous delivery vehicles.",
                    description = "We are seeking a systems engineer to build high-throughput consensus pipelines that ingest lidar and telemetry packets from edge rovers into cloud storage. You will write high-performance Rust and optimize raft partitioning.",
                    requiredSkillsJson = """[{"name":"Rust","minLevel":"Advanced","isEssential":true},{"name":"Raft Consensus & Distributed Systems","minLevel":"Advanced","isEssential":true},{"name":"Cloud Infrastructure & Microservices","minLevel":"Applied","isEssential":true},{"name":"Docker & Kubernetes Orchestration","minLevel":"Applied","isEssential":false}]""",
                    targetDisciplinesJson = """["Distributed Systems","Computer Science","Cloud Computing"]""",
                    ethicalBlindMatchingGuaranteed = true,
                    totalApplicantsCount = 42,
                    isBookmarked = true,
                    hasApplied = false
                ),
                Opportunity(
                    id = 2,
                    type = OpportunityType.MULTIDISCIPLINARY_TEAM,
                    title = "Project NeuroHand: AI-Powered Myoelectric Bionic Prosthetics",
                    hostOrganization = "BioMech Innovation Guild & Clinical Hospital",
                    departmentOrDomain = "Multidisciplinary Capstone / Grant Funded",
                    location = "Boston, MA / Multidisciplinary Hybrid",
                    compensationOrGrant = "$18,000 Team Fellowship + Capstone Credit",
                    duration = "Fall 2026 - Spring 2027 (2 Semesters)",
                    summary = "Forming a 4-person cross-functional squad (Systems/DSP, Biomedical Clinical, CAD Ergonomics, Edge ML) to build affordable pediatric prosthetics.",
                    description = "This high-impact project brings together hardware engineers, clinical specialists, and ML researchers to produce a lightweight 3D-printed bionic hand controlled by muscle electrical signals with sub-10ms latency.",
                    requiredSkillsJson = """[{"name":"PyTorch & Edge AI","minLevel":"Applied","isEssential":true},{"name":"Embedded C++ & Real-Time Control","minLevel":"Applied","isEssential":true},{"name":"Cross-Disciplinary Team Leadership","minLevel":"Applied","isEssential":true},{"name":"Medical Device Regulatory (FDA 510k)","minLevel":"Foundational","isEssential":false}]""",
                    targetDisciplinesJson = """["Biomedical Engineering","Computer Science","Industrial Design","Mechanical Engineering"]""",
                    ethicalBlindMatchingGuaranteed = true,
                    totalApplicantsCount = 18,
                    isBookmarked = false,
                    hasApplied = false
                ),
                Opportunity(
                    id = 3,
                    type = OpportunityType.INTERNSHIP,
                    title = "Edge-AI Perception & Robotics Software Intern",
                    hostOrganization = "Boston Dynamics & Robotics Research Lab",
                    departmentOrDomain = "Mobile Robotics Manipulation",
                    location = "Waltham, MA (On-site)",
                    compensationOrGrant = "$58/hr + Relocation + Lab Access",
                    duration = "Summer 2026 (12 Weeks)",
                    summary = "Deploy quantized neural networks on custom mobile quadruped robots for real-time terrain mapping.",
                    description = "Join our manipulation research team to accelerate perception models on onboard Nvidia Jetson hardware. Work closely with hardware designers to minimize power consumption.",
                    requiredSkillsJson = """[{"name":"PyTorch & Edge AI","minLevel":"Advanced","isEssential":true},{"name":"Embedded C++ & Real-Time Control","minLevel":"Applied","isEssential":true},{"name":"ROS2 Robotics Middleware","minLevel":"Applied","isEssential":true},{"name":"Sensor Fusion","minLevel":"Applied","isEssential":false}]""",
                    targetDisciplinesJson = """["Robotics","Computer Vision","Embedded Systems"]""",
                    ethicalBlindMatchingGuaranteed = true,
                    totalApplicantsCount = 31,
                    isBookmarked = false,
                    hasApplied = false
                ),
                Opportunity(
                    id = 4,
                    type = OpportunityType.MULTIDISCIPLINARY_TEAM,
                    title = "Project CleanGrid: Decentralized Renewable Micro-Grid Dispatch",
                    hostOrganization = "Global Energy Futures Consortium",
                    departmentOrDomain = "Climate Tech & Distributed Computing",
                    location = "Remote / Berkeley Innovation Lab",
                    compensationOrGrant = "$15,000 Research Grant + Equity Pool",
                    duration = "Summer 2026 - Fall 2026",
                    summary = "Collaborate with Power Systems electrical engineers, climate economists, and distributed systems leads to balance solar micro-grids.",
                    description = "An interdisciplinary initiative applying peer-to-peer game-theoretic dispatching algorithms to residential battery grids. Requires strong consensus logic and real-world simulation modeling.",
                    requiredSkillsJson = """[{"name":"Rust","minLevel":"Advanced","isEssential":true},{"name":"Cloud Infrastructure & Microservices","minLevel":"Applied","isEssential":true},{"name":"Cross-Disciplinary Team Leadership","minLevel":"Applied","isEssential":true},{"name":"Power Systems Simulation (OpenDSS)","minLevel":"Foundational","isEssential":false}]""",
                    targetDisciplinesJson = """["Distributed Computing","Power Systems","Environmental Economics"]""",
                    ethicalBlindMatchingGuaranteed = true,
                    totalApplicantsCount = 22,
                    isBookmarked = false,
                    hasApplied = false
                ),
                Opportunity(
                    id = 5,
                    type = OpportunityType.INTERNSHIP,
                    title = "Real-Time Distributed Financial Trading Engine Intern",
                    hostOrganization = "Citadel Grid & High-Frequency Systems",
                    departmentOrDomain = "Low Latency Market Infrastructure",
                    location = "New York, NY (Hybrid)",
                    compensationOrGrant = "$75/hr + $10,000 Signing Bonus",
                    duration = "Summer 2026 (10 Weeks)",
                    summary = "Architect deterministic order matching and zero-loss logging engines handling millions of events per second.",
                    description = "Work with our low-latency distributed core team. Optimize lock-free ring buffers, custom kernel bypass networking, and formal verification proofs for critical transaction paths.",
                    requiredSkillsJson = """[{"name":"Rust","minLevel":"Mastery","isEssential":true},{"name":"Raft Consensus & Distributed Systems","minLevel":"Advanced","isEssential":true},{"name":"Lock-Free Concurrency & Profiling","minLevel":"Advanced","isEssential":true},{"name":"Kernel Bypass Networking (DPDK)","minLevel":"Applied","isEssential":false}]""",
                    targetDisciplinesJson = """["Computer Science","Systems Engineering","Mathematics"]""",
                    ethicalBlindMatchingGuaranteed = true,
                    totalApplicantsCount = 65,
                    isBookmarked = false,
                    hasApplied = false
                ),
                Opportunity(
                    id = 6,
                    type = OpportunityType.INTERNSHIP,
                    title = "Inclusive HCI & Accessible Mobile Platforms Intern",
                    hostOrganization = "Universal Access & Next-Gen Interfaces Studio",
                    departmentOrDomain = "Human-Computer Interaction Research",
                    location = "Seattle, WA / Remote Option",
                    compensationOrGrant = "$55/hr + Hardware Lab Kit",
                    duration = "Summer 2026 (12 Weeks)",
                    summary = "Design and build next-generation adaptive gesture and screen reader experiences for mobile and foldable platforms.",
                    description = "We build accessible interface primitives that adapt dynamically to motor and visual impairments. You will implement reactive Compose UI components and test with real community accessibility partners.",
                    requiredSkillsJson = """[{"name":"Kotlin & Jetpack Compose","minLevel":"Advanced","isEssential":true},{"name":"WCAG Accessibility & Inclusive HCI","minLevel":"Advanced","isEssential":true},{"name":"Cross-Disciplinary Team Leadership","minLevel":"Applied","isEssential":false},{"name":"User Research & Usability Testing","minLevel":"Applied","isEssential":false}]""",
                    targetDisciplinesJson = """["HCI & Interaction Design","Computer Science","Accessibility Studies"]""",
                    ethicalBlindMatchingGuaranteed = true,
                    totalApplicantsCount = 20,
                    isBookmarked = false,
                    hasApplied = false
                )
            )
            dao.insertOpportunities(opportunitiesList)

            // Seed Multidisciplinary Team Projects
            val teamProjectsList = listOf(
                TeamProject(
                    id = 1,
                    title = "Project NeuroHand (AI-Powered Bionic Arm)",
                    domain = "Bio-Robotics & Accessible Prosthetics",
                    description = "Developing open-source, multi-articulating myoelectric hand with adaptive grip force and tactile sensory feedback for amputees.",
                    leadOrganization = "BioMech Innovation Guild",
                    synergyScore = 92,
                    disciplineDiversityCount = 4,
                    roleSlotsJson = """[{"roleTitle":"Lead Systems & DSP Engineer","discipline":"Distributed / Embedded Systems","requiredCompetencies":["Embedded C++","DSP & Signal Processing","Raft / Real-Time"],"assignedMemberAlias":"Candidate #CP-9104 (You)","isFilled":true},{"roleTitle":"Biomedical Clinical Specialist","discipline":"Biomedical Engineering","requiredCompetencies":["Electromyography (EMG)","Clinical Trials","Anatomy"],"assignedMemberAlias":"Candidate #BM-4102","isFilled":true},{"roleTitle":"Industrial & Ergonomic Designer","discipline":"Product Design & HCI","requiredCompetencies":["CAD 3D Printing","Ergonomics","User Research"],"assignedMemberAlias":null,"isFilled":false},{"roleTitle":"Edge ML & Gesture Model Engineer","discipline":"Artificial Intelligence","requiredCompetencies":["PyTorch","Model Quantization","ONNX"],"assignedMemberAlias":"Candidate #AI-6691","isFilled":true}]""",
                    coveredSkillsJson = """["Embedded C++","PyTorch & Edge AI","Signal Processing","Clinical EMG Protocols","Biocompatible Materials"]""",
                    missingSynergySkillsJson = """["Parametric Ergonomic CAD (3D SolidWorks)","User Usability Testing Protocol"]""",
                    isUserMember = true,
                    status = "Recruiting 1 Industrial / Ergonomic Designer"
                ),
                TeamProject(
                    id = 2,
                    title = "Project SolarMesh (Decentralized Clean Micro-Grid)",
                    domain = "Climate Tech & Distributed Infrastructure",
                    description = "Peer-to-peer residential energy trading protocol running on local smart meters to eliminate grid brownouts during heatwaves.",
                    leadOrganization = "Clean Energy Futures Lab",
                    synergyScore = 86,
                    disciplineDiversityCount = 3,
                    roleSlotsJson = """[{"roleTitle":"Distributed Systems Architect","discipline":"Systems Engineering","requiredCompetencies":["Rust","Consensus Protocols","Cloud Infra"],"assignedMemberAlias":"Candidate #CP-9104 (You)","isFilled":true},{"roleTitle":"Power Electronics Engineer","discipline":"Electrical Engineering","requiredCompetencies":["Inverter Topology","Grid Stability","CAN Bus"],"assignedMemberAlias":"Candidate #EE-3001","isFilled":true},{"roleTitle":"Environmental Economics & Policy Lead","discipline":"Public Policy & Economics","requiredCompetencies":["Carbon Offsets","Energy Tariffs","Grid Policy"],"assignedMemberAlias":"Candidate #EC-7128","isFilled":true},{"roleTitle":"Real-Time Data Visualization Lead","discipline":"UI/UX & HCI","requiredCompetencies":["WebSockets","Dashboard Design","Accessibility"],"assignedMemberAlias":null,"isFilled":false}]""",
                    coveredSkillsJson = """["Rust","Distributed Consensus","Inverter Dynamics","Grid Policy Modeling","Cloud Storage"]""",
                    missingSynergySkillsJson = """["Real-time Streaming WebSockets UI","Accessibility WCAG Compliance"]""",
                    isUserMember = true,
                    status = "Recruiting 1 Data Visualization Lead"
                ),
                TeamProject(
                    id = 3,
                    title = "Project SafeFlight: Multi-UAV Swarm Collision Avoidance",
                    domain = "Autonomous Aerospace & Edge Systems",
                    description = "Decentralized flocking and vision-based obstacle avoidance for wildfire search and rescue drone swarms.",
                    leadOrganization = "Autonomous Flight Lab",
                    synergyScore = 78,
                    disciplineDiversityCount = 3,
                    roleSlotsJson = """[{"roleTitle":"Swarm Algorithm Engineer","discipline":"Robotics / Computer Science","requiredCompetencies":["Distributed Optimization","Path Planning","C++"],"assignedMemberAlias":"Candidate #RO-1109","isFilled":true},{"roleTitle":"Aerodynamics & Airframe Specialist","discipline":"Aerospace Engineering","requiredCompetencies":["CFD Analysis","Airframe Fabrication","VTOL Dynamics"],"assignedMemberAlias":"Candidate #AE-9031","isFilled":true},{"roleTitle":"Embedded Hardware & Communications Lead","discipline":"Computer Engineering","requiredCompetencies":["Mesh Radio (915MHz)","STM32","Low Power"],"assignedMemberAlias":null,"isFilled":false}]""",
                    coveredSkillsJson = """["Path Planning","Swarm Robotics","Airframe Design","Aerodynamics"]""",
                    missingSynergySkillsJson = """["Embedded C++ / RTOS","Long-Range Mesh Networking (LoRa/915MHz)"]""",
                    isUserMember = false,
                    status = "Open for Embedded Lead Application"
                )
            )
            dao.insertTeamProjects(teamProjectsList)
        }
    }
}
