package project.yidun.com.yidun;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import project.yidun.com.yidun.url.URLServer;
import project.yidun.com.yidun.utils.EditChangedListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvLogIn;
    private ImageView tvBack;
    private int time = 60;
    private Runnable runnable;
    private EditText etPhoneNumber;
    private EditText etInputVerificationCode;
    private TextView tvSendVerificationCode;
    private EditText etInputInvitationCode;
    private LinearLayout llEnterYidun;
    private Handler enterHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    execute(msg);
                    Log.d("111","ddd");
                    break;

            }
        }
        private void execute(Message msg){
            try {
                JSONObject resultJson = new JSONObject(msg.obj.toString());
                Log.d("111",resultJson.toString());
//                if (!resultJson.get("code").toString().equals(URLServer.EXECUTED_SUCCESS)) {
                if (resultJson.getString("success").toString().equals("true")) {
                    Log.d("111","ddd1");
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
//                    MyApplication.setToken(resultJson.getString("data"));
                    Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(i);

                } else {
                    Toast.makeText(RegisterActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
                }
                Log.d("111",msg.obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Handler mHandler = new Handler(){


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
                    Toast.makeText(RegisterActivity.this,"发送验证码成功",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
                    tvSendVerificationCode.setText("发送验证码");
                    tvSendVerificationCode.setTextColor(Color.parseColor("#000000"));
                    tvSendVerificationCode.setClickable(true);
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
        setContentView(R.layout.activity_register);


        setViews();

    }
    private void setViews () {
        tvLogIn = (TextView) findViewById(R.id.tv_log_in);
        tvLogIn.setText(Html.fromHtml("<u>"+"登录"+"</u>"));

        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        tvSendVerificationCode = (TextView) findViewById(R.id.tv_send_verification_code);
        etInputVerificationCode = (EditText) findViewById(R.id.et_input_verification_code);
        etInputInvitationCode = (EditText) findViewById(R.id.et_input_invitation_code);

        llEnterYidun = (LinearLayout) findViewById(R.id.ll_enter_yidun);
        etInputInvitationCode.addTextChangedListener(new EditChangedListener(llEnterYidun));

        tvLogIn.setOnClickListener(this);
        tvSendVerificationCode.setOnClickListener(this);
        llEnterYidun.setOnClickListener(this);

        runnable = new Runnable() {
            @Override
            public void run() {
                tvSendVerificationCode.setText((--time) + " s");
                if (time >= 1) {
                    mHandler.postDelayed(this, 1000);
                } else {
                    tvSendVerificationCode.setText("发送验证码");
                    tvSendVerificationCode.setTextColor(Color.parseColor("#000000"));
                    tvSendVerificationCode.setClickable(true);
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        {
            switch (view.getId()) {
                case R.id.tv_back:
                    RegisterActivity.this.finish();
                    break;
                case R.id.tv_log_in:
                    RegisterActivity.this.finish();
                    break;
                case R.id.tv_send_verification_code:
                    getSmsCode();
                    time = 60;
                    tvSendVerificationCode.setText("60 s");
                    tvSendVerificationCode.setTextColor(Color.parseColor("#4A4A4A"));
                    tvSendVerificationCode.setClickable(false);
                    mHandler.postDelayed(runnable,1000);
                    break;
                case R.id.ll_enter_yidun:
                    enterLanou();
                    break;
            }
        }
    }

    private void enterLanou() {
        new Thread(enterRunnable).start();

    }

    /**
     * 登录
     */
    public Runnable enterRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(enterHandler);
//            Log.e("token", MyApplication.getToken());
            urlServer.register(etInputInvitationCode.getText().toString().trim(),etPhoneNumber.getText().toString().trim(),etInputVerificationCode.getText().toString().trim());
        }
    };


//    http://1ta5871727.iask.in:39375/sms/login?mobile=13632691661

    private void getSmsCode() {
        new Thread(GetMapUpdateTimeRunnable).start();

    }

    /**
     * 获取验证码
     */
    public Runnable GetMapUpdateTimeRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(mHandler);
//            Log.e("token", MyApplication.getToken());
            urlServer.getSmsZhuce(etPhoneNumber.getText().toString().trim());
        }
    };







//    private void getRegistPhoneCode() {
//        // 向服务器发送获取验证码请求进行注册
//        // 判断网络
//        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
//            Toast.makeText(getApplicationContext(), "网络故障，请检查网络连接", Toast.LENGTH_LONG).show();
//            return;
//        }
//        // 1.判空
//        if (!etPhoneNumber.getText().toString().trim().isEmpty()) {
//            // 2.
//            RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
//            String url = URLServer.SERVER_ADDRESS + "/sms/userRegister/1/" + etPhoneNumber.getText().toString().trim();
////            String url = http://1ta5871727.iask.in:39375/sms/login?mobile=13632691661;
//            StringRequest request = new StringRequest(url, new Listener<String>() {
//
//                @Override
//                public void onResponse(String response) {
//                    Toast.makeText(getApplicationContext(), "发送成功	", Toast.LENGTH_SHORT).show();
//                    new Thread() {
//                        @SuppressLint("HandlerLeak")
//                        private Handler handler = new Handler() {
//                            public void handleMessage(Message msg) {
//                                String text = msg.obj.toString();
//                                if ((Integer) msg.obj == 0) {
//                                    btnGetPhoneCode.setClickable(true);
//                                    btnGetPhoneCode.setText("获取验证码");
//                                    btnGetPhoneCode.setBackgroundResource(R.drawable.shape_getcode_button);
//                                    btnGetPhoneCode.setTextColor(Color.parseColor("#FFFFFF"));
//                                } else {
//                                    btnGetPhoneCode.setClickable(false);
//                                    btnGetPhoneCode.setBackgroundResource(R.drawable.service_btn3_normal);
//                                    btnGetPhoneCode.setTextColor(Color.parseColor("#444444"));
//                                    btnGetPhoneCode.setText("重新发送(" + text + ")s");
//                                }
//                            }
//                        };
//
//                        @Override
//                        public void run() {
//                            int i = 60;
//                            while (i >= 0) {
//                                Message msg = new Message();
//                                msg.obj = i;
//                                i--;
//                                handler.sendMessage(msg);
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                        }
//                    }.start();
//                }
//            }, new ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    String msg = new String(error.networkResponse.data);
//                    Log.d("短信", msg);
//                    try {
//                        JSONObject json = new JSONObject(new String(error.networkResponse.data));
//                        ToastUtil.showShort(json.optString("exception", "系统错误！"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        ToastUtil.showShort( "系统错误！");
//                    }
//                }
//            });
//            mQueue.add(request);
//        }
//    }
}
