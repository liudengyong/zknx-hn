package com.zknx.hn.functions.common;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.functions.common.AisDoc.AisItem;
import com.zknx.hn.functions.common.AisDoc.ItemType;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
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


	/**
	 * ��ȡais��ͼ
	 * @param ais_id
	 * @param context
	 * @return
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public AisLayout GetAisLayout(int ais_id, LayoutInflater inflater, Object jsInterface) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// Ais���ݹ�����ͼ
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);
		
		// Ais���ݹ�����ͼ
		WebView webView = (WebView) aisLayout.findViewById(R.id.ais_webview);
		
		// ���JS�ӿ�
		webView.getSettings().setJavaScriptEnabled(true); // ����JS�ű�
        webView.addJavascriptInterface(jsInterface, "ais");

		String title = parseAis(ais_id, contentLayout, webView);

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
	private String parseAis(int ais_id, LinearLayout root, WebView webView) {
		// ��ȡ�������ais�ĵ�
		AisDoc aisDoc = new AisDoc(ais_id);
		String title = aisDoc.getTitle();
		List<List<AisItem>> aisItemTree = aisDoc.getItemTree();
		
		if (title != null && aisItemTree != null) {

			// ��ʼ������Ƶͼ�����
			mAudioItem = aisDoc.getAudioItem();
			mVideoItem = aisDoc.getVideoItem();
			
			if (mAudioItem == null)
			    Debug.Log("mAudioItem is null");
			else
				Debug.Log("mAudioItem is not null");
			if (mVideoItem == null)
				Debug.Log("mVideoItem is null");
			
			// ������ͨ��ͼΪwebview
			AisItem imageItems[] = aisDoc.getImageItems();
			
			// ���ͼƬ
			String imageTags = "";
			int imageIndex = 0;
			if (imageItems != null) {
				for (AisItem item : imageItems) {
					if (item != null)
						imageTags += genImgTag(item, imageIndex++);
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
			String htmlString = genHtmlString(genMediaIconTags(), imageTags, text);

			// ����webview
			webView.loadData(htmlString, "text/html", "GBK");
			webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
			webView.setBackgroundColor(0); // ����͸��
			
			return title;
		}
		
		mAudioItem = null;
		mVideoItem = null;
		
		return null;
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
	private String genImgTag(AisItem item, int imageIndex) {
		
		String imageFilePathName = DataMan.DataFile("image" + imageIndex + ".bmp");
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
