package com.commanderalchemy.myeconomy.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.commanderalchemy.myeconomy.R;
import com.commanderalchemy.myeconomy.database.DatabaseController;
import com.commanderalchemy.myeconomy.database.Transaction;

/**
 * Income List View Adapter, used to fetch information from the database
 * and show it on the listview. Only used for displaying expenses.
 * @author Artur Olech
 *
 */
public class IncomeListViewAdapter extends BaseAdapter {
	private Context context;
	private DatabaseController dbController;

	/**
	 * Constructor for Adapter. Needs Context and Database Controller.
	 * @param context
	 * @param dbController
	 */
	public IncomeListViewAdapter(Context context, DatabaseController dbController) {
		this.context = context;
		this.dbController = dbController;
	}
	
	/**
	 * Returns size of the current income list generated from Database.
	 */
	@Override
	public int getCount() {
		return dbController.getIncomeTransactions().size();
	}

	/**
	 * Returns Transaction item.
	 */
	@Override
	public Transaction getItem(int position) {
		return dbController.getIncomeTransactions().get(position);
	}

	/**
	 * Returns itemID that can be used to identify item in database.
	 */
	@Override
	public long getItemId(int position) {
		return dbController.getIncomeTransactions().get(position).getId();
	}

	/**
	 * Returns view, populates the listview with correct information.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		convertView = inflater.inflate(R.layout.list_row, null);
	
		// Get references.
		TextView category = (TextView) convertView.findViewById(R.id.list_row_textViewCategory);
		TextView title = (TextView) convertView.findViewById(R.id.list_row_textViewTitle);
		TextView date = (TextView) convertView.findViewById(R.id.list_row_textViewDate);
		TextView amount = (TextView) convertView.findViewById(R.id.list_row_textViewAmount);
		TextView sek = (TextView) convertView.findViewById(R.id.list_row_textViewSek);
		ImageView thumb_image = (ImageView) convertView.findViewById(R.id.list_row_list_image);
		
		// Set red for income.
		amount.setTextColor(Color.parseColor("#00C000"));
		sek.setTextColor(Color.parseColor("#00C000"));

		// Set information from database.
		category.setText(getItem(position).getCategory().toString());
		title.setText(getItem(position).getTitle().toString());
		date.setText(getItem(position).getDate().toString());
		amount.setText(getItem(position).getAmount().toString());
		thumb_image.setImageBitmap(getItem(position).getImg());
		
		return convertView;
	}

}
