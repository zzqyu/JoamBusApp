package com.dolapps.joambus;

import android.content.Context;
import android.content.Intent;

/**
 * Created by JeongGyu on 2015-10-21.
 */
public class SearchItem1 {
	private RouteBoard routeBoard;
	private String routeNo;
	private boolean type = true;
	private Context context;
	// true     노선       item routeboard, routeNo
	// false    정류장     item busstopNo, name, routeList
	public SearchItem1(Context context, RouteBoard routeBoard, String routeNo){
		this.routeBoard = routeBoard;
		this.routeNo = routeNo;
		this.context = context;
	}

	public boolean isType() {
		return type;
	}

	public String getRouteNo() {
		return routeNo;
	}
	public String getRouteStr(){
		return routeBoard.startPointName.replace("  ", "")
				+ "<=>" + routeBoard.endPointName.split("  ")[0];
	}
	public void onClick(){
		Intent intent = new Intent(context, RoutePageActivity.class);
		intent.putExtra("routeBoard", routeBoard);
		context.startActivity(intent);
	}
}
