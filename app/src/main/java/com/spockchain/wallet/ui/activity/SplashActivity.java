package com.spockchain.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.spockchain.wallet.domain.ETHWallet;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import com.spockchain.wallet.interact.FetchWalletInteract;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class SplashActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            finish();
            return;
        }

        new FetchWalletInteract().fetch().observeOn(AndroidSchedulers.mainThread()).delay(2, TimeUnit.SECONDS).subscribe(
                this::onWalltes, this::onError
        );

    }

    public void onWalltes(List<ETHWallet> ethWallets) {

        if (ethWallets.size() == 0) {
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SplashActivity.this.startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SplashActivity.this.startActivity(intent);
        }


    }


    public void onError(Throwable throwable) {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SplashActivity.this.startActivity(intent);
    }

}
