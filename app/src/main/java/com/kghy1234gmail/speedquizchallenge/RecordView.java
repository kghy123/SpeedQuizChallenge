package com.kghy1234gmail.speedquizchallenge;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordView extends SurfaceView implements SurfaceHolder.Callback {

    Context context;
    android.hardware.Camera camera = null;
    SurfaceHolder holder = null;
    MediaRecorder mr;
    String path;
    File file;

    int result;

    boolean isRecording = true;


    public RecordView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void init(){

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        int cameraId = 0;
        /* 카메라가 여러개 일 경우 그 수를 가져옴  */
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();

        for(int i=0; i < numberOfCameras; i++) {
            android.hardware.Camera.getCameraInfo(i, cameraInfo);

            /* 전면 카메라를 쓸 것인지 후면 카메라를 쓸것인지 설정 시 */
            /* 전면카메라 사용시 CAMERA_FACING_FRONT 로 조건절 */
            if(cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT)
                cameraId = i;
        }


        /* open시 camera id를 주어 선택 */
        camera = android.hardware.Camera.open(cameraId);


        /* surface 사용시 카메라가 회전되어 있어 그에 대한 보정 */
        setCameraDisplayOrientation(context, cameraId, camera);

        try{
            camera.setPreviewDisplay(holder);

        }catch(IOException e){
            e.printStackTrace();
            camera.release();
            camera = null;
        }


    }

    public void setCameraDisplayOrientation(Context context,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }


        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {


        if(isRecording)startRec();


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        stopRec();

    }

    public void startRec(){


        if(camera != null) {

            isRecording = true;

            camera.startPreview();
            camera.unlock();
            mr = new MediaRecorder();
            mr.setCamera(camera);



            mr.setAudioSource(MediaRecorder.AudioSource.MIC);
            mr.setVideoSource(MediaRecorder.VideoSource.CAMERA);

//            mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mr.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

            mr.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));

            mr.setOrientationHint(0);

            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date date = new Date();
            String time = sdf.format(date);

            File dir = new File(path + "/SpeedQuizChallenge/");
            if(!dir.exists()) dir.mkdirs();

            path = path + "/SpeedQuizChallenge/" + time + ".mp4";
            mr.setOutputFile(path);
            Log.d("FILE", "파일생성");



            mr.setPreviewDisplay(holder.getSurface());

            try {
                mr.prepare();
                mr.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void stopRec(){
        if(camera != null) {
            isRecording = false;

            camera.stopPreview();
            camera.release();
            camera = null;

            mr.stop();
            mr.release();
            mr = null;

            file = new File(path);

        }
    }
}
