package com.levelup.occasion;

public class LikeOccasionItem {
    private String occasionID;
    private String userID;

    public LikeOccasionItem(String occasionID, String userID) {
        this.occasionID = occasionID;
        this.userID = userID;
    }

    public LikeOccasionItem() {

    }

    public String getOccasionID() {
        return occasionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setOccasionID(String occasionID) {
        this.occasionID = occasionID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
