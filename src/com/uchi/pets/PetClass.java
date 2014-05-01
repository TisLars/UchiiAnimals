package com.uchi.pets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.TextView;

import com.uchi.Database.BackEnd.Internal.DAO.UsersDAO;
import com.uchi.uchianimals.MainActivity;
import com.uchi.uchianimals.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import com.uchi.uchianimals.AlarmReceiver;

public class PetClass {

	private String pName;
	private String petType;
	private int hunger;
	private int play;
	private int hygiene;
	private int bladder;
	private long HUNGERRATE;
	private long PLAYRATE = 288000;
	private long HYGRATE = 576000;

	private Handler hunHandler;
	private Handler playHandler;
	private Handler hygHandler;
	private Handler feedTimesHandler;

	private ArrayList<Long> feedTimes5Min = new ArrayList<Long>();
	private boolean countingFood = false;
	private LinkedList<Long> feedMoments1 = new LinkedList<Long>();
	private LinkedList<Long> feedMoments2 = new LinkedList<Long>();

	private long feedMoment1;
	private long feedMoment2;
	private int lastFeedMoment;
	
	Context context;

	public static int userCoins;
	
	public PetClass() {
		context = MainActivity.getAppContext();
		setupPet();
	}

	/**
	 * retrieve ai data from prefs
	 */
	public void setupPet() {
		
		SharedPreferences prefsStat = context.getSharedPreferences(MainActivity.PETSTAT_PREFS_NAME, 0);
		SharedPreferences prefsAi = context.getSharedPreferences(MainActivity.PETAI_PREFS_NAME, 0);
		String savedString;
		StringTokenizer st;

		// Get preferences
		setHunger(prefsStat.getInt("myPetHunger", 0), 3);
		setHygiene(prefsStat.getInt("myPetHygiene", 0), true);
		setPlay(prefsStat.getInt("myPetPlay", 0), true);

		// retrieve moment 1 data
		savedString = prefsAi.getString("moment1", "1371195900000,1371195900000,1371195900000,1371195900000,1371195900000,1371195900000,1371195900000,1371195900000,1371195900000,1371195900000");
		st = new StringTokenizer(savedString, ",");
		for (int i = 0; i < 10; i++) {
			feedMoments1.add(i, Long.parseLong(st.nextToken()));
		}

		// retrieve moment 2 data
		savedString = prefsAi.getString("moment2", "1371228300000,1371228300000,1371228300000,1371228300000,1371228300000,1371228300000,1371228300000,1371228300000,1371228300000,1371228300000");
		st = new StringTokenizer(savedString, ",");
		for (int i = 0; i < 10; i++) {
			feedMoments2.add(i, Long.parseLong(st.nextToken()));
		}

		// calculate the average time from the lists to get two feedmoments
		feedMoment1 = calcAiAverageTime(feedMoments1);
		feedMoment2 = calcAiAverageTime(feedMoments2);
		lastFeedMoment = prefsAi.getInt("lastFeedMoment", 1);
		HUNGERRATE = prefsAi.getLong("hRate", 0);

		// if failed to retrieve HUNGERRATE, calculate it
		if (HUNGERRATE == 0) {
			if (lastFeedMoment == 1) {
				HUNGERRATE = calcHungerRate(feedMoment2, feedMoments1.getLast());
			}

			else {
				HUNGERRATE = calcHungerRate(feedMoment1, feedMoments2.getLast());
			}
		}

		// difference in millis between last login and now
		long lastTime = (prefsStat.getLong("exitTime", 0));
		long currTime = System.currentTimeMillis();
		Long diff = currTime - lastTime;
		if (lastTime != 0) {
			// parse the time
			diff /= 1000;
			diff /= 60;
			int minutes = diff.intValue() % 60;
			diff /= 60;
			int hours = diff.intValue() % 24;
			diff /= 24;

			// convert back to millis
			long a = hours * 3600000;
			long b = minutes * 60000;
			long c = b + a;

			// total time / HUNGERRATE = leftover amount
			Long d = c / HUNGERRATE;
			setHunger(d.intValue(), 2);

			d = c / PLAYRATE;
			setPlay(d.intValue(), false);

			d = c / HYGRATE;
			setHygiene(d.intValue(), false);
		}

		// start the handlers for updating
		hunHandler = new Handler();
		hunHandler.postDelayed(hunRunnable, HUNGERRATE);
		playHandler = new Handler();
		playHandler.postDelayed(playRunnable, PLAYRATE);
		hygHandler = new Handler();
		hygHandler.postDelayed(hygRunnable, HYGRATE);
	}

	/**
	 * store the Ai data in prefs
	 */
	public void saveAiData() {

		Context context = MainActivity.getAppContext();
		SharedPreferences prefsAi = context.getSharedPreferences(MainActivity.PETAI_PREFS_NAME, 0);
		SharedPreferences prefsStat = context.getSharedPreferences(MainActivity.PETSTAT_PREFS_NAME, 0);

		SharedPreferences.Editor editor = prefsAi.edit();

		// Put feedmoments, HUNGERRATE and last feeding moment in the
		// SharedPreferences file.
		// feedmoments1
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < feedMoments1.size(); i++) {
			str.append(feedMoments1.get(i)).append(",");
		}
		prefsAi.edit().putString("moment1", str.toString());

		str = new StringBuilder();
		for (int i = 0; i < feedMoments2.size(); i++) {
			str.append(feedMoments2.get(i)).append(",");
		}
		prefsAi.edit().putString("moment2", str.toString());

		prefsAi.edit().putLong("hRate", HUNGERRATE);
		prefsAi.edit().putInt("lastFeedMoment", lastFeedMoment);

		// Commit the edits!
		editor.commit();

		long currTime = System.currentTimeMillis();
		editor = prefsStat.edit();
		// Put hunger, hygiene and play in the SharedPreferences file.
		editor.putInt("myPetHunger", getHunger());
		editor.putInt("myPetHygiene", getHygiene());
		editor.putInt("myPetPlay", getPlay());
		editor.putLong("exitTime", currTime);

		// Commit the edits!
		editor.commit();
	}

	/**
	 * calculate the Average time from a list of moments
	 * 
	 * @param moment
	 * @return
	 */
	private long calcAiAverageTime(LinkedList<Long> moment) {
		long total = 0;
		for (int i = 0; i < moment.size(); i++) {
			total += moment.get(i);
		}
		return (total / moment.size());
	}

	/**
	 * Calculate the rate at which hunger will update
	 * 
	 * @param nextMoment
	 * @param lastMoment
	 * @return
	 */
	private long calcHungerRate(long nextMoment, long lastMoment) {

		long diff = calcTimeDiff(nextMoment, lastMoment);

		// target = 25, so the remainder is what should be calculated upon (eg.
		// hunger = 100, target = 25, 100 -25 = 75
		long d = Math.abs(this.hunger - 25);

		// divide the remaining time with d, leaving a rate (interval in millis)
		// at which hunger should be updated.
		diff /= d;
		return (diff);
	}

	public long calcTimeDiff(long moment1, long moment2) {
		// convert millis to a format we can work with (the millis are a date,
		// so they could be yesterday, last week, we only need the time.

		long a1 = Long.parseLong(getDate(moment1, "HH"));
		long b1 = Long.parseLong(getDate(moment2, "HH"));
		long a2 = Long.parseLong(getDate(moment1, "mm"));
		long b2 = Long.parseLong(getDate(moment2, "mm"));

		if (lastFeedMoment == 2) {
			b1 += (24 - b1);
		}

		// convert time back to millis
		a1 *= 3600000;
		a2 *= 60000;
		b1 *= 3600000;
		b2 *= 60000;

		// add the minutes and hours to eachother, then subtract to obtain the
		// difference

		long a = a1 + a2;
		long b = b1 + b2;
		long c = Math.abs(a - b);
		return c;
	}
	
	public void calcScore(String x, int start, int finished) {
		int points = 0;
		if (x == "hunger") {
			int y = this.getHunger();
			if (y > 23 && y < 27) {
				points = 50;
			} else if (y < 25) {
				if (y < 20) {
					if (y < 15) {
						if (y < 10) {
							if (y < 5)
								points = 5;
							else
								points = 10;
						} else
							points = 20;
					} else
						points = 30;
				} else
					points = 40;
			} else if (y > 25) {
				if (y > 35) {
					if (y > 50) {
						if (y > 75) {
							if (y > 80) {
								points = 0;
							} else
								points = 5;
						} else
							points = 15;
					} else
						points = 25;
				} else
					points = 40;
			}
		} else if (x.equals("playHyg")) {
			int diff = finished - start;
			double pointRate = 0;
			int maxPoints = 0;
			if (start > 23 && start < 27) {
				pointRate = 2.5;
			} else if (start < 25) {
				if (start < 20) {
					if (start < 15) {
						if (start < 10) {
							if (start < 5)
								pointRate = 0.5;
							else
								pointRate = 0.75;
						} else
							pointRate = 1;
					} else
						pointRate = 1.5;
				} else
					pointRate = 2;
			} else if (start > 25) {
				if (start > 35) {
					if (start > 50) {
						if (start > 75) {
							if (start > 80) {
								pointRate = 0.25;
							} else
								pointRate = 0.5;
						} else
							pointRate = 1;
					} else
						pointRate = 1.5;
				} else
					pointRate = 2;
			}
			double calc = (pointRate * diff) / 2;
			points = (int) calc;
		}

		updateUserCoins(points);
	}
	
	/*
	 *  Haal de coins op van de user en zet ze in de linker bovenhoek
	 */
	public void updateUserCoins(int amt) {
		UsersDAO userDAO = new UsersDAO(context);
		
		userDAO.getUserCoins(); // Huidige coins ophalen
		userDAO.addCoins(amt); // Coins adden
		
		userCoins = userDAO.getUserCoins(); //Coins weer opnieuw ophalen
		
		userDAO.close();
		
		((TextView) getActivity().findViewById(R.id.currentCoinView)).setText(String.valueOf(userCoins) + "  "); //Coins toevoegen aan view
	}

	/**
	 * convert millis to a date string DD:HH:mm
	 * 
	 * @param milliSeconds
	 * @param dateFormat
	 * @return
	 */
	private String getDate(long milliSeconds, String dateFormat) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		DateFormat formatter = new SimpleDateFormat(dateFormat);

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	/**
	 * Update hunger at a set interval
	 */
	private Runnable hunRunnable = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			setHunger(1, 2);
			if (hunger != 0)
				hunHandler.postDelayed(this, HUNGERRATE);
		}
	};

	/**
	 * Update play at a set interval
	 */
	private Runnable playRunnable = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			setPlay(1, false);

			playHandler.postDelayed(this, PLAYRATE);
		}
	};

	/**
	 * Update hygiene at a set interval
	 */
	private Runnable hygRunnable = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			setHygiene(1, false);

			hygHandler.postDelayed(this, HYGRATE);
		}
	};

	/**
	 * calculate average time over last 5 minutes of input for food and add it
	 * to the last 10 feedtimes for the closest moment
	 */
	private Runnable clearFeedList = new Runnable() {

		@Override
		public void run() {
			if (feedTimes5Min.size() > 0) {
				feedTimesHandler.removeCallbacks(clearFeedList);
				long total = 0;
				// average out the time
				for (int i = 0; i < feedTimes5Min.size(); i++) {
					total += (feedTimes5Min.get(i));
				}

				countingFood = false;

				long average = total / feedTimes5Min.size();

				// add the moment to the last 10 of moment x, perform needed
				// actions
				Long averageMomentHours = (Long.valueOf(getDate((feedMoment1 + feedMoment2) / 2, "HH")) * 3600000);
				Long averageMomentMinutes = (Long.valueOf(getDate((feedMoment1 + feedMoment2) / 2, "mm")) * 60000);

				long averageMomentMillis = averageMomentHours + averageMomentMinutes;

				Long currentHours = (Long.valueOf(getDate(System.currentTimeMillis(), "HH")) * 3600000);
				Long currentMinutes = (Long.valueOf(getDate(System.currentTimeMillis(), "mm")) * 60000);

				long totalCurrentMillis = currentHours + currentMinutes;

				if (totalCurrentMillis > averageMomentMillis) {
					feedMoments2.addLast(average);
					feedMoments2.removeFirst();
					// update moment
					lastFeedMoment = 2;
					feedMoment2 = calcAiAverageTime(feedMoments2);
					HUNGERRATE = calcHungerRate(feedMoment1, feedMoments2.getLast());
					setupNotification(feedMoment1);
					hunHandler.removeCallbacks(hunRunnable);
					hunHandler.postDelayed(hunRunnable, HUNGERRATE);
				} else {
					feedMoments1.addLast(average);
					feedMoments1.removeFirst();
					// update moment
					lastFeedMoment = 1;
					feedMoment1 = calcAiAverageTime(feedMoments1);
					HUNGERRATE = calcHungerRate(feedMoment2, feedMoments1.getLast());
					setupNotification(feedMoment2);
					hunHandler.removeCallbacks(hunRunnable);
					hunHandler.postDelayed(hunRunnable, HUNGERRATE);
				}

				feedTimes5Min.clear();
			}
		}
	};

	public void setupNotification(long target) {
		long diff = calcTimeDiff(target, System.currentTimeMillis());
		long targetTime = System.currentTimeMillis() + diff;
		// MyView is my current Activity, and AlarmReceiver is the
		// BoradCastReceiver
		Intent alarmIntent = new Intent(context, AlarmReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		alarmManager.set(AlarmManager.RTC, targetTime, pendingIntent);
	}

	/**
	 * 
	 */
	public void setImg() {

	}

	/**
	 * Sets the pets name
	 * 
	 * @param pName
	 */
	public void setName(String pName) {

	}

	/**
	 * 
	 * @param amt
	 * @param up
	 */
	public void setHunger(int amt, int up) {
		if (up == 1) {
			if (this.hunger + amt > 100)
				this.hunger = 100;
			else
				this.hunger += amt;
		} else if (up == 2) {
			if (this.hunger - amt < 0)
				this.hunger = 0;
			else
				this.hunger -= amt;
		} else {
			this.hunger = amt;
		}
		MainActivity.updateAdapter();
	}

	/**
	 * 
	 * @param amt
	 * @param up
	 */
	public void setHygiene(int amt, boolean up) {
		if (up) {
			if (this.hygiene + amt > 100)
				this.hygiene = 100;
			else
				this.hygiene += amt;
		} else {
			if (this.hygiene - amt < 0)
				this.hygiene = 0;
			else
				this.hygiene -= amt;
		}
		MainActivity.updateAdapter();
	}

	/**
	 * 
	 * @param amt
	 * @param up
	 */
	public void setPlay(int amt, boolean up) {
		if (up) {
			if (this.play + amt > 100)
				this.play = 100;
			else
				this.play += amt;
		} else {
			if (this.play - amt < 0)
				this.play = 0;
			else
				this.play -= amt;
		}
		MainActivity.updateAdapter();
	}

	/**
	 * Returns
	 * 
	 * @return String
	 */
	public String getName() {
		return (pName);
	}

	/**
	 * Returns the hunger value
	 * 
	 * @return integer
	 */
	public int getHunger() {
		return hunger;
	}

	/**
	 * Returns the play value
	 * 
	 * @return integer
	 */
	public int getPlay() {
		return (play);
	}

	/**
	 * Returns the hygiene value
	 * 
	 * @return integer
	 */
	public int getHygiene() {
		return (hygiene);
	}

	/**
	 * Returns the bladder value
	 * 
	 * @return integer
	 */
	public int getBladder() {
		return (bladder);
	}

	/**
	 * Returns the type of pet.
	 * 
	 * @return String
	 */
	protected String getPetType() {
		return (petType);
	}

	/**
	 * 
	 * @param addNow
	 */
	public void feedTimer() {

		if (countingFood) {
			feedTimes5Min.add(System.currentTimeMillis());
		} else {
			countingFood = true;
			feedTimes5Min.add(System.currentTimeMillis());
			feedTimesHandler = new Handler();
			feedTimesHandler.postDelayed(clearFeedList, 10000);
		}
	}


	public int getPlayImage() {
		return 0;
	}

	public int getPetImage() {
		return 0;
	}

	public String getPetImageTag() {
		return null;
	}

	public String getPlayImageTag() {
		return null;
	}

	public String getPetImageBelt() {
		return null;
	}

	public String getPlayImageBelt() {
		return null;
	}

	public Activity getActivity() {
		// TODO Auto-generated method stub
		return null;
	}
}
