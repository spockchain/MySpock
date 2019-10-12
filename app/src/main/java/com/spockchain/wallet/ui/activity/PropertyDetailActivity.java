package com.spockchain.wallet.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.spockchain.wallet.C;
import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseActivity;
import com.spockchain.wallet.domain.ETHWallet;
import com.spockchain.wallet.entity.TransactionMetadata;
import com.spockchain.wallet.ui.adapter.TransactionsAdapter;
import com.spockchain.wallet.utils.LogUtils;
import com.spockchain.wallet.utils.WalletDaoUtils;
import com.spockchain.wallet.viewmodel.TransactionsViewModel;
import com.spockchain.wallet.viewmodel.TransactionsViewModelFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.spockchain.wallet.C.EXTRA_ADDRESS;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class PropertyDetailActivity extends BaseActivity {

    TransactionsViewModelFactory transactionsViewModelFactory;
    private TransactionsViewModel viewModel;

    private TransactionsAdapter adapter;

    private String currWallet;
    private String contractAddress;
    private int decimals;
    private String balance;
    private String symbol;


    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.tv_title)
    Toolbar tvTitle;

    @BindView(R.id.tv_wallet_address)
    TextView txWalletAddress;

    @BindView(R.id.tv_amount)
    TextView tvAmount;

    List<TransactionMetadata> transactionLists;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(tvTitle);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_property_detail;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

        Intent intent = getIntent();
        currWallet = intent.getStringExtra(C.EXTRA_ADDRESS);
        balance = intent.getStringExtra(C.EXTRA_BALANCE);
        contractAddress = intent.getStringExtra(C.EXTRA_CONTRACT_ADDRESS);
        decimals = intent.getIntExtra(C.EXTRA_DECIMALS, C.ETHER_DECIMALS);
        symbol = intent.getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.SPOCK_SYMBOL: symbol;

        tvTitle.setTitle(symbol);
        txWalletAddress.setText(currWallet);
        tvAmount.setText(balance);

        transactionsViewModelFactory = new TransactionsViewModelFactory();
        viewModel = ViewModelProviders.of(this, transactionsViewModelFactory)
                .get(TransactionsViewModel.class);

        viewModel.transactions().observe(this, this::onTransactions);
        viewModel.progress().observe(this, this::onProgress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LogUtils.d("contractAddress " + contractAddress);

        if (!TextUtils.isEmpty(contractAddress)) {
            viewModel.prepare(contractAddress);
        } else {
            viewModel.prepare(null);
        }

    }



    private void onTransactions(List<TransactionMetadata> transactions) {
        LogUtils.d("onTransactions", "size: " + transactions.size());
        if (transactionLists != null && transactions.size() == transactionLists.size()){
            adapter.loadMoreEnd();
            return;
        }
        transactionLists = transactions;
        adapter.addTransactions(transactionLists, currWallet, symbol);
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(false, 1f)
                .init();

        refreshLayout = findViewById(R.id.refresh_layout);
        RecyclerView list = findViewById(R.id.list);

        list.setLayoutManager(new LinearLayoutManager(this));

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,1);
//        list.addItemDecoration(dividerItemDecoration);

        adapter = new TransactionsAdapter(R.layout.list_item_transaction, null );
        list.setAdapter(adapter);

        adapter.setOnLoadMoreListener(() -> {
            viewModel.fetchNextPageTransactions();
        }, list);
        adapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            TransactionMetadata t = transactionLists.get(position);

            Uri uri = Uri.parse(viewModel.defaultNetwork().getValue().backendUrl + "tx/" + t.getHash());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        refreshLayout.setOnRefreshListener(() -> {
            viewModel.resetPage();
            adapter.loadMoreEnd(true);
            viewModel.fetchTransactions();
        });
        // TODO(satoshi.meow): Enable the refresh after we support fetching transaction.
//        refreshLayout.setEnabled(false);
    }


    private void onProgress(boolean inProgress) {
        if (inProgress && refreshLayout != null && refreshLayout.isRefreshing()) {
            return;
        }

        if (!inProgress) {
            refreshLayout.setRefreshing(false);
            adapter.loadMoreComplete();
        }
    }

    @OnClick({R.id.lly_transfer, R.id.lly_gathering})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_transfer:

                intent = new Intent(mContext, SendActivity.class);

                intent.putExtra(C.EXTRA_BALANCE, balance);
                intent.putExtra(C.EXTRA_ADDRESS, currWallet);
                intent.putExtra(C.EXTRA_CONTRACT_ADDRESS, contractAddress);
                intent.putExtra(C.EXTRA_SYMBOL, symbol);
                intent.putExtra(C.EXTRA_DECIMALS, decimals);

                startActivity(intent);
                break;
            case R.id.lly_gathering:
                intent = new Intent(mContext, GatheringQRCodeActivity.class);
                ETHWallet wallet = WalletDaoUtils.getCurrent();

                intent.putExtra(EXTRA_ADDRESS, wallet.getAddress());
                intent.putExtra(C.EXTRA_CONTRACT_ADDRESS, contractAddress);
                intent.putExtra(C.EXTRA_SYMBOL, symbol);
                intent.putExtra(C.EXTRA_DECIMALS, decimals);

                startActivity(intent);
                break;
//            case R.id.btn_load_more:
//                viewModel.fetchNextPageTransactions();
//                break;
        }
    }
}
