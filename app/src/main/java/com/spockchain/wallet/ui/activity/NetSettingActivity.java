package com.spockchain.wallet.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spockchain.wallet.MySpockApp;
import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseActivity;
import com.spockchain.wallet.entity.NetworkInfo;
import com.spockchain.wallet.repository.EthereumNetworkRepository;


import butterknife.BindView;
import butterknife.OnClick;

import static com.spockchain.wallet.C.SPOCK_MAIN_NETWORK_NAME;
import static com.spockchain.wallet.C.SPOCK_TEST_NETWORK_NAME;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class NetSettingActivity extends BaseActivity {


    EthereumNetworkRepository ethereumNetworkRepository;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    TextView tvBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;

    @BindView(R.id.iv_mainnet)
    ImageView ivMainnet;

    @BindView(R.id.iv_testnet)
    ImageView ivTestnet;

    private String networkName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_net_setting;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_net);
        rlBtn.setVisibility(View.VISIBLE);
        tvBtn.setText(R.string.language_setting_save);
    }

    @Override
    public void initDatas() {
        ethereumNetworkRepository = MySpockApp.repositoryFactory().ethereumNetworkRepository;

        networkName = ethereumNetworkRepository.getDefaultNetwork().name;

        if (SPOCK_MAIN_NETWORK_NAME.equals(networkName)) {
            ivMainnet.setVisibility(View.VISIBLE);
            ivTestnet.setVisibility(View.GONE);
        } else if (SPOCK_TEST_NETWORK_NAME.equals(networkName)) {
            ivMainnet.setVisibility(View.GONE);
            ivTestnet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_mainnet, R.id.rl_testnet, R.id.rl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_mainnet:
                networkName = SPOCK_MAIN_NETWORK_NAME;
                ivMainnet.setVisibility(View.VISIBLE);
                ivTestnet.setVisibility(View.GONE);
                break;
            case R.id.rl_testnet:
                networkName = SPOCK_TEST_NETWORK_NAME;
                ivMainnet.setVisibility(View.GONE);
                ivTestnet.setVisibility(View.VISIBLE);

                 break;
            case R.id.rl_btn:// 设置语言并保存
//                SharedPreferencesUtil.getInstance().putString("pref_rpcServer",networkName );

                NetworkInfo[] networks = ethereumNetworkRepository.getAvailableNetworkList();
                for (NetworkInfo networkInfo : networks) {
                    if (networkInfo.name.equals(networkName)) {
                        ethereumNetworkRepository.setDefaultNetworkInfo(networkInfo);
                    }
                }

                finish();
                break;
        }
    }
}
