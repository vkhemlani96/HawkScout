package com.steelhawks.hawkscout.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steelhawks.hawkscout.R;

public class DialogBuilder extends AlertDialog.Builder {

	protected Context mContext;
	private View mDialogView;
	private TextView mTitle;
	private ImageView mIcon;
	private TextView mMessage;
	private View mDivider;
	private String origText;
//	private CharSequence negButtonText = null;
//	private OnClickListener negButtonListener = null;
//	private CharSequence neutButtonText = null;
//	private OnClickListener neutButtonListener = null;
//	private CharSequence posButtonText = null;
//	private OnClickListener posButtonListener = null;

	public DialogBuilder(Context context) {
		super(context);
		mContext = context;
		mDialogView = View.inflate(context, R.layout.dialog_custom_layout, null);
		setView(mDialogView);
		
		 mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
	     mMessage = (TextView) mDialogView.findViewById(R.id.message);
	     mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
	     mDivider = mDialogView.findViewById(R.id.titleDivider);
	     
	     mTitle.setTextColor(Color.parseColor("#cc0000"));
	     mTitle.setTypeface(null, Typeface.BOLD);
	     mDivider.setBackgroundColor(Color.parseColor("#cc0000"));
	     
	     origText = String.valueOf(mMessage.getText());
	}
	
//	public DialogBuilder setPositiveButton(CharSequence c, OnClickListener o) {
//		mDialogView.findViewById(android.R.id.button3).setVisibility(View.VISIBLE);
//		posButtonText = c;
//		posButtonListener = o;
//		return this;
//	}
//	
//	public DialogBuilder setNegativeButton(CharSequence c, OnClickListener o) {
//		mDialogView.findViewById(android.R.id.button1).setVisibility(View.VISIBLE);
//		negButtonText = c;
//		negButtonListener = o;
//		return this;
//	}
//	
//	public DialogBuilder setNeutralButton(CharSequence c, OnClickListener o) {
//		mDialogView.findViewById(android.R.id.button2).setVisibility(View.VISIBLE);
//		neutButtonText = c;
//		neutButtonListener = o;
//		return this;
//	}
	
	public DialogBuilder setDividerColor(String colorString) {
    	mDivider.setBackgroundColor(Color.parseColor(colorString));
    	return this;
    }
 
    @Override
    public DialogBuilder setTitle(CharSequence text) {
        mTitle.setText(text);
        return this;
    }

    public DialogBuilder setTitleColor(String colorString) {
    	mTitle.setTextColor(Color.parseColor(colorString));
    	return this;
    }

    @Override
    public DialogBuilder setMessage(int textResId) {
        mMessage.setText(textResId);
        return this;
    }

    @Override
    public DialogBuilder setMessage(CharSequence text) {
        mMessage.setText(text);
        return this;
    }

    @Override
    public DialogBuilder setIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    @Override
    public DialogBuilder setIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public DialogBuilder setCustomView(int resId, Context context, boolean seperator) {
    	if(seperator) {
			View greySeperator = new View(context);
				greySeperator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
				greySeperator.setBackgroundColor(Color.parseColor("#848484"));
				((FrameLayout)mDialogView.findViewById(R.id.customPanel)).addView(greySeperator);
    	}
    	
    	View customView = View.inflate(context, resId, null);
    	((FrameLayout)mDialogView.findViewById(R.id.customPanel)).addView(customView);
    	return this;
    }
    
    public DialogBuilder setCustomView(View view, boolean seperator) {
    	if(seperator) {
			View greySeperator = new View(mContext);
				greySeperator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
				greySeperator.setBackgroundColor(Color.parseColor("#848484"));
				((FrameLayout)mDialogView.findViewById(R.id.customPanel)).addView(greySeperator);
    	}
    	
    	((FrameLayout)mDialogView.findViewById(R.id.customPanel)).addView(view);
    	return this;
    }

    @Override
    public AlertDialog create() {
    	if (mTitle.getText().equals("") || mTitle == null) {
    		mDialogView.findViewById(R.id.title_template).setVisibility(View.GONE);
    		mDivider.setVisibility(View.GONE);
    	}
    	if (String.valueOf(mMessage.getText()).equals(origText)) mMessage.setVisibility(View.GONE);
    	
    	return super.create();
    }    
}
