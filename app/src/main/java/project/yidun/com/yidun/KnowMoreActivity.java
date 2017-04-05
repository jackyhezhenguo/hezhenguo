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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import project.yidun.com.yidun.adapter.InvitationAdapter;
import project.yidun.com.yidun.entity.Invitation;
import project.yidun.com.yidun.url.URLServer;

public class KnowMoreActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView tvBack;

    private  ListView lvMore;
    private  int page;
    private InvitationAdapter ia;
    private Invitation invitation;
    private ArrayList<Invitation> invitationList = new ArrayList<Invitation>();
    private String[] invitationarray = new String[]{};
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

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
        setContentView(R.layout.activity_know_more);

        setViews();
        setAdapter();
        getLvMore();
    }

    private void setAdapter() {
        ia = new InvitationAdapter(KnowMoreActivity.this,invitationList,R.layout.layout_item_invi);
        lvMore.setAdapter(ia);
    }

    private void setViews() {
        tvBack = (ImageView) findViewById(R.id.tv_back);

        lvMore = (ListView) findViewById(R.id.lv_more);
        lvMore.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //判断状态
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
//                        scrollFlag = false;
                        // 判断滚动到底部 、position是从0开始算起的
                        if (lvMore.getLastVisiblePosition() == (lvMore
                                .getCount() - 1)) {
                            Log.d("111","dibu");
                            page += 1;
                            getLvMore();

                        }
                        // 判断滚动到顶部
                        if (lvMore.getFirstVisiblePosition() == 0) {

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

    private void getLvMore() {
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
            server.getDownStaffInvitation(MyApplication.getToken(),page, 20);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                KnowMoreActivity.this.finish();
            break;
        }
    }

    private String getTime(long time){

        return format.format(time);
    }
}
