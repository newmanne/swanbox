package com.swandev.swangame;

import lombok.Getter;
import lombok.Setter;

import com.google.inject.Singleton;

@Singleton
public class ActivityProvider {
	
	@Getter
	@Setter
	SwanRoboActivity activity;

}
