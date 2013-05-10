package com.zknx.hn;

import java.util.List;

import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;
import com.zknx.hn.home.FunctionClass;
import com.zknx.hn.home.Functions;
import com.zknx.hn.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class Home extends Activity {

	ViewFlipper mViewFlipper;
	
	List<RelativeLayout> mFunctionClassList;
	List<LinearLayout> mFunctionList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        LayoutInflater inflater = getLayoutInflater();
        
        // 添加主功能按钮
        mFunctionClassList = FunctionClass.GetMainFunctionList(inflater, mOnClickListener);
        
        LinearLayout groupMainFunctions = (LinearLayout)findViewById(R.id.group_main_functions);
        
        for (RelativeLayout mainFunction : mFunctionClassList) {
        	groupMainFunctions.addView(mainFunction, UIConst.GetLayoutParams(L_LAYOUT_TYPE.W_WEIGHT_1));
        }
        
        // 添加子功能按钮
        mViewFlipper = (ViewFlipper)findViewById(R.id.flipper_sub_functions);
        
        mFunctionList = Functions.GetSubFunctionList(inflater, mOnClickListener);
        
        mViewFlipper.addView(mFunctionList.get(0), -1);
        mViewFlipper.addView(mFunctionList.get(1), -1);
        
        //Debug.ExtractTestDataFile(this);
    }
    
    void switchFunctionClassView(int id) {
    	if (id == UIConst.FUNCTION_CLASS_ID_ZKNX && mViewFlipper.getCurrentView() != mFunctionList.get(0)) {
    		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in));
    		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out));
    		mViewFlipper.showNext();
    	} else if (id == UIConst.FUNCTION_CLASS_ID_PARTY &&  mViewFlipper.getCurrentView() != mFunctionList.get(1)) {
    		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in));
    		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out));
    		mViewFlipper.showPrevious();
    	}
    }
    
    /*
    private void startActivity(String packageName) {
    	Intent intent = new Intent();
    	intent.setPackage(packageName);
    	startActivity(intent);
    }
    */

    OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case UIConst.FUNCTION_CLASS_ID_TV:
				//startActivity(UIConst.FUNCTIONS_TV[0]);
				/*
				// 获取屏幕密度（方法2）  
				DisplayMetrics dm = new DisplayMetrics();  
				dm = getResources().getDisplayMetrics();  
				
				float density  = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）  
				int densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320） 
				Toast.makeText(Home.this, "请第三方提供程序接口" + densityDPI + ", " + density, Toast.LENGTH_LONG).show();
				*/
				
				Toast.makeText(Home.this, "请第三方提供程序接口", Toast.LENGTH_LONG).show();
				break;
			case UIConst.FUNCTION_CLASS_ID_ZKNX:
			case UIConst.FUNCTION_CLASS_ID_PARTY:
				switchFunctionClassView(id);
				break;
			default:
				Function.StartFunctionActivity(Home.this, id);
				break;
			}
		}
    };
}
