package com.king.clustermarker.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
 *
 */
public class PreferenceUtils {

	public static final String CONFIG_FILE_NAME = "config";


	public static final String VERSION_CODE = "version_code"; // app VersionCode

	public static String getPreference(String keyName, Context context) {
		return context.getSharedPreferences(CONFIG_FILE_NAME,
				Context.MODE_PRIVATE).getString(keyName, null);
	}

	public static void saveStringPreference(String keyName, String value,
			Context context) {
		Editor editor = context.getSharedPreferences(CONFIG_FILE_NAME,
				Context.MODE_PRIVATE).edit();
		editor.putString(keyName, value);
		editor.commit();
	}

	public static String getStringPreference(String keyName, String value,
			Context context) {
		return context.getSharedPreferences(CONFIG_FILE_NAME,
				Context.MODE_PRIVATE).getString(keyName, value);
	}

	public static int getIntPreference(String keyName, int defautValue,
			Context context) {
		return context.getSharedPreferences(CONFIG_FILE_NAME,
				Context.MODE_PRIVATE).getInt(keyName, defautValue);
	}

	public static void saveIntPreference(String keyName, int value,
			Context context) {
		Editor editor = context.getSharedPreferences(CONFIG_FILE_NAME,
				Context.MODE_PRIVATE).edit();
		editor.putInt(keyName, value);
		editor.commit();
	}

	public static void removePreference(String keyName, Context context) {
		Editor editor = context.getSharedPreferences(CONFIG_FILE_NAME,
				Context.MODE_PRIVATE).edit();
		editor.remove(keyName);
		editor.commit();
	}

}
