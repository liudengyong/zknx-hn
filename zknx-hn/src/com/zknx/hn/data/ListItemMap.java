package com.zknx.hn.data;

import java.util.HashMap;

import com.zknx.hn.common.Debug;

public class ListItemMap extends HashMap<String, Object> {

	/**
	 * 默认serialVersionUID为1
	 */
	private static final long serialVersionUID = 1L;
	
	public ListItemMap(String itemName, String keyId, String id) {
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
		Object value = map.get(key);
		if (value == null) {
			Debug.Log("严重错误：GetMapString," + key);
			return "";
		}

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

		Object value = map.get(key);
		if (value == null) {
			Debug.Log("严重错误：GetMapInt," + key);
			return DataMan.INVALID_ID;
		}

		return DataMan.ParseInt(value.toString());
	}
}
