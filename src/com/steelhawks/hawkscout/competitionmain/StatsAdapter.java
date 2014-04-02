package com.steelhawks.hawkscout.competitionmain;

import java.text.DecimalFormat;
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

	float highGoalMax;
	float highGoalMin;
	float lowGoalMax;
	float lowGoalMin;
	float trussMax;
	float trussMin;
	float catchMax;
	float catchMin;
	float foulsMax;
	float foulsMin;
	float techFoulsMax;
	float techFoulsMin;

	private List<Float> dupsList = new ArrayList<Float>();
	private SparseIntArray dups = new SparseIntArray();

	Comparator<float[]> teamNumber  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.TEAM_NUMBER];
			Float number2 = two[StatsIndex.TEAM_NUMBER];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> highGoal  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.HIGH_GOAL_TOTAL];
			Float number2 = two[StatsIndex.HIGH_GOAL_TOTAL];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> lowGoal  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.LOW_GOAL_TOTAL];
			Float number2 = two[StatsIndex.LOW_GOAL_TOTAL];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> truss  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.TRUSS_TOTAL];
			Float number2 = two[StatsIndex.TRUSS_TOTAL];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> catches  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.CATCHES_TOTAL];
			Float number2 = two[StatsIndex.CATCHES_TOTAL];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> fouls  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.FOULS_TOTAL];
			Float number2 = two[StatsIndex.FOULS_TOTAL];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> techFouls  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.TECHNICAL_FOULS_TOTAL];
			Float number2 = two[StatsIndex.TECHNICAL_FOULS_TOTAL];
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
		case SortBy.TEAM:Collections.sort(this.getData(), teamNumber);
		break;
		case SortBy.HIGH_GOAL_TOTAL:Collections.sort(this.getData(), highGoal);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.HIGH_GOAL_TOTAL]);
			}
			break;
		case SortBy.LOW_GOAL_TOTAL:Collections.sort(this.getData(), lowGoal);
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.LOW_GOAL_TOTAL]);
			}
			break;
		case SortBy.TRUSS_TOTAL:Collections.sort(this.getData(), truss);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TRUSS_TOTAL]);
			}
			break;
		case SortBy.CATCH_TOTAL:Collections.sort(this.getData(), catches);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.CATCHES_TOTAL]);
			}
			break;
		case SortBy.FOUL_TOTAL:Collections.sort(this.getData(), fouls);
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.POSSESSIONS_PER_MATCH]);
			}
			break;
		case SortBy.TECH_FOUL_TOTAL:Collections.sort(this.getData(), techFouls);
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TECHNICAL_FOULS_TOTAL]);
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
		highGoalMax = Collections.max(this.getData(), highGoal)[StatsIndex.HIGH_GOAL_TOTAL];
		highGoalMin = Collections.min(this.getData(), highGoal)[StatsIndex.HIGH_GOAL_TOTAL];
		lowGoalMax = Collections.max(this.getData(), lowGoal)[StatsIndex.LOW_GOAL_TOTAL];
		lowGoalMin = Collections.min(this.getData(), lowGoal)[StatsIndex.LOW_GOAL_TOTAL];
		catchMax = Collections.max(this.getData(), catches)[StatsIndex.CATCHES_TOTAL];
		catchMin = Collections.min(this.getData(), catches)[StatsIndex.CATCHES_TOTAL];
		trussMax = Collections.max(this.getData(), truss)[StatsIndex.TRUSS_TOTAL];
		trussMin = Collections.min(this.getData(), truss)[StatsIndex.TRUSS_TOTAL];
		foulsMax = Collections.max(this.getData(), fouls)[StatsIndex.FOULS_TOTAL];
		foulsMin = Collections.min(this.getData(), fouls)[StatsIndex.FOULS_TOTAL];
		techFoulsMax = Collections.max(this.getData(), fouls)[StatsIndex.TECHNICAL_FOULS_TOTAL];
		techFoulsMin = Collections.min(this.getData(), fouls)[StatsIndex.TECHNICAL_FOULS_TOTAL];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		float[] team = getData().get(position);
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_rankings_row_layout_stats, null);
		TextView rank = (TextView) rootView.findViewById(R.id.rank);
		switch (sort) {
		case SortBy.HIGH_GOAL_TOTAL:
			if (dups.get((int) team[StatsIndex.HIGH_GOAL_TOTAL]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.HIGH_GOAL_TOTAL]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.LOW_GOAL_TOTAL:
			if (dups.get((int) team[StatsIndex.LOW_GOAL_TOTAL]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.LOW_GOAL_TOTAL]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.TRUSS_TOTAL:
			if (dups.get((int) team[StatsIndex.TRUSS_TOTAL]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.TRUSS_TOTAL]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.CATCH_TOTAL:
			if (dups.get((int) team[StatsIndex.CATCHES_TOTAL]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.CATCHES_TOTAL]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.FOUL_TOTAL:
			if (dups.get((int) team[StatsIndex.FOULS_TOTAL]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.FOULS_TOTAL]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.TECH_FOUL_TOTAL:
			if (dups.get((int) team[StatsIndex.TECHNICAL_FOULS_TOTAL]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.TECHNICAL_FOULS_TOTAL]));
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

		DecimalFormat df = new DecimalFormat("#.#");

		TextView hgt = (TextView) rootView.findViewById(R.id.high_goal_total);		
		hgt.setText(df.format(team[StatsIndex.HIGH_GOAL_TOTAL]+.05));
		hgt.setBackgroundColor(getColor(team[StatsIndex.HIGH_GOAL_TOTAL], highGoalMax, highGoalMin));

		TextView lgt = (TextView) rootView.findViewById(R.id.low_goal_total);		
		lgt.setText(df.format(team[StatsIndex.LOW_GOAL_TOTAL]+.05));
		lgt.setBackgroundColor(getColor(team[StatsIndex.LOW_GOAL_TOTAL], lowGoalMax, lowGoalMin));

		TextView tt = (TextView) rootView.findViewById(R.id.truss_total);		
		tt.setText(df.format(team[StatsIndex.TRUSS_TOTAL]+.05));
		tt.setBackgroundColor(getColor(team[StatsIndex.TRUSS_TOTAL], trussMax, trussMin));

		TextView ct = (TextView) rootView.findViewById(R.id.catch_total);		
		ct.setText(df.format(team[StatsIndex.CATCHES_TOTAL]+.05));
		ct.setBackgroundColor(getColor(team[StatsIndex.CATCHES_TOTAL], catchMax, catchMin));

		TextView ft = (TextView) rootView.findViewById(R.id.foul_total);		
		ft.setText(df.format(team[StatsIndex.FOULS_TOTAL]+.05));
		ft.setBackgroundColor(getInverseColor(team[StatsIndex.FOULS_TOTAL], foulsMax, foulsMin));

		TextView tft = (TextView) rootView.findViewById(R.id.tech_foul_total);		
		tft.setText(df.format(team[StatsIndex.TECHNICAL_FOULS_TOTAL]+.05));
		tft.setBackgroundColor(getInverseColor(team[StatsIndex.TECHNICAL_FOULS_TOTAL], techFoulsMax, techFoulsMin));
		return rootView;
	}			

	public StatsAdapter reverse() {
		Collections.reverse(getData());
		reverse = reverse?false:true;
		dups.clear();
		dupsList.clear();
		switch(sort) {
		case SortBy.HIGH_GOAL_TOTAL:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.HIGH_GOAL_TOTAL]);
			}
			break;
		case SortBy.LOW_GOAL_TOTAL:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.LOW_GOAL_TOTAL]);
			}
			break;
		case SortBy.TRUSS_TOTAL:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TRUSS_TOTAL]);
			}
			break;
		case SortBy.CATCH_TOTAL:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.CATCHES_TOTAL]);
			}
			break;
		case SortBy.FOUL_TOTAL:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.FOULS_TOTAL]);
			}
			break;
		case SortBy.TECH_FOUL_TOTAL:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TECHNICAL_FOULS_TOTAL]);
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
		return Color.parseColor(colorStr);
	}

	private int getInverseColor(float score, double qualMax2, double qualMin2) {
		int alpha = (int) ((score-qualMin2)*100/(qualMax2 - qualMin2));
		alpha = 100 - alpha;
		String colorStr;
		if (alpha == 100)  {
			colorStr =  "#99ff0000";
		} else if (alpha<10) {
			colorStr =  "#0" + alpha + "ff0000";
		} else {
			colorStr = "#" + String.valueOf(alpha) + "ff0000";
		}
		return Color.parseColor(colorStr);
	}

	public int getSort() {
		return sort;
	}

	public List<float[]> getData() {
		return data;
	}

}
