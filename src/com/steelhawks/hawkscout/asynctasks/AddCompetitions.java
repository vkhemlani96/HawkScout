package com.steelhawks.hawkscout.asynctasks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.util.ServiceException;
import com.steelhawks.hawkscout.CompetitionMenu;
import com.steelhawks.hawkscout.Globals;
import com.steelhawks.hawkscout.Globals.Competition;
import com.steelhawks.hawkscout.Globals.UserTeam;
import com.steelhawks.hawkscout.R;

public class AddCompetitions extends AsyncTask <Integer, Integer, Void> {
	ProgressDialog pD;
	Context context;
	int teamIndex;
	List<Integer> selected;
	Globals App;
	
	
	public AddCompetitions (Context c, int i, List<Integer> l) {
		context = c;
		teamIndex = i;
		selected = l;
		App = (Globals) c.getApplicationContext();
	}
	
	protected void onPreExecute() {
		pD = ProgressDialog.show(context, null, "Adding Competitions...");
		
	}
	@Override
	protected Void doInBackground(Integer... params) {
		UserTeam team = App.getTeams().get(teamIndex);
		File parent = team.getTeamFolder();
		String[] compCode = context.getResources().getStringArray(R.array.comp_code);
		for (int x=0; x<selected.size(); x++) {
			System.out.println("creating comp");
			File file = new File();
			file.setTitle("(DO NOT DELETE) HawkScout2013." + compCode[selected.get(x)])
			.setMimeType("application/vnd.google-apps.spreadsheet")
			.setParents(Arrays.asList(new ParentReference().setId(parent.getId())));
			try {
				file = App.getDrive().files().insert(file).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			team.addCompetition(App.new Competition(file, true));
		}
		return null;
	}
	protected void onPostExecute(Void result) {
		pD.cancel();
		App.getTeams().get(teamIndex).sort();
		CompetitionMenu activity = (CompetitionMenu) context;
//		MenuItem item = activity.menu.findItem(R.id.action_refresh);
//			item.setActionView(new ProgressBar(activity));
//		activity.new Refresh().execute(item);
		activity.removeCompList();
		activity.createCompList(teamIndex);
	}
}