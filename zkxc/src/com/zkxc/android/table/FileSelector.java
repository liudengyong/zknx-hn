package com.zkxc.android.table;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.zkxc.android.R;
import com.zkxc.android.act.ActTable.Request;
import com.zkxc.android.act.AppZkxc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FileSelector extends Activity {

	private File mCurDir = new File(Environment.getExternalStorageDirectory().getPath());

	ListView mListView;
	ExpBaseAdapter mAdapter;
	
	String mFileEndings[] = {"apk", "png", "bmp", "jpg", "jpeg", "avi", "3gp", "mp3", "mp4" };

	FilenameFilter mFilenameFilter = new FilenameFilter() {
		public boolean accept(File file, String path) {
			
			/*
			if (file.isDirectory())
				return true;

			File f = new File(path);
			String name = f.getName().toUpperCase();

			for (String ending : mFileEndings)
			{
				if (name.endsWith(ending.toUpperCase()))
					return true;
			}
			
			return false;
			*/
			
			return true;
		}
	};

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.file_selector);
		
		ImageView btnClose = (ImageView)findViewById(R.id.fileselect_close);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mAdapter = new ExpBaseAdapter(this);

		mListView = (ListView)findViewById(R.id.lv_file_list);
		mListView.setAdapter(mAdapter);
		
		ListView.OnItemClickListener listener = new ListView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// "_id", "ext_number", "name","ann", "intro"

				boolean isDir = mAdapter.getItemType((int) id);

				String mPath = "";

				if (isDir) {

					String s1 = mAdapter.getItem((int) id).name;

					if (s1.equals("..")) {

						mPath = mCurDir.getParent();

					} else {

						mPath = mCurDir.getPath() + "/" + s1 + "/";

					}

					try {
						mCurDir = new File(mPath);
						ListFile(mCurDir);
					}
					catch (Exception exp)
					{
					}

				} else {

					Bundle bundle = new Bundle();

					bundle.putString("filename", mCurDir.getPath()
							+ "/" + mAdapter.getItem((int)id).name);

					Intent mIntent = new Intent();

					mIntent.putExtras(bundle);

					//setResult(RESULT_OK, mIntent);
					
					if (AppZkxc.mCurMediaPicker != null)
						AppZkxc.mCurMediaPicker.processMediaPicker(Request.PICK_MEDIA_FILE, mIntent);

					FileSelector.this.finish();
				}

			}

		};

		ListFile(mCurDir);

		mListView.setOnItemClickListener(listener);
	}

	private void ListFile(File dir) {

		mAdapter.clearItems();
		mAdapter.notifyDataSetChanged();
		mListView.postInvalidate();

		if (!dir.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
			mAdapter.addItem(new FileData("..", true));
		}

		for (File f : dir.listFiles(mFilenameFilter)) {

			if (f.isDirectory()) {
				if (!(f.getName().startsWith(".")))
				{
					mAdapter.addItem(new FileData(f.getName(), true));
				}
			} else {
				mAdapter.addItem(new FileData(f.getName(), false));
			}
		}

		mAdapter.sortByName();
		mAdapter.notifyDataSetChanged();
		mListView.postInvalidate();
	}

	private class ExpBaseAdapter extends BaseAdapter {

		private Context mContext;

		private Vector<FileData> mItems = new Vector<FileData>();

		public ExpBaseAdapter(Context context) {
			mContext = context;
		}

		public void sortByName() {
			Comparator<FileData> ct = new AbcComparator();
			Collections.sort(mItems, ct);
		}

		public void addItem(FileData it) {
			mItems.add(it);
		}

		public FileData getItem(int it) {
			return mItems.elementAt(it);
		}

		public int getCount() {
			return mItems.size();
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public boolean getItemType(int arg0) {
			return getItem(arg0).isDir;
		}

		public void clearItems() {
			mItems.clear();
		}

		public View getView(int id, View view, ViewGroup arg2) {

			LayoutInflater inflate = (LayoutInflater)mContext.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);

			if (view == null)
				view = (LinearLayout)inflate.inflate(R.layout.file_selector_item, null);
			
			TextView name = (TextView)view.findViewById(R.id.dir_file_name);

			name.setText(getItem(id).name);

			return view;
		}
	}

	class FileData {
		FileData(String _name, boolean _isDir)
		{
			name  = _name;
			isDir = _isDir;
		}
		
		String name;
		boolean isDir;
	}
	
	class AbcComparator implements Comparator<FileData> {

		public int compare(FileData obj1, FileData obj2) {
			String e1 = obj1.name;
			String e2 = obj2.name;
			
			return e1.compareTo(e2);
		}
		
	}
}
