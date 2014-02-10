package com.swandev.swangame;

import io.socket.IOAcknowledge;

import java.net.MalformedURLException;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

	@InjectView(R.id.port)
	EditText portField;

	@Inject
	SocketIOState socketIO;

	@InjectView(R.id.patternsButton)
	Button patternsButton;

	@InjectView(R.id.chatroomButton)
	Button chatroomButton;

	@InjectView(R.id.waitingForGame)
	TextView waitingForGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		patternsButton.setVisibility(View.GONE);
		chatroomButton.setVisibility(View.GONE);
		waitingForGame.setVisibility(View.GONE);

		socketIO.on(SocketIOEvents.ELECTED_CLIENT, new EventCallback() {

			@Override
			public void onEvent(IOAcknowledge ack, Object... args) {
				runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
						enableWaitingText();
		            }
		        });
				socketIO.setHost(false);
			}

		});

		socketIO.on(SocketIOEvents.ELECTED_HOST, new EventCallback() {

			@Override
			public void onEvent(IOAcknowledge ack, Object... args) {
				runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
		                enableChatroomButton();
		                enablePatternsButton();
		            }
		        });
				socketIO.setHost(true);
			}

		});

		socketIO.on(SocketIOEvents.PLAYING_PATTERNS, new EventCallback() {

			@Override
			public void onEvent(IOAcknowledge ack, Object... args) {
				activityProvider.getActivity().startActivity(
						new Intent(activityProvider.getActivity(),
								PatternActivity.class));
			}

		});

		patternsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				socketIO.getClient().emit(SocketIOEvents.START_PATTERNS);
			}
		});

		socketIO.on(SocketIOEvents.PLAYING_CHATROOM, new EventCallback() {

			@Override
			public void onEvent(IOAcknowledge ack, Object... args) {
				activityProvider.getActivity().startActivity(
						new Intent(activityProvider.getActivity(),
								ChatActivity.class));
			}

		});

		socketIO.on(SocketIOEvents.ANNOUNCEMENT, new EventCallback() {

			@Override
			public void onEvent(IOAcknowledge ack, Object... args) {
				final String announcement = (String) args[0];
				SwanUtils.toastOnUI(ConnectActivity.this, announcement,
						Toast.LENGTH_LONG);
			}

		});

		chatroomButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				socketIO.getClient().emit(SocketIOEvents.START_CHATROOM);
			}
		});

		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: validate nickname not blank
				connectButton.setEnabled(false);
				final String serverIP = ipAddressField.getText().toString();
				final String nickname = nicknameField.getText().toString();
				final String port = portField.getText().toString();

				String serverAddress = "http://" + serverIP + ":" + port;
				try {
					socketIO.connect(serverAddress, nickname);
				} catch (MalformedURLException e) {
					SwanUtils.toastOnUI(ConnectActivity.this,
							"Malformed server address " + serverAddress,
							Toast.LENGTH_LONG);
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

	public void enablePatternsButton() {
		patternsButton.setVisibility(View.VISIBLE);
		patternsButton.setEnabled(true);
	}

	public void enableChatroomButton() {
		chatroomButton.setVisibility(View.VISIBLE);
		chatroomButton.setEnabled(true);
	}

	public void enableWaitingText() {
		waitingForGame.setVisibility(View.VISIBLE);
	}

}
