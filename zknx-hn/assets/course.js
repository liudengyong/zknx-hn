
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

// 重做
function resetTest() {

    // 清除答案，隐藏结果，隐藏解析
    var a, b, c, d, note, result;
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

// 是否正确答案
function rightAnwser(anwser, rightAnwser) {

    var hit; // 用于检查是否有正确的答案，为真的话表示是正确答案之一
    for (var i = 0; i < anwser.length; i++) {
        // 检查争取答案中是否有该答案
        hit = false;
        for (var j = 0; j < rightAnwser.length; j++) {
            if (anwser[i] == rightAnwser[j]) {
                hit = true;
                break;
            }
        }

        if (!hit)
            return false;
    }

    return true;
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

    var grade = 0;

    for (var i = 0; i < gQuestionCount; ++i) {
        grade += checkResult(i);       
    }

    alert("您本次测试的得分是：" + grade + "分");
}