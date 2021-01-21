package com.lucas.omnia.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.util.PatternsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.lucas.omnia.R
import com.lucas.omnia.databinding.ActivityAuthBinding
import com.lucas.omnia.models.User

class AuthActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityAuthBinding? = null

    // Firebase
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseRef: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseRef = databaseReference
        firebaseAuth = FirebaseAuth.getInstance()

        // Views
        setProgressBar(R.id.auth_pb)

        // Click listeners
        binding!!.authBtSignup.setOnClickListener(this)
        binding!!.authBtSignin.setOnClickListener(this)
        binding!!.authBtForgotPass.setOnClickListener(this)
        binding!!.authBtSigninGoogle.setOnClickListener(this)
    }

    public override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        // Check auth on Activity start
        if (firebaseAuth!!.currentUser != null) {
            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun signUp() {
        val email = binding!!.authTietEmail.text.toString()
        val password = binding!!.authTietPass.text.toString()
        Log.d(TAG, "signUp")
        if (validateEmail(email)) {
            binding!!.authTietEmail.error = getString(R.string.auth_invalid_email)
            return
        }
        if (validatePassword(password)) {
            binding!!.authTietPass.error = getString(R.string.auth_invalid_password)
            return
        }
        showProgressBar()
        firebaseAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_SUCCESS, "createUserWithEmail:success")
                        onSignUpSuccess(task.result!!.user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FAILURE, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@AuthActivity, getString(R.string.auth_toast_sign_up_failure),
                                Toast.LENGTH_LONG).show()
                    }
                    hideProgressBar()
                }
    }

    private fun signIn() {
        val email = binding!!.authTietEmail.text.toString()
        val password = binding!!.authTietPass.text.toString()
        Log.d(TAG, "signIn")
        if (validateEmail(email)) {
            binding!!.authTietEmail.error = getString(R.string.auth_invalid_email)
            return
        }
        if (validatePassword(password)) {
            binding!!.authTietPass.error = getString(R.string.auth_invalid_password)
            return
        }
        showProgressBar()
        firebaseAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_SUCCESS, "signInWithEmail:success")
                        onSignInSuccess()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FAILURE, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@AuthActivity, getString(R.string.auth_toast_sign_in_failure),
                                Toast.LENGTH_LONG).show()
                    }
                    hideProgressBar()
                }
    }

    private fun signInGoogle() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG_SUCCESS, "firebaseAuthWithGoogle:" + account!!.id)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG_FAILURE, "Google sign in failed", e)
                Toast.makeText(this@AuthActivity, getString(R.string.auth_toast_sign_up_failure),
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_SUCCESS, "signInWithCredential:success")
                        onSignInSuccess()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FAILURE, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@AuthActivity, getString(R.string.auth_toast_sign_up_failure),
                                Toast.LENGTH_LONG).show()
                    }
                    hideProgressBar()
                }
    }

    fun validateEmail(email: String): Boolean {
        return email.isEmpty() || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.isEmpty() || password.length < 6 || password.length > 15
    }

    private fun onSignUpSuccess(user: FirebaseUser?) {
        val username = usernameFromEmail(user!!.email)

        // Send email verification
        sendEmailVerification(user)

        // Write new user
        writeNewUser(user.uid, username, user.email)

        // Go to MainActivity
        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
        finish()
    }

    private fun onSignInSuccess() {
        // Go to MainActivity
        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
        finish()
    }

    fun usernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@").toTypedArray()[0]
        } else {
            email
        }
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user!!.sendEmailVerification()
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent")
                    }
                }
    }

    private fun writeNewUser(userId: String, name: String?, email: String?) {
        val user = User(name, email, 0)
        databaseReference!!.child("users").child(userId).setValue(user)
    }

    private fun forgotPass() {
        val email = binding!!.authTietEmail.text.toString()
        if (email.isEmpty()) binding!!.authTietEmail.error = getString(R.string.auth_forgot_pass) else resetPass(email)
    }

    private fun resetPass(email: String) {
        firebaseAuth!!.sendPasswordResetEmail(email)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                        Toast.makeText(this, getString(R.string.auth_toast_reset_pass_success), Toast.LENGTH_LONG).show()
                    } else {
                        Log.e(TAG, task.exception.toString())
                        Toast.makeText(this, getString(R.string.auth_toast_reset_pass_failure), Toast.LENGTH_LONG).show()
                    }
                }
    }

    override fun onClick(v: View) {
        when (val i = v.id) {
            R.id.auth_bt_signup -> signUp()
            R.id.auth_bt_signin -> signIn()
            R.id.auth_bt_forgot_pass -> forgotPass()
            R.id.auth_bt_signin_google -> signInGoogle()
            else -> throw IllegalStateException("Unexpected value: $i")
        }
    }

    companion object {
        // Tags
        private const val TAG = "AuthActivity"
        private const val TAG_SUCCESS = "success"
        private const val TAG_FAILURE = "failure"
        private const val RC_SIGN_IN = 1
    }
}