package com.lucas.omnia.models;

public class Reply {
    private int id3;
    private String user3;
    private String comment3;
    private String type3;
    private int votes3;
    String text3;

    public Reply(int i1, String s1, String s2, String s3, int i2) {
        id3 = i1;
        user3 = s1;
        comment3 = s2;
        type3 = s3;
        votes3 = i2;
    }

    public Reply(int p, String s) {
        id3 = p;
        text3 = s;
    }

    public String getText3() {
        return text3;
    }

    public int getId3() {
        return id3;
    }

    public String getUser3() {
        return user3;
    }

    public String getComment3() {
        return comment3;
    }

    public String getType3() {
        return type3;
    }

    public void upVote3() {
        votes3++;
    }

    public void downVote3() {
        votes3--;
    }

    public void removeUpVote3() {
        votes3--;
    }

    public void removeDownVote3() {
        votes3++;
    }

    public int getmVotes3() {
        return votes3;
    }
}
