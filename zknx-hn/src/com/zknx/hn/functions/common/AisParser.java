package com.zknx.hn.functions.common;

import java.io.IOException;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AisParser {

	LayoutInflater mInflater;
	
	public AisParser(LayoutInflater inflater) {
		mInflater = inflater;
	}

	/**
	 * 获取ais视图
	 * @param ais_id
	 * @param context
	 * @return
	 */
	public static AisLayout GetAisLayout(int ais_id, LayoutInflater inflater) {
		
		LinearLayout aisLayout = (LinearLayout) inflater.inflate(R.layout.ais_view, null);
		
		// 音视频图标
		LinearLayout mediaIconLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_view_media_icon);
		
		// Ais内容滚动视图
		LinearLayout contentLayout = (LinearLayout) aisLayout.findViewById(R.id.ais_content_view);

		String title = new AisParser(inflater).parse(ais_id, contentLayout, mediaIconLayout);

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
	private String parse(int ais_id, LinearLayout root, LinearLayout mediaIconLayout) {
		// 获取解析后的ais文档
		AisDoc aisDoc = new AisDoc(ais_id);
		String title = aisDoc.getTitle();
		List<List<AisItem>> aisItemTree = aisDoc.getItemTree();
		
		if (title != null && aisItemTree != null) {

			AisItem audioItem = aisDoc.getAudioItem();
			AisItem videoItem = aisDoc.getVideoItem();

			if (audioItem != null) {
				mediaIconLayout.setVisibility(View.VISIBLE);
				mediaIconLayout.findViewById(R.id.ais_view_audio_icon).setVisibility(View.VISIBLE);
			}

			if (videoItem != null) {
				mediaIconLayout.setVisibility(View.VISIBLE);
				mediaIconLayout.findViewById(R.id.ais_view_video_icon).setVisibility(View.VISIBLE);
			}
			
			Context context = root.getContext();

			// 从解析出的ais文档数中生成视图
			for (List<AisItem> aisLine : aisItemTree) {
				for (AisItem aisItem : aisLine) {

					AisView item = getAisItemView(aisItem, context);

					// TODO AIS 排版优化？一行？
					if (item != null)
						root.addView(item.view);
				}
			}

			AisItem imageItems[] = aisDoc.getImageItems();

			if (imageItems != null && imageItems.length > 0) {
				
				LinearLayout imagePreview = (LinearLayout) mInflater.inflate(R.layout.ais_view_images_preview, null);

				AisItem imageItem = imageItems[0];
				if (imageItem.data != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(imageItem.data, 0, imageItem.data.length);
					((ImageView)imagePreview.findViewById(R.id.ais_view_image1_preview)).setImageBitmap(bitmap);
				}

				imageItem = imageItems[1];
				if (imageItems[1].data != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(imageItem.data, 0, imageItem.data.length);
					((ImageView)imagePreview.findViewById(R.id.ais_view_image2_preview)).setImageBitmap(bitmap);
				}

				imageItem = imageItems[2];
				if (imageItems[0].data != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(imageItem.data, 0, imageItem.data.length);
					((ImageView)imagePreview.findViewById(R.id.ais_view_image3_preview)).setImageBitmap(bitmap);
				}

				// 添加图片预览视图
				root.addView(imagePreview);
			}
			
			return title;
		}
		
		return null;
	}
	
	/**
	 * 通过AisItem获取item视图
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
			aisView = new AisMedia(context, aisItem);
			break;
		default:
			Debug.Log("严重错误：getAisItemView，" + aisItem.type);
			break;
		}
		
		return aisView;
	}
	
	/**
	 * Ais视图
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
	 * Ais文本视图
	 */
	
	class AisText extends AisView {
		
		AisText(Context context, AisItem item) {
			super(context, item);
			
			view = genText();
		}
		
		/**
		 * 生成文本视图
		 */
		private TextView genText() {

			TextView tv = new TextView(context);

			String text = EncodingUtils.getString(item.data, "GB2312");
			// 处理Windows换行
			tv.setText(text.replaceAll("\r", "\n"));
			tv.setGravity(Gravity.LEFT);

			tv.setLayoutParams(UIConst.GetLayoutParams(L_LAYOUT_TYPE.WRAP));

			return tv;
		}
	}
	
	/**
	 * Ais图像/视频/音频视图
	 * TODO 待调整
	 */
	
	class AisMedia extends AisView {
		
		private String mediaFile;
		private MediaPlayer player = null;
		
		AisMedia(Context context, AisItem item) {
			super(context, item);
			
			mediaFile = DataMan.DataFile(DataMan.FILE_NAME_TMP);
			
			view = genImage();
		}

		/**
		 * 生成图像视图/播放视频音频图标
		 */
		private ImageView genImage() {

			ImageView image = new ImageView(context);

			image.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			
			switch (item.type) {
			case IMAGE:
			{
				Bitmap bitmap = BitmapFactory.decodeByteArray(item.data, 0, item.data.length);
				image.setImageBitmap(bitmap);
			}
			break;
			case VIDEO:
			case AUDIO:
			{
				// TODO 播放音视频图标
				image.setImageResource((item.type == ItemType.VIDEO) ? R.drawable.ic_video: R.drawable.ic_audio);
				image.setOnClickListener(clickMediaIcon);
			}
			image.setOnClickListener(clickMediaIcon);
			break;
			default:
				Debug.Log("严重错误：媒体类型错误：" + item.type);
				return null;
			}

			// 重新计算视图大小
			image.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			
			image.setScaleType(ScaleType.CENTER_INSIDE);
			image.setLayoutParams(UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
			image.setPadding(2, 2, 2, 2); // 避免分不清边界
			
			return image;
		}
		
		/**
		 * 播放音视频
		 */
		private OnClickListener clickMediaIcon = new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				// 调用系统播放器播放视频
				if (item.type == ItemType.VIDEO) {
					Intent it = new Intent(Intent.ACTION_VIEW);  
	                it.setDataAndType(Uri.parse(mediaFile), "video/mp4");  
	                context.startActivity(it);
	                return;
				}

				if (player == null) {
					player = new MediaPlayer();
				}
				
				// 点击两次停止播放
				if (player.isPlaying()) {
					player.stop();
					return;
				}

				Exception exp = null;
				try {
					FileUtils.WriteFile(mediaFile, item.data);
					player.reset();
					player.setDataSource(mediaFile);
					player.prepare();
					player.start();
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
		};
	}
	
	/**
	 * Ais勾选框视图
	 */
	
	class AisCheckBox extends AisView {
		
		AisCheckBox(Context context, AisItem item) {
			super(context, item);
			
			view = genCheckbox();
		}
		
		/**
		 * 生成文本视图
		 * TODO AIS 生成AIS项视图  勾选
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
