package com.commanderalchemy.myeconomy.fragments;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commanderalchemy.myeconomy.R;
import com.commanderalchemy.myeconomy.aktivities.MainActivity;

/**
 * Summa Summarum, shows information about budget and plots it.
 * @author Artur Olech
 *
 */
public class FragmentSummarum extends Fragment {
	// Debug tag
	private static final String TAG = "Summa";
	
	// Settings
	private SharedPreferences settings;
	private static final String PREFS_NAME = "MyEconomyState";
	private static final int MODE_PRIVATE = 0;
	
	// Controller
	private MainActivity mainActivity;
	
	private View view;

	// Graph
	private static DefaultRenderer mRenderer = new DefaultRenderer();
	private static CategorySeries data = new CategorySeries("Expenses");
	private  GraphicalView mChartView = null;

	// Data
	private ArrayList<String> expenses;
	
	// TextViews
	private TextView textViewName, textViewIncome, textViewExpense, textViewBudget, textViewBudgetGoal, textViewBudgetStatus;

	// Graph colors
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.YELLOW, Color.RED, Color.DKGRAY, Color.BLACK };


	/**
	 * OnCreate
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_summasummarum, null);
		setHasOptionsMenu(true);
		return view;
	}

	/**
	 * When the activity is created
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mainActivity = ((MainActivity) getActivity());
		initComponents(mainActivity);
		initChart(mainActivity);
	}

	/**
	 * Init Components
	 * @param mainActivity
	 */
	private void initComponents(MainActivity mainActivity) {
		settings = mainActivity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		this.textViewName = (TextView) mainActivity.findViewById(R.id.summa_summarum_textViewName);
		this.textViewIncome = (TextView) mainActivity.findViewById(R.id.summa_summarum_textViewIncome);
		this.textViewExpense = (TextView) mainActivity.findViewById(R.id.summa_summarum_textViewExpense);
		this.textViewBudget = (TextView) mainActivity.findViewById(R.id.summa_summarum_textViewBudget);
		this.textViewBudgetGoal = (TextView) mainActivity.findViewById(R.id.summa_summarum_textViewBudgetGoal);
		this.textViewBudgetStatus = (TextView) mainActivity.findViewById(R.id.summa_summarum_textViewBudgetStatus);

		textViewName.setText("Name: " + settings.getString("userName", "null"));
		textViewIncome.setText("Income: " + mainActivity.getTotalIncome() + "");
		textViewExpense.setText("Expense: " + mainActivity.getTotalExpenses() + "");
		textViewBudget.setText("Budget: " + mainActivity.getBudget() + "");
		textViewBudgetGoal.setText("Budget Goal: " + settings.getString("budgetGoal", "null"));
		textViewBudgetStatus.setText("Budget Status: " + mainActivity.getBudgetStatus() + "");

	}

	/**
	 * Init Chart (Pie chart)
	 * @param mainActivity
	 */
	public void initChart(MainActivity mainActivity) {
		mRenderer.setPanEnabled(false);
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setLabelsTextSize(45f);
		mRenderer.setLegendTextSize(45f);
		mRenderer.setFitLegend(true);
		mRenderer.setZoomEnabled(false);

		mChartView = ChartFactory.getPieChartView(mainActivity, data, mRenderer);

		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.summa_summarum_chart);
		layout.addView(mChartView);
		repaint(mainActivity);

	}

	/**
	 * Repaint the chart when new data is added
	 * @param mainActivity
	 */
	public void repaint(MainActivity mainActivity) {
		data.clear();
		mRenderer.removeAllRenderers();
		expenses = mainActivity.getExpenses();

		for (int i = 0; i < expenses.size(); i++) {

			data.add(expenses.get(i).split(",")[0], Double.parseDouble(expenses.get(i).split(",")[1]));
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(data.getItemCount() - 1) % COLORS.length]);
			renderer.setDisplayChartValues(true);
			renderer.setShowLegendItem(true);
			renderer.setChartValuesTextSize(500f);
			mRenderer.addSeriesRenderer(renderer);

		}
	}

	/**
	 * Override the default menu and add a button
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_summarum, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Set Income text
	 * @param income
	 */
	public void setTextViewIncome(String income) {
		textViewIncome.setText("Income: " + income);
	}

	/**
	 * Set Expense text
	 * @param expese
	 */
	public void setTextViewExpense(String expese) {
		textViewExpense.setText("Expense: " + expese);
	}

	/**
	 * Set Budget Text
	 * @param budget
	 */
	public void setTextViewBudget(String budget) {
		textViewBudget.setText("Budget: " + budget);
	}

	/**
	 * Set BudgetGoal Text
	 * @param budgetGoal
	 * @param mainActivity
	 * TODO [HIGH] Get the frakking clue why this is null sometimes (Even when activity is created)
	 */
	public void setTextViewBudgetGoal(String budgetGoal, MainActivity mainActivity) {
		if (textViewBudgetGoal == null)
			initComponents(mainActivity);
		
		textViewBudgetGoal.setText("Budget Goal: " + budgetGoal);
	}

	/**
	 * Set BugetStatus Text
	 * @param budgetStatus
	 * @param mainActivity
	 * TODO [HIGH] Get the frakking clue why this is null sometimes (Even when activity is created)
	 */
	public void setTextViewBudgetStatus(String budgetStatus, MainActivity mainActivity) {
		if (textViewBudgetStatus == null)
			initComponents(mainActivity);

		textViewBudgetStatus.setText("Budget Status: " + budgetStatus);
	}

}
