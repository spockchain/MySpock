package com.spockchain.wallet.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseFragment;
import com.spockchain.wallet.ui.activity.ContactsActivity;
import com.spockchain.wallet.ui.activity.NetSettingActivity;
import com.spockchain.wallet.ui.activity.TransactionsActivity;
import com.spockchain.wallet.ui.activity.WalletMangerActivity;

import butterknife.BindView;
import butterknife.OnClick;


public class MineFragment extends BaseFragment {


    @BindView(R.id.tv_version_name)
    TextView tvVersion;

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
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersion.append(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.lly_wallet_manage, R.id.lly_trade_recode, R.id.lly_contacts, R.id.github_website, R.id.lly_system_setting})
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
            case  R.id.lly_system_setting:
                intent = new Intent(getActivity(), NetSettingActivity.class);
                startActivity(intent);
                break;
        }
    }
}
