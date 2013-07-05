package com.zknx.hn.functions.ais;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.functions.ais.AisDoc.AisItem;

import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class AisParser {

	LayoutInflater mInflater;
	
	// ���ڲ�����Ƶ
	private AisItem mAudioItem;
	private AisItem mVideoItem;

	public AisParser(LayoutInflater inflater) {
		mInflater = inflater;
	}
	
	/**
	 * ��ȡ����Ƶ����
	 * @return
	 */
	public byte[] getAudioData() {
		if (mAudioItem == null)
		    Debug.Log("mAudioItem is null getAudioData");
		else if (mAudioItem.data == null)
			Debug.Log("mAudioItem is null data null");
		else
			Debug.Log("mAudioItem is null data");
		return (mAudioItem != null) ? mAudioItem.data : null;
	}
	
	/**
	 * ��ȡ����Ƶ����
	 * @return
	 */
	public byte[] getVideoData() {
		return (mVideoItem != null) ? mVideoItem.data : null;
	}

	/**
	 * ��ȡais��ͼ
	 * @param ais_id
	 * @param context
	 * @return
	 */
	public AisLayout GetAisLayout(String aisFileName, LayoutInflater inflater, Object jsInterface) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// Ais���ݹ�����ͼ
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);

		String title = parseAis(aisFileName, contentLayout, jsInterface);

		if (title == null) {
			Debug.Log("���ش���AIS parse����");
			return null;
		}
		
		return new AisLayout(title, aisLayout);
	}

	/** 
	 * Ais��ͼ�ṹ��(��ͼ�ͱ���)
	 * @author Dengyong
	 *
	 */
	public static class AisLayout {
		
		AisLayout(String _title, LinearLayout _layout) {
			title  = _title;
			layout = _layout;
		}
		
		/**
		 * ��ȡ����
		 * @return
		 */
		public String getTitle() {
			return title;
		}
		
		/**
		 * ��ȡAis��ͼ
		 * @return
		 */
		public LinearLayout getLayout() {
			return layout;
		}

		// ����
		private String title;
		// Ais��ͼ
		private LinearLayout layout;
	}

	/**
	 * ����ɹ������ر��⣬�������ͼ��root�����򷵻�null
	 * @param ais_id
	 * @param root
	 * @return
	 */
	private String parseAis(String aisFileName, LinearLayout contentLayout, Object jsInterface) {
		// ��ȡ�������ais�ĵ�
		AisDoc aisDoc = new AisDoc(aisFileName);
		String title = aisDoc.getTitle();
		
		// ��ʼ������Ƶͼ�����
		mAudioItem = aisDoc.getAudioItem();
		mVideoItem = aisDoc.getVideoItem();
		
		// Ais���ݹ�����ͼ
		WebView webView = (WebView) contentLayout.findViewById(R.id.ais_webview);

		// �Ƿ�μ�
		if (aisDoc.isCourse()) {
			webView.setVisibility(View.GONE);
			//CourseWebView.GenHtml(ais_id, webView, aisDoc);
			CourseView.InitView(mInflater, contentLayout, aisDoc);
		} else {
			AisWebView.Init(aisDoc, webView, jsInterface);
		}
		
		webView.setBackgroundColor(0); // ����͸��
		webView.getBackground().setAlpha(0);

		return title;
	}
}
