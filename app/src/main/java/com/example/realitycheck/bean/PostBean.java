package com.example.realitycheck.bean;

import java.util.Date;

public class PostBean {

    String avatar;
    String Title;
    String content;
    String description;
    String currentDate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCurrentDate(String aDate){
        this.currentDate = aDate;
    }
    public String getCurrentDate(){
        return this.currentDate;
    }
}
