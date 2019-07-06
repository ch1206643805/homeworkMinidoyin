package com.example.minidoyin.network;

import com.example.minidoyin.bean.FeedResponse;
import com.example.minidoyin.bean.PostFeedResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface DouyinService {


    @GET ("/mini_douyin/invoke/video")
    Call<FeedResponse> randomVideo();

    @Multipart
    @POST("/mini_douyin/invoke/video")
    Call<PostFeedResponse> postVideo(@Query("student_id") String student_id,
                                     @Query("user_name") String user_name,
                                     @Part MultipartBody.Part cover_image,
                                     @Part MultipartBody.Part video
                                     );

}
