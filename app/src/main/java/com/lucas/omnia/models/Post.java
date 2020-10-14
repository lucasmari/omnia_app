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
    public long timestamp;
    public String title;
    public String body;
    public boolean edited;
    public boolean hasImage;
    public int upVoteCount = 0;
    public int downVoteCount = 0;
    public int commentCount = 0;
    public Map<String, Boolean> upVotes = new HashMap<>();
    public Map<String, Boolean> downVotes = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("timestamp", timestamp);
        result.put("title", title);
        result.put("body", body);
        result.put("upVoteCount", upVoteCount);
        result.put("downVoteCount", downVoteCount);
        result.put("commentCount", commentCount);

        return result;
    }
}
