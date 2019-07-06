package com.example.minidoyin.bean;

import com.google.gson.annotations.SerializedName;

/*
 @effect  单个视频图像JSON解析
 */

public class Feed {
    @SerializedName("student_id") private String student_id;
    @SerializedName("user_name") private String user_name;
    @SerializedName("image_url") private String image_url;
    @SerializedName("video_url") private String video_url;
    @SerializedName("createdAt") private String createdAt;
    @SerializedName("updatedAt") private String updatedAt;

    public String getVideo_url() {
        return video_url;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getStudent_id() {
        return student_id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

}
