package com.steelhawks.hawkscout;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.steelhawks.hawkscout.data.ScoutingParameters;

public class Globals extends Application {
	
	public boolean isAvailable() {
		if(EMAIL.equals(null) || TEAMS.size() == 0) {
			SETTINGS = getSharedPreferences("HawkScoutSettings", 0);
			EDIT_SETTINGS = SETTINGS.edit();
			
			CREDENTIALS = GoogleAccountCredential.usingOAuth2(this,
					DriveScopes.DRIVE + 
					" https://spreadsheets.google.com/feeds");
			
			try {SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			} catch (MalformedURLException e) {e.printStackTrace();}
			
			TEAM_NUMBER = SETTINGS.getInt(SAVED_TEAM_NUMBER, -1);
			EMAIL = SETTINGS.getString(SAVED_EMAIL, null);
			
			CREDENTIALS.setSelectedAccountName(EMAIL);
			
			DRIVE = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), CREDENTIALS)
						.setApplicationName("HawkScout")
						.build();

			new createService().execute(true);
			
			String teams[] = SETTINGS.getString(SAVED_DATA, null).split(";");
			for(int x=0; x<teams.length; x++) {
				TEAMS.add(new UserTeam(teams[x]));
			}
			return false;
		} else {
			return true;
		}
			
	}
	
	public void saveData() {
		String comps = "";
		for(int x=0; x<TEAMS.size(); x++) {
			if(x == 0) {
				comps += String.valueOf(TEAMS.get(x).getTeamNumber()) + ":";
			} else {
				comps += ";" + String.valueOf(TEAMS.get(x).getTeamNumber()) + ":";
			}
			for(int y=0; y<TEAMS.get(x).COMPETITIONS.size(); y++) {
				if (y != TEAMS.get(x).COMPETITIONS.size() - 1){
					comps += TEAMS.get(x).COMPETITIONS.get(y).getIndex() + ",";
				} else {
					comps += TEAMS.get(x).COMPETITIONS.get(y).getIndex();
				}
			}
		}
		System.out.println(comps);
		editSettings().putInt(SAVED_TEAM_NUMBER, TEAM_NUMBER)
			.putString(SAVED_EMAIL, EMAIL)
			.putString(SAVED_DATA, comps)
			.commit();
		System.out.println("Pre-Commit" + SETTINGS.getString(STORED_EMAIL, null));
	}
		
	//General Info
	private int TEAM_NUMBER = -1;
	public int getTeamNumber () {
		return TEAM_NUMBER;
	}
	public void setTeamNumber (int i) {
		TEAM_NUMBER = i;
	}
	
	private String EMAIL;
	public String getEmail () {
		return EMAIL;
	}
	public void setEmail (String s) {
		EMAIL = s;
	}
	
	//SharedPref settings
	public final static String STORED_EMAIL = "com.steelhawks.hawkscout.STORED_EMAIL";
	public final static String SAVED_EMAIL = "com.steelhawks.hawkscout.SAVED_EMAIL";
	public final static String SAVED_TEAM_NUMBER = "com.steelhawks.hawkscout.SAVED_TEAM_NUMBER";
	public final static String SAVED_DATA = "com.steelhawks.hawkscout.SAVED_DATA";
	
	private SharedPreferences SETTINGS = null;
	public SharedPreferences getSettings () {
		return SETTINGS;
	}
	
	private SharedPreferences.Editor EDIT_SETTINGS = null;
	public SharedPreferences.Editor editSettings () {
		String storedEmail = SETTINGS.getString(STORED_EMAIL, null);
		String savedEmail = SETTINGS.getString(SAVED_EMAIL, null);
		int savedTeamNumber = SETTINGS.getInt(SAVED_TEAM_NUMBER, -1);
		String savedData = SETTINGS.getString(SAVED_DATA, null);
		EDIT_SETTINGS
			.putString(STORED_EMAIL, storedEmail)
			.putString(SAVED_EMAIL, savedEmail)
			.putInt(SAVED_TEAM_NUMBER, savedTeamNumber)
			.putString(SAVED_DATA, savedData);
		System.out.println("Pre-Commit" + storedEmail);
		return EDIT_SETTINGS;
	}
	
	//Drive SDK Settings
	private Drive DRIVE;
	public Drive getDrive () {
		return DRIVE;
	}
	public void setDrive (Drive service) {
		DRIVE = service;
	}
	
	private SpreadsheetService SERVICE;
	public SpreadsheetService getService () {
		return SERVICE;
	}
	public void setService(SpreadsheetService t) {
		SERVICE = t;
	}
	
	private URL SPREADSHEET_FEED_URL;
	public URL getSpreadsheetFeedUrl () {
		return SPREADSHEET_FEED_URL;
	}
	
	private GoogleAccountCredential CREDENTIALS;
	public GoogleAccountCredential getCredentials () {
		return CREDENTIALS;
	}
	
	private List<Uri> deleteUri = new ArrayList<Uri>();
	public void addToDeleteUri (List<Uri> list) {
		deleteUri.addAll(list);
	}
	
	//WebView for Competition Data
	
/*	public List<SpreadsheetEntry> foundSpreadsheets = new ArrayList<SpreadsheetEntry>();	
	public void listAllSpreadsheets () throws IOException, ServiceException {
		SpreadsheetFeed feed = getService().getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
	    List<SpreadsheetEntry> spreadsheets = feed.getEntries();
    	for (int x = 0; x < spreadsheets.size(); x++) {
    		if (spreadsheets.get(x).getTitle().getPlainText().indexOf("HawkScout") == 0) {
    			Competition comp = new Competition(spreadsheets.get(x));
    			System.out.println(comp.getComp().getTitle().getPlainText());
    			COMPETITIONS.add(comp);
    		}
    	}
		System.out.println(COMPETITIONS.size());
	}
*/	
	private List<File> foundFiles = new ArrayList<File>();	
	public List<File> getAllFolders () {
		foundFiles.clear();
		try {
			Files.List request;
			request = getDrive().files().list();
			request.setQ("title contains '(DO NOT DELETE) HawkScout Scouting Data:' and mimeType = 'application/vnd.google-apps.folder'");
			  do {
				    try {
				      FileList files = request.execute();

				      foundFiles.addAll(files.getItems());
				      request.setPageToken(files.getNextPageToken());
				    } catch (IOException e) {
				      System.out.println("An error occurred: " + e);
				      request.setPageToken(null);
				    }
				  } while (request.getPageToken() != null &&
				           request.getPageToken().length() > 0);
			  for (int x=0; x<foundFiles.size(); x++) {
				  UserTeam t = new UserTeam(foundFiles.get(x), true);
				  TEAMS.add(t);
			  }
			  System.out.println("" + foundFiles.size());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		  return foundFiles;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		SETTINGS = getSharedPreferences("HawkScoutSettings", 0);
		EDIT_SETTINGS = SETTINGS.edit();
		
		CREDENTIALS = GoogleAccountCredential.usingOAuth2(this,
				DriveScopes.DRIVE + 
				" https://spreadsheets.google.com/feeds");
		
		new createService().execute(false);
		
		try {SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
		} catch (MalformedURLException e) {e.printStackTrace();}
		
	}
	
	private List<UserTeam> TEAMS = new ArrayList<UserTeam>();
	public List<UserTeam> getTeams () {
		return TEAMS;
	}

 	public class UserTeam {
 		UserTeam (File f, boolean old) {
			TEAM_FOLDER = f;
			TEAM_NUMBER = Integer.parseInt(f.getTitle().split(":")[1]);
			System.out.println("Found folder:" + TEAM_NUMBER);
			if (old) {
				System.out.println("GETTING CHILDREN");
				getChildren.execute();
			}
		}
 		
 		UserTeam (String str) {
 			String[] split = str.split(":");
 			TEAM_NUMBER = Integer.parseInt(split[0]);
 			String[] indexes = split[1].split(",");
 			for(int x=0; x<indexes.length; x++) {
 				this.addCompetition(new Competition(Integer.parseInt(indexes[x])));
 			}
 		}
 		
 		private getChildren getChildren = new getChildren();
 		public getChildren getTask () {
 			return getChildren;
 		}
		
		private boolean LOADED = false;
		public boolean getLoaded () {
			return LOADED;
		}
		
		private int TEAM_NUMBER;
		public int getTeamNumber () {
			return TEAM_NUMBER;
		}
		public void setTeamNumber (int i) {
			TEAM_NUMBER = i;
		}
		
		private File TEAM_FOLDER;
		public File getTeamFolder () {
			return TEAM_FOLDER;
		}
		public void setTeamFolder (File f) {
			TEAM_FOLDER = f;
		}
	
		private List<Competition> COMPETITIONS = new ArrayList<Competition>();
		public List<Competition> getCompetitions () {
			return COMPETITIONS;
		}
		public Competition addCompetition (Competition c) {
			COMPETITIONS.add(c);
			return c;
		}
	
		private ConversationLog CONVERSATION_LOG = null;
		public ConversationLog getConversationLog () {
			return CONVERSATION_LOG;
		}
		public void setConversationLog (ConversationLog f) {
			CONVERSATION_LOG = f;
		}
	
		private ScoutingParameters SCOUTING_PARAMS = null;
		public ScoutingParameters getScoutingParams() {
			return SCOUTING_PARAMS;
		}
		public void setScoutingParams (ScoutingParameters a) {
			SCOUTING_PARAMS = a;
		}
		
		public void sort() {
			Collections.sort(COMPETITIONS, new Comparator<Competition>() {
				public int compare(Competition one, Competition two) {
					String name1 = one.getCompName();
					String name2 = two.getCompName();
					return name1.compareTo(name2);
				}
			});
		}
	
		protected class getChildren extends AsyncTask <Integer, Integer, Void> {
			
			private List<File> foundChildren = new ArrayList<File>();
			public void listAllChildren () {
				try {
					Files.List request;
					request = getDrive().files().list();
					request.setQ("'" + TEAM_FOLDER.getId() + "' in parents");
					System.out.println("Folder ID:" + TEAM_FOLDER.getId());
					  do {
						    try {
						      FileList files = request.execute();

						      foundChildren.addAll(files.getItems());
						      request.setPageToken(files.getNextPageToken());
						    } catch (IOException e) {
						      System.out.println("An error occurred: " + e);
						      request.setPageToken(null);
						    }
					  } while (request.getPageToken() != null &&
						           request.getPageToken().length() > 0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected Void doInBackground(Integer... arg0) {
				listAllChildren();
				return null;
			}
			protected void onPostExecute (Void result) {
				System.out.println("Size of foundChildren" + foundChildren.size());
				for (int x= 0; x<foundChildren.size(); x++) {
					if (foundChildren.get(x).getTitle().equals("(DO NOT DELETE) HawkScout2013 ConversationLog")
							&& foundChildren.get(x).getMimeType().equals("application/vnd.google-apps.spreadsheet")) {
						CONVERSATION_LOG = new ConversationLog(foundChildren.get(x));
						System.out.println("Found Conversation Log");
					} else if (foundChildren.get(x).getTitle().equals("(DO NOT DELETE) HawkScout2013 ScoutingParameters")
							&& foundChildren.get(x).getMimeType().equals("application/vnd.google-apps.spreadsheet")) {
						SCOUTING_PARAMS = new ScoutingParameters(foundChildren.get(x), Globals.this);
						System.out.println("Found Conversation Log");
					} else if (foundChildren.get(x).getTitle().indexOf("(DO NOT DELETE) HawkScout2013.") == 0
							&& foundChildren.get(x).getMimeType().equals("application/vnd.google-apps.spreadsheet")) {
						COMPETITIONS.add(new Competition(foundChildren.get(x), false));
						System.out.println("Found Competition");
					}
				}
				sort();
			}
		}
	}

	@SuppressWarnings("unused")
	public class Competition {
		@SuppressLint("SimpleDateFormat")
		public Competition(File f, boolean isNew) {
			DRIVE_FILE = f;

			String[] compCodes = getResources().getStringArray(R.array.comp_code);
			String[] compNames = getResources().getStringArray(R.array.competitions);
			String[] compWeeks = getResources().getStringArray(R.array.comp_week);
			String[] compDates = getResources().getStringArray(R.array.comp_date);
			String[] compLocs = getResources().getStringArray(R.array.comp_location);
			for (int x = 0; x<compCodes.length; x++) {
				if (compCodes[x].equals(f.getTitle().split("\\.")[1])) {
					INDEX = x;
					COMP_CODE = compCodes[x];
					COMP_NAME = compNames[x];
					COMP_WEEK = Integer.parseInt(compWeeks[x].replaceAll("[\\D]", ""));
					COMP_LOC = compLocs[x];
					try {
						START_DATE = new SimpleDateFormat("yyyy-MM/dd").parse("2013-0"+compDates[x].split("to")[0]);
						END_DATE = new SimpleDateFormat("yyyy-MM/dd HH:mm:ss").parse(
								"2013-0"+compDates[x].split("to")[1] + " 23:59:59");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					System.out.println(START_DATE);
					System.out.println(END_DATE);
				}
			}
			new getSpreadsheetEntry().execute(isNew);
		}
		
		Competition (int i) {
			String[] compCodes = getResources().getStringArray(R.array.comp_code);
			String[] compNames = getResources().getStringArray(R.array.competitions);
			String[] compWeeks = getResources().getStringArray(R.array.comp_week);
			String[] compDates = getResources().getStringArray(R.array.comp_date);
			String[] compLocs = getResources().getStringArray(R.array.comp_location);
			INDEX = i;
			COMP_CODE = compCodes[i];
			COMP_NAME = compNames[i];
			COMP_WEEK = Integer.parseInt(compWeeks[i].replaceAll("[\\D]", ""));
			COMP_LOC = compLocs[i];
			try {
				START_DATE = new SimpleDateFormat("yyyy-MM/dd").parse("2013-0"+compDates[i].split("to")[0]);
				END_DATE = new SimpleDateFormat("yyyy-MM/dd HH:mm:ss").parse(
						"2013-0"+compDates[i].split("to")[1] + " 23:59:59");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		private File DRIVE_FILE;
		public File getDriveFile() {
			return DRIVE_FILE;
		}
		
		private SpreadsheetEntry SS_FILE;
		
		private WorksheetEntry MATCH_SCOUTING;
		
		private WorksheetEntry PIT_SCOUTING;
		public WorksheetEntry getPitScouting() {
			return PIT_SCOUTING;
		}
		
		private int INDEX;
		public int getIndex() {
			return INDEX;
		}

		private String COMP_CODE;
		public String getCompCode() {
			return COMP_CODE;
		}
		
		private String COMP_NAME;
		public String getCompName () {
			return COMP_NAME;		
		}
		
		private String COMP_LOC;
		public String getCompLoc () {
			return COMP_LOC;
		}
		
		private int COMP_WEEK;
		public int getCompWeek () {
			return COMP_WEEK;
		}
		
		private Date START_DATE;
		public Date getStartDate() {
			return START_DATE;
		}

		private Date END_DATE;
		public Date getEndDate() {
			return END_DATE;
		}

		private List<ListEntry> PIT_DATA = new ArrayList<ListEntry>();
		public List<ListEntry> getPitData() {
			return PIT_DATA;
		}
		public ListEntry getPitData(String teamNumber) {
			for (int x=0; x<PIT_DATA.size(); x++) {
				System.out.println(PIT_DATA.get(x).getTitle().getPlainText());
				if (PIT_DATA.get(x).getTitle().getPlainText().equals(teamNumber)) {
					System.out.print("FOUND DATA FOR" + teamNumber);
					System.out.print(PIT_DATA.get(x).getCustomElements().getValue("scoutedby"));
					System.out.println();
					return PIT_DATA.get(x);
				}
			}
			System.err.println("Didnt find anything");
			return null;
		}
		
		public int getTeamsScouted() {
//			return PIT_DATA.size() > 0 ? PIT_DATA.size() : -1;
			return PIT_SCOUTING == null ? -1 : PIT_SCOUTING.getRowCount();
		}
		
		private List<TeamData> TEAM_DATA = new ArrayList<TeamData>();
		public List<TeamData> getTeamData() {
			return TEAM_DATA;
		}
		public TeamData getTeamData(String teamNumber) {
			for (int x = 0; x<TEAM_DATA.size(); x++) {
				if (String.valueOf(TEAM_DATA.get(x).getTeamNumber()).equals(teamNumber))
					return TEAM_DATA.get(x);
			}
			return null;
		}
		
		private List<MatchData> MATCH_DATA = new ArrayList<MatchData>();
		public List<MatchData> getMatchData() {
			return MATCH_DATA;
		}
		
		public int getLeader() {
			List<TeamData> teams = getTeamData();
			int rank  = 1000;
			int leader = 0;
			for(int x=0; x < teams.size(); x++) {
				if (teams.get(x).getRank() == 1) {
					return teams.get(x).getTeamNumber();
				}
				if (teams.get(x).getRank() < rank) {
					leader = teams.get(x).getTeamNumber();
					rank = teams.get(x).getRank();
				}
			}
			return leader;
		}
		
		
		private int lastMatch = -1;
		public int getLastMatch() {
			return lastMatch;
		}
				
		private getCompInfo GET_COMP_INFO = new getCompInfo();
		private void resetCompInfo() {
			GET_COMP_INFO = new getCompInfo();
		}
		public getCompInfo getCompInfo() {
			return GET_COMP_INFO;
		}

		private getQualSchedule GET_QUAL_SCHEDULE = new getQualSchedule();
		private void resetQualSchedule() {
			GET_QUAL_SCHEDULE = new getQualSchedule();
		}
		public getQualSchedule getQualSchedule() {
			return GET_QUAL_SCHEDULE;
		}
		
		private getMatchResults GET_MATCH_RESULTS = new getMatchResults();
		private void resetMatchResults() {
			GET_MATCH_RESULTS = new getMatchResults();
		}
		public getMatchResults getMatchResults() {
			return GET_MATCH_RESULTS;
		}
		
		private getScoutedTeams GET_SCOUTED_TEAMS = new getScoutedTeams();
		private void resetScoutedTeams() {
			GET_SCOUTED_TEAMS = new getScoutedTeams();
		}
		public getScoutedTeams getScoutedTeams() {
			return GET_SCOUTED_TEAMS;
		}
		
		
		class WorksheetKeys {
			public final static String TEAM_NUMBER = "TEAM_NUMBER";
			public final static String SCOUTED = "SCOUTED?";
			public final static String HEIGHT = "HEIGHT";
			public final static String WEIGHT = "WEIGHT";
			public final static String DRIVE_TYPE = "DRIVE_TYPE";
			public final static String CIMS = "CIMS";
			public final static String SPEED = "SPEED";
			public final static String UNDER_PYRAMID = "UNDER_PYRAMID";
			public final static String AUTON = "AUTON?";
			public final static String AUTON_POINTS = "AUTON_POINTS";
			public final static String ONES = "ONES";
			public final static String TWOS = "TWOS";
			public final static String THREES = "THREES";
			public final static String FIVES = "FIVES";
			public final static String AUTO_LINE = "AUTO_LINE";
			public final static String MAX_CLIMB = "MAX_CLIMB";
			public final static String BLOCKS = "BLOCKS";
			public final static String PICTURES = "PICTURES";
			public final static String NOTES = "NOTES";
			
		}
		
		public class getCompInfo extends AsyncTask <String, Integer, Void> {

			@Override
			protected Void doInBackground(String... arg0) {
				try {
				 	System.out.println("============STARTING GET=========");
		 	    	HttpParams params = new BasicHttpParams();
		    			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				 	  HttpClient client = new DefaultHttpClient(params);  
				 	    String getURL = "http://www2.usfirst.org/2013comp/Events/" + getCompCode().toLowerCase() + "/rankings.html";
				 	    HttpGet get = new HttpGet(getURL);
				 	    HttpResponse responseGet = client.execute(get);  
				 	    HttpEntity resEntityGet = responseGet.getEntity();  
				 	    if (resEntityGet != null) {
				 	    	getTeamData().clear();
				 	        String response = EntityUtils.toString(resEntityGet);
				 	        String startEx = "<p class=\"MsoNormal\"><span style=\"display: none;\"><!--[if !supportEmptyParas]-->&nbsp;<!--[endif]--><o:p></o:p></span></p><div style=\"text-align: left;\">";
				 	        String endEx = "</div>";
				 	        int start = response.indexOf(startEx) + startEx.length();
				 	        int end = response.indexOf(endEx, start);
				 	        response = response.substring(start, end);
				 	        String[] rows = response.split("<TR style=\"background-color:#FFFFFF;\" >");
				 	        for (int x = 1; x<rows.length; x++) {
				 	        	String separator = System.getProperty("line.separator");
				 	        	String[] data = rows[x].split("<TD align=\"center\" style=\"font-family:arial;font-weight:normal;font-size:12px\">");
				 	        	if(data.length == 10) {
					 	        	int rank = Integer.parseInt(String.valueOf(data[1].replace(separator, "").replace("</TD>", "").trim()));
					 	        	int team = Integer.parseInt(data[2].replace(separator, "").replace("</TD>", "").trim());
					 	        	int qs = (int) Double.parseDouble(data[3].replace(separator, "").replace("</TD>", "").trim());
					 	        	int ap = (int) Double.parseDouble(data[4].replace(separator, "").replace("</TD>", "").trim());
					 	        	int cp = (int) Double.parseDouble(data[5].replace(separator, "").replace("</TD>", "").trim());
					 	        	int tp = (int) Double.parseDouble(data[6].replace(separator, "").replace("</TD>", "").trim());
					 	        	String record = data[7].replace(separator, "").replace("</TD>", "").trim();
					 	        	int dq = Integer.parseInt(data[8].replace(separator, "").replace("</TD>", "").trim());
					 	        	int played = Integer.parseInt(data[9].replace(separator, "").replace("</TD>", "").replace("</TR>", "").trim());
					 	        	getTeamData().add(
					 	        			new TeamData(team, rank, qs, ap, cp, tp,
					 	        					record, dq, played));
				 	        	} else {
					 	        	int rank = Integer.parseInt(String.valueOf(data[1].replace(separator, "").replace("</TD>", "").trim()));
					 	        	int team = Integer.parseInt(data[2].replace(separator, "").replace("</TD>", "").trim());
					 	        	int qs = (int) Double.parseDouble(data[3].replace(separator, "").replace("</TD>", "").trim());
					 	        	int ap = (int) Double.parseDouble(data[4].replace(separator, "").replace("</TD>", "").trim());
					 	        	int cp = (int) Double.parseDouble(data[5].replace(separator, "").replace("</TD>", "").trim());
					 	        	int tp = (int) Double.parseDouble(data[6].replace(separator, "").replace("</TD>", "").trim());
					 	        	String record = data[8].replace(separator, "").replace("</TD>", "").trim();
					 	        	int dq = Integer.parseInt(data[9].replace(separator, "").replace("</TD>", "").trim());
					 	        	int played = Integer.parseInt(data[10].replace(separator, "").replace("</TD>", "").replace("</TR>", "").trim());
					 	        	getTeamData().add(
					 	        			new TeamData(team, rank, qs, ap, cp, tp,
					 	        					record, dq, played));
				 	        	}
				 	        }
				 	        Log.wtf("tag", "ONE FINISHED");
//				 	        System.out.println(comp.getTeamData().get(10).rankings + ": "
//				 	        		+ comp.getTeamData().get(10).teamNumber + comp.getTeamData().get(10).record);
				 	    } else {
				 	    	System.out.println("NO Response");
				 	    }
			    } catch (IOException e) {
			        Log.e("HTTP GET:", e.toString());
			    }
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				Collections.sort(getTeamData(), new Comparator<TeamData>() {
					public int compare(TeamData one, TeamData two) {
						Integer number1 = one.getTeamNumber();
						Integer number2 = two.getTeamNumber();
						return number1.compareTo(number2);
					}
				});
				resetCompInfo();
				System.out.println(getLeader());
			}

			
		}

		public class getScoutedTeams extends AsyncTask <String, Integer, Void> {
			boolean completed;
			@Override
			protected Void doInBackground(String... params) {
				try {
					completed = true;
					if (PIT_SCOUTING != null) {
						ListFeed listFeed = getService().getFeed(
								PIT_SCOUTING.getListFeedUrl(), ListFeed.class);
						PIT_DATA = listFeed.getEntries();
						System.err.println("GOT PIT DATA" + PIT_DATA.size());
					} else {
						completed = false;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				System.out.println("COMPLETED?:" + completed);
				if (!completed) {
					GET_SCOUTED_TEAMS = new getScoutedTeams();
					GET_SCOUTED_TEAMS.execute();
				} else {
					resetScoutedTeams();
				}
			}
		}
		
		public class getQualSchedule extends AsyncTask <String, Integer, Void> {

			@Override
			protected Void doInBackground(String... arg0) {
		 	    try {
		 	    	HttpParams params = new BasicHttpParams();
		 	    			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		 	    	HttpClient client = new DefaultHttpClient(params);  
			 	    String getURL = "http://www2.usfirst.org/2013comp/Events/" + getCompCode().toLowerCase() + "/scheduleQual.html";
			 	    HttpGet get = new HttpGet(getURL);
					HttpResponse responseGet = client.execute(get);
			 	    HttpEntity resEntityGet = responseGet.getEntity();  
					if (resEntityGet != null) {
						getMatchData().clear();
			 	        String response = EntityUtils.toString(resEntityGet);
		 	        	String separator = System.getProperty("line.separator");
			 	        response.replace(separator, "");
			 	        String startEx = "<TR style=\"background-color:#FFFFFF;\" >";
			 	        String endEx = "</table>";
			 	        int start = response.indexOf(startEx) + startEx.length();
			 	        int end = response.indexOf(endEx, start);
			 	        response = response.substring(start, end);
//			 	        System.out.println(response);
			 	        String[] rows = response.split("<TR style=\"background-color:#FFFFFF;\" >");
			 	        for (int x = 0; x<rows.length; x++) {
			 	        	rows[x].replace("</TR>", "");
			 	        	String[] data = rows[x]
			 	        			.split("<TD style=\"font-family:arial;font-weight:normal;font-size:9.0pt\">")[1]
			 	        			.split("<TD align=center style=\"font-family:arial;font-weight:normal;font-size:9.0pt\">");
			 	        	String time = data[0].substring(0, data[0].indexOf("</TD>"));
			 	        	int matchNumber = Integer.parseInt(data[1].substring(0, data[1].indexOf("</TD>")));
			 	        	int red1 = Integer.parseInt(data[2].substring(0, data[2].indexOf("</TD>")));
			 	        	int red2 = Integer.parseInt(data[3].substring(0, data[3].indexOf("</TD>")));
			 	        	int red3 = Integer.parseInt(data[4].substring(0, data[4].indexOf("</TD>")));
			 	        	int blue1 = Integer.parseInt(data[5].substring(0, data[5].indexOf("</TD>")));
			 	        	int blue2 = Integer.parseInt(data[6].substring(0, data[6].indexOf("</TD>")));
			 	        	int blue3 = Integer.parseInt(data[7].substring(0, data[7].indexOf("</TD>")));
			 	        	getMatchData().add(new MatchData(time, matchNumber, red1, red2, red3,
			 	        			blue1, blue2, blue3));
			 	        }
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			 	  
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
//				Toast.makeText(CompetitionActivity.this, "GOT MATCH DATA", Toast.LENGTH_LONG).show();
				resetMatchResults();
				super.onPostExecute(result);
			}
			
		}
		
		public class getMatchResults extends AsyncTask<String, Integer, Void> {

			List<MatchResult> matchResults = new ArrayList<MatchResult>();
			int lastmatchfound;
			
			@Override
			protected Void doInBackground(String... arg0) {
				lastmatchfound = 0;
			try {
	 	    	HttpParams params = new BasicHttpParams();
	 	    			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	 	    	HttpClient client = new DefaultHttpClient(params);  
		 	    String getURL = "http://www2.usfirst.org/2013comp/Events/" + getCompCode().toLowerCase() + "/matchresults.html";
		 	    HttpGet get = new HttpGet(getURL);
				HttpResponse responseGet = client.execute(get);
		 	    HttpEntity resEntityGet = responseGet.getEntity();  
				if (resEntityGet != null) {
		 	        String response = EntityUtils.toString(resEntityGet);
	 	        	String separator = System.getProperty("line.separator");
		 	        response.replace(separator, "");
		 	        String startEx = "<TR style=\"background-color:#FFFFFF;\" >";
		 	        String endEx = "</tbody></table>";
		 	        int start = response.indexOf(startEx) + startEx.length();
		 	        int end = response.indexOf(endEx, start);
		 	        response = response.substring(start, end);
		 	        String[] rows = response.split("<TR style=\"background-color:#FFFFFF;\" >");
		 	        for (int x = 0; x<rows.length; x++) {
		 	        	rows[x].replace("</TR>", "");
		 	        	String[] data = rows[x]
		 	        			.split("<TD style=\"font-family:arial;font-weight:normal;font-size:9.0pt\">")[1]
		 	        			.split("<TD align=center style=\"font-family:arial;font-weight:normal;font-size:9.0pt\">");
		 	        	String time;
		 	        	int matchNumber;
		 	        	int red1;
		 	        	int red2;
		 	        	int red3;
		 	        	int blue1;
		 	        	int blue2;
		 	        	int blue3;
		 	        	int redScore;
		 	        	int blueScore;
		 	        	try {
			 	        	time = data[0].substring(0, data[0].indexOf("</TD>"));
		 	        	} catch (Exception e) {
		 	        		time = "0:00";
		 	        	}
		 	        	try {
			 	        	lastmatchfound = Integer.parseInt(data[1].substring(0, data[1].indexOf("</TD>")));
			 	        	matchNumber = Integer.parseInt(data[1].substring(0, data[1].indexOf("</TD>")));
		 	        	} catch (Exception e) {
		 	        		lastmatchfound ++;
		 	        		matchNumber = 0;
		 	        	}
		 	        	try {
			 	        	red1 = Integer.parseInt(data[2].substring(0, data[2].indexOf("</TD>")));
		 	        	} catch (Exception e) {
		 	        		red1 = 0;
		 	        	}
		 	        	try {
			 	        	red2 = Integer.parseInt(data[3].substring(0, data[3].indexOf("</TD>")));
		 	        	} catch (Exception e) {
		 	        		red2 = 0;
		 	        	}
		 	        	try {
			 	        	red3 = Integer.parseInt(data[4].substring(0, data[4].indexOf("</TD>")));
		 	        	} catch (Exception e) {
		 	        		red3 = 0;
		 	        	}
		 	        	try {
			 	        	blue1 = Integer.parseInt(data[5].substring(0, data[5].indexOf("</TD>")));
		 	        	} catch (Exception e) {
		 	        		blue1 = 0;
		 	        	}
		 	        	try {
			 	        	blue2 = Integer.parseInt(data[6].substring(0, data[6].indexOf("</TD>")));
		 	        	} catch (Exception e) {
		 	        		blue2 = 0;
		 	        	}
		 	        	try {
			 	        	blue3 = Integer.parseInt(data[7].substring(0, data[7].indexOf("</TD>")));
		 	        	} catch (Exception e) {
		 	        		blue3 = 0;
		 	        	}
		 	        	try {
			 	        	redScore = Integer.parseInt(data[8].substring(0, data[8].indexOf("</TD>")));
		 	        	} catch (Exception e) {
		 	        		redScore = 0;
		 	        	}
		 	        	try {
			 	        	blueScore = Integer.parseInt(data[9].substring(0, data[9].indexOf("</TD>")));
		 	        	} catch (Exception e) {
		 	        		blueScore = 0;
		 	        	}
		 	        	matchResults.add(new MatchResult(time, matchNumber, red1, red2, red3,
		 	        			blue1, blue2, blue3, redScore, blueScore));
		 	        }
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}  
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				Runnable r = new Runnable() {

					@Override
					public void run() {
						if (getQualSchedule().getStatus() == AsyncTask.Status.PENDING) {
							boolean notFound = true;
							for(int x = 0; x<matchResults.size(); x++) {
								if (getMatchData().size()>x && matchResults.get(x).isSame(getMatchData().get(x))) {
									notFound = false;
									MatchResult result = matchResults.get(x);
									getMatchData().get(x).setResults(result.getTime(),
											result.getRedScore(), result.getBlueScore());
								} else {
									for (int y = 0; y<getMatchData().size(); y++) {
										if (matchResults.get(x).isSame(getMatchData().get(y))) {
											MatchResult result = matchResults.get(x);
											getMatchData().get(x).setResults(result.getTime(),
													result.getRedScore(), result.getBlueScore());
											notFound = false;
										}
									}
								}
								if (notFound) {
									for (int y = 0; y<getMatchData().size(); y++) {
										if (matchResults.get(x).getMatchNumber() ==
												getMatchData().get(y).getMatchNumber()) {
											MatchResult result = matchResults.get(x);
											getMatchData().get(x).setResults(result.getTime(),
													result.getRedScore(), result.getBlueScore());
										}
									}
								}
							}
						} else {
							new Handler().postDelayed(this, 100);
						}
					}
					
				};
				new Handler().post(r);
				lastMatch = lastmatchfound;
				resetQualSchedule();
			}
			
			class MatchResult {
				public MatchResult(String time, int number, int red1, int red2, int red3,
						int blue1, int blue2, int blue3, int redScore, int blueScore) {
					this.time = time;
					this.number = number;
					this.red1 = red1;
					this.red2 = red2;
					this.red3 = red3;
					this.blue1 = blue1;
					this.blue2 = blue2;
					this.blue3 = blue3;
					this.redScore = redScore;
					this.blueScore = blueScore;
				}
				
				private String time;
				public String getTime() {
					return time;
				}
				
				private int number;
				public int getMatchNumber() {
					return number;
				}
				
				private int red1;
				public int getRed1() {
					return red1;
				}
				
				private int red2;
				public int getRed2() {
					return red2;
				}
				
				private int red3;
				public int getRed3() {
					return red3;
				}
				
				private int blue1;
				public int getBlue1() {
					return blue1;
				}
				
				private int blue2;
				public int getBlue2() {
					return blue2;
				}
				
				private int blue3;
				public int getBlue3() {
					return blue3;
				}
				
				private int redScore;
				public int getRedScore() {
					return redScore;
				}
				
				private int blueScore;
				public int getBlueScore() {
					return blueScore;
				}
				
				public boolean isSame(MatchData m) {
					if(this.getMatchNumber() == m.getMatchNumber()	&&
						(this.getBlue1() == m.getBlue1() || 
						 this.getBlue1() == m.getBlue2() ||
						 this.getBlue1() == m.getBlue3()) &&
						(this.getBlue2() == m.getBlue1() || 
						 this.getBlue2() == m.getBlue2() ||
						 this.getBlue2() == m.getBlue3()) &&
						(this.getBlue3() == m.getBlue1() || 
						 this.getBlue3() == m.getBlue2() ||
						 this.getBlue3() == m.getBlue3()) &&
						(this.getRed1() == m.getRed1() || 
						 this.getRed1() == m.getRed2() ||
						 this.getRed1() == m.getRed3()) && 
						(this.getRed2() == m.getRed1() || 
						 this.getRed2() == m.getRed2() ||
						 this.getRed2() == m.getRed3()) && 
						(this.getRed3() == m.getRed1() || 
						 this.getRed3() == m.getRed2() ||
						 this.getRed3() == m.getRed3())) {
						return true;
					}
					return false;
				}
			}
			
		}
		
		protected class getSpreadsheetEntry extends AsyncTask <Boolean, Integer, Void> {
			@Override
			protected Void doInBackground(Boolean... params) {
				try {
					SS_FILE = getService().getEntry(
							new URL("https://spreadsheets.google.com/feeds/spreadsheets/"
									+ DRIVE_FILE.getId()),
							SpreadsheetEntry.class);
					if(params[0]) {
						List<WorksheetEntry> worksheets = SS_FILE.getWorksheets();
						worksheets.get(0).setTitle(new PlainTextConstruct("MATCH_SCOUTING"));
						MATCH_SCOUTING = worksheets.get(0).update();
						WorksheetEntry pitScouting = new WorksheetEntry();
							pitScouting.setTitle(new PlainTextConstruct("PIT_SCOUTING"));
							pitScouting.setRowCount(MATCH_SCOUTING.getRowCount());
							pitScouting.setColCount(MATCH_SCOUTING.getColCount());
						PIT_SCOUTING = getService().insert(SS_FILE.getWorksheetFeedUrl(), pitScouting);
						try {
							CellQuery cellQuery = new CellQuery(PIT_SCOUTING.getCellFeedUrl());
							CellFeed cellFeed = getService().query(cellQuery, CellFeed.class);
							CellEntry cellEntry = new CellEntry(1, 1, "TEAM_NUMBER");
							cellFeed.insert(cellEntry);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
							//TODO AuthenticationException
						} catch (ServiceException e) {
							e.printStackTrace();
						}
						System.out.println("match:" + MATCH_SCOUTING.getId());
						System.out.println("pit:" + PIT_SCOUTING.getId());
					} else {
						List<WorksheetEntry> worksheets = SS_FILE.getWorksheets();
						for (WorksheetEntry entry : worksheets) {
							if (entry.getTitle().toString().equals("MATCH_SCOUTING")) {
								MATCH_SCOUTING = entry;
							} else if (entry.getTitle().getPlainText().equals("PIT_SCOUTING")){
								PIT_SCOUTING = entry;
							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
		}
		
	}

	@SuppressWarnings("unused")
	public class ConversationLog {
		ConversationLog(File f) {
			DRIVE_FILE = f;
//			new getSpreadsheetEntry().execute();
		}
		
		private File DRIVE_FILE;
		public File getDriveFile() {
			return DRIVE_FILE;
		}
		private SpreadsheetEntry SS_FILE;
		
		protected class getSpreadsheetEntry extends AsyncTask <Integer, Integer, Void> {
			@Override
			protected Void doInBackground(Integer... arg0) {
				try {
					SS_FILE = getService().getEntry(
							new URL("https://spreadsheets.google.com/feeds/spreadsheets/"
									+ DRIVE_FILE.getId()), SpreadsheetEntry.class);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
		}	
	
	}

	public class TeamData {
		 
		public TeamData(int a, int b, int c, int d, int e, int f,
				String g, int h, int i) {
			teamNumber = a;
			rank = b;
			qs= c;
			ap = d;
			cp = e;
			tp = f;
			record = g;
			dq = h;
			played = i;
		}
		
		private int teamNumber;
		public int getTeamNumber() {
			return teamNumber;
		}
		
		private int rank;
		public int getRank() {
			return rank;
		}
		
		private int qs;
		public int getQualScore() {
			return qs;
		}
		
		private int ap;
		public int getAutonPoints() {
			return ap;
		}

		private int cp;
		public int getClimbPoints() {
			return cp;
		}

		private int tp;
		public int getTelePoints() {
			return tp;
		}
		
		private String record;
		public String getRecord() {
			return record;
		}
		
		private int dq;
		public int getDisqualified() {
			return dq;
		}
		
		private int played;
		public int getPlayed() {
			return played;
		}
		
	}
	
	public class MatchData {
		
		public MatchData(String time, int number, int red1, int red2, int red3,
				int blue1, int blue2, int blue3) {
			this.time = time;
			this.number = number;
			this.red1 = red1;
			this.red2 = red2;
			this.red3 = red3;
			this.blue1 = blue1;
			this.blue2 = blue2;
			this.blue3 = blue3;
		}
		
		public void setResults(String time, int redScore, int blueScore) {
			playedTime = time;
			this.redScore = redScore;
			this.blueScore = blueScore;
			if(redScore > blueScore) {
				status = RED;
			} else if (redScore < blueScore) {
				status = BLUE;
			} else if (redScore == blueScore) {
				status = TIE;
			}
		}
		
		int status = NOT_PLAYED;
		public int getStatus() {
			return status;
		}

		private String time;
		public String getTime() {
			return time;
		}
		
		private String playedTime;
		public String getPlayedTime() {
			return playedTime;
		}
		
		private int number;
		public int getMatchNumber() {
			return number;
		}
		
		private int redScore;
		public int getRedScore() {
			return redScore;
		}
		
		private int red1;
		public int getRed1() {
			return red1;
		}
		
		private int red2;
		public int getRed2() {
			return red2;
		}
		
		private int red3;
		public int getRed3() {
			return red3;
		}

		private int blueScore;
		public int getBlueScore() {
			return blueScore;
		}
		
		private int blue1;
		public int getBlue1() {
			return blue1;
		}
		
		private int blue2;
		public int getBlue2() {
			return blue2;
		}
		
		private int blue3;
		public int getBlue3() {
			return blue3;
		}
		
		public static final int NOT_PLAYED = -1;
		public static final int RED = 0;
		public static final int BLUE = 1;
		public static final int TIE = 2;
	}
	
	protected class createService extends AsyncTask <Boolean, Integer, Void> {
		@Override
		protected Void doInBackground(Boolean... params) {
			try {
				System.out.println("creating service");
				SERVICE = new SpreadsheetService("HawkScout");
				System.out.println("Finished creating service");
			} catch (IllegalArgumentException e) {
				System.out.println("creating PROBLEMMMMMM");
				e.printStackTrace();
			}
			if (params[0] == true) {
				try {
					SERVICE.setAuthSubToken(CREDENTIALS.getToken());
				} catch (UserRecoverableAuthException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				}
			}
					
			return null;
		}
	}
		
}