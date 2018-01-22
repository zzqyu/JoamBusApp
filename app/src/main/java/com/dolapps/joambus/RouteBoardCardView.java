package com.dolapps.joambus;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RouteBoardCardView extends LinearLayout {
	private Context ctxt;
	private TextView routeNo, startPoint, midPoint, endPoint;
	private ImageView next1, next2;

	public RouteBoardCardView(Context context){
		super(context);
		ctxt = context;
		init();
	}
	public RouteBoardCardView(Context context, AttributeSet attrs){
		super(context, attrs);
		ctxt = context;
		init();
	}

	public RouteBoardCardView(Context context, RouteBoard mRouteBoard){
		super(context);
		ctxt = context;
		init();
		set(mRouteBoard);
	}


	protected void init(){
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(infService);
		removeAllViews();
		addView(inflater.inflate(R.layout.bus_route_board, null));


		routeNo = (TextView) findViewById(R.id.routeNo);
		routeNo.setTypeface(Typeface.createFromAsset(ctxt.getAssets(), "cgothic.ttf"));

		startPoint = (TextView) findViewById(R.id.startPoint);
		startPoint.setTypeface(Typeface.createFromAsset(ctxt.getAssets(), "HANYGO240.ttf"));

		midPoint = (TextView) findViewById(R.id.midPoint);
		midPoint.setTypeface(Typeface.createFromAsset(ctxt.getAssets(), "HANYGO240.ttf"));

		endPoint = (TextView) findViewById(R.id.endPoint);
		endPoint.setTypeface(Typeface.createFromAsset(ctxt.getAssets(), "HANYGO240.ttf"));

		next1 = (ImageView) findViewById(R.id.next1);
		next2 = (ImageView) findViewById(R.id.next2);

	}

	public void set(RouteBoard routeBoard){
		this.set(routeBoard.routeType, routeBoard.routeName, routeBoard.startPointName, routeBoard.midPointName, routeBoard.endPointName);
	}
	public void set(byte routeType, String routeName, String startPointName, String midPointName, String endPointName){
		int routeColor=0;
		Drawable iconNext = getResources().getDrawable(R.drawable.route_next_g);
		switch (routeType){
			case 'r'|'R': routeColor = 0xfff04d2f;
				iconNext = getResources().getDrawable(R.drawable.route_next_r); break;
			case 'b'|'B': routeColor = 0xff009ada;
				iconNext = getResources().getDrawable(R.drawable.route_next_b); break;
			case 'g'|'G': routeColor = 0xff00a080;
				iconNext = getResources().getDrawable(R.drawable.route_next_g); break;
			case 'y'|'Y': routeColor = 0xfffcb814;
				iconNext = getResources().getDrawable(R.drawable.route_next_y); break;
			case 'd'|'D': routeColor = 0xff333333;
				iconNext = getResources().getDrawable(R.drawable.route_next_d); break;
		}

		next1.setImageDrawable(iconNext);
		next2.setImageDrawable(iconNext);

		routeNo.setBackgroundColor(routeColor);
		if(!routeName.contains("  ")){
			routeNo.setText(routeName);
			routeNo.setGravity(Gravity.CENTER | Gravity.TOP);
			routeNo.setPadding(0, 15, 0, 0);
			if(routeNo.length()<=2){
				routeNo.setTextScaleX(0.90f);
				routeNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36);
			}
			else{
				routeNo.setTextScaleX(0.78f);
				routeNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 34);
			}
		}
		else{
			routeNo.setGravity(Gravity.CENTER | Gravity.TOP);
			routeNo.setText(routeName.replace("  ", "\n"));
			routeNo.setTextScaleX(0.8f);
			routeNo.setLineSpacing(0f, 0.7f);
			if(PublicMethods.screendpi(ctxt) < 480 || routeName.replace(" ", "").length() > 8)
				routeNo.setPadding(0, 0, 0, 0);
			else
				routeNo.setPadding(0, 10, 0, 0);

			if(routeName.replace(" ", "").length() > 8) {
				routeNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
			}
			else
				routeNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);

		}

		if(startPointName.replace(" ", "").length() > 3){
			startPoint.setText(startPointName.replace("  ", "\n"));
			startPoint.setTextScaleX(0.85f);
			startPoint.setLineSpacing(0f, 0.95f);
			startPoint.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);

		}
		else{
			startPoint.setText(startPointName);
			startPoint.setTextScaleX(0.88f);
			startPoint.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
		}

		if(midPointName.replace(" ", "").length() > 3){
			midPoint.setText(midPointName.replace("  ", "\n"));
			midPoint.setTextScaleX(0.85f);
			midPoint.setLineSpacing(0f, 0.95f);
			midPoint.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

		}
		else{
			midPoint.setText(midPointName);
			midPoint.setTextScaleX(0.88f);
			midPoint.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		}

		if(endPointName.replace(" ", "").length() > 3){
			endPoint.setText(endPointName.replace("  ", "\n"));
			endPoint.setTextScaleX(0.85f);
			endPoint.setLineSpacing(0f, 0.95f);
			endPoint.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);

		}
		else{
			endPoint.setText(endPointName);
			endPoint.setTextScaleX(0.88f);
			endPoint.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
		}

	}
}
