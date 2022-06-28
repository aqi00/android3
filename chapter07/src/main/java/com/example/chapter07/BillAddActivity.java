package com.example.chapter07;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter07.bean.BillInfo;
import com.example.chapter07.database.BillDBHelper;
import com.example.chapter07.util.DateUtil;
import com.example.chapter07.util.ViewUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BillAddActivity extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private final static String TAG = "BillAddActivity";
    private TextView tv_date; // 声明一个文本视图对象
    private RadioButton rb_income; // 声明一个单选按钮对象
    private RadioButton rb_expand; // 声明一个单选按钮对象
    private EditText et_desc; // 声明一个编辑框对象
    private EditText et_amount; // 声明一个编辑框对象
    private int mBillType = 1; // 账单类型。0 收入；1 支出
    private int xuhao; // 如果序号有值，说明已存在该账单
    private Calendar calendar = Calendar.getInstance(); // 获取日历实例，里面包含了当前的年月日
    private BillDBHelper mBillHelper; // 声明一个账单数据库的帮助器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_add);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_option = findViewById(R.id.tv_option);
        tv_date = findViewById(R.id.tv_date);
        RadioGroup rg_type = findViewById(R.id.rg_type);
        rb_income = findViewById(R.id.rb_income);
        rb_expand = findViewById(R.id.rb_expand);
        et_desc = findViewById(R.id.et_desc);
        et_amount = findViewById(R.id.et_amount);
        tv_title.setText("请填写账单");
        tv_option.setText("账单列表");
        findViewById(R.id.iv_back).setOnClickListener(this);
        tv_option.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        rg_type.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        xuhao = getIntent().getIntExtra("xuhao", -1);
        mBillHelper = BillDBHelper.getInstance(this); // 获取账单数据库的帮助器对象
        if (xuhao != -1) { // 序号有值，就展示数据库里的账单详情
            List<BillInfo> bill_list = (List<BillInfo>) mBillHelper.queryById(xuhao);
            if (bill_list.size() > 0) { // 已存在该账单
                BillInfo bill = bill_list.get(0); // 获取账单信息
                Date date = DateUtil.formatString(bill.date);
                Log.d(TAG, "bill.date="+bill.date);
                Log.d(TAG, "year="+date.getYear()+",month="+date.getMonth()+",day="+date.getDate());
                calendar.set(Calendar.YEAR, date.getYear()+1900);
                calendar.set(Calendar.MONTH, date.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, date.getDate());
                if (bill.type == 0) { // 收入
                    rb_income.setChecked(true);
                } else { // 支出
                    rb_expand.setChecked(true);
                }
                et_desc.setText(bill.desc); // 设置账单的描述文本
                et_amount.setText(""+bill.amount); // 设置账单的交易金额
            }
        }
        tv_date.setText(DateUtil.getDate(calendar)); // 设置账单的发生时间
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish(); // 关闭当前页面
        } else if (v.getId() == R.id.tv_option) {
            Intent intent = new Intent(this, BillPagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 设置启动标志
            startActivity(intent); // 跳到账单列表页面
        } else if (v.getId() == R.id.tv_date) {
            // 构建一个日期对话框，该对话框已经集成了日期选择器。
            // DatePickerDialog的第二个构造参数指定了日期监听器
            DatePickerDialog dialog = new DatePickerDialog(this, this,
                    calendar.get(Calendar.YEAR), // 年份
                    calendar.get(Calendar.MONTH), // 月份
                    calendar.get(Calendar.DAY_OF_MONTH)); // 日子
            dialog.show(); // 显示日期选择对话框
        } else if (v.getId() == R.id.btn_save) {
            saveBill(); // 保存账单
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mBillType = (checkedId==R.id.rb_expand) ? 1 : 0;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        tv_date.setText(DateUtil.getDate(calendar));
    }

    // 保存账单
    private void saveBill() {
        //ViewUtil.hideAllInputMethod(this); // 隐藏输入法软键盘
        ViewUtil.hideOneInputMethod(this, et_amount); // 隐藏输入法软键盘
        BillInfo bill = new BillInfo();
        bill.xuhao = xuhao;
        bill.date = tv_date.getText().toString();
        bill.month = 100*calendar.get(Calendar.YEAR) + (calendar.get(Calendar.MONTH)+1);
        bill.type = mBillType;
        bill.desc = et_desc.getText().toString();
        bill.amount = Double.parseDouble(et_amount.getText().toString());
        mBillHelper.save(bill); // 把账单信息保存到数据库
        Toast.makeText(this, "已添加账单", Toast.LENGTH_SHORT).show();
        resetPage(); // 重置页面
    }

    // 重置页面
    private void resetPage() {
        calendar = Calendar.getInstance();
        et_desc.setText("");
        et_amount.setText("");
        tv_date.setText(DateUtil.getDate(calendar));
    }
    
}
