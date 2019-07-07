package com.example.minidoyin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.minidoyin.Fragment.follow_page_Fragment;
import com.example.minidoyin.Fragment.home_page_Fragment;
import com.example.minidoyin.Fragment.me_page_Fragment;
import com.example.minidoyin.Fragment.message_page_Fragment;

public class Page_Activity extends AppCompatActivity {
    private static final String TAG = "Page_Activity";
    private Button button_camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_);
        button_camera = findViewById(R.id.camera_button);
        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page_Activity.this,MyCameraActivity.class);
                startActivity(intent);
            }
        });
        requestPremission();
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
        //申请相机、麦克风、存储的权限
        if((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_NETWORK_STATE)!=PackageManager.PERMISSION_GRANTED

        ){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE},111);
        }
        else {
            init_viewpage();
        }

    }

    /*
    @effect：返回获取权限结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 111:{
                boolean permission_flag = true;
                for (int i=0;i<grantResults.length;i++){
                    if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                        permission_flag=false;
                    }
                }
                if(permission_flag){
                    init_viewpage();
                }
                else {
                    Toast.makeText(this,"没有权限",Toast.LENGTH_LONG).show();
                }

                break;
            }
        }
    }


}
