/*
 * Copyright (C) 2012 Kris Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.steelhawks.hawkscout.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.steelhawks.hawkscout.PitScouting;
import com.steelhawks.hawkscout.R;
import com.steelhawks.hawkscout.dialogs.EditTextFragment;

/**
 * A Spinner view that does not dismiss the dialog displayed when the control is "dropped down"
 * and the user presses it. This allows for the selection of more than one option.
 */
public class MultiSelectSpinner extends NoDefaultSpinner implements DialogInterface.OnClickListener {
    String[] _items = null;
    boolean[] _selection = null;
    String displayedText;
    Context c;
    EditTextFragment frag;
    AlertDialog dialog;
    
    ArrayAdapter<String> _proxyAdapter;
    
    /**
     * Constructor for use when instantiating directly.
     * @param context
     */
    public MultiSelectSpinner(Context context) {
        super(context);
        c = context;
        _proxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        _proxyAdapter.add("");
        super.setAdapter(_proxyAdapter);
    }

    /**
     * Constructor used by the layout inflater.
     * @param context
     * @param attrs
     */
    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        _proxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(_proxyAdapter);
    }
    
    public String getDisplayedText() {
    	return buildSelectedItemString();
    }

    /**
     * {@inheritDoc}
     */
    
    protected void onMultiItemClick(View view) {
    	System.out.println("Firing CLick");
    	CheckedTextView v = (CheckedTextView) view;
    	
    	int which = v.getId();
    	boolean isChecked = v.isChecked();
    	
        if (_selection != null && which < _selection.length) {
        	if(_items[which].equals("Other") && isChecked) {
        		_selection[which] = false;
        		frag = new EditTextFragment().newInstance(this);
        		frag.show(((PitScouting) c).getSupportFragmentManager(), "Other");
        	}
            _selection[which] = isChecked;
            
            boolean isTrue = false;
            for (int i = 0; i<_selection.length; i++) {
            	if (_selection[i]) isTrue = true;
            }
            if (!isTrue) _proxyAdapter = new ArrayAdapter<String>(c, R.layout.spinner_hint_textview);
            else  _proxyAdapter = new ArrayAdapter<String>(c, R.layout.simple_spinner_dropdown_item);
            _proxyAdapter.clear();
            _proxyAdapter.add(buildSelectedItemString());
            super.setAdapter(_proxyAdapter);
            setSelection(0);
        }
        else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }
    
    public void onClick(DialogInterface dialog,	int which) {
    	System.out.println("Called");
    	EditText e = frag.getCustomView();
		String temp[] = new String[_items.length+1];
	    	for (int i = 0; i<_items.length; i++) {
	    		if (i != _items.length-1) {
	    			temp[i] = _items[i];
	    		} else {
	    			temp[i] = e.getText().toString();
	    			temp[i+1] = _items[i];
	    		}
	    	}
		setItems(temp, false);
		boolean boolTemp[] = new boolean[_selection.length+1];
    	for (int i = 0; i<_selection.length; i++) {
			if (i != _selection.length-1) {
				boolTemp[i] = _selection[i];
			} else {
				boolTemp[i] = true;
				boolTemp[i+1] = false;
			}
    	}
		_selection = boolTemp;
		this.dialog.dismiss();
		
        _proxyAdapter.clear();
        _proxyAdapter.add(buildSelectedItemString());
        super.setAdapter(_proxyAdapter);
        setSelection(0);	
        
		dialog.cancel();
		performClick();
	} 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performClick() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setMultiChoiceItems(_items, _selection, this);
        MultiChoiceDialog builder = new MultiChoiceDialog(c);
        builder.setMultiChoiceIcons(_items, _selection, this);
        dialog = builder.show();
        return true;
    }
    
    /**
     * MultiSelectSpinner does not support setting an adapter. This will throw an exception.
     * @param adapter
     */
    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }
    
    /**
     * Sets the options for this spinner.
     * @param items
     */
    public void setItems(String[] items) {
        _items = items;
        _selection = new boolean[_items.length];
        
        Arrays.fill(_selection, false);
    }

    public void setItems(String[] items, boolean fill) {
        _items = items;
        if (fill) {
            _selection = new boolean[_items.length];
            Arrays.fill(_selection, false);
        }
    }
    
    /**
     * Sets the options for this spinner.
     * @param items
     */
    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        _selection = new boolean[_items.length];
        
        Arrays.fill(_selection, false);
    }
    
    /**
     * Sets the selected options based on an array of string.
     * @param selection
     */
    public void setSelection(String[] selection) {
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    _selection[j] = true;
                }
            }
        }
    }
    
    /**
     * Sets the selected options based on a list of string.
     * @param selection
     */
    public void setSelection(List<String> selection) {
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    _selection[j] = true;
                }
            }
        }
        
        _proxyAdapter = new ArrayAdapter<String>(c, R.layout.spinner_textview);
        _proxyAdapter.clear();
        _proxyAdapter.add(buildSelectedItemString());
        super.setAdapter(_proxyAdapter);
        setSelection(0);
    }
    
    /**
     * Sets the selected options based on an array of positions.
     * @param selectedIndicies
     */
    public void setSelection(int[] selectedIndicies) {
        for (int index : selectedIndicies) {
            if (index >= 0 && index < _selection.length) {
                _selection[index] = true;
            }
            else {
                throw new IllegalArgumentException("Index " + index + " is out of bounds.");
            }
        }
        
        _proxyAdapter = new ArrayAdapter<String>(c, R.layout.spinner_textview);
        _proxyAdapter.clear();
        _proxyAdapter.add(buildSelectedItemString());
        super.setAdapter(_proxyAdapter);
        setSelection(0);
    }
    
    /**
     * Returns a list of strings, one for each selected item.
     * @return
     */
    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < _items.length; ++i) {
            if (_selection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }
    
    /**
     * Returns a list of positions, one for each selected item.
     * @return
     */
    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length; ++i) {
            if (_selection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }
    
    
    
    /**
     * Builds the string for display in the spinner.
     * @return comma-separated list of selected items
     */
    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;
        
        for (int i = 0; i < _items.length; ++i) {
            if (_selection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        if (foundOne) return sb.toString();
        else return (String) getPrompt();
    }
    
    public class MultiChoiceDialog extends DialogBuilder {

    	Context context;
    	
		public MultiChoiceDialog(Context context) {
			super(context);
    		this.context = context;
		}
    	
		public void setMultiChoiceIcons(String[] strs, boolean[] selected, MultiSelectSpinner click) {
			ListView view = new ListView(context);
			view.setAdapter(new ListAdapter(context, strs, selected, click));
			setCustomView(view, false);
		}
		
		class ListAdapter extends ArrayAdapter<String> {

			private final Context context;
			private final String[] list;
			private final boolean[] selected;
			private final MultiSelectSpinner parent;
			
			public ListAdapter(Context c, String[] list, boolean[] selected, MultiSelectSpinner parent) {
				super(c, R.layout.competitions_matches_layout, list);
				this.context = c;
				this.list = list;
				this.selected = selected;
				this.parent = parent;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final int p = position;
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				CheckedTextView v = (CheckedTextView) inflater.inflate(R.layout.checkedtextview, parent, false);
					v.setText(list[position]);
					if (v.getText().equals("Other")) {
						Drawable empty = context.getResources().getDrawable(R.drawable.btn_radio_holo_light);
							empty.setAlpha(0);
						v.setCheckMarkDrawable(empty);
					}
					v.setClickable(true);
					v.setChecked(selected[position]);
					v.setId(position);
					v.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							((CheckedTextView) v).toggle();
							selected[p] = ((CheckedTextView) v).isChecked();
							ListAdapter.this.parent.onMultiItemClick(v);
							if (((CheckedTextView) v).getText().equals("Other")) ((CheckedTextView) v).setChecked(false);
						}
						
					});
				return v;
			}
		}
    }
}
