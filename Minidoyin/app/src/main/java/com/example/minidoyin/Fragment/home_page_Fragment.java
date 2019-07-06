package com.example.minidoyin.Fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Network;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.minidoyin.R;
import com.example.minidoyin.bean.Feed;
import com.example.minidoyin.bean.FeedResponse;
import com.example.minidoyin.network.DouyinService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;


/**
 * A simple {@link Fragment} subclass.
 */
public class home_page_Fragment extends Fragment {
    private List<Feed> mfeeds = new ArrayList<Feed>();
    private RecyclerView recyclerView;
    private static final String TAG = "home_page_Fragment";

    public home_page_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page_,container,false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                Log.i(TAG, "onCreateViewHolder: "+viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setAdjustViewBounds(true);
                return new MyViewHolder(imageView);
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView imageView =(ImageView) viewHolder.itemView;
                String image_url=mfeeds.get(i).getImage_url();
                String video_url=mfeeds.get(i).getVideo_url();
                Glide.with(imageView.getContext()).load(image_url).into(imageView);
            }
            @Override
            public int getItemCount() {
                return mfeeds.size();
            }
        });

        requestData(view);

        return view;
    }

    /*
    @effect :自定义 viewholder
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            ImageView imageView = (ImageView) itemView;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //
                }
            });


        }
    }

    /*
    @effect: 得到图片和视频url
     */
    private void requestData(View view){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<FeedResponse> call = retrofit.create(DouyinService.class).randomVideo();
        call.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null && response.body().getSuccess()){
                        loadPicture(response.body().getFeeds());
                        Log.i(TAG, "onResponse: success");
                    }
                    else{
                        Log.i(TAG, "onResponse: fail");
                        //失败
                    }
                }
                else {
                    Log.i(TAG, "onResponse: fail");
                    //失败
                }
            }
            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Log.i(TAG, "onFailure: ");
            }
        });

    }

    /*
    @input: List<Feed> feeds
    @effect: 从ur中得到图片显示出来
     */
    public void loadPicture(List<Feed> feeds){
        mfeeds=feeds;
        recyclerView.getAdapter().notifyDataSetChanged();
    }


}
