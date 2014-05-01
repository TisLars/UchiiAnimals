package com.uchi.animations;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.uchi.pets.PetClass;
import com.uchi.uchianimals.MainActivity;

public class FoodAnim extends Animations {

	private ImageView foodManger;
	private AnimationDrawable animDrawable;
	private Handler mangerHandler;
	private Handler foodHandler;
	public static PetClass myPet;

	/*
	 * Als er op de knop eten is gedrukt in de swiper wordt hij hierheen
	 * gestuurd. Hier maakt hij wat variabelen aan en wordt je doorgestuurd naar
	 * de animatie.
	 */
	public OnClickListener onClick(final ImageView manger,
			final ViewPager myPager) {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!getFeeding() && !getWashing() && !getPlaying()) {
					foodManger = manger;
					myPet = MainActivity.myPet;
					myPet.feedTimer();
					myPager.setCurrentItem(31);
					startAnimation();
				}
			}
		};
	}

	/*
	 * Dit is de animatie. Hier kijkt hij of de animatie al loopt. Als dit niet
	 * het geval is dan maakt hij een animatie aan en update het eten iedere
	 * keer als het bakje iets verder leeg is.
	 */
	private void startAnimation() {
		animDrawable = (AnimationDrawable) foodManger.getDrawable();
		if (!animDrawable.isRunning()) {
			foodManger.post(new Runnable() {
				@Override
				public void run() {
					animDrawable.start();
					setFeeding(true);
					foodHandler = new Handler();
					foodHandler.postDelayed(foodRunnable, 1000);
					foodHandler.postDelayed(foodRunnable, 2000);
					foodHandler.postDelayed(foodRunnable, 3000);
					mangerHandler = new Handler();
					mangerHandler.postDelayed(mangerRunnable, 4000);
				}
			});
		}
	}

	// Runnable for updating hunger
	private Runnable mangerRunnable = new Runnable() {
		@Override
		public void run() {
			animDrawable.stop();
			setFeeding(false);
		}
	};

	// Runnable for updating hunger
	private Runnable foodRunnable = new Runnable() {
		@Override
		public void run() {
			myPet.setHunger(25, 1);
			myPet.calcScore("hunger",0,0);			
		}
	};
}
