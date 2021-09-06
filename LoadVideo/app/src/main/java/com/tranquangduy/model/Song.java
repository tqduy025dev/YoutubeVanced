package com.tranquangduy.model;

import java.io.Serializable;

public class Song implements Serializable {
    private String tittle;
    private String image;
    private String url;

    public Song(){
    }

    public Song(String tittle, String image, String url) {
        this.tittle = tittle;
        this.image = image;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}