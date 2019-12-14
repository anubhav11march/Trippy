package com.example.trippy;

public class Note {

    String title, time, content, postdate;
    int fav;
    Note(String title, String time, String content, int fav, String postdate){
        this.content = content;
        this.time = time;
        this.title = title;
        this.fav = fav;
        this.postdate = postdate;

    }
    Note(){}

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

