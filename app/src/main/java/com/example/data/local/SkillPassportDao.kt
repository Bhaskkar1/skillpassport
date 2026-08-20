package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.Opportunity
import com.example.data.model.SkillNode
import com.example.data.model.TeamProject
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillPassportDao {

    // User Profile
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    // Evidence Items
    @Query("SELECT * FROM evidence_items ORDER BY createdAt DESC")
    fun getAllEvidence(): Flow<List<EvidenceItem>>

    @Query("SELECT * FROM evidence_items WHERE category = :category ORDER BY createdAt DESC")
    fun getEvidenceByCategory(category: EvidenceCategory): Flow<List<EvidenceItem>>

    @Query("SELECT * FROM evidence_items WHERE id = :id LIMIT 1")
    suspend fun getEvidenceById(id: Long): EvidenceItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(item: EvidenceItem): Long

    @Query("DELETE FROM evidence_items WHERE id = :id")
    suspend fun deleteEvidence(id: Long)

    // Skills
    @Query("SELECT * FROM skills ORDER BY confidenceScore DESC")
    fun getAllSkills(): Flow<List<SkillNode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<SkillNode>)

    @Query("DELETE FROM skills")
    suspend fun clearSkills()

    // Opportunities
    @Query("SELECT * FROM opportunities ORDER BY id ASC")
    fun getAllOpportunities(): Flow<List<Opportunity>>

    @Query("SELECT * FROM opportunities WHERE id = :id LIMIT 1")
    suspend fun getOpportunityById(id: Long): Opportunity?

    @Query("UPDATE opportunities SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("UPDATE opportunities SET hasApplied = :hasApplied WHERE id = :id")
    suspend fun updateApplied(id: Long, hasApplied: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunities(opportunities: List<Opportunity>)

    // Team Projects
    @Query("SELECT * FROM team_projects ORDER BY synergyScore DESC")
    fun getAllTeamProjects(): Flow<List<TeamProject>>

    @Query("SELECT * FROM team_projects WHERE id = :id LIMIT 1")
    suspend fun getTeamProjectById(id: Long): TeamProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamProjects(projects: List<TeamProject>)

    @Update
    suspend fun updateTeamProject(project: TeamProject)
}
