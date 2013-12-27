package com.steelhawks.hawkscout.asynctasks;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.steelhawks.hawkscout.Globals;
import com.steelhawks.hawkscout.Globals.Competition;

public class UpdatePitData extends AsyncTask <Integer, Integer, Void> {	

	boolean update;
	Activity context = null;
	ProgressDialog pD;
	
	ListEntry row;
	String teamNumber;
	
	Globals app;
	Competition currentComp;
	
	List<java.io.File> mediaFiles;
	
	int progress = 0;
	
	public UpdatePitData () {}
	
	public UpdatePitData (Activity context, Globals app, Competition currentComp,
			ListEntry row, String teamNumber, ProgressDialog pD, List<java.io.File> mediaPaths) {
		update = true;

		this.pD = pD;
		this.app = app;
		this.currentComp = currentComp;
		this.teamNumber = teamNumber;
		this.context = context;
		this.row = row;
		this.mediaFiles = mediaPaths;
	}
	
	public UpdatePitData (Activity context, Globals app, Competition currentComp,
			ListEntry row, ProgressDialog pD, List<java.io.File> mediaFiles) {
		update = false;
		
		this.row = row;
		this.pD = pD;
		this.context = context;
		this.app = app;
		this.currentComp = currentComp;
		this.mediaFiles = mediaFiles;
	}
	
	protected void onPreExecute() {
		
//		pD = new ProgressDialog(context);
//		pD.setMessage("Uploading..");
//		pD.show();
	}
	@Override
	protected Void doInBackground(Integer... params) {
		try {
			if (update) {
				row = row.update();
				System.out.println("editmap is not null");
			} else {
				URL listFeedUrl = currentComp.getPitScouting().getListFeedUrl();
				row = app.getService().insert(listFeedUrl,row);
			}
			for (java.io.File file : mediaFiles) {
				progress++;
				
				String mimeType = file.getAbsolutePath()
						.contains("mp4") ? "video/mp4" : "image/jpeg";
				
				File body = new File();
				
				body.setTitle(file.getName());
				body.setMimeType(mimeType);
				
				System.out.println("Parents size:" + currentComp.getDriveFile().getParents().size());
				
				body.setParents(
						Arrays.asList(new ParentReference().setId(
								currentComp.getDriveFile().getParents().get(0).getId()
						))
				);
				
				FileContent mediaContent = new FileContent(mimeType, file);
				
				publishProgress(progress);
				app.getDrive().files().insert(body, mediaContent).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	protected void onPostExecute(Void result) {
		context.runOnUiThread(new Runnable(){
			public void run() {
//				context.sendBroadcast(
//						new Intent(Intent.ACTION_MEDIA_UNMOUNTED,
//								Uri.parse("file://" + Environment.getExternalStorageState())));
				pD.dismiss();
			}
		});
		if (row == null) {
			System.err.println("SOMETHING WENT WRONG");
			context.setResult(Activity.RESULT_CANCELED);
			context.finish();
		} else if (update) currentComp.getPitData().remove(currentComp.getPitData(teamNumber));
	
		currentComp.getPitData().add(row);
		context.setResult(Activity.RESULT_OK);
		context.finish();
	}
	
	protected void onProgressUpdate(Integer...integers) {
		super.onProgressUpdate(integers);
		pD.setMessage("Uploading Media (" + integers[0] + ") ... ");
	}
}