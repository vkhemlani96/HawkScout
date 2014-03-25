package com.steelhawks.hawkscout.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.steelhawks.hawkscout.R;
import com.steelhawks.hawkscout.dialogs.pitscouting.EditTextFragment;

public class NoDefaultSpinner extends Spinner implements DialogInterface.OnClickListener {
	
	FragmentActivity c;
	ArrayAdapter<String> adapter;
	CharSequence prompt;
	CharSequence displayedText;
	List<String> list;
	EditTextFragment frag;
	
	public NoDefaultSpinner(Context context) {
        super(context);
    }
	
	public NoDefaultSpinner(Context context, int mode) {
        super(context, mode);
    }
	
	public NoDefaultSpinner(FragmentActivity context, int mode, List<String> list) {
        super(context, mode);
        c = context;
        this.list = list;
        OnItemSelectedListener listener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				displayedText = ((TextView) arg1).getText();
				if (((TextView) arg1).getText().toString().equals("Other")) {
					frag = new EditTextFragment().newInstance(NoDefaultSpinner.this);
					frag.show(c.getSupportFragmentManager(), "OTHER");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        	
        };
        super.setOnItemSelectedListener(listener);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void onClick(DialogInterface dialog,	int which) {
    	System.out.println("Called");
    	EditText e = frag.getCustomView();
    	list.add(list.size()-1, e.getText().toString());
    	setAdapter(new ArrayAdapter<String>((Context) c, R.layout.simple_spinner_dropdown_item_gray, list));
		dialog.cancel();
		setSelection(list.size()-2);
	}
    
    public CharSequence getPrompt() {
    	return prompt;
    }
    
    public String getDisplayedText() {
    	if (displayedText == null) return null;
    	return displayedText.toString();
    }
    
    public void setPrompt(CharSequence p) {
    	prompt = p;
    	super.setPrompt(null);
    }

    @Override
    public void setAdapter(SpinnerAdapter orig) {
        this.adapter = (ArrayAdapter<String>) orig;
        final SpinnerAdapter adapter = newProxy(orig);

        super.setAdapter(adapter);

        try {
            final Method m = AdapterView.class.getDeclaredMethod(
                               "setNextSelectedPositionInt",int.class);
            m.setAccessible(true);
            m.invoke(this,-1);

            final Method n = AdapterView.class.getDeclaredMethod(
                               "setSelectedPositionInt",int.class);
            n.setAccessible(true);
            n.invoke(this,-1);
        } 
        catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }

    protected SpinnerAdapter newProxy(SpinnerAdapter obj) {
        return (SpinnerAdapter) java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                new Class[]{SpinnerAdapter.class},
                new SpinnerAdapterProxy(obj));
    }



    /**
     * Intercepts getView() to display the prompt if position < 0
     */
    protected class SpinnerAdapterProxy implements InvocationHandler {

        protected SpinnerAdapter obj;
        protected Method getView;


        protected SpinnerAdapterProxy(SpinnerAdapter obj) {
            this.obj = obj;
            try {
                this.getView = SpinnerAdapter.class.getMethod(
                                 "getView",int.class,View.class,ViewGroup.class);
            } 
            catch( Exception e ) {
                throw new RuntimeException(e);
            }
        }

        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            try {
                return m.equals(getView) && 
                       (Integer)(args[0])<0 ? 
                         getView((Integer)args[0],(View)args[1],(ViewGroup)args[2]) : 
                         m.invoke(obj, args);
            } 
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            } 
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected View getView(int position, View convertView, ViewGroup parent) 
          throws IllegalAccessException {
            if( position<0 ) {
                final TextView v = 
                  (TextView) ((LayoutInflater)getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE)).inflate(
                      android.R.layout.simple_spinner_item,parent,false);
                v.setText(prompt);
                displayedText = (String) prompt;
                v.setTextColor(Color.parseColor("#808080"));
                v.setTextSize(18);
                return v;
            }
            TextView t = (TextView) obj.getView(position, convertView, parent);
            t.setBackgroundColor(Color.BLACK);
            System.out.println("Set Stuff");
            t.setTextSize(18);
            return t;
        }
    }
}
