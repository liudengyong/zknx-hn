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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
		
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			String filePathName = DataFile(fileName);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePathName)));

			String line;
			while((line = br.readLine()) != null)
				list.add(line);
			
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
	
	        		list.add(new ListItemMap(name/* 名字 */, key, id/* id */));
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
		return ReadCommonIdName(FILE_NAME_ADDRESS_PROVINCE, KEY_ADDRESS_ID);
	}

	/**
	 * 获取市场列表
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByArea(int address_id) {

        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_MARKETS);
        
        for (String line : lines)  
        {
        	// 01,北京,01002,双桥市场
        	String[] token = GetToken(line);
        	if (token.length == 4) {

        		int address_id_parsed =  ParseInt(token[0]);
        		if (!AddressMatch(address_id, address_id_parsed))
        			continue;

        		list.add(new ListItemMap(token[3]/* 市场名字 */, KEY_MARKET_ID, ParseInt(token[2])/* 市场id */));
        	}
        }

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

        List<String> lines = ReadLines(FILE_NAME_PRODUCTS);
        
        for (String line : lines)
        {
        	String[] token = GetToken(line);
        	if (token.length == 9) {
        		// market_id,市场名字,product_id,产品名,最低价,最高价,平均价,产地价,价格单位
        		int market_id_parsed = ParseInt(token[0]);
        		if (market_id_parsed != market_id)
        			continue;

        		// 暂不需要市场名字
        		// String market_name = token[1];

        		int product_id = ParseInt(token[2]);
        		if (product_id == INVALID_ID)
        			continue;
        		
        		String product_name = token[3];
        		String minPrice = token[4];
        		String maxPrice = token[5];
        		String averagePrice = token[6];
        		String hostPrice = token[7];
        		String unit = token[8];
        		boolean isMyProduct = IsMyProduct(product_id); /* 添加自选按钮状态 */
        		
        		//list.add(new ProductListItemMap("名字", "最低价", "最高价", "平均价", "产地价", "单位", "添加"));
        		list.add(new ProductListItemMap(DataMan.KEY_PRODUCT_ID, product_id, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct));
        	}
        }

        return list;
	}

	/**
	 * 判断是否自选产品
	 * @param product_id
	 * @return
	 */
	private static boolean IsMyProduct(int product_id) {

		List<ListItemMap> list = GetMyProductList();
		
		for (ListItemMap map : list) {
			if (map.getInt(KEY_PRODUCT_ID) == product_id)
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
		
		ListItemMap myNewProduct = new ListItemMap(product_name /* 产品名字 */, KEY_PRODUCT_ID, product_id);
		list.add(myNewProduct);

		return SaveMyProducts(list);
	}
	
	/**
	 * 取消自选产品
	 * @return
	 */
	public static boolean MyProductListRemove(int product_id) {

		List<ListItemMap> list = GetMyProductList();
		
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
		        String product_name_to_be_save = (String)item.get(KEY_NAME);
		        String line = product_id_to_be_save + COMMON_TOKEN + product_name_to_be_save + "\r\n";
		
		        out.write(line.getBytes());
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
	public static List<ListItemMap> GetMarketListByProduct(int product_id) {

		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		List<String> lines = ReadLines(FILE_NAME_PRODUCTS);
        
        for (String line : lines)
        {
        	// market_id,市场名字,product_id,产品名,最低价,最高价,平均价,产地价,价格单位
        	// product_id,product_name
        	String[] token = GetToken(line);
        	if (token.length != 9)
        		continue;
        	
        	int product_id_parsed = ParseInt(token[2]);
    		if (product_id_parsed != product_id)
    			continue;
    		
    		int market_id = ParseInt(token[0]);
    		if (market_id == INVALID_ID)
    			continue;
    		
    		String market_name = token[1];
    		String minPrice = token[4];
    		String maxPrice = token[5];
    		String averagePrice = token[6];
    		String hostPrice = token[7];
    		String unit = token[8];
    		boolean addToMyProduct = false; /* 隐藏自选按钮 */
        	
        	list.add(new ProductListItemMap(DataMan.KEY_MARKET_ID, market_id, market_name, minPrice, maxPrice, averagePrice, hostPrice, unit, addToMyProduct));
        }

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
	public static ProductPriceInfo GetHistoryPrice(int product_id, int market_id) {
		
		// 产品id或者市场id为空的话返回空
		if (market_id == INVALID_ID || product_id == INVALID_ID)
			return null;
		
		// TODO 获取价格单位
		// 价格单位（万元，元，角等）
		String priceUnit = "元";
		// 日期单位（年，月，周等）
		String dateUnit = "月.日";
		
		ProductPriceInfo info = new ProductPriceInfo(priceUnit, dateUnit);
		
		// 日期格式（月.日）
		SimpleDateFormat simpleDate = new SimpleDateFormat("MM.dd"); //如果写成年月日的形式的话，要写小d，如："yyyy/MM/dd"
		
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
	private static Float GetPrice(long today, int product_id, int market_id) {
		return 1.0F;
	}

	/**
	 * 获取产品分类
	 * @return
	 */
	public static List<ListItemMap> GetProductClassList() {
		return ReadCommonIdName(FILE_NAME_PRODUCT_CLASS, KEY_PRODUCT_CLASS_ID);
	}
	
	/**
	 * 根据产品分类获取供求信息列表
	 * @return
	 */
	private static List<ListItemMap> GetSupplyDemandList(int product_class_id) {
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		List<String> lines = ReadLines(FILE_NAME_SUPPLY_DEMAND_INFO);
        
        for (String line : lines)
        {
        	// product_id,产品名,供求信息id(第一位编码0代表供应，1代表求购),user,标题,供求信息内容,发布时间,有效期,数量,单价,产地,产品特点,联系人名字,联系电话,手机号,详细地址
        	String[] token = GetToken(line);
        	if (token.length != 16)
        		continue;
        	
        	int product_id = ParseInt(token[0]);
    		if (!ProductClassMatch(product_class_id, product_id))
    			continue;

    		int supply_demand_id = ParseInt(token[2]);
    		if (supply_demand_id == INVALID_ID)
    			continue;
    				
    		list.add(GetSupplyDemandMap(token));  
        }

        return list;
	}

	/**
	 * 获取供求信息列表
	 * @return
	 */
	public static List<ListItemMap> GetSupplyDemandList(int product_class_id, boolean supply) {

		List<ListItemMap> list = GetSupplyDemandList(product_class_id);
		
		// 遍历列表并删除不满足条件的项
		Iterator<ListItemMap> it = list.iterator();

        while (it.hasNext()) {
        	ListItemMap item = it.next();

        	int supply_demand_id = item.getInt(KEY_SUPPLY_DEMAND_INFO_ID);

        	// 如果不匹配则从列表中删除
    		if (IsSupply(supply_demand_id) != supply)
    			it.remove();
        }

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
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_NAME = "name";
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
	public static ListItemMap GetSupplyDemandInfo(int supply_demand_id) {

		List<String> lines = ReadLines(FILE_NAME_SUPPLY_DEMAND_INFO);
        
        for (String line : lines)
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
	
	/**
	 * 保存详细供求信息map
	 */
	private static ListItemMap GetSupplyDemandMap(String[] token) {
		String title = token[3];
		int supply_demand_id = ParseInt(token[2]);
		
		ListItemMap map = new ListItemMap(title, KEY_SUPPLY_DEMAND_INFO_ID, supply_demand_id);
		
		if (supply_demand_id != INVALID_ID) {
			int product_id = ParseInt(token[0]);
			
			map.put(SUPPLY_DEMAND_INFO_PRODUCT_CLASS,       ProductClassDecode(product_id));
			map.put(SUPPLY_DEMAND_INFO_KEY_USER,            token[3]);
			map.put(SUPPLY_DEMAND_INFO_KEY_TITLE,           token[4]);
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

	private static class SupplayDemandPairInfo {
		public int product_class_id;
		public int supply_demand_id;

		SupplayDemandPairInfo(int _product_class_id, int _supply_demand_id) {
			product_class_id = _product_class_id;
			supply_demand_id = _supply_demand_id;
		}
	}
	/**
	 * 获取供求对接信息列表(不分供求)
	 * @return
	 */
	public static List<ListItemMap> GetSupplyDemandPairList(int product_class_id) {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		ArrayList<SupplayDemandPairInfo> listMine = new ArrayList<SupplayDemandPairInfo>(); // 我的供求信息id列表
		List<ListItemMap> listAll = GetSupplyDemandList(product_class_id);
		String curUserId = UserMan.GetCurrentUserId();
		
		Debug.Log("curUserId=" + curUserId);

		// 筛选我的供求信息
        for (ListItemMap item : listAll)
        {
        	// 判断是当用户自己的供求信息
        	if (curUserId.equals(item.get(SUPPLY_DEMAND_INFO_KEY_USER))) {
        		// 保存产品分类id和供求信息id
        		int product_class    = item.getInt(SUPPLY_DEMAND_INFO_PRODUCT_CLASS);
        		int supply_demand_id = item.getInt(KEY_SUPPLY_DEMAND_INFO_ID);
        		
        		// 产品分类
        		if (product_class == product_class_id)
        			listMine.add(new SupplayDemandPairInfo(product_class, supply_demand_id));
        	}
        }
        
        // 筛选我的供求对接信息
        for (ListItemMap item : listAll)
        {
        	// 判断是当用户自己的供求信息
        	if (curUserId.equals(item.get(SUPPLY_DEMAND_INFO_KEY_USER)))
        		continue;
        	
        	// 判断是否对接
        	for (SupplayDemandPairInfo pairInfo : listMine) {
        		int product_class    = item.getInt(SUPPLY_DEMAND_INFO_PRODUCT_CLASS);
        		int supply_demand_id = item.getInt(KEY_SUPPLY_DEMAND_INFO_ID);
        		
        		// 用到亦或操作
        		if (product_class == pairInfo.product_class_id && (IsSupply(supply_demand_id) ^ IsSupply(pairInfo.supply_demand_id))) {
        			list.add(item);
        		}
        	}
        }

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
		
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_MY_FRIEND);
        
        // TODO 测试代码待删除
        if (!myFriend)
        	list.add(new ListItemMap("非好友"/* 名字 */, KEY_FRIEND_ID, 1/* id */));
        
        for (String line : lines)  
        {
        	// user,名字,专业,联系地址,联系电话
        	//0,zhangsan,张三,专业1,北京通州,13812341234
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
	        		
	        		if (myFriend) {
	        			// TODO 判断是否我的好友
	        		}
	
	        		ListItemMap map = new ListItemMap(name/* 名字 */, KEY_FRIEND_ID, id/* id */);
	        		
	        		map.put(KEY_FRIEND_MAJOR, major);
	        		map.put(KEY_FRIEND_ADDRESS, address);
	        		map.put(KEY_FRIEND_TELEPHONE, telephone);
	        		
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
	public static ListItemMap GetMyFriendInfo(int friend_id) {

		List<ListItemMap> friendList = GetMyGroupFriendList(INVALID_ID, true);
		
		if (friend_id != INVALID_ID) {
			for (ListItemMap item : friendList) {
				int id = item.getInt(KEY_FRIEND_ID);
				
				if (id == friend_id)
					return item;
			}
		}
		
		// 返回空
		return null;
	}
	
	/**
	 * 获取我的商圈朋友留言列表
	 * TODO (讨论)朋友留言内容
	 * fiend_id:INVALID_ID 即返回所有留言信息
	 * @return
	 */
	public static List<ListItemMap> GetMyGroupMessageList(int fiend_id) {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_MY_GROUP_MESSAGE);
        
        // TODO 待删除测试代码
        list.add(new ListItemMap("我的自我介绍……"/* 名字 */, KEY_MY_GROUP_MESSAGE_ID, 1/* id */));
        list.add(new ListItemMap("你好，我有一百吨大米出售，请联系我：18911939853"/* 名字 */, KEY_MY_GROUP_MESSAGE_ID, 1/* id */));
        list.add(new ListItemMap("我想买你的苹果，你的电话是多少？"/* 名字 */, KEY_MY_GROUP_MESSAGE_ID, 1/* id */));
        
        for (String line : lines)  
        {
        	// message_id,owner(发起人id),发起人名字,发起人专业,poster(发言人id),发言人名字,发言人专业,日期,联系地址,联系电话,留言内容
        	String[] token = GetToken(line);
        	if (token.length == 9) {

        		int id =  ParseInt(token[0]);

        		if (id != INVALID_ID) {
        			
        			// INVALID_ID 表示获取全部信息
        			if (id != fiend_id && fiend_id != INVALID_ID)
        				continue;
        			
	        		int owner_id = ParseInt(token[1]);
	        		String owner = token[2];
	        		
	        		int poster_id = ParseInt(token[3]);
	        		String poster = token[4];

	        		// 查询我的留言
        			if (MY_MESSAGE == fiend_id && !token[1].equals(UserMan.GetCurrentUserId())) {
        				continue;
        			}

	        		String date = token[5];
	        		String address = token[6];
	        		String telephone = token[7];
	        		String message = token[8];
	
	        		ListItemMap map = new ListItemMap(poster/* 名字 */, KEY_MY_GROUP_MESSAGE_ID, id/* id */);
	        		
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
	public static boolean PostNewMessage(String userId, int friendId, String message) {
		// TODO interface 留言参数需调整? encoding?
		String params = "user=" + userId + ",friend=" + friendId + ",message=" + message;

		String ret = Downloader.PostUrl(URL_POST_MESSAGE, params);

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
	 * 获取AIS第二级分类列表
	 * @param class_id
	 * @return
	 */
	public static List<ListItemMap> GetAisSubClassList(int class_id) {

		List<ListItemMap> list = ReadCommonIdName(FILE_NAME_AIS_CLASS, KEY_AIS_CLASS_ID);

		// 遍历列表并删除不满足条件的项
		Iterator<ListItemMap> it = list.iterator();

        while (it.hasNext()) {
        	ListItemMap item = it.next();

        	if (!AisSubClassMatch(class_id, item.getInt(KEY_AIS_CLASS_ID)))
				it.remove();
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

		// 检查时间戳
		if (ShouldUpdateData()) {
			return UpdateTodayData();
		}
		
		return false;
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
	public static String GetAisFilePathName(int ais_id) {
		return GetAisFolder() + FromatAisFileName(ais_id);
	}

	/**
	 * 获取ais文件存放路径
	 * @return
	 */
	private static String GetAisFolder() {
		return DataFile(AIS_FOLDER);
	}

	/**
	 * 格式化ais文件名
	 * @return
	 */
	private static String FromatAisFileName(int ais_id) {
		return ais_id + AIS_SURFIX;
	}

	/**
	 * 发布供求信息
	 * TODO 完善发布供求信息：完善参数，功能
	 * @param product_id
	 * @return
	 */
	public static boolean PostSupplyDemandInfo(int product_id) {
		return false;
	}
	
	/**
	 * 从用户名分享专业id
	 * @param userId
	 * @return
	 */
	public static int GetMajor(int userId) {
		return DataInterface.GetMajor(userId);
	}
}
