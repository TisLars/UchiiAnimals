package com.uchi.animations;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FragBuilder {
	ImageView img = null;
	RelativeLayout gameLayout;
	int touchRawX, touchRawY, touchX, touchY, imgWidth, imgHeight;
	int[] imageCoords;
	private Rect imageRect;
	
	public OnTouchListener onTouchMove(final ImageView imageAnimation,
			final RelativeLayout layout) {
		return new OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				final AnimationDrawable animDrawable = (AnimationDrawable) imageAnimation.getDrawable();

				imgWidth = imageAnimation.getWidth();
				imgHeight = imageAnimation.getHeight();
				int[] imageCoords = new int[2];
				imageAnimation.getLocationOnScreen(imageCoords);
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN: {
					touchRawX = (int) event.getRawX();
					touchRawY = (int) event.getRawY();
					return true;
				}
				case MotionEvent.ACTION_MOVE: {
					int touchX = (int) event.getX();
					int touchY = (int) event.getY();
					imageRect = new Rect(imageAnimation.getLeft(),
							imageAnimation.getTop(), imageAnimation.getRight(),
							imageAnimation.getBottom());

					if (touchRawX != touchX && touchRawY != touchY) {
						if (!imageRect.contains(v.getLeft() + (int) event.getX(),
								v.getTop() + (int) event.getY())) {
							animDrawable.stop();
						} else {
							imageAnimation.post(new Runnable() {

								@Override
								public void run() {
									animDrawable.start();
								}
							});
						}
						touchRawX = touchX;
						touchRawY = touchY;
						return true;
					} else
						break;
				}

				case MotionEvent.ACTION_UP: {
					animDrawable.stop();
					break;
				}
				}
				return false;
			}
		};
	}
}