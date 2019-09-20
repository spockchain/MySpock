package com.spockchain.wallet.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseImportAccountFragment;
import com.spockchain.wallet.domain.ETHWallet;
import com.spockchain.wallet.interact.CreateWalletInteract;
import com.spockchain.wallet.utils.ETHWalletUtils;
import com.spockchain.wallet.utils.LogUtils;
import com.spockchain.wallet.utils.ToastUtils;
import com.spockchain.wallet.utils.UUi;
import com.spockchain.wallet.utils.WalletDaoUtils;
import com.spockchain.wallet.view.LoadWalletSelectStandardPopupWindow;
import com.tencent.stat.StatService;

import java.util.Properties;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ImportMnemonicFragment extends BaseImportAccountFragment {

    @BindView(R.id.et_mnemonic)
    EditText etMnemonic;
    @BindView(R.id.et_wallet_name)
    EditText etWalletName;
    @BindView(R.id.et_standard)
    EditText etStandard;
    @BindView(R.id.et_mnemonic_pwd)
    EditText etMnemonicPwd;
    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.et_wallet_pwd_again)
    EditText etWalletPwdAgain;
    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.lly_wallet_agreement)
    LinearLayout llyWalletAgreement;
    @BindView(R.id.btn_load_wallet)
    TextView btnLoadWallet;

    CreateWalletInteract createWalletInteract;

    private LoadWalletSelectStandardPopupWindow popupWindow;
    private String ethType = ETHWalletUtils.ETH_JAXX_TYPE;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_load_wallet_by_mnemonic;
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
        popupWindow = new LoadWalletSelectStandardPopupWindow(getContext());
        popupWindow.setOnPopupItemSelectedListener(new LoadWalletSelectStandardPopupWindow.OnPopupItemSelectedListener() {
            @Override
            public void onSelected(int selection) {
                switch (selection) {
                    case 0:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard);
                        ethType = ETHWalletUtils.ETH_JAXX_TYPE;
                        etStandard.setEnabled(false);
                        break;
                    case 1:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard_ledger);
                        ethType = ETHWalletUtils.ETH_LEDGER_TYPE;
                        etStandard.setEnabled(false);
                        break;
                    case 2:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard_custom);
                        ethType = ETHWalletUtils.ETH_CUSTOM_TYPE;
                        etStandard.setEnabled(true);
                        etStandard.setFocusable(true);
                        etStandard.setFocusableInTouchMode(true);
                        etStandard.requestFocus();
                        break;

                }
            }
        });
    }

    public void loadSuccess(ETHWallet wallet) {
        Properties prop = new Properties();
        prop.setProperty("method", "mnemonic");
        StatService.trackCustomKVEvent(getContext(), "importWalletSuccess", prop);

        dismissDialog();
        ToastUtils.showToast(getString(R.string.load_wallet_success));

        notifyImportAccountSuccess();
    }

    public void onError(Throwable e) {
        ToastUtils.showToast(getString(R.string.load_wallet_fail));
        dismissDialog();
    }


    @OnClick({R.id.btn_load_wallet, R.id.lly_standard_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load_wallet:
                String mnemonic = etMnemonic.getText().toString().trim();
                String name = etWalletName.getText().toString().trim();
                String mnemonicPwd = etMnemonicPwd.getText().toString().trim();
                String walletPwd = etWalletPwd.getText().toString().trim();
                String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(mnemonic, name, walletPwd, confirmPwd);
                if (verifyWalletInfo) {
                    showDialog(getString(R.string.loading_wallet_tip));
                    logEvent();
                    createWalletInteract.loadWalletByMnemonic(ethType, mnemonic, mnemonicPwd, walletPwd, name).subscribe(this::loadSuccess, this::onError);
                }
                break;
            case R.id.lly_standard_menu:
                LogUtils.i("LoadWallet", "lly_standard_menu");
                popupWindow.showAsDropDown(etStandard, 0, UUi.dip2px(10));
                break;
        }
    }

    private boolean verifyInfo(String mnemonic, String name, String pwd, String confirmPwd) {
        if (TextUtils.isEmpty(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_by_mnemonic_input_tip);
            return false;
        } else if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast(R.string.load_wallet_by_private_key_wallet_name_invalid);
            return false;
        } else if (!WalletDaoUtils.isValid(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_by_mnemonic_input_tip);
            return false;
        } else if (WalletDaoUtils.checkRepeatByMnemonic(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_already_exist);
            return false;
        } else if (WalletDaoUtils.checkDuplicateName(name)) {
            ToastUtils.showToast(R.string.load_wallet_name_already_exist);
            return false;
        } else if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
            return false;
        } else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, pwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        }
        return true;
    }

    private void logEvent() {
        Properties prop = new Properties();
        prop.setProperty("method", "mnemonic");
        StatService.trackCustomKVEvent(getContext(), "importWalletStart", prop);
    }
}
