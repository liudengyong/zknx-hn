package com.zknx.hn.functions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.data.DataMan;
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
	private int mCurAisId = DataMan.INVALID_ID;
	
	private static final String DEFAULT_TITLE = "内容";
	private String mAisTitle = DEFAULT_TITLE;

	public AisView(LayoutInflater inflater, LinearLayout frameRoot, int function_id, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		mFrameResId = frameResId;
		
		initClass(function_id);
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
		initSubClassOrAisView(0);
	}
	
	ListItemClickListener mOnClickClass = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			initSubClassOrAisView(position);
		}
	};

	/**
	 * 默认无底部视图，子类可以覆盖，从而实现自定义底部View
	 */
	LinearLayout getCutomBottom() {
		return null;
	}

	/**
	 * 初始化子分类视图（三分视图框架）或者初始化Ais视图（两分视图框架）
	 * @param position
	 */
	void initSubClassOrAisView(int position) {

		int class_id = mAdapterClass.getItemMapInt(position, DataMan.KEY_AIS_CLASS_ID);

		if (mFrameResId == R.layout.func_frame_split) {
			attachAisView(class_id, mContentFrame[1]); // FIXME clss_id != ais_id
		} else if (mFrameResId == R.layout.func_frame_triple) {
			initSubClass(position);
		} else {
			Debug.Log("严重错误：AISView.cutomClass2View," + class_id);
		}
	}
	
	/**
	 * 初始化Ais子分类
	 * @param position
	 */
	void initSubClass(int position) {
		initSubClass(position, null, null);
	}
	
	/**
	 * 初始化Ais子分类
	 * @param position
	 */
	protected void initSubClass(int position, LinearLayout header, LinearLayout footer) {
		
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
				
				initSubClassAisView(position);
			}
		});
		
		// 定制视图为空则是普通列表
		CommonList.Init(listParams, title, header, footer);
		
		// 默认第一个AIS视图
		initSubClassAisView(0);
	}
	
	void initSubClassAisView(int position) {
		int ais_id = mAdapterSubClass.getItemMapInt(position, DataMan.KEY_AIS_CLASS_ID);
		attachAisView(ais_id, mContentFrame[2]);
	}

	void attachAisView(int ais_id, LinearLayout root) {
		
		String title = DEFAULT_TITLE;
		LinearLayout layout = null;
		AisParser.AisLayout aisLayout = AisParser.GetAisLayout(ais_id, mInflater, mClickImage);
		
		if (aisLayout != null) {
			title = aisLayout.getTitle();
			layout = aisLayout.getLayout();
			// 保存当前信息
			mAisViewRoot = root;
			mCurAisId = ais_id;
			mAisTitle = title;
		}

		initContent(title, layout, getCutomBottom(), root);
	}
	
	/**
	 * 监听图片点击，是否在全Ais界面显示图片
	 */
	OnClickListener mClickImage = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view instanceof ImageView) {
				setAisImageView((ImageView)view);
			} else {
				Debug.Log("严重错误：mClickImage监听类型错误，" + view.getClass());
			}
		}
	};
	
	/**
	 * Ais界面显示图片
	 */
	private void setAisImageView(ImageView imageView) {
		
		LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.ais_image_view, null);
		
		layout.findViewById(R.id.ais_image_view_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 返回Ais视图
				attachAisView(mCurAisId, mAisViewRoot);
			}
		});

		ImageView newImageView = (ImageView) layout.findViewById(R.id.ais_image_view_image);
		newImageView.setImageDrawable(imageView.getDrawable());

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
}
