package com.spockchain.wallet.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseActivity;
import com.spockchain.wallet.entity.Token;
import com.spockchain.wallet.entity.TokenInfo;
import com.spockchain.wallet.ui.adapter.AddTokenListAdapter;
import com.spockchain.wallet.utils.LogUtils;
import com.spockchain.wallet.viewmodel.AddTokenViewModel;
import com.spockchain.wallet.viewmodel.AddTokenViewModelFactory;
import com.spockchain.wallet.viewmodel.TokensViewModel;
import com.spockchain.wallet.viewmodel.TokensViewModelFactory;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny熊
 * 微信: xlbxiong
 */

public class AddTokenActivity extends BaseActivity {

    TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;

    protected AddTokenViewModelFactory addTokenViewModelFactory;
    private AddTokenViewModel addTokenViewModel;

    private static final int SEARCH_ICO_TOKEN_REQUEST = 1000;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv_ico)
    ListView tokenList;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;


    List<TokenItem> mItems = new ArrayList<TokenItem>();

    private AddTokenListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

        @Override
    public int getLayoutId() {
        return R.layout.activity_add_new_property;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.add_new_property_title);
        rlBtn.setVisibility(View.VISIBLE);
    }

    public static class TokenItem {
        public final TokenInfo tokenInfo;
        public boolean added;
        public int iconId;

        public TokenItem(TokenInfo tokenInfo, boolean added, int id) {
            this.tokenInfo = tokenInfo;
            this.added = added;
            this.iconId = id;
        }
    }

    @Override
    public void initDatas() {

        // TODO 写死了几个热门的ERC20 （ 主网地址）
        mItems.add(new TokenItem(new TokenInfo("", "Spock Network", "SPOK", 18), true, R.drawable.coin_icon));
        mItems.add(new TokenItem(new TokenInfo("SPOCK-cff8efc2d1a091bbd177257ae8c682203349ca9c", "Rhea Chain", "REA", 18), false, R.drawable.coin_icon));
        mItems.add(new TokenItem(new TokenInfo("SPOCK-12b0ee8fd39e80e9f659055a9ed4c8707b26a390", "Electronic Move Pay", "EMP", 18), false, R.drawable.coin_icon));
        //mItems.add(new TokenItem(new TokenInfo("SPOCK-d249d232dc9d53d73f8c22dc25eeea3a4248bda1", "Electronic Move Pay", "EMP", 18), false, R.drawable.coin_icon));


        tokensViewModelFactory = new TokensViewModelFactory();
        tokensViewModel = ViewModelProviders.of(this, tokensViewModelFactory)
                .get(TokensViewModel.class);
        tokensViewModel.tokens().observe(this, this::onTokens);

        tokensViewModel.prepare();

        addTokenViewModelFactory = new AddTokenViewModelFactory();
        addTokenViewModel = ViewModelProviders.of(this, addTokenViewModelFactory)
                .get(AddTokenViewModel.class);


    }

    private void onTokens(Token[] tokens) {

        for (TokenItem item : mItems) {
            for (Token token: tokens) {
                if (item.tokenInfo.address.equals(token.tokenInfo.address)) {
                    item.added = true;
                }
            }
        }

        // TODO:  Add missed for tokens

        mAdapter = new AddTokenListAdapter(this, mItems, R.layout.list_item_add_ico_property);
        tokenList.setAdapter(mAdapter);
    }

    public void onCheckedChanged(CompoundButton btn, boolean checked){
        TokenItem info = (TokenItem) btn.getTag();
        info.added = checked;
        LogUtils.d(info.toString() + ", checked:" + checked);

        if (checked) {
            addTokenViewModel.save(info.tokenInfo.address, info.tokenInfo.symbol, info.tokenInfo.decimals);
        }


    };

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_btn})
    public void onClick(View view) {
        if (view.getId() == R.id.rl_btn) {
            Intent intent = new Intent(this, AddCustomTokenActivity.class);
            startActivityForResult(intent, SEARCH_ICO_TOKEN_REQUEST);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
