package com.example.minidoyin;

import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCameraActivity extends AppCompatActivity {

    private static final String TAG = "MyCameraActivity";
    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int TYPE_OUTFILE_IMG = 0;
    private int TYPE_OUTFILE_VIDEO = 1;

    private int rotationDegree = 0; //旋转
    private boolean isRecording=false;

    private SurfaceView msurfaceView;
    private SurfaceHolder msurfaceHolder;
    private android.hardware.Camera mcamera;
    private MediaRecorder mmediaRecorder;


    private Button button_flip;
    private Button button_upload;
    private Button button_exit;
    private Button button_recording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        init();
    }

    /*
    @effect:初始
     */
    public void init(){
        button_recording = findViewById(R.id.buttonRecording);
        button_flip = findViewById(R.id.buttonFlip);
        button_exit = findViewById(R.id.buttonExit);
        button_upload = findViewById(R.id.buttonUpload);

        mcamera = getCamera(CAMERA_TYPE);
        msurfaceView = findViewById(R.id.surfaceview);
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
                mcamera.stopPreview();
                mcamera.release();
                mcamera = null;
            }
        });

        //设置视频录制
        button_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    mmediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

                    mmediaRecorder.setOutputFile(getOutputFile(TYPE_OUTFILE_VIDEO).toString());
                    mmediaRecorder.setPreviewDisplay(msurfaceView.getHolder().getSurface());
                    mmediaRecorder.setOrientationHint(rotationDegree);

                    try {
                        mmediaRecorder.prepare();
                        mmediaRecorder.start();
                    }catch (Exception e){
                        //释放 mmediaRecorder
                        mmediaRecorder.stop();
                        mmediaRecorder.reset();
                        mmediaRecorder.release();
                        mmediaRecorder=null;
                        e.printStackTrace();
                    }
                }
            }
        });
        ///


    }

    /*
    @effect:打开摄像头
     */
    private  Camera getCamera(int position){
        CAMERA_TYPE = position;
        if (mcamera != null) {
            mcamera.release();
        }
        Camera cam = Camera.open(position);
        rotationDegree =getCameraDisplayOrientation(position);
        cam.setDisplayOrientation(rotationDegree);

        return cam;
    }

    /*
    @effect:旋转手机屏幕
     */
    private int getCameraDisplayOrientation(int cameraId){
        Camera.CameraInfo info = new Camera.CameraInfo();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: {
                degrees = 0; //DEGREE_0
                break;
            }
            case Surface.ROTATION_90:{
                degrees = 90 ; //DEGREE_90
                break;
            }
            case Surface.ROTATION_180:{
                degrees = 180; //DEGREE_180
                break;
            }
            case Surface.ROTATION_270:{
                degrees = 270; //DEGREE_270
                break;
            }
            default:
                break;
        }
        int result;
        if(info.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (info.orientation + degrees)%360;
            result = (360 - result)%360;
        }else{
            result =(info.orientation -degrees +360)%360;
        }
        return result;
    }

    /*
    @effect:后置摄像头 自动调焦
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
                Environment.DIRECTORY_DCIM),"MiniDoYin");
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

        return outputFile;
    }

}
