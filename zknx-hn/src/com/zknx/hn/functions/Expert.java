package com.zknx.hn.functions;

import com.zknx.hn.R;
import com.zknx.hn.common.Dialog;
import com.zknx.hn.common.Dialog.ConfirmListener;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.WaitDialog;
import com.zknx.hn.common.WaitDialog.WaitListener;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.UserMan;

import android.content.DialogInterface;
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

	// ���ʵȴ�������
	private WaitDialog mWaitDialog;
	// ���ʵ����������
	private EditText mAskSubject;
	private EditText mAskQuestion;

	public Expert(LayoutInflater inflater, LinearLayout frameRoot) {
		super(inflater, frameRoot, UIConst.FUNCTION_ID_EXPERT_GUIDE, R.layout.func_frame_triple);

		Instance = this;
	}

	/**
	 * ��ʼ��Ais�ӷ���
	 * @param position
	 */
	@Override
	void initAisList(int position) {
		LinearLayout inforLayout  = getExpertInfo(position);
		LinearLayout askBtnLayout = getExpertAskButton(position);

		super.initAisList(position, inforLayout, askBtnLayout);
	}

	/**
	 * ��ȡר��������ͼ
	 * @param position
	 * @return
	 */
	private LinearLayout getExpertInfo(int position) {
		LinearLayout inforLayout = (LinearLayout) mInflater.inflate(R.layout.expert_info, null);
		
		// TODO ר����Ƭ
		ImageView newImageView = (ImageView) inforLayout.findViewById(R.id.expert_info_photo);

		long time = System.currentTimeMillis();
		int res = R.drawable.expert;
		if (time % 3 == 1)
			res = R.drawable.expert2;
		else if (time % 3 == 2)
			res = R.drawable.expert3;
		
		try {
			newImageView.setImageDrawable(mContext.getResources().getDrawable(res));
		} catch (Throwable e) {
			Debug.Log("���ش����ڴ治�㣬getExpertInfo");
		}
		
		// TODO ר�����֣�רҵ������
		String name = "������";
		((TextView) inforLayout.findViewById(R.id.expert_info_name)).setText(name);
		
		String major = "��ֲר��";
		((TextView) inforLayout.findViewById(R.id.expert_info_major)).setText(major);
		
		String introduce = "�������Ǳ�������������ֳר��";
		((TextView) inforLayout.findViewById(R.id.expert_info_introduce)).setText(introduce);
		
		return inforLayout;
	}

	/**
	 * ��ȡר�����ʰ�ť��ͼ
	 * @param position
	 * @return
	 */
	private LinearLayout getExpertAskButton(int position) {

		final String expertId = "";
		final String expertName = "��ר��";

		LinearLayout askLayout = getLinearLayoutBtnPair(R.string.ask_expert, R.string.ask_expert_interphone, new OnClickListener() {
			@Override
			public void onClick(View v) {
				initAskView(expertId, expertName);
			}
		});
		
		// TODO ʵ�������Խ�
		askLayout.findViewById(R.id.common_btn_pair2).setEnabled(false);
		
		return askLayout;
	}

	// Ϊ��̬Handler����ʵ��
	private static Expert Instance;
	private static Handler mHandler = new Handler() {
	    /**
	     * ʵ����Ϣ����
	     */
	    @Override
	    public void handleMessage(Message msg) {
	    	Instance.processWaitMessage(msg);
	    }
	};

	/**
	 * ��ʼ��������ͼ
	 * @param expertId
	 */
	private void initAskView(final String expertId, String expertName) {
		RelativeLayout askLayout = (RelativeLayout) mInflater.inflate(R.layout.expert_ask, null);
		
		mAskSubject = (EditText) askLayout.findViewById(R.id.expert_ask_subject);
		mAskQuestion = (EditText) askLayout.findViewById(R.id.expert_ask_question);
		
		// TODO ���ذ�ť
		askLayout.findViewById(R.id.expert_ask_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Ĭ�ϵ�һ��AIS��ͼ
				attachAisView(0);
			}
		});
		
		// TODO ���ʰ�ť
		askLayout.findViewById(R.id.expert_ask_ask).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final String subject = mAskSubject.getEditableText().toString();
				final String question = mAskQuestion.getEditableText().toString();
				
				if (subject.length() < 10 || question.length() < 10) {
					Toast.makeText(mContext, R.string.input_too_short, Toast.LENGTH_LONG).show();
					return;
				}
				
				// ȷ������
				Dialog.Confirm(mContext, R.string.confirm_ask_question, new ConfirmListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mWaitDialog = WaitDialog.Show(mContext, "����", "��������", new WaitListener() {
							@Override
							public void startWait() {
								// TODO interface ����ר��

								int ret = 0;
								
								if (DataMan.AskExpert(UserMan.GetCurrentUserId(), expertId, subject, question))
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
		
		initContent("��" + expertName + "����", askLayout, mContentFrame[2]);
	}

	// ����ר�ҵ���Ϣ
	private static final int MESSAGE_ASK_QUESTION = 1;
	/**
	 * �������ʵȴ�
	 * @param what
	 */
	protected void processWaitMessage(Message msg) {
		
		// ���ؽ�����
		mWaitDialog.dismiss();
		
		if (msg.what == MESSAGE_ASK_QUESTION) {
			if (msg.arg1 == 1)
				Toast.makeText(mContext, "���ʳɹ�����ȴ�ר�ҽ��", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(mContext, "����ʧ�ܣ���������", Toast.LENGTH_LONG).show();
		}
	}
}
