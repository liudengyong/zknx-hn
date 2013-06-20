
var gAisId;
var gQuestionCount;
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

// 重做
function resetTest() {

    init();

    // 清除答案，隐藏结果，隐藏解析
    var a,b,c,d,note,result;
    for (var i = 0; i < gQuestionCount; ++i) {
        a = document.getElementById(gAisId + '_anwser' + i + '_A');
        b = document.getElementById(gAisId + '_anwser' + i + '_B');
        c = document.getElementById(gAisId + '_anwser' + i + '_C');
        d = document.getElementById(gAisId + '_anwser' + i + '_D');           

        a.checked = false;
        b.checked = false;
        c.checked = false;
        d.checked = false;

        note = document.getElementById(gAisId + '_note' + i);
        result = document.getElementById(gAisId + '_result' + i);

        note.disply = false;
        result.disply = false;
    }
}

// 提交答案
function submitTest() {
    
    init();

    var grade = checkAnwser(gQuestionCount);
    showResult();
    showNote();
    
    var a,b,c,d,note,result;
    for (var i = 0; i < gQuestionCount; ++i) {
        a = document.getElementById(gAisId + '_anwser' + i + '_A');
        b = document.getElementById(gAisId + '_anwser' + i + '_B');
        c = document.getElementById(gAisId + '_anwser' + i + '_C');
        d = document.getElementById(gAisId + '_anwser' + i + '_D'); 
    }
    
    alert("您本地测试的得分是：" + grade);
}