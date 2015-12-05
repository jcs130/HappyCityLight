package com.zhongli.happycity.model.user;

/**
 * 记录用户的详细信息
 * 
 * @author zhonglili
 *
 */
public class UserDetail {
	private long user_id;
	private String email;
	private String nickname;
	private String firstname;
	private String midlename;
	private String lastname;
	private int age;
	private int gender;
	private String user_picture;

	public UserDetail() {

	}

	public UserDetail(int user_id) {
		this.user_id = user_id;

	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMidlename() {
		return midlename;
	}

	public void setMidlename(String midlename) {
		this.midlename = midlename;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getUser_picture() {
		return user_picture;
	}

	public void setUser_picture(String user_picture) {
		this.user_picture = user_picture;
	}

	@Override
	public String toString() {
		return "UserDetail [user_id=" + user_id + ", email=" + email
				+ ", nickname=" + nickname + ", firstname=" + firstname
				+ ", midlename=" + midlename + ", lastname=" + lastname
				+ ", age=" + age + ", gender=" + gender + ", user_picture="
				+ user_picture + "]";
	}

}
