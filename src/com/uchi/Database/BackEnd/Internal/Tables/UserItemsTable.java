package com.uchi.Database.BackEnd.Internal.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * This is class to define the Table_Name and his Columns for the local Database and the table is created or upgraded here. The use of a Abstract or 
 * interface isn't possible with static methods and final String types. Therefore I've chosen not inherit from a bas Table class in this situation
 */
public class UserItemsTable {
	public static final String TABLE_USERITEMS = "UserItems";
	
	public static final String COLUMN_USERITEMID = "userItemID";
	public static final String COLUMN_USERID = "userID";
	public static final String COLUMN_ITEMID = "itemID";
	public static final String COLUMN_EQUIPPED = "equipped";
	
	private static final String CREATE_TABLE_USERITEMS = 
			"create table "+ TABLE_USERITEMS +" (" +
					COLUMN_USERITEMID + " integer primary key autoincrement, " +
					COLUMN_USERID + " integer not null, " +
					COLUMN_ITEMID + " integer not null, " +
					COLUMN_EQUIPPED + " integer not null default 0);";

	  public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(CREATE_TABLE_USERITEMS);
		  }

		  public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERITEMS);
		    onCreate(database);
		  }
}
