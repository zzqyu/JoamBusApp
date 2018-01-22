package com.dolapps.joambus;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

/**
 * Created by JeongGyu on 2015-11-03.
 */
public class CheckDb extends AppCompatActivity implements View.OnClickListener {

	private SQLiteDatabase db;

	String[] FilesNames = { "db_version", "route_of_busstop_list", "route_url", "routeboard", "sinae_yogeum", "siwoe_yogeum"
			, "timetable_no8155", "timetable_no9802", "timetable_no50_1", "timetable_no33_1", "timetable_no1"
			, "timetable_no2", "timetable_no2_2", "timetable_no2_4", "timetable_no3", "timetable_no4"
			, "timetable_no5_2", "timetable_no6", "timetable_no8", "timetable_no8_3", "timetable_no9"
			, "timetable_no10", "timetable_no11", "timetable_no11_5", "timetable_no14", "timetable_no15"
			, "timetable_no16", "timetable_no17", "timetable_no18", "timetable_no21", "timetable_no23"
			, "timetable_no24", "timetable_no25", "timetable_no26", "timetable_no27"};
	String mSdPath;
	String ext = Environment.getExternalStorageState();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_db);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("DB 체크");
		setSupportActionBar(toolbar);


		if (ext.equals(Environment.MEDIA_MOUNTED)) {
			mSdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			mSdPath = Environment.MEDIA_UNMOUNTED;
		}
		Button bt = (Button) findViewById(R.id.bt);
		TextView txt_check = (TextView) findViewById(R.id.check_files);
		if (state_files() != 2) {
			txt_check.setText("파일 일부가 없거나 파일 폴더가 없습니다.");
			bt.setVisibility(View.VISIBLE);
			bt.setOnClickListener(this);
		} else {
			if (PublicMethods.DB_Check_update(this)) {

				if(PublicMethods.showHelpOnFirstLaunch(this)){
					Intent intent = new Intent(this, HelpActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					// Show the default page on a clean install, and the what's new
					// page on an upgrade.
					startActivity(intent);
				}

				db = this.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "db_version.sqlite", Context.MODE_PRIVATE, null);
				Cursor cursor = db.rawQuery("SELECT * From db_version", null);
				cursor.moveToFirst();
				String dbVer = cursor.getString(cursor.getColumnIndex("version"));
				txt_check.setText("현재 DB 버전: " + dbVer +
						"\n최신 DB 버전: "
						+ PublicMethods.newAppVersion(this,
						"https://play.google.com/store/apps/details?id=com.dolapps.joambusdb").replace(" ", ""));
				Button bt_next = (Button) findViewById(R.id.bt_next);
				bt.setVisibility(View.VISIBLE);
				bt_next.setVisibility(View.VISIBLE);
				bt.setOnClickListener(this);
				bt_next.setOnClickListener(this);
			}
			else {
				To_main();
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bt:
				Intent Intent1 = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("https://play.google.com/store/apps/details?id=com.dolapps.joambusdb"));
				startActivity(Intent1);
				break;
			case R.id.bt_next:
				To_main();
				break;
		}
	}

	void To_main() {
		finish();
	}

	Boolean check_files(String cmpFileName) {

		for (int i = 0; i < FilesNames.length; i++)
			if (cmpFileName.equals(FilesNames[i]+".sqlite"))
				return true;
		return false;
	}

	int state_files() {
		int cnt_files = 0;

		String[] listFiles = new File(mSdPath + "/Android/data/com.dolapps.joambus").list();
		if (listFiles == null)
			return 1;// 폴더없음
		else {
			for (int i = 0; i < listFiles.length; i++)
				if (check_files(listFiles[i]))
					cnt_files++;

			if (cnt_files >= FilesNames.length)
				return 2;// 모든파일 존재
			else
				return 3; // 파일모자름
		}
	}


	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // 백 버튼
			moveTaskToBack(true);
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return true;
	}

	// TODO Auto-generated method stub
}
