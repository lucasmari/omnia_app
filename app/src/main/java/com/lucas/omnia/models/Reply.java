package com.lucas.omnia.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Reply {
    public String uid;
    public String author;
    public String body;
    public boolean edited;
    public int upVoteCount = 0;
    public int downVoteCount = 0;
    public Map<String, Boolean> upVotes = new HashMap<>();
    public Map<String, Boolean> downVotes = new HashMap<>();

    public Reply() {
        // Default constructor required for calls to DataSnapshot.getValue(Reply.class)
    }

    public Reply(String uid, String author, String body, boolean edited) {
        this.uid = uid;
        this.author = author;
        this.body = body;
        this.edited = edited;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("body", body);
        result.put("edited", edited);
        result.put("upVoteCount", upVoteCount);
        result.put("downVoteCount", downVoteCount);
        result.put("upVotes", upVotes);
        result.put("downVotes", downVotes);

        return result;
    }
}
