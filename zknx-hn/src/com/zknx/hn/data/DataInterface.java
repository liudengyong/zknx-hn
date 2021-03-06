package com.zknx.hn.data;

public class DataInterface {

	// TODO interface 服务器地址，接口
	//public static final String URL_SERVER = "http://www.yczjxt.com.cn/appfora/";
	public static final String URL_SERVER = "http://218.106.254.101:8077/";
	public static final String URL_LOGIN = URL_SERVER + "login.aspx";
	public static final String URL_POST_GRADE = URL_SERVER + "post_grade.aspx";
	public static final String URLT_GET_FRIENDS = "get_my_friend.aspx";
	public final static String RTMP_SERVER = "rtmp://192.168.0.101/zknx-hn";
	
	// 省级地址列表文件名
	protected static final String FILE_NAME_ADDRESS = "address.txt";
	protected static final String FILE_NAME_ADDRESS_PROVINCE = "province.txt";
	
	// 市场（所有地区）列表文件名
	protected static final String FILE_NAME_MARKETS = "market.txt";
	// 产品（所有市场）列表文件名
	protected static final String FILE_NAME_PRODUCTS_PRICE = "commodity_price.txt";
	// 产品名字
	protected static final String FILE_NAME_COMMODITY = "commodity.txt";
	// 自选产品列表
	protected static final String FILE_NAME_MY_PRODUCTS = "my_products.txt";
	// 产品分类列表文件名
	protected static final String FILE_NAME_PRODUCT_CLASS = "product_class.txt";
	// 供求信息文件名
	protected static final String FILE_NAME_SUPPLY_DEMAND_INFO = "tradeInfo.txt";
	// 我的商友文件名
	//protected static final String FILE_NAME_MY_FRIEND = "my_friend.txt";
	// 用户列表
	protected static final String FILE_NAME_USERS = "users.txt";
	// 商友列表
	protected static final String FILE_NAME_FRIEND = "friends.txt";
	// 所有商友留言文件名
	protected static final String FILE_NAME_MY_GROUP_MESSAGE = "my_group_message.txt";
	// 专家问答
	protected static final String FILE_NAME_EXPERTS = "experts.txt";
	// 当前用户本地分数（待同步）
	protected static final String FILE_NAME_GRADE = "grade.txt";
	// 所有用户历史分数
	protected static final String FILE_NAME_GRADES = "grades.txt";
	// ais列表文件名
	protected static final String FILE_NAME_AIS_LIST = "ais_file_list.txt";
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
	// 获取留言
	protected static final String URL_GET_MESSAGE = "get_message.aspx";
	// 新留言文件
	protected static final String FILE_NAME_NEW_MESSAGE = "new_messages.txt";
	// 专家提问的url
	protected static final String URL_ASK_EXPERT = URL_SERVER + "ask_question.aspx";
	// post供求信息
	// http://218.106.254.101:8077/post_supply_demand_info.aspx?type=0&title=供应西红柿&userid=linshi&addressid=06056&commodityid=0305002000&count=大量&price=5&unit=元/公斤&phonenumber=15941652887&place=辽宁锦州凌海市新庄子乡小马村范坨&linkman=刘春宇&remark=备注&validity=2013-06-24&publishdate=2013-05-25
	protected static final String URL_POST_SUPPLY_DEMAND_INFO = "post_supply_demand_info.aspx";
	
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
	// Ais问卷答案长度
	public static final char AIS_TOKEN_COURSE_ANSWER = 'C';
	// Ais问卷分数的长度
	public static final char AIS_TOKEN_COURSE_GRADE = 'S';
	// Ais问卷答案解析
	public static final char AIS_TOKEN_COURSE_NOTE = 'K';
	// 村务公开表格
	public static final char AIS_TOKEN_TABLE = 'T';
	
	/**
	 * market是否匹配（是否属当前区域）
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
	 * 产品是否匹配产品分类
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
	 * 是否供应或者需求
	 * @param supply_demand_id
	 * @return
	 */
	protected static boolean IsSupply(int supply_demand_id) {
		// interface 根据首位编码判断（首位是0则是供应，首位是1则是需求）
		// 大于或者小于编码位数（编码位数需固定）
		//return (supply_demand_id > 100000);
		
		return (supply_demand_id == 0);
	}

	/**
	 * 从产品id中界面产品分类
	 * @return
	 */
	protected static int ProductClassDecode(int product_id) {
		return (product_id / 1000);
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
	
	public static String GetExpertMajor(int majorId) {
		switch (majorId) {
		case 1:
			return "种植专家";
		case 2:
			return "养殖专家";
		case 3:
			return "惠农专家";
		}
		
		return "种植专家";// TODO 未知专业
	}
}
