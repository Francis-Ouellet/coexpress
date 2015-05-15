package com.francisouellet.covoiturageexpress.adapters;

import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.francisouellet.covoiturageexpress.R;
import com.francisouellet.covoiturageexpress.classes.Commentaire;
import com.francisouellet.covoiturageexpress.classes.ParcoursEtCarte;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentairesAdapter extends ArrayAdapter<Commentaire>{

	private Context m_Context;
	private List<Commentaire> m_ListeCommentaires;
	private int m_LayoutResId;
	
	private Utilisateur utilisateur;
	
	private HttpClient m_ClientHttp = new DefaultHttpClient();
	
	 public CommentairesAdapter(Context p_Context, List<Commentaire> p_ListeCommentaires, int p_LayoutResId) {
		super(p_Context, p_LayoutResId, p_ListeCommentaires);
		this.m_Context = p_Context;
		this.m_ListeCommentaires = p_ListeCommentaires;
		this.m_LayoutResId = p_LayoutResId;
		
		UtilisateurDataSource uds = new UtilisateurDataSource(m_Context);
		uds.open();
		this.utilisateur = uds.getConnectedUser();
		uds.close();
	}
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CommentaireHolder contenant;
		View view = convertView;
		
		if(view == null){
			LayoutInflater inflater = ((Activity)m_Context).getLayoutInflater();
			contenant = new CommentaireHolder();
			view = inflater.inflate(m_LayoutResId, parent, false);
			contenant.texte = (TextView)view.findViewById(R.id.commentaire_texte);
			contenant.auteur = (TextView)view.findViewById(R.id.commentaire_auteur);
			contenant.date = (TextView)view.findViewById(R.id.commentaire_date);
			contenant.score = (TextView)view.findViewById(R.id.commentaire_score);
			view.setTag(contenant);
		}
		else
			contenant = (CommentaireHolder)view.getTag();
		
		Commentaire commentaire = m_ListeCommentaires.get(position);
		
		if(!commentaire.getTexte().isEmpty())
			contenant.texte.setText(commentaire.getTexte());
		
		if(!commentaire.getIdAuteur().isEmpty())
			contenant.auteur.setText(commentaire.getIdAuteur());
		
		if(!commentaire.getTimestampCreation().isEmpty()){
			GregorianCalendar calendrier = new GregorianCalendar();
			calendrier.setTimeInMillis(Long.parseLong(commentaire.getTimestampCreation()));
			
			contenant.date.setText(Util.dateFormat.format(calendrier.getTime()));
		}
		
		contenant.score.setText(commentaire.calculerScore() + "");
			
		return view;
	}
	
	private static class CommentaireHolder{
		TextView texte;
		TextView auteur;
		TextView date;
		TextView score;
	}
	
}
