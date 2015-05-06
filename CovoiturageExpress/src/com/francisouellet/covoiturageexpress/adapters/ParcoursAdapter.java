package com.francisouellet.covoiturageexpress.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.francisouellet.covoiturageexpress.R;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.classes.ParcoursEtCarte;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.ParcoursDataSource;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.JsonParser;
import com.francisouellet.covoiturageexpress.util.Util;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class ParcoursAdapter extends ArrayAdapter<ParcoursEtCarte>{
	
	private Context m_Context;
	private List<ParcoursEtCarte> m_ListeParcoursEtCarte;
	private int m_LayoutResId;
	
	private Utilisateur utilisateur;
	
	private HttpClient m_ClientHttp = new DefaultHttpClient();
	
	public ParcoursAdapter(Context p_Context, List<ParcoursEtCarte> p_ListeParcours, int p_LayoutResId) {
		super(p_Context, p_LayoutResId, p_ListeParcours);
		this.m_Context = p_Context;
		this.m_ListeParcoursEtCarte = p_ListeParcours;
		this.m_LayoutResId = p_LayoutResId;
		
		UtilisateurDataSource uds = new UtilisateurDataSource(m_Context);
		uds.open();
		utilisateur = uds.getConnectedUser();
		uds.close();
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
			contenant.carte = (ImageView)view.findViewById(R.id.parcours_item_carte);
			contenant.dateDepart = (TextView)view.findViewById(R.id.parcours_item_date_depart);
			contenant.heureDepart = (TextView)view.findViewById(R.id.parcours_item_heure_depart);
			contenant.nbPlaces = (TextView)view.findViewById(R.id.parcours_item_nb_places);
			view.setTag(contenant);
		}
		else{
			contenant = (ParcoursHolder)view.getTag();
		}
		
		contenant.actif.setOnCheckedChangeListener(
				new CustomOnCheckedChangeListener(view,this.m_ListeParcoursEtCarte.get(p_Position).getParcours()));
		
		Parcours parcours = (Parcours)this.m_ListeParcoursEtCarte.get(p_Position).getParcours();
		Bitmap carte = (Bitmap)this.m_ListeParcoursEtCarte.get(p_Position).getCarte();
		
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
		
		if(carte != null){
			contenant.carte.setImageBitmap(carte);
		}
		
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
		
		if(parcours.getActif()){
			view.findViewById(R.id.parcours_item_conteneur_modifier_supprimer).setVisibility(View.INVISIBLE);
		}
		else{
			view.findViewById(R.id.parcours_item_conteneur_modifier_supprimer).setVisibility(View.VISIBLE);
		}
		
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
			new AsyncStatutActif(parcours, view).execute();
		}
	}
	
	private static class ParcoursHolder{
		Switch	 actif;
		TextView adresseDepart;
		TextView adresseDestination;
		ImageView carte;
		TextView dateDepart;
		TextView heureDepart;
		TextView nbPlaces;
	}
	
	/**
	 * Classe permettant de faire la mise à jour du statut "actif" d'un parcours
	 * @author Francis Ouellet
	 */
	private class AsyncStatutActif extends AsyncTask<Void, Void, Void>{
		
		private Exception m_Exception;
		private Parcours m_Parcours;
		private View m_View;
		
		public AsyncStatutActif(Parcours p_Parcours, View p_View) {
			this.m_Parcours = p_Parcours;
			this.m_View = p_View;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				URI uri = new URI("http", Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + 
						this.m_Parcours.getProprietaire() + Util.REST_PARCOURS + "/" + this.m_Parcours.getId(), null, null);
				
				// Transforme le parcours en JSON et y ajoute le mot de passe de l'utilisateur
				HttpPut put = new HttpPut(uri);
				put.setEntity(new StringEntity(JsonParser.ToJsonObject(this.m_Parcours)
						.put("password", utilisateur.getEncodedPassword()).toString(), "UTF-8"));
				put.addHeader("Content-Type","application/json");
				
				m_ClientHttp.execute(put, new BasicResponseHandler());
				
				// Mise à jour en local du statut
				ParcoursDataSource pds = new ParcoursDataSource(m_View.getContext());
				pds.open();
				pds.update(m_Parcours);
				pds.close();
				
			}catch(Exception e){m_Exception = e; e.printStackTrace();}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// S'il n'y a pas eu de problèmes au niveau du service web
			if(m_Exception == null){
				// Active ou désactive les boutons de modification et de suppression selon l'état
				if(m_Parcours.getActif()){
					m_View.findViewById(R.id.parcours_item_conteneur_modifier_supprimer).setVisibility(View.INVISIBLE);
				}
				else{
					m_View.findViewById(R.id.parcours_item_conteneur_modifier_supprimer).setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	private class AsyncObtenirCarte extends AsyncTask<Double, Void, Bitmap>{
		
		private ImageView m_Carte;
		private Exception m_Exception;
		
		public AsyncObtenirCarte(ImageView p_Carte) {
			this.m_Carte = p_Carte;
			this.m_Exception = null;
		}
		
		@Override
		protected Bitmap doInBackground(Double... params) {
			
			Double latDepart = params[0];
			Double longDepart = params[1];
			Double latDestination = params[2];
			Double longDestination = params[3];
			Bitmap image = null;
			InputStream is = null;
			try{
				String markerDepart = "color:blue|" + latDepart + "," + longDepart;
				String markerDestination = "color:red|" + latDestination + "," + longDestination;
				String size = "600x300";
				
				URI uri = new URI("https",Util.STATIC_MAPS_API, Util.STATIC_MAPS_PATH,
						Util.STATIC_MAPS_MARKERS + "=" + markerDepart + "&" +
						Util.STATIC_MAPS_MARKERS + "=" + markerDestination + "&" +
						Util.STATIC_MAPS_SIZE + "=" + size, null); 
				
				Log.i("URI CARTE", uri.toString());
				
				is = uri.toURL().openStream();
				image = BitmapFactory.decodeStream(is);
				
			}
			catch(IOException e){this.m_Exception = e; e.printStackTrace();}
			catch(Exception e){this.m_Exception = e; e.printStackTrace();}
			finally{
				if(is != null)
					try {
						is.close();
					}catch(Exception e){this.m_Exception = e; e.printStackTrace();}	
			}
			
			return image;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if(m_Exception != null && result != null){
				m_Carte.setImageBitmap(result);
			}
			else{
			//	m_Carte.setVisibility(View.GONE);
			}
		}
	}
	
}
