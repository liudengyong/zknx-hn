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
	// ais����
	public static final String KEY_AIS_DATE = "ais_date";

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
	//public static final int MY_MESSAGE = 0;

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
        List<String> lines = ReadLines(FILE_NAME_ADDRESS, true);
        
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
        
        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, FILE_NAME_ADDRESS_PROVINCE, provinceLines);

        return list;
	}

	/**
	 * ��ȡ�г��б�
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByArea(int address_id) {

		String marketCacheFileName = "markets/market_" + address_id + ".txt";
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        
        // �Ż�Ч��
        if (FileUtils.IsFileExist(DataFile(marketCacheFileName, true)))
        	return ReadCommonIdName(marketCacheFileName, KEY_MARKET_ID, true);
        
        String marketLines = "";
        List<String> lines = ReadLines(FILE_NAME_MARKETS, true);
        
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
        
        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, marketCacheFileName, marketLines);

        return list;
	}

	/**
	 * ��ȡ��Ʒ�б�
	 * @return
	 */
	public static List<ListItemMap> GetProductList(int market_id) {
		// ��ȡ����Ĳ�Ʒ�б�
		return GetProductList(new Date(), market_id, false);
	}
	
	public static List<ListItemMap> GenProductList(int market_id) {
		// ���ɽ���Ĳ�Ʒ�б�
		return GetProductList(new Date(), market_id, true);
	}

	/**
	 * null���ڱ�ʾ����
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
        		boolean isMyProduct = false;

        		// ����ʱ���ù����Ƿ���ѡ
        		if (!justGen)
        			isMyProduct = IsMyProduct(myProducts, product_id); /* �����ѡ��ť״̬ */

        		AddProductList(list, productId, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct);
            }

        	return list;
        }

        String marketProductLines = "";
        lines = ReadLines(dateString + FILE_NAME_PRODUCTS_PRICE, true);
        for (String line : lines)
        {
        	String[] token = GetToken(line);
        	// 0101301,������ƽ��ˮ��ũ����Ʒ�����г�,0101001000,0101301,13.5,Ԫ/����
        	if (token.length == 6) {
        		// market_id,�г�����,product_id,��Ʒ��,��ͼ�,��߼�,ƽ����,���ؼ�,�۸�λ
        		// 1506901,ɽ���ٹ������г�,0101001000,����,3.23,Ԫ/����
        		int market_id_parsed = ParseInt(token[0]);
        		if (market_id_parsed != market_id)
        			continue;

        		// �ݲ���Ҫ�г�����
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

        		// ����ʱ���ù����Ƿ���ѡ
        		if (!justGen)
        			isMyProduct = IsMyProduct(myProducts, product_id); /* �����ѡ��ť״̬ */
        		
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

        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, marketProductsFileName, marketProductLines);

        return list;
	}
	
	/**
	 * ��Ӳ�Ʒ�б�
	 */
	private static void AddProductList(ArrayList<ListItemMap> list, String productId, 
			String product_name, String minPrice, String maxPrice, 
			String averagePrice, String hostPrice, String unit, boolean isMyProduct) {
		list.add(new ProductListItemMap(KEY_PRODUCT_ID, productId, product_name, minPrice, maxPrice, averagePrice, hostPrice, unit, isMyProduct));
	}

	/**
	 * �ж��Ƿ���ѡ��Ʒ
	 * @param product_id
	 * @return
	 */
	private static boolean IsMyProduct(List<ListItemMap> list, int productId) {
		
		if (list == null) {
			Debug.Log("IsMyProduct����");
			return false;
		}

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
		// �����ѡ��Ʒ�б��ļ����ڣ��򸲸�
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
	 * ��ȡ�����иò�Ʒ���г��б�
	 * @return
	 */
	public static void GenMarketListByProduct(String product_id) {
		GetMarketListByProduct(product_id, true);
	}
	
	/**
	 * ��ȡ�����иò�Ʒ���г��б�
	 * @return
	 */
	public static List<ListItemMap> GetMarketListByProduct(String product_id) {
		return GetMarketListByProduct(product_id, false);
	}

	/**
	 * ��ȡ�����иò�Ʒ���г��б�
	 * @return
	 */
	private static List<ListItemMap> GetMarketListByProduct(String product_id, boolean justGenerate) {

		List<String> lines = null;
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		String productMarketFileName = "markets/product_" + product_id + "_markets.txt";

		if (product_id == null || product_id.length() == 0)
			return list;

		// �Ż�
		if (FileUtils.IsFileExist(DataFile(productMarketFileName, true))) {
			
			// ֻ�����ɴ������ݣ�������
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
	    		boolean addToMyProduct = false; /* ������ѡ��ť */
	        	
	        	list.add(new ProductListItemMap(DataMan.KEY_MARKET_ID, marketId, market_name, minPrice, maxPrice, averagePrice, hostPrice, unit, addToMyProduct));
	        }
			
			return list;
		}
		
		String productMarketLines = "";
		lines = ReadLines(FILE_NAME_PRODUCTS_PRICE);
		
        for (String line : lines)
        {
        	// 0101301,������ƽ��ˮ��ũ����Ʒ�����г�,0101001000,0101301,13.5,Ԫ/����
        	// market_id,�г�����,product_id,��Ʒ��,��ͼ�,��߼�,ƽ����,���ؼ�,�۸�λ
        	// product_id,product_name
        	String[] token = GetToken(line);
        	if (token.length != 6)
        		continue;

        	//int product_id_parsed = ParseInt(token[2]);
        	// product_id ��û��0��ͷ�ĸ�ʽ
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
        
        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, productMarketFileName, productMarketLines);

        return list;
	}
	
	private static Float randomPrice() {
		long time = System.currentTimeMillis();
		return 4.0F + (time % 4);
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
		
		/* ����������
		
		ProductPriceInfo info = new ProductPriceInfo();
		
		// ���ڸ�ʽ����.�գ�
		SimpleDateFormat simpleDate = new SimpleDateFormat("M.d", Locale.CHINA);
		// ��ǰ��ȥ7��
		long today = System.currentTimeMillis();
		for (int i = 0; i < 7; ++i) {

			// ��ȡ����۸�
			Float price = randomPrice();
			Debug.Log("���������ʷ�۸�" + price);
			// ��ӵ���ļ۸�
			if (price != 0F)
				info.add(simpleDate.format(new Date(today)), price);

			today -= MILLIS_ONE_DAY;
		}
		
		return info;
		
		*/
		
		/* ��������������
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
		
		*/
		
		// ѭ�����ҽ�30����ʷ�۸�
		ProductPriceInfo info = new ProductPriceInfo();
		
		// ���ڸ�ʽ����.�գ�
		SimpleDateFormat simpleDate = new SimpleDateFormat("M.d", Locale.CHINA);
		// ��ǰ��ȥ7��
		long today = System.currentTimeMillis();
		for (int i = 0; i < 7; ++i) {

			Date date = new Date(today);
			// ��ȡ����۸�
			Float price = GetProductPrice(date, market_id, product_id);//randomPrice();
			// ��ӵ���ļ۸�
			if (price != 0F)
				info.add(simpleDate.format(date), price);

			today -= MILLIS_ONE_DAY;
		}
		
		return info;
	}
	
	/*
	 * ��ȡ�۸�
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
					Debug.Log("����Float�۸����");
				}
				
				return price;
			}
		}
		
		return 0F;
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

		// �Ż�Ч�� ��Ʒ����
		String productClassCacheFileName = "productClass.txt";
	    if (FileUtils.IsFileExist(DataFile(productClassCacheFileName, true)))
	    	return ReadCommonIdName(productClassCacheFileName, KEY_PRODUCT_CLASS_ID, true);

	    String productClassLines = "";
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
        List<String> lines = ReadLines(FILE_NAME_COMMODITY, true);
        
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

        if (lines.size() > 0)
        	FileUtils.WriteGB2312Text(true, productClassCacheFileName, productClassLines);

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
	public static List<ListItemMap> GetSupplyDemandList(String product_class_id, boolean supply) {
		//GenSupplyDemandList();
		
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		// ��ǰ��ȥ30��
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			Date date = new Date(today);
			String strDate = mDateFormater.format(date) + "/";

			// �жϵ�����Ƿ��Ѿ������
			String stampFileName = DataFile(date + "processedSDInfo.txt");
			if (!FileUtils.IsFileExist(stampFileName)) {
				ProcessSupplyDemandInfo(date);
			}

			String genFileName = GetGenSupplyDemandFileName(strDate, product_class_id, supply);
			List<String> lines = ReadLinesWithEncoding(genFileName, "UTF-8", true);

			// ����û�й���
			if (lines.size() == 0)
				continue;

			for (String line : lines) {
				String[] token = GetToken(line);
	        	if (token.length != 16)
	        		continue;
	        	
	        	// ��ӹ�����Ϣ
	        	list.add(GetSupplyDemandMap(token));
			}
		}
        
        return list;
	}
	
	/**
	 * ���ɵĹ�����Ϣ
	 * @param date
	 * @param productClass
	 * @param supply
	 * @return
	 */
	private static String GetGenSupplyDemandFileName(String date, String productClass, boolean supply) {
		// ����Ʒ����+��/�󱣴������ļ�
    	String genFileName = "";
    	if (supply)
    		genFileName = date + "tradinfo/" +productClass + "_supply";
    	else
    		genFileName = date + "tradinfo/" + productClass + "_demand";
    	
    	return genFileName;
	}
	
	/**
	 * ���ɹ�������
	 * @param product_class_id
	 * @param supply
	 */
	private static void GenSupplyDemandList() {
		
		List<ListItemMap> productClass = GetProductClassList();
		
		if (productClass == null || productClass.size() == 0) {
			return;
		}

		// ��ǰ��ȥ30��
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {
			ProcessSupplyDemandInfo(new Date(today));
		}
	}

	private static void ProcessSupplyDemandInfo(Date date) {
		
		String strDate = mDateFormater.format(date) + "/";
		
		// �жϵ�����Ƿ��Ѿ������
		String stampFileName = DataFile(strDate + "processedSDInfo.txt", true);
		if (FileUtils.IsFileExist(stampFileName))
			return;
		
		List<String> lines = ReadLines(strDate + FILE_NAME_SUPPLY_DEMAND_INFO, true);
		
		// ����û�й���
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

			Debug.Log(strDate + FILE_NAME_SUPPLY_DEMAND_INFO + "����" + count++ + "��");

			// ����
			FileUtils.AppendLine(DataFile(genFileName, true), line);
		}

		// дʱ��������ж��Ƿ��Ѿ������
		FileUtils.WriteText(stampFileName, strDate);
	}

	// ��Ʒ����id
	public final static String SUPPLY_DEMAND_INFO_PRODUCT_CLASS = "product_class";
	// ����������Ϣ��user_id
	public final static String SUPPLY_DEMAND_INFO_KEY_USER = "user";
	// ����
	public final static String SUPPLY_DEMAND_INFO_KEY_TITLE = "title";
	// �Ƿ�supply
	public final static String SUPPLY_DEMAND_INFO_KEY_SUPPLY = "supply";
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
	 * �Ƿ�Խ���Ϣ
	 * @return
	 */
	private static boolean IsPairTradeInfo(List<ListItemMap> mySDInfoList, String[] token) {
		// 02,��ʳ����,0,dengyong,qiu liangshi 100dun,,2013-7-22,2013-8-21,12,12345,�����Žֵ�,,刘登�?,18911939853,18911939853,beijng
		for (ListItemMap item : mySDInfoList) {
			boolean supply = ("0".equals(item.getString(SUPPLY_DEMAND_INFO_KEY_SUPPLY)));
			if ((supply && token[2].equals("1")) ||
				(!supply && token[2].equals("0"))) {
				if (supply)
					Debug.Log("�Խ���Ϣ��" + token[4] + ",�ҹ�Ӧ��" + item.getString(SUPPLY_DEMAND_INFO_KEY_TITLE));
				else
					Debug.Log("�Խ���Ϣ��" + token[4] + ",���󹺣�" + item.getString(SUPPLY_DEMAND_INFO_KEY_TITLE));
				return true;
			}
		}

		return false;
	}

	/**
	 * ��ȡ����Խ���Ϣ�б�(���ֹ���)
	 * @return
	 */
	private static String pairLines = "";
	public static List<ListItemMap> GetSupplyDemandPairList(String product_class_id) {
		
		// ���ȴ�������
		GenSupplyDemandList();
		
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		// ��ȡ�ҷ����Ĺ�����Ϣ
		List<ListItemMap> mySDInfoList = new ArrayList<ListItemMap>();
		
		// ��ǰ��ȥ30��
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = mDateFormater.format(new Date(today)) + "/";

			boolean supply = false;
			String genFileName = GetGenSupplyDemandFileName(date, product_class_id, supply);
			List<String> lines = ReadLinesWithEncoding(genFileName, "UTF-8", true);

	        for (String line : lines) {
	        	// 02,��ʳ����,0,dengyong,qiu liangshi 100dun,,
	        	// product_id,��Ʒ��,������Ϣid(��һλ����0����Ӧ��1������),user,����,������Ϣ����,����ʱ��,��Ч��,����,����,����,��Ʒ�ص�,��ϵ������,��ϵ�绰,�ֻ���,��ϸ��ַ
	        	// 1003021000,������,0,,��Ӧ���ֹ����硢�̻���,,2011-2-23,2011-3-22,,����,������,,����,13521120562,13521120562,½��ׯ��Դ����ľ�г�
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
	        	// 02,��ʳ����,0,dengyong,qiu liangshi 100dun,,
	        	// product_id,��Ʒ��,������Ϣid(��һλ����0����Ӧ��1������),user,����,������Ϣ����,����ʱ��,��Ч��,����,����,����,��Ʒ�ص�,��ϵ������,��ϵ�绰,�ֻ���,��ϸ��ַ
	        	// 1003021000,������,0,,��Ӧ���ֹ����硢�̻���,,2011-2-23,2011-3-22,,����,������,,����,13521120562,13521120562,½��ׯ��Դ����ľ�г�
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
			Debug.Log("��ǰ�û�û�з����κ���Ϣ");
			return list;
		}

		// ��ǰ��ȥ30��
		today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = mDateFormater.format(new Date(today)) + "/";

			boolean supply = false;
			String genFileName = GetGenSupplyDemandFileName(date, product_class_id, supply);
			List<String> lines = ReadLinesWithEncoding(genFileName, "UTF-8", true);

	        for (String line : lines) {
	        	// 02,��ʳ����,0,dengyong,qiu liangshi 100dun,,
	        	// product_id,��Ʒ��,������Ϣid(��һλ����0����Ӧ��1������),user,����,������Ϣ����,����ʱ��,��Ч��,����,����,����,��Ʒ�ص�,��ϵ������,��ϵ�绰,�ֻ���,��ϸ��ַ
	        	// 1003021000,������,0,,��Ӧ���ֹ����硢�̻���,,2011-2-23,2011-3-22,,����,������,,����,13521120562,13521120562,½��ׯ��Դ����ľ�г�
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
	        	// 02,��ʳ����,0,dengyong,qiu liangshi 100dun,,
	        	// product_id,��Ʒ��,������Ϣid(��һλ����0����Ӧ��1������),user,����,������Ϣ����,����ʱ��,��Ч��,����,����,����,��Ʒ�ص�,��ϵ������,��ϵ�绰,�ֻ���,��ϸ��ַ
	        	// 1003021000,������,0,,��Ӧ���ֹ����硢�̻���,,2011-2-23,2011-3-22,,����,������,,����,13521120562,13521120562,½��ׯ��Դ����ľ�г�
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
		
		// �Ż�Ч�� ʡ����ַ
	    if (FileUtils.IsFileExist(DataFile(pairCache))) {
	    	if (justGen)
	    		return null;

	    	List<String> lines = ReadLines(pairCache);
	    	List<ListItemMap> list = new ArrayList<ListItemMap>();

	        for (String line : lines) {
	        	// product_id,��Ʒ��,������Ϣid(��һλ����0����Ӧ��1������),user,����,������Ϣ����,����ʱ��,��Ч��,����,����,����,��Ʒ�ص�,��ϵ������,��ϵ�绰,�ֻ���,��ϸ��ַ
	        	// 1003021000,������,0,,��Ӧ���ֹ����硢�̻���,,2011-2-23,2011-3-22,,����,������,,����,13521120562,13521120562,½��ׯ��Դ����ľ�г�
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
		
		pairLines = "";
		
		SupplyDemandListener listener2 = new SupplyDemandListener() {
			@Override
			public boolean meetCondition(String[] token, String line) {
				
				if (token == null ||
					token.length < 4 ||
					curUserId == null)
					return false;
	        
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
	 * ��ȡ�ҵ���Ȧ�����б�
	 * majorIid: INVALID_ID ���������������б�
	 * @return
	 * @param
	 * myFriend���Ƿ��ѯ�ҵĺ���
	 */
	public static List<ListItemMap> GetMyGroupFriendList(int majorIid, boolean myFriend) {
		
		// ���������������б�
		Downloader.DownFile(URLT_GET_FRIENDS + "?userid=" + UserMan.GetUserId() +
				"&after=" + "1970-01-01"
				, DataFile(""), FILE_NAME_FRIEND);
		
        ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();  
        List<String> lines = ReadLines(FILE_NAME_FRIEND, true);
        
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

    			// �ų��Լ�
    			if (id.equals(UserMan.GetUserId()))
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
	public static List<ListItemMap> GetMyGroupMessageList(String fiend_id) {
		
		ArrayList<ListItemMap> list = new ArrayList<ListItemMap>();
		
		if (fiend_id == null)
			return list;
		
		// �����������ҵ�����
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
        			// Ϊ��ֹ��Ϣ���ж��ţ���ȥǰ�����ָ�ͺ������ָ������Ϊ��Ϣ����
        			message += "," + token[i];
        		}

        		String itemText = message + "\t" + time;
        		if (fiend_id.equals(UserMan.GetUserId()))
        			itemText = frienId + " �� " + message;
        		else if (!fiend_id.equals(frienId))
        			continue;
        		
        		ListItemMap map = new ListItemMap(itemText/* ���� */, KEY_FRIEND_ID, frienId/* id */);
        		
        		map.put(KEY_FRIEND_MESSAGE_CONTENT, message);
        		// ���һ���ָ�Ϊʱ��
        		map.put(KEY_FRIEND_MESSAGE_DATE, time);
        		
        		GetUserInfo(frienId, map);
        		
        		list.add(map);
			}
        }
		
		return list;
		
		/*
		// TODO �ݲ���ȡ��ʷ���ԣ��ӿڴ�Э�̣�get_messageֻ��ȡ�������ԣ�
        lines = ReadLines(FILE_NAME_MY_GROUP_MESSAGE);
        
        //int  = ParseInt(friendId);
        
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
	 * ���û��б��в�ѯ�û���Ϣ
	 */
	private static void GetUserInfo(String userId, ListItemMap info) {
		
		List<String> lines = ReadLines(FILE_NAME_USERS);
		
		for (String line : lines) {
			// liye3,10,13800138000,��ע��Ĭ�ϵ�ַ��,ע��Ĭ�ϱ�ע��Ϣ
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
	 * ����������
	 * @param userId
	 * @param friendId
	 * @param message
	 * @return
	 */
	public static boolean PostNewMessage(String userId, String friendId, String message) {
		String params;
		try {
			// TODO userId �� friendId �� ���壿
			params = "user=" + friendId + "&friend=" + userId +
					"&message=" + URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}

		String ret = Downloader.PostUrl(URL_SERVER + URL_POST_MESSAGE, params);

		if (ret.equals("TRUE"))
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
	private static String FILE_NAME_GEN_AIS_LIST = "gen_ais_list.txt";
	public static List<ListItemMap> GetAisColumnChildList(int functionId) {

		List<ListItemMap> list = new ArrayList<ListItemMap>();
		Map<String, String> map = new HashMap<String, String>();

		// ��ǰ��ȥ30��
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = mDateFormater.format(new Date(today)) + "/";

			GenAisList(date);
			
			List<String> lines = ReadLinesWithEncoding(date + FILE_NAME_GEN_AIS_LIST, "UTF-8", true);
			
			String[] token;
			for (String line : lines) {
				token = line.split(TOKEN_SEP);
				
				if (token != null && token.length == 5) {
					// 20130607112901281,10,ʳƷ,�Ծ�һ,2013-6-7
					int column = ParseInt(token[1]);
					if (!IsAisColumnMatch(functionId, column))
						continue;
					
					String child = token[2];

					// ���ظ����
					if (!child.isEmpty() && map.get(child) == null)
						map.put(child, child);
				}
			}
		}

		// ����ӷ���
		for (String childName : map.keySet()) {
			ListItemMap item = new ListItemMap(childName, KEY_AIS_COLUMN_CHILD, childName);
			list.add(item);
		}

		return list;
	}
	
	private static void GenAisList() {
		// ���ڸ�ʽ����.�գ�
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

		// ��ǰ��ȥ30��
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = simpleDate.format(new Date(today)) + "/";

			GenAisList(date);
		}
	}
	
	/**
	 * ����ais list
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
				// 20130607112901281,10,�Ծ�һ,2013-6-7
				int column = ParseInt(token[1]);
				if (column == INVALID_ID)
					continue;
				
				String name = token[2];
				String fileName = name + ".ais";
				
				AisDoc aisDoc = new AisDoc(null, fileName, true, date);
				String child = aisDoc.getAisChildColumn();
				
				// ��������list
				newAisList += token[0] + COMMON_TOKEN +
						token[1] + COMMON_TOKEN +
						child + COMMON_TOKEN +
						token[2] + COMMON_TOKEN +
						token[3] + COMMON_TOKEN + "\n";
			}
		}
		
		// ��������
		FileUtils.WriteFile(genFileName, newAisList.getBytes());
	}

	// һ��ĺ������� 1��=24*60*60*1000=86400000����
	private static final long MILLIS_ONE_DAY = 86400000;

	/**
	 * ��ȡAIS�ڶ��������б�
	 * @param class_id
	 * INVALID��������
	 * @return
	 */
	public static List<ListItemMap> GetAisList(int functionId, String childColumn) {

		List<ListItemMap> list = new ArrayList<ListItemMap>();

		if (childColumn == null)
			return list;
		
		// ��ǰ��ȥ30��
		long today = System.currentTimeMillis();
		for (int i = 0; i < 30; ++i, today -= MILLIS_ONE_DAY) {

			String date = mDateFormater.format(new Date(today)) + "/";

			GenAisList(date);
			
			List<String> lines = ReadLinesWithEncoding(date + FILE_NAME_GEN_AIS_LIST, "UTF-8", true);

			String[] token;
			for (String line : lines) {
				token = line.split(TOKEN_SEP);
				
				if (token != null && token.length == 5) {
					// 20130607112901281,10,ʳƷ,�Ծ�һ,2013-6-7
					int column = ParseInt(token[1]);
					if (!IsAisColumnMatch(functionId, column))
						continue;
					
					String name = token[3];
					String fileName = name + ".ais";
					
					String child = token[2];
					
					// ��������(�ձ�ʾ��ȡ����ais)
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
			return Environment.getExternalStorageDirectory() + "/zknx/broadcast";
		else
			return Environment.getExternalStorageDirectory() + "/zknx/broadcast/" + GetCurrentTime(false);
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
	 */
	public static boolean ProcessBroadcastData() {
		
		// ���������ݰ汾�Ƿ��и���
		/*
		if (IsDataUpdated(FILE_NAME_ADDRESS)) {
			// ����ʡ���б�
			GenProvinceList();
		}
		
		if (IsDataUpdated(FILE_NAME_MARKETS)) {
			// ��ʡ�зָ��г��б�
			GenProvinceMarketList();
		}
		*/
		////////////////////////////////////////////////////
		
		// AIS�б�
		
		GenAisList();

		//  ��������Ϣ
		GenSupplyDemandList();

		// ������Ʒ��Ϣ
		List<ListItemMap> province = GetAddressList();
		
		if (province == null ||province.size() == 0)
			return false;
		
		// ѭ����ȡ���е��������г���������Ʒ��Ϣ�����ȴ������ݣ�
		for (ListItemMap item : province) {
			int addressId = item.getInt(KEY_ADDRESS_ID);
			if (addressId != INVALID_ID) {
				// ��ȡ�����г���Ϣ
				List<ListItemMap> markets = GetMarketListByArea(addressId);
				
				if (markets == null ||markets.size() == 0)
					continue;
				
				for (ListItemMap market : markets) {
					
					// ��ȡ���в�Ʒ��Ϣ
					int marketId = market.getInt(KEY_MARKET_ID);
					List<ListItemMap> products = GenProductList(marketId);
					
					Debug.Log("�����г���" + marketId);
					
					if (products == null ||products.size() == 0)
						continue;
					
					for (ListItemMap product : products) {
						String productId = product.getString(KEY_PRODUCT_ID);
						// ��ȡĳ��Ʒ���г���Ϣ
						Debug.Log("�����Ʒ��" + productId);
						GenMarketListByProduct(productId);
						// ��ȡĳ��Ʒ�ļ۸���Ϣ���������Ѿ�����
					}
				}
			}
		}

		// ���ʱ���
		if (ShouldUpdateData()) {
			return UpdateTodayData();
		}

		return false;
	}
	
	/**
	 * ��ȡ��ǰ�û��µ�����
	 * return�������ϴ����Ե�ʱ��
	 * TODO �������⣬��ʱ��Ĭ����Ϣ�ڲ����Դ��ڶ��ţ�ֻȡ���漸���ָ���
	 */
	private static final String FILE_STAMP_LAST_MESSAGE = "lastMessage.txt";
	public static String GetNewMessages() {

		int ret = Downloader.DownFile(URL_GET_MESSAGE + "?userid=" + UserMan.GetUserId(), DataFile("", true), FILE_NAME_NEW_MESSAGE);
		
		// ���ش���
		if (ret != 0)
			return null;

		List<String> lines = ReadLines(FILE_NAME_NEW_MESSAGE, true);
		
		if (lines.size() == 0)
			return null;

		// ��ȡ���һ�е�����
		// dengyong,test,ceshishij,0,2013-7-23 20:45:15;
		String lastLine = lines.get(lines.size() - 1);
		String token[] = lastLine.split(COMMON_TOKEN);
		
		if (token == null ||
			token.length < 5) {
			Debug.Log("��Ϣ��ʽ����");
			return null;
		}

		// ���һ���ָ�����ʱ��
		String time = token[token.length - 1];
		
		String message = null;
		
		List<String> lastTime = ReadLines(FILE_STAMP_LAST_MESSAGE, true);
		if (lastTime == null ||
			lastTime.size() == 0 ||
			!lastTime.get(0).equals(time)) {
			
			message = token[0] + "��" + token[2];
		}

		FileUtils.WriteText(DataFile(FILE_STAMP_LAST_MESSAGE, true), time);

		return message;
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
	private static final String TIMEID_FORMAT = "yyyyMMddhhmmss";
	private static final SimpleDateFormat mTimeFormater = new SimpleDateFormat(TIME_FORMAT, Locale.CHINA);
	private static final SimpleDateFormat mDateFormater = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
	private static final SimpleDateFormat mTimeIdFormater = new SimpleDateFormat(TIMEID_FORMAT);
	
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
	 * ��ȡʱ��id
	 * @return
	 */
	private static String GetCurrentTimeId() {
		return mTimeIdFormater.format(new java.util.Date());
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
		String params;
		try {
			params = "type=" + info.type + 
				"&title=" + URLEncoder.encode(info.title, "UTF-8") + 
				"&userid=" + UserMan.GetUserId() + 
				"&addressid=" + /*TODO ���������޵�ַid UserMan.GetUserAddressId()*/ "" +
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
			Debug.Log("URL�������");
			return false;
		}
		
		String ret = Downloader.PostUrl(URL_SERVER + URL_POST_SUPPLY_DEMAND_INFO, params);

		if (ret.equals("TRUE"))
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

		if (ret.equals("TRUE")) {
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
		// TODO ר��Ŀ¼
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
        	FileUtils.WriteGB2312Text(false, LOCAL_QUESTION, questionLines);
        }
	}
	
	public static List<ListItemMap> GetExpertAnwserList(String expertId) {
		List<ListItemMap> list = new ArrayList<ListItemMap>();
		
		// TODO
		// ��ȡ���������б�
		List<String> lines = ReadLines(/*LOCAL_QUESTION*/"expert/" + expertId + "/anwsers.txt");
		
		for (String line : lines) {
        	// id,����
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
	 * ������Գɼ�
	 * @param title
	 * @param resultPoint
	 */
	public static void SaveGrade(String aisId, int resultPoint) {
		String time = GetCurrentTimeId();
		String line = time + COMMON_TOKEN + aisId + COMMON_TOKEN + resultPoint;

		String fileName = DataMan.DataFile(FILE_NAME_GRADE, true);
		// ����һ������
		FileUtils.AppendLine(fileName, line);
	}
	
	/**
	 * �ϴ��ɼ�
	 * @return
	 */
	public static String PostGrade() {
		String filePathName = DataFile(FILE_NAME_GRADE);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		//user_id���û�id��
		//time��ʱ�䣻
		//title���μ����⣻
		//grade���ɼ�

		params.add(new BasicNameValuePair("userid", UserMan.GetUserId()));
		params.add(new BasicNameValuePair("filename", FILE_NAME_GRADE));

		String ret = Downloader.PostFile(URL_POST_GRADE, params, filePathName);
		
		if ("TRUE".equals(ret))
			FileUtils.DeleteFile(filePathName);
		
		return ret;
	}

	/**
	 * ��ȡ�ɼ�
	 * @param title
	 * @return
	 */
	public static String GetGrades(String aisId) {
		String grades = "";
		String user = UserMan.GetUserId();
		
		// ��������(���ϴ�)
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
				grades += time + "\t������" + grade + "\n";
			}
		}

		// ��ʷ����
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
				grades += time + "\t������" + grade + "\n";
			}
		}
		
		return grades;
	}
}
