
// ��ʼ������
var textUser;
var textPasswd;
var checkRemember;

// ��ʼ���ӿ�
window.onload = init;

// ��ʼ������
function init() {

    // ��ʼ������
    zknx.initConfig();

    this.textUser = document.getElementById('user');
    this.textPasswd = document.getElementById('passwd');
    this.checkRemember = document.getElementById('remember');

    this.textUser.value = zknx.getUser();
    this.checkRemember.checked = zknx.getRemember();

    if (this.checkRemember.checked)
        this.textPasswd.value = zknx.getPasswd();
}

// ��¼
function login() {
    zknx.login(this.textUser.value, this.textPasswd.value, this.checkRemember.checked);
}