package com.spockchain.wallet.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.webkit.WebView;
import android.widget.TextView;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseFragment;

import butterknife.BindView;

public class ExplorerFragment extends BaseFragment {

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.wv_explorer)
    WebView wvExplorer;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_explorer;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        wvExplorer.loadUrl("https://spock.network/home");
    }

    @Override
    public void configViews() {
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        wvExplorer.getSettings().setJavaScriptEnabled(true);
    }

    public boolean handleBackKey() {
        if (wvExplorer.canGoBack()) {
            wvExplorer.goBack();
            return true;
        }
        return false;
    }

    private void onRefresh() {
        wvExplorer.reload();
        swipeRefreshLayout.setRefreshing(false);
    }
}
