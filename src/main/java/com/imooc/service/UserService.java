package com.imooc.service;


import com.imooc.netty.ChatMsg;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.FriendRequestVO;
import com.imooc.pojo.vo.MyFriendsVO;

import java.util.List;

public interface UserService {
    public boolean queryUsernameIsExist(String username);

    public Users queryUserForLogin(String username, String pwd);

    public Users saveUser(Users user);

    //修改用户记录
    public Users updateUserInfo(Users user);

    //搜索朋友条件前置
    public Integer preconditionSearchFriends(String myUserId, String friendUsername);

    public Users queryUserInfoByUsername(String username);

    //添加好友记录请求
    public void sendFriendRequest(String myUserId, String friendUsername);

    //查询好友请求
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    public void deleteFriendRequest(String sendUserId, String acceptUserId);

    public void passFriendRequest(String sendUserId, String acceptUserId);

    public List<MyFriendsVO> queryMyFriends(String userId);

    //保存聊天消息到实现类
    public String saveMsg(ChatMsg chatMsg);

    public void updateMsgSigned(List<String> msgIdList);

    public List<com.imooc.pojo.ChatMsg> getUnReadMsgList(String acceptId);
}
