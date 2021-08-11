package com.tranquangduy.model;

import java.io.Serializable;
import java.util.List;

public class Root implements Serializable {
    public String kind;
    public String etag;
    public String nextPageToken;
    public String regionCode;
    public PageInfo pageInfo;
    public List<Items> items;


    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public class PageInfo implements Serializable{
        public long totalResults;
        public int resultsPerPage;


        public long getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(long totalResults) {
            this.totalResults = totalResults;
        }

        public int getResultsPerPage() { return resultsPerPage; }

        public void setResultsPerPage(int resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
        }
    }


    public class Items implements Serializable{
        public String kind;
        public String etag;
        public Id id;
        public Snippet snippet;


        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public Id getId() {
            return id;
        }

        public void setId(Id id) {
            this.id = id;
        }
    }

    public class Id implements Serializable{

        public String kind;
        public String videoId;


        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }
    }


    public class Snippet implements Serializable{
        public String channelId;
        public String title;
        public Thumbnails thumbnails;
        public String channelTitle;
        public String publishTime;

    }


    public class Thumbnails implements Serializable{
        public High high;
    }

    public class High implements Serializable{
        public String url;
        public int width;
        public int height;
    }




}





