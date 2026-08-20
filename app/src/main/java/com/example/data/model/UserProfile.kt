package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1,
    val fullName: String,
    val anonymizedAlias: String, // e.g. "Candidate #CP-7821 (Blind Match ID)"
    val email: String,
    val primaryDiscipline: String, // e.g. "Distributed Systems & Cloud Computing"
    val secondaryDiscipline: String, // e.g. "Edge Robotics & AI"
    val headline: String,
    val bio: String,
    val graduationYear: String, // Stored but hidden during blind algorithmic matching
    val university: String, // Stored but hidden during blind algorithmic matching
    val passportDid: String, // e.g. "did:credento:7a9c8b2e1f40d"
    val isBlindMatchingActive: Boolean = true,
    val totalVerifiedEvidenceCount: Int = 0,
    val overallCompetencyScore: Int = 88
)
