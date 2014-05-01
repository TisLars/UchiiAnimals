package com.uchi.Database.BackEnd.Objects;


public class PetItem extends DatabaseData{

	private int petItemID;
	private int petID;
	private int itemID;
	
	public PetItem() {}

	/**
	 * @return the petItemID
	 */
	public int getPetItemID() {
		return petItemID;
	}

	/**
	 * @return the petID
	 */
	public int getPetID() {
		return petID;
	}

	/**
	 * @return the itemID
	 */
	public int getItemID() {
		return itemID;
	}

	/**
	 * @param petItemID the petItemID to set
	 */
	public void setPetItemID(int petItemID) {
		this.petItemID = petItemID;
	}

	/**
	 * @param petID the petID to set
	 */
	public void setPetID(int petID) {
		this.petID = petID;
	}

	/**
	 * @param itemID the itemID to set
	 */
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
}
