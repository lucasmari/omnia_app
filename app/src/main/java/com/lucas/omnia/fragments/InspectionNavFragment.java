package com.lucas.omnia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.adapters.InspectionAdapter;
import com.lucas.omnia.models.Inspection;

import java.util.List;

public class InspectionNavFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inspection, container, false);

        recyclerView = rootView.findViewById(R.id.inspection_rv);
        recyclerView.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        List<Inspection> inspectionList = Inspection.createInspectionList(getResources());
        final InspectionAdapter inspectionAdapter = new InspectionAdapter(getContext(), inspectionList);
        recyclerView.setAdapter(inspectionAdapter);
    }
}
