package com.lucas.omnia.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.lucas.omnia.R;
import com.lucas.omnia.activities.MainActivity;
import com.lucas.omnia.activities.ProfileSettingsActivity;
import com.lucas.omnia.activities.SubscriptionsActivity;
import com.lucas.omnia.models.User;
import com.lucas.omnia.utils.ImageLoadAsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class ProfileNavFragment extends Fragment {

    private static String userId;
    private URL profileImgUrl;
    private ImageView profileImgView;
    private TextView usernameTv;
    private TextView subCountTv;
    private EditText aboutEt;

    private StorageReference storageRef;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private static DatabaseReference usersReference;

    private static final String TAG = "ProfileNavFragment";
    private static final String STORAGE_PATH = "/profile-picture/profile.jpg";

    // Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
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

        usernameTv = view.findViewById(R.id.profile_tv_name);
        subCountTv = view.findViewById(R.id.profile_tv_sub_count);
        aboutEt = view.findViewById(R.id.profile_et_about_body);

        Button saveBt = view.findViewById(R.id.profile_bt_save);
        saveBt.setOnClickListener(v -> addAbout());

        Button subsBt = view.findViewById(R.id.profile_bt_subs);
        subsBt.setOnClickListener(v -> startActivity(new Intent(v.getContext(),
                SubscriptionsActivity.class)));

        profileImgView = view.findViewById(R.id.profile_iv);
        profileImgView.setOnClickListener(v -> verifyStoragePermissions());

        ImageButton editIb = view.findViewById(R.id.profile_ib_edit);
        editIb.setOnClickListener(v -> editUsername());

        ImageButton settingsIb = view.findViewById(R.id.profile_ib);
        settingsIb.setOnClickListener(v -> startActivity(new Intent(v.getContext(),
                ProfileSettingsActivity.class)));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersReference = databaseReference.child("users");

        sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        if (!sharedPreferences.contains("User")) fetchUser();
        else setUser(getUserLocal());
    }

    public void fetchUser() {
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);

                        if (u == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getContext(),
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            setUser(u);
                            saveUser(u);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void addAbout() {
        aboutEt.clearFocus();

        // Save about in database
        usersReference.child(userId).child("about").setValue(aboutEt.getText().toString());

        // Save about in SharedPreferences
        User u = getUserLocal();
        u.setAbout(aboutEt.getText().toString());
        saveUser(u);

        Toast.makeText(getContext(), getString(R.string.profile_toast_about), Toast.LENGTH_SHORT).show();

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

    private void editUsername() {
        final EditText editText = new EditText(getContext());

        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.profile_ad_username))
                .setView(editText)
                .setPositiveButton(getString(R.string.alert_dialog_bt_save),
                        (dialog1, which1) -> {
                            String username = editText.getText().toString();
                            updateUser(username);
                        })
                .setNegativeButton(getString(R.string.alert_dialog_bt_cancel), null)
                .show();
    }

    private void updateUser(String username) {
        usersReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Toast.makeText(getContext(),
                            getString(R.string.profile_toast_username_exists),
                            Toast.LENGTH_SHORT).show();
                } else if (username.length() < 16) {
                    Toast.makeText(getContext(), getString(R.string.profile_toast_saving), Toast.LENGTH_SHORT).show();
                    usernameTv.setText(username);

                    // Save username in database
                    usersReference.child(userId).child("username").setValue(username);

                    // Save username in SharedPreferences
                    User u = getUserLocal();
                    u.setUsername(username);
                    saveUser(u);

                    updatePosts(username);
                } else {
                    Toast.makeText(getContext(),
                            getString(R.string.profile_toast_validation), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "updateUser:onCancelled", databaseError.toException());
            }
        });
    }

    private void updatePosts(String username) {
        DatabaseReference postsReference = databaseReference.child("posts");
        postsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        if (d.child("uid").getValue().toString().equals(userId)) {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("author", username);
                            postsReference.child(String.valueOf(d.getKey())).updateChildren(result);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "updatePosts:onCancelled", databaseError.toException());
            }
        });

        DatabaseReference userPostsReference = databaseReference.child("user-posts").child(userId);
        userPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("author", username);
                        userPostsReference.child(String.valueOf(d.getKey())).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "updatePosts:onCancelled", databaseError.toException());
            }
        });
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
        try {
            if (bitmapCode == MainActivity.RESULT_OK) {
                Uri dataUri = data.getData();
                if (dataUri != null) {
                    StorageReference profileImgRef = storageRef.child(userId + STORAGE_PATH);
                    profileImgRef.putFile(dataUri);
                    setProfileImage(dataUri);

                    // Set hasPhoto in database
                    usersReference.child(userId).child("hasPhoto").setValue(true);

                    // Save hasPhoto in SharedPreferences
                    User u = getUserLocal();
                    u.setHasPhoto(true);
                    saveUser(u);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void setProfileImage(Uri dataUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), dataUri);
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

        profileImgView.setImageBitmap(circleBitmap);
    }

    private void setUser(User u) {
        usernameTv.setText(u.getUsername());
        subCountTv.setText(String.valueOf(u.subCount));
        if (u.getHasPhoto()) fetchProfileImage();
        if (u.getAbout() != null) aboutEt.setText(u.getAbout());
    }

    private void fetchProfileImage() {
        StorageReference profileImgRef = storageRef.child(userId + STORAGE_PATH);
        profileImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                profileImgUrl = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask(profileImgUrl,
                    profileImgView, true);
            imageLoadAsyncTask.execute();
        }).addOnFailureListener(exception -> Toast.makeText(getContext(), getString(R.string.profile_toast_fetch_error), Toast.LENGTH_SHORT).show());
    }

    public void saveUser(User u) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(u);
        prefsEditor.putString("User", json);
        prefsEditor.apply();
    }

    public User getUserLocal() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");

        return gson.fromJson(json, User.class);
    }
}
