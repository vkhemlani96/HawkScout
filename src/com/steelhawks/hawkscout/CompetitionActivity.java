package com.steelhawks.hawkscout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.text.TextUtils.TruncateAt;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.steelhawks.hawkscout.CompetitionActivity.RankingsFragment.TeamAdapter;
import com.steelhawks.hawkscout.CompetitionActivity.RankingsFragment.TeamAdapter.SortBy;
import com.steelhawks.hawkscout.Globals.Competition;
import com.steelhawks.hawkscout.Globals.MatchData;
import com.steelhawks.hawkscout.Globals.TeamData;
import com.steelhawks.hawkscout.Globals.UserTeam;
import com.steelhawks.hawkscout.dialogs.SimpleTextFragment;
import com.steelhawks.hawkscout.util.ProgressLayout;

public class CompetitionActivity extends FragmentActivity implements
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
	static int teamIndex;
	Globals App;
	public static Competition comp;
	public static int compIndex;
	UserTeam team;
	ActionBar actionBar;
	HomeFragment frag1;
	RankingsFragment frag2;
	MatchesFragment frag3;
	static List<String> teamNumbers;
	static ListView listView;
	private static final int RETURN_FROM_PIT = 5479;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_competition);
		
		App = (Globals)getApplicationContext();

		// Set up the action bar.
		actionBar = getActionBar();
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras.getInt("com.steelhawks.hawkscout.CompetitionMenu.TEAM_INDEX", -1) != -1) {
			LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			teamIndex = extras.getInt("com.steelhawks.hawkscout.CompetitionMenu.TEAM_INDEX");
			int compIndex = extras.getInt("com.steelhawks.hawkscout.CompetitionMenu.COMP_INDEX");
			team = App.getTeams().get(teamIndex);
			for (int x=0; x<team.getCompetitions().size(); x++) {
				if(team.getCompetitions().get(x).getIndex() == compIndex) {
					comp = team.getCompetitions().get(x);
					compIndex = x;
				}
			}
		    View v = inflator.inflate(R.layout.action_bar_view, null);
		    TextView tv = (TextView) v.findViewById(R.id.title);
		    	tv.setSingleLine(true);
		    	tv.setEllipsize(TruncateAt.END);
		    	tv.setTypeface(null, Typeface.BOLD);
		    	tv.setText(comp.getCompName());
		    actionBar.setDisplayShowCustomEnabled(true);
		    actionBar.setDisplayShowTitleEnabled(false);
		    actionBar.setCustomView(v);
		}
		

		boolean cache = false;
		if (cache ||
			(comp.getTeamData().size() > 0 && comp.getMatchData().size() > 0
					&& comp.getLastMatch() != -1)) {
			createViewPager();
		} else if (comp.getCompInfo().getStatus() == AsyncTask.Status.RUNNING || 
				comp.getQualSchedule().getStatus() == AsyncTask.Status.RUNNING || 
				comp.getMatchResults().getStatus() == AsyncTask.Status.RUNNING) {
			System.err.println("DATA no PREsent");
			setContentView(new ProgressLayout(this));
			Runnable r = new Runnable () {
				public void run() {
					if (comp.getTeamData().size() > 0 && comp.getMatchData().size() > 0
							&& comp.getLastMatch() != -1) {
						createViewPager();
					} else {
						new Handler().postDelayed(this, 100);
					}
				}
			};
			new Handler().post(r);
		} else {
			setContentView(R.layout.sign_in);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				comp.getCompInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
				comp.getQualSchedule().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
				comp.getMatchResults().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
				comp.getScoutedTeams().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
			} else {
				comp.getCompInfo().execute();
				comp.getQualSchedule().execute();
				comp.getMatchResults().execute();
				comp.getScoutedTeams().execute();
			}
			Runnable r = new Runnable () {
				public void run() {
					if (comp.getCompInfo().getStatus() == AsyncTask.Status.PENDING || 
							comp.getQualSchedule().getStatus() == AsyncTask.Status.PENDING || 
							comp.getMatchResults().getStatus() == AsyncTask.Status.PENDING) {
						createViewPager();
					} else {
						new Handler().postDelayed(this, 100);
					}
				}
			};
			new Handler().post(r);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.competition, menu);
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the VerticalViewPager.
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
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case RETURN_FROM_PIT: 
				if (resultCode == Activity.RESULT_OK)
					Toast.makeText(this, "Data Successfully Uploaded!", Toast.LENGTH_SHORT).show();
				else if (resultCode == Activity.RESULT_CANCELED)
					Toast.makeText(this, "Data was not uploaded.", Toast.LENGTH_SHORT).show();
				else Toast.makeText(this, "A problem has occurred.\nData was not uploaded.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void createViewPager() {
		setContentView(R.layout.activity_competition);
		
		teamNumbers = new ArrayList<String>();
		for(int x=0; x<comp.getTeamData().size(); x++) {
			teamNumbers.add(String.valueOf(comp.getTeamData().get(x).getTeamNumber()));
		}
		
		frag1 = new HomeFragment();
		frag2 = new RankingsFragment();
		frag3 = new MatchesFragment();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
//		LayoutInflater inflater = getLayoutInflater();
//		ViewPager mViewPager = (ViewPager) inflater.inflate(R.layout.activity_competition, null);
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
	}
	
	public void sortRankings(View v) {
		TeamAdapter adapter = (TeamAdapter) listView.getAdapter();
		final boolean ASCENDING = true;
		final boolean DESCENDING = false;
		switch(v.getId()) {
		case R.id.team_header:
			if(adapter.sort == SortBy.TEAM) {
				listView.setAdapter(adapter.reverse());
				if ((Boolean) v.getTag() == ASCENDING) {
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_descending));
					v.setTag(DESCENDING);
				} else {
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
					v.setTag(ASCENDING);
				}
				return;
			} else {
				listView.setAdapter(frag2.new TeamAdapter(this, comp.getTeamData(), SortBy.TEAM));
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
						null, null, null, getResources().getDrawable(R.drawable.rankings_sort_descending));
				v.setTag(DESCENDING);
			}
			break;
		case R.id.qp_header:
			if(adapter.sort == SortBy.QUAL) {
				listView.setAdapter(adapter.reverse());
				if ((Boolean) v.getTag() == ASCENDING) {
					System.out.println("Ascending already");
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_descending));
					v.setTag(DESCENDING);
				} else {
					System.out.println("Descending already");
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
					v.setTag(ASCENDING);
				}
				return;
			} else { System.out.println("its the else");
				listView.setAdapter(frag2.new TeamAdapter(this, comp.getTeamData(), SortBy.QUAL));
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
						null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
				v.setTag(ASCENDING);
			}
			break;
		case R.id.ap_header:
			if(adapter.sort == SortBy.AUTON) {
				listView.setAdapter(adapter.reverse());
				if ((Boolean) v.getTag() == ASCENDING) {
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_descending));
					v.setTag(DESCENDING);
				} else {
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
					v.setTag(ASCENDING);
				}
				return;
			} else {
				listView.setAdapter(frag2.new TeamAdapter(this, comp.getTeamData(), SortBy.AUTON));
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
						null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
				v.setTag(ASCENDING);
			}
			break;
		case R.id.cp_header:
			if(adapter.sort == SortBy.CLIMB) {
				listView.setAdapter(adapter.reverse());
				if ((Boolean) v.getTag() == ASCENDING) {
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_descending));
					v.setTag(DESCENDING);
				} else {
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
					v.setTag(ASCENDING);
				}
				return;
			} else {
				listView.setAdapter(frag2.new TeamAdapter(this, comp.getTeamData(), SortBy.CLIMB));
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
						null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
				v.setTag(ASCENDING);
			}
			break;
		case R.id.tp_header:
			if(adapter.sort == SortBy.TELE) {
				listView.setAdapter(adapter.reverse());
				if ((Boolean) v.getTag() == ASCENDING) {
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_descending));
					v.setTag(DESCENDING);
				} else {
					((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
					v.setTag(ASCENDING);
				}
				return;
			} else {
				listView.setAdapter(frag2.new TeamAdapter(this, comp.getTeamData(), SortBy.TELE));
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
						null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
				v.setTag(ASCENDING);
			}
			break;
		}
		TextView tv;
		switch (adapter.sort) {
		case SortBy.TEAM:
			System.out.println("Still clearing team");
			tv = (TextView) ((LinearLayout)v.getParent()).findViewById(R.id.team_header);
			tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			break;
		case SortBy.QUAL:
			System.out.println("Still clearing qual");
			tv = (TextView) ((LinearLayout)v.getParent()).findViewById(R.id.qp_header);
			tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			break;
		case SortBy.AUTON:
			System.out.println("Still clearing auton");
			tv = (TextView) ((LinearLayout)v.getParent()).findViewById(R.id.ap_header);
			tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			break;
		case SortBy.CLIMB:
			System.out.println("Still clearing climb");
			tv = (TextView) ((LinearLayout)v.getParent()).findViewById(R.id.cp_header);
			tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			break;
		case SortBy.TELE:
			System.out.println("Still clearing tele");
			tv = (TextView) ((LinearLayout)v.getParent()).findViewById(R.id.tp_header);
			tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			break;
		
		}
	}
	
	public void onClick (View v) {
		switch(v.getId()) {
		case R.id.rankings_home_go: frag1.viewData();
			break;
		case R.id.start_pit:Intent i = new Intent(CompetitionActivity.this, com.steelhawks.hawkscout.PitScouting.class);
			i.putExtra("com.steelhawks.hawkscout.TEAM_INDEX_PARAMETERS", teamIndex);
			i.putExtra("com.steelhawks.hawkscout.COMPETITION_INDEX", compIndex);
			startActivityForResult(i, RETURN_FROM_PIT);
			break;
		case R.id.start_match: Intent i1 = new Intent(CompetitionActivity.this, com.steelhawks.hawkscout.MatchScouting.class);
			i1.putExtra("com.steelhawks.hawkscout.TEAM_INDEX_PARAMETERS", teamIndex);
			i1.putExtra("com.steelhawks.hawkscout.COMPETITION_INDEX", compIndex);
			startActivityForResult(i1, RETURN_FROM_PIT);
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
				case 0: return frag1;
				case 1: return frag2;
				case 2: return frag3;
			}
			
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "Menu".toUpperCase(l);
			case 1:
				return "Rankings".toUpperCase(l);
			case 2:
				return "Matches".toUpperCase(l);
			case 3:
				return "Pick List".toUpperCase(l);
			}
			return null;
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
			View rootView = inflater.inflate(
					R.layout.fragment_competition_dummy, container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	
	public static class RankingsFragment extends Fragment {
		
		public RankingsFragment(){}
		
		@SuppressLint("NewApi")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_rankings_layout, null);
				listView = (ListView) rootView.findViewById(R.id.rankings);
					listView.setAdapter(new TeamAdapter(getActivity(), comp.getTeamData(), SortBy.QUAL));
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View view,
								int arg2, long arg3) {
							LinearLayout v = (LinearLayout) view;
							TextView teamNumber = (TextView) v.findViewById(R.id.team_number);
							String s = teamNumber.getText().toString();
							
							Intent i = new Intent(getActivity(), com.steelhawks.hawkscout.TeamActivity.class);
								i.putExtra("com.steelhawks.hawkscout.CompetitionActivity.CURRENT_TEAM_NUMBER", s);
								i.putExtra("com.steelhawks.hawkscout.CompetitionActivity.TEAM_INDEX", teamIndex);
								i.putExtra("com.steelhawks.hawkscout.CompetitionActivity.COMP_INDEX", compIndex);
							startActivity(i);
						}
					});
					rootView.findViewById(R.id.qp_header).setTag(true);
					
				final AutoCompleteTextView teamInput = (AutoCompleteTextView) rootView.findViewById(R.id.team_input);
						Collections.sort(teamNumbers, new Comparator<String>() {
	
							@Override
							public int compare(String arg0, String arg1) {
								Integer int1 = Integer.parseInt(arg0);
								Integer int2 = Integer.parseInt(arg1);
								return int1.compareTo(int2);
							}
						});
					teamInput.setAdapter(new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_spinner_dropdown_item, teamNumbers));
					teamInput.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.crimson_gray_background));
					teamInput.setOnItemClickListener(new OnItemClickListener() {
	
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							String str = String.valueOf(((TextView) arg1).getText());
							for(int x=0; x< ((TeamAdapter)listView.getAdapter()).teams.size(); x++) {
								if (str.equals(String.valueOf(((TeamAdapter)listView.getAdapter()).teams.get(x).getTeamNumber()))) {
									listView.setSelection(x);
									teamInput.setText("");
	//								InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	//								imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
								}
							}
						}
					});
					
				Spinner rankingsType = (Spinner) rootView.findViewById(R.id.rankings_type);
					String[] rankingsEntries = {"Rankings", "Offense", "Defense"};
					final ArrayAdapter<String> rankingsTypeAdapter = new ArrayAdapter<String> (getActivity(),
							R.layout.spinner_text_dark, rankingsEntries);
						rankingsTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					rankingsType.setAdapter(rankingsTypeAdapter);
			return rootView;
		}
		
		@SuppressLint("UseSparseArrays")
		class TeamAdapter extends ArrayAdapter<TeamData> {
			
			private Context context;
			List<TeamData> teams;
			int sort;
			private boolean reverse;
			
			int qualMax;
			int qualMin;
			int autonMax;
			int autonMin;
			int climbMax;
			int climbMin;
			int teleMax;
			int teleMin;

			private List<Integer> dupsList = new ArrayList<Integer>();
			private SparseIntArray dups = new SparseIntArray();
			
			public class SortBy {
				public final static int TEAM = 0;
				public final static int QUAL = 1;
				public final static int AUTON = 2;
				public final static int CLIMB = 3;
				public final static int TELE = 4;
			}
			
			Comparator<TeamData> rankings = new Comparator<TeamData>() {
				
				@Override
				public int compare(TeamData one, TeamData two) {
					Integer number1 = one.getQualScore();
					Integer number2 = two.getQualScore();
					if (number1 != number2)	return number1.compareTo(number2);
					number1 = one.getAutonPoints();
					number2 = two.getAutonPoints();
					if (number1 != number2)	return number1.compareTo(number2);
					number1 = one.getClimbPoints();
					number2 = two.getClimbPoints();
					if (number1 != number2)	return number1.compareTo(number2);
					number1 = one.getTelePoints();
					number2 = two.getTelePoints();
					return number1.compareTo(number2);
				}
			};
			Comparator<TeamData> teamNumber  = new Comparator<TeamData>() {

				@Override
				public int compare(TeamData one, TeamData two) {
					Integer number1 = one.getTeamNumber();
					Integer number2 = two.getTeamNumber();
					return number1.compareTo(number2);
				}
			};
			Comparator<TeamData> qualScore  = new Comparator<TeamData>() {

				@Override
				public int compare(TeamData one, TeamData two) {
					Integer number1 = one.getQualScore();
					Integer number2 = two.getQualScore();
					return number1.compareTo(number2);
				}
			};
			Comparator<TeamData> autonScore  = new Comparator<TeamData>() {

				@Override
				public int compare(TeamData one, TeamData two) {
					Integer number1 = one.getAutonPoints();
					Integer number2 = two.getAutonPoints();
					return number1.compareTo(number2);
				}
			};
			Comparator<TeamData> climbScore  = new Comparator<TeamData>() {

				@Override
				public int compare(TeamData one, TeamData two) {
					Integer number1 = one.getClimbPoints();
					Integer number2 = two.getClimbPoints();
					return number1.compareTo(number2);
				}
			};
			Comparator<TeamData> teleScore  = new Comparator<TeamData>() {

				@Override
				public int compare(TeamData one, TeamData two) {
					Integer number1 = one.getTelePoints();
					Integer number2 = two.getTelePoints();
					return number1.compareTo(number2);
				}
			};
			
			public TeamAdapter(Context c, List<TeamData> teams, int sort) {
				super(c, R.layout.competitions_rankings_row_layout, teams);
				this.context = c;
				this.teams = teams;
				this.sort = sort;
				dupsList.clear();
				dups.clear();
				switch(sort) {
					case SortBy.TEAM:Collections.sort(this.teams, teamNumber);
						break;
					case SortBy.QUAL:Collections.sort(this.teams, rankings);
						Collections.reverse(this.teams);
						break;
					case SortBy.AUTON:Collections.sort(this.teams, autonScore);
						Collections.reverse(this.teams);
						for (int x = 0; x<this.teams.size(); x++) {
							dupsList.add(this.teams.get(x).getAutonPoints());
						}
						break;
					case SortBy.CLIMB:Collections.sort(this.teams, climbScore);
						Collections.reverse(this.teams);
						for (int x = 0; x<this.teams.size(); x++) {
							dupsList.add(this.teams.get(x).getClimbPoints());
						}
						break;
					case SortBy.TELE:Collections.sort(this.teams, teleScore);
						Collections.reverse(this.teams);
						for (int x = 0; x<this.teams.size(); x++) {
							dupsList.add(this.teams.get(x).getTelePoints());
						}
						break;
				}
				for (int x = 0; x<dupsList.size(); ) {
					int freq = Collections.frequency(dupsList, dupsList.get(x));
					if (freq > 1) {
						if (reverse) dups.put(dupsList.get(x), dupsList.size() - x);
						else dups.put(dupsList.get(x), x+1);
					}
					x += freq;
				}
				qualMax = Collections.max(this.teams, qualScore).getQualScore();
				qualMin = Collections.min(this.teams, qualScore).getQualScore();
				autonMax = Collections.max(this.teams, autonScore).getAutonPoints();
				autonMin = Collections.min(this.teams, autonScore).getAutonPoints();
				climbMax = Collections.max(this.teams, climbScore).getClimbPoints();
				climbMin = Collections.min(this.teams, climbScore).getClimbPoints();
				teleMax = Collections.max(this.teams, teleScore).getTelePoints();
				teleMin = Collections.min(this.teams, teleScore).getTelePoints();
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TeamData team = teams.get(position);
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_rankings_row_layout, null);
				TextView rank = (TextView) rootView.findViewById(R.id.rank);
				switch (sort) {
				case SortBy.AUTON:
					if (dups.get(team.getAutonPoints()) != 0){
						rank.setText("T" + dups.get(team.getAutonPoints()));
					} else if (reverse) {
						rank.setText(String.valueOf(teams.size()-position));
					} else {
						rank.setText(String.valueOf(position+1));
					}
					break;
				case SortBy.CLIMB:
					if (dups.get(team.getClimbPoints()) != 0){
						rank.setText("T" + dups.get(team.getClimbPoints()));
					} else if (reverse) {
						rank.setText(String.valueOf(teams.size()-position));
					} else {
						rank.setText(String.valueOf(position+1));
					}
					break;
				case SortBy.TELE:
					if (dups.get(team.getTelePoints()) != 0){
						rank.setText("T" + dups.get(team.getTelePoints()));
					} else if (reverse) {
						rank.setText(String.valueOf(teams.size()-position));
					} else {
						rank.setText(String.valueOf(position+1));
					}
					break;
				default: 
					if (reverse) rank.setText(String.valueOf(teams.size()-position));
					else rank.setText(String.valueOf(position+1));
					break;
				}
				TextView teamNumber = (TextView) rootView.findViewById(R.id.team_number);
				teamNumber.setText(String.valueOf(team.getTeamNumber()));
				TextView qp = (TextView) rootView.findViewById(R.id.qp);
				qp.setText(team.getQualScore() + " (" + team.getRecord() + ")");
				qp.setBackgroundColor(getColor(team.getQualScore(), qualMax, qualMin));
				TextView ap = (TextView) rootView.findViewById(R.id.ap);
				ap.setBackgroundColor(getColor(team.getAutonPoints(), autonMax, autonMin));
				ap.setText(String.valueOf(team.getAutonPoints()));
				TextView cp = (TextView) rootView.findViewById(R.id.cp);
				cp.setText(String.valueOf(team.getClimbPoints()));
				cp.setBackgroundColor(getColor(team.getClimbPoints(), climbMax, climbMin));
				TextView tp = (TextView) rootView.findViewById(R.id.tp);
				tp.setText(String.valueOf(team.getTelePoints()));
				tp.setBackgroundColor(getColor(team.getTelePoints(), teleMax, teleMin));
				return rootView;
			}
			
			
			
			public TeamAdapter reverse() {
				Collections.reverse(teams);
				reverse = reverse?false:true;
				dups.clear();
				dupsList.clear();
				switch(sort) {
				case SortBy.AUTON:
					for (int x = 0; x<this.teams.size(); x++) {
						dupsList.add(this.teams.get(x).getAutonPoints());
					}
					break;
				case SortBy.CLIMB:
					for (int x = 0; x<this.teams.size(); x++) {
						dupsList.add(this.teams.get(x).getClimbPoints());
					}
					break;
				case SortBy.TELE:
					for (int x = 0; x<this.teams.size(); x++) {
						dupsList.add(this.teams.get(x).getTelePoints());
					}
					break;
				}
				for (int x = 0; x<dupsList.size(); ) {
					int freq = Collections.frequency(dupsList, dupsList.get(x));
					if (freq > 1) {
						if (reverse) dups.put(dupsList.get(x), dupsList.size() - x);
						else dups.put(dupsList.get(x), x+1);
					}
					x += freq;
				}
				return this;
			}
			
			public int PX (int dp) {
				final float scale = context.getResources().getDisplayMetrics().density;
				int px = (int) (dp*scale+0.5f);
				return px;
			}
			
			public int getColor(int score, int maxScore, int minScore) {
				int alpha = (score-minScore)*100/(maxScore - minScore);
				String colorStr;
				if (alpha == 100)  {
					colorStr =  "#99ff0000";
				} else if (alpha<10) {
					colorStr =  "#0" + alpha + "ff0000";
				} else {
					colorStr = "#" + String.valueOf(alpha) + "ff0000";
				}
				return Color.parseColor(colorStr);
			}
		}
	}
	
	public static class HomeFragment extends Fragment {
		View rootView;
		Spinner category;
		AutoCompleteTextView input;
		
		public HomeFragment() {}
				
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(
					R.layout.competition_activity_home, container, false);
				TextView title = (TextView) rootView.findViewById(R.id.title);
				title.setText(comp.getCompName());
				TextView location = (TextView) rootView.findViewById(R.id.location);
				location.setText(comp.getCompLoc());
				TextView leader = (TextView) rootView.findViewById(R.id.leader);
				leader.setText(String.valueOf(comp.getLeader()));
				TextView number = (TextView) rootView.findViewById(R.id.number);
				number.setText(String.valueOf(comp.getLastMatch()));
				if(comp.getTeamsScouted() != -1) {
					ProgressBar pitPb = (ProgressBar) rootView.findViewById(R.id.pits_progress);
					pitPb.setVisibility(View.GONE);
					TextView pitTv = (TextView) rootView.findViewById(R.id.pits_text);
					pitTv.setVisibility(View.VISIBLE);
					pitTv.setText(comp.getTeamsScouted()-1 + " / " + comp.getTeamData().size());
				} else {
					updatePitText();
				}
				
				input = (AutoCompleteTextView) rootView.findViewById(R.id.rankings_home_edit);
						Collections.sort(teamNumbers, new Comparator<String>() {
			
							@Override
							public int compare(String arg0, String arg1) {
								Integer int1 = Integer.parseInt(arg0);
								Integer int2 = Integer.parseInt(arg1);
								return int1.compareTo(int2);
							}
						});
					final ArrayAdapter<String> teamsAdapter = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_spinner_dropdown_item, teamNumbers);
					input.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.crimson_gray_background));
					
				category = (Spinner) rootView.findViewById(R.id.rankings_home_spinner);
				String[] strs = {"Team","Match"};
					final ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_spinner_dropdown_item, strs);
					category.setAdapter(a);
					category.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
								switch (arg2) {
								case 1:
									input.setAdapter(null);
									break;
								default: 
									input.setAdapter(teamsAdapter);
									break;
								}
						}
	
						@Override
						public void onNothingSelected(AdapterView<?> arg0) {}
					});
				
			return rootView;
		}
		
		public void viewData () {
			String str = input.getText().toString();
			System.out.println("index" + category.getSelectedItemPosition());
			switch(category.getSelectedItemPosition()) {
				case 0: 
					if (teamNumbers.contains(str)) {
						System.out.println("team exists");
						//TODO open team activity
					} else {
						System.out.println("team doesnt exists");
						new SimpleTextFragment().newInstance("Team Not Found",
								"Team " + str + " is not competing at this competition!", "OK",
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										input.setText("");
										input.requestFocus();
										dialog.dismiss();									
									}
								}, false).show(getActivity().getSupportFragmentManager(), "TEAM NOT FOUND");
					}
					break;
				case 1:
					int i = Integer.parseInt(str);
					for (int x=0; x<comp.getMatchData().size(); x++) {
						if (comp.getMatchData().get(x).getMatchNumber() == i) {
							System.out.println("matches exists");
							//TODO open match
							return;
						}
					}
					System.out.println("match doesnt exists");
					new SimpleTextFragment().newInstance("Match Not Found",
							"Match" + str + "does not exist", "OK",
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									input.setText("");
									input.requestFocus();
									dialog.dismiss();									
								}
							}, false).show(getActivity().getSupportFragmentManager(), "MATCH NOT FOUND");
			}
		}
		
		public void updatePitText() {
			Runnable r = new Runnable() {
				public void run() {
					if(comp.getTeamsScouted() != -1) {
						ProgressBar pitPb = (ProgressBar) rootView.findViewById(R.id.pits_progress);
						pitPb.setVisibility(View.GONE);
						TextView pitTv = (TextView) rootView.findViewById(R.id.pits_text);
						pitTv.setVisibility(View.VISIBLE);
						pitTv.setText(comp.getTeamsScouted() + " / " + comp.getTeamData().size());
					} else {
						new Handler().postDelayed(this, 50);
					}
				}
			};
			new Handler().post(r);
		}
		
	}

	public static class MatchesFragment extends Fragment {
		
		public MatchesFragment(){}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ListView matches = new ListView(getActivity());
			matches.setAdapter(new MatchAdapter(getActivity(), comp.getMatchData()));
			matches.setFastScrollEnabled(true);
			return matches;
		}
		
		class MatchAdapter extends ArrayAdapter<MatchData> {

			private final Context context;
			private final List<MatchData> list;
			
			public MatchAdapter(Context c, List<MatchData> list) {
				super(c, R.layout.competitions_matches_layout, list);
				this.context = c;
				this.list = list;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				MatchData match = list.get(position);
				if(match.getStatus() == MatchData.NOT_PLAYED) {
					ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_matches_layout, parent, false);
						TextView matchNumber = (TextView) rootView.findViewById(R.id.match_number);
						matchNumber.setText(String.valueOf(match.getMatchNumber()));
						TextView red1 = (TextView) rootView.findViewById(R.id.red1);
						red1.setText(String.valueOf(match.getRed1()));
						TextView red2 = (TextView) rootView.findViewById(R.id.red2);
						red2.setText(String.valueOf(match.getRed2()));
						TextView red3 = (TextView) rootView.findViewById(R.id.red3);
						red3.setText(String.valueOf(match.getRed3()));
						TextView blue1 = (TextView) rootView.findViewById(R.id.blue1);
						blue1.setText(String.valueOf(match.getBlue1()));
						TextView blue2 = (TextView) rootView.findViewById(R.id.blue2);
						blue2.setText(String.valueOf(match.getBlue2()));
						TextView blue3 = (TextView) rootView.findViewById(R.id.blue3);
						blue3.setText(String.valueOf(match.getBlue3()));
						TextView time = (TextView) rootView.findViewById(R.id.time);
						time.setText(String.valueOf(match.getTime()));
					return rootView;
				} else {
					ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_matches_finished_layout, parent, false);
						TextView matchNumber = (TextView) rootView.findViewById(R.id.match_number);
						matchNumber.setText(String.valueOf(match.getMatchNumber()));
						TextView redScore = (TextView) rootView.findViewById(R.id.red_score);
						redScore.setText(String.valueOf(match.getRedScore()));
						TextView red1 = (TextView) rootView.findViewById(R.id.red1);
						red1.setText(String.valueOf(match.getRed1()));
						TextView red2 = (TextView) rootView.findViewById(R.id.red2);
						red2.setText(String.valueOf(match.getRed2()));
						TextView red3 = (TextView) rootView.findViewById(R.id.red3);
						red3.setText(String.valueOf(match.getRed3()));
						TextView blueScore = (TextView) rootView.findViewById(R.id.blue_score);
						blueScore.setText(String.valueOf(match.getBlueScore()));
						TextView blue1 = (TextView) rootView.findViewById(R.id.blue1);
						blue1.setText(String.valueOf(match.getBlue1()));
						TextView blue2 = (TextView) rootView.findViewById(R.id.blue2);
						blue2.setText(String.valueOf(match.getBlue2()));
						TextView blue3 = (TextView) rootView.findViewById(R.id.blue3);
						blue3.setText(String.valueOf(match.getBlue3()));
						if (match.getStatus() == MatchData.RED) {
							LinearLayout alliance = (LinearLayout) rootView.findViewById(R.id.blue_alliance);
//								alliance.setAlpha(0.75f);
							red1.setTypeface(null, Typeface.BOLD);
							red2.setTypeface(null, Typeface.BOLD);
							red3.setTypeface(null, Typeface.BOLD);
							redScore.setTypeface(null, Typeface.BOLD);
							redScore.setBackgroundColor(Color.parseColor("#10ff0000"));
						} else if (match.getStatus() == MatchData.BLUE) {
							LinearLayout alliance = (LinearLayout) rootView.findViewById(R.id.red_alliance);
//								alliance.setAlpha(0.75f);
							blue1.setTypeface(null, Typeface.BOLD);
							blue2.setTypeface(null, Typeface.BOLD);
							blue3.setTypeface(null, Typeface.BOLD);
							blueScore.setTypeface(null, Typeface.BOLD);
							blueScore.setBackgroundColor(Color.parseColor("#100000ff"));
						} else if (match.getStatus() == MatchData.TIE) {
							redScore.setTypeface(null, Typeface.BOLD);
							blueScore.setTypeface(null, Typeface.BOLD);
						}
					return rootView;
				}
			}
		}
		
		public int PX (int dp) {
			final float scale = this.getResources().getDisplayMetrics().density;
			int px = (int) (dp*scale+0.5f);
			return px;
		}
	}

	
}
