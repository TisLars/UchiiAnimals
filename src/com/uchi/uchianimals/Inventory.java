package com.uchi.uchianimals;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;

import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO;
import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO.Categorys;

public class Inventory extends BaseListActivity {
	private boolean mIsBound = false;
	public static MusicService mServ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inventory);
		
		/**
		 * The default selected category when tha store is openeds
		 */
		lastSelected = (ImageView) findViewById(R.id.filterAcces);
		lastSelected.setImageResource(R.drawable.acces_active);
		lastSelected.setTag(R.drawable.acces);
		
		selectedCategory = Categorys.BOUGHTALLITEMS;
		ItemsDAO itemDataSource = new ItemsDAO(this);
		fillList(itemDataSource.getItemsByCategory(selectedCategory));
		itemDataSource.close();
		doBindService();
		Inventory.mServ = MainActivity.mServ;
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
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mServ != null) {
			mServ.resumeMusic();
		}
	}

	@Override
	protected void onPause() {
		super.onStop();
		doUnbindService();
		if (mServ != null) {
			mServ.pauseMusic();
		}
	}
}
