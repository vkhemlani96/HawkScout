package com.steelhawks.hawkscout.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.AsyncTask;

import com.google.api.services.drive.model.File;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.steelhawks.hawkscout.Globals;

public class ScoutingParameters {
	
	public ScoutingParameters (File f, Globals g) {
		DRIVE_FILE = f;
		App = g;
		new getSpreadsheetEntry().execute();
	}
	
	public Globals App;
	
	private File DRIVE_FILE;
	public File getDriveFile() {
		return DRIVE_FILE;
	}
	private WorksheetEntry WORKSHEET;
	public WorksheetEntry getWorksheet() {
		return WORKSHEET;
	}
	
	Map<String, List<Parameter>> categories = new HashMap<String, List<Parameter>>();
	public Map<String, List<Parameter>> getParameterLists() {
		return categories;
	}
	
	private getParameters GET_PARAMETERS = new getParameters();
	public getParameters getParameterTask() {
		return GET_PARAMETERS;
	}
	
	protected class getSpreadsheetEntry extends AsyncTask <Integer, Integer, Void> {
		@Override
		protected Void doInBackground(Integer... arg0) {
			try {
				WORKSHEET = App.getService().getEntry(
						new URL("https://spreadsheets.google.com/feeds/spreadsheets/"
								+ DRIVE_FILE.getId()), SpreadsheetEntry.class).getWorksheets().get(0);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			System.err.println("GOT WORKSHEET");
			GET_PARAMETERS.execute();
		}
	}
	
	public class getParameters extends AsyncTask <Integer, Integer, Void> {
		ListFeed listFeed;
		
		@Override
		protected Void doInBackground(Integer... arg0) {
			try {
				URL listFeedUrl = WORKSHEET.getListFeedUrl();
			    listFeed = App.getService().getFeed(listFeedUrl, ListFeed.class);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
		    for (ListEntry entry : listFeed.getEntries()) {
		    	Parameter param;
		    	String cat = entry.getCustomElements().getValue("Category");
		    	String title = entry.getCustomElements().getValue("Title");
		    	String type = entry.getCustomElements().getValue("Type");
		    	String opts = entry.getCustomElements().getValue("Options");
		    		param = new Parameter(title, type, opts);
		    	if (categories.containsKey(cat)) {
		    		categories.get(cat).add(param);
		    	} else {
		    		List<Parameter> list = new ArrayList<Parameter>();
		    		list.add(param);
		    		categories.put(cat, list);
		    	}
		    }
		}
	}
}
