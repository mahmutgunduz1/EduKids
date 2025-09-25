package com.mahmutgunduz.edukids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mahmutgunduz.edukids.R
import com.mahmutgunduz.edukids.data.model.Student

class StudentAdapter(
    private val onItemClick: (Student) -> Unit = {}
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_record, parent, false)
        return StudentViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StudentViewHolder(
        itemView: View,
        private val onItemClick: (Student) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvStudentEmail)
        private val tvDate: TextView = itemView.findViewById(R.id.tvStudentDate)
        private val tvAvatar: TextView = itemView.findViewById(R.id.tvStudentAvatar)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStudentStatus)

        fun bind(student: Student) {
            tvName.text = "${student.name} ${student.surname}"
            tvEmail.text = "${student.parentName} ${student.parentSurname} - ${student.className.ifEmpty { "Sınıf Atanmamış" }}"
            tvDate.text = "Kayıt: ${formatDate(student.createdAt)}"
            
            // Avatar için baş harfleri al
            val initials = "${student.name.firstOrNull() ?: ""}${student.surname.firstOrNull() ?: ""}"
            tvAvatar.text = initials.uppercase()
            
            // Öğrenci durumunu göster
            if (student.isActive) {
                tvStatus.text = "Aktif"
                tvStatus.setTextColor(itemView.context.getColor(R.color.primary_green))
                // Yeşil arka plan için drawable oluştur
                val activeDrawable = android.graphics.drawable.GradientDrawable()
                activeDrawable.setColor(itemView.context.getColor(R.color.primary_green))
                activeDrawable.alpha = 30 // %12 şeffaflık
                activeDrawable.cornerRadius = 8f * itemView.context.resources.displayMetrics.density
                tvStatus.background = activeDrawable
            } else {
                tvStatus.text = "Pasif"
                tvStatus.setTextColor(itemView.context.getColor(R.color.primary_pink))
                // Pembe arka plan için drawable oluştur
                val inactiveDrawable = android.graphics.drawable.GradientDrawable()
                inactiveDrawable.setColor(itemView.context.getColor(R.color.primary_pink))
                inactiveDrawable.alpha = 30 // %12 şeffaflık
                inactiveDrawable.cornerRadius = 8f * itemView.context.resources.displayMetrics.density
                tvStatus.background = inactiveDrawable
            }
            
            // Click listener
            itemView.setOnClickListener {
                onItemClick(student)
            }
        }
        
        private fun formatDate(timestamp: Long): String {
            val date = java.util.Date(timestamp)
            val format = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            return format.format(date)
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}