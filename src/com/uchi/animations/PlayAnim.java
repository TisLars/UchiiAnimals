package com.uchi.animations;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO;
import com.uchi.Database.BackEnd.Internal.DAO.ItemsDAO.Categorys;
import com.uchi.Database.BackEnd.Objects.DatabaseData;
import com.uchi.Database.BackEnd.Objects.Item;
import com.uchi.pets.PetClass;
import com.uchi.uchianimals.MainActivity;
import com.uchi.uchianimals.R;


public class PlayAnim extends Animations {

    public static LinkedList<ImageView> bubblesArray;
    public static int counterRemove;
    private static int petHeight;
    private static RelativeLayout gameLayout;
    private boolean timing500;
    private long start500;
    private static ImageView petImage;
    private static boolean laying;
    private int handImage = R.drawable.hand;
    private static Activity mainActivity;
    public static ImageView hand;
    public static PetClass myPet;
    private static int startValue;
    private int endValue;
	private ArrayList<DatabaseData> boughtItems;
    /*
     * Hier maakt hij een onTouchListener aan voor de hand. Hiermee beweegt de
     * spons en kan hij niet buiten het diertje of het scherm komen.
     */
    public OnClickListener onClick(final Activity act, final ViewPager myPager,
            final ImageView imageAnimation, final RelativeLayout layout,
            final ImageView petImg) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getWashing() && !getFeeding() && !getPlaying()) {
                	myPet = MainActivity.myPet;
					startValue = myPet.getPlay();
					
                    myPager.setCurrentItem(31);
                    petImage = petImg;
                    mainActivity = act;
                    gameLayout = layout;
                    hand = new ImageView(imageAnimation.getContext());
                    hand.setImageResource(handImage);
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
                    hand.setId(R.id.hand);
                    DragBuilder dragBuilder = new DragBuilder();
                    layout.addView(hand, layoutPar);
                    hand.setOnTouchListener(dragBuilder.onTouch(hand, layout));
                    layout.setOnTouchListener(dragBuilder.onTouchLayout(petImg,
                            "image"));
                    setPlaying(true);
                    Handler handHandler = new Handler();
                    handHandler.postDelayed(dragBuilder.handRunnable, 3000);               
                }
            }
        };
    }

    /*
     * Hier maakt hij een bubble aan iedere keer als deze aangeroepen wordt.
     * Deze wordt dan geplaatst op de plek waar de spons op dat moment is.
     */
    public void changeImage(int playImage, String playImageTag) {
        if (!laying) {
            if (!timing500) {
                start500 = System.currentTimeMillis();
                timing500 = true;
            }
            long now500 = System.currentTimeMillis();
            if (now500 - start500 > 500) {
                petImage.setImageResource(playImage);
                petImage.setTag(playImageTag);
        		updateCloth();
                laying = true;
            }
        }
    }

    /*
     * Hier verwijdert hij de spons uit de view. Uiteindelijk moet hij hem ook
     * uit de Array verwijderen.
     */
    public void removeHand(int petDefaultImage, String petDefaultTag) {
        petImage.setImageResource(petDefaultImage);
        //Als verschillende dieren komen, deze regel aanpassen!!!!!!!!!!
        petImage.setTag(petDefaultTag);
        laying = false;
		updateCloth();
        setPlaying(false);
    }

    public void checkLast() {
        setPlaying(false);
        endValue = myPet.getPlay();
        myPet.calcScore("playHyg", startValue, endValue);
        ((MainActivity) mainActivity).updateUserCoins(0);
        startValue = 0;
        endValue = 0;
    }
    
    private void updateCloth(){
        ItemsDAO itemDAO = new ItemsDAO(mainActivity);
		boughtItems = itemDAO.getItemsByCategory(Categorys.BOUGHTALLITEMS);
        ClothingAnim clothAnim = new ClothingAnim();
        if(ClothingAnim.oldImageBelt != null){
			gameLayout.removeView(ClothingAnim.oldImageBelt);
		}
		if(ClothingAnim.oldImageShirt != null){
			gameLayout.removeView(ClothingAnim.oldImageShirt);
		}
		for (DatabaseData data : boughtItems) {
			Item item = (Item) data;
			if (item.isEquipped())
				clothAnim.drawCloth(mainActivity, gameLayout, petImage, item.getItemImage());
		}
    }
}
