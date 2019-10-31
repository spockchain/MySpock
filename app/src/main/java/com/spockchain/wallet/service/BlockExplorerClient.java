package com.spockchain.wallet.service;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.spockchain.wallet.entity.NetworkInfo;
import com.spockchain.wallet.entity.Transaction;
import com.spockchain.wallet.entity.TransactionMetadata;
import com.spockchain.wallet.repository.EthereumNetworkRepository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class BlockExplorerClient implements BlockExplorerClientType {

	private final OkHttpClient httpClient;
	private final Gson gson;
	private final EthereumNetworkRepository networkRepository;

	private TransactionsApiClient transactionsApiClient;

	public BlockExplorerClient(
			OkHttpClient httpClient,
			Gson gson,
			EthereumNetworkRepository networkRepository) {
		this.httpClient = httpClient;
		this.gson = gson;
		this.networkRepository = networkRepository;
		this.networkRepository.addOnChangeDefaultNetwork(this::onNetworkChanged);
		NetworkInfo networkInfo = networkRepository.getDefaultNetwork();
		onNetworkChanged(networkInfo);
	}

	private void buildApiClient(String baseUrl) {
		transactionsApiClient = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(httpClient)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
				.create(TransactionsApiClient.class);
	}

	@Override
	public Observable<Transaction[]> fetchTransactions(String address, String tokenAddr) {
	    if (TextUtils.isEmpty(tokenAddr)) {
            return transactionsApiClient
                    .fetchTransactions(address, true)
                    .lift(apiError(gson))
                    .map(r -> r.docs)
                    .subscribeOn(Schedulers.io());
        } else {
            return transactionsApiClient
                    .fetchTransactions(address, tokenAddr)
                    .lift(apiError(gson))
                    .map(r -> r.docs)
                    .subscribeOn(Schedulers.io());
        }

	}

	@Override
	public Observable<TransactionMetadata[]> fetchTransactions(FetchTransactionsRequestBody body) {
		if (body.getContract() != null && !body.getContract().equals("")){
			return transactionsApiClient
					.fetchTokenTransfers(body)
					.lift(apiError(gson))
					.map(r -> {
						TransactionMetadata[] transactions = new TransactionMetadata[r.getData().size()];
						int index = 0;
						for (List<String> t : r.getData()) {
							transactions[index++] = new TransactionMetadata(body.getContract(), t);
						}
						return transactions;
					})
					.subscribeOn(Schedulers.io());
		}

		return transactionsApiClient
				.fetchTransactions(body)
				.lift(apiError(gson))
				.map(r -> {
					TransactionMetadata[] transactions = new TransactionMetadata[r.getData().size()];
 					int index = 0;
					for (List<String> t : r.getData()) {
						transactions[index++] = new TransactionMetadata(t);
					}
					return transactions;
				})
				.subscribeOn(Schedulers.io());
	}

	private void onNetworkChanged(NetworkInfo networkInfo) {
		buildApiClient(networkInfo.backendUrl);
	}

	private static @NonNull
    <T> ApiErrorOperator<T> apiError(Gson gson) {
		return new ApiErrorOperator<>(gson);
	}


	// TODO(satoshi.meow): Use http://www.spock.network/addr.
	private interface TransactionsApiClient {
		@GET("/transactions?limit=50")
        Observable<Response<EtherScanResponse>> fetchTransactions(
                @Query("address") String address, @Query("filterContractInteraction") boolean filter);

		@GET("/transactions?limit=50")
		Observable<Response<EtherScanResponse>> fetchTransactions(
				@Query("address") String address, @Query("contract") String contract);


		@POST("addr")
		Observable<Response<FetchTransactionsResponse>> fetchTransactions(@Body FetchTransactionsRequestBody body);

		@POST("addressContract")
		Observable<Response<FetchTransactionsResponse>> fetchTokenTransfers(@Body FetchTransactionsRequestBody body);
	}

	private final static class EtherScanResponse {
		Transaction[] docs;
	}

	private final static class ApiErrorOperator <T> implements ObservableOperator<T, Response<T>> {

		private final Gson gson;

		public ApiErrorOperator(Gson gson) {
			this.gson = gson;
		}

		@Override
		public Observer<? super Response<T>> apply(Observer<? super T> observer) throws Exception {
            return new DisposableObserver<Response<T>>() {
                @Override
                public void onNext(Response<T> response) {
                    observer.onNext(response.body());
                    observer.onComplete();
                }

                @Override
                public void onError(Throwable e) {
                    observer.onError(e);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            };
		}
	}
}
