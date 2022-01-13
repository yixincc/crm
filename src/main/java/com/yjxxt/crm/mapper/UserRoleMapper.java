package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.pojo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {

    int countUserRoLeNum(Integer userId);

    int delectUserRoleByUserId(Integer userId);

}