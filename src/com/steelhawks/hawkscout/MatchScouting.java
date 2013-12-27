package com.steelhawks.hawkscout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.steelhawks.hawkscout.util.FixedCountDownTimer;
import com.steelhawks.hawkscout.util.NoDefaultSpinner;
import com.steelhawks.hawkscout.util.Utilities;

public class MatchScouting extends Activity implements OnClickListener, OnLongClickListener {
	
	private ViewFlipper mViewFlipper;
	private TextView mClock;
	private FixedCountDownTimer matchTimer;
	
	private static int AUTON = 47309;
	private static int TELEOP = 79034;
	private static int CLIMB = 14309;
	private static int CURRENT_GAME_MODE = AUTON;
	
	private int autonMissedShots = 0;
	private int autonTopGoals = 0;
	private int autonMiddleGoals = 0;
	private int autonBottomGoals = 0;
	private int teleOpBlocks = 0;
	private int teleOpMissedShots = 0;
	private int teleOpPyramidGoals = 0;
	private int teleOpTopGoals = 0;
	private int teleOpMiddleGoals = 0;
	private int teleOpBottomGoals = 0;
	private int threePointPenaltiesCounter = 0;
	private int twentyPointPenaltiesCounter = 0;
	private int thirtyPointPenaltiesCounter = 0;
	private double climbTime = 0;
	private int finalClimbLevel = 0;
	private boolean validClimb = true;
	private List<ClimbSplit> splits = new ArrayList<ClimbSplit>();
	
	private RelativeLayout blocks;
	private RelativeLayout missedShots;
	private RelativeLayout threePointPenalties;
	private RelativeLayout twentyPointPenalties;
	private RelativeLayout thirtyPointPenalties;
	private RelativeLayout pyramidGoals;
	private RelativeLayout pyramidGoals2;
	private RelativeLayout topGoals;
	private RelativeLayout middleGoals;
	private RelativeLayout bottomGoals;
	private Button level0;
	private Button level1;
	private Button level2;
	private Button level3;
	private LinearLayout climbTimeParentLayout;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_match_scouting);
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.match_scouting_view_flipper);
		mViewFlipper.setInAnimation(this, R.anim.in_from_right);
		
		mClock = (TextView) findViewById(R.id.match_scouting_clock);
 		mClock.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Clock.otf"));

		level0 = (Button) findViewById(R.id.level_0);
		level1 = (Button) findViewById(R.id.level_1);
		level2 = (Button) findViewById(R.id.level_2);
		level3 = (Button) findViewById(R.id.level_3);
		
		climbTimeParentLayout = (LinearLayout) findViewById(R.id.climb_time_parent);
		
		setupLayout();
		setupButtonClicks();
	}
	
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.start_match:Utilities.closeKeyboard(this);
			startMatch();
			break;
		case R.id.skip_to_tele:startTeleOp();
			break;
		case R.id.start_climbing:if (CURRENT_GAME_MODE != CLIMB) startClimb();
			else endMatch();
			break;
		case R.id.blocks:updateButtonText(blocks, ++teleOpBlocks);
			break;
		case R.id.missed_shots:
			if (CURRENT_GAME_MODE == AUTON) updateButtonText(missedShots, ++autonMissedShots);
			else updateButtonText(missedShots, ++teleOpMissedShots);
			break;
		case R.id.three_point_penalties:
			updateButtonText(threePointPenalties, ++threePointPenaltiesCounter);
			break;
		case R.id.twenty_point_penalties: 
			updateButtonText(twentyPointPenalties, ++twentyPointPenaltiesCounter);
			break;
		case R.id.thirty_point_penalties:
			updateButtonText(thirtyPointPenalties, ++thirtyPointPenaltiesCounter);
			break;
		case R.id.pyramid_goals:
			updateButtonText(pyramidGoals, ++teleOpPyramidGoals);
			break;
		case R.id.pyramid_goals2:
			updateButtonText(pyramidGoals2, ++teleOpPyramidGoals);
			break;
		case R.id.top_goals:
			if (CURRENT_GAME_MODE == AUTON) updateButtonText(topGoals, ++autonTopGoals);
			else updateButtonText(topGoals, ++teleOpTopGoals);
			break;
		case R.id.middle_goals:
			if (CURRENT_GAME_MODE == AUTON) updateButtonText(middleGoals, ++autonMiddleGoals);
			else updateButtonText(middleGoals, ++teleOpMiddleGoals);
			break;
		case R.id.bottom_goals:
			if (CURRENT_GAME_MODE == AUTON) updateButtonText(bottomGoals, ++autonBottomGoals);
			else updateButtonText(bottomGoals, ++teleOpBottomGoals);
			break;
		case R.id.valid_climb:
			((CheckedTextView) view).toggle();
			validClimb = !validClimb;
			recalculateFinalStats();
		}
	}
	
	public void editMatchScoutingReview (View view) {
		switch(view.getId()) {
		case R.id.autonomous_bottom_goals_increase: updateReviewText(view, ++autonBottomGoals);
			break;
		case R.id.autonomous_bottom_goals_decrease:	if (autonBottomGoals == 0) break;
			updateReviewText(view, --autonBottomGoals);
			break;
		case R.id.autonomous_middle_goals_increase:	updateReviewText(view, ++autonMiddleGoals);
			break;
		case R.id.autonomous_middle_goals_decrease:	if (autonMiddleGoals == 0) break;
			updateReviewText(view, --autonMiddleGoals);
			break;
		case R.id.autonomous_top_goals_increase:updateReviewText(view, ++autonTopGoals);
			break;
		case R.id.autonomous_top_goals_decrease: if (autonTopGoals == 0) break;
			updateReviewText(view, --autonTopGoals);
			break;
		case R.id.autonomous_missed_shots_increase:updateReviewText(view, ++autonMissedShots);
			break;
		case R.id.autonomous_missed_shots_decrease:	if (autonMissedShots == 0) break;
			updateReviewText(view, --autonMissedShots);
			break;
		case R.id.teleop_pyramid_goals_increase: updateReviewText(view, ++teleOpPyramidGoals);
			break;
		case R.id.teleop_pyramid_goals_decrease: if (teleOpPyramidGoals == 0) break;
			updateReviewText(view, --teleOpPyramidGoals);
			break;
		case R.id.teleop_bottom_goals_increase: updateReviewText(view, ++teleOpBottomGoals);
			break;
		case R.id.teleop_bottom_goals_decrease:	if (teleOpBottomGoals == 0) break;
			updateReviewText(view, --teleOpBottomGoals);
			break;
		case R.id.teleop_middle_goals_increase:	updateReviewText(view, ++teleOpMiddleGoals);
			break;
		case R.id.teleop_middle_goals_decrease:	if (teleOpMiddleGoals == 0) break;
			updateReviewText(view, --teleOpMiddleGoals);
			break;
		case R.id.teleop_top_goals_increase:updateReviewText(view, ++teleOpTopGoals);
			break;
		case R.id.teleop_top_goals_decrease:if (teleOpTopGoals == 0) break;
			updateReviewText(view, --teleOpTopGoals);
			break;
		case R.id.teleop_missed_shots_increase:updateReviewText(view, ++teleOpMissedShots);
			break;
		case R.id.teleop_missed_shots_decrease:if (teleOpMissedShots == 0) break;
			updateReviewText(view, --teleOpMissedShots);
			break;
		case R.id.teleop_blocks_increase:updateReviewText(view, ++teleOpBlocks);
			break;
		case R.id.teleop_blocks_decrease:if (teleOpBlocks == 0) break;
			updateReviewText(view, --teleOpBlocks);
			break;
		case R.id.three_point_penalties_increase:updateReviewText(view, ++threePointPenaltiesCounter);
			break;
		case R.id.three_point_penalties_decrease:if (threePointPenaltiesCounter == 0) break;
			updateReviewText(view, --threePointPenaltiesCounter);
			break;
		case R.id.twenty_point_penalties_increase:updateReviewText(view, ++twentyPointPenaltiesCounter);
			break;
		case R.id.twenty_point_penalties_decrease:if (twentyPointPenaltiesCounter == 0) break;
			updateReviewText(view, --twentyPointPenaltiesCounter);
			break;
		case R.id.thirty_point_penalties_increase:updateReviewText(view, ++thirtyPointPenaltiesCounter);
			break;
		case R.id.thirty_point_penalties_decrease:if (thirtyPointPenaltiesCounter == 0) break;
			updateReviewText(view, --thirtyPointPenaltiesCounter);
			break;
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.blocks:teleOpBlocks--;
			break;
		case R.id.missed_shots: if (CURRENT_GAME_MODE == AUTON || teleOpMissedShots == 0) autonMissedShots--;
			else teleOpMissedShots--;
			break;
		case R.id.three_point_penalties: threePointPenaltiesCounter--;
			break;
		case R.id.twenty_point_penalties: twentyPointPenaltiesCounter--;
			break;
		case R.id.thirty_point_penalties: thirtyPointPenaltiesCounter--;
			break;
		case R.id.pyramid_goals: teleOpPyramidGoals--;
			break;
		case R.id.top_goals: if (CURRENT_GAME_MODE == AUTON || teleOpTopGoals == 0) autonTopGoals--;
			else teleOpTopGoals--;
			break;
		case R.id.middle_goals: if (CURRENT_GAME_MODE == AUTON || teleOpMiddleGoals == 0) autonMiddleGoals--;
			else autonMiddleGoals--;
			break;
		case R.id.bottom_goals: if (CURRENT_GAME_MODE == AUTON || teleOpBottomGoals == 0) autonBottomGoals--;
			else autonBottomGoals--;
			break;	
		}
		return false;
	}
	
	int PX(int dp) {return Utilities.PX(this, dp);}
	
	void setupLayout() {		
		final ImageView pyramidView = (ImageView) findViewById(R.id.pyramid);
		final View robotView = findViewById(R.id.robot);
		pyramidView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (robotView.getVisibility() == View.GONE) robotView.setVisibility(View.VISIBLE);
				int x = (int) event.getX() - (PX(50)/2);
					if (x<0) x=0;
					if (x>PX(300)-PX(50)) x=PX(300)-PX(50);
				int y = (int) event.getY() - (PX(50)/2);
					if (y<0) y=0;
					if (y>PX(300)-PX(50)) y = PX(300)-PX(50);
				robotView.setX(x);
				robotView.setY(y);
				Utilities.closeKeyboard(MatchScouting.this);
				return true;
			}
			
		});
		
		NoDefaultSpinner alliance = (NoDefaultSpinner) findViewById(R.id.alliance);
		alliance.setPrompt("Select");
			String entries[] = {"Blue", "Red"};
			ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_textview, entries);
				a.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		alliance.setAdapter(a);
		alliance.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == 0) {
					pyramidView.setImageResource(R.drawable.ic_content_pyramid_blue);
					robotView.setBackgroundColor(Color.parseColor("#500000cc"));
				} else {
					pyramidView.setImageResource(R.drawable.ic_content_pyramid_red);
					robotView.setBackgroundColor(Color.parseColor("#50cc0000"));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		
	}
	
	private void setupButtonClicks() {
		blocks = (RelativeLayout) findViewById(R.id.blocks);
		missedShots = (RelativeLayout) findViewById(R.id.missed_shots);
		threePointPenalties = (RelativeLayout) findViewById(R.id.three_point_penalties);
		twentyPointPenalties = (RelativeLayout) findViewById(R.id.twenty_point_penalties);
		thirtyPointPenalties = (RelativeLayout) findViewById(R.id.thirty_point_penalties);
		pyramidGoals = (RelativeLayout) findViewById(R.id.pyramid_goals);
		pyramidGoals2 = (RelativeLayout) findViewById(R.id.pyramid_goals2);
		topGoals = (RelativeLayout) findViewById(R.id.top_goals);
		middleGoals = (RelativeLayout) findViewById(R.id.middle_goals);
		bottomGoals = (RelativeLayout) findViewById(R.id.bottom_goals);
		
		blocks.setOnClickListener(this);
		missedShots.setOnClickListener(this);
		threePointPenalties.setOnClickListener(this);
		twentyPointPenalties.setOnClickListener(this);
		thirtyPointPenalties.setOnClickListener(this);
		pyramidGoals.setOnClickListener(this);
		pyramidGoals2.setOnClickListener(this);
		topGoals.setOnClickListener(this);
		middleGoals.setOnClickListener(this);
		bottomGoals.setOnClickListener(this);
		
		blocks.setOnLongClickListener(this);
		missedShots.setOnLongClickListener(this);
		threePointPenalties.setOnLongClickListener(this);
		twentyPointPenalties.setOnLongClickListener(this);
		thirtyPointPenalties.setOnLongClickListener(this);
		pyramidGoals.setOnLongClickListener(this);
		pyramidGoals2.setOnClickListener(this);
		topGoals.setOnLongClickListener(this);
		middleGoals.setOnLongClickListener(this);
		bottomGoals.setOnLongClickListener(this);
	}
	
	private void updateButtonText(RelativeLayout parentLayout, int newText) {
		parentLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		TextView t = (TextView) parentLayout.getChildAt(1);
		t.setText("" + newText);
	}
	
	private void updateReviewText(View v, int newText) {
		EditText e = (EditText) ((RelativeLayout) v.getParent()).getChildAt(1);
		e.setText("" + newText);
	}
	
	private void recalculateFinalStats() {
		int points = autonTopGoals * 6 +
				autonMiddleGoals * 4 +
				autonBottomGoals * 2 +
				teleOpPyramidGoals * 5 +
				teleOpTopGoals * 3 +
				teleOpMiddleGoals * 2 +
				teleOpBottomGoals +
				(!validClimb ? 0 : finalClimbLevel * 10);
		((TextView) findViewById(R.id.total_points)).setText("" + points);
		
		int penalties = threePointPenaltiesCounter * 3 +
				twentyPointPenaltiesCounter * 20 +
				thirtyPointPenaltiesCounter * 30;
		((TextView) findViewById(R.id.total_penalties)).setText("" + penalties);

		((TextView) findViewById(R.id.net_points)).setText("" + (points-penalties));
	}
		
	private void startMatch() {
		getActionBar().hide();
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		mViewFlipper.showNext();
		
		//Disable non-auto buttons
		findViewById(R.id.blocks).setEnabled(false);
		findViewById(R.id.pyramid_goals).setEnabled(false);
		
		//Start timer
		matchTimer = new FixedCountDownTimer(15000, 1000) {
			@Override
			public void onStart() {
				mClock.setText("14");				
			}
			
			@Override
			public void onTick(long millisUntilFinished) {
				mClock.setText("" + ((millisUntilFinished/1000) - (millisUntilFinished % 1000 == 0 ? 1 : 0)));
			}
			
			@Override
			public void onFinish() {
				startTeleOp();
			}
			
		};
		matchTimer.start();
	}
	
	private void startTeleOp() {
		matchTimer.cancel();
		matchTimer = new FixedCountDownTimer(125000, 1000) {
			@Override
			public void onStart() {
				mClock.setText("124");				
			}
			
			@Override
			public void onTick(long millisUntilFinished) {
				mClock.setText("" + ((millisUntilFinished/1000) - (millisUntilFinished % 1000 == 0 ? 1 : 0)));
			}
			
			@Override
			public void onFinish() {
				endMatch();
			}
			
		};
		matchTimer.start();
		
		if (CURRENT_GAME_MODE == CLIMB) return;
		CURRENT_GAME_MODE = TELEOP;
		((TextView) findViewById(R.id.auton_text)).setTextColor(
				getResources().getColor(android.R.color.secondary_text_dark));
		((TextView) findViewById(R.id.tele_op_text)).setTextColor(getResources().getColor(android.R.color.black));
		
		findViewById(R.id.skip_to_tele).setVisibility(View.INVISIBLE);
		
		blocks.setEnabled(true);
		pyramidGoals.setEnabled(true);
		
		updateButtonText(missedShots, teleOpMissedShots);
		updateButtonText(topGoals, teleOpTopGoals);
		updateButtonText(middleGoals, teleOpMiddleGoals);
		updateButtonText(bottomGoals, teleOpBottomGoals);
	}
	
	private void startClimb() {
		CURRENT_GAME_MODE = CLIMB;
		((TextView) findViewById(R.id.tele_op_text)).setTextColor(
				getResources().getColor(android.R.color.secondary_text_dark));
		((TextView) findViewById(R.id.auton_text)).setTextColor(
				getResources().getColor(android.R.color.secondary_text_dark));
		((TextView) findViewById(R.id.climb_text)).setTextColor(getResources().getColor(android.R.color.black));
		
		((Button) findViewById(R.id.start_climbing)).setText("Finish Match + Review");
		findViewById(R.id.skip_to_tele).setVisibility(View.INVISIBLE);
		
		updateButtonText(pyramidGoals2, teleOpPyramidGoals);
		((ViewFlipper) findViewById(R.id.controls_switch)).showNext();		
		
		final Timer timer = new Timer();
		final TextView tV = (TextView) findViewById(R.id.climb_time);

		int maxWidth = getResources().getDisplayMetrics().widthPixels - Utilities.PX(this, 32);
		System.out.println("LEVEL 0 WIDTH:" + maxWidth);
		level3.setWidth(maxWidth/4);
		level2.setWidth(maxWidth/4*2);
		level1.setWidth(maxWidth/4*3);
		
		OnClickListener climbClick = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (v == level0) {
					if (splits.size() > 0) {
						splits.get(splits.size()-1).completeSplit(0, climbTime);
						climbTimeParentLayout.addView(splits.get(splits.size()-1).getSplitView());
					}
					finalClimbLevel = 0;
					splits.add(new ClimbSplit(0, climbTime));
					level1.setEnabled(true);
					level2.setEnabled(true);
					level3.setEnabled(true);
					level0.requestFocus();
					level0.setEnabled(false);
//					tV.setText("TOTAL CLIMB TIME:\n" + ((int)(climbTime*10))/10.0 + "s");
				} else if (v == level1) {
					finalClimbLevel = 1;
					splits.get(splits.size()-1).completeSplit(1, climbTime);
					climbTimeParentLayout.addView(splits.get(splits.size()-1).getSplitView());
					splits.add(new ClimbSplit(1, climbTime));
					level0.setEnabled(true);
					level2.setEnabled(true);
					level3.setEnabled(true);
					level1.requestFocus();
					level1.setEnabled(false);
//					tV.setText("TOTAL CLIMB TIME:\n" + ((int)(climbTime*10))/10.0 + "s");
				} else if (v == level2) {
					finalClimbLevel = 2;
					splits.get(splits.size()-1).completeSplit(2, climbTime);
					climbTimeParentLayout.addView(splits.get(splits.size()-1).getSplitView());
					splits.add(new ClimbSplit(2, climbTime));
					level0.setEnabled(true);
					level1.setEnabled(true);
					level3.setEnabled(true);
					level2.requestFocus();
					level2.setEnabled(false);
//					tV.setText("TOTAL CLIMB TIME:\n" + ((int)(climbTime*10))/10.0 + "s");
				} else if (v == level3) {
					finalClimbLevel = 3;
					splits.get(splits.size()-1).completeSplit(3, climbTime);
					climbTimeParentLayout.addView(splits.get(splits.size()-1).getSplitView());
					splits.add(new ClimbSplit(3, climbTime));
					level0.setEnabled(true);
					level1.setEnabled(true);
					level2.setEnabled(true);
					level3.requestFocus();
					level3.setEnabled(false);
//					tV.setText("TOTAL CLIMB TIME:\n" + ((int)(climbTime*10))/10.0 + "s");
				}
			}
		};
		level0.setOnClickListener(climbClick);
		level1.setOnClickListener(climbClick);
		level2.setOnClickListener(climbClick);
		level3.setOnClickListener(climbClick);
		
		level0.performClick();
	
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						climbTime += .1;
						climbTime = Math.round(climbTime*10)/10.0;
						if(splits.size()==1) tV.setText("TOTAL CLIMB TIME: " + ((int)(climbTime*10))/10.0 + "s");
						else tV.setText("TOTAL CLIMB TIME: " + splits.get(splits.size()-2).getFinalTime() +"s"
								+ " (" + climbTime + "s)");
					}
				});
			}
		};
		timer.scheduleAtFixedRate(task, 0, 100);
	}
	
	@SuppressWarnings("unchecked")
	private void endMatch() {
//		Toast.makeText(this, "End of Match", Toast.LENGTH_SHORT).show();
		getActionBar().show();
		mViewFlipper.showNext();
		matchTimer.cancel();

		((EditText) findViewById(R.id.match_number_review)).setText(
				((EditText) findViewById(R.id.match_number)).getText().toString());
		((EditText) findViewById(R.id.team_number_review)).setText(
				((EditText) findViewById(R.id.team_number)).getText().toString());
		((EditText) findViewById(R.id.scouted_by_review)).setText(
				((EditText) findViewById(R.id.scouted_by)).getText().toString());
		((EditText) findViewById(R.id.notes_review)).setText(
				((EditText) findViewById(R.id.notes)).getText().toString());
		
		String entries[] = {"Blue", "Red"};
		ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_textview, entries);
			a.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		((Spinner) findViewById(R.id.alliance_review)).setAdapter(a);
		int position = ((NoDefaultSpinner) findViewById(R.id.alliance)).getSelectedItemPosition();
		((Spinner) findViewById(R.id.alliance_review)).setSelection(1);
		if (position == 0) {
			((Spinner) findViewById(R.id.alliance_review)).setSelection(0);
			((ImageView) findViewById(R.id.pyramid_review)).setImageDrawable(
					getResources().getDrawable(R.drawable.ic_content_pyramid_blue));
			findViewById(R.id.robot_review).setBackgroundColor(Color.parseColor("#500000cc"));
		} else {
			
			((ImageView) findViewById(R.id.pyramid_review)).setImageDrawable(
					getResources().getDrawable(R.drawable.ic_content_pyramid_red));
			findViewById(R.id.robot_review).setBackgroundColor(Color.parseColor("#50cc0000"));
		}

		findViewById(R.id.robot_review).setX(findViewById(R.id.robot).getX());
		findViewById(R.id.robot_review).setY(findViewById(R.id.robot).getY());

		((EditText) findViewById(R.id.autonomous_top_goals_review)).setText("" + autonTopGoals);
		((EditText) findViewById(R.id.autonomous_middle_goals_review)).setText("" + autonMiddleGoals);
		((EditText) findViewById(R.id.autonomous_bottom_goals_review)).setText("" + autonBottomGoals);
		((EditText) findViewById(R.id.autonomous_missed_shots_review)).setText("" + autonMissedShots);
		((EditText) findViewById(R.id.autonomous_top_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.autonomous_top_goals_review));
		((EditText) findViewById(R.id.autonomous_middle_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.autonomous_middle_goals_review));
		((EditText) findViewById(R.id.autonomous_bottom_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.autonomous_bottom_goals_review));
		((EditText) findViewById(R.id.autonomous_missed_shots_review))
			.addTextChangedListener(new TextChangedListener(R.id.autonomous_missed_shots_review));

		((EditText) findViewById(R.id.teleop_pyramid_goals_review)).setText("" + teleOpPyramidGoals);
		((EditText) findViewById(R.id.teleop_top_goals_review)).setText("" + teleOpTopGoals);
		((EditText) findViewById(R.id.teleop_middle_goals_review)).setText("" + teleOpMiddleGoals);
		((EditText) findViewById(R.id.teleop_bottom_goals_review)).setText("" + teleOpBottomGoals);
		((EditText) findViewById(R.id.blocks_review)).setText("" + teleOpBlocks);
		((EditText) findViewById(R.id.teleop_missed_shots_review)).setText("" + teleOpMissedShots);
		((EditText) findViewById(R.id.teleop_pyramid_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.teleop_pyramid_goals_review));
		((EditText) findViewById(R.id.teleop_top_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.teleop_top_goals_review));
		((EditText) findViewById(R.id.teleop_middle_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.teleop_middle_goals_review));
		((EditText) findViewById(R.id.teleop_bottom_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.teleop_bottom_goals_review));
		((EditText) findViewById(R.id.blocks_review))
			.addTextChangedListener(new TextChangedListener(R.id.blocks_review));
		((EditText) findViewById(R.id.teleop_missed_shots_review))
			.addTextChangedListener(new TextChangedListener(R.id.teleop_missed_shots_review));

		((EditText) findViewById(R.id.three_point_penalties_review)).setText("" + threePointPenaltiesCounter);
		((EditText) findViewById(R.id.twenty_point_penalties_review)).setText("" + twentyPointPenaltiesCounter);
		((EditText) findViewById(R.id.thirty_point_penalties_review)).setText("" + thirtyPointPenaltiesCounter);
		((EditText) findViewById(R.id.three_point_penalties_review))
			.addTextChangedListener(new TextChangedListener(R.id.three_point_penalties_review));
		((EditText) findViewById(R.id.twenty_point_penalties_review))
			.addTextChangedListener(new TextChangedListener(R.id.twenty_point_penalties_review));
		((EditText) findViewById(R.id.thirty_point_penalties_review))
			.addTextChangedListener(new TextChangedListener(R.id.thirty_point_penalties_review));
		
		String[] levels = {"Level 0", "Level 1", "Level 2", "Level 3"};
		ArrayAdapter<String> b = new ArrayAdapter<String>(this, R.layout.spinner_textview, levels);
			b.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		((Spinner) findViewById(R.id.climb_level_review)).setAdapter(b);
		((Spinner) findViewById(R.id.climb_level_review)).setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				finalClimbLevel = arg2;
				recalculateFinalStats();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		((Spinner) findViewById(R.id.climb_level_review)).setSelection(0);
		((Spinner) findViewById(R.id.climb_level_review)).setSelection(finalClimbLevel);
		((CheckedTextView) findViewById(R.id.valid_climb)).setOnClickListener(this);
		((CheckedTextView) findViewById(R.id.valid_climb)).setChecked(true);
		
		double totalClimbTime = 0;
		LinearLayout splitsLayout = (LinearLayout) findViewById(R.id.level_splits_review);
		for (int x = 0; x<splits.size()-1; x++) {
			splitsLayout.addView(splits.get(x).getReviewView());
		}
		if (splits.size()>1) totalClimbTime = Math.round(splits.get(splits.size()-2).getFinalTime()*10)/10.0;
		((EditText) findViewById(R.id.climb_time_review)).setText("" + totalClimbTime);
		((EditText) findViewById(R.id.climb_time_review)).addTextChangedListener(new TextChangedListener(R.id.climb_time_review));
		
		recalculateFinalStats();
	}
	
	class ClimbSplit {
		int initialLevel;
		int finalLevel;
		double initialTime;
		double finalTime;
		double duration;
		
		ClimbSplit (int a, double b) {
			initialLevel = a;
			initialTime = b;
		}
		
		public void completeSplit(int c, double d) {
			finalLevel = c;
			finalTime = Math.round(d*10)/10.0;
			duration = d - initialTime;
			duration = Math.round(duration*10)/10.0;
			System.out.println("New Climb Split: " + initialLevel + " to " + finalLevel + " in " + duration);
		}
		
		public int getInitialLevel() {
			return initialLevel;
		}
		
		public int getFinalLevel() {
			return finalLevel;
		}
		
		public double getFinalTime() {
			return finalTime;
		}
		
		public double getDuration() {
			return duration;
		}
		
		public TextView getSplitView() {
			TextView v = new TextView(MatchScouting.this, null, android.R.style.TextAppearance_Small);
				v.setText("Level " + initialLevel + " to Level " + finalLevel + ": " + duration + "s");
				v.setTextColor(Color.parseColor("#50000000"));
			return v;
		}
		
		public TextView getReviewView() {
			TextView v = new TextView(MatchScouting.this, null, android.R.style.TextAppearance_Medium);
				v.setText("Level " + initialLevel + " to Level " + finalLevel + ": " + duration + "s");
				v.setGravity(Gravity.RIGHT);
			return v;
		}
	}
	
	class TextChangedListener implements TextWatcher {

		EditText view;
		int id;
		public TextChangedListener(int id) {
			this.id = id;
			this.view = (EditText) MatchScouting.this.findViewById(id);
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().equals("")) return;
			System.out.println(s.toString());
			int value = 0;
			if (!s.toString().contains(".")) value = Integer.valueOf(s.toString());
			switch(id) {
			case R.id.autonomous_top_goals_review: autonTopGoals = value; break;
			case R.id.autonomous_middle_goals_review: autonMiddleGoals = value; break;
			case R.id.autonomous_bottom_goals_review: autonBottomGoals = value; break;
			case R.id.autonomous_missed_shots_review: autonMissedShots = value; break;
			case R.id.teleop_pyramid_goals_review: teleOpPyramidGoals = value; break;
			case R.id.teleop_top_goals_review: teleOpTopGoals = value; break;
			case R.id.teleop_middle_goals_review: teleOpMiddleGoals = value; break;
			case R.id.teleop_bottom_goals_review: teleOpBottomGoals = value; break;
			case R.id.teleop_missed_shots_review: teleOpMissedShots = value; break;
			case R.id.blocks_review: teleOpBlocks = value; break;
			case R.id.three_point_penalties_review: threePointPenaltiesCounter = value;  break;
			case R.id.twenty_point_penalties_review: twentyPointPenaltiesCounter = value; break;
			case R.id.thirty_point_penalties_review: thirtyPointPenaltiesCounter = value; break;
			case R.id.climb_time_review: climbTime = Double.valueOf(s.toString()); break;
			}
			recalculateFinalStats();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {}
		
	}

}