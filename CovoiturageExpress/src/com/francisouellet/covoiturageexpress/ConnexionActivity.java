package com.francisouellet.covoiturageexpress;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.JsonParser;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ConnexionActivity extends Activity {
	
	private final String TAG = "CONNEXION";
	private EditText lblCourriel;
	private EditText lblMotDePasse;
	
	private String courriel;
	private String motDePasse;
	
	private UtilisateurDataSource uds;
	private Utilisateur utilisateur;
	
	private HttpClient m_ClientHttp = new DefaultHttpClient();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uds = new UtilisateurDataSource(this);
		utilisateur = null;
		
		//  Vérifier si quelqu'un est déjà connecté 
		//	Si oui, on ouvre directement l'application
		// 	Si non, on récupère le dernier connecté
		try{
			uds.open();
			utilisateur = uds.getConnectedUser();
			uds.close();
			
			if(utilisateur != null){
				Intent i = new Intent(this, MainActivity.class);
				this.startActivity(i);
				this.finish();
			}
			else{
				uds.open();
				utilisateur = uds.getLastConnected();
				uds.close();
			}
			
		}catch(Exception e){Log.i(TAG, e.toString());}
		
		setContentView(R.layout.activity_connexion);
		
		lblCourriel = (EditText)this.findViewById(R.id.connexion_courriel);
		lblMotDePasse = (EditText)this.findViewById(R.id.connexion_mot_de_passe);
		
		// Afficher le nom d'utilisateur de la dernière personne connectée
		if(utilisateur != null){
			lblCourriel.setText(utilisateur.getCourriel());
		}
	}
	
	public void creerCompte(View v){
		Intent i = new Intent(this, CreationCompteActivity.class);
		this.startActivity(i);
	}
	
	public void connexion(View v){
		try{
			courriel = lblCourriel.getText().toString().trim();
			motDePasse = Util.sha1(lblMotDePasse.getText().toString().trim());
			
			new AsyncConnexion(this).execute(courriel, motDePasse);
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	/**
	 * Classe permettant de vérifier la validité de la personne tentant de se connecter à l'application
	 * @author Francis Ouellet
	 */
	private class AsyncConnexion extends AsyncTask<String, Void, Utilisateur>{
		
		private Context m_Context;
		private Exception m_Exception;
		
		public AsyncConnexion(Context p_Context){
			this.m_Context = p_Context;
		}
		
		@Override
		protected Utilisateur doInBackground(String... params) {
			
			Utilisateur u = null;
			try{
				
				String courriel = params[0];
				String motDePasse = params[1];
				
				// Récupère l'utilisateur tentant de se connecter et le transforme en utilisateur existant				
				URI uri = new URI("http",Util.WEB_SERVICE,Util.REST_UTILISATEURS + "/" + courriel, null, null);
				HttpGet get = new HttpGet(uri);
				String body = m_ClientHttp.execute(get, new BasicResponseHandler());
				u = JsonParser.ToUtilisateur(body);
				
				// Si le mot de passe de l'utilisateur reçu est différent du mot de passe inscrit,
				// on invalide le traitement
				if(!u.getEncodedPassword().equals(motDePasse))
					u = null;
				
			}catch(Exception e){
				this.m_Exception = e; 
				e.printStackTrace();
			}
			return u;
		}
		
		@Override
		protected void onPostExecute(Utilisateur u) {
			// Pas d'erreur, le service Web renvoie un utilisateur
			if(this.m_Exception == null && u != null){
				try{
					UtilisateurDataSource uds = new UtilisateurDataSource(m_Context);
					uds.open();
					
					// L'utilisateur n'existe pas en local. On le crée
					if(uds.get(u.getCourriel()) == null){
						uds.insert(u);
					}
					// Connexion de l'utilisateur
					u.setEstConnecte(true);
					uds.updateLastConnected(u);
					
					uds.close();
					
					// Ouvre l'activité principale
					Intent i = new Intent(m_Context, MainActivity.class);
					startActivity(i);
					finish();
				}
				catch(Exception e){e.printStackTrace();}
			}
			// Erreur du traitement, utilisateur invalide ou inconnu
			else{
				lblMotDePasse.setText("");
				Util.easyToast(m_Context, R.string.txt_erreur_connexion);
			}
		}
	}
}
