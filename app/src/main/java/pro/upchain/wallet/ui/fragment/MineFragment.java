package pro.upchain.wallet.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.ui.activity.ContactsActivity;
import pro.upchain.wallet.ui.activity.HelpActivity;
import pro.upchain.wallet.ui.activity.MessageCenterActivity;
import pro.upchain.wallet.ui.activity.SystemSettingActivity;
import pro.upchain.wallet.ui.activity.TransactionsActivity;
import pro.upchain.wallet.ui.activity.WalletMangerActivity;

import butterknife.OnClick;


public class MineFragment extends BaseFragment {
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
    }

    @OnClick({R.id.lly_contacts, R.id.github_website})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lly_contacts:
                intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.github_website:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/spockchain/MySpock"));
                startActivity(intent);
                break;
        }
    }
}
