package com.feriramara.zakony.model

data class PolicyDetail(
    val id: Int,
    val name: String,
    val description: String,
    val provisional: Boolean,
    val policy_divisions: List<BigVote>
) {

    class BigVote(
        val division: Vote,
        val vote: String
    )

}