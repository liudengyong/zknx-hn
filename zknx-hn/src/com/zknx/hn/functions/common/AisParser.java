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
	
	// ��������WebView
	private WebView mWebView;

	/**
	 * ��ȡ��������WebView
	 */
	public WebView getWebview() {
		return mWebView;
	}

	/**
	 * ��ȡais��ͼ
	 * @param ais_id
	 * @param context
	 * @return
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public AisLayout GetAisLayout(String ais_id, LayoutInflater inflater, Object jsInterface) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// Ais���ݹ�����ͼ
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);
		
		// Ais���ݹ�����ͼ
		mWebView = (WebView) aisLayout.findViewById(R.id.ais_webview);
		
		// ���JS�ӿ�
		mWebView.getSettings().setJavaScriptEnabled(true); // ����JS�ű�
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // ����cache
		mWebView.addJavascriptInterface(jsInterface, "ais");

		String title = parseAis(ais_id, contentLayout, mWebView);

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
	private String parseAis(String ais_id, LinearLayout root, WebView webView) {
		// ��ȡ�������ais�ĵ�
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
		
		// ����webview
		if (htmlString != null) {
			//webView.loadData(htmlString, "text/html", "GBK");
			webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
		}

		webView.setBackgroundColor(0); // ����͸��

		return title;
	}

	/**
	 * ����Ais��ͼ
	 * @param ais_id
	 * @param webView
	 * @param aisDoc
	 */
	private String genAisWebview(String ais_id, WebView webView, AisDoc aisDoc) {

		List<List<AisItem>> aisItemTree = aisDoc.getItemTree();
		
		if (aisItemTree == null)
			return null;

		// ��ʼ������Ƶͼ�����
		mAudioItem = aisDoc.getAudioItem();
		mVideoItem = aisDoc.getVideoItem();
		
		// ������ͨ��ͼΪwebview
		AisItem imageItems[] = aisDoc.getImageItems();
		
		// ���ͼƬ
		String imageTags = "";
		int imageIndex = 0;
		if (imageItems != null) {
			for (AisItem item : imageItems) {
				if (item != null)
					imageTags += genImgTag(ais_id, item, imageIndex++);
			}
		}

		// �������
		String text = "";
		for (List<AisItem> aisLine : aisItemTree) {
			for (AisItem aisItem : aisLine) {
				if (aisItem != null && aisItem.type == ItemType.TEXT)
					try {
						text += new String(aisItem.data, "GBK");
					} catch (UnsupportedEncodingException e) {
						Debug.Log("�������" + e.getMessage());
					}
			}
		}
		
		// html�ı�
		return genHtmlString(genMediaIconTags(), imageTags, text);
	}
	
	/**
	 * ����Ais�Ծ���ͼ
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
	 * ����Ais�Ծ���Ŀ
	 * @param i
	 * @return
	 */
	private String genQuestionTags(AisDoc aisDoc, String aisId, int i) {
		// ����  + ͼƬ + CheckBox
		
		String imageFilePathName = DataMan.DataFile(aisId + "_question" + i + ".bmp");
		String imageAlt = "ͼƬ�Ҳ�����" + imageFilePathName;
		
		// ����ͼƬ
		saveImageToFile(aisDoc.getQuestionBitmapData(i), imageFilePathName);

		// ����Ŀ֮��ļ��
		String paddingTop = "";
		if (i != 0)
			paddingTop = "style=\"padding-top:40px;\"";

		String tagQuestionBitmap = "<li " + paddingTop + "><img src=\"file://" + imageFilePathName + "\"" + 
				" alt=\"" + imageAlt + "\"" +
				" style=\"vertical-align:text-top;\"" +
				"</li>";

		char[] anwsers = {'A', 'B', 'C', 'D'};
		String tagAnswer = "<div/>�ش�";
		for (char anwser : anwsers) {
			tagAnswer += (anwser + "<input type=checkbox name=answer id=" + GetAnswerTagId(aisId, i, anwser) + " value=" + anwser + ">"); 
		}

		// ���غ�Сʱ����
		String noteTagId = GetNoteTagId(aisId, i);
		String tagNote = "<div/><label id=" + noteTagId + " style=\"display:none;\">������" + aisDoc.getQuestionNote(i) + "<label/>";
		
		return tagQuestionBitmap + tagAnswer + tagNote;
	}
	
	/**
	 * ��ȡ��Tag��id
	 * @param aisId
	 * @param i
	 * @return
	 */
	private static String GetAnswerTagId(String aisId, int i, char answer) {
		return aisId + "_answer" + i + "_" + answer;
	}
	
	/**
	 * ��ȡ����Tag��id
	 * @param aisId
	 * @param i
	 * @return
	 */
	private static String GetNoteTagId(String aisId, int i) {
		return aisId + "_note" + i;
	}
	
	/**
	 * ��ȡhtml�ı�
	 * @return
	 */
	private String genHtmlString(String mediaIconTags, String imageTags, String text) {
		// �ı����У����<div>��
		return mediaIconTags + "<div class=\"profile-datablock\"><div class=\"profile-content\" style=\"margin-top:8px;font-size:20px;color:white;\">" + imageTags + text.replaceAll("\r", "<div>") + "</div></div>";
	}

	/**
	 * ��ȡͼƬ��ǩ
	 * @param item
	 * @return
	 */
	private String genMediaIconTags() {
		
		// ����
		if (mAudioItem == null && mVideoItem == null)
			return "";
		
		String audioIconTag = "";
		String videoIconTag = "";
		
		if (mAudioItem != null)
			audioIconTag = "<img src=\"file:///android_asset/ic_audio.png\" onclick=\"ais.playAudio()\" alt=audio/>��Ƶ";
		
		if (mVideoItem != null)
			videoIconTag = "<img src=\"file:///android_asset/ic_video.png\" onclick=\"ais.playVideo()\" alt=video/>��Ƶ";
		
		return "<div align=\"right\" style=\"margin:4px;font-size:16px;color:white;\">" + audioIconTag + videoIconTag + "</>";
	}
	
	/**
	 * ��ȡͼƬ��ǩ
	 * @param item
	 * @return
	 */
	private String genImgTag(String aisId, AisItem item, int imageIndex) {
		
		String imageFilePathName = DataMan.DataFile(aisId + "_image" + imageIndex + ".bmp");
		String imageAlt = "ͼƬ�Ҳ�����" + imageFilePathName;
		
		// ����ͼƬ
		saveImageToFile(item.data, imageFilePathName);

		return "<img src=\"file://" + imageFilePathName + "\"" + 
		        " onclick=\"ais.showImage('" + imageFilePathName + "')\"" +
				" alt=\"" + imageAlt + "\"" +
				" width=\"" + IMAGE_WIDTH + "\"" + 
				" height=\"" + IMAGE_HEIGHT + "\"" +
				" style=\"float:left;margin-right:8px;margin-top:8px;\"/>";
	}
	
	/**
	 * ����ͼƬ
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
				Debug.Log("���ش��󣺲���ѹ��ͼƬ��" + e.getMessage());
			} catch (Throwable e) {
				Debug.Log("���ش����ڴ治�㣬setAisImage");
			}
		}
	}
}
