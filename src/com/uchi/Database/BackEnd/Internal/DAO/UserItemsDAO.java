package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.ContentValues;
import android.content.Context;

import com.uchi.Database.BackEnd.Internal.Tables.UserItemsTable;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.Item;
import com.uchi.Database.BackEnd.Objects.User;
import com.uchi.Database.BackEnd.Objects.UserItem;

public class UserItemsDAO extends DataAccesObject{

	public UserItemsDAO(Context context) {
		super(context);
	}
	
	/**
	 * This method is used to prepare a Item Object for a insert into the Local Database. This method is used in StoreDetail page where there is a Item 
	 * object mapped. After this method gets the current userID and itemID. It puts them into a new UserItem Object. This object wil be sent to the insertUserItem method
	 * @param item
	 */
	public void prepareForInsert(Item item) {
		UserItem userItem = new UserItem();
		UsersDAO userDAO = new UsersDAO(context);
		
		User user = userDAO.getUser();
		
		userItem.setItemID(item.getItemID());
		userItem.setUserID(user.getUserID());
		
		insertUserItem(userItem);
	}
	
	/**
	 * This method is used to insert a record into the local database table UserItems.
	 * @param userItem
	 */
	public void insertUserItem(UserItem userItem) {
		
		ContentValues values = new ContentValues();
		
		values.put(UserItemsTable.COLUMN_USERID, userItem.getUserID());
		values.put(UserItemsTable.COLUMN_ITEMID, userItem.getItemID());

		database.insert(UserItemsTable.TABLE_USERITEMS, null, values);
		
	//	close();
	}

	@Override
	public ArrayList<DatabaseData> mapData(JSONArray jArray) {
		return null;
	}

	@Override
	public void insert(ArrayList<DatabaseData> dao) {}
}
