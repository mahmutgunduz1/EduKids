package com.mahmutgunduz.edukids.data.model

data class Staff(
    val uid: String = "",
    val employeeId: String = "",
    val tcKimlikNo: String = "",
    val position: String = "",
    val hireDate: String = "",
    val salary: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val education: String = "",
    val certificates: String = "",
    val qualifications: String = "",
    val additionalNotes: String = "",
    val ibanNumber: String = "",
    val assignedClasses: List<String> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)