package com.steelhawks.hawkscout.teamactivity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.steelhawks.hawkscout.data.Competition;

public class MatchesPageAdapter extends FragmentPagerAdapter {

	List<String> matches;
	List<MatchDataFragment> frags = new ArrayList<MatchDataFragment>();
	
	public MatchesPageAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public MatchesPageAdapter(Context c, Competition currentComp, String teamNumber, FragmentManager fm) {
		super(fm);
		matches = currentComp.getMatchesByTeam(teamNumber);
		for (int x=0; x<matches.size(); x++) {
			String[] data = currentComp.getMatchScoutingDataForTeam(matches.get(x), teamNumber);
			
			Bundle dataBundle = new Bundle();
			dataBundle.putStringArray(MatchDataFragment.MATCH_INFO_ID, currentComp.getMatchInfoByNumber(matches.get(x)));
			dataBundle.putString(MatchDataFragment.MATCH_NUMBER_ID, matches.get(x));
			dataBundle.putString(MatchDataFragment.TEAM_NUMBER_ID, teamNumber);
			dataBundle.putStringArray(MatchDataFragment.DATA_ID, data);
			
			MatchDataFragment frag = new MatchDataFragment();
			frag.setComp(currentComp).setArguments(dataBundle);
			frags.add(frag);
		}
	}

	@Override
	public int getCount() {
		return matches.size();
	}

	@Override
	public Fragment getItem(int arg0) {
		return frags.get(arg0);
	}

}
