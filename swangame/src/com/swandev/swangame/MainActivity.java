package com.swandev.swangame;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	@Setter
	@Getter
	boolean isReady = false;
	@Getter
	@Setter
	long timeToPress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// TODO: wait for server before doing any of this

		final TextView timerText = (TextView) findViewById(R.id.countdownText);
		final Button stopButton = (Button) findViewById(R.id.stopbutton);
		final CountDownTimer timer = new CountDownTimer(5000, 1000) {

			public void onTick(long millisUntilFinished) {
				timerText.setText(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + "s remaining");
				if (millisUntilFinished <= 1999) {
					timerText.setText("????");
				}
				stopButton.setEnabled(true);
			}

			public void onFinish() {
				timerText.setText("PUSH THE BUTTON!");
				setReady(true);
				setTimeToPress(System.currentTimeMillis());
			}
		}.start();
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isReady()) {
					setTimeToPress(System.currentTimeMillis() - getTimeToPress());
					Toast.makeText(getApplicationContext(), "Reaction time: " + getTimeToPress() + "ms", Toast.LENGTH_LONG).show();
				} else {
					timer.cancel();
					Toast.makeText(getApplicationContext(), "You clicked too early :(", Toast.LENGTH_LONG).show();
				}
				stopButton.setEnabled(false);
				// TODO: send result to server
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
