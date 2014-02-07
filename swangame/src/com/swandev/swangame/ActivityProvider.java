package com.swandev.swangame;

import lombok.Getter;
import lombok.Setter;
import android.app.Activity;

import com.google.inject.Singleton;

@Singleton
public class ActivityProvider {
	
	@Getter
	@Setter
	Activity activity;

}
