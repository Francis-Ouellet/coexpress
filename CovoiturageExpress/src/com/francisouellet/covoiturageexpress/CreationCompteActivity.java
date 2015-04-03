package com.francisouellet.covoiturageexpress;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class CreationCompteActivity extends Activity {
	private final String TAG = "CREATIONCOMPTE";
	
	private EditText lblCourriel;
	private EditText lblConfirmationCourriel;
	private EditText lblMotDePasse;
	private EditText lblConfirmationMotDePasse;
	private EditText lblPrenom;
	private EditText lblNom;
	private EditText lblNoTelephone;
	
	private String courriel;
	private String confirmationCourriel;
	private String motDePasse;
	private String confirmationMotDePasse;
	private String prenom;
	private String nom;
	private String noTelephone;
	
	private Utilisateur utilisateur;
	
	private HttpClient m_ClientHttp = new DefaultHttpClient();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creation_compte);
		
		// Récupération des champs
		this.lblCourriel = (EditText)this.findViewById(R.id.creation_compte_courriel);
		this.lblConfirmationCourriel = (EditText)this.findViewById(R.id.creation_compte_confirmation_courriel);
		this.lblMotDePasse = (EditText)this.findViewById(R.id.creation_compte_mot_de_passe);
		this.lblConfirmationMotDePasse = (EditText)this.findViewById(R.id.creation_compte_confirmation_mot_de_passe);
		this.lblPrenom = (EditText)this.findViewById(R.id.creation_compte_prenom);
		this.lblNom = (EditText)this.findViewById(R.id.creation_compte_nom);
		this.lblNoTelephone = (EditText)this.findViewById(R.id.creation_compte_no_telephone);
	}

	public void CreerCompte(View v){
		
		// Récupération du texte des champs
		this.courriel = lblCourriel.getText().toString().trim();
		this.confirmationCourriel = lblConfirmationCourriel.getText().toString().trim();
		this.motDePasse = lblMotDePasse.getText().toString().trim();
		this.confirmationMotDePasse = lblConfirmationMotDePasse.getText().toString().trim();
		this.prenom = lblPrenom.getText().toString().trim();
		this.nom = lblNom.getText().toString().trim();
		this.noTelephone = lblNoTelephone.getText().toString().trim();
		
		
		// Validation que les champs contiennent quelquechose
		if(	!this.courriel.isEmpty() 	&& 	!this.confirmationCourriel.isEmpty()	&& 
			!this.motDePasse.isEmpty() 	&& 	!this.confirmationMotDePasse.isEmpty()	&&
			!this.prenom.isEmpty()		&&	!this.nom.isEmpty()						&&
			!this.noTelephone.isEmpty()) {
			// Validation que le courriel et la confirmation sont pareils
			if(courriel.equals(confirmationCourriel)){
				// Validation du courriel
				if(validationCourriel(courriel)){
					// Validation que le mot de passe et la confirmation sont pareils
					if(motDePasse.equals(confirmationMotDePasse)){
						// Encrypte le mot de passe
						try{ motDePasse = Util.sha1(motDePasse); }
						catch(NoSuchAlgorithmException e){}
						
						utilisateur = new Utilisateur(courriel, motDePasse, nom, prenom, noTelephone, false, false);
						
						new AsyncCreerCompte(this).execute();
						
					} else
						Util.easyToast(this, R.string.txt_mot_de_passe_different);
				} else
					Util.easyToast(this, R.string.txt_courriel_invalide);
			} else 
				Util.easyToast(this, R.string.txt_courriel_different);
		} else 
			Util.easyToast(this, R.string.txt_tous_les_champs);
	}
	
	private boolean validationCourriel(String p_Courriel){
		return Pattern.matches("^.+@.+\\..+$", p_Courriel);
	}
	
	private boolean enregistrementLocalUtilisateur(Utilisateur p_Utilisateur){
		UtilisateurDataSource uds = new UtilisateurDataSource(this);
		try{
			uds.open();
			Boolean valide = uds.insert(p_Utilisateur);
			uds.close();
			return valide;
		}
		catch(Exception e){return false;}
	}
	
	/**
	 * Classe pour la création d'un compte de façon asynchrone sur le service web
	 * @author Francis Ouellet
	 */
	private class AsyncCreerCompte extends AsyncTask<Void, Void, Void>{
		
		private Exception m_Exp;
		private Context m_Context;
		
		public AsyncCreerCompte(Context p_Context) {
			this.m_Context = p_Context;
			this.m_Exp = null;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				// URI du service à appeler
				URI uri = new URI("http",Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + utilisateur.getCourriel(), null, null);
				HttpPut put = new HttpPut(uri);
				// Ajout des données de l'utilisateur sous forme JSON 
				put.setEntity(new StringEntity(JsonParser.ToJsonObject(utilisateur).toString(), "UTF-8"));
				put.addHeader("Content-Type","application/json");
				
				m_ClientHttp.execute(put, new BasicResponseHandler());
			}
			catch(Exception e){
				this.m_Exp = e;
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			// S'il  n'y a pas eu d'erreur avec le Service Web
			if(m_Exp == null){
				// Ajout de l'utilisateur en local
				if(enregistrementLocalUtilisateur(utilisateur)){
					Util.easyToast(m_Context, R.string.txt_utilisateur_cree);
					((CreationCompteActivity)m_Context).finish();
				}
				// Averti l'utilisateur s'il y a une erreur
				else{
					Util.easyToast(m_Context, R.string.txt_utilisateur_erreur_creation);
					lblMotDePasse.setText("");
					lblConfirmationMotDePasse.setText("");
				}
			}
			// Averti l'utilisateur s'il y a une erreur
			else{
				Util.easyToast(m_Context, R.string.txt_utilisateur_erreur_creation);
				lblMotDePasse.setText("");
				lblConfirmationMotDePasse.setText("");
			}
		}
	}
}
