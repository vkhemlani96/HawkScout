package com.steelhawks.hawkscout;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.steelhawks.hawkscout.data.Competition;

public class Competition2 extends Activity {
	
	List<Competition> competitions = new ArrayList<Competition>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_competition_test);
		String[] competitionList = getExternalFilesDir(null).list();
		((TextView) findViewById(R.id.comp_test_text)).setText("Found " + competitionList.length + " competitions.");
		for (int i=0; i<competitionList.length; i++) competitions.add(new Competition(this, competitionList[i]));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.competition_test, menu);
		return true;
	}

}
