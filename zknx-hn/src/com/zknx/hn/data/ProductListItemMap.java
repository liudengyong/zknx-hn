package com.zknx.hn.data;

import com.zknx.hn.functions.common.ProductListAdapter;

public class ProductListItemMap extends ListItemMap {

	/**
	 * Ä¬ÈÏserialVersionUIDÎª1
	 */
	private static final long serialVersionUID = 1L;
	
	public ProductListItemMap(String key_product_market, int product_id, String productName, String priceMin, String priceMax, String priceAverage, String priceHome, String priceUnit, boolean addOrRemove) {
		super(ProductListAdapter.KEY_PRODUCT_NAME, key_product_market, product_id);
		this.put(ProductListAdapter.KEY_PRODUCT_NAME, productName);
		this.put(ProductListAdapter.KEY_PRICE_MIN, priceMin);
		this.put(ProductListAdapter.KEY_PRICE_MAX, priceMax);
		this.put(ProductListAdapter.KEY_PRICE_AVERAGE, priceAverage);
		this.put(ProductListAdapter.KEY_PRICE_HOME, priceHome);
		this.put(ProductListAdapter.KEY_PRICE_UNIT, priceUnit);
		this.put(ProductListAdapter.KEY_ADD_CUSTOM, addOrRemove);
	}
}
