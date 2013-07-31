package com.zkxc.android.data;

public class Base_Table_Version {

	public Base_Table_Version(String baseTableName, float version,
			String baseTableRemark) {
		super();
		BaseTableName = baseTableName;
		Version = version;
		BaseTableRemark = baseTableRemark;
	}
	
	public Base_Table_Version() {
	}

	private String BaseTableName;
	private float Version;
	private String BaseTableRemark;
	
	
	
	public String getBaseTableName() {
		return BaseTableName;
	}
	public void setBaseTableName(String baseTableName) {
		BaseTableName = baseTableName;
	}
	public float getVersion() {
		return Version;
	}
	public void setVersion(float version) {
		Version = version;
	}
	public String getBaseTableRemark() {
		return BaseTableRemark;
	}
	public void setBaseTableRemark(String baseTableRemark) {
		BaseTableRemark = baseTableRemark;
	}

}
