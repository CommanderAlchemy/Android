package com.commanderalchemy.myeconomy.aktivities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.commanderalchemy.myeconomy.R;
import com.commanderalchemy.myeconomy.adapters.ExpenseListViewAdapter;
import com.commanderalchemy.myeconomy.adapters.IncomeListViewAdapter;
import com.commanderalchemy.myeconomy.adapters.SectionsPagerAdapter;
import com.commanderalchemy.myeconomy.database.Category;
import com.commanderalchemy.myeconomy.database.DatabaseController;
import com.commanderalchemy.myeconomy.fragments.FragmentAddBudget;
import com.commanderalchemy.myeconomy.fragments.FragmentAlertDialog;
import com.commanderalchemy.myeconomy.fragments.FragmentExpense;
import com.commanderalchemy.myeconomy.fragments.FragmentIncome;
import com.commanderalchemy.myeconomy.fragments.FragmentLoginDialog;
import com.commanderalchemy.myeconomy.fragments.FragmentSummarum;

public class MainActivity extends FragmentActivity {
	
	// Debug tag
	private static final String TAG = "MainActivity";
	
	// Database Controller
	private DatabaseController dbController;
	
	// Transaction Fragments
	private static final int FRAGMENT_INCOME = 0;
	private static final int FRAGMENT_EXPENSE = 1;

	// Setttings
	private String userID;
	private static final String PREFS_NAME = "MyEconomyState";
	private static SharedPreferences settings;
	private boolean isLoggedIn, isDialogLaunched = false;

	// Fragments
	private static FragmentSummarum fragmentSummarum;
	private static FragmentIncome fragmentIncome;
	private static FragmentExpense fragmentExpense;

	// Adapters
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private IncomeListViewAdapter incomeListViewAdapter;
	private ExpenseListViewAdapter expenseListViewAdapter;
	
	private ViewPager mViewPager;

	/**
	 * MainActivity onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initSettings(savedInstanceState);
		initLogin();
		initDatabase();
		initAdapters();

	}
	/**
	 * Inits application state
	 * 
	 * @param savedInstanceState
	 */
	public void initSettings(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			isDialogLaunched = savedInstanceState.getBoolean("isDialogLaunched");
		}
		
		settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		isLoggedIn = settings.getBoolean("loggedIn", false);
		userID = settings.getString("userID", "null");
	}
	
	
	/**
	 * Check if user is logged in and if not launch loginDialog
	 */
	private void initLogin() {
		Log.i(TAG, "loggedIn : " + isLoggedIn);
		if (!isLoggedIn && !isDialogLaunched) {
			new FragmentLoginDialog().show(getFragmentManager(), TAG);
			isDialogLaunched = true;
		}
	}
	
	/**
	 * Inits database and loads all needed data
	 */
	public void initDatabase() {
		dbController = new DatabaseController(this);
		dbController.loadTransactions(userID);
		dbController.querySumExpensesByUser(userID);
	}
	
	
	/**
	 * Inits the adapters needed for the income and expense fragments.
	 */
	public void initAdapters() {
		incomeListViewAdapter = new IncomeListViewAdapter(this, dbController);
		expenseListViewAdapter = new ExpenseListViewAdapter(this, dbController);
	}

	/**
	 * OnResume
	 */
	@Override
	protected void onResume() {
		super.onResume();
		initFragments();
	}
	

	/**
	 * Loads all fragments.
	 */
	private void initFragments() {
		// Create and populate fragment list.
		fragmentSummarum = new FragmentSummarum();
		fragmentIncome = new FragmentIncome();
		fragmentExpense = new FragmentExpense();
	
		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(fragmentSummarum);
		fragments.add(fragmentIncome);
		fragments.add(fragmentExpense);

		// Create Adapter
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this, fragments);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	/**
	 * Creates the stock settings menu and runs the CreateMenu that creates the custom menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		CreateMenu(menu);
		return true;
	}

	/**
	 * Creates the custom menu in the actionbar.
	 * @param menu
	 */
	private void CreateMenu(Menu menu) {
		menu.setQwertyMode(true);
		MenuItem aMenu1 = menu.add(0, 0, 0, "Logout");
		aMenu1.setAlphabeticShortcut('a');
	}

	/**
	 * On ActionMenu Select Do something when that get selected in the ActionMenu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Item: " + item.toString() + "\nID: " + item.getItemId() + "\nIntent: " + item.getIntent());

		switch (item.getItemId()) {
		case 0:
			new FragmentLoginDialog().show(getFragmentManager(), TAG);
			break;

		case R.id.action_addIncome:
			new FragmentAlertDialog().newInstance(FRAGMENT_INCOME).show(getFragmentManager(), TAG);
			break;

		case R.id.action_addExpense:
			new FragmentAlertDialog().newInstance(FRAGMENT_EXPENSE).show(getFragmentManager(), TAG);
			break;

		case R.id.action_add_budget:
			new FragmentAddBudget().show(getFragmentManager(), TAG);

		default:
			// TODO [LOW] Implement settings fragment
			// startActivity(new Intent(this, SettingsActivity.class));break;
		}
		return false;
	}

	/**
	 * ragmentSummarum.initChart(this); Save things here
	 */
	@Override
	protected void onSaveInstanceState(Bundle onSaveInstanceState) {
		super.onSaveInstanceState(onSaveInstanceState);
		onSaveInstanceState.putBoolean("isDialogLaunched", isDialogLaunched);
	}

	public void newTransaction(int transaction, Category category, String title, String barCode, Date date, Bitmap img, Double ammount) {
		String userID = settings.getString("userID", "null");
		String type = null;

		// Depending on what fragment it's defined what type it will be.
		switch (transaction) {
		case FRAGMENT_INCOME:
			type = "Income";
			break;

		case FRAGMENT_EXPENSE:
			type = "Expense";
			break;
		}

		// Debugging before it gets inputed
		Log.i(TAG, "Before Transaction:\n Income: " + dbController.getIncomeTransactions().size() + "\n Expenses: "
				+ dbController.getExpenseTransactions().size());
		
		if (!userID.equals("null"))
			dbController.createTransaction(userID, type, category, title, barCode, date, img, ammount);
		else
			Toast.makeText(this, "userid == null", Toast.LENGTH_LONG).show();
		
		updateLegend();

		// Debugging after it gets inputed
		Log.i(TAG, "After Transaction:\n Income: " + dbController.getIncomeTransactions().size() + "\n Expenses: "
				+ dbController.getExpenseTransactions().size());
	}

	/**
	 * Returns ArrayList<STRING> with all the expenses and Categories
	 * @return
	 */
	public ArrayList<String> getExpenses() {
		ArrayList<String> chart = new ArrayList<String>();
		chart = dbController.querySumExpensesByUser(userID);
		chart.add("Unused Cash," + (getBudgetGoal() - getTotalExpenses()));

		return chart;
	}

	/**
	 * Returns the total income 
	 * @return Double
	 */
	public double getTotalIncome() {
		double totalIncome = 0;

		ArrayList<String> income = new ArrayList<String>();
		income = dbController.querySumIncomeByUser(userID);

		for (String str : income) {
			totalIncome += Double.parseDouble(str.split(",")[1]);
		}

		return totalIncome;
	}

	/**
	 * Returns the total expense
	 * @return Double
	 */
	public double getTotalExpenses() {
		double totalExpense = 0;
		ArrayList<String> expenses = new ArrayList<String>();
		expenses = dbController.querySumExpensesByUser(userID);

		for (String str : expenses) {
			totalExpense += Double.parseDouble(str.split(",")[1]);
		}

		return totalExpense;

	}
	
	/**
	 * Returnes the Budget based on current TotalIncome and TotalExpenses
	 * @return Double
	 */
	public double getBudget() {
		return (getTotalIncome() - getTotalExpenses());
	}

	/**
	 * Returns BudgetGoal set by the user
	 * @return
	 */
	public double getBudgetGoal() {
		double budgetGoal = 0;
		settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		try {
			budgetGoal = Double.parseDouble(settings.getString("budgetGoal", "null"));
		} catch (Exception e) {
			Log.e(TAG, e + "");
		}
		return budgetGoal;
	}

	/**
	 * Returns the current Budget status
	 * @return Double
	 */
	public double getBudgetStatus() {
		return (getBudgetGoal() - getTotalExpenses());
	}

	/**
	 * Edit Transactions
	 */
	public void editTransaction() {
		// TODO [LOW] implement edit
	}

	/**
	 * Delete Transactions
	 */
	public void deleteTransaction() {
		// TODO [LOW] implement delete
	}

	/**
	 * Returns the IncomeListAdapter
	 * @return adapter
	 */
	public IncomeListViewAdapter getIncomeListViewAdapter() {
		return this.incomeListViewAdapter;
	}

	/**
	 * Returns the ExpenseListAdapter
	 * @return adapter
	 */
	public ExpenseListViewAdapter getExpenseListViewAdapter() {
		return this.expenseListViewAdapter;
	}

	/**
	 * Add Budget
	 * @param budget
	 */
	public void addBudget(String budget) {
		settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		settings.edit().putString("budgetGoal", budget).commit();
		
		// Update Budget and repaint the new graph
		fragmentSummarum.setTextViewBudgetGoal(budget, this);
		fragmentSummarum.setTextViewBudgetStatus(getBudgetStatus()+ "", this);
		fragmentSummarum.repaint(this);
		updateLegend();
	}
	
	/**
	 * Updates legend on SummaSumarum page.
	 */
	public void updateLegend(){
		fragmentSummarum.setTextViewIncome(getTotalIncome() + "");
		fragmentSummarum.setTextViewExpense(getTotalExpenses() + "");
		fragmentSummarum.setTextViewBudgetStatus(getBudgetStatus()+ "", this);
	}
}
