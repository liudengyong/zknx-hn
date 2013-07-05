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

	public ProductPriceInfo() {
		mPriceList = new ArrayList<PricePair>();;
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
			return get(location).mDate;
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
			return get(location).mPrice;
		}
		
		return 0F;
	}
	
	/**
	 * ����ȡ���Ӿ�������������ȡ��addʱ������������ڣ�
	 * @param location
	 * @return
	 */
	private PricePair get(int location) {
		return mPriceList.get(mPriceList.size() - location - 1);
	}
	
	/**
	 * ��ȡ��ͼ۸�
	 * @return
	 */
	public Float getMinPrice() {
		if (mPriceList.size() == 0)
			return 0F;

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
		if (mPriceList.size() == 0)
			return 0F;

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

	private List<PricePair> mPriceList; // ÿ�����ڵļ۸�
}
