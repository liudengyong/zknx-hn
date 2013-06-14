package com.zknx.hn.data;

public class DataInterface {

	// TODO interface ��������ַ���ӿ�
	//public static final String URL_SERVER = "http://www.yczjxt.com.cn/appfora/";
	public static final String URL_SERVER = "http://218.106.254.101:8077/";
	public static final String URL_GET_USER_INFO = URL_SERVER + "getuserinfo.asp";
	// TODO ���ԶԽ���������ַ
	public final static String RTMP_SERVER = "rtmp://192.168.0.101/zknx-hn";
	
	// ʡ����ַ�б��ļ���
	protected static final String FILE_NAME_ADDRESS = "address.txt";
	protected static final String FILE_NAME_ADDRESS_PROVINCE = "province.txt";
	
	// �г������е������б��ļ���
	protected static final String FILE_NAME_MARKETS = "market.txt";
	// ��Ʒ�������г����б��ļ���
	protected static final String FILE_NAME_PRODUCTS = "commodity_price.txt";
	// ��Ʒ����
	protected static final String FILE_NAME_COMMODITY = "commodity.txt";
	// ��ѡ��Ʒ�б�
	protected static final String FILE_NAME_MY_PRODUCTS = "my_products.txt";
	// ��Ʒ�����б��ļ���
	protected static final String FILE_NAME_PRODUCT_CLASS = "product_class.txt";
	// ������Ϣ�ļ���
	protected static final String FILE_NAME_SUPPLY_DEMAND_INFO = "tradeInfo.txt";
	// �ҵ������ļ���
	protected static final String FILE_NAME_MY_FRIEND = "my_friend.txt";
	// �������������ļ���
	protected static final String FILE_NAME_MY_GROUP_MESSAGE = "my_group_message.txt";
	// ais�б��ļ���
	protected static final String FILE_NAME_AIS_LIST = "ais_file_list.txt";
	// ais�����б��ļ���
	protected static final String FILE_NAME_AIS_CLASS = "ais_class.txt";
	// ��ȡais�ļ��ӿ�
	protected static final String AIS_SURFIX = ".ais"; // ais �ļ���׺��
	protected static final String AIS_FOLDER = "ais//"; // ais �ļ�����ļ��У������ļ����£�
	
	// XXX �����ļ���MD5У��ֵ����������ļ�����û�����⣩
	protected static final String MD5_FILE_NAME = "md5.txt";
	
	// ��ȡ��ʷ�۸���Ϣ������
	public static final int HISTORY_PRICE_DAYS = 15;
	
	// ����רҵid
	// ��ֲ��
	public static final int MAJOR_ID_RICH_PLANTER = 0;
	// ��ֲ��
	public static final int MAJOR_ID_RICH_CULTURISTS =1;
	// ��ֲ��
	public static final int MAJOR_ID_MIDDLEMAN = 2;
	// ��ֲ��
	public static final int MAJOR_ID_COOPERATION = 3;
	// רҵ����
	protected static final String[] MAJOR = {"��ֲ��", "��ֳ��", "������", "������"};
	
	// �ָ���
	protected static String TOKEN_SEP = ",";
	
	// post���Ե�url
	// http://218.106.254.101:8077/post_message.aspx?user=linshi&friend=liu&message=���绰�ˡ�
	protected static final String URL_POST_MESSAGE = "post_message.aspx";
	// ��ȡ����
	protected static final String URL_GET_MESSAGE = "get_message.aspx";
	// �������ļ�
	protected static final String FILE_NAME_NEW_MESSAGE = "new_messages.txt";
	// TODO interface postר�����ʵ�url
	protected static final String URL_ASK_EXPERT = "ask_expert.asp";
	// post������Ϣ
	// http://218.106.254.101:8077/post_supply_demand_info.aspx?type=0&title=��Ӧ������&userid=linshi&addressid=06056&commodityid=0305002000&count=����&price=5&unit=Ԫ/����&phonenumber=15941652887&place=���������躣����ׯ����С��巶��&linkman=������&remark=��ע&validity=2013-06-24&publishdate=2013-05-25
	protected static final String URL_POST_SUPPLY_DEMAND_INFO = "post_supply_demand_info.aspx";
	
	// Ais����
	public static final char AIS_TOKEN = '\\';
	// Ais����
	public static final char AIS_TOKEN_FONT  = 'F';
	// AisͼƬ
	public static final char AIS_TOKEN_IMAGE = 'P';
	// Ais��Ƶ
	public static final char AIS_TOKEN_VIDEO = 'V';
	// Ais��Ƶ
	public static final char AIS_TOKEN_AUDIO = 'M';
	
	/**
	 * market�Ƿ�ƥ�䣨�Ƿ�����ǰ����
	 * @param address_id
	 * @param address_id_parsed
	 * @return
	 */
	protected static boolean AddressMatch(int address_id, int address_id_parsed) {
		// TODO interface ��ַ��ŷ������Ƿ����ڵ�ǰ����
		int max = (address_id + 1) * 1000;;
		int min = address_id * 1000;
		
		return (address_id_parsed > min  && address_id_parsed < max);
	}
	
	/**
	 * ��Ʒ�Ƿ�ƥ���Ʒ����
	 * @param product_class_id
	 * @param product_id
	 * @return
	 */
	protected static boolean ProductClassMatch(int product_class_id, int product_id) {
		// TODO interface ��Ʒid���壬�㷨�ж�ǰ��λ����

		int max_product_id = (product_class_id + 1) * 1000;
		int min_product_id = product_class_id * 1000;

		return (product_id > min_product_id && product_id < max_product_id);
	}

	/**
	 * �Ƿ�Ӧ��������
	 * @param supply_demand_id
	 * @return
	 */
	protected static boolean IsSupply(int supply_demand_id) {
		// TODO interface ������λ�����жϣ���λ��0���ǹ�Ӧ����λ��1��������
		// ���ڻ���С�ڱ���λ��������λ����̶���
		return (supply_demand_id > 100000);
	}

	/**
	 * �Ӳ�Ʒid�н����Ʒ����
	 * @return
	 */
	protected static int ProductClassDecode(int product_id) {
		return (product_id / 1000);
	}

	/**
	 * ���ais�����Ƿ����ڵ�ǰ����
	 * TODO interface ��ʵ�� ���ais�����Ƿ����ڵ�ǰ����
	 * @param function_id
	 * @param ais_class_id
	 * @return
	 */
	protected static boolean AisClassMatch(int function_id, int ais_class_id) {
		// ���ǰ��λ��
		return true;
	}
	
	/**
	 * ���ais�ӷ����Ƿ����ڵ�ǰ����
	 * TODO interface ��ʵ�� ���ais�ӷ����Ƿ����ڵ�ǰ����
	 * @param ais_class_id
	 * @param ais_sub_class_id
	 * @return
	 */
	protected static boolean AisSubClassMatch(int ais_class_id, int ais_sub_class_id) {
		// ���ǰ��λ��
		return true;
	}
	
	/**
	 * TODO ���û�������רҵid
	 * @param userId
	 * @return
	 */
	protected static int GetMajor(String userId) {
		return -1;
	}
}
