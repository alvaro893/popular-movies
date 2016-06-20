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
import es.alvaroweb.popularmovies.model.ResultVideos;


/*
 * TODO: Create JavaDoc
 */
public class VideoAdapter extends ArrayAdapter<ResultVideos.Video> {

    public VideoAdapter(Context context , List<ResultVideos.Video> videosList) {
        super(context, 0, videosList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResultVideos.Video video = getItem(position);
        ViewHolder holder;

        // Inflate layout if it is not being reused
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.trailer_row_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.trailer.setText(video.getName());
        holder.source.setText(video.getSite());

        return convertView;
    }

    static final class ViewHolder{
        @BindView(R.id.trailer_text_view)TextView trailer;
        @BindView(R.id.source_trailer_text_view) TextView source;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
