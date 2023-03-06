package hr.tvz.android.qrscan.view

import hr.tvz.android.qrscan.model.Movie

interface IMovies {
    interface IMoviesActivity {
        fun setMoviesSuccess(movies: MutableList<Movie>)
        fun setMoviesFailure(errorMessage: String)
    }

    interface IMoviesPresenter {
        fun onStart(token: String, ipv4: String)
        fun onFilterMovies(token: String, searchText: String, ipv4: String)
    }

    interface IMoviesList {
        interface Finished {
            fun setMoviesSuccess(movies: MutableList<Movie>)
            fun setMoviesFailure(errorMessage: String)
        }

        fun getMovies(listener: Finished, token: String, searchText: String, ipv4: String)
    }
}