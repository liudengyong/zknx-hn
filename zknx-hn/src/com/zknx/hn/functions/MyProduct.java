package com.zknx.hn.functions;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.zknx.hn.R;
import com.zknx.hn.common.Dialog;
import com.zknx.hn.common.Dialog.ConfirmListener;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.ListItemClickListener;
import com.zknx.hn.functions.common.MyProductListAdapter;
import com.zknx.hn.functions.common.PriceChart;
import com.zknx.hn.functions.common.ProductListAdapter;
import com.zknx.hn.functions.common.CommonList.CommonListParams;
import com.zknx.hn.functions.common.ProductPriceInfo;

public class MyProduct extends FunctionView {

	private MyProductListAdapter  mAdapterMyProduct;
	private ProductListAdapter mMarketAdapter;
	
	private LinearLayout mProductListLayout;
	private LinearLayout mChartLayout;
	
	private static final String LEVEL1_TITLE = "��ѡ��Ʒ";
	
	private int mCurProductId = -1;
	
	private final static boolean IS_NEED_CHECKBOX = false;
	
	public MyProduct(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		initMyProductList();
	}
	
	/**
	 * ��ʼ����Ʒ�б���ͼ
	 * @param product_id
	 * @return
	 */
	private void initMyProductList() {
		
		mAdapterMyProduct = new MyProductListAdapter(mContext, DataMan.GetMyProductList(), mOnClickRemove);
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterMyProduct, mOnMyProductClick);
		
		CommonList.Init(listParams, LEVEL1_TITLE);
		
		// ��ʼ����Ʒ��ͼ���
		initProductFrame();
		
		// Ĭ�ϵ�һ����Ʒ
		initProductView(0);
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
			initProductView(position);
		}
	};
	
	/**
	 * ��Ӳ�Ʒ������ͼ�������г���
	 * @param product_id
	 * @return
	 */
	private void initProductView(int position) {

		mCurProductId = mAdapterMyProduct.getItemMapInt(position, DataMan.KEY_PRODUCT_ID);
		
		// ����г��б�
		mMarketAdapter = new ProductListAdapter(mContext, DataMan.GetMarketListByProduct(mCurProductId), IS_NEED_CHECKBOX);

		LinearLayout custom = ProductListAdapter.ListHeader(mInflater, "�г�", IS_NEED_CHECKBOX);
		
		CommonListParams listParams = new CommonListParams(mInflater, mProductListLayout, mMarketAdapter, OnMarketClickListener);
		
		CommonList.Init(listParams, custom);
		
		// Ĭ�ϼ۸�����
		initPriceChartView(0);
	}
	
	OnItemClickListener OnMarketClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			initPriceChartView(position);
		}
	};
	
	/**
	 * ��ʼ���۸�����ͼ
	 * @param position
	 * @return
	 */
	private void initPriceChartView(int position) {
		
		int market_id = mMarketAdapter.getItemMapInt(position, DataMan.KEY_MARKET_ID);

		// ��Ӽ۸�ͼ��
		PriceChart priceChart = null;
		ProductPriceInfo priceInfo = DataMan.GetHistoryPrice(mCurProductId, market_id);
		
		if (priceInfo != null)
			priceChart = new PriceChart(mContext, priceInfo);

		initContent("�۸�����", priceChart, mChartLayout);
	}
	
	/** ����ɾ����ѡ��Ʒ
	 */
    private OnClickListener mOnClickRemove = new OnClickListener() {
		@Override
		public void onClick(final View view) {

			Dialog.Confirm(mContext, R.string.confirm_remove_my_product, new ConfirmListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					int product_id = view.getId();
					DataMan.MyProductListRemove(product_id);

					// XXX ���Ż����Ƿ���Ҫ�ػ�������ͼ����
					initMyProductList();
				}
			});
		}
	};
}
