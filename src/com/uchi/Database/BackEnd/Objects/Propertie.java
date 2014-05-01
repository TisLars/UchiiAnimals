package com.uchi.Database.BackEnd.Objects;


public class Propertie extends DatabaseData{
	private int properID;
	private String properDescription;
	
	public Propertie() {
		
	}
	
	public void setProperID(int value) {
		properID = value;
	}
	
	public void setProperDescription(String value) {
		properDescription = value;
	}
	
	public int getProperID() {
		return properID;
	}
	
	public String getProperDescription() {
		return properDescription;
	}
}
