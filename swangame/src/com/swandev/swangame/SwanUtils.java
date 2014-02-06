package com.swandev.swangame;

import android.app.Activity;
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

}
