package com.uchi.Database.BackEnd.Objects;


public class Category extends DatabaseData{
	private int categoryID;
	private String categoryName;
	
	public Category() {
		
	}
	
	public void setCategoryID(int value) {
		this.categoryID = value;
	}
	
	public void setCategoryName(String value) {
		this.categoryName = value;
	}
	
	public int getCategoryID() {
		return categoryID;
	}
	
	public String getCategoryName() {
		return categoryName;
	}
}
