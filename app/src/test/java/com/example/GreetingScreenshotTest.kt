package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.UserProfile
import com.example.ui.components.PassportCard
import com.example.ui.theme.CredentoTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun passport_card_screenshot() {
    val sampleProfile = UserProfile(
      id = 1,
      fullName = "Alex Mercer",
      anonymizedAlias = "Candidate #CP-9104",
      email = "alex@example.edu",
      primaryDiscipline = "Distributed Systems & Cloud Architecture",
      secondaryDiscipline = "Applied Machine Learning & HCI",
      headline = "Distributed Systems Researcher",
      bio = "Building resilient multi-region infrastructure.",
      graduationYear = "2026",
      university = "Tech Institute",
      passportDid = "did:credento:9f8a32bc",
      overallCompetencyScore = 94,
      isBlindMatchingActive = true
    )

    composeTestRule.setContent {
      CredentoTheme {
        PassportCard(
          userProfile = sampleProfile,
          evidenceList = emptyList(),
          onExportClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/passport_card.png")
  }
}
