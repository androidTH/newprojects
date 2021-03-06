package com.d6.android.app.easypay.network;

import android.util.Log;

import com.d6.android.app.easypay.PayParams;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Author: michaelx
 * Create: 17-3-17.
 * <p>
 * Endcode: UTF-8
 * <p>
 * Blog:http://blog.csdn.net/xiong_it | https://xiong-it.github.io
 * github:https://github.com/xiong-it
 * <p>
 * Description: retrofit网络请求简单封装.
 */

public class RetrofitClient implements NetworkClientInterf {
    @Override
    public void get(PayParams payParams, final CallBack c) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(payParams.getApiUrl())
                .build();
        PrePayInfoService service = retrofit.create(PrePayInfoService.class);
        Call<ResponseBody> call = service.getPrePayInfo(payParams.getPayWay().toString(), String.valueOf(payParams.getGoodsPrice()), payParams.getGoodsName(), payParams.getGoodsIntroduction());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    c.onSuccess(response.body().toString());
                } else {
                    c.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                c.onFailure();
            }
        });
    }

    @Override
    public void post(PayParams payParams, final CallBack c) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(payParams.getApiUrl())
                .build();
        PrePayInfoService service = retrofit.create(PrePayInfoService.class);
        Call<ResponseBody> call=null;
        if(payParams.getType()==1){
            call = service.postBuyFlower(payParams.getiUserid(),payParams.getiSendUserid(),payParams.getsResourceid(),Integer.valueOf(payParams.getPayWay().toString()),payParams.getGoodsPrice(),payParams.getiFlowerCount());
        }else if(payParams.getType() == 0){
            call = service.postPrePayInfo(payParams.getiUserid(),Integer.valueOf(payParams.getPayWay().toString()),payParams.getGoodsPrice(),payParams.getiPoint());
        }else if(payParams.getType()==2){
            call = service.postAddUserClass(payParams.getiUserid(),Integer.valueOf(payParams.getPayWay().toString()),payParams.getGoodsPrice(),payParams.getsAreaName(),payParams.getiUserclassid());
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        c.onSuccess(result);
                        Log.i("RetrofitClient","内容:"+result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    c.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                c.onFailure();
            }
        });
    }
}
