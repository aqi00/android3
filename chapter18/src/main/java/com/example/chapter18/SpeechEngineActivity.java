package com.example.chapter18;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter18.adapter.LanguageListAdapter;
import com.example.chapter18.bean.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpeechEngineActivity extends AppCompatActivity {
    private final static String TAG = "SpeechEngineActivity";
    private TextToSpeech mSpeech; // 声明一个文字转语音对象
    private TextView tv_init; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_engine);
        tv_init = findViewById(R.id.tv_init);
        findViewById(R.id.btn_jump_setting).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("com.android.settings.TTS_SETTINGS");
            startActivity(intent);
        });
        // 创建一个文字转语音对象，初始化结果在监听器TTSListener中返回
        mSpeech = new TextToSpeech(this, new TTSListener());
    }

    private List<TextToSpeech.EngineInfo> mEngineList; // 语音引擎列表
    // 定义一个文字转语音的初始化监听器
    private class TTSListener implements TextToSpeech.OnInitListener {
        // 在初始化完成时触发
        @Override
        public void onInit(int status) {
            tv_init.setText("系统语音引擎初始化"+((status==TextToSpeech.SUCCESS)?"成功":"失败"));
            if (status == TextToSpeech.SUCCESS) { // 初始化成功
                if (mEngineList == null) { // 首次初始化
                    // 获取系统支持的所有语音引擎
                    mEngineList = mSpeech.getEngines();
                    initEngineSpinner(); // 初始化语音引擎下拉框
                }
                initLanguageList(); // 初始化语言列表
            }
        }
    }

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
            //recycleSpeech(); // 回收文字转语音对象
            // 创建指定语音引擎的文字转语音对象
            mSpeech = new TextToSpeech(SpeechEngineActivity.this, new TTSListener(),
                    mEngineList.get(arg2).name);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    private String[] mLanguageArray = {"中文普通话", "英语", "法语", "德语", "意大利语",  };
    private Locale[] mLocaleArray = { Locale.CHINA, Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN, Locale.ITALIAN };

    // 初始化语言列表
    private void initLanguageList() {
        List<Language> languageList = new ArrayList<>();
        // 下面遍历语言数组，从中挑选出当前引擎所支持的语言列表
        for (int i=0; i<mLanguageArray.length; i++) {
            String desc = "正常使用";
            // 设置朗读语言
            int result = mSpeech.setLanguage(mLocaleArray[i]);
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                desc = "缺少数据";
            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                desc = "暂不支持";
            }
            languageList.add(new Language(mLanguageArray[i], desc));
        }
        // 下面把该引擎对各语言的支持情况展示到列表视图上
        ListView lv_language = findViewById(R.id.lv_language);
        LanguageListAdapter adapter = new LanguageListAdapter(this, languageList);
        lv_language.setAdapter(adapter);
    }

}