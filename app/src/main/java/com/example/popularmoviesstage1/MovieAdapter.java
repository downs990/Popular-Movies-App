package com.example.popularmoviesstage1;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private final int mNumberItems;
    private final Context context;
    private final ArrayList<String> movieTitleImages;
    final private ListItemClickListener mOnClickListener;
    private static int viewHolderCount;


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public MovieAdapter(ArrayList<String> movieTitleImages, ListItemClickListener listener, Context context) {

        mOnClickListener = listener;
        viewHolderCount = 0;
        this.movieTitleImages = movieTitleImages;
        this.context = context;

        mNumberItems = movieTitleImages.size();
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    class MovieViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {

        final ImageView listItemImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            listItemImageView = itemView.findViewById(R.id.iv_movie);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            // NOTE: Always HTTPS not HTTP
            String selectedMovieImage = "https://image.tmdb.org/t/p/original" + movieTitleImages.get(listIndex);
            Picasso.with(context)
                    .load(selectedMovieImage)
                    .into(listItemImageView);

        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
