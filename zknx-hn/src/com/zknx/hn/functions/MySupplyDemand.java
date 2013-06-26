package com.zknx.hn.functions;

import java.util.List;

import android.content.DialogInterface;
import android.text.Editable;
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
import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.common.widget.ListBox;
import com.zknx.hn.common.widget.Radio;
import com.zknx.hn.common.widget.WaitDialog;
import com.zknx.hn.common.widget.Dialog.ConfirmListener;
import com.zknx.hn.common.widget.WaitDialog.WaitListener;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.ListItemMap;
import com.zknx.hn.data.UserMan;
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
		
		LinearLayout createInfoBtn = initButton("发布新信息", mOnClickCreateInfo);
		
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
		String product_class_id = mAdapterClass.getItemMapString(position, DataMan.KEY_PRODUCT_CLASS_ID);
		
		mAdapterPair = new CommonListAdapter(mContext, DataMan.GetSupplyDemandPairList(product_class_id));
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[1], mAdapterPair, mOnClickPair);
		
		CommonList.Init(listParams, LEVEL2_TITLE);
		
		// 第一个对接信息内容
		initSupplyDemandInfo(0);
	}
	
	void initSupplyDemandInfo(int position) {
		
		//int supply_demand_id = mAdapterPair.getItemMapInt(position, DataMan.KEY_SUPPLY_DEMAND_INFO_ID);

		// 新建TableLayout 实例  
        TableLayout tableLayout = new TableLayout(mContext);
        
        String title = SupplyDemand.GetSupplyDemandInfo(mAdapterPair.getItem(position), tableLayout);
        
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

			// 检查供求信息填写内容
			if (mIsSupply.getCheckedRadioButtonId() == -1 ||
				mProductClass.getSelectedItemPosition() == -1 ||
				IsEditEmpty(mContent) ||
				IsEditEmpty(mValidDate) ||
				IsEditEmpty(mAmount) ||
				IsEditEmpty(mUnit) ||
				IsEditEmpty(mHost)) {
				Toast.makeText(mContext, "输入不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			
			if (IsEditEmpty(mAddress) ||
				IsEditEmpty(mPhone) ||
				IsEditEmpty(mMobilePhone) ||
				IsEditEmpty(mName)) {
				Toast.makeText(mContext, "个人信息不完善", Toast.LENGTH_LONG).show();
				return;
			}
			
			final DataMan.SupplyDemandInfo info = new DataMan.SupplyDemandInfo();
			
			info.type = mIsSupply.getCheckedRadioButtonId();
			// 获取产品分类id
			String productClassId = mProductList.get(mProductClass.getSelectedItemPosition()).getString(DataMan.KEY_PRODUCT_CLASS_ID);
			info.commodityid =  productClassId;
			info.count = mAmount.getEditableText().toString();
			info.place = mHost.getEditableText().toString();
			info.price = mPrice.getEditableText().toString();
			info.publishdate = DataMan.GetCurrentTime(false); // 发布日期
			info.title = mContent.getEditableText().toString();
			info.unit = mUnit.getEditableText().toString();
			info.validity = mValidDate.getEditableText().toString();

			// 确认发布信息
			Dialog.Confirm(mContext, R.string.confirm_post_supply_demand_info, new ConfirmListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					WaitListener waitListener = new WaitListener() {
						@Override
						public void startWait() {
							if (DataMan.PostSupplyDemandInfo(info)) {
								Toast.makeText(mContext, R.string.post_supply_demand_info_ok, Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(mContext, R.string.post_supply_demand_info_failed, Toast.LENGTH_LONG).show();
							}
						}
					};

					WaitDialog.Show(mContext, "请稍等", "正在发布信息", waitListener);
				}
			});
		}
	};
	
	/**
	 * 判断输入控件是否为空，为空返回true
	 * @param et
	 * @return
	 */
	private boolean IsEditEmpty(EditText et) {
		Editable editable = et.getEditableText();
		if (editable == null || editable.toString().length() == 0)
			return true;
		else
			return false;
	}

	/**
	 * 初始化发布信息内容
	 */
	private Radio mIsSupply;
	private ListBox mProductClass;
	private List<ListItemMap> mProductList;
	private EditText mContent, mValidDate, mAmount, mPrice, mUnit, mHost;
	private LabeText mAddress, mPhone, mMobilePhone, mName;
	
	private TableLayout initCreateInfo() {
		// 新建TableLayout 实例  
        TableLayout tableLayout = new TableLayout(mContext);
		LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	
    	// 全部列自动填充空白处
        //tableLayout.setStretchAllColumns(true);
        tableLayout.setColumnShrinkable(0, true);
        tableLayout.setColumnStretchable(1, true);
    	
        // 用户填写信息
        String[] list = {"供应", "求购"};
        mIsSupply = new Radio(mContext, list, 0); // 默认供应
        // 只初始化一次产品分类列表
        if (mProductList == null)
        	mProductList = DataMan.GetProductClassList();
        mProductClass = new ListBox(mContext, mProductList);
        mContent = new EditText(mContext);
        mValidDate = new EditText(mContext);
        mAmount = new EditText(mContext);
        mPrice = new EditText(mContext);
        mUnit = new EditText(mContext);
        mHost = new EditText(mContext);
        
        // 供求信息输入
        tableLayout.addView(GetTableRow("", mIsSupply), params);
        tableLayout.addView(GetTableRow("产品分类", mProductClass), params);
        tableLayout.addView(GetTableRow("内容", mContent), params);
        tableLayout.addView(GetTableRow("有效期（天）", mValidDate), params);
        tableLayout.addView(GetTableRow("数量", mAmount), params);
        tableLayout.addView(GetTableRow("价格", mPrice), params);
        tableLayout.addView(GetTableRow("单位", mUnit), params);
        tableLayout.addView(GetTableRow("产地", mHost), params);

        // 系统调用用户信息
        mName = new LabeText(UserMan.GetUserName());
        mPhone = new LabeText(UserMan.GetUserPhone());
        mMobilePhone = new LabeText(UserMan.GetUserPhone());
        mAddress = new LabeText(UserMan.GetUserAddress());
        
        tableLayout.addView(GetTableRow("联系人", mName), params);
        tableLayout.addView(GetTableRow("联系电话", mPhone), params);
        tableLayout.addView(GetTableRow("手机", mMobilePhone), params);
        tableLayout.addView(GetTableRow("地址", mAddress), params);

        tableLayout.setGravity(Gravity.CENTER);
        
        return tableLayout;
	}
	
	/**
	 * 标签用的EditText
	 * @author Dengyong
	 */
	private class LabeText extends EditText {
		LabeText(String value) {
			super(mContext);
			setText(value);
			setEnabled(false);
		}
	}
	
	private TableRow GetTableRow(String label, View inputControl) {
		
		TableRow tableRow = new TableRow(mContext);
		LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        // 显示标签
        TextView tv = new TextView(mContext);
        
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
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
