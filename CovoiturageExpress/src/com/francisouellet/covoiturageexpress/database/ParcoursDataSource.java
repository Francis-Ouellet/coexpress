package com.francisouellet.covoiturageexpress.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.util.Util;

public class ParcoursDataSource extends BaseDataSource<Parcours, String> {

	public ParcoursDataSource(Context p_Context) {
		super(p_Context);
	}

	@Override
	public boolean insert(Parcours element) {
		ContentValues row = toContentValues(element);
		int row_id = (int)m_Db.insert(DatabaseHelper.TABLE_PARCOURS, null, row);
		return row_id == -1 ? false : true;
	}

	@Override
	public boolean update(Parcours element) {
		ContentValues row = toContentValues(element);
		int rows_affected = m_Db.update(DatabaseHelper.TABLE_PARCOURS, 
				row, DatabaseHelper.COL_ID + "= '" + element.getId() + "'", null);
		return rows_affected > 0 ? true : false;
	}

	@Override
	public Parcours get(String id) {
		Parcours p = null;
		Cursor c = m_Db.query(DatabaseHelper.TABLE_PARCOURS, null, DatabaseHelper.COL_ID + "= '" + id + "'",
				null, null, null, null);
		c.moveToFirst();
		if(!c.isAfterLast()){
			p = toElement(c);
		}
		return p;
	}

	@Override
	public List<Parcours> getAll() {
		List<Parcours> parcours = new ArrayList<Parcours>();
		Cursor c = m_Db.query(DatabaseHelper.TABLE_PARCOURS, null, null, null, null, null, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			Parcours p = toElement(c);
			parcours.add(p);
			c.moveToNext();
		}
		return parcours;
	}
	
	public List<Parcours> getAllByOwner(String owner){
		List<Parcours> parcours = new ArrayList<Parcours>();
		Cursor c = m_Db.query(DatabaseHelper.TABLE_PARCOURS, null, DatabaseHelper.COL_COURRIEL + "= '" + owner + "'", 
				null, null, null, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			Parcours p = toElement(c);
			parcours.add(p);
			c.moveToNext();
		}
		return parcours;
	}

	@Override
	public void remove(String id) {
		m_Db.delete(DatabaseHelper.TABLE_PARCOURS, DatabaseHelper.COL_ID + "= '" + id + "'", null);
		
	}

	@Override
	public void removeAll() {
		m_Db.delete(DatabaseHelper.TABLE_PARCOURS, null, null);
	}

	@Override
	protected ContentValues toContentValues(Parcours element) {
		ContentValues row = new ContentValues();
		row.put(DatabaseHelper.COL_ID, element.getId());
		row.put(DatabaseHelper.COL_COURRIEL, element.getProprietaire());
		row.put(DatabaseHelper.COL_CONDUCTEUR, element.getConducteur() ? 1:0);
		row.put(DatabaseHelper.COL_ADRESSEDEPART, element.getAdresseDepart());
		row.put(DatabaseHelper.COL_DEPARTLATITUDE, element.getDepartLatitude());
		row.put(DatabaseHelper.COL_DEPARTLONGITUDE, element.getDepartLongitude());
		row.put(DatabaseHelper.COL_ADRESSEDESTINATION, element.getAdresseDestination());
		row.put(DatabaseHelper.COL_DESTINATIONLATITUDE, element.getDestinationLatitude());
		row.put(DatabaseHelper.COL_DESTINATIONLONGITUDE, element.getDestinationLongitude());
		row.put(DatabaseHelper.COL_TIMESTAMPDEPART, element.getTimestampDepart());
		if(element.getJoursRepetes() != null)
			row.put(DatabaseHelper.COL_JOURSREPETES, ((ArrayList<Integer>)element.getJoursRepetes()).toString());
		else
			row.put(DatabaseHelper.COL_JOURSREPETES, "");
		row.put(DatabaseHelper.COL_NBPLACES, element.getNbPlaces());
		row.put(DatabaseHelper.COL_DISTANCESUPPLEMENTAIRE, element.getDistanceSupplementaire());
		row.put(DatabaseHelper.COL_NOTES, element.getNotes());
		row.put(DatabaseHelper.COL_ACTIF, element.getActif() ? 1:0);
		return row;
	}

	@Override
	protected Parcours toElement(Cursor c) {
		Parcours parcours = new Parcours(
				c.getString(c.getColumnIndex(DatabaseHelper.COL_ID)),
				c.getString(c.getColumnIndex(DatabaseHelper.COL_COURRIEL)),
				c.getInt(c.getColumnIndex(DatabaseHelper.COL_CONDUCTEUR)) == 1 ? true:false,
				c.getString(c.getColumnIndex(DatabaseHelper.COL_ADRESSEDEPART)),
				c.getDouble(c.getColumnIndex(DatabaseHelper.COL_DEPARTLATITUDE)),
				c.getDouble(c.getColumnIndex(DatabaseHelper.COL_DEPARTLONGITUDE)),
				c.getString(c.getColumnIndex(DatabaseHelper.COL_ADRESSEDESTINATION)),
				c.getDouble(c.getColumnIndex(DatabaseHelper.COL_DESTINATIONLATITUDE)),
				c.getDouble(c.getColumnIndex(DatabaseHelper.COL_DESTINATIONLONGITUDE)),
				c.getString(c.getColumnIndex(DatabaseHelper.COL_TIMESTAMPDEPART)),
				Util.toList(c.getString(c.getColumnIndex(DatabaseHelper.COL_JOURSREPETES))),
				c.getInt(c.getColumnIndex(DatabaseHelper.COL_NBPLACES)),
				c.getDouble(c.getColumnIndex(DatabaseHelper.COL_DISTANCESUPPLEMENTAIRE)),
				c.getString(c.getColumnIndex(DatabaseHelper.COL_NOTES)),
				c.getInt(c.getColumnIndex(DatabaseHelper.COL_ACTIF)) == 1 ? true:false);
		return parcours;
	}

}
