/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.moviesgrid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.alvaroweb.popularmovies.R;

/*
 *  just an empty fragment with a messageView
 */
public class EmptyFragment extends Fragment {
    private static final String MESSAGE_ARG = "message";
    @BindView(R.id.start_text_view)
    TextView messageView;

    public EmptyFragment() {
    }

    public static EmptyFragment newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(MESSAGE_ARG,message);

        EmptyFragment fragment = new EmptyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, container, false);
        ButterKnife.bind(this, view);
        messageView.setText(getArguments().getString(MESSAGE_ARG));
        return view;
    }
}
