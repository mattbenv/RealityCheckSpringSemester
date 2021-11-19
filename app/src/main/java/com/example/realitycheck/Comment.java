package com.example.realitycheck;

public class Comment {


    private String commentAuthor;
    private String commentContent;
    private String commentDate;

    public Comment(){};
    public Comment(String author, String content, String date){
        this.commentAuthor = author;
        this.commentContent = content;
        this.commentDate = date;
    }


    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

}

