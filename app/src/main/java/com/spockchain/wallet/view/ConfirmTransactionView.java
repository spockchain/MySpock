package com.spockchain.wallet.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.spockchain.wallet.C;
import com.spockchain.wallet.R;
import com.spockchain.wallet.utils.BalanceUtils;

import java.math.BigInteger;

public class ConfirmTransactionView extends FrameLayout {

    public ConfirmTransactionView(Context context, OnClickListener onClickListener) {
        super(context);

        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_confrim_transcation, this, true);

        findViewById(R.id.confirm_button).setOnClickListener(onClickListener);
    }


    public void fillInfo(String fromAddr, String addr, String amount, String fee, BigInteger gasPriceInWei, BigInteger gasLimit) {


        TextView fromAddressText = findViewById(R.id.text_from);
        fromAddressText.setText(fromAddr);

        TextView toAddressText = findViewById(R.id.text_to);
        toAddressText.setText(addr);

        TextView valueText = findViewById(R.id.text_value);
        valueText.setText(amount);

        TextView gasPriceText = findViewById(R.id.text_gas_price);
        String gasPriceInGwei = BalanceUtils.weiToGwei(gasPriceInWei);
        gasPriceText.setText(String.format("%s %s", gasPriceInGwei, C.GWEI_UNIT));

        TextView gasLimitText = findViewById(R.id.text_gas_limit);
        gasLimitText.setText(String.format(gasLimit.toString()));

        TextView networkFeeText = findViewById(R.id.text_network_fee);
        networkFeeText.setText(fee);

    }


}
