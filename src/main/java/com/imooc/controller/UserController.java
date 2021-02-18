package com.imooc.controller;

import com.imooc.enums.OperatorFriendRequestTypeEnum;
import com.imooc.enums.SearchFriendsStatusEnum;
import com.imooc.pojo.ChatMsg;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UsersBO;
import com.imooc.pojo.vo.FriendRequestVO;
import com.imooc.pojo.vo.MyFriendsVO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.FastDFSClient;
import com.imooc.utils.FileUtils;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("u")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/registOrLogin")
    public IMoocJSONResult registOrLogin(@RequestBody Users user) throws Exception{
        System.out.println("ok");
        if (StringUtils.isBlank(user.getUsername()) ||
                StringUtils.isBlank(user.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名或密码不能为空");
        }
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        Users userResult = null;
        if (usernameIsExist) {
            userResult =userService.queryUserForLogin(user.getUsername(),
                    MD5Utils.getMD5Str(user.getPassword()));
            if (userResult == null) {
                return IMoocJSONResult.errorMsg("用户名或密码不正确");
            }
        } else {
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            userResult = userService.saveUser(user);
        }
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userVO, userResult);
        return IMoocJSONResult.ok(userVO);
    }

    @PostMapping("/uploadFaceBase64")
    public IMoocJSONResult uploadFaceBase64(@RequestBody UsersBO userBo) throws Exception{
        //获取前段传来的base64字符串，然后转换为文件对象上传
        String base64Data = userBo.getFaceData();
        String userFacePath = "E:\\springbootTest\\src\\main\\resources\\static\\image\\" + userBo.getUserId() + "\\userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);

        MultipartFile faceFile =  FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);

        //获取缩略图url
        String thump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0] + thump + arr[1];

        Users user = new Users();
        user.setId(userBo.getUserId());
        user.setFaceImage(thumpImgUrl);
        user.setFaceImageBig(url);

        Users result = userService.updateUserInfo(user);

        return IMoocJSONResult.ok(result);
    }

    @PostMapping("/setNickname")
    public IMoocJSONResult setNickName(@RequestBody UsersBO userBo) throws Exception {
        System.out.println("ok");

        Users user = new Users();
        user.setId(userBo.getUserId());
        user.setNickname(userBo.getNickname());

        Users result = userService.updateUserInfo(user);
        return IMoocJSONResult.ok(result);
    }

    //搜索好友接口, 根据账户做匹配查询而不是模糊查询
    @PostMapping("/search")
    public IMoocJSONResult searchUser(String myUserId, String friendUsername) throws Exception {

        if (StringUtils.isBlank(myUserId)
            || StringUtils.isBlank(friendUsername)) {
            return IMoocJSONResult.errorMsg("");
        }

        //前置条件 1 搜索的用户如果不存在 返回无此用户
        //前置条件 2 搜索的用户是自己 返回不能添加自己
        //前置条件 3 搜索的用户如果应经是好友了 返回此用户已经是你的好友
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);

        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            Users user = userService.queryUserInfoByUsername(friendUsername);
            UsersVO userVO = new UsersVO();
            BeanUtils.copyProperties(userVO, user);
            return IMoocJSONResult.ok(userVO);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return IMoocJSONResult.errorMsg(errorMsg);
        }
    }

    //添加好友请求
    @PostMapping("/addFriendRequest")
    public IMoocJSONResult addFriendRequest(String myUserId, String friendUsername) throws Exception {

        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(friendUsername)) {
            return IMoocJSONResult.errorMsg("");
        }

        //前置条件 1 搜索的用户如果不存在 返回无此用户
        //前置条件 2 搜索的用户是自己 返回不能添加自己
        //前置条件 3 搜索的用户如果应经是好友了 返回此用户已经是你的好友
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);

        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            userService.sendFriendRequest(myUserId, friendUsername);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return IMoocJSONResult.errorMsg(errorMsg);
        }
        return IMoocJSONResult.ok();
    }

    @PostMapping("/queryFriendsRequests")
    public IMoocJSONResult queryFriendsRequests(String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("");
        }
        List<FriendRequestVO> list = userService.queryFriendRequestList(userId);
        return IMoocJSONResult.ok(list);
    }

    //通过或者忽略好友请求
    @PostMapping("/operFriendRequest")
    public IMoocJSONResult operFriendRequest(String acceptUserId, String sendUserId,
                                             Integer operType) {
        if (StringUtils.isBlank(acceptUserId)
                || StringUtils.isBlank(sendUserId)
                || operType == null) {
            return IMoocJSONResult.errorMsg("");
        }
        //System.out.println("ok");
        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
            return IMoocJSONResult.errorMsg("");
        }

        if (operType == OperatorFriendRequestTypeEnum.IGNORE.type) {
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        } else if (operType == OperatorFriendRequestTypeEnum.PASS.type) {
            userService.passFriendRequest(sendUserId, acceptUserId);
        }

        List<MyFriendsVO> myFriends = userService.queryMyFriends(acceptUserId);
        return IMoocJSONResult.ok(myFriends);
    }

    @PostMapping("/myFriends")
    public IMoocJSONResult operFriendRequest(String userId) {
        //System.out.println("myfriends");
        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("");
        }

        List<MyFriendsVO> myFriends = userService.queryMyFriends(userId);

        return IMoocJSONResult.ok(myFriends);
    }

    @PostMapping("/getUnReadMsgList")
    public IMoocJSONResult getUnReadMsgList(String acceptUserId) {
        if (StringUtils.isBlank(acceptUserId)) {
            return IMoocJSONResult.errorMsg("");
        }
        //查询列表
        List<ChatMsg> myFriends = userService.getUnReadMsgList(acceptUserId);
        return IMoocJSONResult.ok(myFriends);
    }
}
