package com.zknx.hn.functions.ais;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebView;

import com.zknx.hn.common.Debug;
import com.zknx.hn.common.ImageUtils;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;

public class Course {
	
	private final static String URL_FILE_CRECT_RESULT = "file:///android_asset/icon/crect.png";
	private final static String URL_FILE_INCRECT_RESULT = "file:///android_asset/icon/increct.png";
	
	/**
	 * ��ʼ���μ�����
	 * @param webView
	 */
	public static void GenHtml(String aisId, final WebView webView, AisDoc aisDoc) {
		
		int total = 0;
		int count = aisDoc.getQuestionCount();
		String questionTags = "";
		for (int i = 0; i < count; ++i) {
			questionTags += GenQuestionTags(aisDoc, aisId, i);
			total += aisDoc.getQuestionGrade(i);
		}

		questionTags = "<ol class=questionOl>" + questionTags + "</ol>";

		String jsInitMethod = "initTest(" + count+ ")";
		String cssLink = "<head><link href=\"file:///android_asset/course/course.css\" rel=stylesheet type=\"text/css\">";
		String jsScript = "<script type=\"text/javascript\" src=\"file:///android_asset/course/course.js\"></script></head>";
		String currentResult = "<div id=" + GetCurResultTagId() + " align=\"right\" style=\"display:none;font-size:18px;color:green;\"></div>";
		String totalPoints = "<div align=\"right\" style=\"margin-top:4px;font-size:18px;color:white;\">�ܷ֣�" + total + "��</div>";
		String aisHiddenInfo = "<div id=crectIcon style=\"display:none;\">" + URL_FILE_CRECT_RESULT + "</div>" +
				"<div id=increctIcon style=\"display:none;\"/>" + URL_FILE_INCRECT_RESULT + "</div>";

		String actionPair = "<table class=actionTable>" + 
								"<tr><th>" +
									"<input class=actionButton type=button value=\"����\" onClick=\"resetTest()\"/>" +
								"</th><th>" +
									"<input class=actionButton type=button value=\"����\" onClick=\"submitTest()\"/>" +
								"</th></tr>" +
							"</table>";

		String htmlString = cssLink + jsScript +
				"<body onload=\""+ jsInitMethod + "\">" +
				currentResult + totalPoints + aisHiddenInfo + questionTags + actionPair +
				"</body>";
		
		// ���Խ���
		//htmlString = "<input style=\"height:50px;\" id=input1 type=text /><br><div style=\"color:white;font-size:30px;height:50px;width:100px;\" id=button1 onClick=\"alert(123)\"/>1234</div><br><input style=\"height:50px;\" id=input2 type=text />";

		webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
		
		// ����webview���Ի�ý���
		webView.setFocusable(true);
		webView.requestFocus();
		
		webView.setOnKeyListener(new OnKeyListener() {         
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	        	// ��������ʱ����
	        	if (event.getAction() == KeyEvent.ACTION_DOWN) {
	        		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
	    	                //requestFocus() on parent view
	    	            	//Debug.Log("WebView OnKeyListener direct ��" + keyCode);
	    	            	 //FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, and FOCUS_RIGHT
	    	            }
	    	            
	    	            if (keyCode == KeyEvent.KEYCODE_TAB) {
	    	            	Debug.Log("webview TAB ����");
	    	            	
	    	            	/*
	    	            	Rect rect = new Rect();
	    	                if (webView.requestFocus(WebView.FOCUS_RIGHT, rect) ||
	    	                	webView.requestFocus(WebView.FOCUS_DOWN, rect)) {
	    	                	Debug.Log("webview TAB ���� ��Ч");
	    	                	return true;
	    	                }
	    	                */
	    	            }
	    	            
	    	            Debug.Log("WebView OnKeyListener ��" + keyCode);
	        	}
	            
	            return false;
	        }
	    });

		/* ��������ڲ�����
		try {
            Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);  
            m.invoke(webView, true);
        } catch (Throwable ignored) {
        }
        */
	}
	
	/**
	 * ����Ais�Ծ���Ŀ
	 * @param i
	 * @return
	 */
	private static String GenQuestionTags(AisDoc aisDoc, String aisId, int i) {
		// ����  + ͼƬ + CheckBox
		
		String imageFilePathName = DataMan.DataFile(aisId + "_question" + i + ".bmp");
		String imageAlt = "ͼƬ�Ҳ�����" + imageFilePathName;
		
		// ����ͼƬ
		if (!FileUtils.IsFileExist(imageFilePathName))
			ImageUtils.SaveBitmap(aisDoc.getQuestionBitmapData(i), imageFilePathName);

		// ����Ŀ֮��ļ��
		String tagQuestionBitmap = "<li><img src=\"file://" + imageFilePathName + "\"" + 
				" alt=\"" + imageAlt + "\"" +
				" style=\"vertical-align:text-top;\"" +
				"</li>";

		String anwserId = "";
		String tagCheckbox = "";
		char[] anwsers = {'A', 'B', 'C', 'D'};
		String tagAnswer = "����(" + aisDoc.getQuestionGrade(i) + "��)��";
		for (char anwser : anwsers) {
			anwserId = GetAnswerTagId(i, anwser);
			tagCheckbox = ("<input class=hiddenCheckbox type=checkbox id=" + anwserId + ">"); 
			tagAnswer += (anwser + tagCheckbox + "<div class=divCheckbox onClick=\"divCheckbox('" + anwserId + "', this);\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>");
		}
		
		String rightAnwser = "";
		byte[] rightAnwserBytes = aisDoc.getQuestionAnswer(i);
		if (rightAnwserBytes != null) {
			for (byte a : rightAnwserBytes)
				// �𰸳���һ�����������ȫ����ȷ�����0
				if (a != 0)
					rightAnwser += (char)a;
		}

		String tagRightAnwser = "<div id=" + GetRightAnwserTagId(i) + " style=\"display:none;\">" + aisDoc.getQuestionGrade(i) + DataMan.COMMON_TOKEN + rightAnwser + "</div>";
		String result = "<img id=" + GetResultTagId(i) + " style=\"visibility:hidden;vertical-align:text-bottom;\"/>";
		tagAnswer += (result + tagRightAnwser);

		// ���غ���ʾ����
		String noteTagId = GetNoteTagId(i);
		String tagNote = "<br><div id=" + noteTagId + " style=\"display:none;\">������" + aisDoc.getQuestionNote(i) + "<div/><br>";

		final String DIV = "<div/>";
		return tagQuestionBitmap + DIV + tagAnswer + DIV + tagNote;
	}
	
	/**
	 * ��ȡ��Tag��id
	 * @param i
	 * @return
	 */
	private static String GetAnswerTagId(int i, char answer) {
		return "anwser" + i + "_" + answer;
	}
	
	/**
	 * ��ȡ����Tag��id
	 * @param i
	 * @return
	 */
	private static String GetNoteTagId(int i) {
		return "note" + i;
	}
	
	/**
	 * ��ȡ���Tag��id
	 * @param i
	 * @return
	 */
	private static String GetResultTagId(int i) {
		return "result" + i;
	}
	
	/**
	 * ��ȡ��ȷ��Tag��id
	 * @param i
	 * @return
	 */
	private static String GetRightAnwserTagId(int i) {
		return "rightAnwser" + i;
	}
	
	/**
	 * ��ȡ�÷�Tag��id
	 * @param i
	 * @return
	 */
	private static String GetCurResultTagId() {
		return "currentResult";
	}

}
