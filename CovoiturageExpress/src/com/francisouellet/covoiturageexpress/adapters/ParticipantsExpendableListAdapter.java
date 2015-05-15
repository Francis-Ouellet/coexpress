package com.francisouellet.covoiturageexpress.adapters;

import java.util.List;

import com.francisouellet.covoiturageexpress.ProfilUtilisateurActivity;
import com.francisouellet.covoiturageexpress.R;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class ParticipantsExpendableListAdapter extends BaseExpandableListAdapter{
	
	private Context m_Context;
	private List<String> m_Entetes;
	private List<List<Parcours>> m_ListeGroupesParcours;
	
	public ParticipantsExpendableListAdapter(Context p_Context, List<String> p_Entetes, 
			List<List<Parcours>> p_ListeGroupesParcours) {
		this.m_Context = p_Context;
		this.m_Entetes = p_Entetes;
		this.m_ListeGroupesParcours = p_ListeGroupesParcours;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String entete = m_Entetes.get(groupPosition);
		
		if(convertView == null){
			LayoutInflater inflater = ((Activity)this.m_Context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.entete_liste_participants, null);
		}
		
		TextView lblEntete = (TextView)convertView.findViewById(R.id.participants_entete);
		lblEntete.setText(entete);
			
		return convertView;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		final Parcours parcours = (Parcours)getChild(groupPosition, childPosition);
		ParcoursHolder contenant;
		
		if(convertView == null){
			LayoutInflater inflater = ((Activity)this.m_Context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.liste_participants_item, null);
			contenant = new ParcoursHolder();
			contenant.nom = (TextView)convertView.findViewById(R.id.participant_nom);
			contenant.type = (TextView)convertView.findViewById(R.id.participant_type);
			contenant.notes = (TextView)convertView.findViewById(R.id.participant_notes);
			contenant.adresseDepart = (TextView)convertView.findViewById(R.id.participant_adresse_depart);
			contenant.adresseDestination = (TextView)convertView.findViewById(R.id.participant_adresse_destination);
			convertView.setTag(contenant);
		}
		else
			contenant = (ParcoursHolder)convertView.getTag();
		
		contenant.nom.setText(parcours.getProprietaire());
		// Conducteur
		if(parcours.getConducteur())
			contenant.type.setText(m_Context.getText(R.string.lbl_conducteur));
		else
			contenant.type.setText(m_Context.getText(R.string.lbl_passager));
		
		contenant.notes.setText(parcours.getNotes());
		contenant.adresseDepart.setText(parcours.getAdresseDepart());
		contenant.adresseDestination.setText(parcours.getAdresseDestination());
		
		return convertView;
	}
	
	@Override
	public int getGroupCount() {
		return this.m_ListeGroupesParcours.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.m_ListeGroupesParcours.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.m_ListeGroupesParcours.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.m_ListeGroupesParcours.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private static class ParcoursHolder{
		TextView nom;
		TextView type;
		TextView notes;
		TextView adresseDepart;
		TextView adresseDestination;
	}	
}
