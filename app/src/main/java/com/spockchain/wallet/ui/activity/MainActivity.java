package com.spockchain.wallet.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseActivity;
import com.spockchain.wallet.ui.adapter.HomePagerAdapter;
import com.spockchain.wallet.ui.fragment.ExplorerFragment;
import com.spockchain.wallet.ui.fragment.MineFragment;
import com.spockchain.wallet.ui.fragment.PropertyFragment;
import com.spockchain.wallet.utils.ToastUtils;
import com.spockchain.wallet.utils.VersionChecker;
import com.spockchain.wallet.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * 微信: xlbxiong
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.vp_home)
    NoScrollViewPager vpHome;

    @BindView(R.id.iv_mall)
    ImageView ivMall;
    @BindView(R.id.tv_mall)
    TextView tvMall;
    @BindView(R.id.lly_mall)
    LinearLayout llyMall;

    @BindView(R.id.lly_explorer)
    LinearLayout llyExplorer;
    @BindView(R.id.tv_explorer)
    TextView tvExplorer;
    @BindView(R.id.iv_explorer)
    ImageView ivExplorer;

    @BindView(R.id.iv_mine)
    ImageView ivMine;
    @BindView(R.id.tv_mine)
    TextView tvMine;
    @BindView(R.id.lly_mine)
    LinearLayout llyMine;

    private final VersionChecker versionChecker = new VersionChecker(this);

    private HomePagerAdapter homePagerAdapter;

    @Override
    protected void onResume() {
        super.onResume();

        // Check new version.
        versionChecker.checkVersionIfNeeded();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        ivMall.setSelected(true);
        tvMall.setSelected(true);

        llyMall.setOnClickListener(this);
        llyExplorer.setOnClickListener(this);
        llyMine.setOnClickListener(this);

        vpHome.setOffscreenPageLimit(2);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new PropertyFragment());
        fragmentList.add(new ExplorerFragment());
        fragmentList.add(new MineFragment());
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), fragmentList);
        vpHome.setAdapter(homePagerAdapter);
        vpHome.setCurrentItem(0, false);
    }

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (homePagerAdapter.getItem(vpHome.getCurrentItem()) instanceof ExplorerFragment) {
                boolean handledByExplorer = ((ExplorerFragment) homePagerAdapter.getItem(1)).handleBackKey();
                if (handledByExplorer) {
                    return true;
                }
            }

            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showToast(getString(R.string.exit_tips));
                return true;
            } else {
                finish(); // 退出
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View v) {
        setAllUnselected();
        switch (v.getId()) {
            case R.id.lly_mall:// 商场
                ivMall.setSelected(true);
                tvMall.setSelected(true);
                vpHome.setCurrentItem(0, false);
                break;
            case R.id.lly_explorer:
                ivExplorer.setSelected(true);
                tvExplorer.setSelected(true);
                vpHome.setCurrentItem(1, false);
                break;
            case R.id.lly_mine:// 我的
                ivMine.setSelected(true);
                tvMine.setSelected(true);
                vpHome.setCurrentItem(2, false);
                break;
        }
    }

    private void setAllUnselected() {
        ivMall.setSelected(false);
        tvMall.setSelected(false);

        ivExplorer.setSelected(false);
        tvExplorer.setSelected(false);


        ivMine.setSelected(false);
        tvMine.setSelected(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
