package com.lucas.omnia.authenticator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.lucas.omnia.activities.MainActivity;
import com.lucas.omnia.R;
import com.lucas.omnia.data.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lucas.omnia.utils.AppConfig.SIGN_UP_URL;
import static com.lucas.omnia.utils.AppController.getInstance;

/**
 * Created by Lucas on 06/02/2018.
 */

public class SignUpActivity1 extends AppCompatActivity {

    private EditText mUserEdit;
    private EditText mPassEdit;
    private EditText mEmailEdit;
    private Spinner mStateSpinner;
    List<String> spinnerArray =  new ArrayList<>();
    private String states[] = {"AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG",
            "PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO"};
    private Button mSignUp;
    private SessionManager session;
    private SQLiteHandler db;

    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_1);

        mUserEdit = findViewById(R.id.userEdit2);
        mPassEdit = findViewById(R.id.passEdit2);
        mEmailEdit = findViewById(R.id.emailEdit);

        Collections.addAll(spinnerArray,states);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStateSpinner = findViewById(R.id.stateSpinner);
        mStateSpinner.setAdapter(adapter);

        mSignUp = findViewById(R.id.signUp2);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    return;
                }
                signUp(v.getContext());
            }
        });

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
    }

    public void signUp(final Context context) {

        String url = SIGN_UP_URL;
        final String user = mUserEdit.getText().toString();
        final String password = mPassEdit.getText().toString();
        String email = mEmailEdit.getText().toString();
        String location = mStateSpinner.getSelectedItem().toString();

        JSONObject obj = new JSONObject();
        try {
            obj.put("user", user);
            obj.put("password", password);
            obj.put("email", email);
            obj.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Cadastrando...");
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
                                onSignUpSuccess();

                                // Create login session
                                session.setLogin(true);
                                // Inserting row in users table
                                db.addUser(user, password);
                            } else {
                                onSignUpFailed();
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

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void onSignUpSuccess() {
        Toast.makeText(getBaseContext(), "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();
        startActivity(new Intent(SignUpActivity1.this, MainActivity.class));
        finish();
    }

    public void onSignUpFailed() {
        Toast.makeText(getBaseContext(), "Usuário já existente...", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String user = mUserEdit.getText().toString();
        String password = mPassEdit.getText().toString();
        String email = mEmailEdit.getText().toString();

        if (user.isEmpty() || user.length() < 4 || user.length() > 10) {
            mUserEdit.setError("Usuário inválido! (entre 4 e 10 caracteres)");
            valid = false;
        } else {
            mUserEdit.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            mPassEdit.setError("Senha inválida! (entre 6 e 15 caracteres)");
            valid = false;
        } else {
            mPassEdit.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailEdit.setError("Email inválido!");
            valid = false;
        } else {
            mEmailEdit.setError(null);
        }
        return valid;
    }
}
