package com.spockchain.wallet.service;


import com.spockchain.wallet.entity.Transaction;
import com.spockchain.wallet.entity.TransactionMetadata;

import io.reactivex.Observable;

public interface BlockExplorerClientType {
    Observable<Transaction[]> fetchTransactions(String forAddress, String forToken);

    Observable<TransactionMetadata[]> fetchTransactions(FetchTransactionsRequestBody body);
}
