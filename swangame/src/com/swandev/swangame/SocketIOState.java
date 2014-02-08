package com.swandev.swangame;

import lombok.Getter;

import org.json.JSONArray;

import android.content.Intent;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.DisconnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;

@Singleton
public class SocketIOState {

	@Getter
	SocketIOClient client;

	@Inject
	ActivityProvider activityProvider;

	@Getter
	private boolean host = false;

	public void init(SocketIOClient client, String nickname) {
		this.client = client;
		client.on(SocketIOEvents.ELECTED_HOST, new EventCallback() {

			@Override
			public void onEvent(JSONArray args, Acknowledge ack) {
				host = true;
				Log.d(LogTags.SOCKET_IO, "Elected host");
				activityProvider.getActivity().onElectedHost();
			}

		});
		// Emit a nickname event
		client.emit(SocketIOEvents.NICKNAME_SET, new JSONArray().put(nickname));
		// Return to the connect screen on a disconnect
		client.setDisconnectCallback(new DisconnectCallback() {

			@Override
			public void onDisconnect(Exception ex) {
				Log.d(LogTags.SOCKET_IO, "Disconnected");
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
