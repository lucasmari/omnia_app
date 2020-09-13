package com.lucas.omnia.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucas.omnia.R;

/**
 * Created by Lucas on 29/10/2017.
 */

public class NavFragment3 extends Fragment {
    public static NavFragment3 newInstance() {
        NavFragment3 fragment = new NavFragment3();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nav_3, container, false);
    }
}
