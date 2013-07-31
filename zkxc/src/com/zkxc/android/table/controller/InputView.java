package com.zkxc.android.table.controller;

import com.zkxc.android.R;
import com.zkxc.android.act.AppZkxc;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class InputView extends GridView {
	
	private static final int INPUT_BUTTON_WIDTH  = 88;
	private static final int INPUT_BUTTON_HEIGHT = GridView.LayoutParams.WRAP_CONTENT;
	
	// 固定宽度（因为固定列）
	private static final int INPUT_PANEL_WIDTH  = 295;
	// 单个文字的高度 TODO ZZZ 可以根据字体算出高度
	private static final int INPUT_PANEL_BUTTON_HEIGHT = 34;
	
	float x;
	float y;
	float startX;
	float startY;
    
    public interface InputChangeLisener {
    	void OnInputChanged(String value);
    }
    
    public InputView(Context context) {
        super(context);        
    }
    
    private InputChangeLisener mInputChangeLisener = null;
    private InputLabelAdapter mInputButtonAdapter = null;
    
    private static InputView mInputView = null;
    
    public static InputView GetInputView(final Context context) {
    	if (mInputView == null) {
    		mInputView = new InputView(context.getApplicationContext());
    		
    		// TODO ZZZ 固定显示三列
    		mInputView.setNumColumns(3);
    		
    		// 行与行之间的间隔
    		mInputView.setVerticalSpacing(4);
    		
    		// 与外框的间隔
    		mInputView.setPadding(8, 4, 8, 4);
    		
    		mInputView.setBackgroundResource(R.drawable.input_panle_bg);
    	}
    	
    	return mInputView;
    }
    
    public void setInputSource(String[] values) {
    	mInputButtonAdapter = new InputLabelAdapter(this.getContext(), values);
    	
    	setAdapter(mInputButtonAdapter);
    	
        setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(HelloWorld.this, " " + position, Toast.LENGTH_SHORT).show();
                
    			//String value = (String)mInputButtonAdapter.getItem(position);
    			//mInputChangeLisener.OnInputChanged(value);
            }
        });
    }
    
    @Override  
	public boolean onTouchEvent(MotionEvent event) {
    	
	    x = event.getRawX();
	    y = event.getRawY();
	    
	    Log.e("", "x = " + x + ", y = " + y);

	    switch(event.getAction()) {
        case MotionEvent.ACTION_DOWN:	            
            startX = event.getX();
            startY = event.getY();
            break;  
        case MotionEvent.ACTION_MOVE:  
        	updateViewPosition();
            break;  
        case MotionEvent.ACTION_UP:
        	updateViewPosition();  
	        startX = startY = 0;       	 
            break;
	    }
	    return true;
	}

	private void updateViewPosition() {
		
		// 更新浮动窗口位置参数,x是鼠标在屏幕的位置，mTouchStartX是鼠标在图片的位置
		int newX = (int) (x - startX);
		int newY = (int) (y - startY);
		
		AppZkxc.UpdateInputViewPos(this, newX, newY);
	}

	public void setInputChangeLisener(InputChangeLisener inputChangeLisener) {
		mInputChangeLisener = inputChangeLisener;
	}
	
	class InputLabelAdapter extends BaseAdapter {
		
	    private Context mContext;
	    // 存储输入的值    
		private String[] mInputValues = null;
		
	    public InputLabelAdapter(Context c, String[] values) {
	        mContext = c;
	        mInputValues = values;
	    }
	    
	    public int getCount() {
	    	// TODO ZZZ 最多显示三行三列，9个数据
	    	if (mInputValues.length > 9)
	    		return 9;
	    	
	        return mInputValues.length;
	    }
	    
	    public Object getItem(int position) {
	        return mInputValues[position];
	    }
	    
	    public long getItemId(int position) {
	        return position;
	    }
	    
	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	
	        TextView label;
	        if (convertView == null) {
	        	// if it's not recycled, initialize some attributes
	        	label = new TextView(mContext);
	        	
	        	label.setText(mInputValues[position]);
	        	label.setId(position);
	        	
	        	// 排版
	        	label.setLayoutParams(new GridView.LayoutParams(INPUT_BUTTON_WIDTH, INPUT_BUTTON_HEIGHT));
	        	label.setBackgroundResource(R.drawable.input_label_bg);
	        	
	        	// 超出宽度文字显示省略号
	        	label.setSingleLine(true);
	        	label.setEllipsize(android.text.TextUtils.TruncateAt.END);

	        	// 不要充满显示，丑
	        	label.setPadding(4, 4, 4, 4);
	        	
	        	//button.setScaleType(ImageView.ScaleType.CENTER_CROP);
	        	//button.setImageResource(mThumbIds[position]);
	        	
	        	label.setTextColor(Color.DKGRAY);
	        	label.setGravity(Gravity.CENTER);

	        	// 点击监听
	        	label.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						String value = mInputValues[view.getId()];
	        			mInputChangeLisener.OnInputChanged(value);
					}
	            });
	        }  else {
	        	label = (TextView) convertView;
	        }
	        
	        return label;
	    }
	}

	// 宽度固定（因为文字宽度固定）
	public static int GetPanelWidth() {
		return INPUT_PANEL_WIDTH;
	}
	
	// 可以分别显示一二三行
	public int GetPanelHeight() {
		// 一行的高度
		if (mInputButtonAdapter.getCount() <= 3)
			return (INPUT_PANEL_BUTTON_HEIGHT + 2); // 2为位移调整
		// 三行的高度
		else if (mInputButtonAdapter.getCount() >= 7)
			return (3 * INPUT_PANEL_BUTTON_HEIGHT);
		// 两行的高度
		else
			return (2 * INPUT_PANEL_BUTTON_HEIGHT);
	}
}