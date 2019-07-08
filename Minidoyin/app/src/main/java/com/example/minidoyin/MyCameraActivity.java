package com.example.minidoyin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Picture;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.example.minidoyin.bean.PostFeedResponse;
import com.example.minidoyin.network.DouyinService;
import com.example.minidoyin.network.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.invoke.MutableCallSite;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import android.os.Build;
public class MyCameraActivity extends AppCompatActivity {

    private static final String TAG = "MyCameraActivity";
    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int CURRENT_CAMERA_TYPE =Camera.CameraInfo.CAMERA_FACING_BACK;
    private int TYPE_OUTFILE_IMG = 0;
    private int TYPE_OUTFILE_VIDEO = 1;
    private int progress_camera =0; //缩放程度
    private int rotationDegree = 0; //旋转
    private int Camer_sytle = TYPE_OUTFILE_VIDEO;
    private int Select_style = TYPE_OUTFILE_IMG;
    private boolean isRecording=false;
    private Uri mSelectImg;
    private Uri mSelectVideo;

    private SurfaceView msurfaceView;
    private SurfaceHolder msurfaceHolder;
    private android.hardware.Camera mcamera;
    private MediaRecorder mmediaRecorder;


    private SeekBar seekBar;
    private Button button_flip;
    private Button button_upload;
    private Button button_exit;
    private Button button_recording;
    private Button button_sytle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_camera);
        button_sytle = findViewById(R.id.buttonStyle);
        button_recording = findViewById(R.id.buttonRecording);
        button_flip = findViewById(R.id.buttonFlip);
        button_exit = findViewById(R.id.buttonExit);
        button_upload = findViewById(R.id.buttonUpload);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(100);
        msurfaceView = findViewById(R.id.surfaceview);
        init();
    }

    /*
    @effect:初始
     */
    public void init(){
        
        mcamera = getCamera(CAMERA_TYPE);
        msurfaceHolder = msurfaceView.getHolder();
        msurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        msurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mcamera.setPreviewDisplay(holder);
                    mcamera.startPreview();
                    AutoFocus();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
       //         mcamera.stopPreview();
      //          mcamera.release();
      //          mcamera = null;
            }
        });


        //设置视频录制,图片拍摄
        button_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Camer_sytle == TYPE_OUTFILE_IMG){
                    mcamera.takePicture(null,null,mPicture);
                }
                else if(Camer_sytle ==TYPE_OUTFILE_VIDEO){
                    open_close_video();
                }

            }
        });
        //翻转按钮
        button_flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera_flip();
            }
        });
        //手动调焦
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 progress_camera= progress;
                Camera.Parameters p =mcamera.getParameters();
                p.setZoom(progress_camera);
                mcamera.setParameters(p);
                if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){
                    AutoFocus();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //切换拍照、录像模式
        button_sytle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Camer_sytle == TYPE_OUTFILE_VIDEO){
                    Camer_sytle = TYPE_OUTFILE_IMG;
                    button_sytle.setText("拍照");
                }
                else if(Camer_sytle == TYPE_OUTFILE_IMG){
                    Camer_sytle = TYPE_OUTFILE_VIDEO;
                    button_sytle.setText("录像");
                }

            }
        });

        //开始上传
        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload(Select_style);
            }
        });

    }

    /*
    @effct:开启，关闭录制
     */
    public void open_close_video(){
        if(isRecording){
            //结束录制
            mmediaRecorder.stop();
            mmediaRecorder.reset();
            mmediaRecorder.release();
            mmediaRecorder=null;
            mcamera.lock();
            isRecording=false;
            Toast.makeText(MyCameraActivity.this,"结束录制",Toast.LENGTH_LONG).show();
        }else{
            //开始录制
            Toast.makeText(MyCameraActivity.this,"开始录制",Toast.LENGTH_LONG).show();
            isRecording = true;
            mmediaRecorder = new MediaRecorder();
            mcamera.unlock();
            mmediaRecorder.setCamera(mcamera);
            mmediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mmediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mmediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
            mmediaRecorder.setOutputFile(getOutputFile(TYPE_OUTFILE_VIDEO).toString());
            mmediaRecorder.setPreviewDisplay(msurfaceView.getHolder().getSurface());
            mmediaRecorder.setOrientationHint(rotationDegree);

            try {
                mmediaRecorder.prepare();
            }catch (Exception e){
                //释放 mmediaRecorder
                        /*
                        mmediaRecorder.stop();
                        mmediaRecorder.reset();
                        mmediaRecorder.release();
                        mmediaRecorder=null;
                        */
                e.printStackTrace();
            }
            mmediaRecorder.start();
        }
    }

    /*
    @effect:打开摄像头
     */
    private  Camera getCamera(int position){
        CAMERA_TYPE = position;
        CURRENT_CAMERA_TYPE =position;
        if (mcamera != null) {
            mcamera.release();
        }
        Camera cam = Camera.open(position);
        rotationDegree =getCameraDisplayOrientation(position);
        cam.setDisplayOrientation(rotationDegree);
        return cam;
    }

    /*
    @effect:翻转摄像头
     */
    private void camera_flip(){
        progress_camera =0;
        mcamera.stopPreview();
        mcamera.release();
        if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){
            mcamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
            CURRENT_CAMERA_TYPE=Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else{
            mcamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            CURRENT_CAMERA_TYPE=Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        //调整camera 大小

        Camera.Parameters params = mcamera.getParameters();
        /*
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) (getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getMetrics(displayMetrics);

        Camera.Size size= getOptimalPreviewSize(params.getSupportedPreviewSizes(),
                displayMetrics.widthPixels,displayMetrics.heightPixels);
         params.setPreviewSize();
        Log.i(TAG, "camera_flip: "+size.width+"      "+size.height);
        mcamera.setParameters(params);
        */
        List<Camera.Size> supportedPreviewSizes = params.getSupportedPreviewSizes();
        params.setPreviewSize(supportedPreviewSizes.get(0).width, supportedPreviewSizes.get(0).height);
        mcamera.setParameters(params);
        try {
            mcamera.setPreviewDisplay(msurfaceHolder);
            mcamera.startPreview();

        }catch (Exception e){
            mcamera.startPreview();
            mcamera.release();
            mcamera=null;
            e.printStackTrace();
        }
        if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){
            AutoFocus();
        }
    }



    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;
    /*
    @effect:旋转手机屏幕
     */
    private int getCameraDisplayOrientation(int cameraId){
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId,info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }

    /*
    @effect:摄像头自动调焦
     */
    private void AutoFocus(){
        mcamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){
                    mcamera.cancelAutoFocus();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    camera.setParameters(parameters);
                }
            }
        });
    }

    /*
    @effect:视频录制
     */
    private void video(){


    }
    /*
    @effect:获取输出File，设置其存储路径
     */
    private File getOutputFile(int type){
        File outputFileDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM),"Camera");
        if(!outputFileDir.exists()){
            if(!outputFileDir.mkdir()){
                return null;
            }
        }

        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outputFile;
        if(type==TYPE_OUTFILE_IMG){
            //图片
            outputFile = new File(outputFileDir.getPath()+File.separator+
                    "IMG_"+timeStamp+".jpg");
        }else if(type == TYPE_OUTFILE_VIDEO){
            //视频
            outputFile = new File(outputFileDir.getPath() + File.separator+
                    "VID_"+timeStamp+".mp4");
        }else{
            return  null;
        }
        /*设置file 保存刷新在camera中 但没生效
        Uri localUri = Uri.fromFile(outputFile);
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,localUri);
        sendBroadcast(localIntent);
            */
        return outputFile;
    }

    /*
    @effect: 绑定holder
     */
    private void  newHoler(){
        msurfaceHolder = msurfaceView.getHolder();
    }


    /*
    @effect:修改摄像分辨率
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /*
    @effect:图片拍摄
     */
    private Camera.PictureCallback mPicture =(data,camera)->{
        File picturefile = getOutputFile(TYPE_OUTFILE_IMG);
        if(picturefile ==null){
            return;
        }
        try{
            FileOutputStream fos = new FileOutputStream(picturefile);
            fos.write(data);
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        mcamera.startPreview();
        Toast.makeText(this,"拍照完毕",Toast.LENGTH_LONG).show();;
        AutoFocus();
    };


    /*
    @effect:上传材料
     */
    private void Upload (int style){
        Intent intent = new Intent();
        if(style==TYPE_OUTFILE_IMG){
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"select img"),TYPE_OUTFILE_IMG);

        }else if(style==TYPE_OUTFILE_VIDEO){
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"select_video"),TYPE_OUTFILE_VIDEO);
        }

    }

    /*
    @effect:intent 返回值
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null){
            if(requestCode==TYPE_OUTFILE_IMG){
                mSelectImg=data.getData();
                Toast.makeText(this,"获取图片成功，继续上传视频",Toast.LENGTH_LONG).show();
                Select_style = TYPE_OUTFILE_VIDEO;
            }else if(requestCode==TYPE_OUTFILE_VIDEO){
                mSelectVideo=data.getData();
                Toast.makeText(this,"获取视频成功",Toast.LENGTH_LONG).show();
                Select_style = TYPE_OUTFILE_IMG;
                Post();
            }
        }

    }

    /*
    @effect:post IMG && VIDEO
     */
    private void Post(){
        MultipartBody.Part cover_image = getMultipartFromUri("cover_image",mSelectImg);
        MultipartBody.Part video = getMultipartFromUri("video",mSelectVideo);
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
       Call<PostFeedResponse> call=retrofit.create(DouyinService.class)
               .postVideo("123456","bilibili",
                       cover_image,video);

       call.enqueue(new Callback<PostFeedResponse>() {
           @Override
           public void onResponse(Call<PostFeedResponse> call, Response<PostFeedResponse> response) {
               if(response.isSuccessful()){
                   if(response.body()!=null&&response.body().getSuccess()){
                       Toast.makeText(getApplicationContext(),"上传成功",Toast.LENGTH_LONG).show();
                   }
                   else{
                       Toast.makeText(getApplicationContext(),"上传失败",Toast.LENGTH_LONG).show();
                   }
               }
               else{
                   Toast.makeText(getApplicationContext(),"上传失败",Toast.LENGTH_LONG).show();
               }

           }

           @Override
           public void onFailure(Call<PostFeedResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"上传失败",Toast.LENGTH_LONG).show();
           }
       });




    }

    private MultipartBody.Part getMultipartFromUri(String name,Uri uri){
        File file = new File(ResourceUtils.getRealPath(MyCameraActivity.this,uri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        return MultipartBody.Part.createFormData(name, file.getName(),requestBody);

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mcamera==null){
            mcamera =getCamera(CURRENT_CAMERA_TYPE);
            msurfaceHolder = msurfaceView.getHolder();
            try {
                mcamera.setPreviewDisplay(msurfaceHolder);
            }catch (Exception e){
                e.printStackTrace();
            }
            mcamera.startPreview();
            Log.i(TAG, "onResume: "+mcamera);
            if(CURRENT_CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){
                AutoFocus();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
