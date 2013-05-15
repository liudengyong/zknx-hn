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
	 * 添加一天的价格信息
	 * @param date
	 * @param price
	 */
	public void add(String date, Float price) {
		PricePair pricePair = new PricePair(date, price);
		mPriceList.add(pricePair);
	}
	
	/**
	 * 获取日期
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
	 * 获取价格
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
	 * 获取最低价格
	 * @return
	 */
	public Float getMinPrice() {
		// 新建一个list做比较用
		List<Float> tmp = new ArrayList<Float>();
		for (PricePair price : mPriceList)
			tmp.add(price.mPrice);

		return Collections.min(tmp);
	}
	
	/**
	 * 获取最高价格
	 * @return
	 */
	public Float getMaxPrice() {
		// 新建一个list做比较用
		List<Float> tmp = new ArrayList<Float>();
		for (PricePair price : mPriceList)
			tmp.add(price.mPrice);

		return Collections.max(tmp);
	}
	/**
	 * 获取日期数
	 * @return
	 */
	public int size() {
		return mPriceList.size();
	}

	/**
	 * 获取价格单位
	 * @return
	 */
	public String getPriceUnit() {
		return mPriceUnit;
	}
	
	/**
	 * 获取日期单位
	 * @return
	 */
	public String getDateUnit() {
		return mDateUnit;
	}

	private String mPriceUnit; // 价格单位，元/万元等
	private String mDateUnit; // 日期单位，年/月/日

	private List<PricePair> mPriceList; // 每个日期的价格
}
