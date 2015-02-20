package com.francisouellet.covoiturageexpress.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	
	public static void easyToast(Context context, int resId){
		easyToast(context, resId, Toast.LENGTH_SHORT);
	}
	public static void easyToast(Context context, int resId, int length){
		Toast.makeText(context, context.getResources().getText(resId), length).show();
	} 
}
