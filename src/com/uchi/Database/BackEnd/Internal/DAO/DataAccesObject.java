package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.uchi.Database.BackEnd.Internal.InternalDatabaseConnector;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
/**
 * This abstract class makes sure that every Class that inherits from this one makes connection to the local Database and is able 
 * to close it. Also a dataAccesobject has to have a object to insert Data and to Map them beforehand. Because this class can't 
 * insert anything it has been unimplemented. The extending class has to have a implmentation of this method.
 * 
 * The variables have been made protected. By doing this, the extending class can access them, but other ouside classes can't
 */
public abstract class DataAccesObject {

	protected static InternalDatabaseConnector dbConnector;
	protected static SQLiteDatabase database;
	
	protected Context context;
	
	public DataAccesObject(Context context) {
		dbConnector = InternalDatabaseConnector.getInstance(context);
		this.context = context;
		
		open();
	}
	
	/**
	 * This method opens the connection to the local database. 
	 */
	public void open() {
		if (database == null )
			database = dbConnector.getWritableDatabase();
		else
			if (!database.isOpen())
				database = dbConnector.getWritableDatabase();
	}
	
	/**
	 * This abstract method has no implementation but is nessesary for a DataAccesObject. This method is different in every class. So they have to make a implementation for it.
	 * In this method a JSONArray has to be parsed and mapped to a specific DatabaseData object. Eg. Item, Category, Pet, Propertie
	 * @param JSONArray jArray
	 * @return ArrayList<DatabaseData>
	 */
	public abstract ArrayList<DatabaseData> mapData(JSONArray jArray);
	
	/**
	 * This abstract method has no implementation but is nessesary for a DataAccesObject. This method is different in every class. So they have to make a implementation for it.
	 * In this method a ArrayList of DatabaseData has to be inserted into the local Database
	 * @param dao
	 */
	public abstract void insert(ArrayList<DatabaseData> dao);
	
	/**
	 * This method closes the database connection. When closed properly it will prevent memory leakage. 
	 */
	public void close() {
		database.close();
		dbConnector.close();
	}
}
