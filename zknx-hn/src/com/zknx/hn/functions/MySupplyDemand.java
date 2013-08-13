package com.zknx.hn.functions;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
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
	
	private static final String LEVEL1_TITLE = "�ҵĹ���";
	private static final String LEVEL2_TITLE = "����Խ�";

	CommonListAdapter mAdapterClass; // �������
	CommonListAdapter mAdapterPair;  // ����Խ�
	
	// ������Ϣ��ť
	LinearLayout mPostLayout;

	public MySupplyDemand(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		loadProductClass();
	}
	
	private void loadProductClass() {
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڼ��ز�Ʒ����";
			}

			@Override
			public void waitAction() {
				mAdapterClass = new CommonListAdapter(mContext, DataMan.GetProductClassList());
				mHandler.sendEmptyMessage(MESSAGE_LOADED_PRODUCT_CLASS);
			}
		});
	}

	/**
	 * ��ʼ��������Ϣ����
	 */
	private void initClassList() {
		
		LinearLayout createInfoBtn = initButton("��������Ϣ", mOnClickCreateInfo);
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], mAdapterClass, mOnClassClick);
		
		CommonList.Init(listParams, LEVEL1_TITLE, createInfoBtn);
		
		// Ĭ�ϵ�һ����Ʒ�Խ���Ϣ
		loadPairList(0);
	}
	
	private void loadPairList(final int position) {
		WaitDialog.Show(mContext, new WaitDialog.Action() {
			@Override
			public String getMessage() {
				return "���ڲ��ҶԽ���Ϣ";
			}

			@Override
			public void waitAction() {
				// ��Ʒ����
				String product_class_id = mAdapterClass.getItemMapString(position, DataMan.KEY_PRODUCT_CLASS_ID);
				
				mAdapterPair = new CommonListAdapter(mContext, DataMan.GetSupplyDemandPairList(product_class_id));

				mHandler.sendEmptyMessage(MESSAGE_LOADED_PAIR_INFO_LIST);
			}
		});
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	void initPairList() {
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[1], mAdapterPair, mOnClickPair);
		
		CommonList.Init(listParams, LEVEL2_TITLE);
		
		// ��һ���Խ���Ϣ����
		initSupplyDemandInfo(0);
	}
	
	void initSupplyDemandInfo(int position) {
		
		//int supply_demand_id = mAdapterPair.getItemMapInt(position, DataMan.KEY_SUPPLY_DEMAND_INFO_ID);

		// �½�TableLayout ʵ��  
        TableLayout tableLayout = new TableLayout(mContext);
        
        String title = SupplyDemand.GetSupplyDemandInfo(mAdapterPair.getItem(position), tableLayout);
        
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

	private DataMan.SupplyDemandInfo mInfo;
	
	WaitListener waitListener = new WaitListener() {
		@Override
		public void startWait() {
			final boolean ret = DataMan.PostSupplyDemandInfo(mInfo);
			Activity act = (Activity) mContext;
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (ret) {
						Toast.makeText(mContext, R.string.post_supply_demand_info_ok, Toast.LENGTH_LONG).show();
						clearInfo();
					} else {
						Toast.makeText(mContext, R.string.post_supply_demand_info_failed, Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	};

	/**
	 * ��������������Ϣ��ť
	 */
	OnClickListener mClickPostButton = new OnClickListener() {
		@Override
		public void onClick(View view) {

			// ��鹩����Ϣ��д����
			if (mIsSupply.getCheckedRadioButtonId() == -1 ||
				mProductClass.getSelectedItemPosition() == -1 ||
				IsEditEmpty(mContent) ||
				IsEditEmpty(mValidDate) ||
				IsEditEmpty(mAmount) ||
				IsEditEmpty(mUnit) ||
				IsEditEmpty(mHost)) {
				Toast.makeText(mContext, "���벻��Ϊ��", Toast.LENGTH_LONG).show();
				return;
			}
			
			if (IsEditEmpty(mAddress) ||
				IsEditEmpty(mPhone) ||
				IsEditEmpty(mMobilePhone) ||
				IsEditEmpty(mName)) {
				Toast.makeText(mContext, "������Ϣ������", Toast.LENGTH_LONG).show();
				return;
			}
			
			mInfo = new DataMan.SupplyDemandInfo();
			
			mInfo.type = mIsSupply.getCheckedRadioButtonId();
			// ��ȡ��Ʒ����id
			String productClassId = mProductList.get(mProductClass.getSelectedItemPosition()).getString(DataMan.KEY_PRODUCT_CLASS_ID);
			mInfo.commodityid =  productClassId;
			mInfo.count = mAmount.getEditableText().toString();
			mInfo.place = mHost.getEditableText().toString();
			mInfo.price = mPrice.getEditableText().toString();
			mInfo.publishdate = DataMan.GetCurrentTime(false); // ��������
			mInfo.title = mContent.getEditableText().toString();
			mInfo.unit = mUnit.getEditableText().toString();
			mInfo.validity = mValidDate.getEditableText().toString();

			// ȷ�Ϸ�����Ϣ
			Dialog.Confirm(mContext, R.string.confirm_post_supply_demand_info, new ConfirmListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					WaitDialog.Show(mContext, "���Ե�", "���ڷ�����Ϣ", waitListener);
				}
			});
		}
	};
	
	/**
	 * �����ɹ��������д����
	 */
	private void clearInfo() {
		mAmount.setText("");
		mUnit.setText("");
		mValidDate.setText("");
		mHost.setText("");
		mPrice.setText("");
		mContent.setText("");
	}
	
	/**
	 * �ж�����ؼ��Ƿ�Ϊ�գ�Ϊ�շ���true
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
	 * ��ʼ��������Ϣ����
	 */
	private Radio mIsSupply;
	private ListBox mProductClass;
	private List<ListItemMap> mProductList;
	private EditText mContent, mValidDate, mAmount, mPrice, mUnit, mHost;
	private LabeText mAddress, mPhone, mMobilePhone, mName;
	
	private TableLayout initCreateInfo() {
		// �½�TableLayout ʵ��  
        TableLayout tableLayout = new TableLayout(mContext);
		LayoutParams params = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	
    	// ȫ�����Զ����հ״�
        //tableLayout.setStretchAllColumns(true);
        tableLayout.setColumnShrinkable(0, true);
        tableLayout.setColumnStretchable(1, true);
    	
        // �û���д��Ϣ
        String[] list = {"��Ӧ", "��"};
        mIsSupply = new Radio(mContext, list, 0); // Ĭ�Ϲ�Ӧ
        // ֻ��ʼ��һ�β�Ʒ�����б�
        if (mProductList == null)
        	mProductList = DataMan.GetProductClassList();
        mProductClass = new ListBox(mContext, mProductList);
        mContent = new EditText(mContext);
        mValidDate = new EditText(mContext);
        mAmount = new EditText(mContext);
        mPrice = new EditText(mContext);
        mUnit = new EditText(mContext);
        mHost = new EditText(mContext);
        
        // ������Ϣ����
        tableLayout.addView(GetTableRow("", mIsSupply), params);
        tableLayout.addView(GetTableRow("��Ʒ����", mProductClass), params);
        tableLayout.addView(GetTableRow("����˵��", mContent), params);
        tableLayout.addView(GetTableRow("��Ч�ڣ��죩", mValidDate), params);
        tableLayout.addView(GetTableRow("����", mAmount), params);
        tableLayout.addView(GetTableRow("�۸�", mPrice), params);
        tableLayout.addView(GetTableRow("��λ", mUnit), params);
        tableLayout.addView(GetTableRow("����", mHost), params);

        // ϵͳ�����û���Ϣ
        mName = new LabeText(UserMan.GetUserName());
        mPhone = new LabeText(UserMan.GetUserPhone());
        mMobilePhone = new LabeText(UserMan.GetUserPhone());
        mAddress = new LabeText(UserMan.GetUserAddress());
        
        tableLayout.addView(GetTableRow("��ϵ��", mName), params);
        tableLayout.addView(GetTableRow("��ϵ�绰", mPhone), params);
        tableLayout.addView(GetTableRow("�ֻ�", mMobilePhone), params);
        tableLayout.addView(GetTableRow("��ַ", mAddress), params);

        tableLayout.setGravity(Gravity.CENTER);
        
        return tableLayout;
	}
	
	/**
	 * ��ǩ�õ�EditText
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

        // ��ʾ��ǩ
        TextView tv = new TextView(mContext);
        
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
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
			
			loadPairList(position);
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
	
	// ������Ϣ
	private static final int MESSAGE_LOADED_PRODUCT_CLASS  = 1;
	private static final int MESSAGE_LOADED_PAIR_INFO_LIST = 2;
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg){
		   super.handleMessage(msg);
		   
		   switch (msg.what) {
		   case MESSAGE_LOADED_PRODUCT_CLASS:
			   initClassList();
			   break;
		   case MESSAGE_LOADED_PAIR_INFO_LIST:
			   initPairList();
			   break;
		   }
		}
	};
}
