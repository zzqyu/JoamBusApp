package com.dolapps.joambus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;

public class RoutePage {

	private byte routeType;
	private int timeTableCnt, routeCnt;
	private String[] str_url/**/, routeNames;
	private String startPointName, endPointName, timeDbFileName;
	private boolean holiday;

	private Context ctxt;
	private SQLiteDatabase db, urlDb;

	public RoutePage(Context context, RouteBoard routeBoard){
		this.ctxt = context;
		this.routeType = routeBoard.routeType;
		this.routeNames = routeBoard.routeName.replace("  ", " ").replace(" ", ",").split(",");
		this.startPointName = routeBoard.startPointName;
		this.endPointName = routeBoard.endPointName;
		this.routeCnt = routeNames.length;
		this.timeDbFileName = "timetable_no" + routeNames[0].replace("-", "_");
		loadDb();
		loadTimeTableCntAndHoliday();
		loadRouteUrl();
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
		db = ctxt.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + timeDbFileName +".sqlite", Context.MODE_PRIVATE, null);
		urlDb = ctxt.openOrCreateDatabase(mSdPath + "/android/data/com.dolapps.joambus/" + "route_url.sqlite", Context.MODE_PRIVATE, null);
	}

	private void loadTimeTableCntAndHoliday(){
		Cursor cursor = db.rawQuery("SELECT * From " + timeDbFileName, null);
		cursor.moveToFirst();
		int timeTableNo1 =cursor.getInt(cursor.getColumnIndex("timeTableNo"));
		int isHoliday1 =cursor.getInt(cursor.getColumnIndex("isHoliday"));
		cursor.moveToLast();
		int timeTableNo2 =cursor.getInt(cursor.getColumnIndex("timeTableNo"));
		int isHoliday2 =cursor.getInt(cursor.getColumnIndex("isHoliday"));
		cursor.close();

		if(timeTableNo1 == timeTableNo2)
			timeTableCnt = 1;
		else
			timeTableCnt = 2;

		if(isHoliday1 == isHoliday2)
			holiday = false;
		else
			holiday = true;
	}

	public ArrayList<TimeAndInfo> getTimeTableLines(int timeTableNo, boolean isholiday){
		ArrayList<TimeAndInfo> result = new ArrayList<>();
		int tableNo = 0, holiday = 0;
		if(timeTableCnt == 2) tableNo = timeTableNo;
		if(this.holiday && isholiday) holiday = 1;
		Cursor cursor = db.rawQuery(" select * from " + timeDbFileName + " where timeTableNo = '"
				+ tableNo + "' and isHoliday  = '" + holiday + "'", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String routeName = cursor.getString(cursor.getColumnIndex("routeName"));
			String destination = cursor.getString(cursor.getColumnIndex("destination"));
			result.add(new TimeAndInfo(time, routeName, destination));
			cursor.moveToNext();
		}
		cursor.close();
		return result;
	}

	private void loadRouteUrl(){
		str_url = new String[this.routeCnt];
		int i =0;
		int routeType = 0;

		switch (this.routeType){
			case 'r': routeType = 11; break;
			case 'g': routeType = 13; break;
			case 'd': routeType = 43; break;
			case 'b': routeType = 12; break;
			case 'y': routeType = 30; break;
		}
		if (routeType == 0){
			for(String k: this.routeNames){
				str_url[i] = "null";
				i++;
			}
			return;
		}

		for(String k: this.routeNames){
			Cursor cursor = urlDb.rawQuery(
					" select * from " + "route_url" + " where routeName = '" +
							k.replace("(a)", "").replace("(b)", "").replace("(c)", "") + "'", null);
			cursor.moveToFirst();
			String routeId = cursor.getString(cursor.getColumnIndex("routeId"));
			if(routeId.equals("null"))
				str_url[i] = "null";
			else{
				str_url[i] = "http://m.gbis.go.kr/search/getBusRouteStaionList.do?routeId=" + routeId +
						"&routeName=" + k + "&routeType=" + routeType + "&stationId=&osInfoType=M&mode=realTimeMap";
			}
			cursor.close();
			i++;
		}

	}

	public boolean isHoliday() {
		return holiday;
	}

	public int getRouteCnt() {
		return routeCnt;
	}

	public int getTimeTableCnt() {
		return timeTableCnt;
	}

	public String getEndPointName() {
		return endPointName;
	}

	public String getStartPointName() {
		return startPointName;
	}

	public String[] getRouteNames() {
		return routeNames;
	}

	public String[] getStr_url() {
		return str_url;
	}
}