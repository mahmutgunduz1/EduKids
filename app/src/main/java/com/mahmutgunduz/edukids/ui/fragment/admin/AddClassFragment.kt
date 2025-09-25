package com.mahmutgunduz.edukids.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.chip.ChipGroup
import com.mahmutgunduz.edukids.R
import com.mahmutgunduz.edukids.data.model.SchoolClass
import com.mahmutgunduz.edukids.ui.viewmodel.RecordManagementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddClassFragment : Fragment() {
    

    
    // UI Bileşenleri
    private lateinit var etClassName: TextInputEditText
    private lateinit var etAgeGroup: MaterialAutoCompleteTextView
    private lateinit var etMaxStudentCount: TextInputEditText
    private lateinit var etRoomNumber: TextInputEditText
    private lateinit var etMainTeacher: MaterialAutoCompleteTextView
    private lateinit var etAssistantTeacher: MaterialAutoCompleteTextView
    private lateinit var etDescription: TextInputEditText
    private lateinit var etFeatures: TextInputEditText
    private lateinit var etCurriculum: TextInputEditText
    private lateinit var etSchedule: TextInputEditText
    private lateinit var etSpecialNeeds: TextInputEditText
    private lateinit var chipGroupStatus: ChipGroup
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    
    private var selectedMainTeacherUid = ""
    private var selectedAssistantTeacherUid = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_class, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupClickListeners()
        observeViewModel()
    }
    
    private fun initViews(view: View) {
        // Temel Bilgiler
        etClassName = view.findViewById(R.id.etClassName)
        etAgeGroup = view.findViewById(R.id.etAgeGroup)
        etMaxStudentCount = view.findViewById(R.id.etMaxStudentCount)
        etRoomNumber = view.findViewById(R.id.etRoomNumber)
        
        // Öğretmen Atamaları
        etMainTeacher = view.findViewById(R.id.etMainTeacher)
        etAssistantTeacher = view.findViewById(R.id.etAssistantTeacher)
        
        // Sınıf Özellikleri
        etDescription = view.findViewById(R.id.etDescription)
        etFeatures = view.findViewById(R.id.etFeatures)
        
        // Profesyonel Özellikler
        etCurriculum = view.findViewById(R.id.etCurriculum)
        etSchedule = view.findViewById(R.id.etSchedule)
        etSpecialNeeds = view.findViewById(R.id.etSpecialNeeds)
        
        // Durum ve Butonlar
        chipGroupStatus = view.findViewById(R.id.chipGroupStatus)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
    }  
  
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            saveClass()
        }
        
        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Yaş grubu dropdown'ı setup et
        setupAgeGroupDropdown()
        
        // Öğretmen dropdown'larını setup et
        setupTeacherDropdowns()
        
        // Placeholder metinleri ayarla
        setupPlaceholders()
        

    }
    
    private fun setupAgeGroupDropdown() {
        val ageGroups = arrayOf(
            "0-1 Yaş (Bebek Bakımı)",
            "1-2 Yaş (Yürümeye Başlayan)",
            "2-3 Yaş (Küçük Grup)",
            "3-4 Yaş (Orta Grup)",
            "4-5 Yaş (Büyük Grup)",
            "5-6 Yaş (Anasınıfı)",
            "6+ Yaş (Okul Öncesi)",
            "Karma Yaş Grubu (2-6 Yaş)",
            "Özel Eğitim Grubu"
        )
        
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ageGroups)
        etAgeGroup.setAdapter(adapter)
        
        etAgeGroup.setOnItemClickListener { _, _, position, _ ->
            val selectedAgeGroup = ageGroups[position]
            
            // Yaş grubuna göre önerilen maksimum öğrenci sayısını ayarla
            val suggestedMaxStudents = when (position) {
                0, 1 -> 8  // Bebek ve yürümeye başlayan
                2 -> 12    // Küçük grup
                3 -> 15    // Orta grup
                4 -> 18    // Büyük grup
                5 -> 20    // Anasınıfı
                6 -> 22    // Okul öncesi
                7 -> 16    // Karma yaş
                8 -> 10    // Özel eğitim
                else -> 15
            }
            
            if (etMaxStudentCount.text.toString().trim().isEmpty()) {
                etMaxStudentCount.setText(suggestedMaxStudents.toString())
            }
        }
    }
    
    private fun showAgeGroupDialog() {
        // Bu metod artık kullanılmıyor, dropdown otomatik çalışıyor
        etAgeGroup.showDropDown()
    }
    
    private fun setupTeacherDropdowns() {
        val viewModel: RecordManagementViewModel by viewModels()
        
        // Öğretmenleri yükle
        viewModel.getStaffByRole(com.mahmutgunduz.edukids.data.model.UserRole.TEACHER)
        
        viewModel.staff.observe(viewLifecycleOwner) { teachers ->
            if (teachers.isNotEmpty()) {
                val activeTeachers = teachers.filter { it.isActive }
                
                if (activeTeachers.isNotEmpty()) {
                    // "Seçim Yok" seçeneği ekle
                    val teacherOptions = mutableListOf("Seçim Yok")
                    teacherOptions.addAll(activeTeachers.map { "${it.name} ${it.surname}" })
                    
                    // Ana öğretmen dropdown
                    val mainTeacherAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, teacherOptions)
                    etMainTeacher.setAdapter(mainTeacherAdapter)
                    
                    etMainTeacher.setOnItemClickListener { _, _, position, _ ->
                        if (position == 0) {
                            selectedMainTeacherUid = ""
                        } else {
                            val selectedTeacher = activeTeachers[position - 1]
                            selectedMainTeacherUid = selectedTeacher.uid
                        }
                    }
                    
                    // Yardımcı öğretmen dropdown
                    val assistantTeacherAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, teacherOptions)
                    etAssistantTeacher.setAdapter(assistantTeacherAdapter)
                    
                    etAssistantTeacher.setOnItemClickListener { _, _, position, _ ->
                        if (position == 0) {
                            selectedAssistantTeacherUid = ""
                        } else {
                            val selectedTeacher = activeTeachers[position - 1]
                            selectedAssistantTeacherUid = selectedTeacher.uid
                        }
                    }
                } else {
                    etMainTeacher.hint = "Aktif öğretmen bulunamadı"
                    etAssistantTeacher.hint = "Aktif öğretmen bulunamadı"
                }
            } else {
                etMainTeacher.hint = "Önce öğretmen eklemelisiniz"
                etAssistantTeacher.hint = "Önce öğretmen eklemelisiniz"
            }
        }
    }
    
    private fun showTeacherSelectionDialog(isMainTeacher: Boolean) {
        // Bu metod artık kullanılmıyor, dropdown otomatik çalışıyor
        if (isMainTeacher) {
            etMainTeacher.showDropDown()
        } else {
            etAssistantTeacher.showDropDown()
        }
    }
    
    private fun showNoTeachersDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Öğretmen Bulunamadı")
            .setMessage("Sınıfa öğretmen atamak için önce öğretmen eklemeniz gerekiyor. Personel ekleme sayfasına gitmek ister misiniz?")
            .setPositiveButton("Personel Ekle") { _, _ ->
                // Personel ekleme sayfasına git
                findNavController().navigate(R.id.action_addClassFragment_to_addStaffFragment)
            }
            .setNegativeButton("Devam Et") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    

    private fun saveClass() {
        if (validateInputs()) {
            val isActive = chipGroupStatus.checkedChipId == R.id.chipActive
            
            val schoolClass = SchoolClass(
                id = "", // Firebase otomatik ID oluşturacak
                name = etClassName.text.toString().trim(),
                ageGroup = etAgeGroup.text.toString().trim(),
                maxStudentCount = etMaxStudentCount.text.toString().trim().toIntOrNull() ?: 0,
                currentStudentCount = 0,
                mainTeacherUid = selectedMainTeacherUid,
                assistantTeacherUid = selectedAssistantTeacherUid,
                roomNumber = etRoomNumber.text.toString().trim(),
                description = etDescription.text.toString().trim(),
                features = etFeatures.text.toString().trim(),
                curriculum = etCurriculum.text.toString().trim(),
                schedule = etSchedule.text.toString().trim(),
                specialNeeds = etSpecialNeeds.text.toString().trim(),
                isActive = isActive,
                createdAt = System.currentTimeMillis()
            )
            
            // ViewModel ile kaydet
            val viewModel: RecordManagementViewModel by viewModels()
            
            lifecycleScope.launch {
                try {
                    android.util.Log.d("AddClass", "Saving class: ${schoolClass.name} - ${schoolClass.ageGroup}")
                    viewModel.addClass(schoolClass)
                    
                    android.util.Log.d("AddClass", "Class added successfully")
                    showSuccessDialog(schoolClass.name)
                } catch (e: Exception) {
                    android.util.Log.e("AddClass", "Error saving class", e)
                    Toast.makeText(requireContext(), "Sınıf kaydedilirken hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun observeViewModel() {
        val viewModel: RecordManagementViewModel by viewModels()
        
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            btnSave.isEnabled = !isLoading
            android.util.Log.d("AddClass", "Loading state: $isLoading")
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                android.util.Log.e("AddClass", "Error: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun setupPlaceholders() {
        etClassName.hint = "Örnek: Papatya Sınıfı"
        etRoomNumber.hint = "Örnek: 101"
        etMainTeacher.hint = "Öğretmen seçmek için tıklayın"
        etAssistantTeacher.hint = "Yardımcı öğretmen seçmek için tıklayın"
        etDescription.hint = "Bu sınıf hakkında genel bilgiler yazın..."
        etFeatures.hint = "Oyun alanı, kitaplık, bilgisayar, projeksiyon vb."
        etCurriculum.hint = "Eğitim programı, hedefler ve kazanımlar..."
        etSchedule.hint = "09:00-12:00 Sabah Etkinlikleri\n13:00-16:00 Öğleden Sonra Etkinlikleri"
        etSpecialNeeds.hint = "Özel eğitim gerektiren durumlar, destek ihtiyaçları..."
    }
    
    private fun showSuccessDialog(className: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("✅ Başarılı!")
            .setMessage("'$className' sınıfı başarıyla oluşturuldu.\n\nNe yapmak istiyorsunuz?")
            .setPositiveButton("Yeni Sınıf Ekle") { _, _ ->
                // Formu temizle ve yeni sınıf eklemeye devam et
                clearForm()
            }
            .setNegativeButton("Geri Dön") { _, _ ->
                findNavController().navigateUp()
            }
            .setNeutralButton("Öğrenci Ekle") { _, _ ->
                // Öğrenci ekleme sayfasına git
                findNavController().navigate(R.id.action_addClassFragment_to_addStudentFragment)
            }
            .setCancelable(false)
            .show()
    }
    
    private fun clearForm() {
        etClassName.setText("")
        etAgeGroup.setText("", false) // MaterialAutoCompleteTextView için false parametresi
        etMaxStudentCount.setText("")
        etRoomNumber.setText("")
        etMainTeacher.setText("", false) // MaterialAutoCompleteTextView için false parametresi
        etAssistantTeacher.setText("", false) // MaterialAutoCompleteTextView için false parametresi
        etDescription.setText("")
        etFeatures.setText("")
        etCurriculum.setText("")
        etSchedule.setText("")
        etSpecialNeeds.setText("")
        
        selectedMainTeacherUid = ""
        selectedAssistantTeacherUid = ""
        
        // Aktif chip'i seç
        chipGroupStatus.check(R.id.chipActive)
        
        // İlk alana focus ver
        etClassName.requestFocus()
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Sınıf adı kontrolü
        if (etClassName.text.toString().trim().isEmpty()) {
            etClassName.error = "Sınıf adı gerekli"
            isValid = false
        } else if (etClassName.text.toString().trim().length < 2) {
            etClassName.error = "Sınıf adı en az 2 karakter olmalı"
            isValid = false
        }
        
        // Yaş grubu kontrolü
        if (etAgeGroup.text.toString().trim().isEmpty()) {
            etAgeGroup.error = "Yaş grubu seçimi gerekli"
            isValid = false
        }
        
        // Maksimum öğrenci sayısı kontrolü
        val maxStudentText = etMaxStudentCount.text.toString().trim()
        if (maxStudentText.isEmpty()) {
            etMaxStudentCount.error = "Maksimum öğrenci sayısı gerekli"
            isValid = false
        } else {
            val maxStudentCount = maxStudentText.toIntOrNull()
            if (maxStudentCount == null || maxStudentCount <= 0) {
                etMaxStudentCount.error = "Geçerli bir sayı giriniz"
                isValid = false
            } else if (maxStudentCount > 30) {
                etMaxStudentCount.error = "Maksimum öğrenci sayısı 30'u geçemez"
                isValid = false
            }
        }
        
        // Oda numarası kontrolü
        if (etRoomNumber.text.toString().trim().isEmpty()) {
            etRoomNumber.error = "Oda numarası gerekli"
            isValid = false
        }
        
        return isValid
    }

}