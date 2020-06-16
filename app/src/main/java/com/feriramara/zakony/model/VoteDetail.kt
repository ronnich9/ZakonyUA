package com.feriramara.zakony.model

data class VoteDetail(
    val id: String,
    val name: String,
    val date: String,
    val number: String,
    val aye_votes: String,
    val no_votes: String,
    val clock_time: String,
    val votes: List<Golos>,
    val policy_divisions: List<PolicyDevision>,
    val bills: List<Bill>
) {


    class PolicyDevision(
        val policy: Policy,
        val vote: String,
        val strong: Boolean
    )


    class Policy(
        val id: String,
        val name: String,
        val description: String,
        val provisional: Boolean
    )

    class Golos(
        val vote: String,
        val member: Member
    )

    class Member(
        val id: String,
        val person: ObjectId,
        val first_name: String,
        val last_name: String,
        val electorate: String,
        val party: String
    )

    class ObjectId(
        val id: String
    )

    class Bill (
        val id: String,
        val official_id: String,
        val title: String,
        val url: String
    )
}