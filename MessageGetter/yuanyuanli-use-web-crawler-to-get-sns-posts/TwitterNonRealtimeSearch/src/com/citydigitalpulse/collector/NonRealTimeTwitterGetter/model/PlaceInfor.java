package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model;

import twitter4j.GeoLocation;

public class PlaceInfor {
	private String Name = "";
	private String StreetAddress = "";
	private String CountryCode = "";
	private String Id = "";
	private String Country = "";
	private String PlaceType = "";
	private String URL = "";
	private String FullName = "";
	private String BoundingBoxType = "";
	private GeoLocation[][] BoundingBoxCoordinates;
	private String GeometryType = "";

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getStreetAddress() {
		return StreetAddress;
	}

	public void setStreetAddress(String StreetAddress) {
		this.StreetAddress = StreetAddress;
	}

	public String CountryCode() {
		return CountryCode;
	}

	public void setCountryCode(String CountryCode) {
		this.CountryCode = CountryCode;
	}

	public String getId() {
		return Id;
	}

	public void setId(String Id) {
		this.Id = Id;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String Country) {
		this.Country = Country;
	}

	public String getPlaceType() {
		return PlaceType;
	}

	public void setPlaceType(String PlaceType) {
		this.PlaceType = PlaceType;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String FullName) {
		this.FullName = FullName;
	}

	public String getBoundingBoxType() {
		return BoundingBoxType;
	}

	public void setBoundingBoxType(String BoundingBoxType) {
		this.BoundingBoxType = BoundingBoxType;
	}

	public GeoLocation[][] getBoundingBoxCoordinates() {
		return BoundingBoxCoordinates;
	}

	public void setBoundingBoxCoordinates(GeoLocation[][] BoundingBoxCoordinates) {
		this.BoundingBoxCoordinates = BoundingBoxCoordinates;
	}

	public String getGeometryType() {
		return GeometryType;
	}

	public void setGeometryType(String GeometryType) {
		this.GeometryType = GeometryType;
	}

}
