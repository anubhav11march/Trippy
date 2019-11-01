package com.example.trippy;

public class Note {

    String title, date, content, datePosted;
    int fav;
    Note(String title, String date, String content, int fav, String datePosted){
        this.content = content;
        this.date = date;
        this.title = title;
        this.fav = fav;
        this.datePosted = datePosted;

    }
    Note(){}

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
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

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

