package com.francisouellet.covoiturageexpress.adapters;

import java.net.URI;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import com.francisouellet.covoiturageexpress.R;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.util.JsonParser;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

public class ParcoursDetailPagerAdapter extends FragmentPagerAdapter{
	
	private Parcours m_Parcours;
	private Utilisateur m_Utilisateur;
	
	public static final String ARG_PARCOURS = "parcours";
	public static final String ARG_UTILISATEUR = "utilisateur";
	
	public ParcoursDetailPagerAdapter(FragmentManager fm, Parcours p_Parcours, Utilisateur p_Utilisateur) {
		super(fm);
		this.m_Parcours = p_Parcours;
		this.m_Utilisateur = p_Utilisateur;
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = new Fragment();
		if(arg0 == 0){
			fragment = new ParcoursDetailFragment();
			Bundle args = new Bundle();
			args.putSerializable(ParcoursDetailPagerAdapter.ARG_PARCOURS, m_Parcours);
			fragment.setArguments(args);
		}
		else if(arg0 == 1){
			fragment = new ParcoursDetailParticipantsFragment();
			Bundle args = new Bundle();
			args.putSerializable(ParcoursDetailPagerAdapter.ARG_PARCOURS, m_Parcours);
			args.putSerializable(ParcoursDetailPagerAdapter.ARG_UTILISATEUR, m_Utilisateur);
			fragment.setArguments(args);
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	public static class ParcoursDetailFragment extends Fragment{
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			
			View racine = inflater.inflate(R.layout.fragment_parcours_detail, container, false);
					
			Parcours parcours = (Parcours)getArguments().getSerializable(ParcoursDetailPagerAdapter.ARG_PARCOURS);
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
			
			if(parcours.getAdresseDepart() != null)
				((TextView)racine.findViewById(R.id.fragment_parcours_detail_adresse_depart)).setText(
						parcours.getAdresseDepart());
			else
				((TextView)racine.findViewById(R.id.fragment_parcours_detail_adresse_depart)).setText(
					Util.obtenirAdresse(parcours.getDepartLatitude(), 
							parcours.getDepartLongitude(), getActivity()));
			
			if(parcours.getAdresseDestination() != null)
				((TextView)racine.findViewById(R.id.fragment_parcours_detail_adresse_destination)).setText(
						parcours.getAdresseDestination());
			else
				((TextView)racine.findViewById(R.id.fragment_parcours_detail_adresse_destination)).setText(
					Util.obtenirAdresse(parcours.getDestinationLatitude(),
							parcours.getDestinationLongitude(), getActivity()));
			
			((TextView)racine.findViewById(R.id.fragment_parcours_detail_notes)).setText(
					parcours.getNotes());
			
			return racine;
		}
	}
	
	public static class ParcoursDetailParticipantsFragment extends Fragment{
		
		private ExpandableListView m_ListeParcours;
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				ViewGroup container,
				Bundle savedInstanceState) {
			
			Parcours parcours = (Parcours)getArguments().getSerializable(ParcoursDetailPagerAdapter.ARG_PARCOURS);
			Utilisateur utilisateur = (Utilisateur)getArguments().getSerializable(ParcoursDetailPagerAdapter.ARG_UTILISATEUR);
			
			View racine = inflater.inflate(R.layout.fragment_parcours_participants, container, false);
			m_ListeParcours = (ExpandableListView)racine.findViewById(R.id.liste_groupes_participants);
			
			new AsyncObtenirParticipants(getActivity(),utilisateur ,parcours, m_ListeParcours).execute();
			
			return racine;
		}
	}
	
	private static class AsyncObtenirParticipants extends AsyncTask<Void, Void, List<List<Parcours>>>{
		
		private Exception m_Exception;
		private Context m_Context;
		private Utilisateur m_Utilisateur;
		private Parcours m_Parcours;
		private ExpandableListView m_ListeParcours;
		
		public AsyncObtenirParticipants(Context p_Context, Utilisateur p_Utilisateur, Parcours p_Parcours,
				ExpandableListView p_ListeParcours) {
			this.m_Context = p_Context;
			this.m_Utilisateur = p_Utilisateur;
			this.m_Parcours = p_Parcours;
			this.m_ListeParcours = p_ListeParcours;
		}
		
		@Override
		protected List<List<Parcours>> doInBackground(Void... params) {
			
			List<List<Parcours>> resultat = new ArrayList<List<Parcours>>();
			
			try{
				
				URI uri = new URI("http", Util.WEB_SERVICE, 
						Util.REST_UTILISATEURS + "/" + m_Utilisateur.getCourriel() +
						Util.REST_PARCOURS + "/" + m_Parcours.getId() +
						Util.REST_PARTICIPANTS, null, null);
				
				HttpGet get = new HttpGet(uri);
				String body = new DefaultHttpClient().execute(get, new BasicResponseHandler());
				
				JSONArray resultatJSON = new JSONArray(body);
				for(int i = 0; i < resultatJSON.length(); i++){
					JSONArray participantsJSON = resultatJSON.getJSONArray(i);
					List<Parcours> participants = new ArrayList<Parcours>();
					for(int j = 0; j < participantsJSON.length(); j++){
						participants.add(JsonParser.ToParcours(participantsJSON.getJSONObject(j)));
					}
					resultat.add(participants);
				}
				
			}catch(Exception e){this.m_Exception = e; e.printStackTrace();}
			return resultat;
		}
		
		@Override
		protected void onPostExecute(List<List<Parcours>> result) {
			if(m_Exception == null && result != null && result.size() > 0){
				
				List<String> entetes = new ArrayList<String>();
				for(int i = 0; i < result.size(); i++)
					entetes.add("Covoiturage " + (i + 1));
				
				ParticipantsExpendableListAdapter m_Adapter = new ParticipantsExpendableListAdapter(this.m_Context, entetes, result);
				m_ListeParcours.setAdapter(m_Adapter);
			}else{
				
			}
				
		}
	}
	
}
