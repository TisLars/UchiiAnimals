package com.uchi.Database.BackEnd.Internal.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * This is class to define the Table_Name and his Columns for the local Database and the table is created or upgraded here. The use of a Abstract or 
 * interface isn't possible with static methods and final String types. Therefore I've chosen not inherit from a bas Table class in this situation
 */
public class ItemTable {
	public static final String TABLE_ITEMS = "Items";
	
	public static final String COLUMN_ITEMID = "itemID";
	public static final String COLUMN_CATEGORYID = "categoryID";
	public static final String COLUMN_ITEMNAME = "itemName";
	public static final String COLUMN_ITEMDESCRIPTION = "itemDescription";
	public static final String COLUMN_ITEMPRICE = "itemPrice";
	public static final String COLUMN_ITEMIMAGE = "itemImage";
	public static final String COLUMN_ITEMAVAILABLE = "itemAvailable";
	
	private static final String CREATE_TABLE_ITEMS = 
			"create table " + TABLE_ITEMS + " (" +
			COLUMN_ITEMID + " integer primary key autoincrement, " +
			COLUMN_CATEGORYID + " integer not null, " + 
			COLUMN_ITEMNAME + " Text not null, " + 
			COLUMN_ITEMDESCRIPTION + " Text not null, " +
			COLUMN_ITEMPRICE + " REAL not null, " +
			COLUMN_ITEMIMAGE + " TEXT not null, " +
			COLUMN_ITEMAVAILABLE + " INTEGER not null);";

	
	 public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(CREATE_TABLE_ITEMS);
		  }

	 public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
	    database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
	    onCreate(database);
	  }
}
