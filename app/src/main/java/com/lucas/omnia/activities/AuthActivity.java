package com.lucas.omnia.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lucas.omnia.R;

/**
 * Created by Lucas on 06/02/2018.
 */

public class AuthActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;

    // Sign In
    private EditText mEmailSignIn;
    private EditText mPassSignIn;

    // Sign Up
    private EditText mEmailSignUp;
    private EditText mPassSignUp;

    // Tags
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FAILURE = "failure";

    private static final int RC_SIGN_IN = 1;

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        mEmailSignIn = findViewById(R.id.emailEdit);
        mPassSignIn = findViewById(R.id.passEdit);

        Button mSignIn = findViewById(R.id.signIn);
        mSignIn.setOnClickListener(v -> {
            if (!validateSignIn()) {
                return;
            }
            signIn(v.getContext());
        });

        TextView mRegister = findViewById(R.id.register);
        mRegister.setOnClickListener(v -> showBottomSheetDialog());

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton mGoogleSignIn = findViewById(R.id.signInGoogle);
        mGoogleSignIn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void signIn(final Context context) {

        final String email = mEmailSignIn.getText().toString();
        final String password = mPassSignIn.getText().toString();

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage(getString(R.string.signing_in));
        pDialog.setCancelable(false);
        pDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_SUCCESS, "signInWithEmail:success");
                        startActivity(new Intent(AuthActivity.this,MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FAILURE, "signInWithEmail:failure", task.getException());
                        Toast.makeText(AuthActivity.this, getString(R.string.sign_in_failure),
                                Toast.LENGTH_LONG).show();
                    }
                    pDialog.dismiss();
                });
    }

    public boolean validateSignIn() {
        boolean valid = true;

        String email = mEmailSignIn.getText().toString();
        String password = mPassSignIn.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailSignUp.setError(getString(R.string.invalid_email));
            valid = false;
        }
        if (password.isEmpty()) {
            mPassSignIn.setError(getString(R.string.invalid_sign_in_password));
            valid = false;
        }
        return valid;
    }

    public void showBottomSheetDialog() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = this.getLayoutInflater().inflate(R.layout.fragment_sign_up_1,
                null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        mEmailSignUp = sheetView.findViewById(R.id.emailEdit);
        mPassSignUp = sheetView.findViewById(R.id.passEdit2);

        Button mSignUp = sheetView.findViewById(R.id.signUp2);
        mSignUp.setOnClickListener(v -> {
                if (!validateSignUp()) {
                    return;
                }
                signUp(v.getContext());
                mBottomSheetDialog.dismiss();
        });
    }

    public void signUp(final Context context) {

        String email = mEmailSignUp.getText().toString();
        final String password = mPassSignUp.getText().toString();

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage(getString(R.string.signing_up));
        pDialog.setCancelable(false);
        pDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_SUCCESS, "createUserWithEmail:success");
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FAILURE, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(AuthActivity.this, getString(R.string.sign_up_failure),
                                Toast.LENGTH_LONG).show();
                    }
                    pDialog.dismiss();
                });
    }

    public boolean validateSignUp() {
        boolean valid = true;

        String email = mEmailSignUp.getText().toString();
        String password = mPassSignUp.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailSignUp.setError(getString(R.string.invalid_email));
            valid = false;
        } else {
            mEmailSignUp.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            mPassSignUp.setError(getString(R.string.invalid_sign_up_password));
            valid = false;
        } else {
            mPassSignUp.setError(null);
        }

        return valid;
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
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_SUCCESS, "signInWithCredential:success");
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FAILURE, "signInWithCredential:failure", task.getException());
                        Toast.makeText(AuthActivity.this, getString(R.string.sign_up_failure),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
