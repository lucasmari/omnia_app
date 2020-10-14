package com.lucas.omnia.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lucas.omnia.R;
import com.lucas.omnia.activities.MainActivity;
import com.lucas.omnia.activities.NewImagePostActivity;
import com.lucas.omnia.activities.NewPostActivity;

import java.net.URL;

public class PostBottomSheetDialogFragment extends BottomSheetDialogFragment {

    // Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static PostBottomSheetDialogFragment newInstance() {
        return new PostBottomSheetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container,
                false);

        TextView imagePost = view.findViewById(R.id.sheet_tv_add_image);
        imagePost.setOnClickListener(v -> verifyStoragePermissions());

        TextView textPost = view.findViewById(R.id.sheet_tv_add_text);
        textPost.setOnClickListener(v -> startActivity(new Intent(view.getContext(), NewPostActivity.class)));

        return view;
    }

    public void verifyStoragePermissions() {
        int readPermission = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            openChooser();
        }
    }

    private void openChooser() {
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        final int RESULT_LOAD_IMG = 1;
        startActivityForResult(chooserIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int bitmapCode, Intent data) {
        super.onActivityResult(requestCode, bitmapCode, data);
        if (bitmapCode == MainActivity.RESULT_OK) {
            Uri dataUri = data.getData();
            if (dataUri != null) {
                Intent intent = new Intent(getActivity(), NewImagePostActivity.class);
                intent.putExtra(NewImagePostActivity.EXTRA_DATA, dataUri.toString());
                startActivity(intent);
            }
        }
    }
}
