package com.Mktplace.LevelUp.ui.mktplace;

public class MktplaceItem {
    private String mktPlaceID;
    private String creatorID;

    private String imageUrl;
    private String name;
    private String location;
    private String description;

    public MktplaceItem(String name, String imageUrl, String location, String description) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        this.name = name;
        this.imageUrl = imageUrl;
        this.location = location;
        this.description = description;
    }

    public MktplaceItem(String mktPlaceID, String creatorID, String name, String imageUrl, String location, String description) {
        this.mktPlaceID = mktPlaceID;
        this.creatorID = creatorID;
        if (name.trim().equals("")) {
            name = "No Name";
        }
        this.name = name;
        this.imageUrl = imageUrl;
        this.location = location;
        this.description = description;
    }

    public MktplaceItem() {
        //empty constructor needed
    }

    public String getName() {
        return name;

    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return this.location;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getMktPlaceID() {
        return mktPlaceID;
    }
}