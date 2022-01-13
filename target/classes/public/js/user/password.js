layui.use(['form', 'jquery', 'jquery_cookie', 'layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

//监听提交
    form.on('submit(saveBtn)', function (data) {
        var fieldDate = data.field;

        //发送ajax
        $.ajax({
            type: "post",
            url: ctx + "/user/updatePwd",
            data: {
                oldPassword: fieldDate.old_password,
                newPassword: fieldDate.new_password,
                confirmPassword: fieldDate.again_password
            },
            dataType: "json",
            success: function (data) {
                if (data.code == 200) {
                    layer.msg("修改密码成功了", function () {
                        $.removeCookie("userIdStr", {domain: "localhost", path: "/crm"});
                        $.removeCookie("userName", {domain: "localhost", path: "/crm"});
                        $.removeCookie("trueName", {domain: "localhost", path: "/crm"});
                        //页面跳转
                        window.parent.location.href = ctx + "/index";
                    });
                } else {
                    layer.msg(data.msg);
                }
            }
        });
        return false;
    });
});