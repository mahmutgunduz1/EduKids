package com.mahmutgunduz.edukids.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mahmutgunduz.edukids.R
import com.mahmutgunduz.edukids.databinding.FragmentTeacherBinding
import com.mahmutgunduz.edukids.ui.adapter.TeacherModuleAdapter

class TeacherFragment : Fragment() {
    private lateinit var binding: FragmentTeacherBinding
    private lateinit var auth: FirebaseAuth

    data class TeacherModule(
        val id: String,
        val title: String,
        val icon: String,
        val subtitle: String = "",
        val color: Int = R.color.teacher_primary
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupRecyclerView()
        setupClickListeners()
    }
    
    private fun setupUI() {
        val currentUser = auth.currentUser
        binding.tvWelcome.text = currentUser?.email ?: "Öğretmen"
    }
    
    private fun setupRecyclerView() {
        // Kapsamlı Öğretmen Modülleri (25+ Özellik)
        val teacherModules = listOf(
            // Öğrenci Yönetimi
            TeacherModule("student_list", "Öğrenci\nListesi", "👶", "Sınıf öğrencileri", R.color.teacher_primary),
            TeacherModule("student_profiles", "Öğrenci\nProfilleri", "📋", "Detaylı bilgiler", R.color.teacher_info),
            TeacherModule("attendance", "Yoklama\nAl", "✅", "Günlük devam", R.color.teacher_success),
            TeacherModule("attendance_reports", "Devam\nRaporları", "📊", "Aylık istatistik", R.color.teacher_teal),
            
            // Eğitim ve Etkinlikler
            TeacherModule("daily_activities", "Günlük\nEtkinlikler", "🎨", "Bugünün planı", R.color.teacher_orange),
            TeacherModule("lesson_plans", "Ders\nPlanları", "📚", "Haftalık program", R.color.teacher_purple),
            TeacherModule("activity_photos", "Etkinlik\nFotoğrafları", "📸", "Anı paylaşımı", R.color.teacher_pink),
            TeacherModule("educational_games", "Eğitici\nOyunlar", "🎮", "Öğrenme oyunları", R.color.teacher_lime),
            
            // Veli İletişimi
            TeacherModule("parent_messages", "Veli\nMesajları", "💬", "İletişim merkezi", R.color.teacher_deep_purple),
            TeacherModule("send_announcement", "Duyuru\nGönder", "📢", "Toplu bilgilendirme", R.color.teacher_indigo),
            TeacherModule("parent_meetings", "Veli\nToplantıları", "👥", "Randevu sistemi", R.color.teacher_light_blue),
            TeacherModule("progress_reports", "Gelişim\nRaporları", "📈", "Öğrenci ilerlemesi", R.color.teacher_green),
            
            // Günlük İşlemler
            TeacherModule("daily_notes", "Günlük\nNotlar", "📝", "Öğrenci gözlemleri", R.color.teacher_yellow),
            TeacherModule("meal_tracking", "Yemek\nTakibi", "🍽️", "Beslenme durumu", R.color.teacher_amber),
            TeacherModule("sleep_tracking", "Uyku\nTakibi", "😴", "Dinlenme saatleri", R.color.teacher_deep_orange),
            TeacherModule("behavior_notes", "Davranış\nNotları", "⭐", "Olumlu/olumsuz", R.color.teacher_warning),
            
            // Sağlık ve Güvenlik
            TeacherModule("health_check", "Sağlık\nKontrolü", "🏥", "Günlük muayene", R.color.admin_red),
            TeacherModule("medication_tracker", "İlaç\nTakibi", "💊", "Reçeteli ilaçlar", R.color.admin_teal),
            TeacherModule("emergency_contacts", "Acil Durum\nİletişim", "🚨", "Hızlı erişim", R.color.admin_deep_orange),
            TeacherModule("incident_reports", "Olay\nRaporları", "⚠️", "Güvenlik kayıtları", R.color.admin_amber),
            
            // Kişisel Yönetim
            TeacherModule("my_schedule", "Çalışma\nTakvimim", "📅", "Kişisel program", R.color.teacher_primary),
            TeacherModule("salary_info", "Maaş\nBilgileri", "💰", "Ödeme durumu", R.color.teacher_success),
            TeacherModule("leave_requests", "İzin\nTalepleri", "🏖️", "Tatil başvuruları", R.color.teacher_info),
            TeacherModule("professional_development", "Mesleki\nGelişim", "🎓", "Eğitim programları", R.color.teacher_purple),
            
            // Analiz ve Raporlama
            TeacherModule("class_statistics", "Sınıf\nİstatistikleri", "📊", "Performans analizi", R.color.teacher_teal),
            TeacherModule("monthly_reports", "Aylık\nRaporlar", "📋", "Dönemsel değerlendirme", R.color.teacher_indigo),
            TeacherModule("parent_feedback", "Veli\nGeri Bildirimleri", "💭", "Memnuniyet anketi", R.color.teacher_light_blue),
            
            // Ek Özellikler
            TeacherModule("digital_portfolio", "Dijital\nPortföy", "💼", "Öğrenci eserleri", R.color.teacher_green),
            TeacherModule("birthday_tracker", "Doğum Günü\nTakibi", "🎂", "Özel günler", R.color.teacher_pink),
            TeacherModule("weather_activities", "Hava Durumu\nEtkinlikleri", "🌤️", "Mevsimsel planlar", R.color.teacher_lime)
        )
        
        // RecyclerView'i setup et
        val rvTeacherModules = view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvTeacherModules)
        rvTeacherModules?.let {
            it.layoutManager = GridLayoutManager(requireContext(), 2)
            it.adapter = TeacherModuleAdapter(teacherModules) { module ->
                handleModuleClick(module)
            }
        }
    }
    
    private fun handleModuleClick(module: TeacherModule) {
        when (module.id) {
            "student_list" -> {
                Toast.makeText(context, "👶 Öğrenci listesi açılıyor...", Toast.LENGTH_SHORT).show()
            }
            "attendance" -> {
                Toast.makeText(context, "✅ Yoklama sistemi başlatılıyor...", Toast.LENGTH_SHORT).show()
            }
            "parent_messages" -> {
                Toast.makeText(context, "💬 Veli mesajları yükleniyor...", Toast.LENGTH_SHORT).show()
            }
            "salary_info" -> {
                Toast.makeText(context, "💰 Maaş bilgileri: Bu ay 15.000₺", Toast.LENGTH_LONG).show()
            }
            "daily_activities" -> {
                Toast.makeText(context, "🎨 Bugünün etkinlikleri planlanıyor...", Toast.LENGTH_SHORT).show()
            }
            "health_check" -> {
                Toast.makeText(context, "🏥 Sağlık kontrol formu açılıyor...", Toast.LENGTH_SHORT).show()
            }
            "digital_portfolio" -> {
                Toast.makeText(context, "💼 Dijital portfolyo hazırlanıyor...", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "${module.title} - Yakında!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }
    
    private fun logout() {
        auth.signOut()
        findNavController().navigate(R.id.action_teacherFragment_to_loginFragment)
        Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
    }
}