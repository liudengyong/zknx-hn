
var gQuestionCount = 5;
var gQuestionArray;

// 初始化
function init(aisId, questionCount, questionArray) {
    this.gAisId = aisId;
    this.gQuestionCount = questionCount;
    this.gQuestionArray = unescape(questionArray);

    // 生成问题
    initQuestion();
}

// 初始化问题
function initQuestion() {
    alert(gQuestionArray);
}

function getAnwserId(i, anwser) {
    return "anwser" + i + "_" + anwser;
}

function getResultId(i, anwser) {
    return "anwser" + i + "_" + anwser;
}

function getNoteId(i) {
    return "note" + i;
}

// 重做
function resetTest() {

    // 清除答案，隐藏结果，隐藏解析
    var a,b,c,d,note,result;
    for (var i = 0; i < this.gQuestionCount; i++) {
        a = document.getElementById(getAnwserId(i, "A"));
        b = document.getElementById(getAnwserId(i, "B"));
        c = document.getElementById(getAnwserId(i, "C"));
        d = document.getElementById(getAnwserId(i, "D"));           

        a.checked = false;
        b.checked = false;
        c.checked = false;
        d.checked = false;

        note = document.getElementById(getNoteId(i));
        //result = document.getElementById('result' + i);

        note.style.display="none";
        //result.disply = false;
    }
}

// 提交答案
function submitTest() {

/*
    var grade = checkAnwser(gQuestionCount);
    showResult();
    showNote();
    */
    
    var a,b,c,d,note,result;
    for (var i = 0; i < gQuestionCount; ++i) {
        a = document.getElementById(getAnwserId(i, "A"));
        b = document.getElementById(getAnwserId(i, "B"));
        c = document.getElementById(getAnwserId(i, "C"));
        d = document.getElementById(getAnwserId(i, "D"));

        a.checked = false;
        b.checked = false;
        c.checked = false;
        d.checked = false;

        note = document.getElementById(getNoteId(i));

        note.style.display="block";
    }
    
    alert("您本地测试的得分是：" + 0);
}