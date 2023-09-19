package com.example.chattingapp.model

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val id: Int,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector
)
