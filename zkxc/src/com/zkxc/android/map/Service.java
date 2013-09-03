package com.zkxc.android.map;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.zkxc.android.act.AppZkxc;
import com.zkxc.android.data.DataMan;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Service implements Setting {

	//private static String userid = "S27";

	private static File recordLocalDir;
	private static File recordServerDir;
	private static File contentLocalDir;
	private static File contentServerDir;
	private static File imageDir;
	private static File videoDir;
	private static File soundDir;

	/**
	 * 上传路径文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean uploadPathInfo(Context context, File file) {

		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******UPLOAD|||" + System.currentTimeMillis();
		String srcPath = file.getAbsolutePath();
		
		String userid = AppZkxc.mUserInfo.userId;
		
		try {
			URL url = new URL(APIURL_UPLOAD_GPS_INFO);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
			// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			// 参数userid
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"userid\""
					+ end);
			dos.writeBytes(end);
			dos.writeBytes(userid + end);

			// 参数addtime
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"addtime\""
					+ end);
			Date date = FILE_DATE_FORMAT.parse(file.getName().substring(0, 14));
			dos.writeBytes(end);
			dos.writeBytes(DISPLAY_DATE_FORMAT.format(date) + end);

			// 文件
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"data\"; filename=\""
					+ srcPath.substring(srcPath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();

			dos.writeBytes(end);
			// 结束
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder result = new StringBuilder();
			String line = null;
			while (null != (line = br.readLine())) {
				if (result.length() > 0) {
					result.append('\n');
				}
				result.append(line);
			}
			dos.close();
			is.close();
			Log.d("SERVICE", result.toString());
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("ERROR", e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 上传信息文件
	 * 
	 * @param lat
	 * @param lng
	 * @param date
	 * @param content
	 * @return
	 */
	public static boolean uploadContentInfo(File file) {

		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******UPLOAD|||" + System.currentTimeMillis();
		ContentFile contentFile = ContentFile.getContentFile(file);
		
		String userid = AppZkxc.mUserInfo.userId;
		try {
			URL url = new URL(APIURL_UPLOAD_GPS_INFO);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
			// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			// 参数userid
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"userid\""
					+ end);
			dos.writeBytes(end);
			dos.writeBytes(userid + end);

			// 参数addtime
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"addtime\""
					+ end);
			Date date = FILE_DATE_FORMAT.parse(file.getName().substring(0, 14));
			dos.writeBytes(end);
			dos.writeBytes(DISPLAY_DATE_FORMAT.format(date) + end);

			// 参数lat
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"lat\"" + end);
			String lat = Double.toString(contentFile.getLat());
			dos.writeBytes(end);
			dos.writeBytes(lat + end);

			// 参数lng
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"lng\"" + end);
			String lng = Double.toString(contentFile.getLng());
			dos.writeBytes(end);
			dos.writeBytes(lng + end);

			// 参数active_content
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"active_content\""
					+ end);
			dos.writeBytes(end);
			dos.write(contentFile.getContent().getBytes("UTF8"));
			dos.writeBytes(end);

			// 图片文件
			File imageFile = new File(getImageDir() + "/"
					+ FILE_DATE_FORMAT.format(contentFile.getAddTime())
					+ ".jpg");
			if (imageFile.exists()) {
				dos.writeBytes(twoHyphens + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; name=\"image_info\"; filename=\""
						+ imageFile.getAbsolutePath()
								.substring(
										imageFile.getAbsolutePath()
												.lastIndexOf("/") + 1)
						+ "\""
						+ end);
				dos.writeBytes(end);
				FileInputStream fis = new FileInputStream(imageFile);
				byte[] buffer = new byte[8192]; // 8k
				int count = 0;
				while ((count = fis.read(buffer)) != -1) {
					dos.write(buffer, 0, count);
				}
				fis.close();

				dos.writeBytes(end);
			}

			// 音频文件
			File[] soundList = getVoiceDir().listFiles(new MyFilenameFilter(FILE_DATE_FORMAT.format(contentFile.getAddTime())));
			if(soundList.length>0){
				File soundFile = soundList[0];
				dos.writeBytes(twoHyphens + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; name=\"voice_info\"; filename=\""
						+ soundFile.getAbsolutePath()
								.substring(
										soundFile.getAbsolutePath()
												.lastIndexOf("/") + 1)
						+ "\""
						+ end);
				dos.writeBytes(end);
				FileInputStream fis = new FileInputStream(soundFile);
				byte[] buffer = new byte[8192]; // 8k
				int count = 0;
				while ((count = fis.read(buffer)) != -1) {
					dos.write(buffer, 0, count);
				}
				fis.close();

				dos.writeBytes(end);
			}
			

			// 视频文件
			File[] videoList = getVideoDir().listFiles(new MyFilenameFilter(FILE_DATE_FORMAT.format(contentFile.getAddTime())));
			if(videoList.length>0){
				File videoFile = videoList[0];
				if(videoFile.length()<=0x300000/*3MB*/){
					dos.writeBytes(twoHyphens + boundary + end);
					dos.writeBytes("Content-Disposition: form-data; name=\"video_info\"; filename=\""
							+ videoFile.getAbsolutePath()
									.substring(
											videoFile.getAbsolutePath()
													.lastIndexOf("/") + 1)
							+ "\""
							+ end);
					dos.writeBytes(end);
					FileInputStream fis = new FileInputStream(videoFile);
					byte[] buffer = new byte[8192]; // 8k
					int count = 0;
					while ((count = fis.read(buffer)) != -1) {
						dos.write(buffer, 0, count);
					}
					fis.close();
	
					dos.writeBytes(end);
				}
			}

			// 结束
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder result = new StringBuilder();
			String line = null;
			while (null != (line = br.readLine())) {
				if (result.length() > 0) {
					result.append('\n');
				}
				result.append(line);
			}
			dos.close();
			is.close();
			Log.d("SERVICE", result.toString());
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("ERROR", e.getMessage(), e);
			return false;
		}

		// try {
		// HttpPost post = new HttpPost(APIURL_UPLOAD_GPS_INFO);
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// // params.add(new BasicNameValuePair("userid", userid));
		// // params.add(new BasicNameValuePair("lat",
		// Double.toString(contentFile.getLat())));
		// // params.add(new BasicNameValuePair("lng",
		// Double.toString(contentFile.getLng())));
		// // params.add(new BasicNameValuePair("addtime",
		// DISPLAY_DATE_FORMAT.format(contentFile.getAddTime())));
		// params.add(new BasicNameValuePair("active_content",
		// contentFile.getContent()));
		//
		// HttpEntity httpentity = new UrlEncodedFormEntity(params, "utf-8");
		// post.setEntity(httpentity);
		//
		// HttpClient client = new DefaultHttpClient();
		// HttpResponse response = client.execute(post);
		// String result = EntityUtils.toString(response.getEntity());
		// Log.d("SERVICE", result);
		// if(!result.equals("true")){
		// Log.d("Failed", contentFile.getContent());
		// Log.d("Failed", contentFile.getContent().length()+"??");
		// }
		// return true;
		// } catch (Exception e) {
		// e.printStackTrace();
		// Log.e("ERROR", e.getMessage(), e);
		// return false;
		// }

	}
	
	public static boolean UploadRecordFile(Context context, File file)
	{
		if (file.length() == 0) {
			file.delete();
		} else {
			if (Service.uploadPathInfo(context, file)){
				return file.renameTo(new File(getRecordServerDir().getAbsolutePath()+ "/"+ file.getName()));
			}
		}
		
		return false;
	}
	
	public static boolean UploadContentFile(File file)
	{
		if (file.length() == 0) {
			file.delete();
		} else {
			if (Service.uploadContentInfo(file)) {
				file.renameTo(new File(getContentServerDir().getAbsolutePath()+ "/"+ file.getName()));
			}
		}
		
		return false;
	}

	/**
	 * 上传记录
	 */
	public static void uploadData(final Context context) {
		final List<SyncTask> taskList = new ArrayList<SyncTask>();
		// 上传路径列表
		File[] recordFiles = getRecordLocalDir().listFiles();
		for (File file : recordFiles) {
			SyncTask syncTask = new SyncTask();
			syncTask.setType(SyncTask.TYPE_UPLOAD_PATH);
			syncTask.setItem(file);
			taskList.add(syncTask);
		}
		// 上传文字信息列表
		File[] contentFiles = getContentLocalDir().listFiles();
		for (File file : contentFiles) {
			SyncTask syncTask = new SyncTask();
			syncTask.setType(SyncTask.TYPE_UPLOAD_CONTENT);
			syncTask.setItem(file);
			taskList.add(syncTask);
		}
		
		String userid = AppZkxc.mUserInfo.userId;
		// 下载文字列表
		try {
			HttpGet request = new HttpGet(APIURL_GET_GPS_INFO + "?userid=" + userid);
			HttpResponse httpResponse = new DefaultHttpClient().execute(request);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				String[] lines = strResult.split("\n");
				SyncTask lastSyncTask = null;
				for (String string : lines) {
					String[] values = string.trim().split(",");
					if(values.length>=9){
						//values[0] is content_id
						Date addTime = DISPLAY_DATE_FORMAT.parse(values[1]);
						File existsServerFile = new File(getContentServerDir().getAbsolutePath()+"/"+FILE_DATE_FORMAT.format(addTime)+".txt");
						File existsLocalFile = new File(getContentLocalDir().getAbsolutePath()+"/"+FILE_DATE_FORMAT.format(addTime)+".txt");
						if(existsServerFile.exists()||existsLocalFile.exists()){
							//如果文件存在则不下载
							lastSyncTask = null;//这里设置为null，是为了防止本条数据文字中如果有换行的话会对上一条数据有影响
						}
						else{
							//values[2] is userid
							double lat = Double.parseDouble(values[3]);
							double lng = Double.parseDouble(values[4]);
							if(values[5].length()>0){//照片
								SyncTask syncTask = new SyncTask();
								syncTask.setType(SyncTask.TYPE_DOWNLOAD_IMAGE);
								syncTask.setItem(values[5]);
								taskList.add(syncTask);
							}
							if(values[6].length()>0){//录音
								SyncTask syncTask = new SyncTask();
								syncTask.setType(SyncTask.TYPE_DOWNLOAD_SOUND);
								syncTask.setItem(values[6]);
								taskList.add(syncTask);
							}
							if(values[7].length()>0){//视频
								SyncTask syncTask = new SyncTask();
								syncTask.setType(SyncTask.TYPE_DOWNLOAD_VIDEO);
								syncTask.setItem(values[7]);
								taskList.add(syncTask);
							}
							String content = values[8];//文字
							if(values.length>9){
								for(int i=9;i<values.length;i++){
									content+=","+values[i];
								}
							}
							ContentFile contentFile = ContentFile.createContentFile(lat, lng, addTime, content, true);
							lastSyncTask = new SyncTask();
							lastSyncTask.setItem(contentFile);
							lastSyncTask.setType(SyncTask.TYPE_DOWNLOAD_CONTENT);
							taskList.add(lastSyncTask);
						}
					}
					else{
						if(lastSyncTask!=null&&string.length()>0){
							ContentFile contentFile = (ContentFile)lastSyncTask.getItem();
							contentFile.setContent(contentFile.getContent()+"\n"+string);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 下载轨迹列表
		try {
			HttpGet request = new HttpGet(APIURL_GET_GPS_FILE_LIST + "?userid=" + userid);
			HttpResponse httpResponse = new DefaultHttpClient().execute(request);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				String[] lines = strResult.split("\n");
				for (String string : lines) {
					String[] values = string.trim().split(",");
					if(values.length==4){
						//values[0] is content_id
						String url = values[3];
						String filename = url.substring(url.lastIndexOf('/')+1);
						File existsFile = new File(getRecordServerDir().getAbsolutePath()+"/"+filename);
						if(!existsFile.exists()){//如果文件存在则不下载
							SyncTask syncTask = new SyncTask();
							syncTask.setType(SyncTask.TYPE_DOWNLOAD_PATH);
							syncTask.setItem(values[3]);
							taskList.add(syncTask);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (taskList.size() != 0) {
			final ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMax(taskList.size());
			progressDialog.setTitle("同步数据...");
			progressDialog.setProgress(0);
			progressDialog.show();
			new Thread() {
				public void run() {
					for (SyncTask syncTask : taskList) {
						File file;
						String url;
						String filename;
						switch (syncTask.getType()) {
						case SyncTask.TYPE_UPLOAD_CONTENT:// 上传记录
							file = (File) syncTask.getItem();
							Log.d("SYNCDATA","上传文字记录："+file.getName());
							UploadContentFile(file);
							break;
						case SyncTask.TYPE_UPLOAD_PATH:// 上传轨迹
							file = (File) syncTask.getItem();
							Log.d("SYNCDATA","上传轨迹："+file.getName());
							UploadRecordFile(context, file);
							break;
						case SyncTask.TYPE_DOWNLOAD_PATH:
							url = (String) syncTask.getItem();
							filename = url.substring(url.lastIndexOf('/')+1);
							Log.d("SYNCDATA","下载轨迹："+filename);
							downloadFile(url, new File(getRecordServerDir().getAbsolutePath()+"/"+filename));
							break;
						case SyncTask.TYPE_DOWNLOAD_SOUND:
							url = (String) syncTask.getItem();
							filename = url.substring(url.lastIndexOf('/')+1);
							downloadFile(url, new File(getVoiceDir().getAbsolutePath()+"/"+filename));
							Log.d("SYNCDATA","下载录音："+filename);
							break;
						case SyncTask.TYPE_DOWNLOAD_IMAGE:
							url = (String) syncTask.getItem();
							filename = url.substring(url.lastIndexOf('/')+1);
							downloadFile(url, new File(getImageDir().getAbsolutePath()+"/"+filename));
							Log.d("SYNCDATA","下载照片："+filename);
							break;
						case SyncTask.TYPE_DOWNLOAD_VIDEO:
							url = (String) syncTask.getItem();
							filename = url.substring(url.lastIndexOf('/')+1);
							downloadFile(url, new File(getVideoDir().getAbsolutePath()+"/"+filename));
							Log.d("SYNCDATA","下载视频："+filename);
							break;
						case SyncTask.TYPE_DOWNLOAD_CONTENT:
							ContentFile contentFile = (ContentFile) syncTask.getItem();
							Log.d("SYNCDATA","保存文字记录："+contentFile.toString());
							contentFile.save();
							break;
						}
						progressDialog.setProgress(progressDialog.getProgress()+1);
					}
					progressDialog.cancel();
				};
			}.start();
		} else {
			Toast.makeText(context, "没有需要同步的数据", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 轨迹目录（未上传）
	 * 
	 * @return
	 */
	public static File getRecordLocalDir() {
		if (recordLocalDir == null) {
			recordLocalDir = new File(DataMan.GetDataForlder() + RECORD_LOCAL_DIR);
			if (!recordLocalDir.exists()) {
				recordLocalDir.mkdirs();
			}
		}
		return recordLocalDir;
	}

	/**
	 * 轨迹目录（已上传）
	 * 
	 * @return
	 */
	public static File getRecordServerDir() {
		if (recordServerDir == null) {
			recordServerDir = new File(DataMan.GetDataForlder() + RECORD_SERVER_DIR);
			if (!recordServerDir.exists()) {
				recordServerDir.mkdirs();
			}
		}
		return recordServerDir;
	}

	/**
	 * 笔记目录（未上传）
	 * 
	 * @return
	 */
	public static File getContentLocalDir() {
		if (contentLocalDir == null) {
			contentLocalDir = new File(DataMan.GetDataForlder() + CONTENT_LOCAL_DIR);
			if (!contentLocalDir.exists()) {
				contentLocalDir.mkdirs();
			}
		}
		return contentLocalDir;
	}

	/**
	 * 笔记目录（已上传）
	 * 
	 * @return
	 */
	public static File getContentServerDir() {
		if (contentServerDir == null) {
			contentServerDir = new File(DataMan.GetDataForlder() + CONTENT_SERVER_DIR);
			if (!contentServerDir.exists()) {
				contentServerDir.mkdirs();
			}
		}
		return contentServerDir;
	}

	/**
	 * 图片目录
	 * 
	 * @return
	 */
	public static File getImageDir() {
		if (imageDir == null) {
			imageDir = new File(DataMan.GetDataForlder() + IMAGE_DIR);
			if (!imageDir.exists()) {
				imageDir.mkdirs();
			}
		}
		return imageDir;
	}

	public static File getVideoDir() {
		if (videoDir == null) {
			videoDir = new File(DataMan.GetDataForlder() + VIDEO_DIR);
			if (!videoDir.exists()) {
				videoDir.mkdirs();
			}
		}
		return videoDir;
	}

	public static File getVoiceDir() {
		if (soundDir == null) {
			soundDir = new File(DataMan.GetDataForlder() + SOUND_DIR);
			if (!soundDir.exists()) {
				soundDir.mkdirs();
			}
		}
		return soundDir;
	}

	/**
	 * 获取轨迹文件列表
	 * 
	 * @return
	 */
	public static List<RecordFile> getRecordList(final String prefix) {
		List<RecordFile> list = new ArrayList<RecordFile>();
		List<File> fileList = new ArrayList<File>(Arrays.asList(getRecordServerDir().listFiles()));
		fileList.addAll(Arrays.asList(getRecordLocalDir().listFiles()));
		
		// 获取文字内容列表
		fileList.addAll(Arrays.asList(getContentLocalDir().listFiles()));
		fileList.addAll(Arrays.asList(getContentServerDir().listFiles()));
		
		File[] sortFiles = fileList.toArray(new File[0]);
		Arrays.sort(sortFiles, new Comparator<File>() {
			public int compare(File lhs, File rhs) {
				if(prefix!=null){
					return lhs.getName().compareTo(rhs.getName());
				}
				else{
					return -lhs.getName().compareTo(rhs.getName());
				}
			}
		});
		Date beginDate  = null;
		if(prefix!=null){
			try {
				beginDate = new SimpleDateFormat("yyyyMMdd").parse(prefix);
			} catch (ParseException e) {
				Log.e("ERROR", e.getMessage(),e.getCause());
				e.printStackTrace();
			}
		}
		for (final File file : sortFiles) {
			RecordFile recordFile = new RecordFile(file);
			if(beginDate!=null&&recordFile.getDate().before(beginDate)){
				continue;
			}
			else{
				list.add(recordFile);
			}
		}
		return list;
	}

	/**
	 * 获取标记文件列表
	 * 
	 * @return
	 */
	public static List<ContentFile> getContentList() {
		List<ContentFile> list = new ArrayList<ContentFile>();
		List<File> fileList = new ArrayList<File>(Arrays.asList(Service
				.getContentLocalDir().listFiles()));
		fileList.addAll(Arrays
				.asList(Service.getContentServerDir().listFiles()));
		for (File file : fileList) {
			//Debug.Log2("Filename = " + ContentFile.getContentFile(file).getFile().getName());
			list.add(ContentFile.getContentFile(file));
		}
		return list;
	}
	
	/**
	 * 下载文件到指定位置
	 * @param urlStr
	 * @param desc
	 * @return
	 */
    public static File downloadFile(String urlStr, File desc){  
        try {  
        	URL url = new URL(urlStr);  
        	HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();  
        	InputStream inputStream = urlConn.getInputStream();  
        	FileOutputStream fos = new FileOutputStream(desc);
        	byte[] buffer = new byte[8192];
        	int size = -1;
        	while((size = inputStream.read(buffer))!=-1){
        		fos.write(buffer,0,size);
        	}
        	fos.flush();
        	fos.close();
        	inputStream.close();
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
        return desc;  
    } 
}
