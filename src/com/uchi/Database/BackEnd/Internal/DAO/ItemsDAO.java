package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.uchi.Database.BackEnd.Internal.Tables.ItemTable;
import com.uchi.Database.BackEnd.Internal.Tables.UserItemsTable;
import com.uchi.Database.BackEnd.Objects.Category;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.Item;
import com.uchi.Database.BackEnd.Objects.Propertie;

public class ItemsDAO extends DataAccesObject{

	public static enum Categorys {CURRENTANIMALITEMS, AllPETS, ALLITEMS, STATITEMS, COINS, BOUGHTCURRENTANIMALITEMS, BOUGHTAllPETS, BOUGHTALLITEMS, BOUGHTSTATITEMS, BOUGHTALLPETS};
	
	/**
	 * The String[] allColums is made to ensure that these are all the Columns the ItemTable in the Local Database needs. Also when a select query is run. All colums can be added at the same time
	 */
	private String[] allColumns = { ItemTable.COLUMN_ITEMID, ItemTable.COLUMN_CATEGORYID,
									ItemTable.COLUMN_ITEMNAME, ItemTable.COLUMN_ITEMDESCRIPTION, 
									ItemTable.COLUMN_ITEMPRICE, ItemTable.COLUMN_ITEMIMAGE, 
									ItemTable.COLUMN_ITEMAVAILABLE};
	
	/**
	 * Here I've made a couple of Queries specific for different Categorys. Because the queries are somewhat complex, its difficault to perform these queries with 
	 * a database.query method. Thats why I have chosen to make these queries like this and then execute them using database.rawQuery. 
	 */
	private final String queryAllItems = "SELECT itemID, c.categoryID, itemName, itemDescription, itemPrice, itemImage, itemAvailable FROM Items i " +
										 "INNER JOIN Category c ON c.categoryID = i.categoryID WHERE c.categoryName != ? AND i.itemAvailable = 1";
	
	private final String queryAllStats = "SELECT itemID, c.categoryID, itemName, itemDescription, itemPrice, itemImage, itemAvailable " +
										 "FROM Items i INNER JOIN Category c ON c.categoryID = i.categoryID WHERE c.categoryName = ? AND i.itemAvailable = 1";
	
	private final String queryCurrentPetItems = "Select i.itemID, c.categoryID, i.itemName, i.itemDescription, i.itemPrice, i.itemImage, i.itemAvailable " +
												"FROM Items i INNER JOIN PetItems pItems ON i.itemID = pItems.itemID " +
												"INNER JOIN Category c ON i.categoryID = c.categoryID " +
												"WHERE pItems.petID = ? AND i.itemAvailable = 1 AND c.categoryName != ?" ; 
	
	private final String queryCoinsPackages = "SELECT itemID, c.categoryID, itemName, itemDescription, itemPrice, itemImage, itemAvailable FROM Items i " +
										 	  "INNER JOIN Category c ON c.categoryID = i.categoryID WHERE c.categoryName = ? AND i.itemAvailable = 1";
	
	
	private final String queryBoughtAllItems = "Select i.itemID, categoryID, itemName, itemDescription, itemPrice, itemImage, itemAvailable FROM Items i " +
												"INNER JOIN UserItems uItems ON i.itemID = uItems.itemID WHERE i.itemAvailable = 1 AND uItems.userID = 1";
	
	private final String queryBoughtStatItems = "Select i.itemID, c.categoryID, itemName, itemDescription, itemPrice, itemImage, itemAvailable FROM Items i " +
												"INNER JOIN UserItems uItems ON i.itemID = uItems.itemID " +
												"INNER JOIN Category c ON c.categoryID = i.categoryID WHERE c.categoryName = ? " +
												"AND i.itemAvailable = 1 AND uItems.userID = 1";
	
	private final String queryBoughtCurrentPet= "Select i.itemID, c.categoryID, itemName, itemDescription, itemPrice, itemImage, itemAvailable FROM Items i " +
												"INNER JOIN UserItems uItems ON i.itemID = uItems.itemID " +
												"INNER JOIN Category c ON c.categoryID = i.categoryID WHERE c.categoryName != ? " +
												"AND i.itemAvailable = 1 AND uItems.userID = 1";
	
	public ItemsDAO(Context context) {
		super(context);
	}

	@Override
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is gven here. It maps the JSONArrayData to a Item object. This object will then be added to the arraylist
	 */
	public ArrayList<DatabaseData> mapData(JSONArray data) {
		ArrayList<DatabaseData> map = new ArrayList<DatabaseData>();
		
		for (int i = 0; i < data.length();i++) {
			JSONObject jsonObject;	
			
			try {
				jsonObject = data.getJSONObject(i); 
				Item item = new Item();
				
				item.setItemID(jsonObject.getInt("itemID"));
		    	item.setCategoryID(jsonObject.getInt("categoryID"));
		    	item.setItemName(jsonObject.getString("itemName"));
		    	item.setItemDescription(jsonObject.getString("itemDescription"));
		    	item.setItemPrice((Double)jsonObject.getDouble("itemPrice"));
		    	item.setItemImage(jsonObject.getString("itemImage"));
		    	item.setItemAvailable(jsonObject.getInt("itemAvailable"));
		    	
				map.add(item);
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	@Override
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is given here. It insert/update the ArrayList of Item objects into local Database
	 */
	public void insert(ArrayList<DatabaseData> dataSet) {
		
		for (DatabaseData data : dataSet) {
			Item item = (Item) data;
			
			ContentValues values = new ContentValues();
			
			values.put(ItemTable.COLUMN_ITEMID, item.getItemID());
			values.put(ItemTable.COLUMN_CATEGORYID, item.getCategoryID());
			values.put(ItemTable.COLUMN_ITEMNAME, item.getItemName());
			values.put(ItemTable.COLUMN_ITEMDESCRIPTION, item.getItemDescription());
			values.put(ItemTable.COLUMN_ITEMPRICE, item.getItemPrice());
			values.put(ItemTable.COLUMN_ITEMIMAGE, item.getItemImage());
			values.put(ItemTable.COLUMN_ITEMAVAILABLE, item.getItemAvailable());
			
			Cursor getID = database.rawQuery("SELECT itemID from Items where itemID = ?", new String[]{values.get(ItemTable.COLUMN_ITEMID).toString()});			
			
			if (getID == null)
				database.insert(ItemTable.TABLE_ITEMS, null, values);
			else
				database.replace(ItemTable.TABLE_ITEMS, null, values);
			
			getID.close();
		}

	}

	/**
	 * This method is used for getting specific Items when searched by a category. The diifenrent statements that ares used for getting the data a stated 
	 * as private final. By doing this the stements can't be altered during runtime. After getting the data. It has to be mapped on the Item object. 
	 * @param category
	 * @return
	 */
	public ArrayList<DatabaseData> getItemsByCategory(Categorys category) {
		ArrayList<DatabaseData> items = new ArrayList<DatabaseData>();

		String query = "";
		String[] args = null;
		
		switch(category) {
		case CURRENTANIMALITEMS:
			query = queryCurrentPetItems;
			args = new String[]{"1", "Coins"};
			break;
		case ALLITEMS:
			query = queryAllItems;
			args = new String[]{"Coins"};
			break;
		case STATITEMS :
			query = queryAllStats;
			args = new String[]{"Food"};
			break;
		case COINS :
			query = queryCoinsPackages;
			args = new String[]{"Coins"};
			break;
		case BOUGHTCURRENTANIMALITEMS:
			query = queryBoughtCurrentPet;
			args = new String[]{"Coins"};
			break;
		case BOUGHTALLITEMS:
			query = queryBoughtAllItems;
			args = new String[]{};
			break;
		case BOUGHTSTATITEMS :
			query = queryBoughtStatItems;
			args = new String[]{"Food"};
			break;
		default:
			break;
		}
		
		Cursor dataSet = database.rawQuery(query, args);
		dataSet.moveToFirst();
	    
	    while (!dataSet.isAfterLast()) {
	    	
	    	Item item = setItem(dataSet);
	      
	    	CategoryDAO categoryDAO = new CategoryDAO(context);
	    	Category categoryData = categoryDAO.getCategoryByID(item.getCategoryID());
	    	item.setCategoryName(categoryData.getCategoryName());

	    	Cursor boughtData = database.rawQuery("SELECT userItemID, userID, itemID, equipped FROM UserItems WHERE itemID = ?", new String[]{item.getItemID().toString()});
	    	boughtData.moveToFirst();
	    	if (boughtData.getCount() == 1) {
	    		item.setItemBought(true);
	    		item.setEquipped(boughtData.getInt(3));
	    	}
	    	else
	    		item.setItemBought(false);
	    	
	    	boughtData.close();
	    	
	    	items.add(item);
	    	
	    	dataSet.moveToNext();
	    }
		dataSet.close();

		return items;
	}
	
	/**
	 * This method is used for getting a specific Item by ItemID from the local database. Also the category and the properties are added here to a Item. 
	 * The Category and the properties are accessed using there own DataAccesObjects. 
	 * @param itemID
	 * @return Item
	 */
	public Item getItemByID(Integer itemID) {
		Item item = new Item();
		
		Cursor dataSet = database.query(ItemTable.TABLE_ITEMS, allColumns, "itemID=?", new String[]{itemID.toString()}, null, null, null);
		dataSet.moveToFirst();
		
    	item = setItem(dataSet);
		
    	dataSet.close();
    	
    	CategoryDAO categoryDAO = new CategoryDAO(context);
    	Category category = categoryDAO.getCategoryByID(item.getCategoryID());
    	item.setCategoryName(category.getCategoryName());
    	
    	PropertiesDAO propertieDAO = new PropertiesDAO(context);
    	ArrayList<Propertie> properties = propertieDAO.getPropertiesByItemID(itemID);
    	
    	for(Propertie propertie : properties)
    		item.addPropertie(propertie);
    	
    	Cursor data = database.rawQuery("SELECT userItemID, userID, itemID, equipped FROM UserItems WHERE itemID = ?", new String[]{itemID.toString()});
    	data.moveToFirst();
    	
    	if (data.getCount() == 1){
    		item.setItemBought(true);
    		item.setEquipped(data.getInt(3));
    	}
    	else
    		item.setItemBought(false);
    	
    	data.close();
    	
		return item;
	}
	
	/**
	 * With this method the dataRow from the dataset will be mapped into an Item Object and then returned.
	 * @param dataSet
	 * @return Item
	 */
	private Item setItem(Cursor dataSet) {
		Item item = new Item();
		
		item.setItemID(dataSet.getInt(0));
    	item.setCategoryID(dataSet.getInt(1));
    	item.setItemName(dataSet.getString(2));
    	item.setItemDescription(dataSet.getString(3));
    	item.setItemPrice(dataSet.getDouble(4));
    	item.setItemImage(dataSet.getString(5));
    	item.setItemAvailable(dataSet.getInt(6));
		
		return item;
	}
	
	
	/**
	 * This method is used for ItemEquipment. If a user toggles the equipment button. A call will be made to this method. Here will the database record be updated to either 0 (false) or 1 (true)
	 * @param itemID
	 * @param equipped
	 */
	public void equipItem(Integer itemID, boolean equipped) {
		int active = (equipped) ? 1 : 0;
		
		ContentValues values = new ContentValues();
		values.put(UserItemsTable.COLUMN_EQUIPPED, active);
		
		database.update(UserItemsTable.TABLE_USERITEMS, values, "itemID= ?", new String[]{itemID.toString()});
	}
	
	/**
	 * This method checks how many Items are currently in the local Database
	 * @return itemCount
	 */
	public int getItemCount() {
		int result = 0;
		
		Cursor data = database.query(ItemTable.TABLE_ITEMS, allColumns, null, null, null, null, null);
		result = data.getCount();
		data.close();
		
		return result;
	}
}
