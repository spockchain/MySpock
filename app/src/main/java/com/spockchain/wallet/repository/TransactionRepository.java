package com.spockchain.wallet.repository;


import android.text.TextUtils;
import android.util.Log;

import com.spockchain.wallet.entity.NetworkInfo;
import com.spockchain.wallet.entity.Transaction;
import com.spockchain.wallet.entity.TransactionMetadata;
import com.spockchain.wallet.service.BlockExplorerClientType;
import com.spockchain.wallet.service.FetchTransactionsRequestBody;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import io.reactivex.Maybe;
import io.reactivex.Observable;

public class TransactionRepository implements TransactionRepositoryType {

    public static final int TRANSACTIONS_PER_PAGE = 10;

    private final EthereumNetworkRepository networkRepository;
    private final TransactionLocalSource transactionLocalSource;
    private final BlockExplorerClientType blockExplorerClient;

    public TransactionRepository(
            EthereumNetworkRepository networkRepository,
            TransactionLocalSource inMemoryCache,
            TransactionLocalSource inDiskCache,
            BlockExplorerClientType blockExplorerClient) {
        this.networkRepository = networkRepository;
        this.blockExplorerClient = blockExplorerClient;
        this.transactionLocalSource = inMemoryCache;

        this.networkRepository.addOnChangeDefaultNetwork(this::onNetworkChanged);
    }

    @Override
    public Observable<Transaction[]> fetchTransactions(String walletAddr, String tokenAddr) {
        return Observable.create(e -> {
            Transaction[] transactions;
            if (TextUtils.isEmpty(tokenAddr)) {
                transactions = transactionLocalSource.fetchTransaction(walletAddr).blockingGet();
            } else {
                transactions = transactionLocalSource.fetchTransaction(walletAddr, tokenAddr).blockingGet();
            }

            if (transactions != null && transactions.length > 0) {
                e.onNext(transactions);
            }
            transactions = blockExplorerClient.fetchTransactions(walletAddr, tokenAddr).blockingFirst();
            transactionLocalSource.clear();
            if (TextUtils.isEmpty(tokenAddr)) {
                transactionLocalSource.putTransactions(walletAddr, transactions);
            } else {
                transactionLocalSource.putTransactions(walletAddr, tokenAddr, transactions);
            }
            e.onNext(transactions);
            e.onComplete();
        });
    }

    @Override
    public Observable<TransactionMetadata[]> fetchTransactions(String walletAddr, int pageIndex) {
        FetchTransactionsRequestBody body = new FetchTransactionsRequestBody()
                .withAddr(walletAddr)
                .withLength(TRANSACTIONS_PER_PAGE)
                .withStart(TRANSACTIONS_PER_PAGE * pageIndex);
        return blockExplorerClient.fetchTransactions(body);
    }

    @Override
    public Maybe<Transaction> findTransaction(String walletAddr, String transactionHash) {
        return fetchTransactions(walletAddr, null)
                .firstElement()
                .flatMap(transactions -> {
                    for (Transaction transaction : transactions) {
                        if (transaction.hash.equals(transactionHash)) {
                            return Maybe.just(transaction);
                        }
                    }
                    return null;
                });
    }

    @Override
    public Maybe<Transaction> findTransaction(String transactionHash) {
        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));
        return web3j.ethGetTransactionByHash(transactionHash).flowable().firstElement().map(
                ethTransaction -> {
                    if (ethTransaction.getTransaction().isPresent()) {
                        org.web3j.protocol.core.methods.response.Transaction t = ethTransaction.getTransaction().get();
                        Log.i("qiaoyu", "findTransaction: " + t.getCreates());
                        return new Transaction(t);
                    }
                    return null;
        });
    }

    private void onNetworkChanged(NetworkInfo networkInfo) {
        transactionLocalSource.clear();
    }
}
