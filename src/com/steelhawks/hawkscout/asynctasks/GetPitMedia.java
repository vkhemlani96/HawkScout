package com.steelhawks.hawkscout.asynctasks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.util.ServiceException;
import com.steelhawks.hawkscout.CompetitionMenu;
import com.steelhawks.hawkscout.Globals;
import com.steelhawks.hawkscout.Globals.Competition;
import com.steelhawks.hawkscout.Globals.UserTeam;
import com.steelhawks.hawkscout.R;

public class GetPitMedia extends AsyncTask <Integer, Integer, Void> {
	
	List<File> list = new ArrayList<File>();
	Globals app;
	int teamIndex;
	String compCode;
	String teamNumber;
	
	public GetPitMedia (Globals app, int teamIndex, String compCode, String teamNumber) {
		this.app = app;
		this.teamIndex = teamIndex;
		this.compCode = compCode;
		this.teamNumber = teamNumber;
	}
	
	protected void onPreExecute() {
		System.out.println("GET PIT MEDIA IS EXECUTING");
	}
	@Override
	protected Void doInBackground(Integer... params) {
		UserTeam team = app.getTeams().get(teamIndex);
		try {
			Files.List request = app.getDrive().files().list();
			request.setQ(
					"'" + team.getTeamFolder().getId() + "' in parents" +
					" and " +
					"title contains '" + compCode + "_" + teamNumber + "_'");
			  do {
			      FileList files = request.execute();
				  
			      list.addAll(files.getItems());
				  request.setPageToken(files.getNextPageToken());
			  } while (request.getPageToken() != null &&
				           request.getPageToken().length() > 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	protected void onPostExecute(Void result) {
		for (File file : list) {
			System.out.println("MimeType:" + file.getMimeType());
		}
	}
	
	public List<File> getFiles() {
		return list;
	}
}