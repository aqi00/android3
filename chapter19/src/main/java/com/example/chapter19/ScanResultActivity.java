package com.example.chapter19;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;

public class ScanResultActivity extends AppCompatActivity {
    private final static String TAG = "ScanResultActivity";
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        tv_result = findViewById(R.id.tv_result);
        parserScanResult(); // 解析扫码结果
    }

    // 解析扫码结果
    private void parserScanResult() {
        // 从意图中获取可折叠的扫码结果
        HmsScan hmsScan = getIntent().getParcelableExtra(ScanUtil.RESULT);
        try {
            String desc = String.format("扫码结果如下：\n\t\t格式为%s\n\t\t类型为%s\n\t\t内容为%s",
                    getCodeFormat(hmsScan.getScanType()),
                    getResultType(hmsScan.getScanType(), hmsScan.getScanTypeForm()),
                    hmsScan.getOriginalValue());
            Log.d(TAG, "desc="+desc);
            tv_result.setText(desc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取扫码格式
    private String getCodeFormat(int scan_type) {
        String codeFormat = "未知（Unknown）";
        if (scan_type == HmsScan.QRCODE_SCAN_TYPE) {
            codeFormat = "快速响应码（QR code）";
        } else if (scan_type == HmsScan.AZTEC_SCAN_TYPE) {
            codeFormat = "阿兹特克码（AZTEC code）";
        } else if (scan_type == HmsScan.DATAMATRIX_SCAN_TYPE) {
            codeFormat = "数据矩阵码（DATAMATRIX code）";
        } else if (scan_type == HmsScan.PDF417_SCAN_TYPE) {
            codeFormat = "便携数据文件码（PDF417 code）";
        } else if (scan_type == HmsScan.CODE93_SCAN_TYPE) {
            codeFormat = "CODE93";
        } else if (scan_type == HmsScan.CODE39_SCAN_TYPE) {
            codeFormat = "CODE39";
        } else if (scan_type == HmsScan.CODE128_SCAN_TYPE) {
            codeFormat = "CODE128";
        } else if (scan_type == HmsScan.EAN13_SCAN_TYPE) {
            codeFormat = "欧洲商品编码-标准版（EAN13 code）";
        } else if (scan_type == HmsScan.EAN8_SCAN_TYPE) {
            codeFormat = "欧洲商品编码-缩短版（EAN8 code）";
        } else if (scan_type == HmsScan.ITF14_SCAN_TYPE) {
            codeFormat = "外箱条码（ITF14 code）";
        } else if (scan_type == HmsScan.UPCCODE_A_SCAN_TYPE) {
            codeFormat = "商品统一代码-通用（UPCCODE_A）";
        } else if (scan_type == HmsScan.UPCCODE_E_SCAN_TYPE) {
            codeFormat = "商品统一代码-短码（UPCCODE_E）";
        } else if (scan_type == HmsScan.CODABAR_SCAN_TYPE) {
            codeFormat = "库德巴码（CODABAR）";
        }
        return codeFormat;
    }

    // 获取结果类型
    private String getResultType(int scan_type, int scanForm) {
        String resultType = "文本（Text）";
        if (scan_type == HmsScan.QRCODE_SCAN_TYPE) {
            if (scanForm == HmsScan.PURE_TEXT_FORM) {
                resultType = "文本（Text）";
            } else if (scanForm == HmsScan.EVENT_INFO_FORM) {
                resultType = "事件（Event）";
            } else if (scanForm == HmsScan.CONTACT_DETAIL_FORM) {
                resultType = "联系（Contact）";
            } else if (scanForm == HmsScan.DRIVER_INFO_FORM) {
                resultType = "许可（License）";
            } else if (scanForm == HmsScan.EMAIL_CONTENT_FORM) {
                resultType = "电子邮箱（Email）";
            } else if (scanForm == HmsScan.LOCATION_COORDINATE_FORM) {
                resultType = "位置（Location）";
            } else if (scanForm == HmsScan.TEL_PHONE_NUMBER_FORM) {
                resultType = "电话（Tel）";
            } else if (scanForm == HmsScan.SMS_FORM) {
                resultType = "短信（SMS）";
            } else if (scanForm == HmsScan.WIFI_CONNECT_INFO_FORM) {
                resultType = "无线网络（Wi-Fi）";
            } else if (scanForm == HmsScan.URL_FORM) {
                resultType = "网址（WebSite）";
            }
        } else if (scan_type == HmsScan.EAN13_SCAN_TYPE) {
            if (scanForm == HmsScan.ISBN_NUMBER_FORM) {
                resultType = "国际标准书号（ISBN）";
            } else if (scanForm == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType = "产品（Product）";
            }
        } else if (scan_type == HmsScan.EAN8_SCAN_TYPE
                || scan_type == HmsScan.UPCCODE_A_SCAN_TYPE
                || scan_type == HmsScan.UPCCODE_E_SCAN_TYPE) {
            if (scanForm == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType = "产品（Product）";
            }
        }
        return resultType;
    }

}