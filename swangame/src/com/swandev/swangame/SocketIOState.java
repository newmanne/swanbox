package com.swandev.swangame;

import io.socket.SocketIO;

import java.net.MalformedURLException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SocketIOState {
	
	@Inject
	SwanIOCallback ioCallback;
	
	public void connect(String serverAddress) throws MalformedURLException {
		final SocketIO socket = new SocketIO(serverAddress);
		socket.connect(ioCallback);
		ioCallback.setSocketIO(socket);
	}
	
}
