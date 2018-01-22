package com.dolapps.joambus;

import java.sql.Time;

/**
 * Created by JeongGyu on 2015-10-14.
 */
public class TimeAndInfo {
	private Time time;
	private String routeName, destination, strTime;

	public TimeAndInfo(String time, String routeName, String destination){
		strTime = time;
		this.time = new Time(Integer.parseInt(time.split(":")[0]), Integer.parseInt(time.split(":")[1]), 0);
		this.routeName = routeName;
		this.destination = destination;
	}

	public Time getTime() {
		return time;
	}

	public String getDestination() {
		return destination;
	}

	public String getRouteName() {
		return routeName;
	}

	public String getStrTime() {
		return strTime;
	}
	public String getStrContents(){
		return strTime + "\t" + (routeName.equals("-")?"":routeName) + "\t" + (destination.equals("-")?"":destination);
	}
}
