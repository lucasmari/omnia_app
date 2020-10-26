package com.lucas.omnia.models;

import android.content.res.Resources;

import com.lucas.omnia.R;
import com.lucas.omnia.activities.BlocksActivity;
import com.lucas.omnia.activities.BodiesActivity;
import com.lucas.omnia.activities.DeputiesActivity;
import com.lucas.omnia.activities.EventsActivity;
import com.lucas.omnia.activities.FrontsActivity;
import com.lucas.omnia.activities.PartiesActivity;
import com.lucas.omnia.activities.PollsActivity;
import com.lucas.omnia.activities.PropositionsActivity;

import java.util.ArrayList;

public class Inspection {

    private String name;
    private Class activityClass;

    public Inspection() {}

    private Inspection(String name, Class activityClass) {
        this.name = name;
        this.activityClass = activityClass;
    }

    public String getName() {
        return name;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public static ArrayList<Inspection> createInspectionList(Resources resources) {
        ArrayList<Inspection> inspectionList = new ArrayList<>();

        inspectionList.add(new Inspection(resources.getString(R.string.inspection_item_1),BlocksActivity.class));
        inspectionList.add(new Inspection(resources.getString(R.string.inspection_item_9), BodiesActivity.class));
        inspectionList.add(new Inspection(resources.getString(R.string.inspection_item_2),DeputiesActivity.class));
        inspectionList.add(new Inspection(resources.getString(R.string.inspection_item_3),EventsActivity.class));
        inspectionList.add(new Inspection(resources.getString(R.string.inspection_item_4),FrontsActivity.class));
        inspectionList.add(new Inspection(resources.getString(R.string.inspection_item_6),PartiesActivity.class));
        inspectionList.add(new Inspection(resources.getString(R.string.inspection_item_7),PropositionsActivity.class));
        inspectionList.add(new Inspection(resources.getString(R.string.inspection_item_8), PollsActivity.class));

        return inspectionList;
    }
}
