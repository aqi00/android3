package com.example.chapter18;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter18.bean.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpeechComposeActivity extends AppCompatActivity {
    private final static String TAG = "SpeechComposeActivity";
    private TextToSpeech mSpeech; // 声明一个文字转语音对象
    private EditText et_tts; // 声明一个编辑框对象
    private List<TextToSpeech.EngineInfo> mEngineList; // 语音引擎列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_compose);
        et_tts = findViewById(R.id.et_tts);
        findViewById(R.id.btn_read).setOnClickListener(v -> {
            String content = et_tts.getText().toString();
            // 开始朗读指定文本
            int result = mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, null);
            String desc = String.format("朗读%s", result==TextToSpeech.SUCCESS?"成功":"失败");
            Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
        });
        // 创建一个文字转语音对象，初始化结果在监听器的onInit方法中返回
        mSpeech = new TextToSpeech(this, mListener);
    }

    // 创建一个文字转语音的初始化监听器实例
    private TextToSpeech.OnInitListener mListener = status -> {
        if (status == TextToSpeech.SUCCESS) { // 初始化成功
            if (mEngineList == null) { // 首次初始化
                mEngineList = mSpeech.getEngines(); // 获取系统支持的所有语音引擎
                initEngineSpinner(); // 初始化语音引擎下拉框
            }
            initLanguageSpinner(); // 初始化语言下拉框
        }
    };

    // 初始化语音引擎下拉框
    private void initEngineSpinner() {
        String[] engineArray = new String[mEngineList.size()];
        for(int i=0; i<mEngineList.size(); i++) {
            engineArray[i] = mEngineList.get(i).label;
        }
        ArrayAdapter<String> engineAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, engineArray);
        Spinner sp_engine = findViewById(R.id.sp_engine);
        sp_engine.setPrompt("请选择语音引擎");
        sp_engine.setAdapter(engineAdapter);
        sp_engine.setOnItemSelectedListener(new EngineSelectedListener());
        sp_engine.setSelection(0);
    }

    private class EngineSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            recycleSpeech(); // 回收文字转语音对象
            // 创建指定语音引擎的文字转语音对象
            mSpeech = new TextToSpeech(SpeechComposeActivity.this, mListener,
                    mEngineList.get(arg2).name);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 回收文字转语音对象
    private void recycleSpeech() {
        if (mSpeech != null) {
            mSpeech.stop(); // 停止文字转语音
            mSpeech.shutdown(); // 关闭文字转语音
            mSpeech = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleSpeech(); // 回收文字转语音对象
    }

    private String[] mLanguageArray = {"中文普通话", "英语", "法语", "德语", "意大利语"};
    private Locale[] mLocaleArray = { Locale.CHINA, Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN, Locale.ITALIAN };
    private String[] mValidLanguageArray; // 当前引擎支持的语言名称数组
    private Locale[] mValidLocaleArray; // 当前引擎支持的语言类型数组
    private String mTextCN = "离离原上草，一岁一枯荣。野火烧不尽，春风吹又生。";
    private String mTextEN = "Hello World. Nice to meet you. This is a TTS demo.";

    // 初始化语言下拉框
    private void initLanguageSpinner() {
        List<Language> languageList = new ArrayList<>();
        // 下面遍历语言数组，从中挑选出当前引擎所支持的语言列表
        for (int i=0; i<mLanguageArray.length; i++) {
            // 设置朗读语言。通过检查方法的返回值，判断引擎是否支持该语言
            int result = mSpeech.setLanguage(mLocaleArray[i]);
            Log.d(TAG, "language="+mLanguageArray[i]+",result="+result);
            if (result != TextToSpeech.LANG_MISSING_DATA
                    && result != TextToSpeech.LANG_NOT_SUPPORTED) { // 语言可用
                languageList.add(new Language(mLanguageArray[i], mLocaleArray[i]));
            }
        }
        mValidLanguageArray = new String[languageList.size()];
        mValidLocaleArray = new Locale[languageList.size()];
        for(int i=0; i<languageList.size(); i++) {
            mValidLanguageArray[i] = languageList.get(i).name;
            mValidLocaleArray[i] = languageList.get(i).locale;
        }
        // 下面初始化语言下拉框
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, mValidLanguageArray);
        Spinner sp_language = findViewById(R.id.sp_language);
        sp_language.setPrompt("请选择朗读语言");
        sp_language.setAdapter(languageAdapter);
        sp_language.setOnItemSelectedListener(new LanguageSelectedListener());
        sp_language.setSelection(0);
    }

    private class LanguageSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (mValidLocaleArray[arg2]==Locale.CHINA) { // 汉语
                et_tts.setText(mTextCN);
            } else { // 其他语言
                et_tts.setText(mTextEN);
            }
            mSpeech.setLanguage(mValidLocaleArray[arg2]); // 设置选中的朗读语言
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}