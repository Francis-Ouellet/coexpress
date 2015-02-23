package com.francisouellet.covoiturageexpress.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

public class Util {
	
	
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
	public static List<String> toList(String chaine){
		String[] elements = chaine.replace("[", "").replace("]", "").split(" ,");
		List<String> liste = new ArrayList<String>();
		for (String string : elements) {
			liste.add(string);
		}
		return liste;
	}
}
