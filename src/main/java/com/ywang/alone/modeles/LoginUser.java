package com.ywang.alone.modeles;

import com.alibaba.fastjson.annotation.JSONField;

public class LoginUser {

	@JSONField(name="uname")
	private String uname;
	@JSONField(name="pword")
	private String pword;
	@JSONField(name="longitude")
	private String longitude;
	@JSONField(name="latitude")
	private String latitude;
	
	
	public String getUname() {
		return uname;
	}


	public void setUname(String uname) {
		this.uname = uname;
	}


	public String getPword() {
		return pword;
	}


	public void setPword(String pword) {
		this.pword = pword;
	}


	public String getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	public String getLatitude() {
		return latitude;
	}


	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	@Override
	public String toString() {
		return "LoginUser [uname=" + uname + ", pword=" + pword
				+ ", longitude=" + longitude + ", latitude=" + latitude + "]";
	}
	
}
