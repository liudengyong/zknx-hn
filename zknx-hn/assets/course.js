
var gAisId;
var gQuestionCount;
var gQuestionArray;

// ��ʼ��
function init(aisId, questionCount, questionArray) {
    this.gAisId = aisId;
    this.gQuestionCount = questionCount;
    this.gQuestionArray = unescape(questionArray);

    // ��������
    initQuestion();
}

// ��ʼ������
function initQuestion() {
    alert(gQuestionArray);
}

// ����
function resetTest() {

    init();

    // ����𰸣����ؽ�������ؽ���
    var a,b,c,d,note,result;
    for (var i = 0; i < this.gQuestionCount; i++) {
        alert(gQuestionCount);
        a = document.getElementById(this.gAisId + '_anwser' + i + '_A');
        b = document.getElementById(this.gAisId + '_anwser' + i + '_B');
        c = document.getElementById(this.gAisId + '_anwser' + i + '_C');
        d = document.getElementById(this.gAisId + '_anwser' + i + '_D');           

        a.checked = false;
        b.checked = false;
        c.checked = false;
        d.checked = false;

        note = document.getElementById(this.gAisId + '_note' + i);
        result = document.getElementById(this.gAisId + '_result' + i);

        note.disply = false;
        result.disply = false;
    }
}

// �ύ��
function submitTest() {
    
    init();

/*
    var grade = checkAnwser(gQuestionCount);
    showResult();
    showNote();
    */
    
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
    }
    
    alert("�����ز��Եĵ÷��ǣ�" + 0);
}