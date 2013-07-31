package com.zkxc.android.data;

public class User {
	

	public User(String userid, String addressId, String pAId,
			String mobileNumber, String userName, String village,
			String birthDate, String gender) {
		super();
		this.userid = userid;
		AddressId = addressId;
		PA_id = pAId;
		MobileNumber = mobileNumber;
		UserName = userName;
		Village = village;
		BirthDate = birthDate;
		Gender = gender;
	}

	public User(String userid, String password, String addressId, String pAId,
			String mobileNumber, String userName, String village,
			String birthDate, String gender) {
		super();
		this.userid = userid;
		//this.password = password;
		AddressId = addressId;
		PA_id = pAId;
		MobileNumber = mobileNumber;
		UserName = userName;
		Village = village;
		BirthDate = birthDate;
		Gender = gender;
	}

	private String userid;
	// TODO ZZZ 是否保存用户密码
	//private String password;
	private String AddressId;
	private String PA_id;
	private String MobileNumber;
	private String UserName;
	private String Village;
	private String BirthDate;
	private String Gender;

	public String getAddressId() {
		return AddressId;
	}

	public void setAddressId(String addressId) {
		AddressId = addressId;
	}

	public String getPA_id() {
		return PA_id;
	}

	public void setPA_id(String pAId) {
		PA_id = pAId;
	}

	public String getMobileNumber() {
		return MobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getVillage() {
		return Village;
	}

	public void setVillage(String village) {
		Village = village;
	}

	public String getBirthDate() {
		return BirthDate;
	}

	public void setBirthDate(String birthDate) {
		BirthDate = birthDate;
	}

	public String getGender() {
		return Gender;
	}

	public void setGender(String gender) {
		Gender = gender;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}




}
