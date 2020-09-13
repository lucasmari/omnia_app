package com.lucas.omnia.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucas.omnia.R;

/**
 * Created by Lucas on 06/11/2017.
 */

public class NavFragment5 extends Fragment {
    public static NavFragment5 newInstance() {
        NavFragment5 fragment = new NavFragment5();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nav_5, container, false);
    }
}
