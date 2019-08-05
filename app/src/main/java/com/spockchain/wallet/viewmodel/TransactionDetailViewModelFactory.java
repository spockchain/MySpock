package com.spockchain.wallet.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.spockchain.wallet.MySpockApp;
import com.spockchain.wallet.interact.FetchWalletInteract;
import com.spockchain.wallet.repository.EthereumNetworkRepository;
import com.spockchain.wallet.repository.RepositoryFactory;


public class TransactionDetailViewModelFactory implements ViewModelProvider.Factory {

    private final EthereumNetworkRepository EthereumNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;

    public TransactionDetailViewModelFactory() {
        RepositoryFactory rf = MySpockApp.repositoryFactory();

        this.EthereumNetworkRepository = rf.ethereumNetworkRepository;
        this.findDefaultWalletInteract = new FetchWalletInteract();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TransactionDetailViewModel(
                EthereumNetworkRepository,
                findDefaultWalletInteract
                );
    }
}
