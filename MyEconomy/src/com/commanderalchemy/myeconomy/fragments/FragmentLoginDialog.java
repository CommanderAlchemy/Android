package com.commanderalchemy.myeconomy.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.commanderalchemy.myeconomy.R;

/**
 * Fragment Login Dialog, handles the login and user info.
 * @author Artur Olech
 *
 */
public class FragmentLoginDialog extends DialogFragment {
	// Debug
	private static final String TAG = "LoginDialog";
	
	// Layout
	private View layout;
	private LayoutInflater inflater;


	// Dialog mode
	private static final String PREFS_NAME = "MyEconomyState";
	private static SharedPreferences settings;
	private SharedPreferences.Editor editor;

	private boolean isLoggedIn;

	// Visability
	private static final int VISIBLE = 0;
	private static final int GONE = 8;
	private static final int MODE_PRIVATE = 0;

	// Dialog
	private Builder builder;
	private EditText firstname, lastname, userID, password;
	private TextView confirmText;

	/**
	 * OnCreate
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get sharedprefereces
		settings = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		editor = settings.edit();
		isLoggedIn = settings.getBoolean("loggedIn", false);

		// Prepare ze View
		inflater = getActivity().getLayoutInflater();
		layout = inflater.inflate(R.layout.fragment_dialog_login, null);

		// Get Referneces
		firstname = (EditText) layout.findViewById(R.id.dialog_login_editTextUserFirstNameInput);
		lastname = (EditText) layout.findViewById(R.id.dialog_login_editTextUserLastNameInput);
		userID = (EditText) layout.findViewById(R.id.dialog_login_editTextUserIDInput);
		password = (EditText) layout.findViewById(R.id.dialog_login_editTextUserPasswordInput);
		confirmText = (TextView) layout.findViewById(R.id.dialog_login_editTextViewLogoutText);

		if (!isLoggedIn)
			this.setCancelable(false);
		else
			this.setCancelable(true);

		builder = new AlertDialog.Builder(getActivity());

		if (!isLoggedIn) {
			// Om urloggad
			builder.setTitle("Logga In");

			// Lägg till login knapp
			builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Lägg in värden till sharedpref från dialogen.
					editor.putString("userName", firstname.getText().toString() + " " + lastname.getText().toString());
					editor.putString("userID", userID.getText().toString());
					editor.putString(userID.getText().toString(), password.getText().toString());
					editor.putBoolean("loggedIn", true);
					editor.commit();

					// Hämta värden som nyligen sparats.
					settings = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

					Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

					Log.i(TAG, "UserID : " + settings.getString("userID", "null"));
					Log.i(TAG, "Password : " + settings.getString(userID.getText().toString(), "null"));
				}
			});
		}

		if (isLoggedIn) {
			// Om inloggad..
			builder.setTitle("Logga ut?");
			confirmText.setVisibility(VISIBLE);
			firstname.setVisibility(GONE);
			lastname.setVisibility(GONE);
			userID.setVisibility(GONE);
			password.setVisibility(GONE);
			// Lägg till
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Ta bort värden från sharedpref
					editor.putString("userID", "null");
					editor.putBoolean("loggedIn", false);
					editor.commit();
					getActivity().finish();
				}
			});
		}

		builder.setView(layout);

		// If there are saved instances reload them.
		if (savedInstanceState != null) {
			// TODO [LOW] restore username/password
			// title.setText(savedInstanceState.getString("title"));
			// belopp.setText(savedInstanceState.getString("belopp"));
		}
		return builder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		// TODO [LOW] Save username/password
		// bundle.putString("title", title.getText().toString());
		// bundle.putString("belopp", belopp.getText().toString());
	}
}
