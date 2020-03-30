$(function () {
    $("form").submit(check_data);
    $("input").focus(clear_error);
});

function check_data() {
    var pwd1 = $("#password").val();
    var pwd2 = $("#confirm-password").val();
    if (pwd1 != pwd2) {
        $("#confirm-password").addClass("is-invalid");
        return false;
    }
    getRandomHeader();
    return true;
}

function clear_error() {
    $(this).removeClass("is-invalid");
}

function getRandomHeader() {
    // $.post(
    //     'https://api.uomg.com/api/rand.avatar',
    //     function (data) {
    //         $("#headerUrl").val(data);
    //         alert(data);
    //     }
    // );
    var httpRequest = new XMLHttpRequest();//第一步：建立所需的对象
    httpRequest.open('GET', 'https://api.uomg.com/api/rand.avatar', true);//第二步：打开连接  将请求参数写在url中  ps:"./Ptest.php?name=test&nameone=testone"
    httpRequest.send();//第三步：发送请求  将请求参数写在URL中
    /**
     * 获取数据后的处理程序
     */
    httpRequest.onreadystatechange = function (data) {
        console.log(data);

    	if (httpRequest.readyState == 4 && httpRequest.status == 200) {
    		var json = httpRequest.responseText;//获取到json字符串，还需解析

    		console.log(json);
    	}
    };
}