package com.example.tony.tonyfactory.youtube;

/**
 * Created by Administrator on 2016-11-28.
 */

public class SearchVideo {

    private String videoId;
    private String title;
    private String url;
    private String publishedAt;

    public SearchVideo(String videoId, String title, String url, String publishedAt) {
        super();
        this.videoId = videoId;
        this.title = title;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
