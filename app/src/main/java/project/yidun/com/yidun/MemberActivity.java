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

import java.util.ArrayList;

import project.yidun.com.yidun.adapter.MemberAdapter;
import project.yidun.com.yidun.entity.Member;
import project.yidun.com.yidun.url.URLServer;

import static project.yidun.com.yidun.R.id.tv_1;
import static project.yidun.com.yidun.R.id.tv_3;
import static project.yidun.com.yidun.R.id.tv_back;
import static project.yidun.com.yidun.R.id.tv_first;
import static project.yidun.com.yidun.R.id.tv_second;
import static project.yidun.com.yidun.R.id.tv_third;

public class MemberActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView tvBack;
    private TextView tvFirst;
    private TextView tvSecond;
    private TextView tvThird;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private boolean changeLevel = false;//是否改变level
    private static String select = "1";
    private MemberAdapter ma;
    private ListView lvMember;
    private ArrayList<Member> memberList = new ArrayList<Member>();
    private String[] memberarray = new String[]{};
    private int page = 0;

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
        setContentView(R.layout.activity_member);


    }

    @Override
    protected void onResume() {
        super.onResume();
        select = "1";
        setViews();
        setAdapter();
        getMember();
    }

    private void setAdapter() {
        ma = new MemberAdapter(MemberActivity.this,memberList,R.layout.layout_item_member);
        lvMember.setAdapter(ma);
        setSelectColor(1);
    }

    private void setViews () {
        tvBack = (ImageView) findViewById(R.id.tv_back);
        lvMember = (ListView) findViewById(R.id.lv_member);
        lvMember.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //判断状态
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
//                        scrollFlag = false;
                        // 判断滚动到底部 、position是从0开始算起的
                        if (lvMember.getLastVisiblePosition() == (lvMember
                                .getCount() - 1)) {
                            Log.d("111","dibu");
                            page += 1;
                            getMember();

                        }
                        // 判断滚动到顶部
                        if (lvMember.getFirstVisiblePosition() == 0) {

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
        tvFirst = (TextView) findViewById(tv_first);
        tv1 = (TextView) findViewById(tv_1);
        tvSecond = (TextView) findViewById(R.id.tv_second);
        tv2 = (TextView) findViewById(R.id.tv_2);
        tvThird = (TextView) findViewById(tv_third);
        tv3 = (TextView) findViewById(tv_3);

        tvBack.setOnClickListener(this);
        tvFirst.setOnClickListener(this);
        tvSecond.setOnClickListener(this);
        tvThird.setOnClickListener(this);
    }

    private void getMember() {
        new Thread(getMemberRunnable).start();
    }

    /**
     * 请求下线成员列表
     */
    private Runnable getMemberRunnable = new Runnable() {

        @SuppressLint("HandlerLeak")
        private Handler getMemberHandler = new Handler() {
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

//                     得到指定JSON key对象的value对象
//                    JSONObject dataObject = resultJson.getJSONObject("data");
//                    Log.d("info", "Data=" + dataObject.toString());
//                    pageSize = dataObject.getInt("totalPages");
                    // TODO 数据已获取成功
                    JSONArray contentArray = resultJson.getJSONArray("content");
                    Member member = null;
                    JSONObject itemObject = null;
//                    if (changeLevel) {
//                        memberList = new ArrayList<Member>();
//                        changeLevel = false;
//                    }
//                    for(int i=0;i<20;i++){
//                            member = new Member();
//                            if (MyApplication.level == 1) {
//
//                                member.setTv1("帅哥");
//                            } else if (MyApplication.level == 2) {
//
//                                member.setTv2("中帅哥");
//                            } else if (MyApplication.level == 3) {
//                                member.setTv3("小帅哥");
//                            }
//
//                            memberList.add(member);
//                    }
                    if (changeLevel) {
                        memberList = new ArrayList<Member>();
                        changeLevel = false;
                    }
                    for(int i=0;i<contentArray.length();i++){
                        itemObject = contentArray.getJSONObject(i);
                        if(itemObject!=null){
                            member = new Member();
                            if (MyApplication.level == 1) {
                                Log.d("111","2");
                                member.setTv1(itemObject.getString("nickname"));
                            } else if (MyApplication.level == 2) {
                                member.setTv2(itemObject.getString("nickname"));
                            } else if (MyApplication.level == 3) {
                                member.setTv3(itemObject.getString("nickname"));
                            }

//                            Log.d("555",itemObject.getString("nickname"));
                            memberList.add(member);
                        }
                        Log.d("111","3");
                    }
                    ma.setList(memberList);
                    ma.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            };
        };

        @Override
        public void run() {
            URLServer server = new URLServer(getMemberHandler);
            // TODO token令牌 page页数 pageSize每页显示10条
            server.getDownStaffMember(MyApplication.getToken(), MyApplication.level + "",page, 20);
        }
    };



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case tv_back:
                MemberActivity.this.finish();
                break;
            case tv_first:
                setSelectColor(1);
                select = "1";
                changeLevel = true;
                getMember();
                break;
            case tv_second:
                setSelectColor(2);
                select = "2";
                changeLevel = true;
                getMember();
                break;
            case tv_third:
                setSelectColor(3);
                select = "3";
                changeLevel = true;
                getMember();
                break;

        }
    }
    private void setSelectColor(int n) {
        switch (n) {
            case 1:
                tv1.setVisibility(View.VISIBLE);
                tvFirst.setTextColor(Color.parseColor("#287FC7"));
                tv2.setVisibility(View.INVISIBLE);
                tvSecond.setTextColor(Color.parseColor("#7A7A7A"));
                tv3.setVisibility(View.INVISIBLE);
                tvThird.setTextColor(Color.parseColor("#7A7A7A"));
                MyApplication.level = 1;
                break;
            case 2:
                tv2.setVisibility(View.VISIBLE);
                tvSecond.setTextColor(Color.parseColor("#287FC7"));
                tv1.setVisibility(View.INVISIBLE);
                tvFirst.setTextColor(Color.parseColor("#7A7A7A"));
                tv3.setVisibility(View.INVISIBLE);
                tvThird.setTextColor(Color.parseColor("#7A7A7A"));
                MyApplication.level = 2;
                break;

            case 3:
                tv3.setVisibility(View.VISIBLE);
                tvThird.setTextColor(Color.parseColor("#287FC7"));
                tv1.setVisibility(View.INVISIBLE);
                tvFirst.setTextColor(Color.parseColor("#7A7A7A"));
                tv2.setVisibility(View.INVISIBLE);
                tvSecond.setTextColor(Color.parseColor("#7A7A7A"));
                MyApplication.level = 3;
                break;
        }
    }
}
