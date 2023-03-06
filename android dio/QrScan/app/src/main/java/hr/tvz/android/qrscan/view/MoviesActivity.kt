package hr.tvz.android.qrscan.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hr.tvz.android.qrscan.R
import hr.tvz.android.qrscan.databinding.ActivityMoviesBinding
import hr.tvz.android.qrscan.model.Movie
import hr.tvz.android.qrscan.model.MoviesList
import hr.tvz.android.qrscan.presenter.MoviesPresenter
import hr.tvz.android.qrscan.view.adapter.MoviesAdapter


class MoviesActivity : AppCompatActivity(), IMovies.IMoviesActivity {

    private lateinit var binding: ActivityMoviesBinding
    lateinit var presenter: MoviesPresenter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var token = ""
    private var ipv4 = "";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ipv4 = getSharedPreferences("ipAddress", AppCompatActivity.MODE_PRIVATE).getString(
            "ipAddress",
            ""
        )!!
        token = getSharedPreferences(
            "tokenPreference",
            AppCompatActivity.MODE_PRIVATE
        ).getString("token", "")!!


        presenter = MoviesPresenter(this, MoviesList())


        binding.searchBar.setOnClickListener(View.OnClickListener { view: View? ->
            searchMovie()
        })

        linearLayoutManager = LinearLayoutManager(this)
        binding.recycleViewMovies.layoutManager = linearLayoutManager

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onBackPressed() {
        return;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {

            getSharedPreferences("tokenPreference", MODE_PRIVATE).edit().remove("token").apply()
            getSharedPreferences("ipAddress", MODE_PRIVATE).edit().remove("ipAddress").apply()


            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)


        }
        return super.onOptionsItemSelected(item)
    }

    override fun setMoviesSuccess(movies: MutableList<Movie>) {
        binding.errorMessage.text = ""
        binding.errorMessage.visibility = View.GONE
        binding.recycleViewMovies.visibility = View.VISIBLE
        val adapter = MoviesAdapter(movies)
        binding.recycleViewMovies.adapter = adapter

    }

    override fun setMoviesFailure(errorMessage: String) {
        binding.errorMessage.visibility = View.VISIBLE
        binding.recycleViewMovies.visibility = View.GONE
        binding.errorMessage.text = errorMessage
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart(token, ipv4)
    }

    fun searchMovie() {
        val searchText: String = binding.searchInput.text.toString()
        presenter.onFilterMovies(token, ipv4, searchText)
    }
}