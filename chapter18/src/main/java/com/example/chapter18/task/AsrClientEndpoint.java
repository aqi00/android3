package com.example.chapter18.task;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import javax.websocket.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

@ClientEndpoint
public class AsrClientEndpoint {
    private final static String TAG = "AsrClientEndpoint";
    private Activity mAct; // 声明一个活动实例
    private String mFileName; // 语音文件名称
    private VoiceListener mListener; // 语音监听器
    private Session mSession; // 连接会话

    public AsrClientEndpoint(Activity act, String fileName, VoiceListener listener) {
        mAct = act;
        mFileName = fileName;
        mListener = listener;
    }

    @OnOpen
    public void onOpen(final Session session) {
        mSession = session;
        Log.d(TAG, "->创建连接成功");
        try {
            // 组装请求开始的json报文
            JSONObject frame = new JSONObject();
            frame.put("type", "start");
            JSONObject data = new JSONObject();
            frame.put("data", data);
            data.put("domain", "general"); // 领域。general(通用)，law(司法)，technology(科技)，medical(医疗)
            data.put("lang", "cn"); // 语言。cn(中文普通话)、en(英语)
            data.put("format", "pcm"); // 音频格式。支持mp3和pcm
            data.put("sample", "16k"); // 采样率。16k,8k
            data.put("variable", "true"); // 是否可变结果
            data.put("punctuation", "true"); // 是否开启标点
            data.put("post_proc", "true"); // 是否开启数字转换
            data.put("acoustic_setting", "near"); // 音响设置。near近讲，far远讲
            data.put("server_vad", "false"); // 智能断句
            data.put("max_start_silence", "1000"); // 智能断句前静音
            data.put("max_end_silence", "500"); // 智能断句尾静音
            // 发送开始请求
            session.getBasicRemote().sendText(frame.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 文件名非空，表示从音频文件中识别文本
        if (!TextUtils.isEmpty(mFileName)) {
            new Thread(() -> sendAudioData(session)).start();
        }
    }

    // 发送音频文件的语音数据
    private void sendAudioData(final Session session) {
        try (InputStream is = new FileInputStream(mFileName)) {
            byte[] audioData = new byte[9600];
            int length = 0;
            while ((length = is.read(audioData)) != -1) {
                Log.d(TAG, "发送语音数据 length="+length);
                ByteBuffer buffer = ByteBuffer.wrap(audioData, 0, length);
                session.getAsyncRemote().sendBinary(buffer);
                Thread.sleep(200); // 模拟采集音频休眠
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopAsr(); // 停止语音识别
    }

    // 发送实时语音数据
    public synchronized void sendRealtimeAudio(int seq, byte[] data, int length) {
        if (mSession!=null && mSession.isOpen()) {
            Log.d(TAG, "发送语音数据 seq="+seq+",length="+length);
            ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
            mSession.getAsyncRemote().sendBinary(buffer);
        }
    }

    // 停止语音识别
    public void stopAsr() {
        try {
            // 组装请求结束的json报文
            JSONObject frame = new JSONObject();
            frame.put("type", "end");
            if (mSession!=null && mSession.isOpen()) {
                // 发送结束请求
                mSession.getBasicRemote().sendText(frame.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void processMessage(Session session, String message) {
        Log.d(TAG, "服务端返回：" + message);
        try {
            JSONObject jsonObject = new JSONObject(message);
            boolean end = jsonObject.getBoolean("end"); // 是否结束识别
            int code = jsonObject.getInt("code"); // 处理结果
            String msg = jsonObject.getString("msg"); // 结果说明
            if (code != 0) {
                Log.d(TAG, "错误码：" + code + "，错误描述：" + msg);
                return;
            }
            String text = jsonObject.getString("text");
            mAct.runOnUiThread(() -> mListener.voiceDealEnd(end, msg, text));
            if (end) {
                Log.d(TAG, mFileName + "识别结束");
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
}
