package com.zknx.hn.functions;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.WaitDialog;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;
import com.zknx.hn.common.WaitDialog.WaitListener;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.ListItemMap;
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.CommonListAdapter;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.ListItemClickListener;
import com.zknx.hn.functions.common.CommonList.CommonListParams;

public class SupplyDemand extends FunctionView {
	
	CommonListAdapter mAdapterProductClass; // 产品分类
	CommonListAdapter mAdapterInfo; // 供求信息
	
	ListView mListViewInfo;
	Button mBtnSupply;
	Button mBtnDemand;
	
	private static final String LEVEL1_TITLE = "供求分类";

	public SupplyDemand(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		initProductClassList();
	}
	
	/**
	 * 初始化产品分类
	 */
	void initProductClassList() {
		mAdapterProductClass = new CommonListAdapter(mContext, DataMan.GetProductClassList());
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterProductClass, mOnProductClassClick);
		
		CommonList.Init(listParams, LEVEL1_TITLE);
		
		// 初始化信息分类框架
		initInfoFrame();
		
		// 默认第一类产品, 供应信息
		initInfoList(0, mIsCurrentSuply);
	}
	
	ListItemClickListener mOnProductClassClick = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			
			// 默认初始化供应信息
			initInfoList(position, true);
			
			mCurrentProductClassPosition = position;
		}
	};
	
	/**
	 * 初始化信息分类框架
	 */
	void initInfoFrame() {
		// 信息分类（供应信息或者求购信息）
		LinearLayout infoType = (LinearLayout)mInflater.inflate(R.layout.supply_demand_class, null);
		infoType.setLayoutParams(UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
		
		mBtnSupply = (Button)infoType.findViewById(R.id.supply_demand_btn_supply);
		mBtnDemand = (Button)infoType.findViewById(R.id.supply_demand_btn_demand);
		
		mBtnSupply.setOnClickListener(OnClickInfoType);
		mBtnDemand.setOnClickListener(OnClickInfoType);
		
		// 初始化信息列表
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[1], mAdapterInfo, mOnInfoClick);
		mListViewInfo = CommonList.Init(listParams, "供求信息", infoType);
	}
	
	OnClickListener OnClickInfoType = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();

			switch (id) {
			case R.id.supply_demand_btn_supply:
				mIsCurrentSuply = true;
				break;
			case R.id.supply_demand_btn_demand:
				mIsCurrentSuply = false;
				break;
			default:
				Debug.Log("严重错误：OnClickInfoType");
				return;
			}

			initInfoList(mCurrentProductClassPosition, mIsCurrentSuply);
		}
	};

	ListItemClickListener mOnInfoClick = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			
			initSupplyDemandView(position, mIsCurrentSuply);
		}
	};
	
	private int mCurrentProductClassPosition = 0;
	private boolean mIsCurrentSuply = true;
	
	/**
	 * 等待更新数据
	 * @author John
	 *
	 */
	private class GetSupplyDemandListListener implements WaitListener {
		@Override
		public void startWait() {
			String product_class_id = mAdapterProductClass.getItemMapString(position, DataMan.KEY_PRODUCT_CLASS_ID);
			
			mAdapterInfo = new CommonListAdapter(mContext, DataMan.GetSupplyDemandList(product_class_id, supply));
		}
		
		int position;
		boolean supply;
	}
	
	// 等待更新数据
	private GetSupplyDemandListListener mGetSupplyDemandListListener = new GetSupplyDemandListListener();
	
	// 处理消息
	private static final int REFRESH_INFO_LIST = 0;
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg){
		   super.handleMessage(msg);

		   //WaitDialog.finish(msg.what);
		   
		   if (msg.what == REFRESH_INFO_LIST)
			   refreshInfoList();
		}
	};
	
	/**
	 * 属性供求信息列表
	 */
	private void refreshInfoList() {
		boolean supply = mGetSupplyDemandListListener.supply;
   
		mListViewInfo.setAdapter(mAdapterInfo);
			
		mBtnSupply.setEnabled(!supply);
		mBtnDemand.setEnabled(supply);
			
		// 默认第一个信息
		initSupplyDemandView(0, supply);
	}
	
	/**
	 * 初始化供应或者求购信息
	 * @param suppy
	 */
	void initInfoList(int position, boolean supply) {
		
		mGetSupplyDemandListListener.position = position;
		mGetSupplyDemandListListener.supply = supply;
		
		WaitDialog.Show(mContext, mHandler, REFRESH_INFO_LIST, "读取供求数据", mGetSupplyDemandListListener);
	}
	
	void initSupplyDemandView(int position, boolean supply) {
		
		//int supply_demand_id = mAdapterInfo.getItemMapInt(position, DataMan.KEY_SUPPLY_DEMAND_INFO_ID);
		
		// 新建TableLayout 实例  
        TableLayout tableLayout = new TableLayout(mContext);
        
        ListItemMap mapItem = mAdapterInfo.getItem(position);
        
        String title = GetSupplyDemandInfo(mapItem, tableLayout);
        
		initContent(title, tableLayout, mContentFrame[2]);
	}
	
	/**
	 * 获取供求信息视图
	 * @return
	 * 无论是否成功，都返回标题，如果成功，则添加视图到TableLayout，否则不添加
	 */
	public static String GetSupplyDemandInfo(ListItemMap info, TableLayout tableLayout) {
		Context cxt = tableLayout.getContext();
		
		String title = "无信息";
        
        if (info != null) {
	        // 添加列表数据
        	title = info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_TITLE).toString();
        	LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        	
        	// 全部列自动填充空白处
            tableLayout.setStretchAllColumns(true);
            // 暂无详细信息
        	String value = info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_MESSAGE).toString();
        	if (value.length() > 0)
        		tableLayout.addView(GetTableRow(cxt, "内容", value), params);
        	
	        tableLayout.addView(GetTableRow(cxt, "发布时间", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_POST_TIME).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "有效期", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_INVALIDATE_DATE).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "数量", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_AMOUNT).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "单价", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_PRICE).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "产地", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_HOST).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "联系人", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_CONTACT_NAME).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "联系电话", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_CONTACT_TEL).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "手机", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_CONTACT_PHONE).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "地址", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_CONTACT_ADDRESS).toString()), params);
	        
	        tableLayout.setGravity(Gravity.CENTER);
        }
        
        return title;
	}
	
	static TableRow GetTableRow(Context context, String label, String data) {
		
		TableRow tableRow = new TableRow(context);
        // 显示标签
        TextView tv = new TextView(context);
        
        tv.setGravity(Gravity.RIGHT);
        tv.setText(label);
        tv.setMinimumWidth(100);
        tv.setPadding(0, 0, 10, 0); // 右边padding 10
        tv.setTextSize(22);
        
        tableRow.addView(tv);
        
        // 显示数据
        tv = new TextView(context);
        // tv.setGravity(Gravity.CENTER);
        tv.setAutoLinkMask(Linkify.ALL);
        tv.setSingleLine(false);
        //tv.setMaxWidth(300);
        
        tv.setText(data);
        tv.setTextSize(22);
        //tv.setMovementMethod(LinkMovementMethod.getInstance());
        
        tableRow.addView(tv);
        
        return tableRow;
	}
}
