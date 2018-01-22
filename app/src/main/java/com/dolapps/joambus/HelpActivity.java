package com.dolapps.joambus;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

	int[] help_pic = {0, R.drawable.h1, R.drawable.h2, R.drawable.h3, R.drawable.h4};
	String back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		back = getIntent().getStringExtra("back");
		LinearLayout view = new LinearLayout(this);
		setContentView(view);

		ViewPager pager = new ViewPager(this);
		pager.setAdapter(new myPagerAdapter(this));
		view.addView(pager);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if(back != null){
					finish();
				}
				return true;
			default:
				return false;
		}

	}
	private View.OnClickListener closeListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			finish();
		}
	};

	private class myPagerAdapter extends PagerAdapter {

		private LayoutInflater mInflater;
		private ImageView indic;

		public myPagerAdapter(Context context) {
			super();
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return help_pic.length;
		}

		public Object instantiateItem(View pager, int position) {
			View v = null;

			if (position == 0) {
				v = mInflater.inflate(R.layout.helpmain, null);

			} else if (position >= 1 && position < getCount() - 1) {
				v = mInflater.inflate(R.layout.help, null);
				indic = (ImageView) v.findViewById(R.id.picture);
				indic.setImageResource(help_pic[position]);

			} else if (position == getCount() - 1) {
				v = mInflater.inflate(R.layout.help, null);
				indic = (ImageView) v.findViewById(R.id.picture);
				indic.setImageResource(help_pic[position]);
				Button close_bt = (Button) v.findViewById(R.id.done_button);
				close_bt.setVisibility(View.VISIBLE);
				close_bt.setOnClickListener(closeListener);
			}
			TextView Result1 = (TextView) v.findViewById(R.id.indicator);
			Result1.setText(position + 1 + "/" + getCount());

			((ViewPager) pager).addView(v, null);

			return v;
		}

		// View를 삭제합니다.
		public void destroyItem(View pager, int position, Object view) {
			((ViewPager) pager).removeView((View) view);
		}

		// instantiateItem에서 생성한 객체를 이용할 것인지 여부를 반환합니다.
		public boolean isViewFromObject(View v, Object obj) {
			return v == obj;
		}

	}
}
