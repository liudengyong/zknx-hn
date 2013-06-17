package com.zknx.hn.functions.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.zknx.hn.common.Debug;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;

public class AisDoc {

	public enum ItemType {TEXT, CHEKCBOX, IMAGE, VIDEO, AUDIO}

	// Ais文件头的字节数
	private static final int AIS_HEADER_SIZE = 140;
	// 整形大小
	private static final int INT_SIZE = 4;
	// 字体结构大小
	private static final int FONT_STRUCT_SIZE = 52;

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
		BYTE bSeazon;
		//char szReserved1[3];
		char dwAddress[9];  //所属地区ID,不区分地区时为0
		char szMajor[32];   //所属专业ID,每个BIT对应一个专业,不区分专业时为0
		char szFileDate[12];//文件发布日期,到期的文件才会发布
		char szValidDate[12];//有郊日期,过了日期不显示
		char szTitle[50];   //主题词
		//char szReserved2[1];
	} TAisHeader;
	长度：140字节
	*/

	/**
	 * Ais文件头
	 * @author Dengyong
	 *
	 */
	class AisHeader {
		String type;   //AIS标志
		String fileId; //文件唯一ID
		byte column;   //栏目ID
		byte seazon;
		String address;  //所属地区ID,不区分地区时为0
		String major;    //所属专业ID,每个BIT对应一个专业,不区分专业时为0
		String fileDate; //文件发布日期,到期的文件才会发布
		String validDate;//有郊日期,过了日期不显示
		String title;    //主题词
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

	/**
	 * 通过ais_id构造AisDoc
	 * @param ais_id
	 */
	AisDoc(String ais_id) {
		parseAisDoc(ais_id);
	}

	/**
	 * 获取标题
	 * @return
	 */
	public String getTitle() {
		return (mHeader != null) ? mHeader.title : "";
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

		ItemType type; // 类型
		byte data[]; // 数据
		
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
	private boolean parseAisDoc(String ais_id) {
		if (ais_id == null) {
			Debug.Log("严重错误：parseAisDoc，ais_id为空");
			return false;
		}

		String filePathName = DataMan.GetAisFilePathName(ais_id);
		
		if (!FileUtils.IsFileExist(filePathName)) {
			Debug.Log("Ais文件没找到：" + filePathName);
			return false;
		}
		
		Debug.Log("解析ais文件：" + filePathName);
		
		try {
			
			FileInputStream file = new FileInputStream(filePathName);

			// 文件太短，格式错误
			byte[] buffer = new byte[AIS_HEADER_SIZE];
			if (file.read(buffer, 0, AIS_HEADER_SIZE) != AIS_HEADER_SIZE) {
				Debug.Log("严重错误：parseAisDoc，ais文件格式错误");
				file.close();
				return false;
			}
			
			// 解析Ais头
			mHeader = parseAisHeader(buffer);
			
			// 生成行树
			mItemTree = new ArrayList<List<AisItem>>();
			// 默认生成一个空行
			List<AisItem> row = new ArrayList<AisItem>();

			int v;
			AisItem.ByteArray textBytes = new AisItem.ByteArray(0);
			
			// TODO 空读24个字节？文件头改变？
			byte[] buf = new byte[24];
			file.read(buf);
			
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

				Debug.Log("item长度：" + length);

				byte[] data;
				try {
					data = new byte[length];
				} catch (Throwable e) {
					Debug.Log("严重错误：内存不足，parseAisDoc");
					return false;
				}

				if (length != file.read(data)) {
					Debug.Log("严重错误：Ais解析，读取数据错误！");
					return false;
				}

				switch (v) {
				case DataMan.AIS_TOKEN:
					Debug.Log("TODO:文字");
					break;
				case DataMan.AIS_TOKEN_FONT:
					Debug.Log("字体结构：" + data);
					break;
				case DataMan.AIS_TOKEN_COURSE_ANSWER:
					for (byte b : data) {
						Debug.Log("答案 = " + (char)b);
					}
					break;
				case DataMan.AIS_TOKEN_COURSE_GRADE:
					for (byte b : data) {
						Debug.Log("分数 = " + (char)b);
					}
					break;
				case DataMan.AIS_TOKEN_IMAGE:
					if (mImageItems == null)
						mImageItems = new AisItem[3];

					// 换行
					AisItem imageItem = new AisItem(ItemType.IMAGE, data);
					
					if (mImageItems[0] == null)
						mImageItems[0] = imageItem;
					else if (mImageItems[1] == null)
						mImageItems[1] = imageItem;
					else if (mImageItems[2] == null)
						mImageItems[2] = imageItem;
					else
						Debug.Log("解析Ais警告：多余三个图片Item");
					
					break;
				case DataMan.AIS_TOKEN_VIDEO:
					// 只有一个视频Item
					if (mVideoItem == null)
						mVideoItem = new AisItem(ItemType.VIDEO, data);
					else
						Debug.Log("解析Ais警告：多余一个视频Item");
					break;
				case DataMan.AIS_TOKEN_AUDIO:
					// 只有一个音频Item
					if (mAudioItem == null)
						mAudioItem = new AisItem(ItemType.AUDIO, data);
					else
						Debug.Log("解析Ais警告：多余一个音频Item");
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
	 * 解析ais头
	 * @param buffer
	 * @return
	 */
	private AisHeader parseAisHeader(byte[] buffer) {
		
		if (buffer == null || buffer.length != AIS_HEADER_SIZE) {
			Debug.Log("严重错误：getAisHeader，buffer " + buffer);
			return null;
		}
		
		AisHeader header = new AisHeader();
		
		int offset = 0;
		int size;
		
		// 文件类型标识
		header.type = new String(buffer, offset, size = 5);
		offset += size;
		
		// 文件id
		header.fileId = new String(buffer, offset, size = 18);
		offset += size;
		
		// 栏目id
		size = 1;
		header.column = buffer[offset];
		offset += size;
		
		//
		size = 1;
		header.seazon = buffer[offset];
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
		header.title = readText(buffer, offset, size = 12);
		offset += size;

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
    private static String readText(byte[] buffer, int offset, int len) {

    	try {
			return new String(buffer, offset, len, "GB2312");
		} catch (UnsupportedEncodingException e) {
			Debug.Log("编码错误：" + e.getMessage());
		}
    	
    	// 错误处理？
    	return "标题";
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
}
