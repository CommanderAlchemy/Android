package com.commanderalchemy.myeconomy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database Transactions, Creates the database and also handles the versioning.
 * @author Artur Olech
 *
 */
public class DatabaseTransactions extends SQLiteOpenHelper {
	
	// DB Settings
	private static final int 			DATABASE_VERSION 	= 4;
	protected static final String 	DATABASE_NAME 		= "MyEconomy.db";
	protected static final String 	DATABASE_TABLE_NAME = "Transactions";
	
	// Columns 
	protected static final String 	ROW_ID 				= "ID";
	protected static final String 	USER_ID 			= "USER_ID";
	protected static final String		TYPE				= "TYPE";
	protected static final String 	CATEGORY 			= "CATEGORY";
	protected static final String 	TITLE 				= "TITLE";
	protected static final String		BAR_CODE			= "BAR_CODE";
	protected static final String 	DATE 				= "DATE";
	protected static final String 	IMG 				= "IMG";
	protected static final String 	AMOUNT 				= "AMOUNT";
	
	// Create
	private static final String TRANSACTIONS_TABLE_CREATE = 
			"create table " + DATABASE_TABLE_NAME + " ("
					+ ROW_ID		+ " integer primary key autoincrement, "
					+ USER_ID 		+ " TEXT NOT NULL," 
					+ TYPE			+ " TEXT NOT NULL,"
					+ CATEGORY 		+ " TEXT NOT NULL," 
					+ TITLE			+ " TEXT NOT NULL,"
					+ BAR_CODE		+ " TEXT,"
					+ DATE			+ " TEXT NOT NULL,"
					+ IMG			+ " TEXT,"
					+ AMOUNT		+ " TEXT NOT NULL);";
	
	/**
	 * DatabaseTransactions, gives database name and version to the helper.
	 * @param context
	 */
	public DatabaseTransactions (Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * OnCreate, creates the database with the SQL query above.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TRANSACTIONS_TABLE_CREATE);
	}

	/**
	 * OnUpgrade, when version is changed. At this moment it just destroys all data and creates a new one.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(this.getClass().getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
	        onCreate(db);
	}
}
