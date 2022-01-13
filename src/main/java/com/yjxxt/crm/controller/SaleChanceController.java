package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.pojo.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Autowired
    private SaleChanceService saleChanceService;

    @Autowired
    private UserService userService;


    //营销机会
    @RequestMapping("index")
    public String index() {
        return "saleChance/sale_chance";
    }

    //营销机会添加和修改请求
    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdate(Integer id, Model model) {
        //判断id是否为空
        if (id != null) {
            //查询用户信息
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //存储
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> saylist(SaleChanceQuery saleChanceQuery) {
        //调用方法获取数据
        Map<String, Object> map = saleChanceService.querySaleChanceByParams(saleChanceQuery);
        //map--json
        //返回目标map
        return map;
    }

    //营销机会添加模块
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(HttpServletRequest request, SaleChance saleChance) {
        //获取用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        //创建人
        saleChance.setCreateMan(trueName);
        //添加
        saleChanceService.addSaleChance(saleChance);
        return success("添加成功!");
    }

    //营销机会修改模块
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance) {
        //修改
        saleChanceService.changeSaleChance(saleChance);
        return success("修改成功!");
    }

    //营销机会批量删除模块
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deletes(Integer[] ids) {
        //删除操作
        saleChanceService.removeSaleChanceIds(ids);
        //返回目标
        return success("批量删除成功!");
    }



}
