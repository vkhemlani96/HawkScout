//package com.steelhawks.hawkscout;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.accounts.Account;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
//import android.content.Intent;
//import android.graphics.Typeface;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.View;
//import android.view.ViewGroup.LayoutParams;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ViewFlipper;
//
//import com.google.android.gms.auth.UserRecoverableAuthException;
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
//import com.google.api.client.googleapis.json.GoogleJsonResponseException;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.Drive.Files;
//import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.FileList;
//import com.google.api.services.drive.model.ParentReference;
//import com.google.api.services.drive.model.Permission;
//import com.steelhawks.hawkscout.Globals.UserTeam;
//import com.steelhawks.hawkscout.util.ContactsAutoComplete;
//
//
//public class SignIn extends Activity implements View.OnClickListener {
//	public Globals App;
//	public RadioGroup rG;
//	public Account[] accounts;
//	public ViewFlipper signInFlipper;
//	public ViewFlipper compFlipper;
//	public LinearLayout compList;
//	public AlertDialog dialog;
//	public Intent nextActivity;
//	public ProgressBar progressBar;
//	public AsyncTask<Integer, Integer, Void> authorize;
//	public EditText editTeamNumber;
//	public LinearLayout mainSelectedComp;
//	public String[] compCode;
//	public ContactsAutoComplete inviteUser;
//	public ContactsAutoComplete	inviteGroup;
//	public String[] users;
//	public String[] groups;
//	public int teamNumber;
//	
//	@SuppressWarnings("deprecation")
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		App = ((Globals)getApplicationContext());
//		setContentView(R.layout.sign_in);
//		nextActivity = new Intent(this, CompetitionMenu.class);
//		authorize = new Authorize();
//
//		inviteUser = (ContactsAutoComplete) findViewById(R.id.default_email);
//		inviteGroup = (ContactsAutoComplete) findViewById(R.id.default_group);
//
//		mainSelectedComp = (LinearLayout) findViewById(R.id.selected_competitions);
//		
//		//SetUp Flippers
//		signInFlipper = (ViewFlipper) findViewById(R.id.sign_in_flipper);
//		signInFlipper.setInAnimation(this, R.anim.in_from_right);
//		signInFlipper.setOutAnimation(this, R.anim.out_to_left);
//		compFlipper = (ViewFlipper) findViewById(R.id.competition_flipper);
//		
//		//Change ActionBar Font
//	    this.getActionBar().setDisplayShowCustomEnabled(true);
//	    this.getActionBar().setDisplayShowTitleEnabled(false);
//	    LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    View v = inflator.inflate(R.layout.action_bar_view, null);
//	    TextView tv = (TextView) v.findViewById(R.id.title);
//	    tv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HemiHead.otf"));
//	    tv.setText(this.getTitle());
//	    this.getActionBar().setCustomView(v);
//		
//		//set up the accounts
//		accounts = new GoogleAccountManager(this).getAccounts();
//		
//		for (int x=0;x<accounts.length;x++) {
//			rG = (RadioGroup) findViewById(R.id.radio_group);
//			RadioButton rB = new RadioButton(this);
//			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//				rB.setPadding(PX(40), PX(9), 0, PX(10));
//			} else {
//				rB.setPadding(0, PX(9), 0, PX(10)); //67,13
//			}
//			rB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//				
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//					if (isChecked) {
//						App.setEmail((String) buttonView.getText());
//					}
//				}
//			});
//			rB.setText(accounts[x].name);
//			rB.setTextSize(18);
//			rG.addView(rB);
//		}
//		
//		//set up the competition list
//		compList = (LinearLayout) findViewById(R.id.competition_list);
//		for (int x=1; x<=8; x++ ){
//			TextView comp = new TextView(this);
//				comp.setPadding(16, 8, 16, 8);
//				if (x!=8) {
//					comp.setText("Week " + x);
//				} else {
//					comp.setText("FIRST Championship Event");
//				}
//				comp.setTextAppearance(this, android.R.style.TextAppearance_Small);
//				comp.setTextColor(getResources().getColor(R.color.crimson));
//				compList.addView(comp);
//			View redSeperator = new View(this);
//				//LayoutParams sepParams = ;
//				redSeperator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3));
//				redSeperator.setBackgroundColor(getResources().getColor(R.color.crimson));
//				compList.addView(redSeperator);
//			final String[] competitions = getResources().getStringArray(R.array.competitions);	
//			final String[] compWeek = getResources().getStringArray(R.array.comp_week);
//				for (int y=0; y<competitions.length; y++) {
//					final int idInt = y+1;
//					final int position = y;
//					if(compWeek[y].equals("Week "+x)) {
//						CheckBox compView = new CheckBox(this);
//							compView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 85));
//							if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//								compView.setPadding(PX(40), 0, 16, 0);
//							} else {
//								compView.setPadding(0, 0, 16, 0);
//							}
//							//NOTE CHECKBOX IDs: 1<x<100
//							compView.setId(idInt);
//							compView.setOnCheckedChangeListener(new OnCheckedChangeListener () {
//								public void onCheckedChanged (CompoundButton b, boolean isChecked) {
//									if (isChecked) {
//										System.out.println(b.getId());
//										LinearLayout selectedComp = new LinearLayout(SignIn.this);
//										//-------------------------LINEARLAYOUT IDs: 100<x<10000
//											selectedComp.setId(idInt*100);
//											selectedComp.setLayoutParams(new LayoutParams(
//													LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//										ImageButton delete = new ImageButton(SignIn.this);	
//											RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
//													LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//													buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//											delete.setLayoutParams(buttonParams);
//											//-------------------------BUTTON IDs: 10000<x<1000000
//											delete.setId(idInt*10000);
//											delete.setOnClickListener(SignIn.this);
//											delete.setImageResource(R.drawable.remove_icon);				
//											delete.setBackgroundResource(R.drawable.crimson_background);
//										TextView selectedCompName = new TextView(SignIn.this);
//											selectedCompName.setText(competitions[position]);
//											selectedCompName.setTextAppearance(
//													SignIn.this, android.R.style.TextAppearance_Medium);
//											LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
//													0, LayoutParams.WRAP_CONTENT, 1f);
//											textViewParams.gravity = Gravity.CENTER_VERTICAL;
//											selectedCompName.setLayoutParams(textViewParams);
//										View greySeperator = new View(SignIn.this);
//											greySeperator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
//											greySeperator.setBackgroundColor(getResources().getColor(R.color.gray));
//										selectedComp.addView(selectedCompName);
//										selectedComp.addView(delete);
//										mainSelectedComp.addView(selectedComp);
//										mainSelectedComp.addView(greySeperator);
//									} else {
//										LinearLayout parentView = (LinearLayout) findViewById(R.id.selected_competitions);
//										System.out.println("clicked id" + b.getId()*100);
//										for (int z=0; z<parentView.getChildCount(); z++) {
//											System.out.println("child ids" + parentView.getChildAt(z).getId());
//											if (parentView.getChildAt(z).getId() == b.getId()*100) {
//												parentView.removeViewAt(z);
//												parentView.removeViewAt(z);
//											}
//										}
//									}
//								}
//							});
//							compView.setText(competitions[position]);
//							compView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
//							compList.addView(compView);
//						View greySeperator = new View(this);
//							greySeperator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
//							greySeperator.setBackgroundColor(getResources().getColor(R.color.gray));
//							compList.addView(greySeperator);
//					}
//				}
//		}
//
////		AutoCompleteTextView dE = (AutoCompleteTextView)findViewById(R.id.default_email);
////		AutoCompleteTextView dG = (AutoCompleteTextView)findViewById(R.id.default_group);
////	    Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);
////	    startManagingCursor(emailCursor);
////	    dE.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line, emailCursor, new String[] {Email.DATA1}, new int[] {android.R.id.text1}));
////	    dE.setThreshold(1);
////	    dG.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line, emailCursor, new String[] {Email.DATA1}, new int[] {android.R.id.text1}));
////	    dG.setThreshold(1);
//		
//		
//	}
//	
//	protected void onStart () {		
//		super.onStart();
//		
//		//Set-up the ProgressBar
//		progressBar = (ProgressBar) findViewById(R.id.progressbar);
//		if (App.getSettings().getString(App.STORED_EMAIL, null) != null) {
//			App.setEmail(App.getSettings().getString(App.STORED_EMAIL, null));
//			progressBar.setVisibility(View.VISIBLE);
//			authorize.execute();
//			//TODO catch IllegalStateException when closing and opening application.
//		} else {
//			signInFlipper.setInAnimation(this ,R.anim.do_nothing);
//			signInFlipper.setOutAnimation(this, R.anim.do_nothing);
//			signInFlipper.showNext();
//		}
//	}
//
//	protected void onRestart () {
//		super.onRestart();
//		App.getTeams().clear();
//	}
//	
//	public void onClick (View v) {
//		switch (v.getId()) {
//		default:
//			removeComp(v);
//			break;
//		}
//	}
//	
//	public void onBackPressed () {
//		signInFlipper.showNext();
//	}
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.signin, menu);
//		return true;
//	}
//	
//	public void authorize (UserRecoverableAuthException e) {
//		startActivityForResult(e.getIntent(), 0);
//	}
//	protected void onActivityResult (int reqCode, int resCode, Intent data) {
//		switch (reqCode) {
//		case 8: 
//			signInFlipper.setInAnimation(this ,R.anim.in_from_right);
//			signInFlipper.setOutAnimation(this, R.anim.out_to_left);
//			signInFlipper.setDisplayedChild(2);
//		}
//	}
//	
//	public void flipView (View v) {
//		switch (v.getId()) {
//			case R.id.continue_login:
//				if (App.getEmail() == null) {
//					AlertDialog.Builder builder = new AlertDialog.Builder(this);
//					builder.setMessage("You must select a Google Account to continue.")
//							.setPositiveButton(android.R.string.ok, new OnClickListener () {
//								@Override
//								public void onClick(DialogInterface dialog, int arg1) {
//									dialog.dismiss();
//								}
//							})  
//							.setNeutralButton("Help", new OnClickListener () {
//								public void onClick(DialogInterface dialog, int arg1) {
//									dialog.dismiss();
//									//TODO open help box
//								}
//							})
//							.create()
//							.show();
//				break;
//				}
//				if (((CheckBox) findViewById(R.id.remember_me)).isChecked()) {
//					App.editSettings().putString(App.STORED_EMAIL, App.getEmail())
//						.commit();
//				}
//				authorize.execute();
//				break;
//			case R.id.continue_competitions:
//				editTeamNumber = (EditText) findViewById(R.id.team_number);
//				if (editTeamNumber.getText().toString().equals("")) {
//					AlertDialog.Builder builder = new AlertDialog.Builder(this);
//					builder.setMessage("Please enter a team number to continue.")
//							.setPositiveButton(android.R.string.ok, new OnClickListener () {
//								@Override
//								public void onClick(DialogInterface dialog, int arg1) {
//									dialog.dismiss();
//								}
//							})
//							.create()
//							.show();
//					return;
//				}
//				
//				if (mainSelectedComp.getChildCount() == 0) {
//					System.out.println("there are no children");
//					AlertDialog.Builder builder = new AlertDialog.Builder(this);
//					builder.setMessage("Please choose at least one competition to continue.")
//							.setPositiveButton(android.R.string.ok, new OnClickListener () {
//								@Override
//								public void onClick(DialogInterface dialog, int arg1) {
//									dialog.dismiss();
//								}
//							})
//							.create()
//							.show();
//					return;
//				}
//				
//				teamNumber = Integer.parseInt(editTeamNumber.getText().toString());
//
//				AsyncTask<Integer, Integer, Void> createTeam = new CreateTeam();
//				createTeam.execute();
//				break;
//			case R.id.select_competitions:
//				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//				
//				compFlipper.setInAnimation(this ,R.anim.in_from_bottom);
//				compFlipper.setOutAnimation(this, R.anim.do_nothing);
//				compFlipper.showNext();
//				break;
//			case R.id.finish_select_competitions:
//				compFlipper.setInAnimation(this, R.anim.do_nothing);
//				compFlipper.setOutAnimation(this, R.anim.out_to_bottom);
//				compFlipper.showPrevious();
//				break;
//			case R.id.finish_invite_members:
//				if (zeroInvites()) {
//					System.out.println("there are no invites");
//					AlertDialog.Builder builder = new AlertDialog.Builder(this);
//					builder.setMessage("Are you sure you don't want to invite any users?")
//							.setPositiveButton("Yes", new OnClickListener () {
//								@Override
//								public void onClick(DialogInterface dialog, int arg1) {
//									startActivity(nextActivity);
//								}
//							})
//							.setNegativeButton("No", new OnClickListener () {
//								@Override
//								public void onClick(DialogInterface dialog, int arg1) {
//									dialog.dismiss();
//								}
//							})							
//							.create()
//							.show();
//					return;
//				} else {
//					System.out.println("there are invites");
//					for (int i = 0; i<users.length; i++) {
//						System.out.println(users[i]);
//					}
//					for (int i = 0; i<groups.length; i++) {
//						System.out.println(groups[i]);
//					}
//					new Invite().execute();
//				}
//				break;
//			default:
//				signInFlipper.showNext();
//				break;
//		}
//	}
//	
//	public void removeComp (View v) {
//		LinearLayout parentView = (LinearLayout) findViewById(R.id.selected_competitions);
//		for (int x=0; x<parentView.getChildCount(); x++) {
//			if (parentView.getChildAt(x) == (View)v.getParent()) {
//				parentView.removeViewAt(x);
//				parentView.removeViewAt(x);
//				CheckBox unCheck = (CheckBox) findViewById(v.getId()/10000);
//				unCheck.setChecked(false);
//			}
//		}
//	}
//
//	public boolean zeroInvites () {
//		String emails = inviteUser.getText().toString();
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
//		String groupEmails = inviteGroup.getText().toString();
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
//	public int PX (int dp) {
//		final float scale = this.getResources().getDisplayMetrics().density;
//		int px = (int) (dp*scale+0.5f);
//		return px;
//	}
//	public int DP (int px) {
//		final float scale = this.getResources().getDisplayMetrics().density;
//		int dp = (int) ((px-0.5f)/scale);
//		return dp;
//	}
//		
//	protected class Authorize extends AsyncTask <Integer, Integer, Void> {
//		int status;
//		boolean found;
//		boolean alreadyAuthorized = true;
//		ProgressDialog pD;
//		
//		boolean checkForFinished () {
//			for (int x=0; x<App.getTeams().size(); x++) {
//				if (App.getTeams().get(x).getTask().getStatus() == AsyncTask.Status.RUNNING) {
//					return false;
//				}
//			}
//			return true;
//		}
//		protected void onPreExecute() {
//			pD = ProgressDialog.show(SignIn.this, null, "Signing In...");
//		}
//		@Override
//		protected Void doInBackground(Integer... arg0) {
//			
//			//Set-Up Drive and Spreadsheet Services
//			App.getCredentials().setSelectedAccountName(App.getEmail());
//			Drive d = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), App.getCredentials())
//						.setApplicationName("HawkScout")
//						.build();
//			App.setDrive(d);
//			try {
//				App.getService().setAuthSubToken(App.getCredentials().getToken());
//			} catch (UserRecoverableAuthException e1) {
//				alreadyAuthorized = false;
//				authorize(e1);
//				try {
//					App.getService().setAuthSubToken(App.getCredentials().getToken());
//				} catch (Exception e2) {
//					e1.printStackTrace();
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//			
//			//Find HawkScout Data
//			publishProgress(1);
//				App.getAllFolders();
////			try{
////				Files.List request;
////				request = App.getDrive().files().list();
////				request.setQ("title contains '(DO NOT DELETE) HawkScout Scouting Data:' and mimeType = 'application/vnd.google-apps.folder'");
////				  do {
////					    try {
////					      FileList files = request.execute();
////	
////					      foundFiles.addAll(files.getItems());
////					      request.setPageToken(files.getNextPageToken());
////					    } catch (IOException e) {
////					      System.out.println("An error occurred: " + e);
////					      request.setPageToken(null);
////					    }
////					  } while (request.getPageToken() != null &&
////					           request.getPageToken().length() > 0);
////				  for (int x=0; x<foundFiles.size(); x++) {
////					  Team t = App.new Team(foundFiles.get(x), true);
////					  App.getTeams().add(t);
////				  }
////				  System.out.println("" + foundFiles.size());
////			} catch (IOException e1) {
////				e1.printStackTrace();
////			}
//			return null;
//		}
//		protected void onPostExecute (Void result) {
//			pD.dismiss();
//			Collections.sort(App.getTeams(), new Comparator<UserTeam>() {
//				public int compare(UserTeam one, UserTeam two) {
//					Integer team1 = one.getTeamNumber();
//					Integer team2 = two.getTeamNumber();
//					return team1.compareTo(team2);
//				}
//			});
//			
//			if (App.getTeams().size() != 0) {
//				Timer timer = new Timer();
//				timer.scheduleAtFixedRate(new TimerTask() {
//					public void run() {
//						if (checkForFinished()) {
//							this.cancel();
////							pD.cancel();
//							startActivity(nextActivity);
//							finish();
//						}
//					}
//				}, 0 ,500);	
//			} else {
//				pD.cancel();
//				if (alreadyAuthorized) {
//					signInFlipper.setInAnimation(SignIn.this ,R.anim.in_from_right);
//					signInFlipper.setOutAnimation(SignIn.this, R.anim.out_to_left);
//					signInFlipper.setDisplayedChild(2);
//				}
//			}
//		}
//		protected void  onProgressUpdate (Integer... progress) {
//			switch (progress[0]) {
//				case 1: pD.setMessage("Retrieving HawkScout Data...");
//			}
//		}
//		
//	}
//	
//	protected class CreateTeam extends AsyncTask <Integer, Integer, Void> {
//		ProgressDialog pD;
//		protected void onPreExecute () {
//			pD = ProgressDialog.show(SignIn.this, null, "Creating Team...");
//		}
//		
//		@Override
//		protected Void doInBackground(Integer... arg0) {
//			System.out.println("creating files");
//			try {
//				File parent = new File();
//				parent.setTitle("(DO NOT DELETE) HawkScout Scouting Data:" + teamNumber)
//					.setMimeType("application/vnd.google-apps.folder");
//				parent = App.getDrive().files().insert(parent).execute();
//				UserTeam team = App.new UserTeam(parent, false);
//				App.getTeams().add(team);
//				System.out.println("Finished creating team");
//
//				System.out.println("Creating Conversation Log");
//				File convoFile = new File();
//				convoFile.setTitle("(DO NOT DELETE) HawkScout2013 ConversationLog")
//					.setMimeType("application/vnd.google-apps.spreadsheet")
//					.setParents(Arrays.asList(new ParentReference().setId(parent.getId())));
//				convoFile = App.getDrive().files().insert(convoFile).execute();
//				team.setConversationLog(App.new ConversationLog(convoFile));
//				System.out.println("Finished creating log");
//
//				System.out.println("Creating comp");
//				compCode = getResources().getStringArray(R.array.comp_code);
//				for (int x=0; x<mainSelectedComp.getChildCount(); x++) {
//					if(mainSelectedComp.getChildAt(x).getId() != -1) {
//						File file = new File();
//						file.setTitle("(DO NOT DELETE) HawkScout2013." + 
//						compCode[mainSelectedComp.getChildAt(x).getId()/100-1])
//							.setMimeType("application/vnd.google-apps.spreadsheet")
//							.setParents(Arrays.asList(new ParentReference().setId(parent.getId())));
//						file = App.getDrive().files().insert(file).execute();
//						team.addCompetition(App.new Competition(file, true));
//					}
//				}
//				System.out.println("ConvoLog ID:" + App.getTeams().get(0).getConversationLog().getDriveFile().getId());
//				System.out.println("COmp ID:" + App.getTeams().get(0).getCompetitions().get(0).getDriveFile().getId());
//			} catch (GoogleJsonResponseException e) {
//				Toast.makeText(SignIn.this, "An error has occured", Toast.LENGTH_SHORT).show();
//				e.printStackTrace();
//			} catch (IOException e) {
//				Toast.makeText(SignIn.this, "An error has occured", Toast.LENGTH_SHORT).show();
//				e.printStackTrace();
//			} finally {
//				
//			}
//			return null;
//		}
//		
//		protected void onPostExecute(Void result) {
//			pD.cancel();
//			signInFlipper.setInAnimation(SignIn.this ,R.anim.in_from_right);
//			signInFlipper.setOutAnimation(SignIn.this, R.anim.out_to_left);
//			signInFlipper.showNext();
//		}
//		
//	}
//
//	protected class Invite extends AsyncTask <Integer, Integer, Void> {
//		ProgressDialog pD;
//		protected void onPreExecute() {
//			pD = ProgressDialog.show(SignIn.this, null, "Inviting Team Members...");
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
//			startActivity(nextActivity);
//		}
//	}
//}
