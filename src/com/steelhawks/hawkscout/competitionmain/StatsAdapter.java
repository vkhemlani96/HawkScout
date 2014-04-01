package com.steelhawks.hawkscout.competitionmain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.steelhawks.hawkscout.CompetitionMain.SortBy;
import com.steelhawks.hawkscout.R;
import com.steelhawks.hawkscout.data.Indices.StatsIndex;
import com.steelhawks.hawkscout.util.Utilities;

public class StatsAdapter extends ArrayAdapter<float[]> {

	private Activity activity;
	private List<float[]> data;
	public int sort;
	private boolean reverse;

	float foulsPerMatchMax;
	float foulsPerMatchMin;
	float blocksPerMatchMax;
	float blocksPerMatchMin;
	float pointsPerPossessionMax;
	float pointsPerPossessionMin;
	float possessionsPerMatchMax;
	float possessionsPerMatchMin;
	float pointsPerMatchMax;
	float pointsPerMatchMin;

	private List<Float> dupsList = new ArrayList<Float>();
	private SparseIntArray dups = new SparseIntArray();

	Comparator<float[]> pointsPerMatch  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.POINTS_PER_MATCH];
			Float number2 = two[StatsIndex.POINTS_PER_MATCH];
			if (number1 == number2) {
				number1 = one[StatsIndex.TEAM_NUMBER];
				number2 = two[StatsIndex.TEAM_NUMBER];
				return -1*(number1.compareTo(number2));
			}
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> foulsPerMatch  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.FOULS_PER_MATCH];
			Float number2 = two[StatsIndex.FOULS_PER_MATCH];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> blocksPerMatch  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.BLOCKS_PER_MATCH];
			Float number2 = two[StatsIndex.BLOCKS_PER_MATCH];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> pointsPerPossession  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.POINTS_PER_POSSESSION];
			Float number2 = two[StatsIndex.POINTS_PER_POSSESSION];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> possessionsPerMatch  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.POSSESSIONS_PER_MATCH];
			Float number2 = two[StatsIndex.POSSESSIONS_PER_MATCH];
			return number1.compareTo(number2);
		}
	};
	
	public StatsAdapter(Activity a, List<float[]> teams, int sort) {
		super(a, R.layout.competitions_rankings_row_layout, teams);
		this.activity = a;
		this.data = teams;
		this.sort = sort;
		dupsList.clear();
		dups.clear();
		switch(sort) {
		case SortBy.PPM:Collections.sort(this.getData(), pointsPerMatch);
		Collections.reverse(this.getData());
		for (int x = 0; x<this.getData().size(); x++) {
			dupsList.add(this.getData().get(x)[StatsIndex.POINTS_PER_MATCH]);
		}
		break;
		case SortBy.FPM:Collections.sort(this.getData(), foulsPerMatch);
		Collections.reverse(this.getData());
		for (int x = 0; x<this.getData().size(); x++) {
			dupsList.add(this.getData().get(x)[StatsIndex.FOULS_PER_MATCH]);
		}
		break;
		case SortBy.BPM:Collections.sort(this.getData(), blocksPerMatch);
		Collections.reverse(this.getData());
		for (int x = 0; x<this.getData().size(); x++) {
			dupsList.add(this.getData().get(x)[StatsIndex.BLOCKS_PER_MATCH]);
		}
		break;
		case SortBy.PPP:Collections.sort(this.getData(), pointsPerPossession);
		Collections.reverse(this.getData());
		for (int x = 0; x<this.getData().size(); x++) {
			dupsList.add(this.getData().get(x)[StatsIndex.POINTS_PER_POSSESSION]);
		}
		break;
		case SortBy.PSPM:Collections.sort(this.getData(), possessionsPerMatch);
		Collections.reverse(this.getData());
		for (int x = 0; x<this.getData().size(); x++) {
			dupsList.add(this.getData().get(x)[StatsIndex.POSSESSIONS_PER_MATCH]);
		}
		break;
		}
		for (int x = 0; x<dupsList.size(); ) {
			int freq = Collections.frequency(dupsList, dupsList.get(x));
			if (freq > 1) {
				if (reverse) dups.put(dupsList.get(x).intValue(), dupsList.size() - x);
				else dups.put(dupsList.get(x).intValue(), x+1);
			}
			x += freq;
		}
		foulsPerMatchMax = Collections.max(this.getData(), foulsPerMatch)[StatsIndex.FOULS_PER_MATCH];
		foulsPerMatchMin = Collections.min(this.getData(), foulsPerMatch)[StatsIndex.FOULS_PER_MATCH];
		pointsPerPossessionMax = Collections.max(this.getData(), pointsPerPossession)[StatsIndex.POINTS_PER_POSSESSION];
		pointsPerPossessionMin = Collections.min(this.getData(), pointsPerPossession)[StatsIndex.POINTS_PER_POSSESSION];
		blocksPerMatchMax = Collections.max(this.getData(), blocksPerMatch)[StatsIndex.BLOCKS_PER_MATCH];
		blocksPerMatchMin = Collections.min(this.getData(), blocksPerMatch)[StatsIndex.BLOCKS_PER_MATCH];
		possessionsPerMatchMax = Collections.max(this.getData(), possessionsPerMatch)[StatsIndex.POSSESSIONS_PER_MATCH];
		possessionsPerMatchMin = Collections.min(this.getData(), possessionsPerMatch)[StatsIndex.POSSESSIONS_PER_MATCH];
		pointsPerMatchMax = Collections.max(this.getData(), pointsPerMatch)[StatsIndex.POINTS_PER_MATCH];
		pointsPerMatchMin = Collections.min(this.getData(), pointsPerMatch)[StatsIndex.POINTS_PER_MATCH];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		float[] team = getData().get(position);
		String print = "";
		for (int i=0; i<team.length; i++) print += team[i] + " ";
		System.out.println(print);
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_rankings_row_layout, null);
		TextView rank = (TextView) rootView.findViewById(R.id.rank);
		switch (sort) {
		case SortBy.PPM:
			if (dups.get((int) team[StatsIndex.POINTS_PER_MATCH]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.POINTS_PER_MATCH]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.FPM:
			if (dups.get((int) team[StatsIndex.FOULS_PER_MATCH]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.FOULS_PER_MATCH]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.PPP:
			if (dups.get((int) team[StatsIndex.POINTS_PER_POSSESSION]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.POINTS_PER_POSSESSION]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.BPM:
			if (dups.get((int) team[StatsIndex.BLOCKS_PER_MATCH]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.BLOCKS_PER_MATCH]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.PSPM:
			if (dups.get((int) team[StatsIndex.POSSESSIONS_PER_MATCH]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.POSSESSIONS_PER_MATCH]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		default: 
			if (reverse) rank.setText(String.valueOf(getData().size()-position));
			else rank.setText(String.valueOf(position+1));

			break;
		}
		TextView teamNumber = (TextView) rootView.findViewById(R.id.team_number);
		teamNumber.setText("" + (int) team[StatsIndex.TEAM_NUMBER]);

		
		TextView ppm = (TextView) rootView.findViewById(R.id.qp);				
		ppm.setText("" + (int) (team[StatsIndex.POINTS_PER_MATCH]+.5));
		ppm.setBackgroundColor(getColor(team[StatsIndex.POINTS_PER_MATCH], pointsPerMatchMax, pointsPerMatchMin));

		TextView fpm = (TextView) rootView.findViewById(R.id.ap);
		fpm.setText("" + (int) (team[StatsIndex.FOULS_PER_MATCH]+.5));
		fpm.setBackgroundColor(getColor(team[StatsIndex.FOULS_PER_MATCH], foulsPerMatchMax, foulsPerMatchMin));

		TextView bpm = (TextView) rootView.findViewById(R.id.autonp);
		bpm.setText("" + (int) (team[StatsIndex.BLOCKS_PER_MATCH]+.5));
		bpm.setBackgroundColor(getColor(team[StatsIndex.BLOCKS_PER_MATCH], blocksPerMatchMax, blocksPerMatchMin));

		TextView ppp = (TextView) rootView.findViewById(R.id.trussp);
		ppp.setText("" + (int) (team[StatsIndex.POINTS_PER_POSSESSION]+.5));
		ppp.setBackgroundColor(getColor(team[StatsIndex.POINTS_PER_POSSESSION], pointsPerPossessionMax, pointsPerPossessionMin));

		TextView pspm = (TextView) rootView.findViewById(R.id.tp);
		pspm.setText("" + (int) (team[StatsIndex.POSSESSIONS_PER_MATCH]+.5));
		pspm.setBackgroundColor(getColor(team[StatsIndex.POSSESSIONS_PER_MATCH], possessionsPerMatchMax, possessionsPerMatchMin));
		return rootView;
	}			

	public StatsAdapter reverse() {
		Collections.reverse(getData());
		reverse = reverse?false:true;
		dups.clear();
		dupsList.clear();
		switch(sort) {
		case SortBy.PPM:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.POINTS_PER_MATCH]);
			}
			break;
		case SortBy.FPM:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.FOULS_PER_MATCH]);
			}
			break;
		case SortBy.BPM:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.BLOCKS_PER_MATCH]);
			}
			break;
		case SortBy.PPP:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.POINTS_PER_POSSESSION]);
			}
			break;
		case SortBy.PSPM:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.POSSESSIONS_PER_MATCH]);
			}
			break;
		}
		for (int x = 0; x<dupsList.size(); ) {
			int freq = Collections.frequency(dupsList, dupsList.get(x));
			if (freq > 1) {
				if (reverse) dups.put(dupsList.get(x).intValue(), dupsList.size() - x);
				else dups.put(dupsList.get(x).intValue(), x+1);
			}
			x += freq;
		}
		return this;
	}

	private int getColor(float score, double qualMax2, double qualMin2) {
		int alpha = (int) ((score-qualMin2)*100/(qualMax2 - qualMin2));
		String colorStr;
		if (alpha == 100)  {
			colorStr =  "#99ff0000";
		} else if (alpha<10) {
			colorStr =  "#0" + alpha + "ff0000";
		} else {
			colorStr = "#" + String.valueOf(alpha) + "ff0000";
		}
		System.out.println(colorStr);
		return Color.parseColor(colorStr);
	}
	
	public int getSort() {
		return sort;
	}

	public List<float[]> getData() {
		return data;
	}
	
}
