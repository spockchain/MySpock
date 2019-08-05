package com.spockchain.wallet.ui.adapter;

import android.content.Context;
import android.graphics.Color;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.CommonAdapter;
import com.spockchain.wallet.base.ViewHolder;
import com.spockchain.wallet.domain.ETHWallet;

import java.util.List;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class DrawerWalletAdapter extends CommonAdapter<ETHWallet> {

    private int currentWalletPosition = 0;

    public DrawerWalletAdapter(Context context, List<ETHWallet> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setCurrentWalletPosition(int currentWalletPosition) {
        this.currentWalletPosition = currentWalletPosition;
        notifyDataSetChanged();
    }

    @Override
    public void convert(ViewHolder holder, ETHWallet wallet) {
        boolean isCurrent = wallet.getIsCurrent();
        int position = holder.getPosition();
        if (isCurrent) {
            currentWalletPosition = position;
            holder.getView(R.id.lly_wallet).setBackgroundColor(mContext.getResources().getColor(R.color.item_divider_bg_color));
        } else {
            holder.getView(R.id.lly_wallet).setBackgroundColor(Color.WHITE);
        }
        holder.setText(R.id.tv_wallet_name, wallet.getName());
    }
}
