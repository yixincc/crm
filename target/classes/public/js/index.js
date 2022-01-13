layui.use(['form', 'jquery', 'jquery_cookie', 'layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

//监听提交
    form.on('submit(login)', function (data) {
        var fieldDate = data.field;
        if (fieldDate.username == 'undefinded' || fieldDate.username == '') {
            layer.msg("用户名不能为空");
            return;
        }
        if (fieldDate.password == 'undefinded' || fieldDate.password == '') {
            layer.msg("密码不能为空");
            return;
        }
        //发送ajax请求
        $.ajax({
            type: "post",
            url: ctx + "/user/login",
            data: {
                "userName": fieldDate.username,
                "userPwd": fieldDate.password
            },
            dateType: "json",
            success: function (msg) {
                //resultInfo
                if (msg.code == 200) {
                    layer.msg("登录成功",function (){
                        //将用户输入的数据存储到cookie中
                        $.cookie("userIdStr",msg.result.userIdStr);
                        $.cookie("userName",msg.result.userName);
                        $.cookie("trueName",msg.result.trueName);

                        //判断是否选中记住密码
                        if($(":checkbox:checked")){
                            $.cookie("userIdStr",msg.result.userIdStr,{expires:7});
                            $.cookie("userName",msg.result.userName,{expires:7});
                            $.cookie("trueName",msg.result.trueName,{expires:7});
                        }

                        //跳转页面
                        window.location.href=ctx+"/main";
                    })
                } else {
                    //失败提示
                    layer.msg(msg.msg);
                }
            }
        })
        //取消默认行为
        return false;
    });
});