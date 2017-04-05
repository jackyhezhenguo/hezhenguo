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
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import project.yidun.com.yidun.adapter.AccountAdapter;
import project.yidun.com.yidun.entity.Account;
import project.yidun.com.yidun.url.URLServer;

import static project.yidun.com.yidun.R.id.tv_back;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener{

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private ImageView tvBack;
    private TextView tvLeft;
    private TextView tvRight;
    private TextView tvMy;
    private TextView tvOthers;
    private int page = 0;

    private AccountAdapter aa;
    private ListView lvAccount;
    private ArrayList<Account> accountList = new ArrayList<Account>();
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
        setContentView(R.layout.activity_account);

        setViews();
        setAdapter();
        getIncome();
    }

    private void setAdapter() {
        aa = new AccountAdapter(AccountActivity.this,accountList,R.layout.layout_item_account);
        lvAccount.setAdapter(aa);
    }

    private void getIncome() {
        new Thread(getIncomRunnable).start();
    }

    /**
     * 请求收入数据
     */
    private Runnable getIncomRunnable = new Runnable() {

        @SuppressLint("HandlerLeak")
        private Handler getIncomeHandler = new Handler() {
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
                int pageSize = 1;
                try {
                    JSONObject resultJson = new JSONObject(msg.obj.toString());
                    Log.d("111",resultJson.toString());
                    // 得到指定JSON key对象的value对象
//                    JSONObject dataObject = resultJson.getJSONObject("data");
//                    Log.d("info", "Data=" + dataObject.toString());
//                    pageSize = dataObject.getInt("totalPages");
                    // TODO 数据已获取成功
                    JSONArray contentArray = resultJson.getJSONArray("content");
                    accountList = new ArrayList<>();
                    Account account = null;
                    JSONObject itemObject = null;
                    for(int i=0;i<contentArray.length();i++){
                        itemObject = contentArray.getJSONObject(i);
                        if(itemObject!=null){
                            account = new Account();
                            account.setTime(getTime(itemObject.getLong("time")));
                            account.setMoney(itemObject.getString("yield") + "元");
                            account.setType(getType(itemObject.getString("type").toString()));
//                            Log.d("555",itemObject.getString("mobile"));
//                            member.setTv1(itemObject.getString("nickname"));
//                            member.setTv2(itemObject.getInt("aqi"));
//                            member.setTv3(itemObject.getInt("aqi"));
                            accountList.add(account);
                        }
                    }
                    aa.setList(accountList);
                    aa.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            };
        };

        @Override
        public void run() {
            URLServer server = new URLServer(getIncomeHandler);
            // TODO token令牌 page页数 pageSize每页显示20条
            server.getIncome(MyApplication.getToken(),page, 20);
        }
    };
    private String getTime(long time){

        return format.format(time);
    }
private String getType(String type){
    if (type.equals("STEP")) {
        return "会员收益";
    }
    if (type.equals("RISE")) {
        return "晋级收益";
    }
    if (type.equals("LEVEL")) {
        return "满层奖励";
//        return "层级收益";
    }
    if (type.equals("THANKSGIVING")) {
        return "感恩收益";
    }
    if (type.equals("RECHARGE")) {
        return "充值收益";
    }
    return "";
}

    private void setViews () {
        tvBack = (ImageView) findViewById(R.id.tv_back);
        lvAccount = (ListView) findViewById(R.id.lv_account);
        lvAccount.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //判断状态
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
//                        scrollFlag = false;
                        // 判断滚动到底部 、position是从0开始算起的
                        if (lvAccount.getLastVisiblePosition() == (lvAccount
                                .getCount() - 1)) {
                            Log.d("111","dibu");
                            page += 1;
                            getIncome();

                        }
                        // 判断滚动到顶部
                        if (lvAccount.getFirstVisiblePosition() == 0) {

                            //TODO
                        }

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
//                        scrollFlag = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        // 当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时，即滚动时
//                        scrollFlag = true;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //当滑动时
//                if (scrollFlag
//                        ) {
//                    if (firstVisibleItem < lastVisibleItemPosition) {
//                        // 上滑
//                        //TODO
//                    } else if (firstVisibleItem > lastVisibleItemPosition) {
//                        // 下滑
//                        //TODO
//                    } else {
//                        return;
//                    }
//                    lastVisibleItemPosition = firstVisibleItem;//更新位置


//            }
            }
        });

        tvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case tv_back:
                AccountActivity.this.finish();
                break;

        }
    }
}
