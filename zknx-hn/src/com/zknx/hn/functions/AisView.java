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
	private String mCurAisFileName;
	private String mCurAisDate;
	
	// 解析后的ais排版
	private AisParser.AisLayout mAisParsedLayout;
	
	private static final String DEFAULT_TITLE = "内容";
	private AisDoc.AisHeader mAisHeader = null;
	
	private String mAisTitle = null;
	private List<ListItemMap> mListMap = null;
	
	// 播放器
	private static MediaPlayer mPlayer;
	public static void StopPlaying() {
		if (mPlayer != null &&
			mPlayer.isPlaying()) {
			mPlayer.stop();
		}
	}

	// 解析器
	AisParser mAisParser;
	
	// 课件：交卷布局
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
		
		// 初始化分类（三栏）或者初始化Ais列表（两栏）
		if (mFrameResId == R.layout.func_frame_split) {
			mAisListFrame = mContentFrame[0];
			mAisContentFrame = mContentFrame[1];
			loadAisList(mTitle, "");
		} else if (mFrameResId == R.layout.func_frame_triple) {
			mAisListFrame = mContentFrame[1];
			mAisContentFrame = mContentFrame[2];
			loadChildListData();
		} else {
			Debug.Log("严重错误：AISView mFrameResId");
		}
	}

	/**
	 * 初始化Ais分类
	 */
	protected void loadChildListData() {
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "正在加载资讯列表";
			}

			@Override
			public void waitAction() {
				mAdapterClassList = new CommonListAdapter(mContext, DataMan.GetAisColumnChildList(mFunctionId));
				mHandler.sendEmptyMessage(MESSAGE_LOADED_AIS_CHILD_LIST);
			}
		});
	}
	
	/**
	 * 初始化分类列表
	 */
	private void initChildListView() {
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
	protected void initAisList(int position) {
		ListItemMap mapItem = mAdapterClassList.getItem(position);

		String title = "分类";
		String child = null;

		if (mapItem != null) {
			title = mapItem.get(DataMan.KEY_NAME).toString();
			child = mapItem.getString(DataMan.KEY_AIS_COLUMN_CHILD);
		}
		
		loadAisList(title, child);
	}
	
	/**
	 * 初始化ais列表
	 * @param title
	 * @param class_id
	 */
	private void loadAisList(final String title, final String child) {
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "正在加载资讯列表";
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
	 * 初始化Ais子分类
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
		
		// 定制视图为空则是普通列表
		CommonList.Init(listParams, title, header, footer);
		
		// 默认第一个AIS视图
		attachAisView(0);
	}
	
	void attachAisView(int position) {
		String aisFileName = mAdapterAisList.getItemMapString(position, DataMan.KEY_AIS_FILE_NAME);
		String aisDate = mAdapterAisList.getItemMapString(position, DataMan.KEY_AIS_DATE);
		parseAis(aisDate, aisFileName, mAisContentFrame);
	}
	
	/**
	 * 等待解析ais
	 */
	void parseAis(final String date, final String aisFileName, LinearLayout root) {
		mAisViewRoot = root;

		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "正在解析资讯文件";
			}

			@Override
			public void waitAction() {
				if (mAisParser.parseAisDoc(mContext, date, aisFileName)) {

					// 保存当前信息
					mCurAisFileName = aisFileName;
					mCurAisDate = date;

					mHandler.sendEmptyMessage(MESSAGE_PARSED_AIS);
				}
			}
		});
	}

	/**
	 * 初始化解析后的ais视图
	 */
	void initAisView() {
		LinearLayout layout = null;
		
		AisView.StopPlaying();
		
		mAisParsedLayout = mAisParser.GetAisLayout(mInflater, getJsInterface());

		if (mAisParsedLayout != null) {
			mAisHeader = mAisParsedLayout.getAisHeader();
			layout = mAisParsedLayout.getLayout();

			// 如果有音频则播放音频
			playAisAudio();
		} else {
			// 停止播放
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
	 * Ais界面显示图片
	 */
	private void setAisImageView(String filePathName) {
		
		LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.ais_image_view, null);
		
		layout.findViewById(R.id.ais_image_view_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 返回Ais视图
				parseAis(mCurAisDate, mCurAisFileName, mAisViewRoot);
			}
		});

		ImageView newImageView = (ImageView) layout.findViewById(R.id.ais_image_view_image);
		newImageView.setImageURI(Uri.parse(filePathName));

		initContent(mAisHeader.getTitle(), layout, mAisViewRoot);
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
		
		String tmpFile = DataMan.DataFile("tmp.mp4", true);
		FileUtils.WriteFile(tmpFile, data);
		Intent it = new Intent(Intent.ACTION_VIEW);  
        it.setDataAndType(Uri.parse(tmpFile), "video/mp4");  
        mInflater.getContext().startActivity(it);
	}
	
	/**
	 * 初始化交卷视图
	 */
	private void initCouseSubmitButtons() {
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads()
		.detectDiskWrites()
		.detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
		.penaltyLog() //打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
		.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects() //探测SQLite数据库操作
		.penaltyLog() //打印logcat
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
			        /*.setPositiveButton("上传", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String ret = DataMan.PostGrade();
							if (ret != null) {
								Dialog.MessageBox(mContext, "返回：" + ret);
							}
						}
			        })*/
			        .setPositiveButton("确定", null)
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
	 * 子类覆盖
	 * @param msg
	 */
	protected void processChildMessage(Message msg) {
	}
}
