package com.steelhawks.hawkscout.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.steelhawks.hawkscout.R;
import com.steelhawks.hawkscout.util.DialogBuilder;
import com.steelhawks.hawkscout.asynctasks.AddCompetitions;

public class AddCompetitionFragment extends DialogFragment {
	int[] disabled;
	int teamIndex;
	ScrollView addCompView;
	AlertDialog addCompDialog;

	private static final String disabledKey = "com.steelhawks.hawkscout.DISABLED";
	private static final String teamIndexKey = "com.steelhawks.hawkscout.TEAM_INDEX";
	
	public AddCompetitionFragment() {}
	
	public static AddCompetitionFragment newInstance(int[] disabled, int teamIndex) {
		AddCompetitionFragment frag = new AddCompetitionFragment();
		Bundle args = new Bundle();
			args.putIntArray(disabledKey, disabled);
			args.putInt(teamIndexKey, teamIndex);
		frag.setArguments(args);
		return frag;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		disabled = getArguments().getIntArray(disabledKey);
		teamIndex = getArguments().getInt(teamIndexKey);
		
		addCompView = new ScrollView(getActivity());
			addCompView.setId(10000);
			final LinearLayout compList = new LinearLayout(getActivity());
				compList.setOrientation(LinearLayout.VERTICAL);
			for (int x=1; x<=8; x++ ){
				TextView comp = new TextView(getActivity());
					comp.setPadding(16, 8, 16, 8);
					if (x!=8) {
						comp.setText("Week " + x);
					} else {
						comp.setText("FIRST Championship Event");
					}
					comp.setTextAppearance(getActivity(), android.R.style.TextAppearance_Small);
					comp.setTextColor(getResources().getColor(R.color.crimson));
					compList.addView(comp);
				View redSeperator = new View(getActivity());
					redSeperator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3));
					redSeperator.setBackgroundColor(getResources().getColor(R.color.crimson));
					compList.addView(redSeperator);
				final String[] competitions = getResources().getStringArray(R.array.competitions);	
				final String[] compWeek = getResources().getStringArray(R.array.comp_week);
					for (int y=0; y<competitions.length; y++) {
						final int idInt = y+1;
						final int position = y;
						if(compWeek[y].equals("Week "+x)) {
							CheckBox compView = new CheckBox(getActivity());
								compView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 85));
								if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
									compView.setPadding(PX(40), 0, 16, 0);
								} else {
									compView.setPadding(0, 0, 16, 0);
								}
								//NOTE CHECKBOX IDs: 1<x<100
								compView.setId(idInt);
								if (isContained(idInt-1, disabled)) {
									compView.setChecked(true);
									compView.setEnabled(false);
								}
								compView.setText(competitions[position]);
								compView.setMaxLines(2);
								compView.setEllipsize(TruncateAt.END);
								compView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
								compList.addView(compView);
							View greySeperator = new View(getActivity());
								greySeperator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
								greySeperator.setBackgroundColor(getResources().getColor(R.color.gray));
								compList.addView(greySeperator);
						}
					}
			}
			addCompView.addView(compList);
			
			DialogBuilder addCompBuilder = new DialogBuilder(getActivity());
				addCompBuilder.setTitle("Add Competition")
				.setCustomView(addCompView, true)
				.setMessage("Select the competitions you would like to add:")
				.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,	int which) {
						List<Integer> selected = new ArrayList<Integer>(); 
						for (int x=0; x<compList.getChildCount(); x++) {
							View child = compList.getChildAt(x);
							if (child instanceof CheckBox && ((CheckBox) child).isChecked() && child.isEnabled()) {
								selected.add(child.getId()-1);
							}
						}
						new AddCompetitions(getActivity(), teamIndex, selected).execute();
						dialog.cancel();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,	int which) {
						dialog.cancel();
					}
				});
			addCompDialog = addCompBuilder.create();
			return addCompDialog;
	}
	
	@SuppressWarnings("deprecation")
	public void onStart () {
		super.onStart();
		Point size = new Point();
			getDialog().getWindow().getWindowManager().getDefaultDisplay().getSize(size);
		LayoutParams lP = getDialog().findViewById(10000).getLayoutParams();
			lP.height = (int) (size.y * .6);
		getDialog().findViewById(10000).setLayoutParams(lP);
		System.out.println("Calculated ScrollView Height: " + getDialog()
				.findViewById(10000)
				.getLayoutParams()
				.height);
		Button positiveB = addCompDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			if(positiveB != null) {
				positiveB.setBackgroundDrawable(
						getResources().getDrawable(R.drawable.crimson_background));
			}
		Button negativeB = addCompDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			if(negativeB != null) {
				negativeB.setBackgroundDrawable(
						getResources().getDrawable(R.drawable.crimson_background));
			}
	}
	
	public int PX (int dp) {
		final float scale = this.getResources().getDisplayMetrics().density;
		int px = (int) (dp*scale+0.5f);
		return px;
	}
	
	public boolean isContained (int t, int[] list) {
		for (int x = 0; x<list.length; x++) {
			if (list[x] == t) {
				return true;
			}
		}
		return false;
	}
}