package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.pojo.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {

    //条件查询json列表
    /*
     * code
     * msg
     * count
     * data
     */
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery) {
        //实例
        Map<String, Object> map = new HashMap<String, Object>();
        //实例化分页单位
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
        //开始分页
        PageInfo<SaleChance> plist = new PageInfo<SaleChance>(selectByParams(saleChanceQuery));
        //准备数据
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", plist.getTotal());
        map.put("data", plist.getList());
        //返回map
        return map;
    }

    /**
     * 添加模块
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance) {
        //验证
        checkSaleChanceParam(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        //state 0 1(1 已分配  0 未分配)
        //未分配
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }
        //已分配
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        //设置默认值state,devResult(0 未开发 1 开发中  2 开发成功 3 开发失败)
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        saleChance.setIsValid(1);
        //createDate updateDate ,分配时间
        //是否添加成功
        AssertUtil.isTrue(insertSelective(saleChance) < 1, "添加失败");
    }

    /**
     * 添加模块验证
     *
     * @param customerName 客户名
     * @param linkMan      联系人
     * @param linkPhone    手机号
     */
    private void checkSaleChanceParam(String customerName, String linkMan, String linkPhone) {
        //客户名非空
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "请输入客户名称");
        //联系人非空
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "请输入联系人");
        //联系电话 非空 11位合法数据
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "请输入联系人手机号");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "请输入合法的手机号");

    }

    /**
     * 修改模块
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeSaleChance(SaleChance saleChance) {
        //验证
        SaleChance temp = selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(temp == null, "要修改的记录不存在");
        checkSaleChanceParam(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        //未分配
        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())) {
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        //已分配
        if (StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())) {
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //设置默认值
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //修改是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance) < 1, "修改失败");
    }


    /**
     * 批量删除
     * @param ids
     */
    public void removeSaleChanceIds(Integer[] ids){
        //验证
        AssertUtil.isTrue(ids==null || ids.length==0,"请选择要删除的数据");
        //删除是否成功
        AssertUtil.isTrue(deleteBatch(ids)!=ids.length,"批量删除失败了");
    }
}
