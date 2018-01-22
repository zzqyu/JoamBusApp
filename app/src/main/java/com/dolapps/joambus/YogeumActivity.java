package com.dolapps.joambus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;

public class YogeumActivity extends AppCompatActivity {
	private SQLiteDatabase siwoeDb, sinaeDb, routeDb;
	private ArrayList<SiwoeYogeum> siwoeYogeums = new ArrayList<>();
	private ArrayAdapter<CharSequence> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yogeum);

		loadDb();
		ArrayList<Character> routeType = loadRouteType();

		CardView siwoeCard = (CardView) findViewById(R.id.siwow_card);
		siwoeCard.setVisibility(View.GONE);
		if(routeType.indexOf('d')!=-1){
			siwoeCard.setVisibility(View.VISIBLE);
			TextView titleTv = (TextView) findViewById(R.id.siwoe_title);
			titleTv.setText("시외 버스요금");
			Spinner spinner = (Spinner) findViewById(R.id.spinner);

			adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			loadSiwoeYogeum();
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					TextView routesTv = (TextView) findViewById(R.id.number);
					TextView adult = (TextView) findViewById(R.id.adult);
					TextView student = (TextView) findViewById(R.id.student);
					TextView children = (TextView) findViewById(R.id.child);
					if (siwoeYogeums.get(position) == null) {
						routesTv.setText("노선번호");
						adult.setText("성인");
						student.setText("청소년");
						children.setText("어린이");
					} else {
						adult.setText(siwoeYogeums.get(position).adult + "");
						student.setText(siwoeYogeums.get(position).student + "");
						routesTv.setText(siwoeYogeums.get(position).routes);
						children.setText(siwoeYogeums.get(position).children + "");
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		}

		LinearLayout rootLayout = (LinearLayout) findViewById(R.id.rootLayout);

		if(routeType.indexOf('r')!=-1){
			SinaeCardView redCard = new SinaeCardView(this, loadSinaeYogeum('r'), "직행좌석 버스요금");
			rootLayout.addView(redCard);
		}
		if(routeType.indexOf('b')!=-1){
			SinaeCardView blueCard = new SinaeCardView(this, loadSinaeYogeum('b'), "일반좌석 버스요금");
			rootLayout.addView(blueCard);
		}
		if(routeType.indexOf('g')!=-1){
			SinaeCardView greenCard = new SinaeCardView(this, loadSinaeYogeum('g'), "시내 버스요금");
			rootLayout.addView(greenCard);
		}
		if(routeType.indexOf('y')!=-1){
			SinaeCardView yellowCard = new SinaeCardView(this, loadSinaeYogeum('y'), "마을 버스요금");
			rootLayout.addView(yellowCard);
		}

	}
	private void loadDb(){
		String ext = Environment.getExternalStorageState();
		String mSdPath;
		if (ext.equals(Environment.MEDIA_MOUNTED)) {
			mSdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			mSdPath = Environment.MEDIA_UNMOUNTED;
		}
		siwoeDb = this.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "siwoe_yogeum.sqlite", Context.MODE_PRIVATE, null);
		sinaeDb = this.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "sinae_yogeum.sqlite", Context.MODE_PRIVATE, null);
		routeDb = this.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "routeboard.sqlite", Context.MODE_PRIVATE, null);
	}
	private void loadSiwoeYogeum(){
		siwoeYogeums.add(null);
		adapter.add("목적지를 고르시오");
		Cursor cursor = siwoeDb.rawQuery("SELECT * From siwoe_yogeum", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			String destination =cursor.getString(cursor.getColumnIndex("destination"));
			String routes =cursor.getString(cursor.getColumnIndex("routes"));
			int adult =cursor.getInt(cursor.getColumnIndex("adult"));
			int student =cursor.getInt(cursor.getColumnIndex("student"));
			int children =cursor.getInt(cursor.getColumnIndex("children"));
			siwoeYogeums.add(new SiwoeYogeum(destination, routes, adult, student, children));
			adapter.add(destination);
			cursor.moveToNext();
		}
		cursor.close();
	}
	private ArrayList<Integer> loadSinaeYogeum(char type){
		ArrayList<Integer> yogeums = new ArrayList<>();
		Cursor cursor = sinaeDb.rawQuery("SELECT * From sinae_yogeum where busType = '" + type + "'", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			int yogeum =cursor.getInt(cursor.getColumnIndex("yogeum"));
			yogeums.add(yogeum);
			cursor.moveToNext();
		}
		cursor.close();
		return yogeums;
	}
	private ArrayList<Character> loadRouteType(){
		ArrayList<Character> result = new ArrayList<>();
		Cursor cursor = routeDb.rawQuery("SELECT * From routeboard", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			char type =cursor.getString(cursor.getColumnIndex("routeType")).charAt(0);
			result.add(type);
			cursor.moveToNext();
		}
		cursor.close();
		HashSet hs = new HashSet(result);
		result = new ArrayList<>(hs);
		return result;
	}
	private class SiwoeYogeum{
		String destination, routes;
		int adult, student, children;
		public SiwoeYogeum(String destination, String routes,
				int adult, int student, int children){
			this.destination = destination;
			this.routes = routes;
			this.adult = adult;
			this.student = student;
			this.children = children;
		}
	}

}
