package com.zkxc.android.common;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.zkxc.android.table.Input.Type;

public class Converter {

	public static int ToInt(String value)
	{
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
		}
		
		return -1;
	}
	
	public static int GetInt(NamedNodeMap attrMap, String key)
	{
		try {
			String value = Converter.GetData(attrMap, key);
			return Integer.parseInt(value);
		} catch (Exception e) {
		}
		
		return -1;
	}
	
	public static Type GetInputType(NamedNodeMap attrMap)
	{
		try {
			String value = Converter.GetData(attrMap, "type");
			return Type.valueOf(value);
		} catch (Exception e) {
		}
		
		return Type.LABEL;
	}
	
	public static String GetData(NamedNodeMap attrMap, String key)
	{
		if (attrMap != null)
		{
			Node node = attrMap.getNamedItem(key);
			if (node != null)
				return node.getNodeValue();
		}
		
		return null;
	}

	public static String GetKey0(int rowIndex, int colIndex) {
		return "key_" + rowIndex + "_" + colIndex;
	}
	
	public static String DateToHuman(String date)
	{
		String[] token = date.split("_");
		if (token.length == 3)
		{
			if (token[1].length() == 4 &&
				token[2].length() == 6)
			return token[0] + "年" + 
				token[1].substring(0, 2) + "月" + 
			    token[1].substring(2) + "日 " +
			    token[2].substring(0,2) + ":" + 
			    token[2].substring(2,4) + ":" + 
			    token[2].substring(4);
		}
		
		return date;
	}
}
