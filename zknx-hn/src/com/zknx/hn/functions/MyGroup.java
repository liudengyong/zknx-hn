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

//TODO (讨论)添加商友？回复留言即添加？
public class MyGroup extends FunctionView {

	private CommonListAdapter mAdapterFriend;
	private CommonListAdapter mAdapterMessage;
	
	private int mCurrentFriend = 0;
	
	// 留言视图
	private LinearLayout mMessageLayout;
	// 留言列表内容
	private LinearLayout mMessageContent;
	
	private Button mMessageButtonAll;
	private Button mMessageButtonFriend;
	private Button mMessageButtonNew;
	
	// 新建留言视图
	private RelativeLayout mNewMessageLayout;
	
	// 新建留言回复视图
	private LinearLayout mRelayLayout;
	private TextView mNewMessageReply;
	
	private TextView mNewMessageTitleName;
	private TextView mNewMessageTitleDate;
	// 新建留言内容
	private EditText mNewMessageContent;
	
	// 发布信息
	private Button mNewMessagePost;
	
	// 商友列表视图
	ListView mFriendListView;
	
	public MyGroup(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		initFriendList();
		
		initGroupMessaage();
	}
	
	/**
	 * 初始化我的商友列表
	 */
	private void initFriendList() {
		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.my_group_friend, null);
		
		// XXX 替换为通用frame，并增加添加好友按钮到custom_bottom （是否增加添加好友？）
		
		layout.findViewById(R.id.my_goup_friend_all).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_rich_planter).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_rich_culturists).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_middleman).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_cooperation).setOnClickListener(mOnClickClass);
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], null, mOnClassClick);

		mFriendListView = CommonList.Init(listParams, "我的商友", layout);

		// INVALID_ID 表示全部商友
		initFriendListView(DataMan.INVALID_ID);
	}
	
	/**
	 * 初始化商友列表（商友分类：全部，种植大户……）
	 * @param invalidId
	 */
	private void initFriendListView(int friend_class_id) {
		mAdapterFriend = new CommonListAdapter(mContext, DataMan.GetMyGroupFriendList(friend_class_id));
		mFriendListView.setAdapter(mAdapterFriend);
		
		// 默认第一个商友信息
		initGroupFriendInfo(0);
	}

	/**
	 * 初始化商友信息
	 */
	void initGroupFriendInfo(int position) {
		int friend_id = mAdapterFriend.getItemMapInt(position, DataMan.KEY_FRIEND_ID);
		
		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.my_group_friend_info, null);
		
		ListItemMap info = DataMan.GetMyFriendInfo(friend_id);
		
		if (info != null) {
			TextView tv = (TextView)layout.findViewById(R.id.my_friend_info_name);
			tv.setText(info.getString(DataMan.KEY_NAME));
			
			tv = (TextView)layout.findViewById(R.id.my_friend_info_major);
			tv.setText(info.getString(DataMan.KEY_FRIEND_MAJOR));
			
			tv = (TextView)layout.findViewById(R.id.my_friend_info_address);
			tv.setText(info.getString(DataMan.KEY_FRIEND_ADDRESS));
			
			tv = (TextView)layout.findViewById(R.id.my_friend_info_telephone);
			tv.setText(info.getString(DataMan.KEY_FRIEND_TELEPHONE));
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
			mMessageLayout = (LinearLayout)mInflater.inflate(R.layout.my_group_message, null);
			
			mMessageButtonAll = (Button)mMessageLayout.findViewById(R.id.my_group_message_all);
			mMessageButtonFriend = (Button)mMessageLayout.findViewById(R.id.my_group_message_friend);
			mMessageButtonNew = (Button)mMessageLayout.findViewById(R.id.my_group_message_new);
			
			mMessageButtonAll.setOnClickListener(mOnClickMessage);
			mMessageButtonFriend.setOnClickListener(mOnClickMessage);
			mMessageButtonNew.setOnClickListener(mOnClickMessage);
			
			// 留言列表
			mMessageContent = (LinearLayout)mMessageLayout.findViewById(R.id.my_group_message_content);
		}
		
		// 默认显示所有留言
		initMessageTab(R.id.my_group_message_all);
		
		// 清除第三区后添加视图
		mContentFrame[2].removeAllViews();
		mContentFrame[2].addView(mMessageLayout);
	}
	
	ListItemClickListener mOnClassClick = new ListItemClickListener() {
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
		case R.id.my_group_message_new:
			initNewMessageView(DataMan.INVALID_ID, null);
			break;
		case R.id.my_group_message_all:
			initMessageList(DataMan.INVALID_ID);
			break;
		case R.id.my_group_message_friend:
			initMessageList(mCurrentFriend);
			break;
		}
		
		mMessageButtonAll.setBackgroundResource(getMessageButtonRes(messageButtonId == R.id.my_group_message_all));
		mMessageButtonFriend.setBackgroundResource(getMessageButtonRes(messageButtonId == R.id.my_group_message_friend));
		mMessageButtonNew.setBackgroundResource(getMessageButtonRes(messageButtonId == R.id.my_group_message_new));
	}

	/**
	 * 初始化新建留言视图
	 */
	private void initNewMessageView(int friendId, String messgeOwner) {

		if (mNewMessageLayout == null) {
			mNewMessageLayout = (RelativeLayout) mInflater.inflate(R.layout.new_message, null);
			
			mRelayLayout = (LinearLayout)mNewMessageLayout.findViewById(R.id.new_message_reply);
			mNewMessageReply = (TextView)mNewMessageLayout.findViewById(R.id.new_message_reply_tv);

			mNewMessageTitleName = (TextView)mNewMessageLayout.findViewById(R.id.new_message_title_name);
			mNewMessageTitleDate = (TextView)mNewMessageLayout.findViewById(R.id.new_message_title_date);
			
			mNewMessageContent = (EditText)mNewMessageLayout.findViewById(R.id.new_message_content);
			
			mNewMessagePost = (Button)mNewMessageLayout.findViewById(R.id.new_message_post_btn);
		}
		
		if (friendId == DataMan.INVALID_ID) {
			mRelayLayout.setVisibility(View.GONE);
			
			OnClickListener clickPost = new OnClickListener() {
				@Override
				public void onClick(View view) {
					postNewMessage(DataMan.INVALID_ID);
				}
			};
			
			mNewMessagePost.setOnClickListener(clickPost);

		} else {
			mRelayLayout.setVisibility(View.VISIBLE);
			mNewMessageReply.setText("回复：" + messgeOwner);
			
			final int replyFriendId = friendId;
			
			OnClickListener clickPost = new OnClickListener() {
				@Override
				public void onClick(View view) {
					postNewMessage(replyFriendId);
				}
			};
			
			mNewMessagePost.setOnClickListener(clickPost);
		}

		mNewMessageTitleName.setText("用户：" + UserMan.GetCurrentUserName());
		mNewMessageTitleDate.setText("日期：" + DataMan.GetCurrentTime(true));

		// 添加新建留言视图
		mMessageContent.removeAllViews();
		mMessageContent.addView(mNewMessageLayout, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));;
	}
	
	private int getMessageButtonRes(boolean focus) {
		return focus ? R.drawable.menu_title_focus : R.drawable.menu_title;
	}
	
	/**
	 * 初始化留言列表
	 * @param friend_id
	 */
	void initMessageList(int friend_id) {
		
		mAdapterMessage = new CommonListAdapter(mContext, DataMan.GetMyGroupMessageList(friend_id));
		
		CommonListParams listParams = new CommonListParams(mInflater, mMessageContent, mAdapterMessage, mOnMessageClick);
		
		CommonList.Init(listParams);
	}
	
	/**
	 * 点击留言列表即回复
	 * TODO (讨论) 后台维护留言？后台删除留言？
	 */
	ListItemClickListener mOnMessageClick = new ListItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			super.onItemClick(parent, view, position, id);
			
			int messageOwnerId = getMessageOwnerId(position);
			String messagOwner = messageOwner(position);

			// 切换为新建留言界面，传参留言组id
			initNewMessageView(messageOwnerId, messagOwner);
		}

		/**
		 * 获取message_id
		 * @param position
		 * @return
		 */
		private int getMessageOwnerId(int position) {
			return mAdapterMessage.getItemMapInt(position, DataMan.KEY_MY_GROUP_MESSAGE_ID);
		}
		
		/**
		 * 获取message主人名字
		 * @param position
		 * @return
		 */
		private String messageOwner(int position) {
			ListItemMap map = mAdapterMessage.getItem(position);

			if (map == null) {
				Debug.Log("严重错误：messageOwner为空");
				return null;
			}

			return map.getString(DataMan.KEY_FRIEND_MESSAGE_POSER);
		}
	};

	/**
	 * 发布新留言
	 * thread_id为空则新建用户自己的留言
	 */
	private void postNewMessage(final int message_id) {
		
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
				if (!DataMan.PostNewMessage(userId, message_id, newMessage)) {
					Toast.makeText(mContext, "发布留言失败", Toast.LENGTH_LONG).show();
					return;
				}
				
				Toast.makeText(mContext, "发布留言成功", Toast.LENGTH_LONG).show();
				mNewMessageContent.setText(""); // 成功发布留言后清空留言内容
			}
		});
	}
}
