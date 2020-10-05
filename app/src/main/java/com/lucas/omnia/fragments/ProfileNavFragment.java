package com.lucas.omnia.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lucas.omnia.R;
import com.lucas.omnia.activities.MainActivity;
import com.lucas.omnia.activities.NewPostActivity;
import com.lucas.omnia.activities.ProfileSettingsActivity;
import com.lucas.omnia.models.User;
import com.lucas.omnia.utils.ImageLoadAsyncTask;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Lucas on 29/10/2017.
 */

public class ProfileNavFragment extends Fragment {

    private User user;
    private String userId;
    private URL profileImgUrl;
    private ImageView profileImgView;
    private TextView descriptionTv;

    private StorageReference storageRef;
    private DatabaseReference databaseReference;

    public static final String TAG = "ProfileNavFragment";

    // Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        fetchUser();

        TextView userTv = view.findViewById(R.id.profile_tv_name);
        userTv.setText(user.username);

        profileImgView = view.findViewById(R.id.profile_iv);
        profileImgView.setOnClickListener(v -> {
            verifyStoragePermissions();
        });

        ImageButton userSettingsButton = view.findViewById(R.id.profile_ib_settings);
        userSettingsButton.setOnClickListener(v -> startActivity(new Intent(v.getContext(), ProfileSettingsActivity.class)));

        descriptionTv = view.findViewById(R.id.profile_tv_description);
    }

    public void fetchUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String username = usernameFromEmail(firebaseUser.getEmail());

            user = new User(username, firebaseUser.getEmail());

            // Check if user's email is verified
            boolean emailVerified = firebaseUser.isEmailVerified();
        }

        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);

                        if (u == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getContext(),
                                    getString(R.string.user_fetch_error),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (u.photoUrl != null) fetchProfileImage();
                            if (u.description != null) descriptionTv.setText(u.description);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
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

    private void fetchProfileImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        StorageReference profileImgRef = storageRef.child(userId + "/profile-picture/profile.jpg");
        profileImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                profileImgUrl = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask(profileImgUrl,
                    profileImgView);
            imageLoadAsyncTask.execute();
        }).addOnFailureListener(exception -> {
            Toast.makeText(getContext(), "Error: could not fetch image", Toast.LENGTH_SHORT).show();
        });
    }

    private void openChooser() {
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        int RESULT_LOAD_IMG = 1;
        startActivityForResult(chooserIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == MainActivity.RESULT_OK) {
                // If it returns multiple item from storage
                if (data.getClipData() == null && data.getDataString() != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        StorageReference profileImgRef = storageRef.child(userId + "/profile" +
                                "-picture/profile.jpg");
                        profileImgRef.putFile(Uri.parse(dataString));

                        setProfileImage(dataString);
                        databaseReference.child("users").child(userId).child("photoUrl").setValue(profileImgRef);
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void setProfileImage(String dataString) {
        Bitmap result = BitmapFactory.decodeFile(dataString);
        Bitmap circleBitmap = Bitmap.createBitmap(result.getWidth(), result.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(result,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(result.getWidth()/2, result.getHeight()/2, result.getWidth()/2, paint);

        profileImgView.setImageBitmap(circleBitmap);
    }
}
