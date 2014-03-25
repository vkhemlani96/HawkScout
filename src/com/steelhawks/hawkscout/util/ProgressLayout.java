package com.steelhawks.hawkscout.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ProgressLayout extends LinearLayout {

	public ProgressLayout(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		ProgressBar pB = new ProgressBar(context, null, android.R.attr.progressBarStyle);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = Gravity.CENTER;
			pB.setLayoutParams(layoutParams);
		addView(pB);
	}

}
