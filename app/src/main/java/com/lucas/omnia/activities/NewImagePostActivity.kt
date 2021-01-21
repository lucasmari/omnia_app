package com.lucas.omnia.activities

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lucas.omnia.R
import com.lucas.omnia.databinding.ActivityNewImagePostBinding
import com.lucas.omnia.models.Post
import com.lucas.omnia.models.User
import java.io.IOException
import java.util.*

class NewImagePostActivity : BaseActivity() {
    private var dataUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var databaseRef: DatabaseReference? = null
    private var binding: ActivityNewImagePostBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewImagePostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseRef = databaseReference
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Get post key from intent
        dataUri = Uri.parse(intent.getStringExtra(EXTRA_DATA))
        requireNotNull(dataUri) { "Must pass EXTRA_DATA" }
        setImage()
        binding!!.newImagePostFabSubmit.setOnClickListener { submitPost() }
    }

    private fun setImage() {
        var bitmap: Bitmap?
        try {
            dataUri?.let {
                if(Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            dataUri
                    )
                    binding!!.newImagePostIvBody.setImageBitmap(bitmap)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, dataUri!!)
                    bitmap = ImageDecoder.decodeBitmap(source)
                    binding!!.newImagePostIvBody.setImageBitmap(bitmap)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun submitPost() {
        val title = binding!!.newImagePostEtTitle.text.toString()

        // Title is required
        if (TextUtils.isEmpty(title)) {
            binding!!.newImagePostEtTitle.error = REQUIRED
            return
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false)
        Toast.makeText(this, getString(R.string.new_post_toast_posting), Toast.LENGTH_SHORT).show()
        val userId = uid
        databaseRef!!.child("users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user == null) {
                            Log.e(TAG, "User $userId is unexpectedly null")
                            Toast.makeText(this@NewImagePostActivity,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            writeNewPost(userId, user.username, title)
                        }
                        setEditingEnabled(true)
                        finish()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException())
                        setEditingEnabled(true)
                    }
                })
    }

    private fun setEditingEnabled(enabled: Boolean) {
        binding!!.newImagePostEtTitle.isEnabled = enabled
        if (enabled) {
            binding!!.newImagePostFabSubmit.show()
        } else {
            binding!!.newImagePostFabSubmit.hide()
        }
    }

    private fun writeNewPost(userId: String, username: String?, title: String) {
        // Create new item_post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = databaseRef!!.child("posts").push().key
        val post = Post(userId, username, title, "")
        val postValues = post.toMap()
        val childUpdates: MutableMap<String, Any> = HashMap()
        childUpdates["/posts/$key"] = postValues
        childUpdates["/user-posts/$userId/$key"] = postValues
        databaseRef!!.updateChildren(childUpdates)

        // Set timestamp
        databaseRef!!.child("posts").child(key!!).child("timestamp").setValue(ServerValue.TIMESTAMP)
        databaseRef!!.child("user-posts").child(userId).child(key).child("timestamp").setValue(ServerValue.TIMESTAMP)

        // Save image/video in storage
        val profileImgRef = storageRef!!.child("$userId/posts/$key")
        profileImgRef.putFile(dataUri!!)

        // Set hasImage in database
        databaseRef!!.child("posts").child(key).child("hasImage").setValue(true)
        databaseRef!!.child("user-posts").child(userId).child(key).child("hasImage").setValue(true)
    }

    companion object {
        private const val TAG = "NewImagePostActivity"
        private const val REQUIRED = "Required"
        const val EXTRA_DATA = "data"
    }
}