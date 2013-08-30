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
	
	// 用于播放音频
	private AisItem mAudioItem;
	private AisItem mVideoItem;
	
	// 解析后的ais文档
	private AisDoc mAisDoc;
	
	MediaPlayer mPlayer;

	public AisParser(LayoutInflater inflater) {
		mInflater = inflater;
		
		mPlayer = new MediaPlayer();
		
		mAudioItem = null;
		mVideoItem = null;
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
	public AisLayout GetAisLayout(LayoutInflater inflater, Object jsInterface) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// 音视频图标
		RelativeLayout mediaIconLayout = (RelativeLayout) aisLayout.findViewById(R.id.ais_view_media_icon);
		
		// Ais内容滚动视图
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);

		AisHeader header = parseAis(contentLayout, jsInterface, mediaIconLayout);

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
	 * 解析ais文档
	 * @return
	 */
	public boolean parseAisDoc(Context context, String date, String aisFileName) {
		// 获取解析后的ais文档
		mAisDoc = new AisDoc(context, aisFileName, false, date);
		
		return (mAisDoc != null);
	}

	/**
	 * 如果成功，返回标题，并添加视图到root，否则返回null
	 * @param ais_id
	 * @param root
	 * @return
	 */
	private AisHeader parseAis(LinearLayout contentLayout, Object jsInterface, RelativeLayout mediaIconLayout) {
		
		
		if (mAisDoc == null) {
			Debug.Log("严重错误：parseAis，aisDoc为空");
			return null;
		}
		
		// 初始化音视频图标监听
		mAudioItem = mAisDoc.getAudioItem();
		mVideoItem = mAisDoc.getVideoItem();

		Button videoBtn = null;
		// 没有媒体就隐藏
		if (mAudioItem == null &&
			mVideoItem == null)
			mediaIconLayout.setVisibility(View.GONE);
		else {
			Button mAudioBtn = (Button) mediaIconLayout.findViewById(R.id.ais_view_audio_icon_btn);
			if (mAudioItem != null) {
				// 音视频图标可点击
				mAudioBtn.setOnClickListener(mClickMediaIcon);
			} else {
				mAudioBtn.setVisibility(View.GONE);
			}

			videoBtn = (Button) mediaIconLayout.findViewById(R.id.ais_view_video_icon_btn);
			if (mVideoItem != null) {
				// 音视频图标可点击
				videoBtn.setOnClickListener(mClickMediaIcon);
			} else {
				videoBtn.setVisibility(View.GONE);
			}
		}

		// Ais内容滚动视图
		WebView webView = (WebView) contentLayout.findViewById(R.id.ais_webview);

		// 是否课件
		if (mAisDoc.isCourse()) {
			webView.setVisibility(View.GONE);
			//CourseWebView.GenHtml(ais_id, webView, aisDoc);
			CourseView.InitView(mInflater, contentLayout, mAisDoc);
		} else {
			AisWebView.Init(mAisDoc, webView, jsInterface);
		}

		// 没有文字和图片，有视频时焦点切换到视频
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
	 * 播放音视频
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
				Debug.Log("严重错误：mClickMediaIcon监听错误");
				break;
			}
		}
	};
	
	/**
	 * 播放音频
	 */
	private void playAudio() {
		if (mAudioItem == null) {
			Debug.Log("播放音频错误：mAudioItem空");
			return;
		}
		
		// 点击两次停止播放
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
			Debug.Log("播放异常：" + exp.getMessage());
	}
	
	/**
	 * 播放视频
	 */
	private void playVideo() {
		if (mVideoItem != null) {

			// 如果有音频播放，先停止音频
        	AisView.StopPlaying();

			// 播放保存好的视频文件
			String tmpFile = mVideoItem.fileName;
			String rmvbMimetype = "audio/x-pn-realaudio";
			String mimetype = "video/mp4";
			// TODO 测试rmvb视频

			Intent it = new Intent(Intent.ACTION_VIEW);  
	        it.setDataAndType(Uri.parse(tmpFile), mimetype);
	        mInflater.getContext().startActivity(it);
		}
	}
}
