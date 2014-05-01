package com.uchi.animations;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.uchi.uchianimals.MainActivity;
import com.uchi.uchianimals.R;

public class DragBuilder {
    ImageView img = null;
    RelativeLayout gameLayout;
    int status = 0;
    public int imgWidth, imgHeight, gameLayoutWidth, gameLayoutHeight, i;
    private Rect imageRect;
    private boolean timing350, timing500, timingHand500;
    private long start350, start500, startHand500;
    private Handler soapHandler, handHandler;
    private Handler bubblesHandler;
    private int dragEvent, counter;
    private ImageView bubbles;
    private RelativeLayout.LayoutParams lp;
    private static int counterRemove, counterAdded, handAdded;
    private HygieneAnim hygieneAnim;
    private PlayAnim playAnim;

    public OnTouchListener onTouch(final ImageView imagePet,
            final RelativeLayout layout) {
        return new OnTouchListener() {

            /*
             * Het plaatje moet eerst gedrukt worden voor draggen. Hier maakt
             * hij allemaal variabelen aan voor het plaatje wat tevoorschijn
             * komt. Dan heb je de beginwaardes van het plaatje.
             */

            @SuppressLint("NewApi")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img = imagePet;
                gameLayout = layout;
                status = 1;
                imgWidth = img.getWidth();
                imgHeight = img.getHeight();
                gameLayoutWidth = gameLayout.getWidth();
                gameLayoutHeight = gameLayout.getHeight();
                img.setVisibility(View.INVISIBLE);

                return false;
            }
        };
    }

    public OnTouchListener onTouchLayout(final ImageView petImg,
            final String value) {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                /*
                 * Als er een variabelen waar layout meegegeven wordt, dan moet
                 * dit plaatje gesleept worden voor de gehele layout.
                 */
                if (status == 1) {
                    if (value == "layout") {
                        if (img.getVisibility() != View.GONE) {
                            img.setVisibility(View.VISIBLE);
                            int imgX = (int) event.getX();
                            int imgY = (int) event.getY();
                            imageRect = new Rect(gameLayout.getLeft(),
                                    gameLayout.getTop(), gameLayout.getRight(),
                                    gameLayout.getBottom());
                            if ((imgX - imgWidth / 2) >= 0
                                    && (imgY - imgHeight / 2) >= 0
                                    && (imgX + imgWidth / 2) <= (gameLayoutWidth + 1)
                                    && (imgY + imgHeight / 2) <= (gameLayoutHeight + 1)) {
                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                        imgWidth, imgHeight);
                                lp.leftMargin = imgX - imgWidth / 2;
                                lp.topMargin = imgY - imgHeight / 2;
                                img.setLayoutParams(lp);
                            }
                        }
                    }
                    /*
                     * Als image meegegeven wordt dan moet het plaatje
                     * gecollisiond worden binnen een ander plaatje. Daar haalt
                     * hij dan de waardes van op en bouwt hij de collision.
                     */
                    else if (value == "image") {
                        if (img.getVisibility() != View.GONE) {
                            img.setVisibility(View.VISIBLE);
                            dragEvent = event.getAction();
                            int imgX = (int) event.getX();
                            int imgY = (int) event.getY();
                            imageRect = new Rect(petImg.getLeft(),
                                    petImg.getTop(), petImg.getRight(),
                                    petImg.getBottom());
                            if (imageRect.contains(
                                    v.getLeft() + (int) event.getX(),
                                    v.getTop() + (int) event.getY())
                                    && (imgX - imgWidth / 2) >= 3
                                    && (imgY - imgHeight / 2) >= 3
                                    && (imgX + imgWidth / 2) <= (gameLayoutWidth + 3)
                                    && (imgY + imgHeight / 2) <= (gameLayoutHeight + 3)) {
                                lp = new RelativeLayout.LayoutParams(imgWidth,
                                        imgHeight);
                                switch (dragEvent) {
                                case MotionEvent.ACTION_MOVE:
                                    if (img.getId() == R.id.sponge) {
                                        spongeMove(imgX, imgY);
                                        if (!timing500) {
                                            start500 = System.currentTimeMillis();
                                            timing500 = true;
                                        }
                                        long now500 = System.currentTimeMillis();
                                        if (now500 - start500 > 500) {
                                            MainActivity.myPet.setHygiene(2, true);
                                            hygieneAnim.removeBubbles(bubbles);
                                            timing500 = false;
                                        }
                                        return false;
                                    }
                                    else if (img.getId() == R.id.hand){
                                        handMove(imgX, imgY);
                                        if (!timingHand500) {
                                            startHand500 = System.currentTimeMillis();
                                            timingHand500 = true;
                                        }
                                        long now500 = System.currentTimeMillis();
                                        if (now500 - startHand500 > 500) {
                                            MainActivity.myPet.setPlay(2, true);
                                            playAnim.changeImage(MainActivity.myPet.getPlayImage(), MainActivity.myPet.getPlayImageTag());
                                            timingHand500 = false;                                            
                                        }
                                        return false;
                                    }
                                case MotionEvent.ACTION_UP:
                                    if (img.getId() == R.id.sponge) {
                                        spongeUp();
                                        return false;
                                    }
                                    else if (img.getId() == R.id.hand) {
                                        handHandler = new Handler();
                                        handHandler.postDelayed(handRunnable, 3000);
                                        return false;
                                    }
                                }
                            } else {
                                if (hygieneAnim != null){
                                    if (hygieneAnim.getWashing()) {
                                        spongeUp();
                                    }
                                }
                                else if (playAnim != null){
                                    if (playAnim.getPlaying()) {
                                        handHandler = new Handler();
                                        handHandler.postDelayed(handRunnable, 3000);
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
        };
    }

    private void spongeMove(int imgX, int imgY) {
        if (counterAdded == 0) {
            hygieneAnim = new HygieneAnim();
            counterAdded = 1;
        }
        lp.leftMargin = imgX - imgWidth / 2;
        lp.topMargin = imgY - imgHeight / 2;
        img.setLayoutParams(lp);

        /*
         * Dit is een timer van 0.35 seconden. Deze timer creeert om die tijd
         * een bubble als de sponge beweegt.
         */
        if (!timing350) {
            start350 = System.currentTimeMillis();
            timing350 = true;
        }
        long now350 = System.currentTimeMillis();
        if (now350 - start350 > 200) {
            bubbles = hygieneAnim.createBubbles(lp.leftMargin, lp.topMargin,
                    img, gameLayout);
            timing350 = false;
        }
        MainActivity.updateAdapter();
    }

    private void spongeUp() {
        counterRemove = HygieneAnim.counterRemove;
        for (i = counterRemove; i < HygieneAnim.bubblesArray.size(); i++) {
            counter = 200 * (i + 1);
            bubblesHandler = new Handler();
            bubblesHandler.postDelayed(bubblesRunnable, counter);
            counterRemove = counterRemove + 1;
        }
        counterRemove = 0;
        counterAdded = 0;
        soapHandler = new Handler();
        soapHandler.postDelayed(soapRunnable, 3000);
    }

    private void handMove(int imgX, int imgY) {
        if (handAdded == 0) {
            playAnim = new PlayAnim();
            handAdded = 1;
        }
        lp.leftMargin = imgX - imgWidth / 2;
        lp.topMargin = imgY - imgHeight / 2;
        img.setLayoutParams(lp);
        MainActivity.updateAdapter();
    }
    
    public Runnable soapRunnable = new Runnable() {
        @Override
        public void run() {
        	if (dragEvent == MotionEvent.ACTION_UP || dragEvent == 0) {
	            if(dragEvent == 0){
	        		HygieneAnim.sponge.setVisibility(View.GONE);
	        		HygieneAnim hygieneAnimation = new HygieneAnim();
	        		hygieneAnimation.checkLast();
	        	}
	        	else {
	                img.setVisibility(View.GONE);
	                hygieneAnim.checkLast();
	        	}
            }
        }
    };
    
    public Runnable handRunnable = new Runnable() {
        @Override
        public void run() {
        	if (dragEvent == MotionEvent.ACTION_UP || dragEvent == 0) {
            	if(dragEvent == 0){
            		PlayAnim.hand.setVisibility(View.GONE);
            		PlayAnim playAnimation = new PlayAnim();
                    playAnimation.removeHand(MainActivity.myPet.getPetImage(), MainActivity.myPet.getPetImageTag());
                    playAnimation.checkLast();
            	}
            	else {
            		img.setVisibility(View.GONE);
                    handAdded = 0;
                    playAnim.removeHand(MainActivity.myPet.getPetImage(), MainActivity.myPet.getPetImageTag());
                    playAnim.checkLast();
            	}
            }
        }
    };

    private Runnable bubblesRunnable = new Runnable() {
        @Override
        public void run() {
            hygieneAnim.removeBubbles(bubbles);
        }
    };
}