package com.zknx.hn.data;

import java.util.HashMap;

import com.zknx.hn.common.Debug;

public class ListItemMap extends HashMap<String, Object> {

	/**
	 * Ĭ��serialVersionUIDΪ1
	 */
	private static final long serialVersionUID = 1L;
	
	public ListItemMap(String itemName, String keyId, String id) {
		this.put(DataMan.KEY_NAME, itemName);
		this.put(keyId, id);
	}
	
	/**
	 * ��ȡMap�е��ַ���
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return GetMapString(this, key);
	}
	
	/**
	 * ��ȡMap�е�������
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return GetMapInt(this, key);
	}
	
	/**
	 * ȡ��map�е�String
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
			Debug.Log("���ش���GetMapString," + key);
			return "";
		}

		return map.get(key).toString();
	}

	/**
	 * ȡ��map�е�intֵ
	 * @param objectMap
	 * @param key
	 * @return
	 */
	public static int GetMapInt(ListItemMap map, String key) {
		if (map == null)
			return DataMan.INVALID_ID;

		Object value = map.get(key);
		if (value == null) {
			Debug.Log("���ش���GetMapInt," + key);
			return DataMan.INVALID_ID;
		}

		return DataMan.ParseInt(value.toString());
	}
}
