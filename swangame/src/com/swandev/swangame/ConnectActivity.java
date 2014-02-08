package com.swandev.swangame;

import java.net.MalformedURLException;

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
public class ConnectActivity extends SwanRoboActivity {

	@InjectView(R.id.connectButton)
	Button connectButton;

	@InjectView(R.id.ipAddress)
	EditText ipAddressField;
	
	@InjectView(R.id.nickname)
	EditText nicknameField;

	@Inject
	SocketIOState socketIO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		connectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO: validate nickname not blank
				connectButton.setEnabled(false);
				final String serverAddress = ipAddressField.getText().toString();
				final String nickname = nicknameField.getText().toString();
				try {
					socketIO.connect(serverAddress, nickname);
				} catch (MalformedURLException e) {
					SwanUtils.toastOnUI(ConnectActivity.this, "Malformed server address " + serverAddress, Toast.LENGTH_LONG);
					connectButton.setEnabled(true);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!socketIO.isConnected()) {
			connectButton.setEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
