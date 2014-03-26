package com.steelhawks.hawkscout.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class GraphView extends View {

	private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
	private float[] value_degree;
	private int[] COLORS={Color.parseColor("#cc0000"),Color.LTGRAY,Color.GRAY,Color.CYAN,Color.RED};
	RectF rectf = new RectF(10, 10, 200, 200);
	int temp=0;

	public GraphView(Context context, float[] values) {
		super(context);
		values = calculateData(values);
		value_degree=new float[values.length];
		for(int i=0;i<values.length;i++) {
			value_degree[i]=values[i];
		}
	}
	
	public GraphView(Context context) {
		super(context);
		float[] values = {1};
		values = calculateData(values);
		this.rectf = new RectF(30, 30, 180, 180);
		COLORS[0] = Color.parseColor("#eeeeee");
		value_degree=new float[values.length];
		for(int i=0;i<values.length;i++) {
			value_degree[i]=values[i];
		}
	}

	public GraphView(Context context, float[] values, RectF rectF) {
		super(context);
		values = calculateData(values);
		value_degree=new float[values.length];
		for(int i=0;i<values.length;i++) {
			value_degree[i]=values[i];
		}
		this.rectf = rectF;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		for (int i = 0; i < value_degree.length; i++) {//values2.length; i++) {
			if (i == 0) {
				paint.setColor(COLORS[i]);
				canvas.drawArc(rectf, 0, value_degree[i], true, paint);
			} 
			else {
				temp += (int) value_degree[i - 1];
				paint.setColor(COLORS[i]);
				canvas.drawArc(rectf, temp, value_degree[i], true, paint);
			}
		}
	}


	private float[] calculateData(float[] data) {
		// TODO Auto-generated method stub
		float total=0;
		for(int i=0;i<data.length;i++) {
			total+=data[i];
		}
		for(int i=0;i<data.length;i++) {
			data[i]=360*(data[i]/total);            
		}
		return data;

	}
}