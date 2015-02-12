package com.francisouellet.covoiturageexpress.classes;

public class Utilisateur {
	
	private String m_Courriel;
	private String m_EncodedPassword;
	private String m_Telephone;
	private boolean m_EstConnecte;
	private boolean m_DernierConnecte;
	
	public Utilisateur(String p_Courriel, String p_EncodedPassword, String p_Telephone, boolean p_EstConnecte,
			boolean p_DernierConnecte){
		this.m_Courriel = p_Courriel;
		this.m_EncodedPassword = p_EncodedPassword;
		this.m_Telephone = p_Telephone;
		this.m_EstConnecte = p_EstConnecte;
		this.m_DernierConnecte = p_DernierConnecte;
	}
	
	public Utilisateur(String p_Courriel, String p_Telephone, boolean p_EstConnecte, boolean p_DernierConnecte){
		this.m_Courriel = p_Courriel;
		this.m_Telephone = p_Telephone;
		this.m_EstConnecte = p_EstConnecte;
		this.m_DernierConnecte = p_DernierConnecte;
	}

	/**
	 * @return the Courriel
	 */
	public String getCourriel() {
		return m_Courriel;
	}

	/**
	 * @param m_Courriel the m_Courriel to set
	 */
	public void setCourriel(String m_Courriel) {
		this.m_Courriel = m_Courriel;
	}

	/**
	 * @return the m_EncodedPassword
	 */
	public String getEncodedPassword() {
		return m_EncodedPassword;
	}

	/**
	 * @param m_EncodedPassword the m_EncodedPassword to set
	 */
	public void setEncodedPassword(String m_EncodedPassword) {
		this.m_EncodedPassword = m_EncodedPassword;
	}

	/**
	 * @return the m_Telephone
	 */
	public String getTelephone() {
		return m_Telephone;
	}

	/**
	 * @param m_Telephone the m_Telephone to set
	 */
	public void setTelephone(String m_Telephone) {
		this.m_Telephone = m_Telephone;
	}

	/**
	 * @return the m_EstConnecte
	 */
	public boolean getEstConnecte() {
		return m_EstConnecte;
	}

	/**
	 * @param m_EstConnecte the m_EstConnecte to set
	 */
	public void setEstConnecte(boolean m_EstConnecte) {
		this.m_EstConnecte = m_EstConnecte;
	}

	/**
	 * @return the m_DernierConnecte
	 */
	public boolean getDernierConnecte() {
		return m_DernierConnecte;
	}

	/**
	 * @param m_DernierConnecte the m_DernierConnecte to set
	 */
	public void setDernierConnecte(boolean m_DernierConnecte) {
		this.m_DernierConnecte = m_DernierConnecte;
	}
}
