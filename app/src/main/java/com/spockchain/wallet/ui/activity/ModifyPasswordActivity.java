package com.spockchain.wallet.ui.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseActivity;
import com.spockchain.wallet.domain.ETHWallet;
import com.spockchain.wallet.interact.ModifyWalletInteract;
import com.spockchain.wallet.utils.Md5Utils;
import com.spockchain.wallet.utils.ToastUtils;
import com.spockchain.wallet.view.InputPwdDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class ModifyPasswordActivity extends BaseActivity {
    private static final int MODIFY_PWD_RESULT = 2201;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_old_pwd)
    EditText etOldPwd;
    @BindView(R.id.et_new_pwd)
    EditText etNewPwd;
    @BindView(R.id.et_new_pwd_again)
    EditText etNewPwdAgain;
    @BindView(R.id.iv_btn)
    TextView ivBtn;
    @BindView(R.id.tv_import_wallet)
    TextView tvImportWallet;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    private long walletId;
    private String walletPwd;
    private String walletAddress;
    private String walletName;
    private boolean walletIsBackup;
    private ModifyWalletInteract  modifyWalletInteract;
    private InputPwdDialog inputPwdDialog;
    private String walletMnemonic;

    @Override
    public int getLayoutId() {
        return R.layout.activity_modify_password;
    }

    @Override
    public void initToolBar() {
        rlBtn.setVisibility(View.VISIBLE);
        ivBtn.setText(R.string.modify_password_complete);
        tvTitle.setText(R.string.modify_password_title);
        rlBtn.setEnabled(false);
        ivBtn.setTextColor(getResources().getColor(R.color.property_ico_worth_color));
    }

    @Override
    public void initDatas() {
        modifyWalletInteract = new ModifyWalletInteract();

        Intent intent = getIntent();
        walletId = intent.getLongExtra("walletId", -1);
        walletPwd = intent.getStringExtra("walletPwd");
        walletAddress = intent.getStringExtra("walletAddress");
        walletName = intent.getStringExtra("walletName");
        walletIsBackup = intent.getBooleanExtra("walletIsBackup", false);
        walletMnemonic = intent.getStringExtra("walletMnemonic");
    }

    @Override
    public void configViews() {
        rlBtn.setEnabled(true);
        ivBtn.setTextColor(getResources().getColor(R.color.transfer_advanced_setting_help_text_color));
    }

    @OnClick({R.id.tv_import_wallet, R.id.rl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_import_wallet:
                startActivity(new Intent(this, ImportWalletActivity.class));
                finish();
                break;
            case R.id.rl_btn:
                String oldPwd = etOldPwd.getText().toString().trim();
                String newPwd = etNewPwd.getText().toString().trim();
                String newPwdAgain = etNewPwdAgain.getText().toString().trim();
                if (verifyPassword(oldPwd, newPwd, newPwdAgain)) {
                    showDialog(getString(R.string.saving_wallet_tip));
                    modifyWalletInteract.modifyWalletPwd(walletId, walletName, oldPwd, newPwd).subscribe(this::modifyPwdSuccess, this::onModifyPwdFailure);
                }
                break;
        }
    }

    private boolean verifyPassword(String oldPwd, String newPwd, String newPwdAgain) {
        if (!TextUtils.equals(Md5Utils.md5(oldPwd), walletPwd)) {
            ToastUtils.showToast(R.string.modify_password_alert4);
            return false;
        } else if (!TextUtils.equals(newPwd, newPwdAgain)) {
            ToastUtils.showToast(R.string.modify_password_alert5);
            return false;
        }
        return true;
    }

    public void modifyPwdSuccess(ETHWallet ethWallet) {
        dismissDialog();
        ToastUtils.showToast(R.string.modify_password_success);
        Intent data = new Intent();
        data.putExtra("newPwd", ethWallet.getPassword());
        setResult(MODIFY_PWD_RESULT, data);
        finish();
    }

    private void onModifyPwdFailure(Throwable t) {
        dismissDialog();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.modify_password_fail_title))
                .setMessage(t.getMessage())
                .setPositiveButton(getString(R.string.modify_password_fail_button), (dialog1, id) -> {
                    // Do nothing
                })
                .create();
        dialog.show();
    }

    public void showDerivePrivateKeyDialog(String privateKey) {

    }

    public void showDeriveKeystore(String keystore) {

    }

    public void deleteSuccess(boolean isDelete) {

    }

}
