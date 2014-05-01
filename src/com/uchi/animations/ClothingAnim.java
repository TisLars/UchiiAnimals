package com.uchi.animations;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ClothingAnim {

	private Activity act;
	public static ImageView oldImageBelt, oldImageShirt;
	private int resID;

	/**
	 * Binnen deze methode haalt hij de layoutParameters (Margins) van het
	 * diertje op. Hierbovenop plaatst hij dan de riem van het desbetreffende
	 * diertje.
	 * 
	 * @param mainActivity
	 * @param gameLayout
	 * @param petImg
	 */
	public void drawCloth(final Activity mainActivity, final RelativeLayout gameLayout, final ImageView petImg, final String clothType) {
		String[] clothTypeFilter = clothType.split("_");
		String petImgTag = (String) petImg.getTag();
		this.act = mainActivity;
		RelativeLayout.LayoutParams petlp = (LayoutParams) petImg.getLayoutParams();
		ImageView cloth = new ImageView(mainActivity);
		resID = act.getResources().getIdentifier(petImgTag + clothType, null, act.getPackageName());
		if (clothTypeFilter[0].equals("belt")) {
			if (oldImageBelt != null) {
				gameLayout.removeView(oldImageBelt);
			}
			cloth.setImageResource(resID);
			gameLayout.addView(cloth, 2, petlp);
			oldImageBelt = cloth;
		}

		else if (clothTypeFilter[0].equals("shirt")) {
			if (oldImageShirt != null) {
				gameLayout.removeView(oldImageShirt);
			}
			cloth.setImageResource(resID);
			gameLayout.addView(cloth, 1, petlp);
			oldImageShirt = cloth;
		}
	}
}
