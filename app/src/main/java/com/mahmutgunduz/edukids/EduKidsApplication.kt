package com.mahmutgunduz.edukids

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EduKidsApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Firebase'i manuel olarak initialize et (güvenlik için)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
    }
}