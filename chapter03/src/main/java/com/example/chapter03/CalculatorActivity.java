package com.example.chapter03;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "CalculatorActivity";
    private TextView tv_result; // 声明一个文本视图对象
    private String operator = ""; // 运算符
    private String firstNum = ""; // 第一个操作数
    private String secondNum = ""; // 第二个操作数
    private String result = ""; // 当前的计算结果
    private String showText = ""; // 显示的文本内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        // 从布局文件中获取名叫tv_result的文本视图
        tv_result = findViewById(R.id.tv_result);
        // 下面给每个按钮控件都注册了点击监听器
        findViewById(R.id.btn_cancel).setOnClickListener(this); // “取消”按钮
        findViewById(R.id.btn_divide).setOnClickListener(this); // “除法”按钮
        findViewById(R.id.btn_multiply).setOnClickListener(this); // “乘法”按钮
        findViewById(R.id.btn_clear).setOnClickListener(this); // “清除”按钮
        findViewById(R.id.btn_seven).setOnClickListener(this); // 数字7
        findViewById(R.id.btn_eight).setOnClickListener(this); // 数字8
        findViewById(R.id.btn_nine).setOnClickListener(this); // 数字9
        findViewById(R.id.btn_plus).setOnClickListener(this); // “加法”按钮
        findViewById(R.id.btn_four).setOnClickListener(this); // 数字4
        findViewById(R.id.btn_five).setOnClickListener(this); // 数字5
        findViewById(R.id.btn_six).setOnClickListener(this); // 数字6
        findViewById(R.id.btn_minus).setOnClickListener(this); // “减法”按钮
        findViewById(R.id.btn_one).setOnClickListener(this); // 数字1
        findViewById(R.id.btn_two).setOnClickListener(this); // 数字2
        findViewById(R.id.btn_three).setOnClickListener(this); // 数字3
        findViewById(R.id.btn_reciprocal).setOnClickListener(this); // 求倒数按钮
        findViewById(R.id.btn_zero).setOnClickListener(this); // 数字0
        findViewById(R.id.btn_dot).setOnClickListener(this); // “小数点”按钮
        findViewById(R.id.btn_equal).setOnClickListener(this); // “等号”按钮
        findViewById(R.id.ib_sqrt).setOnClickListener(this); // “开平方”按钮
    }

    private boolean verify(View v) {
        if (v.getId() == R.id.btn_cancel) { // 点击了取消按钮
            if (operator.equals("") && (firstNum.equals("") || firstNum.equals("0"))) { // 无运算符，则表示逐位取消第一个操作数
                Toast.makeText(this, "没有可取消的数字了", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!operator.equals("") && secondNum.equals("")) { // 有运算符，则表示逐位取消第二个操作数
                Toast.makeText(this, "没有可取消的数字了", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (v.getId() == R.id.btn_equal) { // 点击了等号按钮
            if (operator.equals("")) { // 无运算符
                Toast.makeText(this, "请输入运算符", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (firstNum.equals("") || secondNum.equals("")) { // 无操作数
                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (operator.equals("÷") && Double.parseDouble(secondNum) == 0) { // 除数为零
                Toast.makeText(this, "除数不能为零", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (v.getId() == R.id.btn_plus || v.getId() == R.id.btn_minus // 点击了加、减、乘、除按钮
                || v.getId() == R.id.btn_multiply || v.getId() == R.id.btn_divide) {
            if (firstNum.equals("")) { // 缺少第一个操作数
                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!operator.equals("")) { // 已有运算符
                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (v.getId() == R.id.ib_sqrt) { // 点击了开根号按钮
            if (firstNum.equals("")) { // 缺少底数
                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (Double.parseDouble(firstNum) < 0) { // 不能对负数开平方
                Toast.makeText(this, "开根号的数值不能小于零", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (v.getId() == R.id.btn_reciprocal) { // 点击了求倒数按钮
            if (firstNum.equals("")) { // 缺少底数
                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (Double.parseDouble(firstNum) == 0) { // 不能对零求倒数
                Toast.makeText(this, "不能对零求倒数", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (v.getId() == R.id.btn_dot) { // 点击了小数点
            if (operator.equals("") && firstNum.contains(".")) { // 无运算符，则检查第一个操作数是否已有小数点
                Toast.makeText(this, "一个数字不能有两个小数点", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!operator.equals("") && secondNum.contains(".")) { // 有运算符，则检查第二个操作数是否已有小数点
                Toast.makeText(this, "一个数字不能有两个小数点", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (!verify(v)) { // 未通过合法性校验，直接返回
            return;
        }
        String inputText;
        if (v.getId() == R.id.ib_sqrt) { // 如果是开根号按钮
            inputText = "√";
        } else { // 除了开根号之外的其他按钮
            inputText = ((TextView) v).getText().toString();
        }
        Log.d(TAG, "inputText=" + inputText);
        if (v.getId() == R.id.btn_clear) { // 点击了清除按钮
            clear();
        } else if (v.getId() == R.id.btn_cancel) { // 点击了取消按钮
            if (operator.equals("")) { // 无运算符，则表示逐位取消第一个操作数
                if (firstNum.length() == 1) {
                    firstNum = "0";
                } else if (firstNum.length() > 1) {
                    firstNum = firstNum.substring(0, firstNum.length() - 1);
                }
                refreshText(firstNum);
            } else { // 有运算符，则表示逐位取消第二个操作数
                if (secondNum.length() == 1) {
                    secondNum = "";
                } else if (secondNum.length() > 1) {
                    secondNum = secondNum.substring(0, secondNum.length() - 1);
                }
                refreshText(showText.substring(0, showText.length() - 1));
            }
        } else if (v.getId() == R.id.btn_plus || v.getId() == R.id.btn_minus // 点击了加、减、乘、除按钮
                || v.getId() == R.id.btn_multiply || v.getId() == R.id.btn_divide) {
            operator = inputText; // 运算符
            refreshText(showText + operator);
        } else if (v.getId() == R.id.btn_equal) { // 点击了等号按钮
            double calculate_result = calculateFour(); // 加减乘除四则运算
            refreshOperate(String.valueOf(calculate_result));
            refreshText(showText + "=" + result);
        } else if (v.getId() == R.id.ib_sqrt) { // 点击了开根号按钮
            double calculate_result = Math.sqrt(Double.parseDouble(firstNum)); // 开平方运算
            refreshOperate(String.valueOf(calculate_result));
            refreshText(showText + "√=" + result);
        } else if (v.getId() == R.id.btn_reciprocal) { // 点击了求倒数按钮
            double calculate_result = 1.0 / Double.parseDouble(firstNum); // 求倒数运算
            refreshOperate(String.valueOf(calculate_result));
            refreshText(showText + "/=" + result);
        } else { // 点击了其他按钮，包括数字和小数点
            if (result.length() > 0 && operator.equals("")) { // 上次的运算结果已经出来了
                clear();
            }
            if (operator.equals("")) { // 无运算符，则继续拼接第一个操作数
                firstNum = firstNum+inputText;
            } else { // 有运算符，则继续拼接第二个操作数
                secondNum = secondNum + inputText;
            }
            if (showText.equals("0") && !inputText.equals(".")) { // 整数不需要前面的0
                refreshText(inputText);
            } else {
                refreshText(showText + inputText);
            }
        }
    }

    // 刷新运算结果
    private void refreshOperate(String new_result) {
        result = new_result;
        firstNum = result;
        secondNum = "";
        operator = "";
    }

    // 刷新文本显示
    private void refreshText(String text) {
        showText = text;
        tv_result.setText(showText);
    }

    // 清空并初始化
    private void clear() {
        refreshOperate("");
        refreshText("");
    }

    // 加减乘除四则运算，返回计算结果
    private double calculateFour() {
        double calculate_result = 0;
        if (operator.equals("＋")) { // 当前是相加运算
            calculate_result = Double.parseDouble(firstNum) + Double.parseDouble(secondNum);
        } else if (operator.equals("－")) { // 当前是相减运算
            calculate_result = Double.parseDouble(firstNum) - Double.parseDouble(secondNum);
        } else if (operator.equals("×")) { // 当前是相乘运算
            calculate_result = Double.parseDouble(firstNum) * Double.parseDouble(secondNum);
        } else if (operator.equals("÷")) { // 当前是相除运算
            calculate_result = Double.parseDouble(firstNum) / Double.parseDouble(secondNum);
        }
        Log.d(TAG, "calculate_result=" + calculate_result); // 把运算结果打印到日志中
        return calculate_result;
    }

}
