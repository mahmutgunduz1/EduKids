package com.mahmutgunduz.edukids.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutgunduz.edukids.databinding.FragmentActiveStudentsBinding
import com.mahmutgunduz.edukids.ui.adapter.StudentAdapter
import com.mahmutgunduz.edukids.ui.viewmodel.RecordManagementViewModel
import com.mahmutgunduz.edukids.data.model.Student
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActiveStudentsFragment : Fragment() {

    private var _binding: FragmentActiveStudentsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: RecordManagementViewModel by viewModels()
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveStudentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeStudents()
        loadStudents()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter { student ->
            // Öğrenci detayları veya düzenleme
        }
        
        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }
    }

    private fun observeStudents() {
        viewModel.students.observe(viewLifecycleOwner) { students ->
            studentAdapter.submitList(students)
        }
    }
    
    private fun loadStudents() {
        viewModel.loadStudents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}