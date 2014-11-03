package com.commanderalchemy.myeconomy.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.commanderalchemy.myeconomy.R;
import com.commanderalchemy.myeconomy.aktivities.MainActivity;

/**
 * Fragment Add Budget
 * This fragment handles the dialog that shows when the user want to add a BudgetGoal
 * @author Artur Olech
 *
 */
public class FragmentAddBudget extends DialogFragment {
	// Debug
	private static final String TAG = "Add Budget Dialog";
	
	// Controller
	private MainActivity mainActivity;

	// Layout
	private View layout;
	private LayoutInflater inflater;

	// Dialog
	private Builder builder;
	private EditText budget;

	/**
	 * OnCreate
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Prepare ze View
		inflater = getActivity().getLayoutInflater();
		layout = inflater.inflate(R.layout.fragment_add_budget, null);
		builder = new AlertDialog.Builder(getActivity());

		// Get Reference
		this.mainActivity = (MainActivity) getActivity();
		this.budget = (EditText) layout.findViewById(R.id.dialog_add_budget_editTextBudget);
		
		// Build Dialog
		builder.setTitle("Add Budget Goal");
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mainActivity.addBudget(budget.getText().toString());
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		builder.setView(layout);

		// If there are saved instances reload them.
		if (savedInstanceState != null) {
			budget.setText(savedInstanceState.getString("budgetGoal"));
		}

		return builder.create();
	}

	/**
	 * OnSave
	 * Save the budget inputed.
	 */
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putString("budgetGoal", budget.getText().toString());
	}
}