package dev.ch8n.encryptedpayloads.ui.data

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Int,
    val value: String
)
