package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.pojo.User;
import com.yjxxt.crm.pojo.UserRole;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.Md5Util;
import com.yjxxt.crm.utils.PhoneUtil;
import com.yjxxt.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.*;
import java.util.*;

@Service
public class UserService extends BaseService<User, Integer> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    public UserModel userLogin(String userName, String userPwd) {
        checkUserLoginParam(userName, userPwd);
        //用户是否存在
        User temp = userMapper.selectUserByName(userName);
        AssertUtil.isTrue(temp == null, "用户不存在");
        //用户密码是否正确
        checkUserPwd(userPwd, temp.getUserPwd());
        //构建返回对象
        return builderUserInfo(temp);
    }

    /**
     * 构建返回目标对象
     *
     * @param user 传入的用户对象
     * @return
     */
    private UserModel builderUserInfo(User user) {
        //实例化目标对象，初始化值
        UserModel userModel = new UserModel();
        //加密

        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        //返回目标对象
        return userModel;
    }

    /**
     * 对用户名和密码进行校验
     *
     * @param userName 用户名
     * @param userPwd  密码
     */
    private void checkUserLoginParam(String userName, String userPwd) {
        //用户名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空");
        //密码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "密码不能为空");
    }

    /**
     * 验证密码是否正确
     *
     * @param userPwd  用户输入密码
     * @param userPwd1 数据库中存储密码
     */
    private void checkUserPwd(String userPwd, String userPwd1) {
        //对输入的密码加密
        userPwd = Md5Util.encode(userPwd);
        //加密后密码和数据库中密码对比
        AssertUtil.isTrue(!(userPwd.equals(userPwd1)), "用户密码不正确!");
    }


    /**
     * 修改密码
     *
     * @param userId          用户id
     * @param oldPassword     旧密码
     * @param newPassword     新密码
     * @param confirmPassword 确认密码
     */
    public void changeUserPwd(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        //用户登录了修改，userId
        User user = userMapper.selectByPrimaryKey(userId);

        //验证密码
        checkPasswordParams(user, oldPassword, newPassword, confirmPassword);

        //修改密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //确认修改密码是否成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "修改失败!");
    }

    /**
     * 修改密码的验证
     *
     * @param user            用户
     * @param oldPassword     旧密码
     * @param newPassword     新密码
     * @param confirmPassword 确认密码
     */
    public void checkPasswordParams(User user, String oldPassword,
                                    String newPassword, String confirmPassword) {

        AssertUtil.isTrue(user == null, "用户未登录或者不存在");
        //原始密码非空

        //原始密码是否正确
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword), "请输入原始密码");
        //新密码非空
        AssertUtil.isTrue(StringUtils.isBlank(newPassword), "新密码不能为空");
        //确认密码非空
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword), "确认密码不能为空");

        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))), "输入的密码不正确!");
        //新密码不能与原始密码相同
        AssertUtil.isTrue(newPassword.equals(oldPassword), "新密码不能与原始密码相同");
        //确认密码和新密码一致
        AssertUtil.isTrue(!confirmPassword.equals(newPassword), "确认密码和新密码必须保存一致");
    }


    /**
     * 查询所有的销售人员
     *
     * @return
     */
    public List<Map<String, Object>> querySales() {
        return userMapper.selectSales();
    }


    /**
     * 用户模块列表查询
     *
     * @param userQuery 查询条件
     * @return
     */
    public Map<String, Object> findUserByParams(UserQuery userQuery) {
        //实例化map
        Map<String, Object> map = new HashMap<>();
        //初始化分页单位
        PageHelper.startPage(userQuery.getPage(), userQuery.getLimit());
        //开始分页
        PageInfo<User> plist = new PageInfo<>(selectByParams(userQuery));
        //准备数据
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", plist.getTotal());
        map.put("data", plist.getList());
        //返回数据
        return map;
    }

    /**
     * 用户添加模块
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user) {
        //验证
        checkUser(user.getUserName(), user.getEmail(), user.getPhone());
        //用户名唯一
        User temp = userMapper.selectUserByName(user.getUserName());
        AssertUtil.isTrue(temp != null, "用户名已存在");
        //设定默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //密码加密
        user.setUserPwd(Md5Util.encode("123456"));
        //添加是否成功
        //AssertUtil.isTrue(insertSelective(user)<1,"添加失败!");
        AssertUtil.isTrue(insertHasKey(user) < 1, "添加失败!");
        relaionUserRole(user.getId(), user.getRoleIds());

    }

    /**
     * 关联，操作中间表
     *
     * @param userId  用户id
     * @param roleIds 角色id
     */
    private void relaionUserRole(Integer userId, String roleIds) {
        //准备集合存储对象
        List<UserRole> urlist = new ArrayList<>();
        //判断
        AssertUtil.isTrue(StringUtils.isBlank(roleIds), "请选择角色信息");
        //统计当前用户有多少个角色
        int count = userRoleMapper.countUserRoLeNum(userId);
        //删除当前用户的角色
        if (count > 0) {
            AssertUtil.isTrue(userRoleMapper.delectUserRoleByUserId(userId) != count, "删除角色失败");
        }
        //删除原来的角色
        String[] RoleStrId = roleIds.split(",");
        //遍历
        for (String rid : RoleStrId) {

            //准备对象
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(Integer.parseInt(rid));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            //存储到集合
            urlist.add(userRole);
        }
        //批量添加
        AssertUtil.isTrue(userRoleMapper.insertBatch(urlist) != urlist.size(), "用户角色分配失败");


    }

    //验证方法
    private void checkUser(String userName, String email, String phone) {
        //用户名不为空
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空");
        //用户邮箱不为空
        AssertUtil.isTrue(StringUtils.isBlank(email), "邮箱不能为空");
        //用户手机不为空
        AssertUtil.isTrue(StringUtils.isBlank(phone), "手机号不能为空");
        //手机号码格式
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "请输入正确的手机号");
    }

    /**
     * 用户修改模块
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeUser(User user) {
        //根据id查询用户信息
        User temp = userMapper.selectByPrimaryKey(user.getId());
        //判断
        AssertUtil.isTrue(temp == null, "要修改的用户不存在");

        User temp2 = userMapper.selectUserByName(user.getUserName());
        AssertUtil.isTrue(temp2 != null && !(temp2.getId().equals(user.getId())), "用户名已存在");
        //验证参数
        checkUser(user.getUserName(), user.getEmail(), user.getPhone());
        //设定默认值
        user.setUpdateDate(new Date());
        //判断是否修改成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(user) < 1, "修改失败!");

        relaionUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 用户删除模块
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUserIds(Integer[] ids) {
        //验证
        AssertUtil.isTrue(ids == null || ids.length == 0, "请选择要删除的数据");

        //遍历对象
        for (Integer userId : ids) {
            //统计当前用户有多少个角色
            int count = userRoleMapper.countUserRoLeNum(userId);
            //删除当前用户的角色
            if (count > 0) {
                AssertUtil.isTrue(userRoleMapper.delectUserRoleByUserId(userId) != count, "删除角色失败");
            }
        }
        //判断是否删除成功
        AssertUtil.isTrue(deleteBatch(ids) < 1, "删除失败");
    }
}
