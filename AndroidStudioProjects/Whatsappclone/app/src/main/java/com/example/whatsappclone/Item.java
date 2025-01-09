package com.example.whatsappclone;


import android.graphics.drawable.Drawable;

public class Item {
    private int imageResId;
    private String label;
    private String name;

    public Item(int imageResId, String label, String name) {
        this.imageResId = imageResId;
        this.label = label;
        this.name = name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }
}
