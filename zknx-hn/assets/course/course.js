
var gQuestionCount;
var gCrectImageFile;
var gIncrectImageFile;

// 初始化 问题个数
function initTest(questionCount) {
    this.gQuestionCount = questionCount;
    this.gCrectImageFile = document.getElementById("crectIcon");
    this.gIncrectImageFile = document.getElementById("increctIcon");
}

// 获取答案id
function getAnwserId(i, anwser) {
    return "anwser" + i + "_" + anwser;
}

// 正确答案id
function getRightAnwserId(i) {
	return "rightAnwser" + i;
}

// 获取结果id
function getResultId(i) {
    return "result" + i;
}

// 获取解析id
function getNoteId(i) {
    return "note" + i;
}

// 获取本地得分id
function getCurrentResultId() {
    return "currentResult";
}

// 重做
function resetTest() {

    // 清除答案，隐藏结果，隐藏解析
    var a, b, c, d, note, result;
    
    // 隐藏得分
    document.getElementById(getCurrentResultId()).style.display = "none";
    
    for (var i = 0; i < gQuestionCount; i++) {
        a = document.getElementById(getAnwserId(i, "A"));
        b = document.getElementById(getAnwserId(i, "B"));
        c = document.getElementById(getAnwserId(i, "C"));
        d = document.getElementById(getAnwserId(i, "D"));           
        
        a.disabled = false;
        b.disabled = false;
        c.disabled = false;
        d.disabled = false;
        
        a.checked = false;
        b.checked = false;
        c.checked = false;
        d.checked = false;

        note = document.getElementById(getNoteId(i));
        result = document.getElementById(getResultId(i));

        note.style.display = "none";
        result.style.visibility = "hidden";
    }
}

// 字符串包含：包含返回true，不包含返回false
function contains(str, a) {

    for (var i = 0; i < str.length; i++) {
        if (str[i] == a) {
            return true;
        }
    }

    return false;
}

// 是否正确答案
function rightAnwser(anwser, rightAnwser) {

    // 首先比较长度
    if (rightAnwser.length != 0 && rightAnwser.length == anwser.length) {

        // 变为大写
        rightAnwser.toUpperCase();

        for (var i = 0; i < rightAnwser.length; i++) {
            // 答案中是否含有正确答案
            if (!contains(anwser, rightAnwser[i])) {
                return false;
            }
        }

        return true;
    }
}

// 检查结果：正确的话返回该题目分数，否则返回0分
function checkResult(i) {
    
    var a, b, c, d, note, result, token, anwser;
    
    a = document.getElementById(getAnwserId(i, "A"));
    b = document.getElementById(getAnwserId(i, "B"));
    c = document.getElementById(getAnwserId(i, "C"));
    d = document.getElementById(getAnwserId(i, "D"));
    
    anwser = "";
    
    if (a.checked)
        anwser += "A";
    if (b.checked)
        anwser += "B";
    if (c.checked)
        anwser += "C";
    if (d.checked)
        anwser += "D";

    a.disabled = true;
    b.disabled = true;
    c.disabled = true;
    d.disabled = true;
    
    result = document.getElementById(getResultId(i));
    result.style.visibility = "visible";
    
    note = document.getElementById(getNoteId(i));
    note.style.display = "block";
    
    // 10,AB
    // 分数10分，争取答案AB
    token = document.getElementById(getRightAnwserId(i)).innerHTML.split(",");

    if (rightAnwser(anwser, token[1])) {
        result.src = gCrectImageFile.innerHTML;
        return parseInt(token[0], 10);
    } else {
        result.src = gIncrectImageFile.innerHTML;
        return 0;
    }
}

// 提交答案
function submitTest() {

    var grade = 0, curResult;

    for (var i = 0; i < gQuestionCount; ++i) {
        grade += checkResult(i);       
    }

    curResult = document.getElementById(getCurrentResultId());
    curResult.innerHTML = "得分：" + grade + "分";

    // 得分为零显示红色
    if (grade == 0)
        curResult.style.color = "red";
    else
        curResult.style.color = "green";

    curResult.style.display = "block";

    alert("您本次测试的得分是：" + grade + "分");
}