package com.mahmutgunduz.edukids.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahmutgunduz.edukids.data.model.Student
import com.mahmutgunduz.edukids.data.model.User
import com.mahmutgunduz.edukids.data.model.SchoolClass
import com.mahmutgunduz.edukids.data.model.UserRole
import com.mahmutgunduz.edukids.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> = _students

    private val _staff = MutableLiveData<List<User>>()
    val staff: LiveData<List<User>> = _staff

    private val _classes = MutableLiveData<List<SchoolClass>>()
    val classes: LiveData<List<SchoolClass>> = _classes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error



    private val _activeStudents = MutableLiveData<List<Student>>()
    val activeStudents: LiveData<List<Student>> = _activeStudents

    private val _inactiveStudents = MutableLiveData<List<Student>>()
    val inactiveStudents: LiveData<List<Student>> = _inactiveStudents

    private val _preRegistrations = MutableLiveData<List<Student>>()
    val preRegistrations: LiveData<List<Student>> = _preRegistrations



    init {
        // Sayfa açılır açılmaz aktif öğrencileri yükle
        android.util.Log.d("RecordManagementVM", "ViewModel initialized, loading initial data...")
        loadActiveStudents()
    }

    fun loadAllData() {
        loadStudents()
        loadStaff()
        loadClasses()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _loading.value = true
            try {
                android.util.Log.d("RecordManagementVM", "Loading all students from repository...")
                val studentList = userRepository.getStudents()
                android.util.Log.d("RecordManagementVM", "All students loaded: ${studentList.size}")
                _students.value = studentList
            } catch (e: Exception) {
                android.util.Log.e("RecordManagementVM", "Error loading students", e)
                _error.value = "Öğrenci verileri yüklenemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadStaff() {
        viewModelScope.launch {
            _loading.value = true
            try {
                android.util.Log.d("RecordManagementVM", "Loading staff from repository...")
                val staffList = userRepository.getStaff()
                android.util.Log.d("RecordManagementVM", "Staff loaded: ${staffList.size}")
                _staff.value = staffList
            } catch (e: Exception) {
                android.util.Log.e("RecordManagementVM", "Error loading staff", e)
                _error.value = "Personel verileri yüklenemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadClasses() {
        viewModelScope.launch {
            _loading.value = true
            try {
                android.util.Log.d("RecordManagementVM", "Loading classes from repository...")
                val classList = userRepository.getClasses()
                android.util.Log.d("RecordManagementVM", "Classes loaded: ${classList.size}")
                _classes.value = classList
            } catch (e: Exception) {
                android.util.Log.e("RecordManagementVM", "Error loading classes", e)
                _error.value = "Sınıf verileri yüklenemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addStudent(student: Student) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = userRepository.addStudent(student)
                if (success) {
                    loadStudents() // Listeyi yenile
                } else {
                    _error.value = "Öğrenci eklenemedi"
                }
            } catch (e: Exception) {
                _error.value = "Öğrenci ekleme hatası: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addStaff(staff: User) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = userRepository.addStaff(staff)
                if (success) {
                    loadStaff() // Listeyi yenile
                } else {
                    _error.value = "Personel eklenemedi"
                }
            } catch (e: Exception) {
                _error.value = "Personel ekleme hatası: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addClass(schoolClass: SchoolClass) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = userRepository.addClass(schoolClass)
                if (success) {
                    loadClasses() // Listeyi yenile
                } else {
                    _error.value = "Sınıf eklenemedi"
                }
            } catch (e: Exception) {
                _error.value = "Sınıf ekleme hatası: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = userRepository.deleteStudent(studentId)
                if (success) {
                    loadStudents() // Listeyi yenile
                } else {
                    _error.value = "Öğrenci silinemedi"
                }
            } catch (e: Exception) {
                _error.value = "Öğrenci silme hatası: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteStaff(staffId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = userRepository.deleteStaff(staffId)
                if (success) {
                    loadStaff() // Listeyi yenile
                } else {
                    _error.value = "Personel silinemedi"
                }
            } catch (e: Exception) {
                _error.value = "Personel silme hatası: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteClass(classId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = userRepository.deleteClass(classId)
                if (success) {
                    loadClasses() // Listeyi yenile
                } else {
                    _error.value = "Sınıf silinemedi"
                }
            } catch (e: Exception) {
                _error.value = "Sınıf silme hatası: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getStudentsByClass(classId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val studentList = userRepository.getStudentsByClass(classId)
                _students.value = studentList
            } catch (e: Exception) {
                _error.value = "Sınıf öğrencileri yüklenemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getStaffByRole(role: UserRole) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val staffList = userRepository.getStaffByRole(role)
                _staff.value = staffList
            } catch (e: Exception) {
                _error.value = "Personel verileri yüklenemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Gelişmiş filtreleme fonksiyonları
    fun loadActiveStudents() {
        viewModelScope.launch {
            _loading.value = true
            try {
                android.util.Log.d("RecordManagementVM", "Loading active students from repository...")
                val activeStudents = userRepository.getActiveStudents()
                android.util.Log.d("RecordManagementVM", "Active students loaded: ${activeStudents.size}")
                
                // Debug: Aktif öğrencileri logla
                activeStudents.forEachIndexed { index, student ->
                    android.util.Log.d("RecordManagementVM", "Active Student $index: ${student.name} ${student.surname} - isActive: ${student.isActive}")
                }
                
                _activeStudents.value = activeStudents
                _students.value = activeStudents
            } catch (e: Exception) {
                android.util.Log.e("RecordManagementVM", "Error loading active students", e)
                _error.value = "Aktif öğrenci verileri yüklenemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadInactiveStudents() {
        viewModelScope.launch {
            _loading.value = true
            try {
                android.util.Log.d("RecordManagementVM", "Loading inactive students from repository...")
                val inactiveStudents = userRepository.getInactiveStudents()
                android.util.Log.d("RecordManagementVM", "Inactive students loaded: ${inactiveStudents.size}")
                
                // Debug: Pasif öğrencileri logla
                inactiveStudents.forEachIndexed { index, student ->
                    android.util.Log.d("RecordManagementVM", "Inactive Student $index: ${student.name} ${student.surname} - isActive: ${student.isActive}")
                }
                
                _inactiveStudents.value = inactiveStudents
                _students.value = inactiveStudents
            } catch (e: Exception) {
                android.util.Log.e("RecordManagementVM", "Error loading inactive students", e)
                _error.value = "Pasif öğrenci verileri yüklenemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadPreRegistrations() {
        viewModelScope.launch {
            _loading.value = true
            try {
                android.util.Log.d("RecordManagementVM", "Loading pre-registration students from repository...")
                val preRegStudents = userRepository.getPreRegistrationStudents()
                android.util.Log.d("RecordManagementVM", "Pre-registration students loaded: ${preRegStudents.size}")
                _preRegistrations.value = preRegStudents
                _students.value = preRegStudents
            } catch (e: Exception) {
                android.util.Log.e("RecordManagementVM", "Error loading pre-registration students", e)
                _error.value = "Ön kayıt verileri yüklenemedi: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }



    fun refreshData() {
        android.util.Log.d("RecordManagementVM", "Refreshing all data...")
        loadAllData()
    }
    
    // Real-time veri yenileme için özel metodlar
    fun forceRefreshActiveStudents() {
        android.util.Log.d("RecordManagementVM", "Force refreshing active students...")
        loadActiveStudents()
    }
    
    fun forceRefreshStaff() {
        android.util.Log.d("RecordManagementVM", "Force refreshing staff...")
        loadStaff()
    }
    
    fun forceRefreshClasses() {
        android.util.Log.d("RecordManagementVM", "Force refreshing classes...")
        loadClasses()
    }

    fun addStaffDetailed(staff: com.mahmutgunduz.edukids.data.model.Staff) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = userRepository.addStaffDetailed(staff)
                if (success) {
                    loadStaff() // Listeyi yenile
                } else {
                    _error.value = "Personel detayları eklenemedi"
                }
            } catch (e: Exception) {
                _error.value = "Personel detayları ekleme hatası: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun updateClass(schoolClass: SchoolClass) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = userRepository.updateClass(schoolClass)
                if (success) {
                    android.util.Log.d("RecordManagementVM", "Class updated successfully: ${schoolClass.name}")
                    loadClasses() // Sınıf listesini yenile
                } else {
                    _error.value = "Sınıf güncellenemedi"
                }
            } catch (e: Exception) {
                android.util.Log.e("RecordManagementVM", "Error updating class", e)
                _error.value = "Sınıf güncelleme hatası: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}