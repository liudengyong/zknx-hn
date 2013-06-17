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

	// Ais�ļ�ͷ���ֽ���
	private static final int AIS_HEADER_SIZE = 140;
	// ���δ�С
	private static final int INT_SIZE = 4;
	// ����ṹ��С
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
	���ȣ�52�ֽ�


	typedef struct tagAisHeader
	{
		char szType[5];     //AIS��־
		//char dwFormat;   //��������������,Ŀǰĩ��
		char fileId[18];  //�ļ�ΨһID
		BYTE bColumn;     //��ĿID
		BYTE bSeazon;
		//char szReserved1[3];
		char dwAddress[9];  //��������ID,�����ֵ���ʱΪ0
		char szMajor[32];   //����רҵID,ÿ��BIT��Ӧһ��רҵ,������רҵʱΪ0
		char szFileDate[12];//�ļ���������,���ڵ��ļ��Żᷢ��
		char szValidDate[12];//�н�����,�������ڲ���ʾ
		char szTitle[50];   //�����
		//char szReserved2[1];
	} TAisHeader;
	���ȣ�140�ֽ�
	*/

	/**
	 * Ais�ļ�ͷ
	 * @author Dengyong
	 *
	 */
	class AisHeader {
		String type;   //AIS��־
		String fileId; //�ļ�ΨһID
		byte column;   //��ĿID
		byte seazon;
		String address;  //��������ID,�����ֵ���ʱΪ0
		String major;    //����רҵID,ÿ��BIT��Ӧһ��רҵ,������רҵʱΪ0
		String fileDate; //�ļ���������,���ڵ��ļ��Żᷢ��
		String validDate;//�н�����,�������ڲ���ʾ
		String title;    //�����
	}

	// Aisͷ
	private AisHeader mHeader;
	// Item��
	private List<List<AisItem>> mItemTree;
	// ��ƵItem
	private AisItem mAudioItem;
	// ��ƵItem
	private AisItem mVideoItem;
	// ͼ��Items
	private AisItem[] mImageItems;

	/**
	 * ͨ��ais_id����AisDoc
	 * @param ais_id
	 */
	AisDoc(String ais_id) {
		parseAisDoc(ais_id);
	}

	/**
	 * ��ȡ����
	 * @return
	 */
	public String getTitle() {
		return (mHeader != null) ? mHeader.title : "";
	}

	/**
	 * ��ȡItem��
	 * @return
	 */
	public List<List<AisItem>> getItemTree() {
		return mItemTree;
	}

	/**
	 * ����AisItem
	 * @author Dengyong
	 *
	 */
	public static class AisItem {
		AisItem(ItemType _type, byte _data[]) {
			type = _type;
			data = _data;
		}

		ItemType type; // ����
		byte data[]; // ����
		
		/**
		 * ��������byte����
		 * @author Dengyong
		 *
		 */
		static class ByteArray {
			byte[] data;
			int length;

			ByteArray(int size) {
				if (size < 1)
					size = 16; // Ĭ��16�ֽ�
				data = new byte[size];
				length = 0;
			}

			void append(byte v) {
				// ���Ȳ���ʱ������һ��
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
	 * ����ais��list
	 * @param ais_id
	 * @return
	 */
	private boolean parseAisDoc(String ais_id) {
		if (ais_id == null) {
			Debug.Log("���ش���parseAisDoc��ais_idΪ��");
			return false;
		}

		String filePathName = DataMan.GetAisFilePathName(ais_id);
		
		if (!FileUtils.IsFileExist(filePathName)) {
			Debug.Log("Ais�ļ�û�ҵ���" + filePathName);
			return false;
		}
		
		Debug.Log("����ais�ļ���" + filePathName);
		
		try {
			
			FileInputStream file = new FileInputStream(filePathName);

			// �ļ�̫�̣���ʽ����
			byte[] buffer = new byte[AIS_HEADER_SIZE];
			if (file.read(buffer, 0, AIS_HEADER_SIZE) != AIS_HEADER_SIZE) {
				Debug.Log("���ش���parseAisDoc��ais�ļ���ʽ����");
				file.close();
				return false;
			}
			
			// ����Aisͷ
			mHeader = parseAisHeader(buffer);
			
			// ��������
			mItemTree = new ArrayList<List<AisItem>>();
			// Ĭ������һ������
			List<AisItem> row = new ArrayList<AisItem>();

			int v;
			AisItem.ByteArray textBytes = new AisItem.ByteArray(0);
			
			// TODO �ն�24���ֽڣ��ļ�ͷ�ı䣿
			byte[] buf = new byte[24];
			file.read(buf);
			
			while ((v = file.read()) >= 0) {
				if (v != DataMan.AIS_TOKEN) {
					// �ַ�����
					textBytes.append((byte)v);
					//textBytes.append((byte)file.read());
					continue;
				}

				// ������ֲ�Ϊ�գ���һ�α���Ϊ�գ�������ϴ����⵽item����ǰ��
				if (textBytes.length > 0) {
					row.add(new AisItem(ItemType.TEXT, new String(textBytes.data, 0, textBytes.length, "GBK").getBytes("GBK")));
					Debug.Log("����=" + new String(textBytes.data, 0, textBytes.length, "GB2312"));
					textBytes = new AisItem.ByteArray(0);
				}

				// ��ȡ�ؼ���ʶ
				v = file.read();

				// ��ý�崦���������壩
				int length = 0;
				// ����
				if (v == DataMan.AIS_TOKEN_FONT)
					length = FONT_STRUCT_SIZE;
				else
					length = readInt(file, INT_SIZE);

				if (length <= 0) {
					Debug.Log("���ش���Ais���������ȴ���");
					return false;
				}

				Debug.Log("item���ȣ�" + length);

				byte[] data;
				try {
					data = new byte[length];
				} catch (Throwable e) {
					Debug.Log("���ش����ڴ治�㣬parseAisDoc");
					return false;
				}

				if (length != file.read(data)) {
					Debug.Log("���ش���Ais��������ȡ���ݴ���");
					return false;
				}

				switch (v) {
				case DataMan.AIS_TOKEN:
					Debug.Log("TODO:����");
					break;
				case DataMan.AIS_TOKEN_FONT:
					Debug.Log("����ṹ��" + data);
					break;
				case DataMan.AIS_TOKEN_COURSE_ANSWER:
					for (byte b : data) {
						Debug.Log("�� = " + (char)b);
					}
					break;
				case DataMan.AIS_TOKEN_COURSE_GRADE:
					for (byte b : data) {
						Debug.Log("���� = " + (char)b);
					}
					break;
				case DataMan.AIS_TOKEN_IMAGE:
					if (mImageItems == null)
						mImageItems = new AisItem[3];

					// ����
					AisItem imageItem = new AisItem(ItemType.IMAGE, data);
					
					if (mImageItems[0] == null)
						mImageItems[0] = imageItem;
					else if (mImageItems[1] == null)
						mImageItems[1] = imageItem;
					else if (mImageItems[2] == null)
						mImageItems[2] = imageItem;
					else
						Debug.Log("����Ais���棺��������ͼƬItem");
					
					break;
				case DataMan.AIS_TOKEN_VIDEO:
					// ֻ��һ����ƵItem
					if (mVideoItem == null)
						mVideoItem = new AisItem(ItemType.VIDEO, data);
					else
						Debug.Log("����Ais���棺����һ����ƵItem");
					break;
				case DataMan.AIS_TOKEN_AUDIO:
					// ֻ��һ����ƵItem
					if (mAudioItem == null)
						mAudioItem = new AisItem(ItemType.AUDIO, data);
					else
						Debug.Log("����Ais���棺����һ����ƵItem");
					break;
				}
			}
			
			// ������ǿ��У�������һ��
			if (row != null && row.size() > 0)
				mItemTree.add(row);

			file.close();
			
		} catch (IOException e) {
			Debug.Log("���ش���ais�����쳣��" + e.getMessage());
            return false;
		}
		
		Debug.Log("����ais�ļ��ɹ���" + filePathName);
		
		return true;
	}

	/**
	 * ����aisͷ
	 * @param buffer
	 * @return
	 */
	private AisHeader parseAisHeader(byte[] buffer) {
		
		if (buffer == null || buffer.length != AIS_HEADER_SIZE) {
			Debug.Log("���ش���getAisHeader��buffer " + buffer);
			return null;
		}
		
		AisHeader header = new AisHeader();
		
		int offset = 0;
		int size;
		
		// �ļ����ͱ�ʶ
		header.type = new String(buffer, offset, size = 5);
		offset += size;
		
		// �ļ�id
		header.fileId = new String(buffer, offset, size = 18);
		offset += size;
		
		// ��Ŀid
		size = 1;
		header.column = buffer[offset];
		offset += size;
		
		//
		size = 1;
		header.seazon = buffer[offset];
		offset += size;
		
		// ����id
		header.address = new String(buffer, offset, size = 9);
		offset += size;
		
		// רҵid
		header.major = new String(buffer, offset, size = 32);
		offset += size;
		
		// ��������
		header.fileDate = new String(buffer, offset, size = 12);
		offset += size;
		
		// ��Ч��
		header.validDate = new String(buffer, offset, size = 12);
		offset += size;
		
		// ����
		header.title = readText(buffer, offset, size = 12);
		offset += size;

		return header;
	}
	
	/**
	 * ��buffer��ȡint
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
     * ���ļ���ȡint
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
	 * ��buffer��ȡString
	 * @param buffer
	 * @param offset
	 * @param len
	 * @return
	 */
    private static String readText(byte[] buffer, int offset, int len) {

    	try {
			return new String(buffer, offset, len, "GB2312");
		} catch (UnsupportedEncodingException e) {
			Debug.Log("�������" + e.getMessage());
		}
    	
    	// ������
    	return "����";
    }

    /**
     * ��ȡ��ƵItem
     * @return
     */
	public AisItem getAudioItem() {
		return mAudioItem;
	}

	/**
     * ��ȡ��ƵItem
     * @return
     */
	public AisItem getVideoItem() {
		return mVideoItem;
	}
	
	/**
     * ��ȡͼ��Items
     * @return
     */
	public AisItem[] getImageItems() {
		return mImageItems;
	}
}
