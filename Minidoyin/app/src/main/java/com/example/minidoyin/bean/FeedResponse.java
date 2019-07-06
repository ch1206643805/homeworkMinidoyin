package com.example.minidoyin.bean;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
@effect: 解析数据流JSON

 */
public class FeedResponse {
    @SerializedName("feeds") private List<Feed> feeds;
    @SerializedName("success") private boolean success;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public boolean getSuccess() {
        return success;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }
}
