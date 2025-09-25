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
import com.mahmutgunduz.edukids.data.model.ParentModule
import com.mahmutgunduz.edukids.data.model.ParentModules
import com.mahmutgunduz.edukids.databinding.FragmentParentBinding
import com.mahmutgunduz.edukids.ui.adapter.ParentModuleAdapter

class ParentFragment : Fragment() {
    private lateinit var binding: FragmentParentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var parentModuleAdapter: ParentModuleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParentBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        loadChildData()
    }
    
    private fun setupUI() {
        val currentUser = auth.currentUser

        // Çocuk bilgilerini güncelle
        binding.tvChildName.text = "Elif Nur Yılmaz"
        binding.tvChildAge.text = "4 yaş 7 ay • Papatya Sınıfı"
    }
    
    private fun setupRecyclerView() {
        val modules = ParentModules.getAllModules()
        parentModuleAdapter = ParentModuleAdapter(modules) { module ->
            handleModuleClick(module)
        }
        
        binding.rvParentModules.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = parentModuleAdapter
        }
    }
    
    private fun handleModuleClick(module: ParentModule) {
        when (module.id) {
            "daily_report" -> showDailyReport()
            "live_tracking" -> showLiveTracking()
            "photo_gallery" -> showPhotoGallery()
            "development_tracking" -> showDevelopmentTracking()
            "attendance" -> showAttendance()
            "meals" -> showMealsTracking()
            "sleep_tracking" -> showSleepTracking()
            "activities" -> showActivities()
            "teacher_messages" -> showTeacherMessages()
            "health_tracking" -> showHealthTracking()
            "behavior_notes" -> showBehaviorNotes()
            "homework" -> showHomework()
            "announcements" -> showAnnouncements()
            "events" -> showEvents()
            "progress_reports" -> showProgressReports()
            "video_calls" -> showVideoCalls()
            "emergency_contacts" -> showEmergencyContacts()
            "payment_tracking" -> showPaymentTracking()
            "transportation" -> showTransportation()
            "feedback" -> showFeedback()
            else -> {
                Toast.makeText(context, "${module.title} - Yakında!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showDailyReport() {
        findNavController().navigate(R.id.action_parentFragment_to_dailyReportFragment)
    }
    
    private fun showLiveTracking() {
        Toast.makeText(context, "📍 Canlı Takip\n\nŞu anda: Oyun alanında\nEtkinlik: Serbest oyun\nÖğretmen: Ayşe Hanım\nSaat: 14:25", Toast.LENGTH_LONG).show()
    }
    
    private fun showPhotoGallery() {
        Toast.makeText(context, "📸 Fotoğraf Galerisi\n\nBugün 7 yeni fotoğraf!\n• Resim etkinliği (3 fotoğraf)\n• Oyun zamanı (2 fotoğraf)\n• Yemek zamanı (2 fotoğraf)", Toast.LENGTH_LONG).show()
    }
    
    private fun showDevelopmentTracking() {
        Toast.makeText(context, "📈 Gelişim Takibi\n\nBu hafta:\n• Motor beceriler: %85\n• Sosyal beceriler: %92\n• Dil gelişimi: %88\n• Yaratıcılık: %95", Toast.LENGTH_LONG).show()
    }
    
    private fun showAttendance() {
        Toast.makeText(context, "✅ Devam Durumu\n\nBu ay: 18/20 gün\n• Geliş saati ort: 08:25\n• Gidiş saati ort: 16:45\n• Devam oranı: %90", Toast.LENGTH_LONG).show()
    }
    
    private fun showMealsTracking() {
        Toast.makeText(context, "🍽️ Beslenme Takibi\n\nBugün:\n• Kahvaltı: Tam yedi ✅\n• Ara öğün: Yarım 🟡\n• Öğle yemeği: Tam yedi ✅\n• Su tüketimi: 4 bardak", Toast.LENGTH_LONG).show()
    }
    
    private fun showSleepTracking() {
        Toast.makeText(context, "😴 Uyku Takibi\n\nBugün:\n• Uyku saati: 13:00-14:30\n• Uyku süresi: 1.5 saat\n• Uyku kalitesi: Huzurlu\n• Uyanma durumu: Mutlu", Toast.LENGTH_LONG).show()
    }
    
    private fun showActivities() {
        Toast.makeText(context, "🎨 Etkinlikler\n\nBugünkü aktiviteler:\n• 09:00 - Sabah çemberi\n• 10:00 - Resim etkinliği\n• 11:30 - Müzik zamanı\n• 15:00 - Hikaye saati", Toast.LENGTH_LONG).show()
    }
    
    private fun showTeacherMessages() {
        Toast.makeText(context, "💬 Öğretmen Mesajları\n\n\"Elif bugün çok aktifti ve arkadaşlarıyla güzel oyunlar oynadı. Resim etkinliğinde harika bir kelebek çizdi!\" - Ayşe Öğretmen", Toast.LENGTH_LONG).show()
    }
    
    private fun showHealthTracking() {
        Toast.makeText(context, "🏥 Sağlık Takibi\n\nBugün:\n• Genel durum: İyi\n• Ateş: Normal\n• İlaç: Yok\n• Özel not: Yok", Toast.LENGTH_LONG).show()
    }
    
    private fun showBehaviorNotes() {
        Toast.makeText(context, "⭐ Davranış Notları\n\nBu hafta:\n• Paylaşma: Mükemmel ⭐⭐⭐\n• İşbirliği: Çok iyi ⭐⭐\n• Sabır: İyi ⭐⭐\n• Nezaket: Mükemmel ⭐⭐⭐", Toast.LENGTH_LONG).show()
    }
    
    private fun showHomework() {
        Toast.makeText(context, "📝 Ev Ödevleri\n\nBu hafta:\n• Renkleri tekrar et ✅\n• 1-10 sayıları say ✅\n• Aile fotoğrafı getir ⏳\n• Sevdiği oyuncağı anlat ⏳", Toast.LENGTH_LONG).show()
    }
    
    private fun showAnnouncements() {
        Toast.makeText(context, "📢 Duyurular\n\n• Yarın piknik var! 🌳\n• Veli toplantısı: 25 Mart\n• Fotoğraf çekimi: 30 Mart\n• Bahar şenliği: 5 Nisan", Toast.LENGTH_LONG).show()
    }
    
    private fun showEvents() {
        Toast.makeText(context, "📅 Etkinlik Takvimi\n\nYaklaşan etkinlikler:\n• 28 Mart - Doğa yürüyüşü\n• 2 Nisan - Tiyatro gösterisi\n• 8 Nisan - Spor günü\n• 15 Nisan - Kitap fuarı", Toast.LENGTH_LONG).show()
    }
    
    private fun showProgressReports() {
        Toast.makeText(context, "📋 İlerleme Raporları\n\nMart ayı değerlendirmesi:\n• Akademik gelişim: A\n• Sosyal gelişim: A+\n• Fiziksel gelişim: A\n• Duygusal gelişim: A", Toast.LENGTH_LONG).show()
    }
    
    private fun showVideoCalls() {
        Toast.makeText(context, "📹 Video Görüşme\n\n🆕 Yeni özellik!\nÖğretmeninizle video görüşme yapabilirsiniz.\n\nMüsait saatler:\n• Pazartesi 16:00-17:00\n• Çarşamba 15:30-16:30", Toast.LENGTH_LONG).show()
    }
    
    private fun showEmergencyContacts() {
        Toast.makeText(context, "🚨 Acil Durum\n\nHızlı iletişim:\n• Okul: 0212 555 0123\n• Müdür: 0532 123 4567\n• Öğretmen: 0541 987 6543\n• Hemşire: 0555 111 2233", Toast.LENGTH_LONG).show()
    }
    
    private fun showPaymentTracking() {
        Toast.makeText(context, "💳 Ödeme Takibi\n\nMart ayı:\n• Okul ücreti: Ödendi ✅\n• Yemek ücreti: Ödendi ✅\n• Etkinlik ücreti: Bekliyor ⏳\n• Toplam: 2.500 TL", Toast.LENGTH_LONG).show()
    }
    
    private fun showTransportation() {
        Toast.makeText(context, "🚌 Ulaşım Takibi\n\nServis bilgileri:\n• Sabah kalkış: 07:45\n• Akşam kalkış: 17:00\n• Şoför: Mehmet Bey\n• Plaka: 34 ABC 123", Toast.LENGTH_LONG).show()
    }
    
    private fun showFeedback() {
        Toast.makeText(context, "💭 Geri Bildirim\n\nGörüşleriniz bizim için değerli!\n\n• Öğretmen değerlendirmesi\n• Okul hizmetleri\n• Öneriler\n• Şikayetler", Toast.LENGTH_LONG).show()
    }
    
    private fun loadChildData() {
        // Gerçek uygulamada Firebase'den veri çekilecek
        // Şimdilik örnek veriler gösteriliyor
    }
    
    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            logout()
        }
        
        // Çocuk fotoğrafına tıklama
        binding.ivChildPhoto.setOnClickListener {
            Toast.makeText(context, "📸 Çocuğumun fotoğraflarını görüntüle", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun logout() {
        auth.signOut()
        findNavController().navigate(R.id.action_parentFragment_to_loginFragment)
        Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
    }
}