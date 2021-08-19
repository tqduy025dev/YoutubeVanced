package com.tranquangduy.model;

import java.io.Serializable;

public class Song implements Serializable {
    private String tittle;
    private String image;
    private int resource;

    public Song(){

    }

    public Song(String tittle, String image, int resource) {
        this.tittle = tittle;
        this.image = image;
        this.resource = resource;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
