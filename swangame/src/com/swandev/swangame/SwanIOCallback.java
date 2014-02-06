package com.swandev.swangame;

import io.socket.IOCallback;
import io.socket.SocketIO;

public interface SwanIOCallback extends IOCallback {
	
	void setSocketIO(SocketIO socket);

}
