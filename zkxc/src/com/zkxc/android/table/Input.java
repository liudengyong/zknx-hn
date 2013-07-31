package com.zkxc.android.table;

public class Input {
	
	/*
	public enum Type {
		// 系统控件 
		TEXT_VIEW, 
		EDIT_BOX, 
		CHECK_BOX, 
		LIST_VIEW, 
		DATE, 
		TIME,
		// 自定义控件
		PICK_AUDIO, 
		PICK_PICTURE, 
		PICK_VIDEO, 
		LOCATION_GPS,
		Lable};
	*/
		
	public enum Type { LABEL, EDIT, CKECKBOX, LIST, DATE, TIME, LOCATION, AUDIO, PICTURE, VIDEO, SUM, BIO};

	public Input() {
	}

	public void setName(String value) {
		name = value;
	}
	
	public void setType(String value) {
		for (Type _type : Type.values())
        {
			if (_type.toString().equals(value))
			{
				type = _type;
				return;
			}
        }
		
		type = Type.LABEL; // 默认类型
		data = "未实现的数据输入类型";
	}
	
	public void setData(String value) {
		data = value;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}
	
	public String getData() {
		return data;
	}

	String name;
	String data;
	Type type;
}
