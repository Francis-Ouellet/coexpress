package com.francisouellet.covoiturageexpress;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import com.francisouellet.covoiturageexpress.classes.Utilisateur;
import com.francisouellet.covoiturageexpress.database.UtilisateurDataSource;
import com.francisouellet.covoiturageexpress.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreationCompteActivity extends Activity {

	private EditText lblCourriel;
	private EditText lblConfirmationCourriel;
	private EditText lblMotDePasse;
	private EditText lblConfirmationMotDePasse;
	private EditText lblPrenom;
	private EditText lblNom;
	private EditText lblNoTelephone;
	
	private String courriel;
	private String confirmationCourriel;
	private String motDePasse;
	private String confirmationMotDePasse;
	private String prenom;
	private String nom;
	private String noTelephone;
	
	private Utilisateur utilisateur;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creation_compte);
		
		// Récupération des champs
		this.lblCourriel = (EditText)this.findViewById(R.id.creation_compte_courriel);
		this.lblConfirmationCourriel = (EditText)this.findViewById(R.id.creation_compte_confirmation_courriel);
		this.lblMotDePasse = (EditText)this.findViewById(R.id.creation_compte_mot_de_passe);
		this.lblConfirmationMotDePasse = (EditText)this.findViewById(R.id.creation_compte_confirmation_mot_de_passe);
		this.lblPrenom = (EditText)this.findViewById(R.id.creation_compte_prenom);
		this.lblNom = (EditText)this.findViewById(R.id.creation_compte_nom);
		this.lblNoTelephone = (EditText)this.findViewById(R.id.creation_compte_no_telephone);
	}

	public void CreerCompte(View v){
		
		// Récupération du texte des champs
		this.courriel = lblCourriel.getText().toString().trim();
		this.confirmationCourriel = lblConfirmationCourriel.getText().toString().trim();
		this.motDePasse = lblMotDePasse.getText().toString().trim();
		this.confirmationMotDePasse = lblConfirmationMotDePasse.getText().toString().trim();
		this.prenom = lblPrenom.getText().toString().trim();
		this.nom = lblNom.getText().toString().trim();
		this.noTelephone = lblNoTelephone.getText().toString().trim();
		
		
		// Validation que les champs contiennent quelquechose
		if(	!this.courriel.isEmpty() 	&& 	!this.confirmationCourriel.isEmpty()	&& 
			!this.motDePasse.isEmpty() 	&& 	!this.confirmationMotDePasse.isEmpty()	&&
			!this.prenom.isEmpty()		&&	!this.nom.isEmpty()						&&
			!this.noTelephone.isEmpty()) {
			// Validation que le courriel et la confirmation sont pareils
			if(courriel.equals(confirmationCourriel)){
				// Validation du courriel
				if(validationCourriel(courriel)){
					// Validation que le mot de passe et la confirmation sont pareils
					if(motDePasse.equals(confirmationMotDePasse)){
						// Encrypte le mot de passe
						try{ motDePasse = Util.sha1(motDePasse); }
						catch(NoSuchAlgorithmException e){}
						
						utilisateur = new Utilisateur(courriel, motDePasse, nom, prenom, noTelephone, false, false);
						
						if(enregistrementLocalUtilisateur(utilisateur)){
							Util.easyToast(this, R.string.txt_utilisateur_cree);
							this.finish();
						}
						else
							Util.easyToast(this, R.string.txt_utilisateur_erreur_creation);
						
					} else
						Util.easyToast(this, R.string.txt_mot_de_passe_different);
				} else
					Util.easyToast(this, R.string.txt_courriel_invalide);
			} else 
				Util.easyToast(this, R.string.txt_courriel_different);
		} else 
			Util.easyToast(this, R.string.txt_tous_les_champs);
	}
	
	private boolean validationCourriel(String p_Courriel){
		return Pattern.matches("^.+@.+\\..+$", p_Courriel);
	}
	
	private boolean enregistrementLocalUtilisateur(Utilisateur p_Utilisateur){
		UtilisateurDataSource uds = new UtilisateurDataSource(this);
		try{
			uds.open();
			Boolean valide = uds.insert(p_Utilisateur);
			uds.close();
			return valide;
		}
		catch(Exception e){return false;}
	}
}
