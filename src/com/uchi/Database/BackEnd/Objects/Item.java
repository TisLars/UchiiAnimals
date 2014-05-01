package com.uchi.Database.BackEnd.Objects;

import java.util.ArrayList;


public class Item extends DatabaseData{
	private Integer itemID;
	private int categoryID;
	private String categoryName;
	private String itemName;
	private String itemDescription;
	private Double itemPrice;
	private String itemImage;
	private int itemAvailable;
	private boolean equipped;
	
	private boolean itemBought;
	
	private ArrayList<Propertie> properties = new ArrayList<Propertie>();
	
	public Item() {}

	public void addPropertie(Propertie propertie){
		properties.add(propertie);
	}
	
	
	public Integer getItemID() {
		return itemID;
	}

	public int getCategoryID() {
		return categoryID;
	}
	
	public String getCategoryName(){
		return categoryName;
	}

	public String getItemName() {
		return itemName;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public Double getItemPrice() {
			return itemPrice;
	}

	public String getItemImage() {
		return itemImage;
	}

	public int getItemAvailable() {
		return itemAvailable;
	}

	public ArrayList<Propertie> getProperties() {
		return properties;
	}
	
	public boolean getItemBought() {
		return itemBought;
	}
	
	public boolean isEquipped() {
		return equipped;
	}
	
	public void setItemID(Integer itemID) {
		this.itemID = itemID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public void setItemPrice(Double itemPrice) {
		this.itemPrice = itemPrice;
	}

	public void setItemImage(String itemImage) {
		this.itemImage = itemImage;
	}

	public void setItemAvailable(int itemAvailable) {
		this.itemAvailable = itemAvailable;
	}
	
	public void setItemBought(boolean itemBought) {
		this.itemBought = itemBought;
	}
	
	public void setEquipped(int equipped) {
		this.equipped = (equipped == 1) ? true : false;
	}
}
