package com.steelhawks.hawkscout.dialogs.pitscouting;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.util.ServiceException;
import com.steelhawks.hawkscout.Globals;
import com.steelhawks.hawkscout.Globals.Competition;
import com.steelhawks.hawkscout.PitScouting;
import com.steelhawks.hawkscout.util.DialogBuilder;

public class PitScoutingReplaceFragment extends DialogFragment {
	
	String teamNumber;
	Competition currentComp;
	ListEntry row;
	ProgressDialog pD;
	PitScouting activity;
	boolean finished = false;
	static List<ListEntry> list;
	List<java.io.File> files;
	int progress = 0;
	
	public PitScoutingReplaceFragment() {}
	
	public PitScoutingReplaceFragment newInstance(PitScouting c, String s, ListEntry l, Competition co, ProgressDialog pD,
			List<java.io.File> files){
		activity = c;
		teamNumber = s;
		row = l;
		list = co.getPitData();
		currentComp = co;
		this.pD = pD;
		this.files = files;
		return this;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
				DialogBuilder dialog = new DialogBuilder(getActivity());
					dialog.setTitle("Overwrite Data?")
						.setMessage("Data for Team " + teamNumber + " already exists. Do you want to overwrite it?")
						.setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								new Thread(new Runnable() {
										public void run() {
											System.out.println("Clicked!!!!!");
											try {
												row = row.update();
												for (java.io.File file : files) {
													progress++;
													
													String mimeType = file.getAbsolutePath()
															.contains("mp4") ? "video/mp4" : "image/jpeg";
													
													File body = new File();
													
													body.setTitle(file.getName());
													body.setMimeType(mimeType);
													
													System.out.println("Parents size:" + currentComp.getDriveFile()
															.getParents().size());
													
													body.setParents(
															Arrays.asList(new ParentReference().setId(
																	currentComp.getDriveFile().getParents().get(0).getId()
															))
													);
													
													FileContent mediaContent = new FileContent(mimeType, file);
													activity.runOnUiThread(new Runnable(){
														public void run() {
															pD.setMessage(
																	"Uploading Media (" + progress + "/" + 
																			files.size() + ")");
														}
													});
													((Globals) activity.getApplicationContext())
													.getDrive().files().insert(body, mediaContent).execute();
												}
											} catch (IOException e) {
												e.printStackTrace();
											} catch (ServiceException e) {
												e.printStackTrace();
											} finally {
												activity.runOnUiThread(new Runnable(){
													public void run() {
														pD.dismiss();
													}
												});
												if(row != null){
													System.out.println("UPDATED");
													if(currentComp.getPitData(teamNumber) != null) {
														list.remove(currentComp.getPitData(teamNumber));
													}
													list.add(row);
													activity.setResult(Activity.RESULT_OK);
													activity.finish();
												}
											}
										}
								}).start();
							}
							
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								System.out.println("Negative Clicked!!!!");
								activity.runOnUiThread(new Runnable(){
									public void run() {
										pD.dismiss();
									}
								});
								dialog.cancel();
							}
						});
			return dialog.create();
	}
	
	public int PX (int dp) {
		final float scale = this.getResources().getDisplayMetrics().density;
		int px = (int) (dp*scale+0.5f);
		return px;
	}
}