package com.francisouellet.covoiturageexpress;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.francisouellet.covoiturageexpress.R;
import com.francisouellet.covoiturageexpress.adapters.ParcoursAdapter;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.ParcoursDataSource;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.JsonParser;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener{

	private final String TAG = "MAIN";
	
	private ListView m_ListeParcours;
	private ParcoursAdapter m_Adapter;
	private List<Parcours> m_Parcours;
	
	private UtilisateurDataSource uds;
	private ParcoursDataSource pds;
	private Utilisateur utilisateur;
	
	private HttpClient m_ClientHttp = new DefaultHttpClient();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_ListeParcours = (ListView)this.findViewById(R.id.liste_mes_parcours);
		m_ListeParcours.setOnItemClickListener(this);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		uds = new UtilisateurDataSource(this);
		pds = new ParcoursDataSource(this);
		try{
			uds.open();
			this.utilisateur = uds.getConnectedUser();
			uds.close();
		}catch(Exception e){Log.i(TAG, e.toString());}
		
		new AsyncObtenirParcours(this).execute();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_deconnexion) {
			try{
			utilisateur.setEstConnecte(false);
			uds.open();
			uds.update(utilisateur);
			uds.close();
			
			Intent i = new Intent(this, ConnexionActivity.class);
			this.startActivity(i);
			this.finish();
			
			} catch(Exception e){Log.i(TAG,e.toString());}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void ajouterParcours(View v){
		Intent i = new Intent(this, CreationParcoursActivity.class);
		this.startActivity(i);
	}
	
	public void modifierParcours(View v){
		Parcours parcours = this.m_Parcours.get(m_ListeParcours.getPositionForView(v));
		Intent i = new Intent(this, CreationParcoursActivity.class);
		i.putExtra(Util.EXTRA_PARCOURS, parcours);
		this.startActivity(i);
	}
	
	public void supprimerParcours(View v){
		new AsyncSupprimerParcours(this, this.m_Parcours.get(m_ListeParcours.getPositionForView(v))).execute();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent(this, ParcoursDetailActivity.class);
		i.putExtra(Util.EXTRA_PARCOURS, this.m_Parcours.get(position));
		i.putExtra(Util.EXTRA_UTILISATEUR, this.utilisateur);
		this.startActivity(i);
	}
	
	private class AsyncObtenirParcours extends AsyncTask<Void, Void, List<Parcours>>{
			
		private Context m_Context;
		private Exception m_Exception;
		
		public AsyncObtenirParcours(Context p_Context) {
			this.m_Context = p_Context;
		}
		
		@Override
		protected List<Parcours> doInBackground(Void... params) {
			
			List<Parcours> parcours = new ArrayList<Parcours>();
			
			try{
				
				URI uri = new URI("http", Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + utilisateur.getCourriel() + 
						Util.REST_PARCOURS, null, null);
				
				HttpGet get = new HttpGet(uri);
				String body = m_ClientHttp.execute(get, new BasicResponseHandler());
				
				JSONArray parcoursJSON = new JSONArray(body);
				for(int i = 0; i < parcoursJSON.length(); i++){
					parcours.add(JsonParser.ToParcours((JSONObject)parcoursJSON.get(i)));
				}
				
				// Met à jour les données locales si elles ne sont pas synchronisées avec les données du service web
				pds.open();
				for (Parcours item : parcours) {
					if(pds.get(item.getId()) == null)
						pds.insert(item);
					else
						pds.update(item);
				}
				pds.close();
				
				
			}catch(Exception e){ m_Exception = e; e.printStackTrace();}
			
			return parcours;
		}

		@Override
		protected void onPostExecute(List<Parcours> result) {
			// Si la requête a fonctionné et que la liste retournée contient des items, on les affiche
			if(m_Exception == null && result.size() > 0){
				m_Parcours = result;
			}
			// Si la requête n'a pas fonctionné, on vérifie s'il existe des parcours en local à afficher
			else{
				try{
					pds.open();
					m_Parcours = pds.getAllByOwner(utilisateur.getCourriel());
					pds.close();
				}
				catch(Exception e){e.printStackTrace();}
			}
			
			if(m_Parcours != null){
				m_Adapter = new ParcoursAdapter(this.m_Context, m_Parcours, R.layout.liste_parcours_item);
				m_ListeParcours.setAdapter(m_Adapter);
			}
		}
	}
	
	private class AsyncSupprimerParcours extends AsyncTask<Void, Void, Void>{
		
		private Exception m_Exception;
		private Context m_Context;
		private Parcours parcours;
		
		public AsyncSupprimerParcours(Context p_Context, Parcours p_Parcours) {
			this.m_Context = p_Context;
			this.parcours = p_Parcours;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				// Requête pour la suppression du parcours
				URI uri = new URI("http", Util.WEB_SERVICE, Util.REST_UTILISATEURS + "/" + utilisateur.getCourriel() +
						Util.REST_PARCOURS + "/" + parcours.getId(), "password=" + utilisateur.getEncodedPassword(), null);
				
				HttpDelete delete = new HttpDelete(uri);
				m_ClientHttp.execute(delete, new BasicResponseHandler());
				
				// Suppression locale du parcours
				pds.open();
				pds.remove(parcours.getId());
				pds.close();
				
			}catch(Exception e){this.m_Exception = e; e.printStackTrace();}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// Pas d'exception : on supprime le parcours de la liste affichée
			if(m_Exception == null){
				m_Parcours.remove(parcours);
				m_Adapter.remove(parcours);
				m_Adapter.notifyDataSetChanged();
			}
			else{
				Util.easyToast(this.m_Context, R.string.txt_parcours_erreur_suppression);
			}
		}
	}
}
