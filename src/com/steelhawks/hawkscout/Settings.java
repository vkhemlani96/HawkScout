package com.steelhawks.hawkscout;

import android.app.Activity;
import android.os.Bundle;

public class Settings extends Activity {
	
	//Download Media Automatically
	//Max Media Cache

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
	}
	
}
