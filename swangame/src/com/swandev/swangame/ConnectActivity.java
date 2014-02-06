package com.swandev.swangame;

import java.net.MalformedURLException;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;

@ContentView(R.layout.connect_screen)
public class ConnectActivity extends RoboActivity {

	@InjectView(R.id.connectButton)
	Button connectButton;

	@InjectView(R.id.ipAddress)
	EditText ipAddressField;

	@Inject
	SocketIOState socketIO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		connectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				connectButton.setEnabled(false);
				final String serverAddress = ipAddressField.getText().toString();
				try {
					socketIO.connect(serverAddress);
				} catch (MalformedURLException e) {
					SwanUtils.toastOnUI(ConnectActivity.this, "Malformed server address " + serverAddress, Toast.LENGTH_LONG);
					connectButton.setEnabled(true);
				}
			}
		});
	}
	
	public void reenableConnectButton() {
		connectButton.setEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
