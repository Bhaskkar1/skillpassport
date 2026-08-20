package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.EvidenceCategory
import com.example.data.model.OpportunityType
import com.example.data.model.SkillDomain
import com.example.data.model.VerificationStatus

class Converters {
    @TypeConverter
    fun fromEvidenceCategory(category: EvidenceCategory): String = category.name

    @TypeConverter
    fun toEvidenceCategory(value: String): EvidenceCategory = runCatching {
        EvidenceCategory.valueOf(value)
    }.getOrDefault(EvidenceCategory.PROJECT)

    @TypeConverter
    fun fromVerificationStatus(status: VerificationStatus): String = status.name

    @TypeConverter
    fun toVerificationStatus(value: String): VerificationStatus = runCatching {
        VerificationStatus.valueOf(value)
    }.getOrDefault(VerificationStatus.PENDING_VERIFICATION)

    @TypeConverter
    fun fromSkillDomain(domain: SkillDomain): String = domain.name

    @TypeConverter
    fun toSkillDomain(value: String): SkillDomain = runCatching {
        SkillDomain.valueOf(value)
    }.getOrDefault(SkillDomain.SYSTEMS)

    @TypeConverter
    fun fromOpportunityType(type: OpportunityType): String = type.name

    @TypeConverter
    fun toOpportunityType(value: String): OpportunityType = runCatching {
        OpportunityType.valueOf(value)
    }.getOrDefault(OpportunityType.INTERNSHIP)
}
