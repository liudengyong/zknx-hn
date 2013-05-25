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

//TODO (����)������ѣ��ظ����Լ���ӣ�
public class MyGroup extends FunctionView {

	private CommonListAdapter mAdapterFriend;
	private CommonListAdapter mAdapterFriendMessage;
	private CommonListAdapter mAdapterMajor;
	
	private int mCurrentFriend = 0; //TODO ���õ�ǰ����
	
	// ������ͼ
	private LinearLayout mMessageLayout;
	// �����б�����
	private LinearLayout mMessageContent;
	
	private Button mMyMessageBtn;
	private Button mFriendMessageBtn;
	private Button mGroupBtn;
	
	// �½�������ͼ
	private RelativeLayout mNewMessageLayout;
	
	// �½����Իظ���ͼ
	private TextView mNewMessageSelfIntroduce;
	private TextView mNewMessageReply;
	private TextView mNewMessageDate;
	// �½���������
	private EditText mNewMessageContent;
	
	// ������Ϣ
	private Button mNewMessagePost;
	// �����б�
	private Button mNewMessageBack;
	
	// �����б���ͼ
	ListView mFriendListView;
	
	public MyGroup(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);
		
		initFriendList();
		
		initGroupMessaage();
	}
	
	/**
	 * ��ʼ���ҵ������б�
	 */
	private void initFriendList() {
		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.group_my_friend, null);
		
		// XXX �滻Ϊͨ��frame����������Ӻ��Ѱ�ť��custom_bottom ���Ƿ�������Ӻ��ѣ���
		
		layout.findViewById(R.id.my_goup_friend_all).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_rich_planter).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_rich_culturists).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_middleman).setOnClickListener(mOnClickClass);
		layout.findViewById(R.id.my_goup_friend_cooperation).setOnClickListener(mOnClickClass);
		
		CommonListParams listParams = new CommonListParams(mInflater, mContentFrame[0], null, mOnClassClick);

		mFriendListView = CommonList.Init(listParams, "�ҵ�����", layout);

		// INVALID_ID ��ʾȫ������
		initFriendListView(DataMan.INVALID_ID);
	}
	
	/**
	 * ��ʼ�������б����ѷ��ࣺȫ������ֲ�󻧡�����
	 * @param invalidId
	 */
	private void initFriendListView(int majorId) {
		mAdapterFriend = new CommonListAdapter(mContext, DataMan.GetMyGroupFriendList(majorId, true));
		mFriendListView.setAdapter(mAdapterFriend);
		
		// Ĭ�ϵ�һ��������Ϣ
		initGroupFriendInfo(0);
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	void initGroupFriendInfo(int position) {
		String friendId = mAdapterFriend.getItemMapString(position, DataMan.KEY_FRIEND_ID);
		
		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.group_friend_info, null);
		
		ListItemMap info = DataMan.GetMyFriendInfo(friendId);
		
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
		
		// ����ڶ����������ͼ
		initContent("������Ϣ", layout, mContentFrame[1]);
	}
	
	/**
	 * ��ʼ���ҵ���Ȧ����
	 */
	void initGroupMessaage() {
		
		// ������ͼ
		if (mMessageLayout == null) {
			mMessageLayout = (LinearLayout)mInflater.inflate(R.layout.group_message, null);
			
			mMyMessageBtn = (Button)mMessageLayout.findViewById(R.id.my_group_message_my_message);
			mFriendMessageBtn = (Button)mMessageLayout.findViewById(R.id.my_group_message_friend_message);
			mGroupBtn = (Button)mMessageLayout.findViewById(R.id.my_group_message_group);
			
			mMyMessageBtn.setOnClickListener(mOnClickMessage);
			mFriendMessageBtn.setOnClickListener(mOnClickMessage);
			mGroupBtn.setOnClickListener(mOnClickMessage);
			
			// �����б�
			mMessageContent = (LinearLayout)mMessageLayout.findViewById(R.id.my_group_message_content);
		}
		
		// Ĭ����ʾ�ҵ�����
		initMessageTab(R.id.my_group_message_my_message);
		
		// ����������������ͼ
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
				// Ĭ��ȫ������
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
		// �������ԭ����ͼ
		mMessageContent.removeAllViews();
		
		switch (messageButtonId) {
		case R.id.my_group_message_my_message:
			initMyMessageView();
			break;
		case R.id.my_group_message_friend_message:
			initFriendMessage(mCurrentFriend);
			break;
		case R.id.my_group_message_group:
			initMajorGroup(DataMan.GetMajor(mCurrentFriend));
			break;
		}
		
		mMyMessageBtn.setBackgroundResource(getMessageButtonRes(messageButtonId == R.id.my_group_message_my_message));
		mFriendMessageBtn.setBackgroundResource(getMessageButtonRes(messageButtonId == R.id.my_group_message_friend_message));
		mGroupBtn.setBackgroundResource(getMessageButtonRes(messageButtonId == R.id.my_group_message_group));
	}
	
	/**
	 * ��ʼ���ҵ�������ͼ
	 */
	private void initMyMessageView() {
		RelativeLayout myMessageLayout = (RelativeLayout) mInflater.inflate(R.layout.group_my_message, null);
		LinearLayout listViewLayout = (LinearLayout) myMessageLayout.findViewById(R.id.my_group_my_message_listview);
		
		CommonListAdapter adapter = new CommonListAdapter(mContext, DataMan.GetMyGroupMessageList(DataMan.MY_MESSAGE));
		CommonListParams listParams = new CommonListParams(mInflater, listViewLayout, adapter, null);
		
		// ��ʼ���ҵ������б�
		CommonList.Init(listParams);

		// ����½�������ͼ
		mMessageContent.removeAllViews();
		mMessageContent.addView(myMessageLayout, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
	}
	
	/**
	 * ��ʼ����ǰ��������
	 */
	private void initFriendMessage(int friendId) {
		
		mAdapterFriendMessage = new CommonListAdapter(mContext, DataMan.GetMyGroupMessageList(friendId));
		
		CommonListParams listParams = new CommonListParams(mInflater, mMessageContent, mAdapterFriendMessage, new ListItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				super.onItemClick(parent, view, position, id);
				
				// �л�Ϊ�½����Խ��棬����������id
				initNewMessageView(mAdapterFriendMessage, position, R.id.my_group_message_friend_message);
			}
		});
		
		CommonList.Init(listParams);
	}
	
	/**
	 * ��ʼ����Ȧ��רҵ���û��б�
	 * @param friend_id
	 */
	private void initMajorGroup(int majorId) {
		
		mAdapterMajor = new CommonListAdapter(mContext, DataMan.GetMyGroupFriendList(majorId, false));
		
		CommonListParams listParams = new CommonListParams(mInflater, mMessageContent, mAdapterMajor, new ListItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				super.onItemClick(parent, view, position, id);
				
				// �л�Ϊ�½����Խ��棬����������id
				initNewMessageView(mAdapterMajor, position, R.id.my_group_message_group);
			}
		});
		
		CommonList.Init(listParams);
	}

	/**
	 * ��ʼ���½�������ͼ
	 */
	private void initNewMessageView(CommonListAdapter adapterMessage, int position, final int tabBtnId) {
		
		final String friendId = adapterMessage.getItemMapString(position, DataMan.KEY_MY_GROUP_MESSAGE_ID);
		String messageOwner = adapterMessage.getItemMapString(position, DataMan.KEY_FRIEND_MESSAGE_POSER);

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

		// TODO ���ҽ���
		mNewMessageSelfIntroduce.setText("���������ҽ��ܡ���");
		mNewMessageReply.setText("�ظ���" + messageOwner + "����");
		mNewMessageDate.setText("���ڣ�" + DataMan.GetCurrentTime(true));

		// ����½�������ͼ
		mMessageContent.removeAllViews();
		mMessageContent.addView(mNewMessageLayout, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));;
	}

	private int getMessageButtonRes(boolean focus) {
		return focus ? R.drawable.tab_button_cur : R.drawable.tab_button;
	}

	/**
	 * ����������
	 * friendId Ϊ�����½��û��Լ������ԣ����ҽ��ܣ�
	 */
	private void postNewMessage(final String friendId) {
		
		final String userId = UserMan.GetCurrentUserId();

		if (userId == null || userId.length() == 0) {
			Debug.Log("���ش���postNewMessage,userIdΪ��");
			return;
		}

		final String newMessage = mNewMessageContent.getEditableText().toString();

		if (newMessage.length() <= 10) {
			Toast.makeText(mContext, "�������������5����", Toast.LENGTH_LONG).show();
			return;
		}

		Dialog.Confirm(mContext, R.string.confirm_post_message, new ConfirmListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!DataMan.PostNewMessage(userId, friendId, newMessage)) {
					Toast.makeText(mContext, "��������ʧ��", Toast.LENGTH_LONG).show();
					return;
				}
				
				Toast.makeText(mContext, "�������Գɹ�", Toast.LENGTH_LONG).show();
				mNewMessageContent.setText(""); // �ɹ��������Ժ������������
			}
		});
	}
}
