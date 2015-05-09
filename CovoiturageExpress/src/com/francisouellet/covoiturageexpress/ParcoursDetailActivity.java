package com.francisouellet.covoiturageexpress;

import com.francisouellet.covoiturageexpress.adapters.ParcoursDetailPagerAdapter;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Classe pour l'affichage détaillé d'un parcours et de ses participants sous la forme d'une vue avec des onglets
 * @author Francis Ouellet
 *
 */
public class ParcoursDetailActivity extends FragmentActivity {
	
	private Bundle mExtras;
	
	private ViewPager mViewPager;
	private ParcoursDetailPagerAdapter mAdapter;
	private Parcours mParcours;
	private Utilisateur mUtilisateur;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final ActionBar actionBar = getActionBar();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parcours_detail);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Récupération du parcours dans les extras
		mExtras = getIntent().getExtras();
		if(mExtras != null){
			mParcours = (Parcours)mExtras.getSerializable(Util.EXTRA_PARCOURS);
			mUtilisateur = (Utilisateur)mExtras.getSerializable(Util.EXTRA_UTILISATEUR);
		}
		
		// Définition des vues des onglets
		if(mParcours != null){
			
			mAdapter = new ParcoursDetailPagerAdapter(getSupportFragmentManager(), mParcours, mUtilisateur);
			
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
					
				}
				
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					mViewPager.setCurrentItem(tab.getPosition());
				}
				
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {}
			};
			
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.parcours_fragment_titre_parcours))
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.parcours_fragment_titre_participants))
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.parcours_fragment_titre_carte))
					.setTabListener(tabListener));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(mParcours != null)
			if(mParcours.getActif())
				getMenuInflater().inflate(R.menu.parcours_detail, menu);
			else
				getMenuInflater().inflate(R.menu.menu_vide, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.action_ajouter_personne){
			Intent i = new Intent(this, AjouterParticipantsActivity.class);
			i.putExtra(Util.EXTRA_PARCOURS, mParcours);
			i.putExtra(Util.EXTRA_UTILISATEUR, mUtilisateur);
			this.startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
