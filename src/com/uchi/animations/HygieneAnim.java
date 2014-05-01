package com.uchi.animations;

import java.util.LinkedList;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.uchi.pets.PetClass;
import com.uchi.uchianimals.MainActivity;
import com.uchi.uchianimals.R;

public class HygieneAnim extends Animations {

	public static LinkedList<ImageView> bubblesArray;
    public static Activity mainActivity;
	public static int counterRemove;
	private static int petHeight;
	private static ImageView bubbles;
	public static ImageView sponge;
	private static RelativeLayout layout;
	private static int counterAdded;
    public static PetClass myPet;
    private static int startValue;
    private int endValue;

	/*
	 * Hier maakt hij een onTouchListener aan voor de spons. Hiermee beweegt de
	 * spons en kan hij niet buiten het diertje of het scherm komen.
	 */
	public OnClickListener onClick(final Activity act, final ViewPager myPager, final ImageView imageAnimation, final RelativeLayout layout, final ImageView petImg) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!getWashing() && !getFeeding() && !getPlaying()) {
					myPet = MainActivity.myPet;
					startValue = myPet.getHygiene();
					mainActivity = act;
                	myPager.setCurrentItem(31);
                    bubblesArray = new LinkedList<ImageView>();
                    sponge = new ImageView(
                            imageAnimation.getContext());
                    sponge.setImageResource(R.drawable.soap);

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) petImg
                            .getLayoutParams();
                    int bottomMargin = lp.bottomMargin;

                    petHeight = petImg.getHeight() / 2;
                    int screenHeight = layout.getHeight();
                    int screenWidth = layout.getWidth();
                    RelativeLayout.LayoutParams layoutPar = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutPar.setMargins(
                            (screenWidth - (screenWidth / 2)) - 25,
                            ((screenHeight - (bottomMargin + petHeight))) - 16,
                            0, 0);
                    sponge.setId(R.id.sponge);
                    DragBuilder dragBuilder = new DragBuilder();
                    layout.addView(sponge, layoutPar);
                    sponge.setOnTouchListener(dragBuilder.onTouch(sponge,
                            layout));
                    layout.setOnTouchListener(dragBuilder.onTouchLayout(petImg,
                            "image"));
                    setWashing(true);
					Handler handHandler = new Handler();
					handHandler.postDelayed(dragBuilder.soapRunnable, 3000);
				}
			}
		};
	}

	/*
	 * Hier maakt hij een bubble aan iedere keer als deze aangeroepen wordt.
	 * Deze wordt dan geplaatst op de plek waar de spons op dat moment is.
	 */
	public ImageView createBubbles(int leftMargin, int topMargin, ImageView img, RelativeLayout rlayout) {
		bubbles = new ImageView(img.getContext());
		bubbles.setImageResource(R.drawable.bubbles);

		int screenHeight = rlayout.getHeight();
		int screenWidth = rlayout.getWidth();
		RelativeLayout.LayoutParams layoutPar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutPar.setMargins((screenWidth - (screenWidth - leftMargin)), ((screenHeight - (screenHeight - topMargin))), 0, 0);
		layout = rlayout;
		if (counterAdded != 0) {
			bubblesArray.add(bubbles);
			rlayout.addView(bubblesArray.get(bubblesArray.size() - 1), layoutPar);
		} else {
			bubblesArray.add(bubbles);
			rlayout.addView(bubblesArray.get(bubblesArray.size() - 1), layoutPar);
			counterAdded = counterAdded + 1;
		}
		return bubbles;
	}

	/*
	 * Hier verwijdert hij de spons uit de view. Uiteindelijk moet hij hem ook
	 * uit de Array verwijderen.
	 */
	public void removeBubbles(ImageView img) {
		if (counterRemove < bubblesArray.size()) {
			layout.removeView(bubblesArray.get(counterRemove));
			counterRemove = counterRemove + 1;
		}
	}

	public void checkLast() {
		setWashing(false);
        endValue = myPet.getHygiene();
        myPet.calcScore("playHyg", startValue, endValue);
        ((MainActivity) mainActivity).updateUserCoins(0);
	}
}
