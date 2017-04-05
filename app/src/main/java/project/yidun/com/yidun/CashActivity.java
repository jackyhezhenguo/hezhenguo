package project.yidun.com.yidun;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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

import static project.yidun.com.yidun.R.id.ll_tixian;
import static project.yidun.com.yidun.R.id.tv_back;
import static project.yidun.com.yidun.R.id.tv_cash;

public class CashActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView tvBack;
    private TextView tvBalance;
    private TextView tvCash;
    private EditText etName;
    private EditText etPhoneNumber;
    private EditText etBankCardNumber;
    private EditText etBankAccountName;
    private EditText etBankLeavePhoneNumber;
    private EditText etBankWhich;
    private EditText etGetCash;
    private LinearLayout llTixian;
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
        setContentView(R.layout.activity_cash);
       setViews();
    }
    private void setViews () {
        tvBack = (ImageView) findViewById(R.id.tv_back);
        tvBalance = (TextView) findViewById(R.id.tv_balance);
        if (MyApplication.balance != null) {
            tvBalance.setText(MyApplication.balance);
        }
        tvCash = (TextView) findViewById(R.id.tv_cash);
        etName = (EditText) findViewById(R.id.et_name);
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        etBankCardNumber = (EditText) findViewById(R.id.et_bank_card_number);
        etBankAccountName = (EditText) findViewById(R.id.et_bank_account_name);
        etBankLeavePhoneNumber = (EditText) findViewById(R.id.et_bank_leave_phone_number);
        etBankWhich = (EditText) findViewById(R.id.et_bank_which);
        llTixian = (LinearLayout) findViewById(R.id.ll_tixian);
        etGetCash = (EditText) findViewById(R.id.et_get_cash);
        etGetCash.addTextChangedListener(new EditChangedListener(llTixian));


        tvBack.setOnClickListener(this);
        llTixian.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case tv_back:
                CashActivity.this.finish();
                break;
            case ll_tixian:
                getIncome();
                break;

        }
    }

    /**
     * 请求提现
     */
    private void getIncome() {
        new Thread(getCashRunnable).start();
    }

    /**
     * 请求提现
     */
    private Runnable getCashRunnable = new Runnable() {

        @SuppressLint("HandlerLeak")
        private Handler getCashHandler = new Handler() {
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
            JSONObject resultJson = new JSONObject(msg.obj.toString());
            if (resultJson.getString("success").toString().equals("true")) {
                Toast.makeText(CashActivity.this, "提现成功", Toast.LENGTH_LONG).show();
                getBalanceLanou();
                etGetCash.setText("");
            } else {
                Toast.makeText(CashActivity.this, resultJson.getString("errorMsg").toString(), Toast.LENGTH_LONG).show();
                if (resultJson.get("errorType").toString().equals("lano.exception.BalanceNotEnoughException")) {
                    Toast.makeText(CashActivity.this,"余额不足，请联系后台服务人员充值",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Log.d("111",resultJson.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


    };
};

    @Override
    public void run() {
            URLServer server = new URLServer(getCashHandler);
            // TODO token令牌 page页数 pageSize每页显示10条
            server.getCash(MyApplication.getToken(),etGetCash.getText().toString());
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

    private Handler balanceHandler = new Handler(){
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
//                Log.d("111",resultJson.toString());
                if (resultJson.getString("success").toString().equals("true")) {
                    tvBalance.setText(resultJson.getDouble("data") + "");
                } else {
                    Toast.makeText(CashActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
                    if (resultJson.get("errorType").toString().equals("lano.exception.LoginTimeOutException")) {
                        MyApplication.isBack = false;
                        CashActivity.this.startActivity(new Intent(CashActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        CashActivity.this.finish();
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
