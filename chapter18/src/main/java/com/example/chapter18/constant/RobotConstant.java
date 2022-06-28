package com.example.chapter18.constant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.chapter18.bean.CityInfo;
import com.example.chapter18.dao.QuestionDao;
import com.example.chapter18.entity.PoemInfo;
import com.example.chapter18.entity.QuestionInfo;
import com.example.chapter18.util.AssetsUtil;
import com.example.chapter18.util.DateUtil;
import com.example.chapter18.util.FileUtil;
import com.example.chapter18.util.NumberUtil;
import com.example.chapter18.util.PinyinUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class RobotConstant {
    private final static String TAG = "RobotConstant";
    public static String TEMPLETE_FILE = "system_question.txt";
    public static String[] SAMPLE_FILES = {"welcome.mp3", "happy.mp3", "shy.mp3", "cry.mp3", "angry.mp3",
            "regret.mp3", "naive.mp3", "goodbye.mp3", "who.mp3", "name.mp3", "age.mp3"};
    public static String[] SYSTEM_ANSWERS = {
            "您好，很高兴为您服务。有什么可以帮您的呢？",
            "真的嘛，我好开心呢，嘻嘻嘻。",
            "哎哟，不要摸我啦，人家会害羞的呀。",
            "是吗？我真的好伤心呀，呜呜呜。",
            "哼，我不高兴，不理你了。",
            "很抱歉，需要先打开定位和网络，我才知道地点和天气哦。",
            "我还不知道这个问题呢，你能教我吗？",
            "非常感谢您的倾听，下回再见噢。",
            "我是小小机器人呀。",
            "我的名字叫甜甜。",
            "我今年六岁了。"};
    public static String[] SAMPLE_PATHS; // 样本语音的文件路径
    public static String POETRY_FILE = "ancient_poetry.txt";
    public static String RECITE_PART = "(背诵|朗诵|朗读|诵读|会背|你会|备送)";
    public static String RECITE_PATTERN = ".*"+RECITE_PART+".*";

    // 把assets目录的样本音频复制到存储卡
    public static void copySampleFiles(Context context) {
        SAMPLE_PATHS = new String[SAMPLE_FILES.length];
        for (int i=0; i<SAMPLE_FILES.length; i++) {
            String sampleFile = "robot/"+SAMPLE_FILES[i];
            String samplePath = String.format("%s/%s",
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), sampleFile);
            AssetsUtil.Assets2Sd(context, sampleFile, samplePath);
            SAMPLE_PATHS[i] = samplePath;
        }
    }

    // 初始化系统设定的问题
    public static void initSystemQuestion(Context ctx, QuestionDao questionDao) {
        String templateFile = "robot/"+TEMPLETE_FILE;
        String templatePath = String.format("%s/%s",
                ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), templateFile);
        Log.d(TAG, "templatePath="+templatePath);
        if (!new File(templatePath).exists()) {
            List<QuestionInfo> systemList = new ArrayList<>();
            AssetsUtil.Assets2Sd(ctx, templateFile, templatePath);
            String content = FileUtil.openText(templatePath);
            String[] lines = content.split("\\n");
            for (String line : lines) {
                String[] items = line.split(" ");
                systemList.add(new QuestionInfo(items[0], items[1], 0));
            }
            questionDao.insertQuestionList(systemList); // 插入多条问答信息
        }
    }

    // 初始化古诗数据
    public static void initPoetryData(Context context, QuestionDao questionDao) {
        String templateFile = "robot/"+POETRY_FILE;
        String templatePath = String.format("%s/%s",
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), templateFile);
        Log.d(TAG, "templatePath="+templatePath);
        if (!new File(templatePath).exists()) {
            List<PoemInfo> poemList = new ArrayList<>();
            AssetsUtil.Assets2Sd(context, templateFile, templatePath);
            String content = FileUtil.openText(templatePath);
            String[] lines = content.split("\\n");
            for (String line : lines) {
                String[] items = line.split("\\|");
                if (items.length > 3) {
                    poemList.add(new PoemInfo(items[0], items[1], items[2], items[3]));
                }
            }
            questionDao.insertPoemList(poemList); // 插入多条诗歌信息
        }
    }

    // 查找该诗在列表中的位置
    public static int searchPoemPos(String text, int beginPos, List<PoemInfo> poemList) {
        int pos = -1;
        if (beginPos!=-1 && beginPos<poemList.size()-1) {
            PoemInfo poemNext = poemList.get(beginPos+1);
            if (text.contains(poemNext.getTitle())){ // 区分其一和其二
                pos = beginPos+1;
            }
            return pos;
        }
        List<Integer> posList = new ArrayList<>();
        for (int i=beginPos+1; i<poemList.size(); i++) {
            PoemInfo poem = poemList.get(i);
            if (text.contains(poem.getAuthor())){
                posList.add(i);
                String title = poem.getTitle().length()<=1 ?
                        poem.getTitle() : poem.getTitle().substring(0, 2);
                if (text.contains(title)) {
                    return i;
                }
            }
        }
        int dePos = text.lastIndexOf("的");
        if (dePos>=0 && dePos<text.length()-1) {
            String hanzi = text.substring(dePos+1);
            String pinyin = PinyinUtil.getHanziPinYin(hanzi, false);
            if (pinyin!=null && pinyin.length()>0) {
                for (int j=0; j<posList.size(); j++) {
                    PoemInfo poem = poemList.get(posList.get(j));
                    Log.d(TAG, "pinyin="+pinyin+",getPinyin="+poem.getPinyin());
                    if (pinyin.contains(poem.getPinyin())) {
                        pos = posList.get(j);
                        break;
                    }
                }
            }
        }
        return pos;
    }

    // 判断回答的序号和内容
    public static Object[] judgeAnswerResult(String question, String answer, CityInfo cityInfo) {
        int voiceSeq = -1;
        String tempText = "";
        if (answer.contains("askWho")) { // 问个人
            voiceSeq = 8;
        } else if (answer.contains("askName")) { // 问名字
            voiceSeq = 9;
        } else if (answer.contains("askAge")) { // 问年龄
            voiceSeq = 10;
        } else if (answer.contains("askDate")) { // 问日期
            tempText = "今天是"+DateUtil.getNowDateCN();
        } else if (answer.contains("askTime")) { // 问时间
            tempText = "现在是"+DateUtil.getNowTimeCN();
        } else if (answer.contains("askWeek")) { // 问星期
            tempText = "今天是"+DateUtil.getNowWeekCN();
        } else if (answer.contains("askWhere")) { // 问家乡
            if (cityInfo == null) {
                voiceSeq = 5;
            } else {
                tempText = String.format("我是%s人。", cityInfo.city_name.replace("市", ""));
            }
        } else if (answer.contains("askAddress")) { // 问地址
            if (cityInfo == null) {
                voiceSeq = 5;
            } else {
                tempText = String.format("我住在%s。", cityInfo.address);
            }
        } else if (answer.contains("askWeather")) { // 问天气
            if (cityInfo == null || cityInfo.weather_info == null) {
                voiceSeq = 5;
            } else {
                tempText = String.format("今天天气是%s，吹%s风，风力%s级。",
                        cityInfo.weather_info.weather,
                        cityInfo.weather_info.winddirection,
                        cityInfo.weather_info.windpower);
            }
        } else if (answer.contains("askTemperature")) { // 问气温
            if (cityInfo == null || cityInfo.weather_info == null) {
                voiceSeq = 5;
            } else {
                tempText = String.format("现在气温是%s度，湿度是%s%%。",
                        cityInfo.weather_info.temperature,
                        cityInfo.weather_info.humidity);
            }
        } else if (answer.contains("makeHappy")) { // 让它开心
            voiceSeq = 1;
        } else if (answer.contains("makeCry")) { // 让它哭泣
            voiceSeq = 3;
        } else if (answer.contains("makeAngry")) { // 让它愤怒
            voiceSeq = 4;
        } else if (answer.contains("sayGoodbye")) { // 与它再见
            voiceSeq = 7;
        } else if (answer.contains("plusNumber")) { // 整数相加
            long[] operands = NumberUtil.getOperands(question, "加");
            if (operands==null || operands.length<2) {
                voiceSeq = 6;
            } else {
                tempText = String.format("%d加%d等于%d。", operands[0], operands[1], operands[0]+operands[1]);
            }
        } else if (answer.contains("minusNumber")) { // 整数相减
            long[] operands = NumberUtil.getOperands(question, "减");
            if (operands==null || operands.length<2) {
                voiceSeq = 6;
            } else {
                tempText = String.format("%d减%d等于%d。", operands[0], operands[1], operands[0]-operands[1]);
            }
        } else if (answer.contains("multiplyNumber")) { // 整数相乘
            long[] operands = NumberUtil.getOperands(question, "乘");
            if (operands==null || operands.length<2) {
                voiceSeq = 6;
            } else {
                tempText = String.format("%d乘以%d等于%d。", operands[0], operands[1], operands[0]*operands[1]);
            }
        } else if (answer.contains("divideNumber")) { // 整数相除
            long[] operands = NumberUtil.getOperands(question, "除");
            if (operands==null || operands.length<2) {
                voiceSeq = 6;
            } else if (operands[1] == 0) { // 除数为零
                tempText = "小朋友，除数不能为零喔。";
            } else { // 除数非零
                double quotient = 1.0*operands[0]/operands[1];
                // 去掉小数字符串末尾的0
                String result = NumberUtil.removeTailZero(String.format("%.6f", quotient));
                tempText = String.format("%d除以%d等于%s。", operands[0], operands[1], result);
            }
        }
        return new Object[]{voiceSeq, tempText};
    }

}
