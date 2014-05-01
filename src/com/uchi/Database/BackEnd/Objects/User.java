package com.uchi.Database.BackEnd.Objects;


public class User extends DatabaseData{

	private int userID;
	private int userCode;
	private int userCoins;
	
	public User() {}

	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @return the iMEI
	 */
	public int getUserCode() {
		return userCode;
	}
	
	/**
	 * 
	 * @return the coins the user has
	 */
	public int getUserCoins() {
		return userCoins;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * @param iMEI the iMEI to set
	 */
	public void setUserCode(int userCode) {
		this.userCode = userCode;
	}
	
	/**
	 * 
	 * @param userCoins
	 */
	public void setUserCoins(int userCoins){
		this.userCoins = userCoins;
	}
}
