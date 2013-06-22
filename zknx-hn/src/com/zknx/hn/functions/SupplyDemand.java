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
	
	CommonListAdapter mAdapterProductClass; // ��Ʒ����
	CommonListAdapter mAdapterInfo; // ������Ϣ
	
	ListView mListViewInfo;
	Button mBtnSupply;
	Button mBtnDemand;
	
	private static final String LEVEL1_TITLE = "�������";

	public SupplyDemand(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		initProductClassList();
	}
	
	/**
	 * ��ʼ����Ʒ����
	 */
	void initProductClassList() {
		mAdapterProductClass = new CommonListAdapter(mContext, DataMan.GetProductClassList());
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterProductClass, mOnProductClassClick);
		
		CommonList.Init(listParams, LEVEL1_TITLE);
		
		// ��ʼ����Ϣ������
		initInfoFrame();
		
		// Ĭ�ϵ�һ���Ʒ, ��Ӧ��Ϣ
		initInfoList(0, mIsCurrentSuply);
	}
	
	ListItemClickListener mOnProductClassClick = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			
			// Ĭ�ϳ�ʼ����Ӧ��Ϣ
			initInfoList(position, true);
			
			mCurrentProductClassPosition = position;
		}
	};
	
	/**
	 * ��ʼ����Ϣ������
	 */
	void initInfoFrame() {
		// ��Ϣ���ࣨ��Ӧ��Ϣ��������Ϣ��
		LinearLayout infoType = (LinearLayout)mInflater.inflate(R.layout.supply_demand_class, null);
		infoType.setLayoutParams(UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
		
		mBtnSupply = (Button)infoType.findViewById(R.id.supply_demand_btn_supply);
		mBtnDemand = (Button)infoType.findViewById(R.id.supply_demand_btn_demand);
		
		mBtnSupply.setOnClickListener(OnClickInfoType);
		mBtnDemand.setOnClickListener(OnClickInfoType);
		
		// ��ʼ����Ϣ�б�
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[1], mAdapterInfo, mOnInfoClick);
		mListViewInfo = CommonList.Init(listParams, "������Ϣ", infoType);
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
				Debug.Log("���ش���OnClickInfoType");
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
	 * �ȴ���������
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
	
	// �ȴ���������
	private GetSupplyDemandListListener mGetSupplyDemandListListener = new GetSupplyDemandListListener();
	
	// ������Ϣ
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
	 * ���Թ�����Ϣ�б�
	 */
	private void refreshInfoList() {
		boolean supply = mGetSupplyDemandListListener.supply;
   
		mListViewInfo.setAdapter(mAdapterInfo);
			
		mBtnSupply.setEnabled(!supply);
		mBtnDemand.setEnabled(supply);
			
		// Ĭ�ϵ�һ����Ϣ
		initSupplyDemandView(0, supply);
	}
	
	/**
	 * ��ʼ����Ӧ��������Ϣ
	 * @param suppy
	 */
	void initInfoList(int position, boolean supply) {
		
		mGetSupplyDemandListListener.position = position;
		mGetSupplyDemandListListener.supply = supply;
		
		WaitDialog.Show(mContext, mHandler, REFRESH_INFO_LIST, "��ȡ��������", mGetSupplyDemandListListener);
	}
	
	void initSupplyDemandView(int position, boolean supply) {
		
		//int supply_demand_id = mAdapterInfo.getItemMapInt(position, DataMan.KEY_SUPPLY_DEMAND_INFO_ID);
		
		// �½�TableLayout ʵ��  
        TableLayout tableLayout = new TableLayout(mContext);
        
        ListItemMap mapItem = mAdapterInfo.getItem(position);
        
        String title = GetSupplyDemandInfo(mapItem, tableLayout);
        
		initContent(title, tableLayout, mContentFrame[2]);
	}
	
	/**
	 * ��ȡ������Ϣ��ͼ
	 * @return
	 * �����Ƿ�ɹ��������ر��⣬����ɹ����������ͼ��TableLayout���������
	 */
	public static String GetSupplyDemandInfo(ListItemMap info, TableLayout tableLayout) {
		Context cxt = tableLayout.getContext();
		
		String title = "����Ϣ";
        
        if (info != null) {
	        // ����б�����
        	title = info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_TITLE).toString();
        	LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        	
        	// ȫ�����Զ����հ״�
            tableLayout.setStretchAllColumns(true);
            // ������ϸ��Ϣ
        	String value = info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_MESSAGE).toString();
        	if (value.length() > 0)
        		tableLayout.addView(GetTableRow(cxt, "����", value), params);
        	
	        tableLayout.addView(GetTableRow(cxt, "����ʱ��", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_POST_TIME).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "��Ч��", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_INVALIDATE_DATE).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "����", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_AMOUNT).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "����", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_PRICE).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "����", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_HOST).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "��ϵ��", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_CONTACT_NAME).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "��ϵ�绰", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_CONTACT_TEL).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "�ֻ�", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_CONTACT_PHONE).toString()), params);
	        tableLayout.addView(GetTableRow(cxt, "��ַ", info.get(DataMan.SUPPLY_DEMAND_INFO_KEY_CONTACT_ADDRESS).toString()), params);
	        
	        tableLayout.setGravity(Gravity.CENTER);
        }
        
        return title;
	}
	
	static TableRow GetTableRow(Context context, String label, String data) {
		
		TableRow tableRow = new TableRow(context);
        // ��ʾ��ǩ
        TextView tv = new TextView(context);
        
        tv.setGravity(Gravity.RIGHT);
        tv.setText(label);
        tv.setMinimumWidth(100);
        tv.setPadding(0, 0, 10, 0); // �ұ�padding 10
        tv.setTextSize(22);
        
        tableRow.addView(tv);
        
        // ��ʾ����
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
