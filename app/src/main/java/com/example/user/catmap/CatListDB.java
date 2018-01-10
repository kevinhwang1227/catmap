package com.example.user.catmap;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2018-01-10.
 */

public class CatListDB {
    private Bitmap image;
    private float lat,lng;
    private HashMap<String,String> cats;
    private String ID;


    public String getID() {
        return ID;
    }
    public HashMap<String,String> getCats() {
        return cats;
    }
    public Bitmap getimage() {
        return image;
    }
    public float getlat() {
        return lat;
    }
    public float getlng() {
        return lng;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public void setCats(HashMap<String,String> cats) {
        this.cats = cats;
    }
    public void setimage(Bitmap image) {
        this.image = image;
    }
    public void setlat(float lat) {
        this.lat = lat;
    }
    public void setlng(float lng) {
        this.lng = lng;
    }

}
