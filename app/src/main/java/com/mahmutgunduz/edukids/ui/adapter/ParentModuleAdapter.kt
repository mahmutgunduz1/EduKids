package com.mahmutgunduz.edukids.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahmutgunduz.edukids.data.model.ParentModule
import com.mahmutgunduz.edukids.databinding.ItemParentModuleBinding

class ParentModuleAdapter(
    private val modules: List<ParentModule>,
    private val onModuleClick: (ParentModule) -> Unit
) : RecyclerView.Adapter<ParentModuleAdapter.ParentModuleViewHolder>() {

    inner class ParentModuleViewHolder(private val binding: ItemParentModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(module: ParentModule) {
            binding.apply {
                tvModuleTitle.text = module.title
                tvModuleSubtitle.text = module.subtitle
                tvModuleIcon.text = module.icon
                
                // Arka plan rengini ayarla
                try {
                    cvIconBackground.setCardBackgroundColor(Color.parseColor(module.backgroundColor))
                } catch (e: Exception) {
                    // Varsayılan renk
                }
                
                // Bildirim badge'ini ayarla
                if (module.notificationCount > 0) {
                    cvNotificationBadge.visibility = View.VISIBLE
                    tvNotificationCount.text = if (module.notificationCount > 99) "99+" else module.notificationCount.toString()
                } else {
                    cvNotificationBadge.visibility = View.GONE
                }
                
                // Son güncelleme zamanını ayarla
                if (!module.lastUpdate.isNullOrEmpty()) {
                    tvLastUpdate.visibility = View.VISIBLE
                    tvLastUpdate.text = module.lastUpdate
                } else {
                    tvLastUpdate.visibility = View.GONE
                }
                
                // Yeni özellik işareti
                if (module.isNew) {
                    tvLastUpdate.visibility = View.VISIBLE
                    tvLastUpdate.text = "🆕 YENİ"
                    tvLastUpdate.setTextColor(Color.parseColor("#FF5722"))
                }
                
                // Tıklama olayı
                root.setOnClickListener {
                    onModuleClick(module)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentModuleViewHolder {
        val binding = ItemParentModuleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ParentModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParentModuleViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount(): Int = modules.size
}