package com.francisouellet.covoiturageexpress.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	// Version BD
	private final static int DB_VERSION = 2;
	
	// Nom
	private final static String DATABASE_NAME = "covoiturageexpress.sqlite";
	
	// Nom des tables
	public final static String TABLE_UTILISATEUR = "utilisateur";
	
	// Nom des colonnes
	public final static String COL_COURRIEL = "courriel";
	public final static String COL_MOTDEPASSE = "motdepasse";
	public final static String COL_NOM = "nom";
	public final static String COL_PRENOM = "prenom";
	public final static String COL_NOTELEPHONE = "notelephone";
	public final static String COL_ESTCONNECTE = "estconnecte";
	public final static String COL_DERNIERCONNECTE = "dernierconnecte";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Table utilisateur
		db.execSQL("create table " + TABLE_UTILISATEUR + " ("
				+ COL_COURRIEL + " text primary key,"
				+ COL_MOTDEPASSE + " text,"
				+ COL_NOM + " text,"
				+ COL_PRENOM + " text,"
				+ COL_NOTELEPHONE + " text,"
				+ COL_ESTCONNECTE + " integer,"
				+ COL_DERNIERCONNECTE + " integer)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_UTILISATEUR);
		this.onCreate(db);
	}
	
}
