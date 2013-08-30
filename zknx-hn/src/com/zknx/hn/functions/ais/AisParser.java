package com.zknx.hn.functions.ais;

import java.io.IOException;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;
import com.zknx.hn.functions.AisView;
import com.zknx.hn.functions.ais.AisDoc.AisHeader;
import com.zknx.hn.functions.ais.AisDoc.AisItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class AisParser {

	LayoutInflater mInflater;
	
	// ���ڲ�����Ƶ
	private AisItem mAudioItem;
	private AisItem mVideoItem;
	
	// �������ais�ĵ�
	private AisDoc mAisDoc;
	
	MediaPlayer mPlayer;

	public AisParser(LayoutInflater inflater) {
		mInflater = inflater;
		
		mPlayer = new MediaPlayer();
		
		mAudioItem = null;
		mVideoItem = null;
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
	public AisLayout GetAisLayout(LayoutInflater inflater, Object jsInterface) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// ����Ƶͼ��
		RelativeLayout mediaIconLayout = (RelativeLayout) aisLayout.findViewById(R.id.ais_view_media_icon);
		
		// Ais���ݹ�����ͼ
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);

		AisHeader header = parseAis(contentLayout, jsInterface, mediaIconLayout);

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
	 * ����ais�ĵ�
	 * @return
	 */
	public boolean parseAisDoc(Context context, String date, String aisFileName) {
		// ��ȡ�������ais�ĵ�
		mAisDoc = new AisDoc(context, aisFileName, false, date);
		
		return (mAisDoc != null);
	}

	/**
	 * ����ɹ������ر��⣬�������ͼ��root�����򷵻�null
	 * @param ais_id
	 * @param root
	 * @return
	 */
	private AisHeader parseAis(LinearLayout contentLayout, Object jsInterface, RelativeLayout mediaIconLayout) {
		
		
		if (mAisDoc == null) {
			Debug.Log("���ش���parseAis��aisDocΪ��");
			return null;
		}
		
		// ��ʼ������Ƶͼ�����
		mAudioItem = mAisDoc.getAudioItem();
		mVideoItem = mAisDoc.getVideoItem();

		Button videoBtn = null;
		// û��ý�������
		if (mAudioItem == null &&
			mVideoItem == null)
			mediaIconLayout.setVisibility(View.GONE);
		else {
			Button mAudioBtn = (Button) mediaIconLayout.findViewById(R.id.ais_view_audio_icon_btn);
			if (mAudioItem != null) {
				// ����Ƶͼ��ɵ��
				mAudioBtn.setOnClickListener(mClickMediaIcon);
			} else {
				mAudioBtn.setVisibility(View.GONE);
			}

			videoBtn = (Button) mediaIconLayout.findViewById(R.id.ais_view_video_icon_btn);
			if (mVideoItem != null) {
				// ����Ƶͼ��ɵ��
				videoBtn.setOnClickListener(mClickMediaIcon);
			} else {
				videoBtn.setVisibility(View.GONE);
			}
		}

		// Ais���ݹ�����ͼ
		WebView webView = (WebView) contentLayout.findViewById(R.id.ais_webview);

		// �Ƿ�μ�
		if (mAisDoc.isCourse()) {
			webView.setVisibility(View.GONE);
			//CourseWebView.GenHtml(ais_id, webView, aisDoc);
			CourseView.InitView(mInflater, contentLayout, mAisDoc);
		} else {
			AisWebView.Init(mAisDoc, webView, jsInterface);
		}

		// û�����ֺ�ͼƬ������Ƶʱ�����л�����Ƶ
		if (videoBtn != null &&
			webView.getVisibility() == View.GONE)
			videoBtn.requestFocus();
		/*
		else
			webView.requestFocus();
		*/

		return mAisDoc.getHeader();
	}

	/**
	 * ��������Ƶ
	 */
	private OnClickListener mClickMediaIcon = new OnClickListener() {
		@Override
		public void onClick(View view) {
			
			switch (view.getId()) {
			case R.id.ais_view_audio_icon_btn:
				playAudio();
				break;
			case R.id.ais_view_video_icon_btn:
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

			// �������Ƶ���ţ���ֹͣ��Ƶ
        	AisView.StopPlaying();

			// ���ű���õ���Ƶ�ļ�
			String tmpFile = mVideoItem.fileName;
			String rmvbMimetype = "audio/x-pn-realaudio";
			String mimetype = "video/mp4";
			// TODO ����rmvb��Ƶ

			Intent it = new Intent(Intent.ACTION_VIEW);  
	        it.setDataAndType(Uri.parse(tmpFile), mimetype);
	        mInflater.getContext().startActivity(it);
		}
	}
}
