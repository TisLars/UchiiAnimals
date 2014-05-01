package com.uchi.Database.BackEnd.Internal.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * This is class to define the Table_Name and his Columns for the local Database and the table is created or upgraded here. The use of a Abstract or 
 * interface isn't possible with static methods and final String types. Therefore I've chosen not inherit from a bas Table class in this situation
 */
public class ItemPropertieTable {
	public static final String TABLE_ITEMPROPERTIES = "ItemProperties";
	
	public static final String COLUMN_ITEMPROPERTIESID = "ItemPropertiesID";
	public static final String COLUMN_ITEMID = "itemID";
	public static final String COLUMN_PROPERID = "properID";
	
	private static final String CREATE_TABLE_ITEMPROPERTIES = 
			"create table "+ TABLE_ITEMPROPERTIES +" (" +
					COLUMN_ITEMPROPERTIESID + " integer primary key autoincrement, " +
					COLUMN_ITEMID + " integer not null, " +
					COLUMN_PROPERID + " integer not null);";

	  public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(CREATE_TABLE_ITEMPROPERTIES);
		  }

		  public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMPROPERTIES);
		    onCreate(database);
		  }
}
