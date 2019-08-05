package com.spockchain.wallet.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.spockchain.wallet.MySpockApp;
import com.spockchain.wallet.interact.FetchTransactionsInteract;
import com.spockchain.wallet.interact.FetchWalletInteract;
import com.spockchain.wallet.repository.EthereumNetworkRepository;
import com.spockchain.wallet.repository.RepositoryFactory;


public class TransactionsViewModelFactory implements ViewModelProvider.Factory {

    private final EthereumNetworkRepository ethereumNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;
    private final FetchTransactionsInteract fetchTransactionsInteract;


    public TransactionsViewModelFactory() {

        RepositoryFactory rf = MySpockApp.repositoryFactory();
        this.ethereumNetworkRepository = rf.ethereumNetworkRepository;
        this.findDefaultWalletInteract = new FetchWalletInteract();
        this.fetchTransactionsInteract = new FetchTransactionsInteract(rf.transactionRepository);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TransactionsViewModel(
                ethereumNetworkRepository,
                findDefaultWalletInteract,
                fetchTransactionsInteract);
    }
}
