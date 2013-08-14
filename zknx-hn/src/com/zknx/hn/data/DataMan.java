package com.zknx.hn.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.zknx.hn.common.Debug;
import com.zknx.hn.common.Restraint;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.functions.ais.AisDoc;
import com.zknx.hn.functions.common.ProductListAdapter;
import com.zknx.hn.functions.common.ProductPriceInfo;

@SuppressLint("SimpleDateFormat")
public class DataMan extends DataInterface {

	// 通用为名字，也用于ListItem显示文本
	public static final String KEY_NAME = "name";

	public static final String KEY_ADDRESS_ID = "address_id";
	public static final String KEY_MARKET_ID  = "market_id";
	public static final String KEY_PRODUCT_ID = "product_id";
	public static final String KEY_PRODUCT_CLASS_ID = "product_class_id"; //  产品分类
	public static final String KEY_SUPPLY_DEMAND_INFO_ID = "supply_demand_info_id"; // 供求信息id
	public static final String KEY_FRIEND_ID = "friend_id";
	//public static final String KEY_AIS_ID = "ais_id"; // ais_id 暂时无用
	public static final String KEY_AIS_FILE_NAME = "ais_name";
	// ais分类
	public static final String KEY_AIS_COLUMN = "ais_column";
	// ais子分类
	public static final String KEY_AIS_COLUMN_CHILD = "ais_column_child";
	// ais日期
	public static final String KEY_AIS_DATE = "ais_date";

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

	// 专家问答
	public static final String KEY_EXPERT_ID = "expert_id";
	public static final String KEY_EXPERT_MAJOR = "expert_major";
	public static final String KEY_EXPERT_INTRODUCE = "expert_introduce";
	public static final String KEY_EXPERT_QUESTION_SUBJECT = "expert_question_subject";
	public static final String KEY_EXPERT_QUESTION_CONTENT = "expert_question_content";

	// 临时文件名
	public static final String FILE_NAME_TMP = "tmp.txt";
	
	// 数据更新时间戳文件
	private static final String TIME_STAMP_FILE_NAME = ".timestamp";

	// 默认非法id值
	public static final int INVALID_ID = -1;
	// 用于查询我的留言的标志
	//public static final int MY_MESSAGE = 0;

	// 通用分隔符
	public static final String COMMON_TOKEN = ",";

	/**
	 * 按行读取文本文件
	 * @param fileName
	 * @return
	 */
	public static List<String> ReadLines(String fileName, boolean root) {
		// 默认编码
		return ReadLinesWithEncoding(fileName, "GB2312", root);
	}
	
	/**
	 * 兼容设计
	 * @param fileName
	 * @return
	 */
	public static List<String> ReadLines(String fileName) {
		// 默认编码 "UTF-8" "GB2312"
		return ReadLinesWithEncoding(fileName, "GB2312", false);
	}
	
	/**
	 * 按行读取文本文件
	 * @param fileName
	 * @return
	 */
	private static List<String> ReadLinesWithEncoding(String fileName, String encoding, boolean root) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			String filePathName = DataFile(fileName, root);
			
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
	private static List<ListItemMap> ReadCommonIdName(String fileName, String key, boolean root) {

        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(fileName, root); // 都生成在root文件夹
        
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
	
	private static List<ListItemMap> ReadCommonIdName(String fileName, String key) {
		return ReadCommonIdName(fileName, key, false);
	}

	/**
	 * 获取地址列表
	 * @return
	 */
	public static List<ListItemMap> GetAddressList() {
		
	    ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
	    
	    // 优化效率 省级地址
	    if (FileUtils.IsFileExist(DataFile(FILE_NAME_ADDRESS_PROVINCE, true)))
	    	return ReadCommonIdName(FILE_NAME_ADDRESS_PROVINCE, KEY_ADDRESS_ID, true);

	    String provinceLines = "";
        List<String> lines = ReadLines(FILE_NAME_ADDRESS, true);
        
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
        
        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, FILE_NAME_ADDRESS_PROVINCE, provinceLines);

        return list;
	}

	/**
	 * 获取市场列表
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByArea(int address_id) {

		String marketCacheFileName = "markets/market_" + address_id + ".txt";
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        
        // 优化效率
        if (FileUtils.IsFileExist(DataFile(marketCacheFileName, true)))
        	return ReadCommonIdName(marketCacheFileName, KEY_MARKET_ID, true);
        
        String marketLines = "";
        List<String> lines = ReadLines(FILE_NAME_MARKETS, true);
        
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
        
        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, marketCacheFileName, marketLines);

        return list;
	}

	/**
	 * 获取产品列表
	 * @return
	 */
	public static List<ListItemMap> GetProductList(int market_id) {
		// 获取今天的产品列表
		return GetProductList(new Date(), market_id, false);
	}
	
	public static List<ListItemMap> GenProductList(int market_id) {
		// 生成今天的产品列表
		return GetProductList(new Date(), market_id, true);
	}

	/**
	 * null日期表示当天
	 * @param date
	 * @param market_id
	 * @param justGen
	 * @return
	 */
	private static List<ListItemMap> GetProductList(Date date, int market_id, boolean justGen) {

        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        if (market_id == INVALID_ID)
        	return list;
        
        List<ListItemMap> myProducts = null;
        if (!justGen)
        	myProducts = GetMyProductList();
        
        List<String> lines;

        String dateString = "";
        if (date != null)
        	dateString = mDateFormater.format(date) + "/";

        String marketProductsFileName = dateString + "products/" + market_id + "_products.txt";

        // 优化
        if (FileUtils.IsFileExist(DataFile(marketProductsFileName, true))) {
        	lines = ReadLines(marketProductsFileName, true);
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
        		boolean isMyProduct = false;

        		// 生成时不用关心是否自选
        		if (!justGen)
        			isMyProduct = IsMyProduct(myProducts, product_id); /* 添加自选按钮状态 */

        		AddProductList(list, productId, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct);
            }

        	return list;
        }

        String marketProductLines = "";
        lines = ReadLines(dateString + FILE_NAME_PRODUCTS_PRICE, true);
        for (String line : lines)
        {
        	String[] token = GetToken(line);
        	// 0101301,北京昌平区水屯农副产品批发市场,0101001000,0101301,13.5,元/公斤
        	if (token.length == 6) {
        		// market_id,市场名字,product_id,产品名,最低价,最高价,平均价,产地价,价格单位
        		// 1506901,山东寿光批发市场,0101001000,花生,3.23,元/公斤
        		int market_id_parsed = ParseInt(token[0]);
        		if (market_id_parsed != market_id)
        			continue;

        		// 暂不需要市场名字
        		// String market_name = token[1];

        		String productId = token[2];
        		int product_id = ParseInt(productId);
        		if (product_id == INVALID_ID)
        			continue;

        		String product_name = token[3];
        		String minPrice = token[4];
        		String maxPrice = minPrice;//token[5];
        		String averagePrice = minPrice;//token[6];
        		String hostPrice = minPrice;// token[7];
        		String unit = token[5];
        		boolean isMyProduct = false;

        		// 生成时不用关心是否自选
        		if (!justGen)
        			isMyProduct = IsMyProduct(myProducts, product_id); /* 添加自选按钮状态 */
        		
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

        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, marketProductsFileName, marketProductLines);

        return list;
	}
	
	/**
	 * 添加产品列表
	 */
	private static void AddProductList(ArrayList<ListItemMap> list, String productId, 
			String product_name, String minPrice, String maxPrice, 
			String averagePrice, String hostPrice, String unit, boolean isMyProduct) {
		list.add(new ProductListItemMap(KEY_PRODUCT_ID, productId, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct));
	}

	/**
	 * 判断是否自选产品
	 * @param product_id
	 * @return
	 */
	private static boolean IsMyProduct(List<ListItemMap> list, int productId) {
		
		if (list == null) {
			Debug.Log("IsMyProduct错误");
			return false;
		}

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
		return ReadCommonIdName(FILE_NAME_MY_PRODUCTS, KEY_PRODUCT_ID, true);
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
		// 如果自选产品列表文件存在，则覆盖
		String lines = "";
	    for (ListItemMap item : list) {   
	
	    	int product_id_to_be_save = item.getInt(KEY_PRODUCT_ID);

	    	if (product_id_to_be_save != INVALID_ID) {
		        String product_name_to_be_save = (String)item.get(KEY_NAME);
		        String line = product_id_to_be_save + COMMON_TOKEN + product_name_to_be_save + "\r\n";
		
		        lines += line + "\n";
	    	}
	    }
	    
	    return (null == FileUtils.WriteGB2312Text(true, FILE_NAME_MY_PRODUCTS, lines));
	}
	
	/**
	 * 获取所有有该产品的市场列表
	 * @return
	 */
	public static void GenMarketListByProduct(String product_id) {
		GetMarketListByProduct(product_id, true);
	}
	
	/**
	 * 获取所有有该产品的市场列表
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByProduct(String product_id) {
		return GetMarketListByProduct(product_id, false);
	}

	/**
	 * 获取所有有该产品的市场列表
	 * @return
	 */
	private static List<ListItemMap> GetMarketListByProduct(String product_id, boolean justGenerate) {

		List<String> lines = null;
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		String productMarketFileName = "markets/product_" + product_id + "_markets.txt";

		if (product_id == null || product_id.length() == 0)
			return list;

		// 优化
		if (FileUtils.IsFileExist(DataFile(productMarketFileName, true))) {
			
			// 只是生成处理数据，不返回
			if (justGenerate)
				return null;

			lines = ReadLines(productMarketFileName, true);
			
			for (String line : lines)
	        {
	        	String[] token = GetToken(line);
	    		
	        	String marketId = token[0];
	    		String market_name = token[1];
	    		String minPrice = token[2];
	    		String maxPrice = token[3];
	    		String averagePrice = token[4];
	    		String hostPrice = token[5];
	    		String unit = token[6];
	    		boolean addToMyProduct = false; /* 隐藏自选按钮 */
	        	
	        	list.add(new ProductListItemMap(DataMan.KEY_MARKET_ID, marketId, market_name, minPrice, maxPrice, averagePrice, hostPrice, unit, addToMyProduct));
	        }
			
			return list;
		}
		
		String productMarketLines = "";
		lines = ReadLines(FILE_NAME_PRODUCTS_PRICE);
		
        for (String line : lines)
        {
        	// 0101301,北京昌平区水屯农副产品批发市场,0101001000,0101301,13.5,元/公斤
        	// market_id,市场名字,product_id,产品名,最低价,最高价,平均价,产地价,价格单位
        	// product_id,product_name
        	String[] token = GetToken(line);
        	if (token.length != 6)
        		continue;

        	//int product_id_parsed = ParseInt(token[2]);
        	// product_id 是没有0开头的格式
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
        
        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, productMarketFileName, productMarketLines);

        return list;
	}
	
	private static Float randomPrice() {
		long time = System.currentTimeMillis();
		return 4.0F + (time % 4);
	}
	
	// 一天的毫秒数： 1天=24*60*60*1000=86400000毫秒
	//private static final long MILLIS_ONE_DAY = 86400000;
	
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
		
		/* 测试用数据
		
		ProductPriceInfo info = new ProductPriceInfo();
		
		// 日期格式（月.日）
		SimpleDateFormat simpleDate = new SimpleDateFormat("M.d", Locale.CHINA);
		// 向前减去7天
		long today = System.currentTimeMillis();
		for (int i = 0; i < 7; ++i) {

			// 获取当天价格
			Float price = randomPrice();
			Debug.Log("随机生成历史价格：" + price);
			// 添加当天的价格
			if (price != 0F)
				info.add(simpleDate.format(new Date(today)), price);

			today -= MILLIS_ONE_DAY;
		}
		
		return info;
		
		*/
		
		/* 服务器生成数据
		// 获取某市场历史价格
		String fileName = "history_price/" + market_id + ".txt";
		List<String> lines = ReadLines(fileName);
		
		String[] token;
		for (String line : lines) {
			token = line.split(COMMON_TOKEN);
			
			if (token.length < 1)
				continue;
			
			String productId = token[0];
			
			if (product_id.length() < 9 ||
				!productId.contains(product_id))
				continue;
			
			return CreatePriceInfo(line);
		}
		
		*/
		
		// 循环查找近30天历史价格
		ProductPriceInfo info = new ProductPriceInfo();
		
		// 日期格式（月.日）
		SimpleDateFormat simpleDate = new SimpleDateFormat("M.d", Locale.CHINA);
		// 向前减去7天
		long today = System.currentTimeMillis();
		for (int i = 0; i < 7; ++i) {

			Date date = new Date(today);
			// 获取当天价格
			Float price = GetProductPrice(date, market_id, product_id);//randomPrice();
			// 添加当天的价格
			if (price != 0F)
				info.add(simpleDate.format(date), price);

			today -= MILLIS_ONE_DAY;
		}
		
		return info;
	}
	
	/*
	 * 获取价格
	 * */
	private static Float GetProductPrice(Date date, String market_id, String product_id) {
		
		List<ListItemMap> products = GetProductList(date, DataMan.ParseInt(market_id), false);
		
		for (ListItemMap product : products) {
			if (product_id.equals(product.getString(KEY_PRODUCT_ID))) {
				String strPrice = product.getString(ProductListAdapter.KEY_PRICE_AVERAGE);
				Float price = 0F;
				try {
					price = Float.parseFloat(strPrice);
				} catch (Exception e) {
					Debug.Log("解析Float价格错误");
				}
				
				return price;
			}
		}
		
		return 0F;
	}
	
	/**
	 * 生成价格信息
	 * @param line
	 * @param priceUnit
	 * @param dateUnit
	 * @return
	 */
	private static ProductPriceInfo CreatePriceInfo(String line) {
		ProductPriceInfo info = new ProductPriceInfo();

		String[] prices = line.split(COMMON_TOKEN);
		
		if (prices.length < 2) {
			Debug.Log("价格信息错误：" + line);
			return null;
		}
		
		// 从列表取数据时正向
		for (int i = prices.length - 1; i > 0; --i) {

			// 获取当天价格
			String[] token = prices[i].split(":");
			if (token.length != 2)
				continue;
			
			String date  = token[0];
			Float price = 0F;
			try {
				price = Float.parseFloat(token[1]);
			} catch (Exception e) {
				e.printStackTrace();
				Debug.Log("价格信息解析错误：" + e.getMessage());
			}
			// 添加当天的价格
			if (price != 0F)
				info.add(date, price);
		}
				
		return info;
	}

	/**
	 * 获取产品分类
	 * @return
	 */
	public static List<ListItemMap> GetProductClassList() {

		// 优化效率 产品分类
		String productClassCacheFileName = "productClass.txt";
	    if (FileUtils.IsFileExist(DataFile(productClassCacheFileName, true)))
	    	return ReadCommonIdName(productClassCacheFileName, KEY_PRODUCT_CLASS_ID, true);

	    String productClassLines = "";
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(FILE_NAME_COMMODITY, true);
        
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

        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, productClassCacheFileName, productClassLines);

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
			GetSupplyDemandLinesCache = ReadLines(FILE_NAME_SUPPLY_DEMAND_INFO, false);

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
	public static List<ListItemMap> GetSupplyDemandList(String product_class_id, boolean supply) {
		//GenSupplyDemandList();
		
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		// 向前减去30天
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			Date date = new Date(today);
			String strDate = mDateFormater.format(date) + "/";

			// 判断当天的是否已经处理过
			String stampFileName = DataFile(date + "processedSDInfo.txt");
			if (!FileUtils.IsFileExist(stampFileName)) {
				ProcessSupplyDemandInfo(date);
			}

			String genFileName = GetGenSupplyDemandFileName(strDate, product_class_id, supply);
			List<String> lines = ReadLinesWithEncoding(genFileName, "UTF-8", true);

			// 当天没有供求
			if (lines.size() == 0)
				continue;

			for (String line : lines) {
				String[] token = GetToken(line);
	        	if (token.length != 16)
	        		continue;
	        	
	        	// 添加供求信息
	        	list.add(GetSupplyDemandMap(token));
			}
		}
        
        return list;
	}
	
	/**
	 * 生成的供求信息
	 * @param date
	 * @param productClass
	 * @param supply
	 * @return
	 */
	private static String GetGenSupplyDemandFileName(String date, String productClass, boolean supply) {
		// 按商品分类+供/求保存生成文件
    	String genFileName = "";
    	if (supply)
    		genFileName = date + "tradinfo/" +productClass + "_supply";
    	else
    		genFileName = date + "tradinfo/" + productClass + "_demand";
    	
    	return genFileName;
	}
	
	/**
	 * 生成供求数据
	 * @param product_class_id
	 * @param supply
	 */
	private static void GenSupplyDemandList() {
		
		List<ListItemMap> productClass = GetProductClassList();
		
		if (productClass == null || productClass.size() == 0) {
			return;
		}

		// 向前减去30天
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {
			ProcessSupplyDemandInfo(new Date(today));
		}
	}

	private static void ProcessSupplyDemandInfo(Date date) {
		
		String strDate = mDateFormater.format(date) + "/";
		
		// 判断当天的是否已经处理过
		String stampFileName = DataFile(strDate + "processedSDInfo.txt", true);
		if (FileUtils.IsFileExist(stampFileName))
			return;
		
		List<String> lines = ReadLines(strDate + FILE_NAME_SUPPLY_DEMAND_INFO, true);
		
		// 当天没有供求
		if (lines.size() == 0)
			return;

		int count = 1;
		for (String line : lines) {
			String[] token = GetToken(line);
			if (token.length != 16)
				continue;

			String productId = token[0];
			String supplyOrDemand = token[2];
			
			String genFileName = GetGenSupplyDemandFileName(strDate, productId.substring(0, 2), 
					supplyOrDemand.equals("0"));

			Debug.Log(strDate + FILE_NAME_SUPPLY_DEMAND_INFO + "：第" + count++ + "行");

			// 附加
			FileUtils.AppendLine(DataFile(genFileName, true), line);
		}

		// 写时间戳用于判断是否已经处理过
		FileUtils.WriteText(stampFileName, strDate);
	}

	// 产品分类id
	public final static String SUPPLY_DEMAND_INFO_PRODUCT_CLASS = "product_class";
	// 发布供求信息的user_id
	public final static String SUPPLY_DEMAND_INFO_KEY_USER = "user";
	// 标题
	public final static String SUPPLY_DEMAND_INFO_KEY_TITLE = "title";
	// 是否supply
	public final static String SUPPLY_DEMAND_INFO_KEY_SUPPLY = "supply";
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
			map.put(SUPPLY_DEMAND_INFO_KEY_SUPPLY,          token[2]);
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
	 * 是否对接信息
	 * @return
	 */
	private static boolean IsPairTradeInfo(List<ListItemMap> mySDInfoList, String[] token) {
		// 02,粮食作物,0,dengyong,qiu liangshi 100dun,,2013-7-22,2013-8-21,12,12345,东华门街道,,鍒樼櫥鍕?,18911939853,18911939853,beijng
		for (ListItemMap item : mySDInfoList) {
			boolean supply = ("0".equals(item.getString(SUPPLY_DEMAND_INFO_KEY_SUPPLY)));
			if ((supply && token[2].equals("1")) ||
				(!supply && token[2].equals("0"))) {
				if (supply)
					Debug.Log("对接信息：" + token[4] + ",我供应：" + item.getString(SUPPLY_DEMAND_INFO_KEY_TITLE));
				else
					Debug.Log("对接信息：" + token[4] + ",我求购：" + item.getString(SUPPLY_DEMAND_INFO_KEY_TITLE));
				return true;
			}
		}

		return false;
	}

	/**
	 * 获取供求对接信息列表(不分供求)
	 * @return
	 */
	private static String pairLines = "";
	public static List<ListItemMap> GetSupplyDemandPairList(String product_class_id) {
		
		// 首先处理数据
		GenSupplyDemandList();
		
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		// 获取我发布的供求信息
		List<ListItemMap> mySDInfoList = new ArrayList<ListItemMap>();
		
		// 向前减去30天
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = mDateFormater.format(new Date(today)) + "/";

			boolean supply = false;
			String genFileName = GetGenSupplyDemandFileName(date, product_class_id, supply);
			List<String> lines = ReadLinesWithEncoding(genFileName, "UTF-8", true);

	        for (String line : lines) {
	        	// 02,粮食作物,0,dengyong,qiu liangshi 100dun,,
	        	// product_id,产品名,供求信息id(第一位编码0代表供应，1代表求购),user,标题,供求信息内容,发布时间,有效期,数量,单价,产地,产品特点,联系人名字,联系电话,手机号,详细地址
	        	// 1003021000,果树苗,0,,供应各种果树苗、绿化苗,,2011-2-23,2011-3-22,,面议,北京市,,王敏,13521120562,13521120562,陆辛庄华源发苗木市场
	        	String[] token = GetToken(line);
	        	if (token.length != 16 ||
	        		!token[3].equals(UserMan.GetUserId()))
	        		continue;
	        	
	        	String productId = token[0];
	        	
	        	if (productId.startsWith(product_class_id))
	        		mySDInfoList.add(GetSupplyDemandMap(token));  
	        }
	        
	        supply = true;
			genFileName = GetGenSupplyDemandFileName(date, product_class_id, supply);
			lines = ReadLinesWithEncoding(genFileName, "UTF-8", true);

	        for (String line : lines) {
	        	// 02,粮食作物,0,dengyong,qiu liangshi 100dun,,
	        	// product_id,产品名,供求信息id(第一位编码0代表供应，1代表求购),user,标题,供求信息内容,发布时间,有效期,数量,单价,产地,产品特点,联系人名字,联系电话,手机号,详细地址
	        	// 1003021000,果树苗,0,,供应各种果树苗、绿化苗,,2011-2-23,2011-3-22,,面议,北京市,,王敏,13521120562,13521120562,陆辛庄华源发苗木市场
	        	String[] token = GetToken(line);
	        	if (token.length != 16 ||
	        		!token[3].equals(UserMan.GetUserId()))
	        		continue;
	        	
	        	String productId = token[0];
	        	
	        	if (productId.startsWith(product_class_id))
	        		mySDInfoList.add(GetSupplyDemandMap(token));  
	        }
		}

		if (mySDInfoList.size() == 0) {
			Debug.Log("当前用户没有发布任何信息");
			return list;
		}

		// 向前减去30天
		today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = mDateFormater.format(new Date(today)) + "/";

			boolean supply = false;
			String genFileName = GetGenSupplyDemandFileName(date, product_class_id, supply);
			List<String> lines = ReadLinesWithEncoding(genFileName, "UTF-8", true);

	        for (String line : lines) {
	        	// 02,粮食作物,0,dengyong,qiu liangshi 100dun,,
	        	// product_id,产品名,供求信息id(第一位编码0代表供应，1代表求购),user,标题,供求信息内容,发布时间,有效期,数量,单价,产地,产品特点,联系人名字,联系电话,手机号,详细地址
	        	// 1003021000,果树苗,0,,供应各种果树苗、绿化苗,,2011-2-23,2011-3-22,,面议,北京市,,王敏,13521120562,13521120562,陆辛庄华源发苗木市场
	        	String[] token = GetToken(line);
	        	if (token.length != 16 ||
	        		token[3].equals(UserMan.GetUserId()))
	        		continue;
	        	
	        	String productId = token[0];
	        	
	        	if (productId.startsWith(product_class_id) &&
	        		IsPairTradeInfo(mySDInfoList, token))
	        		list.add(GetSupplyDemandMap(token));  
	        }
	        
	        supply = true;
			genFileName = GetGenSupplyDemandFileName(date, product_class_id, supply);
			lines = ReadLinesWithEncoding(genFileName, "UTF-8", true);

	        for (String line : lines) {
	        	// 02,粮食作物,0,dengyong,qiu liangshi 100dun,,
	        	// product_id,产品名,供求信息id(第一位编码0代表供应，1代表求购),user,标题,供求信息内容,发布时间,有效期,数量,单价,产地,产品特点,联系人名字,联系电话,手机号,详细地址
	        	// 1003021000,果树苗,0,,供应各种果树苗、绿化苗,,2011-2-23,2011-3-22,,面议,北京市,,王敏,13521120562,13521120562,陆辛庄华源发苗木市场
	        	String[] token = GetToken(line);
	        	if (token.length != 16 ||
	        		token[3].equals(UserMan.GetUserId()))
	        		continue;
	        	
	        	String productId = token[0];
	        	
	        	if (productId.startsWith(product_class_id) &&
	        		IsPairTradeInfo(mySDInfoList, token))
	        		list.add(GetSupplyDemandMap(token));  
	        }
		}
        
        return list;
	}
	
	private static void GenSupplyDemandPairList(String product_class_id) {
		GetSupplyDemandPairList(product_class_id, true);
	}
	
	private static List<ListItemMap> GetSupplyDemandPairList(final String product_class_id, boolean justGen) {
		
		final String curUserId = UserMan.GetUserId();
		
		Debug.Log("curUserId=" + curUserId);
		
		String pairCache = "pair_" + product_class_id +".txt";
		
		// 优化效率 省级地址
	    if (FileUtils.IsFileExist(DataFile(pairCache))) {
	    	if (justGen)
	    		return null;

	    	List<String> lines = ReadLines(pairCache);
	    	List<ListItemMap> list = new ArrayList<ListItemMap>();

	        for (String line : lines) {
	        	// product_id,产品名,供求信息id(第一位编码0代表供应，1代表求购),user,标题,供求信息内容,发布时间,有效期,数量,单价,产地,产品特点,联系人名字,联系电话,手机号,详细地址
	        	// 1003021000,果树苗,0,,供应各种果树苗、绿化苗,,2011-2-23,2011-3-22,,面议,北京市,,王敏,13521120562,13521120562,陆辛庄华源发苗木市场
	        	String[] token = GetToken(line);
	        	if (token.length != 16)
	        		continue;

	    		list.add(GetSupplyDemandMap(token));  
	        }
	        
	        return list;
	    }
		
		SupplyDemandListener listener = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token, String line) {
				
				if (token == null ||
					token.length < 4 ||
					curUserId == null)
					return false;

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
		
		pairLines = "";
		
		SupplyDemandListener listener2 = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token, String line) {
				
				if (token == null ||
					token.length < 4 ||
					curUserId == null)
					return false;
	        
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
	        			pairLines += line + "\n";
	        			return true;
	        		}
	        	}

	    		return false;
			}
		};
		
		List<ListItemMap> list = GetSupplyDemandList(listener2);
		
		if (list.size() > 0)
			FileUtils.WriteGB2312Text(false, pairCache, pairLines);
		
		return list;
	}

	/**
	 * 获取我的商圈朋友列表
	 * majorIid: INVALID_ID 即返回所有商友列表
	 * @return
	 * @param
	 * myFriend：是否查询我的好友
	 */
	public static List<ListItemMap> GetMyGroupFriendList(int majorIid, boolean myFriend) {
		
		// 从网络下载商友列表
		Downloader.DownFile(URLT_GET_FRIENDS + "?userid=" + UserMan.GetUserId() +
				"&after=" + "1970-01-01"
				, DataFile(""), FILE_NAME_FRIEND);
		
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_FRIEND, true);
        
        for (String line : lines)  
        {
        	// user,名字,专业,联系地址,联系电话
        	//0,zhangsan,张三,专业1,北京通州,13812341234,自我介绍
        	String[] token = GetToken(line);
        	if (token.length == 5) {

        		String id = token[0];
        			
    			// XXX 待优化：从cache中取出，然后匹配
    			int major_id = ParseInt(token[1]);
    			String major = "未知专业";
    			
    			if (major_id < MAJOR.length)
    				major = MAJOR[major_id];
    			else
    				Debug.Log("严重错误：未知专业，" + major_id);

    			// 取所有商友或者商友匹配，不然继续
    			if (majorIid != INVALID_ID && majorIid != major_id)
    				continue;

    			// 排除自己
    			if (id.equals(UserMan.GetUserId()))
    				continue;

        		String name = id;//token[2];
        		String telephone = token[2];
        		String address   = token[3];
        		String introduce = token[4];
        		
        		if (myFriend) {
        			// TODO 判断是否我的好友
        		}

        		ListItemMap map = new ListItemMap(name/* 名字 */, KEY_FRIEND_ID, id/* 用户id */);
        		
        		map.put(KEY_FRIEND_MAJOR, major);
        		map.put(KEY_FRIEND_ADDRESS, address);
        		map.put(KEY_FRIEND_TELEPHONE, telephone);
        		map.put(KEY_FRIEND_INTRODUCE, introduce);
        		
        		list.add(map);
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
	public static List<ListItemMap> GetMyGroupMessageList(String fiend_id) {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		
		if (fiend_id == null)
			return list;
		
		// 从网络下载我的留言
		Downloader.DownFile(URL_GET_MESSAGE + "?userid=" + UserMan.GetUserId() +
				"&after=" + "1970-01-01",
				DataFile(""), FILE_NAME_NEW_MESSAGE);
		
		List<String> lines = ReadLines(FILE_NAME_NEW_MESSAGE, true);
		for (String line : lines) {
			String[] token = GetToken(line);
			// dengyong,test,ni hao a ge men, henhao ,0,201307222129
			if (token.length >= 5) {
				String frienId = token[1];
        		String message = token[2];
        		String time = token[token.length - 1];
        		
        		for (int i = 3; i < token.length - 2; ++i) {
        			// 为防止消息中有逗号，出去前两个分割，和后两个分割，其他都为消息内容
        			message += "," + token[i];
        		}

        		String itemText = message + "\t" + time;
        		if (fiend_id.equals(UserMan.GetUserId()))
        			itemText = frienId + " ： " + message;
        		else if (!fiend_id.equals(frienId))
        			continue;
        		
        		ListItemMap map = new ListItemMap(itemText/* 名字 */, KEY_FRIEND_ID, frienId/* id */);
        		
        		map.put(KEY_FRIEND_MESSAGE_CONTENT, message);
        		// 最后一个分割为时间
        		map.put(KEY_FRIEND_MESSAGE_DATE, time);
        		
        		GetUserInfo(frienId, map);
        		
        		list.add(map);
			}
        }
		
		return list;
		
		/*
		// TODO 暂不读取历史留言，接口待协商？get_message只获取今日留言？
        lines = ReadLines(FILE_NAME_MY_GROUP_MESSAGE);
        
        //int  = ParseInt(friendId);
        
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
	
	        		ListItemMap map = new ListItemMap(poster, KEY_MY_GROUP_MESSAGE_ID, token[0]);
	        		
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
        
        */
	}
	
	/**
	 * 从用户列表中查询用户信息
	 */
	private static void GetUserInfo(String userId, ListItemMap info) {
		
		List<String> lines = ReadLines(FILE_NAME_USERS);
		
		for (String line : lines) {
			// liye3,10,13800138000,‘注册默认地址’,注册默认备注信息
			String[] token = GetToken(line);
			if (token.length == 5 &&
				token[0].equals(userId)) {
				String majorId = token[1];
				String phone = token[2];
				String address = token[3];
				String note = token[4];

				info.put(KEY_FRIEND_MAJOR, majorId);
				info.put(KEY_FRIEND_ADDRESS, address);
				info.put(KEY_FRIEND_TELEPHONE, phone);
				info.put(KEY_FRIEND_MESSAGE_CONTENT, note);
				return;
			}
		}
	}

	/**
	 * 发布新留言
	 * @param userId
	 * @param friendId
	 * @param message
	 * @return
	 */
	public static boolean PostNewMessage(String userId, String friendId, String message) {
		String params;
		try {
			// TODO userId ？ friendId ？ 定义？
			params = "user=" + friendId + "&friend=" + userId +
					"&message=" + URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}

		String ret = Downloader.PostUrl(URL_SERVER + URL_POST_MESSAGE, params);

		if (ret.equals("TRUE"))
			return true;
		
		Debug.Log("发布留言错误：返回：" + ret);

		return false;
	}
	
	/**
	 * 功能id转换
	 * @param functionId
	 * @param column
	 * @return
	 */
	private static boolean IsAisColumnMatch(int functionId, int column) {
		switch (column) {
		case 5: // 农业技术
			return (functionId == UIConst.FUNCTION_ID_ARGRI_TECH);
		case 6: // 专家指导
			return (functionId == UIConst.FUNCTION_ID_EXPERT_GUIDE);
		case 8: // 科学施肥
			return (functionId == UIConst.FUNCTION_ID_EXPERT_FERTILIZE);
		case 9: // 时政要闻
			return (functionId == UIConst.FUNCTION_ID_CUR_POLITICS);
		case 10: // 精选课件
			return (functionId == UIConst.FUNCTION_ID_BEST_COUSE);
		case 11: // 先锋党员
			return (functionId == UIConst.FUNCTION_ID_VANGUARD_PARTY);
		case 12: // 典型模范
			return (functionId == UIConst.FUNCTION_ID_CLASS_EXPERIENCE);
		case 13: // 致富经验
			return (functionId == UIConst.FUNCTION_ID_MODEL);
		case 14: // 快乐农家
			return (functionId == UIConst.FUNCTION_ID_HAPPAY);
		case 15: // 法律法规
			return (functionId == UIConst.FUNCTION_ID_LAW);
		case 16: // 惠农政策
			return (functionId == UIConst.FUNCTION_ID_POLICY);
		default:
			return false;
		}
	}

	/**
	 * 获取AIS第一级分类列表
	 * 如果function_id为0则返回一级分类，否则返回function_id对应的子级分类
	 * @return
	 */
	private static String FILE_NAME_GEN_AIS_LIST = "gen_ais_list.txt";
	public static List<ListItemMap> GetAisColumnChildList(int functionId) {

		List<ListItemMap> list = new ArrayList<ListItemMap>();
		Map<String, String> map = new HashMap<String, String>();

		// 向前减去30天
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = mDateFormater.format(new Date(today)) + "/";

			GenAisList(date);
			
			List<String> lines = ReadLinesWithEncoding(date + FILE_NAME_GEN_AIS_LIST, "UTF-8", true);
			
			String[] token;
			for (String line : lines) {
				token = line.split(TOKEN_SEP);
				
				if (token != null && token.length == 5) {
					// 20130607112901281,10,食品,试卷一,2013-6-7
					int column = ParseInt(token[1]);
					if (!IsAisColumnMatch(functionId, column))
						continue;
					
					String child = token[2];

					// 不重复添加
					if (!child.isEmpty() && map.get(child) == null)
						map.put(child, child);
				}
			}
		}

		// 添加子分类
		for (String childName : map.keySet()) {
			ListItemMap item = new ListItemMap(childName, KEY_AIS_COLUMN_CHILD, childName);
			list.add(item);
		}

		return list;
	}
	
	private static void GenAisList() {
		// 日期格式（月.日）
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

		// 向前减去30天
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = simpleDate.format(new Date(today)) + "/";

			GenAisList(date);
		}
	}
	
	/**
	 * 生成ais list
	 */
	private static void GenAisList(String date) {
		String genFileName = DataFile(date + FILE_NAME_GEN_AIS_LIST, true);
		if (FileUtils.IsFileExist(genFileName))
			return;

		List<String> lines = ReadLines(date + FILE_NAME_AIS_LIST, true);

		if (lines.size() == 0)
			return;
		
		String newAisList = "";
		
		String[] token;
		for (String line : lines) {
			token = line.split(TOKEN_SEP);
			
			if (token != null && token.length == 4) {
				// 20130607112901281,10,试卷一,2013-6-7
				int column = ParseInt(token[1]);
				if (column == INVALID_ID)
					continue;
				
				String name = token[2];
				String fileName = name + ".ais";
				
				AisDoc aisDoc = new AisDoc(null, fileName, true, date);
				String child = aisDoc.getAisChildColumn();
				
				// 重新生成list
				newAisList += token[0] + COMMON_TOKEN +
						token[1] + COMMON_TOKEN +
						child + COMMON_TOKEN +
						token[2] + COMMON_TOKEN +
						token[3] + COMMON_TOKEN + "\n";
			}
		}
		
		// 重新生成
		FileUtils.WriteFile(genFileName, newAisList.getBytes());
	}

	// 一天的毫秒数： 1天=24*60*60*1000=86400000毫秒
	private static final long MILLIS_ONE_DAY = 86400000;

	/**
	 * 获取AIS第二级分类列表
	 * @param class_id
	 * INVALID返回所有
	 * @return
	 */
	public static List<ListItemMap> GetAisList(int functionId, String childColumn) {

		List<ListItemMap> list = new ArrayList<ListItemMap>();

		if (childColumn == null)
			return list;
		
		// 向前减去30天
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = mDateFormater.format(new Date(today)) + "/";

			GenAisList(date);
			
			List<String> lines = ReadLinesWithEncoding(date + FILE_NAME_GEN_AIS_LIST, "UTF-8", true);

			String[] token;
			for (String line : lines) {
				token = line.split(TOKEN_SEP);
				
				if (token != null && token.length == 5) {
					// 20130607112901281,10,食品,试卷一,2013-6-7
					int column = ParseInt(token[1]);
					if (!IsAisColumnMatch(functionId, column))
						continue;
					
					String name = token[3];
					String fileName = name + ".ais";
					
					String child = token[2];
					
					// 符合条件(空表示获取所有ais)
					if (childColumn.isEmpty() || child.equals(childColumn)) {
						ListItemMap item = new ListItemMap(name, KEY_AIS_FILE_NAME, fileName);
						item.put(KEY_AIS_DATE, date);
						list.add(item);
					}
				}
			}
		}

		//String fileName = GetAisColumnFileName(functionId);

        return list;
	}

	/**
	 * 返回加上路径的数据文件名
	 * @param fileName
	 * @return
	 */
	public static String DataFile(String fileName, boolean root) {
		String appPath = AppDataPath(root);
		
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
	 * 兼容之前代码
	 * @param fileName
	 * @return
	 */
	public static String DataFile(String fileName) {
		return DataFile(fileName, false);
	}
	
	/**
	 * 返回加上路径的数据文件名
	 * @param fileName
	 * @return
	 */
	private static String AppDataPath(boolean root) {
		if (root)
			return Environment.getExternalStorageDirectory() + "/zknx/broadcast";
		else
			return Environment.getExternalStorageDirectory() + "/zknx/broadcast/" + GetCurrentTime(false);
	}

	/**
	 * 检查今天广播数据是否需要更新（时间戳不匹配并且数据文件存在）
	 * @return
	 */
	public static boolean ShouldUpdateData() {

		String today = GetCurrentTime(false);
		//String dataFileName = DataFile(today + ".zip");

		List<String> line = ReadLines(TIME_STAMP_FILE_NAME, true);

		// 没有时间戳，或者时间戳不匹配
		boolean timStampNotMatch = (line.size() == 0) || (!line.get(0).equals(today));

		// 时间戳不匹配，且当天数据文件存在
		return timStampNotMatch;// && FileUtils.IsFileExist(dataFileName);
	}
	
	/**
	 * 写日期时间戳，用于标记今天是否解压过数据更新
	 * @param today
	 * @return
	 */
	private static boolean WriteTimeStamp(String today) {
		return FileUtils.WriteFile(DataFile(TIME_STAMP_FILE_NAME, true), today.getBytes());
	}

	/**
	 * 检查广播数据
	 */
	public static boolean ProcessBroadcastData() {
		
		// 检查基础数据版本是否有更新
		/*
		if (IsDataUpdated(FILE_NAME_ADDRESS)) {
			// 生成省市列表
			GenProvinceList();
		}
		
		if (IsDataUpdated(FILE_NAME_MARKETS)) {
			// 按省市分割市场列表
			GenProvinceMarketList();
		}
		*/
		////////////////////////////////////////////////////
		
		// AIS列表
		
		GenAisList();

		//  处理供求信息
		GenSupplyDemandList();

		// 处理商品信息
		List<ListItemMap> province = GetAddressList();
		
		if (province == null ||province.size() == 0)
			return false;
		
		// 循环获取所有地区所有市场的所有商品信息（优先处理数据）
		for (ListItemMap item : province) {
			int addressId = item.getInt(KEY_ADDRESS_ID);
			if (addressId != INVALID_ID) {
				// 获取所有市场信息
				List<ListItemMap> markets = GetMarketListByArea(addressId);
				
				if (markets == null ||markets.size() == 0)
					continue;
				
				for (ListItemMap market : markets) {
					
					// 获取所有产品信息
					int marketId = market.getInt(KEY_MARKET_ID);
					List<ListItemMap> products = GenProductList(marketId);
					
					Debug.Log("处理市场：" + marketId);
					
					if (products == null ||products.size() == 0)
						continue;
					
					for (ListItemMap product : products) {
						String productId = product.getString(KEY_PRODUCT_ID);
						// 获取某商品的市场信息
						Debug.Log("处理产品：" + productId);
						GenMarketListByProduct(productId);
						// 获取某商品的价格信息（服务器已经处理）
					}
				}
			}
		}

		// 检查时间戳
		if (ShouldUpdateData()) {
			return UpdateTodayData();
		}

		return false;
	}
	
	/**
	 * 获取当前用户新的留言
	 * return：返回上次留言的时间
	 * TODO 逗号问题，暂时是默认消息内部可以存在逗号，只取后面几个分隔符
	 */
	private static final String FILE_STAMP_LAST_MESSAGE = "lastMessage.txt";
	public static String GetNewMessages() {

		int ret = Downloader.DownFile(URL_GET_MESSAGE + "?userid=" + UserMan.GetUserId(), DataFile("", true), FILE_NAME_NEW_MESSAGE);
		
		// 下载错误
		if (ret != 0)
			return null;

		List<String> lines = ReadLines(FILE_NAME_NEW_MESSAGE, true);
		
		if (lines.size() == 0)
			return null;

		// 获取最后一行的数据
		// dengyong,test,ceshishij,0,2013-7-23 20:45:15;
		String lastLine = lines.get(lines.size() - 1);
		String token[] = lastLine.split(COMMON_TOKEN);
		
		if (token == null ||
			token.length < 5) {
			Debug.Log("消息格式错误");
			return null;
		}

		// 最后一个分隔符是时间
		String time = token[token.length - 1];
		
		String message = null;
		
		List<String> lastTime = ReadLines(FILE_STAMP_LAST_MESSAGE, true);
		if (lastTime == null ||
			lastTime.size() == 0 ||
			!lastTime.get(0).equals(time)) {
			
			message = token[0] + "：" + token[2];
		}

		FileUtils.WriteText(DataFile(FILE_STAMP_LAST_MESSAGE, true), time);

		return message;
	}
	
	/**
	 * 更新当天数据
	 * @return
	 */
	public static boolean UpdateTodayData() {

		// 获取当天日期
		String today = GetCurrentTime(false);
		return WriteTimeStamp(today);
		
		/*
		String dataFileName = DataFile(today + ".zip");

		// 解压数据更新
		if (FileUtils.IsFileExist(dataFileName) &&
			Ziper.UnZip(dataFileName, DataFile(""))) {
			// 写时间戳
			return WriteTimeStamp(today);
		}

		return false;
		*/
	}

	private static final String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIMEID_FORMAT = "yyyyMMddhhmmss";
	private static final SimpleDateFormat mTimeFormater = new SimpleDateFormat(TIME_FORMAT, Locale.CHINA);
	private static final SimpleDateFormat mDateFormater = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
	private static final SimpleDateFormat mTimeIdFormater = new SimpleDateFormat(TIMEID_FORMAT);
	
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
	 * 获取时间id
	 * @return
	 */
	private static String GetCurrentTimeId() {
		return mTimeIdFormater.format(new java.util.Date());
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
		String params;
		try {
			params = "type=" + info.type + 
				"&title=" + URLEncoder.encode(info.title, "UTF-8") + 
				"&userid=" + UserMan.GetUserId() + 
				"&addressid=" + /*TODO 发布供求无地址id UserMan.GetUserAddressId()*/ "" +
				"&commodityid=" + info.commodityid + 
				"&count=" + URLEncoder.encode(info.count, "UTF-8") +
				"&price=" + URLEncoder.encode(info.price, "UTF-8") + 
				"&unit=" + URLEncoder.encode(info.unit, "UTF-8") + 
				"&phonenumber=" + URLEncoder.encode(UserMan.GetUserPhone(), "UTF-8") + 
				"&place=" + URLEncoder.encode(info.place, "UTF-8") + 
				"&linkman=" + URLEncoder.encode(UserMan.GetUserName(), "UTF-8") + 
				"&remark=" + // NO need remark 
				"&validity=" + URLEncoder.encode(info.validity, "UTF-8") + 
				"&publishdate=" + URLEncoder.encode(info.publishdate, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Debug.Log("URL编码错误");
			return false;
		}
		
		String ret = Downloader.PostUrl(URL_SERVER + URL_POST_SUPPLY_DEMAND_INFO, params);

		if (ret.equals("TRUE"))
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

		if (ret.equals("TRUE")) {
			SaveTodayLocalQuestion(expertId, subject, question);
			return true;
		}

		Debug.Log("提问专家错误：返回：" + ret);

		return false;
	}

	/**
	 * 获取专家列表
	 * @return
	 */
	public static List<ListItemMap> GetExpertList() {
		// TODO 专家目录
		List<String> lines = ReadLines("expert/" + FILE_NAME_EXPERTS, true);
		//return ReadCommonIdName(, KEY_EXPERT_ID);
		
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		String[] token;
		for (String line : lines) {
			token = line.split(COMMON_TOKEN);
			
			if (token == null || token.length != 4)
				continue;
			
			String id = token[0];
			String name = token[1];
			String major = token[2];
			String introduce = token[3];
			
			ListItemMap map = new ListItemMap(name, KEY_EXPERT_ID, id);
			
			map.put(KEY_EXPERT_MAJOR, major);
			map.put(KEY_EXPERT_INTRODUCE, introduce);
			
			list.add(map);
		}
		
		return list;
	}
	
	/**
	 * 保存本地成功的提问，方便快速显示在问题列表中
	 * @param expertId
	 * @param subject
	 * @param question
	 */
	private static String LOCAL_QUESTION = "local_questions.txt";
	private static void SaveTodayLocalQuestion(String expertId, String subject,	String question) {
		// 在列表的第一个
		String questionLines = expertId + COMMON_TOKEN + subject + COMMON_TOKEN + question + "\n";
        List<String> lines = ReadLines(LOCAL_QUESTION);
        boolean duplicated = false;
        
        for (String line : lines) {
        	// id,名字
        	String[] token = GetToken(line);
        	if (token.length == 3) {

        		String savedExpertId = token[0];
        		String savedSubject  = token[1];
        		String savedQuestion = token[2];

        		// 只需要省份
        		if (savedExpertId.equals(expertId) && savedSubject.equals(subject)) {
        			duplicated = true;
        			break;
        		} else {
        			questionLines += savedExpertId + COMMON_TOKEN + savedSubject + COMMON_TOKEN + savedQuestion + "\n";
        		}
        	}
        }
        
        // append
        if (!duplicated) {
        	FileUtils.WriteGB2312Text(false, LOCAL_QUESTION, questionLines);
        }
	}
	
	public static List<ListItemMap> GetExpertAnwserList(String expertId) {
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		// TODO
		// 读取本地问题列表
		List<String> lines = ReadLines(/*LOCAL_QUESTION*/"expert/" + expertId + "/anwsers.txt");
		
		for (String line : lines) {
        	// id,名字
        	String[] token = GetToken(line);
        	if (token.length == 3) {

        		String savedExpertId = token[0];
        		String savedSubject  = token[1];
        		String savedQuestion = token[2];
        		
        		if (savedExpertId.equals(expertId)) {
        			ListItemMap map = new ListItemMap(savedSubject, KEY_EXPERT_QUESTION_SUBJECT, savedSubject);
        			map.put(KEY_EXPERT_QUESTION_CONTENT, savedQuestion);
        			list.add(map);
        		}
        	}
		}
		
		// TODO GetExpertAnwserList
		
		return list;
	}
	
	/**
	 * 保存测试成绩
	 * @param title
	 * @param resultPoint
	 */
	public static void SaveGrade(String aisId, int resultPoint) {
		String time = GetCurrentTimeId();
		String line = time + COMMON_TOKEN + aisId + COMMON_TOKEN + resultPoint;

		String fileName = DataMan.DataFile(FILE_NAME_GRADE, true);
		// 附加一行数据
		FileUtils.AppendLine(fileName, line);
	}
	
	/**
	 * 上传成绩
	 * @return
	 */
	public static String PostGrade() {
		String filePathName = DataFile(FILE_NAME_GRADE);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		//user_id：用户id；
		//time：时间；
		//title：课件标题；
		//grade：成绩

		params.add(new BasicNameValuePair("userid", UserMan.GetUserId()));
		params.add(new BasicNameValuePair("filename", FILE_NAME_GRADE));

		String ret = Downloader.PostFile(URL_POST_GRADE, params, filePathName);
		
		if ("TRUE".equals(ret))
			FileUtils.DeleteFile(filePathName);
		
		return ret;
	}

	/**
	 * 获取成绩
	 * @param title
	 * @return
	 */
	public static String GetGrades(String aisId) {
		String grades = "";
		String user = UserMan.GetUserId();
		
		// 本地数据(待上传)
		List<String> lines = ReadLinesWithEncoding(FILE_NAME_GRADE, "UTF8", true);
		
		for (String line : lines) {
			String token[] = line.split(COMMON_TOKEN);
			
			// time,title,grade
			if (token.length == 3 &&
					aisId.equals(token[1])) {
				String time = token[0];
				try {
					time = mTimeFormater.format(mTimeIdFormater.parse(time));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String grade = token[2];
				grades += time + "\t分数：" + grade + "\n";
			}
		}

		// 历史数据
		lines = ReadLines(FILE_NAME_GRADES);
		
		for (String line : lines) {
			String token[] = line.split(COMMON_TOKEN);
			
			// user_id,time,title,grade
			if (token.length == 4 &&
				user.equals(token[0]) &&
				aisId.equals(token[2])) {
				String time = token[1];
				try {
					time = mTimeFormater.format(mTimeIdFormater.parse(time));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String grade = token[3];
				grades += time + "\t分数：" + grade + "\n";
			}
		}
		
		return grades;
	}
}
