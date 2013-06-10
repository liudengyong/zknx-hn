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

	// ͨ��Ϊ���֣�Ҳ����ListItem��ʾ�ı�
	public static final String KEY_NAME = "name";

	public static final String KEY_ADDRESS_ID = "address_id";
	public static final String KEY_MARKET_ID  = "market_id";
	public static final String KEY_PRODUCT_ID = "product_id";
	public static final String KEY_PRODUCT_CLASS_ID = "product_class_id"; //  ��Ʒ����
	public static final String KEY_SUPPLY_DEMAND_INFO_ID = "supply_demand_info_id"; // ������Ϣid
	public static final String KEY_FRIEND_ID = "friend_id";
	public static final String KEY_AIS_ID = "ais_id";
	public static final String KEY_AIS_CLASS_ID = "ais_class_id";
	// messageId ͬʱҲ�Ƿ������Ե�����id
	public static final String KEY_MY_GROUP_MESSAGE_ID = "my_group_message";

	// �ҵ����� רҵ
	public static final String KEY_FRIEND_MAJOR = "friend_major";
	// �ҵ����� ��ϵ��ַ
	public static final String KEY_FRIEND_ADDRESS = "friend_address";
	// �ҵ����� ��ϵ�绰
	public static final String KEY_FRIEND_TELEPHONE = "friend_telephone";

	// ������id
	public static final String KEY_FRIEND_MESSAGE_POSER_ID = "message_poster_id";
	// ����������
	public static final String KEY_FRIEND_MESSAGE_POSER = "message_poster";
	// ��������
	public static final String KEY_FRIEND_MESSAGE_DATE = "message_date";
	// ��������
	public static final String KEY_FRIEND_MESSAGE_CONTENT = "message_content";

	// ��ʱ�ļ���
	public static final String FILE_NAME_TMP = "tmp.txt";
	
	// ���ݸ���ʱ����ļ�
	private static final String TIME_STAMP_FILE_NAME = ".timestamp";

	// Ĭ�ϷǷ�idֵ
	public static final int INVALID_ID = -1;
	// ���ڲ�ѯ�ҵ����Եı�־
	public static final int MY_MESSAGE = 0;

	// ͨ�÷ָ���
	public static final String COMMON_TOKEN = ",";

	/**
	 * ���ж�ȡ�ı��ļ�
	 * @param fileName
	 * @return
	 */
	public static List<String> ReadLines(String fileName) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			String filePathName = DataFile(fileName);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePathName), "UTF-8"));
			
			String line;
			boolean fistLine = true;
			while ((line = br.readLine()) != null) {
				// ��һ�������UTF8�ļ�ͷ��ȥ��
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
			Debug.Log("�ļ�û�ҵ� �� " + fileName);
		} catch (IOException e) {
			Debug.Log("�ļ������� �� " + fileName);
		} catch (NullPointerException e) {
			Debug.Log("��ָ����� �� " + fileName);
		}
		
		// ��������ֵ��������ʵ���������߲����ж��Ƿ��
		return list;
	}

	/**
	 * ȥ��UTF8�ļ���־ͷ��������ڵĻ�
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
				return new String(line.substring(2));
			}
		}

		return new String(line);
	}

	/**
	 * �Զ��ŷָ�һ������
	 */
	private static String[] GetToken(String line)
	{
		if (line != null && line.length() > 0)
			return line.split(TOKEN_SEP);
		else
			return new String[0]; // ������ֻ��Ҫ�ж�token���ȣ������ж��Ƿ�Ϊ��
	}

	/**
	 * �ַ���תint
	 * @param value
	 * @return
	 */
	public static int ParseInt(String value) {
		try {
			return Integer.parseInt(value, 10);
		}
		catch (NumberFormatException exp) {
			Debug.Log("����ParseInt��" + exp.getMessage());
		}
		
		return INVALID_ID; // ���ش���
	}

	/**
	 * �ַ���תboolean
	 * @param value
	 * @return
	 
	private static boolean ParseBool(String value) {
		return Boolean.parseBoolean(value);
	}*/
	
	/**
	 * ��ȡͨ�õ�id�������б�
	 * ��ʽ��id,����
	 * @return
	 */
	private static List<ListItemMap> ReadCommonIdName(String fileName, String key) {

        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(fileName);
        
        for (String line : lines)  
        {
        	// id,����
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		int id =  ParseInt(token[0]);

        		if (id != INVALID_ID) {

	        		String name = token[1];
	
	        		list.add(new ListItemMap(name/* ���� */, key, token[0]/* id */));
        		}
        	}
        }

        return list;
	}

	/**
	 * ��ȡ��ַ�б�
	 * @return
	 */
	public static List<ListItemMap> GetAddressList() {
		
	    ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(FILE_NAME_ADDRESS);
        
        for (String line : lines) {
        	// id,����
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		int id =  ParseInt(token[0]);

        		// ֻ��Ҫʡ��
        		if (id != INVALID_ID && id < 99) {

	        		String name = token[1];
	
	        		list.add(new ListItemMap(name/* ���� */, KEY_ADDRESS_ID, token[0]/* id */));
        		}
        	}
        }

        return list;
	}

	/**
	 * ��ȡ�г��б�
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByArea(int address_id) {

        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_MARKETS);
        
        for (String line : lines)  
        {
        	// 01,����,01002,˫���г�
        	String[] token = GetToken(line);
        	if (token.length == 4) {

        		int address_id_parsed =  ParseInt(token[0]);
        		if (!AddressMatch(address_id, address_id_parsed))
        			continue;

        		list.add(new ListItemMap(token[3]/* �г����� */, KEY_MARKET_ID, token[2]/* �г�id */));
        	}
        }

        return list;
	}

	/**
	 * ��ȡ��Ʒ�б�
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
        	// 0101301,������ƽ��ˮ��ũ����Ʒ�����г�,0101001000,0101301,13.5,Ԫ/����
        	// TODO �ӿ�ȷ��
        	if (token.length == 6) {
        		// market_id,�г�����,product_id,��Ʒ��,��ͼ�,��߼�,ƽ����,���ؼ�,�۸�λ
        		int market_id_parsed = ParseInt(token[0]);
        		if (market_id_parsed != market_id)
        			continue;

        		// �ݲ���Ҫ�г�����
        		// String market_name = token[1];

        		int product_id = ParseInt(token[2]);
        		if (product_id == INVALID_ID)
        			continue;
        		
        		String product_name = token[3];
        		String minPrice = token[4];
        		String maxPrice = token[5];
        		String averagePrice = "junjia";//token[6];
        		String hostPrice = "chandi";// token[7];
        		String unit = "unit";//token[8];
        		boolean isMyProduct = IsMyProduct(product_id); /* �����ѡ��ť״̬ */
        		
        		//list.add(new ProductListItemMap("����", "��ͼ�", "��߼�", "ƽ����", "���ؼ�", "��λ", "���"));
        		list.add(new ProductListItemMap(DataMan.KEY_PRODUCT_ID, token[2], product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct));
        	}
        }

        return list;
	}

	/**
	 * �ж��Ƿ���ѡ��Ʒ
	 * @param product_id
	 * @return
	 */
	private static boolean IsMyProduct(int product_id) {

		List<ListItemMap> list = GetMyProductList();
		
		for (ListItemMap map : list) {
			if (map.getInt(KEY_PRODUCT_ID) == product_id)
				return true; // ����ѡ��Ʒ 
		}

		return false; // ������ѡ��Ʒ
	}

	/**
	 * ��ȡ��ѡ��Ʒ�б�
	 * @return
	 */
	public static List<ListItemMap> GetMyProductList() {
		return ReadCommonIdName(FILE_NAME_MY_PRODUCTS, KEY_PRODUCT_ID);
	}
	
	/**
	 * �����ѡ��Ʒ
	 * @return
	 */
	public static boolean MyProductListAdd(int product_id, String product_name) {

		if (product_id == INVALID_ID)
			return false;

		List<ListItemMap> list = GetMyProductList();

		// �Ƿ񳬹������������
		if (list.size() >= Restraint.MAX_COUNT_MY_PRODUCT)
			return false;
		
		for (ListItemMap map : list) {
			if (map.getInt(KEY_PRODUCT_ID) == product_id)
				return true; // �Ѿ����ڣ������ظ����
		}
		
		ListItemMap myNewProduct = new ListItemMap(product_name /* ��Ʒ���� */, KEY_PRODUCT_ID, product_id + "");
		list.add(myNewProduct);

		return SaveMyProducts(list);
	}
	
	/**
	 * ȡ����ѡ��Ʒ
	 * @return
	 */
	public static boolean MyProductListRemove(int product_id) {

		List<ListItemMap> list = GetMyProductList();
		
		for (ListItemMap item : list) {
			int item_product_id = item.getInt(KEY_PRODUCT_ID);
			if (item_product_id == product_id) {
				boolean removed = list.remove(item);
				if (!removed) {
					Debug.Log("����AddToMyProductList");
					return false;
				}
				break;
			}
		}

		return SaveMyProducts(list);
	}

	/**
	 * �����ҵ���ѡ��Ʒ
	 * @param list
	 * @return
	 */
	private static boolean SaveMyProducts(List<ListItemMap> list) {

		try {
			// �����ѡ��Ʒ�б��ļ����ڣ��򸲸�
			FileOutputStream out = new FileOutputStream(new File(DataFile(FILE_NAME_MY_PRODUCTS)));   
		
		    for (ListItemMap item : list) {   
		
		    	int product_id_to_be_save = item.getInt(KEY_PRODUCT_ID);
		        String product_name_to_be_save = (String)item.get(KEY_NAME);
		        String line = product_id_to_be_save + COMMON_TOKEN + product_name_to_be_save + "\r\n";
		
		        out.write(line.getBytes());
		    }
		
		    out.close();
		
			// ��ӳɹ�
			return true;
		}
		catch (IOException exp) {
			Debug.Log("����SaveMyProducts��" + exp.getMessage());
		}
		
		return false;
	}

	/**
	 * ��ȡ�����иò�Ʒ���г��б�
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByProduct(int product_id) {

		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		List<String> lines = ReadLines(FILE_NAME_PRODUCTS);
        
        for (String line : lines)
        {
        	// market_id,�г�����,product_id,��Ʒ��,��ͼ�,��߼�,ƽ����,���ؼ�,�۸�λ
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
    		boolean addToMyProduct = false; /* ������ѡ��ť */
        	
        	list.add(new ProductListItemMap(DataMan.KEY_MARKET_ID, token[0], market_name, minPrice, maxPrice, averagePrice, hostPrice, unit, addToMyProduct));
        }

        return list;
	}
	
	// һ��ĺ������� 1��=24*60*60*1000=86400000����
	private static final long MILLIS_ONE_DAY = 86400000;
	
	/**
	 * ��ȡ���30��۸���Ϣ HISTORY_PRICE_DAYS
	 * TODO interface ��ȡ��ʷ�۸����ڣ�
	 * @param product_id
	 * @param market_id
	 * @return
	 * ��Ʒid�����г�idΪ�յĻ����ؿ�
	 */
	public static ProductPriceInfo GetHistoryPrice(int product_id, int market_id) {
		
		// ��Ʒid�����г�idΪ�յĻ����ؿ�
		if (market_id == INVALID_ID || product_id == INVALID_ID)
			return null;
		
		// TODO ��ȡ�۸�λ
		// �۸�λ����Ԫ��Ԫ���ǵȣ�
		String priceUnit = "Ԫ";
		// ���ڵ�λ���꣬�£��ܵȣ�
		String dateUnit = "��.��";
		
		ProductPriceInfo info = new ProductPriceInfo(priceUnit, dateUnit);

		// ���ڸ�ʽ����.�գ�
		SimpleDateFormat simpleDate = new SimpleDateFormat("M.d", Locale.CHINA); //���д�������յ���ʽ�Ļ���ҪдСd���磺"yyyy/MM/dd"

		// ��ǰ��ȥ30��
		long today = System.currentTimeMillis();
		for (int i = 0; i < HISTORY_PRICE_DAYS; ++i) {

			// ��ȡ����۸�
			Float price = GetPrice(today, product_id, market_id);
			// ��ӵ���ļ۸�
			if (price != 0F)
				info.add(simpleDate.format(new Date(today)), price);

			today -= MILLIS_ONE_DAY;
		}
		
		return info;
	}
	
	/**
	 * TODO ��ȡĳ��ļ۸�
	 * @param today
	 * @return
	 */
	private static Float GetPrice(long today, int product_id, int market_id) {
		long time = System.currentTimeMillis();
		return 4.0F + (time % 4);
	}

	/**
	 * ��ȡ��Ʒ����
	 * @return
	 */
	public static List<ListItemMap> GetProductClassList() {
		//return ReadCommonIdName(FILE_NAME_PRODUCT_CLASS, KEY_PRODUCT_CLASS_ID);
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(FILE_NAME_COMMODITY);
        
        for (String line : lines)  
        {
        	// id,����
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		int id =  ParseInt(token[0]);

        		// id ���룺��λ���Ƿ���
        		if (id != INVALID_ID && id < 100) {

	        		String name = token[1];

	        		list.add(new ListItemMap(name/* ���� */, KEY_PRODUCT_CLASS_ID, token[0]/* id */));
        		}
        	}
        }

        return list;
	}
	
	/**
	 * ���ݲ�Ʒ�����ȡ������Ϣ�б�
	 * @return
	 */
	interface SupplyDemandListener {
		boolean meetCondition(String[] token);
	}
	private static List<ListItemMap> GetSupplyDemandList(SupplyDemandListener listener) {
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		List<String> lines = ReadLines(FILE_NAME_SUPPLY_DEMAND_INFO);

        for (String line : lines)
        {
        	// product_id,��Ʒ��,������Ϣid(��һλ����0����Ӧ��1������),user,����,������Ϣ����,����ʱ��,��Ч��,����,����,����,��Ʒ�ص�,��ϵ������,��ϵ�绰,�ֻ���,��ϸ��ַ
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
    		
    		if (listener.meetCondition(token))
    			list.add(GetSupplyDemandMap(token));  
        }

        return list;
	}

	/**
	 * ��ȡ������Ϣ�б�
	 * @return
	 */
	public static List<ListItemMap> GetSupplyDemandList(final int product_class_id, final boolean supply) {

		SupplyDemandListener listener = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token) {
				
				int product_id = ParseInt(token[0]);
	    		if (!ProductClassMatch(product_class_id, product_id))
	    			return false;

	    		int supply_demand_id = ParseInt(token[2]);
	    		if (supply_demand_id == INVALID_ID ||
	    			IsSupply(supply_demand_id) != supply)
	    			return false;

	    		return true;
			}
		};

		return GetSupplyDemandList(listener);
	}

	// ��Ʒ����id
	public final static String SUPPLY_DEMAND_INFO_PRODUCT_CLASS = "product_class";
	// ����������Ϣ��user_id
	public final static String SUPPLY_DEMAND_INFO_KEY_USER = "user";
	// ����
	public final static String SUPPLY_DEMAND_INFO_KEY_TITLE = "title";
	// ��Ϣ
	public final static String SUPPLY_DEMAND_INFO_KEY_MESSAGE = "message";
	// ��������
	public final static String SUPPLY_DEMAND_INFO_KEY_POST_TIME = "post_time";
	// ��Ч��
	public final static String SUPPLY_DEMAND_INFO_KEY_INVALIDATE_DATE = "invalid_date";
	// ����
	public final static String SUPPLY_DEMAND_INFO_KEY_AMOUNT = "amount";
	// ����
	public final static String SUPPLY_DEMAND_INFO_KEY_PRICE = "price";
	// ����
	public final static String SUPPLY_DEMAND_INFO_KEY_HOST = "host";
	// ��Ʒ�ص�
	public final static String SUPPLY_DEMAND_INFO_KEY_FEATURE = "feature";
	// ��ϵ������
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_NAME = "name";
	// ��ϵ�˵绰
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_TEL = "tel";
	// ��ϵ���ֻ�
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_PHONE = "phone";
	// ��ϵ����ϸ��ַ
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_ADDRESS = "address";
	
	/**
	 * ��ȡ������Ϣ
	 * @param supply_demand_id
	 * @return
	 */
	public static ListItemMap GetSupplyDemandInfo(int supply_demand_id) {

		List<String> lines = ReadLines(FILE_NAME_SUPPLY_DEMAND_INFO);
        
        for (String line : lines)
        {
        	// product_id,��Ʒ��,������Ϣid(��һλ����0����Ӧ��1������),����,������Ϣ����,����ʱ��,��Ч��,����,����,����,��Ʒ�ص�,��ϵ������,��ϵ�绰,�ֻ���,��ϸ��ַ
        	String[] token = GetToken(line);
        	if (token.length != 16)
        		continue;
        	
        	int supply_demand_id_parsed = ParseInt(token[2]);

        	// ƥ��ɹ������ع�����Ϣ
    		if (supply_demand_id_parsed == supply_demand_id) {
    			return GetSupplyDemandMap(token);
    		}
        }

		return null;
	}
	
	/**
	 * ������ϸ������Ϣmap
	 */
	private static ListItemMap GetSupplyDemandMap(String[] token) {
		String title = token[3];
		int supply_demand_id = ParseInt(token[2]);
		
		ListItemMap map = new ListItemMap(title, KEY_SUPPLY_DEMAND_INFO_ID, supply_demand_id + "");
		
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
	 * ��ȡ����Խ���Ϣ�б�(���ֹ���)
	 * @return
	 */
	public static List<ListItemMap> GetSupplyDemandPairList(final int product_class_id) {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		ArrayList<SupplayDemandPairInfo> listMine = new ArrayList<SupplayDemandPairInfo>(); // �ҵĹ�����Ϣid�б�
		
		final String curUserId = UserMan.GetCurrentUserId();
		
		Debug.Log("curUserId=" + curUserId);
		
		SupplyDemandListener listener = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token) {

				// ɸѡ�ҵĹ�����Ϣ
	        	// �ж��ǵ��û��Լ��Ĺ�����Ϣ
	        	if (curUserId.equals(token[3])) {
	        		// �����Ʒ����id�͹�����Ϣid
	        		int product_id = ParseInt(token[0]);
	        		int product_class    = ProductClassDecode(product_id);
	        		
	        		// ��Ʒ����
	        		if (product_class == product_class_id)
	        			return true;
	        	}

	    		return false;
			}
		};
		
		final List<ListItemMap> myInfoList = GetSupplyDemandList(listener);
		
		SupplyDemandListener listener2 = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token) {
	        
	        	// �ж��ǵ��û��Լ��Ĺ�����Ϣ
	        	if (curUserId.equals(token[3]))
	        		return false;
	        	
	        	// �ж��Ƿ�Խ�
	        	for (ListItemMap pairInfo : myInfoList) {
	        		int product_class    = ParseInt(token[0]);
	        		int supply_demand_id = ParseInt(token[2]);
	        		
	        		// �õ�������
	        		if (product_class == ListItemMap.GetMapInt(pairInfo, KEY_PRODUCT_CLASS_ID) && 
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
	 * ��ȡ�ҵ���Ȧ�����б�
	 * majorIid: INVALID_ID ���������������б�
	 * @return
	 * @param
	 * myFriend���Ƿ��ѯ�ҵĺ���
	 */
	public static List<ListItemMap> GetMyGroupFriendList(int majorIid, boolean myFriend) {
		
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_MY_FRIEND);
        
        // TODO ���Դ����ɾ��
        if (!myFriend)
        	list.add(new ListItemMap("�Ǻ���"/* ���� */, KEY_FRIEND_ID, "friend_id_todo"/* id */));
        
        for (String line : lines)  
        {
        	// user,����,רҵ,��ϵ��ַ,��ϵ�绰
        	//0,zhangsan,����,רҵ1,����ͨ��,13812341234
        	String[] token = GetToken(line);
        	if (token.length == 6) {

        		int id = ParseInt(token[0]);

        		if (id != INVALID_ID) {
        			
        			// XXX ���Ż�����cache��ȡ����Ȼ��ƥ��
        			int major_id = ParseInt(token[3]);
        			String major = "δ֪רҵ";
        			
        			if (major_id < MAJOR.length)
        				major = MAJOR[major_id];
        			else
        				Debug.Log("���ش���δ֪רҵ��" + major_id);

        			// ȡ�������ѻ�������ƥ�䣬��Ȼ����
        			if (majorIid != INVALID_ID && majorIid != major_id)
        				continue;

	        		String name = token[2];
	        		String address = token[4];
	        		String telephone = token[5];
	        		
	        		if (myFriend) {
	        			// TODO �ж��Ƿ��ҵĺ���
	        		}
	
	        		ListItemMap map = new ListItemMap(name/* ���� */, KEY_FRIEND_ID, token[0]/* id */);
	        		
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
	 * ��ѯ�ҵ�������Ϣ
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
		
		// ���ؿ�
		return null;
	}
	
	/**
	 * ��ȡ�ҵ���Ȧ���������б�
	 * TODO (����)������������
	 * fiend_id:INVALID_ID ����������������Ϣ
	 * @return
	 */
	public static List<ListItemMap> GetMyGroupMessageList(String friendId) {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_MY_GROUP_MESSAGE);
        
        int fiend_id = ParseInt(friendId);
        
        // TODO ��ɾ�����Դ���
        list.add(new ListItemMap("�ҵ����ҽ��ܡ���"/* ���� */, KEY_MY_GROUP_MESSAGE_ID, "friend"/* id */));
        list.add(new ListItemMap("��ã�����һ�ٶִ��׳��ۣ�����ϵ�ң�18911939853"/* ���� */, KEY_MY_GROUP_MESSAGE_ID, "id"/* id */));
        list.add(new ListItemMap("���������ƻ������ĵ绰�Ƕ��٣�"/* ���� */, KEY_MY_GROUP_MESSAGE_ID, "id"/* id */));
        
        for (String line : lines)  
        {
        	// message_id,owner(������id),����������,������רҵ,poster(������id),����������,������רҵ,����,��ϵ��ַ,��ϵ�绰,��������
        	String[] token = GetToken(line);
        	if (token.length == 9) {

        		int id =  ParseInt(token[0]);

        		if (id != INVALID_ID) {
        			
        			// INVALID_ID ��ʾ��ȡȫ����Ϣ
        			if (id != fiend_id && fiend_id != INVALID_ID)
        				continue;
        			
	        		int owner_id = ParseInt(token[1]);
	        		String owner = token[2];
	        		
	        		int poster_id = ParseInt(token[3]);
	        		String poster = token[4];

	        		// ��ѯ�ҵ�����
        			if (MY_MESSAGE == fiend_id && !token[1].equals(UserMan.GetCurrentUserId())) {
        				continue;
        			}

	        		String date = token[5];
	        		String address = token[6];
	        		String telephone = token[7];
	        		String message = token[8];
	
	        		ListItemMap map = new ListItemMap(poster/* ���� */, KEY_MY_GROUP_MESSAGE_ID, "id"/* id */);
	        		
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
	 * ����������
	 * @param userId
	 * @param friendId
	 * @param message
	 * @return
	 */
	public static boolean PostNewMessage(String userId, String friendId, String message) {
		// TODO interface ���Բ��������? encoding?
		String params = "user=" + userId + ",friend=" + friendId + ",message=" + message;

		String ret = Downloader.PostUrl(URL_POST_MESSAGE, params);

		if (ret.equals("true"))
			return true;
		
		Debug.Log("�������Դ��󣺷��أ�" + ret);

		return false;
	}
	
	/**
	 * ��ȡAIS��һ�������б�
	 * ���function_idΪ0�򷵻�һ�����࣬���򷵻�function_id��Ӧ���Ӽ�����
	 * @return
	 */
	public static List<ListItemMap> GetAisClassList(int function_id) {

		if (!FileUtils.IsFileExist(DataMan.DataFile(FILE_NAME_AIS_CLASS))) {
			GenerateAisClassCache();
		}

		List<ListItemMap> list = ReadCommonIdName(FILE_NAME_AIS_CLASS, KEY_AIS_CLASS_ID);

		// �����б�ɾ����������������
		Iterator<ListItemMap> it = list.iterator();

        while (it.hasNext()) {
        	ListItemMap item = it.next();

        	if (!AisClassMatch(function_id, item.getInt(KEY_AIS_CLASS_ID)))
				it.remove();
        }

        return list;
	}
	
	/**
	 * ����Ais�����ļ����Ż�Ч�ʣ�
	 */
	private static void GenerateAisClassCache() {
		List<String> lines = ReadLines(FILE_NAME_AIS_LIST);
		
		Map<String, String> map = new HashMap<String, String>();
		
		String id, name, content = "";
		for (String line : lines) {
			String[] token = line.split(TOKEN_SEP);
			
			id = token[1];
			name = FindCommodityName(id);
			
			// ���û�����־���idΪ����
			if (name == null)
				name = id;

			if (map.get(id) == null) {
				map.put(id, "name:"+token[1]); // TODO ��Ʒ��������
				
				content += id + TOKEN_SEP + name + "\n";
			}
		}
		
		FileUtils.WriteText(DataFile(""), FILE_NAME_AIS_CLASS, content);
	}
	
	/**
	 * ������Ʒ����
	 * @param id
	 * @return
	 */
	private static String FindCommodityName(String id) {
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

		return null;
	}
	
	/**
	 * ��ȡAIS�ڶ��������б�
	 * @param class_id
	 * INVALID��������
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
					// ��������
					String id = token[0];
					String name = token[2];
					list.add(new ListItemMap(name, KEY_AIS_ID, id));
				}
			}
		}

        return list;
	}

	/**
	 * ���ؼ���·���������ļ���
	 * @param fileName
	 * @return
	 */
	public static String DataFile(String fileName) {
		String appPath = AppDataPath();
		File file = new File(appPath);
		
		// �������Ŀ¼��ɾ��
		if (file.exists() && !file.isDirectory())
			file.delete();
		
		// ֻ������һ��Ŀ¼������mkdirs��sd���ļ���Ĭ�ϴ��ڣ�
		if (!file.exists() && !file.mkdir()) {
			Debug.Log("���ش���DataFile�����������ļ���ʧ��");
		}
			
		return appPath + "/" + fileName;
	}
	
	/**
	 * ���ؼ���·���������ļ���
	 * @param fileName
	 * @return
	 */
	private static String AppDataPath() {
		return Environment.getExternalStorageDirectory() + "/zknx.hn";
	}
	
	/**
	 * ������㲥�����Ƿ���Ҫ���£�ʱ�����ƥ�䲢�������ļ����ڣ�
	 * @return
	 */
	public static boolean ShouldUpdateData() {

		String today = GetCurrentTime(false);
		String dataFileName = DataFile(today + ".zip");

		List<String> line = ReadLines(TIME_STAMP_FILE_NAME);

		// û��ʱ���������ʱ�����ƥ��
		boolean timStampNotMatch = (line.size() == 0) || (!line.get(0).equals(today));

		// ʱ�����ƥ�䣬�ҵ��������ļ�����
		return timStampNotMatch && FileUtils.IsFileExist(dataFileName);
	}
	
	/**
	 * д����ʱ��������ڱ�ǽ����Ƿ��ѹ�����ݸ���
	 * @param today
	 * @return
	 */
	private static boolean WriteTimeStamp(String today) {
		return FileUtils.WriteFile(DataFile(TIME_STAMP_FILE_NAME), today.getBytes());
	}

	/**
	 * ���㲥����
	 * TODO (����) ���㲥���ݸ��£������½��棿
	 */
	public static boolean CheckBroadcastData() {
		// ģ���ʱ����
		/*
		int x = 3000;
		while(x-- > 0)
			Debug.Log("" + x);
		*/

		// ���ʱ���
		if (ShouldUpdateData()) {
			return UpdateTodayData();
		}
		
		return false;
	}
	
	/**
	 * ���µ�������
	 * @return
	 */
	public static boolean UpdateTodayData() {

		// ��ȡ��������
		String today = GetCurrentTime(false);
		String dataFileName = DataFile(today + ".zip");

		// ��ѹ���ݸ���
		if (FileUtils.IsFileExist(dataFileName) &&
			Ziper.UnZip(dataFileName, DataFile(""))) {
			// дʱ���
			return WriteTimeStamp(today);
		}

		return false;
	}

	private static final String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final SimpleDateFormat mTimeFormater = new SimpleDateFormat(TIME_FORMAT, Locale.CHINA);
	private static final SimpleDateFormat mDateFormater = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
	
	/**
	 * ��ȡ���ڸ�ʽ��
	 * @return
	 * @param withTime
	 * �Ƿ����ʱ��
	 */
	private static SimpleDateFormat GetDateFormator(boolean withTime) {
		return withTime ? mTimeFormater : mDateFormater;
	}

	/**
	 * ��ȡ��ǰ����
	 * @return
	 * @param withTime
	 * �Ƿ����ʱ��
	 */
	public static String GetCurrentTime(boolean withTime) {
		return GetDateFormator(withTime).format(new java.util.Date());
	}

	/**
	 * ��������Ϊlong
	 * @return
	 * �������󷵻�0
	 * @param withTime
	 * �Ƿ����ʱ��
	 */
	public static long ParseDate(String time, boolean withTime) {
		try {
			return GetDateFormator(withTime).parse(time).getTime();
		} catch (ParseException e) {
			Debug.Log("����ParseDate��" + e.getMessage());
		}

		return 0;
	}

	/**
	 * ��ȡais�ļ�·����
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

		return "��";
	}

	/**
	 * ����������Ϣ
	 * TODO ���Ʒ���������Ϣ�����Ʋ���������
	 * @param product_id
	 * @return
	 */
	public static boolean PostSupplyDemandInfo(int product_id) {
		return false;
	}
	
	/**
	 * ���û�������רҵid
	 * @param userId
	 * @return
	 */
	public static int GetMajor(String userId) {
		return DataInterface.GetMajor(userId);
	}
	
	/**
	 * TODO interface ��ר������
	 * @return
	 */
	public static boolean AskExpert(String userId, String expertId, String subject, String question) {
		// TODO interface ���ʲ��������? encoding?
		String params = "user=" + userId + ",expert=" + expertId + ",subject=" + subject + ",question=" + question;

		String ret = Downloader.PostUrl(URL_ASK_EXPERT, params);

		if (ret.equals("true"))
			return true;
		
		Debug.Log("����ר�Ҵ��󣺷��أ�" + ret);

		return false;
	}
}
