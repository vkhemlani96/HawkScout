package com.steelhawks.hawkscout.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;

import com.steelhawks.hawkscout.data.Indices.MatchScoutingIndex;
import com.steelhawks.hawkscout.data.Indices.PossessionIndex;
import com.steelhawks.hawkscout.data.Indices.StatsIndex;
import com.steelhawks.hawkscout.util.FileIO;

public class Competition {

	private final static String MATCH_SCOUTING_NEW_LINE = "HSNL";
	private final static String MATCH_SCOUTING_SEPARATOR = "HSMSS";
	private final static String PIT_SCOUTING_PARAMS_SEPARATOR = "PSS";
	private final static String PIT_SCOUTING_NEW_LINE = "HSPSNL";
	public	final static String PIT_SCOUTING_SEPARATOR = "HSPSS";
	public	final static String PIT_SCOUTING_KEY_SEPARATOR = "HSPSK";

	private String compCode;
	private String folderPath;
	private String PIT_SCOUTING_PATH;
	private String MATCH_SCOUTING_PATH;
	private String SCHEDULE_PATH;
	private String RANKINGS_PATH;
	public static String MEDIA_PATH;
	private static String PIT_SCOUTING_PARAMS_PATH;
	private String date;
	private String location;
	private String[] teams;
	private List<String[]> matches;
	private List<String[]> rankings;
	private List<String[]> matchScoutingData;
	private List<String[]> pitScoutingData;
	private static Map<String, List<Parameter>> pitScoutingParams;
	private generateStats statsTask;
	private List<float[]> stats;

	public Competition (Context context, String compCode) {		
		this.compCode = compCode;

		folderPath = context.getExternalFilesDir(compCode).getPath();
		try {
			new File(folderPath).createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PIT_SCOUTING_PATH = folderPath + "/PIT_SCOUTING.txt";
		MATCH_SCOUTING_PATH = folderPath  + "/MATCH_SCOUTING.txt";
		SCHEDULE_PATH = folderPath + "/MATCH_SCHEDULE.txt";
		RANKINGS_PATH = folderPath + "/RANKINGS.txt";
		MEDIA_PATH = folderPath + "/MEDIA/";
		PIT_SCOUTING_PARAMS_PATH = new File(folderPath).getParent() + "/PIT_SCOUTING_PARAMS.txt";

		if (compCode.equals("NYNY")) {
			date = "April 4th-6th, 2014";
			location = "New York, NY";
			String[] tempList =
				{"271",	"333",	"334",	"335",	"353",	"354",	"369",	"371",	"375",	"395",	"421",	"514",
					"522",	"597",	"640",	"694",	"743",	"806",	"810",	"1155",	"1156",	"1230",	"1382",	"1396",
					"1600","1635",	"1660",	"1796",	"1880",	"1884",	"2265",	"2344",	"2579",	"2601",	"2869",	"2895",
					"3017","3053",	"3059",	"3137",	"3158",	"3171",	"3204",	"3419",	"3760",	"3940",	"4012",	"4039",
					"4108","4122",	"4263",	"4299",	"4383",	"4528",	"4571",	"4640",	"4684",	"4773",	"4789",	"4797",
					"4856","5123",	"5151",	"5202",	"5289",	"5298"};
			teams = new String[tempList.length];
			for (int i=0; i<tempList.length; i++) teams[i] = tempList[i];
		} else if (compCode.equals("ONTO")) {
			date = "March 6th-8th, 2014";
			location = "Oshawa, ON, Canada";
			String[] tempList = 
				{"216",	"244",	"288",	"746",	"781",	"854",	"886",	"907",	"919",	"1075",	"1114",	"1219",
					"1241",	"1246",	"1285",	"1325",	"1404",	"1503",	"1547",	"2013",	"2198",	"2601",	"2609",	"2994",
					"3173",	"3360",	"3386",	"3387",	"3550",	"3683",	"3710",	"3985",	"3986",	"3988",	"4248",	"4252",
					"4476",	"4618",	"4627",	"4718",	"4727",	"4783",	"4825",	"5031",	"5036",	"5051",	"5158"};
			teams = new String[tempList.length];
			for (int i=0; i<tempList.length; i++) teams[i] = tempList[i];
		} else if (compCode.equals("ONTO")) {
			date = "March 6th-8th, 2014";
			location = "Oshawa, ON, Canada";
			String[] tempList = 
				{"216",	"244",	"288",	"746",	"781",	"854",	"886",	"907",	"919",	"1075",	"1114",	"1219",
					"1241",	"1246",	"1285",	"1325",	"1404",	"1503",	"1547",	"2013",	"2198",	"2601",	"2609",	"2994",
					"3173",	"3360",	"3386",	"3387",	"3550",	"3683",	"3710",	"3985",	"3986",	"3988",	"4248",	"4252",
					"4476",	"4618",	"4627",	"4718",	"4727",	"4783",	"4825",	"5031",	"5036",	"5051",	"5158"};
			teams = new String[tempList.length];
			for (int i=0; i<tempList.length; i++) teams[i] = tempList[i];
		} else if (compCode.equals("SCMB")) {
			date = "March 6th-8th, 2014";
			location = "Myrtle Beach, SC";
			String[] tempList = 
				{"254", "771", "781", "865", "1114", "1241", "1285", "1305", "1334", "2056", "2609", "2702", "3161", "3683", "3756", 
					"3949", "4039", "4069", "4519", "4525", "4678", "4732", "4777", "4907", "4917", "4943", "4992", "5032", "5033", "5039"};
			teams = new String[tempList.length];
			for (int i=0; i<tempList.length; i++) teams[i] = tempList[i];
		}

		if (!compCode.equals("SCMB")) return;
		matches = parseMatches();
		rankings = parseRankings();
		matchScoutingData = parseMatchScouting();
		pitScoutingData = parsePitScouting();
		pitScoutingParams = parsePitScoutingParams();
		statsTask = new generateStats();
		statsTask.execute();
	}

	public void addToMatchScouting(String[] data) {
		String line = "";
		for (int i = 0; i<data.length; i++) {
			if (i != 0) line += "HSMSS";
			line += data[i];
		}
		line += MATCH_SCOUTING_NEW_LINE + "\n";
		line += FileIO.readTextFile(MATCH_SCOUTING_PATH);
		FileIO.writeTextFile(MATCH_SCOUTING_PATH, line);
	}

	public void addToPitScouting(String data) {
		String line = data;
		line += PIT_SCOUTING_NEW_LINE + "\n";
		line += FileIO.readTextFile(PIT_SCOUTING_PATH);
		FileIO.writeTextFile(PIT_SCOUTING_PATH, line);
	}

	private List<String[]> parseMatches() {
		if (!new File(SCHEDULE_PATH).isFile()) {
			System.out.println("Match Schedule not found " + location);
			return null;
		}
		String[] text = FileIO.readTextFile(SCHEDULE_PATH).split("\n");
		List<String[]> matches = new ArrayList<String[]>();

		for (int i = 0; i<text.length; i++) {
			if (!text[i].contains(",")) return matches;
			matches.add(text[i].split(","));
		}

		return matches;
	}

	public List<float[]> getStatistics() {
		System.out.println("Task is running?" + String.valueOf(statsTask.getStatus() == Status.RUNNING));
		System.out.println("Task is finished?" + String.valueOf(statsTask.getStatus() == Status.FINISHED));
		System.out.println("Stats is null? " + String.valueOf(stats == null));
		while (statsTask.getStatus() == Status.RUNNING) System.out.println("Still running");
		return stats;
	}

	private List<String[]> parseRankings() {
		String[] text = FileIO.readTextFile(RANKINGS_PATH)
				.split("\n");
		List<String[]> rankings = new ArrayList<String[]>();

		for (int i = 0; i<text.length; i++) {
			if (!text[i].contains(",")) return rankings;
			rankings.add(text[i].split(","));
		}

		return rankings;
	}

	private List<String[]> parseMatchScouting() {
		String[] text = FileIO.readTextFile(MATCH_SCOUTING_PATH).split(MATCH_SCOUTING_NEW_LINE + "\n");
		List<String[]> data = new ArrayList<String[]>();

		for (int i=0; i<text.length; i++) {
			if (!text[i].contains(MATCH_SCOUTING_SEPARATOR)) return data;
			data.add(text[i].split(MATCH_SCOUTING_SEPARATOR));
		}
		return data;
	}

	private List<String[]> parsePitScouting() {
		String[] text = FileIO.readTextFile(PIT_SCOUTING_PATH).split(PIT_SCOUTING_NEW_LINE + "\n");
		List<String[]> data = new ArrayList<String[]>();

		for (int i=0; i<text.length; i++) {
			if (!text[i].contains(PIT_SCOUTING_SEPARATOR)) return data;
			data.add(text[i].split(PIT_SCOUTING_SEPARATOR));
		}
		return data;
	}

	private Map<String, List<Parameter>> parsePitScoutingParams() {
		String fileText = FileIO.readTextFile(PIT_SCOUTING_PARAMS_PATH);
		if (fileText == null) {
			System.out.println("Params are null");
		}
		String[] paramText = fileText.split("\n");
		Map<String, List<Parameter>> paramMap = new HashMap<String, List<Parameter>>();
		for (int i=0; i<paramText.length; i++) {
			String[] paramLine = paramText[i].split(PIT_SCOUTING_PARAMS_SEPARATOR);
			String category = paramLine[0];
			String title = paramLine[1];
			String type = paramLine[2];
			String opts = paramLine.length==4 ? paramLine[3] : "";
			if (!paramMap.containsKey(category)) paramMap.put(category, new ArrayList<Parameter>());
			paramMap.get(category).add(new Parameter(title, type, opts));
		}
		return paramMap;
	}

	public String[] getMatchInfoByNumber(String matchNumber) {
		for (int x=0; x<matches.size(); x++) {
			if (matches.get(x)[1].trim().equals(matchNumber.trim())) return matches.get(x);
		}
		return null;
	}

	public String[] getPitScoutingForTeam(String teamNumber) {
		for (int x=0; x<pitScoutingData.size(); x++) {
			if (pitScoutingData.get(x)[0].equals(teamNumber)) return pitScoutingData.get(x);
		}
		return null;
	}

	public List<String> getMediaPathsforTeam(String teamNumber) {
		String[] mediaPaths = new File(folderPath + "/MEDIA").list();
		List<String> foundPaths = new ArrayList<String>();
		for (int x=0; x<mediaPaths.length; x++)
			if (mediaPaths[x].contains(teamNumber)) foundPaths.add(folderPath + "/MEDIA/" + mediaPaths[x]);
		return foundPaths;
	}

	public List<String> getMatchesByTeam(String teamNumber) {
		List<String> foundMatches = new ArrayList<String>();
		for (int i = 0; i<matches.size(); i++) {
			if (matchContainsTeam(matches.get(i), teamNumber.trim())) foundMatches.add(matches.get(i)[1]);
		}
		return foundMatches;
	}

	public String[] getMatchScoutingDataForTeam(String matchNumber, String teamNumber) {
		matchNumber = matchNumber.trim();
		teamNumber = teamNumber.trim();
		for (int x = 0; x<matchScoutingData.size(); x++) {
			if(stringsMatch(matchScoutingData.get(x)[0].trim(), matchNumber) && matchScoutingData.get(x)[1].trim().equals(teamNumber))
				return matchScoutingData.get(x);
		}
		return null;
	}

	public boolean stringsMatch(String badString, String goodString) {
		if (badString.equals(goodString)) return true;
		Character[] numberChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		List<Character> goodChars = Arrays.asList(numberChars);
		return badString.length() == goodString.length() + 1 &&
				!goodChars.contains(badString.charAt(0)) && 
				badString.substring(1).equals(goodString);

	}

	private boolean matchContainsTeam(String[] match, String teamNumber) {
		return  match[2].trim().equals(teamNumber) ||
				match[3].trim().equals(teamNumber) ||
				match[4].trim().equals(teamNumber) ||
				match[5].trim().equals(teamNumber) ||
				match[6].trim().equals(teamNumber) ||
				match[7].trim().equals(teamNumber);

	}

	public List<String[]> getRankings() {
		if (rankings == null) return getBlankRankings();
		return rankings;
	}

	private List<String[]> getBlankRankings() {
		List<String[]> rankings = new ArrayList<String[]>();
		for (int i = 0; i<teams.length; i++) {
			String[] rank = new String[10];
			rank[0] = String.valueOf(i);
			rank[1] = teams[i];
			for (int x = 2; x<10; x++) rank[x] = "0";
			rankings.add(rank);
		}
		return rankings;
	}

	public List<String[]> getMatches() {
		return matches;
	}

	public String[] getTeams() {
		return teams;
	}

	public Map<String, List<Parameter>> getScoutingParams() {
		return pitScoutingParams;
	}

	public String getCompCode() {
		return compCode;
	}

	private int getPointsScoredForMatch(String[] data) {
		return Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_HOT].trim()) 	* 20 +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_COLD].trim()) 	* 15 +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_HOT].trim()) 	* 11 +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_COLD].trim())	* 6 +
				Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MADE].trim()) 		* 10 +
				Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MADE].trim()) 		* 1 +
				Integer.parseInt(data[MatchScoutingIndex.TRUSS].trim()) 				* 15 +
				Integer.parseInt(data[MatchScoutingIndex.CATCHES].trim()) 				* 15 +
				(Boolean.parseBoolean(data[MatchScoutingIndex.AUTON_MOVED_FORWARD].trim()) ? 5 : 0);
	}

	private int getFoulsForMatch(String[] data) {
		return Integer.parseInt(data[MatchScoutingIndex.AUTON_FOULS].trim()) * 20 +
				Integer.parseInt(data[MatchScoutingIndex.FOULS].trim()) * 20 +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_TECH_FOULS].trim()) * 50 +
				Integer.parseInt(data[MatchScoutingIndex.TECH_FOULS].trim()) * 50;

	}

	private int getOverallScore(String[] data) {
		return Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_COLD].trim()) 	* 5  +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_HOT].trim()) 	* 5  +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_HOT].trim()) 	* 10 +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_COLD].trim())	* 15 +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_HOT].trim()) 	* 15 +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_HOT].trim()) 	* 10 +
				(Boolean.parseBoolean(data[MatchScoutingIndex.AUTON_MOVED_FORWARD].trim()) ? 2 : 0) +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_MISSED].trim())	* -3 +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_MISSED].trim())* -6 +
				Integer.parseInt(data[MatchScoutingIndex.CATCHES].trim())				* 20 +
				Integer.parseInt(data[MatchScoutingIndex.PASSES_FROM_ROBOT].trim())		* 15 +
				Integer.parseInt(data[MatchScoutingIndex.PASSES_TO_ROBOT].trim())		* 15 +
				Integer.parseInt(data[MatchScoutingIndex.TRUSS].trim())					* 15 +
				Integer.parseInt(data[MatchScoutingIndex.TRUSS_MISSED].trim())			* -5 +
				Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MADE].trim())		*  3 +
				Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MADE].trim())		* 10 +
				Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MISSED].trim())		* -8 +
				Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MISSED].trim())	* -5 +
				Integer.parseInt(data[MatchScoutingIndex.FOULS].trim())					* -8 +
				Integer.parseInt(data[MatchScoutingIndex.TECH_FOULS].trim())			*-15 +
				Integer.parseInt(data[MatchScoutingIndex.AUTON_BLOCKS].trim())			*  5 +
				Integer.parseInt(data[MatchScoutingIndex.TELEOP_BLOCKS].trim())			*  5;
	}

	private class generateStats extends AsyncTask<String[], Void, Void> {

		List<float[]> stats = new ArrayList<float[]>();

		@Override
		protected Void doInBackground(String[]... arg0) {
			for (int x=0; x<teams.length; x++) {
				String teamNumber = teams[x];
				List<String> matches = getMatchesByTeam(teamNumber);
				float matchesPlayed = 0;
				int pointsScored = 0, foulsScored = 0, blocksAndDeflections = 0, totalPossessions = 0, totalAutonHighGoalMade = 0, totalAutonHighGoalTaken = 0,
						totalTeleopHighGoalMade = 0, totalTeleopHighGoalTaken = 0, totalAutonLowGoalMade = 0, totalAutonLowGoalTaken = 0,
						totalTeleopLowGoalMade = 0, totalTeleopLowGoalTaken = 0, totalTruss = 0, totalTrussTaken = 0, totalCatches = 0, totalFouls = 0,
						totalTechFouls = 0, passesFromHP = 0, passesFromRobo = 0, passesToHP = 0, passesToRobo = 0, overallScore = 0;
				for (int y=0; y<matches.size(); y++) {
					String[] data = getMatchScoutingDataForTeam(matches.get(y), teamNumber);
					if(data == null) continue;
					matchesPlayed++;
					pointsScored += getPointsScoredForMatch(data);
					foulsScored += getFoulsForMatch(data);
					blocksAndDeflections += Integer.parseInt(data[MatchScoutingIndex.TELEOP_BLOCKS].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_BLOCKS].trim()) + 
							Integer.parseInt(data[MatchScoutingIndex.DEFLECTIONS].trim());
					totalPossessions += data[MatchScoutingIndex.POSSESSIONS].trim().split("\\|\\|").length;
					totalAutonHighGoalMade += Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_COLD].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_HOT].trim());
					totalAutonHighGoalTaken += Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_COLD].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_HOT].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_MISSED].trim());
					totalAutonLowGoalMade += Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_COLD].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_HOT].trim());
					totalAutonLowGoalTaken += Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_COLD].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_HOT].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_MISSED].trim());
					totalTeleopHighGoalMade += Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MADE].trim());
					totalTeleopHighGoalTaken += Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MADE].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MISSED].trim());
					totalTeleopLowGoalMade += Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MADE].trim());
					totalTeleopLowGoalTaken += Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MADE].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MISSED].trim());
					totalTruss += Integer.parseInt(data[MatchScoutingIndex.TRUSS]);
					totalTrussTaken += Integer.parseInt(data[MatchScoutingIndex.TRUSS]) + Integer.parseInt(data[MatchScoutingIndex.TRUSS_MISSED]);
					totalCatches += Integer.parseInt(data[MatchScoutingIndex.CATCHES]);
					totalFouls += Integer.parseInt(data[MatchScoutingIndex.FOULS]) + Integer.parseInt(data[MatchScoutingIndex.AUTON_FOULS]);
					totalTechFouls += Integer.parseInt(data[MatchScoutingIndex.TECH_FOULS]) + Integer.parseInt(data[MatchScoutingIndex.AUTON_TECH_FOULS]);
					passesFromHP += Integer.parseInt(data[MatchScoutingIndex.PASSES_FROM_HP]);
					passesFromRobo += Integer.parseInt(data[MatchScoutingIndex.PASSES_FROM_ROBOT]);
					passesToHP += Integer.parseInt(data[MatchScoutingIndex.PASSES_TO_HP]);
					passesToRobo += Integer.parseInt(data[MatchScoutingIndex.PASSES_TO_ROBOT]);
					overallScore += getOverallScore(data);
				}
				float[] teamStats = new float[StatsIndex.SIZE];
				teamStats[StatsIndex.TEAM_NUMBER] = Integer.parseInt(teamNumber.trim());
				teamStats[StatsIndex.POINTS_PER_MATCH] = matchesPlayed != 0 ? pointsScored / matchesPlayed : 0;
				teamStats[StatsIndex.FOULS_PER_MATCH] = matchesPlayed != 0 ? foulsScored / matchesPlayed : 0;
				teamStats[StatsIndex.BLOCKS_PER_MATCH] = matchesPlayed != 0 ? blocksAndDeflections / matchesPlayed :0;
				teamStats[StatsIndex.POINTS_PER_POSSESSION] = totalPossessions != 0 ? pointsScored / totalPossessions : 0;
				teamStats[StatsIndex.POSSESSIONS_PER_MATCH] = matchesPlayed != 0 ? totalPossessions / matchesPlayed : 0;
				teamStats[StatsIndex.HIGH_GOAL_TOTAL] = totalAutonHighGoalMade + totalTeleopHighGoalMade;
				teamStats[StatsIndex.LOW_GOAL_TOTAL] = totalAutonLowGoalMade + totalTeleopLowGoalMade;
				teamStats[StatsIndex.TRUSS_TOTAL] = totalTruss;
				teamStats[StatsIndex.CATCHES_TOTAL] = totalCatches;
				teamStats[StatsIndex.FOULS_TOTAL] = totalFouls;
				teamStats[StatsIndex.TECHNICAL_FOULS_TOTAL] = totalTechFouls;
				teamStats[StatsIndex.AUTON_HIGH_GOAL_ACCURACY] = totalAutonHighGoalTaken != 0 ? 100 * totalAutonHighGoalMade / totalAutonHighGoalTaken : 0;
				teamStats[StatsIndex.AUTON_LOW_GOAL_ACCURACY] = totalAutonLowGoalTaken != 0 ? 100 * totalAutonLowGoalMade / totalAutonLowGoalTaken : 0;
				teamStats[StatsIndex.TELE_HIGH_GOAL_ACCURACY] = totalTeleopHighGoalTaken != 0 ? 100 * totalTeleopHighGoalMade / totalTeleopHighGoalTaken : 0;
				teamStats[StatsIndex.TELE_LOW_GOAL_ACCURACY] = totalTeleopLowGoalTaken != 0 ? 100 * totalTeleopLowGoalMade / totalTeleopLowGoalTaken : 0;
				teamStats[StatsIndex.TELE_TRUSS_ACCURACY] = totalTrussTaken != 0 ? 100 * totalTruss / totalTrussTaken : 0;
				teamStats[StatsIndex.PASS_FROM_HP] = passesFromHP;
				teamStats[StatsIndex.PASS_FROM_ROBOT] = passesFromRobo;
				teamStats[StatsIndex.PASS_TO_HP] = passesToHP;
				teamStats[StatsIndex.PASS_TO_ROBOT] = passesToRobo;
				teamStats[StatsIndex.OVERALL_SCORE] = overallScore;
				stats.add(teamStats);
			}
			return null;
		}

		public void onPostExecute(Void arg0) {
			super.onPostExecute(arg0);
			Competition.this.stats = this.stats;
		}

	}

}
