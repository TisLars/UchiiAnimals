package com.uchi.animations;

public class Animations {
	private static boolean playing;
	private static boolean feeding;
	private static boolean washing;
	
	public static boolean getPlaying() {
		return playing;
	}
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
	public static boolean getFeeding() {
		return feeding;
	}
	public void setFeeding(boolean feeding) {
		this.feeding = feeding;
	}
	public static boolean getWashing() {
		return washing;
	}
	public void setWashing(boolean washing) {
		this.washing = washing;
	}
}
