package com.spockchain.wallet.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseFragment;
import com.spockchain.wallet.domain.ETHWallet;
import com.spockchain.wallet.interact.CreateWalletInteract;
import com.spockchain.wallet.utils.ToastUtils;
import com.tencent.stat.StatService;

import java.util.Properties;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ImportPrivateKeyFragment extends BaseFragment {
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.et_wallet_name)
    EditText etWalletName;
    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.et_wallet_pwd_again)
    EditText etWalletPwdAgain;
    @BindView(R.id.et_wallet_pwd_reminder_info)
    EditText etWalletPwdReminderInfo;
    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.lly_wallet_agreement)
    LinearLayout llyWalletAgreement;
    @BindView(R.id.btn_load_wallet)
    TextView btnLoadWallet;
    @BindView(R.id.btn_help)
    TextView btnHelp;

    CreateWalletInteract createWalletInteract;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_load_wallet_by_private_key;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        createWalletInteract = new CreateWalletInteract();
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.btn_load_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load_wallet:
                String privateKey = etPrivateKey.getText().toString().trim();
                String name = etWalletName.getText().toString().trim();
                String walletPwd = etWalletPwd.getText().toString().trim();
                String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(privateKey, name, walletPwd, confirmPwd, pwdReminder);
                if (verifyWalletInfo) {
                    showDialog(getString(R.string.loading_wallet_tip));
                    logEvent();
                    createWalletInteract.loadWalletByPrivateKey(privateKey, walletPwd, name).subscribe(this::loadSuccess, this::onError);
                }
                break;
        }
    }

    private void logEvent() {
        Properties prop = new Properties();
        prop.setProperty("method", "private key");
        StatService.trackCustomKVEvent(getContext(), "importWalletStart", prop);
    }

    private boolean verifyInfo(String privateKey, String name, String walletPwd, String confirmPwd, String pwdReminder) {
        if (TextUtils.isEmpty(privateKey)) {
            ToastUtils.showToast(R.string.load_wallet_by_private_key_input_tip);
            return false;
        } else if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast(R.string.load_wallet_by_private_key_wallet_name_invalid);
            return false;
        } else if (TextUtils.isEmpty(walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
            // 同时判断强弱
            return false;
        } else if (walletPwd.length() < 9) {
            ToastUtils.showToast(R.string.load_wallet_by_private_key_wallet_pwd_too_short);
            return false;
        } else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        }
        return true;
    }

    public void loadSuccess(ETHWallet wallet) {
        Properties prop = new Properties();
        prop.setProperty("method", "private key");
        StatService.trackCustomKVEvent(getContext(), "importWalletSuccess", prop);

        ToastUtils.showToast(getString(R.string.load_wallet_success));
        dismissDialog();

        getActivity().finish();

    }

    private void onError(Throwable error) {
        ToastUtils.showToast(R.string.load_wallet_by_private_key_input_tip);
        dismissDialog();
    }


}
