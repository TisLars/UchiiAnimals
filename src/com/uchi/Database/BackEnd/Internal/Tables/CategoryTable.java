package com.uchi.Database.BackEnd.Internal.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * This is class to define the Table_Name and his Columns for the local Database and the table is created or upgraded here. The use of a Abstract or 
 * interface isn't possible with static methods and final String types. Therefore I've chosen not inherit from a bas Table class in this situation
 */
public class CategoryTable {
	public static final String TABLE_CATEGORY = "Category";
	
	public static final String COLUMN_CATEGORYID = "categoryID";
	public static final String COLUMN_CATEGORYNAME = "categoryName";
	
	private static final String CREATE_TABLE_CATEGORY = 
			"create table "+ TABLE_CATEGORY +" (" +
			COLUMN_CATEGORYID + " integer primary key autoincrement, " +
			COLUMN_CATEGORYNAME + " TEXT not null);";

	 public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(CREATE_TABLE_CATEGORY);
	 }

	 public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
		    onCreate(database);
	  }
}
