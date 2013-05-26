package com.zknx.hn.functions.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.Dialog;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;
import com.zknx.hn.functions.common.AisDoc.AisItem;
import com.zknx.hn.functions.common.AisDoc.ItemType;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AisParser {

	LayoutInflater mInflater;
	
	private final int IMAGE_WIDTH = 240;
	private final int IMAGE_HEIGHT = 360;

	// ���ڲ�����Ƶ
	AisItem mAudioItem;
	AisItem mVideoItem;
	MediaPlayer mPlayer;

	public AisParser(LayoutInflater inflater) {
		mInflater = inflater;
		
		mPlayer = new MediaPlayer();
	}

	/**
	 * ��ȡais��ͼ
	 * @param ais_id
	 * @param context
	 * @return
	 */
	public static AisLayout GetAisLayout(int ais_id, LayoutInflater inflater, OnClickListener clickImage) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);
		
		// ����Ƶͼ��
		LinearLayout mediaIconLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_view_media_icon);
		
		// Ais���ݹ�����ͼ
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);
		
		// Ais���ݹ�����ͼ
		WebView webView = (WebView) aisLayout.findViewById(R.id.ais_webview);

		String title = new AisParser(inflater).parseAis(ais_id, contentLayout, webView, mediaIconLayout, clickImage);

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
	private String parseAis(int ais_id, LinearLayout root, WebView webView, LinearLayout mediaIconLayout, OnClickListener clickImage) {
		// ��ȡ�������ais�ĵ�
		AisDoc aisDoc = new AisDoc(ais_id);
		String title = aisDoc.getTitle();
		List<List<AisItem>> aisItemTree = aisDoc.getItemTree();
		
		if (title != null && aisItemTree != null) {

			// ��ʼ������Ƶͼ�����
			mAudioItem = aisDoc.getAudioItem();
			mVideoItem = aisDoc.getVideoItem();

			initMediaImage(mediaIconLayout, R.id.ais_view_audio_icon, mAudioItem);
			initMediaImage(mediaIconLayout, R.id.ais_view_video_icon, mVideoItem);
			
			// TODO �����μ�
			if (ais_id == DataMan.INVALID_ID) {

				// ����webview
				webView.setVisibility(View.GONE);

				Context context = root.getContext();
				// �ӽ�������ais�ĵ�����������ͼ
				for (List<AisItem> aisLine : aisItemTree) {
					for (AisItem aisItem : aisLine) {
	
						AisView item = getAisItemView(aisItem, context);
	
						// TODO AIS �Ű��Ż���һ�У�
						if (item != null)
							root.addView(item.view);
					}
				}
	
				AisItem imageItems[] = aisDoc.getImageItems();
	
				if (imageItems != null && imageItems.length > 0) {
					
					LinearLayout imagePreview = (LinearLayout) mInflater.inflate(R.layout.ais_view_images_preview, null);
	
					setAisImage(imagePreview, R.id.ais_view_image1_preview, imageItems[0], clickImage);
					setAisImage(imagePreview, R.id.ais_view_image2_preview, imageItems[1], clickImage);
					setAisImage(imagePreview, R.id.ais_view_image3_preview, imageItems[2], clickImage);
	
					// ���ͼƬԤ����ͼ
					root.addView(imagePreview);
				}
			} else { // ������ͨ��ͼΪwebview
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
				String htmlString = genHtmlString(imageTags, text);
	
				// ����webview
				webView.loadData(htmlString, "text/html", "GBK");
				webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
				webView.setBackgroundColor(0); // ����͸��
			}
			
			// �����ɹ�������Ƶ
			playAudio();
			
			return title;
		}
		
		return null;
	}
	
	/**
	 * ��ȡhtml�ı�
	 * @return
	 */
	private String genHtmlString(String imageTags, String text) {
		// �ı����У����<div>��
		return "<div class=\"profile-datablock\"><div class=\"profile-content\" style=\"font-size:20px;color:white;\">" + imageTags + text.replaceAll("\r", "<div>") + "</div></div>";
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

	/**
	 * ����AisͼƬ
	 */
	private void setAisImage(LinearLayout imagePreview, int imageViewId, AisItem imageItem, OnClickListener clickImage) {
		if (imageItem != null) {
			ImageView imageView = (ImageView)imagePreview.findViewById(imageViewId);
			try {
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageItem.data, 0, imageItem.data.length);
				imageView.setImageBitmap(bitmap);
				imageView.setOnClickListener(clickImage);
			} catch (Throwable e) {
				Debug.Log("���ش����ڴ治�㣬setAisImage");
			}
		}
	}
	
	/**
	 * ͨ��AisItem��ȡitem��ͼ
	 * @param aisItem
	 * @return
	 */
	private AisView getAisItemView(AisItem aisItem, Context context) {
		AisView aisView = null;
		
		switch (aisItem.type) {
		case CHEKCBOX:
			aisView = new AisCheckBox(context, aisItem);
			break;
		case TEXT:
			aisView = new AisText(context, aisItem);
			break;
		case IMAGE:
		case VIDEO:
		case AUDIO:
		default:
			Debug.Log("���ش���getAisItemView��" + aisItem.type);
			break;
		}
		
		return aisView;
	}
	
	/**
	 * Ais��ͼ
	 */
	class AisView {

		AisView(Context _context, AisItem _item) {
			context = _context;
			item = _item;
		}

		Context context;
		AisItem item;
		View view;
	}
	
	/**
	 * Ais�ı���ͼ
	 */
	
	class AisText extends AisView {
		
		AisText(Context context, AisItem item) {
			super(context, item);
			
			view = genText();
		}
		
		/**
		 * �����ı���ͼ
		 */
		private TextView genText() {

			TextView tv = new TextView(context);

			String text = EncodingUtils.getString(item.data, "GB2312");
			// ����Windows����
			tv.setText(text.replaceAll("\r", "\n"));
			tv.setGravity(Gravity.LEFT);

			tv.setLayoutParams(UIConst.GetLayoutParams(L_LAYOUT_TYPE.WRAP));

			return tv;
		}
	}
	
	/**
	 * ��ʼ������Ƶ�����¼�
	 * @param mediaResId
	 */
	private void initMediaImage(LinearLayout mediaIconLayout, int mediaResId, AisItem mediaAisItem) {
		if (mediaAisItem != null) {
			mediaIconLayout.setVisibility(View.VISIBLE);
			View mediaView = mediaIconLayout.findViewById(mediaResId);
			mediaView.setVisibility(View.VISIBLE);
			mediaView.setOnClickListener(mClickMediaIcon);
		}
	}

	/**
	 * ��������Ƶ
	 */
	private OnClickListener mClickMediaIcon = new OnClickListener() {
		@Override
		public void onClick(View view) {
			
			switch (view.getId()) {
			case R.id.ais_view_audio_icon:
				playAudio();
				break;
			case R.id.ais_view_video_icon:
                playVideo();
                break;
			default:
				Debug.Log("���ش���mClickMediaIcon��������");
				break;
			}
		}
	};
	
	/**
	 * ������Ƶ
	 */
	private void playAudio() {
		if (mAudioItem == null) {
			Debug.Log("������Ƶ����mAudioItem��");
			return;
		}
		
		// �������ֹͣ����
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
			Dialog.Toast(mInflater.getContext(), R.string.stop_play_audio);
			return;
		}

		Exception exp = null;
		try {
			String tmpFileName = DataMan.DataFile("tmp.mp3");
			FileUtils.WriteFile(tmpFileName, mAudioItem.data);
			mPlayer.reset();
			mPlayer.setDataSource(tmpFileName);
			mPlayer.prepare();
			mPlayer.start();
			Dialog.Toast(mInflater.getContext(), R.string.start_play_audio);
		} catch (IllegalArgumentException e) {
			exp = e;
		} catch (SecurityException e) {
			exp = e;
		} catch (IllegalStateException e) {
			exp = e;
		} catch (IOException e) {
			exp = e;
		}

		if (exp != null)
			Debug.Log("�����쳣��" + exp.getMessage());
	}
	
	/**
	 * ������Ƶ
	 */
	private void playVideo() {
		if (mVideoItem != null) {
			String tmpFile = DataMan.DataFile("tmp.mp4");
			FileUtils.WriteFile(tmpFile, mVideoItem.data);
			Intent it = new Intent(Intent.ACTION_VIEW);  
	        it.setDataAndType(Uri.parse(tmpFile), "video/mp4");  
	        mInflater.getContext().startActivity(it);
		}
	}

	/**
	 * Ais��ѡ����ͼ
	 */
	
	class AisCheckBox extends AisView {
		
		AisCheckBox(Context context, AisItem item) {
			super(context, item);
			
			view = genCheckbox();
		}
		
		/**
		 * �����ı���ͼ
		 * TODO AIS ����AIS����ͼ  ��ѡ
		 */
		private CheckBox genCheckbox() {

			CheckBox tv = new CheckBox(context);
			tv.setText("TODO:Test AIS View");
			tv.setGravity(Gravity.CENTER);
			
			tv.setLayoutParams(UIConst.GetLayoutParams(L_LAYOUT_TYPE.WRAP));
			
			return tv;
		}
	}
}
