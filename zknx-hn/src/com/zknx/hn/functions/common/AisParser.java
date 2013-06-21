package com.zknx.hn.functions.common;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.WebkitClient;
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
	
	private final static String URL_FILE_CRECT_RESULT = "file:///android_asset/icon/crect.png";
	private final static String URL_FILE_INCRECT_RESULT = "file:///android_asset/icon/increct.png";

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
		mWebView.setWebChromeClient(new WebkitClient());

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
		
		if (isCourse) {
			InitCourseHtml(ais_id, webView, aisDoc);
		} else {
			String htmlString = genAisWebview(ais_id, webView, aisDoc);
			
			// 加载webview
			if (htmlString != null) {
				//webView.loadData(htmlString, "text/html", "GBK");
				webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
			}
		}

		webView.setBackgroundColor(0); // 设置透明

		return title;
	}

	/**
	 * 初始化课件试题
	 * @param webView
	 */
	private static void InitCourseHtml(String aisId, WebView webView, AisDoc aisDoc) {
		
		int total = 0;
		int count = aisDoc.getQuestionCount();
		String questionTags = "";
		for (int i = 0; i < count; ++i) {
			questionTags += GenQuestionTags(aisDoc, aisId, i);
			total += aisDoc.getQuestionGrade(i);
		}

		String jsInitMethod = "initTest(" + count+ ")";
		String charset = "<head><meta http-equiv=\"Content-Type\" content=\"text/html\"; charset=gb2312/>";
		String cssLink = "";//"<link href=\"file:///android_asset/ais.css\" rel=\"stylesheet\" type=\"text/css\">";
		String jsScript = "<script type=\"text/javascript\" src=\"file:///android_asset/course.js\"></script></head>";
		String totalPoints = "<body onload=\""+ jsInitMethod + "\"><div align=\"right\" style=\"margin-top:4px;font-size:18px;color:white;\">总分：" + total + "分</div>";
		String aisHiddenInfo = "<div id=crectIcon style=\"display:none;\">" + URL_FILE_CRECT_RESULT + "</div>" +
				"<div id=increctIcon style=\"display:none;\"/>" + URL_FILE_INCRECT_RESULT + "</div>";

		String htmlString = charset + cssLink + jsScript + totalPoints + aisHiddenInfo + "<ol style=\"font-size:18px;color:white;\" >" + questionTags + "</ol></body>";

		webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
	}
	
	/**
	 * 生成Ais试卷题目
	 * @param i
	 * @return
	 */
	private static String GenQuestionTags(AisDoc aisDoc, String aisId, int i) {
		// 标题  + 图片 + CheckBox
		
		String imageFilePathName = DataMan.DataFile(aisId + "_question" + i + ".bmp");
		String imageAlt = "图片找不到：" + imageFilePathName;
		
		// 保存图片
		if (!FileUtils.IsFileExist(imageFilePathName))
			SaveImageToFile(aisDoc.getQuestionBitmapData(i), imageFilePathName);

		// 各题目之间的间隔
		String paddingTop = "";
		if (i != 0)
			paddingTop = "style=\"padding-top:40px;\"";

		String tagQuestionBitmap = "<li " + paddingTop + "><img src=\"file://" + imageFilePathName + "\"" + 
				" alt=\"" + imageAlt + "\"" +
				" style=\"vertical-align:text-top;\"" +
				"</li>";

		char[] anwsers = {'A', 'B', 'C', 'D'};
		String tagAnswer = "答题（" + aisDoc.getQuestionGrade(i) + "分）：";
		for (char anwser : anwsers) {
			tagAnswer += (anwser + "<input type=checkbox id=" + GetAnswerTagId(i, anwser) + " value=" + anwser + ">"); 
		}
		
		String rightAnwser = "";
		byte[] rightAnwserBytes = aisDoc.getQuestionAnswer(i);
		if (rightAnwserBytes != null) {
			for (byte a : rightAnwserBytes)
				// 答案长度一定，如果不是全部正确则会余0
				if (a != 0)
					rightAnwser += (char)a;
		}

		String tagRightAnwser = "<div id=" + GetRightAnwserTagId(i) + " style=\"display:none;\">" + aisDoc.getQuestionGrade(i) + DataMan.COMMON_TOKEN + rightAnwser + "</div>";
		String result = "<img id=" + GetResultTagId(i) + " style=\"visibility:hidden;vertical-align:text-bottom;\"/>";
		tagAnswer += (result + tagRightAnwser);

		// 隐藏和显示解析
		String noteTagId = GetNoteTagId(i);
		String tagNote = "<label id=" + noteTagId + " style=\"display:none;\">解析：" + aisDoc.getQuestionNote(i) + "<label/>";

		final String DIV = "<div/>";
		return tagQuestionBitmap + DIV + tagAnswer + DIV + tagNote;
	}
	
	/**
	 * 获取答案Tag的id
	 * @param i
	 * @return
	 */
	private static String GetAnswerTagId(int i, char answer) {
		return "anwser" + i + "_" + answer;
	}
	
	/**
	 * 获取解析Tag的id
	 * @param i
	 * @return
	 */
	private static String GetNoteTagId(int i) {
		return "note" + i;
	}
	
	/**
	 * 获取结果Tag的id
	 * @param i
	 * @return
	 */
	private static String GetResultTagId(int i) {
		return "result" + i;
	}
	
	/**
	 * 获取正确答案Tag的id
	 * @param i
	 * @return
	 */
	private static String GetRightAnwserTagId(int i) {
		return "rightAnwser" + i;
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
		SaveImageToFile(item.data, imageFilePathName);

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
	private static void SaveImageToFile(byte[] data, String fileName) {
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
