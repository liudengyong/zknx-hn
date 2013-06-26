package com.zknx.hn.functions.ais;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.WebkitClient;
import com.zknx.hn.common.widget.ImageUtils;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;
import com.zknx.hn.functions.ais.AisDoc.AisItem;
import com.zknx.hn.functions.ais.AisDoc.ItemType;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class AisParser {

	LayoutInflater mInflater;
	
	private final int IMAGE_WIDTH = 240;
	private final int IMAGE_HEIGHT = 360;
	
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
	@SuppressLint("SetJavaScriptEnabled")
	public AisLayout GetAisLayout(String ais_id, LayoutInflater inflater, Object jsInterface /* 暂未使用 */) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// Ais内容滚动视图
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);

		String title = parseAis(ais_id, contentLayout/*, jsInterface*/);

		if (title == null) {
			Debug.Log("严重错误：AIS parse错误");
			return null;
		}
		
		return new AisLayout(title, aisLayout);
	}

	/** 
	 * Ais视图结构体(视图和标题)
	 * @author Dengyong
	 *
	 */
	public static class AisLayout {
		
		AisLayout(String _title, LinearLayout _layout) {
			title  = _title;
			layout = _layout;
		}
		
		/**
		 * 获取标题
		 * @return
		 */
		public String getTitle() {
			return title;
		}
		
		/**
		 * 获取Ais视图
		 * @return
		 */
		public LinearLayout getLayout() {
			return layout;
		}

		// 标题
		private String title;
		// Ais视图
		private LinearLayout layout;
	}

	/**
	 * 如果成功，返回标题，并添加视图到root，否则返回null
	 * @param ais_id
	 * @param root
	 * @return
	 */
	private String parseAis(String ais_id, LinearLayout contentLayout) {
		// 获取解析后的ais文档
		AisDoc aisDoc = new AisDoc(ais_id);
		String title = aisDoc.getTitle();
		
		mAudioItem = null;
		mVideoItem = null;
		
		if (title == null)
			return null;

		// Ais内容滚动视图
		WebView webView = (WebView) contentLayout.findViewById(R.id.ais_webview);

		// 是否课件
		if (aisDoc.isCourse()) {
			webView.setVisibility(View.GONE);
			//CourseWebView.GenHtml(ais_id, webView, aisDoc);
			CourseView.InitView(contentLayout, aisDoc);
		} else {
			
			// 添加JS接口
			webView.getSettings().setJavaScriptEnabled(true); // 启用JS脚本
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // 禁用cache
			//webView.addJavascriptInterface(jsInterface, "ais");
			webView.setWebChromeClient(new WebkitClient());

			String htmlString = genAisWebview(ais_id, webView, aisDoc);
			
			// 加载webview
			if (htmlString != null) {
				//webView.loadData(htmlString, "text/html", "GBK");
				webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
			}
			
			webView.setBackgroundColor(0); // 设置透明
		}

		return title;
	}

	/**
	 * 生成Ais视图
	 * @param ais_id
	 * @param webView
	 * @param aisDoc
	 */
	private String genAisWebview(String ais_id, WebView webView, AisDoc aisDoc) {

		List<List<AisItem>> aisItemTree = aisDoc.getItemTree();
		
		if (aisItemTree == null)
			return null;

		// 初始化音视频图标监听
		mAudioItem = aisDoc.getAudioItem();
		mVideoItem = aisDoc.getVideoItem();
		
		// 解析普通视图为webview
		AisItem imageItems[] = aisDoc.getImageItems();
		
		// 添加图片
		String imageTags = "";
		int imageIndex = 0;
		if (imageItems != null) {
			for (AisItem item : imageItems) {
				if (item != null)
					imageTags += genImgTag(ais_id, item, imageIndex++);
			}
		}

		// 添加文字
		String text = "";
		for (List<AisItem> aisLine : aisItemTree) {
			for (AisItem aisItem : aisLine) {
				if (aisItem != null && aisItem.type == ItemType.TEXT)
					try {
						text += new String(aisItem.data, "GBK");
					} catch (UnsupportedEncodingException e) {
						Debug.Log("编码错误：" + e.getMessage());
					}
			}
		}
		
		// html文本
		return genHtmlString(genMediaIconTags(), imageTags, text);
	}

	/**
	 * 获取html文本
	 * @return
	 */
	private String genHtmlString(String mediaIconTags, String imageTags, String text) {
		// 文本换行（添加<div>）
		return mediaIconTags + "<div class=\"profile-datablock\"><div class=\"profile-content\" style=\"margin-top:8px;font-size:20px;color:white;\">" + imageTags + text.replaceAll("\r", "<div>") + "</div></div>";
	}

	/**
	 * 获取图片标签
	 * @param item
	 * @return
	 */
	private String genMediaIconTags() {
		
		// 隐藏
		if (mAudioItem == null && mVideoItem == null)
			return "";
		
		String audioIconTag = "";
		String videoIconTag = "";
		
		if (mAudioItem != null)
			audioIconTag = "<img src=\"file:///android_asset/ic_audio.png\" onclick=\"ais.playAudio()\" alt=audio/>音频";
		
		if (mVideoItem != null)
			videoIconTag = "<img src=\"file:///android_asset/ic_video.png\" onclick=\"ais.playVideo()\" alt=video/>视频";
		
		return "<div align=\"right\" style=\"margin:4px;font-size:16px;color:white;\">" + audioIconTag + videoIconTag + "</>";
	}
	
	/**
	 * 获取图片标签
	 * @param item
	 * @return
	 */
	private String genImgTag(String aisId, AisItem item, int imageIndex) {
		
		String imageFilePathName = DataMan.DataFile(aisId + "_image" + imageIndex + ".bmp");
		String imageAlt = "图片找不到：" + imageFilePathName;
		
		// 保存图片
		if (!FileUtils.IsFileExist(imageFilePathName))
			ImageUtils.SaveBitmap(item.data, imageFilePathName);

		return "<img src=\"file://" + imageFilePathName + "\"" + 
		        " onclick=\"ais.showImage('" + imageFilePathName + "')\"" +
				" alt=\"" + imageAlt + "\"" +
				" width=\"" + IMAGE_WIDTH + "\"" + 
				" height=\"" + IMAGE_HEIGHT + "\"" +
				" style=\"float:left;margin-right:8px;margin-top:8px;\"/>";
	}
}
