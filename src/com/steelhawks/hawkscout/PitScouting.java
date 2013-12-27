package com.steelhawks.hawkscout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.steelhawks.hawkscout.Globals.Competition;
import com.steelhawks.hawkscout.asynctasks.UpdatePitData;
import com.steelhawks.hawkscout.data.Parameter;
import com.steelhawks.hawkscout.data.ScoutingParameters;
import com.steelhawks.hawkscout.dialogs.PitScoutingReplaceFragment;
import com.steelhawks.hawkscout.dialogs.PitScoutingValidatorFragment;
import com.steelhawks.hawkscout.dialogs.SimpleTextFragment;
import com.steelhawks.hawkscout.util.MultiSelectSpinner;
import com.steelhawks.hawkscout.util.NoDefaultSpinner;
import com.steelhawks.hawkscout.util.ProgressLayout;
import com.steelhawks.hawkscout.util.Utilities;

public class PitScouting extends FragmentActivity {
	
	public Globals app;
	Competition currentComp;
	Intent data;
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	int picIndex = -1;
	int vidIndex = -1;
	HashMap<String, String> editMap = null;

	LinearLayout wrapper;
	List<File> mediaFiles = new ArrayList<File>();
	List<String> mediaNames = new ArrayList<String>();
	List<Uri> mediaUris = new ArrayList<Uri>();
	LinearLayout currentLayout = null;
	String lastPath;
	int maxMediaPerRow;
	int currentMediaInRow = 0;
	
	EditText teamName;
	EditText pitNumber;
	EditText scoutName;
	AutoCompleteTextView teamNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (Globals) getApplicationContext();
		
		data = new Intent();
		
		maxMediaPerRow = (getResources().getDisplayMetrics().widthPixels - PX(32)) / PX(158);

		currentComp = app.getTeams().get(getIntent().getExtras().getInt("com.steelhawks.hawkscout.TEAM_INDEX_PARAMETERS"))
				.getCompetitions().get(getIntent().getExtras().getInt("com.steelhawks.hawkscout.COMPETITION_INDEX"));

		final ScoutingParameters test = app.getTeams().get(getIntent().getExtras()
				.getInt("com.steelhawks.hawkscout.TEAM_INDEX_PARAMETERS")).getScoutingParams();
		if (test.getParameterTask().getStatus() != AsyncTask.Status.FINISHED) {
			setContentView(new ProgressLayout(this));
			
			Runnable r = new Runnable () {
				public void run() {
					if (test.getParameterTask().getStatus() == Status.FINISHED) {
						createScoutingLayout();
					} else {
						new Handler().postDelayed(this, 100);
					}
				}
			};
			new Handler().post(r);
		} else {
			createScoutingLayout();
		}	
		
		setupActionBar();
		
		//Check for Internet connection.
		System.out.println(Utilities.isNetworkConnecting(this));
		if (!Utilities.isNetworkConnecting(this)) {
			new SimpleTextFragment().newInstance("Internet Unavailable",
					"You are currently not connected to the Internet. " +
					"Make sure you have an active Internet connection before submitting this form.",
					"OK",
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
						
					}, false).show(getSupportFragmentManager(), "CONNECTION_CHECK");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pit_scouting, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	        	addMediaView(MEDIA_TYPE_IMAGE);
	        } else if (resultCode == RESULT_CANCELED) {
	            // TODO User cancelled the image capture
	        } else {
	            // TODO Image capture failed, advise user
	        }
	        return;
	    }
	
	    if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
	    	System.out.println("VIDEO INTENT");
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	        	addMediaView(MEDIA_TYPE_VIDEO);
	        } else if (resultCode == RESULT_CANCELED) {
	            // TODO User cancelled the video capture
	        } else {
	            // TODO Video capture failed, advise user
	        }
	        return;
	    }
	}

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.add_a_picture:
			Intent pic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			pic.putExtra(MediaStore.EXTRA_OUTPUT, getIntialMediaUri(MEDIA_TYPE_IMAGE));
			startActivityForResult(pic, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			break;
		case R.id.add_a_video:
			Intent vid = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			vid.putExtra(MediaStore.EXTRA_OUTPUT, getIntialMediaUri(MEDIA_TYPE_VIDEO));
			startActivityForResult(vid, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
			break;
		}
	}

	private Uri getIntialMediaUri(int type) {

		File mediaStorageDir = type == MEDIA_TYPE_IMAGE ?
				new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()) :
					new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath());
		
		if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("HawkScout", "failed to create directory");
	            return null;
	        }
	    }

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
			.format(new Date());
		String extension = type == MEDIA_TYPE_IMAGE ? ".jpg" : ".mp4";
		
		String path = mediaStorageDir.getPath() + File.separator + currentComp.getCompCode() + "_" +
				teamNumber.getText().toString() + "_" + timeStamp + extension;
		
		lastPath = path;
		
		return Uri.fromFile(new File(path));
	}

	private File getFinalMediaFile(int type) {

		File mediaStorageDir = new File(this.getExternalFilesDir(
	              Environment.DIRECTORY_PICTURES), "Pit Scouting");
		
		if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("HawkScout", "failed to create directory");
	            return null;
	        }
	    }

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
			.format(new Date());
		String extension = type == MEDIA_TYPE_IMAGE ? ".jpg" : ".mp4";
		
		String path = mediaStorageDir.getPath() + File.separator + currentComp.getCompCode() + "_" +
				teamNumber.getText().toString() + "_" + timeStamp + extension;
		
		return new File(path);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addMediaView(int mediaType) {
		if (currentMediaInRow == 0) wrapper.removeViewAt(wrapper.getChildCount()-2);
		currentMediaInRow++;
		if (currentMediaInRow > maxMediaPerRow) currentMediaInRow = 1;
		
		int picDimension = 
				(getResources().getDisplayMetrics().widthPixels - PX(32) - (maxMediaPerRow-1) * PX(8))
				/ maxMediaPerRow;
		
		
		String filePath;
		
		if (new File(lastPath).exists()) {
			filePath = lastPath;
		} else {
			//Get most recent gallery file
			String projection[] = new String[]{
		            MediaStore.Files.FileColumns.DATA,
		            MediaStore.Files.FileColumns.DATE_MODIFIED,
		    };
			Uri uri = mediaType == MEDIA_TYPE_IMAGE ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI
					: MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		    Cursor cursor = new CursorLoader(this, uri, projection, null, null,
		    		MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC").loadInBackground();
			if (cursor.getCount() == 0) {
				System.out.println("Cursor is 0");
				return;
			}
			else {
			    cursor.moveToFirst();
			    filePath = cursor.getString(0);
			    System.out.println(cursor.getString(0));
			}
			cursor.close();
		}
		
//		Uri uri;
//		if (mediaType == MEDIA_TYPE_IMAGE) uri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES));
//		else uri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_MOVIES));
//		
//		Cursor cursor = new CursorLoader(this, uri, projection, null, null,
//	    		MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC").loadInBackground();
//		if (cursor.getCount() == 0) {
//			uri = mediaType == MEDIA_TYPE_IMAGE ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//					: MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//		    cursor = new CursorLoader(this, uri, projection, null, null,
//		    		MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC").loadInBackground();
//		}
//		if (cursor.getCount() == 0) {
//			switch (mediaType) {
//			case MEDIA_TYPE_IMAGE: uri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(
//		            Environment.DIRECTORY_PICTURES));
//			case MEDIA_TYPE_VIDEO: uri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(
//		            Environment.DIRECTORY_MOVIES));
//			}
//			cursor = new CursorLoader(this, uri, projection, null, null,
//		    		MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC").loadInBackground();
//		}
//		if (cursor.getCount() == 0) {
//			System.out.println("Cursor is 0");
//			return;
//		}
//		else {
//		    cursor.moveToFirst();
//		    System.out.println(cursor.getString(0));
//		}
	    
	    //Move file to HawkScout folder
	    
	    final File oldFile = new File(filePath);
	    final File newFile = getFinalMediaFile(mediaType);
	    
	    System.out.println("File name:" + newFile.getName());
	    System.out.println("Path:" + newFile.getAbsolutePath());
	    
	    
	    FileChannel inChannel = null;
	    FileChannel outChannel = null;
	    try {
			inChannel = new FileInputStream(oldFile).getChannel();
		    outChannel = new FileOutputStream(newFile).getChannel();
		    inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inChannel.close();
				outChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    
	    mediaFiles.add(newFile);

		OnClickListener picClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + newFile.getAbsolutePath()), "image/*");
				PitScouting.this.startActivity(intent);
			}
		};
		
		OnClickListener vidClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + newFile.getAbsolutePath()), "video/*");
				PitScouting.this.startActivity(intent);
			}
		};
		
		if (currentMediaInRow == 1) {
			currentLayout = new LinearLayout(this);
			LayoutParams LLP = new LayoutParams(LayoutParams.MATCH_PARENT, picDimension);
				LLP.topMargin = PX(8);
			currentLayout.setLayoutParams(LLP);
			currentLayout.setOrientation(LinearLayout.HORIZONTAL);
			currentLayout.setGravity(Gravity.CENTER);
			wrapper.addView(currentLayout, wrapper.getChildCount()-1);
		}
		
		ImageView iV = new ImageView(this);
		iV.setScaleType(ImageView.ScaleType.CENTER_CROP);
		
		if (mediaType == MEDIA_TYPE_IMAGE) {
			LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(picDimension, picDimension);
				if (currentMediaInRow > 1) lP.leftMargin = PX(8);
			iV.setLayoutParams(lP);
			iV.setImageBitmap(
				ThumbnailUtils.extractThumbnail(
						Utilities.createBitmapfromFile(newFile.getAbsolutePath(), picDimension, picDimension),
						picDimension, picDimension)
			);
			iV.setOnClickListener(picClick);
			
			currentLayout.addView(iV);
		} else if (mediaType == MEDIA_TYPE_VIDEO) {
			FrameLayout layout = new FrameLayout(this);
			LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(picDimension, picDimension);
				if (currentMediaInRow > 1) lP.leftMargin = PX(8);
			layout.setLayoutParams(lP);
			layout.setOnClickListener(vidClick);
			
			Bitmap bm = 
					ThumbnailUtils.createVideoThumbnail(newFile.getAbsolutePath(),
							MediaStore.Images.Thumbnails.MICRO_KIND);
			iV.setImageBitmap(
				ThumbnailUtils.extractThumbnail(bm, picDimension, picDimension)
			);
			layout.addView(iV);
			
			TextView time = new TextView(this);
				FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, PX(30));
					frameParams.gravity = Gravity.BOTTOM;
			time.setLayoutParams(frameParams);
			time.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
			time.setBackgroundColor(Color.parseColor("#80000000"));
			MediaPlayer mp = new MediaPlayer();
			try {
				mp.setDataSource(newFile.getPath());
				mp.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				int duration = mp.getDuration();
				int minutes = duration / 60000;
				int seconds = (duration % 60000)/1000;
				StringBuilder sB = new StringBuilder(String.valueOf(minutes));
					sB.append(":");
					if (seconds < 10) sB.append("0");
					sB.append(seconds);
					sB.append("   ");
				time.setTextColor(Color.WHITE);
				time.setText(sB);
				layout.addView(time);
				
				currentLayout.addView(layout);
			}
		}
		
//		currentLayout.addView(iV);
		
		ScrollView parent = (ScrollView) wrapper.getParent();
		parent.fullScroll(ScrollView.FOCUS_DOWN);
		
	}

	@SuppressWarnings("unchecked")
	private void createScoutingLayout() {
		setContentView(R.layout.activity_pit_scouting);
		
		if (getIntent().getExtras().containsKey("com.steelhawks.hawkscout.EDIT_DETAILS")) {
			editMap = (HashMap<String, String>) getIntent().getExtras()
					.getSerializable("com.steelhawks.hawkscout.EDIT_DETAILS");
		} else {
			System.out.println("editmap not found");
		}
		
		
		wrapper = (LinearLayout) findViewById(R.id.pit_scouting_wrapper);
		
		teamName = ((EditText) findViewById(R.id.team_name_edittext));
		pitNumber = ((EditText) findViewById(R.id.pit_number_edittext));
		scoutName = ((EditText) findViewById(R.id.scouter_name_edittext));
		teamNumber = ((AutoCompleteTextView) findViewById(R.id.team_number_edittext));
			if (editMap != null) {
				teamName.setText(editMap.get("teamname") == null ? "" : editMap.get("teamname"));
				pitNumber.setText(editMap.get("pitnumber") == null ? "" : editMap.get("pitnumber"));
				scoutName.setText(editMap.get("scoutedby") == null ? "" : editMap.get("scoutedby"));
			}
			teamNumber.setText(getIntent().getExtras().getString("com.steelhawks.hawkscout.TEAM_SCOUTING",""));
			final List<String> teamNumbers = new ArrayList<String>();
			for(int x=0; x<currentComp.getTeamData().size(); x++) {
				teamNumbers.add(String.valueOf(currentComp.getTeamData().get(x).getTeamNumber()));
			}
			teamNumber.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item, teamNumbers));
			teamNumber.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.crimson_white_background));
			teamNumber.setOnFocusChangeListener(new OnFocusChangeListener(){
	
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					AutoCompleteTextView t = (AutoCompleteTextView) v;
					if (!hasFocus &&
						!t.getText().toString().equals("") &&
						!teamNumbers.contains(t.getText().toString())) {
						new PitScoutingValidatorFragment().newInstance(t.getText().toString(), v)
							.show(getSupportFragmentManager(), "VALIDATE_TEAM");
					}
				}});
			
	
		Map<String, List<Parameter>> params = app.getTeams().get(getIntent().getExtras()
				.getInt("com.steelhawks.hawkscout.TEAM_INDEX_PARAMETERS")).getScoutingParams().getParameterLists();
		Set<String> set = params.keySet();
		List<String> keys = new ArrayList<String>(set);
		Collections.reverse(keys);
		int index = 5;
		int id = 0;
		for (String key : keys) {
			TextView title = new TextView(this);
				title.setText(key.toUpperCase(Locale.ENGLISH));
				title.setTextColor(getResources().getColor(R.color.red));
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						lp.gravity = Gravity.CENTER_VERTICAL;
				title.setLayoutParams(lp);
				title.setPadding(PX(4), PX(4), PX(4), PX(4));
				title.setTextSize(14);
				title.setTypeface(null, Typeface.BOLD);
			wrapper.addView(title, index++);
			View redSep = new View(this);
				redSep.setBackgroundColor(getResources().getColor(R.color.red));
				redSep.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,2));
			wrapper.addView(redSep, index++);
			List<Parameter> paramList = params.get(key);
			for (Parameter param : paramList) {
				View v = getView(key, param);
				v.setId(id++);
				wrapper.addView(v, index++);
			}
		}
	}

	public String getExistingValue(String title) {
		if (editMap == null) return null;
		Set<String> set = editMap.keySet();
		List<String> keys = new ArrayList<String>();
		keys.addAll(set);
		
		for (int x = 0; x<editMap.size(); x++) {
			if (title.equals(keys.get(x))) return editMap.get(keys.get(x));
		}
		return null;
	}
	
	public String getSpreadsheetString(String s) {
		return s.replaceAll("[^A-Za-z0-9.]", "")
				.toLowerCase(Locale.ENGLISH);
	}
		
	public View getView(String key, Parameter p) {
		View v = null;
		String existingValue = getExistingValue(key + "." + p.getTitle());
		if (p.getType().contains(Parameter.FIXED_INPUT)) {
			List<String> opts = new ArrayList<String>(Arrays.asList(p.getOpts().split("!:!")));
			if (p.getType().contains(Parameter.OTHER)) opts.add("Other");
			if (p.getType().contains(Parameter.MULTIPLE_SELECTION)) {
				MultiSelectSpinner m = new MultiSelectSpinner(this);
				m.setPrompt(p.getTitle());
				if (existingValue != null) {
					String[] values = existingValue.split(", ");
					List<String> selectedValues = new ArrayList<String>();
					for (int x = 0; x<values.length; x++) {
						if (opts.contains(values[x])) {
							selectedValues.add(values[x]);
						} else if (opts.contains("Other")) {
							opts.add(opts.size()-2, values[x]);
							selectedValues.add(values[x]);
						} else {
							opts.add(existingValue);
							selectedValues.add(values[x]);
						}
					}
					m.setItems(opts);
					m.setSelection(selectedValues);
				} else {
					m.setItems(opts);
				}
				v = m;
			} else {
				NoDefaultSpinner s = new NoDefaultSpinner((Context) this, Spinner.MODE_DIALOG, opts);
					if (existingValue != null) {
						if (opts.contains(existingValue)) {
							ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_textview, opts);
								a.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
							s.setAdapter(a);
							for (int index = 0; index<opts.size(); index++) {
								if (opts.get(index).equals(existingValue)) {
									s.setSelection(index);
									break;
								}
							}
						} else if (opts.contains("Other")) {
							opts.add(opts.size()-1, existingValue);
							ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_textview, opts);
								a.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
							s.setAdapter(a);
							s.setSelection(opts.size()-2);
						}
					} else {
						ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_textview, opts);
							a.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
						s.setAdapter(a);
					}
					s.setPrompt(p.getTitle());
				v = s;
			}
			LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,PX(40));
				lP.topMargin = PX(4);
				lP.bottomMargin = PX(4);
			v.setLayoutParams(lP);
		} else if (p.getType().contains(Parameter.NUMERIC)) {
			EditText e = new EditText(this);
				if (p.getType().equals(Parameter.NUMERIC)) {
					e.setInputType(InputType.TYPE_CLASS_NUMBER);
					LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,PX(40));
						lP.topMargin = PX(4);
						lP.bottomMargin = PX(4);
					e.setLayoutParams(lP);
				} else {
					LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT
							, LayoutParams.WRAP_CONTENT);
						lP.topMargin = PX(4);
						lP.bottomMargin = PX(4);
					e.setLayoutParams(lP);
					e.setMinimumHeight(PX(40));
				}
			e.setHint(p.getTitle());
			e.setText(existingValue == null ? "" : existingValue);
			v = e;
		} else if (p.getType().equals(Parameter.BOOLEAN)) {
			CheckBox cb =  new CheckBox(this);
			cb.setText(p.getTitle());
			cb.setTextSize(18);
			if (existingValue == null) {
				cb.setTextColor(Color.parseColor("#808080"));
				cb.setChecked(false);
			}
			else cb.setChecked(existingValue.equals("Yes"));
			cb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					((CheckBox) arg0).setTextColor(Color.BLACK);
				}
				
			});
			v = cb;
			LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,PX(40));
				lP.topMargin = PX(4);
				lP.bottomMargin = PX(4);
			v.setLayoutParams(lP);
			
		}
		v.setTag(key + "." + p.getTitle());
		return v;
	}
	
	boolean isRequiredComplete() {
		if (teamNumber.getText().toString().equals("") ||
			teamName.getText().toString().equals("") ||
			pitNumber.getText().toString().equals("") ||
			scoutName.getText().toString().equals("")) return false;
		else return true;
	}
	
	boolean isComplete() {
		int id=0;
		while(findViewById(id) != null) {
			View v = findViewById(id);
			if (v instanceof EditText && ((EditText) v).getText().toString().trim().equals("")) {
				System.out.println("Failed at " + id + " from EditText");
				return false;
			} else if (v instanceof NoDefaultSpinner &&
					((String) ((NoDefaultSpinner) v).getPrompt())
					.equals(String.valueOf(((NoDefaultSpinner) v).getDisplayedText()))) {
				System.out.println("Failed at " + id + " from Spinner");
				return false;
			} else {
				id++;
				continue;
			}
		}
		return true;
	}
	
	public int PX (int dp) {
		final float scale = this.getResources().getDisplayMetrics().density;
		int px = (int) (dp*scale+0.5f);
		return px;
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
	    final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View customActionBarView = inflater.inflate(
	            R.layout.action_bar_done_cancel, null);
	    customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
	            new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    // "Done"
	                	if (!Utilities.isNetworkConnected(PitScouting.this)) {
	                		new SimpleTextFragment().newInstance("Internet Unavailable!",
	                				"You need to connect to the Internet to submit this form.",
	                				"OK",
	                				new DialogInterface.OnClickListener() {
	            						
	            						@Override
	            						public void onClick(DialogInterface dialog, int which) {
	            							dialog.cancel();
	            						}
	            						
	            					}, false).show(getSupportFragmentManager(), "NO_INTERNET");
							return;
	                	}
	                	if (!isRequiredComplete()) {
	                		new SimpleTextFragment().newInstance("Required Information Missing!",
	                				"You need to complete the \"Team Info\" section before submitting this form.",
	                				"OK",
	                				new DialogInterface.OnClickListener() {
	            						
	            						@Override
	            						public void onClick(DialogInterface dialog, int which) {
	            							dialog.cancel();
	            						}
	            						
	            					}, false).show(getSupportFragmentManager(), "TEAM_INFO");
							return;
	                	}
	                	if (!isComplete()) {
	                		new SimpleTextFragment().newInstance("Form Incomplete!",
	                				"You still have blank questions. Do you want to submit anyway?",
	                				"Yes",
	                				new DialogInterface.OnClickListener() {
	            						
	            						@Override
	            						public void onClick(DialogInterface dialog, int which) {
	            							dialog.cancel();
	            							update();
	            						}
	            						
	            					}, true).show(getSupportFragmentManager(), "FORM_IMCOMPLETE");
	                		return;
	                	}
	                	update();
	                }
	            });
	    customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
	            new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    // "Cancel"
	                	setResult(Activity.RESULT_CANCELED);
	                    finish();
	                }
	            });
	    final ActionBar actionBar = getActionBar();
	    actionBar.setDisplayOptions(
	            ActionBar.DISPLAY_SHOW_CUSTOM,
	            ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
	                    | ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setCustomView(customActionBarView,
	            new ActionBar.LayoutParams(
	                    ViewGroup.LayoutParams.MATCH_PARENT,
	                    ViewGroup.LayoutParams.MATCH_PARENT));
	}

	private void update() {
		final ProgressDialog pD = new ProgressDialog(this);
		pD.setMessage("Uploading Data...");
		pD.show();
		pD.setCancelable(false);
		Runnable update = new Runnable() {
			public void run() {
				ListEntry row = null;
				boolean update = false;
				try {
					URL existingRowUrl = new URI(currentComp.getPitScouting().getListFeedUrl().toString() + 
							"?sq=teamnumber=" + teamNumber.getText().toString()).toURL();
					ListFeed listFeed = app.getService().getFeed(existingRowUrl, ListFeed.class);
					List<ListEntry> entries = listFeed.getEntries();
					if (entries.size()>0) {
						System.err.println("Found existing row!");
						row = entries.get(0);
						update = true;
					}
					else row = new ListEntry();

    				row.getCustomElements().setValueLocal("teamnumber", teamNumber.getText().toString());
    				row.getCustomElements().setValueLocal("teamname", teamName.getText().toString());
    				row.getCustomElements().setValueLocal("pitnumber", pitNumber.getText().toString());
    				row.getCustomElements().setValueLocal("scoutedby", scoutName.getText().toString());
    				int x=0;
    				while (findViewById(x) != null) {
    					View v = findViewById(x);
    					String columnHeader = "";
						columnHeader = getSpreadsheetString((String) v.getTag());
//						System.out.println(columnHeader);
    					String value = "";
    					if (v instanceof EditText) {
    						value = ((EditText) v).getText().toString().trim();
    					} else if (v instanceof NoDefaultSpinner) {
    						String prompt =(String) ((NoDefaultSpinner) v).getPrompt();
    						String text = String.valueOf(((NoDefaultSpinner) v).getDisplayedText());
    						value = prompt.equals(text) ? "" : text;
    					} else if (v instanceof CheckBox) {
    						value = ((CheckBox) v).isChecked() ? "Yes" : "No";
    					} else {
        					x++;
    						continue;
    					}
    					row.getCustomElements().setValueLocal(columnHeader, value);
    					x++;
    				}
				} catch (Exception e) {
					e.printStackTrace();
				}

				final String n = teamNumber.getEditableText().toString();
				if (teamNumber == null) System.out.println("THE VIEW IS NULL");
				else if (teamNumber.getEditableText() == null) System.out.println("THE EDITABLE TEXT IS NULL");
				else if (teamNumber.getEditableText().toString() == null) System.out.println("THE TEXT IS NUll");
				else System.out.println("TEXT:" + n);
				System.out.println(update);
				System.out.println(editMap == null);
				if (update && editMap == null) {
					final ListEntry updateRow = row;
					runOnUiThread(new Runnable(){
						public void run() {
							System.out.println(n);
							new PitScoutingReplaceFragment().newInstance(PitScouting.this,
									n, updateRow, currentComp, pD, mediaFiles)
								.show(getSupportFragmentManager(), "UPDATE_TEAM");
						}
					});
//					app.addToDeleteUri(mediaUris);
					return;
				}
				else if (editMap != null) new UpdatePitData(PitScouting.this, app, currentComp, row, n, pD,
						mediaFiles).execute();
				else new UpdatePitData(PitScouting.this, app, currentComp, row, pD,
						mediaFiles).execute();
				app.addToDeleteUri(mediaUris);
			}
		};
		new Thread(update).start();
	}
}
