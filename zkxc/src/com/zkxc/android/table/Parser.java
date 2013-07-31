package com.zkxc.android.table;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import android.content.Context;
import android.graphics.Color;

import org.apache.http.util.EncodingUtils;
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList; 
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.zkxc.android.common.Converter;
import com.zkxc.android.data.DataMan;

public class Parser {
	
	Context mContext;
	
	Element tableElement;
	NodeList rowNodes;
	
	public NodeList GetRowNodes()
	{
		return rowNodes;
	}
	
	String parsedTableId;
	
	public Parser(Context context) {
		mContext = context;
	}
	
	public static char ascii2Char(int ASCII) {   
        return (char) ASCII;   
    }  
	
	public static String ascii2String(String ASCIIs) {   
        String[] ASCIIss = ASCIIs.split(",");   
        StringBuffer sb = new StringBuffer();   
        for (int i = 0; i < ASCIIss.length; i++) {   
            sb.append((char) ascii2Char(Integer.parseInt(ASCIIss[i])));   
        }   
        return sb.toString();   
    }
	
	public boolean parserTable(String tabId)
	{
		// 防止重复解析
		if (parsedTableId == tabId && rowNodes != null && rowNodes.getLength() > 0)
			return true;
		
		try
		{
			InputStream is = new FileInputStream(new File(DataMan.GetTabFilePathByUserId() + DataMan.GetTabFileName(tabId)));
			
			int len = is.available();
			
			byte[] buffer = new byte[len];
			is.read(buffer);
			String strXml = EncodingUtils.getString(buffer, "GB2312"); // TODO BBB 采集表中文编码问题，不是UTF8
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document doc = builder.parse(new InputSource(new StringReader(strXml)));
			//Document doc = builder.parse(is);
			
			tableElement = doc.getDocumentElement(); // ("table");
			
			rowNodes = tableElement.getChildNodes(); // ("row")
			
			parsedTableId = tabId;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return (rowNodes != null);
	}

	public int GetFrozenColCount() {
		return Converter.ToInt(tableElement.getAttribute("colFrozen"));
	}

	public int GetFrozenRowCount() {
		return Converter.ToInt(tableElement.getAttribute("rowFrozen"));
	}

	public int GetRowCount() {
		return Converter.ToInt(tableElement.getAttribute("rowCount"));
	}
	
	public int GetColCount() {
		return Converter.ToInt(tableElement.getAttribute("colCount"));
	}
	

	public void initTable(Context context, GridLayout table, Map<String, Object> record) {
		
		NodeList cells = null;
		NamedNodeMap attrMapRow = null;
		NodeList rowNodes = GetRowNodes();
		
		table.setBackgroundColor(Color.DKGRAY);
		table.removeAllViews();
		
		table.setSize(Converter.ToInt(tableElement.getAttribute("rowCount")), 
				      Converter.ToInt(tableElement.getAttribute("colCount")), tableElement);
		
		table.setFrozen(Converter.ToInt(tableElement.getAttribute("rowFrozen")), 
			      Converter.ToInt(tableElement.getAttribute("colFrozen")));
		
		//===================================================
		// 所有是parent的单元格都不保存记录
		Map<String, Object> parentMap = new HashMap<String, Object>();
		
		// 遍历表
		for (int i = 0; i < rowNodes.getLength(); i++)
		{
			cells = rowNodes.item(i).getChildNodes();
			
			attrMapRow = rowNodes.item(i).getAttributes();
			
			if (cells.getLength() <= 0 || attrMapRow == null) continue;
			
			// 查找到行
			NamedNodeMap attrMap = null;
			
			for (int j = 0; j < cells.getLength(); j++)
			{
				attrMap = cells.item(j).getAttributes();
				
				if (attrMap == null) continue;
				
				// 查找到单元格
				String parent = Converter.GetData(attrMap, "parent");
				
				// 先判断然后再添加自己的parent。自己是自己的parent可以保存（神经病就放他走吧）
		        if (parentMap.get(parent) == null)
		        	parentMap.put(parent, "true");
			}
		}
		//===================================================
		
		// 遍历表
		for (int i = 0; i < rowNodes.getLength(); i++)
		{
			cells = rowNodes.item(i).getChildNodes();
			
			attrMapRow = rowNodes.item(i).getAttributes();
			
			if (cells.getLength() <= 0 || attrMapRow == null) continue;
			
			// 查找到行
			int rowIndex = Converter.ToInt(Converter.GetData(attrMapRow, "index"));

			NamedNodeMap attrMap = null;
			
			for (int j = 0; j < cells.getLength(); j++)
			{
				attrMap = cells.item(j).getAttributes();
				
				if (attrMap == null) continue;
				
				// 查找到单元格

				int colIndex = Converter.ToInt(Converter.GetData(attrMap, "index"));
				
				table.addView(GridCell.GetCellView(rowIndex, colIndex, this, parentMap, record, context, attrMap));
			}
		}
		
		// 处理合计控件
		table.initSumCell();
	}
}
