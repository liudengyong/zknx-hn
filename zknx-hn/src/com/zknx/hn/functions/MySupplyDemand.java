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
	
	private static final String LEVEL1_TITLE = "�ҵĹ���";
	private static final String LEVEL2_TITLE = "����Խ�";

	CommonListAdapter mAdapterClass; // �������
	CommonListAdapter mAdapterPair;  // ����Խ�
	
	// ������Ϣ��ť
	LinearLayout mPostLayout;

	public MySupplyDemand(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		initClassList();
	}

	/**
	 * ��ʼ��������Ϣ����
	 */
	void initClassList() {
		
		LinearLayout createInfoBtn = getLinearLayoutBtn("��������Ϣ", mOnClickCreateInfo);
		
		mAdapterClass = new CommonListAdapter(mContext, DataMan.GetProductClassList());
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterClass, mOnClassClick);
		
		CommonList.Init(listParams, LEVEL1_TITLE, createInfoBtn);
		
		// Ĭ�ϵ�һ����Ʒ�Խ���Ϣ
		initPairList(0);
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	void initPairList(int position) {
		
		// ��Ʒ����
		int product_class_id = mAdapterClass.getItemMapInt(position, DataMan.KEY_PRODUCT_CLASS_ID);
		
		mAdapterPair = new CommonListAdapter(mContext, DataMan.GetSupplyDemandPairList(product_class_id));
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[1], mAdapterPair, mOnClickPair);
		
		CommonList.Init(listParams, LEVEL2_TITLE);
		
		// ��һ���Խ���Ϣ����
		initSupplyDemandInfo(0);
	}
	
	void initSupplyDemandInfo(int position) {
		
		int supply_demand_id = mAdapterPair.getItemMapInt(position, DataMan.KEY_SUPPLY_DEMAND_INFO_ID);

		// �½�TableLayout ʵ��  
        TableLayout tableLayout = new TableLayout(mContext);
        
        String title = SupplyDemand.GetSupplyDemandInfo(supply_demand_id, tableLayout);
        
		initContent(title, tableLayout, mContentFrame[2]);
	}
	
	/**
	 * ��ʼ��������Ϣ���棬�ڶ�����ʾ��Ϣ��������������Ϣ
	 */
	void createInfo() {
		// ����ڶ�����������ӷ�����Ϣ����ʾ
		// ��ʾ
		LinearLayout layoutTip = (LinearLayout)mInflater.inflate(R.layout.create_supply_demand_tip, null);

		initContent(mContext.getString(R.string.create_supply_demand), layoutTip, mContentFrame[1]);
		
		// �������������ӷ�����Ϣ����
		LinearLayout layoutCreate = (LinearLayout)mInflater.inflate(R.layout.create_supply_demand, null);

		// ����
		FrameLayout layoutInfo = (FrameLayout)layoutCreate.findViewById(R.id.create_supply_demand_info);
		layoutInfo.addView(initCreateInfo());
		
		// ������ť
		initPostButton();
		
		initContent(mContext.getString(R.string.create_supply_demand), layoutCreate, mPostLayout, mContentFrame[2]);
	}
	
	/**
	 * ��ʼ��������ť
	 */
	private void initPostButton() {
		if (mPostLayout == null) {
			mPostLayout = (LinearLayout)mInflater.inflate(R.layout.create_supply_demand_post, null);
			
			Button postBtn = (Button)mPostLayout.findViewById(R.id.create_supply_demand_post_btn);
			postBtn.setOnClickListener(mClickPostButton);
		}
	}
	
	/**
	 * ��������������Ϣ��ť
	 */
	OnClickListener mClickPostButton = new OnClickListener() {
		@Override
		public void onClick(View view) {

			// TODO ���Ʒ���������Ϣ����鹩����Ϣ��д����
			
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
	 * ��ʼ��������Ϣ����
	 */
	private TableLayout initCreateInfo() {
		// �½�TableLayout ʵ��  
        TableLayout tableLayout = new TableLayout(mContext);
		LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	
    	// ȫ�����Զ����հ״�
        //tableLayout.setStretchAllColumns(true);
        tableLayout.setColumnShrinkable(0, true);
        tableLayout.setColumnStretchable(1, true);
    	
        tableLayout.addView(GetTableRow("����", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("����ʱ��", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("��Ч��", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("����", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("����", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("����", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("��ϵ��", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("��ϵ�绰", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("�ֻ�", new EditText(mContext)), params);
        tableLayout.addView(GetTableRow("��ַ", new EditText(mContext)), params);

        tableLayout.setGravity(Gravity.CENTER);
        
        return tableLayout;
	}
	
	private TableRow GetTableRow(String label, View inputControl) {
		
		TableRow tableRow = new TableRow(mContext);
		LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        // ��ʾ��ǩ
        TextView tv = new TextView(mContext);
        
        tv.setGravity(Gravity.RIGHT);
        tv.setText(label);
        tv.setMinimumWidth(100);
        tv.setPadding(0, 0, 10, 0); // �ұ�padding 10
        
        tableRow.addView(tv);
        
        // ����ؼ�
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
