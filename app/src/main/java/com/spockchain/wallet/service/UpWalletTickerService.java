package com.spockchain.wallet.service;

import com.spockchain.wallet.entity.Ticker;
import com.google.gson.Gson;

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
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.spockchain.wallet.C.TICKER_API_URL;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class UpWalletTickerService implements TickerService {

    //
    private final OkHttpClient httpClient;
    private final Gson gson;
    private ApiClient apiClient;

    public UpWalletTickerService(
            OkHttpClient httpClient,
            Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
        buildApiClient(TICKER_API_URL);
    }

    private void buildApiClient(String baseUrl) {
        apiClient = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiClient.class);
    }

    @Override
    public Observable<Ticker> fetchTickerPrice(String market) {
        return apiClient
                .fetchTickerPrice(market)
                .lift(apiError())
                .map(r -> r.data)
                .subscribeOn(Schedulers.io());
    }

    private static @NonNull
    <T> ApiErrorOperator<T> apiError() {
        return new ApiErrorOperator<>();
    }

    public interface ApiClient {
        @GET("open/api/v1/data/ticker?")
        Observable<Response<TickerResponse>> fetchTickerPrice(@Query("market") String market);
    }

    private static class TickerResponse {
        Ticker data;
    }

    private final static class ApiErrorOperator <T> implements ObservableOperator<T, Response<T>> {

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
