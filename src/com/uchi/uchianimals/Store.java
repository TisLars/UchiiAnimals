package com.uchi.uchianimals;

import java.io.File;

import org.json.JSONArray;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uchi.Database.BackEnd.AsyncTaskCompleteListener;
import com.uchi.Database.BackEnd.DatabaseConnector;
import com.uchi.Database.BackEnd.Statement;
import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO;
import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO.Categorys;
import com.uchi.Database.BackEnd.Internal.DAO.UsersDAO;

public class Store extends BaseListActivity implements AsyncTaskCompleteListener {

	private ProgressBar progressBar;
	private Statement[] storeSync;
	private boolean shownNoDbToast = false;

	public static int userCoins;
	public static boolean syncDone;
	private boolean mIsBound = false;
	public static MusicService mServ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.store);
		
		/**
		 * The default selected category when tha store is openeds
		 */
		lastSelected = (ImageView) findViewById(R.id.filterAcces);
		lastSelected.setImageResource(R.drawable.acces_active);
		lastSelected.setTag(R.drawable.acces);
		
		selectedCategory = 	Categorys.ALLITEMS;
		syncDone = false;
		/**
		 * Checks if there is a internetconnection availabe . If available sync with 
		 * online database. Else check if Database exists get store Data from local Database. 
		 * If not exists give Toast message
		 */

		if (DatabaseConnector.isOnline(this)) {
			progressBar = (ProgressBar) findViewById(R.id.progressBar);
			progressBar.setVisibility(View.VISIBLE);
			
			storeSync = DatabaseConnector.syncStore(this);
		}
		else {
			File dbFile = getApplicationContext().getDatabasePath("petDB");
			if (!dbFile.exists()) 
				Toast.makeText(this, R.string.NoDatabase, Toast.LENGTH_LONG).show();
			else {
				ItemsDAO itemDataSource = new ItemsDAO(this);
				fillList(itemDataSource.getItemsByCategory(selectedCategory));
				itemDataSource.close();
				
				syncDone = true;
			}
		}		
		doBindService();
		Store.mServ = MainActivity.mServ;
		updateUserCoins(0);
	}

	/**
	 * Default method if there is a INSERT or UPDATE statement performed
	 * @param result
	 */
	@Override
	public void onStatementComplete(Boolean result) {}

	/**
	 * Default method if there is a SELECT statement performed. And the user want to retun a JSONArray
	 * @param result
	 */
	@Override
	public void onStatementComplete(JSONArray result) {}

	/**
	 * Default method if there is No Result from the database or there aren't any results found
	 * @param result
	 */
	@Override
	public void onStatementComplete(Object result) {
		ItemsDAO itemDAO = new ItemsDAO(this);
		int itemCount = itemDAO.getItemCount();

		ListView list = (ListView) findViewById(R.id.listView);
		
		list.setEmptyView((TextView) findViewById(R.id.emptyListView));
		
		File dbFile = getApplicationContext().getDatabasePath("petDB");
		
		/**
		 * When a database doesn't exists of the itemCount in the current local Database is equal to zero. Show Toast.
		 * When Database does exist but thare aren't any result from the syncing. Get the data from the local Database
		 */
		if (!dbFile.exists() || itemCount == 0 )
			if (!shownNoDbToast){
				progressBar.setVisibility(View.GONE);
				Toast.makeText(this, R.string.NoDatabase, Toast.LENGTH_SHORT).show();
				shownNoDbToast = true;
			}
		else {
			if (!shownNoDbToast) {
				ItemsDAO itemDataSource = new ItemsDAO(this);
				fillList(itemDataSource.getItemsByCategory(selectedCategory));
				progressBar.setVisibility(View.GONE);
				
				shownNoDbToast = true;
				syncDone = true;
			}
		}
	}

	/**
	 * This is default method for when the executeToDatase is executed. This callBack method will return a String from the 
	 * Asyntask with the tableName of the last synced table
	 * @param result
	 */
	@Override
	public void onStatementComplete(String result) {	
		boolean done = true;
		
		for(int i = 0; i < storeSync.length - 1; i++) {
			if(!(storeSync[i].getStatus() == Status.FINISHED))
				done = false;
		}
		
		if (done) {
			ItemsDAO itemDataSource = new ItemsDAO(this);
			
			progressBar.setVisibility(View.GONE);
			fillList(itemDataSource.getItemsByCategory(selectedCategory));
			
			TextView header = (TextView) findViewById(R.id.header);
			header.setText(R.string.categoryAccesHeader);
			
			syncDone = true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mServ != null) {
			mServ.resumeMusic();
		}
		updateUserCoins(0);
	}

	@Override
	protected void onPause() {
		super.onStop();
		doUnbindService();
		if (mServ != null) {
			mServ.pauseMusic();
		}
		
		for(int i = 0; i < storeSync.length; i++)
			storeSync[i].getTask().cancel(true);
	}
	
	@Override 
	protected void onDestroy() {
		super.onDestroy();
		
		for(int i = 0; i < storeSync.length; i++)
			storeSync[i].getTask().cancel(true);
	}
	
	/**
	 *  Haal de coins op van de user en zet ze in de linker bovenhoek
	 */
	public void updateUserCoins(int amt) {
		UsersDAO userDAO = new UsersDAO(this);
		
		userDAO.getUserCoins(); // Huidige couns ophalen
		userDAO.addCoins(amt); // Coins adden
		
		userCoins = userDAO.getUserCoins(); //Coins weer opnieuw ophalen
		
		userDAO.close();

		((TextView) findViewById(R.id.currentCoins)).setText(String.valueOf(userCoins) + "  "); //Coins toevoegen aan view
	}
	private ServiceConnection Scon = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mServ = ((MusicService.ServiceBinder) binder).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};

	void doBindService() {
		bindService(new Intent(this, MusicService.class), Scon, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			unbindService(Scon);
			mIsBound = false;
		}
	}
}
