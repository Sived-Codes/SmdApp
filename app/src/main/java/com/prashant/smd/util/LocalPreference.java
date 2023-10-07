package com.prashant.smd.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class LocalPreference {

    private static final String USER_DETAILS = "INC11";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;


    public static void storeUserDetail(Context context, String isAdmin, String mobile) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("admin", isAdmin);
        editor.putString("mobile", mobile);
        editor.apply();
    }

    public static void loginVoice(Context context, String string) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("done", string);
        editor.apply();
    }

    public static String getLoginVoiceStatus(Context context) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        return sharedPref.getString("done", "");
    }


    public static void otpVoice(Context context, String string) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("done", string);
        editor.apply();
    }

    public static String getOtpVoiceStatus(Context context) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        return sharedPref.getString("done", "");
    }


    public static String getMobile(Context context) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        return sharedPref.getString("mobile", "");
    }


    public static String isAdmin(Context context) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        return sharedPref.getString("admin", "");
    }

    public static void clearData(Context context) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(context, "Cleared", Toast.LENGTH_SHORT).show();
    }


}
