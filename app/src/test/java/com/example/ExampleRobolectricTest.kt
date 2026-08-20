package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.Opportunity
import com.example.data.model.OpportunityType
import com.example.data.model.SkillDomain
import com.example.data.model.SkillNode
import com.example.data.model.UserProfile
import com.example.data.model.VerificationStatus
import com.example.service.MatchingEngine
import com.example.service.PassportVerificationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Credento", appName)
  }

  @Test
  fun `test deterministic bias-free matching engine`() {
    val opportunity = Opportunity(
      id = 1,
      type = OpportunityType.INTERNSHIP,
      title = "Systems Engineer",
      hostOrganization = "Acme Cloud",
      departmentOrDomain = "Infrastructure",
      location = "Remote",
      compensationOrGrant = "$60/hr",
      duration = "Summer 2026",
      summary = "Build distributed consensus infrastructure",
      description = "Build distributed systems in Rust",
      requiredSkillsJson = """[{"name":"Rust","minLevel":"Advanced","isEssential":true},{"name":"Docker","minLevel":"Applied","isEssential":false}]""",
      targetDisciplinesJson = """["Distributed Systems","Computer Science"]""",
      ethicalBlindMatchingGuaranteed = true
    )

    val candidateSkills = listOf(
      SkillNode(
        id = 1,
        name = "Rust",
        domain = SkillDomain.SYSTEMS,
        masteryLevel = "Mastery",
        confidenceScore = 95,
        verifiedEvidenceCount = 3,
        supportingEvidenceTitlesJson = """["AegisKV"]""",
        primaryEvidenceCategory = EvidenceCategory.PROJECT,
        verificationStamp = "Single Source Verified"
      )
    )

    val candidateEvidence = listOf(
      EvidenceItem(
        id = 1,
        category = EvidenceCategory.PROJECT,
        title = "AegisKV",
        issuerOrInstitution = "GitHub",
        completionDate = "May 2026",
        verificationStatus = VerificationStatus.VERIFIED_GITHUB,
        verificationProofHash = "sha256:abcd",
        verificationUrl = "https://github.com",
        gradeOrRanking = "54 Commits",
        description = "Rust storage engine",
        extractedSkillsJson = """[{"name":"Rust","level":"Advanced","confidence":95,"taxonomyCategory":"Systems"}]""",
        evidenceWeight = 0.95f
      )
    )

    val recommendation = MatchingEngine.computeMatch(opportunity, candidateSkills, candidateEvidence)
    assertTrue("Match score should be positive", recommendation.matchScore > 60)
    assertEquals(1, recommendation.matchedSkills.size)
    assertEquals("Rust", recommendation.matchedSkills[0].skillName)
    assertEquals(1, recommendation.missingSkills.size)
    assertEquals("Docker", recommendation.missingSkills[0].skillName)
  }

  @Test
  fun `test passport verification engine generates valid cryptographic checksum`() {
    val profile = UserProfile(
      id = 1,
      fullName = "Alex Mercer",
      anonymizedAlias = "Candidate #CP-9104",
      email = "alex@example.edu",
      primaryDiscipline = "Distributed Systems",
      secondaryDiscipline = "AI/ML",
      headline = "Systems & Cloud Engineer",
      bio = "Passionate about high-performance consensus systems.",
      graduationYear = "2026",
      university = "Tech Institute",
      passportDid = "did:credento:9f8a32bc",
      isBlindMatchingActive = true
    )

    val passport = PassportVerificationEngine.generatePassport(profile, emptyList(), emptyList())
    assertNotNull(passport)
    assertTrue(passport.cryptographicChecksum.startsWith("0x"))
    val jsonLd = PassportVerificationEngine.exportToW3CJsonLd(passport, profile)
    assertTrue(jsonLd.contains("VerifiableCredential"))
    assertTrue(jsonLd.contains("Candidate #CP-9104"))
  }
}
