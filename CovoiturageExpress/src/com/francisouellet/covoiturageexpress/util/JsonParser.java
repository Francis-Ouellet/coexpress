package com.francisouellet.covoiturageexpress.util;

import org.json.JSONException;
import org.json.JSONObject;

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
	
}
