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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.os.Environment;

import com.zknx.hn.common.Debug;
import com.zknx.hn.common.Restraint;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.functions.ais.AisDoc;
import com.zknx.hn.functions.common.ProductPriceInfo;

public class DataMan extends DataInterface {

	// ͨ��Ϊ���֣�Ҳ����ListItem��ʾ�ı�
	public static final String KEY_NAME = "name";

	public static final String KEY_ADDRESS_ID = "address_id";
	public static final String KEY_MARKET_ID  = "market_id";
	public static final String KEY_PRODUCT_ID = "product_id";
	public static final String KEY_PRODUCT_CLASS_ID = "product_class_id"; //  ��Ʒ����
	public static final String KEY_SUPPLY_DEMAND_INFO_ID = "supply_demand_info_id"; // ������Ϣid
	public static final String KEY_FRIEND_ID = "friend_id";
	//public static final String KEY_AIS_ID = "ais_id"; // ais_id ��ʱ����
	public static final String KEY_AIS_FILE_NAME = "ais_name";
	// ais����
	public static final String KEY_AIS_COLUMN = "ais_column";
	// ais�ӷ���
	public static final String KEY_AIS_COLUMN_CHILD = "ais_column_child";

	// messageId ͬʱҲ�Ƿ������Ե�����id
	public static final String KEY_MY_GROUP_MESSAGE_ID = "my_group_message";

	// �ҵ����� רҵ
	public static final String KEY_FRIEND_MAJOR = "friend_major";
	// �ҵ����� ��ϵ��ַ
	public static final String KEY_FRIEND_ADDRESS = "friend_address";
	// �ҵ����� ��ϵ�绰
	public static final String KEY_FRIEND_TELEPHONE = "friend_telephone";
	// �ҵ����� ���ҽ���
	public static final String KEY_FRIEND_INTRODUCE = "friend_introduce";

	// ������id
	public static final String KEY_FRIEND_MESSAGE_POSER_ID = "message_poster_id";
	// ����������
	public static final String KEY_FRIEND_MESSAGE_POSER = "message_poster";
	// ��������
	public static final String KEY_FRIEND_MESSAGE_DATE = "message_date";
	// ��������
	public static final String KEY_FRIEND_MESSAGE_CONTENT = "message_content";

	// ר���ʴ�
	public static final String KEY_EXPERT_ID = "expert_id";
	public static final String KEY_EXPERT_MAJOR = "expert_major";
	public static final String KEY_EXPERT_INTRODUCE = "expert_introduce";
	public static final String KEY_EXPERT_QUESTION_SUBJECT = "expert_question_subject";
	public static final String KEY_EXPERT_QUESTION_CONTENT = "expert_question_content";

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
	public static List<String> ReadLines(String fileName, boolean root) {
		// Ĭ�ϱ���
		return ReadLinesWithEncoding(fileName, "GB2312", root);
	}
	
	/**
	 * �������
	 * @param fileName
	 * @return
	 */
	public static List<String> ReadLines(String fileName) {
		// Ĭ�ϱ��� "UTF-8" "GB2312"
		return ReadLinesWithEncoding(fileName, "GB2312", false);
	}
	
	/**
	 * ���ж�ȡ�ı��ļ�
	 * @param fileName
	 * @return
	 */
	private static List<String> ReadLinesWithEncoding(String fileName, String encoding, boolean root) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			String filePathName = DataFile(fileName, root);
			
			Debug.Log("��ȡ��" + filePathName);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePathName), encoding));
			
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
				
				return new String(bytes, 3, bytes.length - 3);
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
	 * �ж��ַ����Ƿ�Ϊ�գ�Ϊ�շ���true
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
	private static List<ListItemMap> ReadCommonIdName(String fileName, String key, boolean root) {

        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(fileName, root); // ��������root�ļ���
        
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
	
	private static List<ListItemMap> ReadCommonIdName(String fileName, String key) {
		return ReadCommonIdName(fileName, key, false);
	}

	/**
	 * ��ȡ��ַ�б�
	 * @return
	 */
	public static List<ListItemMap> GetAddressList() {
		
	    ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
	    
	    // �Ż�Ч�� ʡ����ַ
	    if (FileUtils.IsFileExist(DataFile(FILE_NAME_ADDRESS_PROVINCE, true)))
	    	return ReadCommonIdName(FILE_NAME_ADDRESS_PROVINCE, KEY_ADDRESS_ID, true);

	    String provinceLines = "";
        List<String> lines = ReadLines(FILE_NAME_ADDRESS);
        
        for (String line : lines) {
        	// id,����
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		String strId = token[0];
        		int id =  ParseInt(strId);

        		// ֻ��Ҫʡ��
        		if (id != INVALID_ID && id < 99) {

	        		String name = token[1];
	
	        		list.add(new ListItemMap(name/* ���� */, KEY_ADDRESS_ID, strId/* id */));
	        		
	        		provinceLines += strId + COMMON_TOKEN + name + "\n";
        		}
        	}
        }
        
        FileUtils.WriteText(DataFile("", true), FILE_NAME_ADDRESS_PROVINCE, provinceLines);

        return list;
	}

	/**
	 * ��ȡ�г��б�
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByArea(int address_id) {

		String marketCacheFileName = "market_" + address_id + ".txt";
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        
        // �Ż�Ч��
        if (FileUtils.IsFileExist(DataFile(marketCacheFileName)))
        	return ReadCommonIdName(marketCacheFileName, KEY_MARKET_ID);
        
        String marketLines = "";
        List<String> lines = ReadLines(FILE_NAME_MARKETS);
        
        for (String line : lines)  
        {
        	// 01,����,01002,˫���г�
        	String[] token = GetToken(line);
        	if (token.length == 4) {

        		int address_id_parsed =  ParseInt(token[0]);
        		if (!AddressMatch(address_id, address_id_parsed))
        			continue;

        		String id = token[2];
        		String name = token[3];
        		
        		list.add(new ListItemMap(name/* �г����� */, KEY_MARKET_ID, id/* �г�id */));
        		
        		marketLines += id + COMMON_TOKEN + name + "\n";
        	}
        }
        
        FileUtils.WriteText(DataFile(""), marketCacheFileName, marketLines);

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
        
        List<String> lines;
        String marketProductsFileName = "market_" + market_id + "_products.txt";
        
        // �Ż�
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
        		boolean isMyProduct = IsMyProduct(product_id); /* �����ѡ��ť״̬ */
        		
        		AddProductList(list, productId, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct);
            }

        	return list;
        }

        // ���ڴ��в���
        List<ListItemMap> productList = GetProductList();
        
        String marketProductLines = "";
        lines = ReadLines(FILE_NAME_PRODUCTS);
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
        		boolean isMyProduct = IsMyProduct(product_id); /* �����ѡ��ť״̬ */
        		
        		//list.add(new ProductListItemMap("����", "��ͼ�", "��߼�", "ƽ����", "���ؼ�", "��λ", "���"));
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

        FileUtils.WriteText(DataFile("", true), marketProductsFileName, marketProductLines);

        return list;
	}
	
	/**
	 * ��Ӳ�Ʒ�б�
	 */
	private static void AddProductList(ArrayList<ListItemMap> list, String productId, 
			String product_name, String minPrice, String maxPrice, 
			String averagePrice, String hostPrice, String unit, boolean isMyProduct) {
		list.add(new ProductListItemMap(DataMan.KEY_PRODUCT_ID, productId, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct));
	}

	/**
	 * �ж��Ƿ���ѡ��Ʒ
	 * @param product_id
	 * @return
	 */
	private static boolean IsMyProduct(int productId) {

		List<ListItemMap> list = GetMyProductList();

		for (ListItemMap map : list) {
			if (map.getInt(KEY_PRODUCT_ID) == productId)
				return true; // ����ѡ��Ʒ 
		}

		return false; // ������ѡ��Ʒ
	}

	/**
	 * ��ȡ��ѡ��Ʒ�б�
	 * @return
	 */
	public static List<ListItemMap> GetMyProductList() {
		return ReadCommonIdName(FILE_NAME_MY_PRODUCTS, KEY_PRODUCT_ID, true);
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
		
		if (product_id == INVALID_ID) {
			Debug.Log("�����ڲ�����MyProductListRemove");
			return false;
		}

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
			FileOutputStream out = new FileOutputStream(new File(DataFile(FILE_NAME_MY_PRODUCTS, true)));   
		
		    for (ListItemMap item : list) {   
		
		    	int product_id_to_be_save = item.getInt(KEY_PRODUCT_ID);

		    	if (product_id_to_be_save != INVALID_ID) {
			        String product_name_to_be_save = (String)item.get(KEY_NAME);
			        String line = product_id_to_be_save + COMMON_TOKEN + product_name_to_be_save + "\r\n";
			
			        out.write(line.getBytes());
		    	}
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
	public static List<ListItemMap> GetMarketListByProduct(String product_id) {

		List<String> lines = null;
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		String productMarketFileName = "product_" + product_id + "_markets.txt";

		if (product_id == null || product_id.length() == 0)
			return list;

		// �Ż�
		if (FileUtils.IsFileExist(DataFile(productMarketFileName, true))) {
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
	    		String unit = token[5];
	    		boolean addToMyProduct = false; /* ������ѡ��ť */
	        	
	        	list.add(new ProductListItemMap(DataMan.KEY_MARKET_ID, marketId, market_name, minPrice, maxPrice, averagePrice, hostPrice, unit, addToMyProduct));
	        }
			
			return list;
		}
		
		String productMarketLines = "";
		lines = ReadLines(FILE_NAME_PRODUCTS);
		
        for (String line : lines)
        {
        	// 0101301,������ƽ��ˮ��ũ����Ʒ�����г�,0101001000,0101301,13.5,Ԫ/����
        	// market_id,�г�����,product_id,��Ʒ��,��ͼ�,��߼�,ƽ����,���ؼ�,�۸�λ
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
    		boolean addToMyProduct = false; /* ������ѡ��ť */
        	
        	list.add(new ProductListItemMap(DataMan.KEY_MARKET_ID, marketId, market_name, minPrice, maxPrice, averagePrice, hostPrice, unit, addToMyProduct));
        	
        	productMarketLines += marketId + COMMON_TOKEN + market_name + COMMON_TOKEN +
        			minPrice + COMMON_TOKEN + maxPrice + COMMON_TOKEN + 
        			averagePrice + COMMON_TOKEN + hostPrice + COMMON_TOKEN + unit + "\n";
        }
        
        FileUtils.WriteText(DataFile("", true), productMarketFileName, productMarketLines);

        return list;
	}
	
	// һ��ĺ������� 1��=24*60*60*1000=86400000����
	//private static final long MILLIS_ONE_DAY = 86400000;
	
	/**
	 * ��ȡ���30��۸���Ϣ HISTORY_PRICE_DAYS
	 * TODO interface ��ȡ��ʷ�۸����ڣ�
	 * @param product_id
	 * @param market_id
	 * @return
	 * ��Ʒid�����г�idΪ�յĻ����ؿ�
	 */
	public static ProductPriceInfo GetHistoryPrice(String product_id, String market_id) {
		
		// ��Ʒid�����г�idΪ�յĻ����ؿ�
		if (market_id == null || product_id == null)
			return null;

		// ��ȡĳ�г���ʷ�۸�
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

		return null;
	}
	
	/**
	 * ���ɼ۸���Ϣ
	 * @param line
	 * @param priceUnit
	 * @param dateUnit
	 * @return
	 */
	private static ProductPriceInfo CreatePriceInfo(String line) {
		ProductPriceInfo info = new ProductPriceInfo();

		String[] prices = line.split(COMMON_TOKEN);
		
		if (prices.length < 2) {
			Debug.Log("�۸���Ϣ����" + line);
			return null;
		}
		
		// ���б�ȡ����ʱ����
		for (int i = prices.length - 1; i > 0; --i) {

			// ��ȡ����۸�
			String[] token = prices[i].split(":");
			if (token.length != 2)
				continue;
			
			String date  = token[0];
			Float price = 0F;
			try {
				price = Float.parseFloat(token[1]);
			} catch (Exception e) {
				e.printStackTrace();
				Debug.Log("�۸���Ϣ��������" + e.getMessage());
			}
			// ��ӵ���ļ۸�
			if (price != 0F)
				info.add(date, price);
		}
				
		return info;
	}

	/**
	 * ��ȡ��Ʒ����
	 * @return
	 */
	public static List<ListItemMap> GetProductClassList() {
		//return ReadCommonIdName(FILE_NAME_PRODUCT_CLASS, KEY_PRODUCT_CLASS_ID);
		
		// �Ż�Ч�� ʡ����ַ
		String productClassCacheFileName = "productClass.txt";
	    if (FileUtils.IsFileExist(DataFile(productClassCacheFileName, true)))
	    	return ReadCommonIdName(productClassCacheFileName, KEY_PRODUCT_CLASS_ID);
		
	    String productClassLines = "";
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(FILE_NAME_COMMODITY);
        
        for (String line : lines)  
        {
        	// id,����
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		String strId =  token[0];
        		int id =  ParseInt(strId);

        		// id ���룺��λ���Ƿ���
        		if (id != INVALID_ID && id < 100) {

	        		String name = token[1];

	        		list.add(new ListItemMap(name/* ���� */, KEY_PRODUCT_CLASS_ID, strId/* id */));
	        		
	        		productClassLines += strId + COMMON_TOKEN + name + "\n";
        		}
        	}
        }

        FileUtils.WriteText(DataFile("", true), productClassCacheFileName, productClassLines);

        return list;
	}
	
	/**
	 * ��ȡ��Ʒ�б�
	 * @return
	 */
	public static List<ListItemMap> GetProductList() {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(FILE_NAME_COMMODITY);
        
        for (String line : lines)  
        {
        	// id,����
        	String[] token = GetToken(line);
        	if (token.length == 2) {

        		int id =  ParseInt(token[0]);

        		// id ����
        		if (id != INVALID_ID && id > 100) {

	        		String name = token[1];

	        		list.add(new ListItemMap(name/* ���� */, KEY_PRODUCT_ID, token[0]/* id */));
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
		boolean meetCondition(String[] token, String line/* FIXME ��ʱ��*/);
	}
	private static List<String> GetSupplyDemandLinesCache = null;
	private static List<ListItemMap> GetSupplyDemandList(SupplyDemandListener listener) {
		
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		if (GetSupplyDemandLinesCache == null)
			GetSupplyDemandLinesCache = ReadLines(FILE_NAME_SUPPLY_DEMAND_INFO, false);

        for (String line : GetSupplyDemandLinesCache)
        {
        	// product_id,��Ʒ��,������Ϣid(��һλ����0����Ӧ��1������),user,����,������Ϣ����,����ʱ��,��Ч��,����,����,����,��Ʒ�ص�,��ϵ������,��ϵ�绰,�ֻ���,��ϸ��ַ
        	// 1003021000,������,0,,��Ӧ���ֹ����硢�̻���,,2011-2-23,2011-3-22,,����,������,,����,13521120562,13521120562,½��ׯ��Դ����ľ�г�
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
	 * ��ȡ������Ϣ�б�
	 * @return
	 */
	private static String supplyDemandClassLines = "";
	public static List<ListItemMap> GetSupplyDemandList(final String product_class_id, final boolean supply) {
		
		String supplyDemandClassCache = "supplyDemandClass_" + product_class_id + "_" + supply +"_.txt";
		
		// �Ż�Ч�� ʡ����ַ
	    if (FileUtils.IsFileExist(DataFile(supplyDemandClassCache))) {
	    	List<String> lines = ReadLines(supplyDemandClassCache);
	    	List<ListItemMap> list = new ArrayList<ListItemMap>();

	        for (String line : lines)
	        {
	        	// product_id,��Ʒ��,������Ϣid(��һλ����0����Ӧ��1������),user,����,������Ϣ����,����ʱ��,��Ч��,����,����,����,��Ʒ�ص�,��ϵ������,��ϵ�绰,�ֻ���,��ϸ��ַ
	        	// 1003021000,������,0,,��Ӧ���ֹ����硢�̻���,,2011-2-23,2011-3-22,,����,������,,����,13521120562,13521120562,½��ׯ��Դ����ľ�г�
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
	public final static String SUPPLY_DEMAND_INFO_KEY_CONTACT_NAME = "contact_name";
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
	/*
	public static ListItemMap GetSupplyDemandInfo(int supply_demand_id) {

		if (GetSupplyDemandLinesCache == null)
			GetSupplyDemandLinesCache = ReadLines(FILE_NAME_SUPPLY_DEMAND_INFO);
        
        for (String line : GetSupplyDemandLinesCache)
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
	*/
	
	/**
	 * ������ϸ������Ϣmap
	 */
	private static ListItemMap GetSupplyDemandMap(String[] token) {
		String title = token[4];
		
		String supply_demand_id = title; // interface û�й�����Ϣid
		ListItemMap map = new ListItemMap(title, KEY_SUPPLY_DEMAND_INFO_ID, supply_demand_id);
		
		// 1003021000,������,0,,��Ӧ���ֹ����硢�̻���,,2011-2-23,2011-3-22,,����,������,,����,13521120562,13521120562,½��ׯ��Դ����ľ�г�
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
	 * ��ȡ����Խ���Ϣ�б�(���ֹ���)
	 * @return
	 */
	public static List<ListItemMap> GetSupplyDemandPairList(final String product_class_id) {
		
		final String curUserId = UserMan.GetUserId();
		
		Debug.Log("curUserId=" + curUserId);
		
		SupplyDemandListener listener = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token, String line) {

				// ɸѡ�ҵĹ�����Ϣ
	        	// �ж��ǵ��û��Լ��Ĺ�����Ϣ
	        	if (curUserId.equals(token[3])) {
	        		// �����Ʒ����id�͹�����Ϣid
	        		String product_id = token[0];
	        		//int product_class    = ProductClassDecode(product_id);
	        		
	        		// ��Ʒ����
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
	        
	        	// �ж��ǵ��û��Լ��Ĺ�����Ϣ
	        	if (curUserId.equals(token[3]))
	        		return false;
	        	
	        	//int product_class    = ParseInt(token[0]);
	        	String product_class    = token[0];
        		int supply_demand_id = ParseInt(token[2]);
        		
	        	// �ж��Ƿ�Խ�
	        	for (ListItemMap pairInfo : myInfoList) {
	        		
	        		// �õ�������
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
	 * ��ȡ�ҵ���Ȧ�����б�
	 * majorIid: INVALID_ID ���������������б�
	 * @return
	 * @param
	 * myFriend���Ƿ��ѯ�ҵĺ���
	 */
	public static List<ListItemMap> GetMyGroupFriendList(int majorIid, boolean myFriend) {
		
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_USERS);
        
        for (String line : lines)  
        {
        	// user,����,רҵ,��ϵ��ַ,��ϵ�绰
        	//0,zhangsan,����,רҵ1,����ͨ��,13812341234,���ҽ���
        	String[] token = GetToken(line);
        	if (token.length == 5) {

        		String id = token[0];
        			
    			// XXX ���Ż�����cache��ȡ����Ȼ��ƥ��
    			int major_id = ParseInt(token[1]);
    			String major = "δ֪רҵ";
    			
    			if (major_id < MAJOR.length)
    				major = MAJOR[major_id];
    			else
    				Debug.Log("���ش���δ֪רҵ��" + major_id);

    			// ȡ�������ѻ�������ƥ�䣬��Ȼ����
    			if (majorIid != INVALID_ID && majorIid != major_id)
    				continue;

        		String name = id;//token[2];
        		String telephone = token[2];
        		String address   = token[3];
        		String introduce = token[4];
        		
        		if (myFriend) {
        			// TODO �ж��Ƿ��ҵĺ���
        		}

        		ListItemMap map = new ListItemMap(name/* ���� */, KEY_FRIEND_ID, id/* �û�id */);
        		
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
	 * fiend_id:INVALID_ID ����������������Ϣ
	 * 0 ��ʾ�����ҵ�����
	 * @return
	 */
	public static List<ListItemMap> GetMyGroupMessageList(int fiend_id) {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_MY_GROUP_MESSAGE);
        
        //int  = ParseInt(friendId);
        
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
        			if ((id != fiend_id && fiend_id != INVALID_ID) ||
        				(fiend_id != 0 && !UserMan.GetUserId().equals(token[1])))
        				continue;

        			int owner_id = ParseInt(token[1]);
	        		String owner = token[2];
	        		
	        		int poster_id = ParseInt(token[3]);
	        		String poster = token[4];

	        		// ��ѯ�ҵ�����
        			if (MY_MESSAGE == fiend_id && !token[1].equals(UserMan.GetUserId())) {
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
		String params = "user=" + userId + "&friend=" + friendId + "&message=" + message;

		String ret = Downloader.PostUrl(URL_SERVER + URL_POST_MESSAGE, params);

		if (ret.equals("true"))
			return true;
		
		Debug.Log("�������Դ��󣺷��أ�" + ret);

		return false;
	}
	
	/**
	 * ����idת��
	 * @param functionId
	 * @param column
	 * @return
	 */
	private static boolean IsAisColumnMatch(int functionId, int column) {
		switch (column) {
		case 5: // ũҵ����
			return (functionId == UIConst.FUNCTION_ID_ARGRI_TECH);
		case 6: // ר��ָ��
			return (functionId == UIConst.FUNCTION_ID_EXPERT_GUIDE);
		case 8: // ��ѧʩ��
			return (functionId == UIConst.FUNCTION_ID_EXPERT_FERTILIZE);
		case 9: // ʱ��Ҫ��
			return (functionId == UIConst.FUNCTION_ID_CUR_POLITICS);
		case 10: // ��ѡ�μ�
			return (functionId == UIConst.FUNCTION_ID_BEST_COUSE);
		case 11: // �ȷ浳Ա
			return (functionId == UIConst.FUNCTION_ID_VANGUARD_PARTY);
		case 12: // ����ģ��
			return (functionId == UIConst.FUNCTION_ID_CLASS_EXPERIENCE);
		case 13: // �¸�����
			return (functionId == UIConst.FUNCTION_ID_MODEL);
		case 14: // ����ũ��
			return (functionId == UIConst.FUNCTION_ID_HAPPAY);
		case 15: // ���ɷ���
			return (functionId == UIConst.FUNCTION_ID_LAW);
		case 16: // ��ũ����
			return (functionId == UIConst.FUNCTION_ID_POLICY);
		default:
			return false;
		}
	}
	
	/**
	 * ��ȡAIS��һ�������б�
	 * ���function_idΪ0�򷵻�һ�����࣬���򷵻�function_id��Ӧ���Ӽ�����
	 * @return
	 */
	public static List<ListItemMap> GetAisColumnChildList(int functionId) {

		List<ListItemMap> list = new ArrayList<ListItemMap>();
		List<String> lines = ReadLines(FILE_NAME_AIS_LIST);
		Map<String, String> map = new HashMap<String, String>();
		
		String[] token;
		for (String line : lines) {
			token = line.split(TOKEN_SEP);
			
			if (token != null && token.length == 4) {
				// 20130607112901281,10,�Ծ�һ,2013-6-7
				int column = ParseInt(token[1]);
				if (!IsAisColumnMatch(functionId, column))
					continue;
				
				String name = token[2];
				String fileName = name + ".ais";
				
				AisDoc aisDoc = new AisDoc(fileName);
				String child = aisDoc.getAisChildColumn();
				
				// ���ظ����
				if (!child.isEmpty() && map.get(child) == null)
					map.put(child, child);
			}
		}
		
		// ����ӷ���
		for (String childName : map.keySet()) {
			ListItemMap item = new ListItemMap(childName, KEY_AIS_COLUMN_CHILD, childName);
			list.add(item);
		}
		
		return list;
	}

	/**
	 * ������Ʒ����
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
	 * ��ȡAIS�ڶ��������б�
	 * @param class_id
	 * INVALID��������
	 * @return
	 */
	public static List<ListItemMap> GetAisList(int functionId, String childColumn) {

		List<ListItemMap> list = new ArrayList<ListItemMap>();
		List<String> lines = ReadLines(FILE_NAME_AIS_LIST);

		if (childColumn == null)
			return list;
		
		String[] token;
		for (String line : lines) {
			token = line.split(TOKEN_SEP);
			
			if (token != null && token.length == 4) {
				// 20130607112901281,10,�Ծ�һ,2013-6-7
				int column = ParseInt(token[1]);
				if (!IsAisColumnMatch(functionId, column))
					continue;
				
				String name = token[2];
				String fileName = name + ".ais";
				
				AisDoc aisDoc = new AisDoc(fileName);
				
				// ��������
				if (childColumn.isEmpty() ||
					aisDoc.getAisChildColumn().equals(childColumn)) {
					list.add(new ListItemMap(name, KEY_AIS_FILE_NAME, fileName));
				}
			}
		}
		
		//String fileName = GetAisColumnFileName(functionId);

        return list;
	}

	/**
	 * ���ؼ���·���������ļ���
	 * @param fileName
	 * @return
	 */
	public static String DataFile(String fileName, boolean root) {
		String appPath = AppDataPath(root);
		
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
	 * ����֮ǰ����
	 * @param fileName
	 * @return
	 */
	public static String DataFile(String fileName) {
		return DataFile(fileName, false);
	}
	
	/**
	 * ���ؼ���·���������ļ���
	 * @param fileName
	 * @return
	 */
	private static String AppDataPath(boolean root) {
		if (root)
			return Environment.getExternalStorageDirectory() + "/zknx.hn";
		else
			return Environment.getExternalStorageDirectory() + "/zknx.hn/" + GetCurrentTime(false);
	}
	
	/**
	 * ������㲥�����Ƿ���Ҫ���£�ʱ�����ƥ�䲢�������ļ����ڣ�
	 * @return
	 */
	public static boolean ShouldUpdateData() {

		String today = GetCurrentTime(false);
		//String dataFileName = DataFile(today + ".zip");

		List<String> line = ReadLines(TIME_STAMP_FILE_NAME, true);

		// û��ʱ���������ʱ�����ƥ��
		boolean timStampNotMatch = (line.size() == 0) || (!line.get(0).equals(today));

		// ʱ�����ƥ�䣬�ҵ��������ļ�����
		return timStampNotMatch;// && FileUtils.IsFileExist(dataFileName);
	}
	
	/**
	 * д����ʱ��������ڱ�ǽ����Ƿ��ѹ�����ݸ���
	 * @param today
	 * @return
	 */
	private static boolean WriteTimeStamp(String today) {
		return FileUtils.WriteFile(DataFile(TIME_STAMP_FILE_NAME, true), today.getBytes());
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
		
		GetNewMessages();

		// ���ʱ���
		if (ShouldUpdateData()) {
			return UpdateTodayData();
		}
		
		return false;
	}
	
	/**
	 * ��ȡ��ǰ�û��µ�����
	 */
	private static void GetNewMessages() {
		Downloader.DownFile(URL_GET_MESSAGE + "?userid=" + UserMan.GetUserId(), DataFile("", true), FILE_NAME_NEW_MESSAGE /* TODO ��ȡ������ */);
	}
	
	/**
	 * ���µ�������
	 * @return
	 */
	public static boolean UpdateTodayData() {

		// ��ȡ��������
		String today = GetCurrentTime(false);
		return WriteTimeStamp(today);
		
		/*
		String dataFileName = DataFile(today + ".zip");

		// ��ѹ���ݸ���
		if (FileUtils.IsFileExist(dataFileName) &&
			Ziper.UnZip(dataFileName, DataFile(""))) {
			// дʱ���
			return WriteTimeStamp(today);
		}

		return false;
		*/
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
	 * ����������Ϣ
	 * TODO ������ ���Ʒ���������Ϣ�����Ʋ���������
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
		//type=0&title=��Ӧ������&userid=linshi&addressid=06056&commodityid=0305002000&count=����&price=5&unit=Ԫ/����&phonenumber=15941652887&place=���������躣����ׯ����С��巶��&linkman=������&remark=��ע&validity=2013-06-24&publishdate=2013-05-25
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
		
		Debug.Log("����������Ϣ���󣺷��أ�" + ret);

		return false;
	}
	
	/**
	 * ���û�������רҵid
	 * @param userId
	 * @return
	 */
	public static int GetMajor(String userId) {
		//return DataInterface.GetMajor(userId);
		return -1;
	}
	
	/**
	 * TODO interface ��ר������
	 * @return
	 */
	public static boolean AskExpert(String userId, String expertId, String subject, String question) {
		// TODO interface ���ʲ��������? encoding?
		String params = "user=" + userId + ",expert=" + expertId + ",subject=" + subject + ",question=" + question;

		String ret = Downloader.PostUrl(URL_ASK_EXPERT, params);

		if (ret.equals("true")) {
			SaveTodayLocalQuestion(expertId, subject, question);
			return true;
		}
		
		Debug.Log("����ר�Ҵ��󣺷��أ�" + ret);

		return false;
	}

	/**
	 * ��ȡר���б�
	 * @return
	 */
	public static List<ListItemMap> GetExpertList() {
		List<String> lines = ReadLines(FILE_NAME_EXPERTS);
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
	 * ���汾�سɹ������ʣ����������ʾ�������б���
	 * @param expertId
	 * @param subject
	 * @param question
	 */
	private static String LOCAL_QUESTION = "local_questions.txt";
	private static void SaveTodayLocalQuestion(String expertId, String subject,	String question) {
		// ���б�ĵ�һ��
		String questionLines = expertId + COMMON_TOKEN + subject + COMMON_TOKEN + question + "\n";
        List<String> lines = ReadLines(LOCAL_QUESTION);
        boolean duplicated = false;
        
        for (String line : lines) {
        	// id,����
        	String[] token = GetToken(line);
        	if (token.length == 3) {

        		String savedExpertId = token[0];
        		String savedSubject  = token[1];
        		String savedQuestion = token[2];

        		// ֻ��Ҫʡ��
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
        	FileUtils.WriteText(DataFile(""), LOCAL_QUESTION, questionLines);
        }
	}
	
	public static List<ListItemMap> GetExpertAnwserList(String expertId) {
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		// ��ȡ���������б�
		List<String> lines = ReadLines(LOCAL_QUESTION);
		
		for (String line : lines) {
        	// id,����
        	String[] token = GetToken(line);
        	if (token.length == 3) {

        		String savedExpertId = token[0];
        		String savedSubject  = token[1];
        		String savedQuestion = token[2];
        		
        		if (savedExpertId.endsWith(expertId)) {
        			ListItemMap map = new ListItemMap(savedSubject, KEY_EXPERT_QUESTION_SUBJECT, savedSubject);
        			map.put(KEY_EXPERT_QUESTION_CONTENT, savedQuestion);
        			list.add(map);
        		}
        	}
		}
		
		// TODO GetExpertAnwserList
		
		return list;
	}
}
