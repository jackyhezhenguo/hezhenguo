package project.yidun.com.yidun.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import project.yidun.com.yidun.MyApplication;
import project.yidun.com.yidun.R;
import project.yidun.com.yidun.entity.Wolun;

import static project.yidun.com.yidun.MyApplication.totalCount;

/**
 * Created by hezhenguo on 2017/3/9.
 */

public class WolunAdapter extends CommonAdapter<Wolun> {

    private TextView tv00;
    private TextView tv01;
    private TextView tv02;
    private RelativeLayout rl00;
    private RelativeLayout rl01;
    private RelativeLayout rl02;

    public WolunAdapter(Context context, List<Wolun> list, int resID) {
        super(context, list, resID);
    }

    @Override
    public void setItemView(ViewHolder vh, int position, List<Wolun> list) {
        tv00 = (TextView) vh.get(R.id.tv_00);
        tv01 = (TextView) vh.get(R.id.tv_01);
        tv02 = (TextView) vh.get(R.id.tv_02);
        rl00 = (RelativeLayout) vh.get(R.id.rl_00);
        rl01 = (RelativeLayout) vh.get(R.id.rl_01);
        rl02 = (RelativeLayout) vh.get(R.id.rl_02);
        if (totalCount != 0) {
            if (position < MyApplication.levelCount + 1) {

                    tv00.setVisibility(View.VISIBLE);
                    tv01.setVisibility(View.VISIBLE);
                    tv02.setVisibility(View.VISIBLE);
                    tv00.setText(list.get(position).getTv00());
                    tv01.setText(list.get(position).getTv01());
                    tv02.setText(list.get(position).getTv02());
                } else {
                    if (MyApplication.lastCount == 1) {
                        tv00.setVisibility(View.VISIBLE);
                        tv00.setText(list.get(position).getTv00());
                        rl01.setVisibility(View.GONE);
                        rl02.setVisibility(View.GONE);
                    } else if (MyApplication.lastCount==2) {
                        tv00.setVisibility(View.VISIBLE);
                        tv01.setVisibility(View.VISIBLE);
                        tv00.setText(list.get(position).getTv00());
                        tv01.setText(list.get(position).getTv01());
                        rl02.setVisibility(View.GONE);
                    } else if (MyApplication.lastCount==0) {
//                        tv00.setVisibility(View.VISIBLE);
//                        tv01.setVisibility(View.VISIBLE);
//                        tv02.setVisibility(View.VISIBLE);
//                        tv00.setText(list.get(position).getTv00());
//                        tv01.setText(list.get(position).getTv01());
//                        tv02.setText(list.get(position).getTv02());
                }
            }

        }
    }
}
