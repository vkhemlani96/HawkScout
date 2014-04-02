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

public class PassingAdapter extends ArrayAdapter<float[]> {

	private Activity activity;
	private List<float[]> data;
	public int sort;
	private boolean reverse;

	float passFromHpMax;
	float passFromHpMin;
	float passFromRoboMax;
	float passFromRoboMin;
	float passToHpMax;
	float passToHpMin;
	float passToRoboMax;
	float passToRoboMin;

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
	Comparator<float[]> passFromHp  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.PASS_FROM_HP];
			Float number2 = two[StatsIndex.PASS_FROM_HP];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> passFromRobot  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.PASS_FROM_ROBOT];
			Float number2 = two[StatsIndex.PASS_FROM_ROBOT];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> passToHp  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.PASS_TO_HP];
			Float number2 = two[StatsIndex.PASS_TO_HP];
			return number1.compareTo(number2);
		}
	};
	Comparator<float[]> passToRobot  = new Comparator<float[]>() {

		@Override
		public int compare(float[] one, float[] two) {
			Float number1 = one[StatsIndex.PASS_TO_ROBOT];
			Float number2 = two[StatsIndex.PASS_TO_ROBOT];
			return number1.compareTo(number2);
		}
	};

	public PassingAdapter(Activity a, List<float[]> teams, int sort) {
		super(a, R.layout.competitions_rankings_row_layout, teams);
		this.activity = a;
		this.data = teams;
		this.sort = sort;
		dupsList.clear();
		dups.clear();
		switch(sort) {
		case SortBy.TEAM:Collections.sort(this.getData(), teamNumber);
		break;
		case SortBy.PASS_FROM_HP:Collections.sort(this.getData(), passFromHp);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.PASS_FROM_HP]);
			}
			break;
		case SortBy.PASS_FROM_ROBOT:Collections.sort(this.getData(), passFromRobot);
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.PASS_FROM_ROBOT]);
			}
			break;
		case SortBy.PASS_TO_HP:Collections.sort(this.getData(), passToHp);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.PASS_TO_HP]);
			}
			break;
		case SortBy.PASS_TO_ROBOT:Collections.sort(this.getData(), passToRobot);
			Collections.reverse(this.getData());
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.PASS_TO_ROBOT]);
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
		passFromRoboMax = Collections.max(this.getData(), passFromRobot)[StatsIndex.PASS_FROM_ROBOT];
		passFromRoboMin = Collections.min(this.getData(), passFromRobot)[StatsIndex.PASS_FROM_ROBOT];
		passToRoboMax = Collections.max(this.getData(), passToRobot)[StatsIndex.PASS_TO_ROBOT];
		passToRoboMin = Collections.min(this.getData(), passToRobot)[StatsIndex.PASS_TO_ROBOT];
		passToHpMax = Collections.max(this.getData(), passToHp)[StatsIndex.PASS_TO_HP];
		passToHpMin = Collections.min(this.getData(), passToHp)[StatsIndex.PASS_TO_HP];
		passFromHpMax = Collections.max(this.getData(), passFromHp)[StatsIndex.PASS_FROM_HP];
		passFromHpMin = Collections.min(this.getData(), passFromHp)[StatsIndex.PASS_FROM_HP];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		float[] team = getData().get(position);
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_rankings_row_layout_passing, null);
		TextView rank = (TextView) rootView.findViewById(R.id.rank);
		switch (sort) {
		case SortBy.PASS_FROM_HP:
			if (dups.get((int) team[StatsIndex.PASS_FROM_HP]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.PASS_FROM_HP]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.PASS_FROM_ROBOT:
			if (dups.get((int) team[StatsIndex.PASS_FROM_ROBOT]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.PASS_FROM_ROBOT]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.PASS_TO_HP:
			if (dups.get((int) team[StatsIndex.PASS_TO_HP]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.PASS_TO_HP]));
			} else if (reverse) {
				rank.setText(String.valueOf(getData().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.PASS_TO_ROBOT:
			if (dups.get((int) team[StatsIndex.PASS_TO_ROBOT]) != 0){
				rank.setText("T" + dups.get((int) team[StatsIndex.PASS_TO_ROBOT]));
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

		TextView pfhp = (TextView) rootView.findViewById(R.id.pfhp);		
		pfhp.setText(df.format(team[StatsIndex.PASS_FROM_HP]));
		pfhp.setBackgroundColor(getColor(team[StatsIndex.PASS_FROM_HP], passFromHpMax, passFromHpMin));

		TextView pfrbt = (TextView) rootView.findViewById(R.id.pfrbt);		
		pfrbt.setText(df.format(team[StatsIndex.PASS_FROM_ROBOT]));
		pfrbt.setBackgroundColor(getColor(team[StatsIndex.PASS_FROM_ROBOT], passFromRoboMax, passFromRoboMin));

		TextView pthp = (TextView) rootView.findViewById(R.id.pthp);		
		pthp.setText(df.format(team[StatsIndex.PASS_TO_HP]));
		pthp.setBackgroundColor(getColor(team[StatsIndex.PASS_TO_HP], passToHpMax, passToHpMin));

		TextView ptrbt = (TextView) rootView.findViewById(R.id.ptrbt);		
		ptrbt.setText(df.format(team[StatsIndex.PASS_TO_ROBOT]));
		ptrbt.setBackgroundColor(getColor(team[StatsIndex.PASS_TO_ROBOT], passToRoboMax, passToRoboMin));
		return rootView;
	}			

	public PassingAdapter reverse() {
		Collections.reverse(getData());
		reverse = reverse?false:true;
		dups.clear();
		dupsList.clear();
		switch(sort) {
		case SortBy.PASS_FROM_HP:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.PASS_FROM_HP]);
			}
			break;
		case SortBy.PASS_FROM_ROBOT:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.PASS_FROM_ROBOT]);
			}
			break;
		case SortBy.PASS_TO_HP:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.PASS_TO_HP]);
			}
			break;
		case SortBy.PASS_TO_ROBOT:
			for (int x = 0; x<this.getData().size(); x++) {
				dupsList.add(this.getData().get(x)[StatsIndex.PASS_TO_ROBOT]);
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
