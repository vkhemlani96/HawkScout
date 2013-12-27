package com.steelhawks.hawkscout.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.gdata.data.spreadsheet.ListEntry;
import com.steelhawks.hawkscout.Globals.Competition;
import com.steelhawks.hawkscout.TeamActivity;

public class DeletePitData extends AsyncTask <Integer, Integer, Void> {	

	ListEntry row;
	Competition currentComp;
	String teamNumber;
	ProgressDialog pD;
	Activity context;
	
	public DeletePitData (Activity context, Competition currentComp, String teamNumber) {
		this.context = context;
		this.currentComp = currentComp;
		this.teamNumber = teamNumber;
		this.row = currentComp.getPitData(teamNumber);
	}
	
	protected void onPreExecute() {
		pD = ProgressDialog.show(context, null, "Deleting Data...");
	}
	@Override
	protected Void doInBackground(Integer... params) {
		try {
			row.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
	protected void onPostExecute(Void result) {
		currentComp.getPitData().remove(currentComp.getPitData(teamNumber));
		((TeamActivity) context).refreshFragment(2);
		pD.dismiss();
	}
}