package com.uchi.uchianimals;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.uchi.Database.BackEnd.AsyncTaskCompleteListener;
import com.uchi.Database.BackEnd.DBConfig;
import com.uchi.Database.BackEnd.DBConfig.Mode;
import com.uchi.Database.BackEnd.DatabaseConnector;
import com.uchi.Database.BackEnd.Statement;
import com.uchi.Database.BackEnd.Internal.DAO.UsersDAO;
 
/**
 * >>Splash screen<<
 */
public class SplashScreen extends Activity implements AsyncTaskCompleteListener {
 
    private boolean mIsBackButtonPressed;
    private static final int SPLASH_DURATION = 3000; // 3 seconds splash screen duration
    private static final Integer START_COINS = 500; // Aantal coins waarmee de gebruiker start
    
    private boolean onStateComplete = false;
 
 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.splash_screen);
        
        DBConfig.setMode(Mode.LIVE);
        
        Handler handler = new Handler();
 
        if (!checkFirstStartUpConnection()) {
        	new Statement(this, "SELECT userCode FROM Users").executeToArray();
        }
        else {
        	handler.postDelayed(new Runnable() {
        	     
                @Override
                public void run() {
     
                    //finish(); to prevent users from returning to the splash screen via the back button.
     
                    finish();
                     
                    if (!mIsBackButtonPressed) {
                        // start the home screen if the back button wasn't pressed already 
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        SplashScreen.this.startActivity(intent);
                   }
                     
                }
     
            }, SPLASH_DURATION); // time in milliseconds until the run() method will be called
        }
    }
 
    /**
     * Check if its the first startup for the game. After installing. 
     * @return True if database exists. False if Database doesn't exists and User isn't connected to the Internet
     */
    private boolean checkFirstStartUpConnection() {
    	File dbFile = getApplicationContext().getDatabasePath("petDB");
		if (!dbFile.exists()) {
			if (!DatabaseConnector.isOnline(this)){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.firstOpen).setPositiveButton(R.string.confirmFirstOpener, getDialogOnClick()).setOnCancelListener(getDialogOnCancel()).show();
				return false;
			}	
			return false;
		}
		return true;
    }
    
    
    /**
     * This method inserts the new userCode if it has been determined that it doesn't exists in the database
     * @param data Contains all userCodes that already exist.
     */
    private void insertNewUserCode(ArrayList<String> data) {
		String userCode = newCode();
	
		while (data.contains(userCode)) {
			userCode = newCode();
		}
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(userCode);
		
		new Statement(this, "INSERT INTO Users (userCode,userCoins) VALUES (?)", args).executeToBoolean();
		
		UsersDAO usersDAO = new UsersDAO(this);
		usersDAO.insertUserProperties(userCode, START_COINS);
    }
    
    /**
     * This method creates a random value;
     * @return String userCode
     */
    private String newCode(){
    	String userCode = "";
    	
    	Random random = new Random();
    	int firstValue = random.nextInt(1000);
    	int secondValue = random.nextInt(200);
    	int thirdValue = random.nextInt(1000);
    	int fourthValue = random.nextInt(1000);
    	int fifthValue = random.nextInt(30);
    	
    	double rawCode = (firstValue * secondValue) - (Math.PI * fifthValue) + (thirdValue * fourthValue) - secondValue;
    	
    	userCode = ((Long) Math.round(rawCode)).toString();
    	
    	return userCode;
    }
    
    
    
    public DialogInterface.OnClickListener getDialogOnClick() {
		return new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	finish();
		    }
		};
	}
    
    public DialogInterface.OnCancelListener getDialogOnCancel() {
		return new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}

		};
	}
    
    @Override
   public void onBackPressed() {
 
        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();
 
    }


	@Override
	public void onStatementComplete(Boolean result) {
		Log.d("Info", result.toString());
		
		//Check if already ran, otherwize it will start activity 
		if(!onStateComplete){
			Intent intent = new Intent(SplashScreen.this, MainActivity.class);
			SplashScreen.this.startActivity(intent);
			finish();
			onStateComplete = true;
		}
	}


	@Override
	public void onStatementComplete(String result) {

	}


	@Override
	public void onStatementComplete(JSONArray result) {
		ArrayList<String> results = new ArrayList<String>();
		
		for (int i = 0; i < result.length(); i++) {
			JSONObject row;

			try{
				row = result.getJSONObject(i);

				results.add(row.getString("userCode"));
			}
			catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		insertNewUserCode(results);
	}

	@Override
	public void onStatementComplete(Object result) {
		DatabaseConnector.NoConnectionToast(this);
		
	}
}