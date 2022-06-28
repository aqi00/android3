package com.example.chapter14;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.adapter.PhotoGridAdapter;

import java.util.List;

public class PhotoGridActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<String> mPathList; // 照片文件的路径列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_grid);
        GridView gv_photo = findViewById(R.id.gv_photo);
        mPathList = getIntent().getExtras().getStringArrayList("path_list");
        gv_photo.setAdapter(new PhotoGridAdapter(this, mPathList));
        gv_photo.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra("photo_path", mPathList.get(position));
        startActivity(intent);
    }
}