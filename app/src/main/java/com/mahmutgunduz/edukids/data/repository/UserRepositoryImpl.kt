package com.mahmutgunduz.edukids.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mahmutgunduz.edukids.data.model.User
import com.mahmutgunduz.edukids.data.model.Student
import com.mahmutgunduz.edukids.data.model.SchoolClass
import com.mahmutgunduz.edukids.data.model.UserRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun getStudents(): List<Student> {
        return try {
            android.util.Log.d("UserRepository", "Loading all students from 'users' collection with role=STUDENT...")
            
            // Users tablosundan role=STUDENT olanları çek
            val snapshot = try {
                firestore.collection("users")
                    .whereEqualTo("role", "STUDENT")
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get().await()
            } catch (e: Exception) {
                android.util.Log.w("UserRepository", "OrderBy failed, trying without orderBy", e)
                firestore.collection("users")
                    .whereEqualTo("role", "STUDENT")
                    .get().await()
            }
            
            android.util.Log.d("UserRepository", "Student users query returned: ${snapshot.documents.size} documents")
            
            val students = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data
                    if (data != null) {
                        // User verisini Student modeline dönüştür
                        val student = Student(
                            uid = doc.id,
                            studentId = data["studentId"] as? String ?: "",
                            tcId = data["tcId"] as? String ?: "",
                            name = data["name"] as? String ?: "",
                            surname = data["surname"] as? String ?: "",
                            birthDate = data["birthDate"] as? String ?: "",
                            gender = data["gender"] as? String ?: "",
                            bloodType = data["bloodType"] as? String ?: "",
                            classId = data["classId"] as? String ?: "",
                            className = data["className"] as? String ?: "",
                            parentName = data["parentName"] as? String ?: "",
                            parentSurname = data["parentSurname"] as? String ?: "",
                            parentPhone = data["parentPhone"] as? String ?: "",
                            parentEmail = data["parentEmail"] as? String ?: "",
                            address = data["address"] as? String ?: "",
                            emergencyContactName = data["emergencyContactName"] as? String ?: "",
                            emergencyContactPhone = data["emergencyContactPhone"] as? String ?: "",
                            healthInfo = data["healthInfo"] as? String ?: "",
                            allergies = data["allergies"] as? String ?: "",
                            medications = data["medications"] as? String ?: "",
                            isActive = data["isActive"] as? Boolean ?: true,
                            createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                            updatedAt = data["updatedAt"] as? Long ?: System.currentTimeMillis()
                        )
                        android.util.Log.d("UserRepository", "Student: ${student.name} ${student.surname} - Active: ${student.isActive}")
                        student
                    } else null
                } catch (e: Exception) {
                    android.util.Log.w("UserRepository", "Failed to parse student document: ${doc.id}", e)
                    null
                }
            }
            
            android.util.Log.d("UserRepository", "Students loaded successfully: ${students.size}")
            students
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to load students", e)
            emptyList()
        }
    }

    override suspend fun getStaff(): List<User> {
        return try {
            android.util.Log.d("UserRepository", "Loading staff from 'users' collection with role=TEACHER or ADMIN...")
            
            // Firebase'den users koleksiyonundaki TEACHER ve ADMIN rolündeki kullanıcıları çek
            val snapshot = try {
                firestore.collection("users")
                    .whereIn("role", listOf("TEACHER", "ADMIN"))
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get().await()
            } catch (e: Exception) {
                android.util.Log.w("UserRepository", "OrderBy failed, trying without orderBy", e)
                firestore.collection("users")
                    .whereIn("role", listOf("TEACHER", "ADMIN"))
                    .get().await()
            }
            
            android.util.Log.d("UserRepository", "Staff query returned: ${snapshot.documents.size} documents")
            
            val staff = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data
                    if (data != null) {
                        val user = User(
                            uid = doc.id,
                            name = data["name"] as? String ?: "",
                            surname = data["surname"] as? String ?: "",
                            email = data["email"] as? String ?: "",
                            role = when (data["role"] as? String) {
                                "ADMIN" -> UserRole.ADMIN
                                "TEACHER" -> UserRole.TEACHER
                                "PARENT" -> UserRole.PARENT
                                else -> UserRole.TEACHER
                            },
                            position = data["position"] as? String ?: "", // Position bilgisi eklendi
                            createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                            isActive = data["isActive"] as? Boolean ?: true
                        )
                        android.util.Log.d("UserRepository", "Staff: ${user.name} ${user.surname} - Role: ${user.role}")
                        user
                    } else null
                } catch (e: Exception) {
                    android.util.Log.w("UserRepository", "Failed to parse staff document: ${doc.id}", e)
                    null
                }
            }
            
            android.util.Log.d("UserRepository", "Staff loaded successfully: ${staff.size}")
            staff
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to load staff", e)
            emptyList()
        }
    }

    override suspend fun getClasses(): List<SchoolClass> {
        return try {
            android.util.Log.d("UserRepository", "Loading classes from 'classes' collection...")
            
            // Firebase'den classes koleksiyonundaki tüm sınıf verilerini çek
            val snapshot = try {
                firestore.collection("classes")
                    .orderBy("name")
                    .get().await()
            } catch (e: Exception) {
                android.util.Log.w("UserRepository", "OrderBy failed, trying without orderBy", e)
                firestore.collection("classes")
                    .get().await()
            }
            
            android.util.Log.d("UserRepository", "Classes query returned: ${snapshot.documents.size} documents")
            
            val classes = snapshot.documents.mapNotNull { doc ->
                try {
                    val schoolClass = doc.toObject(SchoolClass::class.java)?.copy(id = doc.id)
                    android.util.Log.d("UserRepository", "Class: ${schoolClass?.name} - Students: ${schoolClass?.currentStudentCount}/${schoolClass?.maxStudentCount}")
                    schoolClass
                } catch (e: Exception) {
                    android.util.Log.w("UserRepository", "Failed to parse class document: ${doc.id}", e)
                    null
                }
            }
            
            android.util.Log.d("UserRepository", "Classes loaded successfully: ${classes.size}")
            classes
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to load classes", e)
            emptyList()
        }
    }

    override suspend fun addStudent(student: Student): Boolean {
        return try {
            android.util.Log.d("UserRepository", "Adding student to 'users' collection: ${student.name} ${student.surname}")
            
            // Student verisini users tablosuna role="STUDENT" olarak kaydet
            val studentData = mapOf(
                "role" to "STUDENT",
                "studentId" to student.studentId,
                "tcId" to student.tcId,
                "name" to student.name,
                "surname" to student.surname,
                "birthDate" to student.birthDate,
                "gender" to student.gender,
                "bloodType" to student.bloodType,
                "classId" to student.classId,
                "className" to student.className,
                "parentName" to student.parentName,
                "parentSurname" to student.parentSurname,
                "parentPhone" to student.parentPhone,
                "parentEmail" to student.parentEmail,
                "address" to student.address,
                "emergencyContactName" to student.emergencyContactName,
                "emergencyContactPhone" to student.emergencyContactPhone,
                "healthInfo" to student.healthInfo,
                "allergies" to student.allergies,
                "medications" to student.medications,
                "isActive" to student.isActive,
                "createdAt" to student.createdAt,
                "updatedAt" to student.updatedAt
            )
            
            val result = firestore.collection("users").add(studentData).await()
            android.util.Log.d("UserRepository", "Student added successfully to users collection with ID: ${result.id}")
            true
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to add student: ${student.name}", e)
            false
        }
    }

    override suspend fun addStaff(staff: User): Boolean {
        return try {
            val staffData = mapOf(
                "name" to staff.name,
                "surname" to staff.surname,
                "email" to staff.email,
                "role" to when (staff.role) {
                    UserRole.ADMIN -> "ADMIN"
                    UserRole.TEACHER -> "TEACHER"
                    UserRole.PARENT -> "PARENT"
                },
                "position" to staff.position, // Position bilgisi eklendi
                "createdAt" to staff.createdAt,
                "isActive" to staff.isActive
            )
            val result = firestore.collection("users").add(staffData).await()
            android.util.Log.d("UserRepository", "Staff added successfully with ID: ${result.id}")
            true
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to add staff", e)
            false
        }
    }

    override suspend fun addClass(schoolClass: SchoolClass): Boolean {
        return try {
            val result = firestore.collection("classes").add(schoolClass).await()
            android.util.Log.d("UserRepository", "Class added successfully with ID: ${result.id}")
            true
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to add class", e)
            false
        }
    }

    override suspend fun updateStudent(student: Student): Boolean {
        return try {
            firestore.collection("students").document(student.uid).set(student).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateStaff(staff: User): Boolean {
        return try {
            val staffData = mapOf(
                "name" to staff.name,
                "surname" to staff.surname,
                "email" to staff.email,
                "role" to when (staff.role) {
                    UserRole.ADMIN -> "ADMIN"
                    UserRole.TEACHER -> "TEACHER"
                    UserRole.PARENT -> "PARENT"
                },
                "createdAt" to staff.createdAt,
                "isActive" to staff.isActive
            )
            firestore.collection("users").document(staff.uid).set(staffData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateClass(schoolClass: SchoolClass): Boolean {
        return try {
            firestore.collection("classes").document(schoolClass.id).set(schoolClass).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteStudent(studentId: String): Boolean {
        return try {
            firestore.collection("students").document(studentId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteStaff(staffId: String): Boolean {
        return try {
            firestore.collection("users").document(staffId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteClass(classId: String): Boolean {
        return try {
            firestore.collection("classes").document(classId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getStudentsByClass(classId: String): List<Student> {
        return try {
            val snapshot = firestore.collection("students")
                .whereEqualTo("classId", classId)
                .get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Student::class.java)?.copy(uid = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getStaffByRole(role: UserRole): List<User> {
        return try {
            val roleString = when (role) {
                UserRole.ADMIN -> "ADMIN"
                UserRole.TEACHER -> "TEACHER"
                UserRole.PARENT -> "PARENT"
            }
            val snapshot = firestore.collection("users")
                .whereEqualTo("role", roleString)
                .get().await()
            snapshot.documents.mapNotNull { doc ->
                val data = doc.data
                if (data != null) {
                    User(
                        uid = doc.id,
                        name = data["name"] as? String ?: "",
                        surname = data["surname"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        role = when (data["role"] as? String) {
                            "ADMIN" -> UserRole.ADMIN
                            "TEACHER" -> UserRole.TEACHER
                            "PARENT" -> UserRole.PARENT
                            else -> UserRole.TEACHER
                        },
                        position = data["position"] as? String ?: "", // Position bilgisi eklendi
                        createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                        isActive = data["isActive"] as? Boolean ?: true
                    )
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Gelişmiş filtreleme metodları implementasyonu
    override suspend fun getActiveStudents(): List<Student> {
        return try {
            android.util.Log.d("UserRepository", "Loading active students from 'users' collection with role=STUDENT and isActive=true...")
            
            // Users tablosundan role=STUDENT ve isActive=true olanları çek
            val snapshot = try {
                firestore.collection("users")
                    .whereEqualTo("role", "STUDENT")
                    .whereEqualTo("isActive", true)
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get().await()
            } catch (e: Exception) {
                android.util.Log.w("UserRepository", "OrderBy failed, trying without orderBy", e)
                firestore.collection("users")
                    .whereEqualTo("role", "STUDENT")
                    .whereEqualTo("isActive", true)
                    .get().await()
            }
            
            android.util.Log.d("UserRepository", "Active students query returned: ${snapshot.documents.size} documents")
            
            val activeStudents = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data
                    if (data != null) {
                        val student = Student(
                            uid = doc.id,
                            studentId = data["studentId"] as? String ?: "",
                            tcId = data["tcId"] as? String ?: "",
                            name = data["name"] as? String ?: "",
                            surname = data["surname"] as? String ?: "",
                            birthDate = data["birthDate"] as? String ?: "",
                            gender = data["gender"] as? String ?: "",
                            bloodType = data["bloodType"] as? String ?: "",
                            classId = data["classId"] as? String ?: "",
                            className = data["className"] as? String ?: "",
                            parentName = data["parentName"] as? String ?: "",
                            parentSurname = data["parentSurname"] as? String ?: "",
                            parentPhone = data["parentPhone"] as? String ?: "",
                            parentEmail = data["parentEmail"] as? String ?: "",
                            address = data["address"] as? String ?: "",
                            emergencyContactName = data["emergencyContactName"] as? String ?: "",
                            emergencyContactPhone = data["emergencyContactPhone"] as? String ?: "",
                            healthInfo = data["healthInfo"] as? String ?: "",
                            allergies = data["allergies"] as? String ?: "",
                            medications = data["medications"] as? String ?: "",
                            isActive = data["isActive"] as? Boolean ?: true,
                            createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                            updatedAt = data["updatedAt"] as? Long ?: System.currentTimeMillis()
                        )
                        android.util.Log.d("UserRepository", "Active Student: ${student.name} ${student.surname} - Class: ${student.className}")
                        student
                    } else null
                } catch (e: Exception) {
                    android.util.Log.w("UserRepository", "Failed to parse active student document: ${doc.id}", e)
                    null
                }
            }
            
            android.util.Log.d("UserRepository", "Active students loaded successfully: ${activeStudents.size}")
            activeStudents
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to load active students", e)
            emptyList()
        }
    }

    override suspend fun getInactiveStudents(): List<Student> {
        return try {
            android.util.Log.d("UserRepository", "Loading inactive students from 'users' collection with role=STUDENT and isActive=false...")
            
            // Users tablosundan role=STUDENT ve isActive=false olanları çek
            val snapshot = try {
                firestore.collection("users")
                    .whereEqualTo("role", "STUDENT")
                    .whereEqualTo("isActive", false)
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get().await()
            } catch (e: Exception) {
                android.util.Log.w("UserRepository", "OrderBy failed, trying without orderBy", e)
                firestore.collection("users")
                    .whereEqualTo("role", "STUDENT")
                    .whereEqualTo("isActive", false)
                    .get().await()
            }
            
            android.util.Log.d("UserRepository", "Inactive students query returned: ${snapshot.documents.size} documents")
            
            val inactiveStudents = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data
                    if (data != null) {
                        val student = Student(
                            uid = doc.id,
                            studentId = data["studentId"] as? String ?: "",
                            tcId = data["tcId"] as? String ?: "",
                            name = data["name"] as? String ?: "",
                            surname = data["surname"] as? String ?: "",
                            birthDate = data["birthDate"] as? String ?: "",
                            gender = data["gender"] as? String ?: "",
                            bloodType = data["bloodType"] as? String ?: "",
                            classId = data["classId"] as? String ?: "",
                            className = data["className"] as? String ?: "",
                            parentName = data["parentName"] as? String ?: "",
                            parentSurname = data["parentSurname"] as? String ?: "",
                            parentPhone = data["parentPhone"] as? String ?: "",
                            parentEmail = data["parentEmail"] as? String ?: "",
                            address = data["address"] as? String ?: "",
                            emergencyContactName = data["emergencyContactName"] as? String ?: "",
                            emergencyContactPhone = data["emergencyContactPhone"] as? String ?: "",
                            healthInfo = data["healthInfo"] as? String ?: "",
                            allergies = data["allergies"] as? String ?: "",
                            medications = data["medications"] as? String ?: "",
                            isActive = data["isActive"] as? Boolean ?: false,
                            createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                            updatedAt = data["updatedAt"] as? Long ?: System.currentTimeMillis()
                        )
                        android.util.Log.d("UserRepository", "Inactive Student: ${student.name} ${student.surname} - Class: ${student.className}")
                        student
                    } else null
                } catch (e: Exception) {
                    android.util.Log.w("UserRepository", "Failed to parse inactive student document: ${doc.id}", e)
                    null
                }
            }
            
            android.util.Log.d("UserRepository", "Inactive students loaded successfully: ${inactiveStudents.size}")
            inactiveStudents
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to load inactive students", e)
            emptyList()
        }
    }

    override suspend fun getPreRegistrationStudents(): List<Student> {
        return try {
            android.util.Log.d("UserRepository", "Pre-registration area should be empty as requested")
            // Kullanıcı isteği: Ön kayıt alanı boş kalsın
            emptyList()
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to load pre-registration students", e)
            emptyList()
        }
    }



    override suspend fun getStudentsByStatus(isActive: Boolean): List<Student> {
        return try {
            val snapshot = firestore.collection("students")
                .whereEqualTo("isActive", isActive)
                .get().await()
            val students = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Student::class.java)?.copy(uid = doc.id)
            }
            android.util.Log.d("UserRepository", "Students by status ($isActive) loaded: ${students.size}")
            students
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to load students by status", e)
            emptyList()
        }
    }

    override suspend fun getStaffByActiveStatus(isActive: Boolean): List<User> {
        return try {
            val snapshot = firestore.collection("users")
                .whereIn("role", listOf("ADMIN", "TEACHER"))
                .whereEqualTo("isActive", isActive)
                .get().await()
            val staff = snapshot.documents.mapNotNull { doc ->
                val data = doc.data
                if (data != null) {
                    User(
                        uid = doc.id,
                        name = data["name"] as? String ?: "",
                        surname = data["surname"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        role = when (data["role"] as? String) {
                            "ADMIN" -> UserRole.ADMIN
                            "TEACHER" -> UserRole.TEACHER
                            "PARENT" -> UserRole.PARENT
                            else -> UserRole.TEACHER
                        },
                        position = data["position"] as? String ?: "", // Position bilgisi eklendi
                        createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                        isActive = data["isActive"] as? Boolean ?: true
                    )
                } else null
            }
            android.util.Log.d("UserRepository", "Staff by active status ($isActive) loaded: ${staff.size}")
            staff
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to load staff by active status", e)
            emptyList()
        }
    }

    override suspend fun addStaffDetailed(staff: com.mahmutgunduz.edukids.data.model.Staff): Boolean {
        return try {
            val result = firestore.collection("staff").add(staff).await()
            android.util.Log.d("UserRepository", "Staff detailed added successfully with ID: ${result.id}")
            true
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to add staff detailed", e)
            false
        }
    }
}