package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.pojo.User;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    UserService userService;

    //修改密码页面跳转
    @RequestMapping("toPasswordPage")
    public String updatePwd() {
        return "user/password";
    }

    //进入用户页面
    @RequestMapping("index")
    public String index() {
        return "user/user";
    }
    //用户添加页面
    @RequestMapping("addOrUpdatePage")
    public String addOrUpdatePage(Integer id, Model model) {
        if (id != null){
            User user = userService.selectByPrimaryKey(id);
            model.addAttribute("user",user);
        }
        return "user/add_update";
    }
    //用户修改页面


    //基本资料页面
    @RequestMapping("toSettingPage")
    public String setting(HttpServletRequest request) {
        //获取用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用方法
        User user = userService.selectByPrimaryKey(userId);
        //存储
        request.setAttribute("user",user);
        //转发
        return "user/setting";
    }


    //用户登录
    @RequestMapping("login")
    @ResponseBody
    public ResultInfo say(User user) {
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel = userService.userLogin(user.getUserName(), user.getUserPwd());
        resultInfo.setResult(userModel);
        return resultInfo;
    }


    //用户模块添加
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveUser(User user) {
        userService.addUser(user);
        return success("用户添加成功");
    }

    //用户模块修改
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.changeUser(user);
        return success("用户修改成功");
    }

    //用户模块删除
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids) {
        userService.removeUserIds(ids);
        return success("用户删除成功");
    }


    //修改密码
    @RequestMapping("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest req, String oldPassword,
                                         String newPassword, String confirmPassword) {
        ResultInfo resultInfo = new ResultInfo();
        //获取cookie中的userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        System.out.println("-----------------------" + req.getCookies());
        //调service修改密码
        userService.changeUserPwd(userId, oldPassword, newPassword, confirmPassword);
        return resultInfo;
    }

    //用户基本信息修改
    @RequestMapping("setting")
    @ResponseBody
    public ResultInfo sayUpdate(User user) {
        ResultInfo resultInfo = new ResultInfo();
        //修改信息
        userService.updateByPrimaryKeySelective(user);
        //返回数据
        return resultInfo;
    }

    //查询所有销售
    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String, Object>> findSales(){
        List<Map<String, Object>> list = userService.querySales();
        return list;
    }

    //用户模块条件查询
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(UserQuery userQuery){
        return userService.findUserByParams(userQuery);
    }


}
