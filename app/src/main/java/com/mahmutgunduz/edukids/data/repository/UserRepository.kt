package com.mahmutgunduz.edukids.data.repository

import com.mahmutgunduz.edukids.data.model.User
import com.mahmutgunduz.edukids.data.model.Student
import com.mahmutgunduz.edukids.data.model.SchoolClass
import com.mahmutgunduz.edukids.data.model.UserRole

interface UserRepository {
    suspend fun getStudents(): List<Student>
    suspend fun getStaff(): List<User>
    suspend fun getClasses(): List<SchoolClass>
    suspend fun addStudent(student: Student): Boolean
    suspend fun addStaff(staff: User): Boolean
    suspend fun addClass(schoolClass: SchoolClass): Boolean
    suspend fun updateStudent(student: Student): Boolean
    suspend fun updateStaff(staff: User): Boolean
    suspend fun updateClass(schoolClass: SchoolClass): Boolean
    suspend fun deleteStudent(studentId: String): Boolean
    suspend fun deleteStaff(staffId: String): Boolean
    suspend fun deleteClass(classId: String): Boolean
    suspend fun getStudentsByClass(classId: String): List<Student>
    suspend fun getStaffByRole(role: UserRole): List<User>
    
    // Gelişmiş filtreleme metodları
    suspend fun getActiveStudents(): List<Student>
    suspend fun getInactiveStudents(): List<Student>
    suspend fun getPreRegistrationStudents(): List<Student>
    suspend fun getStudentsByStatus(isActive: Boolean): List<Student>
    suspend fun getStaffByActiveStatus(isActive: Boolean): List<User>
    suspend fun addStaffDetailed(staff: com.mahmutgunduz.edukids.data.model.Staff): Boolean
}