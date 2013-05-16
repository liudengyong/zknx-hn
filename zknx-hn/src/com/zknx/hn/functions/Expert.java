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
	 * 初始化Ais子分类
	 * @param position
	 */
	@Override
	void initSubClass(int position) {
		LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.expert_info, null);
		
		// TODO 提问专家
		layout.findViewById(R.id.expert_info_ask).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		// TODO 专家照片
		ImageView newImageView = (ImageView) layout.findViewById(R.id.expert_info_photo);
		newImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_video));

		super.initSubClass(position, layout);
	}
}
