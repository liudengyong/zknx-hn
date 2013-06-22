package com.zknx.hn.functions.ais;

import android.webkit.WebView;

import com.zknx.hn.common.ImageUtils;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.FileUtils;

public class Course {
	
	private final static String URL_FILE_CRECT_RESULT = "file:///android_asset/icon/crect.png";
	private final static String URL_FILE_INCRECT_RESULT = "file:///android_asset/icon/increct.png";
	
	/**
	 * 初始化课件试题
	 * @param webView
	 */
	public static void GenHtml(String aisId, WebView webView, AisDoc aisDoc) {
		
		int total = 0;
		int count = aisDoc.getQuestionCount();
		String questionTags = "";
		for (int i = 0; i < count; ++i) {
			questionTags += GenQuestionTags(aisDoc, aisId, i);
			total += aisDoc.getQuestionGrade(i);
		}

		questionTags = "<ol style=\"font-size:18px;color:white;\">" + questionTags + "</ol>";

		String jsInitMethod = "initTest(" + count+ ")";
		String cssLink = "<head><link href=\"file:///android_asset/course/course.css\" rel=stylesheet type=\"text/css\">";
		String jsScript = "<script type=\"text/javascript\" src=\"file:///android_asset/course/course.js\"></script></head>";
		String currentResult = "<div id=" + GetCurResultTagId() + " align=\"right\" style=\"display:none;font-size:18px;color:green;\"></div>";
		String totalPoints = "<div align=\"right\" style=\"margin-top:4px;font-size:18px;color:white;\">总分：" + total + "分</div>";
		String aisHiddenInfo = "<div id=crectIcon style=\"display:none;\">" + URL_FILE_CRECT_RESULT + "</div>" +
				"<div id=increctIcon style=\"display:none;\"/>" + URL_FILE_INCRECT_RESULT + "</div>";

		String actionPair = "<table class=actionTable>" + 
								"<tr><th>" +
									"<input class=actionButton type=button value=\"重做\" onClick=\"resetTest()\"/>" +
								"</th><th>" +
									"<input class=actionButton type=button value=\"交卷\" onClick=\"submitTest()\"/>" +
								"</th></tr>" +
							"</table>";

		String htmlString = cssLink + jsScript +
				"<body onload=\""+ jsInitMethod + "\">" +
				currentResult + totalPoints + aisHiddenInfo + questionTags + actionPair +
				"</body>";

		webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null);
	}
	
	/**
	 * 生成Ais试卷题目
	 * @param i
	 * @return
	 */
	private static String GenQuestionTags(AisDoc aisDoc, String aisId, int i) {
		// 标题  + 图片 + CheckBox
		
		String imageFilePathName = DataMan.DataFile(aisId + "_question" + i + ".bmp");
		String imageAlt = "图片找不到：" + imageFilePathName;
		
		// 保存图片
		if (!FileUtils.IsFileExist(imageFilePathName))
			ImageUtils.SaveBitmap(aisDoc.getQuestionBitmapData(i), imageFilePathName);

		// 各题目之间的间隔
		String tagQuestionBitmap = "<li><img src=\"file://" + imageFilePathName + "\"" + 
				" alt=\"" + imageAlt + "\"" +
				" style=\"vertical-align:text-top;\"" +
				"</li>";

		char[] anwsers = {'A', 'B', 'C', 'D'};
		String tagAnswer = "答题(" + aisDoc.getQuestionGrade(i) + "分)：";
		for (char anwser : anwsers) {
			tagAnswer += (anwser + "<input class=checkboxOff type=checkbox id=" + GetAnswerTagId(i, anwser) + " value=" + anwser + ">"); 
			//tagAnswer += (anwser + "<div style=\"background:file:///android_asset/icon/checkbox_none.png\" id=" + GetAnswerTagId(i, anwser) + ">");
		}
		
		String rightAnwser = "";
		byte[] rightAnwserBytes = aisDoc.getQuestionAnswer(i);
		if (rightAnwserBytes != null) {
			for (byte a : rightAnwserBytes)
				// 答案长度一定，如果不是全部正确则会余0
				if (a != 0)
					rightAnwser += (char)a;
		}

		String tagRightAnwser = "<div id=" + GetRightAnwserTagId(i) + " style=\"display:none;\">" + aisDoc.getQuestionGrade(i) + DataMan.COMMON_TOKEN + rightAnwser + "</div>";
		String result = "<img id=" + GetResultTagId(i) + " style=\"visibility:hidden;vertical-align:text-bottom;\"/>";
		tagAnswer += (result + tagRightAnwser);

		// 隐藏和显示解析
		String noteTagId = GetNoteTagId(i);
		String tagNote = "<br><div id=" + noteTagId + " style=\"display:none;\">解析：" + aisDoc.getQuestionNote(i) + "<div/><br>";

		final String DIV = "<div/>";
		return tagQuestionBitmap + DIV + tagAnswer + DIV + tagNote;
	}
	
	/**
	 * 获取答案Tag的id
	 * @param i
	 * @return
	 */
	private static String GetAnswerTagId(int i, char answer) {
		return "anwser" + i + "_" + answer;
	}
	
	/**
	 * 获取解析Tag的id
	 * @param i
	 * @return
	 */
	private static String GetNoteTagId(int i) {
		return "note" + i;
	}
	
	/**
	 * 获取结果Tag的id
	 * @param i
	 * @return
	 */
	private static String GetResultTagId(int i) {
		return "result" + i;
	}
	
	/**
	 * 获取正确答案Tag的id
	 * @param i
	 * @return
	 */
	private static String GetRightAnwserTagId(int i) {
		return "rightAnwser" + i;
	}
	
	/**
	 * 获取得分Tag的id
	 * @param i
	 * @return
	 */
	private static String GetCurResultTagId() {
		return "currentResult";
	}

}
