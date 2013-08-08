package com.zknx.hn.functions;

import com.zknx.hn.common.widget.WaitDialog;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.CommonListAdapter;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.ListItemClickListener;
import com.zknx.hn.functions.common.ProductListAdapter;
import com.zknx.hn.functions.common.CommonList.CommonListParams;

import android.os.Handler;
import android.os.Message;
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

		loadArea();
	}
	
	/**
	 * 加载地区数据
	 */
	void loadArea() {
		WaitDialog.Show(mContext, mLoadAreaListAction);
	}
	
	/**
	 * 初始化地区列表
	 * @return
	 */
	void initAreaList() {
		
		WaitDialog.Show(mContext, mLoadAreaListAction);
		
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
		
		setArea(address_id);
		
		WaitDialog.Show(mContext, mLoadMarketListAction);
	}
	
	/**
	 * 初始化市场的产品行情
	 */
	void initMarketProducts() {
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
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				mAdapterProduct.clickListItem(view);
			}
		};
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[2], mAdapterProduct, listener);
		
		CommonList.Init(listParams, custom);
	}
	
	private int mCurAddressId = DataMan.INVALID_ID;

	private void setArea(int addressId) {
		mCurAddressId = addressId;
	}
	
	/**
	 * 加载地区列表action
	 */
	WaitDialog.Action mLoadAreaListAction = new WaitDialog.Action() {
		@Override
		public String getMessage() {
			return "正在加载地址列表";
		}

		@Override
		public void waitAction() {
			mAdapterArea = new CommonListAdapter(mContext, DataMan.GetAddressList());
			mHandler.sendEmptyMessage(MESSAGE_LOADED_ADDRESSS);
		}
	};
	
	/**
	 * 加载市场列表action
	 */
	WaitDialog.Action mLoadMarketListAction = new WaitDialog.Action() {
		@Override
		public String getMessage() {
			return "正在加载市场列表";
		}

		@Override
		public void waitAction() {
			mAdapterMarket = new CommonListAdapter(mContext, DataMan.GetMarketListByArea(mCurAddressId));
			mHandler.sendEmptyMessage(MESSAGE_LOADED_MARKETS);
		}
	};

	private final static int MESSAGE_LOADED_ADDRESSS = 1;
	private final static int MESSAGE_LOADED_MARKETS  = 2;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg){
		   super.handleMessage(msg);

		   switch (msg.what) {
		   case MESSAGE_LOADED_ADDRESSS:
			   initAreaList();
			   break;
		   case MESSAGE_LOADED_MARKETS:
			   initMarketProducts();
			   break;
		   }
		}
	};
}
