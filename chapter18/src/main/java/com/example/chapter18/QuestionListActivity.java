package com.example.chapter18;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chapter18.adapter.QuestionListAdapter;
import com.example.chapter18.dao.QuestionDao;
import com.example.chapter18.entity.QuestionInfo;

import java.util.ArrayList;
import java.util.List;

public class QuestionListActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private final static String TAG = "QuestionListActivity";
    private ListView lv_question; // 声明一个列表视图对象
    private QuestionDao questionDao; // 声明一个问答的持久化对象
    private List<QuestionInfo> mQuestionList = new ArrayList<>(); // 问答列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("问答列表");
        TextView tv_option = findViewById(R.id.tv_option);
        tv_option.setText("添加新问答");
        tv_option.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuestionEditActivity.class);
            startActivity(intent);
        });
        lv_question = findViewById(R.id.lv_question);
        // 从App实例中获取唯一的问答持久化对象
        questionDao = MainApplication.getInstance().getQuestionDB().questionDao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 打开该页面，或者从添加页面返回该页面，都要重新加载问答列表
        new Handler(Looper.myLooper()).post(() -> loadQuestionList());
    }

    // 加载问答列表
    private void loadQuestionList() {
        mQuestionList = questionDao.queryAllCustomQuestion(); // 加载所有问答信息
        QuestionListAdapter adapter = new QuestionListAdapter(this, mQuestionList);
        lv_question.setAdapter(adapter);
        lv_question.setOnItemClickListener(this); // 单击问答要跳到该问答的详情页
        lv_question.setOnItemLongClickListener(this); // 长按问答要弹出是否删除的对话框
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        QuestionInfo question = mQuestionList.get(position);
        Intent intent = new Intent(this, QuestionEditActivity.class);
        intent.putExtra("id", question.getId());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        QuestionInfo question = mQuestionList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("请确认")
                .setMessage("你是否要删除该问题："+question.getQuestion())
                .setPositiveButton("是", (dialog, which) -> {
                    questionDao.deleteQuestion(question); // 删除问答信息
                    loadQuestionList(); // 加载问答列表
                })
                .setNegativeButton("否", null);
        builder.create().show();
        return true;
    }
}