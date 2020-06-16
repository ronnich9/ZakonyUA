package com.feriramara.zakony.network


import com.feriramara.zakony.BuildConfig
import com.feriramara.zakony.model.Policy
import com.feriramara.zakony.model.PolicyDetail
import com.feriramara.zakony.model.Vote
import com.feriramara.zakony.model.VoteDetail
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://rada4you.org/api/v1/"
private const val API_KEY = BuildConfig.API_KEY

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface RadaApiService {

    @GET("divisions.json?key=$API_KEY")
    suspend fun getAllVotes(): List<Vote>

    @GET("divisions/{id}.json?key=$API_KEY")
    suspend fun getVoteDetail(@Path("id") id: String?): VoteDetail

    @GET("policies.json?key=$API_KEY")
    suspend fun getAllPolicies(): List<Policy>

    @GET("policies/{id}.json?key=$API_KEY")
    suspend fun getPolicyDetail(@Path("id") id: Int?): PolicyDetail
}

object RadaApi {
    val retrofitService : RadaApiService by lazy { retrofit.create(RadaApiService::class.java) }
}
