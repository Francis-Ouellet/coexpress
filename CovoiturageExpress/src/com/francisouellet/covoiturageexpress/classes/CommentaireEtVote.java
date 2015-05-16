package com.francisouellet.covoiturageexpress.classes;

public class CommentaireEtVote {
	private Commentaire m_Commentaire;
	private Boolean m_TypeVote;
	
	public CommentaireEtVote(Commentaire p_Commentaire, Boolean p_TypeVote) {
		this.m_Commentaire = p_Commentaire;
		this.m_TypeVote = p_TypeVote;
	}
	
	public Commentaire getCommentaire() {
		return m_Commentaire;
	}

	public void setCommentaire(Commentaire m_Commentaire) {
		this.m_Commentaire = m_Commentaire;
	}

	public Boolean isTypeVote() {
		return m_TypeVote;
	}

	public void setTypeVote(Boolean m_TypeVote) {
		this.m_TypeVote = m_TypeVote;
	}
}
