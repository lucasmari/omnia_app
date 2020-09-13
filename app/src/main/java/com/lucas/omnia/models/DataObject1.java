package com.lucas.omnia.models;

/**
 * Created by Lucas on 24/10/2017.
 */

public class DataObject1 {

    private int id;
    private String user;
    private String comment;
    private int votes;

    public DataObject1(int i1, String s1, String s2, int i2) {
        id = i1;
        user = s1;
        comment = s2;
        votes = i2;
    }

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getComment() {
        return comment;
    }

    public void upVote() {
        votes++;
    }

    public void downVote() {
        votes--;
    }

    public void removeUpVote() {
        votes--;
    }

    public void removeDownVote() {
        votes++;
    }

    public int getmVotes() {
        return votes;
    }
}
