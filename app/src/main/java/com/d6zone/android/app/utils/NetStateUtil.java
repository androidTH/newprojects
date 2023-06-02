package com.d6zone.android.app.utils;


import android.content.Context;
import android.util.Log;

import com.d6zone.android.app.models.IPBean;
import com.d6zone.android.app.models.IPList;
import com.d6zone.android.app.net.API;
import com.d6zone.android.app.widget.retrofitmanager.RetrofitUrlManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author : jinjiarui
 * time   : 2021/09/21
 * desc   :
 * version:
 */
public class NetStateUtil {
    static HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            return true;
        }
    };

    public static boolean connectingAddress(String remoteInetAddr) {
        boolean flag = false;
        String tempUrl = remoteInetAddr.substring(0, 5);//取出地址前5位
        if (tempUrl.contains("http")) {//判断传过来的地址中是否有http
            if (tempUrl.equals("https")) {//判断服务器是否是https协议
                try {
                    trustAllHttpsCertificates();//当协议是https时
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpsURLConnection.setDefaultHostnameVerifier(hv);//当协议是https时
            }
            flag = isConnServerByHttp(remoteInetAddr);
        } else {//传过来的是IP地址
            flag = isReachable(remoteInetAddr);
        }
//        if (flag) {
//            return "正常";
//        } else {
//            return "异常";
//        }
        return flag;
    }

    /*以下是Https适用*/
    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }

    public static boolean isConnServerByHttp(String serverUrl) {// 服务器是否开启
        boolean connFlag = false;
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(serverUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            if (conn.getResponseCode() == 200) {// 如果连接成功则设置为true
                connFlag = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return connFlag;
    }

    /**
     * 传入需要连接的IP，返回是否连接成功
     *
     * @param remoteInetAddr
     * @return
     */
    public static boolean isReachable(String remoteInetAddr) {// IP地址是否可达，相当于Ping命令
        boolean reachable = false;
        try {
            InetAddress address = InetAddress.getByName(remoteInetAddr);
            reachable = address.isReachable(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reachable;
    }

    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    /*以上是Https适用*/

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

    public static void connectingAddress(final Context activity){
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                boolean IsConnect = isConnect(API.BASE_URL+"backstage/account/pin");//NetStateUtil.connectingAddress(API.URL);//BaseUtils.isConnect("http://apis_test.d6-zone.com/");
                if(!IsConnect){
                    OkHttpClient mOkHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url(API.GETDOMAIN).build();
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
//                                    Looper.prepare();
//                                    Toast.makeText(activity,"域名切换成功",Toast.LENGTH_SHORT).show();
//                                    Looper.loop();
                                    if(mIPList!=null&&mIPList.size()>=1){
                                        if(isConnect(mIPList.get(0).getIp())){
                                            RetrofitUrlManager.getInstance().setGlobalDomain(mIPList.get(0).getIp()+API.LIYU_URL);
                                            return;
                                        }
                                    }

                                    if(mIPList!=null&&mIPList.size()>=2){
                                        if(isConnect(mIPList.get(1).getIp())){
                                            RetrofitUrlManager.getInstance().setGlobalDomain(mIPList.get(1).getIp()+API.LIYU_URL);
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
                    Log.i("httpurl","状态"+IsConnect);
                }
            }
        });
    }
}
