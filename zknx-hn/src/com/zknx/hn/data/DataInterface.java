package com.zknx.hn.data;

public class DataInterface {

	// TODO interface 服务器地址，接口
	//public static final String URL_SEVER = "http://www.yczjxt.com.cn/appfora/";
	public static final String URL_SEVER = "http://218.106.254.101:8077/";
	public static final String URL_GET_USER_INFO = URL_SEVER + "getuserinfo.asp";
	// TODO 语言对讲服务器地址
	public final static String RTMP_SERVER = "rtmp://192.168.0.101/zknx-hn";
	
	// 省级地址列表文件名
	protected static final String FILE_NAME_ADDRESS_PROVINCE = "province.txt";
	// 市场（所有地区）列表文件名
	protected static final String FILE_NAME_MARKETS = "markets.txt";
	// 产品（所有市场）列表文件名
	protected static final String FILE_NAME_PRODUCTS = "products.txt";
	// 产品名字
	protected static final String FILE_NAME_COMMODITY = "commodity.txt";
	// 自选产品列表
	protected static final String FILE_NAME_MY_PRODUCTS = "my_products.txt";
	// 产品分类列表文件名
	protected static final String FILE_NAME_PRODUCT_CLASS = "product_class.txt";
	// 供求信息文件名
	protected static final String FILE_NAME_SUPPLY_DEMAND_INFO = "supply_demand_info.txt";
	// 我的商友文件名
	protected static final String FILE_NAME_MY_FRIEND = "my_friend.txt";
	// 所有商友留言文件名
	protected static final String FILE_NAME_MY_GROUP_MESSAGE = "my_group_message.txt";
	// ais列表文件名
	protected static final String FILE_NAME_AIS_LIST = "ais_file_list.txt";
	// ais分类列表文件名
	protected static final String FILE_NAME_AIS_CLASS = "ais_class.txt";
	// 获取ais文件接口
	protected static final String AIS_SURFIX = ".ais"; // ais 文件后缀名
	protected static final String AIS_FOLDER = "ais//"; // ais 文件存放文件夹（数据文件夹下）
	
	// XXX 数据文件的MD5校验值（假设这个文件传输没有问题）
	protected static final String MD5_FILE_NAME = "md5.txt";
	
	// 获取历史价格信息的天数
	public static final int HISTORY_PRICE_DAYS = 15;
	
	// 商友专业id
	// 种植大户
	public static final int MAJOR_ID_RICH_PLANTER = 0;
	// 种植大户
	public static final int MAJOR_ID_RICH_CULTURISTS =1;
	// 种植大户
	public static final int MAJOR_ID_MIDDLEMAN = 2;
	// 种植大户
	public static final int MAJOR_ID_COOPERATION = 3;
	// 专业名字
	protected static final String[] MAJOR = {"种植大户", "养殖大户", "经纪人", "合作社"};
	
	// 分隔符
	protected static String TOKEN_SEP = ",";
	
	// post留言的url
	// http://218.106.254.101:8077/post_message.aspx?user=linshi&friend=liu&message=来电话了。
	protected static final String URL_POST_MESSAGE = "post_message.aspx";
	// TODO interface post专家提问的url
	protected static final String URL_ASK_EXPERT = "ask_expert.asp";
	
	// Ais解析
	public static final char AIS_TOKEN = '\\';
	// Ais字体
	public static final char AIS_TOKEN_FONT  = 'F';
	// Ais图片
	public static final char AIS_TOKEN_IMAGE = 'P';
	// Ais视频
	public static final char AIS_TOKEN_VIDEO = 'V';
	// Ais音频
	public static final char AIS_TOKEN_AUDIO = 'M';
	
	/**
	 * market是否匹配（是否属当前区域）
	 * @param address_id
	 * @param address_id_parsed
	 * @return
	 */
	protected static boolean AddressMatch(int address_id, int address_id_parsed) {
		// TODO interface 地址编号分析，是否属于当前区域
		return (address_id == address_id_parsed);
	}
	
	/**
	 * 产品是否匹配产品分类
	 * @param product_class_id
	 * @param product_id
	 * @return
	 */
	protected static boolean ProductClassMatch(int product_class_id, int product_id) {
		// TODO interface 产品id定义，算法判断前几位包含

		int max_product_id = (product_class_id + 1) * 1000;
		int min_product_id = product_class_id * 1000;

		return (product_id > min_product_id && product_id < max_product_id);
	}

	/**
	 * 是否供应或者需求
	 * @param supply_demand_id
	 * @return
	 */
	protected static boolean IsSupply(int supply_demand_id) {
		// TODO interface 根据首位编码判断（首位是0则是供应，首位是1则是需求）
		// 大于或者小于编码位数（编码位数需固定）
		return (supply_demand_id > 100000);
	}

	/**
	 * 从产品id中界面产品分类
	 * @return
	 */
	protected static int ProductClassDecode(int product_id) {
		return (product_id / 1000);
	}

	/**
	 * 检查ais分类是否属于当前功能
	 * TODO interface 待实现 检查ais分类是否属于当前功能
	 * @param function_id
	 * @param ais_class_id
	 * @return
	 */
	protected static boolean AisClassMatch(int function_id, int ais_class_id) {
		// 检查前几位？
		return true;
	}
	
	/**
	 * 检查ais子分类是否属于当前分类
	 * TODO interface 待实现 检查ais子分类是否属于当前分类
	 * @param ais_class_id
	 * @param ais_sub_class_id
	 * @return
	 */
	protected static boolean AisSubClassMatch(int ais_class_id, int ais_sub_class_id) {
		// 检查前几位？
		return true;
	}
	
	/**
	 * TODO 从用户名分享专业id
	 * @param userId
	 * @return
	 */
	protected static int GetMajor(String userId) {
		return -1;
	}
}
