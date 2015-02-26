package com.francisouellet.covoiturageexpress;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.ParcoursDataSource;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

public class CreationParcoursActivity extends Activity implements OnCheckedChangeListener{
	
	private final String TAG = "CREATION_PARCOURS";
	
	private Switch lblTypeParcours;
	private EditText lblAdresseDepart;
	private EditText lblAdresseDestination;
	private EditText lblDistanceSupp;
	private EditText lblNbPlaces;
	private TextView lblDateDepart;
	private TextView lblHeureDepart;
	private EditText lblNotes;
	
	private Boolean typeParcours;
	private String adresseDepart;
	private String adresseDestination;
	private Double distanceSupp;
	private int nbPlaces;
	private String timestampDepart;
	private Boolean repeter = false;
	private List<Integer> joursRepetes = null;
	private String notes;
	
	private GregorianCalendar calendrier;
	
	private Utilisateur utilisateur;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creation_parcours);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		this.lblTypeParcours = (Switch)this.findViewById(R.id.creation_parcours_type_parcours);
		this.lblAdresseDepart = (EditText)this.findViewById(R.id.creation_parcours_adresse_depart);
		this.lblAdresseDestination = (EditText)this.findViewById(R.id.creation_parcours_adresse_destination);
		this.lblDistanceSupp = (EditText)this.findViewById(R.id.creation_parcours_distance_supp);
		this.lblNbPlaces = (EditText)this.findViewById(R.id.creation_parcours_nb_places);
		this.lblDateDepart = (TextView)this.findViewById(R.id.creation_parcours_date_depart);
		this.lblHeureDepart = (TextView)this.findViewById(R.id.creation_parcours_heure_depart);
		this.lblNotes = (EditText)this.findViewById(R.id.creation_parcours_notes);
		
		try{
			UtilisateurDataSource uds = new UtilisateurDataSource(this);
			uds.open();
			this.utilisateur = uds.getConnectedUser();
			uds.close();
		}catch(Exception e){Log.i(TAG, e.toString());}
		
		if(this.lblTypeParcours != null){
			this.lblTypeParcours.setOnCheckedChangeListener(this);
			this.verifierTypeParcours(this.lblTypeParcours.isChecked());
		}
		
		this.calendrier = (GregorianCalendar)GregorianCalendar.getInstance(Locale.getDefault());
		
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		verifierTypeParcours(isChecked);
	}
	
	private void sauvegarderParcours(){
		
		if(this.verifierChamps()){
			Parcours parcours = null;
			
			this.adresseDepart = this.lblAdresseDepart.getText().toString();
			this.adresseDestination = this.lblAdresseDestination.getText().toString();
			this.timestampDepart = this.calendrier.getTimeInMillis() + "";
			this.notes = this.lblNotes.getText().toString();
			
			if(!this.repeter)
				this.joursRepetes = null;
			


			if(this.typeParcours){	// True -> Conducteur	
				this.nbPlaces = Integer.parseInt(this.lblNbPlaces.getText().toString());
				this.distanceSupp = Double.parseDouble(this.lblDistanceSupp.getText().toString());
				
				parcours = new Parcours(
						Calendar.getInstance().getTimeInMillis() + "", 
						utilisateur.getCourriel(), this.typeParcours, 
						this.adresseDepart, this.adresseDestination, this.timestampDepart,
						this.joursRepetes, this.nbPlaces, this.distanceSupp, this.notes, false);
			} else { // False -> Passager
				parcours = new Parcours(
						Calendar.getInstance().getTimeInMillis() + "",
						utilisateur.getCourriel(), this.typeParcours,
						this.adresseDepart, this.adresseDestination, this.timestampDepart,
						this.joursRepetes, this.notes, false);
			}
			
			if(enregistrementLocalParcours(parcours)){
				Util.easyToast(this, R.string.txt_parcours_cree);
				this.finish();
			}
			else
				Util.easyToast(this, R.string.txt_parcours_erreur_creation);
		}
	}
	
	
	private boolean enregistrementLocalParcours(Parcours p_Parcours){
		ParcoursDataSource pds = new ParcoursDataSource(this);
		try{
			pds.open();
			pds.insert(p_Parcours);
			pds.close();
			return true;
		}catch(Exception e){
			Log.i(TAG, e.toString());
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
				this.joursRepetes.add(Calendar.SUNDAY);
				this.joursRepetes.add(Calendar.MONDAY);
				this.joursRepetes.add(Calendar.TUESDAY);
				this.joursRepetes.add(Calendar.WEDNESDAY);
				this.joursRepetes.add(Calendar.THURSDAY);
				this.joursRepetes.add(Calendar.FRIDAY);
				this.joursRepetes.add(Calendar.SATURDAY);
			}
				
		} else {
			this.findViewById(R.id.creation_parcours_repetitions).setVisibility(View.GONE);
		}
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
}
