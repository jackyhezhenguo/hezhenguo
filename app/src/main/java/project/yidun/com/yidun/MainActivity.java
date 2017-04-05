package project.yidun.com.yidun;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import project.yidun.com.yidun.url.URLServer;
import project.yidun.com.yidun.utils.SPUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
private TextView tvGg;
private TextView tvLogOut;
private TextView tvContent;
private TextView recharge;
private LinearLayout llWolun;
private LinearLayout llFencheng;
private LinearLayout xiaXian;
private LinearLayout llGeren;
private LinearLayout llYaoqingma;
private LinearLayout llTixian;
private LinearLayout llXiaxian;
private RelativeLayout rlAnnouncement;
private ImageView ivClose;
private TextView tvN;
private TextView tvN1;
private TextView tvCount;
private int count = 0;
private boolean exit = false;

    private Handler balanceHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    execute(msg);

                    break;
                case 1:
                    getDownLanou();
                    break;

            }
        }
        private void execute(Message msg){
            try {
                balanceHandler.sendEmptyMessageDelayed(1,1000);
                JSONObject resultJson = new JSONObject(msg.obj.toString());
//                Log.d("111",resultJson.toString());
                if (resultJson.getString("success").toString().equals("true")) {

                    if ((resultJson.getDouble("data") + "").split("\\.").length == 2) {
                        MyApplication.balance = resultJson.getDouble("data") + "";
                        Log.d("444",MyApplication.balance);
                        tvN.setText((resultJson.getDouble("data") + "").split("\\.")[0]);
                        tvN1.setText("." + (resultJson.getDouble("data") + "").split("\\.")[1]);
                    } else {
                        tvN.setText(resultJson.getDouble("data") + "");
                        tvN1.setText("00");
                    }
                } else {
                    Toast.makeText(MainActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
                    if (resultJson.get("errorType").toString().equals("lano.exception.LoginTimeOutException")) {
                        MyApplication.isBack = false;
                        MainActivity.this.startActivity(new Intent(MainActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        MainActivity.this.finish();
                        return;
                    }
                    if (resultJson.get("errorMsg").toString().equals("登录超时.")) {
                        MyApplication.isBack = false;
                        MainActivity.this.startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        MainActivity.this.finish();
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Handler getDownHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    execute(msg);
                    break;
                case 1:
                    if (MyApplication.display) {
                        MyApplication.display = false;
                        getGonggaoLanou();
                    }

                    break;

            }
        }
        private void execute(Message msg){
            try {

                getDownHandler.sendEmptyMessageDelayed(1,2000);

                JSONObject resultJson = new JSONObject(msg.obj.toString());
//                Log.d("111",resultJson.toString());
                if (resultJson.getString("success").toString().equals("true")) {
                    if ((resultJson.getInt("data") + "" ).equals(MyApplication.balance)) {
                        getDownLanou();
                    }
                    MyApplication.downCount = resultJson.getInt("data") + "";
                    Log.d("444",MyApplication.downCount + "");
                        tvCount.setText(resultJson.getInt("data") + "人");
                    llYaoqingma.setClickable(true);
                } else {
                    Toast.makeText(MainActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
                    if (resultJson.get("errorMsg").toString().equals("登录超时.")) {
                        MyApplication.isBack = false;
                        MainActivity.this.startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        MainActivity.this.finish();
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler gonggaoHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    execute(msg);
                    break;
                case 1:
                    getNewVersion();
                    break;

            }
        }
        private void execute(Message msg){
            gonggaoHandler.sendEmptyMessageDelayed(1,1000);
            //                JSONObject resultJson = new JSONObject(msg.obj.toString());
//                Log.d("10100",resultJson.toString());
            getInviteLimmitLanou();
//                    String dataString = resultJson.toString();
            JSONArray ja = null;
            try {
                ja = new JSONArray(msg.obj.toString());
                if (ja.length() > 0) {
                    tvGg.setText(ja.getJSONObject(0).getString("title"));

                    tvContent.setText(ja.getJSONObject(0).getString("content"));
                    rlAnnouncement.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Handler versionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    execute(msg);
                    break;

            }
        }
        private void execute(Message msg){

            try {//我建议了多给我传一个版本号，后台会一直发的嘛，肯定会，然后我需要版本号判断版本，控制好是否弹出，但是后台那边说永远不会更新了，所以就这样吧，先不管了
                JSONObject resultJson = new JSONObject(msg.obj.toString());
                if (resultJson.getString("success").toString().equals("true")) {
                    if (SPUtil.get(getApplicationContext(),"downloaded","").equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
                                AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                        builder.setTitle("检查到新版本")
                                .setIcon(android.R.drawable.stat_sys_warning)
                                .setMessage("是否更新软件版本？")
                                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        DownLoadApk("http://119.23.38.71/versions/down/last");
                                        SPUtil.putAndApply(getApplicationContext(), "downloaded", "1");//决定过一次不再提醒
                                    }
                                });
                        builder.setCancelable(false);
                        builder.create().show();
                    }
                }
                Log.d("10100",resultJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Handler limmitHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    execute(msg);
                    break;

            }
        }
        private void execute(Message msg){
            try {
                JSONObject resultJson = new JSONObject(msg.obj.toString());
                Log.d("10100",resultJson.toString());
                    int limitString = resultJson.getInt("inviteLimit");
                    if (limitString == 0) {
                        llXiaxian.setVisibility(View.GONE);
                        llYaoqingma.setVisibility(View.GONE);
                    } else {
//                        llXiaxian.setVisibility(View.VISIBLE);
                        llXiaxian.setVisibility(View.GONE);
                        llYaoqingma.setVisibility(View.VISIBLE);
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int version = android.os.Build.VERSION.SDK_INT;
        if (version > 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        }else{

        }

        setContentView(R.layout.activity_main);


        setViews();



    }

    @Override
    protected void onResume() {
        super.onResume();
        llYaoqingma.setClickable(false);
        getBalanceLanou();

    }

    private void setViews () {
        llTixian = (LinearLayout)findViewById(R.id.ll_tixian);
        llXiaxian = (LinearLayout)findViewById(R.id.ll_xiaxian);
        tvLogOut = (TextView)findViewById(R.id.tv_log_out);

        tvGg = (TextView) findViewById(R.id.tv_gg);
        tvContent = (TextView) findViewById(R.id.tv_content);
        recharge = (TextView) findViewById(R.id.tv_recharge);
        llWolun = (LinearLayout)findViewById(R.id.ll_wolun);
        llFencheng = (LinearLayout)findViewById(R.id.ll_fencheng);
        xiaXian = (LinearLayout)findViewById(R.id.ll_xiaxian);
        llGeren = (LinearLayout)findViewById(R.id.ll_geren);
        llYaoqingma = (LinearLayout)findViewById(R.id.ll_yaoqingma);

        rlAnnouncement = (RelativeLayout) findViewById(R.id.rl_announcement);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        tvN = (TextView) findViewById(R.id.tv_number);
        tvN1 = (TextView)findViewById(R.id.tv_number1);
        tvCount = (TextView)findViewById(R.id.tv_count);

        tvLogOut.setOnClickListener(this);
        recharge.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        llWolun.setOnClickListener(this);
        llFencheng.setOnClickListener(this);
        xiaXian.setOnClickListener(this);
        llGeren.setOnClickListener(this);
        llYaoqingma.setOnClickListener(this);
        llTixian.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_recharge:
                Intent intent = new Intent(MainActivity.this,RechargeActivity.class);
                startActivity(intent);
            break;
            case R.id.iv_close:
                rlAnnouncement.setVisibility(View.GONE);
            break;
            case R.id.ll_wolun:
                Intent intentw = new Intent(MainActivity.this,TurbineFirstActivity.class);
                startActivity(intentw);
            break;
            case R.id.ll_fencheng:
                Intent intentf = new Intent(MainActivity.this,AccountActivity.class);
                startActivity(intentf);
            break;
            case R.id.ll_xiaxian:
                Intent intentx = new Intent(MainActivity.this,MemberActivity.class);
                startActivity(intentx);
            break;
            case R.id.ll_geren:
                Intent intentg = new Intent(MainActivity.this,PersonalDataActivity.class);
                startActivity(intentg);
            break;
            case R.id.ll_yaoqingma:
                Intent intenty = new Intent(MainActivity.this,InvatationCodeActivity.class);
                startActivity(intenty);
            break;
            case R.id.ll_tixian:
                Intent intentt = new Intent(MainActivity.this,CashActivity.class);
                startActivity(intentt);
                break;
            case R.id.tv_log_out:
                Toast.makeText(MainActivity.this,"退出登录",Toast.LENGTH_LONG).show();
                MyApplication.isBack = false;
                MainActivity.this.startActivity(new Intent(MainActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            break;
        }
    }
    private void DownLoadApk(String dowloadPath) {
        Toast.makeText(MainActivity.this,"下载完成后自动提示安装",Toast.LENGTH_LONG).show();

        DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(dowloadPath);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置下载路径和文件名
         request.setDestinationInExternalPublicDir("download", "蓝鸥飞翔"+ SystemClock.currentThreadTimeMillis()+".apk");
         request.setDescription("蓝鸥飞翔新版本下载");
         request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
         request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
         request.allowScanningByMediaScanner();
         // 设置为可见和可管理
         request.setVisibleInDownloadsUi(true);
         long refernece = dManager.enqueue(request);
         // 把当前下载的ID保存起来
         SharedPreferences sPreferences = getSharedPreferences("downloadcomplete", 0);
         sPreferences.edit().putLong("refernece", refernece).commit();
    }

    /**
     * 获取邀请限制
     */
    private void getInviteLimmitLanou() {
        new Thread(limmitRunnable).start();

    }
    /**
     * 获取邀请限制
     */
    public Runnable limmitRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(limmitHandler);
            urlServer.getCurrentInfo(MyApplication.getToken());
        }
    };

    /**
     * 获取是否有新版本的信息
     */
    private void getNewVersion() {
        new Thread(versionRunnable).start();
    }
    /**
     * 获取是否有新版本的信息
     */
    public Runnable versionRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(versionHandler);
//            Log.e("token", MyApplication.getToken());
            urlServer.getVersion(MyApplication.getToken());
        }
    };
    /**
     * 获取公告
     */
    private void getGonggaoLanou() {
        new Thread(gonggaoRunnable).start();

    }
    /**
     * 获取公告
     */
    public Runnable gonggaoRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(gonggaoHandler);
//            Log.e("token", MyApplication.getToken());
            urlServer.getGonggao(MyApplication.getToken());
        }
    };

    /**
     * 获取余额
     */
    private void getBalanceLanou() {
        new Thread(balanceRunnable).start();

    }
    /**
     * 获取余额
     */
    public Runnable balanceRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(balanceHandler);
//            Log.e("token", MyApplication.getToken());
            urlServer.getMyBalance(MyApplication.getToken());
        }
    };
    /**
     * 获取下线
     */
    private void getDownLanou() {
        new Thread(downRunnable).start();

    }
    /**
     * 获取下线
     */
    public Runnable downRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(getDownHandler);
            urlServer.getDownLine(MyApplication.getToken());
        }
    };

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (count == 0) {
//                Toast.makeText(MainActivity.this,"再按一次退出登录",Toast.LENGTH_LONG).show();
//                count++;
//            } else {
//                Toast.makeText(MainActivity.this,"退出登录",Toast.LENGTH_LONG).show();
//                MyApplication.isBack = false;
//                MainActivity.this.startActivity(new Intent(MainActivity.this,LoginActivity.class));
//            }
//
//        }
//        return true;
//
//    }
}
