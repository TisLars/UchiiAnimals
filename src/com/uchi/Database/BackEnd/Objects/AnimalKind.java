package com.uchi.Database.BackEnd.Objects;


public class AnimalKind extends DatabaseData{

	private int kindID;
	private String kindName;
	
	public AnimalKind() {}
	
	/**
	 * @return the kindID
	 */
	public int getKindID() {
		return kindID;
	}
	
	/**
	 * @return the kindName
	 */
	public String getKindName() {
		return kindName;
	}
	
	/**
	 * @param kindID the kindID to set
	 */
	public void setKindID(int kindID) {
		this.kindID = kindID;
	}
	
	/**
	 * @param kindName the kindName to set
	 */
	public void setKindName(String kindName) {
		this.kindName = kindName;
	}
	
	
}
