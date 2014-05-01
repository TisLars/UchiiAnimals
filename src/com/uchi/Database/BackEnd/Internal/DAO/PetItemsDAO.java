package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.uchi.Database.BackEnd.Internal.Tables.PetItemsTable;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.PetItem;

public class PetItemsDAO extends DataAccesObject{

	public PetItemsDAO(Context context) {
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
				PetItem petItem = new PetItem();
				
				petItem.setPetItemID(jsonObject.getInt("petItemID"));
				petItem.setPetID(jsonObject.getInt("petID"));
				petItem.setItemID(jsonObject.getInt("itemID"));
				
				map.add(petItem);
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is given here. It insert/update the ArrayList of PetItem objects into local Database
	 */
	@Override
	public void insert(ArrayList<DatabaseData> dataSet) {
		
		for (DatabaseData dataRow : dataSet) {
			PetItem petItem = (PetItem) dataRow;
			
			ContentValues values = new ContentValues();
			
			values.put(PetItemsTable.COLUMN_PETITEMID, petItem.getPetItemID());
			values.put(PetItemsTable.COLUMN_PETID, petItem.getPetID());
			values.put(PetItemsTable.COLUMN_ITEMID, petItem.getItemID());
			
			Cursor getID = database.rawQuery("SELECT petItemID from PetItems where petItemID = ?", new String[]{values.get(PetItemsTable.COLUMN_PETITEMID).toString()});			
			
			if (getID == null)
				database.insert(PetItemsTable.TABLE_PETITEMS, null, values);
			else
				database.replace(PetItemsTable.TABLE_PETITEMS, null, values);
			
			getID.close();
		}
	}

}
