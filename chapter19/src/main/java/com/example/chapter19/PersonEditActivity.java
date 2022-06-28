package com.example.chapter19;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter19.adapter.PortraitRecyclerAdapter;
import com.example.chapter19.dao.PersonDao;
import com.example.chapter19.entity.PersonInfo;
import com.example.chapter19.entity.PersonPortrait;
import com.example.chapter19.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

public class PersonEditActivity extends AppCompatActivity {
    private final static String TAG = "PersonEditActivity";
    private int CHOOSE_CODE = 3; // 只在相册挑选图片的请求码
    private int CUT_CODE = 12; // 抠图的请求码
    private EditText et_name; // 声明一个编辑框对象
    private EditText et_info; // 声明一个编辑框对象
    private RecyclerView rv_sample; // 声明一个循环视图对象
    private PersonDao personDao; // 声明一个人员的持久化对象
    private List<PersonPortrait> portraitList = new ArrayList<>(); // 人员头像列表
    private PortraitRecyclerAdapter mAdapter; // 人员头像的循环适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);
        initView(); // 初始化视图
        initData(); // 初始化数据
    }

    // 初始化视图
    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("添加人员");
        et_name = findViewById(R.id.et_name);
        et_info = findViewById(R.id.et_info);
        rv_sample = findViewById(R.id.rv_sample);
        // 创建一个水平方向的线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rv_sample.setLayoutManager(manager); // 设置循环视图的布局管理器
        findViewById(R.id.btn_save).setOnClickListener(v -> savePerson());
    }

    // 初始化数据
    private void initData() {
        // 从App实例中获取唯一的人员持久化对象
        personDao = com.example.chapter19.MainApplication.getInstance().getPersonDB().personDao();
        portraitList.add(new PersonPortrait("", "", -1)); // -1表示一个加号图片
        mAdapter = new PortraitRecyclerAdapter(this, portraitList);
        rv_sample.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(position -> {
            // 创建一个内容获取动作的意图（准备跳到系统相册）
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // 是否允许多选
            albumIntent.setType("image/*"); // 类型为图像
            startActivityForResult(albumIntent, CHOOSE_CODE); // 打开系统相册
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) { // 从相册返回
            if (requestCode == CHOOSE_CODE && intent.getData() != null) { // 从相册选择一张照片
                Uri uri = intent.getData(); // 获得已选择照片的路径对象
                // 根据指定图片的uri，获得自动缩小后的图片路径
                String image_path = BitmapUtil.getAutoZoomPath(this, uri);
                // 下面跳到头像裁剪页面
                Intent cutIntent = new Intent(this, com.example.chapter19.PersonCutActivity.class);
                cutIntent.putExtra("image_path", image_path);
                startActivityForResult(cutIntent, CUT_CODE);
            } else if (requestCode == CUT_CODE) { // 从头像裁剪页面返回
                String face_path = intent.getStringExtra("face_path");
                PersonPortrait portrait = new PersonPortrait("", face_path, 0);
                portraitList.add(portraitList.size()-1, portrait);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    // 保存人员信息
    private void savePerson() {
        String name = et_name.getText().toString();
        String info = et_info.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入人员姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(info)) {
            Toast.makeText(this, "请输入人员信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (portraitList.size() <= 1) {
            Toast.makeText(this, "请选择人员头像", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonInfo person = new PersonInfo(name, info);
        personDao.insertOnePerson(person); // 插入一条人员信息
        portraitList.remove(portraitList.size()-1); // 去掉末尾的加号图片
        for (int i=0; i<=portraitList.size()-1; i++) {
            PersonPortrait portrait = portraitList.get(i);
            portrait.setName(name); // 设置头像信息的人员名称
            portraitList.set(i, portrait);
        }
        personDao.insertPortraitList(portraitList); // 插入人员头像列表
        Toast.makeText(this, "成功保存人员资料", Toast.LENGTH_SHORT).show();
        finish(); // 关闭当前页面
    }

}