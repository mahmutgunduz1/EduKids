package com.mahmutgunduz.edukids.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

object FirebaseSetup {
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    data class TestUser(
        val email: String,
        val password: String,
        val role: String
    )
    
    private val testUsers = listOf(
        TestUser("admin@edukids.com", "admin123", "admin"),
        TestUser("teacher@edukids.com", "teacher123", "teacher"),
        TestUser("parent@edukids.com", "parent123", "parent")
    )
    
    fun createTestUsers() {
        testUsers.forEach { user ->
            createUser(user)
        }
    }
    
    private fun createUser(testUser: TestUser) {
        auth.createUserWithEmailAndPassword(testUser.email, testUser.password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                
                // Firestore'a kullanıcı bilgilerini ekle
                val userData = hashMapOf(
                    "email" to testUser.email,
                    "role" to testUser.role,
                    "createdAt" to System.currentTimeMillis(),
                    "isActive" to true
                )
                
                db.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        Log.d("FirebaseSetup", "✅ ${testUser.role} kullanıcısı oluşturuldu: ${testUser.email}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseSetup", "❌ Firestore hatası: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                if (e.message?.contains("already in use") == true) {
                    Log.d("FirebaseSetup", "ℹ️ Kullanıcı zaten var: ${testUser.email}")
                } else {
                    Log.e("FirebaseSetup", "❌ Auth hatası: ${e.message}")
                }
            }
    }
}