package com.dolapps.joambus;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class RouteBoardListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<RouteBoard> mItems = new ArrayList<RouteBoard>();

	public RouteBoardListAdapter(Context context, ArrayList<RouteBoard> routeBoards) {
		mContext = context;
		mItems = routeBoards;
	}

	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		RouteBoardCardView itemView;
		if (convertView == null) {
			itemView = new RouteBoardCardView(mContext, mItems.get(position));
		} else {
			itemView = (RouteBoardCardView) convertView;
			itemView.set(mItems.get(position));
		}
		return itemView;
	}
}