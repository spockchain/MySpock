package com.spockchain.wallet.interact;


import com.spockchain.wallet.entity.Transaction;
import com.spockchain.wallet.entity.TransactionMetadata;
import com.spockchain.wallet.repository.TransactionRepositoryType;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class FetchTransactionsInteract {

    private final TransactionRepositoryType transactionRepository;

    public FetchTransactionsInteract(TransactionRepositoryType transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Observable<TransactionMetadata[]> fetch(String walletAddr, int pageIndex) {
        return transactionRepository
                .fetchTransactions(walletAddr, pageIndex)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<Transaction> findTransaction(String transactionHash) {
        return transactionRepository
                .findTransaction(transactionHash)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
