package project.yidun.com.yidun;

import android.app.Application;

import project.yidun.com.yidun.utils.SPUtil;

/**
 * Created by Administrator on 2017/3/24.
 */

public class MyApplication extends Application{
    public static MyApplication context;
    public static int totalCount;
    public static int lastCount;
    public static int levelCount;
    public static String inviteLimit = "";
    public static String token = "";
    public static final String KEY_TOKEN = "KEY_TOKEN";
    public static String tvName = null;
    public static String tvMobile = null;
    public static String tvCard = null;
    public static String tvAccountName = null;
    public static String tvDiergeMobile = null;
    public static String tvSubsidiaryBank = null;
    public static String userId = null;
    public static String downCount = null;
    public static String balance = null;
    public static String cardId = null;
    public static int level;
    public static boolean isBack = true;
    public static boolean display = false;

    public static String getToken(){
        if(token == null || token.equals("")){
            token = (String) SPUtil.get(getInstance(), KEY_TOKEN, "");
        }
        return token;
    }
    public static void setToken(String s){
        token = s;
    }


    public static MyApplication getInstance() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;


    }
}
