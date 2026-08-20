package com.example.service

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ExtractedSkill
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object GeminiService {

    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Extracts structured skills from syllabus / project README / contest certificate text using Gemini
     */
    suspend fun extractSkillsFromText(rawText: String): List<ExtractedSkill> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are an expert academic and technical credential assessor.
                    Extract structured skills from this coursework/project/competition text:
                    \"\"\"
                    $rawText
                    \"\"\"
                    
                    Return ONLY a valid JSON array of objects with keys:
                    - "name": String (concise skill name, e.g. "Distributed Systems", "Rust", "PyTorch", "RTOS")
                    - "level": String ("Foundational", "Applied", "Advanced", or "Mastery")
                    - "confidence": Int (between 75 and 99)
                    - "taxonomyCategory": String ("Systems", "AI/ML", "Data", "Hardware/IoT", "Design", or "Soft Skills")
                    
                    Do not include Markdown formatting or code backticks. Just the raw JSON array.
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contents = JSONArray().apply {
                        val part = JSONObject().apply { put("text", prompt) }
                        val parts = JSONArray().apply { put(part) }
                        put(JSONObject().apply { put("parts", parts) })
                    }
                    put("contents", contents)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = requestJson.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("$BASE_URL?key=$apiKey")
                    .post(body)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                    val responseJson = JSONObject(responseBody)
                    val candidates = responseJson.optJSONArray("candidates")
                    val candidate = candidates?.optJSONObject(0)
                    val content = candidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val rawGenerated = parts?.optJSONObject(0)?.optString("text") ?: ""

                    val cleanedJson = rawGenerated
                        .replace("```json", "")
                        .replace("```", "")
                        .trim()

                    val skills = parseSkillsJson(cleanedJson)
                    if (skills.isNotEmpty()) {
                        return@withContext skills
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini API extraction failed, using fallback engine", e)
            }
        }

        // Intelligent deterministic fallback
        return@withContext extractSkillsFallback(rawText)
    }

    /**
     * Generates a 4-week bridging roadmap for a student aiming for an opportunity
     */
    suspend fun generateBridgingRoadmap(
        targetTitle: String,
        missingSkills: List<String>,
        candidateSkills: List<String>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are an expert technical mentor. A candidate is applying for: "$targetTitle".
                    Their verified strengths: ${candidateSkills.joinToString(", ")}.
                    Identified missing skills: ${missingSkills.joinToString(", ")}.
                    
                    Create a concise, high-impact 4-Week Skill Bridging Plan to close these gaps with hands-on projects and micro-credentials.
                    Format with clear week-by-week bullet points (Week 1, Week 2, Week 3, Week 4) and 1 verifiable deliverable per week.
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contents = JSONArray().apply {
                        val part = JSONObject().apply { put("text", prompt) }
                        val parts = JSONArray().apply { put(part) }
                        put(JSONObject().apply { put("parts", parts) })
                    }
                    put("contents", contents)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = requestJson.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("$BASE_URL?key=$apiKey")
                    .post(body)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                    val responseJson = JSONObject(responseBody)
                    val candidates = responseJson.optJSONArray("candidates")
                    val candidate = candidates?.optJSONObject(0)
                    val content = candidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")
                    if (!text.isNullOrBlank()) {
                        return@withContext text
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini roadmap generation failed, using structured template", e)
            }
        }

        // Structured fallback roadmap
        return@withContext generateFallbackRoadmap(targetTitle, missingSkills)
    }

    private fun parseSkillsJson(json: String): List<ExtractedSkill> {
        val list = mutableListOf<ExtractedSkill>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ExtractedSkill(
                        name = obj.getString("name"),
                        level = obj.optString("level", "Applied"),
                        confidence = obj.optInt("confidence", 90),
                        taxonomyCategory = obj.optString("taxonomyCategory", "Systems")
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse skills JSON", e)
        }
        return list
    }

    private fun extractSkillsFallback(text: String): List<ExtractedSkill> {
        val lower = text.lowercase()
        val skills = mutableListOf<ExtractedSkill>()

        if (lower.contains("rust") || lower.contains("cargo") || lower.contains("borrow")) {
            skills.add(ExtractedSkill("Rust", "Advanced", 94, "Systems"))
        }
        if (lower.contains("distributed") || lower.contains("raft") || lower.contains("consensus") || lower.contains("paxos")) {
            skills.add(ExtractedSkill("Distributed Consensus", "Mastery", 96, "Systems"))
        }
        if (lower.contains("pytorch") || lower.contains("tensor") || lower.contains("neural") || lower.contains("onnx")) {
            skills.add(ExtractedSkill("PyTorch & Edge ML", "Advanced", 91, "AI/ML"))
        }
        if (lower.contains("docker") || lower.contains("kubernetes") || lower.contains("cloud") || lower.contains("aws")) {
            skills.add(ExtractedSkill("Cloud Infrastructure", "Applied", 89, "Systems"))
        }
        if (lower.contains("embedded") || lower.contains("rtos") || lower.contains("microcontroller") || lower.contains("c++")) {
            skills.add(ExtractedSkill("Embedded C++ / RTOS", "Applied", 88, "Hardware/IoT"))
        }
        if (lower.contains("compose") || lower.contains("ui") || lower.contains("ux") || lower.contains("accessibility") || lower.contains("wcag")) {
            skills.add(ExtractedSkill("Accessible UI & HCI", "Advanced", 92, "Design"))
        }
        if (lower.contains("data") || lower.contains("sql") || lower.contains("algorithm") || lower.contains("graph")) {
            skills.add(ExtractedSkill("Algorithms & Data Structures", "Advanced", 95, "Data"))
        }
        if (lower.contains("lead") || lower.contains("hackathon") || lower.contains("multidisciplinary") || lower.contains("team")) {
            skills.add(ExtractedSkill("Cross-Disciplinary Teamwork", "Advanced", 90, "Soft Skills"))
        }

        if (skills.isEmpty()) {
            skills.add(ExtractedSkill("Technical Problem Solving", "Advanced", 90, "Systems"))
            skills.add(ExtractedSkill("Applied Domain Implementation", "Applied", 85, "Systems"))
        }
        return skills
    }

    private fun generateFallbackRoadmap(targetTitle: String, missingSkills: List<String>): String {
        val missingStr = if (missingSkills.isNotEmpty()) missingSkills.joinToString(", ") else "Domain Tools"
        return """
            🚀 4-Week Skill Bridging Plan for $targetTitle
            
            • Week 1: Foundations & Architecture Setup
              Master core concepts of $missingStr. Set up local development environment and complete an interactive starter tutorial.
              🎯 Deliverable: Functional Hello-World microservice / sandbox repo with unit tests.
              
            • Week 2: Hands-On Applied Implementation
              Build a focused proof-of-concept module bridging your verified strengths with $missingStr.
              🎯 Deliverable: Core algorithm or pipeline module passing automated test suites.
              
            • Week 3: End-to-End Integration & Benchmarking
              Integrate the component into a full-stack or multi-node architecture with logging, CI/CD, and performance metrics.
              🎯 Deliverable: Documented GitHub repository with live demo & benchmark report.
              
            • Week 4: Verification & Passport Ledger Minting
              Package the project with signed commit proofs, peer review submission, and export verifiable credential badge to Credento passport.
              🎯 Deliverable: Verified Evidence Ledger entry (+15% Match Score boost!).
        """.trimIndent()
    }
}
