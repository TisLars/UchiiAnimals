package com.uchi.Database.BackEnd;

import org.json.JSONArray;
/**
 * This is a interface that states that an activity that wants connect to the 
 * external database has to have these methods.
 */
public interface AsyncTaskCompleteListener {
	   public void onStatementComplete(Boolean result);
	   public void onStatementComplete(String result);
	   public void onStatementComplete(JSONArray result);
	   public void onStatementComplete(Object result);
	}
