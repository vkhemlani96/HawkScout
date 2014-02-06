package com.steelhawks.hawkscout;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.steelhawks.hawkscout.util.FixedCountDownTimer;
import com.steelhawks.hawkscout.util.NoDefaultSpinner;
import com.steelhawks.hawkscout.util.Utilities;

public class MatchScouting extends Activity implements OnClickListener, OnLongClickListener {

	private ViewFlipper mViewFlipper;
	private ViewFlipper controlsFlipper;
	private TextView mClock;
	private FixedCountDownTimer matchTimer;
	
	private static final int AUTON = 0;
	private static final int TELEOP_POSSESSION = 1;
	private static final int TELEOP_NO_POSSESSION = 2;
	private static int CURRENT_GAME_MODE = AUTON;
	public static final int SECONDS = 1000;
	
	class UndoKeys {
		public static final int AUTON_BALL_PICKUP = R.id.auton_ball_pickup;
		public static final int AUTON_FORWARD_MOVEMENT = R.id.auton_forward_movement;
		public static final int FOUL = -1;
		public static final int TECHNICAL_FOUL = -2;
		public static final int PASS_TO_HUMAN_PLAYER = R.id.human_player_pass;
		public static final int PASS_TO_ROBOT = R.id.robot_pass;
		public static final int LOST_BALL = R.id.lost_ball;
		public static final int TRUSS = R.id.truss;
		public static final int BLOCKED_SHOT = R.id.blocked_shot;
		public static final int DEFLECTION = R.id.deflection;
		public static final int PASS_FROM_HUMAN_PLAYER = R.id.pass_from_human_play;
		public static final int PASS_FROM_ROBOT = R.id.pass_from_robot;
		public static final int STRAY_BALL = R.id.stray_ball;
		public static final int CATCHES = R.id.catches;
		public static final int AUTON_HIGH_GOAL = -5;
		public static final int AUTON_HIGH_HOT = -6;
		public static final int AUTON_HIGH_MISSED = -7;
		public static final int TELEOP_HIGH = -8;
		public static final int TELEOP_HIGH_MISSED = -9;
		public static final int AUTON_LOW_GOAL = -10;
		public static final int AUTON_LOW_HOT = -11;
		public static final int AUTON_LOW_MISSED = -12;
		public static final int TELEOP_LOW = -13;
		public static final int TELEOP_LOW_MISSED = -14;
		public static final int AUTON_FOUL = -3;
		public static final int AUTON_TECHNICAL_FOUL = -4;
		//TODO
	}
	
	List<Integer> undoList = new ArrayList<Integer>();
	
	private boolean initialPossession = false;
	private int autonHighGoal = 0;
	private int autonHighHot = 0;
	private int autonHighTotal = 0;
	private int autonLowGoal = 0;
	private int autonLowHot = 0;
	private int autonLowTotal = 0;
	private int autonBallPickup = 0;
	private boolean autonForwardMovement = false;
	private int autonFouls = 0;
	private int fouls;
	private int autonTechFouls = 0;
	private int techFouls;
	private int teleopHighGoal = 0;
	private int teleopHighGoalTotal = 0;
	private int teleopLowGoal = 0;
	private int teleopLowGoalTotal = 0;
	private int passToHP = 0;
	private int passToRobot = 0;
	private int ballsLost = 0;
	private int trussPoints = 0;
	private int blocks = 0;
	private int deflections = 0;
	private int passFromHP = 0;
	private int passFromRobot = 0;
	private int ballPickup = 0;
	private int catches;
	
	private RelativeLayout autonHighGoalLayout;
	private RelativeLayout autonLowGoalLayout;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_match_scouting);
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.match_scouting_view_flipper);
		mViewFlipper.setInAnimation(this, R.anim.in_from_right);
		
		controlsFlipper = (ViewFlipper) findViewById(R.id.controls_switch);
		controlsFlipper.setInAnimation(this, R.anim.in_from_right);
		controlsFlipper.setOutAnimation(this, R.anim.out_to_left);
		
		mClock = (TextView) findViewById(R.id.match_scouting_clock);
 		mClock.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Clock.otf"));
		
		setupLayout();
		setupButtonClicks();
	}
	
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.start_match:
			Utilities.closeKeyboard(this);
			startMatch();
			return;
		case R.id.auton_ball_pickup:
			updateButtonText(view, ++autonBallPickup);
			undoList.add(UndoKeys.AUTON_BALL_PICKUP);
			return;
		case R.id.auton_forward_movement:
			setForwardMovement(view, true);
			undoList.add(UndoKeys.AUTON_FORWARD_MOVEMENT);
			return;
		case R.id.foul_button:
			if (CURRENT_GAME_MODE == AUTON) {
				updateButtonText(view, ++autonFouls);
				undoList.add(UndoKeys.AUTON_FOUL);
			}
			else  {
				updateButtonText(view, ++fouls + autonFouls);
				undoList.add(UndoKeys.FOUL);
			}
			return;
		case R.id.technical_foul_button:
			if (CURRENT_GAME_MODE == AUTON) {
				updateButtonText(view, ++autonTechFouls);
				undoList.add(UndoKeys.AUTON_TECHNICAL_FOUL);
			}
			else {
				updateButtonText(view, ++techFouls + autonTechFouls);
				undoList.add(UndoKeys.TECHNICAL_FOUL);
			}
			return;
		case R.id.teleop_low_goal:
			updateButtonText(view, true);
			undoList.add(UndoKeys.TELEOP_LOW);
			break;
		case R.id.teleop_high_goal:
			updateButtonText(view, true);
			undoList.add(UndoKeys.TELEOP_HIGH);
			break;
		case R.id.human_player_pass:
			updateButtonText(view, ++passToHP);
			undoList.add(UndoKeys.PASS_TO_HUMAN_PLAYER);
			break;
		case R.id.robot_pass:
			updateButtonText(view, ++passToRobot);
			undoList.add(UndoKeys.PASS_TO_ROBOT);
			break;
		case R.id.lost_ball:
			updateButtonText(view, ++ballsLost);
			undoList.add(UndoKeys.LOST_BALL);
			break;
		case R.id.truss:
			updateButtonText(view, ++trussPoints);
			undoList.add(UndoKeys.TRUSS);
			break;
		case R.id.blocked_shot:
			updateButtonText(view, ++blocks);
			undoList.add(UndoKeys.BLOCKED_SHOT);
			return;
		case R.id.deflection:
			updateButtonText(view, ++deflections);
			undoList.add(UndoKeys.DEFLECTION);
			return;
		case R.id.pass_from_human_play:
			updateButtonText(view, ++passFromHP);
			undoList.add(UndoKeys.PASS_FROM_HUMAN_PLAYER);
			break;
		case R.id.pass_from_robot:
			updateButtonText(view, ++passFromRobot);
			undoList.add(UndoKeys.PASS_FROM_ROBOT);
			break;
		case R.id.stray_ball:
			updateButtonText(view, ++ballPickup);
			undoList.add(UndoKeys.STRAY_BALL);
			break;
		case R.id.catches:
			updateButtonText(view, ++catches);
			undoList.add(UndoKeys.CATCHES);
			break;
		case R.id.beginning_possession:
			((CheckedTextView) view).toggle();
			initialPossession = ((CheckedTextView) view).isChecked();
			return;
		case R.id.undo:
			undo();
			return;
		}
		toggleControls();
	}
	
	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
		case R.id.auton_high_goal:
			updateButtonText(view, autonHighGoal, ++autonHighTotal, autonHighHot);
			undoList.add(UndoKeys.AUTON_HIGH_MISSED);
			break;
		case R.id.auton_low_goal:
			updateButtonText(view, autonLowGoal, ++autonLowTotal, autonLowHot);
			undoList.add(UndoKeys.AUTON_LOW_MISSED);
			break;
		case R.id.teleop_high_goal:
			updateButtonText(view, false);
			undoList.add(UndoKeys.TELEOP_HIGH_MISSED);
			break;
		case R.id.teleop_low_goal:
			updateButtonText(view, false);
			undoList.add(UndoKeys.TELEOP_HIGH_MISSED);
			break;
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		this.recreate();
	}

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
		autonHighGoalLayout = (RelativeLayout) findViewById(R.id.auton_high_goal);
		autonLowGoalLayout = (RelativeLayout) findViewById(R.id.auton_low_goal);
		
	
		OnClickListener highHotGoalListener = new OnClickListener() {
			boolean singleClick = false;
			TextView name = (TextView) autonHighGoalLayout.getChildAt(0);
			@Override
			public void onClick(View arg0) {
				if (singleClick) {
					name.setText(R.string.high_goal);
					updateButtonText(autonHighGoalLayout, autonHighGoal, autonHighTotal, ++autonHighHot);
					undoList.add(UndoKeys.AUTON_HIGH_HOT);
				} else {
					singleClick = true;
					updateButtonText(autonHighGoalLayout, ++autonHighGoal, ++autonHighTotal, autonHighHot);
					undoList.add(UndoKeys.AUTON_HIGH_GOAL);
					name.setText("Hot?");
					new Handler().postDelayed(new Runnable() {
						public void run() {
							singleClick = false;
							name.setText(R.string.high_goal);
						}
					}, 250);
				}
			}
		};
		OnClickListener lowHotGoalListener = new OnClickListener() {
			boolean singleClick = false;
			TextView name = (TextView) autonLowGoalLayout.getChildAt(0);
			@Override
			public void onClick(View arg0) {
				if (singleClick) {
					name.setText(R.string.low_goal);
					updateButtonText(autonLowGoalLayout, autonLowGoal, autonLowTotal, ++autonLowHot);
					undoList.add(UndoKeys.AUTON_LOW_HOT);
				} else {
					singleClick = true;
					updateButtonText(autonLowGoalLayout, ++autonLowGoal, ++autonLowTotal, autonLowHot);
					undoList.add(UndoKeys.AUTON_LOW_GOAL);
					name.setText("Hot?");
					new Handler().postDelayed(new Runnable() {
						public void run() {
							singleClick = false;
							name.setText(R.string.low_goal);
						}
					}, 250);
				}
			}
		};
	
		autonHighGoalLayout.setOnClickListener(highHotGoalListener);
		autonLowGoalLayout.setOnClickListener(lowHotGoalListener);
		
		int[] buttonIDs = {
				R.id.auton_high_goal,
				R.id.auton_low_goal,	
				R.id.teleop_low_goal,				
				R.id.teleop_high_goal,				
				R.id.auton_forward_movement,
				R.id.auton_ball_pickup,
				R.id.foul_button,
				R.id.technical_foul_button,
				R.id.human_player_pass,
				R.id.robot_pass,
				R.id.lost_ball,
				R.id.truss,
				R.id.blocked_shot,
				R.id.deflection,
				R.id.pass_from_human_play,
				R.id.pass_from_robot,
				R.id.stray_ball,
				R.id.catches,
				R.id.beginning_possession
		};
		for (int i = 2; i<buttonIDs.length; i++) findViewById(buttonIDs[i]).setOnClickListener(this);
		for (int i = 0; i<4; i++) findViewById(buttonIDs[i]).setOnLongClickListener(this);
	}

	private void startMatch() {
			getActionBar().hide();
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			mViewFlipper.showNext();
			
			//Start timer
			matchTimer = new FixedCountDownTimer(10*SECONDS, 1*SECONDS) {
				@Override
				public void onStart() {
					mClock.setText("9");				
				}
				
				@Override
				public void onTick(long millisUntilFinished) {
					mClock.setText("" + ((millisUntilFinished/1000) - (millisUntilFinished % 1000 == 0 ? 1 : 0)));
				}
				
				@Override
				public void onFinish() {
					cancel();
					startTeleOp();
				}
				
			};
			matchTimer.start();
		}

	private void startTeleOp() {
		matchTimer = new FixedCountDownTimer(140*SECONDS, 1000) {
			@Override
			public void onStart() {
				mClock.setText("139");				
			}
			@Override
			public void onTick(long millisUntilFinished) {
				mClock.setText("" + ((millisUntilFinished/1000) - (millisUntilFinished % 1000 == 0 ? 1 : 0)));
			}
			@Override
			public void onFinish() {
//				endMatch();
			}
		}.start();
		
		int autonPossessionCounter = (initialPossession ? 1 : 0) + autonBallPickup;
		int autonShotCounter = autonHighTotal + autonLowTotal;
		boolean initTeleOpPossession = autonShotCounter < autonPossessionCounter;
		if (initTeleOpPossession) controlsFlipper.showNext();
		else controlsFlipper.setDisplayedChild(2);
		
		CURRENT_GAME_MODE = initTeleOpPossession ? TELEOP_POSSESSION : TELEOP_NO_POSSESSION;
		((TextView) findViewById(R.id.auton_text)).setTextColor(
				getResources().getColor(android.R.color.secondary_text_dark));
		((TextView) findViewById(R.id.tele_op_text)).setTextColor(getResources().getColor(android.R.color.black));
	}
	
	private void toggleControls() {
		if (CURRENT_GAME_MODE == TELEOP_POSSESSION) {
			CURRENT_GAME_MODE = TELEOP_NO_POSSESSION;
			controlsFlipper.setInAnimation(this, R.anim.in_from_right);
			controlsFlipper.setOutAnimation(this, R.anim.out_to_left);
			controlsFlipper.showNext();
		} else {
			CURRENT_GAME_MODE = TELEOP_POSSESSION;
			controlsFlipper.setInAnimation(this, R.anim.in_from_left);
			controlsFlipper.setOutAnimation(this, R.anim.out_to_right);
			controlsFlipper.showPrevious();
		}
	}

	private void updateButtonText(View parentLayout, int newText) {
		parentLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		TextView t = (TextView) ((ViewGroup) parentLayout).getChildAt(1);
		t.setText("" + newText);
	}

	private void updateButtonText(View parentLayout, int newText, int totalText, int newHotText) {
		parentLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		TextView t = (TextView) ((ViewGroup) parentLayout).getChildAt(1);
		t.setText(newText + " / " + totalText + " (" + newHotText + " HOT)");
	}

	private void updateButtonText(View parentLayout, boolean madeShot) {
		parentLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		TextView t = (TextView) ((ViewGroup) parentLayout).getChildAt(1);
		if (parentLayout.getId() == R.id.teleop_high_goal) {
			if (madeShot) teleopHighGoal++;
			t.setText("" + teleopHighGoal + " / " + ++teleopHighGoalTotal);
		} else {
			if (madeShot) teleopLowGoal++;
			t.setText("" + teleopLowGoal + " / " + ++teleopLowGoalTotal);
		}
	}
	
	private void updateButtonText(View parentLayout, int x, int outOf) {
		TextView t = (TextView) ((ViewGroup) parentLayout).getChildAt(1);
			t.setText("" + x + " / " + outOf);
	}

	private void setForwardMovement(View view, boolean completed) {
		if (completed) {
			autonForwardMovement = true;
			TextView button = (TextView) view;
			button.setEnabled(false);
			button.setText(R.string.moved_forward);
		} else {
			autonForwardMovement = false;
			TextView button = (TextView) view;
			button.setEnabled(true);
			button.setText(R.string.forward_movement);
		}
	}
	
	private void undo() {
		int key = undoList.get(undoList.size()-1);
		View view;
		if (key == UndoKeys.FOUL || key == UndoKeys.AUTON_FOUL)
			view = findViewById(R.id.foul_button);
		else if (key == UndoKeys.TECHNICAL_FOUL || key == UndoKeys.AUTON_TECHNICAL_FOUL)
			view = findViewById(R.id.technical_foul_button);
		else if (key == UndoKeys.AUTON_HIGH_GOAL || key == UndoKeys.AUTON_HIGH_HOT || key == UndoKeys.AUTON_HIGH_MISSED)
			view = findViewById(R.id.auton_high_goal);
		else if (key == UndoKeys.AUTON_LOW_GOAL || key == UndoKeys.AUTON_LOW_HOT || key == UndoKeys.AUTON_LOW_MISSED)
			view = findViewById(R.id.auton_low_goal);
		else if (key == UndoKeys.TELEOP_HIGH || key == UndoKeys.TELEOP_HIGH_MISSED)
			view = findViewById(R.id.teleop_high_goal);
		else if (key == UndoKeys.TELEOP_LOW || key == UndoKeys.TELEOP_LOW_MISSED)
			view = findViewById(R.id.teleop_low_goal);
		else view = findViewById(key);
		switch(key) {
		case UndoKeys.AUTON_BALL_PICKUP:
			updateButtonText(view, --autonBallPickup);
			break;
		case UndoKeys.AUTON_FORWARD_MOVEMENT:
			setForwardMovement(view, false);
			break;
		case UndoKeys.FOUL:
			updateButtonText(view, --fouls + autonFouls);
			break;
		case UndoKeys.TECHNICAL_FOUL:
			updateButtonText(view, --techFouls + autonTechFouls);
			break;
		case UndoKeys.TELEOP_LOW:
			updateButtonText(view, --teleopLowGoal, --teleopLowGoalTotal);
			toggleControls();
			break;
		case UndoKeys.TELEOP_HIGH:
			updateButtonText(view, --teleopHighGoal, --teleopHighGoalTotal);
			toggleControls();
			break;
		case UndoKeys.PASS_TO_HUMAN_PLAYER:
			updateButtonText(view, --passToHP);
			toggleControls();
			break;
		case UndoKeys.PASS_TO_ROBOT:
			updateButtonText(view, --passToRobot);
			toggleControls();
			break;
		case UndoKeys.LOST_BALL:
			updateButtonText(view, --ballsLost);
			toggleControls();
			break;
		case UndoKeys.TRUSS:
			updateButtonText(view, --trussPoints);
			toggleControls();
			break;
		case UndoKeys.BLOCKED_SHOT:
			updateButtonText(view, --blocks);
			break;
		case UndoKeys.DEFLECTION:
			updateButtonText(view, --deflections);
			break;
		case UndoKeys.PASS_FROM_HUMAN_PLAYER:
			updateButtonText(view, --passFromHP);
			toggleControls();
			break;
		case UndoKeys.PASS_FROM_ROBOT:
			updateButtonText(view, --passFromRobot);
			toggleControls();
			break;
		case UndoKeys.STRAY_BALL:
			updateButtonText(view, --ballPickup);
			toggleControls();
			break;
		case UndoKeys.CATCHES:
			updateButtonText(view, --catches);
			toggleControls();
			break;
		case UndoKeys.AUTON_HIGH_GOAL:
			updateButtonText(view, --autonHighGoal, --autonHighTotal, autonHighHot);
			break;
		case UndoKeys.AUTON_LOW_GOAL:
			updateButtonText(view, --autonLowGoal, --autonLowTotal, autonLowHot);
			break;
		case UndoKeys.AUTON_HIGH_HOT:
			updateButtonText(view, --autonHighGoal, --autonHighTotal, --autonHighHot);
			break;
		case UndoKeys.AUTON_LOW_HOT:
			updateButtonText(view, --autonLowGoal, --autonLowTotal, --autonLowHot);
			break;
		case UndoKeys.AUTON_HIGH_MISSED:
			updateButtonText(view, autonHighGoal, --autonHighTotal, autonHighHot);
			break;
		case UndoKeys.AUTON_LOW_MISSED:
			updateButtonText(view, autonLowGoal, --autonLowTotal, autonLowHot);
			break;
		case UndoKeys.TELEOP_HIGH_MISSED:
			updateButtonText(view, teleopHighGoal, --teleopHighGoalTotal);
			break;
		case UndoKeys.TELEOP_LOW_MISSED:
			updateButtonText(view, teleopLowGoal, --teleopLowGoalTotal);
			break;
		case UndoKeys.AUTON_FOUL:
			updateButtonText(view, --autonFouls);
			break;
		case UndoKeys.AUTON_TECHNICAL_FOUL:
			updateButtonText(view, --autonTechFouls);
			break;
		}
		undoList.remove(undoList.size()-1);
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

//		((EditText) findViewById(R.id.autonomous_top_goals_review)).setText("" + autonTopGoals);
//		((EditText) findViewById(R.id.autonomous_middle_goals_review)).setText("" + autonMiddleGoals);
//		((EditText) findViewById(R.id.autonomous_bottom_goals_review)).setText("" + autonBottomGoals);
//		((EditText) findViewById(R.id.autonomous_missed_shots_review)).setText("" + autonMissedShots);
		((EditText) findViewById(R.id.autonomous_top_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.autonomous_top_goals_review));
		((EditText) findViewById(R.id.autonomous_middle_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.autonomous_middle_goals_review));
		((EditText) findViewById(R.id.autonomous_bottom_goals_review))
			.addTextChangedListener(new TextChangedListener(R.id.autonomous_bottom_goals_review));
		((EditText) findViewById(R.id.autonomous_missed_shots_review))
			.addTextChangedListener(new TextChangedListener(R.id.autonomous_missed_shots_review));

//		((EditText) findViewById(R.id.teleop_pyramid_goals_review)).setText("" + teleOpPyramidGoals);
//		((EditText) findViewById(R.id.teleop_pyramid_goals_review))
//			.addTextChangedListener(new TextChangedListener(R.id.teleop_pyramid_goals_review));
				
		recalculateFinalStats();
	}

	public void editMatchScoutingReview (View view) {
		switch(view.getId()) {
//		case R.id.autonomous_middle_goals_increase:	updateReviewText(view, ++autonMiddleGoals);
//			break;
//		case R.id.autonomous_middle_goals_decrease:	if (autonMiddleGoals == 0) break;
//			updateReviewText(view, --autonMiddleGoals);
//			break;
		}
	}
	
	private void updateReviewText(View v, int newText) {
		EditText e = (EditText) ((RelativeLayout) v.getParent()).getChildAt(1);
		e.setText("" + newText);
	}

	private void recalculateFinalStats() {
	//		int points = autonTopGoals * 6 +
	//				autonMiddleGoals * 4 +
	//				autonBottomGoals * 2 +
	//				teleOpPyramidGoals * 5 +
	//				teleOpTopGoals * 3 +
	//				teleOpMiddleGoals * 2 +
	//				teleOpBottomGoals +
	//				(!validClimb ? 0 : finalClimbLevel * 10);
	//		((TextView) findViewById(R.id.total_points)).setText("" + points);
	//		
	//		int penalties = threePointPenaltiesCounter * 3 +
	//				twentyPointPenaltiesCounter * 20 +
	//				thirtyPointPenaltiesCounter * 30;
	//		((TextView) findViewById(R.id.total_penalties)).setText("" + penalties);
	//
	//		((TextView) findViewById(R.id.net_points)).setText("" + (points-penalties));
		}

	int PX (int dp) {return Utilities.PX(this, dp);}
	
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
//			case R.id.autonomous_top_goals_review: autonTopGoals = value; break;
//			case R.id.autonomous_middle_goals_review: autonMiddleGoals = value; break;
//			case R.id.autonomous_bottom_goals_review: autonBottomGoals = value; break;
//			case R.id.autonomous_missed_shots_review: autonMissedShots = value; break;
//			case R.id.teleop_pyramid_goals_review: teleOpPyramidGoals = value; break;
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