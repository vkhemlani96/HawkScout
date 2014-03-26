package com.steelhawks.hawkscout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.steelhawks.hawkscout.data.Competition;
import com.steelhawks.hawkscout.data.Indices.MatchIndex;
import com.steelhawks.hawkscout.dialogs.SimpleTextFragment;
import com.steelhawks.hawkscout.util.FixedCountDownTimer;
import com.steelhawks.hawkscout.util.NoDefaultSpinner;
import com.steelhawks.hawkscout.util.ReviewLayout;
import com.steelhawks.hawkscout.util.Utilities;

public class MatchScouting extends FragmentActivity implements OnClickListener, OnLongClickListener {

	private ViewFlipper mViewFlipper;
	private ViewFlipper controlsFlipper;
	private TextView mClock;
	private FixedCountDownTimer matchTimer;
	private Menu menu;

	private static final int AUTON = 0;
	private static final int TELEOP_POSSESSION = 1;
	private static final int TELEOP_NO_POSSESSION = 2;
	private static int CURRENT_GAME_MODE = AUTON;
	public static final int SECONDS = 1000;
	private static String ALLIANCE = "";
	private static final String ACTIVITY_INTENT_1 = "com.steelhawks.hawkscout.MatchScouting.TEAM_NUMBER";
	private static final String ACTIVITY_INTENT_2 = "com.steelhawks.hawkscout.MatchScouting.MATCH_NUMBER";
	private boolean displayMenuItem = false;

	class UndoKeys {
		public static final int AUTON_BALL_PICKUP = R.id.auton_ball_pickup;
		public static final int AUTON_FORWARD_MOVEMENT = R.id.auton_forward_movement;
		public static final int AUTON_BLOCKS = R.id.auton_blocked_shot;
		public static final int FOUL = -1;
		public static final int TECHNICAL_FOUL = -2;
		public static final int PASS_TO_HUMAN_PLAYER = R.id.human_player_pass;
		public static final int PASS_TO_ROBOT = R.id.robot_pass;
		public static final int LOST_BALL = R.id.lost_ball;
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
		public static final int TRUSS = -15;
		public static final int TRUSS_MISSED = -16;
	}

	List<Integer> undoList = new ArrayList<Integer>();

	private boolean showedUp = true;
	private boolean initialPossession = false;
	private boolean startsInGoalie = false;
	private int autonBlocks = 0;
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
	private int trussPoints = 0;
	private int trussTotal = 0;
	private int passToHP = 0;
	private int passToRobot = 0;
	private int ballsLost = 0;
	private int blocks = 0;
	private int deflections = 0;
	private int passFromHP = 0;
	private int passFromRobot = 0;
	private int ballPickup = 0;
	private int catches;
	private List<Possession> possessions = new ArrayList<Possession>();
	private OnTouchListener pyramidTouch;

	private RelativeLayout autonHighGoalLayout;
	private RelativeLayout autonLowGoalLayout;

	ReviewLayout[][] reviewLayouts;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_match_scouting);
		Competition currentComp = new Competition(this, "SCMB");

		mViewFlipper = (ViewFlipper) findViewById(R.id.match_scouting_view_flipper);
		mViewFlipper.setInAnimation(this, R.anim.in_from_right);

		controlsFlipper = (ViewFlipper) findViewById(R.id.controls_switch);
		controlsFlipper.setInAnimation(this, R.anim.in_from_right);
		controlsFlipper.setOutAnimation(this, R.anim.out_to_left);

		mClock = (TextView) findViewById(R.id.match_scouting_clock);
		mClock.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Clock.otf"));		

		setupLayout();
		setupButtonClicks();

		String teamNumber = getIntent().getExtras().getString(ACTIVITY_INTENT_1, "");
		((EditText) findViewById(R.id.team_number)).setText(teamNumber);
		String matchNumber = getIntent().getExtras().getString(ACTIVITY_INTENT_2, "");
		if (matchNumber.equals("")) return;
		System.out.println("The match number is: " + matchNumber + ".");
		((EditText) findViewById(R.id.match_number)).setText(matchNumber);
		String[] matchInfo = currentComp.getMatchInfoByNumber("23");
		if (matchInfo == null) System.out.println("matchInfo is null");

		if (matchInfo[MatchIndex.BLUE1].trim().equals(teamNumber.trim()) ||
				matchInfo[MatchIndex.BLUE2].trim().equals(teamNumber.trim()) ||
				matchInfo[MatchIndex.BLUE3].trim().equals(teamNumber.trim()))
			((NoDefaultSpinner) findViewById(R.id.alliance)).setSelection(0);
		else ((NoDefaultSpinner) findViewById(R.id.alliance)).setSelection(1);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.match_scouting_menu, menu);
		this.menu = menu;
		menu.findItem(R.id.submit).setVisible(displayMenuItem);
		return true;
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
		case R.id.auton_blocked_shot:
			updateButtonText(view, ++autonBlocks);
			undoList.add(UndoKeys.AUTON_BLOCKS);
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
		case R.id.truss:
			updateButtonText(view, true);
			undoList.add(UndoKeys.TRUSS);
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
		case R.id.beginning_possession_review:
			((CheckedTextView) view).toggle();
			initialPossession = ((CheckedTextView) view).isChecked();
			return;
		case R.id.showed_up:
			((CheckedTextView) view).toggle();
			showedUp = ((CheckedTextView) view).isChecked();
			return;
		case R.id.showed_up_review:
			((CheckedTextView) view).toggle();
			showedUp = ((CheckedTextView) view).isChecked();
			return;
		case R.id.undo:
			undo();
			return;
		}
		toggleControls(false);
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
			toggleControls(false);
			break;
		case R.id.teleop_low_goal:
			updateButtonText(view, false);
			undoList.add(UndoKeys.TELEOP_HIGH_MISSED);
			toggleControls(false);
			break;
		case R.id.truss:
			updateButtonText(view, false);
			undoList.add(UndoKeys.TRUSS_MISSED);
			toggleControls(false);
			break;
		}
		return true;
	}

	public static float convertPixelsToDp(float px, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	void setupLayout() {
		//TODO
		final View robotView = findViewById(R.id.robot);
		pyramidTouch = new OnTouchListener(){
			private final int VERTICAL_PADDING = PX(250-182)/2;
			private final int FIELD_HEIGHT = PX(182);
			private final int ROBOT_DIMENSION = PX(20);
			private final int EDGE_BUFFER = PX(35);
			private final int ZONE_WIDTH = 533/3;
			private final int FIELD_WIDTH = 533;
			private final int CENTER_OFFSET = PX(10);
			private final int LOW_GOAL_DIMENSION = PX(19);

			private final int BLUE_GOALIE_ZONE = 0;
			private final int LEFT_WHITE_ZONE = 1;
			private final int RIGHT_WHITE_ZONE = 2;
			private final int RED_GOALIE_ZONE = 3;

			private int lastZone = -1;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX() - CENTER_OFFSET;
				int y = (int) event.getY() - CENTER_OFFSET;

				boolean allianceIsSelected = ALLIANCE.equals("Red") || ALLIANCE.equals("Blue");
				boolean isLeftOfField = x<0-CENTER_OFFSET;
				boolean isRightOfField = x>PX(400)-CENTER_OFFSET-ROBOT_DIMENSION;
				boolean isInBlueGoalieZone = x<=LOW_GOAL_DIMENSION;
				boolean isInBlueZone = x>LOW_GOAL_DIMENSION && x<ZONE_WIDTH;
				boolean isInLeftWhiteZone = x>=ZONE_WIDTH && x<=FIELD_WIDTH/2;
				boolean isInRightWhiteZone = x>=FIELD_WIDTH/2 + CENTER_OFFSET - ROBOT_DIMENSION && x<=ZONE_WIDTH*2 - ROBOT_DIMENSION + CENTER_OFFSET;
				boolean isInRedZone = x>ZONE_WIDTH*2 && x<FIELD_WIDTH-LOW_GOAL_DIMENSION;
				boolean isInRedGoalieZone = x>=FIELD_WIDTH - LOW_GOAL_DIMENSION + CENTER_OFFSET;
				boolean isBlueGoalieEdge = x>=LOW_GOAL_DIMENSION && x <=LOW_GOAL_DIMENSION + EDGE_BUFFER && lastZone == BLUE_GOALIE_ZONE;
				boolean isLeftWhiteZoneEdge = x>=ZONE_WIDTH-EDGE_BUFFER && x<=ZONE_WIDTH && lastZone == LEFT_WHITE_ZONE;
				boolean isRightWhiteZoneEdge = x>=ZONE_WIDTH*2 && x<=ZONE_WIDTH*2+EDGE_BUFFER && lastZone == RIGHT_WHITE_ZONE;
				boolean isRedGoalieEdge = x>=FIELD_WIDTH-LOW_GOAL_DIMENSION-EDGE_BUFFER && x<=FIELD_WIDTH-LOW_GOAL_DIMENSION && lastZone == RED_GOALIE_ZONE;
				boolean isEdge = isBlueGoalieEdge || isLeftWhiteZoneEdge || isRightWhiteZoneEdge || isRedGoalieEdge;

				startsInGoalie = isInBlueGoalieZone || isInRedGoalieZone || isEdge;

				if (isLeftOfField) x=0;
				if (isRightOfField) x=FIELD_WIDTH - ROBOT_DIMENSION; 
				if ((isInBlueZone || isInRedZone) && !isEdge) return true;
				if ((ALLIANCE.equals("Red") || !allianceIsSelected) && (isInBlueGoalieZone || isBlueGoalieEdge)) {
					System.out.println("Blue Goalie Zone");
					x = 0;
					if (y<LOW_GOAL_DIMENSION + VERTICAL_PADDING)
						y=LOW_GOAL_DIMENSION + VERTICAL_PADDING;
					if (y>=VERTICAL_PADDING + FIELD_HEIGHT - LOW_GOAL_DIMENSION - ROBOT_DIMENSION)
						y = VERTICAL_PADDING + FIELD_HEIGHT - LOW_GOAL_DIMENSION - ROBOT_DIMENSION;
					lastZone = BLUE_GOALIE_ZONE;
				} else if ((ALLIANCE.equals("Red") || !allianceIsSelected) && (isInRightWhiteZone || isRightWhiteZoneEdge)) {
					System.out.println("Right White Zone");
					x = x>=ZONE_WIDTH*2-ROBOT_DIMENSION - ROBOT_DIMENSION + CENTER_OFFSET ? ZONE_WIDTH*2 - ROBOT_DIMENSION  : x;
					lastZone = RIGHT_WHITE_ZONE;
				} else if ((ALLIANCE.equals("Blue") || !allianceIsSelected) && (isInRedGoalieZone || isRedGoalieEdge)) {
					System.out.println("Red Goalie Zone");
					lastZone = RED_GOALIE_ZONE;
					x = FIELD_WIDTH - LOW_GOAL_DIMENSION;
					if (y<LOW_GOAL_DIMENSION + VERTICAL_PADDING)
						y=LOW_GOAL_DIMENSION + VERTICAL_PADDING;
					if (y>=VERTICAL_PADDING + FIELD_HEIGHT - LOW_GOAL_DIMENSION - ROBOT_DIMENSION)
						y = VERTICAL_PADDING + FIELD_HEIGHT - LOW_GOAL_DIMENSION - ROBOT_DIMENSION;
				} else if ((ALLIANCE.equals("Blue") || !allianceIsSelected) && (isInLeftWhiteZone || isLeftWhiteZoneEdge)) {
					System.out.println("Left White Zone");
					lastZone = LEFT_WHITE_ZONE;
					x = x>FIELD_WIDTH/2-ROBOT_DIMENSION ? FIELD_WIDTH/2-ROBOT_DIMENSION : x;
				} else return true;

				boolean isAboveField = y<VERTICAL_PADDING + CENTER_OFFSET;
				boolean isBelowField = y>FIELD_HEIGHT + VERTICAL_PADDING + CENTER_OFFSET - ROBOT_DIMENSION;
				if (isAboveField && !isInRedGoalieZone && !isInBlueGoalieZone) y=VERTICAL_PADDING;
				if (isBelowField && !isInRedGoalieZone && !isInBlueGoalieZone) y=VERTICAL_PADDING + FIELD_HEIGHT - ROBOT_DIMENSION;

				if (!isEdge) {
					robotView.setX(x);
					findViewById(R.id.robot_review).setX(x);
				}
				robotView.setY(y);
				findViewById(R.id.robot_review).setY(y);
				if (robotView.getVisibility() == View.GONE) robotView.setVisibility(View.VISIBLE);
				if (findViewById(R.id.robot_review).getVisibility() == View.GONE) findViewById(R.id.robot_review).setVisibility(View.VISIBLE);

				Utilities.closeKeyboard(MatchScouting.this);
				return true;
			}

		};

		final ImageView fieldView = (ImageView) findViewById(R.id.field);
		fieldView.setOnTouchListener(pyramidTouch);
		final ImageView reviewFieldView = (ImageView) findViewById(R.id.field_review);
		reviewFieldView.setOnTouchListener(pyramidTouch);
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
				robotView.setVisibility(View.GONE);
				if (arg2 == 0) {
					ALLIANCE = "Blue";
					robotView.setBackgroundColor(Color.parseColor("#500000cc"));
				} else {
					ALLIANCE = "Red";
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
				R.id.truss,		
				R.id.auton_forward_movement,
				R.id.auton_ball_pickup,
				R.id.auton_blocked_shot,
				R.id.foul_button,
				R.id.technical_foul_button,
				R.id.human_player_pass,
				R.id.robot_pass,
				R.id.lost_ball,
				R.id.blocked_shot,
				R.id.deflection,
				R.id.pass_from_human_play,
				R.id.pass_from_robot,
				R.id.stray_ball,
				R.id.catches,
				R.id.beginning_possession,
				R.id.beginning_possession_review,
				R.id.showed_up,
				R.id.showed_up_review
		};
		for (int i = 2; i<buttonIDs.length; i++) findViewById(buttonIDs[i]).setOnClickListener(this);
		for (int i = 0; i<5; i++) findViewById(buttonIDs[i]).setOnLongClickListener(this);
	}

	private void startMatch() {		
		String matchNumber = ((EditText) findViewById(R.id.match_number)).getText().toString();
		String teamNumber = ((EditText) findViewById(R.id.team_number)).getText().toString();
		String scoutedBy = ((EditText) findViewById(R.id.scouted_by)).getText().toString();

		//		if (matchNumber.equals("")) {
		//			getMissingFieldsFragment("Please enter the match number.")
		//				.show(getSupportFragmentManager(), "MISSING_FIELDS");
		//			findViewById(R.id.match_number).requestFocus();
		//			return;
		//		} else if (teamNumber.equals("")) {
		//			getMissingFieldsFragment("Please enter the team number.")
		//				.show(getSupportFragmentManager(), "MISSING_FIELDS");
		//			findViewById(R.id.team_number).requestFocus();
		//			return;
		//		} else if (ALLIANCE.equals("")) {
		//			getMissingFieldsFragment("Please select an alliance")
		//				.show(getSupportFragmentManager(), "MISSING_FIELDS");
		//			findViewById(R.id.alliance).requestFocus();
		//			return;
		//		} else if (scoutedBy.equals("")) {
		//			getMissingFieldsFragment("Please enter your name in the \"Scouted By\" field.")
		//				.show(getSupportFragmentManager(), "MISSING_FIELDS");
		//			findViewById(R.id.scouted_by).requestFocus();
		//			return;
		//		} else if (!showedUp) {
		//			noShow();
		//			return;
		//		} else if (findViewById(R.id.robot).getVisibility() == View.GONE){
		//			getMissingFieldsFragment("Please indicate the robot's starting position.")
		//				.show(getSupportFragmentManager(), "MISSING_FIELDS");
		//			return;
		//		}

		getActionBar().hide();
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		//TODO check for goalie zone blocking.
		if (!startsInGoalie) {
			Animation tempAnimOut = controlsFlipper.getOutAnimation();
			Animation tempAnimIn = controlsFlipper.getInAnimation();
			controlsFlipper.setInAnimation(null);
			controlsFlipper.setOutAnimation(null);
			controlsFlipper.showNext();
			controlsFlipper.setInAnimation(tempAnimIn);
			controlsFlipper.setOutAnimation(tempAnimOut);
		}
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

	private SimpleTextFragment getMissingFieldsFragment(String msg) {
		return new SimpleTextFragment().newInstance("Missing Fields!"
				, msg, "OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}, false);
	}

	private void noShow() {
		new SimpleTextFragment().newInstance("Missing Fields!"
				, "Are you sure this robot is not showing up for this match?", "Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}, true).show(getSupportFragmentManager(), "MISSING_FIELDS");
	}

	private void startTeleOp() {
		matchTimer = new FixedCountDownTimer(140*SECONDS, 1000) {
//		matchTimer = new FixedCountDownTimer(30*SECONDS, 1000) {
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
				endMatch();
			}
		}.start();

		int autonPossessionCounter = (initialPossession ? 1 : 0) + autonBallPickup;
		int autonShotCounter = autonHighTotal + autonLowTotal;
		boolean initTeleOpPossession = autonShotCounter < autonPossessionCounter;
		if (initTeleOpPossession) {
			controlsFlipper.setDisplayedChild(2);
			possessions.add(new Possession());
		}
		else controlsFlipper.setDisplayedChild(3);

		CURRENT_GAME_MODE = initTeleOpPossession ? TELEOP_POSSESSION : TELEOP_NO_POSSESSION;
		((TextView) findViewById(R.id.auton_text)).setTextColor(
				getResources().getColor(android.R.color.secondary_text_dark));
		((TextView) findViewById(R.id.tele_op_text)).setTextColor(getResources().getColor(android.R.color.black));

		undoList.clear();
	}

	private void toggleControls(boolean undo) {
		if (CURRENT_GAME_MODE == TELEOP_POSSESSION) {
			CURRENT_GAME_MODE = TELEOP_NO_POSSESSION;
			controlsFlipper.setInAnimation(this, R.anim.in_from_right);
			controlsFlipper.setOutAnimation(this, R.anim.out_to_left);
			controlsFlipper.showNext();

			if (undo) possessions.remove(possessions.size()-1);
			else possessions.get(possessions.size()-1).finish();

		} else {
			CURRENT_GAME_MODE = TELEOP_POSSESSION;
			controlsFlipper.setInAnimation(this, R.anim.in_from_left);
			controlsFlipper.setOutAnimation(this, R.anim.out_to_right);
			controlsFlipper.showPrevious();

			if (undo) possessions.get(possessions.size()-1).restartPossession();
			else possessions.add(new Possession());
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
		} else if (parentLayout.getId() == R.id.teleop_low_goal) {
			if (madeShot) teleopLowGoal++;
			t.setText("" + teleopLowGoal + " / " + ++teleopLowGoalTotal);
		} else {
			if (madeShot) trussPoints++;
			t.setText("" + trussPoints + " / " + ++trussTotal);
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
		if (undoList.size() == 0) return;
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
		else if (key == UndoKeys.TRUSS || key == UndoKeys.TRUSS_MISSED)
			view = findViewById(R.id.truss);
		else view = findViewById(key);
		switch(key) {
		case UndoKeys.AUTON_BALL_PICKUP:
			updateButtonText(view, --autonBallPickup);
			break;
		case UndoKeys.AUTON_FORWARD_MOVEMENT:
			setForwardMovement(view, false);
			break;
		case UndoKeys.AUTON_BLOCKS:
			updateButtonText(view, --autonBlocks);
			break;
		case UndoKeys.FOUL:
			updateButtonText(view, --fouls + autonFouls);
			break;
		case UndoKeys.TECHNICAL_FOUL:
			updateButtonText(view, --techFouls + autonTechFouls);
			break;
		case UndoKeys.TELEOP_LOW:
			updateButtonText(view, --teleopLowGoal, --teleopLowGoalTotal);
			toggleControls(true);
			break;
		case UndoKeys.TELEOP_HIGH:
			updateButtonText(view, --teleopHighGoal, --teleopHighGoalTotal);
			toggleControls(true);
			break;
		case UndoKeys.PASS_TO_HUMAN_PLAYER:
			updateButtonText(view, --passToHP);
			toggleControls(true);
			break;
		case UndoKeys.PASS_TO_ROBOT:
			updateButtonText(view, --passToRobot);
			toggleControls(true);
			break;
		case UndoKeys.LOST_BALL:
			updateButtonText(view, --ballsLost);
			toggleControls(true);
			break;
		case UndoKeys.BLOCKED_SHOT:
			updateButtonText(view, --blocks);
			break;
		case UndoKeys.DEFLECTION:
			updateButtonText(view, --deflections);
			break;
		case UndoKeys.PASS_FROM_HUMAN_PLAYER:
			updateButtonText(view, --passFromHP);
			toggleControls(true);
			break;
		case UndoKeys.PASS_FROM_ROBOT:
			updateButtonText(view, --passFromRobot);
			toggleControls(true);
			break;
		case UndoKeys.STRAY_BALL:
			updateButtonText(view, --ballPickup);
			toggleControls(true);
			break;
		case UndoKeys.CATCHES:
			updateButtonText(view, --catches);
			toggleControls(true);
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
		case UndoKeys.TRUSS:
			updateButtonText(view, --trussPoints, --trussTotal);
			toggleControls(true);
			break;
		case UndoKeys.TRUSS_MISSED:
			updateButtonText(view, trussPoints, --trussTotal);
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
		getActionBar().show();
		System.out.println("Visibility " + menu.findItem(R.id.submit).isVisible());
		displayMenuItem = !displayMenuItem;
		invalidateOptionsMenu();
		System.out.println("Visibility " + menu.findItem(R.id.submit).isVisible());

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
		((CheckedTextView) findViewById(R.id.beginning_possession_review)).setChecked(initialPossession);
		((CheckedTextView) findViewById(R.id.showed_up_review)).setChecked(showedUp);


		final View robotReview = findViewById(R.id.robot_review);
		String entries[] = {"Blue", "Red"};
		ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_textview, entries);
		a.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		Spinner alliance = ((Spinner) findViewById(R.id.alliance_review));
		alliance.setAdapter(a);
		alliance.setOnItemSelectedListener(new OnItemSelectedListener(){
			boolean firstTime = true;
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (!firstTime) findViewById(R.id.robot_review).setVisibility(View.GONE);
				if (arg2 == 0) {
					ALLIANCE = "Blue";
					robotReview.setBackgroundColor(Color.parseColor("#500000cc"));
				} else {
					ALLIANCE = "Red";
					robotReview.setBackgroundColor(Color.parseColor("#50cc0000"));
				}
				firstTime = false;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		if (ALLIANCE.equals("Blue")) {
			((Spinner) findViewById(R.id.alliance_review)).setSelection(0);
			robotReview.setBackgroundColor(Color.parseColor("#500000cc"));
			robotReview.setVisibility(View.VISIBLE);
		} else {
			((Spinner) findViewById(R.id.alliance_review)).setSelection(1);
			robotReview.setBackgroundColor(Color.parseColor("#50cc0000"));
			robotReview.setVisibility(View.VISIBLE);
		}

		int[] reviewIds = {
				R.id.match_scouting_review_auton,
				R.id.match_scouting_review_teleop_with,
				R.id.match_scouting_review_teleop_without,
				R.id.match_scouting_review_penalties
		};

		reviewLayouts = getReviewLayouts();

		for (int x=0; x<reviewIds.length; x++) {
			for (int i=0; i<reviewLayouts[x].length; i++) {
				LinearLayout parent = (LinearLayout) findViewById(reviewIds[x]);
				parent.addView(reviewLayouts[x][i]);
				if (i != reviewLayouts[x].length-1) {
					View sep = new View(this);
					sep.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, PX(1)));
					sep.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.divider_horizontal_bright));
					parent.addView(sep);
				}
			}
		}		
		//		recalculateFinalStats();
	}


	private ReviewLayout[][] getReviewLayouts() {
		ReviewLayout[][] layout = {
				{
					new ReviewLayout(this, "Low Goal Shots Made, Not Hot", autonLowGoal-autonLowHot),//8
					new ReviewLayout(this, "Low Goal Shots Made, Hot", autonLowHot),
					new ReviewLayout(this, "Low Goal Shots Missed", autonLowTotal-autonLowGoal),
					new ReviewLayout(this, "High Goal Shots Made, Not Hot", autonHighGoal-autonHighHot),
					new ReviewLayout(this, "High Goal Shots Made, Hot", autonHighHot),
					new ReviewLayout(this, "High Goal Shots Missed", autonHighTotal-autonHighGoal),
					new ReviewLayout(this, "Balls Picked Up", autonBallPickup),
					new ReviewLayout(this, "Moved Forward?", autonForwardMovement),//15
					new ReviewLayout(this, "Blocks", autonBlocks)//16
				},
				{
					new ReviewLayout(this, "Low Goal Shots Made", teleopLowGoal),//17
					new ReviewLayout(this, "Low Goal Shots, Missed", teleopLowGoalTotal-teleopLowGoal),
					new ReviewLayout(this, "High Goal Shots Made", teleopLowGoal),
					new ReviewLayout(this, "High Goal Shots, Missed", teleopLowGoalTotal-teleopLowGoal),
					new ReviewLayout(this, "Passes to Other Robots", passToRobot),
					new ReviewLayout(this, "Passes to Human Players", passToHP),
					new ReviewLayout(this, "Throws over Truss", trussPoints),
					new ReviewLayout(this, "Throws over Truss, Missed", trussTotal-trussPoints),
					new ReviewLayout(this, "Balls Lost", ballsLost)//25
				},
				{
					new ReviewLayout(this, "Blocks", blocks),//26
					new ReviewLayout(this, "Deflections", deflections),
					new ReviewLayout(this, "Passes from Other Robots", passFromRobot),
					new ReviewLayout(this, "Passes from Human Players", passFromHP),
					new ReviewLayout(this, "Catches", catches),
					new ReviewLayout(this, "Balls Picked Up", ballPickup)//31
				},
				{
					new ReviewLayout(this, "Autonomous Fouls (10 Points)", autonFouls),
					new ReviewLayout(this, "Autonomous Technical Fouls (50 Points)", autonTechFouls),
					new ReviewLayout(this, "Fouls (10 Points)", fouls),
					new ReviewLayout(this, "Technical Fouls (50 Points)", techFouls),//35
				}
		};
		return layout;
	}

	public void saveData(MenuItem item) {
		String[] data = new String[37];
		data[0] = ((EditText) findViewById(R.id.match_number_review)).getEditableText().toString();
		data[1] = ((EditText) findViewById(R.id.team_number_review)).getEditableText().toString();
		data[2] = ALLIANCE;

		Calendar c = Calendar.getInstance();
		String s = "Scouted by " + ((EditText) findViewById(R.id.scouted_by_review)).getEditableText().toString() +
				" at " + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + " " + c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US) +
				" on " + c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US) +
				", " + c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + c.get(Calendar.DATE) + ", " + c.get(Calendar.YEAR);
		data[3] = s;

		data[4]	= ((EditText) findViewById(R.id.notes_review)).getEditableText().toString().trim();
		data[5] = String.valueOf(initialPossession);
		data[6] = String.valueOf(showedUp);
		data[7] = findViewById(R.id.robot_review).getX() + "," + findViewById(R.id.robot_review).getY();
		int index = 8;
		for (int i = 0; i<reviewLayouts.length; i++) {
			for (int j = 0; j<reviewLayouts[i].length; j++) {
				data[index++] = reviewLayouts[i][j].getFinalValue();
			}
		}
		data[index] = "";
		for (int i=0; i<possessions.size(); i++) {
			if (i!=0) data[index] += "||";
			data[index] += possessions.get(i).print();
		}
		System.out.println(data.length + "");
		new Competition(this, "SCMB").addToMatchScouting(data);
		Toast.makeText(this, "Data stored.", Toast.LENGTH_LONG).show();
		recreate();
	}

	//	public void recalculateFinalStats() {
	//			int points = autonHighGoal 	* 15 +
	//						autonHighHot 	* 5  +
	//						autonLowGoal 	* 6  +
	//						autonLowHot 	* 5  +
	//						teleopHighGoal 	* 10 +
	//						teleopLowGoal	* 1	 +
	//						catches 		* 10 +
	//						trussPoints 	* 10 +
	//						(autonForwardMovement ? 5 : 0);
	//			((TextView) findViewById(R.id.total_points)).setText("" + points);
	//			
	//			int penalties = (autonFouls + fouls) * 10 +
	//					(techFouls + fouls) * 50;
	//			((TextView) findViewById(R.id.total_penalties)).setText("" + penalties);
	//	
	//			((TextView) findViewById(R.id.net_points)).setText("" + (points-penalties));
	//	}

	int PX (int dp) {return Utilities.PX(this, dp);}

	public static void start(Activity activity) {
		Intent i = new Intent(activity, com.steelhawks.hawkscout.MatchScouting.class);
		activity.startActivity(i);
	}

	public static void start(Activity activity, String teamNumber, String matchNumber) {
		Intent i = new Intent(activity, com.steelhawks.hawkscout.MatchScouting.class);
		i.putExtra(ACTIVITY_INTENT_1, teamNumber);
		i.putExtra(ACTIVITY_INTENT_2, matchNumber);
		activity.startActivity(i);
	}

	class Possession {

		private String gainedPossessionBy;
		private int startTime = 0;
		private int finishTime = 0;
		private String lostPossessionBy;

		public Possession () {
			gainedPossessionBy =
					undoList.size() == 0 ? "Autonomous Possession" : getPossessionString(undoList.get(undoList.size()-1));
			startTime = matchTimer.getSecondsLeft();
			System.out.println("Gained Possession: " + gainedPossessionBy + " at " + startTime);
		}

		private String getPossessionString(int key) {
			switch(key) {
			case UndoKeys.PASS_TO_HUMAN_PLAYER:
				return "Passed to HP";
			case UndoKeys.PASS_TO_ROBOT:
				return "Passed to Robot";
			case UndoKeys.LOST_BALL:
				return "Lost Ball";
			case UndoKeys.TRUSS:
				return "Threw over Truss";
			case UndoKeys.TRUSS_MISSED:
				return "Missed Truss";
			case UndoKeys.PASS_FROM_HUMAN_PLAYER:
				return "Pass from HP";
			case UndoKeys.PASS_FROM_ROBOT:
				return "Pass from Robot";
			case UndoKeys.STRAY_BALL:
				return "Picked up Stray Ball";
			case UndoKeys.CATCHES:
				return "Caught Ball";
			case UndoKeys.TELEOP_HIGH:
				return "Made High Goal";
			case UndoKeys.TELEOP_LOW:
				return "Made Low Goal";
			case UndoKeys.TELEOP_HIGH_MISSED:
				return "Missed High Goal";
			case UndoKeys.TELEOP_LOW_MISSED:
				return "Missed Low Goal";
			}
			return "Autonomous Possession";
		}

		public void finish() {
			lostPossessionBy = getPossessionString(undoList.get(undoList.size()-1));
			finishTime = matchTimer.getSecondsLeft();
			System.out.println("Possession End: " + lostPossessionBy + " at " + finishTime);
		}

		public void restartPossession() {
			lostPossessionBy = null;
			finishTime = 0;
			System.out.println("Restarting Possession: " + gainedPossessionBy + " at " + startTime);
		}

		public String print() {
			return gainedPossessionBy + "|" + lostPossessionBy + "|" + startTime + "|" + finishTime;
		}
	}

}
