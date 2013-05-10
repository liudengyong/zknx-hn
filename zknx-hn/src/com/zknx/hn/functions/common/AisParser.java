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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AisParser {

	/**
	 * ��ȡais��ͼ
	 * @param ais_id
	 * @param context
	 * @return
	 */
	public static AisLayout GetAisLayout(int ais_id, Context context) {

		// Ais������ͼ
		LinearLayout contentLayout = new LinearLayout(context);
		// ���ݹ�����ͼ
		ScrollView scrollView = new ScrollView(context);
		scrollView.addView(contentLayout, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));

		// Ais��ͼ
		LinearLayout layout = new LinearLayout(context);
		layout.addView(scrollView, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));

		String title = new AisParser().parse(ais_id, contentLayout);
		
		if (title == null) {
			Debug.Log("���ش���AIS parse����");
			return null;
		}
		
		return new AisLayout(title, layout);
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
	private String parse(int ais_id, LinearLayout root) {
		// ��ȡ�������ais�ĵ�
		AisDoc aisDoc = new AisDoc(ais_id);
		String title = aisDoc.getTitle();
		List<List<AisItem>> aisItemTree = aisDoc.getItemTree();
		
		if (title != null && aisItemTree != null) {

			Context context = root.getContext();

			root.setOrientation(LinearLayout.VERTICAL);
			root.setPadding(8, 8, 8, 8);

			// �ӽ�������ais�ĵ�����������ͼ
			for (List<AisItem> aisLine : aisItemTree) {
				for (AisItem aisItem : aisLine) {

					AisView item = getAisItemView(aisItem, context);

					// TODO AIS �Ű��Ż���һ�У�
					if (item != null)
						root.addView(item.view);
				}
			}
			
			return title;
		}
		
		return null;
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
			aisView = new AisMedia(context, aisItem);
			break;
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
	 * Aisͼ��/��Ƶ/��Ƶ��ͼ
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
		 * ����ͼ����ͼ/������Ƶ��Ƶͼ��
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
				// TODO ��������Ƶͼ��
				image.setImageResource((item.type == ItemType.VIDEO) ? R.drawable.ic_video: R.drawable.ic_audio);
				image.setOnClickListener(clickMediaIcon);
			}
			image.setOnClickListener(clickMediaIcon);
			break;
			default:
				Debug.Log("���ش���ý�����ʹ���" + item.type);
				return null;
			}

			// ���¼�����ͼ��С
			image.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			
			image.setScaleType(ScaleType.CENTER_INSIDE);
			image.setLayoutParams(UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
			image.setPadding(2, 2, 2, 2); // ����ֲ���߽�
			
			return image;
		}
		
		/**
		 * ��������Ƶ
		 */
		private OnClickListener clickMediaIcon = new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				// ����ϵͳ������������Ƶ
				if (item.type == ItemType.VIDEO) {
					Intent it = new Intent(Intent.ACTION_VIEW);  
	                it.setDataAndType(Uri.parse(mediaFile), "video/mp4");  
	                context.startActivity(it);
	                return;
				}

				if (player == null) {
					player = new MediaPlayer();
				}
				
				// �������ֹͣ����
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
					Debug.Log("�����쳣��" + exp.getMessage());
			}
		};
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
