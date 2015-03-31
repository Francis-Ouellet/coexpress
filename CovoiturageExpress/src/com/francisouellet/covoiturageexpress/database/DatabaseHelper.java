package com.francisouellet.covoiturageexpress.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	// Version BD
	private final static int DB_VERSION = 5;
	
	// Nom
	private final static String DATABASE_NAME = "covoiturageexpress.sqlite";
	
	// Nom des tables
	public final static String TABLE_UTILISATEUR = "utilisateur";
	public final static String TABLE_PARCOURS = "parcours";
	
	// Nom des colonnes
	public final static String COL_COURRIEL = "courriel";
	public final static String COL_MOTDEPASSE = "motdepasse";
	public final static String COL_NOM = "nom";
	public final static String COL_PRENOM = "prenom";
	public final static String COL_NOTELEPHONE = "notelephone";
	public final static String COL_ESTCONNECTE = "estconnecte";
	public final static String COL_DERNIERCONNECTE = "dernierconnecte";
	
	public final static String COL_ID = "id";
	public final static String COL_CONDUCTEUR = "conducteur";
	public final static String COL_ADRESSEDEPART = "adressedepart";
	public final static String COL_DEPARTLATITUDE = "departlatitude";
	public final static String COL_DEPARTLONGITUDE = "departlongitude";
	public final static String COL_ADRESSEDESTINATION = "adressedestination";
	public final static String COL_DESTINATIONLATITUDE = "destinationlatitude";
	public final static String COL_DESTINATIONLONGITUDE = "destinationlongitude";
	public final static String COL_TIMESTAMPDEPART = "timestampdepart";
	public final static String COL_JOURSREPETES = "joursrepetes";
	public final static String COL_NBPLACES = "nbplaces";
	public final static String COL_DISTANCESUPPLEMENTAIRE = "distancesupplementaire";
	public final static String COL_NOTES = "notes";
	public final static String COL_ACTIF = "actif";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Table des utilisateurs
		db.execSQL("create table " + TABLE_UTILISATEUR + " ("
				+ COL_COURRIEL + " text primary key,"
				+ COL_MOTDEPASSE + " text,"
				+ COL_NOM + " text,"
				+ COL_PRENOM + " text,"
				+ COL_NOTELEPHONE + " text,"
				+ COL_ESTCONNECTE + " integer,"
				+ COL_DERNIERCONNECTE + " integer)");
		// Table des parcours
		db.execSQL("create table " + TABLE_PARCOURS + " ("
				+ COL_ID + " text primary key,"
				+ COL_COURRIEL + " text,"
				+ COL_CONDUCTEUR + " integer,"
				+ COL_ADRESSEDEPART + " text,"
				+ COL_DEPARTLATITUDE + " real,"
				+ COL_DEPARTLONGITUDE + " real,"
				+ COL_ADRESSEDESTINATION + " text,"
				+ COL_DESTINATIONLATITUDE + " real,"
				+ COL_DESTINATIONLONGITUDE + " real,"
				+ COL_TIMESTAMPDEPART + " text,"
				+ COL_JOURSREPETES + " text,"
				+ COL_NBPLACES + " integer,"
				+ COL_DISTANCESUPPLEMENTAIRE + " real,"
				+ COL_NOTES + " text,"
				+ COL_ACTIF + " integer)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_UTILISATEUR);
		db.execSQL("drop table if exists " + TABLE_PARCOURS);
		this.onCreate(db);
	}
	
}
