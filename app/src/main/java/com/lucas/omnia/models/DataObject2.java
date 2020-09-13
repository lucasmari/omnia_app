package com.lucas.omnia.models;

/**
 * Created by Lucas on 21/11/2017.
 */

public class DataObject2 {

    private int id2;
    private String user2;
    private String comment2;
    private String type2;
    private int votes2;
    private String text2;
    private int child2;

    public DataObject2(int p, String s, int c) {
        id2 = p;
        text2 = s;
        child2 = c;
    }

    public String getText2() {
        return text2;
    }

    public int getId2() {
        return id2;
    }

    public int getChild2() {
        return child2;
    }

    public void setChild2() {
        child2++;
    }

    public String getUser2() {
        return user2;
    }

    public String getComment2() {
        return comment2;
    }

    public String getType2() {
        return type2;
    }

    public void upVote2() {
        votes2++;
    }

    public void downVote2() {
        votes2--;
    }

    public void removeUpVote2() {
        votes2--;
    }

    public void removeDownVote2() {
        votes2++;
    }

    public int getmVotes2() {
        return votes2;
    }
}
