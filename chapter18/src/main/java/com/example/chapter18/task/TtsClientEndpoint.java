package com.example.chapter18.task;

import android.app.Activity;
import android.util.Log;

import com.example.chapter18.util.FileUtil;

import org.json.JSONObject;

import javax.websocket.*;

@ClientEndpoint
public class TtsClientEndpoint {
    private final static String TAG = "TtsClientEndpoint";
    private Activity mAct; // 声明一个活动实例
    private String mFileName; // 语音文件名称
    private VoiceListener mListener; // 语音监听器
    private String mText; // 待转换的文本
    private long mStartTime; // 语音合成的开始时间

    public TtsClientEndpoint(Activity act, String fileName, String text, VoiceListener listener) {
        mAct = act;
        mFileName = fileName;
        mText = text;
        mListener = listener;
    }

    @OnOpen
    public void onOpen(Session session) {
        mStartTime = System.currentTimeMillis();
        Log.d(TAG,  "->创建连接成功 text="+mText);
        try {
            // 组装语音合成的json报文
            JSONObject frame = new JSONObject();
            frame.put("format", "mp3"); // 音频格式。支持mp3和pcm
            frame.put("sample", "16000"); // 采样率（单位Hz）。8000,16000,24000
            frame.put("vcn", "kiyo-plus"); // 发音人。kiyo-plus为可爱女生
            frame.put("speed", 30); // 语速
            frame.put("volume", 50); // 音量
            frame.put("pitch", 50); // 音高
            frame.put("bright", 50); // 亮度
            frame.put("text", mText); // 需要合成的文本
            frame.put("user_id", "unisound-home"); // 用户标识
            // 发送语音合成请求
            session.getBasicRemote().sendText(frame.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessgae(Session session, byte[] data) {
        Log.d(TAG,  "====binary=====响应时间：" + (System.currentTimeMillis() - mStartTime) + ";返回字节大小=" + data.length);
        mStartTime = System.currentTimeMillis();
        FileUtil.appendBytesToFile(mFileName, data); // 把音频数据追加至存储卡文件
    }

    @OnMessage
    public void processMessage(Session session, String message) {
        Log.d(TAG,  "服务端返回：" + message);
        try {
            JSONObject jsonObject = new JSONObject(message);
            boolean end = jsonObject.getBoolean("end"); // 是否结束合成
            int code = jsonObject.getInt("code"); // 处理结果
            String msg = jsonObject.getString("msg"); // 结果说明
            if (code != 0) {
                Log.d(TAG, "错误码：" + code + "，错误描述：" + msg);
                return;
            }
            mAct.runOnUiThread(() -> mListener.voiceDealEnd(end, msg, mFileName));
            if (end) {
                Log.d(TAG, mFileName + "合成结束");
                session.close(); // 关闭连接会话
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        Log.d(TAG,  "->连接关闭");
    }
}

