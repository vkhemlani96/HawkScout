package com.steelhawks.hawkscout.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

import com.steelhawks.hawkscout.R;
import com.steelhawks.hawkscout.util.DialogBuilder;
import com.steelhawks.hawkscout.util.Utilities;

public class EditTextFragment extends DialogFragment {

	DialogInterface.OnClickListener listener;
	EditText e;
	
	public EditTextFragment() {}
	
	public EditTextFragment newInstance (DialogInterface.OnClickListener listener) {
		this.listener = listener;
		return this;
	}
	
	public void onStart() {
		super.onStart();
		e.performClick();
	}
	
	public void onCancel() {
		Utilities.closeKeyboard(getActivity());
		super.onCancel(getDialog());
	}
	
	public EditText getCustomView() {
		return e;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
			DialogBuilder builder = new DialogBuilder(getActivity());
				e = new EditText(getActivity());
					LayoutParams l = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					e.setLayoutParams(l);
					e.setPadding(getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin),
							getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin),
							getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin),
							getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));
				builder.setCustomView(e, false)
				.setPositiveButton("OK",listener)
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,	int which) {
							dialog.cancel();
						}
					});
			return builder.create();
	}
}