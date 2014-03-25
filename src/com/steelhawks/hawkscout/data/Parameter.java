package com.steelhawks.hawkscout.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.steelhawks.hawkscout.Globals;
import com.steelhawks.hawkscout.util.MultiSelectSpinner;

public class Parameter {
	
	public final static String NUMERIC = "Numeric";
	public final static String ALPHANUMERIC = "AlphaNumeric";
	public final static String FIXED_INPUT = "Fixed Input";
	public final static String OTHER = "Other";
	public final static String MULTIPLE_SELECTION = "Multiple Selection";
	public final static String BOOLEAN = "Boolean";
	
	public Parameter (String title, String type, String options) {
		title = title.trim();
		TITLE = title;
		TYPE = type;
		OPTS = options;
	}
	
	private String TITLE;
	public String getTitle() {
		return TITLE;
	}
	private String TYPE;
	public String getType() {
		return TYPE;
	}
	private String OPTS;
	public String getOpts() {
		return OPTS;
	}
	
	private View v;
//	public View getView() {
//		if (TYPE.contains(Parameter.FIXED_INPUT)) {
//			List<String> opts = new ArrayList<String>(Arrays.asList(OPTS.split("!:!")));
//			if (TYPE.contains(Parameter.OTHER)) opts.add("Other");
//			if (TYPE.contains(Parameter.MULTIPLE_SELECTION)) {
//				MultiSelectSpinner m = new MultiSelectSpinner(this);
//				m.setItems(opts);
//				m.setPrompt(TITLE);
//				v = m;
//			} else {
//				Spinner s = new Spinner(this);
//					s.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, opts));
//				s.setPrompt(TITLE);
//				v = s;
//			}
//		} else if (TYPE.contains(Parameter.NUMERIC)) {
//			EditText e = new EditText(this);
//				if (TYPE.equals(Parameter.NUMERIC)) e.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
//			e.setHint(TITLE);
//			v = e;
//		} else if (TYPE.equals(Parameter.BOOLEAN)) {
//			CheckedTextView cb =  new CheckedTextView(this);
//			cb.setText(TITLE);
//			cb.setTextSize(18);
//			cb.setTypeface(null, Typeface.BOLD);
//			v = cb;
//		}
//		return v;
//	}
	
	public Drawable getTitleDrawable(String title, Context c) {
		TextView v = new TextView(c);
		v.setText(title);
		v.setTextSize(18);
		v.setTypeface(null, Typeface.BOLD);
		v.setDrawingCacheEnabled(true);
		v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		v.buildDrawingCache(true);
		Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
		Drawable d = new BitmapDrawable(c.getResources(), b);
		v.setDrawingCacheEnabled(false);
		return d;
	}
}
