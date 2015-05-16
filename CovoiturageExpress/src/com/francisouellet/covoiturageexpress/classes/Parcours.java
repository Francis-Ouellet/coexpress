package com.francisouellet.covoiturageexpress.classes;

import java.io.Serializable;
import java.util.List;

import android.location.Geocoder;

/**
 * 
 * @author 	Francis Ouellet
 *			2015-02-20
 */
public class Parcours implements Serializable {

	private static final long serialVersionUID = 8703679374918477232L;
	private String m_Id;
	private String m_Proprietaire;
	private Boolean m_Conducteur;
	private String m_AdresseDepart;
	private Double m_DepartLatitude;
	private Double m_DepartLongitude;
	private String m_AdresseDestination;
	private Double m_DestinationLatitude;
	private Double m_DestinationLongitude;
	private String m_TimestampDepart;
	private String m_TimestampDerniereModification;
	private List<Integer> m_JoursRepetes;
	private int m_NbPlaces;
	private double m_DistanceSupplementaire;
	private String m_Notes;
	private Boolean m_Actif;
	
	/**
	 * Constructeur pour conducteur, avec répétition
	 * @param p_Id
	 * @param p_Proprietaire
	 * @param p_Conducteur				Indique si le parcours est en tant que conducteur (true) ou passager (false)
	 * @param p_AdresseDepart
	 * @param p_DepartLatitude
	 * @param p_DepartLongitude
	 * @param p_AdresseDestination
	 * @param p_DestinationLatitude
	 * @param p_DestinationLongitude
	 * @param p_TimestampDepart
	 * @param p_TimestampDerniereModification
	 * @param p_JoursRepetes			Liste des jours où il faut répéter la demande de parcours
	 * @param p_NbPlaces
	 * @param p_DistanceSupplementaire
	 * @param p_Notes
	 * @param p_Actif
	 */
	public Parcours(
			String p_Id, String p_Proprietaire, Boolean p_Conducteur, 
			String p_AdresseDepart, Double p_DepartLatitude, Double p_DepartLongitude, 
			String p_AdresseDestination, Double p_DestinationLatitude, Double p_DestinationLongitude, 
			String p_TimestampDepart, String p_TimestampDerniereModification,
			List<Integer> p_JoursRepetes, int p_NbPlaces, 
			double p_DistanceSupplementaire, String p_Notes, Boolean p_Actif){
		this.m_Id = p_Id;
		this.m_Proprietaire = p_Proprietaire;
		this.m_Conducteur = p_Conducteur;
		this.m_AdresseDepart = p_AdresseDepart;
		this.m_DepartLatitude = p_DepartLatitude;
		this.m_DepartLongitude = p_DepartLongitude;
		this.m_AdresseDestination = p_AdresseDestination;
		this.m_DestinationLatitude = p_DestinationLatitude;
		this.m_DestinationLongitude = p_DestinationLongitude;
		this.m_TimestampDepart = p_TimestampDepart;
		this.m_TimestampDerniereModification = p_TimestampDerniereModification;
		this.m_JoursRepetes = p_JoursRepetes;
		this.m_NbPlaces = p_NbPlaces;
		this.m_DistanceSupplementaire = p_DistanceSupplementaire;
		this.m_Notes = p_Notes;
		this.m_Actif = p_Actif;
	}
	
	/**
	 * Constructeur pour conducteur, sans répétition
	 * @param p_Id
	 * @param p_Proprietaire
	 * @param p_Conducteur				Indique si le parcours est en tant que conducteur (true) ou passager (false)
	 * @param p_AdresseDepart
	 * @param p_DepartLatitude
	 * @param p_DepartLongitude
	 * @param p_AdresseDestination
	 * @param p_DestinationLatitude
	 * @param p_DestinationLongitude
	 * @param p_TimestampDepart
	 * @param p_TimestampDerniereModification
	 * @param p_NbPlaces
	 * @param p_DistanceSupplementaire
	 * @param p_Notes
	 * @param p_Actif
	 */
	public Parcours(
			String p_Id, String p_Proprietaire, Boolean p_Conducteur, 
			String p_AdresseDepart, Double p_DepartLatitude, Double p_DepartLongitude, 
			String p_AdresseDestination, Double p_DestinationLatitude, Double p_DestinationLongitude, 
			String p_TimestampDepart, String p_TimestampDerniereModification,
			int p_NbPlaces, double p_DistanceSupplementaire, 
			String p_Notes, Boolean p_Actif){
		this.m_Id = p_Id;
		this.m_Proprietaire = p_Proprietaire;
		this.m_Conducteur = p_Conducteur;
		this.m_AdresseDepart = p_AdresseDepart;
		this.m_DepartLatitude = p_DepartLatitude;
		this.m_DepartLongitude = p_DepartLongitude;
		this.m_AdresseDestination = p_AdresseDestination;
		this.m_DestinationLatitude = p_DestinationLatitude;
		this.m_DestinationLongitude = p_DestinationLongitude;
		this.m_TimestampDepart = p_TimestampDepart;
		this.m_TimestampDerniereModification = p_TimestampDerniereModification;
		this.m_NbPlaces = p_NbPlaces;
		this.m_DistanceSupplementaire = p_DistanceSupplementaire;
		this.m_Notes = p_Notes;
		this.m_Actif = p_Actif;
	}
	
	/**
	 * Constructeur pour passager, avec répétitions
	 * @param p_Id
	 * @param p_Proprietaire
	 * @param p_Conducteur				Indique si le parcours est en tant que conducteur (true) ou passager (false)
	 * @param p_AdresseDepart
	 * @param p_DepartLatitude
	 * @param p_DepartLongitude
	 * @param p_AdresseDestination
	 * @param p_DestinationLatitude
	 * @param p_DestinationLongitude
	 * @param p_TimestampDepart
	 * @param p_TimestampDerniereModification
	 * @param p_JoursRepetes			Liste des jours où il faut répéter la demande de parcours
	 * @param p_Notes
	 * @param p_Actif
	 */
	public Parcours(
			String p_Id, String p_Proprietaire, Boolean p_Conducteur, 
			String p_AdresseDepart, Double p_DepartLatitude, Double p_DepartLongitude, 
			String p_AdresseDestination, Double p_DestinationLatitude, Double p_DestinationLongitude, 
			String p_TimestampDepart, String p_TimestampDerniereModification,
			List<Integer> p_JoursRepetes, String p_Notes, Boolean p_Actif){
		this.m_Id = p_Id;
		this.m_Proprietaire = p_Proprietaire;
		this.m_Conducteur = p_Conducteur;
		this.m_AdresseDepart = p_AdresseDepart;
		this.m_DepartLatitude = p_DepartLatitude;
		this.m_DepartLongitude = p_DepartLongitude;
		this.m_AdresseDestination = p_AdresseDestination;
		this.m_DestinationLatitude = p_DestinationLatitude;
		this.m_DestinationLongitude = p_DestinationLongitude;
		this.m_TimestampDepart = p_TimestampDepart;
		this.m_TimestampDerniereModification = p_TimestampDerniereModification;
		this.m_JoursRepetes = p_JoursRepetes;
		this.m_NbPlaces = 1;
		this.m_Notes = p_Notes;
		this.m_Actif = p_Actif;
	}
	
	/**
	 * Constructeur pour passager, sans répétitions
	 * @param p_Id
	 * @param p_Proprietaire
	 * @param p_Conducteur					Indique si le parcours est en tant que conducteur (true) ou passager (false)
	 * @param p_AdresseDepart
	 * @param p_DepartLatitude
	 * @param p_DepartLongitude
	 * @param p_AdresseDestination
	 * @param p_DestinationLatitude
	 * @param p_DestinationLongitude
	 * @param p_TimestampDepart
	 * @param p_TimestampDerniereModification
	 * @param p_Notes
	 * @param p_Actif
	 */
	public Parcours(
			String p_Id, String p_Proprietaire, Boolean p_Conducteur, 
			String p_AdresseDepart, Double p_DepartLatitude, Double p_DepartLongitude, 
			String p_AdresseDestination, Double p_DestinationLatitude, Double p_DestinationLongitude, 
			String p_TimestampDepart, String p_TimestampDerniereModification,
			String p_Notes, Boolean p_Actif){
		this.m_Id = p_Id;
		this.m_Proprietaire = p_Proprietaire;
		this.m_Conducteur = p_Conducteur;
		this.m_AdresseDepart = p_AdresseDepart;
		this.m_DepartLatitude = p_DepartLatitude;
		this.m_DepartLongitude = p_DepartLongitude;
		this.m_AdresseDestination = p_AdresseDestination;
		this.m_DestinationLatitude = p_DestinationLatitude;
		this.m_DestinationLongitude = p_DestinationLongitude;
		this.m_TimestampDepart = p_TimestampDepart;
		this.m_TimestampDerniereModification = p_TimestampDerniereModification;
		this.m_NbPlaces = 1;
		this.m_Notes = p_Notes;
		this.m_Actif = p_Actif;
	}

	public String getId() {
		return m_Id;
	}

	public void setId(String m_Id) {
		this.m_Id = m_Id;
	}

	public String getProprietaire() {
		return m_Proprietaire;
	}

	public void setProprietaire(String m_Proprietaire) {
		this.m_Proprietaire = m_Proprietaire;
	}

	public Boolean getConducteur() {
		return m_Conducteur;
	}

	public void setConducteur(Boolean m_Conducteur) {
		this.m_Conducteur = m_Conducteur;
	}

	public String getAdresseDepart() {
		return m_AdresseDepart;
	}

	public void setAdresseDepart(String m_AdresseDepart) {
		this.m_AdresseDepart = m_AdresseDepart;
	}

	public Double getDepartLatitude() {
		return m_DepartLatitude;
	}

	public void setDepartLatitude(Double m_DepartLatitude) {
		this.m_DepartLatitude = m_DepartLatitude;
	}

	public Double getDepartLongitude() {
		return m_DepartLongitude;
	}

	public void setDepartLongitude(Double m_DepartLongitude) {
		this.m_DepartLongitude = m_DepartLongitude;
	}

	public String getAdresseDestination() {
		return m_AdresseDestination;
	}

	public void setAdresseDestination(String m_AdresseDestination) {
		this.m_AdresseDestination = m_AdresseDestination;
	}

	public Double getDestinationLatitude() {
		return m_DestinationLatitude;
	}

	public void setDestinationLatitude(Double m_DestinationLatitude) {
		this.m_DestinationLatitude = m_DestinationLatitude;
	}

	public Double getDestinationLongitude() {
		return m_DestinationLongitude;
	}

	public void setDestinationLongitude(Double m_DestinationLongitude) {
		this.m_DestinationLongitude = m_DestinationLongitude;
	}

	public String getTimestampDepart() {
		return m_TimestampDepart;
	}

	public void setTimestampDepart(String m_TimestampDepart) {
		this.m_TimestampDepart = m_TimestampDepart;
	}

	public String getTimestampDerniereModification() {
		return m_TimestampDerniereModification;
	}

	public void setTimestampDerniereModification(
			String m_TimestampDerniereModification) {
		this.m_TimestampDerniereModification = m_TimestampDerniereModification;
	}

	public List<Integer> getJoursRepetes() {
		return m_JoursRepetes;
	}

	public void setJoursRepetes(List<Integer> m_JoursRepetes) {
		this.m_JoursRepetes = m_JoursRepetes;
	}

	public int getNbPlaces() {
		return m_NbPlaces;
	}

	public void setNbPlaces(int m_NbPlaces) {
		this.m_NbPlaces = m_NbPlaces;
	}

	public double getDistanceSupplementaire() {
		return m_DistanceSupplementaire;
	}

	public void setDistanceSupplementaire(double m_DistanceSupplementaire) {
		this.m_DistanceSupplementaire = m_DistanceSupplementaire;
	}
	
	public String getNotes() {
		return m_Notes;
	}

	public void setNotes(String m_Notes) {
		this.m_Notes = m_Notes;
	}

	public Boolean getActif() {
		return m_Actif;
	}

	public void setActif(Boolean m_Actif) {
		this.m_Actif = m_Actif;
	}
}
