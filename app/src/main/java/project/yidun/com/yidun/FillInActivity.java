package project.yidun.com.yidun;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import project.yidun.com.yidun.url.URLServer;
import project.yidun.com.yidun.utils.EditChangedListener;

public class FillInActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView tvBack;
    private LinearLayout llSubmit;
    private EditText etName;
    private EditText etPhoneNumber;
    private EditText etBankCardNumber;
    private EditText etBankAccountName;
    private EditText etBankLeavePhoneNumber;
    private EditText etBankWhich;

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
        } else {

        }
        setContentView(R.layout.activity_fill_in);
        setViews();
    }

    private void setViews() {
        tvBack = (ImageView) findViewById(R.id.tv_back);
        llSubmit = (LinearLayout) findViewById(R.id.ll_submit);

        etName = (EditText) findViewById(R.id.et_name);
        if (MyApplication.tvName != null) {
            etName.setText(MyApplication.tvName);
        }

        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        if (MyApplication.tvMobile != null) {
            Log.d("111","11111111");
            etPhoneNumber.setText(MyApplication.tvMobile);
        }
        etBankCardNumber = (EditText) findViewById(R.id.et_bank_card_number);
        if (MyApplication.tvCard != null) {
            etBankCardNumber.setText(MyApplication.tvCard);
        }
        etBankAccountName = (EditText) findViewById(R.id.et_bank_account_name);
        if (MyApplication.tvAccountName != null) {
            etBankAccountName.setText(MyApplication.tvAccountName);
        }
        etBankLeavePhoneNumber = (EditText) findViewById(R.id.et_bank_leave_phone_number);
        if (MyApplication.tvDiergeMobile != null) {
            etBankLeavePhoneNumber.setText(MyApplication.tvDiergeMobile);
        }
        etBankWhich = (EditText) findViewById(R.id.et_bank_which);
        etBankWhich.addTextChangedListener(new EditChangedListener(llSubmit));
        if (MyApplication.tvSubsidiaryBank != null) {
            etBankWhich.setText(MyApplication.tvSubsidiaryBank);
        }

        tvBack.setOnClickListener(this);
        llSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                FillInActivity.this.finish();
                break;
            case R.id.ll_submit:
                submit();
                break;

        }
    }

    private void submit() {
        new Thread(submitRunnable).start();
    }

    /**
     * 请求个人数据
     */
    private Runnable submitRunnable = new Runnable() {

        @SuppressLint("HandlerLeak")
        private Handler getMyDataHandler = new Handler() {
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
                    Log.d("111",resultJson.toString());


                    Toast.makeText(FillInActivity.this,"提交成功",Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            };
        };

        @Override
        public void run() {
            URLServer server = new URLServer(getMyDataHandler);
            // TODO token令牌 page页数 pageSize每页显示10条
            server.httpUrlConnectionPut(MyApplication.getToken(),MyApplication.userId,etName.getText().toString().trim(),etPhoneNumber.getText().toString().trim(),MyApplication.cardId,etBankCardNumber.getText().toString().trim(),etBankWhich.getText().toString().trim(),etBankAccountName.getText().toString().trim(),etBankLeavePhoneNumber.getText().toString().trim());
        }
    };
}
