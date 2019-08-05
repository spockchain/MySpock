package com.spockchain.wallet.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseFragment;
import com.spockchain.wallet.ui.activity.ContactsActivity;
import com.spockchain.wallet.ui.activity.HelpActivity;
import com.spockchain.wallet.ui.activity.MessageCenterActivity;
import com.spockchain.wallet.ui.activity.SystemSettingActivity;
import com.spockchain.wallet.ui.activity.TransactionsActivity;
import com.spockchain.wallet.ui.activity.WalletMangerActivity;

import butterknife.OnClick;


public class MineFragment extends BaseFragment {
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
    }

    @OnClick({R.id.lly_wallet_manage, R.id.lly_trade_recode, R.id.lly_contacts, R.id.github_website})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lly_wallet_manage:
                intent = new Intent(getActivity(), WalletMangerActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_trade_recode:
                intent = new Intent(getActivity(), TransactionsActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_contacts:
                intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.github_website:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/spockchain/MySpock"));
                startActivity(intent);
                break;
        }
    }
}
