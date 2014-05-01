package com.uchi.uchianimals;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uchi.Database.BackEnd.DatabaseConnector;
import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO;
import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO.Categorys;
import com.uchi.Database.BackEnd.Internal.DAO.PetDAO;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.Item;
import com.uchi.Database.BackEnd.Objects.Pet;

public class BaseListActivity extends Activity{

	protected Categorys selectedCategory;
	protected static ImageView lastSelected;
	
	/**
	 * Set the selected image when a category changes
	 * @param id
	 */
	protected void selectImage(int id) {
		lastSelected.setImageResource((Integer) lastSelected.getTag());
		lastSelected = ((ImageView) findViewById(id));
		
		switch(selectedCategory) {
		case CURRENTANIMALITEMS:
			lastSelected.setTag(R.drawable.shirt);
			((ImageView) findViewById(id)).setImageResource(R.drawable.shirt_active);
			break;
		case AllPETS:
			lastSelected.setTag(R.drawable.paw);
			((ImageView) findViewById(id)).setImageResource(R.drawable.paw_active);
			break;
		case ALLITEMS:
			lastSelected.setTag(R.drawable.acces);
			((ImageView) findViewById(id)).setImageResource(R.drawable.acces_active);
			break;
		case STATITEMS:
			lastSelected.setTag(R.drawable.improv);
			((ImageView) findViewById(id)).setImageResource(R.drawable.improv_active);
			break;
		case COINS:
			lastSelected.setTag(R.drawable.charge);
			((ImageView) findViewById(id)).setImageResource(R.drawable.charge_active);
			break;
		case BOUGHTCURRENTANIMALITEMS:
			lastSelected.setTag(R.drawable.shirt);
			((ImageView) findViewById(id)).setImageResource(R.drawable.shirt_active);
			break;
		case BOUGHTAllPETS:
			lastSelected.setTag(R.drawable.shirt);
			((ImageView) findViewById(id)).setImageResource(R.drawable.shirt_active);
			break;
		case BOUGHTALLITEMS:
			lastSelected.setTag(R.drawable.acces);
			((ImageView) findViewById(id)).setImageResource(R.drawable.acces_active);
			break;
		case BOUGHTSTATITEMS:
			lastSelected.setTag(R.drawable.improv);
			((ImageView) findViewById(id)).setImageResource(R.drawable.improv_active);
			break;
		}
	}
	
	/**
	 * This method is a onCategory select method. This will be called if a category is 
	 * clicked. Then the appropiate items will be set into the store. The coin category is 
	 * only available when you hava an internet connection
	 * @param View v
	 */
	public void onCategorySelect(View v) {
		if (this.getClass() == Store.class) {
			if (!Store.syncDone) {
				Toast.makeText(this, R.string.DatabaseStillSyncing, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		ItemsDAO itemDataSource = new ItemsDAO(this);
		PetDAO petDAO = new PetDAO(this);
		TextView header = (TextView) findViewById(R.id.header);
		
		switch (v.getId()) {
		case R.id.filterCurrent:
			header.setText(R.string.categoryCurrentHeader);
			
			selectedCategory = Categorys.CURRENTANIMALITEMS;
			
			if (getClass() == Store.class)
				selectedCategory = Categorys.CURRENTANIMALITEMS;
			else
				selectedCategory = Categorys.BOUGHTCURRENTANIMALITEMS;

			fillList(itemDataSource.getItemsByCategory(selectedCategory));
			break;
		case R.id.filterAnimal:
			header.setText(R.string.categoryAnimalHeader);
			
			selectedCategory = Categorys.AllPETS;
			
			if (getClass() == Store.class)
				fillList(petDAO.getAllPets());
			else
				fillList(petDAO.getBoughtPets());
			break;
		case R.id.filterAcces:
			header.setText(R.string.categoryAccesHeader);
			
			selectedCategory = Categorys.ALLITEMS;

			if (getClass() == Store.class)
				selectedCategory = Categorys.ALLITEMS;
			else
				selectedCategory = Categorys.BOUGHTALLITEMS;	
			
			fillList(itemDataSource.getItemsByCategory(selectedCategory));
			break;
		case R.id.filterImprove:
			header.setText(R.string.categoryImproveHeader);
			
			if (getClass() == Store.class)
				selectedCategory = Categorys.STATITEMS;
			else
				selectedCategory = Categorys.BOUGHTSTATITEMS;			
			
			fillList(itemDataSource.getItemsByCategory(selectedCategory));
			break;
		case R.id.filterCharger:
			header.setText(R.string.categoryCoinHeader);
			if(!DatabaseConnector.isOnline(this))
				DatabaseConnector.NoConnectionToast(this);
			else{
				selectedCategory = Categorys.COINS;
				
				fillList(itemDataSource.getItemsByCategory(selectedCategory));
			}
			break;
		}
		selectImage(v.getId());
		
		itemDataSource.close();
	}
	/**
	 * With this method the data will set into the listView. The list will only accepts Objets that derive from DatabaseData
	 * @param data
	 */
	protected void fillList(ArrayList<DatabaseData> data) {
		final ListView list = (ListView) findViewById(R.id.listView);
		storeListAdapter adapter = new storeListAdapter(this, data);
		list.setAdapter(adapter);

		setItemOnClickListener(list);
	}
	
	/**
	 * With this method ItemClicklisteners will be set on the listview items. So when you click a item you will be
	 * directed to the itemDetail page
	 * @param ListView list
	 */
	private void setItemOnClickListener(final ListView list) {
		list.setClickable(true);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					DatabaseData o = (DatabaseData) list.getItemAtPosition(position);
					
					int listID = 0;
					String type = "";
					
					if (o.getClass() == Pet.class) {
						Pet pet = (Pet) o;
						listID = pet.getPetID(); 
						type = "petID";
					}
					else if (o.getClass() == Item.class) {
						Item pet = (Item) o;
						listID = pet.getItemID(); 
						type = "itemID";
					} 
					
					Intent intent = new Intent(getApplicationContext(), StoreDetail.class);
					intent.putExtra(type, listID);

					startActivity(intent);
			}
		});
		
		list.setEmptyView((TextView) findViewById(R.id.emptyListView));
	}
	
	/**
	 * A onClick method for the bach button on topleft corner
	 * @param v
	 */
	public void onBackClicked(View v) {
		finish();
	}
	
	/**
	 * A onClick method for the exit Button on the topright corner
	 * @param v
	 */
	public void onExitClick(View v) {
		finish();
	}
}
