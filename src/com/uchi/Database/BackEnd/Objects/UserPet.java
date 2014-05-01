package com.uchi.Database.BackEnd.Objects;


public class UserPet extends DatabaseData {

	private int userPetID;
	private int userID;
	private int petID;
	private int active;
	
	public UserPet() {}

	/**
	 * @return the userPetID
	 */
	public int getUserPetID() {
		return userPetID;
	}

	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @return the petID
	 */
	public int getPetID() {
		return petID;
	}

	/**
	 * @return the active
	 */
	public int getActive() {
		return active;
	}

	/**
	 * @param userPetID the userPetID to set
	 */
	public void setUserPetID(int userPetID) {
		this.userPetID = userPetID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * @param petID the petID to set
	 */
	public void setPetID(int petID) {
		this.petID = petID;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(int active) {
		this.active = active;
	}
}
