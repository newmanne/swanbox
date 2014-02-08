package com.swandev.swangame;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;

@ContentView(R.layout.connect_screen)
public class ConnectActivity extends SwanRoboActivity {

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
				SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), serverAddress, new ConnectCallback() {
					@Override
					public void onConnectCompleted(Exception ex, SocketIOClient client) {
						if (ex != null) {
							Log.e(LogTags.SOCKET_IO, "Connection error", ex);
							SwanUtils.toastOnUI(ConnectActivity.this, ex.toString(), Toast.LENGTH_LONG);
							connectButton.setEnabled(true);
							return;
						}
						Log.d(LogTags.SOCKET_IO, "Connected to " + serverAddress);
						socketIO.setClient(client);
						socketIO.init();
						startActivityForResult(new Intent(ConnectActivity.this, PatternActivity.class), 0);
					}
				});
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
