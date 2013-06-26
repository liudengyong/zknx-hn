package com.zknx.hn.functions;

import java.io.IOException;

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
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.CommonListAdapter;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.ListItemClickListener;
import com.zknx.hn.functions.common.CommonList.CommonListParams;

public class AisView extends FunctionView {

	// 子类可以访问
	CommonListAdapter mAdapterClassList;
	CommonListAdapter mAdapterAisList;
	
	// Ais列表视图框架
	LinearLayout mAisListFrame;
	// Ais视图框架
	LinearLayout mAisContentFrame;
	
	// 标题
	String mTitle;

	private int mFrameResId;
	
	// 点击图片返回时用到
	private LinearLayout mAisViewRoot;
	private String mCurAisId;
	
	private static final String DEFAULT_TITLE = "内容";
	private String mAisTitle = DEFAULT_TITLE;
	
	// 播放器
	private MediaPlayer mPlayer;
	// 解析器
	AisParser mAisParser;
	
	// 课件：交卷布局
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
		}
		
		// 初始化分类（三栏）或者初始化Ais列表（两栏）
		if (mFrameResId == R.layout.func_frame_split) {
			mAisListFrame = mContentFrame[0];
			mAisContentFrame = mContentFrame[1];
			initAisList(mTitle, DataMan.INVALID_ID, null, null);
		} else if (mFrameResId == R.layout.func_frame_triple) {
			mAisListFrame = mContentFrame[1];
			mAisContentFrame = mContentFrame[2];
			initClass(function_id);
		} else {
			Debug.Log("严重错误：AISView mFrameResId");
		}
	}

	/**
	 * 初始化Ais分类
	 */
	void initClass(int function_id) {
		
		mAdapterClassList = new CommonListAdapter(mContext, DataMan.GetAisClassList(function_id));
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterClassList, mOnClickClass);
		
		CommonList.Init(listParams, mTitle);
		
		// 默认第一个分类
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
	 * 默认无底部视图，子类可以覆盖，从而实现自定义底部View
	 */
	LinearLayout getCutomBottom() {
		return mCourseSubmitLayout;
	}
	
	/**
	 * 初始化Ais列表
	 * @param position
	 */
	void initAisList(int position) {
		ListItemMap mapItem = mAdapterClassList.getItem(position);

		String title = "AIS分类";
		int class_id = DataMan.INVALID_ID;

		if (mapItem != null) {
			title = mapItem.get(DataMan.KEY_NAME).toString();
			class_id = mapItem.getInt(DataMan.KEY_AIS_CLASS_ID);
		}
		
		initAisList(title, class_id, null, null);
	}
	
	/**
	 * 初始化Ais子分类
	 * @param position
	 */
	protected void initAisList(String title, int class_id, LinearLayout header, LinearLayout footer) {
		
		mAdapterAisList = new CommonListAdapter(mContext, DataMan.GetAisList(class_id));
		
		CommonListParams listParams = new CommonListParams(mInflater, mAisListFrame, mAdapterAisList, new ListItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				super.onItemClick(parent, view, position, id);
				
				attachAisView(position);
			}
		});
		
		// 定制视图为空则是普通列表
		CommonList.Init(listParams, title, header, footer);
		
		// 默认第一个AIS视图
		attachAisView(0);
	}
	
	void attachAisView(int position) {
		String ais_id = mAdapterAisList.getItemMapString(position, DataMan.KEY_AIS_ID);
		attachAisView(ais_id, mAisContentFrame);
	}

	void attachAisView(String ais_id, LinearLayout root) {
		String title = DEFAULT_TITLE;
		LinearLayout layout = null;
		Debug.Log("ais_id = " + ais_id);
		
		AisParser.AisLayout aisLayout = mAisParser.GetAisLayout(ais_id, mInflater, getJsInterface());
		
		if (aisLayout != null) {
			title = aisLayout.getTitle();
			layout = aisLayout.getLayout();
			// 保存当前信息
			mAisViewRoot = root;
			mCurAisId = ais_id;
			mAisTitle = title;
			
			// 如果有音频则播放音频
			playAisAudio();
		}

		initContent(title, layout, getCutomBottom(), root);
	}
	
	/**
	 * Ais界面显示图片
	 */
	private void setAisImageView(String filePathName) {
		
		LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.ais_image_view, null);
		
		layout.findViewById(R.id.ais_image_view_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 返回Ais视图
				attachAisView(mCurAisId, mAisViewRoot);
			}
		});

		ImageView newImageView = (ImageView) layout.findViewById(R.id.ais_image_view_image);
		newImageView.setImageURI(Uri.parse(filePathName));

		initContent(mAisTitle, layout, mAisViewRoot);
	}

	/**
	 * 获取标题
	 * @param function_id
	 * @return
	 */
	private String getTitle(int function_id) {
		switch (function_id) {
		// 中科农信
		case UIConst.FUNCTION_ID_ARGRI_TECH:
		case UIConst.FUNCTION_ID_EXPERT_GUIDE:
		case UIConst.FUNCTION_ID_EXPERT_FERTILIZE:
			return UIConst.FUNCTIONS_ZKNX[function_id - UIConst.FUNCTION_CLASS_ID_ZKNX - 1];
		// 红星党建
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
		
		Debug.Log("严重错误：AISView.getTitle " + function_id);

		return "";
	}
	
	// JS接口
	private JsInterface mJsInterface = new JsInterface();
	
	/**
	 * 子类可以覆盖（试卷ais）
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
	 * 播放音频
	 */
	private void playAisAudio() {
		byte data[] = mAisParser.getAudioData();
		
		if (data == null) {
			Debug.Log("播放音频错误：getAudioData空");
			return;
		}

		// 点击两次停止播放
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
			Dialog.Toast(mInflater.getContext(), R.string.stop_play_audio);
			return;
		}

		Exception exp = null;
		try {
			String tmpFileName = DataMan.DataFile("tmp.mp3");
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
			Debug.Log("播放异常：" + exp.getMessage());
	}

	/**
	 * 播放视频
	 */
	private void playAisVideo() {
		byte data[] = mAisParser.getVideoData();
		
		if (data == null) {
			Debug.Log("播放视频错误：getVideoData空");
			return;
		}
		
		String tmpFile = DataMan.DataFile("tmp.mp4");
		FileUtils.WriteFile(tmpFile, data);
		Intent it = new Intent(Intent.ACTION_VIEW);  
        it.setDataAndType(Uri.parse(tmpFile), "video/mp4");  
        mInflater.getContext().startActivity(it);
	}
	
	/**
	 * 初始化交卷视图
	 */
	private void initCouseSubmitButtons() {
		//if (mCourseSubmitLayout == null) {
			// 初始化交卷和重做按钮
		mCourseSubmitLayout = initButtonPair(R.string.resset, R.string.submit, new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case ID_SUBMIT:
					//invokeJsMethod(R.string.confirm_submit_course, "submitTest()");
					break;
				case ID_RESET:
					//invokeJsMethod(R.string.confirm_reset_course, "resetTest()");
					break;
				}
			}
		});
		/*} else {
			// 首先脱离父类
			ViewParent parent = mCourseSubmitLayout.getParent();
			
			if (parent != null) {
				if (parent instanceof LinearLayout) {
					((LinearLayout)parent).removeAllViews();
				} else if (parent instanceof RelativeLayout) {
					((RelativeLayout)parent).removeAllViews();
				}
			}
		}
		*/
	}
}
