package com.mahmutgunduz.edukids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.mahmutgunduz.edukids.R
import com.mahmutgunduz.edukids.data.model.User

class StaffAdapter(
    private val onItemClick: (User) -> Unit = {}
) : ListAdapter<User, StaffAdapter.StaffViewHolder>(StaffDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_record, parent, false)
        return StaffViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StaffViewHolder(
        itemView: View,
        private val onItemClick: (User) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvStaffName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvStaffEmail)
        private val tvRole: TextView = itemView.findViewById(R.id.tvStaffRole)
        private val tvDate: TextView = itemView.findViewById(R.id.tvStaffDate)
        private val tvAvatar: TextView = itemView.findViewById(R.id.tvStaffAvatar)
        private val chipStatus: Chip = itemView.findViewById(R.id.chipStaffStatus)

        fun bind(staff: User) {
            tvName.text = "${staff.name} ${staff.surname}"
            tvEmail.text = staff.email
            // Position bilgisini göster - artık User modelinde position var
            tvRole.text = if (staff.position.isNotEmpty()) {
                staff.position // Seçilen pozisyonu göster (Hemşire, Sekreter, vs.)
            } else {
                // Fallback: Eğer position boşsa role'a göre göster
                when(staff.role.name) {
                    "TEACHER" -> "Öğretmen"
                    "ADMIN" -> "Yönetici"
                    else -> staff.role.name
                }
            }
            
            // Debug: Position ve Role bilgisini logla
            android.util.Log.d("StaffAdapter", "Staff: ${staff.name} - Position: ${staff.position} - Role: ${staff.role.name}")
            tvDate.text = "Başlangıç: ${formatDate(staff.createdAt)}"
            
            // Avatar için baş harfleri al
            val initials = "${staff.name.firstOrNull() ?: ""}${staff.surname.firstOrNull() ?: ""}"
            tvAvatar.text = initials.uppercase()
            
            // Personel durumunu göster
            if (staff.isActive) {
                chipStatus.text = "Aktif"
                chipStatus.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                    itemView.context.getColor(R.color.primary_green)
                )
                chipStatus.setTextColor(itemView.context.getColor(R.color.white))
            } else {
                chipStatus.text = "Pasif"
                chipStatus.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                    itemView.context.getColor(R.color.primary_pink)
                )
                chipStatus.setTextColor(itemView.context.getColor(R.color.white))
            }
            
            // Click listener
            itemView.setOnClickListener {
                onItemClick(staff)
            }
        }
        
        private fun formatDate(timestamp: Long): String {
            val date = java.util.Date(timestamp)
            val format = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            return format.format(date)
        }
    }

    class StaffDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}