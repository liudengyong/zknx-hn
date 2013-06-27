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
	 * ��ʼ��ר���б�
	 */
	@Override
	protected void initClass(int function_id) {
		mAdapterClassList = new CommonListAdapter(mContext, DataMan.GetExpertList());
	}

	/**
	 * ��ʼ��Ais�ӷ���
	 * @param position
	 */
	@Override
	protected void initAisList(int position) {
		ListItemMap item = mAdapterClassList.getItem(position);
		
		List<ListItemMap> expertList = null;
		
		if (item != null) {
			String expertId = item.getString(DataMan.KEY_EXPERT_ID);
			expertList = DataMan.GetExpertAnwserList(expertId);
		}
		
		LinearLayout inforLayout  = getExpertInfo(item);
		LinearLayout askBtnLayout = getExpertAskButton(position);

		super.initAisList("ר����Ϣ", expertList, inforLayout, askBtnLayout);
	}

	/**
	 * ��ȡר��������ͼ
	 * @param position
	 * @return
	 */
	private LinearLayout getExpertInfo(ListItemMap item) {
		
		LinearLayout inforLayout = (LinearLayout) mInflater.inflate(R.layout.expert_info, null);
		
		if (item != null) {
		
			String expertId = item.getString(DataMan.KEY_EXPERT_ID);
			String name = item.getString(DataMan.KEY_NAME);
			String major = item.getString(DataMan.KEY_EXPERT_MAJOR);
			String introduce = item.getString(DataMan.KEY_EXPERT_INTRODUCE);

			((TextView) inforLayout.findViewById(R.id.expert_info_name)).setText(name);
			((TextView) inforLayout.findViewById(R.id.expert_info_major)).setText(major);
			((TextView) inforLayout.findViewById(R.id.expert_info_introduce)).setText(introduce);
			
			String imageFilePath = DataMan.DataFile("expert/" + expertId + ".jpg");
			Bitmap bm = ImageUtils.GetLoacalBitmap(imageFilePath);
			
			// ���û��ר��ͼƬ������
			if (bm != null) {
				try {
					//newImageView.setImageDrawable(mContext.getResources().getDrawable(res));
					((ImageView) inforLayout.findViewById(R.id.expert_info_photo)).setImageBitmap(bm);
				} catch (Throwable e) {
					Debug.Log("���ش����ڴ治�㣬getExpertInfo");
				}
			} else {
				inforLayout.findViewById(R.id.expert_info_photo).setVisibility(View.GONE);
			}
		}
		
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

		LinearLayout askLayout = initButtonPair(R.string.ask_expert, R.string.ask_expert_interphone, new OnClickListener() {
			@Override
			public void onClick(View v) {
				initAskView(expertId, expertName);
			}
		});
		
		// ʵ�������Խ�
		askLayout.findViewById(R.id.common_btn_pair_right).setEnabled(false);
		
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

		askLayout.findViewById(R.id.expert_ask_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Ĭ�ϵ�һ��AIS��ͼ
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
				
				// ȷ������
				Dialog.Confirm(mContext, R.string.confirm_ask_question, new ConfirmListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mWaitDialog = WaitDialog.Show(mContext, "����", "��������", new WaitListener() {
							@Override
							public void startWait() {
								// TODO interface ����ר��

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
