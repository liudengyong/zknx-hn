package com.zknx.hn.functions;

import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.CommonListAdapter;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.ListItemClickListener;
import com.zknx.hn.functions.common.ProductListAdapter;
import com.zknx.hn.functions.common.CommonList.CommonListParams;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

public class Market extends FunctionView {
	
	static final String LEVEL1_TITLE = "地区";
	static final String LEVEL2_TITLE = "市场";
	
	CommonListAdapter  mAdapterArea;
	CommonListAdapter  mAdapterMarket;
	ProductListAdapter mAdapterProduct;
	
	boolean mAddButton = true;
	
	public Market(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);

		initAreaList();
	}
	
	/**
	 * 初始化地区列表
	 * @return
	 */
	void initAreaList() {
		
		mAdapterArea = new CommonListAdapter(mContext, DataMan.GetAddressList());
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterArea, mOnAreaItemClick);
		
		CommonList.Init(listParams, LEVEL1_TITLE);
		
		// 默认第一个地区
		initMarketList(0);
	}
	
	OnItemClickListener mOnAreaItemClick = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			
			initMarketList(position);
		}
	};
	
	/**
	 * 初始化市场列表
	 * @param position
	 * 地区列表适配器中第一个地区
	 */
	void initMarketList(int position) {
		
		int address_id = mAdapterArea.getItemMapInt(position,  DataMan.KEY_ADDRESS_ID);
		
		mAdapterMarket = new CommonListAdapter(mContext, DataMan.GetMarketListByArea(address_id));
		
		// 初始化市场视图
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[1], mAdapterMarket, mOnMarketItemClick);
		CommonList.Init(listParams, LEVEL2_TITLE);
		
		// 默认第一个市场
		initMarketView(0);
	}
	
	OnItemClickListener mOnMarketItemClick = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			
			// 初始化产品市场行情
			initMarketView(position);
		}
	};
	
	/**
	 * 添加市场产品行情视图
	 * @param positon
	 * @return
	 */
	void initMarketView(int position) {
		
		int market_id = mAdapterMarket.getItemMapInt(position, DataMan.KEY_MARKET_ID);
		
		// 添加新视图
		mAdapterProduct = new ProductListAdapter(mContext, DataMan.GetProductList(market_id), mAddButton);
		
		LinearLayout custom = ProductListAdapter.ListHeader(mInflater, "产品", mAddButton);
		
		OnItemClickListener listener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Dialog.Toast(mContext, "点击：" + position);
				
				mAdapterProduct.clickListItem(view);
			}
			
		};
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[2], mAdapterProduct, listener/*TODO 电视遥控器*/);
		
		CommonList.Init(listParams, custom);
	}
}
