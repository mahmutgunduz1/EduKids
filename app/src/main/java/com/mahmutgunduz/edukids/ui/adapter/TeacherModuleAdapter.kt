package com.mahmutgunduz.edukids.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mahmutgunduz.edukids.databinding.ItemTeacherModuleBinding
import com.mahmutgunduz.edukids.ui.fragment.TeacherFragment

class TeacherModuleAdapter(
    private val modules: List<TeacherFragment.TeacherModule>,
    private val onModuleClick: (TeacherFragment.TeacherModule) -> Unit
) : RecyclerView.Adapter<TeacherModuleAdapter.ModuleViewHolder>() {

    inner class ModuleViewHolder(private val binding: ItemTeacherModuleBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(module: TeacherFragment.TeacherModule) {
            binding.tvModuleIcon.text = module.icon
            binding.tvModuleTitle.text = module.title
            binding.tvModuleSubtitle.text = module.subtitle
            
            // İkon arka plan rengini ayarla
            val backgroundColor = ContextCompat.getColor(binding.root.context, module.color)
            binding.cvIconBackground.setCardBackgroundColor(backgroundColor)
            
            binding.root.setOnClickListener {
                onModuleClick(module)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemTeacherModuleBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount(): Int = modules.size
}