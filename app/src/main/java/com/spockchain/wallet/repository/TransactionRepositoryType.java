package com.spockchain.wallet.repository;


import com.spockchain.wallet.entity.Transaction;
import com.spockchain.wallet.entity.TransactionMetadata;

import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface TransactionRepositoryType {
	Observable<Transaction[]> fetchTransactions(String walletAddr, String token);
	Observable<TransactionMetadata[]> fetchTransactions(String walletAddr, String token, int pageIndex);
	Observable<TransactionMetadata[]> fetchTransactions(String walletAddr, int pageIndex);
	Maybe<Transaction> findTransaction(String walletAddr, String transactionHash);
	Maybe<Transaction> findTransaction(String transactionHash);
}
