package com.zkxc.android.act;

import com.zkxc.android.act.frame.Frame;
import com.zkxc.android.common.CmnListAdapter;
import com.zkxc.android.common.CmnListAdapter.ListItemName;
import com.zkxc.android.data.DataMan;
import com.zkxc.android.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ActProtectionZone extends Activity {
	
	WebView webView;
	CmnListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.protection_zone_list);
        
        Frame.InitFrame(this, getString(R.string.nature_protection_zone));
        
        InitProtectionZoneList();
	}
    
    void InitProtectionZoneList()
    {
    	webView = (WebView)findViewById(R.id.protection_zone);
    	webView.getSettings().setSupportZoom(true);
    	webView.getSettings().setBuiltInZoomControls(true);
    	webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // ��ʹ�û���
    	
    	webView.loadUrl("file://" + DataMan.DATA_FOLDER + "protection_zone_tip.htm"); 

    	ListView listView = (ListView)findViewById(R.id.protection_zone_list);
    	
    	mAdapter = DataMan.GetProtectionZone(this, null/*AppZkxc.mUserInfo.addrId*/);
    	
    	listView.setAdapter(mAdapter);
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> subView, View view, int pos,	long id)
			{
				ProtectionZoneView(pos);
			}
    	});
    }
    
    void ProtectionZoneView(int id)
    {
    	ListItemName listItemName = mAdapter.getItem(id);
        
        Frame.InitFrame(this, listItemName.name);
        
        ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//progressDialog.setTitle("同步数据...");
	
		progressDialog.show();
        InitProtectionZoneView(DataMan.GetProtectionZoneFilePath(listItemName.id));
        progressDialog.dismiss();
    }
    
    void InitProtectionZoneView(String file)
    {
    	/*
    	String baseUrl = "file:///mnt/sdcard/"; 
    	webView.loadDataWithBaseURL(baseUrl, "", "text/html", "utf-8", null);
    	*/

    	webView.loadUrl("file://" + file);
    }
}