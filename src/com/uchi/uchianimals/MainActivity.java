package com.uchi.uchianimals;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO;
import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO.Categorys;
import com.uchi.Database.BackEnd.Internal.DAO.UsersDAO;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.Item;
import com.uchi.animations.Animations;
import com.uchi.animations.BlinkAnim;
import com.uchi.animations.ClothingAnim;
import com.uchi.animations.DragBuilder;
import com.uchi.animations.FragBuilder;
import com.uchi.pets.BrownDog;
import com.uchi.pets.PetClass;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	public static PetClass myPet;
	protected SwipeBuilder swipeBuilder;
	protected LayoutInflater inflater;
	public static SwipeBuilder adapter;
	public DragBuilder dragBuilder;
	public FragBuilder fragBuilder;
	public ViewPager myPager;
	public static final String PREFS_NAME = "myPetPreferences";
	public static ImageView petImg;
	private RelativeLayout gameLayout;
	private static BlinkAnim blinkAnim;
	public static ClothingAnim clothingAnim;

	public static final String PETSTAT_PREFS_NAME = "myPetStats";
	public static final String PETAI_PREFS_NAME = "myPetAI";
	public static final String OPTIONS_PREFS_NAME = "UchiOptions";

	public static Context contextOfApp;
	private boolean mIsBound = false;
	public static MusicService mServ;

	private ArrayList<DatabaseData> boughtItems;

	public static int userCoins;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		contextOfApp = getApplicationContext();
		setContentView(R.layout.activity_main);

		createSwiper();
		createPet();

		gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
		petImg = (ImageView) findViewById(R.id.petView);
		ImageView sponge = (ImageView) findViewById(R.id.sponge);

		// Hier maakt kijkt hij of de sponge al in het spel zit, zo ja dan maakt
		// hij een onTouch listener
		dragBuilder = new DragBuilder();
		if (sponge != null) {
			sponge.setOnTouchListener(dragBuilder.onTouch(sponge, gameLayout));
			gameLayout.setOnTouchListener(dragBuilder.onTouchLayout(petImg, "image"));
		}

		Handler blinkHandler = new Handler();
		blinkHandler.postDelayed(blinkRunnable, 5000);
		blinkAnim = new BlinkAnim();

		updateEquippedItems();

		updateUserCoins(0);
	}

	private void updateEquippedItems() {
		ItemsDAO itemDAO = new ItemsDAO(this);
		boughtItems = itemDAO.getItemsByCategory(Categorys.BOUGHTALLITEMS);

		clothingAnim = new ClothingAnim();
		if (ClothingAnim.oldImageBelt != null) {
			gameLayout.removeView(ClothingAnim.oldImageBelt);
		}
		if (ClothingAnim.oldImageShirt != null) {
			gameLayout.removeView(ClothingAnim.oldImageShirt);
		}
		for (DatabaseData data : boughtItems) {
			Item item = (Item) data;
			if (item.isEquipped())
				clothingAnim.drawCloth(this, gameLayout, petImg, item.getItemImage());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences userPrefs = getSharedPreferences("userPrefs", 0);
		if (userPrefs.getBoolean("musicCheck", true)) {
			doBindService();
			Intent music = new Intent();
			music.setClass(this, MusicService.class);
			startService(music);
		} else if (mServ != null) {
			mServ.stopMusic();
		}
		updateUserCoins(0);
		updateEquippedItems();
	}

	@Override
	protected void onPause() {
		super.onStop();
		doUnbindService();
		if (mServ != null) {
			mServ.pauseMusic();
		}
		myPet.saveAiData();
	}

	/**
	 * Create the pet
	 */
	private void createPet() {
		// SET PET > TODO: BUILD CHECK FOR PET
		myPet = new BrownDog(MainActivity.this);
		myPet.setImg();
	}

	public static Context getAppContext() {
		return (contextOfApp);
	}

	public SharedPreferences retrievePrefs(String type) {
		SharedPreferences preferences;

		if (type == "stat") {
			preferences = this.getSharedPreferences(PETSTAT_PREFS_NAME, 0);
		} else if (type == "petAI") {
			preferences = this.getSharedPreferences(PETAI_PREFS_NAME, Context.MODE_PRIVATE);
		} else {
			preferences = this.getSharedPreferences(OPTIONS_PREFS_NAME, Context.MODE_PRIVATE);
		}
		return (preferences);
	}

	/**
	 * 
	 * @param lastTime
	 */
	public void doTimeCalc(Long lastTime) {
		Long currTime;
		currTime = System.currentTimeMillis();
		Long diff = currTime - lastTime;

		diff /= 1000;
		int seconds = diff.intValue() % 60;
		diff /= 60;
		int minutes = diff.intValue() % 60;
		diff /= 60;
		int hours = diff.intValue() % 24;
		diff /= 24;
		int days = diff.intValue();

		System.out.println("days: " + days + " hours: " + hours + " minutes: " + minutes + " seconds: " + seconds);
	}

	/**
	 * Create the swiper
	 */
	private void createSwiper() {
		int[] swiperArray = new int[63];
		for (int i = 0; i < 63; i++) {
			if (i % 3 == 0) {
				swiperArray[i] = R.layout.first_swiper;
			} else if (i % 3 == 1) {
				swiperArray[i] = R.layout.second_swiper;
			} else if (i % 3 == 2) {
				swiperArray[i] = R.layout.third_swiper;
			}
		}
		RelativeLayout gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
		ImageView img = (ImageView) findViewById(R.id.petView);

		myPager = (ViewPager) findViewById(R.id.page_swiper);
		adapter = new SwipeBuilder(myPager, gameLayout, img, this, swiperArray);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(31);
	}

	public void onStoreClick(View v) {
		if (!Animations.getWashing() && !Animations.getPlaying() && !Animations.getFeeding()) {
			Intent intent = new Intent(v.getContext(), Store.class);
			v.getContext().startActivity(intent);
		}
	}

	public void onInventoryClick(View v) {
		if (!Animations.getWashing() && !Animations.getPlaying() && !Animations.getFeeding()) {
			Intent intent = new Intent(v.getContext(), Inventory.class);
			v.getContext().startActivity(intent);
		}
	}

	public void onOptionsClick(View v) {
		Intent intent = new Intent(v.getContext(), Options.class);
		v.getContext().startActivity(intent);
	}

	/**
	 * Als de user op de connect knop klikt
	 * 
	 * @param v
	 */
	public void onConnectClick(final View v) {
		// Alert building from:
		// @http://www.androiddom.com/2011/06/displaying-android-pop-up-dialog_13.html
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle(R.string.sharePopup);

		// Blauw tand knop
		helpBuilder.setPositiveButton("Bluetooth", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Intent intent = new Intent(v.getContext(), Bluetooth.class);
				// v.getContext().startActivity(intent);
				// Start BT Activity
				Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_LONG).show();
			}
		});

		// Facebook knop
		helpBuilder.setNeutralButton("Facebook", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Start FB Activity
				Intent intent = new Intent(v.getContext(), FacebookConnectActivity.class);
				v.getContext().startActivity(intent);
			}
		});

		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();

	}

	/*
	 * Haal de coins op van de user en zet ze in de linker bovenhoek
	 */
	public void updateUserCoins(int amt) {
		UsersDAO userDAO = new UsersDAO(this);

		userDAO.getUserCoins(); // Huidige couns ophalen
		userDAO.addCoins(amt); // Coins adden

		userCoins = userDAO.getUserCoins(); // Coins weer opnieuw ophalen

		userDAO.close();

		((TextView) findViewById(R.id.currentCoinView)).setText(String.valueOf(userCoins) + "  "); // Coins
																									// toevoegen
																									// aan
																									// view
	}

	private Runnable blinkRunnable = new Runnable() {
		@Override
		public void run() {
			ImageView petImg = (ImageView) findViewById(R.id.petView);
			blinkAnim.Blink(MainActivity.this, petImg);
			Handler blinkHandler = new Handler();
			blinkHandler.postDelayed(blinkRunnable, 5000);
		}
	};

	/*
	 * Hiermee update hij de View van de Swiper.
	 */
	public static void updateAdapter() {
		adapter.notifyDataSetChanged();
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