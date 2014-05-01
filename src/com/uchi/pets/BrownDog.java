package com.uchi.pets;

import android.app.Activity;
import android.widget.ImageView;

import com.uchi.uchianimals.R;

public class BrownDog extends DogClass  {
	protected String petType = "brownDog";
	private Activity main;
	private int playImage, petImage;
	private String petImageTag, petPlayImageTag, petImageBelt, petPlayImageBelt;
	
	
	public BrownDog(Activity m){
		this.main = m;	
		
		petImage = R.drawable.browndog;
		playImage = R.drawable.browndoglaying;
		petImageTag = "drawable/browndog";
		petPlayImageTag = petImageTag + "laying";
		petImageBelt = petImageTag + "belt";
		petPlayImageBelt = petPlayImageTag + "belt";
	}
	
	@Override
	public Activity getActivity(){
		return this.main;
	}
	
	@Override
	public void setImg(){
		ImageView image = (ImageView) main.findViewById(R.id.petView);
		image.setImageResource(petImage);
		image.setTag(petImageTag);
	}
	
	@Override
	public int getPlayImage() {
		return playImage;
	}
	
	@Override
	public int getPetImage() {
		return petImage;
	}	

	@Override
	public String getPetImageTag() {
		return petImageTag;
	}	

	@Override
	public String getPlayImageTag() {
		return petPlayImageTag;
	}	
	
	@Override
	public String getPetImageBelt() {
		return petImageBelt;
	}	

	@Override
	public String getPlayImageBelt() {
		return petPlayImageBelt;
	}	
}
