package com.mahmutgunduz.edukids.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahmutgunduz.edukids.databinding.ItemAdminModuleBinding
import com.mahmutgunduz.edukids.ui.fragment.AdminFragment

class AdminModuleAdapter(
    private val modules: List<AdminFragment.AdminModule>,
    private val onModuleClick: (AdminFragment.AdminModule) -> Unit
) : RecyclerView.Adapter<AdminModuleAdapter.ModuleViewHolder>() {

    inner class ModuleViewHolder(private val binding: ItemAdminModuleBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(module: AdminFragment.AdminModule) {
            binding.tvModuleIcon.text = module.icon
            binding.tvModuleTitle.text = module.title
            
            if (module.subtitle.isNotEmpty()) {
                binding.tvModuleSubtitle.text = module.subtitle
                binding.tvModuleSubtitle.visibility = android.view.View.VISIBLE
            } else {
                binding.tvModuleSubtitle.visibility = android.view.View.GONE
            }
            
            binding.root.setOnClickListener {
                onModuleClick(module)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemAdminModuleBinding.inflate(
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