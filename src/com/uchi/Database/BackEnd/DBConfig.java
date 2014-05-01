package com.uchi.Database.BackEnd;

/** DBConfig Class
 * In this class the Mode and the URL for App will be determed.
 * 
 * As default the Mode has been set to Development. This means that there has to be URL declared. This has to be done using de setURL Method.
 * If the url hasn't been declared, than an Exception will be thrown.
 * 
 * When the Mode has been set to Live. The LIVE_URL will be used during the App. This can be done using the setMode Method
 */

public class DBConfig {

	public enum Mode {DEVELOPMENT, LIVE};
	
	private static final String LIVE_URL = "http://www.axelschelfhout.nl/uchi/?get=%s";
	
	private static Mode mode = Mode.DEVELOPMENT;
	private static String url;
	
	/**
	 * Sets the Mode variable. Default is Mode.LIVE
	 * @param mode
	 */
	public static void setMode(Mode mode) {
		DBConfig.mode = mode;
	}
	
	/**
	 * Sets the Url variable. Only needed when Mode.Development. Otherwise default = LIVE_URL
	 * @param url
	 */
	public static void setURL(String url) {
		DBConfig.url = url;
	}
	
	/**
	 * This method get the value from url. If Mode.LIVE, LIVE_URL will be returned. If Mode.DEVELOPMENT, user defined url will be returned
	 * @return
	 */
	public static String getURL() {
		if (DBConfig.mode == Mode.LIVE)
			return LIVE_URL;
		else
		{
			if (DBConfig.url == "")
				throw new NullPointerException("In Dev Mode. URL has to be set!");
			else
				return DBConfig.url;
		}
	}
}
