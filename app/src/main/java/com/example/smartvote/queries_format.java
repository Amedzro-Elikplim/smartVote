package com.example.smartvote;

public class queries_format {

    private String admin, date, post, time, title;

    public queries_format(){
        // this is the default constructor
    }

    public queries_format(String admin, String date, String post, String time, String title) {
        this.admin = admin;
        this.date = date;
        this.post = post;
        this.time = time;
        this.title = title;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
