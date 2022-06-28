/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.hmsml.face.transactor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.example.hmsml.face.camera.FrameMetadata;
import com.example.hmsml.face.views.graphic.CameraImageGraphic;
import com.example.hmsml.face.views.graphic.LocalFaceGraphic;
import com.example.hmsml.face.views.overlay.GraphicOverlay;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class LocalFaceTransactor extends BaseTransactor<List<MLFace>> {
    private static final String TAG = "LocalFaceTransactor";

    private final MLFaceAnalyzer detector;
    private boolean isOpenFeatures;
    private Context mContext;
    private boolean isOpenDots;

    public LocalFaceTransactor(Context context) {
        super(context);
        int featureType = MLFaceAnalyzerSetting.TYPE_UNSUPPORT_FEATURES;
        int pointsType = MLFaceAnalyzerSetting.TYPE_UNSUPPORT_KEYPOINTS;
        int shapeType = MLFaceAnalyzerSetting.TYPE_UNSUPPORT_SHAPES;
        // Create a face analyzer. You can create an analyzer using the provided customized face detection parameter
        MLFaceAnalyzerSetting options = new MLFaceAnalyzerSetting.Factory()
                .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                .setFeatureType(featureType)
                .setKeyPointType(pointsType)
                .setShapeType(shapeType)
                .setPoseDisabled(false)
                .create();
        this.detector = MLAnalyzerFactory.getInstance().getFaceAnalyzer(options);
        this.isOpenFeatures = isOpenFeatures;
        this.mContext = context;
        this.isOpenDots = isOpenDots;
    }

    public LocalFaceTransactor(MLFaceAnalyzerSetting options, Context context) {
        super(context);
        this.detector = MLAnalyzerFactory.getInstance().getFaceAnalyzer(options);
        this.isOpenFeatures = isOpenFeatures;
        this.mContext = context;
        this.isOpenDots = isOpenDots;
    }

    @Override
    public void stop() {
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(LocalFaceTransactor.TAG, "Exception thrown while trying to close face transactor: " + e.getMessage());
        }
    }

    @Override
    protected Task<List<MLFace>> detectInImage(MLFrame image) {
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d(TAG, "Total HMSFaceProc graphicOverlay start");
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }
        Log.d(TAG, "Total HMSFaceProc hmsMLLocalFaceGraphic start");
        LocalFaceGraphic hmsMLLocalFaceGraphic = new LocalFaceGraphic(graphicOverlay, faces, mContext);
        graphicOverlay.addGraphic(hmsMLLocalFaceGraphic);
        graphicOverlay.postInvalidate();
        if (faces.size() > 0) {
            Log.d(TAG, "width="+originalCameraImage.getWidth()+", height="+originalCameraImage.getHeight());
            Rect face = hmsMLLocalFaceGraphic.translateRect(faces.get(0).getBorder());
            Log.d(TAG, "left="+face.left+", right="+face.right+", top="+face.top+", bottom="+face.bottom);
            if (mPath != null) {
                Log.d(TAG, "mPath != null");
                saveCropFace(originalCameraImage, face);
                mPath = null;
            } else {
                Log.d(TAG, "mPath == null");
            }
        }
        Log.d(TAG, "Total HMSFaceProc graphicOverlay end");
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.d("toby", "Total HMSFaceProc graphicOverlay onFailure");
        Log.e(LocalFaceTransactor.TAG, "Face detection failed: " + e.getMessage());
    }

    private void saveCropFace(Bitmap origin, Rect rect) {
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
        double widthRatio = 1.0 * origin.getWidth() / screenWidth;
        double heightRatio = 1.0 * origin.getHeight() / screenHeight;
        double ratio = Math.min(widthRatio, heightRatio);
        int width = rect.right - rect.left;
        int height = rect.bottom - rect.top;
        int left = Math.max(rect.left-width, 0);
        int top = Math.max(rect.top-height, 0);
        int right = Math.min(rect.right+width, screenWidth) - left;
        int bottom = Math.min(rect.bottom+height, screenHeight) - top;
        Log.d(TAG, "screenWidth="+screenWidth+", widthRatio="+widthRatio+", heightRatio="+heightRatio+", right="+right+", bottom="+bottom);
        Bitmap face =  Bitmap.createBitmap(origin, (int)(left*ratio), (int)(top*ratio),
                (int)(right*ratio), (int)(bottom*ratio));
        saveImage(mPath, face);
    }

    @Override
    public boolean isFaceDetection() {
        return true;
    }

    private static String mPath;
    public void saveFace(String path) {
        mPath = path;
        Log.d(TAG, "mPath="+mPath);
    }

    // 把位图数据保存到指定路径的图片文件
    public static void saveImage(String path, Bitmap bitmap) {
        // 根据指定的文件路径构建文件输出流对象
        try (FileOutputStream fos = new FileOutputStream(path)) {
            // 把位图数据压缩到文件输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
