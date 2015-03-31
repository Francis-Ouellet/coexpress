package com.francisouellet.covoiturageexpress.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	 * @param p Le PArcours a convertir
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
		jsonObj.put("joursRepetes", (ArrayList<Integer>)p.getJoursRepetes());
		jsonObj.put("nbPlaces", p.getNbPlaces());
		jsonObj.put("distanceSupplementaire", p.getDistanceSupplementaire());
		jsonObj.put("notes", p.getNotes());
		jsonObj.put("actif", p.getActif());
		
		return jsonObj;
	}
	
	/**
	 * Converti une chaine de caractères JSON en Utilisateur
	 * @param strJson
	 * @return Une instance d'Utilisateur correspondant à la chaîne
	 * @throws JSONException
	 */
	public static Utilisateur ToUtilisateur(String strJson) throws JSONException{
		Utilisateur u;
		JSONObject jsonU = new JSONObject(strJson);
		u = new Utilisateur(
				jsonU.getString("courriel"),
				jsonU.getString("password"),
				jsonU.getString("nom"),
				jsonU.getString("prenom"),
				jsonU.getString("telephone"), false, false);
		return u;
	}
	
	/**
	 * Converti une chaîne de caractères JSON en Parcours
	 * @param strJson
	 * @return Une instance de Parcours correspondant à la chaîne
	 * @throws JSONException
	 */
	public static Parcours ToParcours(String strJson) throws JSONException{
		Parcours p = null;
		JSONObject jsonP = new JSONObject(strJson);
		List<Integer> joursRepetes = new ArrayList<Integer>();
		JSONArray joursRepetesJSON = jsonP.getJSONArray("joursRepetes");
		for(int i = 0; i < joursRepetesJSON.length(); i++){
			joursRepetes.add(joursRepetesJSON.getInt(i));
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
					jsonP.getString("timestampDepart"), 
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
					jsonP.getString("timestampDepart"), 
					joursRepetes, 
					jsonP.getString("notes"),
					jsonP.getBoolean("actif"));
		}
		
		return p;
	}
	
}
