package com.ywang.alone.modeles;

public class UserInfo {
	private long userid;
	private int accountType;
	private String avatarImg;
	private String nickName;
	private String signature;
	private String longitude;
	private String latitude;
	private int gender;
	private String phone;
	
	
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public int getAccountType() {
		return accountType;
	}
	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}
	public String getAvatarImg() {
		return avatarImg;
	}
	public void setAvatarImg(String avatarImg) {
		this.avatarImg = avatarImg;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
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
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "UserInfo [userid=" + userid + ", accountType=" + accountType
				+ ", avatarImg=" + avatarImg + ", nickName=" + nickName
				+ ", signature=" + signature + ", longitude=" + longitude
				+ ", latitude=" + latitude + ", gender=" + gender + ", phone="
				+ phone + "]";
	}
	
	
	
}
