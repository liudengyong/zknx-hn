package com.zknx.hn.functions.common;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.ListItemMap;

public class ProductListAdapter extends CommonListAdapter {
	
	// ��Ҫ��ʾ��Item���ֶ�Ӧ��Map��Key
	public static String KEY_PRODUCT_NAME  = "product_name";
	public static String KEY_PRICE_MIN     = "price_min";
	public static String KEY_PRICE_MAX     = "price_max";
	public static String KEY_PRICE_AVERAGE = "price_average";
	public static String KEY_PRICE_HOME    = "price_home";
	public static String KEY_PRICE_UNIT    = "price_unit";
	public static String KEY_ADD_CUSTOM    = "add_custom";
	
	private boolean mAdd;
	
	public ProductListAdapter(Context context, List<ListItemMap> data, boolean add) {
		super(context, data, R.layout.product_list_item, 
				new String[] {
				KEY_PRODUCT_NAME,
				KEY_PRICE_MIN,
				KEY_PRICE_MAX,
				KEY_PRICE_AVERAGE,
				KEY_PRICE_HOME,
				KEY_PRICE_UNIT,
				KEY_ADD_CUSTOM}, 
				new int[] {
				R.id.product_list_item_name, // ��Ʒ���ֻ����г�����
				R.id.product_list_item_price_min,
				R.id.product_list_item_price_max,
				R.id.product_list_item_price_average,
				R.id.product_list_item_price_home,
				R.id.product_list_item_price_unit,
				R.id.product_list_item_add_custom_chk});
		
		mAdd = add;
		
		// ʵ��Button�����ݣ��ı䱳����
		setViewBinder(mBinder);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		// ֻ���һ��
		if (convertView == null) {
			LinearLayout addCustom = (LinearLayout)view.findViewById(R.id.product_list_item_add_custom);
			TextView addCustomTv = (TextView)view.findViewById(R.id.product_list_item_add_custom_tv);
			
			// id������position���������ɾ����ѡ��Ʒʱ���Ҳ�Ʒid������
			view.setId(position);
			
			if (mAdd) {
				// ���ر�ͷ����
				addCustomTv.setVisibility(View.GONE);
			}
			else
				addCustom.setVisibility(View.GONE);
		}

		return view;
	}
	
	public static LinearLayout ListHeader(LayoutInflater inflater, String nameTitle, boolean add) {

		LinearLayout h = (LinearLayout)inflater.inflate(R.layout.product_list_item, null);
		
		// ͷ���ܱ�ѡ��
		h.setFocusable(false);
		h.setBackgroundResource(R.drawable.tab_button_bg);

		// "����", "��ͼ�", "��߼�", "ƽ����", "���ؼ�", "��λ", "���"
		// ��ʼ����ͷ��ǩ
		((TextView)h.findViewById(R.id.product_list_item_name)).setText(nameTitle);
		((TextView)h.findViewById(R.id.product_list_item_price_min)).setText("��ͼ�");
		((TextView)h.findViewById(R.id.product_list_item_price_max)).setText("��߼�");
		((TextView)h.findViewById(R.id.product_list_item_price_average)).setText("ƽ����");
		((TextView)h.findViewById(R.id.product_list_item_price_home)).setText("���ؼ�");
		((TextView)h.findViewById(R.id.product_list_item_price_unit)).setText("��λ");

		// �б�ͷ������Ӱ�ť���Ƿ�ռ�ÿռ䣨���룩
		if (add) {
			((TextView)h.findViewById(R.id.product_list_item_add_custom_tv)).setText("��ѡ");
			h.findViewById(R.id.product_list_item_add_custom_chk).setVisibility(View.GONE);
		}
		else
			h.findViewById(R.id.product_list_item_add_custom).setVisibility(View.GONE);

		return h;
	}
	
	/**
	 * ����б���ѡ
	 * @param view
	 */
	public void clickListItem(View view) {
		clickCheckbox(view.findViewById(R.id.product_list_item_add_custom_chk));
	}
	
	/**
	 * ��������checkbox���ݰ�
	 */
	SimpleAdapter.ViewBinder mBinder = new SimpleAdapter.ViewBinder() {

        @Override
        public boolean setViewValue(final View view, Object data, String textRepresentation) {
        	if (view.getId() == R.id.product_list_item_add_custom_chk) {
            //if (view instanceof ImageView) {
        		
            	String checked = data.toString();

        		// ��ʼ��checkbox
        		view.setTag(checked);

            	setCheckMyPriductStatus(view, Boolean.parseBoolean(checked));
            	view.setOnClickListener(mAddMyProduct);
                
                return true;
            }

            return false;
        }
    };
    
    /**
     * �������ɾ���ҵ���ѡ��Ʒ
     */
    OnClickListener mAddMyProduct = new OnClickListener() {
		@Override
		public void onClick(View view) {
			clickCheckbox(view);
		}
	};
	
	/**
	 * ���checkbox
	 * @param view
	 */
	private void clickCheckbox(View view) {
		if (view.getTag() != null) {
			
			// ȡ����check��ĳ�uncheck
			boolean checked = !Boolean.parseBoolean(view.getTag().toString());

			// ��ȡListItem�����׵ĸ��ף���Layout�ṹ����
			View parent = (View) view.getParent().getParent();

			int position = parent.getId();

			if (position < getCount() && position >= 0) {
				ListItemMap map = getItem(position);
				
				boolean ok = false;
				int product_id = map.getInt(DataMan.KEY_PRODUCT_ID);

				if (checked)
					ok = DataMan.MyProductListAdd(product_id, map.getString(KEY_PRODUCT_NAME));
				else
					ok = DataMan.MyProductListRemove(product_id);
				
				// ����chengbox״̬��tag
				if (ok) {
					setCheckMyPriductStatus(view, checked);
				}
			} else {
				Debug.Log("���ش���mAddMyProduct�����߼�����2");
			}
		}
	}
	
	/**
	 * �ı�checkbox����
	 * @param checkBox
	 * @param checked
	 */
	private void setCheckMyPriductStatus(View checkBox, boolean checked) {
		if (checkBox instanceof ImageView) {
			ImageView button = (ImageView)checkBox;
			if (button.getTag() != null) {
				checkBox.setTag(checked);
				button.setImageResource(checked ? R.drawable.checkbox_check : R.drawable.checkbox_none);
			}
		}
	}
}