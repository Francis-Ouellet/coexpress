package com.francisouellet.covoiturageexpress.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.francisouellet.covoiturageexpress.classes.Commentaire;
import com.francisouellet.covoiturageexpress.classes.Parcours;
import com.francisouellet.covoiturageexpress.classes.Utilisateur;

public class JsonParser {
	
	/**
	 * Converti un utilisateur en objet JSON
	 * @param u	L'utilisateur à convertir
	 * @return	Un JSONObject représentant l'utilisateur
	 * @throws JSONException
	 */
	public static JSONObject ToJsonObject(Utilisateur u) throws JSONException{
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("nom", u.getNom());
		jsonObj.put("prenom", u.getPrenom());
		jsonObj.put("telephone", u.getTelephone());
		jsonObj.put("password", u.getEncodedPassword());
		jsonObj.put("courriel", u.getCourriel());
		
		return jsonObj;
	}
	
	/**
	 * Converti un Parcours en objet JSON
	 * @param p Le Parcours a convertir
	 * @return Un JSONObject représentant le parcours 
	 * @throws JSONException
	 */
	public static JSONObject ToJsonObject(Parcours p) throws JSONException{
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("proprietaire", p.getProprietaire());
		jsonObj.put("typeParcours", p.getConducteur());
		jsonObj.put("adresseDepart", p.getAdresseDepart());
		jsonObj.put("departLatitude", p.getDepartLatitude());
		jsonObj.put("departLongitude", p.getDepartLongitude());
		jsonObj.put("adresseDestination", p.getAdresseDestination());
		jsonObj.put("destinationLatitude", p.getDestinationLatitude());
		jsonObj.put("destinationLongitude", p.getDestinationLongitude());
		jsonObj.put("departTimestamp", p.getTimestampDepart());
		jsonObj.put("derniereModificationTimestamp", p.getTimestampDerniereModification());
		jsonObj.put("joursRepetes", (ArrayList<Integer>)p.getJoursRepetes());
		jsonObj.put("nbPlaces", p.getNbPlaces());
		jsonObj.put("distanceSupplementaire", p.getDistanceSupplementaire());
		jsonObj.put("notes", p.getNotes());
		jsonObj.put("actif", p.getActif());
		
		return jsonObj;
	}
	
	/**
	 * Converti un commentaire en objet JSON
	 * @param c	Le commentaire à convertir
	 * @return Un JSONObject représentant le parcours
	 * @throws JSONException
	 */
	public static JSONObject ToJsonObject(Commentaire c) throws JSONException{
		JSONObject jsonObj = new JSONObject();
		
		
		jsonObj.put("auteur", c.getIdAuteur());
		jsonObj.put("timestampCreation", c.getTimestampCreation());
		jsonObj.put("texte", c.getTexte());
		jsonObj.put("upvotes", c.getUpvotes());
		jsonObj.put("downvotes", c.getDownvotes());
		
		return jsonObj;
	}
	
	/**
	 * Converti un JSONObject en Utilisateur
	 * @param jsonU	Objet JSON
	 * @return Une instance d'Utilisateur correspondant au JSON
	 * @throws JSONException
	 */
	public static Utilisateur ToUtilisateur(JSONObject jsonU) throws JSONException{
		Utilisateur u;
		u = new Utilisateur(
				jsonU.getString("courriel"),
				jsonU.getString("password"),
				jsonU.getString("nom"),
				jsonU.getString("prenom"),
				jsonU.getString("telephone"), false, false);
		return u;
	}
	
	/**
	 * Converti un JSONObject en Parcours
	 * @param jsonP
	 * @return Une instance de Parcours correspondant au JSON
	 * @throws JSONException
	 */
	public static Parcours ToParcours(JSONObject jsonP) throws JSONException{
		Parcours p = null;
		List<Integer> joursRepetes = null;
		JSONArray joursRepetesJSON = jsonP.getJSONArray("joursRepetes");
		if(joursRepetesJSON.length() > 0){
			joursRepetes = new ArrayList<Integer>();
			for(int i = 0; i < joursRepetesJSON.length(); i++){
				joursRepetes.add(joursRepetesJSON.getInt(i));
			}
		}
		// Conducteur
		if(jsonP.getBoolean("typeParcours")){ 
			p = new Parcours(
					jsonP.getString("idParcours"), 
					jsonP.getString("proprietaire"), 
					jsonP.getBoolean("typeParcours"), 
					jsonP.getString("adresseDepart"), 
					jsonP.getDouble("departLatitude"), 
					jsonP.getDouble("departLongitude"), 
					jsonP.getString("adresseDestination"), 
					jsonP.getDouble("destinationLatitude"), 
					jsonP.getDouble("destinationLongitude"), 
					jsonP.getString("departTimestamp"),
					jsonP.getString("derniereModificationTimestamp"),
					joursRepetes, 
					jsonP.getInt("nbPlaces"),
					jsonP.getDouble("distanceSupplementaire"),
					jsonP.getString("notes"),
					jsonP.getBoolean("actif"));
		}
		// Passager
		else{
			p = new Parcours(
					jsonP.getString("idParcours"), 
					jsonP.getString("proprietaire"), 
					jsonP.getBoolean("typeParcours"), 
					jsonP.getString("adresseDepart"), 
					jsonP.getDouble("departLatitude"), 
					jsonP.getDouble("departLongitude"), 
					jsonP.getString("adresseDestination"), 
					jsonP.getDouble("destinationLatitude"), 
					jsonP.getDouble("destinationLongitude"), 
					jsonP.getString("departTimestamp"),
					jsonP.getString("derniereModificationTimestamp"),
					joursRepetes, 
					jsonP.getString("notes"),
					jsonP.getBoolean("actif"));
		}
		
		return p;
	}
	
	/**
	 * Converti un JSONObject en Commentaire
	 * @param jsonC
	 * @return Une instance de Commentaire correspondant au JSON
	 * @throws JSONException
	 */
	public static Commentaire ToCommentaire(JSONObject jsonC) throws JSONException{
		Commentaire c = null;
		c = new Commentaire(
				jsonC.getString("idCommentaire"), 
				jsonC.getString("auteur"), 
				jsonC.getString("timestampCreation"), 
				jsonC.getString("texte"));
		c.setUpvotes(Integer.parseInt(jsonC.getString("upvotes")));
		c.setDownvotes(Integer.parseInt(jsonC.getString("downvotes")));
		return c;
	}
	
}
