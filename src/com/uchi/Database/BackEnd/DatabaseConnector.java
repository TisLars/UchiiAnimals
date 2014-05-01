package com.uchi.Database.BackEnd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.uchi.Database.BackEnd.Internal.DAO.DataAccesObject;
import com.uchi.uchianimals.R;

/**The Database Connector Class. 
 * In class the conenction will be made with the PHP API. By extending from the AsyncTask Class, this action will be performed Asynchronously.
 * First this will try to make a connection with the PHP API. If this is established, the statement will be sent to the API Base64 encoded. 
 * 
 * After gettting a response from the API by the method getContent, the content will then be converted in a usable formed. So that it can be used in the entire app.
 * If the statement was a SELECT statement. It will result in either a JSONArray type value or if Null a Object type with the null is returned
 * In case of UPDATE or INSERT statement. A Boolean is returned.
 */

public class DatabaseConnector extends AsyncTask<String, Void, Object>{

	public static enum QueryType {Array, Boolean, DatabaseData};
	
    private AsyncTaskCompleteListener callback;
    private Activity activity;
    
	private HttpPost httpPost;
	private HttpParams httpParams;
    private HttpClient httpClient;

    private String url;
    private QueryType queryType;
    private String statement;
    
    // The Constructor
	public DatabaseConnector(AsyncTaskCompleteListener callback, QueryType queryType)
	{
		this.callback = callback;
		this.queryType = queryType;
		this.url = DBConfig.getURL();
		
		this.activity = (Activity) callback;
	}

	/**
	 * The Method for making the Connection to the API. It uses the statement and the encoded statement
	 * @param sql
	 * @param sql64
	 */
	private void dbConnection(String sql, String sql64) {
		this.statement = sql;
	
		this.url = String.format(this.url, sql64);
		Log.i("info", this.url);
		
		httpPost = new HttpPost(this.url);
        
		httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);

        httpClient = new DefaultHttpClient(httpParams);
	}
	
    /**
     * This method checks if there is a active network connection. If not it returns false and Displays a ErrorToast*
     * @param context
     * @return True or False
     */
	public static boolean isOnline(Context context) {
        ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }  
        
        return false;
    }

	/**
	 * A Error Toast that can be called from everywhere without a instance of DatabaseConnector
	 * @param context
	 */
	public static void NoConnectionToast(Context context) {
    	Toast.makeText(context, R.string.NoConnection, Toast.LENGTH_LONG).show();
    }
    
	/**
	 * This method checks if there is any feedback from the database. And if the content that it returns has no value
	 * @param content
	 * @return True or False
	 */
    private boolean checkEmpty(String content) {
    	if (!content.equals("[]") && !content.equals("") && !content.equals("TRUE") && !content.equals("FALSE"))
    		return false;
    	
    	return true;
    }

	/**
	 * The Method for returning the values from the API in a appropiate manner. The String returned from readContent will then 
	 * be converted into either a JSONArray or a Boolean
	 * @returns a Object type because the return values have different types
	 */
    private Object getContent() {
		InputStream content = null;
		
		try {
			HttpResponse response = httpClient.execute(httpPost);
	        HttpEntity entity = response.getEntity();
	        content = entity.getContent();
		}
	    catch (IOException e) {
	    	e.printStackTrace();
		}
		
		String contentStr = readContent(content);
		
	    try
	    {
	    	if (!checkEmpty(contentStr)){
	    		switch(queryType) {
	    		case Array:
	    			return new JSONArray(contentStr); 
	    		case DatabaseData: 
	    			return contentToDatabase(new JSONArray(contentStr));
				default:
					break;
	    		}
	    	}
	    	else
	    		if(contentStr.equals("TRUE"))
	    			return true;
	    		else if (contentStr.equals("FALSE"))
	    			return false;
	    }
	    catch(JSONException e){
	            Log.e("log_tag", "Error parsing data "+e.toString());
	    }	
	    
	    return null;
	}

    /**
     * This method converts the JSONArray into the specific object and then makes a call to the DataAccessObject to first map them and then to insert them into the Local Database
     * @param JSONArray jArray
     * @return the tableName for a check in the callBack activity
     */
	private String contentToDatabase(JSONArray jArray) {
		String tableName = "";
		
		if (this.statement.indexOf("WHERE") == -1)
			tableName = this.statement.substring(this.statement.indexOf("FROM") + 4, this.statement.length()).trim();
		else
			tableName = this.statement.substring(this.statement.indexOf("FROM") + 4, this.statement.indexOf("WHERE")).trim();

		DataAccesObject dao = getDaoObject(tableName);
		dao.insert(dao.mapData(jArray));
		dao.close();

		return tableName;
	}
	
	/**
	 * This Method searches the specific class by the table name. By doing this it cleares all If statements
	 * @param tableName
	 * @return a DataAccesObject
	 */
	private DataAccesObject getDaoObject(String tableName) {
		String packageName = "com.uchi.Database.BackEnd.Internal.DAO.";
		DataAccesObject dao = null;
		Class<?> cls = null;
		
		try {
			cls = Class.forName(packageName + tableName + "DAO");
			dao = (DataAccesObject) cls.getDeclaredConstructor(Context.class).newInstance(activity);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return dao;
	}
	
	/**
	 * Reading the content from the API. This will return a single string which contains all the results of the API Call
	 * @param input
	 * @returns a String with all the results
	 */
	private String readContent(InputStream input) {
		String result = "";
		
		try
		{
    		if (input != null)
    		{
    			BufferedReader reader = new BufferedReader(new InputStreamReader(input,"iso-8859-1"),8);
 	            StringBuilder sb = new StringBuilder();
 	            String line = null;
 	            while ((line = reader.readLine()) != null) {
 	                    sb.append(line + "\n");
 	            }
 	            
 	            input.close();
 	            result=sb.toString().replace("\n", "");
 	            
    		}
	    }
		catch(NullPointerException e){
	            Log.e("log_tag", "Error converting result "+e.toString());
	    } 
		catch (IOException e) {
	    		Log.e("log_tag", "Error reading input "+e.toString());
		}

		return result;
	}

	public static Statement[] syncStore(AsyncTaskCompleteListener context) {
		Statement syncCategory = new Statement(context, "Select categoryID, categoryName FROM Category");
		Statement syncPropertie = new Statement(context, "Select properID, properDescription FROM Properties");
		Statement syncItems = new Statement(context, "Select itemID, categoryID, itemName, itemDescription , itemPrice, itemImage, itemAvailable FROM Items");
		Statement syncItemProperties = new Statement(context, "Select ItemPropertiesID, itemID, properID FROM ItemProperties");
		Statement syncAnimalKind = new Statement(context, "Select kindID, kindName FROM AnimalKind");
		Statement syncPets = new Statement(context, "Select petID, kindID, petDescription, petPrice, petImage, petAvailable FROM Pet WHERE petAvailable = 1");
		Statement syncPetItems = new Statement(context, "Select petItemID, petID, itemID FROM PetItems");
		
		syncCategory.executeToDatabase();
		syncPropertie.executeToDatabase();
		syncItems.executeToDatabase();
		syncItemProperties.executeToDatabase();
		syncAnimalKind.executeToDatabase();
		syncPets.executeToDatabase();
		syncPetItems.executeToDatabase();
		
		Statement[] storeSync = {syncCategory, syncPropertie, syncItems, syncItemProperties, syncAnimalKind, syncPets, syncPetItems};
		
		return storeSync;
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
	
	
	/**
	 * This method in a overrided method from AsynTask. This will run everything asynchronously. Also this object return a Object type because the return type from this method can differ from a JSONArray
	 * to a Boolean of a String. If a null value is returned then it means that no result has been found neither a Boolean JSONArray or a String.
	 */
	@Override
	protected Object doInBackground(String... sql) {
		dbConnection(sql[0], sql[1]);
		return getContent();
	}
	
	/**
	 * After there is a result from the doInBackground method, this method is called. In this method the 
	 * values will be sent back to the callBack Activity. Differ on the QueryType. The appropriate method will be called
	 * */
	@Override
	protected void onPostExecute(Object result) {
		if (this.queryType == QueryType.Array && result != null) 
			callback.onStatementComplete((JSONArray)result);
		else if (this.queryType == QueryType.Boolean && result != null)
			callback.onStatementComplete((Boolean)result);
		else if (this.queryType == QueryType.DatabaseData && result != null)
			callback.onStatementComplete((String)result);
		else
			callback.onStatementComplete(result);
    }
}
