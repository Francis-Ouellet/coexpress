package com.francisouellet.covoiturageexpress;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
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

public class CreationParcoursActivity extends Activity implements OnCheckedChangeListener{

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
	private Double distanceSupp;
	private int nbPlaces;
	private String dateDepart;
	private String heureDepart;
	private Boolean repeter;
	private String notes;
	
	private GregorianCalendar calendrier;
	private DateFormat dateFormat;
	private DateFormat timeFormat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creation_parcours);
		
		this.lblTypeParcours = (Switch)this.findViewById(R.id.creation_parcours_type_parcours);
		this.lblAdresseDepart = (EditText)this.findViewById(R.id.creation_parcours_adresse_depart);
		this.lblAdresseDestination = (EditText)this.findViewById(R.id.creation_parcours_adresse_destination);
		this.lblDistanceSupp = (EditText)this.findViewById(R.id.creation_parcours_distance_supp);
		this.lblNbPlaces = (EditText)this.findViewById(R.id.creation_parcours_nb_places);
		this.lblDateDepart = (TextView)this.findViewById(R.id.creation_parcours_date_depart);
		this.lblHeureDepart = (TextView)this.findViewById(R.id.creation_parcours_heure_depart);
		this.lblRepeter = (CheckBox)this.findViewById(R.id.creation_parcours_repeter);
		this.lblNotes = (EditText)this.findViewById(R.id.creation_parcours_notes);
		
		if(this.lblTypeParcours != null){
			this.lblTypeParcours.setOnCheckedChangeListener(this);
			this.verifierTypeParcours(this.lblTypeParcours.isChecked());
		}
		
		Locale locale = Locale.getDefault();
		this.calendrier = (GregorianCalendar)GregorianCalendar.getInstance(locale);
		
		this.dateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);
		this.timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
		
		this.lblDateDepart.setText(this.dateFormat.format(calendrier.getTime()));
		this.lblHeureDepart.setText(this.timeFormat.format(calendrier.getTime()));
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void verifierTypeParcours(Boolean type){
		this.typeParcours = type;
		if(this.typeParcours){
			// Conducteur
			this.lblDistanceSupp.setVisibility(View.VISIBLE);
			this.lblNbPlaces.setVisibility(View.VISIBLE);
		} else{
			// Passager
			this.lblDistanceSupp.setVisibility(View.GONE);
			this.lblNbPlaces.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		verifierTypeParcours(isChecked);
	}
	
	public void clickDate(View v){
		
		DialogFragment datePicker = new DatePickerFragment(this);
		datePicker.show(getFragmentManager(), "datepicker");
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
			((CreationParcoursActivity)this.m_Context).lblDateDepart.setText(((CreationParcoursActivity)this.m_Context).dateFormat.format(calendrier.getTime()));
		}
		
	}
	
	public void clickHeure(View v){
		DialogFragment timePicker = new TimePickerFragment(this);
		timePicker.show(getFragmentManager(), "timepicker");
		
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
			((CreationParcoursActivity)this.m_Context).lblHeureDepart.setText(((CreationParcoursActivity)this.m_Context).timeFormat.format(calendrier.getTime()));
			
		}
	}
}
