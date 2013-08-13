package com.zknx.hn.functions.ais;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.zknx.hn.common.Debug;
import com.zknx.hn.common.widget.ImageUtils;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;
import com.zknx.hn.functions.ais.AisDoc.AisItem;
import com.zknx.hn.functions.ais.AisDoc.ItemType;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AisWebView {

	/**
	 * ��ʼ����ͼ
	 * @param webView
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public static void Init(AisDoc aisDoc, WebView webView, Object jsInterface) {
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // ����cache
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setBackgroundColor(0);

		String htmlString = genAisWebview(aisDoc.getAisId(), webView, aisDoc);
		
		// ����webview
		if (htmlString != null) {
			//webView.loadData(htmlString, "text/html", "GBK");
			webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
			webView.setVisibility(View.VISIBLE);
		} else {
			webView.setVisibility(View.GONE);
		}
	}
	

	/**
	 * ����Ais��ͼ
	 * @param ais_id
	 * @param webView
	 * @param aisDoc
	 */
	private static String genAisWebview(String ais_id, WebView webView, AisDoc aisDoc) {

		List<List<AisItem>> aisItemTree = aisDoc.getItemTree();
		
		if (aisItemTree == null)
			return null;
		
		// ������ͨ��ͼΪwebview
		AisItem imageItems[] = aisDoc.getImageItems();
		
		// ����ͼƬ
		String imageTags = "";
		int imageIndex = 0;
		if (imageItems != null) {
			for (AisItem item : imageItems) {
				if (item != null)
					imageTags += genImgTag(ais_id, item, imageIndex++);
			}
		}

		// ��������
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
		
		String mediaTags = "";//genMediaIconTags(aisDoc);

		String htmlStirng = mediaTags +
				"<div style=\"margin-top:8px;font-size:20px;color:white;\">" +
					imageTags +
					text.replaceAll("\r", "<div>") +
				"</div>";

		return htmlStirng;
	}

	/**
	 * ��ȡͼƬ��ǩ
	 * @param item
	 * @return
	 */
	private static String genMediaIconTags(AisDoc aisDoc) {
		
		AisItem audioItem = aisDoc.getAudioItem();
		AisItem videoItem = aisDoc.getVideoItem();
		
		// ����
		if (audioItem == null && videoItem == null)
			return "";
		
		String audioIconTag = "";
		String videoIconTag = "";
		
		if (audioItem != null)
			audioIconTag = "<img src=\"file:///android_asset/ic_audio.png\" onclick=\"ais.playAudio()\" alt=audio/>��Ƶ";
		
		if (videoItem != null)
			videoIconTag = "<img src=\"file:///android_asset/ic_video.png\" onclick=\"ais.playVideo()\" alt=video/>��Ƶ";
		
		return "<div align=\"right\" style=\"margin:4px;font-size:16px;color:white;\">" + audioIconTag + videoIconTag + "</>";
	}
	
	/**
	 * ��ȡͼƬ��ǩ
	 * @param item
	 * @return
	 */
	private static String genImgTag(String aisId, AisItem item, int imageIndex) {

		aisId = aisId.trim(); // ɾ�����ܴ��ڵĿո�

		String imageFilePathName = DataMan.DataFile(aisId + "_image" + imageIndex + ".bmp", true);
		String imageAlt = "ͼƬ�Ҳ�����" + imageFilePathName;

		// ����ͼƬ
		if (!FileUtils.IsFileExist(imageFilePathName))
			ImageUtils.SaveBitmap(item.data, imageFilePathName);

		return "<img src=\"file://" + imageFilePathName + "\"" + 
		        " onclick=\"ais.showImage('" + imageFilePathName + "')\"" +
				" alt=\"" + imageAlt + "\"" +
				" style=\"float:left;margin-right:8px;margin-top:8px;\"/>";
	}
}
