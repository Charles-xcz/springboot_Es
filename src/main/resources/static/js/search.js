var PATH = '/house/search';
$(function () {
    $(".btn-s").click(hreurl);
    $(".btn-sub").click(submit0);
    $(".btn-order").click(orderBtn);
});

function hreurl() {
    var search = window.location.search;
    var param = $(this).attr("name") + '=' + $(this).val();
    search = decodeURI(search);
    if (search.charAt(0) != '?') {
        param = '?' + param;
        // alert(path + param);
    } else {
        const index = search.indexOf(param);
        if (index != -1) {
            if (search.charAt(index - 1) == '&') {
                var items = search.split('&' + param);
                param = items.join("");
            } else {
                if (search.indexOf(param + '&') != -1) {
                    var items = search.split(param + '&');
                } else {
                    var items = search.split(param);
                }
                param = items.join("");
            }
            // alert(path+param);
        } else {
            param = search + '&' + $(this).attr("name") + '=' + $(this).val();
            // alert(path + param);
        }
    }
    param = replaceFragment(param, "current=", "current=1");
    param = encodeURI(param);
    window.location.href = PATH + param;
}

function submit0() {
    var search = window.location.search;
    search = decodeURI(search);
    var param = $("#keyword").attr("name") + '=' + $("#keyword").val();
    if (search.charAt(0) != '?') {
        param = '?' + param;
        // alert(path + param);
    } else {
        param = replaceFragment(search, "keyword=", param);
        param = replaceFragment(param, "current=", "current=1");
    }
    param = encodeURI(param);
    location.href = PATH + param;
}

function orderBtn() {
    var search = window.location.search;
    search = decodeURI(search);
    var param = $(this).attr("name") + '=' + $(this).val();
    if (search.charAt(0) != '?') {
        param = '?' + param;
        // alert(path + param);
    } else {
        param = replaceFragment(search, "orderMode=", param);
        param = replaceFragment(param, "current=", "current=1");
    }
    param = encodeURI(param);
    location.href = PATH + param;
}

function replaceFragment(target, fragmentOut, fragmentIn) {
    if (target.indexOf(fragmentOut) != -1) {
        const begin = target.indexOf(fragmentOut);
        var str = "";
        for (let i = begin; i < target.length; i++) {
            if (target.charAt(i) != '&') {
                str = str + target.charAt(i);
            } else {
                break;
            }
        }
        return target.replace(str, fragmentIn);
    } else {
        return target + '&' + fragmentIn;
    }
}