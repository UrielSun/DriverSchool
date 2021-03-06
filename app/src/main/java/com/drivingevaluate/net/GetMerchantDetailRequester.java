package com.drivingevaluate.net;

import android.util.Log;

import com.drivingevaluate.app.App;
import com.drivingevaluate.app.ServerConf;
import com.drivingevaluate.config.UrlConfig;
import com.drivingevaluate.model.Merchant;

import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Yat3s on 15/8/5.
 * Email:hawkoyates@gmail.com
 */
public class GetMerchantDetailRequester {
    private Callback<Merchant> callback;
    private int merchantId;

    public GetMerchantDetailRequester(Callback<Merchant> callback, int merchantId) {
        this.callback = callback;
        this.merchantId = merchantId;
    }

    private interface GetMerchantDetailService {
        @GET(UrlConfig.merchantDetailAPI)
        void getMerchantDetail(@Query("merchantId") int merchantId, Callback<Merchant> callback);
    }

    public void request(){
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("token", App.DEFAULT_TOKEN);
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ServerConf.SERVER_IP)
                .setErrorHandler(new MyErrorHandler())
                .setRequestInterceptor(requestInterceptor)
                .build();

        GetMerchantDetailService getMerchantDetailService = restAdapter.create(GetMerchantDetailService.class);
        getMerchantDetailService.getMerchantDetail(merchantId,callback);
    }

    class MyErrorHandler implements ErrorHandler {
        @Override public Throwable handleError(RetrofitError cause) {
            Log.e("yat3s", "GetMerchantDetailRequester---->" + cause.getMessage());
            return cause;
        }
    }
}
