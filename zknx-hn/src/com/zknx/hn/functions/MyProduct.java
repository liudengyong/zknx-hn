package com.zknx.hn.functions;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.zknx.hn.R;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;
import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.common.widget.WaitDialog;
import com.zknx.hn.common.widget.Dialog.ConfirmListener;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.ListItemClickListener;
import com.zknx.hn.functions.common.MyProductListAdapter;
import com.zknx.hn.functions.common.PriceChart;
import com.zknx.hn.functions.common.ProductListAdapter;
import com.zknx.hn.functions.common.ProductPriceInfo;
import com.zknx.hn.functions.common.CommonList.CommonListParams;

public class MyProduct extends FunctionView {

	private MyProductListAdapter  mAdapterMyProduct;
	private ProductListAdapter mMarketAdapter;
	private ProductPriceInfo mPriceInfo;
	
	private LinearLayout mProductListLayout;
	private LinearLayout mChartLayout;
	
	private static final String LEVEL1_TITLE = "��ѡ��Ʒ";
	
	private String mCurProductId = null;
	
	private final static boolean IS_NEED_CHECKBOX = false;
	
	public MyProduct(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);

		loadMyProducts();
	}
	
	/**
	 * ���ص�������
	 */
	void loadMyProducts() {
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڼ�����ѡ��Ʒ";
			}

			@Override
			public void waitAction() {
				mAdapterMyProduct = new MyProductListAdapter(mContext, DataMan.GetMyProductList(), mOnClickRemove);
				mHandler.sendEmptyMessage(MESSAGE_LOADED_MY_PRODUCT);
			}
		});
	}
	
	/**
	 * ��ʼ����Ʒ�б���ͼ
	 * @param product_id
	 * @return
	 */
	private void initMyProductList() {
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterMyProduct, mOnMyProductClick);
		
		CommonList.Init(listParams, LEVEL1_TITLE);
		
		// ��ʼ����Ʒ��ͼ���
		initProductFrame();
		
		// Ĭ�ϵ�һ����Ʒ
		loadMarketsList(0);
	}
	
	/**
	 * ��ʼ��������ͼ���
	 * @param product_id
	 * @return
	 */
	private void initProductFrame() {

		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.my_product, null);
		
		mProductListLayout  = (LinearLayout)layout.findViewById(R.id.my_product_list_layout);
		mChartLayout = (LinearLayout)layout.findViewById(R.id.my_product_price_chart_layout);

		// ����������������ͼ
		mContentFrame[1].removeAllViews();
		mContentFrame[1].addView(layout, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
	}
	
	private ListItemClickListener mOnMyProductClick = new ListItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long product_id) {
			super.onItemClick(parent, view, position, product_id);
			loadMarketsList(position);
		}
	};
	
	/**
	 * �����г�����
	 */
	private void loadMarketsList(final int position) {
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڼ����г��б�";
			}

			@Override
			public void waitAction() {
				mCurProductId = mAdapterMyProduct.getItemMapString(position, DataMan.KEY_PRODUCT_ID);
				
				// ����г��б�
				mMarketAdapter = new ProductListAdapter(mContext, DataMan.GetMarketListByProduct(mCurProductId), IS_NEED_CHECKBOX);

				mHandler.sendEmptyMessage(MESSAGE_LOADED_MARKETS_BY_PRODUCT);
			}
		});
	}
	
	/**
	 * ��Ӳ�Ʒ������ͼ�������г���
	 * @param product_id
	 * @return
	 */
	private void initProductView() {
		
		LinearLayout custom = ProductListAdapter.ListHeader(mInflater, "�г�", IS_NEED_CHECKBOX);
		
		CommonListParams listParams = new CommonListParams(mInflater, mProductListLayout, mMarketAdapter, OnMarketClickListener);
		
		CommonList.Init(listParams, custom);
		
		// Ĭ�ϼ۸�����
		loadHistoryPrice(0);
	}
	
	OnItemClickListener OnMarketClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			loadHistoryPrice(position);
		}
	};
	
	/**
	 * �����г�����
	 */
	private void loadHistoryPrice(final int position) {
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڼ�����ʷ�۸�";
			}

			@Override
			public void waitAction() {
				String market_id = mMarketAdapter.getItemMapString(position, DataMan.KEY_MARKET_ID);
				
				mPriceInfo = DataMan.GetHistoryPrice(mCurProductId, market_id);

				mHandler.sendEmptyMessage(MESSAGE_LOADED_HISTORY_PRICE);
			}
		});
	}
	
	/**
	 * ��ʼ���۸�����ͼ
	 * @param position
	 * @return
	 */
	private void initPriceChartView() {
		// ��Ӽ۸�ͼ��
		PriceChart priceChart = null;
		
		if (mPriceInfo != null)
			priceChart = new PriceChart(mContext, mPriceInfo);
		
		initContent("�۸�����", priceChart, mChartLayout);
	}
	
	/** ����ɾ����ѡ��Ʒ
	 */
    private OnKeyListener mOnClickRemove = new OnKeyListener() {
		@Override
		public boolean onKey(View view, int arg1, KeyEvent keyEvent) {

			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
				keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			Dialog.Toast(mContext, "HIT");
				final int product_id = view.getId();
				Dialog.Confirm(mContext, R.string.confirm_remove_my_product, new ConfirmListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (product_id != DataMan.INVALID_ID) {
							DataMan.MyProductListRemove(product_id);
							loadMyProducts();
						}
					}
				});
			} else
			
			Dialog.Toast(mContext, "NO HIT:" + keyEvent.getKeyCode());

			return false;
		}
	};
	
	private final static int MESSAGE_LOADED_MY_PRODUCT     = 1;
	private final static int MESSAGE_LOADED_MARKETS_BY_PRODUCT  = 2;
	private final static int MESSAGE_LOADED_HISTORY_PRICE  = 3;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg){
		   super.handleMessage(msg);

		   switch (msg.what) {
		   case MESSAGE_LOADED_MY_PRODUCT:
			   initMyProductList();
			   break;
		   case MESSAGE_LOADED_MARKETS_BY_PRODUCT:
			   initProductView();
			   break;
		   case MESSAGE_LOADED_HISTORY_PRICE:
			   initPriceChartView();
			   break;
		   }
		}
	};
}
