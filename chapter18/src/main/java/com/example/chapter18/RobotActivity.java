package com.example.chapter18;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter18.bean.CityInfo;
import com.example.chapter18.constant.RobotConstant;
import com.example.chapter18.constant.SoundConstant;
import com.example.chapter18.dao.QuestionDao;
import com.example.chapter18.entity.PoemInfo;
import com.example.chapter18.entity.QuestionInfo;
import com.example.chapter18.task.AsrClientEndpoint;
import com.example.chapter18.task.GetAddressTask;
import com.example.chapter18.task.GetCityCodeTask;
import com.example.chapter18.task.GetWeatherTask;
import com.example.chapter18.task.TtsClientEndpoint;
import com.example.chapter18.task.VoiceRecognizeTask;
import com.example.chapter18.util.SoundUtil;
import com.example.chapter18.util.SwitchUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RobotActivity extends AppCompatActivity {
    private final static String TAG = "RobotActivity";
    private TextView tv_question; // 声明一个文本视图对象
    private TextView tv_answer; // 声明一个文本视图对象
    private String mInitQuestion, mInitAnswer; // 初始的问题，初始的答案
    private MediaPlayer mMediaPlayer = new MediaPlayer(); // 媒体播放器
    private LocationManager mLocationMgr; // 声明一个定位管理器对象
    private boolean isLocated = false; // 是否已经定位
    private CityInfo mCityInfo; // 城市信息
    private Map<String, QuestionInfo> mQuestionSystemMap = new HashMap<>(); // 系统自带的问答映射
    private Map<String, QuestionInfo> mQuestionCustomMap = new HashMap<>(); // 用户添加的问答映射
    private List<PoemInfo> mPoemList = new ArrayList<>();
    private QuestionDao questionDao; // 声明一个问答的持久化对象
    private VoiceRecognizeTask mRecognizeTask; // 声明一个原始音频识别线程对象
    private boolean isPlaying = false; // 是否正在播放
    private boolean isComposing = false; // 是否正在合成
    private long mBeginTime; // 语音识别的开始时间
    private Timer mTimer = new Timer(); // 语音识别计时器
    private TimerTask mTimerTask; // 计时任务
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("小小机器人");
        tv_question = findViewById(R.id.tv_question);
        tv_answer = findViewById(R.id.tv_answer);
        mInitQuestion = tv_question.getText().toString();
        mInitAnswer = tv_answer.getText().toString();
        findViewById(R.id.btn_question_list).setOnClickListener(v -> {
            Intent intent = new Intent(this, QuestionListActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_question_edit).setOnClickListener(v -> {
            Intent intent = new Intent(this, QuestionEditActivity.class);
            startActivity(intent);
        });
        SwitchUtil.checkLocationIsOpen(this, "需要打开定位功能才能查看定位信息");
        // 从App实例中获取唯一的问答持久化对象
        questionDao = MainApplication.getInstance().getQuestionDB().questionDao();
        new Thread(() -> {
            RobotConstant.copySampleFiles(this);
            runOnUiThread(() -> playVoice(RobotConstant.SAMPLE_PATHS[0])); // 播放欢迎语音
        }).start(); // 启动线程把资产目录下的欢迎音频文件复制到存储卡
        new Thread(() -> loadAllPoem()).start(); // 启动线程加载所有诗歌
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tv_question.setText(mInitQuestion);
        tv_answer.setText(mInitAnswer);
        playVoice(RobotConstant.SAMPLE_PATHS[0]); // 播放欢迎语音
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(() -> loadAllQuestion()).start(); // 启动线程加载所有问答
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseRobot(); // 释放机器人资源
    }

    // 释放机器人资源
    private void releaseRobot() {
        if (mRecognizeTask != null) {
            new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
        }
        isPlaying = false;
        if (mMediaPlayer.isPlaying()) { // 如果正在播放
            mMediaPlayer.stop(); // 停止播放
        }
        mTimer.cancel(); // 取消计时器
        if (mTimerTask != null) {
            mTimerTask.cancel(); // 取消计时任务
        }
    }

    // 加载所有问答
    private void loadAllQuestion() {
        RobotConstant.initSystemQuestion(this, questionDao); // 初始化系统设定的问题
        List<QuestionInfo> questionList = questionDao.queryAllQuestion(); // 加载所有问答信息
        for (QuestionInfo item : questionList) {
            if (item.getType() == 0) { // 系统问答
                mQuestionSystemMap.put(item.getQuestion(), item);
            } else { // 用户问答
                mQuestionCustomMap.put(item.getQuestion(), item);
            }
        }
        Log.d(TAG, "SystemMap.size()="+mQuestionSystemMap.size()+",CustomMap.size()="+mQuestionCustomMap.size());
    }

    // 加载所有诗歌
    private void loadAllPoem() {
        RobotConstant.initPoetryData(this, questionDao); // 初始化古诗数据
        mPoemList = questionDao.queryAllPoem(); // 加载所有诗歌信息
        Log.d(TAG, "mPoemList.size()="+mPoemList.size());
    }

    // 播放语音
    private void playVoice(String audioPath) {
        Log.d(TAG, "playVoice audioPath="+audioPath);
        if (mTimerTask != null) {
            mTimerTask.cancel(); // 取消计时任务
        }
        if (isPlaying) {
            return;
        }
        isPlaying = true;
        findViewById(R.id.iv_robot).setOnClickListener(null);
        mMediaPlayer.reset(); // 重置媒体播放器
        // 设置媒体播放器的完成监听器
        mMediaPlayer.setOnCompletionListener(mp -> new Thread(() -> onlineRecognize()).start());
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // 设置音频流的类型为音乐
        try {
            mMediaPlayer.setDataSource(audioPath); // 设置媒体数据的文件路径
            mMediaPlayer.prepare(); // 媒体播放器准备就绪
            mMediaPlayer.start(); // 媒体播放器开始播放
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 在线识别实时语音
    private void onlineRecognize() {
        // 创建语音识别任务，并指定语音监听器
        AsrClientEndpoint asrTask = new AsrClientEndpoint(this, "",
                arg -> checkRecognize((boolean)arg[0], arg[2].toString()));
        SoundUtil.startSoundTask(SoundConstant.URL_ASR, asrTask); // 启动语音识别任务
        // 创建一个原始音频识别线程
        mRecognizeTask = new VoiceRecognizeTask(this, asrTask);
        mRecognizeTask.start(); // 启动原始音频识别线程
        isPlaying = false;
        findViewById(R.id.iv_robot).setOnClickListener(v -> {
            new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
            playVoice(RobotConstant.SAMPLE_PATHS[2]); // 播放样本音频
        });
        mBeginTime = System.currentTimeMillis();
        mTimer = new Timer(); // 创建一个录音计时器
        // 创建一个录音计时任务
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Log.d(TAG, "interval="+(now - mBeginTime));
                if (now - mBeginTime > 10 * 1000) { // 超过10秒，则停止本次识别，重新开始识别
                    mTimer.cancel(); // 取消计时器
                    new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
                    runOnUiThread(() -> tv_answer.setText("我的回答是："+RobotConstant.SYSTEM_ANSWERS[6]));
                    playVoice(RobotConstant.SAMPLE_PATHS[6]); // 播放样本音频
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000); // 计时器每隔一秒就检查识别语音是否超时了
    }

    // 检查已识别的文本是否为已设定的问题
    private void checkRecognize(boolean isEnd, String text) {
        tv_question.setText("您的问题是："+text);
        text = text.replace("，", "");
        for (Map.Entry<String, QuestionInfo> item : mQuestionCustomMap.entrySet()) {
            if (text.contains(item.getKey())) { // 匹配用户添加的问题
                if (!isEnd) { // 尚未结束识别
                    new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
                } else { // 已经结束识别
                    answerCustomQuestion(item.getValue()); // 回答用户问题
                }
                return;
            }
        }
        for (Map.Entry<String, QuestionInfo> item : mQuestionSystemMap.entrySet()) {
            if (text.matches(item.getKey())) { // 匹配系统自带的问题
                if (!isEnd) { // 尚未结束识别
                    new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
                } else { // 已经结束识别
                    answerSystemQuestion(text, item.getValue()); // 回答系统问题
                }
                return;
            }
        }
        if (text.matches(RobotConstant.RECITE_PATTERN)) { // 匹配诗歌背诵
            String[] recites = text.split(RobotConstant.RECITE_PART);
            if (recites.length < 2) {
                return;
            }
            Log.d(TAG, "content="+recites[1]);
            if (isEnd) { // 已经结束识别
                if (mPoemPos == -1) {
                    mPoemPos = RobotConstant.searchPoemPos(recites[1], mPoemPos, mPoemList);
                }
                if (mPoemPos != -1) {
                    readPoem(mPoemList.get(mPoemPos)); // // 朗读诗歌
                }
                return;
            }
            // 查找该诗在列表中的位置
            int poemPos = RobotConstant.searchPoemPos(recites[1], mPoemPos, mPoemList);
            if (poemPos > mPoemPos) {
                mTimerTask.cancel(); // 取消计时任务
                if (mPoemPos == -1) {
                    mHandler.postDelayed(mHasFindPoem, 2000);
                }
                mPoemPos = poemPos;
            }
        }
    }

    private int mPoemPos = -1; // 诗歌在列表中的位置
    private Runnable mHasFindPoem = () -> {
        new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
    };

    // 朗读诗歌
    private void readPoem(PoemInfo poem) {
        mTimerTask.cancel(); // 取消计时任务
        mPoemPos = -1;
        String poemContent = poem.getContent().replace("\\n", "\n");
        String showText = String.format("%s\n%s\n%s", poem.getTitle(), poem.getAuthor(), poemContent);
        String answer = String.format("%s，%s。%s", poem.getTitle(), poem.getAuthor(), poemContent);
        Log.d(TAG, "answerCustomQuestion answer=" + answer);
        new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
        runOnUiThread(() -> tv_answer.setText(showText));
        // 启动在线合成语音的线程
        new Thread(() -> onlineCompose(poem.getAuthor()+"_"+poem.getTitle(), -1, answer, "")).start();
    }

    // 回答系统问题
    private void answerSystemQuestion(String question, QuestionInfo questionInfo) {
        mTimerTask.cancel(); // 取消计时任务
        String answer = questionInfo.getAnswer();
        Log.d(TAG, "answerSystemQuestion answer="+answer);
        new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
        Object[] resultArray = RobotConstant.judgeAnswerResult(question, answer, mCityInfo);
        int voiceSeq = (int) resultArray[0];
        String tempText = (String) resultArray[1];
        String answerText = TextUtils.isEmpty(tempText) ? RobotConstant.SYSTEM_ANSWERS[voiceSeq] : tempText;
        runOnUiThread(() -> tv_answer.setText("我的回答是："+answerText));
        String voicePath = voiceSeq==-1 ? "" : RobotConstant.SAMPLE_PATHS[voiceSeq];
        Log.d(TAG, "voiceSeq="+voiceSeq+",answerText="+answerText+",voicePath="+voicePath);
        // 启动在线合成语音的线程
        new Thread(() -> onlineCompose(answer, voiceSeq, answerText, voicePath)).start();
    }

    // 回答用户问题
    private void answerCustomQuestion(QuestionInfo questionInfo) {
        mTimerTask.cancel(); // 取消计时任务
        String answer = questionInfo.getAnswer();
        Log.d(TAG, "answerCustomQuestion answer=" + answer);
        new Thread(() -> mRecognizeTask.cancel()).start(); // 启动取消识别语音的线程
        runOnUiThread(() -> tv_answer.setText("我的回答是："+answer));
        // 启动在线合成语音的线程
        new Thread(() -> onlineCompose(""+questionInfo.getId(), -1, answer, "")).start();
    }

    // 在线合成语音
    private void onlineCompose(String function, int seq, String text, String path) {
        Log.d(TAG, "onlineCompose function="+function+",text="+text);
        String voicePath = !TextUtils.isEmpty(path) ? path : String.format("%s/robot/%s.mp3",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), function);
        File file = new File(voicePath);
        if (file.exists() && seq==-1) { // 不是样本音频，如果已经存在语音文件，就要删除文件，这样才能重新生成新的语音文件
            file.delete();
        }
        if (file.exists()) {
            playVoice(voicePath); // 播放语音
        } else if (!isComposing) {
            isComposing = true;
            // 创建语音合成任务，并指定语音监听器
            TtsClientEndpoint task = new TtsClientEndpoint(this, voicePath, text, arg -> {
                if (Boolean.TRUE.equals(arg[0])) {
                    playVoice(voicePath); // 播放语音
                    isComposing = false;
                }
            });
            SoundUtil.startSoundTask(SoundConstant.URL_TTS, task); // 启动语音合成任务
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCityInfo == null) {
            initLocation(); // 初始化定位服务
        }
    }

    // 初始化定位服务
    private void initLocation() {
        Log.d(TAG, "initLocation");
        // 从系统服务中获取定位管理器
        mLocationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria(); // 创建一个定位准则对象
        // 设置定位精确度。Criteria.ACCURACY_COARSE表示粗略，Criteria.ACCURACY_FIN表示精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true); // 设置是否需要海拔信息
        criteria.setBearingRequired(true); // 设置是否需要方位信息
        criteria.setCostAllowed(true); // 设置是否允许运营商收费
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 设置对电源的需求
        // 获取定位管理器的最佳定位提供者
        String bestProvider = mLocationMgr.getBestProvider(criteria, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 实测发现部分手机的android11系统使用卫星定位会没返回
            bestProvider = "network";
        }
        if (mLocationMgr.isProviderEnabled(bestProvider)) { // 定位提供者当前可用
            beginLocation(bestProvider); // 开始定位
        }
        Log.d(TAG, "isProviderEnabled="+mLocationMgr.isProviderEnabled(bestProvider));
    }

    // 开始定位
    private void beginLocation(String method) {
        // 检查当前设备是否已经开启了定位功能
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请授予定位权限并开启定位功能", Toast.LENGTH_SHORT).show();
            return;
        }
        // 设置定位管理器的位置变更监听器
        mLocationMgr.requestLocationUpdates(method, 300, 0, mLocationListener);
        // 获取最后一次成功定位的位置信息
        Location location = mLocationMgr.getLastKnownLocation(method);
        getAddress(location); // 获取详细地址
    }

    // 获取详细地址
    private void getAddress(Location location) {
        if (location != null) {
            // 创建一个根据经纬度查询详细地址的任务
            GetAddressTask task = new GetAddressTask(this, location, cityInfo -> getCityCode(cityInfo));
            task.start(); // 启动地址查询任务
        }
    }

    // 获取城市代码
    private void getCityCode(CityInfo cityInfo) {
        isLocated = true;
        mCityInfo = cityInfo;
        // 创建一个查询城市编码的任务
        GetCityCodeTask task = new GetCityCodeTask(this, cityInfo, cityCode -> getWeather(cityCode));
        task.start(); // 启动城市编码查询任务
    }

    // 获取天气信息
    private void getWeather(String city_code) {
        mCityInfo.city_code = city_code;
        // 创建一个查询城市天气的任务
        GetWeatherTask task = new GetWeatherTask(this, city_code, weatherInfo -> {
            mCityInfo.weather_info = weatherInfo;
        });
        task.start(); // 启动城市天气查询任务
    }

    // 定义一个位置变更监听器
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (!isLocated) {
                getAddress(location); // 获取详细地址
            }
        }

        @Override
        public void onProviderDisabled(String arg0) {}

        @Override
        public void onProviderEnabled(String arg0) {}

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationMgr != null) {
            mLocationMgr.removeUpdates(mLocationListener); // 移除定位管理器的位置变更监听器
        }
        releaseRobot(); // 释放机器人资源
        mMediaPlayer.release(); // 释放媒体播放器
    }

}