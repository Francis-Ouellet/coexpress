package com.francisouellet.covoiturageexpress.adapters;

import java.util.GregorianCalendar;

import com.francisouellet.covoiturageexpress.R;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.util.Util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ParcoursDetailPagerAdapter extends FragmentPagerAdapter{
	
	private Parcours m_Parcours;
	
	public ParcoursDetailPagerAdapter(FragmentManager fm, Parcours p_Parcours) {
		super(fm);
		this.m_Parcours = p_Parcours;
		}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = new Fragment();
		if(arg0 == 0){
			fragment = new ParcoursDetailFragment();
			Bundle args = new Bundle();
			args.putSerializable(ParcoursDetailFragment.ARG_PARCOURS, m_Parcours);
			fragment.setArguments(args);
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	public static class ParcoursDetailFragment extends Fragment{
		
		public static final String ARG_PARCOURS = "parcours";
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			
			View racine = inflater.inflate(R.layout.fragment_parcours_detail, container, false);
					
			Parcours parcours = (Parcours)getArguments().getSerializable(ParcoursDetailFragment.ARG_PARCOURS);
			GregorianCalendar calendrier = new GregorianCalendar();
			calendrier.setTimeInMillis(Long.parseLong(parcours.getTimestampDepart()));
			
			((TextView)racine.findViewById(R.id.fragment_parcours_detail_date_depart))
				.setText(Util.dateFormat.format(calendrier.getTime()));
			((TextView)racine.findViewById(R.id.fragment_parcours_detail_heure_depart))
				.setText(Util.timeFormat.format(calendrier.getTime()));
			
			// Conducteur
			if(parcours.getConducteur()){
				if(parcours.getNbPlaces() > 1)
					((TextView)racine.findViewById(R.id.fragment_parcours_detail_nb_places)).setText(
							parcours.getNbPlaces() + " " + 
							getString(R.string.parcours_item_places_disponibles));
				else
					((TextView)racine.findViewById(R.id.fragment_parcours_detail_nb_places)).setText(
							parcours.getNbPlaces() + " " + 
							getString(R.string.parcours_item_place_disponible));
			}
			// Passager
			else{
				if(parcours.getNbPlaces() > 1)
					((TextView)racine.findViewById(R.id.fragment_parcours_detail_nb_places)).setText(
							getString(R.string.parcours_item_demande) + " " + 
							parcours.getNbPlaces() + " " +
							getString(R.string.parcours_item_places));
				else
					((TextView)racine.findViewById(R.id.fragment_parcours_detail_nb_places)).setText(
							getString(R.string.parcours_item_demande) + " " + 
							parcours.getNbPlaces() + " " +
							getString(R.string.parcours_item_place));
			}
			
			((TextView)racine.findViewById(R.id.fragment_parcours_detail_adresse_depart)).setText(
					parcours.getAdresseDepart());
			((TextView)racine.findViewById(R.id.fragment_parcours_detail_adresse_destination)).setText(
					parcours.getAdresseDestination());
			((TextView)racine.findViewById(R.id.fragment_parcours_detail_notes)).setText(
					parcours.getNotes());
			
			return racine;
		}
	}
	
}
