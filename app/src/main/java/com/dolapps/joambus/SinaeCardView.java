package com.dolapps.joambus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SinaeCardView extends LinearLayout {
	private Context ctxt;
	private TextView title;
	private TextView[] yogeum = new TextView[6];
	private TableLayout tableLayout;

	public SinaeCardView(Context context){
		super(context);
		ctxt = context;
		init();
	}
	public SinaeCardView(Context context, AttributeSet attrs){
		super(context, attrs);
		ctxt = context;
		init();
	}

	public SinaeCardView(Context context, ArrayList<Integer> yogeums, String title){
		super(context);
		ctxt = context;
		init();
		set(yogeums, title);
	}


	protected void init(){
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(infService);
		removeAllViews();
		addView(inflater.inflate(R.layout.sinae_yogeum_view, null));
		title = (TextView) findViewById(R.id.title);
		yogeum[0] = (TextView) findViewById(R.id.cash1);
		yogeum[1] = (TextView) findViewById(R.id.cash2);
		yogeum[2] = (TextView) findViewById(R.id.cash3);
		yogeum[3] = (TextView) findViewById(R.id.card1);
		yogeum[4] = (TextView) findViewById(R.id.card2);
		yogeum[5] = (TextView) findViewById(R.id.card3);
		tableLayout = (TableLayout) findViewById(R.id.table);
		tableLayout.setLayoutParams(new android.support.v7.widget.CardView.LayoutParams(
				(int)PublicMethods.dpToPx(ctxt, PublicMethods.screenWidthDp(ctxt)), ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	public void set(ArrayList<Integer> yogeums, String title){
		this.title.setText(title);
		for (int i = 0; i<6; i++)
			yogeum[i].setText(yogeums.get(i) + "");
		if(yogeums.size()==9){
			TableRow tableRow = new TableRow(ctxt);
			tableLayout.addView(tableRow);
			TableRow.LayoutParams params = new TableRow.LayoutParams((int)PublicMethods.dpToPx(ctxt, 75f), ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f);
			TextView rowTitleTv = new TextView(ctxt);
			rowTitleTv.setLayoutParams(params);
			rowTitleTv.setTextColor(0xff333333);
			rowTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			rowTitleTv.setText("조조");
			tableRow.addView(rowTitleTv);

			TextView[] textViews = new TextView[3];
			params = new TableRow.LayoutParams((int)PublicMethods.dpToPx(ctxt, 85f), ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
			for (int i = 0; i<3; i++){
				textViews[i] = new TextView(ctxt);
				textViews[i].setLayoutParams(params);
				textViews[i].setTextColor(0xff444444);
				textViews[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
				textViews[i].setText(yogeums.get(6 + i) + "");
				tableRow.addView(textViews[i]);
			}
		}

	}

}
