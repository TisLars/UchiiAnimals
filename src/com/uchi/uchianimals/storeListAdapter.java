package com.uchi.uchianimals;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.Item;
import com.uchi.Database.BackEnd.Objects.Pet;

public class storeListAdapter extends BaseAdapter{

	public ArrayList<DatabaseData> items;
	private Activity activity;
	
	private int listItem, resID;
	
	public storeListAdapter(Activity activity, ArrayList<DatabaseData> data) {
		super();
		
		this.activity = activity;
		this.items = data;
		
		if (activity.getClass() == Inventory.class)
			listItem = R.layout.inventory_item;
		else
			listItem = R.layout.list_item;
		
	}
	
	private boolean checkBought(DatabaseData item) {
		if (item.getClass() == Item.class) 
			return ((Item)item).getItemBought();
		else if (item.getClass() == Pet.class)
			return ((Pet)item).getPetBought();
		
		return false;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		DatabaseData curItem = items.get(position);

		if (curItem.getClass() == Item.class) {
			Item item = (Item) curItem;
			
			return item.getItemID();
		}
		else if (curItem.getClass() == Pet.class) {
			Pet pet = (Pet) curItem;
			
			return pet.getPetID();
		}
		
		return 0;
	}

	private class ViewHolder {
		TextView name;
		TextView description;
		TextView price;
		ImageView image;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();
		DatabaseData curItem = items.get(position);

		if (checkBought(curItem))
			listItem = R.layout.inventory_item;
		else
			listItem = R.layout.list_item;

		convertView = null;
		
		convertView = inflater.inflate(listItem, null);
		
		holder = new ViewHolder();
		holder.name = (TextView) convertView.findViewById(R.id.itemTitle);
		holder.description = (TextView) convertView.findViewById(R.id.itemDescription);
		holder.image = (ImageView) convertView.findViewById(R.id.coinEmblem);
		
		if(listItem == R.layout.list_item)
			holder.price = (TextView) convertView.findViewById(R.id.price);
		
		convertView.setTag(holder);
		
		if (curItem.getClass() == Item.class) {
			Item item = (Item) curItem;
			
			String itemPrice = "";
			if (item.getCategoryName().equals("Coins"))
				itemPrice = item.getItemPrice().toString();
			else if (!item.getCategoryName().equals("Coins") && item.getCategoryName() != null)
				itemPrice = ((Integer)item.getItemPrice().intValue()).toString();
				
			holder.name.setText(item.getItemName());
			holder.description.setText(item.getItemDescription());
			resID = activity.getResources().getIdentifier("drawable/" + item.getItemImage(), null, activity.getPackageName());
			holder.image.setImageResource(resID);
			
			if(listItem == R.layout.list_item)
				holder.price.setText(itemPrice);
		}
		else if (curItem.getClass() == Pet.class) {
			Pet pet = (Pet) curItem;
			
			holder.name.setText(pet.getKindName());
			holder.description.setText(pet.getPetDescription());
			resID = activity.getResources().getIdentifier("drawable/" + pet.getPetImage(), null, activity.getPackageName());
			holder.image.setImageResource(resID);
			
			if(listItem == R.layout.list_item)
				holder.price.setText(pet.getPetPrice().toString());
		}

		return convertView;
	}

}
