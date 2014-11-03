package com.commanderalchemy.myeconomy.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.commanderalchemy.myeconomy.R;
import com.commanderalchemy.myeconomy.aktivities.MainActivity;
import com.commanderalchemy.myeconomy.database.Category;

/**
 * Fragment Add Income/Expense Dialog
 * @author Artur Olech
 *
 */
public class FragmentAlertDialog extends DialogFragment {
	// Debug
	private static final String TAG = "TransactionDialog";
	
	// Camera ID
	private static final int CAMERA_PHOTO = 1000;
	
	// Controller
	private MainActivity mainActivity;

	// Layout
	private View layout;
	private LayoutInflater inflater;
	
	// Visability
	private static final int VISIBLE = 0;
	private static final int GONE = 8;

	// Dialog mode
	private static final int FRAGMENT_INCOME = 0;
	private static final int FRAGMENT_EXPENSE = 1;
	private int dialogMode;
	
	// Dialog
	private Builder builder;
	private Spinner spinner;

	private EditText title, amount;
	private Button btnCamera;
	private Bitmap picture = null;

	private ArrayList<String> category = new ArrayList<String>();

	/**
	 * FragmentAlertDialog 
	 * Instance as Fragment_Income or Fragment_Expense
	 * @param num
	 * @return
	 */
	public FragmentAlertDialog newInstance(int num) {
		FragmentAlertDialog dialog = new FragmentAlertDialog();
		Bundle bundle = new Bundle();
		bundle.putInt("num", num);
		dialog.setArguments(bundle);
		return dialog;
	}

	/**
	 * OnCreate
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialogMode = getArguments().getInt("num");

		// Prepare ze View
		inflater = getActivity().getLayoutInflater();
		layout = inflater.inflate(R.layout.fragment_dialog_budget, null);
		builder = new AlertDialog.Builder(getActivity());

		// Get Reference
		this.mainActivity = (MainActivity) getActivity();
		this.spinner = (Spinner) layout.findViewById(R.id.dialog_budget_SpinnerCategory);
		this.title = (EditText) layout.findViewById(R.id.dialog_budget_textViewTitleInput);
		this.amount = (EditText) layout.findViewById(R.id.dialog_budget_textViewBeloppInput);
		this.btnCamera = (Button) layout.findViewById(R.id.dialog_budget_btnCamera);

		// If its created by Inkomst
		if (dialogMode == FRAGMENT_INCOME) {
			builder.setTitle("Add Income");
			title.setHint("income");

			// Spinner
			category.add(Category.LÖN.toString());
			category.add(Category.ANNAT.toString());

		} else if (dialogMode == FRAGMENT_EXPENSE) {
			builder.setTitle("Add Expense");
			title.setHint("expense");

			// Spinner
			category.add(Category.NÖJE.toString());
			category.add(Category.LIVSMEDEL.toString());
			category.add(Category.RESA.toString());
			category.add(Category.HÄLSA.toString());
			category.add(Category.BOENDE.toString());
			Collections.sort(category);
		}

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, category);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		// Dialog buttons
		btnCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, CAMERA_PHOTO);

			}
		});

		// Spinner Listener
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				if (parent.getItemAtPosition(pos).equals(Category.LÖN.toString()) || parent.getItemAtPosition(pos).equals(Category.BOENDE.toString())) {
					layout.findViewById(R.id.dialog_budget_textViewTitleInput).setVisibility(GONE);
					layout.findViewById(R.id.dialog_budget_textViewTitlelbl).setVisibility(GONE);
				} else {
					layout.findViewById(R.id.dialog_budget_textViewTitleInput).setVisibility(VISIBLE);
					layout.findViewById(R.id.dialog_budget_textViewTitlelbl).setVisibility(VISIBLE);
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
				// Create a dummypicture if phone == Sony Xperia Z
				if (picture == null) {
					picture = BitmapFactory.decodeResource(getResources(), R.drawable.navigation_next_item);
				}
				try {
					mainActivity.newTransaction(dialogMode, Category.valueOf(spinner.getSelectedItem().toString()), title.getText().toString(), "000",
							new Date(), picture, Double.parseDouble(amount.getText().toString()));
				} catch (Exception e) {
					Log.e(TAG, e + "");
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

		// If there are saved instances reload them.
		if (savedInstanceState != null) {
			title.setText(savedInstanceState.getString("title"));
			amount.setText(savedInstanceState.getString("amount"));
		}

		return builder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putString("title", title.getText().toString());
		bundle.putString("amount", amount.getText().toString());
	}

	/**
	 * Result from the camera application
	 * At this moment the Sony Xperia Z Camera does not return a RESuLT_OK.
	 * TODO [LOW] Detect Sony Xperia Z and find another way?
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PHOTO) {
			Log.i(TAG, "Photo Taken");
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Result OK");
				Bitmap image = (Bitmap) data.getExtras().get("data");
				picture = image;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}