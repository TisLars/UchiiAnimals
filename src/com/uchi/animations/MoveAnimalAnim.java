package com.uchi.animations;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MoveAnimalAnim {

	public void move(final Activity activity, final ImageView petImg,
			final ImageView target) {
		// Hier haalt hij de target waardes op op het scherm.
		RelativeLayout.LayoutParams targetlp = (RelativeLayout.LayoutParams) target
				.getLayoutParams();
		int targetLeftMargin = targetlp.leftMargin;
		int targetBottomMargin = targetlp.bottomMargin;
		int targetTopMargin = targetlp.topMargin;
		int targetRightMargin = targetlp.rightMargin;
		// Hier verandert hij de waardes van het diertje naar de target. Dit
		// doet het niet, omdat de ImageView niet over de andere heen kan. Dit
		// moet nog uitgezocht worden.
		RelativeLayout.LayoutParams animallp = (RelativeLayout.LayoutParams) petImg
				.getLayoutParams();
		animallp.leftMargin = targetLeftMargin;
		animallp.bottomMargin = targetBottomMargin;
		animallp.topMargin = targetTopMargin;
		animallp.rightMargin = targetRightMargin;
	}

}
