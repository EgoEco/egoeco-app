package com.example.egoeco_app.model

import com.example.egoeco_app.model.entity.GithubUser
import com.example.egoeco_app.model.entity.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EgoEcoAPIService {
    @GET("users/{user}")
//    fun getUser(@Path("user") user: String): Observable<User>
    fun getUser(@Path("user") user: String): Observable<GithubUser>

    @POST("users/{user}")
    fun postUser(@Path("user") user: String): Completable
}