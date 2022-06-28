package com.example.chapter18;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter18.dao.QuestionDao;
import com.example.chapter18.entity.QuestionInfo;

import java.io.File;

public class QuestionEditActivity extends AppCompatActivity {
    private final static String TAG = "QuestionEditActivity";
    private TextView tv_option; // 声明一个文本视图对象
    private EditText et_question; // 声明一个编辑框对象
    private EditText et_answer; // 声明一个编辑框对象
    private LinearLayout ll_view; // 声明一个线性视图对象
    private TextView tv_question; // 声明一个文本视图对象
    private TextView tv_answer; // 声明一个文本视图对象
    private int mQuestionId; // 问答编号
    private boolean isEditing = false; // 是否为编辑状态
    private QuestionDao questionDao; // 声明一个问答的持久化对象
    private QuestionInfo mQuestionInfo; // 问答信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);
        mQuestionId = getIntent().getIntExtra("id", -1);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_option = findViewById(R.id.tv_option);
        et_question = findViewById(R.id.et_question);
        et_answer = findViewById(R.id.et_answer);
        ll_view = findViewById(R.id.ll_view);
        tv_question = findViewById(R.id.tv_question);
        tv_answer = findViewById(R.id.tv_answer);
        tv_option.setOnClickListener(v -> {
            if (mQuestionId==-1 || isEditing) { // 添加或者修改
                saveQuestion(); // 保存问答信息
            } else { // 查看详情
                tv_option.setText("保存");
                ll_view.setVisibility(View.GONE);
            }
            isEditing = !isEditing;
        });
        // 从App实例中获取唯一的问答持久化对象
        questionDao = MainApplication.getInstance().getQuestionDB().questionDao();
        if (mQuestionId == -1) { // 添加新问答
            tv_title.setText("添加问答");
            tv_option.setText("保存");
        } else { // 查看问答详情
            tv_title.setText("问答详情");
            tv_option.setText("编辑");
            showQuestion(); // 显示问答详情
        }
    }

    // 显示问答详情
    private void showQuestion() {
        // 根据编号加载问答信息
        mQuestionInfo = questionDao.queryQuestionById(mQuestionId);
        et_question.setText(mQuestionInfo.getQuestion());
        et_answer.setText(mQuestionInfo.getAnswer());
        tv_question.setText(mQuestionInfo.getQuestion());
        tv_answer.setText(mQuestionInfo.getAnswer());
        ll_view.setVisibility(View.VISIBLE);
    }

    // 保存问答信息
    private void saveQuestion() {
        String question = et_question.getText().toString();
        String answer = et_answer.getText().toString();
        if (TextUtils.isEmpty(question)) {
            Toast.makeText(this, "请先输入问题描述", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(answer)) {
            Toast.makeText(this, "请先输入回答内容", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mQuestionId == -1) { // 添加来源
            mQuestionInfo = new QuestionInfo(question, answer, 1);
            questionDao.insertOneQuestion(mQuestionInfo); // 插入一条问答信息
        } else { // 查看来源
            mQuestionInfo.setQuestion(question);
            mQuestionInfo.setAnswer(answer);
            questionDao.updateQuestion(mQuestionInfo); // 更新问答信息
            String voicePath = String.format("%s/robot/%s.mp3",
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                    mQuestionInfo.getId()+"");
            File voiceFile = new File(voicePath);
            if (voiceFile.exists()) { // 如果已经存在该问答的语音文件，就要删除原文件，这样下次才会重新合成新的语音文件
                voiceFile.delete();
            }
        }
        Toast.makeText(this, "成功保存问答信息", Toast.LENGTH_SHORT).show();
        finish(); // 关闭当前页面
    }
}