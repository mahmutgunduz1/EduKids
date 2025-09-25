package com.mahmutgunduz.edukids.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mahmutgunduz.edukids.R
import com.mahmutgunduz.edukids.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var selectedRole: UserRole = UserRole.PARENT
    
    enum class UserRole(
        val displayName: String, 
        val iconRes: Int, 
        val colorRes: Int,
        val firebaseRole: String
    ) {
        PARENT("Veli Girişi", R.drawable.ic_parent, R.color.button_parent, "parent"),
        TEACHER("Öğretmen Girişi", R.drawable.ic_teacher, R.color.button_teacher, "teacher"),
        ADMIN("Admin Girişi", R.drawable.ic_admin, R.color.button_admin, "admin")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        
        // Firebase initialize
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupInitialAnimations()
        setupRoleSelection()
        setupLoginForm()
    }
    
    private fun setupInitialAnimations() {
        // Logo animasyonu
        val logoAnimation = ObjectAnimator.ofFloat(binding.ivLogo, "rotationY", 0f, 360f)
        logoAnimation.duration = 1000
        logoAnimation.start()
        
        // Başlık animasyonu
        binding.tvAppName.alpha = 0f
        binding.tvAppName.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(300)
            .start()
            
        binding.tvSubtitle.alpha = 0f
        binding.tvSubtitle.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(500)
            .start()
            
        // Kart animasyonu
        binding.cardRoleSelection.alpha = 0f
        binding.cardRoleSelection.translationY = 100f
        binding.cardRoleSelection.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(700)
            .start()
    }
    
    private fun setupRoleSelection() {
        // Rol seçim butonları
        binding.layoutParent.setOnClickListener { 
            selectRole(UserRole.PARENT)
            animateRoleSelection(binding.layoutParent)
        }
        
        binding.layoutTeacher.setOnClickListener { 
            selectRole(UserRole.TEACHER)
            animateRoleSelection(binding.layoutTeacher)
        }
        
        binding.layoutAdmin.setOnClickListener { 
            selectRole(UserRole.ADMIN)
            animateRoleSelection(binding.layoutAdmin)
        }
        
        // Rol değiştir butonu
        binding.tvChangeRole.setOnClickListener {
            hideLoginForm()
        }
    }
    
    private fun selectRole(role: UserRole) {
        selectedRole = role
        updateSelectedRoleUI()
        showLoginForm()
    }
    
    private fun updateSelectedRoleUI() {
        binding.ivSelectedRole.setImageResource(selectedRole.iconRes)
        binding.tvSelectedRole.text = selectedRole.displayName
        
        // Login butonunun rengini seçilen role göre değiştir
        binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(requireContext(), selectedRole.colorRes)
    }
    
    private fun animateRoleSelection(selectedView: View) {
        // Seçilen butona pulse animasyonu
        val scaleX = ObjectAnimator.ofFloat(selectedView, "scaleX", 1f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(selectedView, "scaleY", 1f, 1.1f, 1f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = 200
        animatorSet.start()
    }
    
    private fun showLoginForm() {
        if (binding.cardLoginForm.visibility == View.GONE) {
            binding.cardLoginForm.visibility = View.VISIBLE
            
            // Slide in animasyonu
            binding.cardLoginForm.alpha = 0f
            binding.cardLoginForm.translationY = 50f
            binding.cardLoginForm.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start()
        }
    }
    
    private fun hideLoginForm() {
        binding.cardLoginForm.animate()
            .alpha(0f)
            .translationY(50f)
            .setDuration(300)
            .withEndAction {
                binding.cardLoginForm.visibility = View.GONE
                clearForm()
            }
            .start()
    }
    
    private fun setupLoginForm() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
        
        binding.tvForgotPassword.setOnClickListener {
            handleForgotPassword()
        }
        
        // Hızlı giriş butonları
        binding.btnQuickAdmin.setOnClickListener {
            quickLogin("admin@test.com", "123456", UserRole.ADMIN)
        }
        
        binding.btnQuickTeacher.setOnClickListener {
            quickLogin("teacher@test.com", "123456", UserRole.TEACHER)
        }
        
        binding.btnQuickParent.setOnClickListener {
            quickLogin("parent@test.com", "123456", UserRole.PARENT)
        }
    }
    
    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        // Validasyon
        if (email.isEmpty()) {
            binding.tilEmail.error = "E-posta gerekli"
            return
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "Şifre gerekli"
            return
        }
        
        // Hataları temizle
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        
        // Loading göster
        showLoading(true)
        animateLoginButton()
        
        // Firebase Authentication
        loginUser(email, password)
    }
    
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                
                // Firestore'dan kullanıcı rolünü kontrol et
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        showLoading(false)
                        
                        if (document.exists()) {
                            val userRole = document.getString("role")
                            
                            // Seçilen rol ile Firestore'daki rol eşleşiyor mu kontrol et
                            if (userRole == selectedRole.firebaseRole) {
                                showSuccessMessage()
                                navigateBasedOnRole(userRole)
                            } else {
                                showError("Bu hesap ${selectedRole.displayName} için değil!")
                                auth.signOut() // Yanlış rol seçimi durumunda çıkış yap
                            }
                        } else {
                            showError("Kullanıcı bilgileri bulunamadı!")
                            auth.signOut()
                        }
                    }
                    .addOnFailureListener { exception ->
                        showLoading(false)
                        showError("Veritabanı hatası: ${exception.message}")
                        auth.signOut()
                    }
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                showError("Giriş başarısız: ${exception.message}")
            }
    }
    
    private fun navigateBasedOnRole(role: String?) {
        when (role) {
            "admin" -> goToAdminScreen()
            "teacher" -> goToTeacherScreen()
            "parent" -> goToParentScreen()
            else -> showError("Geçersiz rol: $role")
        }
    }
    
    private fun goToAdminScreen() {
        try {
            findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
        } catch (e: Exception) {
            showError("Admin sayfası henüz hazır değil")
        }
    }
    
    private fun goToTeacherScreen() {
        try {
            findNavController().navigate(R.id.action_loginFragment_to_teacherFragment)
        } catch (e: Exception) {
            showError("Öğretmen sayfası henüz hazır değil")
        }
    }
    
    private fun goToParentScreen() {
        try {
            findNavController().navigate(R.id.action_loginFragment_to_parentFragment)
        } catch (e: Exception) {
            showError("Veli sayfası henüz hazır değil")
        }
    }
    
    private fun handleForgotPassword() {
        val email = binding.etEmail.text.toString().trim()
        
        if (email.isEmpty()) {
            binding.tilEmail.error = "Şifre sıfırlama için e-posta gerekli"
            return
        }
        
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(), 
                    "Şifre sıfırlama e-postası gönderildi", 
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { exception ->
                showError("E-posta gönderilemedi: ${exception.message}")
            }
    }
    
    private fun animateLoginButton() {
        val scaleX = ObjectAnimator.ofFloat(binding.btnLogin, "scaleX", 1f, 0.95f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.btnLogin, "scaleY", 1f, 0.95f, 1f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = 150
        animatorSet.start()
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
        binding.btnLogin.text = if (show) "Giriş yapılıyor..." else "Giriş Yap"
        
        // Form alanlarını devre dışı bırak
        binding.etEmail.isEnabled = !show
        binding.etPassword.isEnabled = !show
    }
    
    private fun showSuccessMessage() {
        Toast.makeText(
            requireContext(), 
            "${selectedRole.displayName} başarılı! Hoş geldiniz 🎉", 
            Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    private fun clearForm() {
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        binding.tilEmail.error = null
        binding.tilPassword.error = null
    }
    
    private fun quickLogin(email: String, password: String, role: UserRole) {
        // Rolü seç ve formu göster
        selectedRole = role
        updateSelectedRoleUI()
        showLoginForm()
        
        // Form alanlarını doldur
        binding.etEmail.setText(email)
        binding.etPassword.setText(password)
        
        // Otomatik giriş yap
        performLogin()
    }
}