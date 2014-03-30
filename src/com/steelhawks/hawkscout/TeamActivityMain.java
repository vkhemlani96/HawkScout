package com.steelhawks.hawkscout;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
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

import com.steelhawks.hawkscout.data.Competition;
import com.steelhawks.hawkscout.data.Indices.MatchIndex;
import com.steelhawks.hawkscout.data.Indices.MatchScoutingIndex;
import com.steelhawks.hawkscout.data.Indices.PossessionIndex;
import com.steelhawks.hawkscout.data.Parameter;
import com.steelhawks.hawkscout.teamactivity.MatchesViewPager;
import com.steelhawks.hawkscout.util.GraphView;
import com.steelhawks.hawkscout.util.Utilities;

public class TeamActivityMain extends FragmentActivity implements
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
	private static final String ACTIVITY_INTENT_1 = "com.steelhawks.hawkscout.TEAM_ACTIVITY_INTENT.TEAM_NUMBER";
	Fragment[] frag = new Fragment[3];
	private static final int RETURN_FROM_PIT = 466;
	private static FragmentManager fm;

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
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		currentComp = new Competition(this, "SCMB");
		teamNumber = getIntent().getExtras().getString(ACTIVITY_INTENT_1);

		View v = inflator.inflate(R.layout.action_bar_view, null);
		TextView tv = (TextView) v.findViewById(R.id.title);
		tv.setSingleLine(true);
		tv.setEllipsize(TruncateAt.END);
		tv.setTypeface(null, Typeface.BOLD);
		tv.setText(teamNumber);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(v);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		fm = getSupportFragmentManager();
		mSectionsPagerAdapter = new SectionsPagerAdapter(fm);

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
		frag[0] = new MatchDataParentFragment();
		frag[1]	= new TeamFragment();
		frag[2] = new PitDataFragment();

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

		List<String> matches = currentComp.getMatchesByTeam(teamNumber);
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
			((PitDataFragment) frag[2]).refresh();
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

	public static void start(Activity activity, String teamNumber) {
		Intent i = new Intent(activity, com.steelhawks.hawkscout.TeamActivityMain.class);
		i.putExtra(ACTIVITY_INTENT_1, teamNumber);
		activity.startActivity(i);
	}

	public void refreshFragment(int i) {
		switch(i) {
		case 2: ((PitDataFragment) frag[2]).refresh();
		break;
		}
	}

	protected void onResume() {
		//		frag2.refresh();
		super.onResume();
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
			if (position < frag.length) {
				return frag[position];
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
			LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_team_dummy,
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
		String[] data;

		public PitDataFragment() {
			params = currentComp.getScoutingParams();
			data = currentComp.getPitScoutingForTeam(teamNumber);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return getNewView();
		}

		public void refresh() {
			data = currentComp.getPitScoutingForTeam(teamNumber);
			if (data != null) params = app.getTeams().get(teamIndex).getScoutingParams().getParameterLists();
			if (getView() != null) ((ViewGroup) getView()).removeAllViews();
			((ViewGroup) getView()).addView(getNewView());
		}

		@SuppressLint("NewApi")
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
						PitScoutingMain.start(getActivity(), teamNumber);
					}});
				content.addView(b);
				wrapper.addView(content);
				return wrapper;
			} else {
				String teamName = data[1].trim();
				String pitNumber = data[2].trim();
				String scoutedBy = data[3].trim();
				//TODO fix editMap
				//				editMap.put("teamname", getParameterValue("teamname"));
				//				editMap.put("pitnumber", getParameterValue("pitnumber"));
				//				editMap.put("scoutedby", getParameterValue("scoutedby"));
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
				//TODO fix time
				//							Date date = new Date(data.getEdited().getValue());
				//							Calendar c = Calendar.getInstance();
				//								c.setTime(date);
				TextView scouter = new TextView(getActivity());
				scouter.setText("Scouted by " + scoutedBy);
				//										+ System.getProperty("line.separator")
				//										+ (c.get(Calendar.HOUR) == 0 ? "12" : c.get(Calendar.HOUR)) + ":"
				//										+ (c.get(Calendar.MINUTE)<10 ? "0" + c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE))
				//										+ " " + c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US) + " on "
				//										+ c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " "
				//										+ c.get(Calendar.DATE) + ", " + c.get(Calendar.YEAR));
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
				edit.setOnClickListener(getEditListener(data));
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
				//TODO fix delete data
				//								delete.setOnClickListener(deleteListener);
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
						s.append(paramValue);
						//TODO Fix editMap;
						//								if (paramValue.equals("N/A")) editMap.put(key + "." + param.getTitle(), null);
						//								else editMap.put(key + "." + param.getTitle(), paramValue);
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

				addMediaViews(body, false);

				wrapper.addView(body);
				return wrapper;
			}
		}


		//		private OnClickListener deleteListener = new OnClickListener(){
		//			@Override
		//			public void onClick(View v) {
		//				new SimpleTextFragment().newInstance("Delete Data?", "Are you sure you want to permanently delete " +
		//						"this data?", "Delete", new DialogInterface.OnClickListener() {
		//							@Override
		//							public void onClick(DialogInterface dialog, int which) {
		//								new DeletePitData(getActivity(), currentComp, teamNumber).execute();
		//							}
		//						}, true).show(getActivity().getSupportFragmentManager(), "DELETE_DATA");
		//			}
		//		};

		private String getParameterValue(String key) {
			for (int x=0; x<data.length; x++) {
				String[] keyAndValue = data[x].split(Competition.PIT_SCOUTING_KEY_SEPARATOR);
				if (keyAndValue[0].equals(key)) return keyAndValue.length>1 ? keyAndValue[1] : "N/A";
			}
			return "COULDNT FIND IT";
		}

		private OnClickListener getEditListener(String[] data) {
			final String[] finalData = data;
			OnClickListener editListener = new OnClickListener(){
				@Override
				public void onClick(View v) {
					System.out.println("DataFinal is null:" + String.valueOf(finalData == null));
					PitScoutingMain.start(getActivity(), teamNumber, finalData);			
				}
			};
			return editListener;
		}

		TextView getEmptyMediaView() {
			TextView empty = new TextView(getActivity());
			empty.setTextSize(18);
			empty.setText("No Pictures or Videos Taken");
			empty.setGravity(Gravity.CENTER);
			empty.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, PX(48)));
			return empty;
		}

		void addMediaViews(LinearLayout layout, boolean remove) {

			List<String> mediaPaths = currentComp.getMediaPathsforTeam(teamNumber);

			final LinearLayout wrapper = layout;
			if (remove) wrapper.removeViewAt(wrapper.getChildCount()-1);

			if (mediaPaths.size() == 0) {
				wrapper.addView(getEmptyMediaView());
				return;
			}

			int maxMediaPerRow = (getResources().getDisplayMetrics().widthPixels - PX(32)) / PX(158);
			int currentMediaInRow = 0;

			LinearLayout currentLayout = null;

			for (int x=0; x<mediaPaths.size(); x++) {
				final String filePath = mediaPaths.get(x);
				System.out.println(filePath);

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
				text.setText(filePath.contains("jpg") ? "Delete Image?" : "Delete Video?");
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
						//TODO delete
						//							PitDataFragment.this.delete.execute(iV);
					}
				});
				buttons.addView(yesButton);
				delete.addView(buttons);
				frameLayout.addView(delete);				

				if (filePath.contains("jpg")) {	
					FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, PX(30));
					frameParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
					time.setLayoutParams(frameParams);
					time.setGravity(Gravity.CENTER_VERTICAL);
					time.setBackgroundColor(Color.parseColor("#80000000"));
				}
				Bitmap result = retry(filePath, picDimension);
				if(!filePath.contains("jpg")) {
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
									filePath.contains("jpg") ? "image/*" : "video/*");
							startActivity(intent);
						}
					}
				});
				iV.setTag(filePath);
				vF.showNext();
				currentLayout.addView(vF);
			}
		}

		private Bitmap retry(String filePath, int picDimension) {
			try {
				if (new File(filePath).exists()) {
					System.out.println("Found the file");
					if (filePath.contains("jpg")) return Utilities.createBitmapfromFile(
							filePath, picDimension, picDimension);
					else return ThumbnailUtils.createVideoThumbnail(
							filePath,
							MediaStore.Images.Thumbnails.MINI_KIND
							);
				} else System.out.println("THE FILE DOESNT EXIST");
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				Log.wtf("TAG", "OUT OF MEMORY!!!!!!!!!!!!");
				System.gc();
				return retry(filePath, picDimension);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public int PX(int dp) {
			return Utilities.PX(getActivity(), dp);
		}		
	}

	public static class MatchDataParentFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			MatchesViewPager rootView = 
					(MatchesViewPager) inflater.inflate(R.layout.activity_team_match_data, container, false);
			rootView.setArguments(getActivity(), currentComp, teamNumber, this.getChildFragmentManager());
			return rootView;
		}

	}

	public static class TeamFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public TeamFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ScrollView root = (ScrollView) inflater.inflate(R.layout.activity_team_stats,
					container, false);

			LayoutParams wrap = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			float totalMatches = 0.f;
			int totalPoints = 0, totalFouls = 0, totalBlocksDeflections = 0, totalAllianceScore = 0, possessionCount = 0, autonHighGoalMade = 0,
					autonHighGoalHot = 0, autonHighGoalTotal = 0, autonLowGoalMade = 0, autonLowGoalHot = 0, autonLowGoalTotal = 0, teleopHighGoalMade = 0,
					teleopHighGoalTotal = 0, teleopLowGoalMade = 0, teleopLowGoalTotal = 0, trussMade = 0, trussTotal = 0, catches = 0, fouls = 0, 
					techFouls = 0, passesFromHP = 0, passesToHP = 0, passesFromRobo = 0, passesToRobo = 0, possessionTime = 0;
			int HPtoRobot = 0, robotToRobot = 0, robotToGoalHighMade = 0, robotToGoalHighMissed = 0, robotToGoalLowMade = 0, robotToGoalLowMissed = 0, 
					robotToTruss = 0, robotToTrussMissed = 0, HPtoTruss = 0, HPToTrussMissed = 0, catchToGoalHighMade = 0, catchToGoalHighMissed = 0, catchToGoalLowMissed = 0, catchToGoalLowMade = 0,
					HPToGoalHighMade = 0, HPToGoalHighMissed = 0, HPToGoalLowMissed = 0, HPToGoalLowMade = 0,
					pickUpToGoalHighMade = 0, pickUpToGoalHighMissed = 0, pickUpToGoalLowMissed = 0, pickUpToGoalLowMade = 0;

			LinearLayout graph = new LinearLayout(getActivity());
			RelativeLayout.LayoutParams relativeWrap = new RelativeLayout.LayoutParams(wrap);
			relativeWrap.addRule(RelativeLayout.CENTER_HORIZONTAL);
			relativeWrap.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			graph.setLayoutParams(relativeWrap);

			List<String> matches = currentComp.getMatchesByTeam(teamNumber);
			for (int x=0; x<matches.size(); x++) {
				String[] data = currentComp.getMatchScoutingDataForTeam(matches.get(x), teamNumber);
				if (data != null) {
					totalMatches++;
					System.out.println(totalMatches);
					totalPoints += getPointsScoredForMatch(data);
					totalFouls += getFoulsForMatch(data);
					totalBlocksDeflections += Integer.parseInt(data[MatchScoutingIndex.TELEOP_BLOCKS].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_BLOCKS].trim()) + 
							Integer.parseInt(data[MatchScoutingIndex.DEFLECTIONS].trim());
					totalAllianceScore += getAllianceScore(data);
					possessionCount += data[MatchScoutingIndex.POSSESSIONS].trim().split("\\|\\|").length;
					autonHighGoalMade += Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_COLD].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_HOT].trim());
					autonHighGoalHot += Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_HOT].trim());
					autonHighGoalTotal += Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_COLD].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_HIGH_GOAL_HOT].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_MISSED].trim());
					autonLowGoalMade += Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_COLD].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_HOT].trim());
					autonLowGoalHot += Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_HOT].trim());
					autonLowGoalTotal += Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_COLD].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_HOT].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_LOW_GOAL_MISSED].trim());
					teleopHighGoalMade += Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MADE].trim());
					teleopHighGoalTotal += Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MADE].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.TELEOP_HIGH_MISSED].trim());
					teleopLowGoalMade += Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MADE].trim());
					teleopLowGoalTotal += Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MADE].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.TELEOP_LOW_MISSED].trim());
					trussMade += Integer.parseInt(data[MatchScoutingIndex.TRUSS].trim());
					trussTotal += Integer.parseInt(data[MatchScoutingIndex.TRUSS].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.TRUSS_MISSED].trim());
					catches += Integer.parseInt(data[MatchScoutingIndex.CATCHES].trim());
					fouls += Integer.parseInt(data[MatchScoutingIndex.FOULS].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_FOULS].trim());	
					techFouls += Integer.parseInt(data[MatchScoutingIndex.TECH_FOULS].trim()) +
							Integer.parseInt(data[MatchScoutingIndex.AUTON_TECH_FOULS].trim());
					passesFromHP += Integer.parseInt(data[MatchScoutingIndex.PASSES_FROM_HP].trim());
					passesFromRobo += Integer.parseInt(data[MatchScoutingIndex.PASSES_FROM_ROBOT].trim());
					passesToHP += Integer.parseInt(data[MatchScoutingIndex.PASSES_TO_HP].trim());
					passesToRobo += Integer.parseInt(data[MatchScoutingIndex.PASSES_TO_ROBOT].trim());

					LinearLayout parent = new LinearLayout(getActivity());
					parent.setOrientation(LinearLayout.VERTICAL);
					parent.setLayoutParams(wrap);

					LayoutParams wrapWithMargins = (LayoutParams) parent.getLayoutParams();
					wrapWithMargins.leftMargin = wrapWithMargins.rightMargin = PX(4);
					wrapWithMargins.gravity = Gravity.BOTTOM;
					parent.setLayoutParams(wrapWithMargins);

					float pointsScored = getPointsScoredForMatch(data);
					float foulsAccumulated = getFoulsForMatch(data);
					foulsAccumulated = foulsAccumulated == 0 ? .7f : foulsAccumulated; 

					LinearLayout bars = new LinearLayout(getActivity());
					bars.setGravity(Gravity.BOTTOM);
					bars.setLayoutParams(wrap);
					bars.setOrientation(LinearLayout.HORIZONTAL);

					LinearLayout pointsBar = new LinearLayout(getActivity());
					pointsBar.setOrientation(LinearLayout.VERTICAL);

					TextView pointsNumber = new TextView(getActivity(), null, android.R.style.TextAppearance_Small);
					pointsNumber.setText(((int)pointsScored) + "");
					pointsNumber.setLayoutParams(new LayoutParams(PX(24), LayoutParams.WRAP_CONTENT));
					pointsNumber.setSingleLine();
					pointsNumber.setGravity(Gravity.CENTER_HORIZONTAL);
					pointsBar.addView(pointsNumber);

					View points = new View(getActivity());
					points.setLayoutParams(new LayoutParams(PX(24), PX((int) (pointsScored/20.0 * 48 + .5))));
					points.setBackgroundColor(Color.parseColor("#cc0000"));
					pointsBar.addView(points);
					bars.addView(pointsBar);

					LinearLayout foulsBar = new LinearLayout(getActivity());
					foulsBar.setGravity(Gravity.CENTER_HORIZONTAL);
					foulsBar.setOrientation(LinearLayout.VERTICAL);

					TextView foulsNumber = new TextView(getActivity(), null, android.R.style.TextAppearance_Small);
					foulsNumber.setLayoutParams(new LayoutParams(PX(24), LayoutParams.WRAP_CONTENT));
					foulsNumber.setGravity(Gravity.CENTER_HORIZONTAL);
					foulsNumber.setSingleLine();
					if (foulsAccumulated == .07f) foulsNumber.setText(0 + "");
					else foulsNumber.setText(((int) foulsAccumulated) + "");
					foulsBar.addView(foulsNumber);

					View foulsView = new View(getActivity());
					foulsView.setLayoutParams(new LayoutParams(PX(24), PX((int) (foulsAccumulated/20.0 * 48 + .5))));
					foulsView.setBackgroundColor(Color.parseColor("#0000cc"));
					foulsBar.addView(foulsView);
					bars.addView(foulsBar);

					parent.addView(bars);

					TextView matchNumber = new TextView(getActivity(), null, android.R.style.TextAppearance_Small);
					matchNumber.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, PX(24)));
					matchNumber.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
					matchNumber.setText(data[MatchScoutingIndex.MATCH_NUMBER].trim());
					parent.addView(matchNumber);

					graph.addView(parent);

					String[] possessions = data[MatchScoutingIndex.POSSESSIONS].trim().split("\\|\\|");
					for (int y=0; y<possessions.length; y++) {

						String[] possessionPart = possessions[y].trim().split("\\|");
						if (possessionPart[PossessionIndex.POSSESSION_END].trim().equals("null")) {
							possessionTime += Integer.parseInt(possessionPart[PossessionIndex.START_TIME].trim());
						} else {
							possessionTime += Integer.parseInt(possessionPart[PossessionIndex.START_TIME].trim())
									- Integer.parseInt(possessionPart[PossessionIndex.END_TIME].trim());
						}
						
						String start = possessionPart[PossessionIndex.POSSESSION_START].trim();
						String end = possessionPart[PossessionIndex.POSSESSION_END].trim();
								
						if (start.equals("Pass from HP") &&	end.equals("Passed to Robot")) HPtoRobot++;
						else if (start.equals("Pass from Robot") && end.equals("Passed to Robot")) robotToRobot++;
						else if (start.equals("Pass from Robot") && end.equals("Made High Goal")) robotToGoalHighMade++;
						else if (start.equals("Pass from Robot") && end.equals("Made Low Goal")) robotToGoalLowMade++;
						else if (start.equals("Pass from Robot") && end.equals("Missed High Goal")) robotToGoalHighMissed++;
						else if (start.equals("Pass from Robot") && end.equals("Missed Low Goal")) robotToGoalLowMissed++;
						else if (start.equals("Pass from HP") && end.equals("Passed to Robot")) HPtoRobot++;
						else if (start.equals("Pass from Robot") && end.equals("Threw over Truss")) robotToTruss++;
						else if (start.equals("Pass from Robot") && end.equals("Missed Truss")) robotToTrussMissed++;
						else if (start.equals("Pass from HP") && end.equals("Threw over Truss")) HPtoTruss++;
						else if (start.equals("Pass from HP") && end.equals("Missed Truss")) HPToTrussMissed++;
						else if (start.equals("Caught Ball") && end.equals("Made High Goal")) catchToGoalHighMade++;
						else if (start.equals("Caught Ball") && end.equals("Made Low Goal")) catchToGoalLowMade++;
						else if (start.equals("Caught Ball") && end.equals("Missed High Goal")) catchToGoalHighMissed++;
						else if (start.equals("Caught Ball") && end.equals("Missed Low Goal")) catchToGoalLowMissed++;
						else if (start.equals("Pass from HP") && end.equals("Made High Goal")) HPToGoalHighMade++;
						else if (start.equals("Pass from HP") && end.equals("Made Low Goal")) HPToGoalLowMade++;
						else if (start.equals("Pass from HP") && end.equals("Missed High Goal")) HPToGoalHighMissed++;
						else if (start.equals("Pass from pickUp") && end.equals("Missed Low Goal")) pickUpToGoalLowMissed++;
						else if (start.equals("Picked up Stray Ball") && end.equals("Made High Goal")) pickUpToGoalHighMade++;
						else if (start.equals("Picked up Stray Ball") && end.equals("Made Low Goal")) pickUpToGoalLowMade++;
						else if (start.equals("Picked up Stray Ball") && end.equals("Missed High Goal")) pickUpToGoalHighMissed++;
						else if (start.equals("Picked up Stray Ball") && end.equals("Missed Low Goal")) pickUpToGoalLowMissed++;
					}
				}
			}
			((RelativeLayout) root.findViewById(R.id.graph_parent)).addView(graph);
			DecimalFormat df = new DecimalFormat("###.#");
			((TextView) root.findViewById(R.id.avg_ppm)).setText(df.format(totalPoints/totalMatches));
			((TextView) root.findViewById(R.id.avg_fpm)).setText(df.format(totalFouls/totalMatches));
			((TextView) root.findViewById(R.id.blocks_deflections_per_match)).setText(df.format(totalBlocksDeflections/totalMatches));
			((TextView) root.findViewById(R.id.percent_of_total_alliance_score)).setText(df.format(totalPoints*100.f/totalAllianceScore) +"%");
			((TextView) root.findViewById(R.id.possessions_per_match)).setText(df.format(possessionCount/totalMatches));
			((TextView) root.findViewById(R.id.points_per_possession)).setText(df.format(totalPoints*1.f/possessionCount));
			((TextView) root.findViewById(R.id.auton_high_goal)).setText(autonHighGoalMade + "/" + autonHighGoalTotal + ", " + autonHighGoalHot + " Hot (" +
					autonHighGoalMade*100/autonHighGoalTotal + "%)");
			((TextView) root.findViewById(R.id.auton_low_goal)).setText(autonLowGoalMade + "/" + autonLowGoalTotal + ", " + autonLowGoalHot + " Hot (" +
					autonLowGoalMade*100/autonLowGoalTotal + "%)");
			((TextView) root.findViewById(R.id.teleop_high_goal)).setText(teleopHighGoalMade + "/" + teleopHighGoalTotal + " (" +
					teleopHighGoalMade*100/teleopHighGoalTotal + "%)");
			((TextView) root.findViewById(R.id.teleop_low_goal)).setText(teleopLowGoalMade + "/" + teleopLowGoalTotal + " (" +
					teleopLowGoalMade*100/teleopLowGoalTotal + "%)");
			((TextView) root.findViewById(R.id.truss_points)).setText(trussMade + "/" + trussTotal + " (" +
					trussMade*100/trussTotal + "%)");
			((TextView) root.findViewById(R.id.catches)).setText(catches + "");
			((TextView) root.findViewById(R.id.fouls)).setText(fouls + "");
			((TextView) root.findViewById(R.id.tech_fouls)).setText(techFouls + "");
			((TextView) root.findViewById(R.id.catches)).setText(catches + "");
			((TextView) root.findViewById(R.id.passes_from_hp)).setText(passesFromHP + "");
			((TextView) root.findViewById(R.id.passes_to_HP)).setText(passesToHP + "");
			((TextView) root.findViewById(R.id.passes_from_robots)).setText(passesFromRobo + "");
			((TextView) root.findViewById(R.id.passes_to_robots)).setText(passesToRobo + "");

			int autonPoints = autonHighGoalMade * 15 + autonHighGoalHot * 5 + autonLowGoalHot * 5 + autonLowGoalMade * 6;
			int highGoalPoints = teleopHighGoalMade * 10;
			int lowGoalPoints = teleopLowGoalMade;
			int trussPoints = trussMade * 10;
			int catchPoints = catches * 10;
			int total = autonPoints + highGoalPoints + lowGoalPoints + trussPoints + catchPoints;

			((TextView) root.findViewById(R.id.autonomous_scored_stat)).setText(autonPoints + "");
			((TextView) root.findViewById(R.id.autonomous_scored_percent)).setText(100*autonPoints/total + "%");
			if (autonPoints == 0) ((View) root.findViewById(R.id.autonomous_scored_stat).getParent()).setAlpha(.5f);

			((TextView) root.findViewById(R.id.high_goal_scored_stat)).setText(highGoalPoints + "");
			((TextView) root.findViewById(R.id.high_goal_scored_percent)).setText(100*highGoalPoints/total + "%");
			if (highGoalPoints == 0) ((View) root.findViewById(R.id.high_goal_scored_stat).getParent()).setAlpha(.5f);

			((TextView) root.findViewById(R.id.low_goal_scored_stat)).setText(lowGoalPoints + "");
			((TextView) root.findViewById(R.id.low_goal_scored_percent)).setText(100*lowGoalPoints/total + "%");
			if (lowGoalPoints == 0) ((View) root.findViewById(R.id.low_goal_scored_stat).getParent()).setAlpha(.5f);

			((TextView) root.findViewById(R.id.truss_scored_stat)).setText(trussPoints + "");
			((TextView) root.findViewById(R.id.truss_scored_percent)).setText(100*trussPoints/total + "%");
			if (trussPoints == 0) ((View) root.findViewById(R.id.truss_scored_stat).getParent()).setAlpha(.5f);

			((TextView) root.findViewById(R.id.catch_scored_stat)).setText(catchPoints + "");
			((TextView) root.findViewById(R.id.catch_scored_percent)).setText(100*catchPoints/total + "%");
			if (catchPoints == 0) ((View) root.findViewById(R.id.catch_scored_stat).getParent()).setAlpha(.5f);

			float[] distributionValues = {autonPoints, highGoalPoints, lowGoalPoints, trussPoints, catchPoints};
			((RelativeLayout) root.findViewById(R.id.score_graph)).addView(new GraphView(getActivity(), distributionValues));

			((TextView) root.findViewById(R.id.high_goal_accuracy_stat)).setText(teleopHighGoalMade + "/"+ teleopHighGoalTotal);
			((TextView) root.findViewById(R.id.high_goal_accuracy_percent)).setText(100*teleopHighGoalMade/teleopHighGoalTotal + "%");
			if (teleopHighGoalTotal == 0) ((View) root.findViewById(R.id.high_goal_accuracy_stat).getParent()).setAlpha(.5f);		

			((TextView) root.findViewById(R.id.low_goal_accuracy_stat)).setText(teleopLowGoalMade + "/"+ teleopLowGoalTotal);
			((TextView) root.findViewById(R.id.low_goal_accuracy_percent)).setText(100*teleopLowGoalMade/teleopLowGoalTotal + "%");
			if (teleopLowGoalTotal == 0) ((View) root.findViewById(R.id.low_goal_accuracy_stat).getParent()).setAlpha(.5f);		

			((TextView) root.findViewById(R.id.truss_accuracy_stat)).setText(trussMade + "/"+ trussTotal);
			((TextView) root.findViewById(R.id.truss_accuracy_percent)).setText(100*trussMade/trussTotal + "%");
			if (trussTotal == 0) ((View) root.findViewById(R.id.truss_accuracy_stat).getParent()).setAlpha(.5f);	

			int totalMade = teleopHighGoalMade + teleopLowGoalMade + trussMade;
			int totalTaken = teleopHighGoalTotal + teleopLowGoalTotal + trussTotal;
			((TextView) root.findViewById(R.id.overall_accuracy_stat)).setText(totalMade + "/"+ totalTaken);
			((TextView) root.findViewById(R.id.overall_accuracy_percent)).setText(100*totalMade/totalTaken + "%");	

			float[] accuracyValues = {totalMade, totalTaken-totalMade};
			((RelativeLayout) root.findViewById(R.id.accuracy_graph)).addView(new GraphView(getActivity(), accuracyValues));

			((TextView) root.findViewById(R.id.time_with_possession_stat)).setText(possessionTime + "s");
			((TextView) root.findViewById(R.id.time_with_possession_percent)).setText(((int) (100.0*possessionTime/140 + .5)) + "%");
			((TextView) root.findViewById(R.id.time_without_possession_stat)).setText((int) (140*totalMatches-possessionTime) + "s");
			((TextView) root.findViewById(R.id.time_without_possession_percent)).setText(((int) (100*(140.0*totalMatches-possessionTime)/(140*totalMatches) + .5)) + "%");

			float[] timeValues = {possessionTime, (int) (140*totalMatches)-possessionTime};
			((RelativeLayout) root.findViewById(R.id.time_graph)).addView(new GraphView(getActivity(), timeValues));

			((TextView) root.findViewById(R.id.HPtoRobot1)).setText(HPtoRobot + "");	
			((TextView) root.findViewById(R.id.robotToRobot)).setText(robotToRobot + "");	
			((TextView) root.findViewById(R.id.robotToGoal)).setText(robotToGoalHighMade + "/" + (robotToGoalHighMade + robotToGoalHighMissed) + " High, " +
					robotToGoalLowMade + "/" + (robotToGoalLowMade+robotToGoalLowMissed) + " Low");	
			((TextView) root.findViewById(R.id.HPToRobot2)).setText(HPtoRobot + "");	
			((TextView) root.findViewById(R.id.robotToTruss)).setText(robotToTruss + "/" + (robotToTruss+robotToTrussMissed) + "");	
			((TextView) root.findViewById(R.id.HPToTruss)).setText(HPtoTruss + "/" + (HPtoTruss+HPToTrussMissed) + "");	
			((TextView) root.findViewById(R.id.catchToGoal)).setText(catchToGoalHighMade + "/" + (catchToGoalHighMade + catchToGoalHighMissed) + " High, " +
					catchToGoalLowMade + "/" + (catchToGoalLowMade+catchToGoalLowMissed) + " Low");	
			((TextView) root.findViewById(R.id.HPToGoal)).setText(HPToGoalHighMade + "/" + (HPToGoalHighMade + HPToGoalHighMissed) + " High, " +
					HPToGoalLowMade + "/" + (HPToGoalLowMade+HPToGoalLowMissed) + " Low");	
			((TextView) root.findViewById(R.id.pickUpToGoal)).setText(pickUpToGoalHighMade + "/" + (pickUpToGoalHighMade + pickUpToGoalHighMissed) + " High, " +
					pickUpToGoalLowMade + "/" + (pickUpToGoalLowMade+pickUpToGoalLowMissed) + " Low");			
			
			root.findViewById(R.id.buttons).setVisibility(View.GONE);
			return root;
		}

		private int getAllianceScore(String[] data) {
			String matchNumber = data[MatchScoutingIndex.MATCH_NUMBER];
			String[] matchData = currentComp.getMatchInfoByNumber(matchNumber.trim());
			String alliance = getAllianceByMatchAndTeam(matchData, teamNumber);
			if (alliance.equals("Blue")) return Integer.parseInt(matchData[MatchIndex.BLUE_SCORE].trim());
			else return Integer.parseInt(matchData[MatchIndex.RED_SCORE].trim());
		}

		private String getAllianceByMatchAndTeam(String[] matchData, String teamNumber) {
			if (matchData[MatchIndex.BLUE1].equals(teamNumber) ||
					matchData[MatchIndex.BLUE2].equals(teamNumber) ||
					matchData[MatchIndex.BLUE3].equals(teamNumber)) return "Blue";
			return "Red";
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

		private int PX(int dp) {
			return Utilities.PX(getActivity(), dp);
		}

	}

}