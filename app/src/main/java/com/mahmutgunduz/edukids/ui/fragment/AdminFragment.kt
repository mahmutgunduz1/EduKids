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
import com.mahmutgunduz.edukids.databinding.FragmentAdminBinding
import com.mahmutgunduz.edukids.ui.adapter.AdminModuleAdapter

class AdminFragment : Fragment() {
    private lateinit var binding: FragmentAdminBinding
    private lateinit var auth: FirebaseAuth

    data class AdminModule(
        val id: String,
        val title: String,
        val icon: String,
        val subtitle: String = "",
        val color: Int = R.color.button_admin
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupRecyclerViews()
        setupClickListeners()
    }
    
    private fun setupUI() {
        val currentUser = auth.currentUser
        binding.tvWelcome.text = currentUser?.email ?: "Admin"
    }
    
    private fun setupRecyclerViews() {
        // Tüm Admin Modülleri
        val allModules = listOf(
            // Ana Yönetim
            AdminModule("user_management", "Kullanıcı\nYönetimi", "👥"),
            AdminModule("class_management", "Sınıf\nYönetimi", "🏫"),
            AdminModule("system_settings", "Sistem\nAyarları", "⚙️"),
            AdminModule("records", "Kayıtlar", "📋"),
            AdminModule("staff", "Personel", "👨‍💼"),
            AdminModule("institution_info", "Kurum\nBilgileri", "🏢"),
            
            // Eğitim ve Öğrenci İşleri
            AdminModule("daily_summary", "Günün\nÖzeti", "📅"),
            AdminModule("attendance", "Yoklama", "✅"),
            AdminModule("daily_report", "Günlük\nRapor", "📊"),
            AdminModule("physical_development", "Fiziksel\nGelişim", "🏃‍♂️"),
            AdminModule("daily_meals", "Günün\nYemekleri", "🍽️"),
            AdminModule("agenda", "Ajanda", "📖"),
            
            // İletişim ve Raporlama
            AdminModule("messages", "Mesajlar", "💬"),
            AdminModule("announcements", "Duyurular", "📢"),
            AdminModule("reports", "Raporlar", "📈"),
            AdminModule("feedback", "Geri\nBildirim", "💭"),
            AdminModule("gallery", "Galeri", "📸"),
            AdminModule("documents", "Dokümanlar", "📄"),
            
            // Sağlık ve Güvenlik
            AdminModule("medical_tracking", "Medikal\nTakip", "🏥"),
            AdminModule("appointments", "Randevular", "📋"),
            AdminModule("institution_bell", "Kurum\nZili", "🔔"),
            AdminModule("service_tracking", "Servis\nTakibi", "🚌"),
            AdminModule("emergency_contacts", "Acil Durum\nİletişim", "🚨"),
            AdminModule("health_reports", "Sağlık\nRaporları", "📋"),
            
            // Mali İşler ve Analiz
            AdminModule("accounting", "Muhasebe", "💰"),
            AdminModule("statistics", "İstatistikler", "📊"),
            AdminModule("flow", "Akış", "🔄"),
            AdminModule("budget_planning", "Bütçe\nPlanlama", "💳"),
            AdminModule("payment_tracking", "Ödeme\nTakibi", "💸"),
            AdminModule("financial_reports", "Mali\nRaporlar", "📈"),
            
            // Gelişmiş Özellikler
            AdminModule("ai_analytics", "AI Analiz", "🤖"),
            AdminModule("smart_notifications", "Akıllı\nBildirimler", "🔔"),
            AdminModule("backup_restore", "Yedekleme\nGeri Yükleme", "💾"),
            AdminModule("performance_monitor", "Performans\nİzleme", "⚡"),
            AdminModule("integration_hub", "Entegrasyon\nMerkezi", "🔗")
        )
        
        // RecyclerView'i bul ve setup et
        val rvAdminModules = view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvAdminModules)
        rvAdminModules?.let { setupRecyclerView(it, allModules) }
    }
    
    private fun setupRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView, modules: List<AdminModule>) {
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = AdminModuleAdapter(modules) { module ->
            handleModuleClick(module)
        }
    }
    
    private fun handleModuleClick(module: AdminModule) {
        Toast.makeText(context, "${module.title} - Yakında!", Toast.LENGTH_SHORT).show()
        
        // Burada her modül için farklı işlemler yapılabilir
        when (module.id) {
            "user_management" -> {
                findNavController().navigate(R.id.action_adminFragment_to_recordManagmentFragment)
            }

            "system_settings" -> {

            }
            "staff" -> {
                // Personel sayfasına git
            }
            "institution_info" -> {

            }



            "class_management" -> {
                // Sınıf yönetimi sayfasına git
            }
            "ai_analytics" -> {
                Toast.makeText(context, "🤖 AI Analiz sistemi geliştiriliyor...", Toast.LENGTH_LONG).show()
            }
            // Diğer modüller için case'ler eklenebilir
        }
    }
    
    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            logout()
        }


    }
    
    private fun logout() {
        auth.signOut()
        findNavController().navigate(R.id.action_adminFragment_to_loginFragment)
        Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
    }
}