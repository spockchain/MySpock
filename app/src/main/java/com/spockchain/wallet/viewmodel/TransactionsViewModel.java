package com.spockchain.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.common.collect.Lists;
import com.spockchain.wallet.domain.ETHWallet;
import com.spockchain.wallet.entity.NetworkInfo;
import com.spockchain.wallet.entity.TransactionMetadata;
import com.spockchain.wallet.interact.FetchTransactionsInteract;
import com.spockchain.wallet.interact.FetchWalletInteract;
import com.spockchain.wallet.repository.EthereumNetworkRepository;
import com.spockchain.wallet.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;

import static com.spockchain.wallet.repository.TransactionRepository.TRANSACTIONS_PER_PAGE;

public class TransactionsViewModel extends BaseViewModel {
    private static final long FETCH_TRANSACTIONS_INTERVAL = 1;
    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();
    private final MutableLiveData<ETHWallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<List<TransactionMetadata>> transactions = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> defaultWalletBalance = new MutableLiveData<>();

    private int pageIndex = 0;

    public MutableLiveData<Boolean> getHasMoreTransactions() {
        return hasMoreTransactions;
    }

    private final MutableLiveData<Boolean> hasMoreTransactions = new MutableLiveData<>();

    private final EthereumNetworkRepository ethereumNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;
    private final FetchTransactionsInteract fetchTransactionsInteract;

    private Disposable transactionDisposable;

    private String tokenAddr;

    TransactionsViewModel(
            EthereumNetworkRepository ethereumNetworkRepository,
            FetchWalletInteract findDefaultWalletInteract,
            FetchTransactionsInteract fetchTransactionsInteract) {
        this.ethereumNetworkRepository = ethereumNetworkRepository;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.fetchTransactionsInteract = fetchTransactionsInteract;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (transactionDisposable != null) {
            transactionDisposable.dispose();
        }
//        balanceDisposable.dispose();
    }

    public LiveData<NetworkInfo> defaultNetwork() {
        return defaultNetwork;
    }

    public LiveData<ETHWallet> defaultWallet() {
        return defaultWallet;
    }

    public LiveData<List<TransactionMetadata>> transactions() {
        return transactions;
    }

    public LiveData<Map<String, String>> defaultWalletBalance() {
        return defaultWalletBalance;
    }

    public void prepare(String token) {
        this.tokenAddr = token;
        progress.postValue(true);
        disposable = ethereumNetworkRepository
                .find()
                .subscribe(this::onDefaultNetwork, this::onError);
    }

    public void resetPage(){
        pageIndex = 0;
    }

    public void fetchTransactions() {
        progress.postValue(true);

        fetchTransactionsInteract
                .fetch(defaultWallet.getValue().address, 0)
                .subscribe(this::onFirstPageTransactions, this::onError);

//        transactionDisposable = Observable.interval(0, FETCH_TRANSACTIONS_INTERVAL, TimeUnit.MINUTES)
//            .doOnNext(l ->
//                disposable = fetchTransactionsInteract
////                        .fetch(defaultWallet.getValue().address, 0)
//                        .fetch("SPOCK-bbb97b57213c2aa4ea7b29a600bbbae77461a844", 0)
//                        .subscribe(this::onTransactions, this::onError))
//            .subscribe();
    }

    public void fetchNextPageTransactions() {
        progress.postValue(true);

        fetchTransactionsInteract
                .fetch(defaultWallet.getValue().address, ++pageIndex)
                .subscribe(this::onTransactions, this::onError);
    }

    public void getBalance() {
//        balanceDisposable = Observable.interval(0, GET_BALANCE_INTERVAL, TimeUnit.SECONDS)
//                .doOnNext(l -> getDefaultWalletBalance
//                        .get(defaultWallet.getValue())
//                        .subscribe(defaultWalletBalance::postValue, t -> {}))
//                .subscribe();
    }

    private void onDefaultNetwork(NetworkInfo networkInfo) {
        defaultNetwork.postValue(networkInfo);
        disposable = findDefaultWalletInteract
                .findDefault()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    private void onDefaultWallet(ETHWallet wallet) {
        LogUtils.d("onDefaultWallet");
        defaultWallet.setValue(wallet);
//        getBalance();
        fetchTransactions();
    }

    private void onFirstPageTransactions(TransactionMetadata[] transactions) {
        progress.postValue(false);
        hasMoreTransactions.postValue(transactions.length >= TRANSACTIONS_PER_PAGE);
        this.transactions.postValue(Arrays.asList(transactions));
    }

    private void onTransactions(TransactionMetadata[] transactions) {
        progress.postValue(false);
        hasMoreTransactions.postValue(transactions.length >= TRANSACTIONS_PER_PAGE);

        List<TransactionMetadata> currentList = new ArrayList<>();

        if (this.transactions.getValue() != null){
            currentList.addAll(this.transactions.getValue());
        }

        currentList.addAll(Arrays.asList(transactions));

        this.transactions.postValue(currentList);

//        // ETH transfer ignores the contract call
//        if (TextUtils.isEmpty(tokenAddr)) {
//            ArrayList<Transaction> transactionList = new ArrayList<>();
//            LogUtils.d("transactions size:" + transactionList.size());
//            for (TransactionMetadata t : transactions) {
//                if (t.operations == null || t.operations.length == 0) {
//                    transactionList.add(t);
//                }
//            }
//            this.transactions.postValue(transactionList);
//        } else {
//            this.transactions.postValue(Arrays.asList(transactions));
//        }


    }

}
