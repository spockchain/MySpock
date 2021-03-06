package com.spockchain.wallet.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Single;
import com.spockchain.wallet.R;
import com.spockchain.wallet.base.BaseFragment;
//import com.zxing.support.library.qrcode.QRCodeEncode;

import butterknife.BindView;
import com.spockchain.wallet.ui.activity.GatheringQRCodeActivity;
import com.spockchain.wallet.utils.GlideImageLoader;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ExportKeystoreQRCodeFragment extends BaseFragment {
    @BindView(R.id.iv_keystore)
    ImageView ivKeystore;

    String walletKeystore;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_derive_keystore_qrcode;
    }

    @Override
    public void attachView() {
        Bundle arguments = getArguments();
        walletKeystore = arguments.getString("walletKeystore");
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

        ivKeystore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Single.fromCallable(
                        () -> {
                            return QRCodeEncoder.syncEncodeQRCode(walletKeystore, BGAQRCodeUtil.dp2px(getSupportActivity()
                                    , 240), Color.parseColor("#000000"));
                        }
                ).subscribe( bitmap ->  GlideImageLoader.loadBmpImage(ivKeystore, bitmap, -1) );
            }
        });

    }

}
