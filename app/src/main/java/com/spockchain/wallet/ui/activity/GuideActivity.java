package com.spockchain.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.spockchain.wallet.R;
import com.spockchain.wallet.utils.VersionChecker;
import com.spockchain.wallet.view.AddWalletView;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class GuideActivity extends AppCompatActivity  implements
        View.OnClickListener,
        AddWalletView.OnNewWalletClickListener,
        AddWalletView.OnImportWalletClickListener  {

    private final VersionChecker versionChecker = new VersionChecker(this);

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);

        frameLayout = (FrameLayout)findViewById(R.id.frame);

        AddWalletView addWalletView = new AddWalletView(this, R.layout.layout_empty_add_account);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        addWalletView.setLayoutParams(lp);

        addWalletView.setOnNewWalletClickListener(this);
        addWalletView.setOnImportWalletClickListener(this);
        frameLayout.addView(addWalletView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        versionChecker.checkVersionIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    @Override
    public void onNewWallet(View view) {
        Intent intent = new Intent(this, CreateWalletActivity.class);
        intent.putExtra("first_account", true);
        startActivity(intent);
    }

    @Override
    public void onImportWallet(View view) {
        Intent intent = new Intent(this, ImportWalletActivity.class);
        intent.putExtra("first_account", true);
        startActivity(intent);
    }
}


