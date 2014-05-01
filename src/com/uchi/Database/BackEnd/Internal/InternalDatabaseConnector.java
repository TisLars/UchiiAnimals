package com.uchi.Database.BackEnd.Internal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.uchi.Database.BackEnd.Internal.Tables.AnimalKindTable;
import com.uchi.Database.BackEnd.Internal.Tables.CategoryTable;
import com.uchi.Database.BackEnd.Internal.Tables.ItemPropertieTable;
import com.uchi.Database.BackEnd.Internal.Tables.ItemTable;
import com.uchi.Database.BackEnd.Internal.Tables.PetItemsTable;
import com.uchi.Database.BackEnd.Internal.Tables.PetTable;
import com.uchi.Database.BackEnd.Internal.Tables.PropertieTable;
import com.uchi.Database.BackEnd.Internal.Tables.UserItemsTable;
import com.uchi.Database.BackEnd.Internal.Tables.UserPetsTable;
import com.uchi.Database.BackEnd.Internal.Tables.UsersTable;

public class InternalDatabaseConnector extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 23;
	private static final String DATABASE_NAME = "petDB";
	
	private static InternalDatabaseConnector mInstance = null;
	private Context context;
	
	private InternalDatabaseConnector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

	public static InternalDatabaseConnector getInstance(Context context) {
        /** 
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information: 
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new InternalDatabaseConnector(context.getApplicationContext());
        }
        return mInstance;
    }
	
	/**
	 * This method is called when the database File doesn't exist yet. The Create statement from the different table classes are executed here
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		ItemTable.onCreate(db);
		CategoryTable.onCreate(db);
		PropertieTable.onCreate(db);
		ItemPropertieTable.onCreate(db);
		AnimalKindTable.onCreate(db);
		PetTable.onCreate(db);
		PetItemsTable.onCreate(db);
		UsersTable.onCreate(db);
		UserItemsTable.onCreate(db);
		UserPetsTable.onCreate(db);
	}

	/**
	 * This method is called when there is a upgrade available for the database. All tables will be cleared and the new tables with be created. All data will be lost.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		CategoryTable.onUpgrade(db, oldVersion, newVersion);
		PropertieTable.onUpgrade(db, oldVersion, newVersion);
		ItemTable.onUpgrade(db, oldVersion, newVersion);
		ItemPropertieTable.onUpgrade(db, oldVersion, newVersion);
		PetTable.onUpgrade(db, oldVersion, newVersion);
		AnimalKindTable.onUpgrade(db, oldVersion, newVersion);
		PetItemsTable.onUpgrade(db, oldVersion, newVersion);
		UsersTable.onUpgrade(db, oldVersion, newVersion);
		UserItemsTable.onUpgrade(db, oldVersion, newVersion);
		UserPetsTable.onUpgrade(db, oldVersion, newVersion);
	}
}
