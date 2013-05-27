package com.zknx.hn.functions;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.Dialog;
import com.zknx.hn.common.Dialog.ConfirmListener;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.ListItemMap;
import com.zknx.hn.data.UserMan;
import com.zknx.hn.functions.common.CommonList;
import com.zknx.hn.functions.common.CommonListAdapter;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.functions.common.CommonList.CommonListParams;
import com.zknx.hn.functions.common.ListItemClickListener;

public class MyGroup extends FunctionView {

	private CommonListAdapter mAdapterFriend;
	private CommonListAdapter mAdapterFriendMessage;
	private CommonListAdapter mAdapterMajor;
	
	// 留言视图
	private LinearLayout mMessageLayout;
	// 留言列表内容
	private LinearLayout mMessageContent;
	
	private Button mMyMessageBtn;
	private Button mFriendMessageBtn;
	private Button mGroupBtn;
	
	// 新建留言视图
	private RelativeLayout mNewMessageLayout;
	
	// 新建留言回复视图
	private TextView mNewMessageSelfIntroduce;
	private TextView mNewMessageReply;
	private TextView mNewMessageDate;
	// 新建留言内容
	private EditText mNewMessageContent;
	
	// 发布信息
	private Button mNewMessagePost;
	// 返回列表
	private Button mNewMessageBack;
	
	// 商友列表视图
	ListView mFriendListView;
	
	// 保存当前好友
	private String mCurFriendId;
	private String mCurFriendName;
	
	public MyGroup(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		initFriendList();
		
		initGroupMessaage();
	}
	
	/**
	 * 初始化我的商友列表
	 */
	private void initFriendList() {
		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.group_my_friend, null);
		
		// XXX 替换为通用frame，并增加添加好友按钮到custom_bottom （是否增加添加好友？）
		
		layout.findViewById(R.id.my_goup_friend_all).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_rich_planter).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_rich_culturists).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_middleman).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_cooperation).setOnClickListener(mOnClickClass);
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], null, mClickFriend);

		mFriendListView = CommonList.Init(listParams, "我的商友", layout);

		// INVALID_ID 表示全部商友
		initFriendListView(DataMan.INVALID_ID);
	}
	
	/**
	 * 初始化商友列表（商友分类：全部，种植大户……）
	 * @param invalidId
	 */
	private void initFriendListView(int majorId) {
		mAdapterFriend = new CommonListAdapter(mContext, DataMan.GetMyGroupFriendList(majorId, true));
		mFriendListView.setAdapter(mAdapterFriend);
		
		// 默认第一个商友信息
		initGroupFriendInfo(0);
	}

	/**
	 * 初始化商友信息
	 */
	void initGroupFriendInfo(int position) {
		mCurFriendId = mAdapterFriend.getItemMapString(position, DataMan.KEY_FRIEND_ID);
		
		Debug.Log("mCurFriendId:" + mCurFriendId);
		
		RelativeLayout layout = (RelativeLayout)mInflater.inflate(R.layout.group_friend_info, null);
		LinearLayout layoutContent = (LinearLayout)layout.findViewById(R.id.group_friend_info_contact);
		
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.common_btn_pair1:
					// 切换为新建留言界面，传参我的留言组id（返回我的留言tab）
					initNewMessageView(mCurFriendName, mCurFriendId, R.id.my_group_message_my_message);
					break;
				case R.id.common_btn_pair2:
					// TODO 实现语音对讲
					break;
				}
			}
		};

		layoutContent.addView(getLinearLayoutBtnPair(R.string.new_message, R.string.friend_interphone, listener ), UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
		
		// TODO 实现语音对讲
		layoutContent.findViewById(R.id.common_btn_pair2).setEnabled(false);
		
		ListItemMap info = DataMan.GetMyFriendInfo(mCurFriendId);
		
		if (info != null) {
			TextView tv = (TextView)layout.findViewById(R.id.my_friend_info_name);
			// 保存当前好友名字
			mCurFriendName = info.getString(DataMan.KEY_NAME);
			tv.setText(mCurFriendName);
			
			tv = (TextView)layout.findViewById(R.id.my_friend_info_major);
			tv.setText(info.getString(DataMan.KEY_FRIEND_MAJOR));
			
			tv = (TextView)layout.findViewById(R.id.my_friend_info_address);
			tv.setText(info.getString(DataMan.KEY_FRIEND_ADDRESS));
			
			tv = (TextView)layout.findViewById(R.id.my_friend_info_telephone);
			tv.setText(info.getString(DataMan.KEY_FRIEND_TELEPHONE));
		} else {
			mCurFriendName = "未知好友"; // 容错处理
		}
		
		// 清除第二区后添加视图
		initContent("商友信息", layout, mContentFrame[1]);
	}
	
	/**
	 * 初始化我的商圈留言
	 */
	void initGroupMessaage() {
		
		// 留言视图
		if (mMessageLayout == null) {
			mMessageLayout = (LinearLayout)mInflater.inflate(R.layout.group_message, null);
			
			mMyMessageBtn = (Button)mMessageLayout.findViewById(R.id.my_group_message_my_message);
			mFriendMessageBtn = (Button)mMessageLayout.findViewById(R.id.my_group_message_friend_message);
			mGroupBtn = (Button)mMessageLayout.findViewById(R.id.my_group_message_group);
			
			mMyMessageBtn.setOnClickListener(mOnClickMessage);
			mFriendMessageBtn.setOnClickListener(mOnClickMessage);
			mGroupBtn.setOnClickListener(mOnClickMessage);
			
			// 留言列表
			mMessageContent = (LinearLayout)mMessageLayout.findViewById(R.id.my_group_message_content);
		}
		
		// 默认显示我的留言
		initMessageTab(R.id.my_group_message_my_message);
		
		// 清除第三区后添加视图
		mContentFrame[2].removeAllViews();
		mContentFrame[2].addView(mMessageLayout);
	}
	
	ListItemClickListener mClickFriend = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			initGroupFriendInfo(position);
		}
	};

	OnClickListener mOnClickClass = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int friend_class_id = DataMan.INVALID_ID;
			
			switch (view.getId()) {
			case R.id.my_goup_friend_all:
				// 默认全部商友
				break;
			case R.id.my_goup_friend_rich_planter:
				friend_class_id = DataMan.MAJOR_ID_RICH_PLANTER;
				break;
			case R.id.my_goup_friend_rich_culturists:
				friend_class_id = DataMan.MAJOR_ID_RICH_CULTURISTS;
				break;
			case R.id.my_goup_friend_middleman:
				friend_class_id = DataMan.MAJOR_ID_MIDDLEMAN;
				break;
			case R.id.my_goup_friend_cooperation:
				friend_class_id = DataMan.MAJOR_ID_COOPERATION;
				break;
			}

			initFriendListView(friend_class_id);
		}
	};
	
	OnClickListener mOnClickMessage = new OnClickListener() {
		@Override
		public void onClick(View view) {
			initMessageTab(view.getId());			
		}
	};
	
	private void initMessageTab(int messageButtonId) {
		// 首先清除原有视图
		mMessageContent.removeAllViews();
		
		switch (messageButtonId) {
		case R.id.my_group_message_my_message:
			initMyMessageView();
			break;
		case R.id.my_group_message_friend_message:
			initFriendMessage(mCurFriendId);
			break;
		case R.id.my_group_message_group:
			initMajorGroup(DataMan.GetMajor(mCurFriendId));
			break;
		}
		
		mMyMessageBtn.setBackgroundResource(getMessageButtonRes(messageButtonId == R.id.my_group_message_my_message));
		mFriendMessageBtn.setBackgroundResource(getMessageButtonRes(messageButtonId == R.id.my_group_message_friend_message));
		mGroupBtn.setBackgroundResource(getMessageButtonRes(messageButtonId == R.id.my_group_message_group));
	}
	
	/**
	 * 初始化我的留言视图
	 */
	private void initMyMessageView() {
		RelativeLayout myMessageLayout = (RelativeLayout) mInflater.inflate(R.layout.group_my_message, null);
		LinearLayout listViewLayout = (LinearLayout) myMessageLayout.findViewById(R.id.my_group_my_message_listview);
		
		CommonListAdapter adapter = new CommonListAdapter(mContext, DataMan.GetMyGroupMessageList(DataMan.MY_MESSAGE + ""));
		CommonListParams listParams = new CommonListParams(mInflater, listViewLayout, adapter, null);
		
		// 初始化我的留言列表
		CommonList.Init(listParams);

		// 添加新建留言视图
		mMessageContent.removeAllViews();
		mMessageContent.addView(myMessageLayout, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
	}
	
	/**
	 * 初始化当前商友留言
	 */
	private void initFriendMessage(String friendId) {
		
		mAdapterFriendMessage = new CommonListAdapter(mContext, DataMan.GetMyGroupMessageList(friendId));
		
		CommonListParams listParams = new CommonListParams(mInflater, mMessageContent, mAdapterFriendMessage, new ListItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				super.onItemClick(parent, view, position, id);
				
				String friendId = mAdapterFriendMessage.getItemMapString(position, DataMan.KEY_MY_GROUP_MESSAGE_ID);
				String messageOwner = mAdapterFriendMessage.getItemMapString(position, DataMan.KEY_FRIEND_MESSAGE_POSER);
				
				// 切换为新建留言界面，传参好友留言组id
				initNewMessageView(messageOwner, friendId, R.id.my_group_message_friend_message);
			}
		});
		
		CommonList.Init(listParams);
	}
	
	/**
	 * 初始化商圈（专业）用户列表
	 * @param friend_id
	 */
	private void initMajorGroup(int majorId) {
		
		mAdapterMajor = new CommonListAdapter(mContext, DataMan.GetMyGroupFriendList(majorId, false));
		
		CommonListParams listParams = new CommonListParams(mInflater, mMessageContent, mAdapterMajor, new ListItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				super.onItemClick(parent, view, position, id);
				
				String friendId = mAdapterMajor.getItemMapString(position, DataMan.KEY_MY_GROUP_MESSAGE_ID);
				String messageOwner = mAdapterMajor.getItemMapString(position, DataMan.KEY_FRIEND_MESSAGE_POSER);
				
				// 切换为新建留言界面，传参留言组id
				initNewMessageView(messageOwner, friendId, R.id.my_group_message_group);
			}
		});
		
		CommonList.Init(listParams);
	}

	/**
	 * 初始化新建留言视图
	 */
	private void initNewMessageView(String messageOwner, final String friendId, final int tabBtnId) {

		if (mNewMessageLayout == null) {
			mNewMessageLayout = (RelativeLayout) mInflater.inflate(R.layout.group_new_message, null);
			
			mNewMessageSelfIntroduce = (TextView)mNewMessageLayout.findViewById(R.id.new_message_self_introduce);
			mNewMessageReply = (TextView)mNewMessageLayout.findViewById(R.id.new_message_reply);
			mNewMessageDate = (TextView)mNewMessageLayout.findViewById(R.id.new_message_date);
			
			mNewMessageContent = (EditText)mNewMessageLayout.findViewById(R.id.new_message_content);
			
			mNewMessagePost = (Button)mNewMessageLayout.findViewById(R.id.new_message_post_btn);
			mNewMessageBack = (Button)mNewMessageLayout.findViewById(R.id.new_message_back);
		}

		OnClickListener clickPost = new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.new_message_post_btn:
					postNewMessage(friendId);
					break;
				case R.id.new_message_back:
					initMessageTab(tabBtnId);
					break;
				}
			}
		};
		
		mNewMessagePost.setOnClickListener(clickPost);
		mNewMessageBack.setOnClickListener(clickPost);

		// TODO 自我介绍
		mNewMessageSelfIntroduce.setText(messageOwner + "的自我介绍……");
		mNewMessageReply.setText("回复：" + messageOwner);
		mNewMessageDate.setText("日期：" + DataMan.GetCurrentTime(true));

		// 添加新建留言视图
		mMessageContent.removeAllViews();
		mMessageContent.addView(mNewMessageLayout, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));;
	}

	private int getMessageButtonRes(boolean focus) {
		return focus ? R.drawable.tab_button_cur : R.drawable.tab_button;
	}

	/**
	 * 发布新留言
	 * friendId 为空则新建用户自己的留言（自我介绍）
	 */
	private void postNewMessage(final String friendId) {
		
		final String userId = UserMan.GetCurrentUserId();

		if (userId == null || userId.length() == 0) {
			Debug.Log("严重错误：postNewMessage,userId为空");
			return;
		}

		final String newMessage = mNewMessageContent.getEditableText().toString();

		if (newMessage.length() <= 10) {
			Toast.makeText(mContext, "留言内容需大于5个字", Toast.LENGTH_LONG).show();
			return;
		}

		Dialog.Confirm(mContext, R.string.confirm_post_message, new ConfirmListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!DataMan.PostNewMessage(userId, friendId, newMessage)) {
					Toast.makeText(mContext, "发布留言失败", Toast.LENGTH_LONG).show();
					return;
				}
				
				Toast.makeText(mContext, "发布留言成功", Toast.LENGTH_LONG).show();
				mNewMessageContent.setText(""); // 成功发布留言后清空留言内容
			}
		});
	}
}
