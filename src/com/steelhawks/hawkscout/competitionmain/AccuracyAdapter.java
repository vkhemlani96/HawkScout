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

public class AccuracyAdapter extends ArrayAdapter<float[]> {

	private Activity activity;
	private List<float[]> data;
	public int sort;
	private boolean reverse;

	float autonHighGoalAccMax;
	float autonHighGoalAccMin;
	float autonLowGoalAccMax;
	float autonLowGoalAccMin;
	float teleopHighGoalAccMax;
	float teleopHighGoalAccMin;
	float teleopLowGoalAccMax;
	float teleopLowGoalAccMin;
	float teleopTrussAccMax;
	float teleopTrussAccMin;

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
	Comparator<float[]> autonHighGoalAcc  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.AUTON_HIGH_GOAL_ACCURACY];
			Float number2 = two[StatsIndex.AUTON_HIGH_GOAL_ACCURACY];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> autonLowGoalAcc  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.AUTON_LOW_GOAL_ACCURACY];
			Float number2 = two[StatsIndex.AUTON_LOW_GOAL_ACCURACY];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> teleopHighGoalAcc  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.TELE_HIGH_GOAL_ACCURACY];
			Float number2 = two[StatsIndex.TELE_HIGH_GOAL_ACCURACY];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> teleopLowGoalAcc  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.TELE_LOW_GOAL_ACCURACY];
			Float number2 = two[StatsIndex.TELE_LOW_GOAL_ACCURACY];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> trussAcc  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.TELE_TRUSS_ACCURACY];
			Float number2 = two[StatsIndex.TELE_TRUSS_ACCURACY];
			return number1.compareTo(number2);
		}
	};

	public AccuracyAdapter(Activity a, List<float[]> teams, int sort) {
		super(a, R.layout.competitions_rankings_row_layout, teams);
		this.activity = a;
		this.data = teams;
		this.sort = sort;
		dupsList.clear();
		dups.clear();
		switch(sort) {
		case SortBy.TEAM:Collections.sort(this.getData(), teamNumber);
		break;
		case SortBy.AUTON_HIGH_GOAL_ACC:Collections.sort(this.getData(), autonHighGoalAcc);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.AUTON_HIGH_GOAL_ACCURACY]);
			}
			break;
		case SortBy.AUTON_LOW_GOAL_ACC:Collections.sort(this.getData(), autonLowGoalAcc);
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.AUTON_LOW_GOAL_ACCURACY]);
			}
			break;
		case SortBy.TELEOP_HIGH_GOAL_ACC:Collections.sort(this.getData(), teleopHighGoalAcc);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TELE_HIGH_GOAL_ACCURACY]);
			}
			break;
		case SortBy.TELEOP_LOW_GOAL_ACC:Collections.sort(this.getData(), teleopLowGoalAcc);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TELE_LOW_GOAL_ACCURACY]);
			}
			break;
		case SortBy.TELEOP_TRUSS_ACC:Collections.sort(this.getData(), trussAcc);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TELE_TRUSS_ACCURACY]);
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
		autonLowGoalAccMax = Collections.max(this.getData(), autonLowGoalAcc)[StatsIndex.AUTON_LOW_GOAL_ACCURACY];
		autonLowGoalAccMin = Collections.min(this.getData(), autonLowGoalAcc)[StatsIndex.AUTON_LOW_GOAL_ACCURACY];
		teleopLowGoalAccMax = Collections.max(this.getData(), teleopLowGoalAcc)[StatsIndex.TELE_LOW_GOAL_ACCURACY];
		teleopLowGoalAccMin = Collections.min(this.getData(), teleopLowGoalAcc)[StatsIndex.TELE_LOW_GOAL_ACCURACY];
		teleopHighGoalAccMax = Collections.max(this.getData(), teleopHighGoalAcc)[StatsIndex.TELE_HIGH_GOAL_ACCURACY];
		teleopHighGoalAccMin = Collections.min(this.getData(), teleopHighGoalAcc)[StatsIndex.TELE_HIGH_GOAL_ACCURACY];
		teleopTrussAccMax = Collections.max(this.getData(), trussAcc)[StatsIndex.TELE_TRUSS_ACCURACY];
		teleopTrussAccMin = Collections.min(this.getData(), trussAcc)[StatsIndex.TELE_TRUSS_ACCURACY];
		autonHighGoalAccMax = Collections.max(this.getData(), autonHighGoalAcc)[StatsIndex.AUTON_HIGH_GOAL_ACCURACY];
		autonHighGoalAccMin = Collections.min(this.getData(), autonHighGoalAcc)[StatsIndex.AUTON_HIGH_GOAL_ACCURACY];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		float[] team = getData().get(position);
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_rankings_row_layout, null);
		TextView rank = (TextView) rootView.findViewById(R.id.rank);
		switch (sort) {
		case SortBy.AUTON_HIGH_GOAL_ACC:
			if (dups.get((int) team[StatsIndex.AUTON_HIGH_GOAL_ACCURACY]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.AUTON_HIGH_GOAL_ACCURACY]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.AUTON_LOW_GOAL_ACC:
			if (dups.get((int) team[StatsIndex.AUTON_LOW_GOAL_ACCURACY]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.AUTON_LOW_GOAL_ACCURACY]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.TELEOP_HIGH_GOAL_ACC:
			if (dups.get((int) team[StatsIndex.TELE_HIGH_GOAL_ACCURACY]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.TELE_HIGH_GOAL_ACCURACY]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.TELEOP_LOW_GOAL_ACC:
			if (dups.get((int) team[StatsIndex.TELE_LOW_GOAL_ACCURACY]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.TELE_LOW_GOAL_ACCURACY]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.TELEOP_TRUSS_ACC:
			if (dups.get((int) team[StatsIndex.TELE_TRUSS_ACCURACY]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.TELE_TRUSS_ACCURACY]));
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

		TextView ahg = (TextView) rootView.findViewById(R.id.qp);		
		ahg.setText(df.format(team[StatsIndex.AUTON_HIGH_GOAL_ACCURACY]+.05) + "%");
		ahg.setBackgroundColor(getColor(team[StatsIndex.AUTON_HIGH_GOAL_ACCURACY], autonHighGoalAccMax, autonHighGoalAccMin));

		TextView alg = (TextView) rootView.findViewById(R.id.ap);		
		alg.setText(df.format(team[StatsIndex.AUTON_LOW_GOAL_ACCURACY]+.05) + "%");
		alg.setBackgroundColor(getColor(team[StatsIndex.AUTON_LOW_GOAL_ACCURACY], autonLowGoalAccMax, autonLowGoalAccMin));

		TextView thg = (TextView) rootView.findViewById(R.id.autonp);		
		thg.setText(df.format(team[StatsIndex.TELE_HIGH_GOAL_ACCURACY]+.05) + "%");
		thg.setBackgroundColor(getColor(team[StatsIndex.TELE_HIGH_GOAL_ACCURACY], teleopHighGoalAccMax, teleopHighGoalAccMin));

		TextView tlg = (TextView) rootView.findViewById(R.id.trussp);		
		tlg.setText(df.format(team[StatsIndex.TELE_LOW_GOAL_ACCURACY]+.05) + "%");
		tlg.setBackgroundColor(getColor(team[StatsIndex.TELE_LOW_GOAL_ACCURACY], teleopLowGoalAccMax, teleopLowGoalAccMin));

		TextView tt = (TextView) rootView.findViewById(R.id.tp);		
		tt.setText(df.format(team[StatsIndex.TELE_TRUSS_ACCURACY]+.05) + "%");
		tt.setBackgroundColor(getColor(team[StatsIndex.TELE_TRUSS_ACCURACY], teleopTrussAccMax, teleopTrussAccMin));
		return rootView;
	}			

	public AccuracyAdapter reverse() {
		Collections.reverse(getData());
		reverse = reverse?false:true;
		dups.clear();
		dupsList.clear();
		switch(sort) {
		case SortBy.AUTON_HIGH_GOAL_ACC:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.AUTON_HIGH_GOAL_ACCURACY]);
			}
			break;
		case SortBy.AUTON_LOW_GOAL_ACC:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.AUTON_LOW_GOAL_ACCURACY]);
			}
			break;
		case SortBy.TELEOP_HIGH_GOAL_ACC:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TELE_HIGH_GOAL_ACCURACY]);
			}
			break;
		case SortBy.TELEOP_LOW_GOAL_ACC:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TELE_LOW_GOAL_ACCURACY]);
			}
			break;
		case SortBy.TELEOP_TRUSS_ACC:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.TELE_TRUSS_ACCURACY]);
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
