package project.yidun.com.yidun.adapter;

import android.content.Context;
import android.widget.TextView;

import java.util.List;

import project.yidun.com.yidun.R;
import project.yidun.com.yidun.entity.Invitation;

/**
 * Created by hezhenguo on 2017/3/9.
 */

public class InvitationAdapter extends CommonAdapter<Invitation> {

    private TextView tvName;
    private TextView tvTime;

    public InvitationAdapter(Context context, List<Invitation> list, int resID) {
        super(context, list, resID);
    }

    @Override
    public void setItemView(ViewHolder vh, int position, List<Invitation> list) {
        tvName = (TextView) vh.get(R.id.tv_down_name);
        tvTime = (TextView) vh.get(R.id.tv_date);

        tvName.setText(list.get(position).getTvName());
        tvTime.setText(list.get(position).getTvTime());
    }
}
