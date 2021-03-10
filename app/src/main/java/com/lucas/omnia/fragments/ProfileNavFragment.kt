package com.lucas.omnia.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.lucas.omnia.R
import com.lucas.omnia.activities.MainActivity
import com.lucas.omnia.activities.ProfileSettingsActivity
import com.lucas.omnia.activities.SubscriptionsActivity
import com.lucas.omnia.models.User
import java.io.IOException
import java.net.MalformedURLException
import java.util.*

class ProfileNavFragment : Fragment() {
    private var profileImgUrl: String? = null
    private var profileImgView: ImageView? = null
    private var usernameTv: TextView? = null
    private var subCountTv: TextView? = null
    private var aboutEt: EditText? = null
    private var storageRef: StorageReference? = null
    private var sharedPreferences: SharedPreferences? = null
    private var databaseReference: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usernameTv = view.findViewById(R.id.profile_tv_name)
        subCountTv = view.findViewById(R.id.profile_tv_sub_count)
        aboutEt = view.findViewById(R.id.profile_et_about_body)
        val saveBt = view.findViewById<Button>(R.id.profile_bt_save)
        saveBt.setOnClickListener { addAbout() }
        val subsBt = view.findViewById<Button>(R.id.profile_bt_subs)
        subsBt.setOnClickListener { v: View ->
            startActivity(Intent(v.context,
                    SubscriptionsActivity::class.java))
        }
        profileImgView = view.findViewById(R.id.profile_iv)
        profileImgView?.setOnClickListener {
            verifyStoragePermissions()
        }
        val editIb = view.findViewById<ImageButton>(R.id.profile_ib_edit)
        editIb.setOnClickListener { v: View? -> editUsername() }
        val settingsIb = view.findViewById<ImageButton>(R.id.profile_ib)
        settingsIb.setOnClickListener { v: View ->
            startActivity(Intent(v.context,
                    ProfileSettingsActivity::class.java))
        }
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        databaseReference = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        usersReference = databaseReference!!.child("users")
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        if (!sharedPreferences?.contains("User")!!) fetchUser() else setUser(userLocal)
    }

    fun fetchUser() {
        databaseReference!!.child("users").child(userId!!).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val u = dataSnapshot.getValue(User::class.java)
                        if (u == null) {
                            Log.e(TAG, "User $userId is unexpectedly null")
                            Toast.makeText(context,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            setUser(u)
                            saveUser(u)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException())
                    }
                })
    }

    private fun addAbout() {
        aboutEt!!.clearFocus()

        // Save about in database
        usersReference!!.child(userId!!).child("about").setValue(aboutEt!!.text.toString())

        // Save about in SharedPreferences
        val u = userLocal
        u.about = aboutEt!!.text.toString()
        saveUser(u)
        Toast.makeText(context, getString(R.string.profile_toast_about), Toast.LENGTH_SHORT).show()
    }

    private fun verifyStoragePermissions() {
        val readPermission = ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        } else {
            openChooser()
        }
    }

    private fun editUsername() {
        val editText = EditText(context)
        AlertDialog.Builder(context)
                .setTitle(getString(R.string.profile_ad_username))
                .setView(editText)
                .setPositiveButton(getString(R.string.alert_dialog_bt_save)
                ) { _: DialogInterface?, _: Int ->
                    val username = editText.text.toString()
                    updateUser(username)
                }
                .setNegativeButton(getString(R.string.alert_dialog_bt_cancel), null)
                .show()
    }

    private fun updateUser(username: String) {
        usersReference!!.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(context,
                            getString(R.string.profile_toast_username_exists),
                            Toast.LENGTH_SHORT).show()
                } else if (username.length < 16) {
                    Toast.makeText(context, getString(R.string.profile_toast_saving), Toast.LENGTH_SHORT).show()
                    usernameTv!!.text = username

                    // Save username in database
                    usersReference!!.child(userId!!).child("username").setValue(username)

                    // Save username in SharedPreferences
                    val u = userLocal
                    u.username = username
                    saveUser(u)
                    updatePosts(username)
                } else {
                    Toast.makeText(context,
                            getString(R.string.profile_toast_validation), Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "updateUser:onCancelled", databaseError.toException())
            }
        })
    }

    private fun updatePosts(username: String) {
        val postsReference = databaseReference!!.child("posts")
        postsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (d in dataSnapshot.children) {
                        if (d.child("uid").value.toString() == userId) {
                            val result = HashMap<String, Any>()
                            result["author"] = username
                            postsReference.child(d.key.toString()).updateChildren(result)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "updatePosts:onCancelled", databaseError.toException())
            }
        })
        val userPostsReference = databaseReference!!.child("user-posts").child(userId!!)
        userPostsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (d in dataSnapshot.children) {
                        val result = HashMap<String, Any>()
                        result["author"] = username
                        userPostsReference.child(d.key.toString()).updateChildren(result)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "updatePosts:onCancelled", databaseError.toException())
            }
        })
    }

    private fun openChooser() {
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = "image/*"
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        startActivityForResult(chooserIntent, RESULT_LOAD_IMG)
    }

    override fun onActivityResult(requestCode: Int, bitmapCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, bitmapCode, data)
        try {
            if (bitmapCode == MainActivity.RESULT_OK) {
                val dataUri = data!!.data
                if (dataUri != null) {
                    val profileImgRef = storageRef!!.child(userId + STORAGE_PATH)
                    profileImgRef.putFile(dataUri)
                    setProfileImage(dataUri.toString())

                    // Set hasPhoto in database
                    usersReference!!.child(userId!!).child("hasPhoto").setValue(true)

                    // Save hasPhoto in SharedPreferences
                    val u = userLocal
                    u.hasPhoto = true
                    saveUser(u)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Exception: " + e.message)
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
        }
    }

    private fun setProfileImage(dataString: String?) {
        profileImgView?.load(dataString) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    private fun setUser(u: User) {
        usernameTv!!.text = u.username
        subCountTv!!.text = u.subCount.toString()
        if (u.hasPhoto) fetchProfileImage()
        if (u.about != null) aboutEt!!.setText(u.about)
    }

    private fun fetchProfileImage() {
        val profileImgRef = storageRef!!.child(userId + STORAGE_PATH)
        profileImgRef.downloadUrl.addOnSuccessListener { uri: Uri ->
            try {
                profileImgUrl = uri.toString()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            setProfileImage(profileImgUrl)
        }.addOnFailureListener { Toast.makeText(context, getString(R.string.profile_toast_fetch_error), Toast.LENGTH_SHORT).show() }
    }

    fun saveUser(u: User?) {
        val prefsEditor = sharedPreferences!!.edit()
        val gson = Gson()
        val json = gson.toJson(u)
        prefsEditor.putString("User", json)
        prefsEditor.apply()
    }

    val userLocal: User
        get() {
            val gson = Gson()
            val json = sharedPreferences!!.getString("User", "")
            return gson.fromJson(json, User::class.java)
        }

    companion object {
        private var userId: String? = null
        private var usersReference: DatabaseReference? = null
        private const val TAG = "ProfileNavFragment"
        private const val STORAGE_PATH = "/profile-picture/profile.jpg"
        private const val RESULT_LOAD_IMG = 1

        // Permissions
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}