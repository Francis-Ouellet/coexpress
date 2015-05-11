package com.francisouellet.covoiturageexpress;

import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class ProfilUtilisateurActivity extends Activity {
	
	private TextView lblPrenom;
	private TextView lblNom;
	private TextView lblTelephone;
	private TextView lblStatPassager;
	private TextView lblStatConducteur;
	private ListView listCommentaires;
	
	private Utilisateur mUtilisateur;
	// Indique si la page de profil est celle de :
	// l'utilisateur connecté (true)
	// ou d'un autre utilisateur (false)
	private Boolean monProfil;
	private Bundle mExtras;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profil_utilisateur);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		lblPrenom = (TextView)findViewById(R.id.profil_utilisateur_prenom);
		lblNom = (TextView)findViewById(R.id.profil_utilisateur_nom);
		lblTelephone = (TextView)findViewById(R.id.profil_utilisateur_telephone);
		lblStatPassager = (TextView)findViewById(R.id.profil_utilisateur_stat_passager);
		lblStatConducteur = (TextView)findViewById(R.id.profil_utilisateur_stat_conducteur);
		listCommentaires = (ListView)findViewById(R.id.profil_utilisateur_commentaires);
		
		mExtras = this.getIntent().getExtras();
		if(mExtras != null){
			mUtilisateur = (Utilisateur)mExtras.getSerializable(Util.EXTRA_UTILISATEUR);
			monProfil = mExtras.getBoolean(Util.EXTRA_TYPE_PROFIL);
		}
		
		// Consulte mon profil
		if(monProfil){
			getActionBar().setTitle(R.string.title_activity_profil_utilisateur_owner);
		}
		// Consulte un autre profil
		else{
			getActionBar().setTitle(getText(R.string.title_activity_profil_utilisateur) 
					+ " " + mUtilisateur.getPrenom());
		}
		
		if(mUtilisateur != null){
			lblPrenom.setText(mUtilisateur.getPrenom());
			lblNom.setText(mUtilisateur.getNom());
			lblTelephone.setText(mUtilisateur.getTelephone());
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profil_utilisateur, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}
}
