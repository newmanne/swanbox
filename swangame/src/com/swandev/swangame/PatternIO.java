package com.swandev.swangame;

import io.socket.IOAcknowledge;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.json.JSONObject;

import roboguice.inject.ContextSingleton;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.inject.Inject;

// probably want an extendable class...
@ContextSingleton
public class PatternIO implements SwanIOCallback {
	
	private static final String USER_RESPONSE = "user_response";
	@Inject
	Activity activity;
	SocketIO socket;
	
	public final String NEW_PATTERN = "new_pattern";

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		if (event.equals(NEW_PATTERN)) {
			if (activity instanceof PatternActivity) {
				((PatternActivity) activity).handlePattern((List<String>) args[0]);
			}
		}
	}

	@Override
	public void onDisconnect() {
		Log.d(LogTags.SOCKIT_IO, "disconnected");
		reallowConnection();
	}

	@Override
	public void onConnect() {
		Log.d(LogTags.SOCKIT_IO, "connected");
		final Intent myIntent = new Intent(activity, PatternActivity.class);
		activity.startActivityForResult(myIntent, 0);
	}

	@Override
	public void onError(final SocketIOException socketIOException) {
		Log.e(LogTags.SOCKIT_IO, "ERROR");
		SwanUtils.toastOnUI(activity,  "Connect error " + socketIOException, Toast.LENGTH_LONG);
		reallowConnection();
	}

	private void reallowConnection() {
		if (activity instanceof ConnectActivity) {
			((ConnectActivity) activity).reenableConnectButton();
		}
	}

	@Override
	public void onMessage(String arg0, IOAcknowledge arg1) {
	
	}

	@Override
	public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
	}

	@Override
	public void setSocketIO(SocketIO socket) {
		this.socket = socket;
	}

	public void sendPatternSuccess(String patternString) {
		socket.emit(USER_RESPONSE, new PatternResponse(patternString, true));
	}
	
	@RequiredArgsConstructor(suppressConstructorProperties=true)
	public static class PatternResponse {
		final String nextPattern;
		final boolean success;
	}

	public void sendPatternFail() {
		socket.emit(USER_RESPONSE, new PatternResponse("", false));
		
	}

}