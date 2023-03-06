package hr.tvz.android.qrscan.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import hr.tvz.android.qrscan.R
import hr.tvz.android.qrscan.model.Movie
import hr.tvz.android.qrscan.view.EventsActivity


class MoviesAdapter(var movies: MutableList<Movie>) : RecyclerView.Adapter<MoviesAdapter.MovieViewItem>() {

    class MovieViewItem(
        movieView: View,
    ) : RecyclerView.ViewHolder(movieView) {
        val titleView: TextView = movieView.findViewById(R.id.title_text);
    };



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewItem {
        return MovieViewItem(LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false));
    }


    override fun onBindViewHolder(holder: MovieViewItem, position: Int) {
        val movieViewItem = holder

        movieViewItem.titleView.text = movies[position].title


        movieViewItem.itemView.setOnClickListener {
            val intent = Intent(holder.titleView.context, EventsActivity::class.java)
            intent.putExtra("selectedMovieId", movies[position]?.id.toString())
            holder.titleView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return movies.size;
    }
}