package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.uchi.Database.BackEnd.Internal.Tables.PropertieTable;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.Propertie;

public class PropertiesDAO extends DataAccesObject{

	public PropertiesDAO(Context context) {
		super(context);
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is gven here. It maps the JSONArrayData to a PetItem object. This object will then be added to the arraylist
	 */
	@Override
	public ArrayList<DatabaseData> mapData(JSONArray data) {
		ArrayList<DatabaseData> map = new ArrayList<DatabaseData>();
		
		for (int i = 0; i < data.length();i++) {
			JSONObject jsonObject;	
			
			try {
				jsonObject = data.getJSONObject(i); 
				Propertie propertie = new Propertie();
				
				propertie.setProperID(jsonObject.getInt("properID"));
				propertie.setProperDescription(jsonObject.getString("properDescription"));
				
				map.add(propertie);
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is given here. It insert/update the ArrayList of Propertie objects into local Database
	 */
	@Override
	public void insert(ArrayList<DatabaseData> dataSet) {
		
		for (DatabaseData dataRow : dataSet) {
			Propertie propertie = (Propertie) dataRow; 
			
			ContentValues values = new ContentValues();
			
			values.put(PropertieTable.COLUMN_PROPERID, propertie.getProperID());
			values.put(PropertieTable.COLUMN_PROPERDESCRIPTION, propertie.getProperDescription());
			
			Cursor getID = database.rawQuery("SELECT properID from Properties where properID = ?", new String[]{values.get(PropertieTable.COLUMN_PROPERID).toString()});			
			
			if (getID == null)
				database.insert(PropertieTable.TABLE_PROPERTIE, null, values);
			else
				database.replace(PropertieTable.TABLE_PROPERTIE, null, values);
			
			getID.close();
		}
	}
	
	/**
	 * This method gets all the properties available for a specific item. This is defined by an itemID. It returns a ArrayList of Properie Objects
	 * @param itemID
	 * @return ArrayList
	 */
	public ArrayList<Propertie> getPropertiesByItemID(Integer itemID) {
		ArrayList<Propertie> properties = new ArrayList<Propertie>();
		
		Cursor data = database.rawQuery("SELECT p.properID, p.properDescription " + 
				" FROM "+ PropertieTable.TABLE_PROPERTIE + " p INNER JOIN ItemProperties iProp ON p.properID = iProp.properID" +
				" WHERE iProp.itemID = ? ", new String[]{itemID.toString()});

		data.moveToFirst();
		
		while (!data.isAfterLast()) {
			Propertie propertie = new Propertie();
			
			propertie.setProperID(data.getInt(0));
			propertie.setProperDescription(data.getString(1));
			
			properties.add(propertie);
			
			data.moveToNext();
		}
		
		data.close();
		
		return properties;
	}

}
