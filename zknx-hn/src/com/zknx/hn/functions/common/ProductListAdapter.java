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
	
	// 需要显示的Item文字对应的Map的Key
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
				R.id.product_list_item_name, // 产品名字或者市场名字
				R.id.product_list_item_price_min,
				R.id.product_list_item_price_max,
				R.id.product_list_item_price_average,
				R.id.product_list_item_price_home,
				R.id.product_list_item_price_unit,
				R.id.product_list_item_add_custom_chk});
		
		mAdd = add;
		
		// 实现Button绑定数据（改变背景）
		setViewBinder(mBinder);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		// 只添加一次
		if (convertView == null) {
			LinearLayout addCustom = (LinearLayout)view.findViewById(R.id.product_list_item_add_custom);
			TextView addCustomTv = (TextView)view.findViewById(R.id.product_list_item_add_custom_tv);
			
			// id绑定数据position，便于添加删除自选产品时查找产品id和名字
			view.setId(position);
			
			if (mAdd) {
				// 隐藏表头文字
				addCustomTv.setVisibility(View.GONE);
			}
			else
				addCustom.setVisibility(View.GONE);
		}

		return view;
	}
	
	public static LinearLayout ListHeader(LayoutInflater inflater, String nameTitle, boolean add) {

		LinearLayout h = (LinearLayout)inflater.inflate(R.layout.product_list_item, null);
		
		// 头不能被选中
		h.setFocusable(false);
		h.setBackgroundResource(R.drawable.tab_button_bg);

		// "名字", "最低价", "最高价", "平均价", "产地价", "单位", "添加"
		// 初始化列头标签
		((TextView)h.findViewById(R.id.product_list_item_name)).setText(nameTitle);
		((TextView)h.findViewById(R.id.product_list_item_price_min)).setText("最低价");
		((TextView)h.findViewById(R.id.product_list_item_price_max)).setText("最高价");
		((TextView)h.findViewById(R.id.product_list_item_price_average)).setText("平均价");
		((TextView)h.findViewById(R.id.product_list_item_price_home)).setText("产地价");
		((TextView)h.findViewById(R.id.product_list_item_price_unit)).setText("单位");

		// 列表头隐藏添加按钮，是否占用空间（对齐）
		if (add) {
			((TextView)h.findViewById(R.id.product_list_item_add_custom_tv)).setText("自选");
			h.findViewById(R.id.product_list_item_add_custom_chk).setVisibility(View.GONE);
		}
		else
			h.findViewById(R.id.product_list_item_add_custom).setVisibility(View.GONE);

		return h;
	}
	
	/**
	 * 点击列表自选
	 * @param view
	 */
	public void clickListItem(View view) {
		clickCheckbox(view.findViewById(R.id.product_list_item_add_custom_chk));
	}
	
	/**
	 * 定制特殊checkbox数据绑定
	 */
	SimpleAdapter.ViewBinder mBinder = new SimpleAdapter.ViewBinder() {

        @Override
        public boolean setViewValue(final View view, Object data, String textRepresentation) {
        	if (view.getId() == R.id.product_list_item_add_custom_chk) {
            //if (view instanceof ImageView) {
        		
            	String checked = data.toString();

        		// 初始化checkbox
        		view.setTag(checked);

            	setCheckMyPriductStatus(view, Boolean.parseBoolean(checked));
            	view.setOnClickListener(mAddMyProduct);
                
                return true;
            }

            return false;
        }
    };
    
    /**
     * 监听添加删除我的自选产品
     */
    OnClickListener mAddMyProduct = new OnClickListener() {
		@Override
		public void onClick(View view) {
			clickCheckbox(view);
		}
	};
	
	/**
	 * 点击checkbox
	 * @param view
	 */
	private void clickCheckbox(View view) {
		if (view.getTag() != null) {
			
			// 取反，check则改成uncheck
			boolean checked = !Boolean.parseBoolean(view.getTag().toString());

			// 获取ListItem（父亲的父亲，从Layout结构来）
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
				
				// 更新chengbox状态和tag
				if (ok) {
					setCheckMyPriductStatus(view, checked);
				}
			} else {
				Debug.Log("严重错误：mAddMyProduct处理逻辑错误2");
			}
		}
	}
	
	/**
	 * 改变checkbox背景
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