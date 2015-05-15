package com.francisouellet.covoiturageexpress;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.francisouellet.covoiturageexpress.adapters.CommentairesAdapter;
import com.francisouellet.covoiturageexpress.classes.Commentaire;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.JsonParser;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ProfilUtilisateurActivity extends Activity {
	
	private static TextView lblPrenom;
	private static TextView lblNom;
	private static TextView lblTelephone;
	private static TextView lblStatPassager;
	private static TextView lblStatConducteur;
	private static ListView listCommentaires;
	private static EditText lblCommentaire;
	
	private static Utilisateur mUtilisateur = null;
	private static String mIdUtilisateur = null;
	private static List<Commentaire> mCommentaires = null;
	// Indique si la page de profil est celle de :
	// l'utilisateur connecté (true)
	// ou d'un autre utilisateur (false)
	private static Boolean monProfil;
	private Bundle mExtras;
	
	private static CommentairesAdapter m_Adapter;
	
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
		lblCommentaire = (EditText)findViewById(R.id.profil_utilisateur_commentaire);
		
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
		super.onResume();
		new ObtenirUtilisateurAsync(this).execute(mIdUtilisateur);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(monProfil)
			getMenuInflater().inflate(R.menu.profil_utilisateur, menu);
		else
			getMenuInflater().inflate(R.menu.menu_vide, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_editer_profil:
			Intent i = new Intent(this, ModifierCompteActivity.class);
			i.putExtra(Util.EXTRA_UTILISATEUR, mUtilisateur);
			this.startActivityForResult(i, 1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1){
			if(resultCode == RESULT_OK){
				mUtilisateur = (Utilisateur)data.getSerializableExtra(Util.EXTRA_UTILISATEUR);
			}
		}
	}
	
	public void envoyerCommentaire(View v){
		String commentaire = lblCommentaire.getText().toString();
		if(!commentaire.isEmpty())
			new EnvoyerCommentaireAsync(this).execute(commentaire);
	}
	
	public void upvote(View v){
		Commentaire c = mCommentaires.get(listCommentaires.getPositionForView(v));
		new VoterAsync(this).execute(c.getId(), "true");
		
	}
	
	public void downvote(View v){
		Commentaire c = mCommentaires.get(listCommentaires.getPositionForView(v));
		new VoterAsync(this).execute(c.getId(), "false");
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
				
				if(mIdUtilisateur != null){
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
			if(m_Exception == null && result != null){
				mUtilisateur = result;
				
				// Consulte un autre profil
				if(!monProfil)
					((Activity)m_Context).getActionBar().setTitle(
							m_Context.getText(R.string.title_activity_profil_utilisateur) 
							+ " " + mUtilisateur.getPrenom());
				
				lblPrenom.setText(mUtilisateur.getPrenom());
				lblNom.setText(mUtilisateur.getNom());
				lblTelephone.setText(mUtilisateur.getTelephone());
				
				new ObtenirCommentairesAsync(m_Context).execute();
				
			}else{
				
			}
		}
	}
	
	private static class EnvoyerCommentaireAsync extends AsyncTask<String, Void, Commentaire>{
		
		private Exception m_Exception;
		private Context m_Context;
		
		public EnvoyerCommentaireAsync(Context p_Context) {
			this.m_Context = p_Context;
			this.m_Exception = null;
		}
		
		@Override
		protected Commentaire doInBackground(String... params) {
			Commentaire commentaire = null;
			try{
				
				UtilisateurDataSource uds = new UtilisateurDataSource(m_Context);
				uds.open();
				Utilisateur commenteur = uds.getConnectedUser();
				uds.close();
				
				String timestamp = GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis() + "";
				commentaire = new Commentaire(timestamp + mUtilisateur.getCourriel(), 
						commenteur.getCourriel(), timestamp, params[0]);
				
				URI uri = new URI("http",Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + mUtilisateur.getCourriel() +
						Util.REST_COMMENTAIRES + "/" + commentaire.getId(), null, null);
				
				HttpPut put = new HttpPut(uri);
				put.setEntity(new StringEntity(JsonParser.ToJsonObject(commentaire).toString(), "UTF-8"));
				put.addHeader("Content-Type","application/json");
				m_ClientHttp.execute(put, new BasicResponseHandler());
				
			}catch(Exception e){this.m_Exception = e; e.printStackTrace();}
			return commentaire;
		}
		
		@Override
		protected void onPostExecute(Commentaire result) {
			if(m_Exception == null){
				lblCommentaire.setText("");
				InputMethodManager imm = (InputMethodManager)((Activity)m_Context).getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(lblCommentaire.getWindowToken(), 0);
				mCommentaires.add(result);
				m_Adapter.notifyDataSetChanged();
			}
		}
	} 
	
	private static class ObtenirCommentairesAsync extends AsyncTask<Void, Void, List<Commentaire>>{
		
		private Context m_Context;
		private Exception m_Exception;
		
		public ObtenirCommentairesAsync(Context p_Context) {
			this.m_Context = p_Context;
			this.m_Exception = null;
		}
		
		@Override
		protected List<Commentaire> doInBackground(Void... params) {
			
			List<Commentaire> commentaires = null;
			
			try{
				
				URI uri = new URI("http",Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + mUtilisateur.getCourriel() +
						Util.REST_COMMENTAIRES, null, null);
				
				HttpGet get = new HttpGet(uri);
				String body = m_ClientHttp.execute(get, new BasicResponseHandler());
				
				JSONArray jsonCommentaires = new JSONArray(body);
				commentaires = new ArrayList<Commentaire>();
				for(int i = 0; i < jsonCommentaires.length(); i++){
					commentaires.add(JsonParser.ToCommentaire(jsonCommentaires.getJSONObject(i)));
				}
				
			}catch(Exception e){m_Exception = e; e.printStackTrace();}
			return commentaires;
		}
		
		@Override
		protected void onPostExecute(List<Commentaire> result) {
			if(m_Exception == null && result != null){
				mCommentaires = result;
				m_Adapter = new CommentairesAdapter(m_Context, mCommentaires, R.layout.liste_commentaires_item);
				listCommentaires.setAdapter(m_Adapter);
			}
		}
	}  
	
	private static class VoterAsync extends AsyncTask<String, Void, Void>{
		
		private Context m_Context;
		private Exception m_Exception;
		
		public VoterAsync(Context p_Context) {
			this.m_Context = p_Context;
			this.m_Exception = null;
		}
		
		@Override
		protected Void doInBackground(String... params) {
			try{
				
				String idCommentaire = params[0];
				Boolean vote = Boolean.valueOf(params[1]);
				
				UtilisateurDataSource uds = new UtilisateurDataSource(m_Context);
				uds.open();
				Utilisateur commenteur = uds.getConnectedUser();
				uds.close();
				
				URI uri = new URI("http", Util.WEB_SERVICE, 
						Util.REST_UTILISATEURS + "/" + mUtilisateur.getCourriel() +
						Util.REST_COMMENTAIRES + "/" + idCommentaire + 
						Util.REST_VOTE + "/" + commenteur.getCourriel(), null, null);
				
				HttpPut put = new HttpPut(uri);
				put.setEntity(new StringEntity(new JSONObject()
						.put("typeVote", vote)
						.put("password", commenteur.getEncodedPassword()).toString(), "UTF-8"));
				put.addHeader("Content-Type","application/json");
				m_ClientHttp.execute(put, new BasicResponseHandler());
				
			}catch(Exception e){m_Exception = e; e.printStackTrace();}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(m_Exception == null){
				
			}
		}
	}
	
}
