package com.example.chapter07.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.chapter07.R;
import com.example.chapter07.adapter.BillListAdapter;
import com.example.chapter07.bean.BillInfo;
import com.example.chapter07.database.BillDBHelper;

import java.util.ArrayList;
import java.util.List;

public class BillFragment extends Fragment {
    private static final String TAG = "BillFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private int mMonth; // 当前选择的月份
    private ListView lv_bill; // 声明一个列表视图对象
    private List<BillInfo> mBillList = new ArrayList<BillInfo>(); // 账单信息列表

    // 获取该碎片的一个实例
    public static BillFragment newInstance(int month) {
        BillFragment fragment = new BillFragment(); // 创建该碎片的一个实例
        Bundle bundle = new Bundle(); // 创建一个新包裹
        bundle.putInt("month", month); // 往包裹存入月份
        fragment.setArguments(bundle); // 把包裹塞给碎片
        return fragment; // 返回碎片实例
    }

    // 创建碎片视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        if (getArguments() != null) { // 如果碎片携带有包裹，就打开包裹获取参数信息
            mMonth = getArguments().getInt("month", 1);
        }
        // 根据布局文件fragment_bill.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_bill, container, false);
        // 从布局视图中获取名叫lv_bill的列表视图
        lv_bill = mView.findViewById(R.id.lv_bill);
        return mView; // 返回该碎片的视图对象
    }

    @Override
    public void onStart() {
        super.onStart();
        // 获得数据库帮助器的实例
        BillDBHelper helper = BillDBHelper.getInstance(mContext);
        mBillList = helper.queryByMonth(mMonth); // 查询指定月份的账单列表
        if (mBillList!=null && mBillList.size()>0) {
            double income=0, expend=0;
            for (BillInfo bill : mBillList) {
                if (bill.type == 0) { // 收入金额累加
                    income += bill.amount;
                } else { // 支出金额累加
                    expend += bill.amount;
                }
            }
            BillInfo sum = new BillInfo();
            sum.date = "合计";
            sum.desc = String.format("收入%d元\n支出%d元", (int) income, (int) expend);
            sum.remark = String.format("余额%d元", (int) (income-expend));
            mBillList.add(sum); // 往账单信息列表末尾添加一个总计行
        }
        // 构建一个当月账单的列表适配器
        BillListAdapter listAdapter = new BillListAdapter(mContext, mBillList);
        lv_bill.setAdapter(listAdapter); // 设置列表视图的适配器
        lv_bill.setOnItemClickListener(listAdapter); // 设置列表视图的点击监听器
        lv_bill.setOnItemLongClickListener(listAdapter); // 设置列表视图的长按监听器
    }


}
