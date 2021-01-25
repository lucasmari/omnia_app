package com.lucas.omnia.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lucas.omnia.R
import com.lucas.omnia.activities.MainActivity
import com.lucas.omnia.activities.NewImagePostActivity
import com.lucas.omnia.activities.NewPostActivity

class PostBottomSheetDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet, container,
                false)
        val imagePost = view.findViewById<TextView>(R.id.sheet_tv_add_image)
        imagePost.setOnClickListener { verifyStoragePermissions() }
        val textPost = view.findViewById<TextView>(R.id.sheet_tv_add_text)
        textPost.setOnClickListener {
            startActivity(Intent(view.context, NewPostActivity::class.java))
            dismiss()
        }
        return view
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
        if (bitmapCode == MainActivity.RESULT_OK) {
            val dataUri = data!!.data
            if (dataUri != null) {
                val intent = Intent(activity, NewImagePostActivity::class.java)
                intent.putExtra(NewImagePostActivity.EXTRA_DATA, dataUri.toString())
                startActivity(intent)
                dismiss()
            }
        }
    }

    companion object {
        const val RESULT_LOAD_IMG = 1

        // Permissions
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
        )

        fun newInstance(): PostBottomSheetDialogFragment {
            return PostBottomSheetDialogFragment()
        }
    }
}