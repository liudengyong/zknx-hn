package com.zknx.hn.functions.ais;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.functions.ais.AisDoc.AisHeader;
import com.zknx.hn.functions.ais.AisDoc.AisItem;

import android.content.Context;
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
	public AisLayout GetAisLayout(String date, String aisFileName, LayoutInflater inflater, Object jsInterface) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// Ais���ݹ�����ͼ
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);

		AisHeader header = parseAis(inflater.getContext(), date, aisFileName, contentLayout, jsInterface);

		if (header == null) {
			Debug.Log("���ش���AIS parse����");
			return null;
		}
		
		return new AisLayout(header, aisLayout);
	}

	/** 
	 * Ais��ͼ�ṹ��(��ͼ�ͱ���)
	 * @author Dengyong
	 *
	 */
	public static class AisLayout {
		
		AisLayout(AisHeader _header, LinearLayout _layout) {
			header = _header;
			layout = _layout;
		}

		/**
		 * ��ȡͷ
		 * @return
		 */
		public AisHeader getAisHeader() {
			return header;
		}
		
		/**
		 * ��ȡAis��ͼ
		 * @return
		 */
		public LinearLayout getLayout() {
			return layout;
		}

		// Ais��ͼ
		private LinearLayout layout;
		// ais header
		private AisHeader header;
	}

	/**
	 * ����ɹ������ر��⣬�������ͼ��root�����򷵻�null
	 * @param ais_id
	 * @param root
	 * @return
	 */
	private AisHeader parseAis(Context context, String date, String aisFileName, LinearLayout contentLayout, Object jsInterface) {
		// ��ȡ�������ais�ĵ�
		AisDoc aisDoc = new AisDoc(context, aisFileName, false, date);
		
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

		return aisDoc.getHeader();
	}
}
