package com.lucas.omnia.models;

/**
 * Created by Lucas on 24/10/2017.
 */
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public String title;
    public String body;
    public boolean edited;
    public int upVoteCount = 0;
    public int downVoteCount = 0;
    public int commentsCount = 0;
    public Map<String, Boolean> upVotes = new HashMap<>();
    public Map<String, Boolean> downVotes = new HashMap<>();
    public Map<String, Boolean> comments = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body, boolean edited) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.edited = edited;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("edited", edited);
        result.put("upVoteCount", upVoteCount);
        result.put("downVoteCount", downVoteCount);
        result.put("upVotes", upVotes);
        result.put("downVotes", downVotes);
        result.put("comments", comments);

        return result;
    }
}
