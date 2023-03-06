package hr.tvz.android.qrscan.model

import hr.tvz.android.qrscan.model.service.MoviesService
import hr.tvz.android.qrscan.model.service.ServiceRetrofit
import hr.tvz.android.qrscan.view.IMovies
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MoviesList: IMovies.IMoviesList {

    override fun getMovies(listener: IMovies.IMoviesList.Finished, token: String, searchText: String, ipv4: String) {
        ServiceRetrofit().createAndGetService(MoviesService::class.java, ipv4).getActiveMovies("Bearer $token").enqueue(object : Callback<MutableList<Movie>> {
            override fun onResponse(call: Call<MutableList<Movie>>, response: Response<MutableList<Movie>>) {
                if(response.isSuccessful) {
                    if(searchText.isNotEmpty()) {
                        val movies: MutableList<Movie> = response.body()!!
                        val filteredMovies = movies.filter{movie -> movie.title.contains(searchText, ignoreCase = true)}
                        if(filteredMovies.isEmpty()) {
                            listener.setMoviesFailure("Ne postoji film s tim naslovom")
                        } else {
                            listener.setMoviesSuccess(filteredMovies as MutableList<Movie>)
                        }
                    } else {
                        val movies: MutableList<Movie> = response.body()!!
                        if(movies.isEmpty()) {
                            listener.setMoviesFailure("Ne postoji nijedan filma")
                        } else {
                            listener.setMoviesSuccess(response.body()!!)
                        }
                    }
                } else {
                    listener.setMoviesFailure("Nažalost, nismo mogli dohvatiti filmove")
                }

            }

            override fun onFailure(call: Call<MutableList<Movie>>, t: Throwable) {
                listener.setMoviesFailure("Došlo je do pogreške")
            }
            })
    }
}