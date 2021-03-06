package com.spockchain.wallet.interact;

import java.util.Arrays;

import com.spockchain.wallet.domain.ETHWallet;
import com.spockchain.wallet.utils.ETHWalletUtils;
import com.spockchain.wallet.utils.WalletDaoUtils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateWalletInteract {


    public CreateWalletInteract() {
    }

    public Single<ETHWallet> create(final String name, final String mnemonicPwd, String walletPwd) {
        return Single.fromCallable(() -> {
            ETHWallet ethWallet = ETHWalletUtils.generateMnemonic(name, mnemonicPwd, walletPwd);
            WalletDaoUtils.insertNewWallet(ethWallet);
            return ethWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<ETHWallet> loadWalletByKeystore(final String keystore, final String pwd) {
        return Single.fromCallable(() -> {
            ETHWallet ethWallet = ETHWalletUtils.loadWalletByKeystore(keystore, pwd);
            if (ethWallet != null) {
                WalletDaoUtils.insertNewWallet(ethWallet);
            }

            return ethWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ETHWallet> loadWalletByPrivateKey(final String privateKey, final String pwd, final String name) {
        return Single.fromCallable(() -> {

                    ETHWallet ethWallet = ETHWalletUtils.loadWalletByPrivateKey(privateKey, pwd, name);
                    if (ethWallet != null) {
                        WalletDaoUtils.insertNewWallet(ethWallet);
                    }
                    return ethWallet;
                }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<ETHWallet> loadWalletByMnemonic(final String bipPath, final String mnemonic, final String mnemonicPwd, final String walletPwd, final String name) {
        return Single.fromCallable(() -> {
            ETHWallet ethWallet = ETHWalletUtils.importMnemonic(bipPath
                    , Arrays.asList(mnemonic.split(" ")), mnemonicPwd, walletPwd, name);
            if (ethWallet != null) {
                WalletDaoUtils.insertNewWallet(ethWallet);
            }
            return ethWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


    }

}
