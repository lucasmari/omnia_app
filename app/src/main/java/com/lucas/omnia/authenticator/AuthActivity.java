package com.lucas.omnia.authenticator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lucas.omnia.activities.MainActivity;
import com.lucas.omnia.R;
import com.lucas.omnia.data.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import static com.lucas.omnia.utils.AppConfig.LOG_IN_URL;
import static com.lucas.omnia.utils.AppController.getInstance;

/**
 * Created by Lucas on 06/02/2018.
 */

public class AuthActivity extends AppCompatActivity {

    private Button mLogIn;
    private TextView mSignUp;
    private EditText mUserEdit;
    private EditText mPassEdit;
    private SessionManager session;
    private SQLiteHandler db;
    private FirebaseAuth mAuth;

    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        mUserEdit = findViewById(R.id.userEdit);
        mPassEdit = findViewById(R.id.passEdit);
        
        mLogIn = findViewById(R.id.logIn);
        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    return;
                }
                logIn(v.getContext());
            }
        });

        mSignUp = findViewById(R.id.signUp);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(AuthActivity.this, SignUpActivity1.class));
            }
        });

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        /*if (session.isLoggedIn()) {
            // User is already logged in. Take him to activity_main activity
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/
        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }

    public void logIn(final Context context) {

        String url = LOG_IN_URL;
        final String user = mUserEdit.getText().toString();
        final String password = mPassEdit.getText().toString();

        JSONObject obj = new JSONObject();
        try {
            obj.put("user", user);
            obj.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Autenticando...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Checking for SUCCESS TAG
                    int success = response.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        onLogInSuccess();

                        // Create login session
                        session.setLogin(true);
                        // Inserting row in users table
                        db.addUser(user, password);
                    } else {
                        onLogInFailed();
                    }
                    Log.d("Response", response.toString());
                    pDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Error", response.toString());
                pDialog.hide();
                Toast.makeText(context, "Erro do servidor", Toast.LENGTH_SHORT).show();
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    public boolean validate() {
        boolean valid = true;

        String user = mUserEdit.getText().toString();
        String password = mPassEdit.getText().toString();

        if (user.isEmpty()) {
            mUserEdit.setError("Preencha o campo!");
            valid = false;
        }
        if (password.isEmpty()) {
            mPassEdit.setError("Preencha o campo!");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLogInSuccess() {
        startActivity(new Intent(AuthActivity.this,MainActivity.class));
        finish();
    }

    public void onLogInFailed() {
        Toast.makeText(getBaseContext(), "Usuário ou senha incorretos!", Toast.LENGTH_LONG).show();
    }
}
