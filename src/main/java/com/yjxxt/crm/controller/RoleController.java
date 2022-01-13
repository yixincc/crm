package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.pojo.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    //查询
    @RequestMapping("list")
    @ResponseBody
    @RequiredPermission(code = "60")
    public Map<String, Object> list(RoleQuery roleQuery) {
        return roleService.findRoleByParam(roleQuery);
    }

    @RequestMapping("findRoles")
    @ResponseBody
    public List<Map<String, Object>> sayRoles(Integer userId) {
        return roleService.findRoles(userId);
    }


    @RequestMapping("index")
    public String index() {
        return "role/role";
    }

    //添加授权页面
    @RequestMapping("toAddGrantPage")
    public String toRoleGrantPage(Integer roleId, Model model) {
        model.addAttribute("roleId", roleId);
        return "role/grant";
    }

    //角色授权页面跳转
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo grant(Integer roleId, Integer[] mids) {
        roleService.addGrant(roleId, mids);
        return success("授权成功");
    }


    //添加更新
    @RequestMapping("toAddOrUpdate")
    public String addOrUpdate(Integer roleId, Model model) {
        if (roleId != null) {
            Role role = roleService.selectByPrimaryKey(roleId);
            model.addAttribute("role", role);
        }
        return "role/add_update";
    }

    //添加
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(Role role) {
        roleService.addRole(role);
        return success("角色添加成功");
    }

    //修改
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Role role) {
        roleService.changeRole(role);
        return success("角色修改成功");
    }

    //删除
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(Role role) {
        roleService.removeRoleById(role);
        return success("角色删除成功");
    }


}
