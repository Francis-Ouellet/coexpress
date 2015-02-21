package com.francisouellet.covoiturageexpress;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class CreationParcoursActivity extends Activity {

	private Switch indConducteur;
	private EditText lblAdresseDepart;
	private EditText lblAdresseDestination;
	private EditText lblDistanceSupp;
	private EditText lblNbPlaces;
	private TextView lblDateDepart;
	private TextView lblHeureDepart;
	private CheckBox lblRepeter;
	private EditText lblNotes;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creation_parcours);
		
		this.indConducteur = (Switch)this.findViewById(R.id.creation_parcours_type_parcours);
		this.lblAdresseDepart = (EditText)this.findViewById(R.id.creation_parcours_adresse_depart);
		this.lblAdresseDestination = (EditText)this.findViewById(R.id.creation_parcours_adresse_destination);
		this.lblDistanceSupp = (EditText)this.findViewById(R.id.creation_parcours_distance_supp);
		this.lblNbPlaces = (EditText)this.findViewById(R.id.creation_parcours_nb_places);
		this.lblDateDepart = (TextView)this.findViewById(R.id.creation_parcours_date_depart);
		this.lblHeureDepart = (TextView)this.findViewById(R.id.creation_parcours_heure_depart);
		this.lblRepeter = (CheckBox)this.findViewById(R.id.creation_parcours_repeter);
		this.lblNotes = (EditText)this.findViewById(R.id.creation_parcours_notes);
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
}
