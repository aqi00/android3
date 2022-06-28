package com.example.chapter07.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chapter07.BillAddActivity;
import com.example.chapter07.R;
import com.example.chapter07.bean.BillInfo;
import com.example.chapter07.database.BillDBHelper;

import java.util.ArrayList;
import java.util.List;

public class BillListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{
    private static final String TAG = "BillListAdapter";
    private Context mContext; // 声明一个上下文对象
    private List<BillInfo> mBillList = new ArrayList<BillInfo>(); // 账单信息列表

    public BillListAdapter(Context context, List<BillInfo> billList) {
        mContext = context;
        mBillList = billList;
    }

    @Override
    public int getCount() {
        return mBillList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBillList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            // 根据布局文件item_bill.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bill, null);
            holder.tv_date = convertView.findViewById(R.id.tv_date);
            holder.tv_desc = convertView.findViewById(R.id.tv_desc);
            holder.tv_amount = convertView.findViewById(R.id.tv_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BillInfo bill = mBillList.get(position);
        holder.tv_date.setText(bill.date);
        holder.tv_desc.setText(bill.desc);
        if (bill.date.equals("合计")) {
            holder.tv_amount.setText(bill.remark);
        } else {
            holder.tv_amount.setText(String.format("%s%d元", bill.type==0?"收入":"支出", (int) bill.amount));
        }
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mBillList.size()-1) { // 合计行不响应点击事件
            return;
        }
        Log.d(TAG, "onItemClick position=" + position);
        BillInfo bill = mBillList.get(position);
        // 以下跳转到账单填写页面
        Intent intent = new Intent(mContext, BillAddActivity.class);
        intent.putExtra("xuhao", bill.xuhao); // 携带账单序号，表示已存在该账单
        mContext.startActivity(intent); // 因为已存在该账单，所以跳过去实际会编辑账单
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (position >= mBillList.size()-1) { // 合计行不响应长按事件
            return true;
        }
        Log.d(TAG, "onItemLongClick position=" + position);
        BillInfo bill = mBillList.get(position); // 获得当前位置的账单信息
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String desc = String.format("是否删除以下账单？\n%s %s%d %s", bill.date,
                bill.type==0?"收入":"支出", (int) bill.amount, bill.desc);
        builder.setMessage(desc); // 设置提醒对话框的消息文本
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBill(position); // 删除该账单
            }
        });
        builder.setNegativeButton("否", null);
        builder.create().show(); // 显示提醒对话框
        return true;
    }

    // 删除该账单
    private void deleteBill(int position) {
        BillInfo bill = mBillList.get(position);
        mBillList.remove(position);
        notifyDataSetChanged(); // 通知适配器发生了数据变化
        // 获得数据库帮助器的实例
        BillDBHelper helper = BillDBHelper.getInstance(mContext);
        helper.delete(bill.xuhao); // 从数据库删除指定序号的账单
    }

    public final class ViewHolder {
        public TextView tv_date;
        public TextView tv_desc;
        public TextView tv_amount;
    }

}
