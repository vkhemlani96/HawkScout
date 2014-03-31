package com.steelhawks.hawkscout.data;

public class Indices {

	public class MatchIndex {
		public static final int TIME = 0;
		public static final int MATCH_NUMBER = 1;
		public static final int RED1 = 2;
		public static final int RED2 = 3;
		public static final int RED3 = 4;
		public static final int BLUE1 = 5;
		public static final int BLUE2 = 6;
		public static final int BLUE3 = 7;
		public static final int RED_SCORE = 8;
		public static final int BLUE_SCORE = 9;
	}
	
	public class MatchScoutingIndex {
		public static final int MATCH_NUMBER = 0;
		public static final int TEAM_NUMBER = 1;
		public static final int ALLIANCE = 2;
		public static final int SCOUT = 3;
		public static final int NOTES = 4;
		public static final int INITIAL_POSSESSION = 5;
		public static final int SHOWED_UP = 6;
		public static final int AUTON_POSITION = 7;
		public static final int AUTON_LOW_GOAL_COLD = 8;
		public static final int AUTON_LOW_GOAL_HOT = 9;
		public static final int AUTON_LOW_GOAL_MISSED = 10;
		public static final int AUTON_HIGH_GOAL_COLD = 11;
		public static final int AUTON_HIGH_GOAL_HOT = 12;
		public static final int AUTON_HIGH_GOAL_MISSED = 13;
		public static final int AUTON_BALLS_PICKED_UP = 14;
		public static final int AUTON_MOVED_FORWARD = 15;
		public static final int AUTON_BLOCKS = 16;
		public static final int TELEOP_LOW_MADE = 17;
		public static final int TELEOP_LOW_MISSED = 18;
		public static final int TELEOP_HIGH_MADE = 19;
		public static final int TELEOP_HIGH_MISSED = 20;
		public static final int PASSES_TO_ROBOT = 21;
		public static final int PASSES_TO_HP = 22;
		public static final int TRUSS = 23;
		public static final int TRUSS_MISSED = 24;
		public static final int BALLS_LOST = 25;
		public static final int TELEOP_BLOCKS = 26;
		public static final int DEFLECTIONS = 27;
		public static final int PASSES_FROM_ROBOT = 28;
		public static final int PASSES_FROM_HP = 29;
		public static final int CATCHES = 30;
		public static final int BALLS_PICKED_UP = 31;
		public static final int AUTON_FOULS = 32;
		public static final int AUTON_TECH_FOULS = 33;
		public static final int FOULS = 34;
		public static final int TECH_FOULS = 35;
		public static final int POSSESSIONS = 36;
	}
	
	public class PossessionIndex {
		public static final int POSSESSION_START = 0;
		public static final int POSSESSION_END = 1;
		public static final int START_TIME = 2;
		public static final int END_TIME = 3;
	}
	
	public class PitScoutingIndex {
		public static final int TEAM_NUMBER = 0;
		public static final int TEAM_NAME = 1;
		public static final int PIT_NUMBER = 2;
		public static final int SCOUTED_BY = 3;
	}
	
	public class StatsIndex {
		public static final int TEAM_NUMBER = 0;
		public static final int POINTS_PER_MATCH = 1;
		public static final int FOULS_PER_MATCH = 2;
		public static final int BLOCKS_PER_MATCH = 3;
		public static final int POINTS_PER_POSSESSION = 4;
		public static final int POSSESSIONS_PER_MATCH = 5;
		public static final int HIGH_GOAL_TOTAL = 6;
		public static final int LOW_GOAL_TOTAL = 7;
		public static final int TRUSS_TOTAL = 8;
		public static final int CATCHES_TOTAL = 9;
		public static final int FOULS_TOTAL = 10;
		public static final int TECHNICAL_FOULS_TOTAL = 11;
		public static final int AUTON_HIGH_GOAL_ACCURACY = 12;
		public static final int AUTON_LOW_GOAL_ACCURACY = 13;
		public static final int TELE_HIGH_GOAL_ACCURACY = 14;
		public static final int TELE_LOW_GOAL_ACCURACY = 15;
		public static final int TELE_TRUSS_ACCURACY = 16;
		public static final int PASS_FROM_HP = 17;
		public static final int PASS_FROM_ROBOT = 18;
		public static final int PASS_TO_HP = 19;
		public static final int PASS_TO_ROBOT = 20;
		public static final int SIZE = 21;
	}
	
}
