package com.uchi.Database.BackEnd.Objects;


public class UserItem extends DatabaseData{

	private int userItemID;
	private int userID;
	private int itemID;
	private int equipped;
	
	public UserItem() {}

	/**
	 * @return the userItemID
	 */
	public int getUserItemID() {
		return userItemID;
	}

	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @return the itemID
	 */
	public int getItemID() {
		return itemID;
	}

	/**
	 * @return the equipped
	 */
	public int getEquipped() {
		return equipped;
	}

	/**
	 * @param userItemID the userItemID to set
	 */
	public void setUserItemID(int userItemID) {
		this.userItemID = userItemID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * @param itemID the itemID to set
	 */
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	/**
	 * @param equipped the equipped to set
	 */
	public void setEquipped(int equipped) {
		this.equipped = equipped;
	}
	
	
}
