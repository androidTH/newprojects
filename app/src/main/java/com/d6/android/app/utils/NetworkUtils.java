package com.d6.android.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;

import com.d6.android.app.models.FriendBean;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.INTERNET;

/**
 * author : jinjiarui
 * time   : 2018/10/05
 * desc   :
 * version:
 */
public class NetworkUtils {
    private static final String TAG = "NetUtil";
    private static final boolean D = true;
    private static NetConnChangedReceiver sNetConnChangedReceiver = new NetConnChangedReceiver();
    private static List<NetConnChangedListener> sNetConnChangedListeners = new ArrayList<>();

    /**
     * 是否有网络连接
     *
     * @return
     */
    public static boolean hasInternet(Context context) {
        try {
            ConnectivityManager localConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (localConnectivityManager != null) {
                NetworkInfo localNetworkInfo = localConnectivityManager
                        .getNetworkInfo(1);
                if ((localNetworkInfo != null)
                        && localNetworkInfo.isConnected()) {
                    if (localNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
                localNetworkInfo = localConnectivityManager.getNetworkInfo(0);
                if (localNetworkInfo != null && localNetworkInfo.isConnected())
                    if (localNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 是否移动数据连接
     *
     * @param context 上下文
     * @return {@code true} 移动数据连接
     */
    public static boolean isMobileConnected(Context context) {
        checkNonNull(context, "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        return (activeInfo != null && activeInfo.isConnected() && activeInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * 是否2G网络连接
     *
     * @param context 上下文
     * @return {@code true} 2G网络连接
     */
    public static boolean is2GConnected(Context context) {
        checkNonNull(context, "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        if (activeInfo == null || !activeInfo.isConnected()) {
            return false;
        }
        int subtype = activeInfo.getSubtype();
        switch (subtype) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否3G网络连接
     *
     * @param context 上下文
     * @return {@code true} 3G网络连接
     */
    public static boolean is3GConnected(Context context) {
        checkNonNull(context, "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        if (activeInfo == null || !activeInfo.isConnected()) {
            return false;
        }
        int subtype = activeInfo.getSubtype();
        switch (subtype) {
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否4G网络连接
     *
     * @param context 上下文
     * @return {@code true} 4G网络连接
     */
    public static boolean is4GConnected(Context context) {
        checkNonNull(context, "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        if (activeInfo == null || !activeInfo.isConnected()) {
            return false;
        }
        int subtype = activeInfo.getSubtype();
        switch (subtype) {
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                return true;
            default:
                return false;
        }
    }

    /**
     * 获取移动网络运营商名称
     * <lu>
     * <li>中国联通</li>
     * <li>中国移动</li>
     * <li>中国电信</li>
     * </lu>
     *
     * @param context 上下文
     * @return 移动网络运营商名称
     */
    public static String getNetworkOperatorName(Context context) {
        checkNonNull(context, "context == null");
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName();
    }

    /**
     * 获取移动终端类型
     *
     * @param context 上下文
     * @return 手机制式
     * <ul>
     * <li>{@link TelephonyManager#PHONE_TYPE_NONE } : 0 手机制式未知</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_GSM  } : 1 手机制式为GSM，移动和联通</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_CDMA } : 2 手机制式为CDMA，电信</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_SIP  } : 3</li>
     * </ul>
     */
    public static int getPhoneType(Context context) {
        checkNonNull(context, "context == null");
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneType();
    }

    /**
     * 判断是否Wifi连接
     *
     * @param context 上下文
     * @return true 如果是wifi连接
     */
    public static boolean isWifiConnected(Context context) {
        checkNonNull(context, "context == null");
        NetworkInfo activeInfo = getActiveNetworkInfo(context);
        return (activeInfo != null && activeInfo.isConnected() && activeInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }


    private static final class NetConnChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo activeInfo = getActiveNetworkInfo(context);
            if (activeInfo == null) {
                broadcastConnStatus(ConnectStatus.NO_NETWORK);
            } else if (activeInfo.isConnected()) {
                int networkType = activeInfo.getType();
                if (ConnectivityManager.TYPE_WIFI == networkType) {
                    broadcastConnStatus(ConnectStatus.WIFI);
                } else if (ConnectivityManager.TYPE_MOBILE == networkType) {
                    broadcastConnStatus(ConnectStatus.MOBILE);
                    int subtype = activeInfo.getSubtype();
                    if (TelephonyManager.NETWORK_TYPE_GPRS == subtype
                            || TelephonyManager.NETWORK_TYPE_GSM == subtype
                            || TelephonyManager.NETWORK_TYPE_EDGE == subtype
                            || TelephonyManager.NETWORK_TYPE_CDMA == subtype
                            || TelephonyManager.NETWORK_TYPE_1xRTT == subtype
                            || TelephonyManager.NETWORK_TYPE_IDEN == subtype) {
                        broadcastConnStatus(ConnectStatus.MOBILE_2G);
                    } else if (TelephonyManager.NETWORK_TYPE_UMTS == subtype
                            || TelephonyManager.NETWORK_TYPE_EVDO_0 == subtype
                            || TelephonyManager.NETWORK_TYPE_EVDO_A == subtype
                            || TelephonyManager.NETWORK_TYPE_HSDPA == subtype
                            || TelephonyManager.NETWORK_TYPE_HSUPA == subtype
                            || TelephonyManager.NETWORK_TYPE_HSPA == subtype
                            || TelephonyManager.NETWORK_TYPE_EVDO_B == subtype
                            || TelephonyManager.NETWORK_TYPE_EHRPD == subtype
                            || TelephonyManager.NETWORK_TYPE_HSPAP == subtype
                            || TelephonyManager.NETWORK_TYPE_TD_SCDMA == subtype) {
                        broadcastConnStatus(ConnectStatus.MOBILE_3G);
                    } else if (TelephonyManager.NETWORK_TYPE_LTE == subtype
                            || TelephonyManager.NETWORK_TYPE_IWLAN == subtype) {
                        broadcastConnStatus(ConnectStatus.MOBILE_4G);
                    } else {
                        broadcastConnStatus(ConnectStatus.MOBILE_UNKNOWN);
                    }
                } else {
                    broadcastConnStatus(ConnectStatus.OTHER);
                }
            } else {
                broadcastConnStatus(ConnectStatus.NO_CONNECTED);
            }
        }
    }

    /**
     * 注册网络接收者
     * * @param context 上下文
     */
    public static void registerNetConnChangedReceiver(Context context) {
        checkNonNull(context, "context == null");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(sNetConnChangedReceiver, filter);
    }

    /**
     * 取消注册网络接收者
     * * @param context 上下文
     */
    public static void unregisterNetConnChangedReceiver(Context context) {
        checkNonNull(context, "context == null");
        context.unregisterReceiver(sNetConnChangedReceiver);
        sNetConnChangedListeners.clear();
    }

    /**
     * 添加网络状态变化监听
     *
     * @param listener 网络连接状态改变监听
     */
    public static void addNetConnChangedListener(NetConnChangedListener listener) {
        checkNonNull(listener, "listener == null");
        boolean result = sNetConnChangedListeners.add(listener);
    }

    /**
     * 移除指定网络变化监听
     *
     * @param listener 网络连接状态改变监听
     */
    public static void removeNetConnChangedListener(NetConnChangedListener listener) {
        checkNonNull(listener, "listener == null");
        boolean result = sNetConnChangedListeners.remove(listener);
    }

    private static void broadcastConnStatus(ConnectStatus connectStatus) {
        int size = sNetConnChangedListeners.size();
        if (size == 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            sNetConnChangedListeners.get(i).onNetConnChanged(connectStatus);
        }
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo();
    }

    private static void checkNonNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public interface NetConnChangedListener {
        void onNetConnChanged(ConnectStatus connectStatus);
    }

    public enum ConnectStatus {
        NO_NETWORK,
        WIFI,
        MOBILE,
        MOBILE_2G,
        MOBILE_3G,
        MOBILE_4G,
        MOBILE_UNKNOWN,
        OTHER,
        NO_CONNECTED
    }

    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public enum NetworkType {
        NETWORK_ETHERNET,
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    /**
     * Open the settings of wireless.
     */
    public static void openWirelessSettings(Context context) {
        context.startActivity(
                new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    /**
     * Return the ip address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @RequiresPermission(INTERNET)
    public static String getIPAddress(final boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) return hostAddress;
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
}
