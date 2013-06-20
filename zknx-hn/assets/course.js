
var gQuestionCount;
var gCrectImageFile;
var gIncrectImageFile;

// 初始化 问题个数，正确结果的图标，错误结果的图标
function initTest(questionCount, crectImageFile, increctImageFile) {
    this.gQuestionCount = questionCount;
    this.gCrectImageFile = crectImageFile;
    this.gIncrectImageFile = increctImageFile;
}

// 获取答案id
function getAnwserId(i, anwser) {
    return "anwser" + i + "_" + anwser;
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
    for (var i = 0; i < 5; i++) {
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

// 提交答案
function submitTest() {

    var a, b, c, d, note, result, crectIcon, increctIcon;

    crectIcon = document.getElementById("crectIcon");
    increctIcon = document.getElementById("increctIcon");

    for (var i = 0; i < 5; ++i) {
        a = document.getElementById(getAnwserId(i, "A"));
        b = document.getElementById(getAnwserId(i, "B"));
        c = document.getElementById(getAnwserId(i, "C"));
        d = document.getElementById(getAnwserId(i, "D"));

        a.disabled = true;
        b.disabled = true;
        c.disabled = true;
        d.disabled = true;

        note = document.getElementById(getNoteId(i));
        note.style.display = "block";

        result = document.getElementById(getResultId(i));

        result.src = crectIcon.innerHTML;
        result.style.visibility = "visible";
    }
    
    alert("您本地测试的得分是：" + increctIcon.innerHTML);
}