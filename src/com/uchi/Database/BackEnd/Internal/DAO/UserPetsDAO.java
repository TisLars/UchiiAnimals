package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.ContentValues;
import android.content.Context;

import com.uchi.Database.BackEnd.Internal.Tables.UserPetsTable;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.Pet;
import com.uchi.Database.BackEnd.Objects.User;
import com.uchi.Database.BackEnd.Objects.UserPet;

public class UserPetsDAO extends DataAccesObject{

	public UserPetsDAO(Context context) {
		super(context);
	}
	
	/**
	 * This method is used to prepare a Pet Object for a insert into the Local Database. This method is used in StoreDetail page where there is a Pet 
	 * object mapped. After this method gets the current userID and petID. It puts them into a new UserPet Object. This object wil be sent to the insertUserPet method
	 * @param item
	 */
	public void prepareForInsert(Pet pet) {
		UserPet userPet = new UserPet();
		
		UsersDAO userDAO = new UsersDAO(context);
		User user = userDAO.getUser();
		
		userPet.setPetID(pet.getPetID());
		userPet.setUserID(user.getUserID());
		
		insertUserPet(userPet);
	}
	
	/**
	 * This method is used to insert a record into the local database table UserPets.
	 * @param userItem
	 */
	public void insertUserPet(UserPet userPet) {
		
		ContentValues values = new ContentValues();
		
		values.put(UserPetsTable.COLUMN_USERID, userPet.getUserID());
		values.put(UserPetsTable.COLUMN_PETID, userPet.getPetID());

		database.insert(UserPetsTable.TABLE_USERPETS, null, values);
		
	//	close();
	}

	@Override
	public ArrayList<DatabaseData> mapData(JSONArray jArray) {
		return null;
	}

	@Override
	public void insert(ArrayList<DatabaseData> dao) {}
}
