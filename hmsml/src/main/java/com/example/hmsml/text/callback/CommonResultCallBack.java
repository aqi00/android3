package com.example.hmsml.text.callback;

import android.graphics.Bitmap;

import com.example.hmsml.text.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.document.MLDocument;
import com.huawei.hms.mlsdk.text.MLText;

public class CommonResultCallBack implements CouldInfoResultCallBack {
    private final TextListener mListener;
    public CommonResultCallBack(TextListener listener) {
        mListener = listener;
    }

    @Override
    public void onSuccessForText(Bitmap originalCameraImage, MLText text, GraphicOverlay graphicOverlay) {
        if (text == null) {
            mListener.onSuccess(null);
        } else {
            mListener.onSuccess(text.getStringValue());
        }
    }

    @Override
    public void onSuccessForDoc(Bitmap originalCameraImage, MLDocument text, GraphicOverlay graphicOverlay) {
        if (text == null) {
            mListener.onSuccess(null);
        } else {
            mListener.onSuccess(text.getStringValue());
        }
    }
}
