package com.spockchain.wallet.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spockchain.wallet.R;
import com.spockchain.wallet.entity.Transaction;
import com.spockchain.wallet.entity.TransactionMetadata;
import com.spockchain.wallet.entity.TransactionOperation;
import com.spockchain.wallet.utils.LogUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class TransactionsAdapter  extends BaseQuickAdapter<TransactionMetadata, BaseViewHolder> {

    private final List<Transaction> items = new ArrayList<>();

    private static final int SIGNIFICANT_FIGURES = 3;

    private String symbol;
    private String defaultAddress;


    public TransactionsAdapter(int layoutResId, @Nullable List<TransactionMetadata> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, TransactionMetadata transaction) {
        LogUtils.d(TAG, "convert: helper:" + helper + ", transaction:" + transaction);

        boolean isSent = transaction.getFrom().toLowerCase().equals(defaultAddress.toLowerCase());
//        boolean isCreateContract = TextUtils.isEmpty(transaction.getTo());

        helper.setText(R.id.from, "发送: " + transaction.getFrom());
        helper.setText(R.id.to, "接受: " + transaction.getTo());
        helper.setText(R.id.created, "时间: " + convertTimestampToDate(transaction.getTimestamp()));

//        if (isSent) {
//            if (isCreateContract) {
//                helper.setText(R.id.type, R.string.create);
//            } else {
//                helper.setText(R.id.type, R.string.sent);
//            }
//        }
//        else {
//            helper.setText(R.id.type, R.string.received);
//        }

//
//        if (isSent) {
//            helper.setImageResource(R.id.type_icon, R.drawable.ic_arrow_upward_black_24dp);
//        } else {
//            helper.setImageResource(R.id.type_icon, R.drawable.ic_arrow_downward_black_24dp);
//        }
//
//        if (isCreateContract) {
//            helper.setText(R.id.address, transaction.contract);
//        } else {
//            helper.setText(R.id.address, isSent ? transaction.getTo() : transaction.getFrom());
//        }


        helper.setTextColor(R.id.value, ContextCompat.getColor(mContext, isSent ? R.color.red : R.color.green));

        String valueStr = "";


        // If operations include token transfer, display token transfer instead
//        TransactionOperation operation = transaction.operations == null
//                || transaction.operations.length == 0 ? null : transaction.operations[0];

        TransactionOperation operation = null;

        if (operation == null || operation.contract == null) {  // default to ether transaction
            valueStr = transaction.getValue();

            if (valueStr.equals("0")) {
                valueStr = "0 " + symbol;
            } else {
                valueStr = (isSent ? "-" : "+") +  valueStr + " " + symbol;
            }

        } else {
            valueStr = operation.value;

            if (valueStr.equals("0")) {
                valueStr = "0 " + symbol;
            } else {
                valueStr = (isSent ? "-" : "+") +  getScaledValue(valueStr, operation.contract.decimals) + " " + symbol;
            }
        }
        helper.setText(R.id.value, "数额: " + valueStr);
    }


    private String getScaledValue(String valueStr, long decimals) {
        // Perform decimal conversion
        BigDecimal value = new BigDecimal(valueStr);
        value = value.divide(new BigDecimal(Math.pow(10, decimals)));
        int scale = SIGNIFICANT_FIGURES - value.precision() + value.scale();
        return value.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }


    public void addTransactions(List<TransactionMetadata> transactions, String walletAddress, String symbol) {
        setNewData(transactions);
        this.defaultAddress = walletAddress;
        this.symbol = symbol;
    }

    private String convertTimestampToDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp * DateUtils.SECOND_IN_MILLIS);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }


}
