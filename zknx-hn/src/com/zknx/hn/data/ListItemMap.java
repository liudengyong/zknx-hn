package com.zknx.hn.data;

import java.util.HashMap;

public class ListItemMap extends HashMap<String, Object> {

	/**
	 * 默认serialVersionUID为1
	 */
	private static final long serialVersionUID = 1L;
	
	public ListItemMap(String itemName, String keyId, int id) {
		this.put(DataMan.KEY_NAME, itemName);
		this.put(keyId, id);
	}
	
	/**
	 * 获取Map中的字符串
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return GetMapString(this, key);
	}
	
	/**
	 * 获取Map中的整形数
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return GetMapInt(this, key);
	}
	
	/**
	 * 取得map中的String
	 * @param objectMap
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String GetMapString(Object objectMap, String key) {
		if (objectMap == null)
			return "";
		
		HashMap<String, Object> map = (HashMap<String, Object>)objectMap;
		return map.get(key).toString();
	}

	/**
	 * 取得map中的int值
	 * @param objectMap
	 * @param key
	 * @return
	 */
	public static int GetMapInt(ListItemMap map, String key) {
		if (map == null)
			return DataMan.INVALID_ID;

		int value = DataMan.ParseInt(map.get(key).toString());

		return value;
	}
}
