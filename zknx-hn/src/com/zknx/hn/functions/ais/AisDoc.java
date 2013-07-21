package com.zknx.hn.functions.ais;

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
	private static final int AIS_HEADER_SIZE = 164;
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
	char child_column[20]; //����Ŀ��
	BYTE bSeazon;
	//char szReserved1[3];
	char bColumn_child[4];
	char dwAddress[9];  //��������ID,�����ֵ���ʱΪ0
	char szMajor[32];   //����רҵID,ÿ��BIT��Ӧһ��רҵ,������רҵʱΪ0
	char szFileDate[12];//�ļ���������,���ڵ��ļ��Żᷢ��
	char szValidDate[12];//�н�����,�������ڲ���ʾ
	char szTitle[50];   //�����
	//char szReserved2[1];
}TAisHeader;

	���ȣ�140�ֽ�
	*/
	
	/*
BYTE bColumn  

1	��ѡ��Ʒ
2	�г�����
3	������Ϣ
4	�ҵ���Ȧ
5	ũҵ����
6	ר��ָ��
7	�ҵĹ���
8	��ѧʩ��
9	ʱ������
10	��ѡ�μ�
11	�ȷ浳Ա
12	����ģ��
13	�¸�����
14	����ũ��
15	���ɷ���
16	��ũ����

char child_column

CString column_child1[]={""};
CString column_child2[]={""};
CString column_child3[]={""};
CString column_child4[]={""};
CString column_child5[]={"��ʳ","�߲�","����","ˮ��","ҩ��","��������","ˮ��","����"};
CString column_child6[]={"��ֲ", "��ֳ", "��ũ"};
CString column_child7[]={""};
CString column_child8[]={""};
CString column_child9[]={"ʱ������", "ũ��Ҫ��", "����ѧϰ"};
CString column_child10[]={""};
CString column_child11[]={""};
CString column_child12[]={""};
CString column_child13[]={"��ʳ","�߲�","����","ˮ��","ҩ��","��������","ˮ��","����"};
CString column_child14[]={"��ʷ����","��ѡЦ��","����ѧϰ","��������"};
CString column_child15[]={"��������","��ũ����"};
CString column_child16[]={"������","������","ҽ����","������"};
	 */
	
	private static int COLUMN_COURSE = 12; // �μ�column

	/**
	 * Ais�ļ�ͷ
	 * @author Dengyong
	 *
	 */
	class AisHeader {
		String type;   //AIS��־
		String fileId; //�ļ�ΨһID
		byte column;   //��ĿID
		String childColumn; // ����Ŀ��
		byte seazon; // ����
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
	public AisDoc(String aisFileName) {
		parseAisDoc(aisFileName);
	}

	/**
	 * ��ȡ����
	 * @return
	 */
	public String getTitle() {
		return (mHeader != null) ? mHeader.title : "����";
	}
	
	/**
	 * ��ȡais id
	 * @return
	 */
	public String getAisId() {
		return (mHeader != null) ? mHeader.fileId : "";
	}
	
	/**
	 * ��ȡais child id
	 * @return
	 */
	public String getAisChildColumn() {
		return (mHeader != null) ? mHeader.childColumn : "";
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
	private boolean parseAisDoc(String aisFileName) {
		if (aisFileName == null || aisFileName.length() == 0) {
			Debug.Log("���ش���parseAisDoc��ais_fileΪ��");
			return false;
		}

		String filePathName = DataMan.DataFile(aisFileName);
		
		if (!FileUtils.IsFileExist(filePathName)) {
			Debug.Log("Ais�ļ�û�ҵ���" + filePathName);
			return false;
		}
		
		Debug.Log("����ais�ļ���" + filePathName);
		
		try {
			
			FileInputStream file = new FileInputStream(filePathName);

			// ����Aisͷ
			mHeader = parseAisHeader(file);
			
			// ��������
			mItemTree = new ArrayList<List<AisItem>>();
			// Ĭ������һ������
			List<AisItem> row = new ArrayList<AisItem>();

			int v;
			AisItem.ByteArray textBytes = new AisItem.ByteArray(0);
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

				//Debug.Log("item���ȣ�" + length);

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
				case DataMan.AIS_TOKEN_COURSE_GRADE:
				case DataMan.AIS_TOKEN_COURSE_NOTE:
					if (isCourse())
						addQuestion(v, data);
					else
						Debug.Log("Ais�ṹ���󣺷��Ծ�Ӧ�д𰸽ṹ��" + mHeader.column);
					break;
				case DataMan.AIS_TOKEN_IMAGE:
					if (isCourse())
						addQuestion(v, data);
					else {
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
					}
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
	 * ��Ӵ𰸵���ǰ����
	 */
	private class Question {
		Question(byte[] data) {
			//dataΪ���ͼƬ����
			bitmapData = data;
		}

		byte[] bitmapData;
		int grade;
		byte[] answer;
		String note;
	}

	private List<Question> mQuestionList;

	/**
	 * ��ȡ��Ŀ����
	 * @return
	 */
	public int getQuestionCount() {
		return mQuestionList != null ? mQuestionList.size() : 0;
	}
	
	/**
	 * ��ȡ���ͼƬ
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
	 * ��ȡ��Ŀ��
	 * @return
	 */
	public String getQuestionAnswer(int i) {
		if (mQuestionList == null || mQuestionList.size() < i)
			return "";
		
		String rightAnwser = "";
		byte[] rightAnwserBytes = mQuestionList.get(i).answer;
		if (rightAnwserBytes != null) {
			for (byte a : rightAnwserBytes)
				// �𰸳���һ�����������ȫ����ȷ�����0
				if (a != 0)
					rightAnwser += (char)a;
		}
		
		return rightAnwser;
	}
	
	/**
	 * ��ȡ��Ŀ����
	 * @return
	 */
	public int getQuestionGrade(int i) {
		if (mQuestionList == null || mQuestionList.size() < i)
			return DataMan.INVALID_ID;
		
		return mQuestionList.get(i).grade;
	}
	
	/**
	 * ��ȡ��Ŀ����
	 * @return
	 */
	public String getQuestionNote(int i) {
		if (mQuestionList == null || mQuestionList.size() <= 0 ||
			mQuestionList.get(i).note == null)
			return "��";
		
		return mQuestionList.get(i).note;
	}
	
	/**
	 * �Ƿ�����Ais
	 * @return
	 */
	public boolean isCourse() {
		return (mHeader != null) ? (mHeader.column == COLUMN_COURSE) : false;
	}

	/**
	 * �������
	 * @param v
	 * @param data
	 * @throws IOException
	 */
	private void addQuestion(int v, byte[] data) throws IOException {
		if (!isCourse()) {
			throw new IOException("Ais�ṹ���󣺷��Ծ�Ӧ�д𰸽ṹ��" + mHeader.column);
		}

		// ���ͼƬ
		if (v == DataMan.AIS_TOKEN_IMAGE) {
			if (mQuestionList == null)
				mQuestionList = new ArrayList<Question>();
			
			mQuestionList.add(new Question(data));
			
			return;
		}
		
		if (mQuestionList == null) {
			throw new IOException("Ais�ṹ����mQuestionList == null");
		}
		
		Question lastQuestion = mQuestionList.get(mQuestionList.size() - 1);

		switch (v) {
		// ��ӷ���
		case DataMan.AIS_TOKEN_COURSE_GRADE:
			lastQuestion.grade = DataMan.ParseInt(new String(data, 0, 2));
			if (lastQuestion.grade == -1)
				lastQuestion.grade = DataMan.ParseInt(new String(data, 0, 1));
			break;
		case DataMan.AIS_TOKEN_COURSE_ANSWER:
			if (data.length != 4) {
				Debug.Log("�𰸳��ȴ���" + data.length);
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
	 * ����aisͷ
	 * @param buffer
	 * @return
	 * @throws IOException 
	 */
	private AisHeader parseAisHeader(FileInputStream file) throws IOException {
		
		// �ļ�̫�̣���ʽ����
		byte[] buffer = new byte[AIS_HEADER_SIZE];
		if (file.read(buffer, 0, AIS_HEADER_SIZE) != AIS_HEADER_SIZE) {
			file.close();
			throw new IOException("Aisͷ���ش���parseAisDoc��ais�ļ���ʽ����");
		}

		/*
	char szType[5];     //AIS��־
	char fileId[18];  //�ļ�ΨһID
	BYTE bColumn;     //��ĿID
	char child_column[20]; //����Ŀ��
	BYTE bSeazon;
	char bColumn_child[4];
	char dwAddress[9];  //��������ID,�����ֵ���ʱΪ0
	char szMajor[32];   //����רҵID,ÿ��BIT��Ӧһ��רҵ,������רҵʱΪ0
	char szFileDate[12];//�ļ���������,���ڵ��ļ��Żᷢ��
	char szValidDate[12];//�н�����,�������ڲ���ʾ
	char szTitle[50];   //�����
		 */
		
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
		
		Debug.Log("column = " + header.column);
		
		// ����Ŀ��
		header.childColumn = readText(buffer, offset, size = 20);//new String(buffer, offset, size = 20);
		offset += size;
		
		// ����
		size = 1;
		header.seazon = buffer[offset];
		offset += size;
		
		// ����Ŀid ��ʱû����
		//header.childColumnId = new String(buffer, offset, size = 4);
		size = 4;
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
		header.title = readText(buffer, offset, size = 50);

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
    private static String readText(byte[] buffer) {
    	return readText(buffer, 0, buffer.length);
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

    		for (int i = 0; i < len ; ++i) {
    			if (buffer[offset + i] == 0) {
    				len = i; // �ַ���ʵ�ʳ���
    				break; // �����ַ�����β��־
    			}
    		}

   			return new String(buffer, offset, len, "GB2312");
   		} catch (UnsupportedEncodingException e) {
   			Debug.Log("�������" + e.getMessage());
   		}
       	
       	// ������
       	return "";
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
