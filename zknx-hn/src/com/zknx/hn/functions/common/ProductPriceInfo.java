package com.zknx.hn.functions.common;

import java.util.List;

public class ProductPriceInfo {

	public ProductPriceInfo(List<Float> _price, String _priceUnit, String _dateUnit) {
		price = _price;
		
		priceUnit = _priceUnit;
		dateUnit = _dateUnit;
	}
	
	String priceUnit; // �۸�λ��Ԫ/��Ԫ��
	String dateUnit; // ���ڵ�λ����/��/��

	List<Float> price; // ÿ�����ڵļ۸�
}
