package com.mahmutgunduz.edukids.ui.fragment.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.chip.ChipGroup
import com.mahmutgunduz.edukids.R
import com.mahmutgunduz.edukids.data.model.Staff
import com.mahmutgunduz.edukids.data.model.User
import com.mahmutgunduz.edukids.data.model.UserRole
import com.mahmutgunduz.edukids.ui.viewmodel.RecordManagementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddStaffFragment : Fragment() {

    
    // Kişisel Bilgiler
    private lateinit var etName: TextInputEditText
    private lateinit var etSurname: TextInputEditText
    private lateinit var etTcKimlikNo: TextInputEditText
    
    // İş Bilgileri
    private lateinit var etPosition: TextInputEditText
    private lateinit var etHireDate: TextInputEditText
    private lateinit var etSalary: TextInputEditText
    
    // İletişim Bilgileri
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etEmergencyContactName: TextInputEditText
    private lateinit var etEmergencyContactPhone: TextInputEditText
    
    // Eğitim ve Nitelikler
    private lateinit var etEducation: TextInputEditText
    private lateinit var etCertificates: TextInputEditText
    private lateinit var etQualifications: TextInputEditText
    
    // Mali Bilgiler
    private lateinit var etIbanNumber: TextInputEditText
    private lateinit var etAdditionalNotes: TextInputEditText
    
    // Durum ve Butonlar
    private lateinit var chipGroupStatus: ChipGroup
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_staff, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupClickListeners()
        setupPositionDropdown()

    }
    
    private fun initViews(view: View) {
        // Kişisel Bilgiler
        etName = view.findViewById(R.id.etName)
        etSurname = view.findViewById(R.id.etSurname)
        etTcKimlikNo = view.findViewById(R.id.etTcKimlikNo)
        
        // İş Bilgileri
        etPosition = view.findViewById(R.id.etPosition)
        etHireDate = view.findViewById(R.id.etHireDate)
        etSalary = view.findViewById(R.id.etSalary)
        
        // İletişim Bilgileri
        etPhone = view.findViewById(R.id.etPhone)
        etEmail = view.findViewById(R.id.etEmail)
        etAddress = view.findViewById(R.id.etAddress)
        etEmergencyContactName = view.findViewById(R.id.etEmergencyContactName)
        etEmergencyContactPhone = view.findViewById(R.id.etEmergencyContactPhone)
        
        // Eğitim ve Nitelikler
        etEducation = view.findViewById(R.id.etEducation)
        etCertificates = view.findViewById(R.id.etCertificates)
        etQualifications = view.findViewById(R.id.etQualifications)
        
        // Mali Bilgiler
        etIbanNumber = view.findViewById(R.id.etIbanNumber)
        etAdditionalNotes = view.findViewById(R.id.etAdditionalNotes)
        
        // Durum ve Butonlar
        chipGroupStatus = view.findViewById(R.id.chipGroupStatus)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
    }
    
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            saveStaff()
        }
        
        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // İşe başlama tarihi seçici
        etHireDate.setOnClickListener {
            showDatePicker()
        }
        
        // Pozisyon seçici
        etPosition.setOnClickListener {
            showPositionDialog()
        }
    }
    
    private fun setupPositionDropdown() {
        // Bu fonksiyon artık gerekli değil, dialog ile yapıyoruz
    }
    
    private fun showPositionDialog() {
        val positions = arrayOf(
            "Okul Öncesi Öğretmeni",
            "Sınıf Öğretmeni",
            "Müdür",
            "Müdür Yardımcısı",
            "Rehber Öğretmeni",
            "Hemşire",
            "Temizlik Personeli",
            "Güvenlik Görevlisi",
            "Aşçı",
            "Sekreter",
            "Muhasebeci"
        )
        
        AlertDialog.Builder(requireContext())
            .setTitle("Pozisyon Seçin")
            .setItems(positions) { _, which ->
                val selectedPosition = positions[which]
                etPosition.setText(selectedPosition)
                android.util.Log.d("AddStaff", "Selected position: $selectedPosition")
            }
            .setNegativeButton("İptal", null)
            .show()
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                etHireDate.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Maksimum tarih bugün
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        // Minimum tarih 10 yıl önce
        val minDate = Calendar.getInstance()
        minDate.add(Calendar.YEAR, -10)
        datePickerDialog.datePicker.minDate = minDate.timeInMillis
        
        datePickerDialog.show()
    }
    
    private fun saveStaff() {
        if (validateInputs()) {
            val isActive = chipGroupStatus.checkedChipId == R.id.chipActive
            
            val staffData = Staff(
                employeeId = generateEmployeeId(),
                tcKimlikNo = etTcKimlikNo.text.toString().trim(),
                position = etPosition.text.toString().trim(),
                hireDate = etHireDate.text.toString().trim(),
                salary = etSalary.text.toString().trim(),
                phone = etPhone.text.toString().trim(),
                email = etEmail.text.toString().trim(),
                address = etAddress.text.toString().trim(),
                emergencyContactName = etEmergencyContactName.text.toString().trim(),
                emergencyContactPhone = etEmergencyContactPhone.text.toString().trim(),
                education = etEducation.text.toString().trim(),
                certificates = etCertificates.text.toString().trim(),
                qualifications = etQualifications.text.toString().trim(),
                additionalNotes = etAdditionalNotes.text.toString().trim(),
                ibanNumber = etIbanNumber.text.toString().trim(),
                isActive = isActive
            )
            
            // User objesi oluştur (Firebase Authentication için)
            val userRole = when (etPosition.text.toString().trim().lowercase()) {
                "öğretmen", "teacher" -> UserRole.TEACHER
                "yönetici", "admin", "müdür" -> UserRole.ADMIN
                else -> UserRole.TEACHER
            }
            
            val user = User(
                name = etName.text.toString().trim(),
                surname = etSurname.text.toString().trim(),
                email = etEmail.text.toString().trim(),
                role = userRole,
                position = etPosition.text.toString().trim(), // Position bilgisi eklendi
                isActive = isActive
            )
            
            // ViewModel ile kaydet - hem User hem de Staff detaylarını kaydet
            val viewModel: RecordManagementViewModel by viewModels()
            viewModel.addStaff(user) // Firebase users koleksiyonuna
            viewModel.addStaffDetailed(staffData) // Firebase staff koleksiyonuna detaylı bilgiler
            
            android.util.Log.d("AddStaff", "Staff added: ${user.name} ${user.surname} - ${user.role}")
            Toast.makeText(requireContext(), "Personel başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
    
    private fun generateEmployeeId(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val random = (100..999).random()
        return "EMP$year$random"
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Kişisel Bilgiler
        if (etName.text.toString().trim().isEmpty()) {
            etName.error = "Ad gerekli"
            isValid = false
        }
        if (etSurname.text.toString().trim().isEmpty()) {
            etSurname.error = "Soyad gerekli"
            isValid = false
        }
        if (etTcKimlikNo.text.toString().trim().length != 11) {
            etTcKimlikNo.error = "TC Kimlik No 11 haneli olmalı"
            isValid = false
        }
        
        // İş Bilgileri
        if (etPosition.text.toString().trim().isEmpty()) {
            etPosition.error = "Pozisyon seçimi gerekli"
            isValid = false
        }
        if (etHireDate.text.toString().trim().isEmpty()) {
            etHireDate.error = "İşe başlama tarihi gerekli"
            isValid = false
        }
        if (etSalary.text.toString().trim().isEmpty()) {
            etSalary.error = "Maaş bilgisi gerekli"
            isValid = false
        }
        
        // İletişim Bilgileri
        if (etPhone.text.toString().trim().isEmpty()) {
            etPhone.error = "Telefon gerekli"
            isValid = false
        }
        val email = etEmail.text.toString().trim()
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Geçerli e-posta gerekli"
            isValid = false
        }
        if (etAddress.text.toString().trim().isEmpty()) {
            etAddress.error = "Adres gerekli"
            isValid = false
        }
        if (etEmergencyContactName.text.toString().trim().isEmpty()) {
            etEmergencyContactName.error = "Acil durum iletişim adı gerekli"
            isValid = false
        }
        if (etEmergencyContactPhone.text.toString().trim().isEmpty()) {
            etEmergencyContactPhone.error = "Acil durum telefon gerekli"
            isValid = false
        }
        
        return isValid
    }

}