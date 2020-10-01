package com.lucas.omnia.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.lucas.omnia.R;
import com.lucas.omnia.activities.ProfileSettingsActivity;

import static android.app.Activity.RESULT_OK;
import static com.lucas.omnia.activities.MainActivity.userName;

/**
 * Created by Lucas on 29/10/2017.
 */

public class ProfileNavFragment extends Fragment {

    private static int RESULT_LOAD_IMG = 1;
    private ImageButton profileImageButton;

    public static ProfileNavFragment newInstance() {
        ProfileNavFragment fragment = new ProfileNavFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView userTv = view.findViewById(R.id.profile_tv_name);
        userTv.setText(userName);
        profileImageButton = view.findViewById(R.id.profile_ib);
        profileImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
        });

        ImageButton userSettingsButton = view.findViewById(R.id.profile_ib_settings);
        userSettingsButton.setOnClickListener(v -> startActivity(new Intent(v.getContext(), ProfileSettingsActivity.class)));

        EditText descriptionEt = view.findViewById(R.id.profile_et_description);
        TextView descriptionTv = view.findViewById(R.id.profile_tv_description);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {

                Uri uri = data.getData();
                String filePath = "";
                String fileId = DocumentsContract.getDocumentId(uri);
                // Split at colon, use second item in the array
                String id = fileId.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String selector = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, selector, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                profileImageButton.setImageBitmap(bitmap);

            } else {
                Toast.makeText(getContext(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
