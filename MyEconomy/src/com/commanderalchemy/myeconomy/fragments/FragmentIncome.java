package com.commanderalchemy.myeconomy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.commanderalchemy.myeconomy.R;
import com.commanderalchemy.myeconomy.aktivities.MainActivity;

/**
 * Fragment Income
 * 
 * @author Artur Olech
 * 
 */
public class FragmentIncome extends Fragment implements OnItemClickListener {
	// Debug tag
	private static final String TAG = "FragmentInkomster";

	private View view;
	private ListView listView;
	private MainActivity mainActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_income, container, false);
		mainActivity = (MainActivity) getActivity();
		setHasOptionsMenu(true);
		initListView();

		return view;
	}

	/**
	 * Init the ListView
	 */
	private void initListView() {
		listView = (ListView) view.findViewById(R.id.listView);
		registerForContextMenu(listView);
		listView.setAdapter(mainActivity.getIncomeListViewAdapter());
		listView.setOnItemClickListener(this);
	}

	/**
	 * Override the default menu and add a button
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_income, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Create ContextMenu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = mainActivity.getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	/**
	 * When user clicks on an item in the list
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO [LOW] DetailsView
	}

	/**
	 * When a user longclicks on an item, open contextmenu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.context_action_edit:
			editNote(info.id);
			return true;
		case R.id.context_action_delete:
			deleteNote(info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void deleteNote(long id) {
		Log.i(TAG, "ID: " + id);

	}

	private void editNote(long id) {
		Log.i(TAG, "ID: " + id);

	}
}