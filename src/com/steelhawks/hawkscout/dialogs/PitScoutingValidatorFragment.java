package com.steelhawks.hawkscout.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.steelhawks.hawkscout.util.DialogBuilder;

public class PitScoutingValidatorFragment extends DialogFragment {
	
	static String teamNumber;
	static View v;
	boolean finished = false;
	
	public PitScoutingValidatorFragment() {}
	
	public PitScoutingValidatorFragment newInstance(String s, View view) {
		teamNumber = s;
		v = view;
		PitScoutingValidatorFragment frag = new PitScoutingValidatorFragment();
		return frag;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
				DialogBuilder dialog = new DialogBuilder(getActivity());
					dialog.setMessage("Team " + teamNumber + " does not appear to be at this competition. " +
							"Do you want to continue anyway?")
						.setPositiveButton("Continue", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();								
							}
							
						})
						.setNegativeButton("Fix", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								v.setFocusable(true);
								v.setFocusableInTouchMode(true);
								v.requestFocus();
							}
						});
			return dialog.create();
	}
	
	
	
	public boolean isFinished() {
		return finished;
	}
	
	public int PX (int dp) {
		final float scale = this.getResources().getDisplayMetrics().density;
		int px = (int) (dp*scale+0.5f);
		return px;
	}
}