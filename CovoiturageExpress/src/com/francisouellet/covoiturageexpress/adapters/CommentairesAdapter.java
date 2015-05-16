package com.francisouellet.covoiturageexpress.adapters;

import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.francisouellet.covoiturageexpress.R;
import com.francisouellet.covoiturageexpress.classes.Commentaire;
import com.francisouellet.covoiturageexpress.classes.CommentaireEtVote;
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

public class CommentairesAdapter extends ArrayAdapter<CommentaireEtVote>{

	private Context m_Context;
	private List<CommentaireEtVote> m_ListeCommentaires;
	private int m_LayoutResId;
	
	
	 public CommentairesAdapter(Context p_Context, List<CommentaireEtVote> p_ListeCommentaires, int p_LayoutResId) {
		super(p_Context, p_LayoutResId, p_ListeCommentaires);
		this.m_Context = p_Context;
		this.m_ListeCommentaires = p_ListeCommentaires;
		this.m_LayoutResId = p_LayoutResId;
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
			contenant.upvote = (ImageView)view.findViewById(R.id.commentaire_upvote);
			contenant.downvote = (ImageView)view.findViewById(R.id.commentaire_downvote);
			view.setTag(contenant);
		}
		else
			contenant = (CommentaireHolder)view.getTag();
		
		Commentaire commentaire = m_ListeCommentaires.get(position).getCommentaire();
		Boolean typeVote = m_ListeCommentaires.get(position).isTypeVote();
		
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
		
		// Si typeVote true, il s'agit d'un vote positif. Vote négatif sinon. 
		if(typeVote != null)
			if(typeVote)
				contenant.upvote.setImageResource(R.drawable.ic_thumb_up_black_24dp);
			else
				contenant.downvote.setImageResource(R.drawable.ic_thumb_down_black_24dp);
			
		return view;
	}
	
	private static class CommentaireHolder{
		TextView texte;
		TextView auteur;
		TextView date;
		TextView score;
		ImageView upvote;
		ImageView downvote;
	}
	
}
