package com.francisouellet.covoiturageexpress;

import java.util.List;

import com.francisouellet.covoiturageexpress.R;
import com.francisouellet.covoiturageexpress.arrayadapters.ParcoursAdapter;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.ParcoursDataSource;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ListView;

public class MainActivity extends Activity{

	private final String TAG = "MAIN";
	
	private ListView m_ListeParcours;
	private ParcoursAdapter m_Adapter;
	private List<Parcours> m_Parcours;
	
	private UtilisateurDataSource uds;
	private ParcoursDataSource pds;
	private Utilisateur utilisateur;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_ListeParcours = (ListView)this.findViewById(R.id.liste_mes_parcours);
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
			
			pds.open();
			this.m_Parcours = pds.getAllByOwner(this.utilisateur.getCourriel());
			pds.close();
			
		}catch(Exception e){Log.i(TAG, e.toString());}
		
		if(this.m_Parcours != null){
			m_Adapter = new ParcoursAdapter(this, this.m_Parcours, R.layout.liste_parcours_item);
			m_ListeParcours.setAdapter(m_Adapter);
		}
		
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
		try{
			Parcours parcours = this.m_Parcours.get(m_ListeParcours.getPositionForView(v));
			this.pds.open();
			this.pds.remove(parcours.getId());
			this.pds.close();
			
			this.m_Parcours.remove(m_ListeParcours.getPositionForView(v));
			this.m_Adapter.remove(parcours);
			this.m_Adapter.notifyDataSetChanged();
			
		}catch(Exception e){
			Util.easyToast(this, R.string.txt_parcours_erreur_suppression);
			e.printStackTrace();}
	}
	
}
