package com.zknx.hn.functions.ais;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.zknx.hn.common.Debug;
import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;

public class AisDoc {

	public enum ItemType {TEXT, CHEKCBOX, IMAGE, VIDEO, AUDIO}

	// Ais文件头的字节数
	private static final int AIS_HEADER_SIZE = 164;
	// 整形大小
	private static final int INT_SIZE = 4;
	// 字体结构大小
	private static final int FONT_STRUCT_SIZE = 52;
	// 村务公开表格HTML
	private static final String AIS_TABLE_FILENAME = "table.html";

	/*
	typedef struct tagTextFont
	{
		COLORREF color;//4
		char szFontName[32];//32
		BOOL bBold;//4
		BOOL bItalic;//4
		BOOL bUnderline;//4
		DWORD dwSize;//4
	} TEXTFONT;
	长度：52字节

typedef struct tagAisHeader
{
	char szType[5];     //AIS标志
	//char dwFormat;   //包含的内容种类,目前末用
	char fileId[18];  //文件唯一ID
	BYTE bColumn;     //栏目ID
	char child_column[20]; //子栏目名
	BYTE bSeazon;
	//char szReserved1[3];
	char bColumn_child[4];
	char dwAddress[9];  //所属地区ID,不区分地区时为0
	char szMajor[32];   //所属专业ID,每个BIT对应一个专业,不区分专业时为0
	char szFileDate[12];//文件发布日期,到期的文件才会发布
	char szValidDate[12];//有郊日期,过了日期不显示
	char szTitle[50];   //主题词
	//char szReserved2[1];
}TAisHeader;

	长度：140字节
	*/
	
	/*
BYTE bColumn  

1	自选产品
2	市场行情
3	供求信息
4	我的商圈
5	农业技术
6	专家指导
7	我的供求
8	科学施肥
9	时政新闻
10	精选课件
11	先锋党员
12	典型模范
13	致富经验
14	快乐农家
15	法律法规
16	惠农政策

增加“政务应用”一级栏目
17  党务公开
18  国家政策
19  村务公开
20  财务公开
21  计生公开
22  办事公开
23  办事指南
24  用工信息

char child_column

CString column_child1[]={""};
CString column_child2[]={""};
CString column_child3[]={""};
CString column_child4[]={""};
CString column_child5[]={"粮食","蔬菜","禽畜","水产","药材","经济作物","水果","其他"};
CString column_child6[]={"种植", "养殖", "惠农"};
CString column_child7[]={""};
CString column_child8[]={""};
CString column_child9[]={"时政新闻", "农经要闻", "理论学习"};
CString column_child10[]={""};
CString column_child11[]={""};
CString column_child12[]={""};
CString column_child13[]={"粮食","蔬菜","禽畜","水产","药材","经济作物","水果","其他"};
CString column_child14[]={"历史记忆","精选笑话","厨艺学习","生活窍门"};
CString column_child15[]={"基本法律","三农法律"};
CString column_child16[]={"生产类","生活类","医疗类","教育类"};
	 */
	
	public static int COLUMN_COURSE = 12; // 课件column

	/**
	 * Ais文件头
	 * @author Dengyong
	 *
	 */
	public class AisHeader {
		String type;   //AIS标志
		String fileId; //文件唯一ID
		byte column;   //栏目ID
		String childColumn; // 子栏目名
		byte seazon; // 季节
		String address;  //所属地区ID,不区分地区时为0
		String major;    //所属专业ID,每个BIT对应一个专业,不区分专业时为0
		String fileDate; //文件发布日期,到期的文件才会发布
		String validDate;//有郊日期,过了日期不显示
		String title;    //主题词
		public String getTitle() {
			return title;
		}
		public String getAisId() {
			return fileId;
		}
	}

	// Ais头
	private AisHeader mHeader;
	// Item树
	private List<List<AisItem>> mItemTree;
	// 音频Item
	private AisItem mAudioItem;
	// 视频Item
	private AisItem mVideoItem;
	// 图像Items
	private AisItem[] mImageItems;
	// 用于ais内图片计数
	//private int mImageIndex;
	// 是否是表格ais
	private boolean mConmContainsTable = false;

	/**
	 * 通过ais_id构造AisDoc
	 * @param ais_id
	 */
	public AisDoc(Context context, String aisFileName, boolean parseHeader, String strDate) {
		parseAisDoc(context, aisFileName, parseHeader, strDate);
	}

	/**
	 * 获取标题
	 * @return
	 */
	public String getTitle() {
		return (mHeader != null) ? mHeader.title : "标题";
	}
	
	/**
	 * 获取ais id
	 * @return
	 */
	public String getAisId() {
		return (mHeader != null) ? mHeader.fileId : "";
	}
	
	/**
	 * 获取ais child id
	 * @return
	 */
	public String getAisChildColumn() {
		return (mHeader != null) ? mHeader.childColumn : "";
	}

	/**
	 * 获取Item树
	 * @return
	 */
	public List<List<AisItem>> getItemTree() {
		return mItemTree;
	}

	/**
	 * 定义AisItem
	 * @author Dengyong
	 *
	 */
	public static class AisItem {
		AisItem(ItemType _type, byte _data[]) {
			type = _type;
			data = _data;
		}
		
		AisItem(ItemType _type, String _fileName) {
			type = _type;
			data = null;
			fileName = _fileName;
		}

		ItemType type; // 类型
		byte data[]; // 数据
		String fileName;
		
		/**
		 * 保存文字byte数组
		 * @author Dengyong
		 *
		 */
		static class ByteArray {
			byte[] data;
			int length;

			ByteArray(int size) {
				if (size < 1)
					size = 16; // 默认16字节
				data = new byte[size];
				length = 0;
			}

			void append(byte v) {
				// 长度不够时，增加一倍
				if (length == data.length) {
					byte[] t = new byte[length + length];
					for (int i = 0 ; i < length ; ++i)
						t[i] = data[i];
					data = t;
				}

				data[length] = v;
				++length;
			}
		}
	}

	/**
	 * 解析ais成list
	 * @param ais_id
	 * @return
	 */
	private boolean parseAisDoc(Context context, String aisFileName, boolean parseHeader, String strDate) {
		if (aisFileName == null || aisFileName.length() == 0) {
			Debug.Log("严重错误：parseAisDoc，ais_file为空");
			return false;
		}

		if (strDate == null)
			strDate = "";

		String filePathName = DataMan.DataFile(strDate + aisFileName, true);
		
		if (!FileUtils.IsFileExist(filePathName)) {
			Debug.Log("Ais文件没找到：" + filePathName);
			return false;
		}
		
		Debug.Log("解析ais文件：" + filePathName);
		
		// 默认是图文ais
		mConmContainsTable = false;
		
		try {
			
			FileInputStream file = new FileInputStream(filePathName);

			// 解析Ais头
			mHeader = parseAisHeader(file);
			
			if (parseHeader)
				return (mHeader != null);
			
			//mImageIndex = 0;
			
			// 生成行树
			mItemTree = new ArrayList<List<AisItem>>();
			// 默认生成一个空行
			List<AisItem> row = new ArrayList<AisItem>();

			int v;
			AisItem.ByteArray textBytes = new AisItem.ByteArray(0);
			while ((v = file.read()) >= 0) {
				if (v != DataMan.AIS_TOKEN) {
					// 字符处理
					textBytes.append((byte)v);
					//textBytes.append((byte)file.read());
					continue;
				}

				// 如果文字不为空（第一次遍历为空），添加上次问题到item树当前行
				if (textBytes.length > 0) {
					row.add(new AisItem(ItemType.TEXT, new String(textBytes.data, 0, textBytes.length, "GBK").getBytes("GBK")));
					Debug.Log("文字=" + new String(textBytes.data, 0, textBytes.length, "GB2312"));
					textBytes = new AisItem.ByteArray(0);
				}

				// 读取控件标识
				v = file.read();

				// 多媒体处理（包括字体）
				int length = 0;
				// 字体
				if (v == DataMan.AIS_TOKEN_FONT)
					length = FONT_STRUCT_SIZE;
				else
					length = readInt(file, INT_SIZE);

				if (length <= 0) {
					Debug.Log("严重错误：Ais解析，长度错误！");
					return false;
				}

				//Debug.Log("item长度：" + length);

				switch (v) {
				case DataMan.AIS_TOKEN:
					Debug.Log("TODO:文字");
					file.skip(length);
					break;
				case DataMan.AIS_TOKEN_FONT:
					Debug.Log("TODO：字体结构");
					file.skip(length);
					break;
				case DataMan.AIS_TOKEN_COURSE_ANSWER:
				case DataMan.AIS_TOKEN_COURSE_GRADE:
				case DataMan.AIS_TOKEN_COURSE_NOTE:
					if (isCourse()) {
						addQuestion(v, readAisData(context, file, length));
					} else {
						Debug.Log("Ais结构错误：非试卷不应有答案结构，" + mHeader.column);
						byte[] data = new byte[length];
						if (length == file.read(data))
							row.add(new AisItem(ItemType.TEXT, new String(data, 0, length, "GBK").getBytes("GBK")));
						else
							Debug.Log("Ais解析错误 length == file.read(data)");
					}
					break;
				case DataMan.AIS_TOKEN_IMAGE:
					if (isCourse())
						addQuestion(v, readAisData(context, file, length));
					else {
						if (mImageItems == null)
							mImageItems = new AisItem[3];
						
						// TODO 替换成readAisToFile
						//String imageFileName = DataMan.DataFile("ais_image_" + mImageIndex + ".jpg", true);
	
						// 换行
						AisItem imageItem = new AisItem(ItemType.IMAGE, readAisData(context, file, length));
						
						if (mImageItems[0] == null)
							mImageItems[0] = imageItem;
						else if (mImageItems[1] == null)
							mImageItems[1] = imageItem;
						else if (mImageItems[2] == null)
							mImageItems[2] = imageItem;
						else
							Debug.Log("解析Ais警告：多余三个图片Item");
					}
					break;
				case DataMan.AIS_TOKEN_VIDEO:
					// 只有一个视频Item
					if (mVideoItem == null) {
						String fileName = DataMan.DataFile("ais_video.rmvb", true);
						mVideoItem = new AisItem(ItemType.VIDEO, readAisToFile(context, fileName, file, length));
					} else
						Debug.Log("解析Ais警告：多余一个视频Item");
					break;
				case DataMan.AIS_TOKEN_AUDIO:
					// 只有一个音频Item
					if (mAudioItem == null) {
						// TODO 替换成readAisToFile
						//String fileName = DataMan.DataFile("ais_audio.mp3", true);
						mAudioItem = new AisItem(ItemType.AUDIO, readAisData(context, file, length));
					} else
						Debug.Log("解析Ais警告：多余一个音频Item");
					break;
				case DataMan.AIS_TOKEN_TABLE:
					mConmContainsTable = true;
					String fileName = getTableHtmlFile();
					readAisToFile(context, fileName, file, length);
					break;
				default:
					Debug.Log("解析AIS错误，无此标志：" + v);
					file.skip(length);
					break;
				}
			}
			
			// 如果不是空行，添加最后一行
			if (row != null && row.size() > 0)
				mItemTree.add(row);

			file.close();
			
		} catch (IOException e) {
			Debug.Log("严重错误：ais解析异常，" + e.getMessage());
            return false;
		}
		
		Debug.Log("解析ais文件成功：" + filePathName);
		
		return true;
	}
	
	/**
	 * 保存AIS解析数据到临时file
	 * @return
	 * 正确保存数据后返回保存的文件名，否则返回null
	 * @throws IOException 
	 */
	String readAisToFile(Context context, String fileName, FileInputStream aisFile, int length) throws IOException {
		int bufferLength = 1024 * 1024; // 1MB

		if (length < bufferLength)
			bufferLength = length;

		byte[] data;
		try {
			data = new byte[bufferLength];
		} catch (Throwable e) {
			if (context != null)
				Dialog.Toast(context, "解析AIS内存不足：" + (bufferLength / 1024) + "K");
			return null;
		}

		if (!FileUtils.IsFileExist(fileName))
			FileUtils.CreateFile(fileName);

        FileOutputStream fileWriter = new FileOutputStream(fileName);

		int leftByte = length;
		do {
			int readed;
			if (leftByte >= bufferLength)
				readed = aisFile.read(data);
			else
				readed = aisFile.read(data, 0, leftByte);

			if (bufferLength <= 0) {
				Debug.Log("readAisToFile严重错误：Ais解析，读取数据错误！");
				fileWriter.close();
				return null;
			}

			// 保存解析ais的数据
			fileWriter.write(data, 0, readed);

			leftByte -= readed;
		} while (leftByte > 0);
		
		fileWriter.close();
		
		// 保存成功
		return fileName;
	}
	
	/**
	 * 保存AIS解析数据到内存
	 * @return
	 * 正确解析数据后返回data数据，否则返回null
	 */
	byte[] readAisData(Context context, FileInputStream file, int length) throws IOException {
		byte[] data;
		try {
			data = new byte[length];
		} catch (Throwable e) {
			Debug.Log("严重错误：内存不足，parseAisDoc");
			if (context != null)
				Dialog.Toast(context, "解析AIS内存不足：" + (length / 1024) + "K");
			return null;
		}

		if (length != file.read(data)) {
			Debug.Log("严重错误：Ais解析，读取数据错误！");
			return null;
		}

		return data;
	}


	/**
	 * 添加答案到当前问题
	 */
	private class Question {
		Question(byte[] data) {
			//data为题干图片数据
			bitmapData = data;
		}

		byte[] bitmapData;
		int grade;
		byte[] answer;
		String note;
	}

	private List<Question> mQuestionList;

	/**
	 * 获取题目个数
	 * @return
	 */
	public int getQuestionCount() {
		return mQuestionList != null ? mQuestionList.size() : 0;
	}
	
	/**
	 * 获取题干图片
	 * @return
	 */
	public byte[] getQuestionBitmapData(int i) {
		if (mQuestionList == null || mQuestionList.size() < i)
			return null;
		
		byte[] data = mQuestionList.get(i).bitmapData;
		//return BitmapFactory.decodeByteArray(data, 0, data.length);
		return data;
	}
	
	/**
	 * 获取题目答案
	 * @return
	 */
	public String getQuestionAnswer(int i) {
		if (mQuestionList == null || mQuestionList.size() < i)
			return "";
		
		String rightAnwser = "";
		byte[] rightAnwserBytes = mQuestionList.get(i).answer;
		if (rightAnwserBytes != null) {
			for (byte a : rightAnwserBytes)
				// 答案长度一定，如果不是全部正确则会余0
				if (Character.isLetter(a))
					rightAnwser += (char)a;
		}
		
		return rightAnwser;
	}
	
	/**
	 * 获取题目分数
	 * @return
	 */
	public int getQuestionGrade(int i) {
		if (mQuestionList == null || mQuestionList.size() < i)
			return DataMan.INVALID_ID;
		
		return mQuestionList.get(i).grade;
	}
	
	/**
	 * 获取题目解析
	 * @return
	 */
	public String getQuestionNote(int i) {
		String anwser = "答案" + getQuestionAnswer(i) + "。";
		if (mQuestionList == null || mQuestionList.size() <= 0 ||
			mQuestionList.get(i).note == null)
			return anwser + "无";
		
		return anwser + mQuestionList.get(i).note;
	}
	
	/**
	 * 是否试题Ais
	 * @return
	 */
	public boolean isCourse() {
		return (mHeader != null) ? (mHeader.column == COLUMN_COURSE) : false;
	}
	
	// 是否村务公开表格
	public boolean isTable() {
		return mConmContainsTable;
	}

	/**
	 * 添加试题
	 * @param v
	 * @param data
	 * @throws IOException
	 */
	private void addQuestion(int v, byte[] data) throws IOException {
		if (!isCourse()) {
			throw new IOException("Ais结构错误：非试卷不应有答案结构，" + mHeader.column);
		}
		
		if (data == null) {
			throw new IOException("内存不足：Should hit in readAisData");
		}

		// 题干图片
		if (v == DataMan.AIS_TOKEN_IMAGE) {
			if (mQuestionList == null)
				mQuestionList = new ArrayList<Question>();
			
			mQuestionList.add(new Question(data));
			
			return;
		}
		
		if (mQuestionList == null) {
			throw new IOException("Ais结构错误：mQuestionList == null");
		}
		
		Question lastQuestion = mQuestionList.get(mQuestionList.size() - 1);

		switch (v) {
		// 添加分数
		case DataMan.AIS_TOKEN_COURSE_GRADE:
			lastQuestion.grade = DataMan.ParseInt(new String(data, 0, 2));
			if (lastQuestion.grade == -1)
				lastQuestion.grade = DataMan.ParseInt(new String(data, 0, 1));
			break;
		case DataMan.AIS_TOKEN_COURSE_ANSWER:
			if (data.length != 4) {
				Debug.Log("答案长度错误：" + data.length);
				return;
			}
			lastQuestion.answer = data;
			break;
		case DataMan.AIS_TOKEN_COURSE_NOTE:
			lastQuestion.note = readText(data);
			break;
		}
	}

	/**
	 * 解析ais头
	 * @param buffer
	 * @return
	 * @throws IOException 
	 */
	private AisHeader parseAisHeader(FileInputStream file) throws IOException {
		
		// 文件太短，格式错误
		byte[] buffer = new byte[AIS_HEADER_SIZE];
		if (file.read(buffer, 0, AIS_HEADER_SIZE) != AIS_HEADER_SIZE) {
			file.close();
			throw new IOException("Ais头严重错误：parseAisDoc，ais文件格式错误");
		}

		/*
	char szType[5];     //AIS标志
	char fileId[18];  //文件唯一ID
	BYTE bColumn;     //栏目ID
	char child_column[20]; //子栏目名
	BYTE bSeazon;
	char bColumn_child[4];
	char dwAddress[9];  //所属地区ID,不区分地区时为0
	char szMajor[32];   //所属专业ID,每个BIT对应一个专业,不区分专业时为0
	char szFileDate[12];//文件发布日期,到期的文件才会发布
	char szValidDate[12];//有郊日期,过了日期不显示
	char szTitle[50];   //主题词
		 */
		
		AisHeader header = new AisHeader();
		
		int offset = 0;
		int size;
		
		// 文件类型标识
		header.type = new String(buffer, offset, size = 5);
		offset += size;
		
		// 文件id（本来17位，但是头用吧8位表示）
		header.fileId = new String(buffer, offset, size = 17);
		offset += (size + 1);
		
		// 栏目id
		size = 1;
		header.column = buffer[offset];
		offset += size;

		// 子栏目名
		header.childColumn = readText(buffer, offset, size = 20);//new String(buffer, offset, size = 20);
		offset += size;
		
		// 季节
		size = 1;
		header.seazon = buffer[offset];
		offset += size;
		
		// 子栏目id 暂时没有用
		//header.childColumnId = new String(buffer, offset, size = 4);
		size = 4;
		offset += size;

		// 地区id
		header.address = new String(buffer, offset, size = 9);
		offset += size;
		
		// 专业id
		header.major = new String(buffer, offset, size = 32);
		offset += size;
		
		// 发布日期
		header.fileDate = new String(buffer, offset, size = 12);
		offset += size;
		
		// 有效期
		header.validDate = new String(buffer, offset, size = 12);
		offset += size;
		
		// 主题
		header.title = readText(buffer, offset, size = 50);

		return header;
	}
	
	/**
	 * 从buffer读取int
	 * @param buffer
	 * @param offset
	 * @param len
	 * @return
	 */
    private static int readInt(byte[] buffer, int offset, int len) {

        int value = 0;
        offset += len - 1;
        while (len > 0) {
            value = (value << 8 | (buffer[offset] & 0xFF));
            --offset;
            --len;
        }

        return value;
    }
    
    /**
     * 从文件读取int
     * @param file
     * @param len
     * @return
     * @throws IOException
     */
    private static int readInt(FileInputStream file, int len) throws IOException {
        byte[] buffer = new byte[len];
        file.read(buffer);
        return readInt(buffer, 0, len);
    }
    
    /**
	 * 从buffer读取String
	 * @param buffer
	 * @param offset
	 * @param len
	 * @return
	 */
    private static String readText(byte[] buffer) {
    	return readText(buffer, 0, buffer.length);
    }
    
    /**
   	 * 从buffer读取String
   	 * @param buffer
   	 * @param offset
   	 * @param len
   	 * @return
   	 */
       private static String readText(byte[] buffer, int offset, int len) {

       	try {

    		for (int i = 0; i < len ; ++i) {
    			if (buffer[offset + i] == 0) {
    				len = i; // 字符串实际长度
    				break; // 查找字符串结尾标志
    			}
    		}

   			return new String(buffer, offset, len, "GB2312");
   		} catch (UnsupportedEncodingException e) {
   			Debug.Log("编码错误：" + e.getMessage());
   		}
       	
       	// 错误处理？
       	return "";
       }

    /**
     * 获取音频Item
     * @return
     */
	public AisItem getAudioItem() {
		return mAudioItem;
	}

	/**
     * 获取视频Item
     * @return
     */
	public AisItem getVideoItem() {
		return mVideoItem;
	}
	
	/**
     * 获取图像Items
     * @return
     */
	public AisItem[] getImageItems() {
		return mImageItems;
	}

	/**
	 * 获取头
	 * @return
	 */
	public AisHeader getHeader() {
		return  mHeader;
	}

	/**
	 * 获取村务公开表格HTML路径
	 * @return
	 */
	public String getTableHtmlFile() {
		return DataMan.DataFile(AIS_TABLE_FILENAME, true);
	}
}
