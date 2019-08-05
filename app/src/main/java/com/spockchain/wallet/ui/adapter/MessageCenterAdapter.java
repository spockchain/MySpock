package com.spockchain.wallet.ui.adapter;

import android.content.Context;

import com.spockchain.wallet.base.CommonAdapter;
import com.spockchain.wallet.base.ViewHolder;

import java.util.List;


public class MessageCenterAdapter extends CommonAdapter<String> {
    public MessageCenterAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, String s) {

    }
}
