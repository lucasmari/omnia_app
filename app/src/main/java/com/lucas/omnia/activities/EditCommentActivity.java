package com.lucas.omnia.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.R;
import com.lucas.omnia.databinding.ActivityEditCommentBinding;
import com.lucas.omnia.models.Comment;
import com.lucas.omnia.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.lucas.omnia.activities.CommentsActivity.postKey;

public class EditCommentActivity extends BaseActivity {

    private static final String TAG = "EditCommentActivity";
    private static final String REQUIRED = "Required";
    public static final String EXTRA_COMMENT_KEY = "comment_key";

    private ActivityEditCommentBinding binding;
    private String commentKey;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = getDatabaseReference();

        // Get item_Comment key from intent
        commentKey = getIntent().getStringExtra(EXTRA_COMMENT_KEY);
        if (commentKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_COMMENT_KEY");
        }

        // Initialize Database
        DatabaseReference commentReference = databaseReference.child("post-comments")
                .child(postKey).child(commentKey);

        commentReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Comment comment = dataSnapshot.getValue(Comment.class);

                        binding.editCommentEtBody.setText(comment.body);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "loadComment:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });

        binding.editCommentFabSubmit.setOnClickListener(v -> submitComment());
    }

    private void submitComment() {
        final String body = binding.editCommentEtBody.getText().toString();

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding.editCommentEtBody.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-Comments
        setEditingEnabled(false);
        Toast.makeText(this, getString(R.string.new_comment_toast_commenting), Toast.LENGTH_SHORT).show();

        databaseReference.child("post-comments").child(postKey).child(commentKey).child("body").setValue(body);
        databaseReference.child("post-comments").child(postKey).child(commentKey).child("edited").setValue(true);

        setEditingEnabled(true);
        finish();
    }

    private void setEditingEnabled(boolean enabled) {
        binding.editCommentEtBody.setEnabled(enabled);
        if (enabled) {
            binding.editCommentFabSubmit.show();
        } else {
            binding.editCommentFabSubmit.hide();
        }
    }
}
