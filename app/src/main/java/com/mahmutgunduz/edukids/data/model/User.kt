package com.mahmutgunduz.edukids.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val role: UserRole = UserRole.PARENT,
    val position: String = "", // Pozisyon bilgisi eklendi
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)