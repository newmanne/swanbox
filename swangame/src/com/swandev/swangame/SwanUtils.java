package com.swandev.swangame;

import java.util.List;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SwanUtils {
	
	public static void toastOnUI(final Activity activity, final String toastText, final int duration) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(activity, toastText, duration).show();
			}
		});
	}
	
	public static void updateListViewWithList(final Activity activity, final ListView view, final List<String> strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, android.R.id.text1, strings);
		view.setAdapter(adapter);
	}

}
