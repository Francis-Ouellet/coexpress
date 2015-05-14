package com.francisouellet.covoiturageexpress;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.util.JsonParser;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class ProfilUtilisateurActivity extends Activity {
	
	private static TextView lblPrenom;
	private static TextView lblNom;
	private static TextView lblTelephone;
	private static TextView lblStatPassager;
	private static TextView lblStatConducteur;
	private static ListView listCommentaires;
	
	private static Utilisateur mUtilisateur = null;
	private String mIdUtilisateur;
	// Indique si la page de profil est celle de :
	// l'utilisateur connecté (true)
	// ou d'un autre utilisateur (false)
	private static Boolean monProfil;
	private Bundle mExtras;
	
	private static HttpClient m_ClientHttp = new DefaultHttpClient();
	
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
		
		// Obtient le type de profil
		// Obtient l'objet de l'utilisateur actuel s'il s'agit de mon profil
		// Obtient l'identifiant de l'utilisateur s'il ne s'agit pas de mon profil
		mExtras = this.getIntent().getExtras();
		if(mExtras != null){
			monProfil = mExtras.getBoolean(Util.EXTRA_TYPE_PROFIL);
			if(monProfil)
				mUtilisateur = (Utilisateur)mExtras.getSerializable(Util.EXTRA_UTILISATEUR);
			else
				mIdUtilisateur = mExtras.getString(Util.EXTRA_ID_UTILISATEUR);
		}
		
		// Consulte mon profil
		if(monProfil){
			getActionBar().setTitle(R.string.title_activity_profil_utilisateur_owner);
		}
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(monProfil)
			getMenuInflater().inflate(R.menu.profil_utilisateur, menu);
		else
			getMenuInflater().inflate(R.menu.profil_utilisateur_consultation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_ajouter_commentaire:
			
			break;
		case R.id.action_editer_profil:
			Intent i = new Intent(this, ModifierCompteActivity.class);
			i.putExtra(Util.EXTRA_UTILISATEUR, mUtilisateur);
			this.startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private static class ObtenirUtilisateurAsync extends AsyncTask<String, Void, Utilisateur>{
		
		private Exception m_Exception;
		private Context m_Context;
		
		public ObtenirUtilisateurAsync(Context p_Context) {
			this.m_Context = p_Context;
			this.m_Exception = null;
		}
		
		@Override
		protected Utilisateur doInBackground(String... params) {
			Utilisateur utilisateur = null;
			
			String idUtilisateur = params[0];
			
			try{
				
				if(mUtilisateur == null){
					URI uri = new URI("http", Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + idUtilisateur, null, null);
					HttpGet get = new HttpGet(uri);
					String body = m_ClientHttp.execute(get, new BasicResponseHandler());
					utilisateur = JsonParser.ToUtilisateur(new JSONObject(body));
				}
				else{
					utilisateur = mUtilisateur;
				}
				
			}catch(Exception e){e.printStackTrace(); this.m_Exception = e;}
			
			return utilisateur;
		}
		
		@Override
		protected void onPostExecute(Utilisateur result) {
			if(m_Exception != null && result != null){
				mUtilisateur = result;
				
				// Consulte un autre profil
				if(!monProfil)
					((Activity)m_Context).getActionBar().setTitle(
							m_Context.getText(R.string.title_activity_profil_utilisateur) 
							+ " " + mUtilisateur.getPrenom());
				
				lblPrenom.setText(mUtilisateur.getPrenom());
				lblNom.setText(mUtilisateur.getNom());
				lblTelephone.setText(mUtilisateur.getTelephone());
				
				
			}else{
				
			}
		}
	}
	
	
}
