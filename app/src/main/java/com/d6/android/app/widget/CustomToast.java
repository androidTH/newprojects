package com.d6.android.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d6.android.app.R;
import com.d6.android.app.utils.AppUtils;

/**
 * Created by fangwu on 2/6/2017.
 */
public class CustomToast {

    @ColorInt
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");

    @ColorInt
    private static final int ERROR_COLOR = Color.parseColor("#FD4C5B");

    @ColorInt
    private static final int INFO_COLOR = Color.parseColor("#3F51B5");

    @ColorInt
    private static final int SUCCESS_COLOR = Color.parseColor("#388E3C");

    @ColorInt
    private static final int WARNING_COLOR = Color.parseColor("#FFA900");
    private static final int TEXT_COLOR_BLACK= Color.parseColor("#333333");
    private static final int TOAST_BG = Color.parseColor("#FFFFFF");

    private static final String TOAST_TYPEFACE = "sans-serif-condensed";

    private static Toast currentToast;

    private static Context getLocalContext(){
           return AppUtils.Companion.getContext();
    }

    //*******************************************普通 使用ApplicationContext 方法*********************
    public static void showToast(@NonNull String message) {
        normal(getLocalContext(), message, Toast.LENGTH_SHORT, null, false).show();
    }

    public static void showToast(Context context, @NonNull String message) {
        normal(context, message, Toast.LENGTH_SHORT, null, false).show();
    }

    public static void showToast(@NonNull String message, Drawable icon) {
        normal(getLocalContext(), message, Toast.LENGTH_SHORT, icon, true).show();
    }

    public static void showToast(@NonNull String message, int duration) {
        normal(getLocalContext(), message, duration, null, false).show();
    }

    public static void showToast(@NonNull String message, int duration, Drawable icon) {
        normal(getLocalContext(), message, duration, icon, true).show();
    }

    public static Toast showToast(@NonNull String message, int duration, Drawable icon, boolean withIcon) {
        return custom(getLocalContext(), message, icon, DEFAULT_TEXT_COLOR, duration, withIcon);
    }

    public static void info(@NonNull String message) {
        info(getLocalContext(), message, Toast.LENGTH_SHORT, true).show();
    }

    public static void info(@NonNull String message, int duration) {
        info(getLocalContext(), message, duration, true).show();
    }

    public static Toast info(@NonNull String message, int duration, boolean withIcon) {
        return custom(getLocalContext(), message, getDrawable(getLocalContext(), R.drawable.ic_info_outline_white_48dp), DEFAULT_TEXT_COLOR, INFO_COLOR, duration, withIcon, true);
    }

    public static void success(@NonNull String message) {
        success(getLocalContext(), message, Toast.LENGTH_SHORT, true).show();
    }

    public static void success(@NonNull String message, int duration) {
        success(getLocalContext(), message, duration, true).show();
    }

    public static Toast success(@NonNull String message, int duration, boolean withIcon) {
        return custom(getLocalContext(), message, getDrawable(getLocalContext(), R.drawable.ic_check_white_48dp), DEFAULT_TEXT_COLOR, SUCCESS_COLOR, duration, withIcon, true);
    }

    public static Toast success(@NonNull String message,int iconId, int duration, boolean withIcon) {
        return custom(getLocalContext(), message, getDrawable(getLocalContext(),iconId), TEXT_COLOR_BLACK, TOAST_BG, duration, withIcon, true);
    }

    //===========================================使用ApplicationContext 方法=========================

    //*******************************************常规方法********************************************

    public static Toast error(@NonNull String message, int duration, boolean withIcon) {
        return custom(getLocalContext(), message, getDrawable(getLocalContext(), R.drawable.ic_clear_white_48dp), DEFAULT_TEXT_COLOR, ERROR_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull String message) {
        return normal(context, message, Toast.LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull String message, Drawable icon) {
        return normal(context, message, Toast.LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull String message, int duration) {
        return normal(context, message, duration, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull String message, int duration, Drawable icon) {
        return normal(context, message, duration, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull String message, int duration, Drawable icon, boolean withIcon) {
        return custom(context, message, icon, DEFAULT_TEXT_COLOR, duration, withIcon);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @NonNull String message) {
        return info(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @NonNull String message, int duration) {
        return info(context, message, duration, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @NonNull String message, int duration, boolean withIcon) {
        return custom(context, message, getDrawable(context, R.drawable.ic_info_outline_white_48dp), DEFAULT_TEXT_COLOR, INFO_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull String message) {
        return success(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull String message, int duration) {
        return success(context, message, duration, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull String message, int duration, boolean withIcon) {
        return custom(context, message, getDrawable(context, R.drawable.ic_check_white_48dp), DEFAULT_TEXT_COLOR, SUCCESS_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull String message, Drawable icon, @ColorInt int textColor, int duration, boolean withIcon) {
        return custom(context, message, icon, textColor, -1, duration, withIcon, false);
    }

    //*******************************************内需方法********************************************

    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull String message, @DrawableRes int iconRes, @ColorInt int textColor, @ColorInt int tintColor, int duration, boolean withIcon, boolean shouldTint) {
        return custom(context, message, getDrawable(context, iconRes), textColor, tintColor, duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull String message, Drawable icon, @ColorInt int textColor, @ColorInt int tintColor, int duration, boolean withIcon, boolean shouldTint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            currentToast = new Toast(context);
        } else if (currentToast == null) {
            currentToast = new Toast(context);
        }
        final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_toast, null);
        final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;

        if (shouldTint) {
            drawableFrame = tint9PatchDrawableFrame(context, tintColor);
        } else {
            drawableFrame = getDrawable(context, R.drawable.toast_frame);
        }
        setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon == null)
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            setBackground(toastIcon, icon);
        } else{
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setTextColor(textColor);
        toastTextView.setText(message);
        toastTextView.setTypeface(Typeface.create(TOAST_TYPEFACE, Typeface.NORMAL));

        currentToast.setView(toastLayout);
        currentToast.setDuration(duration);
        return currentToast;
    }

    public static Drawable tint9PatchDrawableFrame(@NonNull Context context, @ColorInt int tintColor) {
        final NinePatchDrawable toastDrawable = (NinePatchDrawable) getDrawable(context, R.drawable.toast_frame);
        toastDrawable.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        return toastDrawable;
    }
    //===========================================内需方法============================================


    //******************************************系统 Toast 替代方法***************************************

    public static void setBackground(@NonNull View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(drawable);
        else
            view.setBackgroundDrawable(drawable);
    }

    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return context.getDrawable(id);
        else
            return context.getResources().getDrawable(id);
    }
}
