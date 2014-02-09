package com.swandev.swangame;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;

import lombok.Getter;
import lombok.Setter;

import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SocketIOState {

	@Getter
	SocketIO client;

	@Inject
	ActivityProvider activityProvider;

	@Inject
	EventEmitter eventEmitter;

	@Getter
	@Setter
	private String nickname;

	public void on(String eventName, EventCallback callback) {
		eventEmitter.on(eventName, callback);
	}

	@Getter
	@Setter
	private boolean host = false;

	public boolean isConnected() {
		return getClient() != null && getClient().isConnected();
	}

	public void connect(final String serverAddress, final String nickname)
			throws MalformedURLException {
		final SocketIO socketIO = new SocketIO(serverAddress);
		this.client = socketIO;
		client.connect(new IOCallback() {

			@Override
			public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMessage(String arg0, IOAcknowledge arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(SocketIOException ex) {
				Log.e(LogTags.SOCKET_IO, "Connection error", ex);
			}

			@Override
			public void onDisconnect() {
				Log.d(LogTags.SOCKET_IO, "Disconnected");
				final Intent intent = new Intent(
						activityProvider.getActivity(), ConnectActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						);
				eventEmitter.clear();
				activityProvider.getActivity().startActivity(intent);
			}

			@Override
			public void onConnect() {
				Log.d(LogTags.SOCKET_IO, "Connected to " + serverAddress);
				client.emit(SocketIOEvents.NICKNAME_SET, nickname);
				SocketIOState.this.setNickname(nickname);
			}

			@Override
			public void on(String event, IOAcknowledge ack, Object... arguments) {
				eventEmitter.onEvent(event, ack, arguments);
			}
		});

	}
}
