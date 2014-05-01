package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.uchi.Database.BackEnd.Internal.Tables.CategoryTable;
import com.uchi.Database.BackEnd.Objects.Category;
import com.uchi.Database.BackEnd.Objects.DatabaseData;

public class CategoryDAO extends DataAccesObject{

	private final static String[] allColumns = new String[]{CategoryTable.COLUMN_CATEGORYID, 
															CategoryTable.COLUMN_CATEGORYNAME};
	
	public CategoryDAO(Context context) {
		super(context);
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is gven here. It maps the JSONArrayData to a Category object. This object will then be added to the arraylist
	 */
	@Override
	public ArrayList<DatabaseData> mapData(JSONArray data) {
		ArrayList<DatabaseData> map = new ArrayList<DatabaseData>();
		
		for (int i = 0; i < data.length();i++) {
			JSONObject jsonObject;	
			
			try {
				jsonObject = data.getJSONObject(i); 
				Category category = new Category();
				
				category.setCategoryID(jsonObject.getInt("categoryID"));
				category.setCategoryName(jsonObject.getString("categoryName"));
				
				map.add(category);
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is given here. It insert/update the ArrayList of Category objects into local Database
	 */
	@Override
	public void insert(ArrayList<DatabaseData> dataSet) {
		
		for (DatabaseData dataRow : dataSet) {
			
			Category category = (Category )dataRow;
			ContentValues values = new ContentValues();
			
			values.put(CategoryTable.COLUMN_CATEGORYID, category.getCategoryID());
			values.put(CategoryTable.COLUMN_CATEGORYNAME, category.getCategoryName());
			
			Cursor getID = database.rawQuery("SELECT categoryID from Category where categoryID = ?", new String[]{values.get(CategoryTable.COLUMN_CATEGORYID).toString()});			
			
			if (getID == null)
				database.insert(CategoryTable.TABLE_CATEGORY, null, values);
			else
				database.replace(CategoryTable.TABLE_CATEGORY, null, values);
			
			getID.close();
		}
	}

	/**
	 * Gets a specific Category from the Local Database using a categoryID
	 * @param Integer categoryID
	 * @return Category 
	 */
	public Category getCategoryByID(Integer categoryID) {
		Category category = new Category();
		
		Cursor data = database.query(CategoryTable.TABLE_CATEGORY, allColumns, "categoryID=?", new String[]{categoryID.toString()}, null, null, null, null);
		data.moveToFirst();
		
		category.setCategoryID(data.getInt(0));
		category.setCategoryName(data.getString(1));
		
		data.close();
		
		return category;
	}
	
}
