package com.zknx.hn.functions.common;

import java.util.List;

public class ProductPriceInfo {

	public ProductPriceInfo(List<Float> _price, String _priceUnit, String _dateUnit) {
		price = _price;
		
		priceUnit = _priceUnit;
		dateUnit = _dateUnit;
	}
	
	String priceUnit; // 价格单位，元/万元等
	String dateUnit; // 日期单位，年/月/日

	List<Float> price; // 每个日期的价格
}
