package com.francisouellet.covoiturageexpress.adapters;

import java.util.GregorianCalendar;
import java.util.List;

import com.francisouellet.covoiturageexpress.R;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.database.ParcoursDataSource;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class ParcoursAdapter extends ArrayAdapter<Parcours>{
	
	private Context m_Context;
	private List<Parcours> m_ListeParcours;
	private int m_LayoutResId;
	
	public ParcoursAdapter(Context p_Context, List<Parcours> p_ListeParcours, int p_LayoutResId) {
		super(p_Context, p_LayoutResId, p_ListeParcours);
		this.m_Context = p_Context;
		this.m_ListeParcours = p_ListeParcours;
		this.m_LayoutResId = p_LayoutResId;
	}
	
	@Override
	public View getView(int p_Position, View p_ConvertView, ViewGroup p_Parent) {
		ParcoursHolder contenant;
		View view = p_ConvertView;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)m_Context).getLayoutInflater();
			contenant = new ParcoursHolder();
			view = inflater.inflate(this.m_LayoutResId, p_Parent, false);
			contenant.actif = (Switch)view.findViewById(R.id.parcours_item_actif);
			contenant.adresseDepart = (TextView)view.findViewById(R.id.parcours_item_adresse_depart);
			contenant.adresseDestination = (TextView)view.findViewById(R.id.parcours_item_adresse_destination);
			contenant.dateDepart = (TextView)view.findViewById(R.id.parcours_item_date_depart);
			contenant.heureDepart = (TextView)view.findViewById(R.id.parcours_item_heure_depart);
			contenant.nbPlaces = (TextView)view.findViewById(R.id.parcours_item_nb_places);
			view.setTag(contenant);
		}
		else{
			contenant = (ParcoursHolder)view.getTag();
		}
		
		contenant.actif.setOnCheckedChangeListener(
				new CustomOnCheckedChangeListener(view,this.m_ListeParcours.get(p_Position)));
		
		Parcours parcours = (Parcours)this.m_ListeParcours.get(p_Position);
		
		if(parcours.getAdresseDepart() != null)
			contenant.adresseDepart.setText(parcours.getAdresseDepart());
		else if(parcours.getDepartLatitude() != null && parcours.getDepartLongitude() != null)
			contenant.adresseDepart.setText(Util.obtenirAdresse(parcours.getDepartLatitude(),
					parcours.getDepartLongitude(), m_Context));
		
		if(parcours.getAdresseDestination() != null)
			contenant.adresseDestination.setText(parcours.getAdresseDestination());
		else if(parcours.getDestinationLatitude() != null && parcours.getDestinationLongitude() != null)
			contenant.adresseDestination.setText(Util.obtenirAdresse(parcours.getDestinationLatitude(), 
					parcours.getDestinationLongitude(), m_Context));
		
		if(parcours.getTimestampDepart() != null){
			GregorianCalendar calendrier = new GregorianCalendar();
			calendrier.setTimeInMillis(Long.parseLong(parcours.getTimestampDepart()));
			
			contenant.dateDepart.setText(Util.dateFormat.format(calendrier.getTime()));
			contenant.heureDepart.setText(Util.timeFormat.format(calendrier.getTime()));
		}
		
		// Conducteur
		if(parcours.getConducteur()){
			if(parcours.getNbPlaces() > 1)
				contenant.nbPlaces.setText(
						parcours.getNbPlaces() + " " + 
						m_Context.getString(R.string.parcours_item_places_disponibles));
			else
				contenant.nbPlaces.setText(
						parcours.getNbPlaces() + " " +
						m_Context.getString(R.string.parcours_item_place_disponible));
		}
		// Passager
		else{
			if(parcours.getNbPlaces() > 1)
				contenant.nbPlaces.setText(
						m_Context.getString(R.string.parcours_item_demande) + " " +
						parcours.getNbPlaces() + " " +
						m_Context.getString(R.string.parcours_item_places));
			else
				contenant.nbPlaces.setText(
						m_Context.getString(R.string.parcours_item_demande) + " " +
						parcours.getNbPlaces() + " " +
						m_Context.getString(R.string.parcours_item_place));
		}
		
		contenant.actif.setChecked(parcours.getActif());
		
		return view;
	}
	
	private class CustomOnCheckedChangeListener implements OnCheckedChangeListener{
		
		private View view;
		private Parcours parcours;
		
		public CustomOnCheckedChangeListener(View p_view, Parcours p_parcours){
			this.view = p_view;
			this.parcours = p_parcours;
		}
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			parcours.setActif(isChecked);
			
			try{
				ParcoursDataSource psd = new ParcoursDataSource(view.getContext());
				psd.open();
				psd.update(parcours);
				psd.close();
			}catch(Exception e){}
			
			if(parcours.getActif()){
				view.findViewById(R.id.parcours_item_conteneur_modifier_supprimer).setVisibility(View.INVISIBLE);
			}
			else{
				view.findViewById(R.id.parcours_item_conteneur_modifier_supprimer).setVisibility(View.VISIBLE);
			}
		}
	}
	
	private static class ParcoursHolder{
		Switch	 actif;
		TextView adresseDepart;
		TextView adresseDestination;
		TextView dateDepart;
		TextView heureDepart;
		TextView nbPlaces;
	}
	
}
