package com.uchi.Database.BackEnd.Internal.DAO;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.uchi.Database.BackEnd.Internal.Tables.PetTable;
import com.uchi.Database.BackEnd.Internal.Tables.UserPetsTable;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.Pet;

public class PetDAO extends DataAccesObject{
	
	private String[] allColumns = { PetTable.COLUMN_PETID, PetTable.COLUMN_KINDID,
			PetTable.COLUMN_PETDESCRIPTION, PetTable.COLUMN_PETPRICE, 
			PetTable.COLUMN_PETIMAGE, PetTable.COLUMN_PETAVAILABLE};
	
	private final String queryBoughtPets= "SELECT p.petID, kindID, petDescription, petPrice, petImage, petAvailable " +
										  "FROM Pet p INNER JOIN UserPets uPets ON p.petID = uPets.petID WHERE petAvailable = 1";
	
	public PetDAO(Context context) {
		super(context);
	}

	/**
	 * This method overrides from DataAccesObject. A implementation of that method is gven here. It maps the JSONArrayData to a Pet object. This object will then be added to the arraylist
	 */
	@Override
	public ArrayList<DatabaseData> mapData(JSONArray data) {
		ArrayList<DatabaseData> map = new ArrayList<DatabaseData>();
		
		for (int i = 0; i < data.length();i++) {
			JSONObject jsonObject;	
			
			try {
				jsonObject = data.getJSONObject(i); 
				Pet pet = new Pet();
				
				pet.setPetID(jsonObject.getInt("petID"));
				pet.setKindID(jsonObject.getInt("kindID"));
				pet.setPetDescription(jsonObject.getString("petDescription"));
				pet.setPetPrice(jsonObject.getInt("petPrice"));
				pet.setPetImage(jsonObject.getString("petImage"));
				pet.setPetAvailable(jsonObject.getInt("petAvailable"));
		    	
				map.add(pet);
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/**
	 * This method overrides from DataAccesObject. A implementation of that method is given here. It insert/update the ArrayList of Pet objects into local Database
	 */
	@Override
	public void insert(ArrayList<DatabaseData> dataSet) {
		
		for (DatabaseData data : dataSet) {
			Pet pet = (Pet) data;
			
			ContentValues values = new ContentValues();
			
			values.put(PetTable.COLUMN_PETID, pet.getPetID());
			values.put(PetTable.COLUMN_KINDID, pet.getKindID());
			values.put(PetTable.COLUMN_PETDESCRIPTION, pet.getPetDescription());
			values.put(PetTable.COLUMN_PETPRICE, pet.getPetPrice());
			values.put(PetTable.COLUMN_PETIMAGE, pet.getPetImage());
			values.put(PetTable.COLUMN_PETAVAILABLE, pet.getPetAvailable());
			
			Cursor getID = database.rawQuery("SELECT petID from Pet where petID = ?", new String[]{values.get(PetTable.COLUMN_PETID).toString()});			
			
			if (getID == null)
				database.insert(PetTable.TABLE_PET, null, values);
			else
				database.replace(PetTable.TABLE_PET, null, values);
			
			getID.close();
		}
		
		close();
	}
	
	/**
	 * This method gets all the pets that are available and returns them into an ArrayList
	 * @return ArrayList
	 */
	public ArrayList<DatabaseData> getAllPets() {

		ArrayList<DatabaseData> pets = new ArrayList<DatabaseData>();

	    Cursor cursor = database.query(PetTable.TABLE_PET,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();

	    while (!cursor.isAfterLast()) {
	    	Pet pet = setPet(cursor);

	    	Cursor boughtData = database.rawQuery("SELECT userPetID, userID, petID FROM UserPets WHERE petID = ?", new String[]{pet.getPetID().toString()});
	    	if (boughtData.getCount() == 1)
	    		pet.setPetBought(true);
	    	else
	    		pet.setPetBought(false);

	    	boughtData.close();
	    	
	    	Cursor animalData = database.rawQuery("SELECT kindName From AnimalKind a INNER JOIN Pet p ON a.kindID = " +
					   "p.kindID WHERE p.petID = ?", new String[]{pet.getPetID().toString()});
			animalData.moveToFirst();
			pet.setKindName(animalData.getString(0));
			animalData.close();
	    	
	    	pets.add(pet);
	      
	      cursor.moveToNext();
	    }
		cursor.close();
	    
		return pets;
	}
	
	/**
	 * This method returns a Pet specific by a petID. It also adds the animalKind name to the pet
	 * @param petID
	 * @return Pet
	 */
	public Pet getPetByID(Integer petID) {
		Cursor data = database.query(PetTable.TABLE_PET, allColumns, "petID=?", new String[]{petID.toString()}, null, null, null);
		data.moveToFirst();
		
		Pet pet = setPet(data);
		
    	data.close();
    	
    	Cursor animalData = database.rawQuery("SELECT kindName From AnimalKind a INNER JOIN Pet p ON a.kindID = " +
    										   "p.kindID WHERE p.petID = ?", new String[]{petID.toString()});
		animalData.moveToFirst();
		pet.setKindName(animalData.getString(0));
		animalData.close();
		
    	Cursor boughtData = database.rawQuery("SELECT userPetID, userID, petID, active FROM UserPets WHERE petID = ?", new String[]{pet.getPetID().toString()});
    	boughtData.moveToFirst();
    	
    	if (boughtData.getCount() == 1) {
    		pet.setPetBought(true);
    		pet.setActive(boughtData.getInt(3));
    	}
    	else
    		pet.setPetBought(false);
		
    	boughtData.close();
    	
    	//close();
    	
		return pet;
	}
	
	/**
	 * This method returns all the Bought pets from the local Database into an ArrayList
	 * @return ArrayList
	 */
	public ArrayList<DatabaseData> getBoughtPets() {
		ArrayList<DatabaseData> result = new ArrayList<DatabaseData>();
		
		Cursor data = database.rawQuery(queryBoughtPets, new String[]{});
		
		data.moveToFirst();
		
		while(!data.isAfterLast()) {
			Pet pet = setPet(data);
	    	pet.setPetBought(true);

	    	Cursor animalData = database.rawQuery("SELECT kindName From AnimalKind a INNER JOIN Pet p ON a.kindID = " +
					   "p.kindID WHERE p.petID = ?", new String[]{pet.getPetID().toString()});
			animalData.moveToFirst();
			pet.setKindName(animalData.getString(0));
			animalData.close();
	    	
	    	result.add(pet);
	    	data.moveToNext();
		}
		data.close();
		
		//close();
		
		return result;
	}
	
	/**
	 * This method maps the Cursor row from the local Database call intp a Pet Object. Then it return the Pet Object
	 * @param dataSet
	 * @return Pet
	 */
	private Pet setPet(Cursor dataSet) {
		Pet pet = new Pet();
		
		pet.setPetID(dataSet.getInt(0));
    	pet.setKindID(dataSet.getInt(1));
    	pet.setPetDescription(dataSet.getString(2));
    	pet.setPetPrice(dataSet.getInt(3));
    	pet.setPetImage(dataSet.getString(4));
    	pet.setPetAvailable(dataSet.getInt(5));
		
		return pet;
	}
	
	/**
	 * This method is used for PetActivation. If a user toggles the active button. A call will be made to this method. Here will the database record be updated to either 0 (false) or 1 (true)
	 * @param petID
	 * @param checked
	 */
	public void activePet(Integer petID, boolean checked) {
		int active = (checked) ? 1 : 0;
		
		ContentValues values = new ContentValues();
		values.put(UserPetsTable.COLUMN_ACTIVE, active);
		
		database.update(UserPetsTable.TABLE_USERPETS, values, "petID= ?", new String[]{petID.toString()});
	}
}
