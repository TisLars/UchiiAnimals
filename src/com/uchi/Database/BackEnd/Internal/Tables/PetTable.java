package com.uchi.Database.BackEnd.Internal.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * This is class to define the Table_Name and his Columns for the local Database and the table is created or upgraded here. The use of a Abstract or 
 * interface isn't possible with static methods and final String types. Therefore I've chosen not inherit from a bas Table class in this situation
 */
public class PetTable {
	public static final String TABLE_PET = "Pet";
	
	public static final String COLUMN_PETID = "petID";
	public static final String COLUMN_KINDID = "kindID";
	public static final String COLUMN_PETDESCRIPTION= "petDescription";
	public static final String COLUMN_PETPRICE = "petPrice";
	public static final String COLUMN_PETIMAGE = "petImage";
	public static final String COLUMN_PETAVAILABLE= "petAvailable";
	
	private static final String CREATE_TABLE_PET = 
			"create table " + TABLE_PET + " (" +
			COLUMN_PETID + " integer primary key autoincrement, " +
			COLUMN_KINDID + " integer not null, " + 
			COLUMN_PETDESCRIPTION + " Text not null, " + 
			COLUMN_PETPRICE + " INTEGER not null, " +
			COLUMN_PETIMAGE + " TEXT not null, " +
			COLUMN_PETAVAILABLE + " INTEGER not null);";

	
	  public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(CREATE_TABLE_PET);
		  }

		  public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE_PET);
		    onCreate(database);
		  }
}
