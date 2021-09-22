package com.d6.android.app.utils;

import android.util.Log;

import com.d6.android.app.models.IPBean;
import com.d6.android.app.models.IPList;
import com.d6.android.app.net.API;
import com.d6.android.app.widget.retrofitmanager.RetrofitUrlManager;
import com.huawei.hms.api.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.d6.android.app.utils.UtilKt.getAppVersion;
import static com.d6.android.app.utils.UtilKt.getLoginToken;

public class BaseUtils {
    public static String replaceUrlParamReg(String url, String name, String value) {
        url = url.replaceAll("(" + name + "=[^&]*)", name + "=" + value);
        System.out.println(url);
        return url;
    }

    public static String urlEncodeUTF8(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static boolean isConnect(String url) {
        String result = null;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS).build();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            if(response.code()!=200){
                return false;
            }
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void connectingAddress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean IsConnect = BaseUtils.isConnect(API.URL);// NetStateUtil.connectingAddress(API.URL);//BaseUtils.isConnect("http://apis_test.d6-zone.com/");
                Log.i("httpurl","内容："+IsConnect);
                if(!IsConnect){
                    OkHttpClient mOkHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url("http://domain_test.d6-zone.com/getDomain").build();
                    mOkHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    String obj = jsonObject.optString("obj");
                                    List<IPBean> mIPList = GsonHelper.GsonToBean(obj, IPList.class).getDomain();
                                    Log.i("httpurl","内容："+mIPList.get(0));
                                    if(mIPList!=null){
                                        if(BaseUtils.isConnect(mIPList.get(0).getIp())){
                                            RetrofitUrlManager.getInstance().setGlobalDomain(mIPList.get(0).getIp()+"JyPhone/");
                                            return;
                                        }
                                    }

                                    if(mIPList!=null&&mIPList.size()>=2){
                                        if(BaseUtils.isConnect(mIPList.get(1).getIp())){
                                            RetrofitUrlManager.getInstance().setGlobalDomain(mIPList.get(1).getIp()+"JyPhone/");
                                            return;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }else{
                    Log.i("httpurl","内容：success");
                }
            }
        }).start();
    }
}
