package com.example.chapter07.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.example.chapter07.R;

import java.util.ArrayList;
import java.util.List;

public class LaunchSimpleAdapter extends PagerAdapter {
    private List<View> mViewList = new ArrayList<View>(); // 声明一个引导页的视图列表

    // 引导页适配器的构造方法，传入上下文与图片数组
    public LaunchSimpleAdapter(final Context context, int[] imageArray) {
        for (int i = 0; i < imageArray.length; i++) {
            // 根据布局文件item_launch.xml生成视图对象
            View view = LayoutInflater.from(context).inflate(R.layout.item_launch, null);
            ImageView iv_launch = view.findViewById(R.id.iv_launch);
            RadioGroup rg_indicate = view.findViewById(R.id.rg_indicate);
            Button btn_start = view.findViewById(R.id.btn_start);
            iv_launch.setImageResource(imageArray[i]); // 设置引导页的全屏图片
            // 每个页面都分配一个对应的单选按钮
            for (int j = 0; j < imageArray.length; j++) {
                RadioButton radio = new RadioButton(context); // 创建一个单选按钮
                radio.setLayoutParams(new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                radio.setButtonDrawable(R.drawable.launch_guide); // 设置单选按钮的图标
                radio.setPadding(10, 10, 10, 10); // 设置单选按钮的四周间距
                rg_indicate.addView(radio); // 把单选按钮添加到页面底部的单选组
            }
            // 当前位置的单选按钮要高亮显示，比如第二个引导页就高亮第二个单选按钮
            ((RadioButton) rg_indicate.getChildAt(i)).setChecked(true);
            // 如果是最后一个引导页，则显示入口按钮，以便用户点击按钮进入主页
            if (i == imageArray.length - 1) {
                btn_start.setVisibility(View.VISIBLE);
                btn_start.setOnClickListener(v -> {
                    // 这里要跳到应用主页
                    Toast.makeText(context, "欢迎您开启美好生活",
                            Toast.LENGTH_SHORT).show();
                });
            }
            mViewList.add(view); // 把该图片对应的页面添加到引导页的视图列表
        }
    }

    // 获取页面项的个数
    public int getCount() {
        return mViewList.size();
    }

    // 判断当前视图是否来自指定对象
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // 从容器中销毁指定位置的页面
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    // 实例化指定位置的页面，并将其添加到容器中
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }
}
