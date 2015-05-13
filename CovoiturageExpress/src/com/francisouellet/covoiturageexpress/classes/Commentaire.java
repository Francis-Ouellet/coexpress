package com.francisouellet.covoiturageexpress.classes;

/**
 * Classe représantant un commentaire sur la fiche d'un utilisateur
 * @author Francis Ouellet
 *
 */
public class Commentaire {
	private String m_Id;
	private String m_IdAuteur;
	private String m_TimestampCreation;
	private String m_Texte;
	private int m_Upvotes;
	private int m_Downvotes;
	
	/**
	 * Constructeur de base, sans votes
	 * @param p_Id					L'identifiant du commentaire
	 * @param p_IdAuteur			L'identifiant de l'auteur du commentaire
	 * @param p_TimestampCreation	Le moment où le commentaire a été créé
	 * @param p_Texte				Le texte du commentaire
	 */
	public Commentaire(String p_Id, String p_IdAuteur, String p_TimestampCreation, String p_Texte) {
		this.m_Id = p_Id;
		this.m_IdAuteur = p_IdAuteur;
		this.m_TimestampCreation = p_TimestampCreation;
		this.m_Texte = p_Texte;
		this.m_Upvotes = 0;
		this.m_Downvotes = 0;
	}
	
	/**
	 * Constructeur 
	 * @param p_Id					L'identifiant du commentaire
	 * @param p_IdAuteur			L'identifiant de l'auteur du commentaire
	 * @param p_TimestampCreation	Le moment où le commentaire a été créé
	 * @param p_Texte				Le texte du commentaire
	 * @param p_Upvotes				Nombre de votes positifs
	 * @param p_Downvotes			Nombre de votes négatifs
	 */
	public Commentaire(String p_Id, String p_IdAuteur, String p_TimestampCreation, String p_Texte, int p_Upvotes, int p_Downvotes) {
		this.m_Id = p_Id;
		this.m_IdAuteur = p_IdAuteur;
		this.m_TimestampCreation = p_TimestampCreation;
		this.m_Texte = p_Texte;
		this.m_Upvotes = p_Upvotes;
		this.m_Downvotes = p_Downvotes;
	}
	
	/**
	 * Calcule le score du commentaire
	 * @return Le nombre "d'upvotes" moins le nombre de "downvotes"
	 */
	public int calculerScore(){
		return this.m_Upvotes - this.m_Downvotes;
	}
	
	/**
	 * Ajoute un point au compteur de votes
	 */
	public void ajouterUpvote(){
		this.m_Upvotes++;
	}
	
	/**
	 * Enlève un point au compteur de votes
	 */
	public void ajouterDownvote(){
		this.m_Downvotes++;
	}

	public String getId() {
		return m_Id;
	}

	public void setId(String m_Id) {
		this.m_Id = m_Id;
	}

	public String getIdAuteur() {
		return m_IdAuteur;
	}

	public void setIdAuteur(String m_IdAuteur) {
		this.m_IdAuteur = m_IdAuteur;
	}

	public String getTimestampCreation() {
		return m_TimestampCreation;
	}

	public void setTimestampCreation(String m_TimestampCreation) {
		this.m_TimestampCreation = m_TimestampCreation;
	}

	public String getTexte() {
		return m_Texte;
	}

	public void setTexte(String m_Texte) {
		this.m_Texte = m_Texte;
	}

	public int getUpvotes() {
		return m_Upvotes;
	}

	public void setUpvotes(int m_Upvotes) {
		this.m_Upvotes = m_Upvotes;
	}

	public int getDownvotes() {
		return m_Downvotes;
	}

	public void setDownvotes(int m_Downvotes) {
		this.m_Downvotes = m_Downvotes;
	}
	
}
