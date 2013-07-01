package com.zknx.hn.functions;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;
import com.zknx.hn.data.ListItemMap;
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
	
	private static final String DEFAULT_TITLE = "����";
	private String mAisTitle = DEFAULT_TITLE;
	
	// ������
	private MediaPlayer mPlayer;
	// ������
	AisParser mAisParser;
	
	// �μ���������
	private LinearLayout mCourseSubmitLayout;
	
	private final static int ID_RESET = R.id.common_btn_pair_left;
	private final static int ID_SUBMIT = R.id.common_btn_pair_right;

	public AisView(LayoutInflater inflater, LinearLayout frameRoot, int function_id, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		mPlayer = new MediaPlayer();
		mAisParser = new AisParser(inflater);
		
		mFrameResId = frameResId;
		
		mTitle = getTitle(function_id);
		
		if (function_id == UIConst.FUNCTION_ID_BEST_COUSE) {
			initCouseSubmitButtons();
		} else {
			mCourseSubmitLayout = null;
		}
		
		// ��ʼ�����ࣨ���������߳�ʼ��Ais�б�������
		if (mFrameResId == R.layout.func_frame_split) {
			mAisListFrame = mContentFrame[0];
			mAisContentFrame = mContentFrame[1];
			initAisList(mTitle, DataMan.INVALID_ID);
		} else if (mFrameResId == R.layout.func_frame_triple) {
			mAisListFrame = mContentFrame[1];
			mAisContentFrame = mContentFrame[2];
			initClass(function_id);
			initClassList();
		} else {
			Debug.Log("���ش���AISView mFrameResId");
		}
	}

	/**
	 * ��ʼ��Ais����
	 */
	protected void initClass(int function_id) {
		mAdapterClassList = new CommonListAdapter(mContext, DataMan.GetAisClassList(function_id));
	}
	
	/**
	 * ��ʼ�������б�
	 */
	private void initClassList() {
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
		int class_id = DataMan.INVALID_ID;

		if (mapItem != null) {
			title = mapItem.get(DataMan.KEY_NAME).toString();
			class_id = mapItem.getInt(DataMan.KEY_AIS_CLASS_ID);
		}
		
		initAisList(title, class_id);
	}
	
	/**
	 * ��ʼ��ais�б�
	 * @param title
	 * @param class_id
	 */
	private void initAisList(String title, int class_id) {
		List<ListItemMap> listMap = DataMan.GetAisList(class_id);
		initAisList(title, listMap, null, null);
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
		attachAisView(aisFileName, mAisContentFrame);
	}

	void attachAisView(String aisFileName, LinearLayout root) {
		String title = DEFAULT_TITLE;
		LinearLayout layout = null;
		Debug.Log("ais_file = " + aisFileName);
		
		AisParser.AisLayout aisLayout = mAisParser.GetAisLayout(aisFileName, mInflater, getJsInterface());
		
		if (aisLayout != null) {
			title = aisLayout.getTitle();
			layout = aisLayout.getLayout();
			// ���浱ǰ��Ϣ
			mAisViewRoot = root;
			mCurAisFileName = aisFileName;
			mAisTitle = title;
			
			// �������Ƶ�򲥷���Ƶ
			playAisAudio();
		}

		initContent(title, layout, getCutomBottom(), root);
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
				attachAisView(mCurAisFileName, mAisViewRoot);
			}
		});

		ImageView newImageView = (ImageView) layout.findViewById(R.id.ais_image_view_image);
		newImageView.setImageURI(Uri.parse(filePathName));

		initContent(mAisTitle, layout, mAisViewRoot);
	}

	/**
	 * ��ȡ����
	 * @param function_id
	 * @return
	 */
	private String getTitle(int function_id) {
		switch (function_id) {
		// �п�ũ��
		case UIConst.FUNCTION_ID_ARGRI_TECH:
		case UIConst.FUNCTION_ID_EXPERT_GUIDE:
		case UIConst.FUNCTION_ID_EXPERT_FERTILIZE:
			return UIConst.FUNCTIONS_ZKNX[function_id - UIConst.FUNCTION_CLASS_ID_ZKNX - 1];
		// ���ǵ���
		case UIConst.FUNCTION_ID_BEST_COUSE:
		case UIConst.FUNCTION_ID_MODEL:
		case UIConst.FUNCTION_ID_VANGUARD_PARTY:
		case UIConst.FUNCTION_ID_POLICY:
		case UIConst.FUNCTION_ID_CUR_POLITICS:
		case UIConst.FUNCTION_ID_CLASS_EXPERIENCE:
		case UIConst.FUNCTION_ID_HAPPAY:
		case UIConst.FUNCTION_ID_LAW:
			return UIConst.FUNCTIONS_PARTY[function_id - UIConst.FUNCTION_CLASS_ID_PARTY - 1];
		}
		
		Debug.Log("���ش���AISView.getTitle " + function_id);

		return "";
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
		mCourseSubmitLayout = initButtonPair(R.string.resset, R.string.submit, new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				switch (view.getId()) {
				case ID_SUBMIT:
					CourseView.SubmitOrReset(view, false);
					break;
				case ID_RESET:
					CourseView.SubmitOrReset(view, true);
					break;
				}
			}
		});
	}
}
