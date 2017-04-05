package project.yidun.com.yidun;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
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
import project.yidun.com.yidun.utils.SPUtil;

import static project.yidun.com.yidun.R.id.tv_back;
import static project.yidun.com.yidun.R.id.tv_register;
import static project.yidun.com.yidun.R.id.tv_send_verification_code;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView tvBack;
    private int time = 60;
    private Runnable runnable;
    private EditText etPhoneNumber;
    private EditText etInputVerificationCode;
    private TextView tvSendVerificationCode;
    private TextView tvRegister;
    private LinearLayout llEnterYidun;
    private Handler mHandler = new Handler(){
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
//                if (!resultJson.get("code").toString().equals(URLServer.EXECUTED_SUCCESS)) {
                if (resultJson.getString("success").toString().equals("true")) {
                    Toast.makeText(LoginActivity.this,"发送验证码成功",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
                    tvSendVerificationCode.setText("发送验证码");
                    tvSendVerificationCode.setTextColor(Color.parseColor("#000000"));
                    tvSendVerificationCode.setClickable(true);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
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
                Log.d("1010",resultJson.toString());
//                if (!resultJson.get("code").toString().equals(URLServer.EXECUTED_SUCCESS)) {
                if (resultJson.getString("success").toString().equals("true")) {
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                    Log.d("1010",resultJson.getJSONObject("data").getString("token").toString());
//                    MyApplication.inviteLimit = resultJson.getJSONObject("data").getString("inviteLimit").toString();
                    MyApplication.setToken(resultJson.getJSONObject("data").getString("token").toString());
                    SPUtil.putAndApply(getApplicationContext(), MyApplication.KEY_TOKEN, resultJson.getJSONObject("data").getString("token").toString());
                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(i);
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(LoginActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
                }
                Log.d("111",msg.obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((!SPUtil.get(getApplicationContext(), MyApplication.KEY_TOKEN, "") .equals("")) && MyApplication.isBack) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MyApplication.setToken((String) SPUtil.get(getApplicationContext(), MyApplication.KEY_TOKEN, ""));
            this.startActivity(new Intent(LoginActivity.this,MainActivity.class));
            LoginActivity.this.finish();
        }


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
        MyApplication.display = true;
        setContentView(R.layout.activity_log_in);
        setViews();
    }
    private void setViews () {
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        tvSendVerificationCode = (TextView) findViewById(tv_send_verification_code);
        etInputVerificationCode = (EditText) findViewById(R.id.et_input_verification_code);

        tvRegister = (TextView) findViewById(tv_register);
        llEnterYidun = (LinearLayout) findViewById(R.id.ll_enter_yidun);
        etInputVerificationCode.addTextChangedListener(new EditChangedListener(llEnterYidun));

//        tvBack.setOnClickListener(this);
        tvSendVerificationCode.setOnClickListener(this);
        llEnterYidun.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

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
        switch (view.getId()) {
            case tv_back:
                LoginActivity.this.finish();
                break;
            case tv_register:
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
                break;
            case tv_send_verification_code:
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
            urlServer.enter(etPhoneNumber.getText().toString().trim(),etInputVerificationCode.getText().toString().trim());
        }
    };

    private void getSmsCode() {
        new Thread(GetverRunnable).start();

    }

    /**
     * 获取验证码
     */
    public Runnable GetverRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(mHandler);
//            Log.e("token", MyApplication.getToken());
            urlServer.getSms(etPhoneNumber.getText().toString().trim());
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;

        }
        return false;
    }

}
