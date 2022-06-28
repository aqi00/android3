package com.example.chapter19;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter19.adapter.PersonListAdapter;
import com.example.chapter19.dao.PersonDao;
import com.example.chapter19.entity.PersonInfo;
import com.example.chapter19.entity.PersonPortrait;

import java.util.ArrayList;
import java.util.List;

public class WisdomEyeActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private final static String TAG = "WisdomEyeActivity";
    private ListView lv_person; // 声明一个列表视图对象
    private PersonDao personDao; // 声明一个人员的持久化对象
    private List<PersonInfo> mPersonList = new ArrayList<>(); // 人员信息列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wisdom_eye);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("智慧天眼");
        lv_person = findViewById(R.id.lv_person);
        findViewById(R.id.btn_add).setOnClickListener(v -> {
            Intent intent = new Intent(this, PersonEditActivity.class);
            startActivity(intent);
        });
        // 从App实例中获取唯一的人员持久化对象
        personDao = com.example.chapter19.MainApplication.getInstance().getPersonDB().personDao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler(Looper.myLooper()).post(() -> loadPersonList());
    }

    // 加载人员列表
    private void loadPersonList() {
        mPersonList = personDao.queryAllPerson(); // 加载所有人员信息
        for (int i=0; i<mPersonList.size(); i++) {
            PersonInfo person = mPersonList.get(i);
            // 根据人员名称查询该人员的样本头像列表
            List<PersonPortrait> portraitList = personDao.queryPersonPortrait(person.getName(), 0);
            person.setPortraitList(portraitList);
            mPersonList.set(i, person);
        }
        PersonListAdapter adapter = new PersonListAdapter(this, mPersonList);
        lv_person.setAdapter(adapter);
        // 注册列表项的点击监听器，点击时打开人员详情页面
        lv_person.setOnItemClickListener(this);
        // 注册列表项的长按监听器，长按时弹出是否删除对话框
        lv_person.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PersonInfo person = mPersonList.get(position);
        Intent intent = new Intent(this, com.example.chapter19.PersonDetailActivity.class);
        intent.putExtra("person_name", person.getName());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PersonInfo person = mPersonList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("请确认")
                .setMessage("你是否要删除"+person.getName()+"的寻人记录")
                .setPositiveButton("是", (dialog, which) -> {
                    // 确定删除，则删除人员信息及其头像信息
                    personDao.deletePerson(person);
                    personDao.deletePortraitByName(person.getName(), -1);
                    loadPersonList(); // 加载人员列表
                })
                .setNegativeButton("否", null);
        builder.create().show();
        return true;
    }
}