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
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.chip.ChipGroup
import com.mahmutgunduz.edukids.R
import com.mahmutgunduz.edukids.data.model.Student
import com.mahmutgunduz.edukids.ui.viewmodel.RecordManagementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddStudentFragment : Fragment() {
    

    // Öğrenci Bilgileri
    private lateinit var etName: TextInputEditText
    private lateinit var etSurname: TextInputEditText
    private lateinit var etTcKimlikNo: TextInputEditText
    private lateinit var etBirthDate: TextInputEditText
    private lateinit var etClass: MaterialAutoCompleteTextView
    
    // Veli Bilgileri
    private lateinit var etParentName: TextInputEditText
    private lateinit var etParentSurname: TextInputEditText
    private lateinit var etParentPhone: TextInputEditText
    private lateinit var etParentEmail: TextInputEditText
    private lateinit var etParentSecondPhone: TextInputEditText
    
    // İletişim Bilgileri
    private lateinit var etAddress: TextInputEditText
    private lateinit var etEmergencyContactName: TextInputEditText
    private lateinit var etEmergencyContactPhone: TextInputEditText
    
    // Sağlık Durumu
    private lateinit var etHealthStatus: TextInputEditText
    private lateinit var etDiseases: TextInputEditText
    private lateinit var etAllergies: TextInputEditText
    private lateinit var etAdditionalNotes: TextInputEditText
    
    // Durum ve Butonlar
    private lateinit var chipGroupStatus: ChipGroup
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupClickListeners()
        setupClassDropdown()
        observeViewModel()
    }
    
    private fun initViews(view: View) {
        // Öğrenci Bilgileri
        etName = view.findViewById(R.id.etName)
        etSurname = view.findViewById(R.id.etSurname)
        etTcKimlikNo = view.findViewById(R.id.etTcKimlikNo)
        etBirthDate = view.findViewById(R.id.etBirthDate)
        etClass = view.findViewById(R.id.etClass)
        
        // Veli Bilgileri
        etParentName = view.findViewById(R.id.etParentName)
        etParentSurname = view.findViewById(R.id.etParentSurname)
        etParentPhone = view.findViewById(R.id.etParentPhone)
        etParentEmail = view.findViewById(R.id.etParentEmail)
        etParentSecondPhone = view.findViewById(R.id.etParentSecondPhone)
        
        // İletişim Bilgileri
        etAddress = view.findViewById(R.id.etAddress)
        etEmergencyContactName = view.findViewById(R.id.etEmergencyContactName)
        etEmergencyContactPhone = view.findViewById(R.id.etEmergencyContactPhone)
        
        // Sağlık Durumu
        etHealthStatus = view.findViewById(R.id.etHealthStatus)
        etDiseases = view.findViewById(R.id.etDiseases)
        etAllergies = view.findViewById(R.id.etAllergies)
        etAdditionalNotes = view.findViewById(R.id.etAdditionalNotes)
        
        // Durum ve Butonlar
        chipGroupStatus = view.findViewById(R.id.chipGroupStatus)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
    }
    
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            saveStudent()
        }
        
        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Doğum tarihi seçici
        etBirthDate.setOnClickListener {
            showDatePicker()
        }
    }
    
    private fun setupClassDropdown() {
        val viewModel: RecordManagementViewModel by viewModels()
        
        // Observer'ı önce kur, sonra veri yükle
        viewModel.classes.observe(viewLifecycleOwner) { classes ->
            android.util.Log.d("AddStudent", "Classes observer triggered with ${classes.size} classes")
            if (classes.isEmpty()) {
                android.util.Log.w("AddStudent", "No classes found! Make sure classes are added to Firebase")
                showNoClassesDialog()
            }
            updateClassDropdown(classes)
        }
        
        // Sınıfları yükle
        android.util.Log.d("AddStudent", "Loading classes for dropdown...")
        viewModel.loadClasses()
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                etBirthDate.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR) - 5, // Varsayılan olarak 5 yaş
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
    
    private fun saveStudent() {
        if (validateInputs()) {
            val isActive = chipGroupStatus.checkedChipId == R.id.chipActive
            
            // Seçilen sınıfın bilgilerini al
            val selectedClassText = etClass.text.toString().trim()
            val selectedClassId = getSelectedClassId(selectedClassText)
            
            // Sınıf adını temizle (sadece sınıf adı ve yaş grubu)
            val selectedClass = availableClasses.find { it.id == selectedClassId }
            val selectedClassName = selectedClass?.name ?: ""
            
            // Öğrenci için otomatik öğrenci numarası oluştur
            val studentNumber = generateStudentNumber()
            
            val student = Student(
                studentId = studentNumber,
                tcId = etTcKimlikNo.text.toString().trim(),
                name = etName.text.toString().trim(),
                surname = etSurname.text.toString().trim(),
                birthDate = etBirthDate.text.toString().trim(),
                gender = "", // Layout'ta cinsiyet alanı yok, boş bırakıyoruz
                bloodType = "", // Layout'ta kan grubu alanı yok, boş bırakıyoruz
                classId = selectedClassId,
                className = selectedClassName,
                parentName = etParentName.text.toString().trim(),
                parentSurname = etParentSurname.text.toString().trim(),
                parentPhone = etParentPhone.text.toString().trim(),
                parentEmail = etParentEmail.text.toString().trim(),
                address = etAddress.text.toString().trim(),
                emergencyContactName = etEmergencyContactName.text.toString().trim(),
                emergencyContactPhone = etEmergencyContactPhone.text.toString().trim(),
                healthInfo = etHealthStatus.text.toString().trim(),
                allergies = etAllergies.text.toString().trim(),
                medications = etAdditionalNotes.text.toString().trim(),
                isActive = isActive
            )
            
            // ViewModel ile kaydet
            val viewModel: RecordManagementViewModel by viewModels()
            
            lifecycleScope.launch {
                try {
                    android.util.Log.d("AddStudent", "Saving student: ${student.name} ${student.surname}")
                    viewModel.addStudent(student)
                    
                    // Seçilen sınıfın öğrenci sayısını artır
                    selectedClass?.let { schoolClass ->
                        val updatedClass = schoolClass.copy(
                            currentStudentCount = schoolClass.currentStudentCount + 1
                        )
                        viewModel.updateClass(updatedClass)
                        android.util.Log.d("AddStudent", "Updated class student count: ${updatedClass.currentStudentCount}")
                    }
                    
                    android.util.Log.d("AddStudent", "Student added successfully")
                    Toast.makeText(requireContext(), "Öğrenci başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } catch (e: Exception) {
                    android.util.Log.e("AddStudent", "Error saving student", e)
                    Toast.makeText(requireContext(), "Öğrenci kaydedilirken hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    
    private fun generateStudentNumber(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val random = (1000..9999).random()
        return "$year$random"
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Öğrenci Bilgileri
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
        if (etBirthDate.text.toString().trim().isEmpty()) {
            etBirthDate.error = "Doğum tarihi gerekli"
            isValid = false
        }
        if (etClass.text.toString().trim().isEmpty()) {
            etClass.error = "Sınıf seçimi gerekli"
            isValid = false
        } else {
            // Seçilen sınıfın kapasitesini kontrol et
            val selectedClassText = etClass.text.toString().trim()
            val selectedClass = availableClasses.find { 
                "${it.name} (${it.ageGroup}) - ${it.currentStudentCount}/${it.maxStudentCount}" == selectedClassText 
            }
            
            if (selectedClass != null && selectedClass.currentStudentCount >= selectedClass.maxStudentCount) {
                etClass.error = "Bu sınıf dolu! Başka bir sınıf seçin."
                isValid = false
            }
        }
        
        // Veli Bilgileri
        if (etParentName.text.toString().trim().isEmpty()) {
            etParentName.error = "Veli adı gerekli"
            isValid = false
        }
        if (etParentSurname.text.toString().trim().isEmpty()) {
            etParentSurname.error = "Veli soyadı gerekli"
            isValid = false
        }
        if (etParentPhone.text.toString().trim().isEmpty()) {
            etParentPhone.error = "Veli telefon gerekli"
            isValid = false
        }
        val parentEmail = etParentEmail.text.toString().trim()
        if (parentEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(parentEmail).matches()) {
            etParentEmail.error = "Geçerli veli e-posta gerekli"
            isValid = false
        }
        
        // İletişim Bilgileri
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
    
    private fun observeViewModel() {
        val viewModel: RecordManagementViewModel by viewModels()
        
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            btnSave.isEnabled = !isLoading
            android.util.Log.d("AddStudent", "Loading state: $isLoading")
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                android.util.Log.e("AddStudent", "Error: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private var availableClasses: List<com.mahmutgunduz.edukids.data.model.SchoolClass> = emptyList()
    
    private fun updateClassDropdown(classes: List<com.mahmutgunduz.edukids.data.model.SchoolClass>) {
        availableClasses = classes
        
        if (classes.isEmpty()) {
            android.util.Log.w("AddStudent", "No classes available for dropdown")
            etClass.setText("")
            etClass.hint = "Önce sınıf eklemelisiniz"
            etClass.isEnabled = false
            return
        }
        
        // Aktif sınıfları filtrele
        val activeClasses = classes.filter { it.isActive }
        
        if (activeClasses.isEmpty()) {
            android.util.Log.w("AddStudent", "No active classes available")
            etClass.setText("")
            etClass.hint = "Aktif sınıf bulunmuyor"
            etClass.isEnabled = false
            return
        }
        
        // Sınıf isimlerini formatla: "Sınıf Adı (Yaş Grubu) - Mevcud/Kapasite"
        val classNames = activeClasses.map { schoolClass ->
            val status = if (schoolClass.currentStudentCount >= schoolClass.maxStudentCount) {
                " [DOLU]"
            } else {
                ""
            }
            "${schoolClass.name} (${schoolClass.ageGroup}) - ${schoolClass.currentStudentCount}/${schoolClass.maxStudentCount}$status"
        }
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, classNames)
        etClass.setAdapter(adapter)
        etClass.isEnabled = true
        etClass.hint = "Sınıf seçiniz"
        
        // Dropdown'ı tıklanabilir yap
        etClass.setOnClickListener {
            etClass.showDropDown()
        }
        
        // Otomatik tamamlama özelliği
        etClass.threshold = 1
        
        android.util.Log.d("AddStudent", "Updated class dropdown with ${activeClasses.size} active classes")
        activeClasses.forEach { schoolClass ->
            android.util.Log.d("AddStudent", "Active Class: ${schoolClass.name} - ${schoolClass.ageGroup} - Students: ${schoolClass.currentStudentCount}/${schoolClass.maxStudentCount}")
        }
    }
    
    private fun getSelectedClassId(selectedClassName: String): String {
        val selectedClass = availableClasses.find { schoolClass ->
            val status = if (schoolClass.currentStudentCount >= schoolClass.maxStudentCount) {
                " [DOLU]"
            } else {
                ""
            }
            "${schoolClass.name} (${schoolClass.ageGroup}) - ${schoolClass.currentStudentCount}/${schoolClass.maxStudentCount}$status" == selectedClassName
        }
        return selectedClass?.id ?: ""
    }
    
    private fun showNoClassesDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Sınıf Bulunamadı")
            .setMessage("Öğrenci eklemek için önce sınıf oluşturmanız gerekiyor. Sınıf ekleme sayfasına gitmek ister misiniz?")
            .setPositiveButton("Sınıf Ekle") { _, _ ->
                // Sınıf ekleme sayfasına git
                findNavController().navigate(R.id.action_addStudentFragment_to_addClassFragment)
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .setCancelable(false)
            .show()
    }
}