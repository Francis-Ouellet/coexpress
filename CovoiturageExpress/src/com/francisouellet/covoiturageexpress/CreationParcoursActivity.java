package com.francisouellet.covoiturageexpress;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.ParcoursDataSource;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.JsonParser;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreationParcoursActivity extends Activity implements OnCheckedChangeListener{
	
	private final String TAG = "CREATION_PARCOURS";
	private final String JOURS_REPETES = "jours_repetes";
	private final String TIMESTAMP_DEPART = "timestamp_depart";
	private final String REPETER = "repeter";
	private final String MODIFICATION = "modification";
	private Bundle extras;
	private Boolean modeModification;
	
	private Switch lblTypeParcours;
	private EditText lblAdresseDepart;
	private EditText lblAdresseDestination;
	private EditText lblDistanceSupp;
	private EditText lblNbPlaces;
	private TextView lblDateDepart;
	private TextView lblHeureDepart;
	private CheckBox lblRepeter;
	private EditText lblNotes;
	
	private Boolean typeParcours;
	private String adresseDepart;
	private String adresseDestination;
	private Double departLatitude;
	private Double departLongitude;
	private Double destinationLatitude;
	private Double destinationLongitude;
	private Double distanceSupp;
	private int nbPlaces;
	private String timestampDepart;
	private Boolean repeter = false;
	private List<Integer> joursRepetes = null;
	private String notes;
	
	private GregorianCalendar calendrier;
	
	private Utilisateur utilisateur;
	private Parcours parcours;
	
	private HttpClient m_ClientHttp = new DefaultHttpClient();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creation_parcours);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
		if(sp.getBoolean(Util.SHARED_PREFERENCES_AIDE_CREATION_PARCOURS, true)){
			sp.edit().putBoolean(Util.SHARED_PREFERENCES_AIDE_CREATION_PARCOURS, false).commit();
			Util.montrerAideInteractive(this, R.layout.aide_creation_parcours, R.id.aide_creation_parcours);
		}
		
		this.lblTypeParcours = (Switch)this.findViewById(R.id.creation_parcours_type_parcours);
		this.lblAdresseDepart = (EditText)this.findViewById(R.id.creation_parcours_adresse_depart);
		this.lblAdresseDestination = (EditText)this.findViewById(R.id.creation_parcours_adresse_destination);
		this.lblDistanceSupp = (EditText)this.findViewById(R.id.creation_parcours_distance_supp);
		this.lblNbPlaces = (EditText)this.findViewById(R.id.creation_parcours_nb_places);
		this.lblDateDepart = (TextView)this.findViewById(R.id.creation_parcours_date_depart);
		this.lblHeureDepart = (TextView)this.findViewById(R.id.creation_parcours_heure_depart);
		this.lblRepeter = (CheckBox)this.findViewById(R.id.creation_parcours_repeter);
		this.lblNotes = (EditText)this.findViewById(R.id.creation_parcours_notes);
		
		this.lblTypeParcours.setOnCheckedChangeListener(this);
		
		// Récupération de l'utilisateur courant
		try{
			UtilisateurDataSource uds = new UtilisateurDataSource(this);
			uds.open();
			this.utilisateur = uds.getConnectedUser();
			uds.close();
		}catch(Exception e){e.printStackTrace();}

		// Récupération du parcours en cas de modification
		extras = this.getIntent().getExtras();
		if(extras != null){
			parcours = (Parcours)extras.getSerializable(Util.EXTRA_PARCOURS);
			modeModification = true;
		}
		else
			modeModification = false;
		
		this.calendrier = (GregorianCalendar)GregorianCalendar.getInstance(Locale.getDefault());
		
		if(savedInstanceState != null){
			// Répétitions
			this.joursRepetes = savedInstanceState.getIntegerArrayList(JOURS_REPETES);
			if(savedInstanceState.getBoolean(REPETER)){
				this.lblRepeter.setChecked(true);
				this.lblRepeter.callOnClick();
			}
			// Date
			this.calendrier.setTimeInMillis(savedInstanceState.getLong(TIMESTAMP_DEPART));
			// ModeModification
			this.modeModification = savedInstanceState.getBoolean(MODIFICATION);
		}
		else if(parcours != null){
			getActionBar().setTitle(R.string.title_activity_modification_parcours);
			this.modeModification = true;
			this.lblTypeParcours.setChecked(parcours.getConducteur());
			this.lblAdresseDepart.setText(parcours.getAdresseDepart());
			this.lblAdresseDestination.setText(parcours.getAdresseDestination());
			this.lblDistanceSupp.setText(parcours.getDistanceSupplementaire() + "");
			this.lblNbPlaces.setText(parcours.getNbPlaces() + "");
			if(parcours.getJoursRepetes() != null){
				this.lblRepeter.setChecked(true);
				this.joursRepetes = parcours.getJoursRepetes();
				this.lblRepeter.callOnClick();
			} 
			this.lblNotes.setText(parcours.getNotes());
			this.calendrier.setTimeInMillis(Long.parseLong(parcours.getTimestampDepart()));
		} 
		
		this.verifierTypeParcours(this.lblTypeParcours.isChecked());
		
		this.lblDateDepart.setText(Util.dateFormat.format(calendrier.getTime()));
		this.lblHeureDepart.setText(Util.timeFormat.format(calendrier.getTime()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creation_parcours, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_ajouter_parcours) {
			sauvegarderParcours();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putIntegerArrayList(this.JOURS_REPETES, (ArrayList<Integer>)this.joursRepetes);
		outState.putLong(TIMESTAMP_DEPART, this.calendrier.getTimeInMillis());
		outState.putBoolean(REPETER, this.repeter);
		outState.putBoolean(MODIFICATION, this.modeModification);
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		verifierTypeParcours(isChecked);
	}
	
	private void sauvegarderParcours(){
		
		if(this.verifierChamps()){
			this.adresseDepart = this.lblAdresseDepart.getText().toString();
			this.adresseDestination = this.lblAdresseDestination.getText().toString();
			this.timestampDepart = this.calendrier.getTimeInMillis() + "";
			this.notes = this.lblNotes.getText().toString();
			
			if(!this.repeter)
				this.joursRepetes = null;
			
			String id_Parcours;
			String timestamp = Calendar.getInstance().getTimeInMillis() + "";
			if(this.modeModification)
				id_Parcours = this.parcours.getId();
			else
				id_Parcours = timestamp + this.utilisateur.getCourriel();
			
			if(Geocoder.isPresent()){
				try{
					Geocoder geocoder = new Geocoder(this);
					List<Address> depart = geocoder.getFromLocationName(this.adresseDepart, 1);
					List<Address> destination = geocoder.getFromLocationName(this.adresseDestination, 1);
					
					if(depart.size() > 0){
						this.departLatitude = depart.get(0).getLatitude();
						this.departLongitude = depart.get(0).getLongitude();
					}
					if(destination.size() > 0){
						this.destinationLatitude = destination.get(0).getLatitude();
						this.destinationLongitude = destination.get(0).getLongitude();
					}
					
				}catch(Exception e){
					e.printStackTrace(); 
					Util.easyToast(this, R.string.txt_erreur_geocoder, Toast.LENGTH_LONG);
				}
			}
			else{
				Util.easyToast(this, R.string.txt_erreur_geocoder, Toast.LENGTH_LONG);
			}
			
			if(this.typeParcours){	// True -> Conducteur	
				this.nbPlaces = Integer.parseInt(this.lblNbPlaces.getText().toString());
				this.distanceSupp = Double.parseDouble(this.lblDistanceSupp.getText().toString());
				
				this.parcours = new Parcours(
						id_Parcours, utilisateur.getCourriel(), this.typeParcours,
						this.adresseDepart, this.departLatitude, this.departLongitude, 
						this.adresseDestination, this.destinationLatitude, this.destinationLongitude, 
						this.timestampDepart, timestamp, this.joursRepetes, 
						this.nbPlaces, this.distanceSupp, this.notes, false); 
			} else { // False -> Passager
				this.parcours = new Parcours(
						id_Parcours, utilisateur.getCourriel(), this.typeParcours,
						this.adresseDepart, this.departLatitude, this.departLongitude, 
						this.adresseDestination, this.destinationLatitude, this.destinationLongitude, 
						this.timestampDepart, timestamp,
						this.joursRepetes, this.notes, false);
			}
			
			new AsyncEnregistrementParcours(this).execute();
		}
	}
	
	private boolean enregistrementLocalParcours(Parcours p_Parcours){
		ParcoursDataSource pds = new ParcoursDataSource(this);
		try{
			pds.open();
			Boolean valide = pds.insert(p_Parcours);
			pds.close();
			return valide;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean miseAJourLocalParcours(Parcours p_Parcours){
		ParcoursDataSource pds = new ParcoursDataSource(this);
		try{
			pds.open();
			boolean valide = pds.update(p_Parcours);
			pds.close();
			return valide;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean verifierChamps(){
		if(this.lblAdresseDepart.getText().toString().equals("")){
			Util.easyToast(this, R.string.txt_adresse_depart_invalide);
			return false;
		}
		if(this.lblAdresseDestination.getText().toString().equals("")){
			Util.easyToast(this, R.string.txt_adresse_destination_invalide);
			return false;
		}
		if(Calendar.getInstance().getTimeInMillis() > this.calendrier.getTimeInMillis()){
			Util.easyToast(this, R.string.txt_date_heure_invalide);
			return false;
		}
		// Conducteur
		if(this.typeParcours){
			if(	this.lblDistanceSupp.getText().toString().equals("") ||
				this.lblDistanceSupp.getText().toString().equals(".")){
				Util.easyToast(this, R.string.txt_distance_supp_invalide);
				return false;
			}
			if(this.lblNbPlaces.getText().toString().equals("")){
				Util.easyToast(this, R.string.txt_nb_places_invalide);
				return false;
			}
		}
		return true;
	}
	
	private void verifierTypeParcours(Boolean type){
		if(this.typeParcours = type){
			// Conducteur
			this.lblDistanceSupp.setVisibility(View.VISIBLE);
			this.lblNbPlaces.setVisibility(View.VISIBLE);
		} else{
			// Passager
			this.lblDistanceSupp.setVisibility(View.GONE);
			this.lblNbPlaces.setVisibility(View.GONE);
		}
	}
	
	public void clickDate(View v){
		DialogFragment datePicker = new DatePickerFragment(this);
		datePicker.show(getFragmentManager(), "datepicker");
	}
	
	public void clickHeure(View v){
		DialogFragment timePicker = new TimePickerFragment(this);
		timePicker.show(getFragmentManager(), "timepicker");
	}
	
	public void clickRepeter(View v){
		if(this.repeter = ((CheckBox)v).isChecked()){
			this.findViewById(R.id.creation_parcours_repetitions).setVisibility(View.VISIBLE);
			if(this.joursRepetes == null){
				this.joursRepetes = new ArrayList<Integer>();
			}
			else
				majAffichageJoursSelectionnes();
			
		} else 
			this.findViewById(R.id.creation_parcours_repetitions).setVisibility(View.GONE);
		
	}
	
	public void clickJourRepetition(View v){
		if(v.getBackground() != null){
			v.setBackgroundResource(0);
			this.joursRepetes.remove((Object)getSelectedDay(v.getId()));
		} else{
			v.setBackgroundResource(R.drawable.ic_panorama_fisheye_black_48dp);
			this.joursRepetes.add(getSelectedDay(v.getId()));
		}
	}
	
	private int getSelectedDay(int viewId){
		switch(viewId){
		case R.id.creation_parcours_rep_dimanche:
			return Calendar.SUNDAY;
		case R.id.creation_parcours_rep_lundi:
			return Calendar.MONDAY;
		case R.id.creation_parcours_rep_mardi:
			return Calendar.TUESDAY;
		case R.id.creation_parcours_rep_mercredi:
			return Calendar.WEDNESDAY;
		case R.id.creation_parcours_rep_jeudi:
			return Calendar.THURSDAY;
		case R.id.creation_parcours_rep_vendredi:
			return Calendar.FRIDAY;
		case R.id.creation_parcours_rep_samedi:
			return Calendar.SATURDAY;
		default:
			return -1;
		}
	}
	
	private void majAffichageJoursSelectionnes(){
		for (int id_jour : this.joursRepetes) {
			int id_view;
			switch(id_jour){
			case Calendar.SUNDAY:
				id_view = R.id.creation_parcours_rep_dimanche;
				break;
			case Calendar.MONDAY:
				id_view = R.id.creation_parcours_rep_lundi;
				break;
			case Calendar.TUESDAY:
				id_view = R.id.creation_parcours_rep_mardi;
				break;
			case Calendar.WEDNESDAY:
				id_view = R.id.creation_parcours_rep_mercredi;
				break;
			case Calendar.THURSDAY:
				id_view = R.id.creation_parcours_rep_jeudi;
				break;
			case Calendar.FRIDAY:
				id_view = R.id.creation_parcours_rep_vendredi;
				break;
			case Calendar.SATURDAY:
				id_view = R.id.creation_parcours_rep_samedi;
				break;
			default:
				id_view = -1;
			}
			if(id_view != -1)
				this.findViewById(id_view).setBackgroundResource(R.drawable.ic_panorama_fisheye_black_48dp);
		}
	}
		
	private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
		
		private Context m_Context;
		
		public DatePickerFragment(Context context){
			this.m_Context = context;
		}
		
		public Dialog onCreateDialog(Bundle savedInstanceState){
			Calendar c = ((CreationParcoursActivity)this.m_Context).calendrier;
			int annee = c.get(Calendar.YEAR);
			int mois = c.get(Calendar.MONTH);
			int jour = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, annee, mois, jour);
		}
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			((CreationParcoursActivity)this.m_Context).calendrier.set(year, monthOfYear, dayOfMonth);
			((CreationParcoursActivity)this.m_Context).lblDateDepart.setText(Util.dateFormat.format(calendrier.getTime()));
		}
		
	}
	
	private class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
		
		private Context m_Context;
		
		public TimePickerFragment(Context p_Context){
			this.m_Context = p_Context;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			
			Calendar c = ((CreationParcoursActivity)this.m_Context).calendrier;
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			return new TimePickerDialog(getActivity(), this, hour, minute, 
					android.text.format.DateFormat.is24HourFormat(getActivity()));
		}
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			((CreationParcoursActivity)this.m_Context).calendrier.set(Calendar.HOUR_OF_DAY, hourOfDay);
			((CreationParcoursActivity)this.m_Context).calendrier.set(Calendar.MINUTE, minute);
			((CreationParcoursActivity)this.m_Context).lblHeureDepart.setText(Util.timeFormat.format(calendrier.getTime()));
			
		}
	}
	
	private class AsyncEnregistrementParcours extends AsyncTask<Void, Void, Void>{
		
		private Context m_Context;
		private Exception m_Exception;
		
		public AsyncEnregistrementParcours(Context p_Context){
			this.m_Context = p_Context;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				
				URI uri = new URI("http", Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + utilisateur.getCourriel()
						+ Util.REST_PARCOURS + "/" + parcours.getId(), null, null);
				
				HttpPut put = new HttpPut(uri);
				put.setEntity(new StringEntity(JsonParser.ToJsonObject(parcours)
						.put("password", utilisateur.getEncodedPassword()).toString(), "UTF-8"));
				put.addHeader("Content-Type","application/json");
				m_ClientHttp.execute(put, new BasicResponseHandler());
				
			}catch(Exception e){m_Exception = e; e.printStackTrace();}
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// Tout s'est bien déroulé au niveau du service Web
			if(m_Exception != null){
				Util.easyToast(this.m_Context, R.string.txt_parcours_erreur_creation,Toast.LENGTH_LONG);
			}
			
			// Si on est en modification
			if(modeModification){
				if(miseAJourLocalParcours(parcours)){
					if(m_Exception == null)
						Util.easyToast(this.m_Context, R.string.txt_parcours_modifie);
					finish();
				}
				else
					Util.easyToast(this.m_Context, R.string.txt_parcours_erreur_modification_locale);
			}
			// Si on est en ajout
			else{
				if(enregistrementLocalParcours(parcours)){
					if(m_Exception == null)
						Util.easyToast(this.m_Context, R.string.txt_parcours_cree);
					finish();
				}
				else
					Util.easyToast(this.m_Context, R.string.txt_parcours_erreur_creation_locale);
			}
		}
	}
}
