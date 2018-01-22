package com.dolapps.joambus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BusStopWebActivity extends AppCompatActivity {
	String busStopName, busStopNo, url;
	CoordinatorLayout coordinatorLayout;
	WebView webView;
	private SQLiteDatabase routeDb, urlDb;
	FloatingActionButton fabRefresh;
	ArrayList<String> urlList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.busStopNo = getIntent().getStringExtra("busStopNo");
		this.busStopName = getIntent().getStringExtra("busStopName");
		this.url = getIntent().getStringExtra("url");

		setContentView(R.layout.activity_busstop_web);
		coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
		webView = (WebView) findViewById(R.id.webview);
		fabRefresh = (FloatingActionButton) findViewById(R.id.fab_refresh);
		loadDb();
		loadPage();
		fabRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				webView.reload();
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if(urlList.size()>1){
					urlList.remove(urlList.size()-1);
					webView.loadUrl(urlList.get(urlList.size()-1));
				}
				else if (urlList.size() == 1)
					finish();
				return true;
			default:
				return false;
		}

	}

	public String makeLink(){
		String html;
		String bsNO = busStopNo;
		if(busStopNo.length() != 9) {
			return "http://m.gbis.go.kr/common/getBusInfoList.do?searchData=" + busStopNo + "&osInfoType=M";
		}
		else {
			html = busStopNo;
			bsNO = "00000";
		}
		return "http://m.gbis.go.kr/search/StationArrivalViaList.do?stationId="
				+ html + "&stationName=" + busStopName + "&mobileNo=" + bsNO;
	}

	public void loadPage(){
		View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadPage();
			}
		};
		if(!PublicMethods.network_check(this)) {
			Snackbar.make(coordinatorLayout, "모바일인터넷이 꺼져있습니다.\n데이터를 켠 후, 새로고침을 눌러주세요.", Snackbar.LENGTH_INDEFINITE)
					.setAction("새로고침",clickListener).show();
		}
		else {
			String url = (this.url == null) ? makeLink() : this.url;
			urlList.add(url);

			if (url.equals("null")) {
				Snackbar.make(coordinatorLayout, "GBIS 서버에 문제가 발생했습니다.\n새로고침을 눌러주세요.", Snackbar.LENGTH_INDEFINITE)
						.setAction("새로고침", clickListener).show();
			} else {
				webView.loadUrl(url);
				webView.setVerticalScrollBarEnabled(false);
				webView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						Log.i("urlurl", url);
						if (url.startsWith("http://m.gbis.go.kr/search/StationArrivalViaList.do?")) {
							urlList.add(url);
							webView.loadUrl(url);
							return true;
						} else if (url.startsWith("http://m.gbis.go.kr/search/getBusRouteDetail.do?")
								|| url.startsWith("http://m.gbis.go.kr/search/StationArrivalTvItem.do?")) {
							String routeId = url.replace("routeId=", "CUT!")
									.replace("&routeName", "CUT!").replace("&stationId", "CUT!").split("CUT!")[1];
							RouteBoard route = routeBoard(routeId);
							if (route != null) {
								Intent intent = new Intent(BusStopWebActivity.this, RoutePageActivity.class);
								intent.putExtra("routeBoard", route);
								startActivity(intent);
								return true;
							} else {
								urlList.add(url);
								webView.loadUrl(url);
								Snackbar.make(coordinatorLayout, "조암버스 앱에 없는 노선입니다.", Snackbar.LENGTH_LONG).show();
							}
						}
						return false;
					}
				});
				WebSettings set = webView.getSettings();
				set.setJavaScriptEnabled(true);
				//set.setCacheMode(WebSettings.LOAD_NO_CACHE);
			}
		}
	}
	public RouteBoard routeBoard(String routeId){
		RouteBoard result = null;
		String route;
		Cursor cursor = urlDb.rawQuery(
				"select * from " + "route_url"
						+ " where routeId = '" + routeId + "' ", null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			route = cursor.getString(cursor.getColumnIndex("routeName"));
			cursor = routeDb.rawQuery(
					"select * from " + "routeboard"
							+ " where routeName like '%" + route + "%' ", null);
		}
		else {
			cursor.close();
			return result;
		}

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				String routeName = cursor.getString(cursor.getColumnIndex("routeName"));
				List<String> wordList = Arrays.asList(
						routeName.replace("  ", " ").split(" "));
				if(wordList.indexOf(route) != -1) {
					byte routeType = cursor.getString(cursor.getColumnIndex("routeType")).getBytes()[0];
					String startPointName = cursor.getString(cursor.getColumnIndex("startPointName"));
					String midPointName = cursor.getString(cursor.getColumnIndex("midPointName"));
					String endPointName = cursor.getString(cursor.getColumnIndex("endPointName"));
					result = new RouteBoard(routeType, routeName, startPointName, midPointName, endPointName);
					break;
				}
				else{
					cursor.moveToNext();
				}
			}
			cursor.close();
			return result;
		}
		else {
			cursor.close();
			return result;
		}

	}
	public void loadDb() {
		String ext = Environment.getExternalStorageState();
		String mSdPath;
		if (ext.equals(Environment.MEDIA_MOUNTED)) {
			mSdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			mSdPath = Environment.MEDIA_UNMOUNTED;
		}
		routeDb = this.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "routeboard.sqlite", Context.MODE_PRIVATE, null);
		urlDb = this.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "route_url.sqlite", Context.MODE_PRIVATE, null);
	}


}
