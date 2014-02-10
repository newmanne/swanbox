package com.swandev.swangame;

public class SocketIOEvents {

	public static final String NICKNAME_SET = "nickname_set";
	
	public static final String ELECTED_CLIENT = "elected_client";
	public static final String ELECTED_HOST = "elected_host";
	
	public static final String ANNOUNCEMENT = "announcement";
	
	//General Game Events
	public static final String GAME_STARTED = "game_started";
	public static final String GAME_OVER = "game_over";
	
	//Patterns Events
	public static final String START_PATTERNS = "start_patterns";
	public static final String PLAYING_PATTERNS = "playing_patterns";
	public static final String PATTERN_REQUESTED = "pattern_requested";
	public static final String PATTERN_ENTERED = "pattern_entered";
	
	//Chatroom Events
	public static final String START_CHATROOM = "start_chatroom";
	public static final String PLAYING_CHATROOM = "playing_chatroom";
	public static final String USER_MESSAGE = "user_message";
	public static final String MESSAGE_TO_ROOM = "msg_to_room";

}
