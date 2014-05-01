package com.uchi.Database.BackEnd.Internal.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * This is class to define the Table_Name and his Columns for the local Database and the table is created or upgraded here. The use of a Abstract or 
 * interface isn't possible with static methods and final String types. Therefore I've chosen not inherit from a bas Table class in this situation
 */
public class UserPetsTable {
	public static final String TABLE_USERPETS = "UserPets";
	
	public static final String COLUMN_USERPETID = "userPetID";
	public static final String COLUMN_USERID = "userID";
	public static final String COLUMN_PETID = "petID";
	public static final String COLUMN_ACTIVE = "active";
	
	private static final String CREATE_TABLE_USERPETS = 
			"create table "+ TABLE_USERPETS +" (" +
					COLUMN_USERPETID + " integer primary key autoincrement, " +
					COLUMN_USERID + " integer not null, " +
					COLUMN_PETID + " integer not null, " +
					COLUMN_ACTIVE + " integer not null default 0);";

	  public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(CREATE_TABLE_USERPETS);
		  }

		  public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERPETS);
		    onCreate(database);
		  }
}
