package com.zknx.hn.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.os.Environment;

import com.zknx.hn.common.Debug;
import com.zknx.hn.common.Restraint;
import com.zknx.hn.functions.common.ProductPriceInfo;
import com.zknx.hn.zip.Ziper;

public class DataMan extends DataInterface {

	// 通用为名字，也用于ListItem显示文本
	public static final String KEY_NAME = "name";

	public static final String KEY_ADDRESS_ID = "address_id";
	public static final String KEY_MARKET_ID  = "market_id";
	public static final String KEY_PRODUCT_ID = "product_id";
	public static final String KEY_PRODUCT_CLASS_ID = "product_class_id"; //  产品分类
	public static final String KEY_SUPPLY_DEMAND_INFO_ID = "supply_demand_info_id"; // 供求信息id
	public static final String KEY_FRIEND_ID = "friend_id";
	public static final String KEY_AIS_ID = "ais_id";
	public static final String KEY_AIS_CLASS_ID = "ais_class_id";
	// messageId 同时也是发布留言的商友id
	public static final String KEY_MY_GROUP_MESSAGE_ID = "my_group_message";

	// 我的商友 专业
	public static final String KEY_FRIEND_MAJOR = "friend_major";
	// 我的商友 联系地址
	public static final String KEY_FRIEND_ADDRESS = "friend_address";
	// 我的商友 联系电话
	public static final String KEY_FRIEND_TELEPHONE = "friend_telephone";
	// 我的商友 自我介绍
	public static final String KEY_FRIEND_INTRODUCE = "friend_introduce";

	// 发言人id
	public static final String KEY_FRIEND_MESSAGE_POSER_ID = "message_poster_id";
	// 发言人名字
	public static final String KEY_FRIEND_MESSAGE_POSER = "message_poster";
	// 留言日期
	public static final String KEY_FRIEND_MESSAGE_DATE = "message_date";
	// 留言内容
	public static final String KEY_FRIEND_MESSAGE_CONTENT = "message_content";

	// 临时文件名
	public static final String FILE_NAME_TMP = "tmp.txt";
	
	// 数据更新时间戳文件
	private static final String TIME_STAMP_FILE_NAME = ".timestamp";

	// 默认非法id值
	public static final int INVALID_ID = -1;
	// 用于查询我的留言的标志
	public static final int MY_MESSAGE = 0;

	// 通用分隔符
	public static final String COMMON_TOKEN = ",";

	/**
	 * 按行读取文本文件
	 * @param fileName
	 * @return
	 */
	public static List<String> ReadLines(String fileName) {
		// 默认编码
		return ReadLines(fileName, "UTF-8");
	}
	
	/**
	 * 按行读取文本文件
	 * @param fileName
	 * @return
	 */
	public static List<String> ReadLines(String fileName, String encoding) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			String filePathName = DataFile(fileName);
			
			Debug.Log("读取：" + filePathName);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePathName), encoding));
			
			String line;
			boolean fistLine = true;
			while ((line = br.readLine()) != null) {
				// 第一行如果有UTF8文件头，去掉
				if (fistLine) {
					String newline = RemoveIfContainsUTF8Flags(line);
					fistLine = false;
					list.add(newline);
				} else {
					list.add(line);
				}
			}
			
			br.close();

		} catch (FileNotFoundException e) {
			Debug.Log("文件没找到 ： " + fileName);
		} catch (IOException e) {
			Debug.Log("文件读错误 ： " + fileName);
		} catch (NullPointerException e) {
			Debug.Log("空指针错误 ： " + fileName);
		}
		
		// 无论有无值，都返回实例，调用者不用判断是否空
		return list;
	}

	/**
	 * 去除UTF8文件标志头，如果存在的话
	 * @param line
	 * @return
	 */
	private static String RemoveIfContainsUTF8Flags(String line) {
		final byte[] bom = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF }; 

		if (line.length() > 3) {
			byte[] bytes = line.getBytes();

			if (bytes[0] == bom[0] && 
				bytes[1] == bom[1] && 
				bytes[2] == bom[2]) {
				
				return new String(bytes, 3, bytes.length - 3);
			}
		}

		return new String(line);
	}

	/**
	 * 以逗号分割一行数据
	 */
	private static String[] GetToken(String line)
	{
		if (line != null && line.length() > 0)
			return line.split(TOKEN_SEP);
		else
			return new String[0]; // 调用者只需要判断token长度，不用判断是否为空
	}

	/**
	 * 字符串转int
	 * @param value
	 * @return
	 */
	public static int ParseInt(String value) {
		try {
			return Integer.parseInt(value, 10);
		}
		catch (NumberFormatException exp) {
			Debug.Log("错误：ParseInt，" + exp.getMessage());
		}
		
		return INVALID_ID; // 返回错误
	}
	
	/**
	 * 判断字符串是否为空，为空返回true
	 * @param str
	 * @return
	 */
	public boolean IsStringEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

	/**
	 * 字符串转boolean
	 * @param value
	 * @return
	 
	private static boolean ParseBool(String value) {
		return Boolean.parseBoolean(value);
	}*/
	
	/**
	 * 读取通用的id和名字列表
	 * 格式：id,名字
	 * @return
	 */
	private static List<ListItemMap> ReadCommonIdName(String fileName, String key) {

        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(fileName);
        
        for (String line : lines)  
        {
        	// id,名字
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		int id =  ParseInt(token[0]);

        		if (id != INVALID_ID) {

	        		String name = token[1];
	
	        		list.add(new ListItemMap(name/* 名字 */, key, token[0]/* id */));
        		}
        	}
        }

        return list;
	}

	/**
	 * 获取地址列表
	 * @return
	 */
	public static List<ListItemMap> GetAddressList() {
		
	    ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
	    
	    // 优化效率 省级地址
	    if (FileUtils.IsFileExist(DataFile(FILE_NAME_ADDRESS_PROVINCE)))
	    	return ReadCommonIdName(FILE_NAME_ADDRESS_PROVINCE, KEY_ADDRESS_ID);

	    String provinceLines = "";
        List<String> lines = ReadLines(FILE_NAME_ADDRESS);
        
        for (String line : lines) {
        	// id,名字
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		String strId = token[0];
        		int id =  ParseInt(strId);

        		// 只需要省份
        		if (id != INVALID_ID && id < 99) {

	        		String name = token[1];
	
	        		list.add(new ListItemMap(name/* 名字 */, KEY_ADDRESS_ID, strId/* id */));
	        		
	        		provinceLines += strId + COMMON_TOKEN + name + "\n";
        		}
        	}
        }
        
        FileUtils.WriteText(DataFile(""), FILE_NAME_ADDRESS_PROVINCE, provinceLines);

        return list;
	}

	/**
	 * 获取市场列表
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByArea(int address_id) {

		String marketCacheFileName = "market_" + address_id + ".txt";
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        
        // 优化效率
        if (FileUtils.IsFileExist(DataFile(marketCacheFileName)))
        	return ReadCommonIdName(marketCacheFileName, KEY_MARKET_ID);
        
        String marketLines = "";
        List<String> lines = ReadLines(FILE_NAME_MARKETS);
        
        for (String line : lines)  
        {
        	// 01,北京,01002,双桥市场
        	String[] token = GetToken(line);
        	if (token.length == 4) {

        		int address_id_parsed =  ParseInt(token[0]);
        		if (!AddressMatch(address_id, address_id_parsed))
        			continue;

        		String id = token[2];
        		String name = token[3];
        		
        		list.add(new ListItemMap(name/* 市场名字 */, KEY_MARKET_ID, id/* 市场id */));
        		
        		marketLines += id + COMMON_TOKEN + name + "\n";
        	}
        }
        
        FileUtils.WriteText(DataFile(""), marketCacheFileName, marketLines);

        return list;
	}

	/**
	 * 获取产品列表
	 * @return
	 */
	public static List<ListItemMap> GetProductList(int market_id) {

        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        if (market_id == INVALID_ID)
        	return list;
        
        List<String> lines;
        String marketProductsFileName = "market_" + market_id + "_products.txt";
        
        // 优化
        if (FileUtils.IsFileExist(DataFile(marketProductsFileName))) {
        	lines = ReadLines(marketProductsFileName);
        	for (String line : lines)
            {
            	String[] token = GetToken(line);
            	
            	String productId = token[0];
        		int product_id = ParseInt(productId);
        		if (product_id == INVALID_ID)
        			continue;
        		
            	String product_name = token[1];
        		String minPrice = token[2];
        		String maxPrice = token[3];
        		String averagePrice = token[4];
        		String hostPrice = token[5];
        		String unit = token[6];
        		boolean isMyProduct = IsMyProduct(product_id); /* 添加自选按钮状态 */
        		
        		AddProductList(list, productId, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct);
            }

        	return list;
        }

        // 在内存中查找
        List<ListItemMap> productList = GetProductList();
        
        String marketProductLines = "";
        lines = ReadLines(FILE_NAME_PRODUCTS);
        for (String line : lines)
        {
        	String[] token = GetToken(line);
        	// 0101301,北京昌平区水屯农副产品批发市场,0101001000,0101301,13.5,元/公斤
        	// TODO 接口确定
        	if (token.length == 6) {
        		// market_id,市场名字,product_id,产品名,最低价,最高价,平均价,产地价,价格单位
        		int market_id_parsed = ParseInt(token[0]);
        		if (market_id_parsed != market_id)
        			continue;

        		// 暂不需要市场名字
        		// String market_name = token[1];

        		String productId = token[2];
        		int product_id = ParseInt(productId);
        		if (product_id == INVALID_ID)
        			continue;

        		String product_name = FindCommodityName(productList, KEY_PRODUCT_ID, productId);
        		String minPrice = token[4];
        		String maxPrice = minPrice;//token[5];
        		String averagePrice = minPrice;//token[6];
        		String hostPrice = minPrice;// token[7];
        		String unit = token[5];
        		boolean isMyProduct = IsMyProduct(product_id); /* 添加自选按钮状态 */
        		
        		//list.add(new ProductListItemMap("名字", "最低价", "最高价", "平均价", "产地价", "单位", "添加"));
        		AddProductList(list, productId, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct);
        		
        		marketProductLines += productId + COMMON_TOKEN + 
        				product_name +COMMON_TOKEN + 
        				minPrice + COMMON_TOKEN +
        				maxPrice + COMMON_TOKEN +
        				averagePrice + COMMON_TOKEN +
        				hostPrice + COMMON_TOKEN +
        				unit + "\n";
        	}
        }

        FileUtils.WriteText(DataFile(""), marketProductsFileName, marketProductLines);

        return list;
	}
	
	/**
	 * 添加产品列表
	 */
	private static void AddProductList(ArrayList<ListItemMap> list, String productId, 
			String product_name, String minPrice, String maxPrice, 
			String averagePrice, String hostPrice, String unit, boolean isMyProduct) {
		list.add(new ProductListItemMap(DataMan.KEY_PRODUCT_ID, productId, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct));
	}

	/**
	 * 判断是否自选产品
	 * @param product_id
	 * @return
	 */
	private static boolean IsMyProduct(int productId) {

		List<ListItemMap> list = GetMyProductList();

		for (ListItemMap map : list) {
			if (map.getInt(KEY_PRODUCT_ID) == productId)
				return true; // 是自选产品 
		}

		return false; // 不是自选产品
	}

	/**
	 * 获取自选产品列表
	 * @return
	 */
	public static List<ListItemMap> GetMyProductList() {
		return ReadCommonIdName(FILE_NAME_MY_PRODUCTS, KEY_PRODUCT_ID);
	}

	/**
	 * 添加自选产品
	 * @return
	 */
	public static boolean MyProductListAdd(int product_id, String product_name) {

		if (product_id == INVALID_ID)
			return false;

		List<ListItemMap> list = GetMyProductList();

		// 是否超过最大数量限制
		if (list.size() >= Restraint.MAX_COUNT_MY_PRODUCT)
			return false;

		for (ListItemMap map : list) {
			if (map.getInt(KEY_PRODUCT_ID) == product_id)
				return true; // 已经存在，不用重复添加
		}

		ListItemMap myNewProduct = new ListItemMap(product_name /* 产品名字 */, KEY_PRODUCT_ID, product_id + "");
		list.add(myNewProduct);

		return SaveMyProducts(list);
	}
	
	/**
	 * 取消自选产品
	 * @return
	 */
	public static boolean MyProductListRemove(int product_id) {

		List<ListItemMap> list = GetMyProductList();
		
		if (product_id == INVALID_ID) {
			Debug.Log("严重内部错误：MyProductListRemove");
			return false;
		}

		for (ListItemMap item : list) {
			int item_product_id = item.getInt(KEY_PRODUCT_ID);
			if (item_product_id == product_id) {
				boolean removed = list.remove(item);
				if (!removed) {
					Debug.Log("错误：AddToMyProductList");
					return false;
				}
				break;
			}
		}

		return SaveMyProducts(list);
	}

	/**
	 * 保存我的自选产品
	 * @param list
	 * @return
	 */
	private static boolean SaveMyProducts(List<ListItemMap> list) {

		try {
			// 如果自选产品列表文件存在，则覆盖
			FileOutputStream out = new FileOutputStream(new File(DataFile(FILE_NAME_MY_PRODUCTS)));   
		
		    for (ListItemMap item : list) {   
		
		    	int product_id_to_be_save = item.getInt(KEY_PRODUCT_ID);

		    	if (product_id_to_be_save != INVALID_ID) {
			        String product_name_to_be_save = (String)item.get(KEY_NAME);
			        String line = product_id_to_be_save + COMMON_TOKEN + product_name_to_be_save + "\r\n";
			
			        out.write(line.getBytes());
		    	}
		    }

		    out.close();
		
			// 添加成功
			return true;
		}
		catch (IOException exp) {
			Debug.Log("错误：SaveMyProducts，" + exp.getMessage());
		}
		
		return false;
	}

	/**
	 * 获取所有有该产品的市场列表
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByProduct(String product_id) {

		List<String> lines = null;
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		String productMarketFileName = "product_" + product_id + "_markets.txt";

		if (product_id == null || product_id.length() == 0)
			return list;

		// 优化
		if (FileUtils.IsFileExist(DataFile(productMarketFileName))) {
			lines = ReadLines(productMarketFileName);
			
			for (String line : lines)
	        {
	        	String[] token = GetToken(line);
	    		
	        	String marketId = token[0];
	    		String market_name = token[1];
	    		String minPrice = token[2];
	    		String maxPrice = token[3];
	    		String averagePrice = token[4];
	    		String hostPrice = token[5];
	    		String unit = token[5];
	    		boolean addToMyProduct = false; /* 隐藏自选按钮 */
	        	
	        	list.add(new ProductListItemMap(DataMan.KEY_MARKET_ID, marketId, market_name, minPrice, maxPrice, averagePrice, hostPrice, unit, addToMyProduct));
	        }
			
			return list;
		}
		
		String productMarketLines = "";
		lines = ReadLines(FILE_NAME_PRODUCTS);
		
        for (String line : lines)
        {
        	// 0101301,北京昌平区水屯农副产品批发市场,0101001000,0101301,13.5,元/公斤
        	// market_id,市场名字,product_id,产品名,最低价,最高价,平均价,产地价,价格单位
        	// product_id,product_name
        	String[] token = GetToken(line);
        	if (token.length != 6)
        		continue;

        	//int product_id_parsed = ParseInt(token[2]);
    		if (!token[2].endsWith(product_id))
    			continue;

    		String marketId = token[0];
    		int market_id = ParseInt(marketId);
    		if (market_id == INVALID_ID)
    			continue;
    		
    		String market_name = token[1];
    		String minPrice = token[4];
    		String maxPrice = minPrice;//token[5];
    		String averagePrice = minPrice;//token[6];
    		String hostPrice = minPrice;//token[7];
    		String unit = token[5];
    		boolean addToMyProduct = false; /* 隐藏自选按钮 */
        	
        	list.add(new ProductListItemMap(DataMan.KEY_MARKET_ID, marketId, market_name, minPrice, maxPrice, averagePrice, hostPrice, unit, addToMyProduct));
        	
        	productMarketLines += marketId + COMMON_TOKEN + market_name + COMMON_TOKEN +
        			minPrice + COMMON_TOKEN + maxPrice + COMMON_TOKEN + 
        			averagePrice + COMMON_TOKEN + hostPrice + COMMON_TOKEN + unit + "\n";
        }
        
        FileUtils.WriteText(DataFile(""), productMarketFileName, productMarketLines);

        return list;
	}
	
	// 一天的毫秒数： 1天=24*60*60*1000=86400000毫秒
	private static final long MILLIS_ONE_DAY = 86400000;
	
	/**
	 * 获取最近30天价格信息 HISTORY_PRICE_DAYS
	 * TODO interface 获取历史价格，周期？
	 * @param product_id
	 * @param market_id
	 * @return
	 * 产品id或者市场id为空的话返回空
	 */
	public static ProductPriceInfo GetHistoryPrice(String product_id, String market_id) {
		
		// 产品id或者市场id为空的话返回空
		if (market_id == null || product_id == null)
			return null;

		// TODO 获取价格单位
		// 价格单位（万元，元，角等）
		String priceUnit = "元";
		// 日期单位（年，月，周等）
		String dateUnit = "月.日";
		
		ProductPriceInfo info = new ProductPriceInfo(priceUnit, dateUnit);

		// 日期格式（月.日）
		SimpleDateFormat simpleDate = new SimpleDateFormat("M.d", Locale.CHINA); //如果写成年月日的形式的话，要写小d，如："yyyy/MM/dd"

		// 向前减去30天
		long today = System.currentTimeMillis();
		for (int i = 0; i < HISTORY_PRICE_DAYS; ++i) {

			// 获取当天价格
			Float price = GetPrice(today, product_id, market_id);
			// 添加当天的价格
			if (price != 0F)
				info.add(simpleDate.format(new Date(today)), price);

			today -= MILLIS_ONE_DAY;
		}
		
		return info;
	}
	
	/**
	 * TODO 获取某天的价格
	 * @param today
	 * @return
	 */
	private static Float GetPrice(long today, String product_id, String market_id) {
		long time = System.currentTimeMillis();
		return 4.0F + (time % 4);
	}

	/**
	 * 获取产品分类
	 * @return
	 */
	public static List<ListItemMap> GetProductClassList() {
		//return ReadCommonIdName(FILE_NAME_PRODUCT_CLASS, KEY_PRODUCT_CLASS_ID);
		
		// 优化效率 省级地址
		String productClassCacheFileName = "productClass.txt";
	    if (FileUtils.IsFileExist(DataFile(productClassCacheFileName)))
	    	return ReadCommonIdName(productClassCacheFileName, KEY_PRODUCT_CLASS_ID);
		
	    String productClassLines = "";
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(FILE_NAME_COMMODITY);
        
        for (String line : lines)  
        {
        	// id,名字
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		String strId =  token[0];
        		int id =  ParseInt(strId);

        		// id 编码：两位数是分类
        		if (id != INVALID_ID && id < 100) {

	        		String name = token[1];

	        		list.add(new ListItemMap(name/* 名字 */, KEY_PRODUCT_CLASS_ID, strId/* id */));
	        		
	        		productClassLines += strId + COMMON_TOKEN + name + "\n";
        		}
        	}
        }

        FileUtils.WriteText(DataFile(""), productClassCacheFileName, productClassLines);

        return list;
	}
	
	/**
	 * 获取产品列表
	 * @return
	 */
	public static List<ListItemMap> GetProductList() {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(FILE_NAME_COMMODITY);
        
        for (String line : lines)  
        {
        	// id,名字
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		int id =  ParseInt(token[0]);

        		// id 编码
        		if (id != INVALID_ID && id > 100) {

	        		String name = token[1];

	        		list.add(new ListItemMap(name/* 名字 */, KEY_PRODUCT_ID, token[0]/* id */));
        		}
        	}
        }

        return list;
	}
	
	/**
	 * 根据产品分类获取供求信息列表
	 * @return
	 */
	interface SupplyDemandListener {
		boolean meetCondition(String[] token, String line/* FIXME 临时的*/);
	}
	private static List<String> GetSupplyDemandLinesCache = null;
	private static List<ListItemMap> GetSupplyDemandList(SupplyDemandListener listener) {
		
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		if (GetSupplyDemandLinesCache == null)
			GetSupplyDemandLinesCache = ReadLines(FILE_NAME_SUPPLY_DEMAND_INFO, "GB2312");

        for (String line : GetSupplyDemandLinesCache)
        {
        	// product_id,产品名,供求信息id(第一位编码0代表供应，1代表求购),user,标题,供求信息内容,发布时间,有效期,数量,单价,产地,产品特点,联系人名字,联系电话,手机号,详细地址
        	// 1003021000,果树苗,0,,供应各种果树苗、绿化苗,,2011-2-23,2011-3-22,,面议,北京市,,王敏,13521120562,13521120562,陆辛庄华源发苗木市场
        	String[] token = GetToken(line);
        	if (token.length != 16)
        		continue;

        	/*
        	int product_id = ParseInt(token[0]);
    		if (!ProductClassMatch(product_class_id, product_id))
    			continue;

    		int supply_demand_id = ParseInt(token[2]);
    		if (supply_demand_id == INVALID_ID)
    			continue;
    		*/
    		
    		if (listener.meetCondition(token, line))
    			list.add(GetSupplyDemandMap(token));  
        }

        return list;
	}

	/**
	 * 获取供求信息列表
	 * @return
	 */
	private static String supplyDemandClassLines = "";
	public static List<ListItemMap> GetSupplyDemandList(final String product_class_id, final boolean supply) {
		
		String supplyDemandClassCache = "supplyDemandClass_" + product_class_id + "_" + supply +"_.txt";
		
		// 优化效率 省级地址
	    if (FileUtils.IsFileExist(DataFile(supplyDemandClassCache))) {
	    	List<String> lines = ReadLines(supplyDemandClassCache);
	    	List<ListItemMap> list = new ArrayList<ListItemMap>();

	        for (String line : lines)
	        {
	        	// product_id,产品名,供求信息id(第一位编码0代表供应，1代表求购),user,标题,供求信息内容,发布时间,有效期,数量,单价,产地,产品特点,联系人名字,联系电话,手机号,详细地址
	        	// 1003021000,果树苗,0,,供应各种果树苗、绿化苗,,2011-2-23,2011-3-22,,面议,北京市,,王敏,13521120562,13521120562,陆辛庄华源发苗木市场
	        	String[] token = GetToken(line);
	        	if (token.length != 16)
	        		continue;

	    		list.add(GetSupplyDemandMap(token));  
	        }
	        
	        return list;
	    }
	    
	    supplyDemandClassLines = "";
	    
		SupplyDemandListener listener = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token, String line) {
				
				String product_id = token[0];
				//int product_id = ParseInt(token[0]);
	    		if (!ProductClassMatch(product_class_id, product_id))
	    			return false;

	    		int supply_demand_id = ParseInt(token[2]);
	    		if (supply_demand_id == INVALID_ID ||
	    			IsSupply(supply_demand_id) != supply)
	    			return false;
	    		
	    		supplyDemandClassLines += line + "\n";

	    		return true;
			}
		};

		List<ListItemMap> list = GetSupplyDemandList(listener);
		
		FileUtils.WriteText(DataFile(""), supplyDemandClassCache, supplyDemandClassLines);
		
		return list;
	}

	// 产品分类id
	public final static String SUPPLY_DEMAND_INFO_PRODUCT_CLASS = "product_class";
	// 发布供求信息的user_id
	public final static String SUPPLY_DEMAND_INFO_KEY_USER = "user";
	// 标题
	public final static String SUPPLY_DEMAND_INFO_KEY_TITLE = "title";
	// 信息
	public final static String SUPPLY_DEMAND_INFO_KEY_MESSAGE = "message";
	// 发布日期
	public final static String SUPPLY_DEMAND_INFO_KEY_POST_TIME = "post_time";
	// 有效期
	public final static String SUPPLY_DEMAND_INFO_KEY_INVALIDATE_DATE = "invalid_date";
	// 数量
	public final static String SUPPLY_DEMAND_INFO_KEY_AMOUNT = "amount";
	// 单价
	public final static String SUPPLY_DEMAND_INFO_KEY_PRICE = "price";
	// 产地
	public final static String SUPPLY_DEMAND_INFO_KEY_HOST = "host";
	// 产品特点
	public final static String SUPPLY_DEMAND_INFO_KEY_FEATURE = "feature";
	// 联系人名字
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_NAME = "contact_name";
	// 联系人电话
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_TEL = "tel";
	// 联系人手机
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_PHONE = "phone";
	// 联系人详细地址
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_ADDRESS = "address";
	
	/**
	 * 获取供求信息
	 * @param supply_demand_id
	 * @return
	 */
	/*
	public static ListItemMap GetSupplyDemandInfo(int supply_demand_id) {

		if (GetSupplyDemandLinesCache == null)
			GetSupplyDemandLinesCache = ReadLines(FILE_NAME_SUPPLY_DEMAND_INFO);
        
        for (String line : GetSupplyDemandLinesCache)
        {
        	// product_id,产品名,供求信息id(第一位编码0代表供应，1代表求购),标题,供求信息内容,发布时间,有效期,数量,单价,产地,产品特点,联系人名字,联系电话,手机号,详细地址
        	String[] token = GetToken(line);
        	if (token.length != 16)
        		continue;
        	
        	int supply_demand_id_parsed = ParseInt(token[2]);

        	// 匹配成功，返回供求信息
    		if (supply_demand_id_parsed == supply_demand_id) {
    			return GetSupplyDemandMap(token);
    		}
        }

		return null;
	}
	*/
	
	/**
	 * 保存详细供求信息map
	 */
	private static ListItemMap GetSupplyDemandMap(String[] token) {
		String title = token[4];
		
		String supply_demand_id = title; // interface 没有供求信息id
		ListItemMap map = new ListItemMap(title, KEY_SUPPLY_DEMAND_INFO_ID, supply_demand_id);
		
		// 1003021000,果树苗,0,,供应各种果树苗、绿化苗,,2011-2-23,2011-3-22,,面议,北京市,,王敏,13521120562,13521120562,陆辛庄华源发苗木市场
		int product_id = ParseInt(token[0]);
		if (product_id != INVALID_ID) {
			map.put(SUPPLY_DEMAND_INFO_PRODUCT_CLASS,       ProductClassDecode(product_id));
			map.put(SUPPLY_DEMAND_INFO_KEY_USER,            "TODO");
			map.put(SUPPLY_DEMAND_INFO_KEY_TITLE,           title);
			map.put(SUPPLY_DEMAND_INFO_KEY_MESSAGE,         token[5]);
			map.put(SUPPLY_DEMAND_INFO_KEY_POST_TIME,       token[6]);
			map.put(SUPPLY_DEMAND_INFO_KEY_INVALIDATE_DATE, token[7]);
			map.put(SUPPLY_DEMAND_INFO_KEY_AMOUNT,          token[8]);
			map.put(SUPPLY_DEMAND_INFO_KEY_PRICE,           token[9]);
			map.put(SUPPLY_DEMAND_INFO_KEY_HOST,            token[10]);
			map.put(SUPPLY_DEMAND_INFO_KEY_FEATURE,         token[11]);
			map.put(SUPPLY_DEMAND_INFO_KEY_CONTACT_NAME,    token[12]);
			map.put(SUPPLY_DEMAND_INFO_KEY_CONTACT_TEL,     token[13]);
			map.put(SUPPLY_DEMAND_INFO_KEY_CONTACT_PHONE,   token[14]);
			map.put(SUPPLY_DEMAND_INFO_KEY_CONTACT_ADDRESS, token[15]);
		}
		
		return map;
	}

	/**
	 * 获取供求对接信息列表(不分供求)
	 * @return
	 */
	public static List<ListItemMap> GetSupplyDemandPairList(final String product_class_id) {
		
		final String curUserId = UserMan.GetUserId();
		
		Debug.Log("curUserId=" + curUserId);
		
		SupplyDemandListener listener = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token, String line) {

				// 筛选我的供求信息
	        	// 判断是当用户自己的供求信息
	        	if (curUserId.equals(token[3])) {
	        		// 保存产品分类id和供求信息id
	        		String product_id = token[0];
	        		//int product_class    = ProductClassDecode(product_id);
	        		
	        		// 产品分类
	        		if (ProductClassMatch(product_class_id, product_id))
	        			return true;
	        	}

	    		return false;
			}
		};
		
		final List<ListItemMap> myInfoList = GetSupplyDemandList(listener);
		
		SupplyDemandListener listener2 = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token, String line) {
	        
	        	// 判断是当用户自己的供求信息
	        	if (curUserId.equals(token[3]))
	        		return false;
	        	
	        	//int product_class    = ParseInt(token[0]);
	        	String product_class    = token[0];
        		int supply_demand_id = ParseInt(token[2]);
        		
	        	// 判断是否对接
	        	for (ListItemMap pairInfo : myInfoList) {
	        		
	        		// 用到亦或操作
	        		if (ProductClassMatch(ListItemMap.GetMapString(pairInfo, KEY_PRODUCT_CLASS_ID), product_class) && 
	        			(IsSupply(supply_demand_id) ^ IsSupply(ListItemMap.GetMapInt(pairInfo, KEY_SUPPLY_DEMAND_INFO_ID)))) {
	        			return true;
	        		}
	        	}

	    		return false;
			}
		};
		
		return GetSupplyDemandList(listener2);
	}

	/**
	 * 获取我的商圈朋友列表
	 * majorIid: INVALID_ID 即返回所有商友列表
	 * @return
	 * @param
	 * myFriend：是否查询我的好友
	 */
	public static List<ListItemMap> GetMyGroupFriendList(int majorIid, boolean myFriend) {
		
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_MY_FRIEND);
        
        for (String line : lines)  
        {
        	// user,名字,专业,联系地址,联系电话
        	//0,zhangsan,张三,专业1,北京通州,13812341234,自我介绍
        	String[] token = GetToken(line);
        	if (token.length == 6) {

        		int id = ParseInt(token[0]);

        		if (id != INVALID_ID) {
        			
        			// XXX 待优化：从cache中取出，然后匹配
        			int major_id = ParseInt(token[3]);
        			String major = "未知专业";
        			
        			if (major_id < MAJOR.length)
        				major = MAJOR[major_id];
        			else
        				Debug.Log("严重错误：未知专业，" + major_id);

        			// 取所有商友或者商友匹配，不然继续
        			if (majorIid != INVALID_ID && majorIid != major_id)
        				continue;

	        		String name = token[2];
	        		String address = token[4];
	        		String telephone = token[5];
	        		String introduce = "TODO介绍";//token[6];
	        		
	        		if (myFriend) {
	        			// TODO 判断是否我的好友
	        		}
	
	        		ListItemMap map = new ListItemMap(name/* 名字 */, KEY_FRIEND_ID, token[0]/* id */);
	        		
	        		map.put(KEY_FRIEND_MAJOR, major);
	        		map.put(KEY_FRIEND_ADDRESS, address);
	        		map.put(KEY_FRIEND_TELEPHONE, telephone);
	        		map.put(KEY_FRIEND_INTRODUCE, introduce);
	        		
	        		list.add(map);
        		}
        	}
        }

        return list;
	}
	
	/**
	 * 查询我的朋友信息
	 * @return
	 */
	public static ListItemMap GetMyFriendInfo(String friend_id) {

		List<ListItemMap> friendList = GetMyGroupFriendList(INVALID_ID, true);
		
		if (friend_id != null) {
			for (ListItemMap item : friendList) {
				if (item.getString(KEY_FRIEND_ID).equals(friend_id))
					return item;
			}
		}
		
		// 返回空
		return null;
	}
	
	/**
	 * 获取我的商圈朋友留言列表
	 * fiend_id:INVALID_ID 即返回所有留言信息
	 * 0 表示返回我的留言
	 * @return
	 */
	public static List<ListItemMap> GetMyGroupMessageList(int fiend_id) {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_MY_GROUP_MESSAGE);
        
        //int  = ParseInt(friendId);
        
        // TODO 待删除测试代码
        list.add(new ListItemMap("我的自我介绍……"/* 名字 */, KEY_MY_GROUP_MESSAGE_ID, "friend"/* id */));
        list.add(new ListItemMap("你好，我有一百吨大米出售，请联系我：18911939853"/* 名字 */, KEY_MY_GROUP_MESSAGE_ID, "id"/* id */));
        list.add(new ListItemMap("我想买你的苹果，你的电话是多少？"/* 名字 */, KEY_MY_GROUP_MESSAGE_ID, "id"/* id */));
        
        for (String line : lines)  
        {
        	// message_id,owner(发起人id),发起人名字,发起人专业,poster(发言人id),发言人名字,发言人专业,日期,联系地址,联系电话,留言内容
        	String[] token = GetToken(line);
        	if (token.length == 9) {

        		int id =  ParseInt(token[0]);

        		if (id != INVALID_ID) {
        			
        			// INVALID_ID 表示获取全部信息
        			if ((id != fiend_id && fiend_id != INVALID_ID) ||
        				(fiend_id != 0 && !UserMan.GetUserId().equals(token[1])))
        				continue;

        			int owner_id = ParseInt(token[1]);
	        		String owner = token[2];
	        		
	        		int poster_id = ParseInt(token[3]);
	        		String poster = token[4];

	        		// 查询我的留言
        			if (MY_MESSAGE == fiend_id && !token[1].equals(UserMan.GetUserId())) {
        				continue;
        			}

	        		String date = token[5];
	        		String address = token[6];
	        		String telephone = token[7];
	        		String message = token[8];
	
	        		ListItemMap map = new ListItemMap(poster/* 名字 */, KEY_MY_GROUP_MESSAGE_ID, "id"/* id */);
	        		
	        		map.put(KEY_FRIEND_MAJOR, owner_id);
	        		map.put(KEY_FRIEND_MESSAGE_POSER, owner);
	        		map.put(KEY_FRIEND_MESSAGE_POSER_ID, poster_id);
	        		map.put(KEY_FRIEND_MESSAGE_DATE, date);
	        		map.put(KEY_FRIEND_ADDRESS, address);
	        		map.put(KEY_FRIEND_TELEPHONE, telephone);
	        		map.put(KEY_FRIEND_MESSAGE_CONTENT, message);
	        		
	        		list.add(map);
        		}
        	}
        }

        return list;
	}

	/**
	 * 发布新留言
	 * @param userId
	 * @param friendId
	 * @param message
	 * @return
	 */
	public static boolean PostNewMessage(String userId, String friendId, String message) {
		// TODO interface 留言参数需调整? encoding?
		String params = "user=" + userId + "&friend=" + friendId + "&message=" + message;

		String ret = Downloader.PostUrl(URL_SERVER + URL_POST_MESSAGE, params);

		if (ret.equals("true"))
			return true;
		
		Debug.Log("发布留言错误：返回：" + ret);

		return false;
	}
	
	/**
	 * 获取AIS第一级分类列表
	 * 如果function_id为0则返回一级分类，否则返回function_id对应的子级分类
	 * @return
	 */
	public static List<ListItemMap> GetAisClassList(int function_id) {

		if (!FileUtils.IsFileExist(DataMan.DataFile(FILE_NAME_AIS_CLASS))) {
			GenerateAisClassCache();
		}

		List<ListItemMap> list = ReadCommonIdName(FILE_NAME_AIS_CLASS, KEY_AIS_CLASS_ID);

		// 遍历列表并删除不满足条件的项
		Iterator<ListItemMap> it = list.iterator();

        while (it.hasNext()) {
        	ListItemMap item = it.next();

        	if (!AisClassMatch(function_id, item.getInt(KEY_AIS_CLASS_ID)))
				it.remove();
        }

        return list;
	}
	
	/**
	 * 生成Ais分类文件（优化效率）
	 */
	private static void GenerateAisClassCache() {
		List<String> lines = ReadLines(FILE_NAME_AIS_LIST);
		
		Map<String, String> map = new HashMap<String, String>();
		
		List<ListItemMap> productClassList = GetProductClassList();
		
		String id, name, content = "";
		for (String line : lines) {
			String[] token = line.split(TOKEN_SEP);
			
			id = token[1];
			name = FindCommodityName(productClassList, KEY_PRODUCT_CLASS_ID, id);
			
			// 如果没有名字就以id为名字
			if (name == null)
				name = id;

			if (map.get(id) == null) {
				map.put(id, "name:"+token[1]); // TODO 产品分类名字
				
				content += id + TOKEN_SEP + name + "\n";
			}
		}
		
		FileUtils.WriteText(DataFile(""), FILE_NAME_AIS_CLASS, content);
	}
	
	/**
	 * 查找商品名字
	 * @param id
	 * @return
	 */
	private static String FindCommodityName(List<ListItemMap> productList, String key, String id) {
		/*
		List<String> lines = ReadLines(FILE_NAME_COMMODITY);
		
		int intId = ParseInt(id);
		if (intId == INVALID_ID)
			return null;

		String[] token;
		for (String line : lines) {
			token = line.split(TOKEN_SEP);
			if (token != null && token.length >= 2) {
				if (intId == ParseInt(token[0]))
					return token[1];
			}
		}
		*/
		
		for (ListItemMap item : productList) {
			if (item.getString(key).equals(id))
				return item.getString(KEY_NAME);
		}

		return null;
	}
	
	/**
	 * 获取AIS第二级分类列表
	 * @param class_id
	 * INVALID返回所有
	 * @return
	 */
	public static List<ListItemMap> GetAisList(int class_id) {

		List<ListItemMap> list = new ArrayList<ListItemMap>();
		List<String> lines = ReadLines(FILE_NAME_AIS_LIST);
		
		String[] token;
		for (String line : lines) {
			token = line.split(TOKEN_SEP);
			if (token != null && token.length >= 2) {
				String calssId = token[1];
				if (class_id == INVALID_ID || class_id == ParseInt(calssId)) {
					// 符合条件
					String id = token[0];
					String name = token[2];
					list.add(new ListItemMap(name, KEY_AIS_ID, id));
				}
			}
		}

        return list;
	}

	/**
	 * 返回加上路径的数据文件名
	 * @param fileName
	 * @return
	 */
	public static String DataFile(String fileName) {
		String appPath = AppDataPath();
		File file = new File(appPath);
		
		// 如果不是目录先删除
		if (file.exists() && !file.isDirectory())
			file.delete();
		
		// 只创建第一级目录，不用mkdirs（sd卡文件夹默认存在）
		if (!file.exists() && !file.mkdir()) {
			Debug.Log("严重错误：DataFile，创建数据文件夹失败");
		}
			
		return appPath + "/" + fileName;
	}
	
	/**
	 * 返回加上路径的数据文件名
	 * @param fileName
	 * @return
	 */
	private static String AppDataPath() {
		return Environment.getExternalStorageDirectory() + "/zknx.hn";
	}
	
	/**
	 * 检查今天广播数据是否需要更新（时间戳不匹配并且数据文件存在）
	 * @return
	 */
	public static boolean ShouldUpdateData() {

		String today = GetCurrentTime(false);
		String dataFileName = DataFile(today + ".zip");

		List<String> line = ReadLines(TIME_STAMP_FILE_NAME);

		// 没有时间戳，或者时间戳不匹配
		boolean timStampNotMatch = (line.size() == 0) || (!line.get(0).equals(today));

		// 时间戳不匹配，且当天数据文件存在
		return timStampNotMatch && FileUtils.IsFileExist(dataFileName);
	}
	
	/**
	 * 写日期时间戳，用于标记今天是否解压过数据更新
	 * @param today
	 * @return
	 */
	private static boolean WriteTimeStamp(String today) {
		return FileUtils.WriteFile(DataFile(TIME_STAMP_FILE_NAME), today.getBytes());
	}

	/**
	 * 检查广播数据
	 * TODO (讨论) 检查广播数据更新，并更新界面？
	 */
	public static boolean CheckBroadcastData() {
		// 模拟耗时操作
		/*
		int x = 3000;
		while(x-- > 0)
			Debug.Log("" + x);
		*/
		
		GetNewMessages();

		// 检查时间戳
		if (ShouldUpdateData()) {
			return UpdateTodayData();
		}
		
		return false;
	}
	
	/**
	 * 获取当前用户新的留言
	 */
	private static void GetNewMessages() {
		Downloader.DownFile(URL_GET_MESSAGE + "?userid=" + UserMan.GetUserId(), DataFile(""), FILE_NAME_NEW_MESSAGE);
	}
	
	/**
	 * 更新当天数据
	 * @return
	 */
	public static boolean UpdateTodayData() {

		// 获取当天日期
		String today = GetCurrentTime(false);
		String dataFileName = DataFile(today + ".zip");

		// 解压数据更新
		if (FileUtils.IsFileExist(dataFileName) &&
			Ziper.UnZip(dataFileName, DataFile(""))) {
			// 写时间戳
			return WriteTimeStamp(today);
		}

		return false;
	}

	private static final String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final SimpleDateFormat mTimeFormater = new SimpleDateFormat(TIME_FORMAT, Locale.CHINA);
	private static final SimpleDateFormat mDateFormater = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
	
	/**
	 * 获取日期格式者
	 * @return
	 * @param withTime
	 * 是否带有时间
	 */
	private static SimpleDateFormat GetDateFormator(boolean withTime) {
		return withTime ? mTimeFormater : mDateFormater;
	}

	/**
	 * 获取当前日期
	 * @return
	 * @param withTime
	 * 是否带有时间
	 */
	public static String GetCurrentTime(boolean withTime) {
		return GetDateFormator(withTime).format(new java.util.Date());
	}

	/**
	 * 解析日期为long
	 * @return
	 * 解析错误返回0
	 * @param withTime
	 * 是否带有时间
	 */
	public static long ParseDate(String time, boolean withTime) {
		try {
			return GetDateFormator(withTime).parse(time).getTime();
		} catch (ParseException e) {
			Debug.Log("错误：ParseDate，" + e.getMessage());
		}

		return 0;
	}

	/**
	 * 获取ais文件路径名
	 * @param ais_id
	 * @return
	 */
	public static String GetAisFilePathName(String ais_id) {
		List<String> lines = DataMan.ReadLines(DataMan.FILE_NAME_AIS_LIST);

		String token[];
		for (String line : lines) {
			token = line.split(DataMan.COMMON_TOKEN);
			
			if (token != null && token.length >= 4 && token[0].equals(ais_id)) {
				return DataMan.DataFile(token[2] + AIS_SURFIX);
			}
		}

		return "空";
	}

	/**
	 * 发布供求信息
	 * TODO 待测试 完善发布供求信息：完善参数，功能
	 * @param product_id
	 * @return
	 */
	public static class SupplyDemandInfo {
		public int type;
		public String title;
		public String commodityid;
		public String count;
		public String publishdate;
		public String validity;
		public String place;
		public String unit;
		public String price;
	}
	
	public static boolean PostSupplyDemandInfo(SupplyDemandInfo info) {
		//type=0&title=供应西红柿&userid=linshi&addressid=06056&commodityid=0305002000&count=大量&price=5&unit=元/公斤&phonenumber=15941652887&place=辽宁锦州凌海市新庄子乡小马村范坨&linkman=刘春宇&remark=备注&validity=2013-06-24&publishdate=2013-05-25
		String params = "type=" + info.type + 
			"&title=" + info.title + 
			"&userid=" + UserMan.GetUserId() + 
			"&addressid=" + UserMan.GetUserAddressId() +
			"&commodityid=" + info.commodityid + 
			"&count=" + info.count +
			"&price=" + info.price + 
			"&unit=" + info.unit + 
			"&phonenumber=" + UserMan.GetUserPhone() + 
			"&place=" + info.place + 
			"&linkman=" + UserMan.GetUserName() + 
			"&remark=" + // NO need remark 
			"&validity=" + info.validity + 
			"&publishdate=" + info.publishdate;
		
		String ret = Downloader.PostUrl(URL_SERVER + URL_POST_SUPPLY_DEMAND_INFO, params);

		if (ret.equals("0"))
			return true;
		
		Debug.Log("发布供求信息错误：返回：" + ret);

		return false;
	}
	
	/**
	 * 从用户名分析专业id
	 * @param userId
	 * @return
	 */
	public static int GetMajor(String userId) {
		//return DataInterface.GetMajor(userId);
		return -1;
	}
	
	/**
	 * TODO interface 向专家提问
	 * @return
	 */
	public static boolean AskExpert(String userId, String expertId, String subject, String question) {
		// TODO interface 提问参数需调整? encoding?
		String params = "user=" + userId + ",expert=" + expertId + ",subject=" + subject + ",question=" + question;

		String ret = Downloader.PostUrl(URL_ASK_EXPERT, params);

		if (ret.equals("true"))
			return true;
		
		Debug.Log("提问专家错误：返回：" + ret);

		return false;
	}
}
