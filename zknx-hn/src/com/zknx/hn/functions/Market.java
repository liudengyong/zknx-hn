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
	
	static final String LEVEL1_TITLE = "����";
	static final String LEVEL2_TITLE = "�г�";
	
	CommonListAdapter  mAdapterArea;
	CommonListAdapter  mAdapterMarket;
	ProductListAdapter mAdapterProduct;
	
	boolean mAddButton = true;
	
	public Market(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);

		loadArea();
	}
	
	/**
	 * ���ص�������
	 */
	void loadArea() {
		WaitDialog.Show(mContext, mLoadAreaListAction);
	}
	
	/**
	 * ��ʼ�������б�
	 * @return
	 */
	void initAreaList() {
		
		WaitDialog.Show(mContext, mLoadAreaListAction);
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterArea, mOnAreaItemClick);
		
		CommonList.Init(listParams, LEVEL1_TITLE);
		
		// Ĭ�ϵ�һ������
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
	 * ��ʼ���г��б�
	 * @param position
	 * �����б��������е�һ������
	 */
	void initMarketList(int position) {
		
		int address_id = mAdapterArea.getItemMapInt(position,  DataMan.KEY_ADDRESS_ID);
		
		setArea(address_id);
		
		WaitDialog.Show(mContext, mLoadMarketListAction);
	}
	
	/**
	 * ��ʼ���г��Ĳ�Ʒ����
	 */
	void initMarketProducts() {
		// ��ʼ���г���ͼ
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[1], mAdapterMarket, mOnMarketItemClick);
		CommonList.Init(listParams, LEVEL2_TITLE);
		
		// Ĭ�ϵ�һ���г�
		initMarketView(0);
	}
	
	OnItemClickListener mOnMarketItemClick = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			
			// ��ʼ����Ʒ�г�����
			initMarketView(position);
		}
	};
	
	/**
	 * ����г���Ʒ������ͼ
	 * @param positon
	 * @return
	 */
	void initMarketView(int position) {
		
		int market_id = mAdapterMarket.getItemMapInt(position, DataMan.KEY_MARKET_ID);
		
		// �������ͼ
		mAdapterProduct = new ProductListAdapter(mContext, DataMan.GetProductList(market_id), mAddButton);
		
		LinearLayout custom = ProductListAdapter.ListHeader(mInflater, "��Ʒ", mAddButton);
		
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
	 * ���ص����б�action
	 */
	WaitDialog.Action mLoadAreaListAction = new WaitDialog.Action() {
		@Override
		public String getMessage() {
			return "���ڼ��ص�ַ�б�";
		}

		@Override
		public void waitAction() {
			mAdapterArea = new CommonListAdapter(mContext, DataMan.GetAddressList());
			mHandler.sendEmptyMessage(MESSAGE_LOADED_ADDRESSS);
		}
	};
	
	/**
	 * �����г��б�action
	 */
	WaitDialog.Action mLoadMarketListAction = new WaitDialog.Action() {
		@Override
		public String getMessage() {
			return "���ڼ����г��б�";
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
