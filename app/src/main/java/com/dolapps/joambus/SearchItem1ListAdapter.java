package com.dolapps.joambus;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchItem1ListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<SearchItem1> mItems = new ArrayList<>();

	public SearchItem1ListAdapter(Context context, ArrayList<SearchItem1> searchItem1s) {
		mContext = context;
		mItems = searchItem1s;
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
		txt1.setText(mItems.get(position).getRouteNo());
		TextView txt2 = (TextView) v.findViewById(R.id.txt2);
		txt2.setText(mItems.get(position).getRouteStr());

		return v;
	}
}