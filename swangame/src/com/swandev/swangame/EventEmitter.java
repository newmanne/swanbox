package com.swandev.swangame;

import io.socket.IOAcknowledge;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class EventEmitter {

	ListMultimap<String, EventCallback> callbacks = ArrayListMultimap.create();

	void onEvent(String event, IOAcknowledge ack, Object... arguments) {
		final List<EventCallback> cbs = callbacks.get(event);
		for (EventCallback callback : cbs) {
			callback.onEvent(ack,arguments);
		}
	}

	public void on(String event, EventCallback callback) {
		callbacks.put(event, callback);
	}

}
