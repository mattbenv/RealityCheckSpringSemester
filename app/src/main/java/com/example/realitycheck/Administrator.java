package com.example.realitycheck;

import java.util.ArrayList;

public class Administrator {
    //Lets build an array of features for the administrator to have control over USERS specific privelages
    public String name;
    public ArrayList features;
    public boolean can_post;
    public boolean is_blocked;
    public boolean banned_group;
    public boolean banned_followers;
    public boolean banned_following;
    public boolean can_like;
    public boolean can_follow;
    public boolean can_joingroup;
    public boolean mediaImages;
    public boolean getnotified;

    //Lets build an array of features within the POSTS & COMMENTS
    public boolean flaggedposts;


    public Administrator(String name, ArrayList features, boolean can_post, boolean is_blocked, boolean banned_group, boolean banned_followers, boolean banned_following, boolean can_like, boolean can_follow, boolean can_joingroup, boolean mediaImages, boolean getnotified, boolean flaggedposts) {
        this.name = name;
        this.features = features;
        this.can_post = can_post;
        this.is_blocked = is_blocked;
        this.banned_group = banned_group;
        this.banned_followers = banned_followers;
        this.banned_following = banned_following;
        this.can_like = can_like;
        this.can_follow = can_follow;
        this.can_joingroup = can_joingroup;
        this.mediaImages = mediaImages;
        this.getnotified = getnotified;
        this.flaggedposts = flaggedposts;
    }
    public Administrator(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList features) {
        this.features = features;
    }

    public boolean isCan_post() {
        return can_post;
    }

    public void setCan_post(boolean can_post) {
        this.can_post = can_post;
    }

    public boolean isIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(boolean is_blocked) {
        this.is_blocked = is_blocked;
    }

    public boolean isBanned_group() {
        return banned_group;
    }

    public void setBanned_group(boolean banned_group) {
        this.banned_group = banned_group;
    }

    public boolean isBanned_followers() {
        return banned_followers;
    }

    public void setBanned_followers(boolean banned_followers) {
        this.banned_followers = banned_followers;
    }

    public boolean isBanned_following() {
        return banned_following;
    }

    public void setBanned_following(boolean banned_following) {
        this.banned_following = banned_following;
    }

    public boolean isCan_like() {
        return can_like;
    }

    public void setCan_like(boolean can_like) {
        this.can_like = can_like;
    }

    public boolean isCan_follow() {
        return can_follow;
    }

    public void setCan_follow(boolean can_follow) {
        this.can_follow = can_follow;
    }

    public boolean isCan_joingroup() {
        return can_joingroup;
    }

    public void setCan_joingroup(boolean can_joingroup) {
        this.can_joingroup = can_joingroup;
    }

    public boolean isMediaImages() {
        return mediaImages;
    }

    public void setMediaImages(boolean mediaImages) {
        this.mediaImages = mediaImages;
    }

    public boolean isGetnotified() {
        return getnotified;
    }

    public void setGetnotified(boolean getnotified) {
        this.getnotified = getnotified;
    }

    public boolean isFlaggedposts() {
        return flaggedposts;
    }

    public void setFlaggedposts(boolean flaggedposts) {
        this.flaggedposts = flaggedposts;
    }
}
