package com.uchi.animations;

import android.app.Activity;
import android.os.Handler;
import android.widget.ImageView;

public class BlinkAnim {

	private String petImg;
	private ImageView petImgView;
	private Activity act;
	
	public void Blink(Activity act, ImageView petImg) {
		String petImgTag = (String) petImg.getTag();
		this.petImg = petImgTag;
		this.petImgView = petImg;
		this.act = act;
		petImgTag = petImgTag + "blink";
		int resID = act.getResources().getIdentifier(petImgTag, null, act.getPackageName());
		petImg.setImageResource(resID);
		
		Handler openHandler = new Handler();
		openHandler.postDelayed(openRunnable, 200);
	}
	
	private Runnable openRunnable = new Runnable() {
        @Override
        public void run() {
    		int resID = act.getResources().getIdentifier(petImg, null, act.getPackageName());
    		petImgView.setImageResource(resID);    		
        }
    };
}
