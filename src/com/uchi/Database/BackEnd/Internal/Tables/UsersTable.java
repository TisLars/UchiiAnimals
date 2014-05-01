package com.uchi.Database.BackEnd.Internal.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * This is class to define the Table_Name and his Columns for the local Database and the table is created or upgraded here. The use of a Abstract or 
 * interface isn't possible with static methods and final String types. Therefore I've chosen not inherit from a bas Table class in this situation
 */
public class UsersTable {
	public static final String TABLE_USERS = "Users";
	
	public static final String COLUMN_USERID = "userID";
	public static final String COLUMN_USERCODE = "userCode";
	public static final String COLUMN_USERCOINS = "userCoins";
	
	private static final String CREATE_TABLE_USERS = 
			"create table "+ TABLE_USERS +" (" +
					COLUMN_USERID + " integer primary key autoincrement, " +
					COLUMN_USERCODE + " integer not null, "+
					COLUMN_USERCOINS + " integer not null);";

	  public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(CREATE_TABLE_USERS);
		  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
	    database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
	    onCreate(database);
	  }
}
