package com.zknx.hn.data;

public class DataInterface {

	// TODO interface ��������ַ���ӿ�
	//public static final String URL_SERVER = "http://www.yczjxt.com.cn/appfora/";
	public static final String URL_SERVER = "http://218.106.254.101:8077/";
	public static final String URL_LOGIN = URL_SERVER + "login.aspx";
	public static final String URL_POST_GRADE = URL_SERVER + "post_grade.aspx";
	public static final String URLT_GET_FRIENDS = URL_SERVER + "get_my_friend.aspx";
	public final static String RTMP_SERVER = "rtmp://192.168.0.101/zknx-hn";
	
	// ʡ����ַ�б��ļ���
	protected static final String FILE_NAME_ADDRESS = "address.txt";
	protected static final String FILE_NAME_ADDRESS_PROVINCE = "province.txt";
	
	// �г������е������б��ļ���
	protected static final String FILE_NAME_MARKETS = "market.txt";
	// ��Ʒ�������г����б��ļ���
	protected static final String FILE_NAME_PRODUCTS_PRICE = "commodity_price.txt";
	// ��Ʒ����
	protected static final String FILE_NAME_COMMODITY = "commodity.txt";
	// ��ѡ��Ʒ�б�
	protected static final String FILE_NAME_MY_PRODUCTS = "my_products.txt";
	// ��Ʒ�����б��ļ���
	protected static final String FILE_NAME_PRODUCT_CLASS = "product_class.txt";
	// ������Ϣ�ļ���
	protected static final String FILE_NAME_SUPPLY_DEMAND_INFO = "tradeInfo.txt";
	// �ҵ������ļ���
	//protected static final String FILE_NAME_MY_FRIEND = "my_friend.txt";
	// �û��б�
	protected static final String FILE_NAME_USERS = "users.txt";
	// �����б�
	protected static final String FILE_NAME_FRIEND = "friends.txt";
	// �������������ļ���
	protected static final String FILE_NAME_MY_GROUP_MESSAGE = "my_group_message.txt";
	// ר���ʴ�
	protected static final String FILE_NAME_EXPERTS = "experts.txt";
	// ��ǰ�û����ط�������ͬ����
	protected static final String FILE_NAME_GRADE = "grade.txt";
	// �����û���ʷ����
	protected static final String FILE_NAME_GRADES = "grades.txt";
	// ais�б��ļ���
	protected static final String FILE_NAME_AIS_LIST = "ais_file_list.txt";
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
	// ר�����ʵ�url
	protected static final String URL_ASK_EXPERT = URL_SERVER + "ask_question.aspx";
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
	// Ais�ʾ�𰸳���
	public static final char AIS_TOKEN_COURSE_ANSWER = 'C';
	// Ais�ʾ�����ĳ���
	public static final char AIS_TOKEN_COURSE_GRADE = 'S';
	// Ais�ʾ�𰸽���
	public static final char AIS_TOKEN_COURSE_NOTE = 'K';
	
	/**
	 * market�Ƿ�ƥ�䣨�Ƿ�����ǰ����
	 * @param address_id
	 * @param address_id_parsed
	 * @return
	 */
	protected static boolean AddressMatch(int address_id, int address_id_parsed) {
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
	protected static boolean ProductClassMatch(String product_class_id, String product_id) {
		
		if (product_class_id == null ||
			product_id == null)
			return false;

		/*
		int max_product_id = (product_class_id + 1) * 1000;
		int min_product_id = product_class_id * 1000;

		return (product_id > min_product_id && product_id < max_product_id);
		*/
		
		return product_id.startsWith(product_class_id);
	}

	/**
	 * �Ƿ�Ӧ��������
	 * @param supply_demand_id
	 * @return
	 */
	protected static boolean IsSupply(int supply_demand_id) {
		// interface ������λ�����жϣ���λ��0���ǹ�Ӧ����λ��1��������
		// ���ڻ���С�ڱ���λ��������λ����̶���
		//return (supply_demand_id > 100000);
		
		return (supply_demand_id == 0);
	}

	/**
	 * �Ӳ�Ʒid�н����Ʒ����
	 * @return
	 */
	protected static int ProductClassDecode(int product_id) {
		return (product_id / 1000);
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
	
	public static String GetExpertMajor(int majorId) {
		switch (majorId) {
		case 1:
			return "��ֲר��";
		case 2:
			return "��ֳר��";
		case 3:
			return "��ũר��";
		}
		
		return "��ֲר��";// TODO δ֪רҵ
	}
}
