package com.feriramara.zakony.model

data class Vote(
    val id: String,
    val name: String,
    val date: String,
    val number: String,
    val aye_votes: String,
    val no_votes: String,
    val possible_turnout: String
) {
}