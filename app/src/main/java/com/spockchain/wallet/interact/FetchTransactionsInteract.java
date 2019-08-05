package com.spockchain.wallet.interact;


import com.spockchain.wallet.entity.Transaction;
import com.spockchain.wallet.repository.TransactionRepositoryType;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class FetchTransactionsInteract {

    private final TransactionRepositoryType transactionRepository;

    public FetchTransactionsInteract(TransactionRepositoryType transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Observable<Transaction[]> fetch(String walletAddr, String token) {
        return transactionRepository
                .fetchTransaction(walletAddr, token)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
