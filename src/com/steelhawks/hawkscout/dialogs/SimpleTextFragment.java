package com.steelhawks.hawkscout.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.steelhawks.hawkscout.util.DialogBuilder;

public class SimpleTextFragment extends DialogFragment {

	DialogInterface.OnClickListener listener;
	String message;
	String buttonText;
	String title;
	boolean showNegative;
	
	public SimpleTextFragment() {}
	
	public SimpleTextFragment newInstance(String title, String message,
				String buttonText,  DialogInterface.OnClickListener click, boolean neg) {
		showNegative = neg;
		this.title = title;
		listener = click;
		this.message = message;
		this.buttonText = buttonText;
		return this;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
			DialogBuilder builder = new DialogBuilder(getActivity());
				if(title != null) builder.setTitle(title);
				builder.setMessage(message)
				.setPositiveButton(buttonText, listener);
				if(showNegative) builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,	int which) {
							dialog.cancel();
						}
					});
			return builder.create();
	}
}