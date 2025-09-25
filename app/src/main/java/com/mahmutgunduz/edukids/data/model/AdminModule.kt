package com.mahmutgunduz.edukids.data.model

data class AdminModule(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val colorRes: Int,
    val isEnabled: Boolean = true
)