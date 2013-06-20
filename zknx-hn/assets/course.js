
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
    var a,b,c,d,note,result;
    for (var i = 0; i < gQuestionCount; i++) {
        a = document.getElementById(getAnwserId(i, "A"));
        b = document.getElementById(getAnwserId(i, "B"));
        c = document.getElementById(getAnwserId(i, "C"));
        d = document.getElementById(getAnwserId(i, "D"));           

        a.enabled = true;
        a.checked = false;

        c.enabled = true;
        b.checked = false;

        c.enabled = true;
        c.checked = false;

        d.enabled = true;
        d.checked = false;

        note = document.getElementById(getNoteId(i));
        result = document.getElementById(getResultId(i));

        note.style.display = "none";
        result.style.display = "none";
    }
}

// 提交答案
function submitTest() {

    var a, b, c, d, note, result, crectIcon, increctIcon;

    crectIcon = document.getElementById("crectIcon");
    increctIcon = document.getElementById("increctIcon");

    for (var i = 0; i < gQuestionCount; ++i) {
        a = document.getElementById(getAnwserId(i, "A"));
        b = document.getElementById(getAnwserId(i, "B"));
        c = document.getElementById(getAnwserId(i, "C"));
        d = document.getElementById(getAnwserId(i, "D"));

        a.enabled = false;
        b.enabled = false;
        c.enabled = false;
        d.enabled = false;

        note = document.getElementById(getNoteId(i));
        note.style.display = "block";

        result = document.getElementById(getResultId(i));

        result.src = crectIcon.value;
        result.style.display = "block";
    }
    
    alert("您本地测试的得分是：" + 0);
}