package project.yidun.com.yidun;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import project.yidun.com.yidun.adapter.WolunAdapter;
import project.yidun.com.yidun.entity.Wolun;
import project.yidun.com.yidun.url.URLServer;

import static project.yidun.com.yidun.MyApplication.levelCount;
import static project.yidun.com.yidun.MyApplication.totalCount;
import static project.yidun.com.yidun.R.id.tv_back;
import static project.yidun.com.yidun.R.id.tv_disappear;
import static project.yidun.com.yidun.R.id.tv_enter_wolun;
import static project.yidun.com.yidun.R.id.tv_tips;
import static project.yidun.com.yidun.R.id.tv_yes;

public class TurbineFirstActivity extends AppCompatActivity implements View.OnClickListener{
    private WolunAdapter wolunAdapter;
    private ListView lvWolun;
    private List<Wolun> wolunList;
    private ImageView tvBack;
    private TextView tvEnterWolun;
    private TextView tvTips;
    private TextView tv00;
    private TextView tv01;
    private TextView tv02;
    private TextView tvDisappear;
    private TextView tvYes;
    private boolean first;
    private RelativeLayout rlConfirm;
    private RelativeLayout rlWolun;
    private Handler priceHandler = new Handler(){
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
                rlConfirm.setVisibility(View.VISIBLE);
                tvTips.setText("确定支付" + resultJson.getString("money").toString() + "元");
//                if (resultJson.getString("errorMsg") != null) {
//                    Toast.makeText(TurbineFirstActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Handler yesHandler = new Handler(){
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
                if (resultJson.getString("success").toString().equals("true")) {
                    Toast.makeText(TurbineFirstActivity.this,"恭喜，支付成功！",Toast.LENGTH_SHORT).show();
                    first = false;
                    getWolun();
                    return;
                }
                if (resultJson.getString("errorMsg") != null) {
                    first = false;
                    Toast.makeText(TurbineFirstActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
                    if (resultJson.getString("errorType") != null) {
                        if (resultJson.get("errorType").toString().equals("lano.exception.BalanceNotEnoughException")) {
                            Toast.makeText(TurbineFirstActivity.this,"余额不足，请联系后台服务人员充值",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    return;
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
            window.setStatusBarColor(Color.parseColor("#89D4FE"));
        }else{

        }
        setContentView(R.layout.activity_turbine_first);
        setViews();
    }

    /**
     * 获取价钱
     */
    public Runnable priceRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(priceHandler);
            urlServer.getWolunprice(MyApplication.getToken());
        }
    };
    /**
     * 确认付款
     */
    public Runnable yesRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(yesHandler);
            urlServer.getYes(MyApplication.getToken());
        }
    };

    @Override
    protected void onResume() {
        getWolun();
        super.onResume();

    }

    private void setViews () {
        lvWolun = (ListView) findViewById(R.id.lv_wolun);
        wolunList = new ArrayList<Wolun>();
        wolunAdapter = new WolunAdapter(TurbineFirstActivity.this,wolunList,R.layout.layout_item_wolun);
        lvWolun.setAdapter(wolunAdapter);
        tvBack = (ImageView) findViewById(R.id.tv_back);
        tv00 = (TextView) findViewById(R.id.tv_00);
        tv01 = (TextView) findViewById(R.id.tv_01);
        tv02 = (TextView) findViewById(R.id.tv_02);
        tvTips = (TextView) findViewById(tv_tips);
        tvDisappear = (TextView) findViewById(R.id.tv_disappear);
        tvYes = (TextView) findViewById(tv_yes);
        tvEnterWolun = (TextView) findViewById(tv_enter_wolun);
        rlConfirm = (RelativeLayout) findViewById(R.id.rl_confirm);
        rlWolun = (RelativeLayout) findViewById(R.id.rl_wolun);

        tvBack.setOnClickListener(this);
        tvEnterWolun.setOnClickListener(this);
        tvDisappear.setOnClickListener(this);
        //        tvYes.setOnClickListener(this);
        tvYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!first) {
                    first = true;
                    getYes();
                    Log.d("111","queren");
                }
                rlConfirm.setVisibility(View.GONE);
                return true;
            }
        });

    }

    /**
     * 请求排位
     */
    private void getWolun() {
        new Thread(getWolunRunnable).start();
    }

    /**
     * 请求排位
     */
    private Runnable getWolunRunnable = new Runnable() {

        @SuppressLint("HandlerLeak")
        private Handler getWolunHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0:
                        execute(msg);
                        break;

                    default:
                        break;
                }
            }

            private void execute(Message msg) {
                try {
                    ArrayList<Wolun>  wolunList = new ArrayList<>();

                    JSONObject resultJson = new JSONObject(msg.obj.toString());
                    Log.d("111",resultJson.toString());
                    if (!resultJson.get("success").toString().equals("true")) {
                        Toast.makeText(TurbineFirstActivity.this,resultJson.get("errorMsg").toString(),Toast.LENGTH_LONG).show();
                    }
                    rlWolun.setVisibility(View.VISIBLE);
                    JSONArray dataArray = resultJson.getJSONArray("data");
                    MyApplication.totalCount = dataArray.length();
                    Log.d("12",dataArray.length()+"");
                    {   MyApplication.levelCount = (int)(dataArray.length() / 3) + 1;

                        MyApplication.lastCount = dataArray.length() % 3;
                        Log.d("1111",(int)(1 % 3) + "");
                        Log.d("1111",(int)(2 % 3) + "");
                        Log.d("1111",(int)(4 % 3) + "");
                        Log.d("1111",(int)(5 % 3) + "");
                    }
//                    String[] array = new String[]{"1","2","3","4","5","6","7","8","9","10"};
                    if (MyApplication.totalCount != 0) {
                        for (int i = 0; i < MyApplication.levelCount ; i++) {
//                            Log.d("1111","ll  :" + (int)(array.length / 3));
                            Wolun wolun = new Wolun();
//                            if (array.length % 3  == 1) {
//                                for (int j = 0;j<array.length / 3;j++) {
                                    if (i<MyApplication.levelCount -1) {
                                        Log.d("1111","ll  :" + (MyApplication.levelCount));
                                        wolun.setTv00(dataArray.getString(((i * 3) + 0)) + "号");
                                        wolun.setTv01(dataArray.getString(((i * 3) + 1)) + "号");
                                        wolun.setTv02(dataArray.getString(((i* 3) + 2)) + "号");
                                        wolunList.add(wolun);
                                    } else {
                                        if (MyApplication.lastCount==1) {
                                            wolun.setTv00(dataArray.getString(((i * 3) + 0)) + "号");
                                            wolunList.add(wolun);
                                        } else if (MyApplication.lastCount==2) {
                                            wolun.setTv00(dataArray.getString(((i * 3) + 0)) + "号");
                                            wolun.setTv01(dataArray.getString(((i * 3) + 1)) + "号");
                                            wolunList.add(wolun);
                                        } else if (MyApplication.lastCount==0) {
//                                                wolun.setTv00(dataArray.getString(((i * 3) -1)) + "号");
//                                                wolun.setTv01(dataArray.getString(((i * 3) + 0)) + "号");
//                                                wolun.setTv02(dataArray.getString(((i * 3) + 2)) + "号");
                                            }
//                                    }
                                }


                        }
                    }
                    wolunAdapter = new WolunAdapter(TurbineFirstActivity.this,wolunList,R.layout.layout_item_wolun);
                    lvWolun.setAdapter(wolunAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            };
        };

        @Override
        public void run() {
            URLServer server = new URLServer(getWolunHandler);
            // TODO token令牌 page页数 pageSize每页显示10条
            server.getWolun(MyApplication.getToken());
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case tv_back:
                TurbineFirstActivity.this.finish();
                break;
            case tv_enter_wolun:
                getPrice();
                break;
            case tv_disappear:
                rlConfirm.setVisibility(View.GONE);
                break;
            case tv_yes:

                break;

        }
    }

    public void getPrice() {
        new Thread(priceRunnable).start();
    }
    public void getYes() {
        new Thread(yesRunnable).start();
    }
}
