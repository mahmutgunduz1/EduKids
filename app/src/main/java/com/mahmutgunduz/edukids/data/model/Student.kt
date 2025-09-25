package com.mahmutgunduz.edukids.data.model

data class Student(
    val uid: String = "",
    val studentId: String = "",
    val tcId: String = "",
    val name: String = "",
    val surname: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val bloodType: String = "",
    val classId: String = "",
    val className: String = "",
    val parentName: String = "",
    val parentSurname: String = "",
    val parentPhone: String = "",
    val parentEmail: String = "",
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val healthInfo: String = "",
    val allergies: String = "",
    val medications: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)