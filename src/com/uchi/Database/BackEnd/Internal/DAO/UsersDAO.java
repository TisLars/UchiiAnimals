package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.uchi.Database.BackEnd.Internal.Tables.UsersTable;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.User;

public class UsersDAO extends DataAccesObject{

	private String[] allColums = new String[]{UsersTable.COLUMN_USERID, UsersTable.COLUMN_USERCODE, UsersTable.COLUMN_USERCOINS};
	
	private User user;
	
	public UsersDAO(Context context) {
		super(context);
		user = new User();
	}
	
	/**
	 * In this method the userCode and the default userCoins will be inserted into the Local Database
	 * @param userCode
	 * @param userCoins
	 */
	public void insertUserProperties(String userCode, int userCoins) {
		ContentValues values = new ContentValues();
		
		values.put(UsersTable.COLUMN_USERCODE, userCode);
		values.put(UsersTable.COLUMN_USERCOINS, userCoins);
		
		database.insert(UsersTable.TABLE_USERS, null, values);

	}

	/**
	 * This method gets the userCoins of the User from the local database. And returns a int coins
	 * @return coins
	 */
	public int getUserCoins(){
		Cursor data = database.query(UsersTable.TABLE_USERS, allColums, null, null, null, null, null);
		data.moveToFirst();
		
		System.out.println(data.getInt(2));
		
		user.setUserID(data.getInt(0));
		user.setUserCoins(data.getInt(2));
		int coins = user.getUserCoins();

		return coins;
	}
	
	/**
	 * This method gets the User from the Local database. and returns that User
	 * @return
	 */
	public User getUser() {
		Cursor data = database.query(UsersTable.TABLE_USERS, allColums, null, null, null, null, null);
		data.moveToFirst();
		
		user.setUserID(data.getInt(0));
		user.setUserCode(data.getInt(1));
		data.close();

		return user;
	}
	
	/**
	 * With this method a new coin is created. The current coins change depending on the amt argument. If this parameter contains positive value,
	 *  the current coint will raise. Otherwise if amt argument contains a negative value, the currentCoins will decrease
	 * @param amt
	 */
	public void addCoins(int amt) {
		int currentCoins = user.getUserCoins();
		user.setUserCoins(currentCoins + amt);

		System.out.println("addCoins(): "+currentCoins+" + "+amt);
		System.out.println("getCoins: "+user.getUserCoins());

		ContentValues values = new ContentValues();
		values.put(UsersTable.COLUMN_USERCOINS, (currentCoins+amt));
		
		database.update(UsersTable.TABLE_USERS, values, "userID = ?", new String[]{String.valueOf(user.getUserID())});

	}

	@Override
	public ArrayList<DatabaseData> mapData(JSONArray jArray) {
		return null;
	}

	@Override
	public void insert(ArrayList<DatabaseData> dao) {}
}
