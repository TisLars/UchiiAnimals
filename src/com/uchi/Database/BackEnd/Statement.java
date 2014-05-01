package com.uchi.Database.BackEnd;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Base64;
import android.util.Log;

/**Statement Class. 
 * 
 * Used to build a statement with the nessecary arguments. And then to execute de statement using the Async method.
 * By doing this Asynchronously, the main thread won't be harmed by any delays. So the user doesn't have nay lag operating the app.
 * 
 * A object can be declared either with arguments or without arguments. Using arguments like this isn't nessecary, but it makes the code look cleaner
 * and there won't be any problems using "" in the statements
 */

public class Statement{

	private AsyncTaskCompleteListener context;
	private AsyncTask<String, Void, Object> task;
	private ArrayList<String> arguments;
	
	private String statement;
	private String statementEncoded;
	
	/**
	 * Contructor Method. Declares statement and the callBack Context. And then calls the build method, for building the statement
	 * @param context
	 * @param sql
	 */
	public Statement(AsyncTaskCompleteListener context, String sql) {
		this.statement = sql;
		this.context = context;
		
		build();
	}
	
	/** 
	 * Contructor method. Declares statement, callBack Context and the list with arguments. And then calls the build method, for building the statement with the provided arguments
	 * @param context
	 * @param sql
	 * @param arg
	 */
	public Statement(AsyncTaskCompleteListener context, String sql, ArrayList<String> arg) {
		this.statement = sql;
		this.arguments = arg;
		this.context = context;
		
		build();
	}
	
	/**
	 * With this method the statement will be build with the optional arguments given in the constructor. 
	 * After that the statement will be Base64 encoded for safe URL transfer
	 */
	private void build() {
		int count = 0;
		
		for(int i = 0; i < statement.length(); i++){
			if (statement.charAt(i) == '?')
			{
				String firstPart = statement.substring(0, i);
				String lastPart = statement.substring(i + 1);
				
				String argument = this.arguments.get(count).toString();

				statement = String.format(firstPart + "\"%s\"" + lastPart, argument);
				count++;
			}
		}
		
		Log.i("info", statement);
		
		statementEncoded = Base64.encodeToString(statement.getBytes(), Base64.URL_SAFE);
		statementEncoded = statementEncoded.replace("\n", "");
	}
	
	/**
	 * This method gets the status from task that is performed on the background
	 * @return Status
	 */
	public Status getStatus() {
		return task.getStatus();
	}
	
	public AsyncTask<String, Void, Object> getTask() {
		return task;
	}
	
	/**
	 * With this method a Async Task will be started and the statement will be executed on the PHP API. By calling this method a JSONarray will returned afterwards in the callBack
	 * */
	public void executeToArray() {
		task  = new DatabaseConnector(context, DatabaseConnector.QueryType.Array);
		task.execute(new String[]{statement, statementEncoded});
	}
	
	/**
	 * With this method a Async Task will be started and the statement will be executed on the PHP API. By calling this method a Boolean will returned afterwards in the callBack
	 * */
	public void executeToBoolean() {
		task  = new DatabaseConnector(context, DatabaseConnector.QueryType.Boolean);
		task.execute(new String[]{statement, statementEncoded});
	}
	
	/**
	 * With this method a Async Task will be started and the statement will be executed on the PHP API. By calling this method a String will returned afterwards in the callBack with the tableName that 
	 * has been synced to the local Database
	 * */
	public void executeToDatabase() {
		task  = new DatabaseConnector(context, DatabaseConnector.QueryType.DatabaseData);
		task.execute(new String[]{statement, statementEncoded});
	}
}
