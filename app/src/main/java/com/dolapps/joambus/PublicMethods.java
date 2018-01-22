package com.dolapps.joambus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;


import com.ibm.icu.util.ChineseCalendar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by JeongGyu on 2015-10-05.
 */
public class PublicMethods {
	static public float dpToPx(Context context, float dp) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}
	static public float screenWidthDp(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float dpWidth  = displayMetrics.widthPixels / displayMetrics.density;

		return dpWidth;
	}

	static public float screenHeightDp(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float dpHeightth  = displayMetrics.heightPixels / displayMetrics.density;

		return dpHeightth;
	}

	static public int screendpi(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

		return displayMetrics.densityDpi;
	}

	static public Boolean Today_is_Holiday(){
		int holiday1[][] = { { 1, 1 },/*설날대체*/{ 2, 10 }, { 3, 1 }, { 5, 5 }, { 6, 6 }, { 8, 15 },
				{ 10, 3 }, { 10, 9 }, { 12, 25 } };
		int holiday2[][] = { { 12, 30 }, { 1, 1 }, { 1, 2 }, { 4, 8 }, { 8, 14 },
				{ 8, 15 },{ 8, 16 } };
		Calendar cal = new GregorianCalendar();
		ChineseCalendar ccal = new ChineseCalendar();
		int yoil = cal.get(Calendar.DAY_OF_WEEK),
				mon = cal.get(Calendar.MONTH) + 1,
				day = cal.get(Calendar.DAY_OF_MONTH);

		int cmon = ccal.get(ChineseCalendar.MONTH) + 1,
				cday = ccal.get(ChineseCalendar.DAY_OF_MONTH);
		if(yoil==1||yoil==7) return true;
		for(int i=0; i<holiday1.length; i++){
			if(mon==holiday1[i][0]&&day==holiday1[i][1]) return true;
		}
		for(int i=0; i<holiday2.length; i++){
			if(cmon==holiday2[i][0]&&cday==holiday2[i][1]) return true;
		}

		return false;
	}
	static public String downloadHtml(String addr){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
		StringBuilder html = new StringBuilder();
		try {
			URL url = new URL(addr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					for (;;) {
						String line = br.readLine();
						if (line == null) break;
						html.append(line + '\n');
					}
					br.close();
				}
				conn.disconnect();
			}
		} catch (NetworkOnMainThreadException e) {
			return "!error";
		} catch (Exception e) {
			return "!error";
		}
		return html.toString();
	}
	static public Boolean network_check(Context ctxt) {
		ConnectivityManager manager = (ConnectivityManager) ctxt
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mobile.isConnected() || wifi.isConnected()) {
			// 어디 한군데라도 연결되어 있는 경우
			return true;
		} else {
			// 아무 네트워크에도 연결안되어 있는 경우
			return false;
		}
	}
	static public String newAppVersion(Context ctxt, String uri) {
		StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(pol);
		if (network_check(ctxt) == false)
			return "인터넷미연결";
		else {
			return mf_ver(downloadHtml(uri));
		}

	}
	static private String mf_ver(String html) {
		if (html.contains("!error"))
			return "!error";
		else if(html.contains("softwareVersion\">")){
			String str = html.replace("softwareVersion\">","###");
			str=str.split("###")[1];
			str=str.split("<")[0];
			str=str.replace(" ", "");
			return str;
		}
		else return "!error";
	}
	static public Boolean DB_Check_update(Context ctxt){
		String mSdPath;
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED)) {
			mSdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			mSdPath = Environment.MEDIA_UNMOUNTED;
		}
		SQLiteDatabase db;
		Cursor cursor = null;
		try{
			db = ctxt.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "db_version.sqlite", Context.MODE_PRIVATE, null);
			cursor = db.rawQuery("SELECT * From db_version", null);
		}
		catch (SQLiteException e){
			return true;
		}

		cursor.moveToFirst();
		String dbVer = cursor.getString(cursor.getColumnIndex("version"));
		dbVer= dbVer.replace(".", "#");
		if(dbVer.equals("")) return true;
		int intDbVer = Integer.parseInt(dbVer.split("#")[0])*100+
				Integer.parseInt(dbVer.split("#")[1])*10+
				Integer.parseInt(dbVer.split("#")[2]);

		Log.i("intDbVer", "" + intDbVer);

		String play_ver=newAppVersion(ctxt, "https://play.google.com/store/apps/details?id=com.dolapps.joambusdb");
		if (play_ver.contains("!error") || play_ver.contains("인터넷미연결")) {
			return false;
		}
		play_ver= play_ver.replace(".", "#");
		int int_play_ver = Integer.parseInt(play_ver.split("#")[0])*100+
				Integer.parseInt(play_ver.split("#")[1])*10+
				Integer.parseInt(play_ver.split("#")[2]);
		Log.i("int_play_ver", "" + int_play_ver);
		if(intDbVer<int_play_ver) return true;
		else return false;
	}
	static public boolean showHelpOnFirstLaunch(Context ctxt) {
		try {
			PackageInfo info = ctxt.getPackageManager().getPackageInfo(
					ctxt.getPackageName(), 0);
			int currentVersion = info.versionCode;
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(ctxt);
			int lastVersion = prefs.getInt(
					PreferencesActivity.KEY_HELP_VERSION_SHOWN, 0);
			if (currentVersion > lastVersion) {
				prefs.edit()
						.putInt(PreferencesActivity.KEY_HELP_VERSION_SHOWN,
								currentVersion).commit();

				return true;
			}
		} catch (PackageManager.NameNotFoundException e) {
			Log.w(ctxt.getClass().getSimpleName(), e);
		}
		return false;
	}
	static public Boolean Check_update(Context ctxt, String app_ver){
		app_ver= app_ver.replace(".", "#");
		int int_app_ver = Integer.parseInt(app_ver.split("#")[0])*100+
				Integer.parseInt(app_ver.split("#")[1])*10+
				Integer.parseInt(app_ver.split("#")[2]);

		String play_ver=newAppVersion(ctxt, "https://play.google.com/store/apps/details?id=com.dolapps.joambus");
		if (play_ver.contains("!error") || play_ver.contains("인터넷미연결")) {
			return false;
		}
		play_ver= play_ver.replace(".", "#");
		int int_play_ver = Integer.parseInt(play_ver.split("#")[0])*100+
				Integer.parseInt(play_ver.split("#")[1])*10+
				Integer.parseInt(play_ver.split("#")[2]);
		if(int_app_ver<int_play_ver) return true;
		else return false;
	}

	static public String New_App_feature(Context ctxt, String uri) {
		StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(pol);
		if (network_check(ctxt) == false)
			return "인터넷미연결";
		else {

			return mf_new_feature(downloadHtml(uri));
		}

	}
	static String mf_new_feature(String html) {
		if (html.contains("!error"))
			return "Error";
		else if(html.contains("새로운 기능")){
			String str = html.split("새로운 기능")[1];
			str=str.replace("<div class=\"show-more-end\"", "절취절취");
			str=str.split("절취절취")[0];
			str=str.replace("<div class=\"recent-change\">", "");
			str=str.replace("<div class=\"recent-change\">", "");
			str=str.replace("</h1> ", "");
			str=str.replace("</div>", "\n");
			return str;
		}
		else return "Error";
	}


}
