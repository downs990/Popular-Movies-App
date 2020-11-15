package com.example.popularmoviesstage1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MovieReviewViewHolder> {

    private static final String TAG = ReviewsAdapter.class.getSimpleName();
    private final int mNumberItems;
    private final Context context;
    private final ArrayList<String> reviewAuthor;
    private final ArrayList<String> reviewContent;

    final private ListItemClickListener mOnClickListener;
    private static int viewHolderCount;


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public ReviewsAdapter(ArrayList<String> reviewAuthor, ArrayList<String> reviewContent, ListItemClickListener listener, Context context) {

        mOnClickListener = listener;
        viewHolderCount = 0;
        this.reviewAuthor = reviewAuthor;
        this.reviewContent  = reviewContent;
        this.context = context;

        mNumberItems = reviewAuthor.size();
    }


    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        MovieReviewViewHolder viewHolder = new MovieReviewViewHolder(view);

        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    class MovieReviewViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final TextView reviewAuthorTextView;
        final TextView reviewContentTextView;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);

            reviewAuthorTextView = itemView.findViewById(R.id.review_author_tv);
            reviewContentTextView = itemView.findViewById(R.id.review_content_tv);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            reviewAuthorTextView.setText( reviewAuthor.get(listIndex) );
            reviewContentTextView.setText( reviewContent.get(listIndex)  );
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
