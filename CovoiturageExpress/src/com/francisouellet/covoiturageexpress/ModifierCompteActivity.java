package com.francisouellet.covoiturageexpress;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.JsonParser;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class ModifierCompteActivity extends Activity {
	
	private Bundle mExtras;
	
	private EditText lblPrenom;
	private EditText lblNom;
	private EditText lblTelephone;
	private EditText lblMotDePasse;
	private EditText lblMotDePasseConfirmer;
	
	private Utilisateur mUtilisateur;
	private String mAncienMotDePasse;
	
	private HttpClient m_HttpClient = new DefaultHttpClient();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifier_compte);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		this.lblPrenom = (EditText)this.findViewById(R.id.modifier_compte_prenom);
		this.lblNom = (EditText)this.findViewById(R.id.modifier_compte_nom);
		this.lblTelephone = (EditText)this.findViewById(R.id.modifier_compte_telephone);
		this.lblMotDePasse = (EditText)this.findViewById(R.id.modifier_compte_mot_de_passe);
		this.lblMotDePasseConfirmer = (EditText)this.findViewById(R.id.modifier_compte_confirmer_mot_de_passe);
		
		mExtras = this.getIntent().getExtras();
		if(mExtras != null){
			mUtilisateur = (Utilisateur)mExtras.getSerializable(Util.EXTRA_UTILISATEUR);
		}
		
		if(mUtilisateur != null){
			this.lblPrenom.setText(mUtilisateur.getPrenom());
			this.lblNom.setText(mUtilisateur.getNom());
			this.lblTelephone.setText(mUtilisateur.getTelephone());
			this.mAncienMotDePasse = mUtilisateur.getEncodedPassword();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modifier_compte, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_confirmer_modification) {
			
			if(validerChamps()){
				new ModifierUtilisateurAsync(this).execute();
			}
			else{
				Util.easyToast(this, R.string.txt_mot_de_passe_different);
			}
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Permet de mettre à jour l'objet utilisateur selon ce qui est entré dans les champs
	 * Vérification au niveau du mot de passe pour qu'il corresponde bien à sa confirmation 
	 * @return False seulement si les mots de passe ne correspondent pas
	 */
	private boolean validerChamps(){
		
		String prenom = this.lblPrenom.getText().toString().trim();
		String nom = this.lblNom.getText().toString().trim();
		String telephone = this.lblTelephone.getText().toString().trim();
		String motDePasse = this.lblMotDePasse.getText().toString().trim();
		String motDePasseConfirmer = this.lblMotDePasseConfirmer.getText().toString().trim();
		
		if(prenom != null && prenom != "")
			mUtilisateur.setPrenom(prenom);
		if(nom != null && nom != "")
			mUtilisateur.setNom(nom);
		if(telephone != null && telephone != "")
			mUtilisateur.setTelephone(telephone);
		if(motDePasse != null && motDePasse != "" && motDePasseConfirmer != null && motDePasseConfirmer != ""){
			if(motDePasse.equals(motDePasseConfirmer))	
				try{mUtilisateur.setEncodedPassword(Util.sha1(motDePasse));}catch(Exception e){e.printStackTrace();}
			else
				return false;
		}
		
		return true;
	}
	
	private class ModifierUtilisateurAsync extends AsyncTask<Void, Void, Void>{
		
		private Context m_Context;
		private Exception m_Exception;
		
		public ModifierUtilisateurAsync(Context p_Context) {
			this.m_Context = p_Context;
			this.m_Exception = null;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			try{
				
				URI uri = new URI("http",Util.WEB_SERVICE, 
						Util.REST_UTILISATEURS + "/" + mUtilisateur.getCourriel(), null, null);
				
				HttpPut put = new HttpPut(uri);
				
				put.setEntity(new StringEntity(JsonParser.ToJsonObject(mUtilisateur)
						.put("previousPassword", mAncienMotDePasse).toString(), "UTF-8"));
				put.addHeader("Content-Type","application/json");
				
				m_HttpClient.execute(put, new BasicResponseHandler());
				
			}catch(Exception e){m_Exception = e; e.printStackTrace();}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(m_Exception != null){
				UtilisateurDataSource uds = new UtilisateurDataSource(m_Context);
				uds.open();
				uds.update(mUtilisateur);
				uds.close();
				((Activity)m_Context).finish();
			}
			else{
				Util.easyToast(m_Context, R.string.txt_utilisateur_erreur_modification);
			}
		}
	}
}
