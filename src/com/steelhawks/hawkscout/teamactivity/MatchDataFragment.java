package com.steelhawks.hawkscout.teamactivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.steelhawks.hawkscout.MatchScouting;
import com.steelhawks.hawkscout.R;
import com.steelhawks.hawkscout.data.Competition;
import com.steelhawks.hawkscout.data.Indices.MatchIndex;
import com.steelhawks.hawkscout.data.Indices.MatchScoutingIndex;
import com.steelhawks.hawkscout.data.Indices.PossessionIndex;
import com.steelhawks.hawkscout.util.GraphView;
import com.steelhawks.hawkscout.util.Utilities;

public class MatchDataFragment extends Fragment implements OnClickListener {

	Competition currentComp;
	String teamNumber;
	String[] data;
	String[] matchInfo;
	String matchNumber;
	String alliance;
	ViewFlipper root;
	public static final String DATA_ID = "DATA_ID";
	public static final String MATCH_NUMBER_ID = "MATCH_NUMBER_ID";
	public static final String TEAM_NUMBER_ID = "TEAM_NUMBER_ID";
	public static final String MATCH_INFO_ID = "MATCH_INFO_ID";

	public MatchDataFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		storeArguments();

		if (data == null) return getEmptyMatchLayout(inflater, container, savedInstanceState);

		root = (ViewFlipper) inflater.inflate(R.layout.activity_team_match_review_layout, container, false);
		((TextView) root.findViewById(R.id.match_number)).setText(matchNumber);

		int[] ids = {
				R.id.red1,
				R.id.red2,
				R.id.red3,
				R.id.red_score,
				R.id.blue1,
				R.id.blue2,
				R.id.blue3,
				R.id.blue_score
		};

		int[] indices = {
				MatchIndex.RED1,
				MatchIndex.RED2,
				MatchIndex.RED3,
				MatchIndex.RED_SCORE,
				MatchIndex.BLUE1,
				MatchIndex.BLUE2,
				MatchIndex.BLUE3,
				MatchIndex.BLUE_SCORE
		};
		/*
		 * Find what alliance and station the current team is on and make that view bold. Also, store the alliance color
		 * for later use.
		 */
		for (int x=0; x<ids.length; x++) {
			String s = matchInfo[indices[x]].trim();
			if (indices[x] != MatchIndex.RED_SCORE && indices[x] != MatchIndex.BLUE_SCORE && s.equals(teamNumber)) {
				if (indices[x] == MatchIndex.RED1 || indices[x] == MatchIndex.RED2 || indices[x] == MatchIndex.RED3) alliance = "RED";
				else alliance = "BLUE";
				((TextView) root.findViewById(ids[x])).setTypeface(null, Typeface.BOLD);
			}
			((TextView) root.findViewById(ids[x])).setText(s);
		}

		/*
		 * Parse the matchInfo for the red and blue scores and compare them to see who won. If the current team is 
		 * apart of the winning alliance, make the result "W". If not, make it "L".
		 */
		try {
			int redScore = Integer.parseInt(matchInfo[MatchIndex.RED_SCORE].trim());
			int blueScore = Integer.parseInt(matchInfo[MatchIndex.BLUE_SCORE].trim());
			String result;
			if (redScore > blueScore && alliance.equals("RED") || (blueScore > redScore && alliance.equals("BLUE")))
				result = "W";
			else result = "L";
			((TextView) root.findViewById(R.id.match_result)).setText(result);
			int winningId = redScore > blueScore ? R.id.red_score : R.id.blue_score;
			int backgroundColor = redScore > blueScore ? Color.parseColor("#10ff0000") : Color.parseColor("#100000ff");
			root.findViewById(winningId).setBackgroundColor(backgroundColor);
		} catch(Exception e) {
			System.err.println("Result not set.");
		}

		/*
		 * Calculate states from data array.
		 */
		/* Autonomous High Goal Stats */
		int highHot = Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_HOT].trim());
		int highMade = highHot + Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_COLD].trim());
		int highTotal = highMade + Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_MISSED].trim());
		String autonHigh = highMade + "/" + highTotal + " (" + highHot + " Hot)";
		int autonHighPoints = highMade*15 + highHot*5;

		int lowHot = Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_HOT].trim());
		int lowMade = lowHot + Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_COLD].trim());
		int lowTotal = lowMade + Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_MISSED].trim());
		String autonLow = lowMade + "/" + lowTotal + " (" + highHot + " Hot)";
		int autonLowPoints = lowMade*6 + lowHot*5;

		boolean forwardMovement = Boolean.parseBoolean(data[MatchScoutingIndex.AUTON_MOVED_FORWARD].trim());

		int teleopLowMade = Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MADE].trim());
		int teleopLowTotal = teleopLowMade + Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MISSED].trim());
		String teleopLow = teleopLowMade + "/" + teleopLowTotal + " (" + (teleopLowMade*100/teleopLowTotal) + "%)";
		int teleopLowPoints = teleopLowMade;

		int teleopHighMade = Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MADE].trim());
		int teleopHighTotal = teleopHighMade + Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MISSED].trim());
		String teleopHigh = teleopHighMade + "/" + teleopHighTotal + " (" + (teleopHighMade*100/teleopHighTotal) + "%)";
		int teleopHighPoints = teleopHighMade*10;

		int trussMade = Integer.parseInt(data[MatchScoutingIndex.TRUSS].trim());
		int trussTotal = trussMade + Integer.parseInt(data[MatchScoutingIndex.TRUSS_MISSED]);
		String truss = trussMade + "/" + trussTotal + " (" + (trussMade*100/trussTotal) + "%)";
		int trussPoints = trussMade*10;

		int catches = Integer.parseInt(data[MatchScoutingIndex.CATCHES].trim());
		int catchPoints = catches*10;

		float totalPoints = autonHighPoints + autonLowPoints + teleopHighPoints + teleopLowPoints + trussPoints + catchPoints +
				(forwardMovement ? 5 : 0);

		((TextView) root.findViewById(R.id.auton_high_goal)).setText(autonHigh);
		((TextView) root.findViewById(R.id.auton_high_goal_points)).setText(autonHighPoints + " Points");
		root.findViewById(R.id.auton_high_goal_bar).setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, autonHighPoints/totalPoints));

		((TextView) root.findViewById(R.id.auton_low_goal)).setText(autonLow);
		((TextView) root.findViewById(R.id.auton_low_goal_points)).setText(autonLowPoints + " Points");	
		root.findViewById(R.id.auton_low_goal_bar).setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, autonLowPoints/totalPoints));

		((TextView) root.findViewById(R.id.teleop_high_goal)).setText(teleopHigh);
		((TextView) root.findViewById(R.id.teleop_high_goal_points)).setText(teleopHighPoints + " Points");
		root.findViewById(R.id.teleop_high_goal_bar).setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, teleopHighPoints/totalPoints));

		((TextView) root.findViewById(R.id.teleop_low_goal)).setText(teleopLow);
		((TextView) root.findViewById(R.id.teleop_low_goal_points)).setText(teleopLowPoints + " Points");
		root.findViewById(R.id.teleop_low_goal_bar).setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, teleopLowPoints/totalPoints));

		((TextView) root.findViewById(R.id.truss)).setText(truss);
		((TextView) root.findViewById(R.id.truss_points)).setText(trussPoints + " Points");
		root.findViewById(R.id.truss_bar).setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, trussPoints/totalPoints));

		((TextView) root.findViewById(R.id.catches)).setText(catches + "");
		((TextView) root.findViewById(R.id.catch_points)).setText(catchPoints + " Points");
		root.findViewById(R.id.catch_bar).setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, catchPoints/totalPoints));

		((TextView) root.findViewById(R.id.forward_movement)).setText(forwardMovement ? "Yes" : "No");
		((TextView) root.findViewById(R.id.auton_blocks)).setText(data[MatchScoutingIndex.AUTON_BLOCKS].trim());
		((TextView) root.findViewById(R.id.auton_fouls)).setText(data[MatchScoutingIndex.AUTON_FOULS].trim());
		((TextView) root.findViewById(R.id.auton_tech_fouls)).setText(data[MatchScoutingIndex.AUTON_TECH_FOULS].trim());
		((TextView) root.findViewById(R.id.teleop_blocks)).setText(data[MatchScoutingIndex.TELEOP_BLOCKS].trim());
		((TextView) root.findViewById(R.id.deflections)).setText(data[MatchScoutingIndex.DEFLECTIONS].trim());
		((TextView) root.findViewById(R.id.passes_to_robots)).setText(data[MatchScoutingIndex.PASSES_TO_ROBOT].trim());
		((TextView) root.findViewById(R.id.passes_to_HP)).setText(data[MatchScoutingIndex.PASSES_TO_HP].trim());
		((TextView) root.findViewById(R.id.balls_lost)).setText(data[MatchScoutingIndex.BALLS_LOST].trim());
		((TextView) root.findViewById(R.id.passes_from_robots)).setText(data[MatchScoutingIndex.PASSES_FROM_ROBOT].trim());
		((TextView) root.findViewById(R.id.passes_from_hp)).setText(data[MatchScoutingIndex.PASSES_FROM_HP].trim());
		((TextView) root.findViewById(R.id.balls_picked_up)).setText(data[MatchScoutingIndex.BALLS_PICKED_UP].trim());
		((TextView) root.findViewById(R.id.teleop_fouls)).setText(data[MatchScoutingIndex.FOULS].trim());
		((TextView) root.findViewById(R.id.teleop_technical_fouls)).setText(data[MatchScoutingIndex.TECH_FOULS].trim());
		/*
		 * Puts robot on the field view to indicate starting position. Pulls coordinates from data array, parses into floats and then
		 * sets x and y in frameview. 
		 * Sets robot color based on alliance's color.
		 */
		String[] robotCoordinates = data[MatchScoutingIndex.AUTON_POSITION].trim().split(",");
		View robot = root.findViewById(R.id.robot);
		robot.setX(Float.parseFloat(robotCoordinates[0].trim()));
		robot.setY(Float.parseFloat(robotCoordinates[1].trim()));
		System.out.println(data[MatchScoutingIndex.ALLIANCE].trim() + data[MatchScoutingIndex.ALLIANCE].trim().length());
		int robotColor = data[MatchScoutingIndex.ALLIANCE].trim().equals("Red") ? Color.parseColor("#50cc0000") : Color.parseColor("#500000cc");
		robot.setBackgroundColor(robotColor);

		String[] possessions = data[MatchScoutingIndex.POSSESSIONS].trim().split("\\|\\|");
		int possessionTime = 0;
		for (int x=0; x<possessions.length; x++) {

			String[] possessionPart = possessions[x].trim().split("\\|");
			if (possessionPart[PossessionIndex.POSSESSION_END].trim().equals("null")) {
				possessionTime += Integer.parseInt(possessionPart[PossessionIndex.START_TIME].trim());
				possessionPart[PossessionIndex.POSSESSION_END] = "Match Ended";
			} else {
				possessionTime += Integer.parseInt(possessionPart[PossessionIndex.START_TIME].trim())
						- Integer.parseInt(possessionPart[PossessionIndex.END_TIME].trim());
			}

			LinearLayout possessionRoot = (LinearLayout) inflater.inflate(R.layout.activity_team_match_review_possession_layout, null, false);
			((TextView) possessionRoot.findViewById(R.id.possession_start)).setText(possessionPart[PossessionIndex.POSSESSION_START].trim());
			((TextView) possessionRoot.findViewById(R.id.possession_end)).setText(possessionPart[PossessionIndex.POSSESSION_END].trim());
			((TextView) possessionRoot.findViewById(R.id.end_time)).setText(possessionPart[PossessionIndex.START_TIME].trim() + " to " + 
					possessionPart[PossessionIndex.END_TIME].trim());
			((ViewGroup) root.findViewById(R.id.possessions)).addView(possessionRoot);

			if (x != possessions.length-1) {
				View sep = new View(getActivity(), null, R.style.thin_seperator);
				sep.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Utilities.PX(getActivity(), 1)));
				((ViewGroup) root.findViewById(R.id.possessions)).addView(sep);
			}

		}
		
		int autonomousPoints = autonHighPoints + autonLowPoints;
		int total = autonomousPoints + teleopHighPoints + teleopLowPoints + trussPoints + catchPoints;

		((TextView) root.findViewById(R.id.autonomous_scored_stat)).setText(autonomousPoints + "");
		((TextView) root.findViewById(R.id.autonomous_scored_percent)).setText(100*autonomousPoints/total + "%");
		if (autonomousPoints == 0) ((View) root.findViewById(R.id.autonomous_scored_stat).getParent()).setAlpha(.5f);
		
		((TextView) root.findViewById(R.id.high_goal_scored_stat)).setText(teleopHighPoints + "");
		((TextView) root.findViewById(R.id.high_goal_scored_percent)).setText(100*teleopHighPoints/total + "%");
		if (teleopHighPoints == 0) ((View) root.findViewById(R.id.high_goal_scored_stat).getParent()).setAlpha(.5f);
		
		((TextView) root.findViewById(R.id.low_goal_scored_stat)).setText(teleopLowPoints + "");
		((TextView) root.findViewById(R.id.low_goal_scored_percent)).setText(100*teleopLowPoints/total + "%");
		if (teleopLowPoints == 0) ((View) root.findViewById(R.id.low_goal_scored_stat).getParent()).setAlpha(.5f);
		
		((TextView) root.findViewById(R.id.truss_scored_stat)).setText(trussPoints + "");
		((TextView) root.findViewById(R.id.truss_scored_percent)).setText(100*trussPoints/total + "%");
		if (trussPoints == 0) ((View) root.findViewById(R.id.truss_scored_stat).getParent()).setAlpha(.5f);

		((TextView) root.findViewById(R.id.catch_scored_stat)).setText(catchPoints + "");
		((TextView) root.findViewById(R.id.catch_scored_percent)).setText(100*catchPoints/total + "%");
		if (catchPoints == 0) ((View) root.findViewById(R.id.catch_scored_stat).getParent()).setAlpha(.5f);
		
		float[] distributionValues = {autonomousPoints, teleopHighPoints, teleopLowPoints, trussPoints, catchPoints};
		((RelativeLayout) root.findViewById(R.id.score_graph)).addView(new GraphView(getActivity(), distributionValues));

		int totalHighMade = teleopHighMade + highMade;
		int totalHigh = teleopHighTotal + highTotal;
		((TextView) root.findViewById(R.id.high_goal_accuracy_stat)).setText(totalHighMade + "/"+ totalHigh);
		((TextView) root.findViewById(R.id.high_goal_accuracy_percent)).setText(100*totalHighMade/totalHigh + "%");
		if (totalHigh == 0) ((View) root.findViewById(R.id.high_goal_accuracy_stat).getParent()).setAlpha(.5f);		

		int totalLowMade = teleopLowMade + lowMade;
		int totalLow = teleopLowTotal + lowTotal;
		((TextView) root.findViewById(R.id.low_goal_accuracy_stat)).setText(totalLowMade + "/"+ totalLow);
		((TextView) root.findViewById(R.id.low_goal_accuracy_percent)).setText(100*totalLowMade/totalLow + "%");
		if (totalLow == 0) ((View) root.findViewById(R.id.low_goal_accuracy_stat).getParent()).setAlpha(.5f);		

		((TextView) root.findViewById(R.id.truss_accuracy_stat)).setText(trussMade + "/"+ trussTotal);
		((TextView) root.findViewById(R.id.truss_accuracy_percent)).setText(100*trussMade/trussTotal + "%");
		if (trussTotal == 0) ((View) root.findViewById(R.id.truss_accuracy_stat).getParent()).setAlpha(.5f);		
		
		int totalMade = totalHighMade + totalLowMade + trussMade;
		int totalTaken = totalHigh + totalLow + trussTotal;
		((TextView) root.findViewById(R.id.overall_accuracy_stat)).setText(totalMade + "/"+ totalTaken);
		((TextView) root.findViewById(R.id.overall_accuracy_percent)).setText(100*totalMade/totalTaken + "%");	
		
		float[] accuracyValues = {totalMade, totalTaken-totalMade};
		((RelativeLayout) root.findViewById(R.id.accuracy_graph)).addView(new GraphView(getActivity(), accuracyValues));

		((TextView) root.findViewById(R.id.time_with_possession_stat)).setText(possessionTime + "s");
		((TextView) root.findViewById(R.id.time_with_possession_percent)).setText(((int) (100.0*possessionTime/140 + .5)) + "%");
		((TextView) root.findViewById(R.id.time_without_possession_stat)).setText(140-possessionTime + "s");
		((TextView) root.findViewById(R.id.time_without_possession_percent)).setText(((int) (100*(140.0-possessionTime)/140 + .5)) + "%");
		
		float[] timeValues = {possessionTime, 140-possessionTime};
		((RelativeLayout) root.findViewById(R.id.time_graph)).addView(new GraphView(getActivity(), timeValues));
		

		TextView noteView = (TextView) root.findViewById(R.id.notes);
		String notes = data[MatchScoutingIndex.NOTES].trim();
		if (notes.equals("")) {
			noteView.setText("No notes found.");
			noteView.setGravity(Gravity.CENTER);
			noteView.setTextColor(getActivity().getResources().getColor(android.R.color.tertiary_text_light));
		} else {
			noteView.setText(notes);
		}

		/*
		 * Set the "View More" button to switch to the additional information.
		 */
		root.findViewById(R.id.specfics_1).setOnClickListener(this);
		root.findViewById(R.id.specfics_2).setOnClickListener(this);
		root.findViewById(R.id.calcs_1).setOnClickListener(this);
		root.findViewById(R.id.calcs_2).setOnClickListener(this);
		root.findViewById(R.id.stats_1).setOnClickListener(this);
		root.findViewById(R.id.stats_2).setOnClickListener(this);
		return root;
	}

	private View getEmptyMatchLayout(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final LinearLayout root = (LinearLayout) inflater.inflate(R.layout.activity_team_match_review_empty_layout, container, false);
		TextView tv = (TextView) root.findViewById(R.id.match_number);
		tv.setText(matchNumber);
		Button b = (Button) root.findViewById(R.id.begin_scouting);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MatchScouting.start(getActivity(), teamNumber.trim(), matchNumber.trim());
			}
		});
		return root;
	}

	public MatchDataFragment setComp(Competition c) {
		currentComp = c;
		return this;
	}

	private void storeArguments() {
		teamNumber = getArguments().getString(TEAM_NUMBER_ID);
		matchNumber = getArguments().getString(MATCH_NUMBER_ID);
		data = getArguments().getStringArray(DATA_ID);
		matchInfo = getArguments().getStringArray(MATCH_INFO_ID);
	}

	@Override
	public void onClick(View arg0) {
		root.setInAnimation(getActivity(), R.anim.expand_in);
		root.setOutAnimation(getActivity(), R.anim.shrink_out);
		switch(arg0.getId()) {
		case R.id.calcs_1:
			root.setDisplayedChild(2);
			break;
		case R.id.calcs_2:
			root.setDisplayedChild(2);
			break;
		case R.id.specfics_1:
			root.setDisplayedChild(1);
			break;
		case R.id.specfics_2:
			root.setDisplayedChild(1);
			break;
		case R.id.stats_1:
			root.setDisplayedChild(0);
			break;
		case R.id.stats_2:
			root.setDisplayedChild(0);
			break;
		}
		
	}
}