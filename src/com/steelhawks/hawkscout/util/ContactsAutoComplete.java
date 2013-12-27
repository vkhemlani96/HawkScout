package com.steelhawks.hawkscout.util;

import com.steelhawks.hawkscout.R;
import com.steelhawks.hawkscout.R.drawable;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts.People;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactsAutoComplete extends AutoCompleteTextView {
	 
    public ContactsAutoComplete(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        this.setThreshold(0);
        this.setUpContacts();
        this.setBackgroundResource(R.drawable.edit_text_holo_light);
    }
 
    public ContactsAutoComplete(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.setThreshold(0);
        this.setUpContacts();
        this.setBackgroundResource(R.drawable.edit_text_holo_light);
    }
 
    public ContactsAutoComplete(final Context context) {
        super(context);
        this.setThreshold(0);
        this.setUpContacts();
        this.setBackgroundResource(R.drawable.edit_text_holo_light);
    }
 
    // --- comma separating stuff
 
    private String previous = ""; //$NON-NLS-1$
    private String seperator = ", "; //$NON-NLS-1$
 
    /**
     * This method filters out the existing text till the separator and launched
     * the filtering process again
     */
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = text.toString().trim();
        previous = filterText.substring(0,
                filterText.lastIndexOf(getSeperator()) + 1);
        filterText = filterText.substring(filterText.lastIndexOf(getSeperator()) + 1);
        if (!TextUtils.isEmpty(filterText)) {
            super.performFiltering(filterText, keyCode);
        }
    }
 
    /**
     * After a selection, capture the new value and append to the existing text
     */
    @Override
    protected void replaceText(final CharSequence text) {
    	if (previous.length() == 0) {
            super.replaceText(previous + text + getSeperator());
    	} else {
            super.replaceText(previous + System.getProperty("line.separator") + text + getSeperator());
    	}
    }
 
    public String getSeperator() {
        return seperator;
    }
 
    public void setSeperator(final String seperator) {
        this.seperator = seperator;
    }
 
    // --- contacts stuff
 
    private void setUpContacts() {
        ContactListAdapter adapter = new ContactListAdapter(getContext(), null);
        setAdapter(adapter);
    }
 
    @SuppressWarnings("nls")
    public static class ContactListAdapter extends CursorAdapter implements Filterable {
        public ContactListAdapter(Context context, Cursor c) {
            super(context, c);
            mContent = context.getContentResolver();
        }
 
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final TextView view = (TextView) inflater.inflate(
                    android.R.layout.simple_list_item_1, parent, false);
            view.setText(convertToString(getCursor()));
        	return view;
        }
 
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
        	((TextView) view).setText(convertToString(cursor));
        }
 
        @Override
        public String convertToString(Cursor cursor) {
        	if (cursor.getString(1) == null) {
        		return cursor.getString(2);
        	} else {
        		return cursor.getString(1) + " <" + cursor.getString(2) +">";
        	}
//        	String str = cursor.getString(1) + " <" + cursor.getString(2) +">";
//            return str;
        }
        
        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }
 
            StringBuilder buffer = null;
            String[] args = null;
            if (constraint != null) {
                constraint = constraint.toString().trim();
                buffer = new StringBuilder();
                buffer.append("UPPER(").append(People.NAME).append(") GLOB ?");
                buffer.append(" OR ");
                buffer.append("UPPER(").append(ContactMethods.DATA).append(") GLOB ?");
                args = new String[] { constraint.toString().toUpperCase() + "*",
                        constraint.toString().toUpperCase() + "*" };
            }
 
            return mContent.query(ContactMethods.CONTENT_EMAIL_URI,
                    PEOPLE_PROJECTION, buffer == null ? null : buffer.toString(), args,
                    People.DEFAULT_SORT_ORDER);
        }
 
        private final ContentResolver mContent;
    }
 
    private static final String[] PEOPLE_PROJECTION = new String[] {
        People._ID,
        People.NAME,
        ContactMethods.DATA
    };
}
