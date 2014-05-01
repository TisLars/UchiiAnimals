package com.uchi.Database.BackEnd.Objects;


public class Pet extends DatabaseData{

	private Integer petID;
	private int kindID;
	private String petDescription;
	private Integer petPrice;
	private String petImage;
	private int petAvailable;
	private boolean active;
	
	private String kindName;
	private boolean petBought;
	
	public Integer getPetID() {
		return petID;
	}
	
	public int getKindID() {
		return kindID;
	}
	
	public String getPetDescription() {
		return petDescription;
	}
	
	public Integer getPetPrice() {
		return petPrice;
	}
	
	public String getPetImage() {
		return petImage;
	}
	
	public int getPetAvailable() {
		return petAvailable;
	}
	
	public String getKindName() {
		return kindName;
	}
	
	public boolean getPetBought() {
		return petBought;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setPetID(Integer petID) {
		this.petID = petID;
	}
	
	public void setKindID(int kindID) {
		this.kindID = kindID;
	}
	
	public void setPetDescription(String petDescription) {
		this.petDescription = petDescription;
	}
	
	public void setPetPrice(int petPrice) {
		this.petPrice = petPrice;
	}
	
	public void setPetImage(String petImage) {
		this.petImage = petImage;
	}
	
	public void setPetAvailable(int petAvailable) {
		this.petAvailable = petAvailable;
	}
	
	public void setKindName(String kindName) {
		this.kindName = kindName;
	}
	
	public void setPetBought(boolean petBought) {
		this.petBought = petBought;
	}
	
	public void setActive(int active) {
		this.active = (active == 1) ? true : false;
	}
}
