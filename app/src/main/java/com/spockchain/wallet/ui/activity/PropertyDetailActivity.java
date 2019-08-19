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
    TextView tvTitle;

    @BindView(R.id.tv_amount)
    TextView tvAmount;

    @BindView(R.id.btn_load_more)
    Button btnLoadMore;

    @BindView(R.id.lly_load_more)
    View llyLoadMore;

    List<TransactionMetadata> transactionLists;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        tvTitle.setText(symbol);

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
        transactionLists = transactions;
        adapter.addTransactions(transactionLists, currWallet, symbol);
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .init();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        RecyclerView list = (RecyclerView) findViewById(R.id.list);

        list.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,1);
        list.addItemDecoration(dividerItemDecoration);

        adapter = new TransactionsAdapter(R.layout.list_item_transaction, null );
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            TransactionMetadata t = transactionLists.get(position);

            Uri uri = Uri.parse(viewModel.defaultNetwork().getValue().backendUrl + "tx/" + t.getHash());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        refreshLayout.setOnRefreshListener(viewModel::fetchTransactions);
        // TODO(satoshi.meow): Enable the refresh after we support fetching transaction.
//        refreshLayout.setEnabled(false);

        viewModel.getHasMoreTransactions().observe(this, this::hasMoreTransactions);
    }


    private void onProgress(boolean inProgress) {
        if (inProgress && refreshLayout != null && refreshLayout.isRefreshing()) {
            return;
        }

        if (!inProgress) {
            refreshLayout.setRefreshing(false);
        }

        setLoadMoreButtonVisibility();
    }

    private void hasMoreTransactions(boolean hasMore) {
        setLoadMoreButtonVisibility();
    }


    @OnClick({R.id.lly_back, R.id.lly_transfer, R.id.lly_gathering, R.id.btn_load_more})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_back:
                finish();
                break;
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
            case R.id.btn_load_more:
                viewModel.fetchNextPageTransactions();
                break;
        }
    }

    void setLoadMoreButtonVisibility() {
        boolean isInProgress = viewModel.progress().getValue() == null ? false : viewModel.progress().getValue();
        boolean hasMoreData = viewModel.getHasMoreTransactions().getValue() == null ? false : viewModel.getHasMoreTransactions().getValue();

        llyLoadMore.setVisibility(!isInProgress && hasMoreData ? View.VISIBLE : View.GONE);
    }
}
