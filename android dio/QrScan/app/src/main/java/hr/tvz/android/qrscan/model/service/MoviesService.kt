package hr.tvz.android.qrscan.model.service

import hr.tvz.android.qrscan.model.Movie
import retrofit2.http.GET
import retrofit2.http.Header

interface MoviesService {

    @GET("/movies/active")
    fun getActiveMovies(@Header("Authorization") token: String): retrofit2.Call<MutableList<Movie>>

}

