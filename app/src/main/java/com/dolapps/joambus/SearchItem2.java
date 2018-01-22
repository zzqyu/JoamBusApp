package com.dolapps.joambus;

import android.content.Context;
import android.content.Intent;

/**
 * Created by JeongGyu on 2015-10-21.
 */
public class SearchItem2 {
	private String busStopNo, busStopName, routeList;
	private boolean type = false;
	private Context context;

	// true     노선       item routeboard, routeNo
	// false    정류장     item busstopNo, name, routeList
	public SearchItem2(Context context, String busStopNo, String busStopName, String routeList) {
		this.busStopNo = busStopNo;
		this.busStopName = busStopName;
		this.routeList = routeList;
		this.context = context;
	}

	public boolean isType() {
		return type;
	}

	public String getBusStopNo() {
		return busStopNo;
	}

	public String getBusStopName() {
		return busStopName;
	}

	public String getRouteList() {
		return routeList;
	}

	public void onClick() {
		Intent intent = new Intent(context, BusStopWebActivity.class);
		intent.putExtra("busStopNo", busStopNo);
		intent.putExtra("busStopName", busStopName);
		context.startActivity(intent);
	}
}