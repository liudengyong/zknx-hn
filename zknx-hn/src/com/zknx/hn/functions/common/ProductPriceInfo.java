package com.zknx.hn.functions.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductPriceInfo {
	
	private class PricePair {

		PricePair(String date, Float price) {
			mDate = date;
			mPrice = price;
		}

		String mDate;
		Float mPrice;
	}

	public ProductPriceInfo(String priceUnit, String dateUnit) {
		mPriceList = new ArrayList<PricePair>();;
		
		mPriceUnit = priceUnit;
		mDateUnit  = dateUnit;
	}

	/**
	 * ���һ��ļ۸���Ϣ
	 * @param date
	 * @param price
	 */
	public void add(String date, Float price) {
		PricePair pricePair = new PricePair(date, price);
		mPriceList.add(pricePair);
	}
	
	/**
	 * ��ȡ����
	 * @param location
	 * @return
	 */
	public String getDate(int location) {
		if (location < mPriceList.size()) {
			return mPriceList.get(location).mDate;
		}
		
		return null;
	}
	
	/**
	 * ��ȡ�۸�
	 * @param location
	 * @return
	 */
	public Float getPrice(int location) {
		if (location < mPriceList.size()) {
			return mPriceList.get(location).mPrice;
		}
		
		return 0F;
	}
	
	/**
	 * ��ȡ��ͼ۸�
	 * @return
	 */
	public Float getMinPrice() {
		// �½�һ��list���Ƚ���
		List<Float> tmp = new ArrayList<Float>();
		for (PricePair price : mPriceList)
			tmp.add(price.mPrice);

		return Collections.min(tmp);
	}
	
	/**
	 * ��ȡ��߼۸�
	 * @return
	 */
	public Float getMaxPrice() {
		// �½�һ��list���Ƚ���
		List<Float> tmp = new ArrayList<Float>();
		for (PricePair price : mPriceList)
			tmp.add(price.mPrice);

		return Collections.max(tmp);
	}
	/**
	 * ��ȡ������
	 * @return
	 */
	public int size() {
		return mPriceList.size();
	}

	/**
	 * ��ȡ�۸�λ
	 * @return
	 */
	public String getPriceUnit() {
		return mPriceUnit;
	}
	
	/**
	 * ��ȡ���ڵ�λ
	 * @return
	 */
	public String getDateUnit() {
		return mDateUnit;
	}

	private String mPriceUnit; // �۸�λ��Ԫ/��Ԫ��
	private String mDateUnit; // ���ڵ�λ����/��/��

	private List<PricePair> mPriceList; // ÿ�����ڵļ۸�
}
