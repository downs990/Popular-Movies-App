package com.example.popularmoviesstage1;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MovieTrailerViewHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();
    private final int mNumberItems;
    private final Context context;
    private final ArrayList<String> movieTrailerTitles;
    final private ListItemClickListener mOnClickListener;
    private static int viewHolderCount;


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public TrailerAdapter(ArrayList<String> movieTrailerTitles, ListItemClickListener listener, Context context) {

        mOnClickListener = listener;
        viewHolderCount = 0;
        this.movieTrailerTitles = movieTrailerTitles;
        this.context = context;

        mNumberItems = movieTrailerTitles.size();
    }


    @NonNull
    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        MovieTrailerViewHolder viewHolder = new MovieTrailerViewHolder(view);

        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MovieTrailerViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    class MovieTrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final TextView listItemTextView;
        final ImageView playIconImageView;

        public MovieTrailerViewHolder(View itemView) {
            super(itemView);

            listItemTextView = itemView.findViewById(R.id.movie_trailer_title_tv);
            playIconImageView = itemView.findViewById(R.id.play_ic);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            listItemTextView.setText( movieTrailerTitles.get(listIndex) );
            playIconImageView.setColorFilter(Color.WHITE);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
