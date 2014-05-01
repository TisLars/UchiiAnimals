package com.uchi.uchianimals;

import java.math.BigDecimal;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO;
import com.uchi.Database.BackEnd.Internal.DAO.PetDAO;
import com.uchi.Database.BackEnd.Internal.DAO.UserItemsDAO;
import com.uchi.Database.BackEnd.Internal.DAO.UserPetsDAO;
import com.uchi.Database.BackEnd.Internal.DAO.UsersDAO;
import com.uchi.Database.BackEnd.Objects.Item;
import com.uchi.Database.BackEnd.Objects.Pet;
import com.uchi.Database.BackEnd.Objects.Propertie;

public class StoreDetail extends Activity {

	// set to PaymentActivity.ENVIRONMENT_NO_NETWORK to kick the tires without
	// communicating to PayPal's servers.
	private static final String CONFIG_ENVIRONMENT = PaymentActivity.ENVIRONMENT_NO_NETWORK;

	// note that these credentials will differ between live & sandbox
	// environments.
	private static final String CONFIG_CLIENT_ID = "credential from developer.paypal.com";
	// when testing in sandbox, this is likely the -facilitator email address.
	private static final String CONFIG_RECEIVER_EMAIL = "matching paypal email address";

	private Pet pet;
	private Item item;
	private TextView txtTitle, txtDescription, txtProperties, txtPrice;
	private ImageView txtImage;
	private Button btnDetailBuy;
	private ToggleButton btnToggleEquip;
	private int resID;

	private UsersDAO userDAO;
	private int itemID, petID;
	private boolean mIsBound = false;
	public static MusicService mServ;

	protected int userCoins;
	protected int currentCoins;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_detail);

		itemID = getIntent().getIntExtra("itemID", 0);
		petID = getIntent().getIntExtra("petID", 0);

		setContentFields();
		getContent(itemID, petID);

		Intent intent = new Intent(this, PayPalService.class);

		intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, CONFIG_ENVIRONMENT);
		intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
		intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL, CONFIG_RECEIVER_EMAIL);

		startService(intent);

		userDAO = new UsersDAO(this);
		currentCoins = userDAO.getUserCoins();

		doBindService();
		StoreDetail.mServ = MainActivity.mServ;
	}

	private void setContentFields() {
		txtTitle = (TextView) findViewById(R.id.Title);
		txtDescription = (TextView) findViewById(R.id.Description);
		txtProperties = (TextView) findViewById(R.id.Properties);
		txtPrice = (TextView) findViewById(R.id.price);
		txtImage = (ImageView) findViewById(R.id.imageStoreDetail);

		btnDetailBuy = (Button) findViewById(R.id.detailBuy);
		btnToggleEquip = (ToggleButton) findViewById(R.id.toggleEquip);
	}

	private void getContent(int itemID, int petID) {
		if (itemID != 0) {
			ItemsDAO itemDAO = new ItemsDAO(this);
			item = itemDAO.getItemByID(itemID);
			itemDAO.close();
		} else if (petID != 0) {
			PetDAO petDAO = new PetDAO(this);
			pet = petDAO.getPetByID(petID);
			petDAO.close();
		}

		showContent();
	}

	private void showContent() {
		if (item != null) {

			String itemPrice = "";
			if (item.getCategoryName().equals("Coins"))
				itemPrice = item.getItemPrice().toString();
			else if (!item.getCategoryName().equals("Coins") && item.getCategoryName() != null)
				itemPrice = ((Integer) item.getItemPrice().intValue()).toString();

			txtTitle.setText(item.getItemName());
			txtDescription.setText(item.getItemDescription());
			resID = getResources().getIdentifier("drawable/" + item.getItemImage(), null, getPackageName());
			txtImage.setImageResource(resID);

			if (!item.getItemBought()) {
				txtPrice.setText(itemPrice);
				btnDetailBuy.setVisibility(View.VISIBLE);
			} else {
				txtPrice.setText(R.string.itemTag);
				txtPrice.setBackgroundColor(Color.parseColor("#18d7e5"));
				btnToggleEquip.setVisibility(View.VISIBLE);

				if (item.isEquipped())
					btnToggleEquip.setChecked(true);
			}

			for (Propertie propertie : item.getProperties()) {
				String prevText = txtProperties.getText().toString();
				txtProperties.setText(prevText + propertie.getProperDescription() + "\n");
			}
		}

		else if (pet != null) {
			txtTitle.setText(pet.getKindName());
			txtDescription.setText(pet.getPetDescription());
			txtPrice.setText(pet.getPetPrice().toString());
			resID = getResources().getIdentifier("drawable/" + pet.getPetImage(), null, getPackageName());
			txtImage.setImageResource(resID);
			if (!pet.getPetBought()) {
				txtPrice.setText(pet.getPetPrice().toString());
				btnDetailBuy.setVisibility(View.VISIBLE);
			} else {
				txtPrice.setText(R.string.itemTag);
				txtPrice.setBackgroundColor(Color.parseColor("#18d7e5"));
				btnToggleEquip.setVisibility(View.VISIBLE);

				if (pet.isActive())
					btnToggleEquip.setChecked(true);
			}

		}
	}

	public void onBackClicked(View v) {
		finish();
	}

	public void onBuyClicked(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirmMessage).setPositiveButton(R.string.confirmMessagePositive, getDialogOnClick()).setNegativeButton(R.string.confirmMessageNegative, getDialogOnClick()).show();
	}

	public void onEquipToggle(View view) {
		boolean checked = ((ToggleButton) view).isChecked();

		if (item != null) {
			ItemsDAO itemDAO = new ItemsDAO(this);
			itemDAO.equipItem(item.getItemID(), checked);
			itemDAO.close();
		} else if (pet != null) {
			PetDAO petDAO = new PetDAO(this);
			petDAO.activePet(pet.getPetID(), checked);
			petDAO.close();
		}
	}

	public void onExitClick(View v) {
		finish();
	}

	public DialogInterface.OnClickListener getDialogOnClick() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:

					if (item != null) {
						if (!item.getCategoryName().equals("Coins")) {
							if (currentCoins >= item.getItemPrice().intValue()) {
								UserItemsDAO userItemsDAO = new UserItemsDAO(StoreDetail.this);
								userItemsDAO.prepareForInsert(item);
								userItemsDAO.close();

								updateUserCoins(-item.getItemPrice().intValue());

								Intent intent = new Intent(StoreDetail.this, Inventory.class);
								startActivity(intent);
							} else
								Toast.makeText(getApplicationContext(), R.string.notEnoughMoney, Toast.LENGTH_SHORT).show();
						} else
							startPayPalActivity();
					}

					if (pet != null) {
						if (currentCoins >= pet.getPetPrice().intValue()) {
							UserPetsDAO userPetsDAO = new UserPetsDAO(StoreDetail.this);
							userPetsDAO.prepareForInsert(pet);
							userPetsDAO.close();

							Intent intent = new Intent(StoreDetail.this, Inventory.class);
							startActivity(intent);
						} else
							Toast.makeText(getApplicationContext(), R.string.notEnoughMoney, Toast.LENGTH_SHORT).show();
					}
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
				}
			}
		};
	}

	private void startPayPalActivity() {
		PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(item.getItemPrice().toString()), "USD", item.getItemName());

		Intent intent = new Intent(this, PaymentActivity.class);

		intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, CONFIG_ENVIRONMENT);
		intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
		intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL, CONFIG_RECEIVER_EMAIL);

		// It's important to repeat the clientId here so that the SDK has it if
		// Android restarts your
		// app midway through the payment UI flow.
		intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, "credential-from-developer.paypal.com");
		intent.putExtra(PaymentActivity.EXTRA_PAYER_ID, "your-customer-id-in-your-system");
		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
			if (confirm != null) {
				try {
					Log.i("paymentExample", confirm.toJSONObject().toString(4));

					// TODO: send 'confirm' to your server for verification.
					// see
					// https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
					// for more details.

				} catch (JSONException e) {
					Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
				}
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			Log.i("paymentExample", "The user canceled.");
		} else if (resultCode == PaymentActivity.RESULT_PAYMENT_INVALID) {
			Log.i("paymentExample", "An invalid payment was submitted. Please see the docs.");
		}
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
	}

	@Override
	public void onResume() {
		btnDetailBuy.setVisibility(View.GONE);
		btnToggleEquip.setVisibility(View.GONE);
		setContentFields();
		getContent(itemID, petID);
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
	@Override
	public void onDestroy() {
		stopService(new Intent(this, PayPalService.class));
		super.onDestroy();
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
