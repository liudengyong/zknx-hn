
var aisId;
var questionCount;

// ��ʼ��
function init() {
    this.aisId = document.getElementById('aisId');
    this.count = document.getElementById('questionCount');
}

// ����
function resetTest() {
    
    init();

    // ����𰸣����ؽ�������ؽ���
    var a,b,c,d,note,result;
    for (var i = 0; i < count; ++i) {
        a = document.getElementById(aisId + '_anwser' + i + '_A');
        b = document.getElementById(aisId + '_anwser' + i + '_B');
        c = document.getElementById(aisId + '_anwser' + i + '_C');
        d = document.getElementById(aisId + '_anwser' + i + '_D');           

        a.checked = false;
        b.checked = false;
        c.checked = false;
        d.checked = false;

        note = document.getElementById(aisId + '_note' + i);
        result = document.getElementById(aisId + '_result' + i);

        note.disply = false;
        result.disply = false;
    }
}

// �ύ��
function submitTest(var aisId, var count) {
    
    init();

    var grade = checkAnwser(count);
    showResult();
    showNote();
    
    var a,b,c,d,note,result;
    for (var i = 0; i < count; ++i) {
        a = document.getElementById(aisId + '_anwser' + i + '_A');
        b = document.getElementById(aisId + '_anwser' + i + '_B');
        c = document.getElementById(aisId + '_anwser' + i + '_C');
        d = document.getElementById(aisId + '_anwser' + i + '_D'); 
    }
    
    alert("�����ز��Եĵ÷��ǣ�" + grade);
}