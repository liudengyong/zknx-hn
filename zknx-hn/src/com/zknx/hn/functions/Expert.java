package com.zknx.hn.functions;

import com.zknx.hn.R;
import com.zknx.hn.common.UIConst;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Expert extends AisView {

	public Expert(LayoutInflater inflater, LinearLayout frameRoot) {
		super(inflater, frameRoot, UIConst.FUNCTION_ID_EXPERT_GUIDE, R.layout.func_frame_triple);
	}
	
	/**
	 * ��ʼ��Ais�ӷ���
	 * @param position
	 */
	@Override
	void initSubClass(int position) {
		LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.expert_info, null);
		
		// TODO ����ר��
		layout.findViewById(R.id.expert_info_ask).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		// TODO ר����Ƭ
		ImageView newImageView = (ImageView) layout.findViewById(R.id.expert_info_photo);
		newImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_video));

		super.initSubClass(position, layout);
	}
}
