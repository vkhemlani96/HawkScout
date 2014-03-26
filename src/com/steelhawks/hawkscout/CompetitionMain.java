package com.steelhawks.hawkscout;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.steelhawks.hawkscout.competitionmain.TeamAdapter;
import com.steelhawks.hawkscout.data.Competition;
import com.steelhawks.hawkscout.data.Indices.MatchIndex;
import com.steelhawks.hawkscout.util.Utilities;

public class CompetitionMain extends FragmentActivity implements
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
	static ListView listView;
	public static Competition comp;

	static RankingsFragment frag1;
	static MatchesFragment frag2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_competition_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		//TODO make dynamic competition?
		comp = new Competition(this,"SCMB");
		frag1 = new RankingsFragment();
		frag2 = new MatchesFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.competition_main, menu);
		menu.findItem(R.id.match).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				MatchScouting.start(CompetitionMain.this);
				return false;
			}
		});
		menu.findItem(R.id.pit).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				PitScoutingMain.start(CompetitionMain.this);
				return false;
			}
		});
		return true;
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
				listView.setAdapter(new TeamAdapter(this, comp.getRankings(), SortBy.TEAM));
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
				listView.setAdapter(new TeamAdapter(this, comp.getRankings(), SortBy.QUAL));
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
						null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
				v.setTag(ASCENDING);
			}
			break;
		case R.id.assistp_header:
			if(adapter.sort == SortBy.ASSIST) {
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
				listView.setAdapter(new TeamAdapter(this, comp.getRankings(), SortBy.ASSIST));
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
				listView.setAdapter(new TeamAdapter(this, comp.getRankings(), SortBy.AUTON));
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
						null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
				v.setTag(ASCENDING);
			}
			break;
		case R.id.trussp_header:
			if(adapter.sort == SortBy.TRUSS) {
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
				listView.setAdapter(new TeamAdapter(this, comp.getRankings(), SortBy.TRUSS));
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
				listView.setAdapter(new TeamAdapter(this, comp.getRankings(), SortBy.TELE));
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(
						null, null, null, getResources().getDrawable(R.drawable.rankings_sort_ascending));
				v.setTag(ASCENDING);
			}
			break;
		}
		TextView tv;
		int id = 0;
		switch (adapter.sort) {
		case SortBy.TEAM:
			id = R.id.team_header;
			break;
		case SortBy.QUAL:
			id = R.id.qp_header;
			break;
		case SortBy.ASSIST:
			id = R.id.assistp_header;
			break;
		case SortBy.AUTON:
			id = R.id.ap_header;
			break;
		case SortBy.TRUSS:
			id = R.id.trussp_header;
			break;
		case SortBy.TELE:
			id = R.id.tp_header;
			break;
		}
		tv = (TextView) ((LinearLayout)v.getParent()).findViewById(id);
		tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
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
			switch (position) {
			case 0: return frag1;
			case 1: return frag2;
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "Rankings".toUpperCase();
			case 1:
				return "Matches".toUpperCase(l);
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
					R.layout.fragment_competition_main_dummy, container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	
	public class SortBy {
		public final static int TEAM = 1;
		public final static int QUAL = 2;
		public final static int ASSIST = 3;
		public final static int AUTON = 4;
		public final static int TRUSS = 5;
		public final static int TELE = 6;
	}
	
	public static class RankingsFragment extends Fragment {
		
		public RankingsFragment(){}
		
		@SuppressLint("NewApi")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final String[] teamNumbers = comp.getTeams();
			final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_rankings_layout, null);
			listView = (ListView) rootView.findViewById(R.id.rankings);
			listView.setAdapter(new TeamAdapter(getActivity(), comp.getRankings(), SortBy.QUAL));
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					TextView teamNumber = (TextView) arg1.findViewById(R.id.team_number);
					String team = ((String) teamNumber.getText()).trim();
					TeamActivityMain.start(getActivity(), team);
				}
			});
			rootView.findViewById(R.id.qp_header).setTag(true);

			final AutoCompleteTextView teamInput = (AutoCompleteTextView) rootView.findViewById(R.id.team_input);
			Collections.sort(Arrays.asList(teamNumbers), new Comparator<String>() {

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
					for(int x=0; x<((TeamAdapter)listView.getAdapter()).getTeams().size(); x++) {
						if (str.equals(((TeamAdapter)listView.getAdapter()).getTeams().get(x)[1].trim())) {
							Utilities.closeKeyboard(getActivity());
							listView.setSelection(x);
							teamInput.setText("");
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
	}

	public static class MatchesFragment extends Fragment {
		
		public MatchesFragment(){}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ListView matches = new ListView(getActivity());
			matches.setAdapter(new MatchAdapter(getActivity(), comp.getMatches()));
			matches.setFastScrollEnabled(true);
			return matches;
		}
		
		class MatchAdapter extends ArrayAdapter<String[]> {

			private final Context context;
			private final List<String[]> list;
			
			public MatchAdapter(Context c, List<String[]> list) {
				super(c, R.layout.competitions_matches_layout, list);
				this.context = c;
				this.list = list;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				String[] match = list.get(position);
				if(match.length < 10 || 
						(match[MatchIndex.RED_SCORE].trim().equals("") && match[MatchIndex.BLUE_SCORE].trim().equals(""))) {
					ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_matches_layout, parent, false);
						TextView matchNumber = (TextView) rootView.findViewById(R.id.match_number);
						matchNumber.setText(match[MatchIndex.MATCH_NUMBER].trim());
						TextView red1 = (TextView) rootView.findViewById(R.id.red1);
						red1.setText(match[MatchIndex.RED1].trim());
						TextView red2 = (TextView) rootView.findViewById(R.id.red2);
						red2.setText(match[MatchIndex.RED2].trim());
						TextView red3 = (TextView) rootView.findViewById(R.id.red3);
						red3.setText(match[MatchIndex.RED3].trim());
						TextView blue1 = (TextView) rootView.findViewById(R.id.blue1);
						blue1.setText(match[MatchIndex.BLUE1].trim());
						TextView blue2 = (TextView) rootView.findViewById(R.id.blue2);
						blue2.setText(match[MatchIndex.BLUE2].trim());
						TextView blue3 = (TextView) rootView.findViewById(R.id.blue3);
						blue3.setText(match[MatchIndex.BLUE3].trim());
						TextView time = (TextView) rootView.findViewById(R.id.time);
						time.setText(match[MatchIndex.TIME].trim());
					return rootView;
				} else {
					ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.competitions_matches_finished_layout, parent, false);
						TextView matchNumber = (TextView) rootView.findViewById(R.id.match_number);
						matchNumber.setText(match[MatchIndex.MATCH_NUMBER].trim());
						TextView redScore = (TextView) rootView.findViewById(R.id.red_score);
						redScore.setText(match[MatchIndex.RED_SCORE].trim());
						TextView red1 = (TextView) rootView.findViewById(R.id.red1);
						red1.setText(match[MatchIndex.RED1].trim());
						TextView red2 = (TextView) rootView.findViewById(R.id.red2);
						red2.setText(match[MatchIndex.RED2].trim());
						TextView red3 = (TextView) rootView.findViewById(R.id.red3);
						red3.setText(match[MatchIndex.RED3].trim());
						TextView blueScore = (TextView) rootView.findViewById(R.id.blue_score);
						blueScore.setText(match[MatchIndex.BLUE_SCORE].trim());
						TextView blue1 = (TextView) rootView.findViewById(R.id.blue1);
						blue1.setText(match[MatchIndex.BLUE1].trim());
						TextView blue2 = (TextView) rootView.findViewById(R.id.blue2);
						blue2.setText(match[MatchIndex.BLUE2].trim());
						TextView blue3 = (TextView) rootView.findViewById(R.id.blue3);
						blue3.setText(match[MatchIndex.BLUE3].trim());
						boolean redWon = Integer.parseInt(match[MatchIndex.RED_SCORE].trim()) >
						Integer.parseInt(match[MatchIndex.BLUE_SCORE].trim());
						boolean blueWon = Integer.parseInt(match[MatchIndex.RED_SCORE].trim()) <
						Integer.parseInt(match[MatchIndex.BLUE_SCORE].trim());
						if (redWon) {
							LinearLayout alliance = (LinearLayout) rootView.findViewById(R.id.blue_alliance);
//								alliance.setAlpha(0.75f);
							red1.setTypeface(null, Typeface.BOLD);
							red2.setTypeface(null, Typeface.BOLD);
							red3.setTypeface(null, Typeface.BOLD);
							redScore.setTypeface(null, Typeface.BOLD);
							redScore.setBackgroundColor(Color.parseColor("#10ff0000"));
						} else if (blueWon) {
							LinearLayout alliance = (LinearLayout) rootView.findViewById(R.id.red_alliance);
//								alliance.setAlpha(0.75f);
							blue1.setTypeface(null, Typeface.BOLD);
							blue2.setTypeface(null, Typeface.BOLD);
							blue3.setTypeface(null, Typeface.BOLD);
							blueScore.setTypeface(null, Typeface.BOLD);
							blueScore.setBackgroundColor(Color.parseColor("#100000ff"));
						} else {
							redScore.setTypeface(null, Typeface.BOLD);
							blueScore.setTypeface(null, Typeface.BOLD);
						}
					return rootView;
				}
			}
		}
	}


}
