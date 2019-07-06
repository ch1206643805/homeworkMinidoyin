package com.example.minidoyin.network;

import com.example.minidoyin.bean.FeedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;

public interface DouyinService {


    @GET ("/mini_douyin/invoke/video")
    Call<FeedResponse> randomVideo();

}
