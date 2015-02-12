package com.francisouellet.covoiturageexpress.database;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author Francis Ouellet
 *
 * @param <T> Le type de l'élément à enregistrer
 * @param <I> Le type de l'identifiant de l'élément
 */
public abstract class BaseDataSource<T, I> {
	private DatabaseHelper m_Helper;
	protected SQLiteDatabase m_Db;
	
	public BaseDataSource(Context p_Context){
		m_Helper = new DatabaseHelper(p_Context);
	}
	
	/**
	 * Ouvre la connexion à la BD
	 */
	public void open(){
		m_Db = this.m_Helper.getWritableDatabase();
	}
	
	/**
	 * Ferme la connexion à la BD
	 */
	public void close(){
		m_Db.close();
	}
	
	/**
	 * Insére un élément dans la BD
	 * @param element
	 * @return true si fonctionne, false sinon
	 */
	public abstract boolean insert(T element);
	
	/**
	 * Met à jour un élément existant
	 * @param element
	 * @return true si fonctionne, false sinon
	 */
	public abstract boolean update(T element);
	
	/**
	 * Obtient un élément selon un identifiant
	 * @param id
	 * @return L'élément
	 */
	public abstract T get(I id);
	
	/**
	 * Obtient tous les éléments de la BD
	 * @return Liste des éléments
	 */
	public abstract List<T> getAll();
	
	/**
	 * Supprime un élément selon un identifiant
	 * @param id
	 */
	public abstract void remove (I id);
	
	/**
	 * Supprime tous les éléments de la BD
	 */
	public abstract void removeAll();
	
	/**
	 * Méthode utilitaire pour convertir un élément en données de BD
	 * @param element
	 * @return Les données à ajouter dans la BD
	 */
	protected abstract ContentValues toContentValues(T element);
	
	/**
	 * Méthode utilitaire pour convertir un curseur de BD en élément
	 * @param c
	 * @return Une instance d'un élément
	 */
	protected abstract T toElement(Cursor c);
	
}
