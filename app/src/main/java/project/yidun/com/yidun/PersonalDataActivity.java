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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import project.yidun.com.yidun.url.URLServer;

import static project.yidun.com.yidun.R.id.ll_left;
import static project.yidun.com.yidun.R.id.tv_back;
import static project.yidun.com.yidun.R.id.tv_edit;

public class PersonalDataActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView tvBack;
    private TextView tvEdit;
    private LinearLayout llFinish;
    private TextView tvName;
    private TextView tvMobile;
    private TextView tvCard;
    private TextView tvAccountName;
    private TextView tvDiergeMobile;
    private TextView tvSubsidiaryBank;

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
        setContentView(R.layout.activity_personal_data);
        setViews();

    }


    private void setViews () {
        tvBack = (ImageView) findViewById(R.id.tv_back);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvMobile = (TextView) findViewById(R.id.tv_mobile);
        tvCard = (TextView) findViewById(R.id.tv_card);
        tvAccountName = (TextView) findViewById(R.id.tv_account_name);
        tvDiergeMobile = (TextView) findViewById(R.id.tv_dierge_mobile);
        tvSubsidiaryBank = (TextView) findViewById(R.id.tv_subsidiary_bank);
        llFinish = (LinearLayout) findViewById(ll_left);

        tvBack.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        llFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case tv_back:
                PersonalDataActivity.this.finish();
                break;
            case tv_edit:
                Intent intent = new Intent(PersonalDataActivity.this,FillInActivity.class);
                startActivity(intent);
                break;
            case ll_left:

                break;

        }
    }

    private void getMyData() {
        new Thread(getMyDataRunnable).start();
    }

    /**
     * 请求个人数据
     */
    private Runnable getMyDataRunnable = new Runnable() {

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




                    MyApplication.userId = resultJson.get("id").toString();

                    tvName.setText(resultJson.get("nickname").toString());
                    MyApplication.tvName = resultJson.get("nickname").toString();

                    tvMobile.setText(resultJson.get("mobile").toString());
                    MyApplication.tvMobile = resultJson.get("mobile").toString();

                    JSONObject cardObject = new JSONObject(resultJson.get("card").toString());
                    MyApplication.cardId = cardObject.get("id").toString();

                    tvCard.setText(cardObject.get("account").toString());
                    MyApplication.tvCard = cardObject.get("account").toString();

                    tvAccountName.setText(cardObject.get("holder").toString());
                    Log.d("111","cardObject.get(\"holder\").toString()"+cardObject.get("holder").toString());
                    MyApplication.tvAccountName = cardObject.get("holder").toString();

                    tvDiergeMobile.setText(cardObject.get("phone").toString());
                    MyApplication.tvDiergeMobile = cardObject.get("phone").toString();

                    tvSubsidiaryBank.setText(cardObject.get("bankName").toString());
                    MyApplication.tvSubsidiaryBank = cardObject.get("bankName").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            };
        };

        @Override
        public void run() {
            URLServer server = new URLServer(getMyDataHandler);
            server.getPerson(MyApplication.getToken());
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        //这里做所有的数据更新的操作
        getMyData();
    }
}
