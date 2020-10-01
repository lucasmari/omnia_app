package com.lucas.omnia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.lucas.omnia.R;
import com.lucas.omnia.databinding.ActivityAuthBinding;
import com.lucas.omnia.models.User;

/**
 * Created by Lucas on 06/02/2018.
 */

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAuthBinding binding;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    // Tags
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FAILURE = "failure";
    private static final String TAG = "AuthActivity";
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = getDatabaseReference();
        firebaseAuth = FirebaseAuth.getInstance();

        // Views
        setProgressBar(R.id.auth_pb);

        // Click listeners
        binding.authBtSignup.setOnClickListener(this);
        binding.authBtSignin.setOnClickListener(this);
        binding.authBtSigninGoogle.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        // Check auth on Activity start
        if (firebaseAuth.getCurrentUser() != null) {
            onAuthSuccess(firebaseAuth.getCurrentUser());
        }
    }

    private void signUp() {
        String email = binding.authTietEmail.getText().toString();
        String password = binding.authTietPass.getText().toString();

        Log.d(TAG, "signUp");
        if (!validateForm(email, password)) {
            return;
        }

        showProgressBar();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_SUCCESS, "createUserWithEmail:success");
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FAILURE, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(AuthActivity.this, getString(R.string.sign_up_failure),
                                Toast.LENGTH_LONG).show();
                    }
                    hideProgressBar();
                });
    }

    private void signIn() {
        String email = binding.authTietEmail.getText().toString();
        String password = binding.authTietPass.getText().toString();

        Log.d(TAG, "signIn");
        if (!validateForm(email, password)) {
            return;
        }

        showProgressBar();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_SUCCESS, "signInWithEmail:success");
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FAILURE, "signInWithEmail:failure", task.getException());
                        Toast.makeText(AuthActivity.this, getString(R.string.sign_in_failure),
                                Toast.LENGTH_LONG).show();
                    }
                    hideProgressBar();
                });
    }

    private void signInGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG_SUCCESS, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG_FAILURE, "Google sign in failed", e);
                Toast.makeText(AuthActivity.this, getString(R.string.sign_up_failure),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showProgressBar();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_SUCCESS, "signInWithCredential:success");
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FAILURE, "signInWithCredential:failure", task.getException());
                        Toast.makeText(AuthActivity.this, getString(R.string.sign_up_failure),
                                Toast.LENGTH_LONG).show();
                    }
                    hideProgressBar();
                });
    }

    public boolean validateForm(String email, String password) {
        boolean result = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.authTietEmail.setError(getString(R.string.invalid_email));
            result = false;
        }
        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            binding.authTietPass.setError(getString(R.string.invalid_password));
            result = false;
        }
        return result;
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(AuthActivity.this,MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        databaseReference.child("users").child(userId).setValue(user);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.auth_bt_signup:
                signUp();
                break;
            case R.id.auth_bt_signin:
                signIn();
                break;
            case R.id.auth_bt_signin_google:
                signInGoogle();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }
    }
}
