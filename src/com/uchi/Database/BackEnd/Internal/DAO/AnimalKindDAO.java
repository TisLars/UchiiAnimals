package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.uchi.Database.BackEnd.Internal.Tables.AnimalKindTable;
import com.uchi.Database.BackEnd.Objects.AnimalKind;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
/**
 * 
 */
public class AnimalKindDAO extends DataAccesObject{

	public AnimalKindDAO(Context context) {
		super(context);
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is gven here. It maps the JSONArrayData to a AnimalKind object. This object will then be added to the arraylist
	 */
	@Override
	public ArrayList<DatabaseData> mapData(JSONArray data) {
		ArrayList<DatabaseData> map = new ArrayList<DatabaseData>();
		
		for (int i = 0; i < data.length();i++) {
			JSONObject jsonObject;	
			
			try {
				jsonObject = data.getJSONObject(i); 
				AnimalKind animalKind = new AnimalKind();
				
				animalKind.setKindID(jsonObject.getInt("kindID"));
				animalKind.setKindName(jsonObject.getString("kindName"));
				
				map.add(animalKind);
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is given here. It insert/update the ArrayList of AnimalKind objects into local Database
	 */
	@Override
	public void insert(ArrayList<DatabaseData> dataSet) {

		for (DatabaseData dataRow : dataSet) {
			
			AnimalKind animalKind = (AnimalKind)dataRow;
			ContentValues values = new ContentValues();
			
			values.put(AnimalKindTable.COLUMN_KINDID, animalKind.getKindID());
			values.put(AnimalKindTable.COLUMN_KINDNAME, animalKind.getKindName());
			
			Cursor getID = database.rawQuery("SELECT properID from Properties where properID = ?", new String[]{values.get(AnimalKindTable.COLUMN_KINDID).toString()});			
			
			if (getID == null)
				database.insert(AnimalKindTable.TABLE_ANIMALKIND, null, values);
			else
				database.replace(AnimalKindTable.TABLE_ANIMALKIND, null, values);
			
			getID.close();
		}
	}



}
