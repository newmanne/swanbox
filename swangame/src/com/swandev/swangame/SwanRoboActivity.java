package com.swandev.swangame;

import roboguice.activity.RoboActivity;
import android.os.Bundle;

import com.google.inject.Inject;

public class SwanRoboActivity extends RoboActivity {
	
	@Inject
	ActivityProvider activityProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityProvider.setActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		activityProvider.setActivity(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		activityProvider.setActivity(null);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityProvider.setActivity(null);
	}
	
}
