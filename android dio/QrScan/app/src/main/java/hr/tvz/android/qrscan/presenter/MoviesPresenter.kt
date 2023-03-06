package hr.tvz.android.qrscan.presenter

import hr.tvz.android.qrscan.model.Movie
import hr.tvz.android.qrscan.model.MoviesList
import hr.tvz.android.qrscan.view.IMovies
import hr.tvz.android.qrscan.view.MoviesActivity


class MoviesPresenter(var iMoviesActivity: MoviesActivity, var moviesList: MoviesList): IMovies.IMoviesPresenter, IMovies.IMoviesList.Finished{

    override fun setMoviesSuccess(movies: MutableList<Movie>) {
        iMoviesActivity.setMoviesSuccess(movies)
    }

    override fun setMoviesFailure(errorMessage: String) {
        iMoviesActivity.setMoviesFailure(errorMessage)
    }

    override fun onStart(token: String, ipv4: String) {
        moviesList.getMovies(this, token, "", ipv4)
    }

    override fun onFilterMovies(token: String, ipv4: String, searchText: String) {
        moviesList.getMovies(this, token, searchText, ipv4);
    }

}