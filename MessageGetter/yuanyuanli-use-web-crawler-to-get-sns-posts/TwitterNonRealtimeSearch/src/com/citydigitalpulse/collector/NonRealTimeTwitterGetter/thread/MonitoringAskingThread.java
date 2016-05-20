/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Yuanyuan Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.thread;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.AskingInterface;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml.AskingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml.QueuingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.geo.GetGeoDetails;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.geo.SearchPlaces;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.asking;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.placeID;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.queuing;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool.Tools;

import twitter4j.GeoLocation;
import twitter4j.Place;

/**
 * Scan asking table and get requirements. Get place IDs based on latitude and
 * longtude.Store obtained information into queuing table and change the status
 * of asking table. Status in asking table: 0,User asked but not queued in
 * queuing table or started; 1, user requests accepted and queuing; 2, working
 * on this request; 3, no results found for this request; 4, finished; 5, failed
 * to get geometry information, wrong latitude and longitude pair.
 *
 */
public class MonitoringAskingThread extends ServiceThread {
	private int time;// waiting time
	private boolean isRunning = false;
	private AskingInterface inforGetter;
	private QueuingInterface_MySQL saveQueuingMsg;
	private SearchPlaces place_id_getter;
	private GetGeoDetails getGeoDetails;

	public MonitoringAskingThread(int time) {
		this.time = time;
		this.settName(this.getClass().getSimpleName());
		this.inforGetter = new AskingInterface_MySQL();
		this.saveQueuingMsg = new QueuingInterface_MySQL();
		this.place_id_getter = new SearchPlaces();
		this.getGeoDetails = new GetGeoDetails();

	}

	@Override
	public void run() {
		super.run();
		isRunning = true;
		ArrayList<asking> askingPlaces;
		ArrayList<queuing> queList;
		while (isRunning) {
			// 1.Scan asking table, get all the requirement with status 0
			System.out.println("Scaning Asking table");
			askingPlaces = (ArrayList<asking>) inforGetter.GetAskingInfo(0);
			queList = new ArrayList<queuing>();
			// 2. Use the latitude and longitude get place IDs
			for (int i = 0; i < askingPlaces.size(); i++) {
				ArrayList<Place> places = new ArrayList<Place>();
				double lat = askingPlaces.get(i).get_latitude();
				double lon = askingPlaces.get(i).get_longitude();
				String[] latlong = { Double.toString(lat), Double.toString(lon) };
				ArrayList<String> askingPlacesIDs = place_id_getter.getPlaceID(latlong);
				if (askingPlacesIDs == null) {
					// Failed to retrieve places No place associated with the
					// Given latitude longitude pair Set status in asking table
					// to 5
					inforGetter.ChangeAskingStatus(askingPlaces.get(i).get_num_id(), 5);
					continue;
				} else {
					// 3. Get Place Object from place ID
					// And save to queuing table
					// Once saved change the status in asking table to 1
					// Assign places IDs to Place obejct
					for (int j = 0; j < askingPlacesIDs.size(); j++) {
						// If the returned place has no value then continue to
						// next
						Place placeDetector = getGeoDetails.getLatitudeLongitude(askingPlacesIDs.get(j));
						// API limit exceed or no place associated with that
						// place
						// id
						if (placeDetector == null) {
							continue;
						} else {
							places.add(placeDetector);
							// System.out.println("Get places ");
						}
					}
				}

				// 4. Save places to queuing table
				for (int p = 0; p < places.size(); p++) {
					// 5. Use the average values of latitude and longitude as
					// the place id's latitude and longitude
					double latTempDouble = 0;
					double longTempDouble = 0;
					// if this place id has been saved
					if (Tools.cacheUpdatePlacesIDs.containsKey(places.get(p).getId())) {
						latTempDouble = Tools.cacheUpdatePlacesIDs.get(places.get(p).getId()).get_Latitude();
						longTempDouble = Tools.cacheUpdatePlacesIDs.get(places.get(p).getId()).get_Longitude();
					} else {
						// no such place id saved
						// closed polygon set size to be -1
						int size = 0;
						GeoLocation[][] location = places.get(p).getBoundingBoxCoordinates();
						for (int q0 = 0; q0 < location.length; q0++) {
							for (int q1 = 0; q1 < location[q0].length - 1; q1++) {
								latTempDouble = latTempDouble + location[q0][q1].getLatitude();
								longTempDouble = longTempDouble + location[q0][q1].getLongitude();
								size++;
							}
						}
						latTempDouble = latTempDouble / (double) size;
						longTempDouble = longTempDouble / (double) size;
						// save new place id into d
						placeID newplaceid = new placeID();
						newplaceid.set_Placeid(places.get(p).getId());
						newplaceid.set_Latitude(latTempDouble);
						newplaceid.set_Longitude(longTempDouble);
						Tools.cacheUpdatePlacesIDs.put(newplaceid.get_Placeid(), newplaceid);

					}

					long startTimeStamp = 0;
					long endTimeStamp = 0;
					// To get the tweets of one day we need the timestamp from
					// 0:00 AM to 24:00 PM
					String startDate = askingPlaces.get(i).get_start_date() + " 00:00:00";
					String endDate = askingPlaces.get(i).get_end_date() + " 24:00:00";
					try {
						startTimeStamp = Tools.getTimeStamp(startDate);
						endTimeStamp = Tools.getTimeStamp(endDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					/*
					 * To get a full set of tweets, we will try in 4 ways.A: use
					 * timestamp and place ID; B: use timestamp and place name;
					 * C: use DATE and place id; D use DATE and place id.
					 */

					/*
					 * save date. We need the days before and after the day that
					 * a user asks for. For example, to get the tweets of 15th
					 * of a month, we need set the start date as 14th and end
					 * date as 16th.
					 */
					// Save the date
					queuing queDate = new queuing();
					queDate.set_task_id(askingPlaces.get(i).get_num_id());
					queDate.set_place_name(places.get(p).getName());
					queDate.set_streetAddress(places.get(p).getStreetAddress());
					queDate.set_country(places.get(p).getCountry());
					queDate.set_placeType(places.get(p).getPlaceType());
					queDate.set_place_fullName(places.get(p).getFullName());
					queDate.set_boundingBoxType(places.get(p).getBoundingBoxType());
					queDate.set_boundingBoxCoordinatesLatitude(latTempDouble);
					queDate.set_boundingBoxCoordinatesLongitude(longTempDouble);
					queDate.set_place_id(places.get(p).getId());
					queDate.set_in_what_lan(askingPlaces.get(i).get_in_what_lan());
					queDate.set_message_from(askingPlaces.get(i).get_message_from());
					queDate.set_status(0);// ready to start
					// save date
					try {
						queDate.set_start_date(Tools.formatYesterday(askingPlaces.get(i).get_start_date()));
						queDate.set_end_date(Tools.formatTomorrow(askingPlaces.get(i).get_end_date()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					queList.add(queDate);
					// If user asks for multiple days, we need to divided into
					// several continuously days.
					queuing queTimeStampFull = new queuing();
					queTimeStampFull.set_task_id(askingPlaces.get(i).get_num_id());
					queTimeStampFull.set_place_name(places.get(p).getName());
					queTimeStampFull.set_streetAddress(places.get(p).getStreetAddress());
					queTimeStampFull.set_country(places.get(p).getCountry());
					queTimeStampFull.set_placeType(places.get(p).getPlaceType());
					queTimeStampFull.set_place_fullName(places.get(p).getFullName());
					queTimeStampFull.set_boundingBoxType(places.get(p).getBoundingBoxType());
					queTimeStampFull.set_boundingBoxCoordinatesLatitude(latTempDouble);
					queTimeStampFull.set_boundingBoxCoordinatesLongitude(longTempDouble);
					queTimeStampFull.set_place_id(places.get(p).getId());
					queTimeStampFull.set_in_what_lan(askingPlaces.get(i).get_in_what_lan());
					queTimeStampFull.set_message_from(askingPlaces.get(i).get_message_from());
					queTimeStampFull.set_status(0);// ready to start
					queTimeStampFull.set_start_date(String.valueOf(startTimeStamp - 43200));
					queTimeStampFull.set_end_date(String.valueOf(endTimeStamp + 43200));
					queList.add(queTimeStampFull);
					if (((endTimeStamp - startTimeStamp) / 86400) > 1) {
						long tempStarteTimeStamp = startTimeStamp - 86400;
						long tempEndTimeStamp = endTimeStamp + 86400;

						while (tempStarteTimeStamp <= tempEndTimeStamp) {
							queuing queTimeStamp = new queuing();
							queTimeStamp.set_task_id(askingPlaces.get(i).get_num_id());
							queTimeStamp.set_place_name(places.get(p).getName());
							queTimeStamp.set_streetAddress(places.get(p).getStreetAddress());
							queTimeStamp.set_country(places.get(p).getCountry());
							queTimeStamp.set_placeType(places.get(p).getPlaceType());
							queTimeStamp.set_place_fullName(places.get(p).getFullName());
							queTimeStamp.set_boundingBoxType(places.get(p).getBoundingBoxType());
							queTimeStamp.set_boundingBoxCoordinatesLatitude(latTempDouble);
							queTimeStamp.set_boundingBoxCoordinatesLongitude(longTempDouble);
							queTimeStamp.set_place_id(places.get(p).getId());
							queTimeStamp.set_in_what_lan(askingPlaces.get(i).get_in_what_lan());
							queTimeStamp.set_message_from(askingPlaces.get(i).get_message_from());
							queTimeStamp.set_status(0);// ready to start
							queTimeStamp.set_start_date(String.valueOf(tempStarteTimeStamp));
							queTimeStamp.set_end_date(String.valueOf(tempStarteTimeStamp + 86400));
							tempStarteTimeStamp = tempStarteTimeStamp + 86400;
							queList.add(queTimeStamp);
						}
					}

					// }
				}
				System.out.println("Writing data into Queuing table");
				saveQueuingMsg.insert(queList);
			}
			// Change the status in asking table
			HashSet<Integer> asks = new HashSet<Integer>();
			for (int m = 0; m < queList.size(); m++) {
				asks.add(queList.get(m).get_task_id());
			}
			inforGetter.ChangeAskingStatus(asks, 1);
			try {
				sleep(time);
			} catch (InterruptedException e) {
				isRunning = false;
				// e.printStackTrace();
			}
		}
	}

	@Override
	public void stopMe() {
		isRunning = false;
		if (this.isAlive()) {
			super.interrupt();
		}
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean isRunning() {
		return isRunning;
	}

}