package com.example.minidoyin;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.minidoyin.Fragment.follow_page_Fragment;
import com.example.minidoyin.Fragment.home_page_Fragment;
import com.example.minidoyin.Fragment.me_page_Fragment;
import com.example.minidoyin.Fragment.message_page_Fragment;

public class Page_Activity extends AppCompatActivity {
    private static final String TAG = "Page_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_);

        init_viewpage();
    }


    /*
    @effect: 在viewpage根据页数初始化不同的fragment
     */
    private void init_viewpage(){
        ViewPager pager = findViewById(R.id.view_page);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                Fragment fragment=null;
                switch (i){
                    case 0:{
                        fragment=new home_page_Fragment();
                        break;
                    }
                    case 1:{
                        fragment=new follow_page_Fragment();
                        break;
                    }
                    case 2:{
                        fragment=new message_page_Fragment();
                        break;
                    }
                    case 3:{
                        fragment=new me_page_Fragment();
                        break;
                    }
                    default:{
                        Log.i(TAG, "init_viewpage getItem i not found");
                        break;
                    }
                }
                return fragment;
            }
            @Override
            public int getCount() {
                return 4;
            }
        });
    }








    /*
    @effect: 申请权限
     */
    private void requestPremission(){

    }

    /*
    @effect：返回获取权限结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
