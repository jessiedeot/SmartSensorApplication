package com.project;

import android.graphics.Bitmap;

public class PlaceInfo {

	protected String placeText;
	protected String placeToken;
	protected Bitmap placebitMap;
	protected String placeUrl;
	
	public String getPlaceText() {
		return placeText;
	}
	public void setPlaceText(String placeText) {
		this.placeText = placeText;
	}
	public String getPlaceToken() {
		return placeToken;
	}
	public void setPlaceToken(String placeToken) {
		this.placeToken = placeToken;
	}
	public Bitmap getPlacebitMap() {
		return placebitMap;
	}
	public void setPlacebitMap(Bitmap placebitMap) {
		this.placebitMap = placebitMap;
	}
	public String getPlaceUrl() {
		return placeUrl;
	}
	public void setPlaceUrl(String placeUrl) {
		this.placeUrl = placeUrl;
	}
	
	
}