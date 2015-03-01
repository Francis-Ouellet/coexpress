package com.francisouellet.covoiturageexpress;

import com.francisouellet.covoiturageexpress.adapters.ParcoursDetailPagerAdapter;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class ParcoursDetailActivity extends FragmentActivity {
	
	private Bundle mExtras;
	
	private ViewPager mViewPager;
	private ParcoursDetailPagerAdapter mAdapter;
	private Parcours mParcours;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final ActionBar actionBar = getActionBar();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parcours_detail);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mExtras = getIntent().getExtras();
		if(mExtras != null)
			mParcours = (Parcours)mExtras.getSerializable(Util.EXTRA_PARCOURS);
		
		if(mParcours != null){
			mAdapter = new ParcoursDetailPagerAdapter(getSupportFragmentManager(), mParcours);
			
			mViewPager = (ViewPager)findViewById(R.id.pager_parcours_detail);
			mViewPager.setAdapter(mAdapter);
			mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
				@Override
				public void onPageSelected(int position) {
					getActionBar().setSelectedNavigationItem(position);
				}
			});
			
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			
			ActionBar.TabListener tabListener = new ActionBar.TabListener() {
				
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					mViewPager.setCurrentItem(tab.getPosition());
					
				}
				
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
					// TODO Auto-generated method stub
					
				}
			};
			
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.parcours_fragment_titre_parcours))
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.parcours_fragment_titre_passagers))
					.setTabListener(tabListener));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parcours_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}
}
