package com.uchi.uchianimals;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.uchi.animations.FoodAnim;
import com.uchi.animations.HygieneAnim;
import com.uchi.animations.PlayAnim;

public class SwipeBuilder extends PagerAdapter {
	private int[] pageIDsArray;
	private int count;
	private RelativeLayout rlayout;
	private ImageView petImg;
	public static Activity activity;
	private static int pageId;
	private static int btnCounter;
	private ViewPager myPager;

	public SwipeBuilder(final ViewPager pager, RelativeLayout layout, ImageView img, Activity act, int... pageIDs) {
		super();

		rlayout = layout;
		petImg = img;
		activity = act;
		myPager = pager;

		int actualNoOfIDs = pageIDs.length;
		count = actualNoOfIDs + 2;
		pageIDsArray = new int[count];
		for (int i = 0; i < actualNoOfIDs; i++) {
			pageIDsArray[i + 1] = pageIDs[i];
		}
		pageIDsArray[0] = pageIDs[actualNoOfIDs - 1];
		pageIDsArray[count - 1] = pageIDs[0];

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int position) {
				int pageCount = getCount();
				if (position == 0) {
					pager.setCurrentItem(pageCount - 2, false);
				} else if (position == pageCount - 1) {
					pager.setCurrentItem(1, false);
				}
			}

			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub
			}

			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
			}
		});
	}

	// Set total number of pages
	public int getCount() {
		return count;
	}

	// Set content per screen
	@SuppressLint("NewApi")
	@Override
	public Object instantiateItem(View container, int position) {
		LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pageId = pageIDsArray[position];
		View view = inflater.inflate(pageId, null);

		/*
		 * Hier kijkt hij of de current swiper layout degene is waar de bars in
		 * staan. (first_swiper).
		 */
		if (pageIDsArray[position] == R.layout.first_swiper) {
			// Zo ja dan update hij de progressBar.
			ProgressBar hBar = (ProgressBar) view.findViewById(R.id.progressBarFood);
			ProgressBar hyBar = (ProgressBar) view.findViewById(R.id.progressBarHygiene);
			ProgressBar pBar = (ProgressBar) view.findViewById(R.id.progressBarPlay);

			hBar.setProgress(MainActivity.myPet.getHunger());
			hyBar.setProgress(MainActivity.myPet.getHygiene());
			pBar.setProgress(MainActivity.myPet.getPlay());
		}
		/*
		 * Hier kijkt hij of de current swiper layout degene is waar de buttons
		 * in staan. (second_swiper).
		 */
		if (pageIDsArray[position] == R.layout.second_swiper) {

			ImageView food = (ImageView) view.findViewById(R.id.foodRadio);
			ImageView foodManger = (ImageView) activity.findViewById(R.id.foodManger);
			FoodAnim foodAnim = new FoodAnim();
			food.setOnClickListener(foodAnim.onClick(foodManger, myPager));

			PlayAnim playAnim = new PlayAnim();
			ImageView play = (ImageView) view.findViewById(R.id.playRadio);
			play.setOnClickListener(playAnim.onClick(activity, myPager, play, rlayout, petImg));

			// Hier maakt hij een onTouchListener aan voor de HygieneButton en
			// een OnClick om terug te gaan naar first_swiper.
			HygieneAnim hygieneAnim = new HygieneAnim();
			ImageView hygiene = (ImageView) view.findViewById(R.id.hygieneRadio);
			hygiene.setOnClickListener(hygieneAnim.onClick(activity, myPager, hygiene, rlayout, petImg));
		}

		LinearLayout leftBtnLayout = (LinearLayout) activity.findViewById(R.id.leftBtnLayout);
		LinearLayout rightBtnLayout = (LinearLayout) activity.findViewById(R.id.rightBtnLayout);
		leftBtnLayout.setOnClickListener(onClickLeft(myPager, position));
		rightBtnLayout.setOnClickListener(onClickRight(myPager, position));

		// Hier add hij de nieuwe view incl. de geupdate bars als die er waren.
		((ViewPager) container).addView(view, 0);
		return view;
	}

	// This override allows calling of notifyDatasetChanged() in order to
	// instantiate the views.
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == ((View) arg1);
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	public OnClickListener onClickLeft(final ViewPager thePager, final int positioning) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnCounter == 0) {
					thePager.setCurrentItem(positioning - 2);
					btnCounter = btnCounter + 1;
				} else {
					btnCounter = 0;
				}
			}
		};
	}

	public OnClickListener onClickRight(final ViewPager thePager, final int positioning) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnCounter == 0) {
					thePager.setCurrentItem(positioning);
					btnCounter = btnCounter + 1;
				} else {
					btnCounter = 0;
				}
			}
		};
	}
}