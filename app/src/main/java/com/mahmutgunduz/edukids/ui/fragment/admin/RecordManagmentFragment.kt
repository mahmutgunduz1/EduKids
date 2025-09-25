package com.mahmutgunduz.edukids.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.widget.Toast
import com.mahmutgunduz.edukids.R
import com.mahmutgunduz.edukids.ui.adapter.ClassAdapter
import com.mahmutgunduz.edukids.ui.adapter.StudentAdapter
import com.mahmutgunduz.edukids.ui.adapter.StaffAdapter
import com.mahmutgunduz.edukids.ui.viewmodel.RecordManagementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class RecordManagmentFragment : Fragment() {

    private val viewModel: RecordManagementViewModel by viewModels()
    
    private lateinit var cardAddStudent: View
    private lateinit var cardAddStaff: View
    private lateinit var cardAddClass: View
    private lateinit var fabMain: FloatingActionButton
    private lateinit var fabAddStudent: FloatingActionButton
    private lateinit var fabAddStaff: FloatingActionButton
    private lateinit var fabAddClass: FloatingActionButton
    private lateinit var fabMenuOverlay: View
    private lateinit var labelAddStudent: TextView
    private lateinit var labelAddStaff: TextView
    private lateinit var labelAddClass: TextView
    
    // RecyclerViews
    private lateinit var rvActiveStudents: RecyclerView
    private lateinit var rvStaff: RecyclerView
    private lateinit var rvClasses: RecyclerView
    private lateinit var rvPreRegistrations: RecyclerView
    private lateinit var rvInactiveStudents: RecyclerView
    


    private lateinit var classAdapter: ClassAdapter
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var inactiveStudentAdapter: StudentAdapter
    private lateinit var preRegStudentAdapter: StudentAdapter
    private lateinit var staffAdapter: StaffAdapter
    
    // Chips
    private lateinit var chipActiveStudents: Chip
    private lateinit var chipStaff: Chip
    private lateinit var chipClasses: Chip
    private lateinit var chipPreReg: Chip
    private lateinit var chipInactiveStudents: Chip
    

    
    private var isFabMenuOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record_managment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupRecyclerViews()
        setupClickListeners()
        setupChipListeners()
        setupInitialFabState()
        setupObservers()

        loadData()
        
        // Debug: Firebase bağlantısını test et
        testFirebaseData()
    }
    
    private fun testFirebaseData() {
        lifecycleScope.launch {
            try {
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                
                // Users koleksiyonunu test et - Tüm veriler burada
                val usersSnapshot = firestore.collection("users").get().await()
                android.util.Log.d("FirebaseDebug", "Users collection size: ${usersSnapshot.documents.size}")
                
                var studentCount = 0
                var activeStudentCount = 0
                var inactiveStudentCount = 0
                var staffCount = 0
                
                usersSnapshot.documents.forEach { doc ->
                    val data = doc.data
                    val role = data?.get("role") as? String ?: "unknown"
                    val name = data?.get("name") as? String ?: "Unknown"
                    val surname = data?.get("surname") as? String ?: "Unknown"
                    val isActive = data?.get("isActive") as? Boolean ?: true
                    
                    when (role) {
                        "STUDENT" -> {
                            studentCount++
                            if (isActive) activeStudentCount++ else inactiveStudentCount++
                            android.util.Log.d("FirebaseDebug", "Student: $name $surname - isActive: $isActive")
                        }
                        "TEACHER", "ADMIN" -> {
                            staffCount++
                            android.util.Log.d("FirebaseDebug", "Staff: $name $surname - Role: $role")
                        }
                        else -> {
                            android.util.Log.d("FirebaseDebug", "Other User: $name $surname - Role: $role")
                        }
                    }
                }
                
                android.util.Log.d("FirebaseDebug", "Summary - Total Students: $studentCount, Active: $activeStudentCount, Inactive: $inactiveStudentCount, Staff: $staffCount")
                
                // Classes koleksiyonunu test et
                val classesSnapshot = firestore.collection("classes").get().await()
                android.util.Log.d("FirebaseDebug", "Classes collection size: ${classesSnapshot.documents.size}")
                classesSnapshot.documents.forEach { doc ->
                    val data = doc.data
                    val className = data?.get("name") as? String ?: "Unknown"
                    android.util.Log.d("FirebaseDebug", "Class: $className")
                }
                
            } catch (e: Exception) {
                android.util.Log.e("FirebaseDebug", "Firebase test failed", e)
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        android.util.Log.d("RecordManagement", "Fragment resumed - refreshing data dynamically...")
        
        // Fragment'e geri dönüldüğünde verileri dinamik olarak yenile
        refreshCurrentData()
        
        // Arka planda diğer verileri de yenile
        lifecycleScope.launch {
            android.util.Log.d("RecordManagement", "Background refresh of all data...")
            viewModel.forceRefreshStaff()
            viewModel.forceRefreshClasses()
        }
    }

    private fun initViews(view: View) {
        cardAddStudent = view.findViewById(R.id.cardAddStudent)
        cardAddStaff = view.findViewById(R.id.cardAddStaff)
        cardAddClass = view.findViewById(R.id.cardAddClass)
        fabMain = view.findViewById(R.id.fabMain)
        fabAddStudent = view.findViewById(R.id.fabAddStudent)
        fabAddStaff = view.findViewById(R.id.fabAddStaff)
        fabAddClass = view.findViewById(R.id.fabAddClass)
        fabMenuOverlay = view.findViewById(R.id.fabMenuOverlay)
        labelAddStudent = view.findViewById(R.id.labelAddStudent)
        labelAddStaff = view.findViewById(R.id.labelAddStaff)
        labelAddClass = view.findViewById(R.id.labelAddClass)
        
        // RecyclerViews
        rvActiveStudents = view.findViewById(R.id.rvActiveStudents)
        rvStaff = view.findViewById(R.id.rvStaff)
        rvClasses = view.findViewById(R.id.rvClasses)
        rvPreRegistrations = view.findViewById(R.id.rvPreRegistrations)
        rvInactiveStudents = view.findViewById(R.id.rvInactiveStudents)
        
        // Chips
        chipActiveStudents = view.findViewById(R.id.chipActiveStudents)
        chipStaff = view.findViewById(R.id.chipStaff)
        chipClasses = view.findViewById(R.id.chipClasses)
        chipPreReg = view.findViewById(R.id.chipPreReg)
        chipInactiveStudents = view.findViewById(R.id.chipInactiveStudents)
        

    }

    private fun setupClickListeners() {
        // Quick Action Cards
        cardAddStudent.setOnClickListener {
            navigateToAddStudent()
        }

        cardAddStaff.setOnClickListener {
            navigateToAddStaff()
        }

        cardAddClass.setOnClickListener {
            navigateToAddClass()
        }

        // FAB Menu
        fabMain.setOnClickListener {
            toggleFabMenu()
        }

        fabAddStudent.setOnClickListener {
            navigateToAddStudent()
            closeFabMenu()
        }

        fabAddStaff.setOnClickListener {
            navigateToAddStaff()
            closeFabMenu()
        }

        fabAddClass.setOnClickListener {
            navigateToAddClass()
            closeFabMenu()
        }
        
        // Overlay'e tıklandığında menüyü kapat
        fabMenuOverlay.setOnClickListener {
            closeFabMenu()
        }
    }

    private fun navigateToAddStudent() {
        findNavController().navigate(R.id.action_recordManagment_to_addStudent)
    }

    private fun navigateToAddStaff() {
        findNavController().navigate(R.id.action_recordManagment_to_addStaff)
    }

    private fun navigateToAddClass() {
        findNavController().navigate(R.id.action_recordManagment_to_addClass)
    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    private fun openFabMenu() {
        isFabMenuOpen = true
        
        // Overlay'i göster
        fabMenuOverlay.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate()
                .alpha(0.5f)
                .setDuration(300)
                .start()
        }
        
        // FAB'ları ve label'ları görünür yap ve başlangıç pozisyonlarını ayarla
        fabAddStudent.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        labelAddStudent.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        
        fabAddStaff.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        labelAddStaff.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        
        fabAddClass.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        labelAddClass.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        
        // Animasyonları başlat - Layout'taki pozisyonlardan ana FAB pozisyonuna
        fabAddStudent.animate()
            .translationY(360f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(0)
            .start()
        labelAddStudent.animate()
            .translationY(360f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(0)
            .start()
            
        fabAddStaff.animate()
            .translationY(240f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(50)
            .start()
        labelAddStaff.animate()
            .translationY(240f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(50)
            .start()
            
        fabAddClass.animate()
            .translationY(120f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(100)
            .start()
        labelAddClass.animate()
            .translationY(120f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(100)
            .start()
            
        fabMain.animate()
            .rotation(45f)
            .setDuration(300)
            .start()
    }

    private fun closeFabMenu() {
        isFabMenuOpen = false
        
        // Overlay'i gizle
        fabMenuOverlay.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                fabMenuOverlay.visibility = View.GONE
            }
            .start()
        
        // Animasyonları başlat
        fabAddStudent.animate()
            .translationY(0f)
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(200)
            .setStartDelay(0)
            .withEndAction {
                fabAddStudent.visibility = View.GONE
            }
            .start()
        labelAddStudent.animate()
            .translationY(0f)
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(200)
            .setStartDelay(0)
            .withEndAction {
                labelAddStudent.visibility = View.GONE
            }
            .start()
            
        fabAddStaff.animate()
            .translationY(0f)
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(200)
            .setStartDelay(50)
            .withEndAction {
                fabAddStaff.visibility = View.GONE
            }
            .start()
        labelAddStaff.animate()
            .translationY(0f)
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(200)
            .setStartDelay(50)
            .withEndAction {
                labelAddStaff.visibility = View.GONE
            }
            .start()
            
        fabAddClass.animate()
            .translationY(0f)
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(200)
            .setStartDelay(100)
            .withEndAction {
                fabAddClass.visibility = View.GONE
            }
            .start()
        labelAddClass.animate()
            .translationY(0f)
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(200)
            .setStartDelay(100)
            .withEndAction {
                labelAddClass.visibility = View.GONE
            }
            .start()
            
        fabMain.animate()
            .rotation(0f)
            .setDuration(300)
            .start()
    }
    
    private fun setupObservers() {
        android.util.Log.d("RecordManagement", "Setting up observers...")
        
        // Students observer - Ana observer
        viewModel.students.observe(viewLifecycleOwner) { students ->
            android.util.Log.d("RecordManagement", "Students observer triggered: ${students.size} students")
            
            // Debug: Her öğrenciyi logla
            students.forEachIndexed { index, student ->
                android.util.Log.d("RecordManagement", "Student $index: ${student.name} ${student.surname} - Active: ${student.isActive} - Class: ${student.className}")
            }
            
            // Hangi chip seçiliyse ona göre güncelle
            when {
                chipActiveStudents.isChecked -> {
                    android.util.Log.d("RecordManagement", "Updating active students adapter with ${students.size} students")
                    studentAdapter.submitList(students.toList()) {
                        // Liste güncellenince RecyclerView'i yenile
                        rvActiveStudents.post {
                            rvActiveStudents.scrollToPosition(0)
                        }
                    }
                    updateEmptyState(students.isEmpty())
                }
                chipInactiveStudents.isChecked -> {
                    android.util.Log.d("RecordManagement", "Updating inactive students adapter with ${students.size} students")
                    inactiveStudentAdapter.submitList(students.toList()) {
                        rvInactiveStudents.post {
                            rvInactiveStudents.scrollToPosition(0)
                        }
                    }
                    updateEmptyState(students.isEmpty())
                }
                chipPreReg.isChecked -> {
                    android.util.Log.d("RecordManagement", "Updating pre-reg students adapter with ${students.size} students")
                    preRegStudentAdapter.submitList(students.toList()) {
                        rvPreRegistrations.post {
                            rvPreRegistrations.scrollToPosition(0)
                        }
                    }
                    updateEmptyState(students.isEmpty())
                }
            }
        }
        
        // Staff observer
        viewModel.staff.observe(viewLifecycleOwner) { staff ->
            android.util.Log.d("RecordManagement", "Staff observer triggered: ${staff.size} staff")
            
            if (chipStaff.isChecked) {
                android.util.Log.d("RecordManagement", "Updating staff adapter with ${staff.size} staff")
                staffAdapter.submitList(staff.toList()) {
                    rvStaff.post {
                        rvStaff.scrollToPosition(0)
                    }
                }
                updateEmptyState(staff.isEmpty())
            }
        }
        
        // Classes observer
        viewModel.classes.observe(viewLifecycleOwner) { classes ->
            android.util.Log.d("RecordManagement", "Classes observer triggered: ${classes.size} classes")
            
            if (chipClasses.isChecked) {
                android.util.Log.d("RecordManagement", "Updating classes adapter with ${classes.size} classes")
                classAdapter.submitList(classes.toList()) {
                    rvClasses.post {
                        rvClasses.scrollToPosition(0)
                    }
                }
                updateEmptyState(classes.isEmpty())
            }
        }
        
        // Loading observer
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            android.util.Log.d("RecordManagement", "Loading state: $isLoading")
            // Loading indicator göster/gizle
        }
        
        // Error observer
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                android.util.Log.e("RecordManagement", "Error occurred: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        android.util.Log.d("RecordManagement", "Observers setup completed")
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        val emptyStateLayout = view?.findViewById<View>(R.id.layoutEmptyState)
        emptyStateLayout?.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
    

    

    
    private fun setupInitialFabState() {
        // Overlay'i gizle
        fabMenuOverlay.apply {
            visibility = View.GONE
            alpha = 0f
        }
        
        // FAB'ları ve label'ları başlangıçta gizle ve pozisyonlarını ayarla
        fabAddStudent.apply {
            visibility = View.GONE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        labelAddStudent.apply {
            visibility = View.GONE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        
        fabAddStaff.apply {
            visibility = View.GONE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        labelAddStaff.apply {
            visibility = View.GONE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        
        fabAddClass.apply {
            visibility = View.GONE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        labelAddClass.apply {
            visibility = View.GONE
            alpha = 0f
            translationY = 0f
            scaleX = 0f
            scaleY = 0f
        }
        
        // Ana FAB'ı normal durumda ayarla
        fabMain.apply {
            rotation = 0f
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
        }
    }
    
    private fun setupRecyclerViews() {
        android.util.Log.d("RecordManagement", "Setting up RecyclerViews...")
        
        // Student Adapter
        studentAdapter = StudentAdapter { student ->
            android.util.Log.d("RecordManagement", "Student clicked: ${student.name}")
            // Öğrenci detayları
        }
        
        // Her RecyclerView için ayrı adapter instance'ları oluştur
        inactiveStudentAdapter = StudentAdapter { student ->
            android.util.Log.d("RecordManagement", "Inactive student clicked: ${student.name}")
        }
        
        preRegStudentAdapter = StudentAdapter { student ->
            android.util.Log.d("RecordManagement", "Pre-reg student clicked: ${student.name}")
        }
        
        rvActiveStudents.apply {
            adapter = studentAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            android.util.Log.d("RecordManagement", "Active students RecyclerView setup complete")
        }
        
        rvInactiveStudents.apply {
            adapter = inactiveStudentAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            android.util.Log.d("RecordManagement", "Inactive students RecyclerView setup complete")
        }

        // Staff Adapter
        staffAdapter = StaffAdapter { staff ->
            android.util.Log.d("RecordManagement", "Staff clicked: ${staff.name}")
            // Personel detayları
        }
        
        rvStaff.apply {
            adapter = staffAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            android.util.Log.d("RecordManagement", "Staff RecyclerView setup complete")
        }
        
        // Class Adapter
        classAdapter = ClassAdapter { schoolClass ->
            android.util.Log.d("RecordManagement", "Class clicked: ${schoolClass.name}")
            // Sınıf detayları
        }
        rvClasses.apply {
            adapter = classAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            android.util.Log.d("RecordManagement", "Classes RecyclerView setup complete")
        }
        
        // Pre-registration için ayrı adapter
        rvPreRegistrations.apply {
            adapter = preRegStudentAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            android.util.Log.d("RecordManagement", "Pre-registrations RecyclerView setup complete")
        }
        
        android.util.Log.d("RecordManagement", "All RecyclerViews setup completed")
    }
    
    private fun setupChipListeners() {
        chipActiveStudents.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                android.util.Log.d("RecordManagement", "Active students chip selected")
                showRecyclerView(rvActiveStudents)
                // Önce RecyclerView'i göster, sonra veri yükle
                lifecycleScope.launch {
                    viewModel.loadActiveStudents()
                }
            }
        }
        
        chipStaff.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                android.util.Log.d("RecordManagement", "Staff chip selected")
                showRecyclerView(rvStaff)
                lifecycleScope.launch {
                    viewModel.loadStaff()
                }
            }
        }
        
        chipClasses.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                android.util.Log.d("RecordManagement", "Classes chip selected")
                showRecyclerView(rvClasses)
                lifecycleScope.launch {
                    viewModel.loadClasses()
                }
            }
        }
        
        chipPreReg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                android.util.Log.d("RecordManagement", "Pre-registration chip selected")
                showRecyclerView(rvPreRegistrations)
                lifecycleScope.launch {
                    viewModel.loadPreRegistrations()
                }
            }
        }
        
        chipInactiveStudents.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                android.util.Log.d("RecordManagement", "Inactive students chip selected")
                showRecyclerView(rvInactiveStudents)
                lifecycleScope.launch {
                    viewModel.loadInactiveStudents()
                }
            }
        }
    }
    
    private fun showRecyclerView(targetRv: RecyclerView) {
        android.util.Log.d("RecordManagement", "Showing RecyclerView: ${getRecyclerViewName(targetRv)}")
        
        // Tüm RecyclerView'leri gizle
        rvActiveStudents.visibility = View.GONE
        rvStaff.visibility = View.GONE
        rvClasses.visibility = View.GONE
        rvPreRegistrations.visibility = View.GONE
        rvInactiveStudents.visibility = View.GONE
        
        // Empty state'i gizle
        view?.findViewById<View>(R.id.layoutEmptyState)?.visibility = View.GONE
        
        // Seçilen RecyclerView'i göster ve adapter'ı yenile
        targetRv.visibility = View.VISIBLE
        targetRv.adapter?.notifyDataSetChanged()
        
        // RecyclerView'in layout'unu zorla yenile
        targetRv.post {
            targetRv.requestLayout()
        }
        
        android.util.Log.d("RecordManagement", "RecyclerView visibility set to VISIBLE and refreshed")
    }
    
    private fun getRecyclerViewName(rv: RecyclerView): String {
        return when (rv) {
            rvActiveStudents -> "Active Students"
            rvStaff -> "Staff"
            rvClasses -> "Classes"
            rvPreRegistrations -> "Pre Registrations"
            rvInactiveStudents -> "Inactive Students"
            else -> "Unknown"
        }
    }
    

    

    private fun loadData() {
        android.util.Log.d("RecordManagement", "Loading initial data...")
        
        // Firebase bağlantısını test et
        testFirebaseData()
        
        // İlk açılışta aktif öğrenciler chip'ini seç ve yükle
        chipActiveStudents.isChecked = true
        showRecyclerView(rvActiveStudents)
        
        // Direkt olarak aktif öğrencileri yükle
        android.util.Log.d("RecordManagement", "Loading active students on startup...")
        lifecycleScope.launch {
            viewModel.loadActiveStudents()
            
            // Diğer verileri de arka planda yükle
            android.util.Log.d("RecordManagement", "Pre-loading staff and classes data...")
            viewModel.loadStaff()
            viewModel.loadClasses()
        }
    }
    
    private fun testFirebaseConnection() {
        lifecycleScope.launch {
            try {
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val snapshot = firestore.collection("users").limit(1).get().await()
                android.util.Log.d("FirebaseTest", "Firebase connection successful. Document count: ${snapshot.documents.size}")
                
                if (snapshot.documents.isEmpty()) {
                    android.util.Log.w("FirebaseTest", "No documents found in 'users' collection")
                    android.widget.Toast.makeText(requireContext(), "Firebase'de henüz kullanıcı verisi yok", android.widget.Toast.LENGTH_LONG).show()
                } else {
                    // İlk belgeyi kontrol et
                    val firstDoc = snapshot.documents.first()
                    android.util.Log.d("FirebaseTest", "First document data: ${firstDoc.data}")
                }
            } catch (e: Exception) {
                android.util.Log.e("FirebaseTest", "Firebase connection failed", e)
                android.widget.Toast.makeText(requireContext(), "Firebase bağlantı hatası: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
    

    

    
    // Verileri yenile
    private fun refreshCurrentData() {
        android.util.Log.d("RecordManagement", "Refreshing current data based on selected chip...")
        lifecycleScope.launch {
            when {
                chipActiveStudents.isChecked -> {
                    android.util.Log.d("RecordManagement", "Refreshing active students...")
                    showRecyclerView(rvActiveStudents)
                    viewModel.loadActiveStudents()
                }
                chipInactiveStudents.isChecked -> {
                    android.util.Log.d("RecordManagement", "Refreshing inactive students...")
                    showRecyclerView(rvInactiveStudents)
                    viewModel.loadInactiveStudents()
                }
                chipPreReg.isChecked -> {
                    android.util.Log.d("RecordManagement", "Refreshing pre-registrations...")
                    showRecyclerView(rvPreRegistrations)
                    viewModel.loadPreRegistrations()
                }
                chipStaff.isChecked -> {
                    android.util.Log.d("RecordManagement", "Refreshing staff...")
                    showRecyclerView(rvStaff)
                    viewModel.loadStaff()
                }
                chipClasses.isChecked -> {
                    android.util.Log.d("RecordManagement", "Refreshing classes...")
                    showRecyclerView(rvClasses)
                    viewModel.loadClasses()
                }
                else -> {
                    android.util.Log.d("RecordManagement", "No chip selected, loading active students by default...")
                    chipActiveStudents.isChecked = true
                    showRecyclerView(rvActiveStudents)
                    viewModel.loadActiveStudents()
                }
            }
        }
    }

}