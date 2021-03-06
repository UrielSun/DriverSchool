package com.drivingevaluate.adapter;

import android.content.Context;

import com.drivingevaluate.R;
import com.drivingevaluate.adapter.component.AutoRVAdapter;
import com.drivingevaluate.adapter.component.ViewHolder;
import com.drivingevaluate.model.LuckyMoney;
import com.drivingevaluate.util.DateUtils;
import com.drivingevaluate.util.StringUtil;

import java.util.List;

/**
 * Created by Yat3s on 8/16/15.
 * Email:hawkoyates@gmail.com
 */
public class LuckyMoneyAdapter extends AutoRVAdapter {

    public LuckyMoneyAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.item_luckmoney;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LuckyMoney luckyMoney = (LuckyMoney) list.get(position);
        holder.setTextView(R.id.name_money_tv, StringUtil.getPhoneStrWithStar(luckyMoney.getAccount()));
        holder.setTextView(R.id.time_money_tv, DateUtils.getDayDateStr(luckyMoney.getCreatedTime()));
        holder.setTextView(R.id.count_money_tv,"￥"+luckyMoney.getBalance()+ "元");
    }
}
