package com.spockchain.wallet.ui.adapter;

import android.content.Context;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.CommonAdapter;
import com.spockchain.wallet.base.ViewHolder;
import com.spockchain.wallet.domain.ETHWallet;

import java.util.List;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class WalletManagerAdapter extends CommonAdapter<ETHWallet> {
    public WalletManagerAdapter(Context context, List<ETHWallet> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, ETHWallet wallet) {
        holder.setText(R.id.tv_wallet_name,wallet.getName());
        holder.setText(R.id.tv_wallet_address,wallet.getAddress());
    }
}
