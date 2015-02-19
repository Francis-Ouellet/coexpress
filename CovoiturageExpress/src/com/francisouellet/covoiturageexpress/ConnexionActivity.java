package com.francisouellet.covoiturageexpress;

import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ConnexionActivity extends Activity {
	
	private final String TAG = "CONNEXION";
	private EditText lblCourriel;
	private EditText lblMotDePasse;
	
	private String courriel;
	private String motDePasse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connexion);
		
		lblCourriel = (EditText)this.findViewById(R.id.connexion_courriel);
		lblMotDePasse = (EditText)this.findViewById(R.id.connexion_mot_de_passe);
	}
	
	public void creerCompte(View v){
		Intent i = new Intent(this, CreationCompteActivity.class);
		this.startActivity(i);
	}
	
	public void connexion(View v){
		// TODO Connexion via le Web Service
		try{
			courriel = lblCourriel.getText().toString().trim();
			motDePasse = Util.sha1(lblMotDePasse.getText().toString().trim());
			
			// Obtient l'utilisateur qui tente de se connecter
			UtilisateurDataSource uds = new UtilisateurDataSource(this);
			uds.open();
			Utilisateur u = uds.get(courriel);
			uds.close();
			
			// Si l'utilisateur existe et que le mot de passe est valide
			if(u != null && u.getEncodedPassword().equals(motDePasse)){
				// Indique que l'utilisateur est connecté
				u.setEstConnecte(true);
				uds.open();
				// Indique que l'utilisateur est le dernier connecté et le met a jour
				uds.updateLastConnected(u);
				uds.close();
				
				// Ouvre l'activité principale
				Intent i = new Intent(this, MainActivity.class);
				this.startActivity(i);
				this.finish();
			}
			
		} catch(Exception e){Log.i(TAG, e.toString());}
	}
}
