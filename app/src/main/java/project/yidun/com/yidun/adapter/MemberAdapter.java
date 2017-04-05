package project.yidun.com.yidun.adapter;

import android.content.Context;
import android.widget.TextView;

import java.util.List;

import project.yidun.com.yidun.MyApplication;
import project.yidun.com.yidun.R;
import project.yidun.com.yidun.entity.Member;

/**
 * Created by hezhenguo on 2017/3/9.
 */

public class MemberAdapter extends CommonAdapter<Member> {

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    public MemberAdapter(Context context, List<Member> list, int resID) {
        super(context, list, resID);
    }

    @Override
    public void setItemView(ViewHolder vh, int position, List<Member> list) {
        tv1 = (TextView) vh.get(R.id.tv_name1);
        tv2 = (TextView) vh.get(R.id.tv_name2);
        tv3 = (TextView) vh.get(R.id.tv_name3);

        if (MyApplication.level == 1) {
            tv1.setText(list.get(position).getTv1());
            tv2.setText("");
            tv3.setText("");
        } else if (MyApplication.level == 2) {
            tv2.setText(list.get(position).getTv2());
            // 每次不论显示第几个，在这里把所有它旁边的都设置为不显示东西就行了
            tv1.setText("");
            tv3.setText("");
        } else if (MyApplication.level == 3) {
            tv3.setText(list.get(position).getTv3());
            tv1.setText("");
            tv2.setText("");
        }
    }
}
