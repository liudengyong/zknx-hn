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
import com.zknx.hn.common.Dialog;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;
import com.zknx.hn.data.ListItemMap;
import com.zknx.hn.functions.common.AisParser;
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.CommonListAdapter;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.CommonList.CommonListParams;
import com.zknx.hn.functions.common.ListItemClickListener;

public class AisView extends FunctionView {

	// 子类可以访问
	CommonListAdapter mAdapterClass;
	CommonListAdapter mAdapterSubClass;

	private int mFrameResId;
	
	// 点击图片返回时用到
	private LinearLayout mAisViewRoot;
	private int mCurAisId;
	
	private static final String DEFAULT_TITLE = "内容";
	private String mAisTitle = DEFAULT_TITLE;
	
	// 播放器
	private MediaPlayer mPlayer;
	// 解析器
	AisParser mAisParser;

	public AisView(LayoutInflater inflater, LinearLayout frameRoot, int function_id, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		mPlayer = new MediaPlayer();
		mAisParser = new AisParser(inflater);
		
		mFrameResId = frameResId;
		
		if (mFrameResId == R.layout.func_frame_split) {
			initAisList(function_id);
		} else if (mFrameResId == R.layout.func_frame_triple) {
			initClass(function_id);
		} else {
			Debug.Log("严重错误：AISView mFrameResId");
		}
	}

	/**
	 * 初始化Ais分类
	 */
	void initClass(int function_id) {
		
		String title = getTitle(function_id);
		
		mAdapterClass = new CommonListAdapter(mContext, DataMan.GetAisClassList(function_id));
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterClass, mOnClickClass);
		
		CommonList.Init(listParams, title);
		
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
		return null;
	}
	
	/**
	 * 初始化Ais列表
	 * @param position
	 */
	void initAisList(int position) {
		initAisList(position, null, null);
	}
	
	/**
	 * 初始化Ais子分类
	 * @param position
	 */
	protected void initAisList(int position, LinearLayout header, LinearLayout footer) {
		
		ListItemMap mapItem = mAdapterClass.getItem(position);
		String title = "AIS分类";
		
		int class_id = DataMan.INVALID_ID;

		if (mapItem != null) {
			title = mapItem.get(DataMan.KEY_NAME).toString();
			class_id = mapItem.getInt(DataMan.KEY_AIS_CLASS_ID);
		}
		
		mAdapterSubClass = new CommonListAdapter(mContext, DataMan.GetAisSubClassList(class_id));
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[1], mAdapterSubClass, new ListItemClickListener() {
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
		int ais_id = mAdapterSubClass.getItemMapInt(position, DataMan.KEY_AIS_CLASS_ID);
		attachAisView(ais_id, mContentFrame[2]);
	}

	void attachAisView(int ais_id, LinearLayout root) {
		String title = DEFAULT_TITLE;
		LinearLayout layout = null;
		// TODO int id 转换
		AisParser.AisLayout aisLayout = mAisParser.GetAisLayout("" + ais_id, mInflater, mJsInterface);
		
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

	String getTitle(int function_id) {
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
}
