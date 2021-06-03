package com.example.egoeco_app.model

import com.example.egoeco_app.model.entity.GithubUser
import com.example.egoeco_app.model.entity.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EgoEcoAPIService {
//    @GET("users/s10th24b")
    @GET("users/{user}")
//    fun getUser(): Observable<GithubUser>
    fun getUser(@Path("user") user: String): Observable<GithubUser>
//    fun getUser(@Path("user") user: String): Observable<GithubUser>

    @GET("search/repositories?q=tetris+&per_page=200")
    fun getRepos(): Observable<GithubUser>

    @POST("users/{user}")
    fun postUser(@Path("user") user: String): Completable
}