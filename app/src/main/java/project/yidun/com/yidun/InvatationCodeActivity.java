package project.yidun.com.yidun;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import project.yidun.com.yidun.adapter.InvitationAdapter;
import project.yidun.com.yidun.entity.Invitation;
import project.yidun.com.yidun.url.URLServer;

import static project.yidun.com.yidun.R.id.tv_back;
import static project.yidun.com.yidun.R.id.tv_invitation;
import static project.yidun.com.yidun.R.id.tv_know_more;

public class InvatationCodeActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView tvBack;
    private TextView tvInvitation;
    private TextView tvKnowMore;

    private InvitationAdapter ia;
    private Invitation invitation;
    private ListView lvInvitation;
    private ArrayList<Invitation> invitationList = new ArrayList<Invitation>();
    private String[] invitationarray = new String[]{};
    private int page = 0;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private Handler inviHandler = new Handler(){
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
//                if (!resultJson.get("code").toString().equals(URLServer.EXECUTED_SUCCESS)) {
                if (resultJson.getString("success").toString().equals("true")) {
                    Toast.makeText(InvatationCodeActivity.this,"获取成功",Toast.LENGTH_LONG).show();
                    if (MyApplication.downCount.equals(resultJson.getString("data").toString()) || MyApplication.balance.equals(MyApplication.downCount.equals(resultJson.getString("data").toString()))) {
                        getInviCode();
                    }
                    Log.d("444",resultJson.getString("data").toString());
                    tvInvitation.setText(resultJson.getString("data").toString());
                    getInvitation();
                } else {
                    Toast.makeText(InvatationCodeActivity.this,resultJson.getString("errorMsg").toString(),Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_invatation_code);
        setViews();
        setAdapter();

    }
    private void setAdapter() {
        ia = new InvitationAdapter(InvatationCodeActivity.this,invitationList,R.layout.layout_item_invi);
        lvInvitation.setAdapter(ia);
    }
    private void setViews () {
        tvBack = (ImageView) findViewById(R.id.tv_back);
        tvKnowMore = (TextView) findViewById(R.id.tv_know_more);
        tvInvitation = (TextView) findViewById(R.id.tv_invitation);
        lvInvitation = (ListView) findViewById(R.id.lv_invitation);
        lvInvitation.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //判断状态
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
//                        scrollFlag = false;
                        // 判断滚动到底部 、position是从0开始算起的
                        if (lvInvitation.getLastVisiblePosition() == (lvInvitation
                                .getCount() - 1)) {
                            Log.d("111","dibu");
//                            page += 1;
                            getInvitation();

                        }
                        // 判断滚动到顶部
                        if (lvInvitation.getFirstVisiblePosition() == 0) {

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
        tvKnowMore.setOnClickListener(this);
        tvInvitation.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInviCode();

    }
    private void getInvitation() {
        new Thread(getInvitationRunnable).start();
    }
    /**
     * 请求邀请过的下线成员列表
     */
    private Runnable getInvitationRunnable = new Runnable() {

        @SuppressLint("HandlerLeak")
        private Handler getInvitationHandler = new Handler() {
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
                    Invitation invitation = null;
                    JSONObject itemObject = null;
                    invitationList = new ArrayList<>();
                    for(int i=0;i<contentArray.length();i++){
                        itemObject = contentArray.getJSONObject(i);
                        if(itemObject!=null){
                            invitation = new Invitation();
//                                invitation.setTvName(itemObject.getString("nickname"));
                            if (!itemObject.getString("invitee").equals("null")) {
                                Log.d("444", itemObject.getJSONObject("invitee").toString());

                                JSONObject inObj = new JSONObject(itemObject.getJSONObject("invitee").toString());
                                if (inObj.getString("nickname") != null) {
                                    invitation.setTvTime(getTime(itemObject.getLong("inviteTime")));
                                    invitation.setTvName(inObj.getString("nickname"));
                                }
                                invitationList.add(invitation);
                            }

                        }
                    }
                    ia.setList(invitationList);
                    ia.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            };
        };

        @Override
        public void run() {
            URLServer server = new URLServer(getInvitationHandler);
            // TODO token令牌 page页数 pageSize每页显示20条
            server.getDownStaffInvitation(MyApplication.getToken(),0, 8);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case tv_back:
                InvatationCodeActivity.this.finish();
                break;
            case tv_know_more:
                InvatationCodeActivity.this.startActivity(new Intent(InvatationCodeActivity.this,KnowMoreActivity.class));
                break;
            case tv_invitation:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                cm.setText(tvInvitation.getText().toString());
                Toast.makeText(InvatationCodeActivity.this,"恭喜，复制成功",Toast.LENGTH_LONG).show();
                break;

        }
    }

    private void getInviCode() {
        new Thread(inviRunnable).start();

    }

    /**
     * 生成邀请码
     */
    public Runnable inviRunnable = new Runnable() {
        public void run() {
            URLServer urlServer = new URLServer(inviHandler);
//            Log.e("token", MyApplication.getToken());
            urlServer.buildInvites(MyApplication.getToken());
        }
    };


    private String getTime(long time){

        return format.format(time);
    }

}
