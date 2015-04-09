package com.francisouellet.covoiturageexpress;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import com.francisouellet.covoiturageexpress.adapters.PassagersPotentielsAdapter;
import com.francisouellet.covoiturageexpress.classes.Parcours;
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
import android.view.View;
import android.widget.ListView;

public class AjouterParticipantsActivity extends Activity {
	
	private Bundle mExtras;
	private Parcours mParcoursDemandeur;
	private Utilisateur mUtilisateur;
	
	private ListView m_ListeParcoursPotentiels;
	private PassagersPotentielsAdapter m_Adapter;
	private List<Parcours> m_Parcours;
	
	private HttpClient m_ClientHttp = new DefaultHttpClient();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajouter_participants);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mExtras = getIntent().getExtras();
		if(mExtras != null){
			mParcoursDemandeur = (Parcours)mExtras.getSerializable(Util.EXTRA_PARCOURS);
			mUtilisateur = (Utilisateur)mExtras.getSerializable(Util.EXTRA_UTILISATEUR);
		}
		
		// Conducteur cherche passagers
		if(mParcoursDemandeur.getConducteur())
			getActionBar().setTitle(R.string.title_activity_trouver_passagers);
		// Passager cherche conducteur
		else
			getActionBar().setTitle(R.string.title_activity_trouver_conducteur);
		
		m_ListeParcoursPotentiels = (ListView)this.findViewById(R.id.liste_participants_potentiels);
		
		new AsyncChercherParticipantsPotentiels(this).execute();
	}
	
	public void ajouterParticipant(View v){}
	
	public void enleverParticipant(View v){
		m_Adapter.remove(m_Parcours.get(m_ListeParcoursPotentiels.getPositionForView(v)));
		m_Adapter.notifyDataSetChanged();
	}
	
	private class AsyncChercherParticipantsPotentiels extends AsyncTask<Void, Void, List<Parcours>>{
		
		private Context m_Context;
		private Exception m_Exception;
		
		public AsyncChercherParticipantsPotentiels(Context p_Context) {
			this.m_Context = p_Context;
		}
		
		@Override
		protected List<Parcours> doInBackground(Void... params) {
			// Requête pour trouver des parcours potentiels
			
			List<Parcours> parcours = new ArrayList<Parcours>();
			
			try{
				URI uri = new URI("http", Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + mUtilisateur.getCourriel()
						+ Util.REST_PARCOURS + "/" + mParcoursDemandeur.getId() + Util.REST_CHERCHER, null, null);
				
				HttpGet get = new HttpGet(uri);
				String body = m_ClientHttp.execute(get, new BasicResponseHandler());
				JSONArray parcoursJSON = new JSONArray(body);
				for(int i = 0; i < parcoursJSON.length(); i++){
					parcours.add(JsonParser.ToParcours(parcoursJSON.getJSONObject(i)));
				}
			}
			catch(Exception e){m_Exception = e; e.printStackTrace();}
			return parcours;
		}
		
		@Override
		protected void onPostExecute(List<Parcours> result) {
			if(m_Exception == null && result.size() > 0)
				m_Parcours = result;
			
			if(m_Parcours != null){
				m_Adapter = new PassagersPotentielsAdapter(this.m_Context, m_Parcours, R.layout.liste_participants_potentiels_item);
				m_ListeParcoursPotentiels.setAdapter(m_Adapter);
			}
		}
	}
}
