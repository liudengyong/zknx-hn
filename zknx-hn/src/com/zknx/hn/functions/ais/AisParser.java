package com.zknx.hn.functions.ais;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.functions.ais.AisDoc.AisHeader;
import com.zknx.hn.functions.ais.AisDoc.AisItem;

import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class AisParser {

	LayoutInflater mInflater;
	
	// 用于播放音频
	private AisItem mAudioItem;
	private AisItem mVideoItem;

	public AisParser(LayoutInflater inflater) {
		mInflater = inflater;
	}
	
	/**
	 * 获取音视频数据
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
	 * 获取音视频数据
	 * @return
	 */
	public byte[] getVideoData() {
		return (mVideoItem != null) ? mVideoItem.data : null;
	}

	/**
	 * 获取ais视图
	 * @param ais_id
	 * @param context
	 * @return
	 */
	public AisLayout GetAisLayout(String aisFileName, LayoutInflater inflater, Object jsInterface) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// Ais内容滚动视图
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);

		AisHeader header = parseAis(aisFileName, contentLayout, jsInterface);

		if (header == null) {
			Debug.Log("严重错误：AIS parse错误");
			return null;
		}
		
		return new AisLayout(header, aisLayout);
	}

	/** 
	 * Ais视图结构体(视图和标题)
	 * @author Dengyong
	 *
	 */
	public static class AisLayout {
		
		AisLayout(AisHeader _header, LinearLayout _layout) {
			header = _header;
			layout = _layout;
		}

		/**
		 * 获取头
		 * @return
		 */
		public AisHeader getAisHeader() {
			return header;
		}
		
		/**
		 * 获取Ais视图
		 * @return
		 */
		public LinearLayout getLayout() {
			return layout;
		}

		// Ais视图
		private LinearLayout layout;
		// ais header
		private AisHeader header;
	}

	/**
	 * 如果成功，返回标题，并添加视图到root，否则返回null
	 * @param ais_id
	 * @param root
	 * @return
	 */
	private AisHeader parseAis(String aisFileName, LinearLayout contentLayout, Object jsInterface) {
		// 获取解析后的ais文档
		AisDoc aisDoc = new AisDoc(aisFileName, false);
		
		// 初始化音视频图标监听
		mAudioItem = aisDoc.getAudioItem();
		mVideoItem = aisDoc.getVideoItem();

		// Ais内容滚动视图
		WebView webView = (WebView) contentLayout.findViewById(R.id.ais_webview);

		// 是否课件
		if (aisDoc.isCourse()) {
			webView.setVisibility(View.GONE);
			//CourseWebView.GenHtml(ais_id, webView, aisDoc);
			CourseView.InitView(mInflater, contentLayout, aisDoc);
		} else {
			AisWebView.Init(aisDoc, webView, jsInterface);
		}
		
		webView.setBackgroundColor(0); // 设置透明

		return aisDoc.getHeader();
	}
}
