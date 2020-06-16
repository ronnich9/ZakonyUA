package com.feriramara.zakony.model

data class Policy(
    val id: String,
    val name: String,
    val description: String,
    val provisional: Boolean
) {
}