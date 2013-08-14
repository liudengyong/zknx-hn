package com.zknx.hn.functions;

import java.util.List;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.common.widget.ImageUtils;
import com.zknx.hn.common.widget.WaitDialog;
import com.zknx.hn.common.widget.Dialog.ConfirmListener;
import com.zknx.hn.common.widget.WaitDialog.WaitListener;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.ListItemMap;
import com.zknx.hn.data.UserMan;
import com.zknx.hn.functions.common.CommonListAdapter;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Expert extends AisView {

	// 提问等待进度条
	private WaitDialog mWaitDialog;
	// 提问的主题和问题
	private EditText mAskSubject;
	private EditText mAskQuestion;
	
	private static final int MESSAGE_ARG_OK     = 0;
	private static final int MESSAGE_ARG_FAILED = 1;
	
	// 当前专家id
	private int mCurExpertPosition;

	public Expert(LayoutInflater inflater, LinearLayout frameRoot) {
		super(inflater, frameRoot, UIConst.FUNCTION_ID_EXPERT_GUIDE, R.layout.func_frame_triple);

		Instance = this;
	}
	
	/**
	 * 初始化专家列表
	 */
	@Override
	protected void initChildListData() {
		mAdapterClassList = new CommonListAdapter(mContext, DataMan.GetExpertList());
	}

	/**
	 * 初始化Ais子分类
	 * @param position
	 */
	@Override
	protected void initAisList(int position) {
		initExpertQuestionList(position);

		// 默认第三栏视图
		initContent("暂无问题解答", null, mContentFrame[2]);
	}
	
	/**
	 * 初始化专家问题列表
	 */
	private void initExpertQuestionList(int position) {
		ListItemMap item = mAdapterClassList.getItem(position);
		
		String expertId = "";
		String expertName = "";
		if (item != null) {
			expertId = item.getString(DataMan.KEY_EXPERT_ID);
			expertName = item.getString(DataMan.KEY_NAME);
		}
		
		List<ListItemMap> expertList = DataMan.GetExpertAnwserList(expertId);
		
		LinearLayout inforLayout  = getExpertInfo(item);
		LinearLayout askBtnLayout = getExpertAskButton(expertId, expertName);

		super.initAisList("专家信息", expertList, inforLayout, askBtnLayout);
		
		mCurExpertPosition = position;
	}

	/**
	 * 获取专家资料视图
	 * @param position
	 * @return
	 */
	private LinearLayout getExpertInfo(ListItemMap item) {
		
		LinearLayout inforLayout = (LinearLayout) mInflater.inflate(R.layout.expert_info, null);
		
		if (item != null) {
		
			String expertId = item.getString(DataMan.KEY_EXPERT_ID);
			String name = item.getString(DataMan.KEY_NAME);
			String major = DataMan.GetExpertMajor(item.getInt(DataMan.KEY_EXPERT_MAJOR));
			String introduce = item.getString(DataMan.KEY_EXPERT_INTRODUCE);

			((TextView) inforLayout.findViewById(R.id.expert_info_name)).setText(name);
			((TextView) inforLayout.findViewById(R.id.expert_info_major)).setText(major);
			((TextView) inforLayout.findViewById(R.id.expert_info_introduce)).setText(introduce);
			
			String imageFilePath = DataMan.DataFile("expert/" + expertId + "/" + expertId + ".JPG", true);
			Bitmap bm = ImageUtils.GetLoacalBitmap(imageFilePath);
			
			// 如果没有专家图片，隐藏
			if (bm != null) {
				try {
					//newImageView.setImageDrawable(mContext.getResources().getDrawable(res));
					((ImageView) inforLayout.findViewById(R.id.expert_info_photo)).setImageBitmap(bm);
				} catch (Throwable e) {
					Debug.Log("严重错误：内存不足，getExpertInfo setImageBitmap");
				}
			} else {
				inforLayout.findViewById(R.id.expert_info_photo).setVisibility(View.GONE);
			}
		}
		
		return inforLayout;
	}

	/**
	 * 获取专家提问按钮视图
	 * @param position
	 * @return
	 */
	private LinearLayout getExpertAskButton(final String expertId, final String expertName) {
		
		OnClickListener clickAskButton = null;

		// 专家id和名字不为空，否则禁用按钮
		if (expertId != null &&
			!expertId.isEmpty() &&
			expertName != null &&
			!expertName.isEmpty()) {

			clickAskButton = new OnClickListener() {
				@Override
				public void onClick(View v) {
					initAskView(expertId, expertName);
				}
			};

		}

		LinearLayout askLayout = initButtonPair(R.string.ask_expert, R.string.ask_expert_interphone, clickAskButton);
		
		// 实现语音对讲
		askLayout.findViewById(R.id.common_btn_pair_right).setEnabled(false);
		
		return askLayout;
	}

	// 为静态Handler保存实例
	private static Expert Instance;
	private static Handler mHandler = new Handler() {
	    /**
	     * 实现消息处理
	     */
	    @Override
	    public void handleMessage(Message msg) {
	    	Instance.processWaitMessage(msg);
	    }
	};

	/**
	 * 初始化提问视图
	 * @param expertId
	 */
	private void initAskView(final String expertId, String expertName) {

		if (expertId == null || expertId.isEmpty() || expertName == null || expertName.isEmpty()) {
			return;
		}

		RelativeLayout askLayout = (RelativeLayout) mInflater.inflate(R.layout.expert_ask, null);

		mAskSubject = (EditText) askLayout.findViewById(R.id.expert_ask_subject);
		mAskQuestion = (EditText) askLayout.findViewById(R.id.expert_ask_question);

		askLayout.findViewById(R.id.expert_ask_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 默认第一个AIS视图
				attachAisView(0);
			}
		});

		askLayout.findViewById(R.id.expert_ask_ask).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final String subject = mAskSubject.getEditableText().toString();
				final String question = mAskQuestion.getEditableText().toString();
				
				if (subject.length() < 5 || question.length() < 5) {
					Toast.makeText(mContext, R.string.input_too_short, Toast.LENGTH_LONG).show();
					return;
				}
				
				// 确认提问
				Dialog.Confirm(mContext, R.string.confirm_ask_question, new ConfirmListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mWaitDialog = WaitDialog.Show(mContext, "提问", "正在提问", new WaitListener() {
							@Override
							public void startWait() {

								Message msg = new Message();
								msg.what = MESSAGE_ASK_QUESTION;
								
								if (DataMan.AskExpert(UserMan.GetUserId(), expertId, subject, question))
									msg.arg1 = MESSAGE_ARG_OK;
								else
									msg.arg1 = MESSAGE_ARG_FAILED;
								
								mHandler.sendMessage(msg);
							}
						});
					}
				});
			}
		});
		
		initContent("向" + expertName + "提问", askLayout, mContentFrame[2]);
	}

	// 提问专家的消息
	private static final int MESSAGE_ASK_QUESTION = 1;
	/**
	 * 出题提问等待
	 * @param what
	 */
	protected void processWaitMessage(Message msg) {
		
		// 隐藏进度条
		mWaitDialog.dismiss();
		
		if (msg.what == MESSAGE_ASK_QUESTION) {
			if (msg.arg1 == MESSAGE_ARG_OK) {
				Toast.makeText(mContext, "提问成功，请等待专家解答", Toast.LENGTH_LONG).show();
				// 重新初始化问题列表，加载本地
				initExpertQuestionList(mCurExpertPosition);
			}
			else
				Toast.makeText(mContext, "提问失败，请检查网络", Toast.LENGTH_LONG).show();
		}
	}
}
