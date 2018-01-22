package com.dolapps.joambus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

	private ListView mainListView;
	private ArrayList<RouteBoard> routeBoards;
	private SQLiteDatabase routeDb, busStopDb;

	private SearchView mSearchView;
	private FloatingActionMenu famMenu;
	private ScrollView search_ll;

	private int mLastScrollY, mPreviousFirstVisibleItem, mScrollThreshold;

	private Handler mHandler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		Intent intent = new Intent(this, CheckDb.class);
		startActivity(intent);

		if (PublicMethods.DB_Check_update(this) == false && PublicMethods.showHelpOnFirstLaunch(this)) {
			Intent intent1 = new Intent(this, HelpActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			// Show the default page on a clean install, and the what's new
			// page on an upgrade.
			startActivity(intent1);
		}

		String version = "";
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			version = "정보없음";
			e.printStackTrace();
		}

		if (PublicMethods.Check_update(this, version)) {
			final String url = "https://play.google.com/store/apps/details?id=com.dolapps.joambus";
			new AlertDialog.Builder(this)
					.setTitle("업데이트 알림")
					.setMessage(PublicMethods.New_App_feature(this, url))
					.setPositiveButton("확인",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
								                    int id) {
									// Action for '확인' Button
									Intent intent = new Intent(
											Intent.ACTION_VIEW, Uri.parse(url));
									startActivity(intent);
								}
							})
					.setNegativeButton("취소",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
								                    int id) {
									// Action for '취소' Button
									dialog.cancel();
								}
							}).show();
		}

		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		famMenu = (FloatingActionMenu) findViewById(R.id.fam_menu);
		FloatingActionButton fabSetting = (FloatingActionButton) findViewById(R.id.fab_seting);
		fabSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AppSettingActivity.class);
				startActivity(intent);
			}
		});
		FloatingActionButton fabHelp = (FloatingActionButton) findViewById(R.id.fab_help);
		fabHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, HelpActivity.class);
				intent.putExtra("back","no");
				startActivity(intent);
			}
		});
		FloatingActionButton fabYogeum = (FloatingActionButton) findViewById(R.id.fab_yogeum);
		fabYogeum.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, YogeumActivity.class);
				startActivity(intent);
			}
		});

		mHandler = new Handler();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadDb();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							setMainList();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 50);
			}
		});

	}


	public void setMainList() {
		routeBoards = new ArrayList<RouteBoard>();
		Cursor cursor = routeDb.rawQuery("SELECT * From routeboard", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			byte routeType = cursor.getString(cursor.getColumnIndex("routeType")).getBytes()[0];
			String routeName = cursor.getString(cursor.getColumnIndex("routeName"));
			String startPointName = cursor.getString(cursor.getColumnIndex("startPointName"));
			String midPointName = cursor.getString(cursor.getColumnIndex("midPointName"));
			String endPointName = cursor.getString(cursor.getColumnIndex("endPointName"));
			routeBoards.add(new RouteBoard(routeType, routeName, startPointName, midPointName, endPointName));

			cursor.moveToNext();
		}
		cursor.close();
		AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MainActivity.this, RoutePageActivity.class);
				intent.putExtra("routeBoard", MainActivity.this.routeBoards.get(position));
				startActivity(intent);
			}
		};
		AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalItemCount == 0) return;
				if (firstVisibleItem == mPreviousFirstVisibleItem) {
					int newScrollY = getTopItemScrollY();
					boolean isSignificantDelta = Math.abs(mLastScrollY - newScrollY) > mScrollThreshold;
					if (isSignificantDelta) {
						if (mLastScrollY > newScrollY) {
							famMenu.hideMenu(true);
						} else {
							famMenu.showMenu(true);
						}
					}
					mLastScrollY = newScrollY;
				} else {
					if (firstVisibleItem > mPreviousFirstVisibleItem) {
						famMenu.hideMenu(true);
					} else {
						famMenu.showMenu(true);
					}

					mLastScrollY = getTopItemScrollY();
					mPreviousFirstVisibleItem = firstVisibleItem;
				}

			}
		};

		mainListView = (ListView) findViewById(R.id.main_list);
		mainListView.setAdapter(new RouteBoardListAdapter(MainActivity.this, routeBoards));
		mainListView.setOnItemClickListener(itemClickListener);
		mainListView.setOnScrollListener(scrollListener);


	}

	private int getTopItemScrollY() {
		if (mainListView == null || mainListView.getChildAt(0) == null) return 0;
		View topChild = mainListView.getChildAt(0);
		return topChild.getTop();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (famMenu.isOpened())
					famMenu.close(true);
				else if (!mSearchView.isIconified()) {
					search_ll = (ScrollView) findViewById(R.id.search_ll);
					mSearchView.setIconified(true);
					search_ll.setVisibility(View.GONE);
				} else
					finish();
				return true;
			default:
				return false;
		}

	}


	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		if (listAdapter == null) {
			Log.i("listAdapter", "null");
			return;
		}
		if (listAdapter.getCount() > 0) {
			View listItem = listAdapter.getView(0, null, listView);
			listItem.measure(0, 0);
			int totalHeight = listItem.getMeasuredHeight() * listAdapter.getCount();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		} else {
			params.height = 0;
		}
		listView.setLayoutParams(params);
		listView.requestLayout();
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
		busStopDb = this.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "route_of_busstop_list.sqlite", Context.MODE_PRIVATE, null);
	}


	//===========================================================================================

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);

		mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		mSearchView.setQueryHint("노선번호 1자 이상, 정류장이름 2자 이상,정류장번호 5자 이상");
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				TextView txt1 = (TextView) findViewById(R.id.header1);
				TextView txt2 = (TextView) findViewById(R.id.header2);
				ListView list1 = (ListView) findViewById(R.id.searchlist1);
				ListView list2 = (ListView) findViewById(R.id.searchlist2);
				search_ll = (ScrollView) findViewById(R.id.search_ll);
				if (query.length() == 1) {
					txt1.setVisibility(View.VISIBLE);
					list1.setVisibility(View.VISIBLE);
					txt2.setVisibility(View.GONE);
					list2.setVisibility(View.GONE);
					search_ll.setVisibility(View.VISIBLE);
					search_list(query, query.length());
				} else if (query.length() >= 2) {
					txt1.setVisibility(View.VISIBLE);
					list1.setVisibility(View.VISIBLE);
					txt2.setVisibility(View.VISIBLE);
					list2.setVisibility(View.VISIBLE);
					search_ll.setVisibility(View.VISIBLE);
					search_list(query, query.length());

				} else {
					txt1.setVisibility(View.GONE);
					txt2.setVisibility(View.GONE);
					list1.setVisibility(View.GONE);
					list2.setVisibility(View.GONE);
					search_ll.setVisibility(View.GONE);
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				return true;
			}
		});

		return true;
	}

	void search_list(String keyword, int cnt) {
		final ArrayList<SearchItem1> rtSearchList = new ArrayList<>();
		final ArrayList<SearchItem2> stSearchList = new ArrayList<>();
		//노선목록
		AdapterView.OnItemClickListener searchitemclick1 = new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			                        long arg3) {
				rtSearchList.get(arg2).onClick();

			}
		};
		//정류장목록
		AdapterView.OnItemClickListener searchitemclick2 = new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			                        long arg3) {
				stSearchList.get(arg2).onClick();
			}
		};

		if (cnt >= 1) {
			for (SearchItem1 k : loadRouteByRouteNo(keyword)) {
				rtSearchList.add(k);
			}
		}
		if (cnt >= 2) {
			for (SearchItem2 k : loadBusStopByBusStopName(keyword)) {
				stSearchList.add(k);
			}
		}
		if (cnt >= 5) {
			if (loadBusStopByBusStopNo(keyword) != null)
				stSearchList.add(loadBusStopByBusStopNo(keyword));
		}
		ListView list1 = (ListView) findViewById(R.id.searchlist1);
		list1.setAdapter(new SearchItem1ListAdapter(this, rtSearchList));
		setListViewHeightBasedOnChildren(list1);
		list1.setOnItemClickListener(searchitemclick1);

		ListView list2 = (ListView) findViewById(R.id.searchlist2);
		list2.setAdapter(new SearchItem2ListAdapter(this, stSearchList));
		setListViewHeightBasedOnChildren(list2);
		list2.setOnItemClickListener(searchitemclick2);

	}


	private ArrayList<SearchItem1> loadRouteByRouteNo(String route) {
		ArrayList<SearchItem1> result = new ArrayList<>();
		Cursor cursor = routeDb.rawQuery(
				"select * from " + "routeboard"
						+ " where routeName like '%" + route + "%' ", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				byte routeType = cursor.getString(cursor.getColumnIndex("routeType")).getBytes()[0];
				String routeName = cursor.getString(cursor.getColumnIndex("routeName"));
				String startPointName = cursor.getString(cursor.getColumnIndex("startPointName"));
				String midPointName = cursor.getString(cursor.getColumnIndex("midPointName"));
				String endPointName = cursor.getString(cursor.getColumnIndex("endPointName"));
				RouteBoard routeBoard = new RouteBoard(routeType, routeName, startPointName, midPointName, endPointName);
				String[] routes = routeName.replace("  ", " ").split(" ");
				for (String k : routes) {
					if (k.contains(route))
						result.add(new SearchItem1(this, routeBoard, k));
				}
				cursor.moveToNext();
			}
		}
		cursor.close();
		return result;
	}

	private ArrayList<SearchItem2> loadBusStopByBusStopName(String busStopName) {
		ArrayList<SearchItem2> result = new ArrayList<>();
		Cursor cursor = busStopDb.rawQuery(
				"select * from " + "route_of_busstop_list"
						+ " where busStopName like '%" + busStopName + "%' ", null);
		Log.i("검색결과", "" + cursor.getCount());
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String no = cursor.getString(cursor.getColumnIndex("busStopNo"));
				String name = cursor.getString(cursor.getColumnIndex("busStopName"));
				String routeList = cursor.getString(cursor.getColumnIndex("routeList"));
				result.add(new SearchItem2(this, no, name, routeList));
				cursor.moveToNext();
			}

		}
		cursor.close();
		return result;
	}

	private SearchItem2 loadBusStopByBusStopNo(String busStopNo) {
		SearchItem2 result = null;
		if (busStopNo.charAt(0) == '0')
			busStopNo = busStopNo.substring(1, busStopNo.length());

		Cursor cursor = busStopDb.rawQuery(
				" select * from " + "route_of_busstop_list" + " where busStopNo = '" +
						busStopNo + "'", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String no = cursor.getString(cursor.getColumnIndex("busStopNo"));
			String name = cursor.getString(cursor.getColumnIndex("busStopName"));
			String routeList = cursor.getString(cursor.getColumnIndex("routeList"));
			result = new SearchItem2(this, no, name, routeList);
		}
		cursor.close();
		return result;
	}


}

