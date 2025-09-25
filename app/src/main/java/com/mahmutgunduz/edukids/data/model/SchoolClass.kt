package com.mahmutgunduz.edukids.data.model

data class SchoolClass(
    val id: String = "",
    val name: String = "",
    val ageGroup: String = "",
    val maxStudentCount: Int = 0,
    val currentStudentCount: Int = 0,
    val mainTeacherUid: String = "",
    val assistantTeacherUid: String = "",
    val roomNumber: String = "",
    val description: String = "",
    val features: String = "",
    
    // Ek profesyonel özellikler
    val curriculum: String = "", // Müfredat
    val schedule: String = "", // Ders programı
    val specialNeeds: String = "", // Özel ihtiyaçlar
    
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)