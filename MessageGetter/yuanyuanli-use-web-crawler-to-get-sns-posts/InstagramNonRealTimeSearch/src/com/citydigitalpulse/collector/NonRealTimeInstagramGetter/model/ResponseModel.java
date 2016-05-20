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
package com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response model of GET urlConnection
 */
public class ResponseModel {
	@JsonIgnore
	private String attribution;
	@JsonProperty("videos")
	private Video videos;
	@JsonProperty("tags")
	private String[] tags;
	@JsonProperty("type")
	private String type;
	@JsonProperty("location")
	private Location location;
	@JsonProperty("comments")
	private Comments comments;
	@JsonProperty("filter")
	private String filter;
	@JsonProperty("created_time")
	private long created_time;
	@JsonProperty("link")
	private String link;
	@JsonProperty("likes")
	private Like likes;
	@JsonProperty("images")
	private Images images;
	@JsonProperty("users_in_photo")
	private UserInPhoto[] users_in_photo;
	@JsonProperty("caption")
	private Caption caption;
	@JsonProperty("user_has_liked")
	private boolean user_has_liked;
	@JsonProperty("id")
	private String id;
	@JsonProperty("user")
	private User user;

	public static class User {
		private String username;;
		private String profile_picture;
		private long id;
		private String full_name;

		public String getUsername() {
			return this.username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getProfile_picture() {
			return this.profile_picture;
		}

		public void setProfile_picture(String profile_picture) {
			this.profile_picture = profile_picture;
		}

		public long getId() {
			return this.id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getFull_name() {
			return this.full_name;
		}

		public void setFull_name(String full_name) {
			this.full_name = full_name;
		}

	}

	public Video getVideos() {
		return this.videos;
	}

	public void setVideos(Video videos) {
		this.videos = videos;
	}

	public String getTags() {
		String temp = "";
		for (int i = 0; i < this.tags.length; i++) {
			temp = temp + this.tags[i] + " ";
		}
		return temp;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Comments getComments() {
		return this.comments;
	}

	public void setComments(Comments comments) {
		this.comments = comments;
	}

	public String getFilter() {
		return this.filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public long getCreated_time() {
		return this.created_time;
	}

	public void setCreated_time(long created_time) {
		this.created_time = created_time;
	}

	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Like getLikes() {
		return this.likes;
	}

	public void setLikes(Like likes) {
		this.likes = likes;
	}

	public Images getImages() {
		return this.images;
	}

	public void setImages(Images images) {
		this.images = images;
	}

	public UserInPhoto[] getUsers_in_photo() {
		return this.users_in_photo;
	}

	public void setUsers_in_photo(UserInPhoto[] users_in_photo) {
		this.users_in_photo = users_in_photo;
	}

	public Caption getCaption() {
		return this.caption;
	}

	public void setCaption(Caption caption) {
		this.caption = caption;
	}

	public boolean isUser_has_liked() {
		return this.user_has_liked;
	}

	public void setUser_has_liked(boolean user_has_liked) {
		this.user_has_liked = user_has_liked;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@JsonProperty
	public String getAttribution() {
		return attribution;
	}

	@JsonIgnore
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	public static class Video {
		private Low_Bandwidth low_bandwidth;
		private Standard_Resolution standard_resolution;
		private Low_Resolution low_resolution;

		public Low_Bandwidth getLow_bandwidth() {
			return this.low_bandwidth;
		}

		public void setLow_bandwidth(Low_Bandwidth low_bandwidth) {
			this.low_bandwidth = low_bandwidth;
		}

		public Standard_Resolution getStandard_resolution() {
			return this.standard_resolution;
		}

		public void setStandard_resolution(Standard_Resolution standard_resolution) {
			this.standard_resolution = standard_resolution;
		}

		public Low_Resolution getLow_resolution() {
			return this.low_resolution;
		}

		public void setLow_resolution(Low_Resolution low_resolution) {
			this.low_resolution = low_resolution;
		}

	}

	public static class Low_Bandwidth {
		private String url;
		private int width;
		private int height;

		public String getUrl() {
			return this.url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getWidth() {
			return this.width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return this.height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}

	public static class Standard_Resolution {
		private String url;
		private int width;
		private int height;

		public String getUrl() {
			return this.url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getWidth() {
			return this.width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return this.height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}

	public static class Low_Resolution {
		private String url;
		private int width;
		private int height;

		public String getUrl() {
			return this.url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getWidth() {
			return this.width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return this.height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}

	public static class Location {
		private double latitude;
		private String name;
		private double longitude;
		private long id;

		public double getLatitude() {
			return this.latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public double getLongitude() {
			return this.longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public long getId() {
			return this.id;
		}

		public void setId(long id) {
			this.id = id;
		}
	}

	public static class Comments {
		private int count;
		private Commentes_data[] data;

		public int getCount() {
			return this.count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public Commentes_data[] getData() {
			return this.data;
		}

		public void setData(Commentes_data[] data) {
			this.data = data;
		}
	}

	public static class Commentes_data {
		private long created_time;
		private String text;
		private User from;
		private long id;

		public long getCreated_time() {
			return this.created_time;
		}

		public void setCreated_time(long created_time) {
			this.created_time = created_time;
		}

		public String getText() {
			return this.text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public User getFrom() {
			return this.from;
		}

		public void setFrom(User from) {
			this.from = from;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}
	}

	public static class Like {
		private int count;
		private User[] data;

		public int getCount() {
			return this.count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public User[] getData() {
			return this.data;
		}

		public void setData(User[] data) {
			this.data = data;
		}
	}

	public static class Images {
		private Low_Resolution low_resolution;
		private Thumbnail thumbnail;
		private Standard_Resolution standard_resolution;

		public Low_Resolution getLow_resolution() {
			return this.low_resolution;
		}

		public void setLow_resolution(Low_Resolution low_resolution) {
			this.low_resolution = low_resolution;
		}

		public Thumbnail getThumbnail() {
			return this.thumbnail;
		}

		public void setThumbnail(Thumbnail thumbnail) {
			this.thumbnail = thumbnail;
		}

		public Standard_Resolution getStandard_resolution() {
			return this.standard_resolution;
		}

		public void setStandard_resolution(Standard_Resolution standard_resolution) {
			this.standard_resolution = standard_resolution;
		}
	}

	public static class Thumbnail {
		private String url;
		private int width;
		private int height;

		public String getUrl() {
			return this.url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getWidth() {
			return this.width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return this.height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}

	public static class UserInPhoto {

		private Position position;
		private User user;

		public Position getPosition() {
			return this.position;
		}

		public void setPosition(Position position) {
			this.position = position;
		}

		public User getUser() {
			return this.user;
		}

		public void setUser(User user) {
			this.user = user;
		}

	}

	public static class Position {
		private double y;
		private double x;

		public double getY() {
			return this.y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public double getX() {
			return this.x;
		}

		public void setX(double x) {
			this.x = x;
		}
	}

	public static class Caption {
		private String created_time;
		private String text;
		private User from;
		private String id;
		private boolean user_has_liked;

		public String getCreated_time() {
			return this.created_time;
		}

		public void setCreated_time(String created_time) {
			this.created_time = created_time;
		}

		public String getText() {
			return this.text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public User getFrom() {
			return this.from;
		}

		public void setFrom(User from) {
			this.from = from;
		}

		public String getId() {
			return this.id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public boolean isUser_has_liked() {
			return this.user_has_liked;
		}

		public void setUser_has_liked(boolean user_has_liked) {
			this.user_has_liked = user_has_liked;
		}
	}

}
