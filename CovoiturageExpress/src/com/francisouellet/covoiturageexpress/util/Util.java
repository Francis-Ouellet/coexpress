package com.francisouellet.covoiturageexpress.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

public class Util {
	
	/**
	 * Formatage d'une date et d'une heure
	 */
	public static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
	public static final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
	
	public static final String EXTRA_PARCOURS = "parcours";
	public static final String EXTRA_UTILISATEUR = "utilisateur";
	public static final String EXTRA_ID_UTILISATEUR = "id_utilisateur";
	public static final String EXTRA_TYPE_PROFIL = "type_profil";
	
	//public static final String WEB_SERVICE = "localhost:11080";
	public static final String WEB_SERVICE = "coexpress624fx.appspot.com";
	public static final String REST_UTILISATEURS = "/utilisateurs";
	public static final String REST_PARCOURS = "/parcours";
	public static final String REST_CHERCHER = "/chercher";
	public static final String REST_AJOUTER = "/ajouter";
	public static final String REST_PARTICIPANTS = "/participants";
	public static final String REST_COMMENTAIRES = "/commentaires";
	
	public static final String STATIC_MAPS_API = "maps.googleapis.com";
	public static final String STATIC_MAPS_PATH = "/maps/api/staticmap";
	public static final String STATIC_MAPS_SIZE = "size";
	public static final String STATIC_MAPS_MARKERS = "markers";
	
	/**
	 * Génère un SHA1 à partir d'une chaîne de caractère
	 * Source : http://www.sha1-online.com/sha1-java/
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
    }
	
	/**
	 * Génère un toast court avec la ressource spécifiée
	 * @param context
	 * @param resId
	 */
	public static void easyToast(Context context, int resId){
		easyToast(context, resId, Toast.LENGTH_SHORT);
	}
	/**
	 * Génère un toast avec la ressource et la durée spécifiée
	 * @param context
	 * @param resId
	 * @param length
	 */
	public static void easyToast(Context context, int resId, int length){
		Toast.makeText(context, context.getResources().getText(resId), length).show();
	} 
	/**
	 * Génère un toast court avec le texte spécifié
	 * @param context
	 * @param text
	 */
	public static void easyToast(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Sépare une chaine de caractère généré par la méthode "toString" d'Arraylist en une liste de ses éléments
	 * @param chaine
	 * @param separateur
	 * @return
	 */
	public static List<Integer> toList(String chaine){
		if(chaine != null && !chaine.equals("") && !chaine.equals("[]")){
			String[] elements = chaine.replace("[", "").replace("]", "").split(", ");
			List<Integer> liste = new ArrayList<Integer>();
			for (String string : elements) {
				liste.add(Integer.parseInt(string));
			}
			return liste;
		}
		return null;
	}
	
	public static String obtenirAdresse(double latitude, double longitude, Context context){
		String adresse = null;
		if(Geocoder.isPresent()){
			Geocoder geocoder = new Geocoder(context);
			try{
				List<Address> depart = geocoder.getFromLocation(latitude,longitude, 1);
				if(depart.size() > 0){
					adresse = depart.get(0).getAddressLine(0);
				}
				
			} catch(Exception e){e.printStackTrace();}
		}
		
		return adresse;
	}
}
