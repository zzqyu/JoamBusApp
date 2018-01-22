package com.dolapps.joambus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class AppSettingActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_setting);

		LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
		Toolbar bar = new Toolbar(this);
		bar.setTitle("앱 정보");
		bar.setBackgroundResource(R.color.primary_material);
		bar.setTitleTextColor(0xffffffff);
		root.addView(bar, 0); // insert at top



		Preference nowDbVersion = findPreference("db_version_now");
		String mSdPath, ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED)) {
			mSdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			mSdPath = Environment.MEDIA_UNMOUNTED;
		}
		SQLiteDatabase db = this.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "db_version.sqlite", Context.MODE_PRIVATE, null);
		Cursor cursor = db.rawQuery("SELECT * From db_version", null);
		cursor.moveToFirst();
		String db_ver = cursor.getString(cursor.getColumnIndex("version"));
		nowDbVersion.setSummary(db_ver);

		Preference newDbVersion = findPreference("db_version_new");
		newDbVersion.setSummary(PublicMethods.newAppVersion(this,
				"https://play.google.com/store/apps/details?id=com.dolapps.joambusdb"));

		Preference dbApp = findPreference("db_app");
		dbApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = getPackageManager().getLaunchIntentForPackage("com.dolapps.joambusdb");
				if(intent == null){
					intent = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("https://play.google.com/store/apps/details?id=com.dolapps.joambusdb"));
				}
				startActivity(intent);
				return false;
			}
		});

		String version = "";
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			version = "정보없음";
			e.printStackTrace();
		}
		Preference nowAppVersion = findPreference("app_version_now");
		nowAppVersion.setSummary(version);

		Preference newAppVersion = findPreference("app_version_new");
		newAppVersion.setSummary(PublicMethods.newAppVersion(this,
				"https://play.google.com/store/apps/details?id=com.dolapps.joambus"));

		Preference facebookPage = findPreference("facebook_page");
		facebookPage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("https://www.facebook.com/n/?joambusapp&ref=stale_email&medium=email&mid=92137fdG5af38ef8807eG0G147&bcode=1.1387739855.Abm_vOq7859GWFdY&n_m=wjdrb0626%40naver.com"));
				startActivity(intent);
				return false;
			}
		});
		Preference playstorePage = findPreference("playstore_page");
		playstorePage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("https://play.google.com/store/apps/details?id=com.dolapps.joambus"));
				startActivity(intent);
				return false;
			}
		});

	}
}