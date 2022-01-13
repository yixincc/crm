package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.pojo.User;
import org.apache.ibatis.annotations.MapKey;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {



    User selectUserByName(String userName);

    @MapKey("")
    List<Map<String,Object>> selectSales();
}