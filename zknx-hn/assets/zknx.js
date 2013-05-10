
// 初始化变量
var textUser;
var textPasswd;
var checkRemember;

// 初始化接口
window.onload = init;

// 初始化设置
function init() {

    // 初始化配置
    zknx.initConfig();

    this.textUser = document.getElementById('user');
    this.textPasswd = document.getElementById('passwd');
    this.checkRemember = document.getElementById('remember');

    this.textUser.value = zknx.getUser();
    this.checkRemember.checked = zknx.getRemember();

    if (this.checkRemember.checked)
        this.textPasswd.value = zknx.getPasswd();
}

// 登录
function login() {
    zknx.login(this.textUser.value, this.textPasswd.value, this.checkRemember.checked);
}