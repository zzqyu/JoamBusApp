package com.dolapps.joambus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class RoutePageActivity extends AppCompatActivity {

	RoutePage routePage;
	Menu timeTableList;
	NavigationView navigationView;
	DrawerLayout drawerLayout;
	FloatingActionMenu fam_TimeTable;
	int holiday = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_page);

		RouteBoard routeBoard = (RouteBoard)getIntent().getParcelableExtra("routeBoard");
		routePage = new RoutePage(this, routeBoard);

		if(routePage.isHoliday()) holiday = 1;

		final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		final WebViewPager webViewPager = new WebViewPager(this, routePage.getRouteNames(), routePage.getStr_url());
		viewPager.setAdapter(webViewPager);

		final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		tabLayout.setupWithViewPager(viewPager);

		FloatingActionButton fab_Refresh= (FloatingActionButton) findViewById(R.id.fab_refresh);
		fab_Refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int no = tabLayout.getSelectedTabPosition();
				setTimeTable();
				viewPager.setAdapter(webViewPager);
				viewPager.setCurrentItem(no, false);
			}
		});

		CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navigationView = (NavigationView) findViewById(R.id.nav_view);
		timeTableList = navigationView.getMenu();

		fam_TimeTable =  (FloatingActionMenu) findViewById(R.id.fam_time);
		fam_TimeTable.setIconAnimationInterpolator(new AccelerateDecelerateInterpolator());
		if(routePage.getTimeTableCnt() * (holiday + 1) > 1){

			String[] labelStr = {routePage.getStartPointName(), routePage.getEndPointName()};
			String[] holidayStr = {"평일", "주말공휴일"};

			final FloatingActionButton[] fab_time =
					new FloatingActionButton[routePage.getTimeTableCnt() * (holiday + 1)];

			int i = 0;
			for(i = 0; i<fab_time.length; i++){
				fab_time[i] = new FloatingActionButton(this);
				fab_time[i].setButtonSize(FloatingActionButton.SIZE_MINI);
				fab_time[i].setLabelVisibility(View.VISIBLE);
				final int finalI = i;
				if(routePage.getTimeTableCnt() == 1 && routePage.isHoliday()) {
					fab_time[i].setLabelText(labelStr[0] + holidayStr[i] + "출발시간표");
					fab_time[i].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							setTimeTableList(fab_time[finalI].getLabelText(), routePage.getTimeTableLines(0, finalI ==0 ? false:true));
							drawerLayout.openDrawer(GravityCompat.START);
						}
					});
				}
				else if(routePage.getTimeTableCnt() == 2 && routePage.isHoliday()) {
					fab_time[i].setLabelText(labelStr[i % 2] + holidayStr[i < 2 ? 0 : 1] + "출발시간표");
					fab_time[i].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							setTimeTableList(fab_time[finalI].getLabelText(), routePage.getTimeTableLines(finalI % 2, finalI < 2 ? false : true));
							drawerLayout.openDrawer(GravityCompat.START);
						}
					});
				}
				else {
					fab_time[i].setLabelText(labelStr[i] + "출발시간표");
					fab_time[i].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							setTimeTableList(fab_time[finalI].getLabelText(), routePage.getTimeTableLines(finalI, false));
							drawerLayout.openDrawer(GravityCompat.START);
						}
					});
				}
				fam_TimeTable.addMenuButton(fab_time[i]);
			}
		}
		else{
			fam_TimeTable.setOnMenuButtonClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setTimeTableList(routePage.getStartPointName() + "출발시간표", routePage.getTimeTableLines(0, false));
					drawerLayout.openDrawer(GravityCompat.START);
				}
			});
		}

		setTimeTable();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if(fam_TimeTable.isOpened() && !drawerLayout.isDrawerOpen(GravityCompat.START))
					fam_TimeTable.close(true);
				else if(drawerLayout.isDrawerOpen(GravityCompat.START))
					drawerLayout.closeDrawers();
				else {
					RoutePageActivity.this.finish();
				}
				return true;
			default:
				return false;
		}

	}
	private void setTimeTable(){
		LinearLayout timeTableLayout = (LinearLayout) findViewById(R.id.time_table_layout);
		timeTableLayout.removeAllViews();
		boolean isHoliday = routePage.isHoliday()&& PublicMethods.Today_is_Holiday();
		timeTableLayout.addView(
				makeTimeTableList(routePage.getStartPointName() + "출발시간표", 0, isHoliday));
		if(routePage.getTimeTableCnt() == 2){
			timeTableLayout.addView(
					makeTimeTableList(routePage.getEndPointName() + "출발시간표", 1, isHoliday));
		}

	}
	private LinearLayout makeTimeTableList(String title, int tableNo, boolean isholiday){
		LinearLayout result = new LinearLayout(this);
		result.setOrientation(LinearLayout.VERTICAL);
		result.setLayoutParams(
				new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
		LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
		TextView[] txts = new TextView[4];
		String[] strTxts = new String[4];
		strTxts[0] = title;
		ArrayList<TimeAndInfo> timeAndInfos = routePage.getTimeTableLines(tableNo, isholiday);
		Date date = new Date(System.currentTimeMillis());
		Time time = new Time(date.getHours(), date.getMinutes(), date.getSeconds());

		if (timeAndInfos.size()> 1 && time.before(timeAndInfos.get(0).getTime())){
			strTxts[1] = "▽ 첫차";
			strTxts[2] = timeAndInfos.get(0).getStrContents();
			strTxts[3] = timeAndInfos.get(1).getStrContents();
		}
		else if(timeAndInfos.size()> 1
				&& time.after(timeAndInfos.get(timeAndInfos.size()-1).getTime())){
			strTxts[1] = timeAndInfos.get(timeAndInfos.size()-1).getStrContents();
			strTxts[2] = "운행종료";
			strTxts[3] = "";
		}
		else if(timeAndInfos.size() == 1){
			strTxts[1] = "";
			strTxts[2] = timeAndInfos.get(0).getStrContents();
			strTxts[3] = "";
		}
		else{
			for (int i = 0; i<timeAndInfos.size()-1; i++){//be 객체가 작을때 af 개체가 클때

				if(time.after(timeAndInfos.get(i).getTime())
						&& time.before(timeAndInfos.get(i+1).getTime())){
					strTxts[1] = timeAndInfos.get(i).getStrContents();
					if(i+1 == timeAndInfos.size()-1){
						strTxts[2] = timeAndInfos.get(i+1).getStrContents();
						strTxts[3] = "△ 막차";
					}else{
						strTxts[2] = timeAndInfos.get(i+1).getStrContents();
						strTxts[3] = timeAndInfos.get(i+2).getStrContents();
					}
					break;
				}
			}
		}

		for(int i=0; i< txts.length; i++){
			txts[i] = new TextView(this);
			txts[i].setLayoutParams(params);
			txts[i].setText(strTxts[i]);
			txts[i].setTextColor(getResources().getColor(android.R.color.white));
			result.addView(txts[i]);
		}
		txts[0].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		txts[0].setTypeface(null, Typeface.BOLD);
		txts[1].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		txts[2].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		txts[2].setTextColor(getResources().getColor(R.color.accent_material_light));
		txts[3].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

		return result;
	}
	private void setTimeTableList(String timetableTitle, ArrayList<TimeAndInfo> timetableLines){

		timeTableList.removeItem(0);
		SubMenu topChannelMenu = timeTableList.addSubMenu(timetableTitle);
		for(TimeAndInfo k: timetableLines)
			topChannelMenu.add(k.getStrContents());
	}
	private class WebViewPager extends PagerAdapter{
		String[] tabTitles, urls;
		LayoutInflater mInflater;
		Context context;
		WebView webView;
		public WebViewPager(Context context, String[] tabTitles, String[] urls){
			super();
			this.context = context;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.tabTitles = tabTitles;
			this.urls = urls;
		}
		public int getCount() {
			return tabTitles.length;
		}


		public Object instantiateItem(ViewGroup container, int position) {

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setLayoutParams(params);

			webView = new WebView(context);
			linearLayout.addView(webView);

			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion >= Build.VERSION_CODES.KITKAT){
				params.setMargins(0, (int)PublicMethods.dpToPx(context, -200f), 0,0);
			}

			webView.setLayoutParams(params);
			webView.setVerticalScrollBarEnabled(false);

			WebSettings set = webView.getSettings();
			set.setJavaScriptEnabled(true);
			set.setCacheMode(WebSettings.LOAD_NO_CACHE);
			for(int i = 0; i< getCount(); i++){
				webView.loadUrl(urls[position]);
				webView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String overrideUrl) {
						if (overrideUrl.startsWith("http://m.gbis.go.kr/search/StationArrivalTvItem.do?")) {
							Intent intent = new Intent(context, BusStopWebActivity.class);
							intent.putExtra("url", overrideUrl);
							startActivity(intent);
							return true;
						}
						return false;
					}
					});
			}

			((ViewPager) container).addView(linearLayout);


			return linearLayout;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return tabTitles[position];
		}

		// View를 삭제합니다.
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((LinearLayout) object);
		}

		// instantiateItem에서 생성한 객체를 이용할 것인지 여부를 반환합니다.
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((LinearLayout) object);
		}

	}
}
