package com.commanderalchemy.myeconomy.adapters;

import java.util.List;
import java.util.Locale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.commanderalchemy.myeconomy.R;
import com.commanderalchemy.myeconomy.aktivities.MainActivity;

/**
 * SectionPagerAdapter, used to maintain the fragments
 * @author Artur Olech
 *
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragments;
	private MainActivity mainActivity;

	/**
	 * Constructor
	 * @param fm
	 * @param mainActivity
	 * @param fragments	
	 */
	public SectionsPagerAdapter(FragmentManager fm, MainActivity mainActivity, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
		this.mainActivity = mainActivity;
	}

	/**
	 * Returns the fragment in the given position
	 */
	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	/**
	 * Returns the fragments size (ammount of fragments)
	 */
	@Override
	public int getCount() {
		return fragments.size();
	}

	/**
	 * Returns the pagetitle from strings in uppercase
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:	return mainActivity.getString(R.string.title_section1);
		case 1:	return mainActivity.getString(R.string.title_section2);
		case 2:	return mainActivity.getString(R.string.title_section3);
		default: return mainActivity.getString(R.string.title_section_error);
		}
	}
}
