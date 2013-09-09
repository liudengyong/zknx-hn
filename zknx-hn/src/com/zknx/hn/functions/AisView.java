package com.zknx.hn.functions;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.common.widget.WaitDialog;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;
import com.zknx.hn.data.ListItemMap;
import com.zknx.hn.functions.ais.AisDoc;
import com.zknx.hn.functions.ais.AisParser;
import com.zknx.hn.functions.ais.CourseView;
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.CommonListAdapter;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.ListItemClickListener;
import com.zknx.hn.functions.common.CommonList.CommonListParams;

public class AisView extends FunctionView {

	// ������Է���
	CommonListAdapter mAdapterClassList;
	CommonListAdapter mAdapterAisList;
	
	// Ais�б���ͼ���
	LinearLayout mAisListFrame;
	// Ais��ͼ���
	LinearLayout mAisContentFrame;
	
	// ����
	String mTitle;

	private int mFrameResId;
	
	// ���ͼƬ����ʱ�õ�
	private LinearLayout mAisViewRoot;
	private String mCurAisFileName;
	private String mCurAisDate;
	
	// �������ais�Ű�
	private AisParser.AisLayout mAisParsedLayout;
	
	private static final String DEFAULT_TITLE = "����";
	private AisDoc.AisHeader mAisHeader = null;
	
	private String mAisTitle = null;
	private List<ListItemMap> mListMap = null;
	
	// ������
	private static MediaPlayer mPlayer;
	public static void StopPlaying() {
		if (mPlayer != null &&
			mPlayer.isPlaying()) {
			mPlayer.stop();
		}
	}

	// ������
	AisParser mAisParser;
	
	// �μ���������
	private LinearLayout mCourseSubmitLayout;
	
	private int mFunctionId = DataMan.INVALID_ID;
	
	private final static int ID_GRADE = R.id.common_btn_triple_left;
	private final static int ID_RESET = R.id.common_btn_triple_middle;
	private final static int ID_SUBMIT = R.id.common_btn_triple_right;

	public AisView(LayoutInflater inflater, LinearLayout frameRoot, int function_id, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		AisView.StopPlaying();
		
		mPlayer = new MediaPlayer();
		mAisParser = new AisParser(inflater);
		
		mFrameResId = frameResId;
		
		mFunctionId = function_id;
		
		mTitle = UIConst.GetFunctionTitle(mFunctionId);
		
		if (function_id == UIConst.FUNCTION_ID_BEST_COUSE) {
			initCouseSubmitButtons();
		} else {
			mCourseSubmitLayout = null;
		}
		
		// ��ʼ�����ࣨ���������߳�ʼ��Ais�б�������
		if (mFrameResId == R.layout.func_frame_split) {
			mAisListFrame = mContentFrame[0];
			mAisContentFrame = mContentFrame[1];
			loadAisList(mTitle, "");
		} else if (mFrameResId == R.layout.func_frame_triple) {
			mAisListFrame = mContentFrame[1];
			mAisContentFrame = mContentFrame[2];
			loadChildListData();
		} else {
			Debug.Log("���ش���AISView mFrameResId");
		}
	}

	/**
	 * ��ʼ��Ais����
	 */
	protected void loadChildListData() {
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڼ�����Ѷ�б�";
			}

			@Override
			public void waitAction() {
				mAdapterClassList = new CommonListAdapter(mContext, DataMan.GetAisColumnChildList(mFunctionId));
				mHandler.sendEmptyMessage(MESSAGE_LOADED_AIS_CHILD_LIST);
			}
		});
	}
	
	/**
	 * ��ʼ�������б�
	 */
	private void initChildListView() {
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterClassList, mOnClickClass);
		
		CommonList.Init(listParams, mTitle);
		
		// Ĭ�ϵ�һ������
		initAisList(0);
	}
	
	ListItemClickListener mOnClickClass = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			initAisList(position);
		}
	};

	/**
	 * Ĭ���޵ײ���ͼ��������Ը��ǣ��Ӷ�ʵ���Զ���ײ�View
	 */
	LinearLayout getCutomBottom() {
		return mCourseSubmitLayout;
	}
	
	/**
	 * ��ʼ��Ais�б�
	 * @param position
	 */
	protected void initAisList(int position) {
		ListItemMap mapItem = mAdapterClassList.getItem(position);

		String title = "����";
		String child = null;

		if (mapItem != null) {
			title = mapItem.get(DataMan.KEY_NAME).toString();
			child = mapItem.getString(DataMan.KEY_AIS_COLUMN_CHILD);
		}
		
		loadAisList(title, child);
	}
	
	/**
	 * ��ʼ��ais�б�
	 * @param title
	 * @param class_id
	 */
	private void loadAisList(final String title, final String child) {
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڼ�����Ѷ�б�";
			}

			@Override
			public void waitAction() {
				mAisTitle = title;
				mListMap = DataMan.GetAisList(mFunctionId, child);
				mHandler.sendEmptyMessage(MESSAGE_LOADED_AIS_LIST);
			}
		});
	}

	/**
	 * ��ʼ��Ais�ӷ���
	 * @param position
	 */
	protected void initAisList(String title, List<ListItemMap> listMap, LinearLayout header, LinearLayout footer) {
		
		mAdapterAisList = new CommonListAdapter(mContext, listMap);
		
		CommonListParams listParams = new CommonListParams(mInflater, mAisListFrame, mAdapterAisList, new ListItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				super.onItemClick(parent, view, position, id);
				
				attachAisView(position);
			}
		});
		
		// ������ͼΪ��������ͨ�б�
		CommonList.Init(listParams, title, header, footer);
		
		// Ĭ�ϵ�һ��AIS��ͼ
		attachAisView(0);
	}
	
	void attachAisView(int position) {
		String aisFileName = mAdapterAisList.getItemMapString(position, DataMan.KEY_AIS_FILE_NAME);
		String aisDate = mAdapterAisList.getItemMapString(position, DataMan.KEY_AIS_DATE);
		parseAis(aisDate, aisFileName, mAisContentFrame);
	}
	
	/**
	 * �ȴ�����ais
	 */
	void parseAis(final String date, final String aisFileName, LinearLayout root) {
		mAisViewRoot = root;

		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڽ�����Ѷ�ļ�";
			}

			@Override
			public void waitAction() {
				if (mAisParser.parseAisDoc(mContext, date, aisFileName)) {

					// ���浱ǰ��Ϣ
					mCurAisFileName = aisFileName;
					mCurAisDate = date;

					mHandler.sendEmptyMessage(MESSAGE_PARSED_AIS);
				}
			}
		});
	}

	/**
	 * ��ʼ���������ais��ͼ
	 */
	void initAisView() {
		LinearLayout layout = null;
		
		AisView.StopPlaying();
		
		mAisParsedLayout = mAisParser.GetAisLayout(mInflater, getJsInterface());

		if (mAisParsedLayout != null) {
			mAisHeader = mAisParsedLayout.getAisHeader();
			layout = mAisParsedLayout.getLayout();

			// �������Ƶ�򲥷���Ƶ
			playAisAudio();
		} else {
			// ֹͣ����
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
				Dialog.Toast(mInflater.getContext(), R.string.stop_play_audio);
			}
		}
		
		String title = DEFAULT_TITLE;
		
		if (mAisHeader != null)
			title = mAisHeader.getTitle();

		initContent(title, layout, getCutomBottom(), mAisViewRoot);
	}
	
	/**
	 * Ais������ʾͼƬ
	 */
	private void setAisImageView(String filePathName) {
		
		LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.ais_image_view, null);
		
		layout.findViewById(R.id.ais_image_view_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ����Ais��ͼ
				parseAis(mCurAisDate, mCurAisFileName, mAisViewRoot);
			}
		});

		ImageView newImageView = (ImageView) layout.findViewById(R.id.ais_image_view_image);
		newImageView.setImageURI(Uri.parse(filePathName));

		initContent(mAisHeader.getTitle(), layout, mAisViewRoot);
	}

	// JS�ӿ�
	private JsInterface mJsInterface = new JsInterface();
	
	/**
	 * ������Ը��ǣ��Ծ�ais��
	 * @return
	 */
	protected Object getJsInterface() {
		return mJsInterface;
	}
	
	class JsInterface {
		
		JsInterface() {
		}
		
		public void playAudio() {
			runAction(new Runnable() {
	            @Override
	            public void run() {
	            	playAisAudio();	
	            }
	    	});
		}
		
		public void playVideo() {
			runAction(new Runnable() {
	            @Override
	            public void run() {
	            	playAisVideo();	
	            }
	    	});
		}
		
		public void showImage(final String filePathName) {
			runAction(new Runnable() {
	            @Override
	            public void run() {
	            	setAisImageView(filePathName);
	            }
	    	});
		}
		
		private void runAction(Runnable action) {
			((Activity)mContext).runOnUiThread(action);
		}
	}

	/**
	 * ������Ƶ
	 */
	private void playAisAudio() {
		byte data[] = mAisParser.getAudioData();
		
		if (data == null) {
			Debug.Log("������Ƶ����getAudioData��");
			return;
		}

		// �������ֹͣ����
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
			Dialog.Toast(mInflater.getContext(), R.string.stop_play_audio);
			return;
		}

		Exception exp = null;
		try {
			String tmpFileName = DataMan.DataFile("tmp.mp3", true);
			FileUtils.WriteFile(tmpFileName, data);
			mPlayer.reset();
			mPlayer.setDataSource(tmpFileName);
			mPlayer.prepare();
			mPlayer.start();
			Dialog.Toast(mInflater.getContext(), R.string.start_play_audio);
		} catch (IllegalArgumentException e) {
			exp = e;
		} catch (SecurityException e) {
			exp = e;
		} catch (IllegalStateException e) {
			exp = e;
		} catch (IOException e) {
			exp = e;
		}

		if (exp != null)
			Debug.Log("�����쳣��" + exp.getMessage());
	}

	/**
	 * ������Ƶ
	 */
	private void playAisVideo() {
		byte data[] = mAisParser.getVideoData();
		
		if (data == null) {
			Debug.Log("������Ƶ����getVideoData��");
			return;
		}
		
		String tmpFile = DataMan.DataFile("tmp.mp4", true);
		FileUtils.WriteFile(tmpFile, data);
		Intent it = new Intent(Intent.ACTION_VIEW);  
        it.setDataAndType(Uri.parse(tmpFile), "video/mp4");  
        mInflater.getContext().startActivity(it);
	}
	
	/**
	 * ��ʼ��������ͼ
	 */
	private void initCouseSubmitButtons() {
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads()
		.detectDiskWrites()
		.detectNetwork() // ��������滻ΪdetectAll() �Ͱ����˴��̶�д������I/O
		.penaltyLog() //��ӡlogcat����ȻҲ���Զ�λ��dropbox��ͨ���ļ�������Ӧ��log
		.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects() //̽��SQLite���ݿ����
		.penaltyLog() //��ӡlogcat
		.penaltyDeath()
		.build()); 
		
		mCourseSubmitLayout = initButtonTriple(R.string.grade, R.string.reset, R.string.submit, new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				switch (view.getId()) {
				case ID_GRADE:
					if (mAisHeader == null)
						return;

					String grades = DataMan.GetGrades(mAisHeader.getAisId());
					if (grades == null || grades.length() == 0)
						return;
					
					new AlertDialog.Builder(mContext)
			        .setIcon(null)
			        .setTitle(R.string.app_name)
			        .setMessage(grades)
			        /*.setPositiveButton("�ϴ�", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String ret = DataMan.PostGrade();
							if (ret != null) {
								Dialog.MessageBox(mContext, "���أ�" + ret);
							}
						}
			        })*/
			        .setPositiveButton("ȷ��", null)
			        .show();

					break;
				case ID_RESET:
					CourseView.SubmitOrReset(mAisHeader, view, true);
					break;
				case ID_SUBMIT:
					CourseView.SubmitOrReset(mAisHeader, view, false);
					break;
				}
			}
		});
	}
	
	protected final static int MESSAGE_LOADED_AIS_CHILD_LIST = 1;
	private   final static int MESSAGE_LOADED_AIS_LIST = 2;
	private   final static int MESSAGE_PARSED_AIS = 3;
	protected final static int MESSAGE_CHILD = 4;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg){
		   super.handleMessage(msg);

		   switch (msg.what) {
		   case MESSAGE_LOADED_AIS_CHILD_LIST:
			   initChildListView();
			   break;
		   case MESSAGE_LOADED_AIS_LIST:
			   initAisList(mAisTitle, mListMap, null, null);
			   break;
		   case MESSAGE_PARSED_AIS:
			   initAisView();
			   break;
		   case MESSAGE_CHILD:
			   processChildMessage(msg);
			   break;
		   }
		}
	};
	
	/**
	 * ���า��
	 * @param msg
	 */
	protected void processChildMessage(Message msg) {
	}
}
