package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.uchi.Database.BackEnd.Internal.Tables.ItemPropertieTable;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.ItemPropertie;

public class ItemPropertiesDAO extends DataAccesObject{

	public ItemPropertiesDAO(Context context) {
		super(context);
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is gven here. It maps the JSONArrayData to a ItemPropertie object. This object will then be added to the arraylist
	 */
	@Override
	public ArrayList<DatabaseData> mapData(JSONArray data) {
		ArrayList<DatabaseData> map = new ArrayList<DatabaseData>();
		
		for (int i = 0; i < data.length();i++) {
			JSONObject jsonObject;	
			
			try {
				jsonObject = data.getJSONObject(i); 
				ItemPropertie itemPropertie = new ItemPropertie();
				
				itemPropertie.setItemPropertiesID(jsonObject.getInt("ItemPropertiesID"));
				itemPropertie.setItemID(jsonObject.getInt("itemID"));
				itemPropertie.setproperID(jsonObject.getInt("properID"));
				
				map.add(itemPropertie);
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is given here. It insert/update the ArrayList of ItemPropertie objects into local Database
	 */
	@Override
	public void insert(ArrayList<DatabaseData> dataSet) {
		
		for (DatabaseData dataRow : dataSet) {
			ItemPropertie itemPropertie = (ItemPropertie)dataRow;
			
			ContentValues values = new ContentValues();
			
			values.put(ItemPropertieTable.COLUMN_ITEMPROPERTIESID, itemPropertie.getItemPropertiesID());
			values.put(ItemPropertieTable.COLUMN_ITEMID, itemPropertie.getItemID());
			values.put(ItemPropertieTable.COLUMN_PROPERID, itemPropertie.getProperID());
			
			Cursor getID = database.rawQuery("SELECT ItemPropertiesID from ItemProperties where ItemPropertiesID = ?", new String[]{values.get(ItemPropertieTable.COLUMN_ITEMPROPERTIESID).toString()});			
			
			if (getID == null)
				database.insert(ItemPropertieTable.TABLE_ITEMPROPERTIES, null, values);
			else
				database.replace(ItemPropertieTable.TABLE_ITEMPROPERTIES, null, values);
			
			getID.close();
		}
	}

}
