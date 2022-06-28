package com.example.chapter18;

import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter18.constant.SoundConstant;
import com.example.chapter18.task.AsrClientEndpoint;
import com.example.chapter18.task.VoicePlayTask;
import com.example.chapter18.task.VoiceRecognizeTask;
import com.example.chapter18.util.AssetsUtil;
import com.example.chapter18.util.SoundUtil;

public class VoiceRecognizeActivity extends AppCompatActivity {
    private final static String TAG = "VoiceRecognizeActivity";
    private String SAMPLE_FILE = "sample/spring.pcm"; // 样本音频名称
    private TextView tv_recognize_text; // 声明一个文本视图对象
    private Button btn_recognize; // 声明一个按钮对象
    private String mSamplePath; // 样本音频的文件路径
    private boolean isRecognizing = false; // 是否正在识别
    private VoiceRecognizeTask mRecognizeTask; // 声明一个原始音频识别线程对象

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognize);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("在线语音识别");
        TextView tv_option = findViewById(R.id.tv_option);
        tv_option.setText("识别样本音频");
        tv_recognize_text = findViewById(R.id.tv_recognize_text);
        btn_recognize = findViewById(R.id.btn_recognize);
        btn_recognize.setOnClickListener(v -> {
            if (!isRecognizing) { // 未在识别
                btn_recognize.setText("停止实时识别");
                new Thread(() -> onlineRecognize("")).start(); // 启动在线识别语音的线程
            } else { // 正在识别
                btn_recognize.setText("开始实时识别");
                new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
            }
            isRecognizing = !isRecognizing;
        });
        tv_option.setOnClickListener(v -> {
            new Thread(() -> onlineRecognize(mSamplePath)).start(); // 启动在线识别语音的线程
        });
        mSamplePath = String.format("%s/%s",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), SAMPLE_FILE);
        // 把资产目录下的样本音频文件复制到存储卡
        new Thread(() -> AssetsUtil.Assets2Sd(this, SAMPLE_FILE, mSamplePath)).start();
    }

    // 在线识别音频文件（文件路径为空的话，表示识别实时语音）
    private void onlineRecognize(String filePath) {
        runOnUiThread(() -> {
            tv_recognize_text.setText("");
            Toast.makeText(this, "开始识别语音", Toast.LENGTH_SHORT).show();
        });
        // 创建语音识别任务，并指定语音监听器
        AsrClientEndpoint asrTask = new AsrClientEndpoint(this, filePath, arg -> {
            Log.d(TAG, "arg[0]="+arg[0]+",arg[2]="+arg[2]);
            tv_recognize_text.setText(arg[2].toString());
            if (Boolean.TRUE.equals(arg[0])) {
                Toast.makeText(this, "语音识别结束", Toast.LENGTH_SHORT).show();
            }
        });
        SoundUtil.startSoundTask(SoundConstant.URL_ASR, asrTask); // 启动语音识别任务
        if (TextUtils.isEmpty(filePath)) { // 文件路径为空，表示识别实时语音
            // 创建一个原始音频识别线程
            mRecognizeTask = new VoiceRecognizeTask(this, asrTask);
            mRecognizeTask.start(); // 启动原始音频识别线程
        } else { // 文件路径非空，表示识别音频文件
            int[] params = new int[] {16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT};
            // 创建一个原始音频播放线程
            VoicePlayTask playTask = new VoicePlayTask(this, filePath, params);
            playTask.start(); // 启动原始音频播放线程
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecognizeTask != null) {
            new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
        }
    }

}
