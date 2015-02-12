package com.francisouellet.covoiturageexpress.database;

import java.util.ArrayList;
import java.util.List;

import com.francisouellet.covoiturageexpress.classes.Utilisateur;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class UtilisateurDataSource extends BaseDataSource<Utilisateur, String> {
	
	public UtilisateurDataSource(Context p_Context) {
		super(p_Context);
	}
	
	// 
	@Override
	public boolean insert(Utilisateur element) {
		ContentValues row = toContentValues(element);
		int row_id = (int)m_Db.insert(DatabaseHelper.TABLE_UTILISATEUR, null, row);
		return row_id == -1 ? false : true;
	}

	@Override
	public boolean update(Utilisateur element) {
		ContentValues row = toContentValues(element);
		int rows_affected = m_Db.update(DatabaseHelper.TABLE_UTILISATEUR, 
				row, DatabaseHelper.COL_COURRIEL + "=" + element.getCourriel(), null);
		return rows_affected > 0 ? true : false;
	}
	
	@Override
	public Utilisateur get(String id) {
		Utilisateur u = null;
		Cursor c = m_Db.query(DatabaseHelper.TABLE_UTILISATEUR, null, DatabaseHelper.COL_COURRIEL + "=" + id,
				null, null, null, null);
		c.moveToFirst();
		if(!c.isAfterLast()){
			u = toElement(c);
		}
		return u;	
	}
	
	public Utilisateur getConnectedUtilisateur(){
		Utilisateur u = null;
		Cursor c = m_Db.query(DatabaseHelper.TABLE_UTILISATEUR, null, DatabaseHelper.COL_ESTCONNECTE + "=1" ,
				null, null, null, null);
		c.moveToFirst();
		if(!c.isAfterLast()){
			u = toElement(c);
		}
		return u;
	}
	
	public Utilisateur getDernierConnecte(){
		Utilisateur u = null;
		Cursor c = m_Db.query(DatabaseHelper.TABLE_UTILISATEUR, null, DatabaseHelper.COL_DERNIERCONNECTE + "=1",
				null, null, null, null);
		c.moveToFirst();
		if(!c.isAfterLast()){
			u = toElement(c);
		}
		return u;
	}
	
	@Override
	public List<Utilisateur> getAll() {
		List<Utilisateur> utilisateurs = new ArrayList<Utilisateur>();
		Cursor c = m_Db.query(DatabaseHelper.TABLE_UTILISATEUR, null, null, null, null, null, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			Utilisateur u = toElement(c);
			utilisateurs.add(u);
			c.moveToNext();
		}
		return utilisateurs;
	}
	
	@Override
	public void remove(String id) {
		m_Db.delete(DatabaseHelper.TABLE_UTILISATEUR, DatabaseHelper.COL_COURRIEL + "=" + id, null);
	}
	
	@Override
	public void removeAll() {
		m_Db.delete(DatabaseHelper.TABLE_UTILISATEUR, null, null);
	}

	@Override
	protected ContentValues toContentValues(Utilisateur element) {
		ContentValues row = new ContentValues();
		row.put(DatabaseHelper.COL_COURRIEL, element.getCourriel());
		row.put(DatabaseHelper.COL_MOTDEPASSE, element.getEncodedPassword());
		row.put(DatabaseHelper.COL_NOTELEPHONE, element.getTelephone());
		row.put(DatabaseHelper.COL_ESTCONNECTE, element.getEstConnecte() ? 1:0);
		row.put(DatabaseHelper.COL_DERNIERCONNECTE, element.getDernierConnecte() ? 1:0);
		return row;
	}

	@Override
	protected Utilisateur toElement(Cursor c) {
		Utilisateur u = new Utilisateur(
				c.getString(c.getColumnIndex(DatabaseHelper.COL_COURRIEL)), 
				c.getString(c.getColumnIndex(DatabaseHelper.COL_NOTELEPHONE)), 
				c.getInt(c.getColumnIndex(DatabaseHelper.COL_ESTCONNECTE)) == 1 ? true : false, 
				c.getInt(c.getColumnIndex(DatabaseHelper.COL_DERNIERCONNECTE)) == 1 ? true : false );
		return u;
	}
	
}
