package com.lucas.omnia.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lucas.omnia.R;
import com.lucas.omnia.activities.UserSettingsActivity;

import static android.app.Activity.RESULT_OK;
import static com.lucas.omnia.activities.MainActivity.hideSoftKeyboard;
import static com.lucas.omnia.activities.MainActivity.showSoftKeyboard;
import static com.lucas.omnia.activities.MainActivity.userName;

/**
 * Created by Lucas on 29/10/2017.
 */

public class NavFragment2 extends Fragment {

    private static int RESULT_LOAD_IMG = 1;
    private TextView mUserTv;
    private ImageButton mProfileImage;
    private ImageButton mUserSettings;
    private EditText mDescriptionEdit;
    private TextView mDescriptionText;
    private ImageButton mDescriptionButton;
    private Animation mSlideIn;
    private Animation mSlideOut;
    private Animation mHide;
    static boolean visible;

    public static NavFragment2 newInstance() {
        NavFragment2 fragment = new NavFragment2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_2, container, false);

        mUserTv = view.findViewById(R.id.userName);
        mUserTv.setText(userName);
        mProfileImage = view.findViewById(R.id.profileImage);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
            }
        });

        mUserSettings = view.findViewById(R.id.userSettings);
        mUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), UserSettingsActivity.class));
            }
        });

        mDescriptionEdit = view.findViewById(R.id.descriptionEdit);
        mDescriptionText = view.findViewById(R.id.descriptionText);
        mDescriptionButton = view.findViewById(R.id.descriptionButton);

        mSlideIn = AnimationUtils.loadAnimation(getContext(), R.anim.et_slide_in);
        mSlideOut = AnimationUtils.loadAnimation(getContext(), R.anim.et_slide_out);
        mHide = AnimationUtils.loadAnimation(getContext(), R.anim.rl_hide);

        mDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible = !visible;
                if(visible) {
                    mDescriptionEdit.startAnimation(mSlideIn);
                    mDescriptionEdit.setVisibility(View.VISIBLE);
                    showSoftKeyboard(getContext(),mDescriptionEdit);
                }
                else {
                    if(mDescriptionEdit.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Escreva algo!", Toast.LENGTH_SHORT).show();
                    } else {
                        mDescriptionText.setText(mDescriptionEdit.getText().toString());
                    }
                    hideSoftKeyboard(getContext(),mDescriptionEdit);
                    mDescriptionEdit.startAnimation(mSlideOut);
                    mDescriptionEdit.startAnimation(mHide);
                    mDescriptionEdit.getText().clear();
                    mDescriptionEdit.setVisibility(View.GONE);
                }
            }
        });

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
                mProfileImage.setImageBitmap(bitmap);

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
