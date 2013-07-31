package com.zkxc.android.act;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.zkxc.android.act.frame.Frame;
import com.zkxc.android.common.CmnListAdapter;
import com.zkxc.android.common.CmnListAdapter.ListItemName;
import com.zkxc.android.common.Converter;
import com.zkxc.android.common.Debug;
import com.zkxc.android.common.MessageBox;
import com.zkxc.android.common.MsgBox;
import com.zkxc.android.data.DataMan;
import com.zkxc.android.data.NoteMan;
import com.zkxc.android.data.RecordMan;
import com.zkxc.android.table.FileSelector;
import com.zkxc.android.table.GridLayout;
import com.zkxc.android.table.Parser;
import com.zkxc.android.R;
import com.zkxc.android.table.controller.MediaPicker;
import com.zkxc.android.table.controller.MediaPicker.MediaListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActTable extends Activity implements MediaListener {

	public static class Request {
	    public static final int CAPTURE_IMAGE  = 1;// 照相的requestCode
	    public static final int RECORD_VIDEO   = 2;// 摄像的照相的requestCode
	    public static final int RECORD_SOUND   = 3;// 录音的requestCode
	    public static final int PICK_MEDIA_FILE = 4;// 录音的requestCode
	    
	    public static final int PENDDING_ACT_TABLE = 5;// 采集表 等待登陆成功
	    public static final int PENDDING_ACT_TRACK = 6;// 轨迹记录 等待登陆成功
	};
	
	static final int RECORD_POST_ID   = 0;
	static final int RECORD_EDIT_ID   = 1;
	static final int RECORD_DELETE_ID = 2;
	
    MediaPicker mCurMediaPicker;
    ListView mList;
    Parser mParser;
    
    RecordMan mRecordMan;
    
    CmnListAdapter mAdapter;
    CmnListAdapter mAdapterRecord;
    ListView mListViewRecord;
    
    GridLayout mTableLayout;
    
    boolean mCollectingData = false;
    int mRecordIndex = 0;
    GridLayout mGridLayout;
    
    TextView mTextViewRecordDate;
    Button mBtnSaveModified;
    
    String mCurRecordFileName = null;
    
    // 快捷输入法开关
    Button mBtnShortInputSwitch;
    
    long mDateSort = 0;
    
    public static AppZkxc mApplication;
    
    TextView mTabName;
    TextView mRecordTabName;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AppZkxc.mActivityTable = this;
        
        mApplication = (AppZkxc)getApplicationContext();
        
        // 初始化 解析器
    	if (mParser == null)
    	{
        	mParser = new Parser(this);
        	mRecordMan = new RecordMan();
    	}
        
        String tabId = null;
        
        if (ActSetting.IsOpenLastTab(this) && (tabId = ActSetting.GetLastTabId(this)) != null)
        	InitCollectionOrRecordView(tabId, ActSetting.GetLastTabName(this), null);
        else
        	InitTabListView();
	}
    
    // 初始化备注按钮
    void intiNoteButtons()
    {
    	findViewById(R.id.btn_show_table_note).setOnClickListener(mOnClickListener);
    	findViewById(R.id.btn_add_table_note).setOnClickListener(mOnClickListener);
    }
    
    DatePickerDialog mDatePickerDialog = null;
    
	DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			
			Date date = new Date();
			
			date.setYear(year - 1900);
			date.setMonth(monthOfYear);
			date.setDate(dayOfMonth);
			
			mDateSort = date.getTime();
			
			Debug.Log("onDateSet, mDateSort = " + mDateSort);
			
			// 重新初始化记录列表
			InitRecordList();
		}
	};
    
    // 初始化记录过滤按钮
    void initRecortSortButtons()
    {
    	findViewById(R.id.btn_table_record_sort_all).setOnClickListener(mOnClickListener);
    	findViewById(R.id.btn_table_record_sort_date).setOnClickListener(mOnClickListener);
    }
    
    // 初始化 新建采集 按钮
    void initNewRecordButtons()
    {
	    findViewById(R.id.btn_new_collection).setOnClickListener(mOnClickListener);
		findViewById(R.id.btn_sync_table).setOnClickListener(mOnClickListener);
    }
    
    private int mCurSelection = 0;
    
    // 初始化采集表列表
    void InitTableList()
    {
    	ListView listView = (ListView)findViewById(R.id.table_list);
    	listView.setFocusableInTouchMode(true);
    	
    	mAdapter = DataMan.GetTables(this, AppZkxc.mUserInfo.addrId);

    	listView.setAdapter(mAdapter);
    	
    	// 恢复之前的选中
    	if (mCurSelection < mAdapter.getCount())
    	{
    		mAdapter.setSelectedPosition(mCurSelection);
    	}
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> subView, View view, int pos,	long id)
			{
				if (mAdapter.getItemSelectedIndex() != pos)
				{
					// 选中点击的采集表
					mAdapter.setSelectedPosition(pos);
					// 显示全部日期的记录（清除日期过滤）
					ShowRecordList(mAdapter.getItem(pos));
					
					// 保存选中的序号，返回时重新初始化
					mCurSelection = pos;
				}
			}
    	});
    	
    	// 重新初始化记录列表
		InitRecordList();
    }
    
    void InitRecordList()
    {
    	mListViewRecord = (ListView)findViewById(R.id.lv_record);
    	findViewById(R.id.table_btn_sync_tablelist).setOnClickListener(mOnClickListener);
		
        mListViewRecord.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				
				ListItemName item = mAdapterRecord.getItem(pos);
				ShowRecord((Map<String, Object>)item.getTag());
			}
		});

        mListViewRecord.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  

			@Override
			public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("请选择");

            	ListItemName itemName = mAdapterRecord.getItem(((AdapterContextMenuInfo)menuInfo).position);
            	@SuppressWarnings("unchecked")
        		Map<String, Object> record = (Map<String, Object>)itemName.getTag();

            	boolean pending = (record.get("pending").toString() == "1");

            	// TODO AAA 同步后不可删除，编辑
            	if (pending)
            	{
            		menu.add(0, RECORD_POST_ID, 0, "上传");
	                menu.add(0, RECORD_EDIT_ID, 0, "编辑");
	                menu.add(0, RECORD_DELETE_ID, 0, "删除");
            	}
			}  
        }); 

        // 初始化 上次选择的采集表 
        if (mAdapter != null && mAdapter.getSelectedTableItem() != null)
        {
	    	ShowRecordList(mAdapter.getSelectedTableItem());
    	}
    }

    //长按菜单响应函数  
    @Override  
    public boolean onContextItemSelected(MenuItem item) {  

    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	ListItemName itemName = mAdapterRecord.getItem(info.position);
    	@SuppressWarnings("unchecked")
		Map<String, Object> tag = (Map<String, Object>)itemName.getTag();
    	
        switch (item.getItemId()) {
        case RECORD_POST_ID: // 上传
        	PostOrDeleteRecord(true, tag);
        	break;
        case RECORD_EDIT_ID: // 编辑
			ShowRecord(tag);
        	break;
        case RECORD_DELETE_ID: // 删除
        	PostOrDeleteRecord(false, tag);
        	break;
        }
        
        return super.onContextItemSelected(item);  
    }
    
    // 初始化采集表列表视图
    void InitTabListView() 
    {
    	AppZkxc.CloseInputView(this);
    	
    	setContentView(R.layout.table_list);
        
        Frame.InitFrame(this, getString(R.string.select_table_tip));
        mRecordTabName = (TextView)findViewById(R.id.tv_record_table_name);
        
        // 初始化 采集表 列表
        InitTableList();
        
        // 初始化 新建采集 按钮
        initNewRecordButtons();
    	
        // 初始化记录过滤按钮
        initRecortSortButtons();
        
        // 初始化备注按钮
        intiNoteButtons();
	}

	private void ShowRecord(Map<String, Object> tag) {		
    	ListItemName tagItem = mAdapter.getSelectedTableItem();
    	InitCollectionOrRecordView(tagItem.id, tagItem.name, tag);
	}
	
	private void setTitleWithTableName(String tabName)
	{
		if (tabName != null && mTabName != null)
    	{
			mTabName.setText("(" + tabName + ")");
    	}
	}
	
	private void PostOrDeleteRecord(final boolean post, final Map<String, Object> tag) {
		String msg = "确认" + (post ?  "上传" : "删除") + "这条记录吗？";
		
		MsgBox.YesNo(this, msg, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
		    	ListItemName tagItem = mAdapter.getSelectedTableItem();
		    	if (RecordMan.PostOrDeleteRecord(post, tagItem.id, tag))
		    	{
		    		mDateSort = 0;
			    	InitRecordList();// 重新初始化记录列表
		    	}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void ShowPrevRecord() {
		
		ListItemName item = mAdapterRecord.getItem(mRecordIndex - 1);
		if (item != null)
		{
			--mRecordIndex;
			ShowRecord((Map<String, Object>)item.getTag());
		}
		else
		{
			Toast.makeText(this, "第一条数据", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressWarnings("unchecked")
	private void ShowNextRecord() {
		ListItemName item = mAdapterRecord.getItem(mRecordIndex + 1);
		if (item != null)
		{
			++mRecordIndex;
			ShowRecord((Map<String, Object>)item.getTag());
		}
		else
		{
			Toast.makeText(this, "最后一条数据", Toast.LENGTH_SHORT).show();
		}
	}
	
	void updateShortInputStatus() {
		if (AppZkxc.IsShortInputOn) {
			mBtnShortInputSwitch.setText(R.string.short_input_switch_off);
		} else {
			// 关闭输入法窗口
			mBtnShortInputSwitch.setText(R.string.short_input_switch_on);
			AppZkxc.CloseInputView(this);
		}
		
		Log.e("IsShortInputOn", "IsShortInputOn = " + AppZkxc.IsShortInputOn);
	}
	
	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId())
			{
			case R.id.btn_short_input_switch:
				AppZkxc.IsShortInputOn = !AppZkxc.IsShortInputOn;
				updateShortInputStatus();
				break;
			case R.id.btn_save_modified:
				if (mCollectingData)
				{
					if (mCurRecordFileName == null)
						mCurRecordFileName = DataMan.GetCurDate();
					saveRecord(mCurRecordFileName);
				}
				else
				{
					if (mCurRecordFileName != null)
						saveRecord(mCurRecordFileName);
					else
						Debug.Log("这不应该出现");
				}
				break;
			case R.id.btn_sync_table:
				String err = SyncTableRecord();
				if (err != null)
				{
					Toast.makeText(ActTable.this, "同步错误：" + err, Toast.LENGTH_SHORT).show();
					return;
				}
				break;
			case R.id.btn_new_collection:
				ListItemName tagItem = mAdapter.getSelectedTableItem();
				if (tagItem != null)
					InitCollectionOrRecordView(tagItem.id, tagItem.name, null);
				break;
			case R.id.btn_select_table:
				InitTabListView(); // 重新选中采集表
				break;
			case R.id.tab_btn_next_page:
				if (!mCollectingData)
					ShowNextRecord();
				break;
			case R.id.tab_btn_prev_page:
				ShowPrevRecord();
				break;
			case R.id.btn_add_table_note:
			case R.id.btn_show_table_note:
				editTaleNote(view.getId());
				break;
			case R.id.btn_table_record_sort_all:
				// 初始化 采集表记录 列表
				mDateSort = 0;
		    	InitRecordList();
				break;
			case R.id.btn_table_record_sort_date:
				
				if (mDatePickerDialog == null)
				{
					Date now = new Date(System.currentTimeMillis());
					mDatePickerDialog = new DatePickerDialog(ActTable.this, mDateSetListener, now.getYear()+1900, now.getMonth(), now.getDate());
				}
				
				mDatePickerDialog.show();
				
				break;
			case R.id.table_btn_sync_tablelist:
				err = SyncTableList();
				if (err != null)
				{
					Toast.makeText(ActTable.this, "同步错误：" + err, Toast.LENGTH_SHORT).show();
					return;
				}
				break;
			}
		}
		
		void editTaleNote(int btnId)
		{
			// 取得当前选中表的备注
			final ListItemName item = mAdapter.getSelectedTableItem();
			
			Debug.Log("editTaleNote, item = " + item);
			
			if (item != null)
			{
				String note = NoteMan.GetTableNote(item.id);
				
				if (btnId == R.id.btn_add_table_note)
				{
					final EditText noteEdit = new EditText(ActTable.this);
					
					if (note != null)
						noteEdit.setText(note);
					
					new AlertDialog.Builder(ActTable.this)
					.setTitle("请输入备注")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(noteEdit)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							NoteMan.SaveTableNote(item.id, noteEdit.getEditableText().toString());
						}
					})
					.setNegativeButton(android.R.string.cancel, null).show();
				}
				else if (btnId == R.id.btn_show_table_note)
				{
					MessageBox.Show(ActTable.this, (note != null) ? note : "没有备注");
				}
			}
		}
		
		private String SyncTableList() {
	    	// 网络不通
	    	if (mApplication.getNetworkStatus() < 0) 
	    		return "网络错误";
	    	
	    	if (progressDialog == null)
	    	{
		    	progressDialog = new ProgressDialog(ActTable.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				//progressDialog.setTitle("同步数据...");
				progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
	                @Override
	                public void onDismiss(DialogInterface dialog) {
	                	if (threadSyncData.isAlive())
	                	{
	                		threadSyncData.interrupt();
	                		threadSyncData = null;
	                	}
	                }
	            });
	    	}
			
			progressDialog.show();
			
			// 线程不能启动两次
			threadSyncData = new Thread() {
				public void run() {
					
					// 从服务器下载历史数据
			    	if (DataMan.DownloadTableList() >= 0)
			    	{
			    		String path     = DataMan.GetTableListFilePath();
			    		String fileName = DataMan.TABLE_LIST_FILE_NAME;
			    		
			    		if (path == null)
			    			return;
			    		
			    		String[] lines = DataMan.ReadLines(path + fileName);
			    		
			    		if (lines != null) {
			    			
				    		for (String line : lines) {
				    			
				    			String[] token = line.split(",");
				    			
				    			//21,PA_B_19131-15,1,采集表名字,2012-7-18 21:48:23,xxxxx
				    			if (token == null || token.length < 6)
				    				continue;
				    			
				    			String tableId = token[0];
								DataMan.DownloadTableById(tableId);
								
								NoteMan.PutNoteToServer(tableId);
								NoteMan.GetNoteFromServer(tableId);
							}
			    		}
			    	}
			    	
					progressDialog.cancel();

					// 重新加载采集表列表
					runOnUiThread(returnRes);
				};
			};
			
			if (!threadSyncData.isAlive())
				threadSyncData.start();
	    	
	    	return null;
		}
		
		private String SyncTableRecord() {
	    	// 网络不通
	    	if (mApplication.getNetworkStatus() < 0) 
	    		return "网络错误";
	    	
	    	if (progressDialog == null)
	    	{
		    	progressDialog = new ProgressDialog(ActTable.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				//progressDialog.setTitle("同步数据...");
				progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
	                @Override
	                public void onDismiss(DialogInterface dialog) {
	                	if (threadSyncData.isAlive())
	                	{
	                		threadSyncData.interrupt();
	                		threadSyncData = null;
	                	}
	                }
	            });
	    	}
			
			progressDialog.show();
			
			// Thread can not be started twice
			//if (threadSyncData == null)
			{
				threadSyncData = new Thread() {
					public void run() {
						
						String tableIds = "";
	
						// 从服务器下载历史数据
				    	{
				    		String path     = DataMan.GetTableListFilePath();
				    		String fileName = DataMan.TABLE_LIST_FILE_NAME;
				    		
				    		if (path == null)
				    			return;
				    		
				    		String[] lines = DataMan.ReadLines(path + fileName);
				    		
				    		if (lines != null) {
				    			
					    		for (String line : lines) {
					    			
					    			String[] token = line.split(",");
					    			
					    			//21,PA_B_19131-15,1,采集表名字,2012-7-18 21:48:23,xxxxx
					    			if (token == null || token.length < 6)
					    				continue;
					    			
					    			String tableId = token[0];
					    			
					    			tableIds += "," + tableId;
					    			
									DataMan.DownloadTableById(tableId);
				
							    	// 取得所有采集表历史记录
							    	RecordMan.GetRecordsFromServer(tableId);
								}
				    		}
				    	}
				    	
						// 上传位置记录文件
				    	// 单个文件上传
				    	//RecordMan.UploadLocationFile(ActTable.this);

				    	// 上传采集记录
				    	if (RecordMan.PostRecords(tableIds) != 0)
				    	{
				    		errMsg = "上传采集表记录错误";
				    	}

						progressDialog.cancel();

						// 重新加载采集表列表
						runOnUiThread(returnRes);
					};
				};
			}

			if (!threadSyncData.isAlive())
				threadSyncData.start();

	    	return null;
		}
	};

	ProgressDialog progressDialog = null;
	Thread threadSyncData = null;
	String errMsg = null;

	Runnable returnRes = new Runnable() {
		public void run() {
			if (errMsg != null)
			{
				Toast.makeText(ActTable.this, errMsg, Toast.LENGTH_SHORT).show();
				errMsg = null;
			}
			//在这里更新UI
			InitTableList();
		}
	};

	void InitTabActionBtn(boolean collectingData)
    {
		Button btnSelectTable = (Button)findViewById(R.id.btn_select_table);
		btnSelectTable.setOnClickListener(mOnClickListener);
		
		Button btnNextPage = (Button)findViewById(R.id.tab_btn_next_page);
		btnNextPage.setOnClickListener(mOnClickListener);
	
		Button btnPrevPage = (Button)findViewById(R.id.tab_btn_prev_page);
		btnPrevPage.setOnClickListener(mOnClickListener);
    	
    	Debug.Log("collectingData = " + collectingData);
    	
    	// 采集数据界面时，隐藏“下一页”按钮
    	btnNextPage.setVisibility(collectingData ? View.INVISIBLE : View.VISIBLE);
		// 采集数据界面时，隐藏“上一页”按钮
		btnPrevPage.setVisibility(collectingData ? View.INVISIBLE : View.VISIBLE);

    	// “保存修改” 和 “保存记录” 共用一个Button排版
    	mBtnSaveModified.setText(collectingData ? R.string.save_record : R.string.save_modified);
    	
    	mCollectingData = collectingData;
    }

	void saveRecord(String fileName)
	{
		ListItemName tagItem = mAdapter.getSelectedTableItem();
		Map<String, Object> records = mGridLayout.GetRecords();
		
		if (records.size() == 0 || !RecordMan.SaveRecord(tagItem, records, mGridLayout.GetLocationRecords(), fileName))
			Toast.makeText(this, R.string.save_failed, Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, R.string.save_ok, Toast.LENGTH_SHORT).show();
	}
    
	void ShowRecordList(ListItemName itemName)
	{
		if (itemName == null)
			return;
		
		if (mRecordTabName != null)
			mRecordTabName.setText(itemName.name);
		
		String tabId = itemName.id;
		Vector<Map<String, Object>> records = mRecordMan.getRecords(tabId);
		
		mAdapterRecord = new CmnListAdapter(this);
		
		//Debug.Log("ShowRecordList, mDateSort = " + mDateSort);
		
		if (records != null)
		{
			for (int i = 0; i < records.size(); ++i)
			{
				Map<String, Object> record = records.get(i);
				
				// 文件名是日期的原始格式
				// TODO ZZZ 日期过滤规则（该日期之前的记录）
				if (mDateSort == 0 || mDateSort >= DataMan.GetLongDate((String)record.get(RecordMan.RECORD_TIME)))
					mAdapterRecord.addItem(record);
			}
		}
		
		mAdapterRecord.sortByName(false);
		
		mListViewRecord.setAdapter(mAdapterRecord);
	}
	
    void InitCollectionOrRecordView(String tabId, String tabName, Map<String, Object> record)
    {
        setContentView(R.layout.table);
        
        Frame.InitFrame(this, getString(R.string.data_colleection));
        
        mTabName = (TextView)findViewById(R.id.tv_cole_tab_name);
        mTabName.setTextColor(Color.BLACK);
        
        mTextViewRecordDate = (TextView)findViewById(R.id.tv_record_date);
        mBtnSaveModified = (Button)findViewById(R.id.btn_save_modified);
        mBtnShortInputSwitch = (Button)findViewById(R.id.btn_short_input_switch);
        mBtnShortInputSwitch.setOnClickListener(mOnClickListener);
        
        updateShortInputStatus();
        
        mBtnSaveModified.setOnClickListener(mOnClickListener);
        mBtnSaveModified.setEnabled(true);
        
        AppZkxc.mSyncedRecord = false;
        
        if (record == null)
        {
        	// 初始化采集表日期（名字）为空
        	mCurRecordFileName = null;
        	
        	mCurRecordFileName = null;
	        mTextViewRecordDate.setVisibility(View.INVISIBLE);
        }
        else
        {
        	String dateHuman = Converter.DateToHuman(record.get(RecordMan.RECORD_TIME).toString());
        	
        	Debug.Log("InitCollectionOrRecordView, pending should be 1 = " + record.get(RecordMan.PENDING).toString());
        	
        	// 也可以修改同步过的数据？
    		// 保存日期，用于计算修改时保存文件名
        	if (record.get(RecordMan.PENDING).toString() == "1")
        	{
        		mCurRecordFileName = record.get(RecordMan.FILE_NAME).toString();
        	}
        	else
        	{
        		AppZkxc.mSyncedRecord = true;
        		mBtnSaveModified.setEnabled(false);
        	}
    		
	        mTextViewRecordDate.setVisibility(View.VISIBLE);
	        mTextViewRecordDate.setText(dateHuman);
        }
        
        InitTabActionBtn(record == null); // 初始化左右按钮的文字
        
        mGridLayout = (GridLayout)findViewById(R.id.collection_table);
          
        if (mParser.parserTable(tabId))
        {
        	mParser.initTable(this, mGridLayout, record);
        	
        	// 采集时才记录上次采集表
        	if (record == null)
        		ActSetting.SetLastTab(this, tabId, tabName);
        }
        else
    	{
    		// TODO ZZZ 解析采集表错误 处理
    	}
        
        setTitleWithTableName(tabName);
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        super.onActivityResult(requestCode, resultCode, data);

        Debug.Log("RequestCode=" + requestCode);
        Debug.Log("ResultCode=" + resultCode);
        
        if (resultCode == RESULT_OK)
        {
	        switch (requestCode)
	        {
	        case Request.PICK_MEDIA_FILE: // 选取媒体文件
	        case Request.CAPTURE_IMAGE:   // 拍照
	        case Request.RECORD_VIDEO:    // 拍摄视频
	        case Request.RECORD_SOUND:    // 录音
	        {
		        mCurMediaPicker.processMediaPicker(requestCode, data);
	        }
	        break;
	        }
        }
    }

	@Override
	public void startMediaActivity(FileSelector fileSelector, Intent intent, int requestCode, MediaPicker mediaPicker) {
		mCurMediaPicker = mediaPicker;
		
		AppZkxc.mCurMediaPicker = mCurMediaPicker;
		
		if (fileSelector == null)
			startActivityForResult(intent, requestCode);
		else
			startActivityFromChild(fileSelector, intent, requestCode);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		AppZkxc.CloseInputView(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		AppZkxc.CloseInputView(this);
	}
	
	@Override
    public void onBackPressed()
    {
		ListView listView = (ListView)findViewById(R.id.table_list);

		if (listView != null)
			super.onBackPressed();
		else
			InitTabListView();
    }
}
