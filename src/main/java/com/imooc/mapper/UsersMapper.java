package com.imooc.mapper;

import com.imooc.utils.MyMapper;
import com.imooc.pojo.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper extends MyMapper<Users> {
}