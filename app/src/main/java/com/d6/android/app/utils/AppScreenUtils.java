package com.d6.android.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import static android.view.View.NO_ID;

/**
 * 获得屏幕相关的辅助类
 * 
 * @author zhy
 * 
 */
public class AppScreenUtils
{
	private AppScreenUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics outMetrics;
		try{
			if(context!=null){
				WindowManager wm = (WindowManager) context
						.getSystemService(Context.WINDOW_SERVICE);
				outMetrics = new DisplayMetrics();
				wm.getDefaultDisplay().getMetrics(outMetrics);
			}else{
				return 1920;
			}
		}catch(Exception e){
			e.printStackTrace();
			return 1920;
		}
		return outMetrics.widthPixels;
	}

	public static int getScreenRealWidth(Context context) {
		DisplayMetrics outMetrics;
		try{
			if(context!=null){
				WindowManager wm = (WindowManager) context
						.getSystemService(Context.WINDOW_SERVICE);
				outMetrics = new DisplayMetrics();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					wm.getDefaultDisplay().getRealMetrics(outMetrics);
				}
			}else{
				return 1920;
			}
		}catch(Exception e){
			e.printStackTrace();
			return 1920;
		}
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	public static int getScreenRealHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			wm.getDefaultDisplay().getRealMetrics(outMetrics);
		}else{
			wm.getDefaultDisplay().getMetrics(outMetrics);
		}
		return outMetrics.heightPixels;
	}

	public static float getPhoneRatio(Context context) {
		return ((float) getScreenHeight(context)) / ((float) getScreenWidth(context));
	}

	public static float getPhoneRealRatio(Context context,boolean isShow,int NavHeight) {
		if(isShow){
			return ((float)(getScreenRealHeight(context) - NavHeight)) / ((float) getScreenRealWidth(context));
		}
		return ((float)getScreenRealHeight(context)) / ((float) getScreenRealWidth(context));
	}

	public static float getPhoneRealRatio(Context context) {
			return ((float)(getScreenRealHeight(context) - getNavigationBarHeight(context))) / ((float) getScreenRealWidth(context));
	}

	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context)
	{

		int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusHeight;
	}

	/**
	 * 获取当前屏幕截图，包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;

	}


	/**
	 * 判断虚拟导航栏是否显示
	 *
	 * @param context 上下文对象
	 * @return true(显示虚拟导航栏)，false(不显示或不支持虚拟导航栏)
	 */
	public static boolean checkNavigationBarShow(@NonNull Context context) {
		boolean hasNavigationBar = false;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			//判断是否隐藏了底部虚拟导航
			int navigationBarIsMin = 0;
			String brand = Build.BRAND;
			String mDeviceInfo;
			if (brand.equalsIgnoreCase("HUAWEI")) {
				mDeviceInfo = "navigationbar_is_min";
			} else if (brand.equalsIgnoreCase("XIAOMI")) {
				mDeviceInfo = "force_fsg_nav_bar";
			} else if (brand.equalsIgnoreCase("VIVO")) {
				mDeviceInfo = "navigation_gesture_on";
			} else if (brand.equalsIgnoreCase("OPPO")) {
				mDeviceInfo = "navigation_gesture_on";
			} else {
				mDeviceInfo = "navigationbar_is_min";
			}
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				navigationBarIsMin = Settings.System.getInt(context.getContentResolver(),
						mDeviceInfo, 0);
			} else {
				navigationBarIsMin = Settings.Global.getInt(context.getContentResolver(),
						mDeviceInfo, 0);
			}
			if ("1".equals(navBarOverride) || 1 == navigationBarIsMin) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
			Log.i("AppScreenUtils", Build.MANUFACTURER+"ddddd"+brand);
		} catch (Exception e) {
		}
		return hasNavigationBar;
	}

	private static final String NAVIGATION= "navigationBarBackground";

	// 该方法需要在View完全被绘制出来之后调用，否则判断不了
	//在比如 onWindowFocusChanged（）方法中可以得到正确的结果
	public static boolean isNavigationBarExist(@NonNull Activity activity){
		ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
		if (vp != null) {
			for (int i = 0; i < vp.getChildCount(); i++) {
				vp.getChildAt(i).getContext().getPackageName();
				if (vp.getChildAt(i).getId()!= NO_ID && NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
					return true;
				}
			}
		}
		return false;
	}

	public static void isNavigationBarExist(Activity activity, final OnNavigationStateListener onNavigationStateListener) {
		if (activity == null) {
			return;
		}
		final int height = getNavigationHeight(activity);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
			activity.getWindow().getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
				@Override
				public WindowInsets onApplyWindowInsets(View v, WindowInsets windowInsets) {
					boolean isShowing = false;
					int b = 0;
					if (windowInsets != null) {
						b = windowInsets.getSystemWindowInsetBottom();
						isShowing = (b == height);
					}
					if (onNavigationStateListener != null && b <= height) {
						onNavigationStateListener.onNavigationState(isShowing, b);
					}
					return windowInsets;
				}
			});
		}
	}

	public interface OnNavigationStateListener{
		void onNavigationState(boolean isShowing,int b);
	}

	public static int getNavigationHeight(Context activity) {
		if (activity == null) {
			return 0;
		}
		Resources resources = activity.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height",
				"dimen", "android");
		int height = 0;
		if (resourceId > 0) {
			//获取NavigationBar的高度
			height = resources.getDimensionPixelSize(resourceId);
		}
		return height;
	}

	public static int getNavigationBarHeight(@NonNull Context context) {
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
		int height = 0;
		if(checkNavigationBarShow(context)){
			height = resources.getDimensionPixelSize(resourceId);
		}
		return height;
	}

	//true 的话就是隐藏的虚拟导航栏 false 反之是显示导航栏
	public static boolean xiaomiNavigationBarHeight(@NonNull Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if ((Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0)) {
                return true;
            }
        }
        return false;
    }

	protected static final String PREFS_FILE = "device_id.xml";
	protected static final String PREFS_DEVICE_ID = "device_id";
	protected static volatile UUID uuid;
	/**
	 *
	 * @param context
	 * @return
	 */
	@SuppressLint("MissingPermission")
	public static String getIMEI(Context context) {
		if (uuid == null) {
			synchronized (AppScreenUtils.class) {
				if (uuid == null) {
					final SharedPreferences prefs = context
							.getSharedPreferences(PREFS_FILE, 0);
					final String id = prefs.getString(PREFS_DEVICE_ID, null);
					if (id != null) {
						// Use the ids previously computed and stored in the
						// prefs file
						uuid = UUID.fromString(id);
					} else {
						final String androidId = Settings.Secure.getString(
								context.getContentResolver(), Settings.Secure.ANDROID_ID);
						// Use the Android ID unless it's broken, in which case
						// fallback on deviceId,
						// unless it's not available, then fallback on a random
						// number which we store to a prefs file
						try {
							if (!"9774d56d682e549c".equals(androidId)) {
								uuid = UUID.nameUUIDFromBytes(androidId
										.getBytes("utf8"));
							} else {
								final String deviceId = ((TelephonyManager)
										context.getSystemService(
												Context.TELEPHONY_SERVICE))
										.getDeviceId();
								uuid = deviceId != null ? UUID
										.nameUUIDFromBytes(deviceId
												.getBytes("utf8")) : UUID
										.randomUUID();
							}
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e);
						}
						// Write the value out to the prefs file
						prefs.edit()
								.putString(PREFS_DEVICE_ID, uuid.toString())
								.commit();
					}
				}
			}
		}
		return uuid.toString();
	}

	/**
	 * 进行加密操作
	 * 参数一：待加密的字符串，参数二：加密密钥
	 * 返回经过Base64编码后的字符串
	 * 编码格式为UTF-8
	 */
	public static String encrypt(String encryptionStr, String password) {
		try{
			byte[] encryptionBytes = encryptionStr.getBytes("UTF-8");
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密钥工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密钥初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 执行加密操作
//			byte[] encryptionBase64Bytes = Base64.encodeToString(cipher.doFinal(encryptionBytes),0);
			// 转换为字符串返回
			return Base64.encodeToString(cipher.doFinal(encryptionBytes),Base64.NO_WRAP);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
