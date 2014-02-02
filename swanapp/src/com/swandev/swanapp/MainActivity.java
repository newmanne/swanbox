package com.swandev.swanapp;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.collect.Lists;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ListView listView = (ListView) findViewById(R.id.swanapps);

		// Build the intent
		final PackageManager pm = getPackageManager();
		// get a list of installed apps.
		final List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		final List<String> swanApps = Lists.newArrayList();
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.contains("swan")) {
				swanApps.add(packageInfo.packageName);
			}
		}
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, swanApps));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final String appName = (String) listView.getItemAtPosition(position);
				final Intent launch = pm.getLaunchIntentForPackage(appName);
				final List<ResolveInfo> activities = pm.queryIntentActivities(launch, 0);
				final boolean isIntentSafe = activities.size() > 0;
				if (isIntentSafe) {
					startActivity(launch);
				} 
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
