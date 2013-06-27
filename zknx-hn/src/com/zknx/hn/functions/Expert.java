package com.zknx.hn.functions;

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

	public Expert(LayoutInflater inflater, LinearLayout frameRoot) {
		super(inflater, frameRoot, UIConst.FUNCTION_ID_EXPERT_GUIDE, R.layout.func_frame_triple);

		Instance = this;
	}
	
	/**
	 * 初始化专家列表
	 */
	@Override
	protected void initClass(int function_id) {
		mAdapterClassList = new CommonListAdapter(mContext, DataMan.GetExpertList());
	}

	/**
	 * 初始化Ais子分类
	 * @param position
	 */
	@Override
	protected void initAisList(int position) {
		LinearLayout inforLayout  = getExpertInfo(position);
		LinearLayout askBtnLayout = getExpertAskButton(position);

		super.initAisList("专家信息", DataMan.INVALID_ID, inforLayout, askBtnLayout);
	}

	/**
	 * 获取专家资料视图
	 * @param position
	 * @return
	 */
	private LinearLayout getExpertInfo(int position) {
		ListItemMap item = mAdapterClassList.getItem(position);
		
		String expertId = item.getString(DataMan.KEY_EXPERT_ID);
		
		LinearLayout inforLayout = (LinearLayout) mInflater.inflate(R.layout.expert_info, null);
		
		ImageView newImageView = (ImageView) inforLayout.findViewById(R.id.expert_info_photo);

		String imageFilePath = DataMan.DataFile("expert/" + expertId + ".jpg");

		Bitmap bm = ImageUtils.GetLoacalBitmap(imageFilePath);
		
		try {
			//newImageView.setImageDrawable(mContext.getResources().getDrawable(res));
			newImageView.setImageBitmap(bm);
		} catch (Throwable e) {
			Debug.Log("严重错误：内存不足，getExpertInfo");
		}

		String name = item.getString(DataMan.KEY_NAME);
		((TextView) inforLayout.findViewById(R.id.expert_info_name)).setText(name);
		
		String major = item.getString(DataMan.KEY_EXPERT_MAJOR);
		((TextView) inforLayout.findViewById(R.id.expert_info_major)).setText(major);
		
		String introduce = item.getString(DataMan.KEY_EXPERT_INTRODUCE);
		((TextView) inforLayout.findViewById(R.id.expert_info_introduce)).setText(introduce);
		
		return inforLayout;
	}

	/**
	 * 获取专家提问按钮视图
	 * @param position
	 * @return
	 */
	private LinearLayout getExpertAskButton(int position) {

		final String expertId = "";
		final String expertName = "刘专家";

		LinearLayout askLayout = initButtonPair(R.string.ask_expert, R.string.ask_expert_interphone, new OnClickListener() {
			@Override
			public void onClick(View v) {
				initAskView(expertId, expertName);
			}
		});
		
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
				
				if (subject.length() < 10 || question.length() < 10) {
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
								// TODO interface 提问专家

								int ret = 0;
								
								if (DataMan.AskExpert(UserMan.GetUserId(), expertId, subject, question))
									ret = 1;

								Message msg = new Message();
								msg.what = MESSAGE_ASK_QUESTION;
								msg.arg1 = ret;
								mHandler.sendMessage(msg);
								//mHandler.sendEmptyMessage(MESSAGE_ASK_QUESTION);
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
			if (msg.arg1 == 1)
				Toast.makeText(mContext, "提问成功，请等待专家解答", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(mContext, "提问失败，请检查网络", Toast.LENGTH_LONG).show();
		}
	}
}
