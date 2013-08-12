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
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڼ��ص�ַ�б�";
			}

			@Override
			public void waitAction() {
				mAdapterArea = new CommonListAdapter(mContext, DataMan.GetAddressList());
				mHandler.sendEmptyMessage(MESSAGE_LOADED_ADDRESSS);
			}
		});
	}
	
	/**
	 * ��ʼ�������б�
	 * @return
	 */
	void initAreaList() {

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
	void initMarketList(final int position) {
		
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڼ����г��б�";
			}

			@Override
			public void waitAction() {
				int address_id = mAdapterArea.getItemMapInt(position,  DataMan.KEY_ADDRESS_ID);
				mAdapterMarket = new CommonListAdapter(mContext, DataMan.GetMarketListByArea(address_id));
				mHandler.sendEmptyMessage(MESSAGE_LOADED_MARKETS);
			}
		});
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
	void initMarketView(final int position) {
		
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڼ��ز�Ʒ�б�";
			}

			@Override
			public void waitAction() {
				int market_id = mAdapterMarket.getItemMapInt(position, DataMan.KEY_MARKET_ID);
				
				// �������ͼ
				mAdapterProduct = new ProductListAdapter(mContext, DataMan.GetProductList(market_id), mAddButton);
				
				mHandler.sendEmptyMessage(MESSAGE_LOADED_PRODUCTS);
			}
		});
	}

	/**
	 * ��ʼ����Ʒ�б�
	 */
	void initProductsView() {	
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

	private final static int MESSAGE_LOADED_ADDRESSS = 1;
	private final static int MESSAGE_LOADED_MARKETS  = 2;
	private final static int MESSAGE_LOADED_PRODUCTS = 3;

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
		   case MESSAGE_LOADED_PRODUCTS:
			   initProductsView();
			   break;
		   }
		}
	};
}
