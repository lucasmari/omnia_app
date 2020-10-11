package com.lucas.omnia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lucas.omnia.R;

/**
 * Created by Lucas on 06/11/2017.
 */

public class ProjectsNavFragment extends Fragment {
    public static ProjectsNavFragment newInstance() {
        ProjectsNavFragment fragment = new ProjectsNavFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }
}