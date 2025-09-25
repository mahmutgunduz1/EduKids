package com.mahmutgunduz.edukids.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mahmutgunduz.edukids.databinding.FragmentDailyReportBinding

class DailyReportFragment : Fragment() {
    private lateinit var binding: FragmentDailyReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        // Günlük rapor verilerini yükle
        loadDailyReportData()
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun loadDailyReportData() {
        // Gerçek uygulamada Firebase'den veri çekilecek
        // Şimdilik örnek veriler
    }
}