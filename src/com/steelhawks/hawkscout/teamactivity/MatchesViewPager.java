package com.steelhawks.hawkscout.teamactivity;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;

import com.steelhawks.hawkscout.data.Competition;
import com.steelhawks.hawkscout.vvp.VerticalViewPager;

public class MatchesViewPager extends VerticalViewPager {

	Context c;
	
	public MatchesViewPager(Context context) {
		super(context);
		c = context;
	}
	
	public MatchesViewPager(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	
	public void setArguments(Context c, Competition currentComp, String teamNumber, FragmentManager fm) {
		setAdapter(new MatchesPageAdapter(c, currentComp, teamNumber, fm));
	}

}
