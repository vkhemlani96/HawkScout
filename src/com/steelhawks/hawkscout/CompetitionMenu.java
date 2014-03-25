//package com.steelhawks.hawkscout;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//
//import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
//import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
//import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.widget.DrawerLayout;
//import android.text.TextUtils.TruncateAt;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup.LayoutParams;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;
//import android.view.animation.Animation.AnimationListener;
//import android.view.animation.AnimationSet;
//import android.view.animation.TranslateAnimation;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.PopupMenu;
//import android.widget.PopupMenu.OnMenuItemClickListener;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ViewFlipper;
//
//import com.google.api.services.drive.model.Permission;
//import com.steelhawks.hawkscout.Globals.Competition;
//import com.steelhawks.hawkscout.Globals.UserTeam;
//import com.steelhawks.hawkscout.dialogs.SimpleTextFragment;
//import com.steelhawks.hawkscout.dialogs.competitionmenu.AddCompetitionFragment;
//import com.steelhawks.hawkscout.dialogs.competitionmenu.AddMemberFragment;
//import com.steelhawks.hawkscout.util.ContactsAutoComplete;
//
//public class CompetitionMenu extends FragmentActivity implements
//	OnRefreshListener{
//	
//	Globals App;
//	String text;
//	LinearLayout main;
//	LinearLayout inviteLayout;
//	RelativeLayout container;
//	List<Integer> dups;
//	boolean refreshed;
//	int teamPosition;
//	int teamSelected;
//	boolean isSelected;
//	boolean isInviteShown;
//	int origHeight;
//	int runnableCount;
//	public boolean b = false;
//	ContactsAutoComplete inviteUser;
//	ContactsAutoComplete inviteGroup;
//	ContactsAutoComplete cacu;
//	ContactsAutoComplete cacg;
//	String[] users;
//	String[] groups;
//	ViewFlipper flipper;
//	public Menu menu;
//	PullToRefreshAttacher mPullToRefreshAttacher;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_competition_menu);
//		App = ((Globals)getApplicationContext());
//		main = (LinearLayout) findViewById(R.id.competition_list);
////		inviteUser = (ContactsAutoComplete) findViewById(R.id.default_email);
////		inviteGroup = (ContactsAutoComplete) findViewById(R.id.default_group);
//		cacu = new ContactsAutoComplete(this);
//		cacg = new ContactsAutoComplete(this);
//		
//		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
//	    PullToRefreshLayout ptrLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
//	    ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);
//		
//		runnableCount = 0;
//
//	    this.getActionBar().setDisplayShowCustomEnabled(true);
//	    this.getActionBar().setDisplayShowTitleEnabled(false);
//	    
//	    isSelected=true;
//	    if (savedInstanceState != null) {
//	    	teamPosition = savedInstanceState.getInt("com.hawkscout.steelhawks.SPINNER_VALUE");
//	    	setActionBarView();
//	    	removeCompList();
//	    	createCompList(teamPosition);
//	    	((Spinner) getActionBar().getCustomView()).setSelection(teamPosition);
//	    	return;
//	    } else {
//			teamPosition = 0;
//	    }
//
//		if (App.isAvailable()) {
//			setActionBarView();
//			removeCompList();
//			createCompList(0);
//		} else {
//			Toast.makeText(this, "App was not available!", Toast.LENGTH_LONG).show();
//			setActionBarView();
//			removeCompList();
//			createCompList(teamPosition);
//			new Refresh().execute();
//		}
//	}
//	
//	public void onPause(Bundle savedInstanceState) {
//		App.saveData();
//		super.onPause();
//	}
//	
//	public void onSaveInstanceState (Bundle savedInstanceState) {
//		savedInstanceState.putInt("com.hawkscout.steelhawks.SPINNER_VALUE", teamPosition);
////		Toast.makeText(this, "SAVING INSTANCE STATE", 2000).show();
//	}
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		this.menu = menu;
//		getMenuInflater().inflate(R.menu.competition_menu, menu);
//		menu.findItem(R.id.action_refresh);
//		return true;
//	}
//	
//	@SuppressWarnings("static-access")
//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		switch(item.getItemId()) {
//			case R.id.action_refresh:
//				new Refresh().execute();
//				break;
//			case R.id.action_add_person:
//				new AddMemberFragment().newInstance()
//					.show(getSupportFragmentManager(), "ADD_MEMBER");
//				break;
//			case R.id.action_messages:
//				break;
//			case R.id.action_settings:
//				startActivity(new Intent(CompetitionMenu.this, com.steelhawks.hawkscout.Settings.class));
//		}
//		return super.onMenuItemSelected(featureId, item);
//	}
//	
//	public void onInviteFinished(View v) {
//		if (zeroInvites()) {
//			System.out.println("there are no invites");
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setMessage("Are you sure you don't want to invite any users");
//			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					isInviteShown = false;
//					removeCompList();
//					createCompList(teamPosition);
//					TranslateAnimation anim = new TranslateAnimation(0, 0, inviteLayout.getHeight(), 0);
//					anim.setDuration(400);
//					anim.setAnimationListener(new AnimationListener() {
//
//						@Override
//						public void onAnimationEnd(Animation arg0) {
////							removeCompList();
////							createCompList(teamPosition);
//							inviteLayout.setVisibility(View.GONE);
//						}
//
//						@Override
//						public void onAnimationRepeat(Animation arg0) {}
//
//						@Override
//						public void onAnimationStart(Animation arg0) {
//							AlphaAnimation alphaAnim = new AlphaAnimation (1.0f, 0.0f);
//								alphaAnim.setDuration(400);
//								alphaAnim.setFillAfter(true);
//								alphaAnim.setAnimationListener(new AnimationListener() {
//
//									@Override
//									public void onAnimationEnd(Animation arg0) {}
//
//									@Override
//									public void onAnimationRepeat(Animation arg0) {}
//
//									@Override
//									public void onAnimationStart(Animation arg0) {}
//									
//								});
//							inviteLayout.startAnimation(alphaAnim);
//							for (int x=0; x<main.getChildCount(); x++) {
//								main.getChildAt(x).setEnabled(true);
//							}
//							for (int x=0; x<inviteLayout.getChildCount(); x++) {
//								inviteLayout.getChildAt(x).setEnabled(false);
//							}
//						}
//						
//					});
//				AlphaAnimation mainAnim = new AlphaAnimation (0.4f, 1.0f);
//					mainAnim.setDuration(400);
//					mainAnim.setFillAfter(true);
//				AnimationSet setAnim = new AnimationSet(true);
//					setAnim.addAnimation(anim);
//					setAnim.setFillAfter(true);
//					setAnim.addAnimation(mainAnim);
//					main.startAnimation(setAnim);		
//				}
//				
//			});
//			builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();				
//				}
//				
//			});
//			builder.show();
//			return;
//		} else {
//			System.out.println("there are invites");
//			for (int i = 0; i<users.length; i++) {
//				System.out.println(users[i]);
//			}
//			for (int i = 0; i<groups.length; i++) {
//				System.out.println(groups[i]);
//			}
//			new Invite().execute();
//		}
//	}
//	
//	public boolean zeroInvites () {
//		String emails = cacu.getText().toString();
//		System.out.println("original" + emails);
//		users = emails.split(",");
//		for (int x=0; x<users.length; x++) {
//			int start = users[x].indexOf("<");
//			int end = users[x].indexOf(">");
//			if (start != -1 && end != -1 ) {
//				users[x] = users[x].substring(start+1,end);
//			}
//			users[x] = users[x].trim();
//		}
//		
//		String groupEmails = cacg.getText().toString();
//		System.out.println("original" + groupEmails);
//		groups = groupEmails.split(",");
//		for (int x=0; x<groups.length; x++) {
//			int start = groups[x].indexOf("<");
//			int end = groups[x].indexOf(">");
//			if (start != -1 && end != -1 ) {
//				groups[x] = groups[x].substring(start+1,end);
//			}
//			groups[x] = groups[x].trim();
//		}
//		
//		if ((users.length == 0 && groups.length == 0) ||
//				(emails.equals("") && groupEmails.equals(""))) {
//			System.out.println(users.length);
//			System.out.println(groups.length);
//			return true;
//		} else {
//			System.out.println("user0" + users[0]);
//			System.out.println("group0" + groups[0]);
//			return false;
//		}
//	}
//
//	@SuppressWarnings("deprecation")
//	public boolean setActionBarView() {
//	    LayoutInflater inflator = (LayoutInflater) this.getSystemService(
//	    		Context.LAYOUT_INFLATER_SERVICE);
//	    View v = inflator.inflate(R.layout.action_bar_view, null);
//	    TextView tv = (TextView) v.findViewById(R.id.title);
//	    tv.setTypeface(null, Typeface.BOLD);
//		if (App.getTeams().size() == 1) {
//		    tv.setText(String.valueOf(App.getTeams().get(0).getTeamNumber()));
//		    this.getActionBar().setCustomView(v);
//		} else {
//			Spinner teams = new Spinner(this);
//			teams.setBackgroundDrawable(
//					getResources().getDrawable(R.drawable.spinner_background_dark));
//				List<String> teamNumbers = new ArrayList<String>();
//					teamNumbers.clear();
//				for (int x=0; x<App.getTeams().size(); x++) {
//					teamNumbers.add(String.valueOf(App.getTeams().get(x).getTeamNumber()));
//				}
//				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//					this, R.layout.action_bar_spinner, teamNumbers);
//					adapter.setDropDownViewResource(
//						android.R.layout.simple_spinner_dropdown_item);
//			teams.setAdapter(adapter);
//			this.getActionBar().setCustomView(teams);
//			isSelected = false;
//			System.out.print("before listener:" + isSelected);
//			teams.setOnItemSelectedListener(new OnItemSelectedListener() {
//				@Override
//				public void onItemSelected(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					if(b) {
//						System.out.println("team Number="+arg2);
//						teamSelected = Integer.parseInt((String) ((TextView) arg1).getText());
//						teamPosition = arg2;
//						removeCompList();
//						createCompList(arg2);
//					}
//					b=true;
//				}
//
//				@Override
//				public void onNothingSelected(AdapterView<?> arg0) {
//					System.out.println("nothing selected");
//				}
//			});
//			System.out.print("after listener:" + isSelected);
//			isSelected = true;
//			System.out.print("final listener:" + isSelected);
//		}
//		return true;
//	}
//	
//	public void removeCompList() {
//		System.out.print("removing comp");
//		main.removeAllViews();
//	}
//	
//	@SuppressWarnings("deprecation")
//	public void createCompList(int i) {
//		if (App.getTeams().size() > 0) {
//			System.out.println("PreExecute" + isInviteShown);
//			List<List<Competition>> lists = new ArrayList<List<Competition>>();
//			List<Competition> completed = new ArrayList<Competition>();
//			List<Competition> ongoing = new ArrayList<Competition>();
//			List<Competition> upcoming = new ArrayList<Competition>();
//			lists.add(ongoing);
//			lists.add(upcoming);
//			lists.add(completed);
//			for (int y=0; y<App.getTeams().get(i).getCompetitions().size(); y++) {
//				Competition comp  = App.getTeams().get(i).getCompetitions().get(y);
//				if (comp
//						.getEndDate().before(new Date(System.currentTimeMillis()))) {
//					completed.add(comp);
//				} else if (comp.getStartDate().after(new Date(System.currentTimeMillis()))) {
//					upcoming.add(comp);
//				} else {
//					ongoing.add(comp);
//					if(comp.getCompInfo().getStatus() != AsyncTask.Status.RUNNING && 
//							comp.getQualSchedule().getStatus() != AsyncTask.Status.RUNNING && 
//							comp.getMatchResults().getStatus() != AsyncTask.Status.RUNNING && 
//							comp.getScoutedTeams().getStatus() != AsyncTask.Status.RUNNING) {
//						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//							comp.getCompInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
//							comp.getQualSchedule().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
//							comp.getMatchResults().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
//							comp.getScoutedTeams().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
//						} else {
//							comp.getCompInfo().execute();
//							comp.getQualSchedule().execute();
//							comp.getMatchResults().execute();
//							comp.getScoutedTeams().execute();
//						}
//					}
//				}
//			}
//			for (int x = 0; x<lists.size(); x++) {
//				if (lists.get(x).size() != 0) {
//					final List<Competition> currentList = lists.get(x);
//					LinearLayout divider = new LinearLayout(this);
//						divider.setOrientation(LinearLayout.HORIZONTAL);
//						divider.setLayoutParams(new LayoutParams(
//								LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//						divider.setPadding(0, 0, 0, PX(10));
//					TextView dT = new TextView(this);
//						dT.setLayoutParams(new LayoutParams(
//								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//						dT.setPadding(0, 0, PX(4), 0);
//						dT.setTypeface(null, Typeface.BOLD);
//						dT.setTextSize(18);
//						dT.setTextColor(getResources().getColor(R.color.crimson));
//						dT.setTextColor(getResources().getColor(android.R.color.black));
//						dT.setTextColor(Color.parseColor("#cc0000"));
//						dT.setTextScaleX(0.9f);
//						switch(x) {
//						case 0: dT.setText("ONGOING");
//							break;
//						case 1: dT.setText("UPCOMING");
//							break;
//						case 2: dT.setText("COMPLETED");
//							break;
//						}
//					divider.addView(dT);
//					View line = new View(this);
//						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//								0, PX(2), 1.0f);
//						params.gravity = Gravity.CENTER_VERTICAL;
//						line.setLayoutParams(params);
//						line.setAlpha(0.5f);
//						line.setBackgroundColor(Color.parseColor("#cc0000"));
//					divider.addView(line);
//					main.addView(divider);
//					System.out.println(currentList.size());
//					for (int y=0; y < currentList.size(); y++) {
//						final int z = y;
//						final RelativeLayout wrapper = new RelativeLayout(this);
//						LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(
//								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//							wrapperParams.leftMargin = PX(8);
//							wrapperParams.rightMargin = PX(8);
//							wrapperParams.bottomMargin = PX(10);
//							wrapper.setLayoutParams(wrapperParams);
//							wrapper.setId(currentList.get(y).getIndex());
//						LinearLayout listing = new LinearLayout(this);
//							listing.setOrientation(LinearLayout.VERTICAL);
//							RelativeLayout.LayoutParams listingParams = new RelativeLayout.LayoutParams(
//									LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//							listing.setLayoutParams(listingParams);
//							listing.setPadding(
//									getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin),
//									getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin),
//									getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin),
//									getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));
//							listing.setBackgroundDrawable(
//									getResources().getDrawable(R.drawable.card_background_pressable));
//							listing.setClickable(true);
//							listing.setOnClickListener(new OnClickListener() {
//	
//								@Override
//								public void onClick(View v) {
//									Intent i = new Intent(CompetitionMenu.this, com.steelhawks.hawkscout.CompetitionActivity.class);
////									Toast.makeText(CompetitionMenu.this, String.valueOf(((View)v.getParent()).getId()), Toast.LENGTH_LONG).show();
//										i.putExtra("com.steelhawks.hawkscout.CompetitionMenu.TEAM_INDEX",
//												teamPosition);
//										i.putExtra("com.steelhawks.hawkscout.CompetitionMenu.COMP_INDEX",
//												((View)v.getParent()).getId());
//										startActivity(i);
//									return;
//								}
//								
//							});
//							
//						TextView lT = new TextView(this);
//							LinearLayout.LayoutParams lTParams = new LinearLayout.LayoutParams(
//								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//								lTParams.gravity = Gravity.CENTER_VERTICAL;
//							lT.setLayoutParams(lTParams);
//							lT.setEllipsize(TruncateAt.END);
//							lT.setSingleLine(true);
//							lT.setTextSize(22);
//							lT.setId(currentList.get(y).getIndex());
//							lT.setText(currentList.get(y).getCompName());
//							lT.setVisibility(View.VISIBLE);
//							lT.setTypeface(null, Typeface.BOLD);
//							listing.addView(lT);
//							
//						LinearLayout listingBottom = new LinearLayout(this);
//							LinearLayout.LayoutParams lBParams = new LinearLayout.LayoutParams(
//									LayoutParams.MATCH_PARENT, PX(46));
//							lBParams.gravity = Gravity.BOTTOM;
//							listingBottom.setLayoutParams(lBParams);
//							listingBottom.setOrientation(LinearLayout.HORIZONTAL);
//						ImageView iV = new ImageView(this);
//							iV.setPadding(0, PX(3), PX(3), 0);
//							iV.setLayoutParams(new LayoutParams(
//									PX(46), PX(46)));
//							switch(currentList.get(y).getCompWeek()) {
//							case 1: iV.setImageDrawable(
//									getResources().getDrawable(R.drawable.competition_menu_calendar_1));
//								break;
//							case 2: iV.setImageDrawable(
//									getResources().getDrawable(R.drawable.competition_menu_calendar_2));
//								break;
//							case 3: iV.setImageDrawable(
//									getResources().getDrawable(R.drawable.competition_menu_calendar_3));
//								break;
//							case 4: iV.setImageDrawable(
//									getResources().getDrawable(R.drawable.competition_menu_calendar_4));
//								break;
//							case 5: iV.setImageDrawable(
//									getResources().getDrawable(R.drawable.competition_menu_calendar_5));
//								break;
//							case 6: iV.setImageDrawable(
//									getResources().getDrawable(R.drawable.competition_menu_calendar_6));
//								break;
//							case 7: iV.setImageDrawable(
//									getResources().getDrawable(R.drawable.competition_menu_calendar_7));
//								break;
//							case 8: iV.setImageDrawable(
//									getResources().getDrawable(R.drawable.competition_menu_calendar_8));
//								break;
//							}
//							listingBottom.addView(iV);
//						LinearLayout details = new LinearLayout(this);
//							LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
//									0, PX(46), 1.0f);
//								detailsParams.gravity = Gravity.BOTTOM;
//							details.setLayoutParams(detailsParams);
//							details.setOrientation(LinearLayout.VERTICAL);
//							details.setPadding(0, PX(3), 0, 0);
//						TextView date = new TextView(this);
//							date.setLayoutParams(new LayoutParams(
//									LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//							date.setTextColor(
//									getResources().getColor(android.R.color.primary_text_light));
//							Calendar start = Calendar.getInstance();
//								start.setTime(currentList.get(y).getStartDate());
//							Calendar end = Calendar.getInstance();
//								end.setTime(currentList.get(y).getEndDate());
//							if (start.get(Calendar.MONTH) == end.get(Calendar.MONTH)) {
//								date.setText(getMonth(start.get(Calendar.MONTH)) + " " + start.get(Calendar.DATE)
//										+ " - " + end.get(Calendar.DATE) + ", 2013");
//							} else {
//								date.setText(getMonth(start.get(Calendar.MONTH)) + " " + start.get(Calendar.DATE)
//										+ " - " + getMonth(end.get(Calendar.MONTH)) + " " + end.get(Calendar.DATE)
//										+ ", 2013");
//							}
//							details.addView(date);
//						TextView location = new TextView(this);
//							location.setLayoutParams(new LayoutParams(
//									LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
//							location.setTextColor(
//									getResources().getColor(android.R.color.primary_text_light));
//							location.setText(currentList.get(y).getCompLoc());
//						details.addView(location);
//						listingBottom.addView(details);
//						listing.addView(listingBottom);
//						ImageButton menu = new ImageButton(this);
//							menu.setImageDrawable(
//									getResources().getDrawable(R.drawable.menu_overflow));
//							menu.setBackgroundDrawable(
//									getResources().getDrawable(R.drawable.crimson_background));
//							RelativeLayout.LayoutParams menuParams = new RelativeLayout.LayoutParams(PX(46), PX(46));
//								menuParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//								menuParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//							menu.setLayoutParams(menuParams);
//							menu.setOnClickListener(new OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									PopupMenu popup = new PopupMenu(CompetitionMenu.this, v);
//									MenuInflater inflater = popup.getMenuInflater();
//									inflater.inflate(R.menu.competition_options, popup.getMenu());
//									popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//	
//										@Override
//										public boolean onMenuItemClick(MenuItem arg0) {
//											switch (arg0.getItemId()) {
//												case R.id.comp_delete: new SimpleTextFragment().newInstance(
//														currentList.get(z).getCompName(),
//														"Are you sure you want to delete this competition?" + System.getProperty("line.separator") + "All your data will be permanently deleted.", 
//														"Delete", 
//														new DialogInterface.OnClickListener() {
//															
//															@Override
//															public void onClick(DialogInterface dialog, int which) {
//																new Delete().execute(wrapper.getId());
//																dialog.cancel();													
//															}
//														}, true).show(getSupportFragmentManager(), "DELETE COMPETITION");
//													break;
//												default: Toast.makeText(CompetitionMenu.this, "YAY YOU PRESSED A BUTTON!", Toast.LENGTH_SHORT).show();
//													break;
//											}
//											return false;
//										}
//										
//									});
//									popup.show();
//								}		
//							});
//						wrapper.addView(listing);
//						wrapper.addView(menu);
//						main.addView(wrapper);
//					}
//				}
//			}
//			Button addComp = new Button(this);
//			addComp.setLayoutParams(new LayoutParams(
//					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//			addComp.setText("Add Another Competition");
//			addComp.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					int[] disabled = new int[App.getTeams().get(teamPosition).getCompetitions().size()];
//					for(int x=0; x<App.getTeams().get(teamPosition).getCompetitions().size(); x++) {
//						disabled[x] = App.getTeams().get(teamPosition).getCompetitions().get(x).getIndex();
//					}
//					AddCompetitionFragment addComp = AddCompetitionFragment.newInstance(disabled, teamPosition);
//						addComp.show(getSupportFragmentManager(), "FRAGMENT_ADD_COMP");
//				}				
//			});
//			main.addView(addComp);
//		}
//	}
//		
//	public boolean isContained (int t, List<Integer> list) {
//		for (int x = 0; x<list.size(); x++) {
//			if (list.get(x) == t) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public int PX (int dp) {
//		final float scale = this.getResources().getDisplayMetrics().density;
//		int px = (int) (dp*scale+0.5f);
//		return px;
//	}
//	
//	public int DP (int px) {
//		final float scale = this.getResources().getDisplayMetrics().density;
//		int dp = (int) ((px-0.5f)/scale);
//		return dp;
//	}
//	
//	public String getMonth (int monthId) {
//		switch (monthId) {
//		case 0: return "January";
//		case 1: return "February";
//		case 2: return "March";
//		case 3: return "April";
//		case 4: return "May";
//		case 5: return "June";
//		case 6: return "July";
//		case 7: return "August";
//		case 8: return "September";
//		case 9: return "October";
//		case 10: return "November";
//		case 11: return "December";
//		default: return "Some Month";
//		}
//	}
//
//	protected class Delete extends AsyncTask <Integer, Integer, Void> {
//		ProgressDialog pD;
//		
//		protected void onPreExecute() {
//			pD = ProgressDialog.show(CompetitionMenu.this, null, "Deleting Competitions...");
//		}
//		
//		@Override
//		protected Void doInBackground(Integer... params) {
//			List<Competition> comps = App.getTeams().get(teamPosition).getCompetitions();
//			String fileId;
//			for(int x=0; x<comps.size(); x++) {
//				if(comps.get(x).getIndex() == params[0]) {
//					fileId = comps.get(x).getDriveFile().getId();
//					try {
//						App.getDrive().files().delete(fileId).execute();
//						App.getTeams().get(teamPosition).getCompetitions().remove(x);
//						return null;
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			return null;
//		}
//		protected void onPostExecute(Void result) {
//			App.getTeams().get(teamPosition).sort();
//			removeCompList();
//			createCompList(teamPosition);
//			pD.cancel();
//		}
//	}
//
//	protected class Refresh extends AsyncTask<MenuItem, Integer, Void> {
//		
//		int[] startTeamNumbers;
//		int[] endTeamNumbers;
//		MenuItem refreshItem;
//		
//		boolean checkForFinished () {
//			for (int x=0; x<App.getTeams().size(); x++) {
//				if (App.getTeams().get(x).getTask().getStatus() == AsyncTask.Status.RUNNING) {
//					return false;
//				}
//			}
//			return true;
//		}
//		
//		boolean sameTeams () {
//			if (startTeamNumbers.length != endTeamNumbers.length) {
//				return false;
//			} else {
//				for (int x=0; x<startTeamNumbers.length; x++) {
//					if (startTeamNumbers[x] != endTeamNumbers[x]) {
//						return false;
//					}
//				}
//			}
//			return true;
//		}
//		
//		int pastTeam () {
//			if (endTeamNumbers.length == 1) {
//				return endTeamNumbers[0];
//			}
//			for (int x=0; x<endTeamNumbers.length; x++) {
//				if (endTeamNumbers[x] == teamSelected) {
//					return x;
//				}
//			}
//			return -1;
//		}
//		
//		@Override
//		protected void onPreExecute() {
//			startTeamNumbers = new int[App.getTeams().size()];
//			for (int x=0; x<App.getTeams().size(); x++) {
//				startTeamNumbers[x] = App.getTeams().get(x).getTeamNumber();
//			}
//			System.out.println("beginning to execute");
//			App.getTeams().clear();
//			final Handler h = new Handler();
//			Runnable r = new Runnable() {
//				@Override
//				public void run() {
//					if(menu.findItem(R.id.action_refresh) != null) {
//						refreshItem = menu.findItem(R.id.action_refresh);
//						refreshItem.setActionView(new ProgressBar(CompetitionMenu.this));
//					} else {
//						h.postDelayed(this, 100);
//					}
//				}		
//			};
//			h.post(r);
//		}
//		
//		protected Void doInBackground(MenuItem... params) {
//			System.out.println("really beginning to execute");
//			App.getAllFolders();
//			System.out.println("done executing");
//			return null;
//		}
//		
//		protected void onPostExecute (Void result) {
//			System.out.println("post executing");
//			Collections.sort(App.getTeams(), new Comparator<UserTeam>() {
//				public int compare(UserTeam one, UserTeam two) {
//					Integer team1 = one.getTeamNumber();
//					Integer team2 = two.getTeamNumber();
//					return team1.compareTo(team2);
//				}
//			});
//			
//			endTeamNumbers = new int[App.getTeams().size()];
//			for (int x=0; x<App.getTeams().size(); x++) {
//				endTeamNumbers[x] = App.getTeams().get(x).getTeamNumber();
//			}
//			
//			if (App.getTeams().size() != 0) {
//				final Handler handler = new Handler();
//				Runnable r = new Runnable() {
//					public void run() {
//						if (checkForFinished()) {
//							if (sameTeams()) {
//								removeCompList();
//								createCompList(teamPosition);
//								if (refreshItem != null) {
//									refreshItem.setActionView(null);
//								}
//							} else if (pastTeam() != -1){
//								if (endTeamNumbers.length == 1) {
//									removeCompList();
//									createCompList(0);
//									setActionBarView();
//									((TextView) getActionBar().getCustomView())
//										.setText(String.valueOf(pastTeam()));
//									teamSelected = pastTeam();
//								} else {
//									removeCompList();
//									createCompList(pastTeam());
//									setActionBarView();
//									((Spinner) getActionBar().getCustomView()).setSelection(pastTeam());
//								}
//								if (refreshItem != null) {
//									refreshItem.setActionView(null);
//								}
//							} else {
//								removeCompList();
//								createCompList(teamPosition);
//								setActionBarView();
//								if (refreshItem != null) {
//									refreshItem.setActionView(null);
//								}
//							}
//						} else {
//							handler.postDelayed(this, 500);
//						}
//					}
//				};
//				handler.postAtFrontOfQueue(r);
//			} else {
//				//TODO what happens if there are no files found
//			}
//		}
//	}
//	public class Invite extends AsyncTask <Integer, Integer, Void> {
//		ProgressDialog pD;
//		protected void onPreExecute() {
//			pD = ProgressDialog.show(CompetitionMenu.this, null, "Inviting Team Members...");
//			
//		}
//		@Override
//		protected Void doInBackground(Integer... params) {
//			for (int x = 0; x<users.length-1; x++) {
//				String address = null;
//				if (users[x].indexOf("@") == -1) {
//					address = users[x].trim() + "@gmail.com";
//				} else {
//					address = users[x].trim();
//				}
//				
//				Permission newPermission = new Permission();
//
//			    newPermission.setValue(address);
//			    newPermission.setType("user");
//			    newPermission.setRole("writer");
//			    System.out.println("TRYING TO DO It " + App.getTeams().size());
//			    for (int y=0; y<App.getTeams().size(); y++) {
//			    	System.out.println("IS the Loop working?");
//				    try {System.out.println("STARTINGOOOOOOOO user" + address);
//					      App.getDrive().permissions().insert(App.getTeams().get(0).getTeamFolder().getId()
//					    		  , newPermission).setSendNotificationEmails(false).execute();
//					      System.out.println("FINITOOOOOOOOO");
//					    } catch (IOException e) {
//					      System.out.println("An error occurred: " + e);
//					    }
//			    }
//			}
//			for (int x = 0; x<groups.length-1; x++) {
//				String address = null;
//				if (groups[x].indexOf("@") == -1) {
//					address = groups[x].trim() + "@googlegroups.com";
//				} else {
//					address = groups[x].trim();
//				}
//				
//				Permission newPermission = new Permission();
//
//			    newPermission.setValue(address);
//			    newPermission.setType("group");
//			    newPermission.setRole("writer");
//			    System.out.println("TRYING TO DO It " + App.getTeams().size());
//			    for (int y=0; y<App.getTeams().size(); y++) {
//			    	System.out.println("IS the Loop working?");
//				    try {System.out.println("STARTINGOOOOOOOO group" + address);
//					      App.getDrive().permissions().insert(App.getTeams().get(0).getTeamFolder().getId()
//					    		  , newPermission).setSendNotificationEmails(false).execute();
//					      System.out.println("FINITOOOOOOOOO");
//					    } catch (IOException e) {
//					      System.out.println("An error occurred: " + e);
//					    }
//			    }
//			}
//			return null;
//		}
//		protected void onPostExecute(Void result) {
//			pD.cancel();
//			isInviteShown = false;
//			removeCompList();
//			createCompList(teamPosition);
//
//			TranslateAnimation anim = new TranslateAnimation(0, 0, inviteLayout.getHeight(), 0);
//				anim.setDuration(400);
//				anim.setAnimationListener(new AnimationListener() {
//
//					@Override
//					public void onAnimationEnd(Animation arg0) {
////						removeCompList();
////						createCompList(teamPosition);
//						inviteLayout.setVisibility(View.GONE);
//					}
//
//					@Override
//					public void onAnimationRepeat(Animation arg0) {}
//
//					@Override
//					public void onAnimationStart(Animation arg0) {
//						AlphaAnimation alphaAnim = new AlphaAnimation (1.0f, 0.0f);
//							alphaAnim.setDuration(400);
//							alphaAnim.setFillAfter(true);
//							alphaAnim.setAnimationListener(new AnimationListener() {
//
//								@Override
//								public void onAnimationEnd(Animation arg0) {}
//
//								@Override
//								public void onAnimationRepeat(Animation arg0) {}
//
//								@Override
//								public void onAnimationStart(Animation arg0) {}
//								
//							});
//						inviteLayout.startAnimation(alphaAnim);
//						for (int x=0; x<main.getChildCount(); x++) {
//							main.getChildAt(x).setEnabled(true);
//						}
//						for (int x=0; x<inviteLayout.getChildCount(); x++) {
//							inviteLayout.getChildAt(x).setEnabled(false);
//						}
//					}
//					
//				});
//			AlphaAnimation mainAnim = new AlphaAnimation (0.4f, 1.0f);
//				mainAnim.setDuration(400);
//				mainAnim.setFillAfter(true);
//			AnimationSet setAnim = new AnimationSet(true);
//				setAnim.addAnimation(anim);
//				setAnim.setFillAfter(true);
//				setAnim.addAnimation(mainAnim);
//				main.startAnimation(setAnim);
//		}
//	}
//	@Override
//	public void onRefreshStarted(View view) {
//		// TODO Auto-generated method stub
//		new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                
//            	
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//
//            	Toast.makeText(CompetitionMenu.this, "REFRESHINGGGGGGGGGG", Toast.LENGTH_LONG).show();
//                new Handler().postDelayed(new Runnable(){
//                		public void run() {mPullToRefreshAttacher.setRefreshComplete();};
//                	}, 5000);
//            }
//        }.execute();
//	}
//
//}
