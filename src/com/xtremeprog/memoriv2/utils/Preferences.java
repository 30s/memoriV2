package com.xtremeprog.memoriv2.utils;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xtremeprog.memoriv2.R;

public class Preferences {

	public static boolean DEBUG = true;
	public static String SERVER;

	public static void setLoginInfo(Context context, String username,
			String token, String refresh_token, long expire) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		preferences.edit().putString("username", username)
				.putString("token", token)
				.putString("refresh_token", refresh_token)
				.putLong("expire", expire).commit();
	}

	public static void setRemember(Context context, String username,
			String password) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString("username", username)
				.putString("password", password).commit();
	}

	public static String getRememberedUsername(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString("username", "");
	}

	public static String getRememberedPassword(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString("password", "");
	}

	public static String getToken(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString("token", null);
	}

	public static long getExpire(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(
				"expire", 0);
	}

	public static void expireToken(Context context) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putLong("expire", 0).commit();
	}

	public static String getServer(Context context) {
		if (Preferences.SERVER != null) {
			return Preferences.SERVER;
		}

		if (Preferences.DEBUG) {
			Preferences.SERVER = context.getString(R.string.debug_server);
		} else {
			Preferences.SERVER = context.getString(R.string.production_server);
		}

		return Preferences.SERVER;
	}

	public static void setSyncTime(Context context) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putLong("sync_time", new Date().getTime() / 1000).commit();
	}

	public static long getSyncTime(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(
				"sync_time", 0);
	}
}
