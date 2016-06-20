/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.model.ResultReviews;

/*
 * TODO: Create JavaDoc
 */
public class ReviewAdapter extends ArrayAdapter<ResultReviews.Review>{

    private final List<ResultReviews.Review> reviews;

    public ReviewAdapter(Context context, List<ResultReviews.Review> reviews) {
        super(context, 0, reviews);
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResultReviews.Review review = reviews.get(position);
        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.review_row_layout, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.review_author_text_view) TextView author;
        @BindView(R.id.review_content_text_view) TextView content;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
