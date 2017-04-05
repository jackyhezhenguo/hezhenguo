package project.yidun.com.yidun.adapter;

import android.content.Context;
import android.widget.TextView;

import java.util.List;

import project.yidun.com.yidun.R;
import project.yidun.com.yidun.entity.Account;

/**
 * Created by hezhenguo on 2017/3/9.
 */

public class AccountAdapter extends CommonAdapter<Account> {

    private TextView tvTime;
    private TextView tvMoney;
    private TextView tvType;

    public AccountAdapter(Context context, List<Account> list, int resID) {
        super(context, list, resID);
    }

    @Override
    public void setItemView(ViewHolder vh, int position, List<Account> list) {
        tvTime = (TextView) vh.get(R.id.tv_time);
        tvMoney = (TextView) vh.get(R.id.tv_money);
        tvType = (TextView) vh.get(R.id.tv_type);

        tvTime.setText(list.get(position).getTime());
        tvMoney.setText(list.get(position).getMoney());
        tvType.setText(list.get(position).getType());
    }
}
