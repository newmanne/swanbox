package com.swandev.swangame;

import lombok.Getter;
import lombok.Setter;
import android.content.Intent;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.koushikdutta.async.http.socketio.DisconnectCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;

@Singleton
public class SocketIOState {

	@Getter
	@Setter
	SocketIOClient client;

	@Inject
	ActivityProvider activityProvider;

	public void init() {
		// Return to the connect screen on a disconnect
		getClient().setDisconnectCallback(new DisconnectCallback() {
			
			@Override
			public void onDisconnect(Exception ex) {
				Log.d(LogTags.SOCKIT_IO, "Disconnected");
				final Intent intent = new Intent(activityProvider.getActivity(), ConnectActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				activityProvider.getActivity().startActivity(intent);
			}
		});
	}
	
	public boolean isConnected() {
		return getClient() != null && getClient().isConnected();
	}
}
