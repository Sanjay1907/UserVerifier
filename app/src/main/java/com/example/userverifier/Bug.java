package com.example.userverifier;

public class Bug {
    private String userName;
    private String id;
    private String description;
    private String imageUrl;

    public Bug(String userName, String id, String description, String imageUrl) {
        this.userName = userName;
        this.id = id;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getId(){
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

