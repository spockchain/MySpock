package com.spockchain.wallet.ui.activity;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.spockchain.wallet.C;
import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseActivity;
import com.spockchain.wallet.entity.Address;
import com.spockchain.wallet.entity.ConfirmationType;
import com.spockchain.wallet.entity.ErrorEnvelope;
import com.spockchain.wallet.entity.GasSettings;
import com.spockchain.wallet.utils.BalanceUtils;
import com.spockchain.wallet.utils.ToastUtils;
import com.spockchain.wallet.view.ConfirmTransactionView;
import com.spockchain.wallet.view.InputPwdView;
import com.spockchain.wallet.viewmodel.ConfirmationViewModel;
import com.spockchain.wallet.viewmodel.ConfirmationViewModelFactory;
import com.tencent.stat.StatService;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Properties;


import butterknife.BindView;
import butterknife.OnClick;

import static com.spockchain.wallet.C.SPCOK_CHAIN_NAME;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class SendActivity extends BaseActivity {
    private static final String TAG = "SendActivity";

    ConfirmationViewModelFactory confirmationViewModelFactory;
    ConfirmationViewModel viewModel;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;

    @BindView(R.id.et_transfer_address)
    EditText etTransferAddress;

    @BindView(R.id.send_amount)
    EditText amountText;

    @BindView(R.id.lly_contacts)
    LinearLayout llyContacts;
    @BindView(R.id.seekbar)
    SeekBar seekbar;

    @BindView(R.id.tv_gas_cost)
    TextView tvGasCost;

    @BindView(R.id.gas_price)
    TextView tvGasPrice;

    @BindView(R.id.lly_gas)
    LinearLayout llyGas;
    @BindView(R.id.et_hex_data)
    EditText etHexData;
    @BindView(R.id.lly_advance_param)
    LinearLayout llyAdvanceParam;


    @BindView(R.id.advanced_switch)
    Switch advancedSwitch;

    @BindView(R.id.custom_gas_price)
    EditText customGasPrice;

    @BindView(R.id.custom_gas_limit)
    EditText customGasLimit;


    private String walletAddr;
    private String contractAddress;
    private int decimals;
    private String balance;
    private String symbol;

    private String netCost;
    private BigInteger gasPriceInWei;
    private BigInteger gasLimit = new BigInteger("144000");


    private boolean sendingTokens = false;

    private Dialog dialog;

    private static final int QRCODE_SCANNER_REQUEST = 1100;

    private static final double miner_min = 5 ;
    private static final double miner_max = 55;

    private String scanResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transfer;
    }

    @Override
    public void initToolBar() {
        ivBtn.setImageResource(R.drawable.ic_transfer_scanner);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {


        Intent intent = getIntent();
        walletAddr = intent.getStringExtra(C.EXTRA_ADDRESS);

        contractAddress = intent.getStringExtra(C.EXTRA_CONTRACT_ADDRESS);

        decimals = intent.getIntExtra(C.EXTRA_DECIMALS, C.ETHER_DECIMALS);
        symbol = intent.getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.SPOCK_SYMBOL : symbol;

        tvTitle.setText(symbol + " " + getString(R.string.transfer_title));

        confirmationViewModelFactory = new ConfirmationViewModelFactory();
        viewModel = ViewModelProviders.of(this, confirmationViewModelFactory)
                .get(ConfirmationViewModel.class);

        viewModel.sendTransaction().observe(this, this::onTransaction);
        viewModel.gasSettings().observe(this, this::onGasSettings);
        viewModel.progress().observe(this, this::onProgress);
        viewModel.error().observe(this, this::onError);

        // 首页直接扫描进入
        scanResult = intent.getStringExtra("scan_result");
        if (!TextUtils.isEmpty(scanResult)) {
            parseScanResult(scanResult);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
        viewModel.prepare(this, sendingTokens? ConfirmationType.ETH: ConfirmationType.ERC20);
    }

    @Override
    protected void onPause() {
        StatService.onPause(this);
        super.onPause();
    }

    @Override
    public void configViews() {
        customGasLimit.setText(gasLimit.toString());

        advancedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llyAdvanceParam.setVisibility(View.VISIBLE);
                    llyGas.setVisibility(View.GONE);

                    customGasPrice.setText(Convert.fromWei(new BigDecimal(gasPriceInWei), Convert.Unit.GWEI).toString());
                    customGasLimit.setText(gasLimit.toString());

                } else {
                    llyAdvanceParam.setVisibility(View.GONE);
                    llyGas.setVisibility(View.VISIBLE);

                }
            }
        });


        final DecimalFormat gasformater = new DecimalFormat();
        //保留几位小数
        gasformater.setMaximumFractionDigits(2);
        //模式  四舍五入
        gasformater.setRoundingMode(RoundingMode.CEILING);


        final String spockUnit = C.SPOCK_UNIT;


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged!");

                double p = progress / 100f;
                double d = (miner_max - miner_min) * p + miner_min;

                gasPriceInWei = BalanceUtils.gweiToWei(BigDecimal.valueOf(d));
                tvGasPrice.setText(gasformater.format(d) + " " + C.GWEI_UNIT);

                updateNetworkFee();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar.setProgress(10);


        customGasPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    return;
                }
                gasPriceInWei = BalanceUtils.gweiToWei(new BigDecimal(s.toString()));

                try {
                    netCost = getNetCost(spockUnit);
                    tvGasCost.setText(String.valueOf(netCost ));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        customGasLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    return;
                }
                gasLimit = new BigInteger(s.toString());

                updateNetworkFee();
            }
        });

        try {
            netCost = getNetCost(spockUnit);
        } catch (Exception e) {
            Log.e(TAG, "Failed to calculate netCost", e);
        }
    }

    private String getNetCost(String spockUnit) throws Exception {
        return String.format("%s %s", BalanceUtils.weiToEth(gasPriceInWei.multiply(gasLimit), 4), spockUnit);
    }

    private void updateNetworkFee() {

        try {
            netCost = BalanceUtils.weiToEth(gasPriceInWei.multiply(gasLimit),  4) + " " + C.SPOCK_UNIT;
            tvGasCost.setText(String.valueOf(netCost ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onGasSettings(GasSettings gasSettings) {
        gasPriceInWei = gasSettings.gasPrice;
        gasLimit = gasSettings.gasLimit;

    }

    private boolean verifyInfo(String address, String amount) {
        if (!Address.isSpockAddress(address)) {
            ToastUtils.showToast(R.string.addr_error_tips);
            return false;
        }

        try {
            String wei = BalanceUtils.EthToWei(amount);
            return wei != null;
        } catch (Exception e) {
            ToastUtils.showToast(R.string.amount_error_tips);
            return false;
        }
    }



    @OnClick({R.id.rl_btn, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                Intent intent = new Intent(SendActivity.this, QRCodeScannerActivity.class);
                startActivityForResult(intent, QRCODE_SCANNER_REQUEST);
                break;
            case R.id.btn_next:
                // confirm info;
                String toAddr = getToAddress();
                String amount = amountText.getText().toString().trim();

                if (verifyInfo(toAddr, amount)) {
                    BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);

                    String amountForDisplay = String.format("-%s %s", amount, C.SPOCK_UNIT);
                    ConfirmTransactionView confirmView =
                            new ConfirmTransactionView(
                                    this,
                                    (v) -> {
                                        dialog.dismiss();
                                        sendTransaction();
                                    });
                    confirmView.fillInfo(walletAddr, toAddr, amountForDisplay, netCost, gasPriceInWei, gasLimit);
                    dialog.setContentView(confirmView);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
                break;
        }
    }

    private void sendTransaction() {
        InputPwdView pwdView = new InputPwdView(this, pwd -> {
            if (sendingTokens) {
                viewModel.createTokenTransfer(pwd,
                        etTransferAddress.getText().toString().trim(),
                        contractAddress,
                        BalanceUtils.tokenToWei(new BigDecimal(amountText.getText().toString().trim ()), decimals).toBigInteger(),
                        gasPriceInWei,
                        gasLimit
                );
            } else {
                viewModel.createTransaction(pwd, getToAddress(),
                        Convert.toWei(amountText.getText().toString().trim(), Convert.Unit.ETHER).toBigInteger(),
                        gasPriceInWei,
                        gasLimit );
                logStartEvent();
            }

        });

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(pwdView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private String getToAddress() {
        return etTransferAddress.getText().toString().trim().toUpperCase();
    }

    private void logStartEvent() {
        Properties prop = new Properties();
        StatService.trackCustomKVEvent(this, "sendMoneyStart", prop);
    }

    private void logSuccessEvent() {
        Properties prop = new Properties();
        StatService.trackCustomKVEvent(this, "sendMoneySuccess", prop);
    }


    private void onProgress(boolean shouldShowProgress) {
        if (shouldShowProgress) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_dialog_sending)
                    .setView(new ProgressBar(this))
                    .setCancelable(false)
                    .create();
            dialog.show();
        } else {
            if (dialog != null) {
                dialog.hide();
            }
        }
    }

    private void onError(ErrorEnvelope error) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.error_transaction_failed)
                .setMessage(getString(R.string.error_send_transaction_failed, error.message))
                .setPositiveButton(R.string.button_ok, (dialog1, id) -> {
                    // Do nothing
                })
                .create();
        dialog.show();
    }

    private void onTransaction(String hash) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.transaction_succeeded)
                .setMessage(hash)
                .setPositiveButton(R.string.button_ok, (dialog1, id) -> {
                    finish();
                })
                .setNeutralButton(R.string.copy, (dialog1, id) -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("transaction hash", hash);
                    clipboard.setPrimaryClip(clip);
                    finish();
                })
                .create();
        dialog.show();
        logSuccessEvent();
    }

    private void fillAddress(String addr) {
        try {
            new Address(addr);
            etTransferAddress.setText(addr);
        } catch (Exception e) {
            ToastUtils.showToast(R.string.addr_error_tips);
        }
    }

    private void parseScanResult(String result) {
        if (result.contains(":") && result.contains("?")) {  // 符合协议格式
            String[] urlParts = result.split(":");
            if (urlParts[0].equals(SPCOK_CHAIN_NAME)) {
                urlParts =  urlParts[1].split("\\?");

                fillAddress(urlParts[0]);

                // ?contractAddress=0xdxx & decimal=1 & value=100000
//                 String[] params = urlParts[1].split("&");
//                for (String param : params) {
//                    String[] keyValue = param.split("=");
//                }

            }


        } else {  // 无格式， 只有一个地址
            fillAddress(result);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE_SCANNER_REQUEST) {
            if (data != null) {
                String scanResult = data.getStringExtra("scan_result");
                // 对扫描结果进行处理
                parseScanResult(scanResult);
//                ToastUtils.showLongToast(scanResult);
            }
        }
    }

}
