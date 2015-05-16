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
import com.francisouellet.covoiturageexpress.classes.CommentaireEtVote;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.JsonParser;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
	private static ImageView imgUpvote;
	private static ImageView imgDownvote;
	
	private static Utilisateur mUtilisateur;
	private static String mIdUtilisateur;
	private static List<Commentaire> mCommentaires;
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
		
		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
		if(sp.getBoolean(Util.SHARED_PREFERENCES_AIDE_PROFIL_UTILISATEUR, true)){
			sp.edit().putBoolean(Util.SHARED_PREFERENCES_AIDE_PROFIL_UTILISATEUR, false).commit();
			Util.montrerAideInteractive(this, R.layout.aide_profil_utilisateur, R.id.aide_profil_utilisateur);
		}
		
		lblPrenom = (TextView)findViewById(R.id.profil_utilisateur_prenom);
		lblNom = (TextView)findViewById(R.id.profil_utilisateur_nom);
		lblTelephone = (TextView)findViewById(R.id.profil_utilisateur_telephone);
		lblStatPassager = (TextView)findViewById(R.id.profil_utilisateur_stat_passager);
		lblStatConducteur = (TextView)findViewById(R.id.profil_utilisateur_stat_conducteur);
		listCommentaires = (ListView)findViewById(android.R.id.list);
		listCommentaires.setEmptyView(findViewById(android.R.id.empty));
		lblCommentaire = (EditText)findViewById(R.id.profil_utilisateur_commentaire);
		
		mIdUtilisateur = null;
		mUtilisateur = null;
		mCommentaires = null;
		
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
		int position = listCommentaires.getPositionForView(v);
		Commentaire c = mCommentaires.get(position);
		if(m_Adapter.getItem(position).isTypeVote() == null){
			new VoterAsync(this, c, position, true).execute();
			m_Adapter.getItem(position).getCommentaire().ajouterUpvote();
		}else if (!m_Adapter.getItem(position).isTypeVote()){
			new VoterAsync(this, c, position, true).execute();
			m_Adapter.getItem(position).getCommentaire().ajouterUpvote();
			m_Adapter.getItem(position).getCommentaire().ajouterUpvote();
		}else{
			new VoterAsync(this, c, position, null).execute();
			m_Adapter.getItem(position).getCommentaire().ajouterDownvote();
		}
	}
	
	public void downvote(View v){
		int position = listCommentaires.getPositionForView(v);
		Commentaire c = mCommentaires.get(position);
		if(m_Adapter.getItem(position).isTypeVote() == null){
			new VoterAsync(this, c, position, false).execute();
			m_Adapter.getItem(position).getCommentaire().ajouterDownvote();
		}else if(m_Adapter.getItem(position).isTypeVote()){
			new VoterAsync(this, c, position, false).execute();
			m_Adapter.getItem(position).getCommentaire().ajouterDownvote();
			m_Adapter.getItem(position).getCommentaire().ajouterDownvote();
		}else{
			new VoterAsync(this, c, position, null).execute();
			m_Adapter.getItem(position).getCommentaire().ajouterUpvote();
		}
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
				m_Adapter.add(new CommentaireEtVote(result, null));
				m_Adapter.notifyDataSetChanged();
			}
		}
	} 
	
	private static class ObtenirCommentairesAsync extends AsyncTask<Void, Void, List<CommentaireEtVote>>{
		
		private Context m_Context;
		private Exception m_Exception;
		
		public ObtenirCommentairesAsync(Context p_Context) {
			this.m_Context = p_Context;
			this.m_Exception = null;
		}
		
		@Override
		protected List<CommentaireEtVote> doInBackground(Void... params) {
			
			List<CommentaireEtVote> commentairesEtVotes = null;
			List<Commentaire> commentaires = null;
			List<StringBoolean> votes = null;
			
			try{
				
				UtilisateurDataSource uds = new UtilisateurDataSource(m_Context);
				uds.open();
				Utilisateur voteur = uds.getConnectedUser();
				uds.close();
				
				URI uri = new URI("http",Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + mUtilisateur.getCourriel() +
						Util.REST_COMMENTAIRES, null, null);
				
				HttpGet get = new HttpGet(uri);
				String body = m_ClientHttp.execute(get, new BasicResponseHandler());
				
				JSONArray jsonCommentaires = new JSONArray(body);
				commentaires = new ArrayList<Commentaire>();
				for(int i = 0; i < jsonCommentaires.length(); i++){
					commentaires.add(JsonParser.ToCommentaire(jsonCommentaires.getJSONObject(i)));
				}
				
				uri = new URI("http", Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + mUtilisateur.getCourriel() +
						Util.REST_COMMENTAIRES + Util.REST_VOTE + "/" + voteur.getCourriel(), null, null);
				
				get = new HttpGet(uri);
				body = m_ClientHttp.execute(get, new BasicResponseHandler());
				JSONArray jsonVotes = new JSONArray(body);
				votes = new ArrayList<StringBoolean>();
				for(int i = 0; i < jsonVotes.length(); i++){
					votes.add(new StringBoolean(
							jsonVotes.getJSONObject(i).getBoolean("typeVote"), 
							jsonVotes.getJSONObject(i).getString("idCommentaire")));
				}
				
				commentairesEtVotes = new ArrayList<CommentaireEtVote>();
				for(int i = 0; i < commentaires.size(); i++){
					
					boolean found = false;
					int j = 0;
					while(!found && j < votes.size())
						if(commentaires.get(i).getId().equals(votes.get(j).string))
							found = true;
						else
							j++;
					
					if(found)
						commentairesEtVotes.add(new CommentaireEtVote(commentaires.get(i), votes.get(j).bool));
					else
						commentairesEtVotes.add(new CommentaireEtVote(commentaires.get(i), null));
					
				}
				
			}catch(Exception e){m_Exception = e; e.printStackTrace();}
			return commentairesEtVotes;
		}
		
		@Override
		protected void onPostExecute(List<CommentaireEtVote> result) {
			if(m_Exception == null && result != null){
				mCommentaires = new ArrayList<Commentaire>();
				for (CommentaireEtVote commentaireEtVote : result) {
					mCommentaires.add(commentaireEtVote.getCommentaire());
				}
				m_Adapter = new CommentairesAdapter(m_Context, result, R.layout.liste_commentaires_item);
				listCommentaires.setAdapter(m_Adapter);
			}
		}
		
		private static class StringBoolean{
			Boolean bool;
			String string;
			
			public StringBoolean(Boolean pBool, String pString) {
				bool = pBool;
				string = pString;
			}
		}
	}  
	
	private static class VoterAsync extends AsyncTask<Void, Void, Void>{
		
		private Context m_Context;
		private Exception m_Exception;
		private Commentaire m_Commentaire;
		private int m_Position;
		private Boolean m_TypeVote;
		
		public VoterAsync(Context p_Context, Commentaire p_Commentaire, int p_Position, Boolean p_TypeVote) {
			this.m_Context = p_Context;
			this.m_Commentaire = p_Commentaire;
			this.m_Position = p_Position;
			this.m_TypeVote = p_TypeVote;
			this.m_Exception = null;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				
				UtilisateurDataSource uds = new UtilisateurDataSource(m_Context);
				uds.open();
				Utilisateur voteur = uds.getConnectedUser();
				uds.close();
				
				URI uri = new URI("http", Util.WEB_SERVICE, 
						Util.REST_UTILISATEURS + "/" + mUtilisateur.getCourriel() +
						Util.REST_COMMENTAIRES + "/" + m_Commentaire.getId() + 
						Util.REST_VOTE + "/" + voteur.getCourriel(), null, null);
				
				HttpPut put = new HttpPut(uri);
				if(m_TypeVote != null)
					put.setEntity(new StringEntity(new JSONObject()
							.put("typeVote", m_TypeVote)
							.put("password", voteur.getEncodedPassword()).toString(), "UTF-8"));
				else
					put.setEntity(new StringEntity(new JSONObject()
						.put("password", voteur.getEncodedPassword()).toString(), "UTF-8"));
				put.addHeader("Content-Type","application/json");
				m_ClientHttp.execute(put, new BasicResponseHandler());
				
			}catch(Exception e){m_Exception = e; e.printStackTrace();}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(m_Exception == null){
				m_Adapter.getItem(m_Position).setTypeVote(m_TypeVote);
				listCommentaires.setAdapter(m_Adapter);
			}
		}
	}
	
}
