package com.francisouellet.covoiturageexpress.classes;

import android.graphics.Bitmap;

public class ParcoursEtCarte{
	private Parcours parcours;
	private Bitmap carte;
	
	public ParcoursEtCarte(Parcours parcours, Bitmap carte) {
		this.parcours = parcours;
		this.carte = carte;
	}
	
	public ParcoursEtCarte(Parcours parcours){
		this.parcours = parcours;
		this.carte = null;
	}

	public Parcours getParcours() {
		return parcours;
	}

	public void setParcours(Parcours parcours) {
		this.parcours = parcours;
	}

	public Bitmap getCarte() {
		return carte;
	}

	public void setCarte(Bitmap carte) {
		this.carte = carte;
	}
}
