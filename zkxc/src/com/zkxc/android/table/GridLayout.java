package com.zkxc.android.table;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Element;

import com.zkxc.android.R;
import com.zkxc.android.common.Converter;
import com.zkxc.android.common.Debug;
import com.zkxc.android.table.controller.Sum;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.widget.LinearLayout;

public class GridLayout extends ViewGroup {

	Context mContext;
	
	public static String SEP_RECORD = ";";
	public static String SEP_CHILD_RECORD = ":";
	
	/* 手势 */
	private GestureDetector mGesture;
	
	private int mRowCount = 0;
	private int mColCount = 0;
	
	private final static int TABLE_HALF_WIDTH  = (1280 - 10) / 2; // TODO AAA 固定列宽
	private final static int TABLE_HALF_HEIGHT = (800 - 30) / 2; // TODO AAA 固定行宽
	
	private final static int MULTI = 3;
	
	private int[] mColumWidth = null;
	private int[] mRowHeight = null;
	
	//private int mColumWidth = 300;//240; // TODO AAA 固定列宽 100
	//private int mRowHeight  = 69;//80; // TODO AAA 固定行宽 23
	
	private int mFrozenCol = -1;
	private int mFrozenRow = -1;
	
	private int mOffsetX = 0;
	private int mOffsetY = 0;
	
	public GridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
		
		mGesture = new GestureDetector(context, mOnGesture);
	}
	
	/* 触摸事件 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return mGesture.onTouchEvent(ev);
	}
	
	@Override
	public void addView(View child) {
		if (child == null || child.getTag(R.id.tag_cell_tag) == null) return;
		
		super.addView(child);
	}
	
	public Map<String, Object> GetRecords()
	{
		Map<String, Object> records = new HashMap<String, Object>();
		
		// 所有是parent的单元格都不保存记录
		Map<String, Object> parentMap = new HashMap<String, Object>();

		for (int i = 0, N = getChildCount(); i < N; i++) {
			View c = getChildAt(i);
            if (c.getVisibility() == View.GONE) continue;
            
            // 是某单元格的parent，不保存
            if (c.getTag(R.id.tag_cell_parent) != null)
            	continue;

            CellTag tag = (CellTag)c.getTag(R.id.tag_cell_tag);
            LinearLayout cellLayout = (LinearLayout)c;

            String record = GridCell.GetRecord(cellLayout);

            String parent = tag.getParent();
            
            // 是否存在parent
            if (parent != null && parent.length() > 0 && record != null && record.length() > 0) {
            	Object value = records.get(parent);

            	// TODO ZZZ 错误处理
            	String strValue = null;

            	if (value != null) 
            		strValue = (String)value;
            	
            	// TODO AAA parent数据上传
        		if (strValue == null || strValue.length() == 0)
        			strValue = (tag.filed + SEP_CHILD_RECORD + record);
        		else
        			strValue += (SEP_RECORD + tag.filed + SEP_CHILD_RECORD + record);
        		
        		Debug.Log("parent record : " + strValue + ", parent = " + parent);
        		
        		//appendRecord(records, parent, strValue);
        		records.put(parent, strValue);
        		
        		// 只保存给parent，自己不保留，按顺序来
        		//return records;
        		continue;
            }

            // TODO ZZZ 错误处理		
            if ((parent == null || parent.length() == 0) && record != null && record.length() > 0)
            {
            	appendRecord(records, tag.filed, record);
            }
		}

		return records;
	}
	
	public Vector<String> GetLocationRecords()
	{
		Vector<String> records = new Vector<String>();

		for (int i = 0, N = getChildCount(); i < N; i++) {
			View c = getChildAt(i);
            if (c.getVisibility() == View.GONE) continue;

            String record = GridCell.GetLocationRecord((LinearLayout)c);
            if (record ==null) continue;
            
            records.add(record);
		}

		return records;
	}
	
	private void appendRecord(Map<String, Object> records, String filed, String record)
	{
    	Object old = records.get(filed);
    	
    	if (old == null)
    		records.put(filed, record);
    	else
    	{
    		// 如果已经添加过，则返回
    		if (!old.toString().contains(filed))
    			records.put(filed, old.toString() + SEP_RECORD + filed + SEP_CHILD_RECORD + record);
    	}
	}

	void layoutChildren(LayoutType layoutType, boolean bFrozen)
	{
        // 不让表格被拖动到屏幕不能看见的地方, 只滚动到可动宽度和高度的一半
        int commnWidth  = getTableWidth();
        int commnHeight = getTableHeight();
        
        int maxOffsetX = (TABLE_HALF_WIDTH  - commnWidth);
        int maxOffsetY = (TABLE_HALF_HEIGHT - commnHeight);
        
        //Debug.Log("A mOffsetX=" + mOffsetX+", mOffsetY="+mOffsetY);
        
        if (mOffsetX > 0)
        	mOffsetX = 0; // 左边界
        else if (mOffsetX < maxOffsetX)
        	mOffsetX = maxOffsetX; // 右边界
        
        if (mOffsetY > 0)
        	mOffsetY = 0; // 上边界
        else if (mOffsetY < maxOffsetY)
        	mOffsetY = maxOffsetY; // 下边界
        
        //Debug.Log("B mOffsetX=" + mOffsetX+", mOffsetY="+mOffsetY);
        
		for (int i = 0, N = getChildCount(); i < N; i++) {
			View c = getChildAt(i);
            if (c.getVisibility() == View.GONE) continue;

            CellTag tag = (CellTag)c.getTag(R.id.tag_cell_tag);
            
            CellBound cellBound = GetCellBoundFromCellTag(tag);
            
            if (cellBound == null)
            {
            	c.setVisibility(View.GONE);
            }
            else
            {
                int width = cellBound.width;
                int height = cellBound.height;
                
            	if (width != c.getMeasuredWidth() || height != c.getMeasuredHeight()) {
                    c.measure(makeMeasureSpec(width, EXACTLY), makeMeasureSpec(height, EXACTLY));
                }
            	
                int x = cellBound.x;
                int y = cellBound.y;
                
                boolean shouldLayout = false;
                
                // 滚动行
                if (layoutType == LayoutType.SCROLL_ROW)
                {
                	// 非固定行
                	if (tag.rowIndex >= mFrozenRow)
                	{
                		// 非固定行
                		if (!bFrozen)
                			shouldLayout = true;
                	}
                	// 固定行
                	else
                	{
                		// 固定行
                		if (bFrozen)
                			shouldLayout = true;
                	}
                } else if (layoutType == LayoutType.SCROLL_COL)
                {
                	// 非固定列
                	if (tag.colIndex >= mFrozenCol)
                	{
                		// 非固定列
                		if (!bFrozen)
                			shouldLayout = true;
                	}
                	// 固定列
                	else
                	{
                		// 固定列
                		if (bFrozen)
                			shouldLayout = true;
                	}
                }
                
                // 固定行不滚动
				if (tag.rowIndex > mFrozenRow && mOffsetY != 0) {
	                y += mOffsetY;
				}
				// 固定列不滚动
				if (tag.colIndex > mFrozenCol && mOffsetX != 0) {
	                x += mOffsetX;
				}
				
				if (layoutType == LayoutType.SCROLL_NONE || shouldLayout)
				{
					c.layout(x, y, x + width, y + height);
					
					//if (DataMan.DEBUG && false)
					//	Log.w("DDD", "layout, rowIndex = "+tag.rowIndex+ ", colIndex = " + tag.colIndex);
					
					if (bFrozen)
						c.bringToFront();
				}
            }
		}
	}
	
	private int getTableHeight() {
		int sum = 0;
		
		if (mRowHeight == null)
			return 0;
		
		for (int height : mRowHeight)
			sum += height;
		
		return sum;
	}

	private int getTableWidth() {
		int sum = 0;
		
		if (mColumWidth == null)
			return 0;
		
		for (int width : mColumWidth)
			sum += width;
		
		return sum;
	}
	
	private int getCellXByColIndex(int colIndex)
	{
		int sum = 0;
		
		// 之前单元格的宽度和
		for (int i = 0; i < colIndex; ++i)
			sum += mColumWidth[i];
		
		return sum;
	}
	
	private int getCellYByRowIndex(int rowIndex)
	{
		int sum = 0;
		
		// 之前单元格的宽度和
		for (int i = 0; i < rowIndex; ++i)
			sum += mRowHeight[i];
		
		return sum;
	}
	
	private int getCellWidth(int colIndex, int span)
	{
		int sum = 0;
		
		// 合并单元格的宽度
		for (int i = 0; i < span; ++i)
		{
			if (colIndex + i >= mColumWidth.length)
				break;
			
			sum += mColumWidth[colIndex + i];
		}
		
		return sum;
	}
	
	private int getCellHeight(int rowIndex, int span)
	{
		int sum = 0;
		
		// 合并单元格的宽度
		for (int i = 0; i < span; ++i)
		{
			if (rowIndex + i >= mRowHeight.length)
				break;
			
			sum += mRowHeight[rowIndex + i];
		}
		
		return sum;
	}

	enum LayoutType {SCROLL_NONE, SCROLL_ROW, SCROLL_COL};
	
	LayoutType mLayoutType = LayoutType.SCROLL_NONE;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		
		if (mLayoutType == LayoutType.SCROLL_NONE) {
	        // 无顺序排版
	        layoutChildren(mLayoutType, false);
		} else {
	        // 先排版非固定行/列
	        layoutChildren(mLayoutType, false);
	        // 后排版固定行/列
	        layoutChildren(mLayoutType, true);
		}
		
		mLayoutType = LayoutType.SCROLL_NONE;
	}

	private CellBound GetCellBoundFromCellTag(CellTag cellTag) {
		if (cellTag.rowSpan == 0 || cellTag.colSpan == 0)
			return null;
		
		CellBound cellBound = new CellBound();
		
		cellBound.x = getCellXByColIndex(cellTag.colIndex);//mColumWidth * cellTag.colIndex;
		cellBound.y = getCellYByRowIndex(cellTag.rowIndex);//mRowHeight  * cellTag.rowIndex;
		
		cellBound.width  = getCellWidth(cellTag.colIndex, cellTag.colSpan);//mColumWidth * cellTag.colSpan;// - 2;
		cellBound.height = getCellHeight(cellTag.rowIndex, cellTag.rowSpan);//mRowHeight * cellTag.rowSpan;// - 2;	
		
		return cellBound;
	}

	class CellBound {
		public int x = 0;
		public int y = 0;
		public int width = 0;
		public int height = 0;
	}

	public static class CellTag {
		int rowIndex = -1;
		int colIndex = -1;
		int rowSpan = -1;
		int colSpan = -1;
		String filed = null;
		String parent = null;
		
		public CellTag(int _rowIndex, int _colIndex, int _rowSpan, int _colSpan)
		{
			rowIndex = _rowIndex;
			colIndex = _colIndex;
			rowSpan = _rowSpan;
			colSpan = _colSpan;
		}
		
		public CellTag(int _rowIndex, int _colIndex)
		{
			rowIndex = _rowIndex;
			colIndex = _colIndex;
			rowSpan = 1;
			colSpan = 1;
		}
		
		public int GetRowIndex()
		{
			return rowIndex;
		}
		
		public int GetColIndex()
		{
			return colIndex;
		}

		public void setFiled(String _filed) {
			filed = _filed;
		}
		
		public String getFiled() {
			return filed;
		}
		
		public void setParent(String _parent) {
			parent = _parent;
		}
		
		public String getParent() {
			return parent;
		}
	}
	
	/* 手势 */
	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

		/* 滚动事件 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			synchronized (GridLayout.this) {
				
				int moveX = (int) distanceX;
				int moveY = (int) distanceY;
				
				//Log.w("DDD", "moveX = "+moveX+ ", moveY = " + moveY);
				
				// 不同时上下所有滚动
				if (Math.abs(distanceX) >= Math.abs(distanceY)) {
					mOffsetX -= moveX;
					mLayoutType = LayoutType.SCROLL_COL;
				} else {
					mOffsetY -= moveY;
					mLayoutType = LayoutType.SCROLL_ROW;
				}
			}
				
			requestLayout();
			
			return true;
		}
	};

	public void setSize(int rowCount, int colCount, Element tableElement) {
		mRowCount = rowCount;
		mColCount = colCount;
		
		// 初始化宽高
		mColumWidth = new int[mColCount];
		mRowHeight = new int[mRowCount];
		
		Debug.Log("mColCount " + mColCount);
		Debug.Log("mRowCount " + mRowCount);
		
		for (int i = 0; i < mRowCount; ++i)
		{
			mRowHeight[i] = MULTI * Converter.ToInt(tableElement.getAttribute("rowHeight" + i));
			//Debug.Log("mRowHeight " + mRowHeight[i]);
		}
		
		for (int i = 0; i < mColCount; ++i)
		{
			mColumWidth[i] = MULTI * Converter.ToInt(tableElement.getAttribute("colWidth" + i));
			//Debug.Log("mColumWidth " + mColumWidth[i]);
		}
		// 初始化宽高
	}
	
	public void setFrozen(int frozenRow, int frozenCol) {
		mFrozenCol = frozenCol;
		mFrozenRow = frozenRow;
	}

	public void initSumCell() {
		
		// 查找sum控件的单元格
		for (int i = 0, N = getChildCount(); i < N; i++) {
			View c = getChildAt(i);
            if (c.getVisibility() == View.GONE) continue;
            
            Sum sum = GridCell.GetSumView((LinearLayout)c);
            
            if (sum == null)
            	continue; // 查找失败，继续
            
			Object sumTag = sum.getTag(R.id.tag_sum_src);
			
			// 合计类型需要设计监听器
			if (sumTag == null)
				continue; // 没有合计内容，继续
			
			String[] sumSrcs = (String[])sumTag;
			
			if (sumSrcs == null || sumSrcs.length == 0)
				continue; // 错误，继续
			
    		// 查找sum控件的单元格
    		for (int j = 0, M = getChildCount(); j < M; j++) 
    		{
    			View c2 = getChildAt(j);
                if (c2.getVisibility() == View.GONE) continue;
                
                // 自己不能统计自己
                if (c2 == sum) continue;
                
                // 遍历src，设置监听
                for (String src : sumSrcs)
                {
                	Object tag = c2.getTag(R.id.tag_key);
                	if (tag != null) 
                	{
	                	String key = (String)tag;
	                	
		                if (src == key)
		                	GridCell.SetSumListener((LinearLayout)c2, sum);
                	}
                }
    		}
		}
	}
}
