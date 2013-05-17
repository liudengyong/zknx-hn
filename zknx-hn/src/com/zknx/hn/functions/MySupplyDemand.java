package com.zknx.hn.functions;

import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.zknx.hn.R;
import com.zknx.hn.common.Dialog;
import com.zknx.hn.common.Dialog.ConfirmListener;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.CommonListAdapter;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.ListItemClickListener;
import com.zknx.hn.functions.common.CommonList.CommonListParams;

public class MySupplyDemand extends FunctionView {
	
	private static final String LEVEL1_TITLE = "我的供求";
	private static final String LEVEL2_TITLE = "供求对接";

	CommonListAdapter mAdapterClass; // 供求分类
	CommonListAdapter mAdapterPair;  // 供求对接
	
	// 发布信息按钮
	LinearLayout mPostLayout;

	public MySupplyDemand(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		initClassList();
	}

	/**
	 * 初始化供求信息分类
	 */
	void initClassList() {
		
		LinearLayout createInfoBtn = getLinearLayoutBtn("发布新信息", mOnClickCreateInfo);
		
		mAdapterClass = new CommonListAdapter(mContext, DataMan.GetProductClassList());
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterClass, mOnClassClick);
		
		CommonList.Init(listParams, LEVEL1_TITLE, createInfoBtn);
		
		// 默认第一个产品对接信息
		initPairList(0);
	}

	/**
	 * 初始化供求信息
	 */
	void initPairList(int position) {
		
		// 产品分类
		int product_class_id = mAdapterClass.getItemMapInt(position, DataMan.KEY_PRODUCT_CLASS_ID);
		
		mAdapterPair = new CommonListAdapter(mContext, DataMan.GetSupplyDemandPairList(product_class_id));
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[1], mAdapterPair, mOnClickPair);
		
		CommonList.Init(listParams, LEVEL2_TITLE);
		
		// 第一个对接信息内容
		initSupplyDemandInfo(0);
	}
	
	void initSupplyDemandInfo(int position) {
		
		int supply_demand_id = mAdapterPair.getItemMapInt(position, DataMan.KEY_SUPPLY_DEMAND_INFO_ID);

		// 新建TableLayout 实例  
        TableLayout tableLayout = new TableLayout(mContext);
        
        String title = SupplyDemand.GetSupplyDemandInfo(supply_demand_id, tableLayout);
        
		initContent(title, tableLayout, mContentFrame[2]);
	}
	
	/**
	 * 初始化发布信息界面，第二区提示信息，第三区发布信息
	 */
	void createInfo() {
		// 清除第二区后重新添加发布信息的提示
		// 提示
		LinearLayout layoutTip = (LinearLayout)mInflater.inflate(R.layout.create_supply_demand_tip, null);

		initContent(mContext.getString(R.string.create_supply_demand), layoutTip, mContentFrame[1]);
		
		// 清除第三区后添加发布信息界面
		LinearLayout layoutCreate = (LinearLayout)mInflater.inflate(R.layout.create_supply_demand, null);

		// 内容
		FrameLayout layoutInfo = (FrameLayout)layoutCreate.findViewById(R.id.create_supply_demand_info);
		layoutInfo.addView(initCreateInfo());
		
		// 发布按钮
		initPostButton();
		
		initContent(mContext.getString(R.string.create_supply_demand), layoutCreate, mPostLayout, mContentFrame[2]);
	}
	
	/**
	 * 初始化发布按钮
	 */
	private void initPostButton() {
		if (mPostLayout == null) {
			mPostLayout = (LinearLayout)mInflater.inflate(R.layout.create_supply_demand_post, null);
			
			Button postBtn = (Button)mPostLayout.findViewById(R.id.create_supply_demand_post_btn);
			postBtn.setOnClickListener(mClickPostButton);
		}
	}
	
	/**
	 * 监听发布供求信息按钮
	 */
	OnClickListener mClickPostButton = new OnClickListener() {
		@Override
		public void onClick(View view) {

			// TODO 完善发布供求信息：检查供求信息填写内容
			
			final int product_id = DataMan.INVALID_ID;

			Dialog.Confirm(mContext, R.string.confirm_post_supply_demand_info, new ConfirmListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					int msg;
					if (DataMan.PostSupplyDemandInfo(product_id)) {
						msg = R.string.post_supply_demand_info_ok;
					} else {
						msg = R.string.post_supply_demand_info_failed;
					}
					
					Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
				}
			});
		}
	};

	/**
	 * 初始化发布信息内容
	 */
	private TableLayout initCreateInfo() {
		// 新建TableLayout 实例  
        TableLayout tableLayout = new TableLayout(mContext);
		LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	
    	// 全部列自动填充空白处
        //tableLayout.setStretchAllColumns(true);
        tableLayout.setColumnShrinkable(0, true);
        tableLayout.setColumnStretchable(1, true);
    	
        tableLayout.addView(GetTableRow("内容", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("发布时间", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("有效期", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("数量", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("单价", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("产地", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("联系人", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("联系电话", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("手机", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("地址", new EditText(mContext)), params);

        tableLayout.setGravity(Gravity.CENTER);
        
        return tableLayout;
	}
	
	private TableRow GetTableRow(String label, View inputControl) {
		
		TableRow tableRow = new TableRow(mContext);
		LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        // 显示标签
        TextView tv = new TextView(mContext);
        
        tv.setGravity(Gravity.RIGHT);
        tv.setText(label);
        tv.setMinimumWidth(100);
        tv.setPadding(0, 0, 10, 0); // 右边padding 10
        
        tableRow.addView(tv);
        
        // 输入控件
        tableRow.addView(inputControl);

        tableRow.setLayoutParams(params);
        
        return tableRow;
	}
	
	ListItemClickListener mOnClassClick = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			
			initPairList(position);
		}
	};
	
	ListItemClickListener mOnClickPair = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			
			initSupplyDemandInfo(position);
		}
	};
	
	OnClickListener mOnClickCreateInfo = new OnClickListener() {
		@Override
		public void onClick(View view) {
			createInfo();
		}
	};
}
