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
	
	@InjectView(R.id.patternsButton)
	Button patternsButton;
	
	@InjectView(R.id.chatroomButton)
	Button chatroomButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		patternsButton.setVisibility(View.GONE);
		chatroomButton.setVisibility(View.GONE);
		
		socketIO.on(SocketIOEvents.ELECTED_CLIENT, new EventCallback(){

			@Override
			public void onEvent(IOAcknowledge ack, Object... args) {
				final String gameTitle = (String) args[0];
				if (gameTitle.equals("patterns")){
					enablePatternsButton();
				} else if (gameTitle.equals("chatroom")){
					enableChatroomButton();
				} else {
					//TODO: handle the case where the host crashed
				}
				socketIO.setHost(false);
			}
			
		});
		
		socketIO.on(SocketIOEvents.ELECTED_HOST, new EventCallback(){

			@Override
			public void onEvent(IOAcknowledge ack, Object... args) {
					enablePatternsButton();
					enableChatroomButton();
					socketIO.setHost(true);
			}
			
		});
		
		patternsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				socketIO.getClient().emit(SocketIOEvents.PLAYING_PATTERNS);
				activityProvider.getActivity().startActivity( 
						new Intent(activityProvider.getActivity(), PatternActivity.class));
			}
		});
		
		chatroomButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				socketIO.getClient().emit(SocketIOEvents.PLAYING_CHATROOM);
				activityProvider.getActivity().startActivity( 
						new Intent(activityProvider.getActivity(), ChatActivity.class));
			}
		});
		
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
	
	public void enablePatternsButton(){
		patternsButton.setVisibility(View.VISIBLE);
		patternsButton.setEnabled(true);
	}

	public void enableChatroomButton() {
		chatroomButton.setVisibility(View.VISIBLE);
		chatroomButton.setEnabled(true);		
	}
	
}
