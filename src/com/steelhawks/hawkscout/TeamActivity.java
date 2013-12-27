package com.steelhawks.hawkscout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.steelhawks.hawkscout.Globals.Competition;
import com.steelhawks.hawkscout.asynctasks.DeletePitData;
import com.steelhawks.hawkscout.asynctasks.GetPitMedia;
import com.steelhawks.hawkscout.data.Parameter;
import com.steelhawks.hawkscout.dialogs.SimpleTextFragment;
import com.steelhawks.hawkscout.util.ProgressLayout;
import com.steelhawks.hawkscout.util.Utilities;

public class TeamActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private static String teamNumber;
	private static int teamIndex;
	private static int compIndex;
	private static Competition currentComp;
	private static Globals app;
	CharSequence[] TITLES = {"MATCH DATA", "TEAM INFO", "PIT DATA"};
	PitDataFragment frag2;
	private static final int RETURN_FROM_PIT = 466;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team);
		
		app = (Globals) getApplicationContext();

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
//		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Bundle extras = getIntent().getExtras();
		if (extras.getInt("com.steelhawks.hawkscout.CompetitionActivity.TEAM_INDEX", -1) != -1) {
			LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			teamIndex = extras.getInt("com.steelhawks.hawkscout.CompetitionActivity.TEAM_INDEX");
			compIndex = extras.getInt("com.steelhawks.hawkscout.CompetitionActivity.COMP_INDEX");
			currentComp = app.getTeams().get(teamIndex).getCompetitions().get(compIndex);
			teamNumber = extras.getString("com.steelhawks.hawkscout.CompetitionActivity.CURRENT_TEAM_NUMBER");
			
		    View v = inflator.inflate(R.layout.action_bar_view, null);
		    TextView tv = (TextView) v.findViewById(R.id.title);
		    	tv.setSingleLine(true);
		    	tv.setEllipsize(TruncateAt.END);
		    	tv.setTypeface(null, Typeface.BOLD);
		    	tv.setText(extras.getString("com.steelhawks.hawkscout.CompetitionActivity.CURRENT_TEAM_NUMBER"));
		    actionBar.setDisplayShowCustomEnabled(true);
		    actionBar.setDisplayShowTitleEnabled(false);
		    actionBar.setCustomView(v);
		}

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
		frag2 = new PitDataFragment();

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		mViewPager.setCurrentItem(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.team, menu);
		return true;
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
//			NavUtils.navigateUpFromSameTask(this);
//			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("Activity result. " + requestCode);
		switch(requestCode) {
			case RETURN_FROM_PIT: 
				if (resultCode == Activity.RESULT_OK)
					Toast.makeText(this, "Data Successfully Uploaded!", Toast.LENGTH_SHORT).show();
				else if (resultCode == Activity.RESULT_CANCELED)
					Toast.makeText(this, "Data was not uploaded.", Toast.LENGTH_SHORT).show();
				else Toast.makeText(this, "A problem has occurred.\nData was not uploaded.", Toast.LENGTH_SHORT).show();
				frag2.refresh();
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	public void refreshFragment(int i) {
		switch(i) {
		case 2: frag2.refresh();
			break;
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch(position) {
			case 2: return frag2;
			}
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_team_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	
	public static class PitDataFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		Map<String, List<Parameter>> params;
		ListEntry data;
		HashMap<String, String> editMap = new HashMap<String, String>();
		GetPitMedia mediaTask;
		File mediaStorageDir;

		public PitDataFragment() {
			data = currentComp.getPitData(teamNumber);
			if (data != null) {
				params = app.getTeams().get(teamIndex).getScoutingParams().getParameterLists();
				mediaTask = new GetPitMedia(app, teamIndex, currentComp.getCompCode(), teamNumber);
				mediaTask.execute();
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			if (app.getTeams().get(teamIndex).getScoutingParams().getParameterTask().getStatus()
					== Status.FINISHED) {
				if (data == null) data = currentComp.getPitData(teamNumber);
				return getNewView();
			} else {
				Runnable r = new Runnable() {
					public void run() {
						if (app.getTeams().get(teamIndex).getScoutingParams().getParameterTask()
								.getStatus() == Status.FINISHED) {
							data = currentComp.getPitData(teamNumber);
							refresh();
						} else {
							new Handler().postDelayed(this, 100);
						}
					}
				};
				new Handler().postDelayed(r, 100);
				return new ProgressLayout(getActivity());
			}
		}
		
		public void refresh() {
			data = currentComp.getPitData(teamNumber);
			if (data != null) params = app.getTeams().get(teamIndex).getScoutingParams().getParameterLists();
			mediaTask = new GetPitMedia(app, teamIndex, currentComp.getCompCode(), teamNumber);
			mediaTask.execute();
			if (getView() != null) ((ViewGroup) getView()).removeAllViews();
			((ViewGroup) getView()).addView(getNewView());
		}
		
		private View getNewView() {
			if (data == null) {
				RelativeLayout wrapper = new RelativeLayout(getActivity());
					LinearLayout content = new LinearLayout(getActivity());
						RelativeLayout.LayoutParams rL = new RelativeLayout.LayoutParams(
								LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						rL.addRule(RelativeLayout.CENTER_IN_PARENT);
						rL.leftMargin = PX(16);
						rL.rightMargin = PX(16);
					content.setOrientation(LinearLayout.VERTICAL);
					content.setLayoutParams(rL);
						ImageView i = new ImageView(getActivity());
							i.setImageDrawable(getResources().getDrawable(R.drawable.ic_content_pit_scouting_background));
					content.addView(i);
						TextView tV = new TextView(getActivity());
							tV.setText("Pit Scouting is a great way to scout out your opponents and prepare for a tough" +
									" match or a possible alliance selection. To begin taking notes on Team " + teamNumber
									 + " click the button below.");
							tV.setTextSize(18);
							tV.setGravity(Gravity.CENTER);
							tV.setPadding(0, PX(16), 0, PX(16));
					content.addView(tV);
						Button b = new Button(getActivity());
							b.setText("Begin Scouting");
							LayoutParams l = new LayoutParams(LayoutParams.MATCH_PARENT, PX(40));
								l.leftMargin = PX(4);
								l.rightMargin = PX(4);
								l.gravity = Gravity.CENTER_HORIZONTAL;
							b.setLayoutParams(l);
							b.setPadding(PX(4), 0, PX(4), 0);
							b.setOnClickListener(new OnClickListener(){
	
								@Override
								public void onClick(View arg0) {
									Intent i = new Intent(getActivity(), PitScouting.class);
										i.putExtra("com.steelhawks.hawkscout.TEAM_INDEX_PARAMETERS", teamIndex);
										i.putExtra("com.steelhawks.hawkscout.COMPETITION_INDEX", compIndex);
										i.putExtra("com.steelhawks.hawkscout.TEAM_SCOUTING", String.valueOf(teamNumber));
									System.out.println("" + TeamActivity.RETURN_FROM_PIT);
									getActivity().startActivityForResult(i, TeamActivity.RETURN_FROM_PIT);
								}});
					content.addView(b);
				wrapper.addView(content);
				return wrapper;
			} else {
				editMap.put("teamname", getParameterValue("teamname"));
				editMap.put("pitnumber", getParameterValue("pitnumber"));
				editMap.put("scoutedby", getParameterValue("scoutedby"));
				ScrollView wrapper = new ScrollView(getActivity());
					final LinearLayout body = new LinearLayout(getActivity());
						RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(
								LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
							contentParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						body.setLayoutParams(contentParams);
						body.setPadding(getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin),
								PX(12),
								getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin),
								getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));
						body.setOrientation(LinearLayout.VERTICAL);
						
						LinearLayout header = new LinearLayout(getActivity());
							header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, PX(48)));
							header.setOrientation(LinearLayout.HORIZONTAL);
							header.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_background));
							Date date = new Date(data.getEdited().getValue());
							Calendar c = Calendar.getInstance();
								c.setTime(date);
							TextView scouter = new TextView(getActivity());
								scouter.setText("Scouted by " + getParameterValue("scoutedby")
										+ System.getProperty("line.separator")
										+ (c.get(Calendar.HOUR) == 0 ? "12" : c.get(Calendar.HOUR)) + ":"
										+ (c.get(Calendar.MINUTE)<10 ? "0" + c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE))
										+ " " + c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US) + " on "
										+ c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " "
										+ c.get(Calendar.DATE) + ", " + c.get(Calendar.YEAR));
								scouter.setTextColor(getResources().getColor(R.color.gray));
								scouter.setTextSize(14);
								scouter.setPadding(PX(10), 0, 0, 0);
								scouter.setLayoutParams(new LinearLayout.LayoutParams(
										0, LayoutParams.MATCH_PARENT, 1.0f));
								scouter.setGravity(Gravity.CENTER_VERTICAL);
							header.addView(scouter);
							ImageButton edit = new ImageButton(getActivity());
								edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_content_edit));
								edit.setBackgroundDrawable(getResources().getDrawable(R.drawable.crimson_background));
								edit.setLayoutParams(new LayoutParams(PX(48), PX(48)));
								edit.setOnClickListener(editListener);
							header.addView(edit);
							View vertSep = new View(getActivity());
								LayoutParams sepParams = new LayoutParams(1, PX(30));
									sepParams.gravity = Gravity.CENTER_VERTICAL;
								vertSep.setLayoutParams(sepParams);
								vertSep.setBackgroundDrawable(
										getResources().getDrawable(android.R.drawable.divider_horizontal_bright));
							header.addView(vertSep);
							ImageButton delete = new ImageButton(getActivity());
								delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_content_discard));
								delete.setBackgroundDrawable(getResources().getDrawable(R.drawable.crimson_background));
								delete.setLayoutParams(new LayoutParams(PX(48), PX(48)));
								delete.setOnClickListener(deleteListener);
							header.addView(delete);
						body.addView(header);
					List<String> keys = new ArrayList<String>(params.keySet());
					Collections.reverse(keys);
					int screenWidth = getResources().getDisplayMetrics().widthPixels - PX(40);
					for (String key : keys) {
						TextView title = new TextView(getActivity());
							title.setText(key.toUpperCase(Locale.ENGLISH));
							title.setTextColor(getResources().getColor(R.color.red));
								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
									lp.gravity = Gravity.CENTER_VERTICAL;
							title.setLayoutParams(lp);
							title.setPadding(PX(4), PX(0), PX(4), PX(4));
							title.setTextSize(14);
							title.setTypeface(null, Typeface.BOLD);
						body.addView(title);
						View redSep = new View(getActivity());
							redSep.setBackgroundColor(getResources().getColor(R.color.red));
							redSep.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,2));
						body.addView(redSep);
						List<Parameter> paramList = params.get(key);
						for (int x = 0; x < paramList.size(); x++) {
							Parameter param = paramList.get(x);
							StringBuilder s = new StringBuilder("<b>" + param.getTitle() + " </b>");								
								String paramValue = getParameterValue(key + "." + param.getTitle());
								paramValue = paramValue != null ? paramValue : "N/A";
								s.append(paramValue);
								if (paramValue.equals("N/A")) editMap.put(key + "." + param.getTitle(), null);
								else editMap.put(key + "." + param.getTitle(), paramValue);
							TextView t = new TextView(getActivity());
								LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
								l.gravity = Gravity.CENTER_VERTICAL;
							t.setLayoutParams(l);
							t.setMinimumHeight(PX(48));
							t.setText(Html.fromHtml(s.toString()));
							t.setTextSize(18);
							t.measure(0, 0);
							if (t.getMeasuredWidth() <= screenWidth) {
								paramValue = paramValue != null ? paramValue : "N/A";
								RelativeLayout parent = new RelativeLayout(getActivity());
									LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(
											LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
										parentParams.leftMargin = PX(4);
										parentParams.rightMargin = PX(4);
									parent.setLayoutParams(parentParams);
									parent.setGravity(Gravity.CENTER);
									parent.setMinimumHeight(PX(48));
									parent.setPadding(0, PX(4), 0, PX(4));
									TextView paramTitle = new TextView(getActivity());
									paramTitle.setText(param.getTitle());
									paramTitle.setTextSize(18);
									paramTitle.setTypeface(null, Typeface.BOLD);
										RelativeLayout.LayoutParams lP = new RelativeLayout.LayoutParams(
												LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
										lP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
									paramTitle.setLayoutParams(lP);
								parent.addView(paramTitle);
									
									TextView paramText = new TextView(getActivity());
									paramText.setText(paramValue);
									paramText.setTextSize(18);
										RelativeLayout.LayoutParams lP1 = new RelativeLayout.LayoutParams(
												LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
										lP1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
									paramText.setLayoutParams(lP1);
								parent.addView(paramText);	
							body.addView(parent);
							} else {
								TextView paramTitle = new TextView(getActivity());
								paramTitle.setText(param.getTitle());
								paramTitle.setTextSize(18);
								paramTitle.setTypeface(null, Typeface.BOLD);
									LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(
											LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
									lP.topMargin = PX(4);
									lP.leftMargin = PX(4);
									lP.rightMargin = PX(4);
								paramTitle.setLayoutParams(lP);
							body.addView(paramTitle);
								TextView paramText = new TextView(getActivity());
								paramText.setText(paramValue);
								paramText.setTextSize(18);
									LinearLayout.LayoutParams lP1 = new LinearLayout.LayoutParams(lP);
									lP1.leftMargin = PX(12);
									lP1.rightMargin = PX(4);
									lP1.topMargin = 0;
									lP1.bottomMargin = PX(4);
								paramText.setLayoutParams(lP1);
							body.addView(paramText);
							}
							if (x != paramList.size()-1) {
								View sep = new View(getActivity());
									sep.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
									sep.setBackgroundDrawable(
											getResources().getDrawable(android.R.drawable.divider_horizontal_bright));
								body.addView(sep);
							}
						}
					}
					TextView title = new TextView(getActivity());
						title.setText("MEDIA");
						title.setTextColor(getResources().getColor(R.color.red));
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
								lp.gravity = Gravity.CENTER_VERTICAL;
						title.setLayoutParams(lp);
						title.setPadding(PX(4), PX(0), PX(4), PX(4));
						title.setTextSize(14);
						title.setTypeface(null, Typeface.BOLD);
					body.addView(title);
					View redSep = new View(getActivity());
						redSep.setBackgroundColor(getResources().getColor(R.color.red));
						redSep.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,2));
					body.addView(redSep);
					
					if (mediaTask.getStatus() != AsyncTask.Status.FINISHED) {
						LinearLayout progress = new LinearLayout(getActivity());
						progress.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, PX(48)));
						progress.setOrientation(LinearLayout.HORIZONTAL);
						progress.setGravity(Gravity.CENTER);
						
						ProgressBar pBar = new ProgressBar(getActivity(), null,
								android.R.style.Widget_Holo_Light_ProgressBar_Small);
							pBar.setIndeterminate(true);
						progress.addView(pBar);
						
						TextView tView = new TextView(getActivity());
						tView.setText("Checking for Media...");
						tView.setTextSize(18);
						progress.addView(tView);
						
						body.addView(progress);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								if (mediaTask.getStatus() == AsyncTask.Status.FINISHED)
									addMediaViews(body, true);
								else new Handler().postDelayed(this, 100);
							}
						}, 100);
					} else addMediaViews(body, false);
					
				wrapper.addView(body);
				return wrapper;
			}
		}
		
		private OnClickListener editListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent edit = new Intent(getActivity(), com.steelhawks.hawkscout.PitScouting.class);
				edit.putExtra("com.steelhawks.hawkscout.TEAM_INDEX_PARAMETERS", teamIndex);
				edit.putExtra("com.steelhawks.hawkscout.COMPETITION_INDEX", compIndex);
				edit.putExtra("com.steelhawks.hawkscout.TEAM_SCOUTING", String.valueOf(teamNumber));
				edit.putExtra("com.steelhawks.hawkscout.EDIT_DETAILS", editMap);
				getActivity().startActivityForResult(edit, RETURN_FROM_PIT);				
			}
		};
		
		private OnClickListener deleteListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				new SimpleTextFragment().newInstance("Delete Data?", "Are you sure you want to permanently delete " +
						"this data?", "Delete", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								new DeletePitData(getActivity(), currentComp, teamNumber).execute();
							}
						}, true).show(getActivity().getSupportFragmentManager(), "DELETE_DATA");
			}
		};
		
		TextView getEmptyMediaView() {
			TextView empty = new TextView(getActivity());
			empty.setTextSize(18);
			empty.setText("No Pictures or Videos Taken");
			empty.setGravity(Gravity.CENTER);
			empty.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, PX(48)));
			return empty;
		}
		
		void addMediaViews(LinearLayout layout, boolean remove) {
			mediaStorageDir = new File(
					getActivity().getExternalFilesDir(
							Environment.DIRECTORY_PICTURES), "Pit Scouting");

			if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            Log.d("HawkScout", "failed to create directory");
		        }
		    }
			
			final LinearLayout wrapper = layout;
			if (remove) wrapper.removeViewAt(wrapper.getChildCount()-1);
			
			if (mediaTask.getFiles().size() == 0) {
				wrapper.addView(getEmptyMediaView());
				return;
			}
			
			int maxMediaPerRow = (getResources().getDisplayMetrics().widthPixels - PX(32)) / PX(158);
			int currentMediaInRow = 0;
			
			LinearLayout currentLayout = null;
			
			for (final com.google.api.services.drive.model.File file : mediaTask.getFiles()) {
				final String filePath = mediaStorageDir.getPath() + "/" + file.getTitle();
				
				currentMediaInRow++;
				if (currentMediaInRow > maxMediaPerRow)	currentMediaInRow = 1;
				
				final int picDimension = 
						(getResources().getDisplayMetrics().widthPixels - PX(32) - (maxMediaPerRow-1) * PX(8))
						/ maxMediaPerRow;
				
				
				if (currentMediaInRow == 1) {
					currentLayout = new LinearLayout(getActivity());
					LayoutParams LLP = new LayoutParams(LayoutParams.MATCH_PARENT, picDimension);
						LLP.topMargin = PX(8);
					currentLayout.setLayoutParams(LLP);
					currentLayout.setOrientation(LinearLayout.HORIZONTAL);
					currentLayout.setGravity(Gravity.CENTER);
					wrapper.addView(currentLayout, wrapper.getChildCount());
				}
				
				final ViewFlipper vF = new ViewFlipper(getActivity());
				LinearLayout.LayoutParams lP = new LinearLayout.LayoutParams(picDimension, picDimension);
					if (currentMediaInRow > 1) lP.leftMargin = PX(8);
				vF.setLayoutParams(lP);
				vF.setInAnimation(getActivity(), R.anim.expand_in);
				vF.setOutAnimation(getActivity(), R.anim.shrink_out);
				
				ProgressBar pB = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyle);
					pB.setIndeterminate(true);
					ViewFlipper.LayoutParams pBparams = new ViewFlipper.LayoutParams(
							LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
						pBparams.gravity = Gravity.CENTER_HORIZONTAL;
					pB.setLayoutParams(pBparams);
				vF.addView(pB);
				
				final ImageView iV = new ImageView(getActivity());
				iV.setLayoutParams(lP);
				iV.setScaleType(ImageView.ScaleType.CENTER_CROP);
				
				final FrameLayout frameLayout = new FrameLayout(getActivity());
				
				LinearLayout.LayoutParams frameLayoutParams = new LinearLayout.LayoutParams(picDimension, picDimension);
					if (currentMediaInRow > 1) frameLayoutParams.leftMargin = PX(8);
				frameLayout.setLayoutParams(frameLayoutParams);
				frameLayout.addView(iV);	
				final TextView time = new TextView(getActivity());
				frameLayout.addView(time);

				final LinearLayout delete = new LinearLayout(getActivity());
				FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				delete.setLayoutParams(deleteParams);
				delete.setGravity(Gravity.CENTER);
				delete.setOrientation(LinearLayout.VERTICAL);
				delete.setBackgroundColor(Color.argb(200, 255, 255, 255));
				delete.setVisibility(View.GONE);
				
				TextView text = new TextView(getActivity());
				text.setTextSize(18);
				text.setGravity(Gravity.CENTER);
				text.setText(file.getMimeType().contains("image") ? "Delete Image?" : "Delete Video?");
				delete.addView(text);
				
				LinearLayout buttons = new LinearLayout(getActivity());
				buttons.setGravity(Gravity.CENTER_HORIZONTAL);

				ImageButton noButton = new ImageButton(getActivity());
					noButton.setImageDrawable(getResources().getDrawable(R.drawable.remove_icon));
					noButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.crimson_background));
					noButton.setLayoutParams(new LayoutParams(PX(48), PX(48)));
					noButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delete.setVisibility(View.GONE);
						}
					});
				buttons.addView(noButton);
				ImageButton yesButton = new ImageButton(getActivity());
					yesButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_accept));
					yesButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.crimson_background));
					yesButton.setLayoutParams(new LayoutParams(PX(48), PX(48)));
					yesButton.setOnClickListener(new OnClickListener() { 
						@Override
						public void onClick(View v) {
							PitDataFragment.this.delete.execute(iV);
						}
					});
				buttons.addView(yesButton);
				delete.addView(buttons);
				frameLayout.addView(delete);				
				
				if (!file.getMimeType().contains("image")) {	
					FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, PX(30));
						frameParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
					time.setLayoutParams(frameParams);
					time.setGravity(Gravity.CENTER_VERTICAL);
					time.setBackgroundColor(Color.parseColor("#80000000"));
				}
				
				new AsyncTask<String, Integer, Bitmap>() {
					String filePath;
					boolean image = file.getMimeType().contains("image");
					@Override
					protected Bitmap doInBackground(String... params) {
						filePath = params[0];
						return retry();
					}
					
					private Bitmap retry() {

						try {
							if (new File(filePath).exists()) {
								System.out.println("Found the file");
								if (image) return Utilities.createBitmapfromFile(
										filePath, picDimension, picDimension);
								else return ThumbnailUtils.createVideoThumbnail(
										filePath,
										MediaStore.Images.Thumbnails.MINI_KIND
								);

							//If not download it.
							} else {
								
								//Download file from Drive.								
								HttpResponse resp =
							            app.getDrive().getRequestFactory().buildGetRequest(
							            		new GenericUrl(file.getDownloadUrl())
							            ).execute();
								InputStream stream = resp.getContent();
								
								//Save file locally.
								FileOutputStream output = new FileOutputStream(filePath);
								byte[] buffer = new byte[1024];
								int len = 0;
								while ((len = stream.read(buffer)) != -1) {
								    output.write(buffer, 0, len);
								}
								output.close();
								
								if (image) return Utilities.createBitmapfromFile(filePath, picDimension, picDimension);
								else return ThumbnailUtils.createVideoThumbnail(
												filePath,
												MediaStore.Images.Thumbnails.MINI_KIND
								);
							}
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
							Log.wtf("TAG", "OUT OF MEMORY!!!!!!!!!!!!");
							System.gc();
							return retry();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
					
					protected void onPostExecute(Bitmap result) {
						if(!image) {
							final MediaPlayer mp = new MediaPlayer();
							try {
								mp.setDataSource(filePath);
								mp.prepare();
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								int duration = mp.getDuration();
								int minutes = duration / 60000;
								int seconds = (duration % 60000)/1000;
								StringBuilder sB = new StringBuilder("   " + String.valueOf(minutes));
									sB.append(":");
									if (seconds < 10) sB.append("0");
									sB.append(seconds);
									sB.append("   ");
								time.setTextColor(Color.WHITE);
								time.setText(sB);
							}
						}
						vF.addView(frameLayout);

						iV.setImageBitmap(
							ThumbnailUtils.extractThumbnail(result, picDimension, picDimension)
						);
						iV.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								if (delete.getVisibility() == View.GONE) delete.setVisibility(View.VISIBLE);
								return true;
							}
						});
						iV.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (delete.getVisibility() == View.GONE) {
									Intent intent = new Intent();
									intent.setAction(Intent.ACTION_VIEW);
									intent.setDataAndType(Uri.parse("file://" + filePath),
											file.getMimeType().contains("image") ? "image/*" : "video/*");
									startActivity(intent);
								}
							}
						});
						iV.setTag(new MediaTag(file.getId(), filePath));
						vF.showNext();
						
					}
				}.execute(filePath);
				
				currentLayout.addView(vF);
			}
		}
		
		String getParameterValue(String title) {
			return data.getCustomElements().getValue(getSpreadsheetString(title));
		}
		
		public String getSpreadsheetString(String s) {
			return s.replaceAll("[^A-Za-z0-9.]", "")
					.toLowerCase(Locale.ENGLISH);
		}
		
		public int PX(int dp) {
			return Utilities.PX(getActivity(), dp);
		}
		
		private class MediaTag {
			String fileId;
			String path;
			public MediaTag (String fileId, String path) {
				this.fileId = fileId;
				this.path = path;
			}
			
			public String getFileId() {
				return fileId;
			}
			
			public String getPath() {
				return path;
			}
		}
		
		AsyncTask<ImageView, ImageView,ImageView> delete = new AsyncTask<ImageView, ImageView, ImageView>() {


			@Override
			protected ImageView doInBackground(ImageView... view) {
				MediaTag tag = (MediaTag) view[0].getTag();
				try {
					app.getDrive().files().delete(tag.getFileId()).execute();
					new File(tag.getPath()).delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return view[0];
			}
			
			@Override
			protected void onPostExecute(final ImageView result) {
				Animation out = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_out);
				out.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {}
					
					@Override
					public void onAnimationRepeat(Animation animation) {}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						View flipper = (View) result.getParent().getParent();
						LinearLayout parent = (LinearLayout) flipper.getParent();
						LinearLayout wrapper = (LinearLayout) parent.getParent();
						if (parent.getChildCount() > 1) parent.removeView(flipper);
						else ((LinearLayout)parent.getParent()).removeView(parent);
						if (!(wrapper.getChildAt(wrapper.getChildCount()-2) instanceof LinearLayout))
							wrapper.addView(getEmptyMediaView());
					}
				});
				((View) result.getParent().getParent()).startAnimation(out);
			}
		};
	}
}