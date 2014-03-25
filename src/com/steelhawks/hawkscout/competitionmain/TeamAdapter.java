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
import com.steelhawks.hawkscout.util.Utilities;

public class TeamAdapter extends ArrayAdapter<String[]> {

	private Activity activity;
	private List<String[]> teams;
	public int sort;
	private boolean reverse;

	double qualMax;
	double qualMin;
	double autonMax;
	double autonMin;
	double assistMax;
	double assistMin;
	double trussMax;
	double trussMin;
	double teleMax;
	double teleMin;

	private List<Double> dupsList = new ArrayList<Double>();
	private SparseIntArray dups = new SparseIntArray();

	Comparator<String[]> rankings = new Comparator<String[]>() {

		@Override
		public int compare(String[] one, String[] two) {
			Double number1 = Double.parseDouble(one[SortBy.QUAL]);
			Double number2 = Double.parseDouble(two[SortBy.QUAL]);
			if (number1 != number2)	return number2.compareTo(number1);
			number1 = Double.parseDouble(one[SortBy.ASSIST]);
			number2 = Double.parseDouble(two[SortBy.ASSIST]);
			if (number1 != number2)	return number2.compareTo(number1);
			number1 = Double.parseDouble(one[SortBy.AUTON]);
			number2 = Double.parseDouble(two[SortBy.AUTON]);
			if (number1 != number2)	return number2.compareTo(number1);
			number1 = Double.parseDouble(one[SortBy.TRUSS]);
			number2 = Double.parseDouble(two[SortBy.TRUSS]);
			if (number1 != number2)	return number2.compareTo(number1);
			number1 = Double.parseDouble(one[SortBy.TELE]);
			number2 = Double.parseDouble(two[SortBy.TELE]);
			return number2.compareTo(number1);
		}
	};
	Comparator<String[]> teamNumber  = new Comparator<String[]>() {

		@Override
		public int compare(String[] one, String[] two) {
			Double number1 = Double.parseDouble(one[SortBy.TEAM]);
			Double number2 = Double.parseDouble(two[SortBy.TEAM]);
			return number1.compareTo(number2);
		}
	};
	Comparator<String[]> qualScore  = new Comparator<String[]>() {

		@Override
		public int compare(String[] one, String[] two) {
			Double number1 = Double.parseDouble(one[SortBy.QUAL]);
			Double number2 = Double.parseDouble(two[SortBy.QUAL]);
			return number1.compareTo(number2);
		}
	};
	Comparator<String[]> assistScore  = new Comparator<String[]>() {

		@Override
		public int compare(String[] one, String[] two) {
			Double number1 = Double.parseDouble(one[SortBy.ASSIST]);
			Double number2 = Double.parseDouble(two[SortBy.ASSIST]);
			return number1.compareTo(number2);
		}
	};
	Comparator<String[]> autonScore  = new Comparator<String[]>() {

		@Override
		public int compare(String[] one, String[] two) {
			Double number1 = Double.parseDouble(one[SortBy.AUTON]);
			Double number2 = Double.parseDouble(two[SortBy.AUTON]);
			return number1.compareTo(number2);
		}
	};
	Comparator<String[]> trussScore  = new Comparator<String[]>() {

		@Override
		public int compare(String[] one, String[] two) {
			Double number1 = Double.parseDouble(one[SortBy.TRUSS]);
			Double number2 = Double.parseDouble(two[SortBy.TRUSS]);
			return number1.compareTo(number2);
		}
	};
	Comparator<String[]> teleScore  = new Comparator<String[]>() {

		@Override
		public int compare(String[] one, String[] two) {
			Double number1 = Double.parseDouble(one[SortBy.TELE]);
			Double number2 = Double.parseDouble(two[SortBy.TELE]);
			return number1.compareTo(number2);
		}
	}; 
	
	public TeamAdapter(Activity a, List<String[]> teams, int sort) {
		super(a, R.layout.competitions_rankings_row_layout, teams);
		this.activity = a;
		this.teams = teams;
		this.sort = sort;
		dupsList.clear();
		dups.clear();
		switch(sort) {
		case SortBy.TEAM:Collections.sort(this.getTeams(), teamNumber);
		break;
		case SortBy.QUAL:Collections.sort(this.getTeams(), rankings);
		//				Collections.reverse(this.teams);
		break;
		case SortBy.AUTON:Collections.sort(this.getTeams(), autonScore);
		Collections.reverse(this.getTeams());
		for (int x = 0; x<this.getTeams().size(); x++) {
			dupsList.add(Double.parseDouble(this.getTeams().get(x)[SortBy.AUTON]));
		}
		break;
		case SortBy.ASSIST:Collections.sort(this.getTeams(), assistScore);
		Collections.reverse(this.getTeams());
		for (int x = 0; x<this.getTeams().size(); x++) {
			dupsList.add(Double.parseDouble(this.getTeams().get(x)[SortBy.ASSIST]));
		}
		break;
		case SortBy.TRUSS:Collections.sort(this.getTeams(), trussScore);
		Collections.reverse(this.getTeams());
		for (int x = 0; x<this.getTeams().size(); x++) {
			dupsList.add(Double.parseDouble(this.getTeams().get(x)[SortBy.TRUSS]));
		}
		break;
		case SortBy.TELE:Collections.sort(this.getTeams(), teleScore);
		Collections.reverse(this.getTeams());
		for (int x = 0; x<this.getTeams().size(); x++) {
			dupsList.add(Double.parseDouble(this.getTeams().get(x)[SortBy.TELE]));
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
		qualMax = Double.parseDouble(Collections.max(this.getTeams(), qualScore)[SortBy.QUAL]);
		qualMin = Double.parseDouble(Collections.min(this.getTeams(), qualScore)[SortBy.QUAL]);
		autonMax = Double.parseDouble(Collections.max(this.getTeams(), autonScore)[SortBy.AUTON]);
		autonMin = Double.parseDouble(Collections.min(this.getTeams(), autonScore)[SortBy.AUTON]);
		assistMax = Double.parseDouble(Collections.max(this.getTeams(), assistScore)[SortBy.ASSIST]);
		assistMin = Double.parseDouble(Collections.min(this.getTeams(), assistScore)[SortBy.ASSIST]);
		trussMax = Double.parseDouble(Collections.max(this.getTeams(), trussScore)[SortBy.TRUSS]);
		trussMin = Double.parseDouble(Collections.min(this.getTeams(), trussScore)[SortBy.TRUSS]);
		teleMax = Double.parseDouble(Collections.max(this.getTeams(), teleScore)[SortBy.TELE]);
		teleMin = Double.parseDouble(Collections.min(this.getTeams(), teleScore)[SortBy.TELE]);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String[] team = getTeams().get(position);
		String print = "";
		for (int i=0; i<team.length; i++) print += team[i] + " ";
		System.out.println(print);
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_rankings_row_layout, null);
		TextView rank = (TextView) rootView.findViewById(R.id.rank);
		switch (sort) {
		case SortBy.AUTON:
			if (dups.get((int) Double.parseDouble(team[SortBy.AUTON])) != 0){
				rank.setText("T" + dups.get((int) Double.parseDouble(team[SortBy.AUTON])));
			} else if (reverse) {
				rank.setText(String.valueOf(getTeams().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.ASSIST:
			if (dups.get((int) Double.parseDouble(team[SortBy.ASSIST])) != 0){
				rank.setText("T" + dups.get((int) Double.parseDouble(team[SortBy.ASSIST])));
			} else if (reverse) {
				rank.setText(String.valueOf(getTeams().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.TRUSS:
			if (dups.get((int) Double.parseDouble(team[SortBy.TRUSS])) != 0){
				rank.setText("T" + dups.get((int) Double.parseDouble(team[SortBy.TRUSS])));
			} else if (reverse) {
				rank.setText(String.valueOf(getTeams().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		case SortBy.TELE:
			if (dups.get((int) Double.parseDouble(team[SortBy.TELE])) != 0){
				rank.setText("T" + dups.get((int) Double.parseDouble(team[SortBy.TELE])));
			} else if (reverse) {
				rank.setText(String.valueOf(getTeams().size()-position));
			} else {
				rank.setText(String.valueOf(position+1));
			}
			break;
		default: 
			if (reverse) rank.setText(String.valueOf(getTeams().size()-position));
			else rank.setText(String.valueOf(position+1));

			break;
		}
		TextView teamNumber = (TextView) rootView.findViewById(R.id.team_number);
		teamNumber.setText(team[SortBy.TEAM]);

		TextView qp = (TextView) rootView.findViewById(R.id.qp);				
		qp.setText(team[SortBy.QUAL].trim().equals("0") ? "0" : 
			team[SortBy.QUAL].substring(0, team[SortBy.QUAL].indexOf(".")));
		qp.setBackgroundColor(getColor(team[SortBy.QUAL], qualMax, qualMin));

		TextView ap = (TextView) rootView.findViewById(R.id.ap);
		ap.setText(team[SortBy.ASSIST].trim().equals("0") ? "0" : 
			team[SortBy.ASSIST].substring(0, team[SortBy.ASSIST].indexOf(".")));
		ap.setBackgroundColor(getColor(team[SortBy.ASSIST], assistMax, assistMin));

		TextView autonp = (TextView) rootView.findViewById(R.id.autonp);
		autonp.setText(team[SortBy.AUTON].trim().equals("0") ? "0" : 
			team[SortBy.AUTON].substring(0, team[SortBy.AUTON].indexOf(".")));
		autonp.setBackgroundColor(getColor(team[SortBy.AUTON], autonMax, autonMin));

		TextView trussp = (TextView) rootView.findViewById(R.id.trussp);
		trussp.setText(team[SortBy.TRUSS].trim().equals("0") ? "0" : 
			team[SortBy.TRUSS].substring(0, team[SortBy.TRUSS].indexOf(".")));
		trussp.setBackgroundColor(getColor(team[SortBy.TRUSS], trussMax, trussMin));

		TextView tp = (TextView) rootView.findViewById(R.id.tp);
		tp.setText(team[SortBy.TELE].trim().equals("0") ? "0" : 
			team[SortBy.TELE].substring(0, team[SortBy.TELE].indexOf(".")));
		tp.setBackgroundColor(getColor(team[SortBy.TELE], teleMax, teleMin));
		return rootView;
	}			

	public TeamAdapter reverse() {
		Collections.reverse(getTeams());
		reverse = reverse?false:true;
		dups.clear();
		dupsList.clear();
		switch(sort) {
		case SortBy.AUTON:
			for (int x = 0; x<this.getTeams().size(); x++) {
				dupsList.add(Double.parseDouble(this.getTeams().get(x)[SortBy.AUTON]));
			}
			break;
		case SortBy.ASSIST:
			for (int x = 0; x<this.getTeams().size(); x++) {
				dupsList.add(Double.parseDouble(this.getTeams().get(x)[SortBy.ASSIST]));
			}
			break;
		case SortBy.TRUSS:
			for (int x = 0; x<this.getTeams().size(); x++) {
				dupsList.add(Double.parseDouble(this.getTeams().get(x)[SortBy.TRUSS]));
			}
			break;
		case SortBy.TELE:
			for (int x = 0; x<this.getTeams().size(); x++) {
				dupsList.add(Double.parseDouble(this.getTeams().get(x)[SortBy.TELE]));
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

	private int getColor(String scoreString, double qualMax2, double qualMin2) {
		double score = Double.parseDouble(scoreString);
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
	
	public int getSort() {
		return sort;
	}

	public List<String[]> getTeams() {
		return teams;
	}
	
}
