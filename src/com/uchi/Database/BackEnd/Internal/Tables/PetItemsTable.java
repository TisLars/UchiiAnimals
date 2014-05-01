package com.uchi.Database.BackEnd.Internal.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * This is class to define the Table_Name and his Columns for the local Database and the table is created or upgraded here. The use of a Abstract or 
 * interface isn't possible with static methods and final String types. Therefore I've chosen not inherit from a bas Table class in this situation
 */
public class PetItemsTable {
	public static final String TABLE_PETITEMS= "PetItems";
	
	public static final String COLUMN_PETITEMID = "petItemID";
	public static final String COLUMN_PETID = "petID";
	public static final String COLUMN_ITEMID = "itemID";
	
	private static final String CREATE_TABLE_PETITEMS = 
			"create table "+ TABLE_PETITEMS +" (" +
					COLUMN_PETITEMID + " integer primary key autoincrement, " +
					COLUMN_PETID + " integer not null, " +
					COLUMN_ITEMID + " integer not null);";

	  public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(CREATE_TABLE_PETITEMS);
		  }

		  public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE_PETITEMS);
		    onCreate(database);
		  }
}
