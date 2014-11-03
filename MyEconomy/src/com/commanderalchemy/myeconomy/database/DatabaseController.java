package com.commanderalchemy.myeconomy.database;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.commanderalchemy.myeconomy.aktivities.MainActivity;

/**
 * Database Controller, handles all the database queries.
 * @author Artur Olech
 *
 */
public class DatabaseController {
	// Debugging tag
	private static final String TAG = "DatabaseController";
	
	// DAtabase
	private SQLiteDatabase db;
	private Transaction transaction;
	private DatabaseTransactions dbTransactions;
	private ArrayList<Transaction> incomeTransactions = new ArrayList<Transaction>();
	private ArrayList<Transaction> expenseTransactions = new ArrayList<Transaction>();
	
	// Database Columns
	private String[] columns = { 	DatabaseTransactions.ROW_ID, 
									DatabaseTransactions.USER_ID, 
									DatabaseTransactions.TYPE, 
									DatabaseTransactions.CATEGORY,
									DatabaseTransactions.TITLE, 
									DatabaseTransactions.BAR_CODE, 
									DatabaseTransactions.DATE, 
									DatabaseTransactions.IMG, 
									DatabaseTransactions.AMOUNT };

	// Controller
	private MainActivity mainActivity;

	/**
	 * Constructor for the DatabaseController
	 * @param mainActivity
	 */
	public DatabaseController(MainActivity mainActivity) {
		this.dbTransactions = new DatabaseTransactions(mainActivity);
		this.mainActivity = mainActivity;
	}

	/**
	 * Open Database Connection and get write status
	 * @throws SQLException
	 */
	private void open() throws SQLException {
		db = dbTransactions.getWritableDatabase();
	}

	/**
	 * Close the database connection
	 */
	private void close() {
		dbTransactions.close();
	}

	/**
	 * Returns Income Transaction List 
	 * @return
	 */
	public ArrayList<Transaction> getIncomeTransactions() {
		return incomeTransactions;
	}

	/**
	 * Returns Expense Transaction List
	 * @return
	 */
	public ArrayList<Transaction> getExpenseTransactions() {
		return expenseTransactions;
	}

	/**
	 * Query all the Transactions
	 * @param userID
	 */
	public void loadTransactions(String userID) {
		queryExpenseByUser(userID);
		queryIncomeByUser(userID);
	}

	/**
	 * Adds a new transaction to the database.
	 * @param userID	
	 * @param type
	 * @param category
	 * @param title
	 * @param barCode
	 * @param date
	 * @param img
	 * @param amount
	 * @return Transaction object
	 */
	public Transaction createTransaction(String userID, String type, Category category, String title, String barCode, Date date, Bitmap img, Double amount) {
		Log.i(TAG, "Running SQL Statement \n Inserted " + type);
		open();
		
		// Create TransactionObject
		ContentValues values = new ContentValues();
		values.put(DatabaseTransactions.USER_ID, userID);
		values.put(DatabaseTransactions.TYPE, type);
		values.put(DatabaseTransactions.CATEGORY, category.toString());
		values.put(DatabaseTransactions.TITLE, title);
		values.put(DatabaseTransactions.BAR_CODE, barCode);
		values.put(DatabaseTransactions.DATE, date.getTime() + "");
		values.put(DatabaseTransactions.IMG, toBase64(img));
		values.put(DatabaseTransactions.AMOUNT, amount.toString());

		// Insert object into table and return ID
		long insertID = db.insert(DatabaseTransactions.DATABASE_TABLE_NAME, null, values);

		// Query for the newly inserted object
		Cursor cursor = db.query(DatabaseTransactions.DATABASE_TABLE_NAME, columns, DatabaseTransactions.ROW_ID + " = '" + insertID + "'", null, null, null,
				null);
		
		cursor.moveToFirst();
		Transaction newTransaction = cursorToTransaction(cursor);
		
		// Cleanup
		cursor.close();
		close();

		/**
		 * Updates the list and also notifies about changed data.
		 * If performance is needed this should be remade to only add the recently
		 * instead of queing all data again.
		 * TODO [MID] Remake this!
		 */
		if (type.equals("Income")) {
			queryIncomeByUser(userID);
			mainActivity.getIncomeListViewAdapter().notifyDataSetChanged();
		} else if (type.equals("Expense")) {
			queryExpenseByUser(userID);
			mainActivity.getExpenseListViewAdapter().notifyDataSetChanged();
		}

		return newTransaction;
	}

	/**
	 * Returns transaction object from queing the database
	 * @param cursor
	 * @return
	 */
	private Transaction cursorToTransaction(Cursor cursor) {
		Transaction transaction = new Transaction();

		try {
			transaction.setId((long) cursor.getInt(0));
			transaction.setUserID(cursor.getString(1));
			transaction.setType(cursor.getString(2));
			transaction.setCategory(Category.valueOf(cursor.getString(3)));
			transaction.setTitle(cursor.getString(4));
			transaction.setBarCode(cursor.getString(5));
			transaction.setDate(new Date(Long.parseLong(cursor.getString(6))));
			transaction.setImg(toBitmap(cursor.getString(7)));
			transaction.setAmount(Double.parseDouble(cursor.getString(8)));

		} catch (Exception e) {
			Log.e(TAG, e + "");
		}

		return transaction;
	}

	
	/**
	 * QueryIncome's by current user.
	 * TODO [MID] implement String USER_ID, Long DATE to sort through
	 * @param USER_ID
	 * @return
	 */
	public ArrayList<Transaction> queryIncomeByUser(String USER_ID) {
		Log.i(TAG, "Running queryIncomeByUser: " + USER_ID);
		open();
		Cursor cursor = db.rawQuery("select * from " + DatabaseTransactions.DATABASE_TABLE_NAME + " where " + DatabaseTransactions.USER_ID + "='" + USER_ID
				+ "' AND " + DatabaseTransactions.TYPE + " = 'Income'", null);

		incomeTransactions = new ArrayList<Transaction>();
		while (cursor.moveToNext()) {
			incomeTransactions.add(cursorToTransaction(cursor));
		}
		// Cleanup
		cursor.close();
		close();

		return incomeTransactions;
	}

	/**
	 * QueryExpenses by current user.
	 * TODO [MID] implement String USER_ID, Long DATE to sort through
	 * @param USER_ID
	 * @return
	 */
	public ArrayList<Transaction> queryExpenseByUser(String USER_ID) {
		Log.i(TAG, "Running queryExpenseByUser: " + USER_ID);
		open();
		Cursor cursor = db.rawQuery("select * from " + DatabaseTransactions.DATABASE_TABLE_NAME + " where " + DatabaseTransactions.USER_ID + "='" + USER_ID
				+ "' AND " + DatabaseTransactions.TYPE + " = 'Expense'", null);

		expenseTransactions = new ArrayList<Transaction>();
		while (cursor.moveToNext()) {
			transaction = cursorToTransaction(cursor);
			expenseTransactions.add(transaction);
		}
		// Cleanup
		cursor.close();
		close();

		return expenseTransactions;
	}

	/**
	 * Query Expenses and get Categories and total SUM
	 * @param USER_ID
	 * @return
	 */
	public ArrayList<String> querySumExpensesByUser(String USER_ID) {
		Log.i(TAG, "Running querySumExpensesByUser: " + USER_ID);
		open();
		Cursor cursor = db.rawQuery("select " + DatabaseTransactions.CATEGORY + ", sum(" + DatabaseTransactions.AMOUNT + ") from "
				+ DatabaseTransactions.DATABASE_TABLE_NAME + " where " + DatabaseTransactions.USER_ID + "= '" + USER_ID + "' AND " + DatabaseTransactions.TYPE
				+ " = 'Expense'" + "group by " + DatabaseTransactions.CATEGORY, null);

		ArrayList<String> expenseSum = new ArrayList<String>();
		while (cursor.moveToNext()) {
			expenseSum.add(cursor.getString(0) + "," + cursor.getString(1));
		}
		cursor.close();
		close();
		return expenseSum;
	}

	/**
	 * Query Incomes and get Categories and total SUM
	 * @param USER_ID
	 * @return
	 */
	public ArrayList<String> querySumIncomeByUser(String USER_ID) {
		Log.i(TAG, "Running querySumIncomeByUser: " + USER_ID);
		open();
		Cursor cursor = db.rawQuery("select " + DatabaseTransactions.CATEGORY + ", sum(" + DatabaseTransactions.AMOUNT + ") from "
				+ DatabaseTransactions.DATABASE_TABLE_NAME + " where " + DatabaseTransactions.USER_ID + "= '" + USER_ID + "' AND " + DatabaseTransactions.TYPE
				+ " = 'Income'" + "group by " + DatabaseTransactions.CATEGORY, null);

		ArrayList<String> incomeSum = new ArrayList<String>();
		while (cursor.moveToNext()) {
			incomeSum.add(cursor.getString(0) + "," + cursor.getString(1));
		}
		cursor.close();
		close();
		return incomeSum;
	}
	
	/**
	 * @param bitmap
	 * @return converting bitmap and return a string
	 */

	private String toBase64(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = Base64.encodeToString(b, Base64.DEFAULT);

		return temp;

	}

	/**
	 * @param encodedString
	 * @return bitmap (from given string)
	 */
	private Bitmap toBitmap(String encodedString) {

		try {
			byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
			return bitmap;
		} catch (Exception e) {
			e.getMessage();

			return null;
		}
	}
}
