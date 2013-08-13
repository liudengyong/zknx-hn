package com.zknx.hn.functions.ais;

import java.io.IOException;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;
import com.zknx.hn.functions.ais.AisDoc.AisHeader;
import com.zknx.hn.functions.ais.AisDoc.AisItem;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class AisParser {

	LayoutInflater mInflater;
	
	// ���ڲ�����Ƶ
	private AisItem mAudioItem;
	private AisItem mVideoItem;
	
	MediaPlayer mPlayer;

	public AisParser(LayoutInflater inflater) {
		mInflater = inflater;
		
		mPlayer = new MediaPlayer();
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

		// ����Ƶͼ��
		LinearLayout mediaIconLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_view_media_icon);
		
		// Ais���ݹ�����ͼ
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);

		AisHeader header = parseAis(inflater.getContext(), date, aisFileName, contentLayout, jsInterface, mediaIconLayout);

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
	private AisHeader parseAis(Context context, String date, String aisFileName, LinearLayout contentLayout, Object jsInterface, LinearLayout mediaIconLayout) {
		// ��ȡ�������ais�ĵ�
		AisDoc aisDoc = new AisDoc(context, aisFileName, false, date);
		
		// ��ʼ������Ƶͼ�����
		mAudioItem = aisDoc.getAudioItem();
		mVideoItem = aisDoc.getVideoItem();
		
		initMediaImage(mediaIconLayout, R.id.ais_view_audio_icon, mAudioItem);
		initMediaImage(mediaIconLayout, R.id.ais_view_video_icon, mVideoItem);

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
}
