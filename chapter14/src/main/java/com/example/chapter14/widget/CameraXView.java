package com.example.chapter14.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.chapter14.util.BitmapUtil;
import com.example.chapter14.util.DateUtil;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("RestrictedApi")
public class CameraXView extends RelativeLayout {
    private static final String TAG = "CameraXView";
    private Context mContext; // 声明一个上下文对象
    private PreviewView mCameraPreview; // 声明一个预览视图对象
    private CameraSelector mCameraSelector; // 声明一个摄像头选择器
    private Preview mPreview; // 声明一个预览对象
    private ProcessCameraProvider mCameraProvider; // 声明一个相机提供器
    private ImageCapture mImageCapture; // 声明一个图像捕捉器
    private VideoCapture mVideoCapture; // 声明一个视频捕捉器
    private ExecutorService mExecutorService; // 声明一个线程池对象
    private LifecycleOwner mOwner; // 声明一个生命周期拥有者

    private final Handler mHandler = new Handler(Looper.getMainLooper()); // 声明一个处理器对象
    public final static int MODE_PHOTO = 0;
    public final static int MODE_RECORD = 1;
    private int mCameraMode = MODE_PHOTO; // 0拍照，1录像
    private int mCameraType = CameraSelector.LENS_FACING_BACK; // 摄像头类型，默认后置摄像头
    private int mAspectRatio = AspectRatio.RATIO_16_9; // 宽高比例。RATIO_4_3表示宽高3比4；RATIO_16_9表示宽高9比16
    private int mFlashMode = ImageCapture.FLASH_MODE_AUTO; // 闪光灯模式
    private String mMediaDir; // 媒体保存目录
    private OnStopListener mStopListener; // 停止拍摄监听器

    public interface OnStopListener {
        void onStop(String result);
    }

    public CameraXView(Context context) {
        this(context, null);
    }

    public CameraXView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCameraPreview = new PreviewView(mContext); // 创建一个预览视图
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCameraPreview.setLayoutParams(params);
        addView(mCameraPreview); // 把预览视图添加到界面上
        mExecutorService = Executors.newSingleThreadExecutor(); // 创建一个单线程线程池
        mMediaDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
    }

    // 打开相机
    public void openCamera(LifecycleOwner owner, int cameraMode, OnStopListener sl) {
        mOwner = owner;
        mCameraMode = cameraMode;
        mStopListener = sl;
        mHandler.post(() ->  initCamera()); // 初始化相机
    }

    // 初始化相机
    private void initCamera() {
        ListenableFuture future = ProcessCameraProvider.getInstance(mContext);
        future.addListener(() -> {
            try {
                mCameraProvider = (ProcessCameraProvider) future.get();
                resetCamera(); // 重置相机
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(mContext));
    }

    // 重置相机
    private void resetCamera() {
        int rotation = mCameraPreview.getDisplay().getRotation();
        // 构建一个摄像头选择器
        mCameraSelector = new CameraSelector.Builder().requireLensFacing(mCameraType).build();
        // 构建一个预览对象
        mPreview = new Preview.Builder()
                .setTargetAspectRatio(mAspectRatio) // 设置宽高比例
                .build();
        // 构建一个图像捕捉器
        mImageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) // 设置捕捉模式
                .setTargetRotation(rotation) // 设置旋转角度
                .setTargetAspectRatio(mAspectRatio) // 设置宽高比例
                .setFlashMode(mFlashMode) // 设置闪光模式
                .build();
        if (mCameraMode == MODE_RECORD) { // 录像
            // 构建一个视频捕捉器
            mVideoCapture = new VideoCapture.Builder()
                    .setTargetAspectRatio(mAspectRatio) // 设置宽高比例
                    .setVideoFrameRate(60) // 设置视频帧率
                    .setBitRate(3 * 1024 * 1024) // 设置比特率
                    .setTargetRotation(rotation) // 设置旋转角度
                    .setAudioRecordSource(MediaRecorder.AudioSource.MIC)
                    .build();
        }
        bindCamera(MODE_PHOTO); // 绑定摄像头
        // 设置预览视图的表面提供器
        mPreview.setSurfaceProvider(mCameraPreview.getSurfaceProvider());
    }

    // 绑定摄像头
    private void bindCamera(int captureMode) {
        mCameraProvider.unbindAll(); // 重新绑定前要先解绑
        try {
            if (captureMode == MODE_PHOTO) { // 拍照
                // 把相机选择器、预览视图、图像捕捉器绑定到相机提供器的生命周期
                Camera camera = mCameraProvider.bindToLifecycle(
                        mOwner, mCameraSelector, mPreview, mImageCapture);
            } else if (captureMode == MODE_RECORD) { // 录像
                // 把相机选择器、预览视图、视频捕捉器绑定到相机提供器的生命周期
                Camera camera = mCameraProvider.bindToLifecycle(
                        mOwner, mCameraSelector, mPreview, mVideoCapture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 切换摄像头
    public void switchCamera() {
        if (mCameraType == CameraSelector.LENS_FACING_BACK) {
            mCameraType = CameraSelector.LENS_FACING_FRONT;
        } else {
            mCameraType = CameraSelector.LENS_FACING_BACK;
        }
        resetCamera(); // 重置相机
    }

    private String mPhotoPath; // 照片保存路径
    // 获取照片的保存路径
    public String getPhotoPath() {
        return mPhotoPath;
    }

    // 开始拍照
    public void takePicture() {
        mPhotoPath = String.format("%s/%s.jpg", mMediaDir, DateUtil.getNowDateTime());
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        // 构建图像捕捉器的输出选项
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(new File(mPhotoPath))
                .setMetadata(metadata).build();
        // 执行拍照动作
        mImageCapture.takePicture(options, mExecutorService, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                BitmapUtil.notifyPhotoAlbum(mContext, mPhotoPath); // 通知相册来了张新图片
                mStopListener.onStop("已完成拍摄，照片保存路径为"+mPhotoPath);
            }

            @Override
            public void onError(ImageCaptureException exception) {
                mStopListener.onStop("拍摄失败，错误信息为："+exception.getMessage());
            }
        });
    }

    private String mVideoPath; // 视频保存路径
    private int MAX_RECORD_TIME = 15; // 最大录制时长，默认15秒
    // 获取视频的保存路径
    public String getVideoPath() {
        return mVideoPath;
    }

    // 开始录像
    public void startRecord(int max_record_time) {
        MAX_RECORD_TIME = max_record_time;
        bindCamera(MODE_RECORD); // 绑定摄像头
        mVideoPath = String.format("%s/%s.mp4", mMediaDir, DateUtil.getNowDateTime());
        VideoCapture.Metadata metadata = new VideoCapture.Metadata();
        // 构建视频捕捉器的输出选项
        VideoCapture.OutputFileOptions options = new VideoCapture.OutputFileOptions.Builder(new File(mVideoPath))
                .setMetadata(metadata).build();
        // 开始录像动作
        mVideoCapture.startRecording(options, mExecutorService, new VideoCapture.OnVideoSavedCallback() {
            @Override
            public void onVideoSaved(VideoCapture.OutputFileResults outputFileResults) {
                mHandler.post(() -> bindCamera(MODE_PHOTO));
                mStopListener.onStop("录制完成的视频路径为"+mVideoPath);
            }

            @Override
            public void onError(int videoCaptureError, String message, Throwable cause) {
                mHandler.post(() -> bindCamera(MODE_PHOTO));
                mStopListener.onStop("录制失败，错误信息为："+cause.getMessage());
            }
        });
        // 限定时长到达之后自动停止录像
        mHandler.postDelayed(() -> stopRecord(), MAX_RECORD_TIME*1000);
    }

    // 停止录像
    public void stopRecord() {
        mVideoCapture.stopRecording(); // 视频捕捉器停止录像
    }

    // 关闭相机
    public void closeCamera() {
        mCameraProvider.unbindAll(); // 解绑相机提供器
        mExecutorService.shutdown(); // 关闭线程池
    }

}
