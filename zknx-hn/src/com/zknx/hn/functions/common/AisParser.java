package com.zknx.hn.functions.common;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;
import com.zknx.hn.functions.common.AisDoc.AisItem;
import com.zknx.hn.functions.common.AisDoc.ItemType;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
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
	
	// 解析出的WebView
	private WebView mWebView;

	/**
	 * 获取解析出的WebView
	 */
	public WebView getWebview() {
		return mWebView;
	}

	/**
	 * 获取ais视图
	 * @param ais_id
	 * @param context
	 * @return
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public AisLayout GetAisLayout(String ais_id, LayoutInflater inflater, Object jsInterface) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// Ais内容滚动视图
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);
		
		// Ais内容滚动视图
		mWebView = (WebView) aisLayout.findViewById(R.id.ais_webview);
		
		// 添加JS接口
		mWebView.getSettings().setJavaScriptEnabled(true); // 启用JS脚本
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // 禁用cache
		mWebView.addJavascriptInterface(jsInterface, "ais");

		String title = parseAis(ais_id, contentLayout, mWebView);

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
	private String parseAis(String ais_id, LinearLayout root, WebView webView) {
		// 获取解析后的ais文档
		AisDoc aisDoc = new AisDoc(ais_id);
		String title = aisDoc.getTitle();
		boolean isCourse = aisDoc.isCourse();
		
		mAudioItem = null;
		mVideoItem = null;
		
		if (title == null)
			return null;
		
		String htmlString =null;
		if (isCourse)
			htmlString = genAisCourseWebview(ais_id, webView, aisDoc);
		else
			htmlString = genAisWebview(ais_id, webView, aisDoc);
		
		// 加载webview
		if (htmlString != null) {
			//webView.loadData(htmlString, "text/html", "GBK");
			webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
		}

		webView.setBackgroundColor(0); // 设置透明

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
	 * 生成Ais试卷视图
	 * @param ais_id
	 * @param webView
	 * @param aisDoc
	 */
	private String genAisCourseWebview(String ais_id, WebView webView, AisDoc aisDoc) {

		String htmlString = "";
		for (int i = 0; i < aisDoc.getQuestionCount(); ++i) {
			htmlString += genQuestionTags(aisDoc, ais_id, i);
		}

		String cssLink = "<link href=\"file:///android_asset/ais.css\" rel=\"stylesheet\" type=\"text/css\">";
		String jsLink = "<link href=\"file:///android_asset/course.js\" type=\"text/javascript\">";
		String aisHiddenInfo = "<label id=aisId style=\"display:none;\">" + ais_id + "</label><label id=questionCount style=\"display:none;\">" + aisDoc.getQuestionCount() + "</label>";

		return cssLink + jsLink + aisHiddenInfo + "<ol>" + htmlString + "</ol>";
	}
	
	/**
	 * 生成Ais试卷题目
	 * @param i
	 * @return
	 */
	private String genQuestionTags(AisDoc aisDoc, String aisId, int i) {
		// 标题  + 图片 + CheckBox
		
		String imageFilePathName = DataMan.DataFile(aisId + "_question" + i + ".bmp");
		String imageAlt = "图片找不到：" + imageFilePathName;
		
		// 保存图片
		saveImageToFile(aisDoc.getQuestionBitmapData(i), imageFilePathName);

		// 各题目之间的间隔
		String paddingTop = "";
		if (i != 0)
			paddingTop = "style=\"padding-top:40px;\"";

		String tagQuestionBitmap = "<li " + paddingTop + "><img src=\"file://" + imageFilePathName + "\"" + 
				" alt=\"" + imageAlt + "\"" +
				" style=\"vertical-align:text-top;\"" +
				"</li>";

		char[] anwsers = {'A', 'B', 'C', 'D'};
		String tagAnswer = "<div/>回答：";
		for (char anwser : anwsers) {
			tagAnswer += (anwser + "<input type=checkbox name=answer id=" + GetAnswerTagId(aisId, i, anwser) + " value=" + anwser + ">"); 
		}

		// 隐藏和小时解析
		String noteTagId = GetNoteTagId(aisId, i);
		String tagNote = "<div/><label id=" + noteTagId + " style=\"display:none;\">解析：" + aisDoc.getQuestionNote(i) + "<label/>";
		
		return tagQuestionBitmap + tagAnswer + tagNote;
	}
	
	/**
	 * 获取答案Tag的id
	 * @param aisId
	 * @param i
	 * @return
	 */
	private static String GetAnswerTagId(String aisId, int i, char answer) {
		return aisId + "_answer" + i + "_" + answer;
	}
	
	/**
	 * 获取解析Tag的id
	 * @param aisId
	 * @param i
	 * @return
	 */
	private static String GetNoteTagId(String aisId, int i) {
		return aisId + "_note" + i;
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
		saveImageToFile(item.data, imageFilePathName);

		return "<img src=\"file://" + imageFilePathName + "\"" + 
		        " onclick=\"ais.showImage('" + imageFilePathName + "')\"" +
				" alt=\"" + imageAlt + "\"" +
				" width=\"" + IMAGE_WIDTH + "\"" + 
				" height=\"" + IMAGE_HEIGHT + "\"" +
				" style=\"float:left;margin-right:8px;margin-top:8px;\"/>";
	}
	
	/**
	 * 保存图片
	 */
	private void saveImageToFile(byte[] data, String fileName) {
		if (data != null) {
			try {
				if (FileUtils.IsFileExist(fileName))
					FileUtils.DeleteFile(fileName);

				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				FileOutputStream out = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			} catch (Exception e) {
				Debug.Log("严重错误：不能压缩图片，" + e.getMessage());
			} catch (Throwable e) {
				Debug.Log("严重错误：内存不足，setAisImage");
			}
		}
	}
}
