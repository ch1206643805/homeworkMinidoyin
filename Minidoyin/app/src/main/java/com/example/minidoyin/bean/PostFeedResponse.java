package com.example.minidoyin.bean;

import com.google.gson.annotations.SerializedName;

public class PostFeedResponse {
    @SerializedName("success") private boolean success;

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public boolean getSuccess(){
        return this.success;
    }
}
