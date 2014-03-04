package com.steelhawks.hawkscout.dialogs.competitionmenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steelhawks.hawkscout.CompetitionMenu;
import com.steelhawks.hawkscout.util.ContactsAutoComplete;
import com.steelhawks.hawkscout.util.DialogBuilder;

public class AddMemberFragment extends DialogFragment {
	String[] users;
	String[] groups;
	ContactsAutoComplete cacu;
	ContactsAutoComplete cacg;
	
	public AddMemberFragment() {}
	
	public static AddMemberFragment newInstance() {
		AddMemberFragment frag = new AddMemberFragment();
		return frag;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		cacu = new ContactsAutoComplete(getActivity());
		cacg = new ContactsAutoComplete(getActivity());
		LinearLayout inviteLL = new LinearLayout(getActivity());
					inviteLL.setOrientation(LinearLayout.VERTICAL);
					FrameLayout.LayoutParams inviteLLParams = new FrameLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						inviteLLParams.leftMargin = PX(8);
						inviteLLParams.rightMargin = PX(8);
						inviteLLParams.topMargin = PX(8);
						inviteLLParams.bottomMargin = PX(8);
					inviteLL.setLayoutParams(inviteLLParams);
				TextView iU = new TextView(getActivity());
					iU.setText("Invite Users:");
					iU.setTextSize(18);
					iU.setTypeface(null, Typeface.BOLD);
				inviteLL.addView(iU);
					cacu.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
				inviteLL.addView(cacu);
				TextView iG = new TextView(getActivity());
					iG.setText("Invite Groups:");
					iG.setPadding(0, PX(8), 0, 0);
					iG.setTextSize(18);
					iG.setTypeface(null, Typeface.BOLD);
				inviteLL.addView(iG);
					cacg.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
				inviteLL.addView(cacg);
				TextView sep = new TextView(getActivity());
					sep.setText("Separate addresses with commas");
					sep.setPadding(0, PX(8), 0, 0);
					sep.setTextSize(15);
//					LinearLayout.LayoutParams sepParams = (LinearLayout.LayoutParams) sep.getLayoutParams();
//						sepParams.leftMargin = 8;
//					sep.setLayoutParams(sepParams);
				inviteLL.addView(sep);
				DialogBuilder invite = new DialogBuilder(getActivity());
					invite.setTitle("Add Team Members")
						.setCustomView(inviteLL, false)
						.setPositiveButton("Add", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (zeroInvites()) {
									AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
									builder.setMessage("Are you sure you don't want to invite any users?");
									builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

										@Override
										public void onClick(DialogInterface dialog,	int which) {
											dialog.cancel();
										}	
										
									});
									builder.setNegativeButton("No", new DialogInterface.OnClickListener(){

										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();				
										}
										
									});
									builder.show();
									return;
								} else {
									System.out.println("there are invites");
									for (int i = 0; i<users.length; i++) {
										System.out.println(users[i]);
									}
									for (int i = 0; i<groups.length; i++) {
										System.out.println(groups[i]);
									}
									CompetitionMenu activity = (CompetitionMenu) getActivity();
									activity.new Invite().execute();
								}
							}
							
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();								
							}
						});
			return invite.create();
	}
	
	public int PX (int dp) {
		final float scale = this.getResources().getDisplayMetrics().density;
		int px = (int) (dp*scale+0.5f);
		return px;
	}
	
	public boolean zeroInvites () {
		String emails = cacu.getText().toString();
		System.out.println("original" + emails);
		users = emails.split(",");
		for (int x=0; x<users.length; x++) {
			int start = users[x].indexOf("<");
			int end = users[x].indexOf(">");
			if (start != -1 && end != -1 ) {
				users[x] = users[x].substring(start+1,end);
			}
			users[x] = users[x].trim();
		}
		
		String groupEmails = cacg.getText().toString();
		System.out.println("original" + groupEmails);
		groups = groupEmails.split(",");
		for (int x=0; x<groups.length; x++) {
			int start = groups[x].indexOf("<");
			int end = groups[x].indexOf(">");
			if (start != -1 && end != -1 ) {
				groups[x] = groups[x].substring(start+1,end);
			}
			groups[x] = groups[x].trim();
		}
		
		if ((users.length == 0 && groups.length == 0) ||
				(emails.equals("") && groupEmails.equals(""))) {
			System.out.println(users.length);
			System.out.println(groups.length);
			return true;
		} else {
			System.out.println("user0" + users[0]);
			System.out.println("group0" + groups[0]);
			return false;
		}
	}
}