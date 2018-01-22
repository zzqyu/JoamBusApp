package com.dolapps.joambus;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchItem2ListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<SearchItem2> mItems = new ArrayList<>();

	public SearchItem2ListAdapter(Context context, ArrayList<SearchItem2> searchItem2s) {
		mContext = context;
		mItems = searchItem2s;
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

		View v = convertView;
		if (v == null) {
			v = View.inflate(mContext, R.layout.search_item_view, null);
		}
		TextView txt1 = (TextView) v.findViewById(R.id.txt1);
		txt1.setText(mItems.get(position).getBusStopName() + " - " +
				mItems.get(position).getBusStopNo());
		TextView txt2 = (TextView) v.findViewById(R.id.txt2);
		txt2.setText(mItems.get(position).getRouteList().replace("!", ", "));

		return v;
	}
}