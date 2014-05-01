package com.uchi.Database.BackEnd.Objects;


public class ItemPropertie extends DatabaseData{
	private int itemPropertiesID;
	private int itemID;
	private int properID;
	
	public ItemPropertie() {
		
	}
	
	public void setItemPropertiesID(int value) {
		this.itemPropertiesID = value;
	}
	
	public void setItemID(int value) {
		this.itemID = value;
	}
	
	public void setproperID(int value) {
		this.properID = value;
	}
	
	public int getItemPropertiesID() {
		return itemPropertiesID;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getProperID(){
		return properID;
	}
}
