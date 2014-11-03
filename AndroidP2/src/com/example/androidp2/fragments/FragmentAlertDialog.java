package com.example.androidp2.fragments;

import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.androidp2.*;

/**
 * Fragment Register.
 * Handles the input from user about user information
 * and to register user to a group.
 * 
 * @author Artur Olech
 *
 */
public class FragmentAlertDialog extends DialogFragment {
	// Debug
	private static final String TAG = "DialogFragment";

	// Controller
	private MainActivity mainActivity;

	// Layout
	private View layout;
	private LayoutInflater inflater;

	// Visability
	private static final int VISIBLE = 0;
	private static final int GONE = 8;

	// Dialog
	private Builder builder;
	private Spinner spinner;

	// UI elements
	private EditText user_name, user_group;

	private ArrayList<String> groups = new ArrayList<String>();

	/**
	 * FragmentAlertDialog Instance as Fragment_Income or Fragment_Expense DEPRICATED
	 * 
	 * @param num
	 * @return
	 *
	 */
	public FragmentAlertDialog newInstance(ArrayList<String> groups) {
		FragmentAlertDialog dialog = new FragmentAlertDialog();

		Bundle bundle = new Bundle();
		bundle.putStringArrayList("groups", groups);
		dialog.setArguments(bundle);
		return dialog;
	}

	/**
	 * OnCreate
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Prepare ze View
		inflater = getActivity().getLayoutInflater();
		layout = inflater.inflate(R.layout.fragment_dialog_register, null);
		builder = new AlertDialog.Builder(getActivity());
		Bundle bundle = getArguments();

		// Get Reference
		this.mainActivity = (MainActivity) getActivity();
		this.spinner = (Spinner) layout.findViewById(R.id.dialog_register_SpinnerGroups);
		this.user_name = (EditText) layout.findViewById(R.id.dialog_register_textViewUserName);
		this.user_group = (EditText) layout.findViewById(R.id.dialog_register_textViewUserGroup);
		this.groups = bundle.getStringArrayList("groups");

		
		// If there are saved instances reload them.
		if (savedInstanceState != null) {
			user_name.setText(savedInstanceState.getString("user_name"));
			user_group.setText(savedInstanceState.getString("user_group"));
			groups = savedInstanceState.getStringArrayList("groups");
		}

		// Builder
		builder.setTitle("Register");

		// Sort
		Collections.sort(groups);

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, groups);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		// Spinner Listener
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				if (parent.getItemAtPosition(pos).equals("New Group")) {
					layout.findViewById(R.id.dialog_register_textViewGrouplbl).setVisibility(VISIBLE);
					layout.findViewById(R.id.dialog_register_textViewUserGroup).setVisibility(VISIBLE);
				} else {
					layout.findViewById(R.id.dialog_register_textViewGrouplbl).setVisibility(GONE);
					layout.findViewById(R.id.dialog_register_textViewUserGroup).setVisibility(GONE);
					user_group.setText(spinner.getSelectedItem().toString());
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// Set Positive
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mainActivity != null) {
					Log.d(TAG, "Selected: " + spinner.getSelectedItem().toString() + ": " + user_group.getText().toString() + ", " + user_name.getText().toString());
					mainActivity.updateUserInfo(user_name.getText().toString(), user_group.getText().toString());
				} else {
					Log.d(TAG, "MainActivity null");
				}

			}
		});

		// Set Negative
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		builder.setView(layout);
		return builder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putString("user_name", user_name.getText().toString());
		bundle.putString("user_group", user_group.getText().toString());
		bundle.putStringArrayList("groups", groups);
	}
}