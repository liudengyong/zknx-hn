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
			return get(location).mDate;
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
			return get(location).mPrice;
		}
		
		return 0F;
	}
	
	/**
	 * 反着取，从旧日期往新日期取（add时是先添加新日期）
	 * @param location
	 * @return
	 */
	private PricePair get(int location) {
		return mPriceList.get(mPriceList.size() - location - 1);
	}
	
	/**
	 * 获取最低价格
	 * @return
	 */
	public Float getMinPrice() {
		if (mPriceList.size() == 0)
			return 0F;

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
		if (mPriceList.size() == 0)
			return 0F;

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

	private List<PricePair> mPriceList; // 每个日期的价格
}
