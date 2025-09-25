package com.mahmutgunduz.edukids.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mahmutgunduz.edukids.R
import com.mahmutgunduz.edukids.data.model.SchoolClass

class ClassAdapter(
    private val onItemClick: (SchoolClass) -> Unit = {}
) : ListAdapter<SchoolClass, ClassAdapter.ClassViewHolder>(ClassDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class_record, parent, false)
        return ClassViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ClassViewHolder(
        itemView: View,
        private val onItemClick: (SchoolClass) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvClassName: TextView = itemView.findViewById(R.id.tvClassName)
        private val tvGrade: TextView = itemView.findViewById(R.id.tvClassGrade)
        private val tvCapacity: TextView = itemView.findViewById(R.id.tvClassCapacity)
        private val tvRoom: TextView = itemView.findViewById(R.id.tvClassRoom)

        fun bind(schoolClass: SchoolClass) {
            tvClassName.text = schoolClass.name
            tvGrade.text = schoolClass.ageGroup
            tvCapacity.text = "${schoolClass.currentStudentCount}/${schoolClass.maxStudentCount}"
            tvRoom.text = "🏠 ${schoolClass.roomNumber} • 08:00-15:00"
            
            // Click listener
            itemView.setOnClickListener {
                onItemClick(schoolClass)
            }
        }
    }

    class ClassDiffCallback : DiffUtil.ItemCallback<SchoolClass>() {
        override fun areItemsTheSame(oldItem: SchoolClass, newItem: SchoolClass): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SchoolClass, newItem: SchoolClass): Boolean {
            return oldItem == newItem
        }
    }
}