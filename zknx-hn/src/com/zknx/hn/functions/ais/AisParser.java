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
	
	// 用于播放音频
	private AisItem mAudioItem;
	private AisItem mVideoItem;
	
	MediaPlayer mPlayer;

	public AisParser(LayoutInflater inflater) {
		mInflater = inflater;
		
		mPlayer = new MediaPlayer();
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
	public AisLayout GetAisLayout(String date, String aisFileName, LayoutInflater inflater, Object jsInterface) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);

		// 音视频图标
		LinearLayout mediaIconLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_view_media_icon);
		
		// Ais内容滚动视图
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);

		AisHeader header = parseAis(inflater.getContext(), date, aisFileName, contentLayout, jsInterface, mediaIconLayout);

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
	 * 如果成功，返回标题，并添加视图到root，否则返回null
	 * @param ais_id
	 * @param root
	 * @return
	 */
	private AisHeader parseAis(Context context, String date, String aisFileName, LinearLayout contentLayout, Object jsInterface, LinearLayout mediaIconLayout) {
		// 获取解析后的ais文档
		AisDoc aisDoc = new AisDoc(context, aisFileName, false, date);
		
		// 初始化音视频图标监听
		mAudioItem = aisDoc.getAudioItem();
		mVideoItem = aisDoc.getVideoItem();
		
		initMediaImage(mediaIconLayout, R.id.ais_view_audio_icon, mAudioItem);
		initMediaImage(mediaIconLayout, R.id.ais_view_video_icon, mVideoItem);

		// Ais内容滚动视图
		WebView webView = (WebView) contentLayout.findViewById(R.id.ais_webview);

		// 是否课件
		if (aisDoc.isCourse()) {
			webView.setVisibility(View.GONE);
			//CourseWebView.GenHtml(ais_id, webView, aisDoc);
			CourseView.InitView(mInflater, contentLayout, aisDoc);
		} else {
			AisWebView.Init(aisDoc, webView, jsInterface);
		}
		
		webView.setBackgroundColor(0); // 设置透明

		return aisDoc.getHeader();
	}
	
	
	/**
	 * 初始化音视频监听事件
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
	 * 播放音视频
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
			String tmpFile = DataMan.DataFile("tmp.mp4");
			FileUtils.WriteFile(tmpFile, mVideoItem.data);
			Intent it = new Intent(Intent.ACTION_VIEW);  
	        it.setDataAndType(Uri.parse(tmpFile), "video/mp4");  
	        mInflater.getContext().startActivity(it);
		}
	}
}
