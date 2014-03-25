package com.steelhawks.hawkscout.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.steelhawks.hawkscout.MatchScouting;
import com.steelhawks.hawkscout.R;

public class ReviewLayout extends RelativeLayout {

	private MatchScouting context;
	public int intValue = -1;
	private boolean booleanValue;
	private boolean containsInt;
	private TextView title;
	private String titleText;
	private EditText review;
	private Button increase;
	private Button decrease;
	private CheckedTextView toggle;
	
	public ReviewLayout(MatchScouting context, String titleText, int  value) {
		super(context);
		containsInt = true;
		this.context = (MatchScouting) context;
		this.titleText = titleText;
		this.intValue = value;

		super.setLayoutParams(
				new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utilities.PX(context, 48)));
		title = getTitle();
		review = getEditText();
		increase = getIncreaseButton();
		decrease = getDecreaseButton();
		
		super.addView(title);
		super.addView(review);
		super.addView(increase);
		super.addView(decrease);
	}
	
	public ReviewLayout(MatchScouting context, String titleText, boolean value) {
		super(context);
		containsInt = false;
		this.context = context;
		this.titleText = titleText;
		this.booleanValue = value;
		
		super.setLayoutParams(
				new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utilities.PX(context, 48)));
		
		toggle = getCheckedTextView();
		super.addView(toggle);
	}
	
	public String getValue() {
		if (intValue == -1) return String.valueOf(booleanValue);
		else return String.valueOf(intValue);
	}
	
	public String getFinalValue() {
		if (containsInt) {
			return review.getText().toString();
		}
		else return String.valueOf(toggle.isChecked());
	}
	
	private Button getIncreaseButton() {
		OnClickListener increase = new OnClickListener() {
			@Override
			public void onClick(View v) {
				intValue = Integer.parseInt(review.getEditableText().toString());
				review.setText("" + ++intValue);
			}
		};
		Button button = new Button(context, null, R.style.match_scouting_increment);
		button.setOnClickListener(increase);
		RelativeLayout.LayoutParams lP = 
				new RelativeLayout.LayoutParams(Utilities.PX(context, 48), LayoutParams.MATCH_PARENT);
			lP.addRule(RelativeLayout.CENTER_VERTICAL);
			lP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lP.rightMargin = Utilities.PX(context, 144);
		button.setLayoutParams(lP);
		button.setGravity(Gravity.CENTER);
		button.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		button.setTypeface(null, Typeface.BOLD);
		button.setBackgroundDrawable(getResources().getDrawable(R.drawable.crimson_background));
		button.setText("+");
		return button;
	}
	
	private Button getDecreaseButton() {
		OnClickListener decrease = new OnClickListener() {
			@Override
			public void onClick(View v) {
				intValue = Integer.parseInt(review.getEditableText().toString());
				if (intValue == 0) return;
				review.setText("" + --intValue);
			}
		};
		Button button = new Button(context, null, R.style.match_scouting_increment);
		button.setOnClickListener(decrease);
		RelativeLayout.LayoutParams lP = 
				new RelativeLayout.LayoutParams(Utilities.PX(context, 48), LayoutParams.MATCH_PARENT);
			lP.addRule(RelativeLayout.CENTER_VERTICAL);
			lP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		button.setLayoutParams(lP);
		button.setGravity(Gravity.CENTER);
		button.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		button.setTypeface(null, Typeface.BOLD);
		button.setBackgroundDrawable(getResources().getDrawable(R.drawable.crimson_background));
		button.setText("-");
		return button;
	}
	
	private EditText getEditText() {
		EditText view = new EditText(context);
		view.setText("" + intValue);
		view.addTextChangedListener(new TextChangedListener(view));
			RelativeLayout.LayoutParams lP = new RelativeLayout.LayoutParams(Utilities.PX(context, 96), LayoutParams.MATCH_PARENT);
				lP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lP.addRule(RelativeLayout.CENTER_VERTICAL);
				lP.rightMargin = Utilities.PX(context, 48);
		view.setLayoutParams(lP);
		view.setMinHeight(Utilities.PX(context, 40));
			int padding = Utilities.PX(context, 4);
		view.setPadding(padding, padding, padding, padding);
		view.setInputType(InputType.TYPE_CLASS_NUMBER);
		view.setGravity(Gravity.CENTER);
		return view;
	}
	
	private TextView getTitle() {
		TextView title = new TextView(context, null, R.style.match_scouting_review_title);
		title.setText(titleText);
		title.setTextAppearance(context, R.style.match_scouting_review_title);
			RelativeLayout.LayoutParams lp = 
					new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		title.setLayoutParams(lp);
		title.setTypeface(null, Typeface.BOLD);
		title.setMinHeight(Utilities.PX(context, 48));
		title.setPadding(Utilities.PX(context, 4), 0, 0, 0);
		title.setGravity(Gravity.CENTER_VERTICAL);
		return title;
	}
	
	private CheckedTextView getCheckedTextView() {
		final CheckedTextView toggle = new CheckedTextView(context);
		toggle.setText(titleText);
		toggle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		toggle.setCheckMarkDrawable(getResources().getDrawable(R.drawable.btn_check_holo_light));
		toggle.setClickable(true);
		toggle.setGravity(Gravity.CENTER_VERTICAL);
		toggle.setTypeface(null, Typeface.BOLD);
		toggle.setPadding(Utilities.PX(context, 4),0,0,0);
		toggle.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		toggle.setChecked(booleanValue);
		toggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				toggle.toggle();
				booleanValue = !booleanValue;
			}
			
		});
		return toggle;
	}
	
	class TextChangedListener implements TextWatcher {

		EditText view;
		int id;
		public TextChangedListener(EditText view) {
			this.view = view;
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().equals("") || s == null) return;
			if (!s.toString().contains(".")) intValue = Integer.valueOf(s.toString());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {}
		
	}

}
