package com.lucas.omnia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.adapters.InspectionAdapter
import com.lucas.omnia.models.Inspection
import com.lucas.omnia.models.Inspection.Companion.createInspectionList

class InspectionNavFragment : Fragment() {
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_inspection, container, false)
        recyclerView = rootView.findViewById(R.id.inspection_rv)
        recyclerView?.setHasFixedSize(true)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = layoutManager
        val inspectionList: List<Inspection> = createInspectionList(resources)
        val inspectionAdapter = InspectionAdapter(requireContext(), inspectionList)
        recyclerView!!.adapter = inspectionAdapter
    }
}